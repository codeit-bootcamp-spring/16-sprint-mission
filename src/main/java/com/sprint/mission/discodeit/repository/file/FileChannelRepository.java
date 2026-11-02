package com.sprint.mission.discodeit.repository.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileChannelRepository implements ChannelRepository {
    // 채널 데이터 파일이 저장될 경로를 지정한다.
    private final Path filePath;

    // 파일에서 읽어 온 채널 목록을 보관한다.
    private List<Channel> data;

    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Paths.get(fileDirectory, "channelRepository.ser");
        createDataDirectory();
        this.data = loadData();
    }

    // 데이터 저장 디렉터리 생성
    private void createDataDirectory(){
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException ioException){
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다.", ioException);
        }
    }

    // 파일에서 채널 목록을 불러온다.
    @SuppressWarnings("unchecked")
    private List<Channel> loadData() {
        // 저장된 파일이 없으면 빈 채널 목록을 반환한다.
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        // 파일에 저장된 객체를 읽는다. (역직렬화)
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(filePath))) {

            // 파일에 저장된 채널 목록을 읽어 반환한다.
            return (List<Channel>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("채널 데이터를 불러올 수 없습니다.", exception);
        }
    }

    // 현재 채널 목록을 임시 파일에 직렬화한 뒤 기존 파일을 교체하여 저장
    private void saveData() {
        Path tempFile = null;

        try {
            // 임시 파일 생성
            tempFile = Files.createTempFile(
                    filePath.getParent(),
                    "channelRepository-",
                    ".tmp"
            );

            // 임시 파일에 현재 채널 목록을 직렬화하여 저장
            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(tempFile))) {
                outputStream.writeObject(data);
            }

            // 임시 파일을 기존 데이터 파일로 교체
            try {
                Files.move(
                        tempFile,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE
                );
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(
                        tempFile,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }

        } catch (IOException ioException) {
            throw new RuntimeException("채널 데이터를 저장할 수 없습니다.", ioException);

        } finally {
            // 저장 실패 등으로 남아 있는 임시 파일 정리
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ioException) {
                    System.out.println("임시 파일을 삭제할 수 없습니다." + ioException.getMessage());
                }
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        // 변경 전 채널 목록 백업
        List<Channel> prevData = new ArrayList<>(data);

        try {
            // 기존 채널 목록에서 같은 ID를 가진 채널을 조회한다.
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getId().equals(channel.getId())) {
                    // 같은 채널을 새 채널 객체로 교체한다.
                    data.set(i, channel);

                    // 변경된 채널 목록을 파일에 저장한다.
                    saveData();
                    return channel;
                }
            }
            // 동일한 ID를 가진 채널이 없으면 새 채널을 추가한다.
            data.add(channel);

            // 변경된 채널 목록 저장
            saveData();
            return channel;

        } catch (RuntimeException rtException){
            // 저장 실패 시 기존 목록으로 복구
            data = prevData;
            throw rtException;
        }
    }

    @Override
    public Optional<Channel> findByChannelId(UUID id) {
        // ID가 일치하는 채널을 찾아 반환한다. (단건 조회)
        // 일치하는 채널이 없으면 Optional.empty()를 반환한다.
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        // data에 저장된 채널을 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsByChannelId(UUID id) {
        // 해당 채널의 ID가 존재하는지 확인한다.
        return data.stream().anyMatch(channel -> channel.getId().equals(id));
    }

    @Override
    public void deleteByChannelId(UUID id) {
        // 삭제 전 채널 목록 백업
        List<Channel> prevData = new ArrayList<>(data);

        // ID가 일치하는 채널을 삭제한다. (삭제)
        boolean remove = data.removeIf(channel -> channel.getId().equals(id));

        // 삭제된 채널이 없으면 예외 발생
        if(!remove){
            throw new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다.");
        }

        try {
            // 변경된 채널 목록 저장
            saveData();
        } catch (RuntimeException re){
            // 저장 실패 시 삭제 전 목록으로 복구
            data = prevData;
            throw re;
        }
    }
}