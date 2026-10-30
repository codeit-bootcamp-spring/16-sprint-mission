package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

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
public class FileChannelRepository implements ChannelRepository {

    private final File file;

    // 기존 코드에서 사용하는 기본 생성자
    public FileChannelRepository() {
        this(".discodeit");
    }

    // Spring이 YAML 설정을 읽어서 사용하는 생성자
    @Autowired
    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")
            String fileDirectory
    ) {
        this.file = new File(fileDirectory, "channels.dat");

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs() && !parent.exists()) {
                throw new IllegalStateException(
                        "채널 저장 폴더를 생성할 수 없습니다."
                );
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널 정보는 필수입니다.");
        }

        if (channel.getId() == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        Map<UUID, Channel> data = readAllAsMap();
        data.put(channel.getId(), channel);
        writeAll(data);

        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        return Optional.ofNullable(readAllAsMap().get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(readAllAsMap().values());
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        Map<UUID, Channel> data = readAllAsMap();

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        data.remove(id);
        writeAll(data);
    }

    private Map<UUID, Channel> readAllAsMap() {
        if (!file.exists() || file.length() == 0) {
            return new LinkedHashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {

            Object object = in.readObject();

            if (!(object instanceof Map<?, ?> map)) {
                throw new IllegalStateException(
                        "채널 파일의 데이터 형식이 올바르지 않습니다."
                );
            }

            Map<UUID, Channel> data = new LinkedHashMap<>();

            for (Map.Entry<?, ?> entry : map.entrySet()) {

                if (!(entry.getKey() instanceof UUID id)
                        || !(entry.getValue() instanceof Channel channel)) {
                    throw new IllegalStateException(
                            "채널 파일에 잘못된 데이터가 있습니다."
                    );
                }

                if (!id.equals(channel.getId())) {
                    throw new IllegalStateException(
                            "채널 ID와 저장된 데이터가 일치하지 않습니다."
                    );
                }

                data.put(id, channel);
            }

            return data;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "채널 파일을 읽을 수 없습니다.", e
            );
        }
    }

    private void writeAll(Map<UUID, Channel> data) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {

            out.writeObject(new LinkedHashMap<>(data));

        } catch (IOException e) {
            throw new IllegalStateException(
                    "채널 파일을 저장할 수 없습니다.", e
            );
        }
    }
}