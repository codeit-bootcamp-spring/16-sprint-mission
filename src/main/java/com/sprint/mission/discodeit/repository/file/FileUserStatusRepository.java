
package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

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
public class FileUserStatusRepository implements UserStatusRepository {

    // 유저 상태 데이터 파일이 저장될 경로
    private final Path filePath;

    // 파일에서 읽어 온 유저 상태 목록
    private List<UserStatus> data;

    // 데이터 디렉터리를 생성하고 파일에 저장된 유저 상태 목록을 초기화한다.
    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:data}") String fileDirectory
    ) {
        this.filePath = Paths.get(fileDirectory, "userStatusRepository.ser");
        createDataDirectory();
        this.data = loadData();
    }

    // 데이터 파일을 저장할 폴더가 없으면 생성한다.
    private void createDataDirectory() {
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException exception) {
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다.", exception);
        }
    }

    // 파일에서 유저 상태 목록을 불러온다.
    @SuppressWarnings("unchecked")
    private List<UserStatus> loadData() {
        if (Files.notExists(filePath)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(filePath))) {

            return (List<UserStatus>) inputStream.readObject();

        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("유저 상태 데이터를 불러올 수 없습니다.", exception);
        }
    }

    // 현재 유저 상태 목록을 파일에 저장한다.
    private void saveData(List<UserStatus> dataToSave) {
        Path tempFile = null;

        try {
            // 기존 파일을 보호하기 위해 같은 디렉터리에 임시 파일을 생성한다.
            tempFile = Files.createTempFile(
                    filePath.getParent(), "userStatusRepository-", ".tmp");

            // 임시 파일에 유저 상태 목록을 직렬화한다.
            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(tempFile))) {

                outputStream.writeObject(dataToSave);
            }

            // 출력 스트림이 닫힌 후 임시 파일을 기존 데이터 파일로 교체한다.
            try {
                Files.move(tempFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                // 원자적 이동을 지원하지 않으면 일반 파일 이동으로 대체한다.
                Files.move(tempFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException exception) {
            throw new RuntimeException("유저 상태 데이터를 저장할 수 없습니다.", exception);
        } finally {
            // 저장 과정에서 남은 임시 파일을 정리한다.
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                }
            }
        }
    }

    // 유저 ID를 기준으로 기존 유저 상태를 수정하거나 새로운 상태를 추가한다.
    @Override
    public void save(UserStatus userStatus) {
        // 기존 데이터에 영향을 주지 않도록 복사본을 생성한다.
        List<UserStatus> updatedData = new ArrayList<>(data);

        for (int i = 0; i < updatedData.size(); i++) {
            if (updatedData.get(i).getUserId().equals(userStatus.getUserId())) {
                updatedData.set(i, userStatus);
                saveData(updatedData);
                data = updatedData;
                return;
            }
        }

        updatedData.add(userStatus);
        saveData(updatedData);
        data = updatedData;
    }

    // ID로 UserStatus 조회
    @Override
    public Optional<UserStatus> findById(UUID id) {
        return data.stream()
                .filter(userStatus -> userStatus.getId().equals(id))
                .findFirst();
    }

    // 특정 유저 ID와 일치하는 유저 상태를 조회한다.
    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        return data.stream()
                .filter(userStatus -> userStatus.getUserId().equals(userId))
                .findFirst();
    }

    // 모든 UserStatus 조회
    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data);
    }

    // 특정 유저의 상태를 삭제하고 변경된 목록을 파일에 저장한다.
    @Override
    public void deleteByUserId(UUID userId) {
        List<UserStatus> updatedData = new ArrayList<>(data);

        boolean removed = updatedData.removeIf(
                userStatus -> userStatus.getUserId().equals(userId));

        if (!removed) {
            saveData(updatedData);
            data = updatedData;
            // throw new IllegalArgumentException("삭제할 유저 상태를 찾을 수 없습니다.");
        }

    }

    // ID로 UserStatus 삭제
    @Override
    public void deleteById(UUID id) {
        List<UserStatus> updatedData = new ArrayList<>(data);

        boolean removed = updatedData.removeIf(
                userStatus -> userStatus.getId().equals(id));

        if (!removed) {
            throw new IllegalArgumentException("삭제할 UserStatus를 찾을 수 없습니다.");
        }

        saveData(updatedData);
        data = updatedData;
    }
}