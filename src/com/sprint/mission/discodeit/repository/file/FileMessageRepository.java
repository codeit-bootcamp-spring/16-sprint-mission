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
import java.util.concurrent.locks.ReentrantLock;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileMessageRepository implements MessageRepository {

    private final File file;
    private final ReentrantLock lock;

    public FileMessageRepository() {
        this(".discodeit", new FileLockProvider());
    }

    @Autowired
    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")
            String fileDirectory,
            FileLockProvider fileLockProvider
    ) {
        this.file = new File(fileDirectory, "messages.dat");
        this.lock = fileLockProvider.getLock(file.toPath());

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

        lock.lock();
        try {
            Map<UUID, Message> data = readAllAsMap();
            data.put(message.getId(), message);
            writeAll(data);

            return message;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        lock.lock();
        try {
            return Optional.ofNullable(readAllAsMap().get(id));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Message> findAll() {
        lock.lock();
        try {
            return new ArrayList<>(readAllAsMap().values());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        lock.lock();
        try {
            return readAllAsMap().values().stream()
                    .filter(message -> channelId.equals(message.getChannelId()))
                    .toList();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("메시지 ID는 필수입니다.");
        }

        lock.lock();
        try {
            Map<UUID, Message> data = readAllAsMap();

            if (!data.containsKey(id)) {
                throw new IllegalArgumentException("메시지를 찾을 수 없습니다.");
            }

            data.remove(id);
            writeAll(data);
        } finally {
            lock.unlock();
        }
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