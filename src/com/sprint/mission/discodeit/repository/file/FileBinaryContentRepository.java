package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

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
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final File file;

    // 기존 코드에서 사용하는 기본 생성자
    public FileBinaryContentRepository() {
        this(".discodeit");
    }

    // Spring이 YAML 설정을 읽어서 사용하는 생성자
    @Autowired
    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")
            String fileDirectory
    ) {
        this.file = new File(fileDirectory, "binary-contents.dat");

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs() && !parent.exists()) {
                throw new IllegalStateException(
                        "첨부파일 정보 저장 폴더를 생성할 수 없습니다."
                );
            }
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        if (binaryContent == null) {
            throw new IllegalArgumentException("첨부파일 정보는 필수입니다.");
        }

        if (binaryContent.getId() == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        Map<UUID, BinaryContent> data = readAllAsMap();

        data.put(binaryContent.getId(), binaryContent);

        writeAll(data);

        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        return Optional.ofNullable(readAllAsMap().get(id));
    }

    @Override
    public List<BinaryContent> findAll() {
        return new ArrayList<>(readAllAsMap().values());
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        if (ids.stream().anyMatch(id -> id == null)) {
            throw new IllegalArgumentException(
                    "첨부파일 ID 목록에 null이 포함되어 있습니다."
            );
        }

        Map<UUID, BinaryContent> data = readAllAsMap();

        List<BinaryContent> result = new ArrayList<>();

        for (UUID id : ids) {
            BinaryContent content = data.get(id);

            if (content != null) {
                result.add(content);
            }
        }

        return result;
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("첨부파일 ID는 필수입니다.");
        }

        Map<UUID, BinaryContent> data = readAllAsMap();

        if (!data.containsKey(id)) {
            throw new IllegalArgumentException(
                    "첨부파일 정보를 찾을 수 없습니다."
            );
        }

        data.remove(id);

        writeAll(data);
    }

    private Map<UUID, BinaryContent> readAllAsMap() {
        if (!file.exists() || file.length() == 0) {
            return new LinkedHashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(
                new FileInputStream(file))) {

            Object object = in.readObject();

            if (!(object instanceof Map<?, ?> map)) {
                throw new IllegalStateException(
                        "첨부파일 정보 파일의 데이터 형식이 올바르지 않습니다."
                );
            }

            Map<UUID, BinaryContent> data = new LinkedHashMap<>();

            for (Map.Entry<?, ?> entry : map.entrySet()) {

                if (!(entry.getKey() instanceof UUID id)
                        || !(entry.getValue() instanceof BinaryContent content)) {
                    throw new IllegalStateException(
                            "첨부파일 정보 파일에 잘못된 데이터가 있습니다."
                    );
                }

                if (!id.equals(content.getId())) {
                    throw new IllegalStateException(
                            "첨부파일 ID와 저장된 데이터가 일치하지 않습니다."
                    );
                }

                data.put(id, content);
            }

            return data;

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(
                    "첨부파일 정보 파일을 읽을 수 없습니다.", e
            );
        }
    }

    private void writeAll(Map<UUID, BinaryContent> data) {
        try (ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream(file))) {

            out.writeObject(new LinkedHashMap<>(data));

        } catch (IOException e) {
            throw new IllegalStateException(
                    "첨부파일 정보 파일을 저장할 수 없습니다.", e
            );
        }
    }
}