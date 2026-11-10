package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserStatusRepository implements UserStatusRepository {

    private final File file;

    // 기존 코드와의 호환성을 위한 기본 생성자
    public FileUserStatusRepository() {
        this(".discodeit");
    }

    // Spring이 YAML 설정값을 주입하는 생성자
    @Autowired
    public FileUserStatusRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")
            String fileDirectory
    ) {
        this.file = new File(fileDirectory, "user-statuses.dat");

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs() && !parent.exists()) {
                throw new IllegalStateException(
                        "사용자 접속 상태 저장 폴더를 생성할 수 없습니다."
                );
            }
        }
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        if (userStatus == null) {
            throw new IllegalArgumentException("사용자 접속 상태는 필수입니다.");
        }

        if (userStatus.getId() == null) {
            throw new IllegalArgumentException("사용자 접속 상태 ID는 필수입니다.");
        }

        Map<UUID, UserStatus> data = readAllAsMap();
        data.put(userStatus.getId(), userStatus);
        writeAll(data);

        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 접속 상태 ID는 필수입니다.");
        }

        return Optional.ofNullable(readAllAsMap().get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(readAllAsMap().values());
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        return findAll().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .findFirst();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 접속 상태 ID는 필수입니다.");
        }

        Map<UUID, UserStatus> data = readAllAsMap();

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException(
                    "사용자 접속 상태를 찾을 수 없습니다."
            );
        }

        data.remove(id);
        writeAll(data);
    }

    private Map<UUID, UserStatus> readAllAsMap() {
        if (!file.exists() || file.length() == 0) {
            return new LinkedHashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {

            Object object = in.readObject();

            if (!(object instanceof Map<?, ?> map)) {
                throw new IllegalStateException(
                        "사용자 접속 상태 파일의 데이터 형식이 올바르지 않습니다."
                );
            }

            Map<UUID, UserStatus> data = new LinkedHashMap<>();

            for (Map.Entry<?, ?> entry : map.entrySet()) {

                if (!(entry.getKey() instanceof UUID id)
                        || !(entry.getValue() instanceof UserStatus status)) {
                    throw new IllegalStateException(
                            "사용자 접속 상태 파일에 잘못된 데이터가 있습니다."
                    );
                }

                if (!id.equals(status.getId())) {
                    throw new IllegalStateException(
                            "사용자 접속 상태 ID와 저장된 데이터가 일치하지 않습니다."
                    );
                }

                data.put(id, status);
            }

            return data;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "사용자 접속 상태 파일을 읽을 수 없습니다.", e
            );
        }
    }

    private void writeAll(Map<UUID, UserStatus> data) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {

            out.writeObject(new LinkedHashMap<>(data));

        } catch (IOException e) {
            throw new IllegalStateException(
                    "사용자 접속 상태 파일을 저장할 수 없습니다.", e
            );
        }
    }
}