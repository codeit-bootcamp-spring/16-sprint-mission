package com.sprint.mission.discodeit.repository.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.File;
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
public class FileUserRepository implements UserRepository {

    // 유저 데이터 파일이 저장될 경로
    private final Path filePath;

    // 파일에서 읽어 온 유저 목록
    private List<User> data;

    public FileUserRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Paths.get(fileDirectory, "userRepository.ser");
        createDataDirectory();
        this.data = loadData();
    }

    // 데이터 파일을 저장할 폴더가 없으면 생성한다.
    private void createDataDirectory() {
        try {
            // 데이터 파일을 저장할 폴더가 없으면 생성한다.
            Files.createDirectories(filePath.getParent());
        } catch (IOException exception) {
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다", exception);
        }
    }

    // 파일에서 유저 목록을 불러온다.
    @SuppressWarnings("unchecked")
    private List<User> loadData(){

        // 파일이 없으면 빈 목록을 반환한다.
        if (Files.notExists(filePath)){
            return new ArrayList<>();
        }

        // 파일에 저장된 객체를 읽는다. (역직렬화)
        try (ObjectInputStream inputStream =
                new ObjectInputStream(Files.newInputStream(filePath))){

            // 파일에 저장된 유저 목록을 읽어 반환한다.
            return (List<User>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception){
            throw new RuntimeException("유저 데이터를 불러올 수 없습니다.", exception);
        }
    }

    // 현재 유저 목록을 임시 파일에 직렬화한 뒤 기존 파일과 교체한다.
    private void saveData(List<User> dataToSave) {
        Path tempFile = null;

        try {
            // 기존 데이터 파일과 같은 디렉터리에 임시 파일을 생성한다.
            tempFile = Files.createTempFile(filePath.getParent(),
                    "userRepository-", ".tmp");

            // 객체를 userRepository 파일에 저장한다. (직렬화)
            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(tempFile))) {

                // 유저 목록 전체를 임시 파일에 직렬화한다.
                outputStream.writeObject(dataToSave);

                // 임시 파일을 기존 데이터 파일과 원자적으로 교체
                try {
                    Files.move(tempFile,
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);

                } catch (AtomicMoveNotSupportedException exception) {
                    // 원자적 이동을 지원하지 않으면 일반적인 파일 교체를 수행한다.
                    Files.move(tempFile,
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING);
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("유저 데이터를 저장할 수 없습니다", exception);
        } finally {
            // 저장 과정에서 남은 임시 파일 정리
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // 임시 파일 정리 실패가 기존 예외에 영향을 주지 않도록 처리
                }
            }
        }
    }

    @Override
    public User save(User user) {
        // 변경 전 유저 목록을 보관한다.
        List<User> updatedData = new ArrayList<>(data);

        try {
            // 기존 유저 목록에서 동일한 ID 조회
            for (int i = 0; i < updatedData.size(); i++) {
                if (updatedData.get(i).getId().equals(user.getId())) {
                    // 기존 유저를 새 유저 객체로 교체
                    updatedData.set(i, user);

                    // 파일 저장 성공 후 메모리 갱신
                    saveData(updatedData);
                    data = updatedData;

                    return user;
                }
            }

            // 동일한 ID가 없으면 새 유저 추가
            updatedData.add(user);

            // 파일 저장 성공 후 메모리 갱신
            saveData(updatedData);
            data = updatedData;

            return user;

        } catch (RuntimeException rtException){
            // 저장 실패 시 기존 메모리 상태를 유지하고 예외 전달
            System.out.println(rtException.getMessage());
            throw rtException;
        }
    }

    @Override
    public Optional<User> findByUserId(UUID id) {
        // ID가 일치하는 유저를 찾아 반환한다. (단건 조회)
        // 일치하는 유저가 없으면 Optional.empty()를 반환한다.
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        // data에 저장된 유저들을 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data);
    }

    @Override
    public void deleteByUserId(UUID id) {
        // 기존 목록을 변경하지 않고 수정 후보 목록을 만든다.
        List<User> updatedData = new ArrayList<>(data);

        // ID가 일치하는 유저를 삭제한다. (삭제)
        boolean remove = updatedData.removeIf(user -> user.getId().equals(id));

        // 삭제된 유저가 없으면 예외를 발생시킨다.
        if(!remove){
            throw new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다.");
        }

        // 파일 저장 성공 후 메모리 갱신
        saveData(updatedData);
        data = updatedData;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return data.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }

    @Override
    public boolean existsByUserId(UUID id) {
        // 유저 ID 존재 여부 확인
        return data.stream().anyMatch(user -> user.getId().equals(id));
    }
}