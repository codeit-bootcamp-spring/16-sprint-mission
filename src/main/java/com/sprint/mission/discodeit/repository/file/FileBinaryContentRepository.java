
package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path directory = Path.of("data", "binary-content");

    public FileBinaryContentRepository() {
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("저장 폴더 생성 실패", e);
        }
    }

    @Override
    public void save(BinaryContent binaryContent) {
        Path path = getPath(binaryContent.getId());

        try (ObjectOutputStream output =
                     new ObjectOutputStream(Files.newOutputStream(path))) {
            output.writeObject(binaryContent);
        } catch (IOException e) {
            throw new IllegalStateException("파일 저장 실패", e);
        }
    }

    @Override
    public BinaryContent getById(UUID id) {
        Path path = getPath(id);

        if (!Files.exists(path)) {
            return null;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(path))) {
            return (BinaryContent) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("파일 조회 실패", e);
        }
    }

    @Override
    public List<BinaryContent> findAllByIdIn(List<UUID> ids) {
        List<BinaryContent> result = new ArrayList<>();

        for (UUID id : ids) {
            BinaryContent binaryContent = getById(id);

            if (binaryContent != null) {
                result.add(binaryContent);
            }
        }

        return result;
    }

    @Override
    public void delete(UUID id) {
        try {
            Files.deleteIfExists(getPath(id));
        } catch (IOException e) {
            throw new IllegalStateException("파일 삭제 실패", e);
        }
    }

    private Path getPath(UUID id) {
        return directory.resolve(id + ".ser");
    }
}
