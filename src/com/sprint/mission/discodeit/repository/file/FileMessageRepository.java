package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

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
public class FileMessageRepository implements MessageRepository {

    private final File file;

    // 기존 코드에서 사용하는 기본 생성자
    public FileMessageRepository() {
        this(".discodeit");
    }

    // Spring이 YAML 설정을 읽어서 사용하는 생성자
    @Autowired
    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")
            String fileDirectory
    ) {
        this.file = new File(fileDirectory, "messages.dat");

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs() && !parent.exists()) {
                throw new IllegalStateException(
                        "메시지 저장 폴더를 생성할 수 없습니다: " + parent.getPath()
                );
            }
        }
    }

    @Override
    public Message save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("메시지 정보는 필수입니다.");
        }

        if (message.getId() == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        Map<UUID, Message> data = readAllAsMap();
        data.put(message.getId(), message);
        writeAll(data);

        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        return Optional.ofNullable(readAllAsMap().get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(readAllAsMap().values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        return readAllAsMap().values().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        Map<UUID, Message> data = readAllAsMap();

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
        }

        data.remove(id);
        writeAll(data);
    }

    private Map<UUID, Message> readAllAsMap() {
        if (!file.exists() || file.length() == 0) {
            return new LinkedHashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {

            Object object = in.readObject();

            if (!(object instanceof Map<?, ?> map)) {
                throw new IllegalStateException(
                        "메시지 파일의 데이터 형식이 올바르지 않습니다."
                );
            }

            Map<UUID, Message> data = new LinkedHashMap<>();

            for (Map.Entry<?, ?> entry : map.entrySet()) {

                if (!(entry.getKey() instanceof UUID id)
                        || !(entry.getValue() instanceof Message message)) {
                    throw new IllegalStateException(
                            "메시지 파일에 잘못된 데이터가 있습니다."
                    );
                }

                if (!id.equals(message.getId())) {
                    throw new IllegalStateException(
                            "메시지 ID와 저장된 데이터가 일치하지 않습니다."
                    );
                }

                data.put(id, message);
            }

            return data;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "메시지 파일을 읽을 수 없습니다.", e
            );
        }
    }

    private void writeAll(Map<UUID, Message> data) {
        Map<UUID, Message> orderedData = new LinkedHashMap<>(data);

        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {

            out.writeObject(orderedData);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "메시지 파일을 저장할 수 없습니다.", e
            );
        }
    }
}