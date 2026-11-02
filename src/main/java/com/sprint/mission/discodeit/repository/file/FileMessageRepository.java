package com.sprint.mission.discodeit.repository.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

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
public class FileMessageRepository implements MessageRepository {
    // 메시지 데이터 파일 경로
    private final Path filePath;

    // 파일에서 불러온 메시지 목록
    private List<Message> data;

    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Paths.get(fileDirectory,"messageRepository.ser");
        createDataDirectory();
        this.data = loadData();
    }

    // 데이터 저장 디렉터리 생성
    private void createDataDirectory(){
        try {
            // 데이터 파일을 저장할 폴더가 없으면 생성한다.
            Files.createDirectories(filePath.getParent());
        } catch (IOException ioException){
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다.", ioException);
        }
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    // 파일에서 메시지 목록을 불러온다.
    @SuppressWarnings("unchecked")
    private List<Message> loadData() {
        // 저장된 파일이 없으면 빈 메시지 목록을 반환한다.
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        // 파일에 저장된 객체를 읽는다. (역직렬화)
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(filePath))) {

            // 파일에 저장된 메시지 목록을 읽어 반환한다.
            return (List<Message>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("메시지 데이터를 불러올 수 없습니다.", exception);
        }
    }

    // 현재 메시지 목록을 파일에 저장한다.
    private void saveData() {
        Path tempFile = null;

        try {
            // 임시 파일 생성
            tempFile = Files.createTempFile(
                    filePath.getParent(),
                    "messageRepository-",
                    ".tmp"
            );

            // 객체를 messageRepository 파일에 저장한다. (직렬화)
            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(tempFile))) {
                // 현재 메시지 목록 전체를 파일에 저장한다.
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
                } catch (AtomicMoveNotSupportedException amnsException) {
                    Files.move(
                        tempFile,
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                    );
                }

            } catch (IOException exception) {
                throw new RuntimeException("메시지 데이터를 저장할 수 없습니다.", exception);

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
    public Message save(Message message) {
        // 변경 전 메시지 목록을 보관한다.
        List<Message> prevData = new ArrayList<>(data);
        try {
            // 기존 메시지 목록에서 같은 ID를 가진 메시지를 찾는다.
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getId().equals(message.getId())) {
                    // 같은 ID를 가진 메시지를 새 메시지 객체로 교체한다.
                    data.set(i, message);

                    // 수정된 메시지 목록을 파일에 저장한다.
                    saveData();
                    return message;
                }
            }
            // 같은 ID를 가진 메시지가 없으면 새 메시지를 추가한다.
            data.add(message);

            // 추가된 메시지 목록을 파일에 저장한다.
            saveData();
            return message;

        } catch (RuntimeException rtException){
            // 저장에 실패하면 메모리의 목록을 이전 상태로 복구한다.
            data = prevData;
            throw rtException;
        }
    }

    @Override
    public Optional<Message> findByMessageId(UUID id) {
        // ID가 일치하는 메시지를 찾아 반환한다.
        // 일치하는 메시지가 없으면 Optional.empty()를 반환한다.
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        // data에 저장된 메시지를 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsByMessageId(UUID id) {
        // 해당 메시지의 ID가 존재하는지 확인한다.
        return data.stream().anyMatch(message -> message.getId().equals(id));
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        // 삭제 전 메시지 목록 백업
        List<Message> prevData = new ArrayList<>(data);

        // 해당 채널에 속한 메시지 삭제
        boolean removed = data.removeIf(
                message -> message.getChannelId().equals(channelId));

        // 메시지가 삭제되지 않았다면 종료
        if (!removed) {
            return;
        }

        try {
            // 변경된 메시지 목록 저장
            saveData();
        } catch (RuntimeException rtException) {
            // 저장 실패 시 삭제 전 목록으로 복구
            data = prevData;
            throw rtException;
        }
    }

    @Override
    public void deleteByMessageId(UUID id) {
        // 삭제 전 메시지 목록 백업
        List<Message> prevData = new ArrayList<>(data);

        // ID가 일치하는 메시지를 삭제한다.
        boolean remove = data.removeIf(message -> message.getId().equals(id));

        // 삭제된 메시지가 없으면 예외를 발생시킨다.
        if(!remove){
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }

        try {
            // 변경된 메시지 목록을 파일에 저장한다.
            saveData();
        } catch (RuntimeException rtException) {
            // 저장 실패 시 삭제 전 목록으로 복구
            data = prevData;
            throw rtException;
        }
    }
}