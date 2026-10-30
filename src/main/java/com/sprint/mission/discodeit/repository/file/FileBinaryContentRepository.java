package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileBinaryContentRepository implements BinaryContentRepository {

    private final Path directory;

    public FileBinaryContentRepository() {
        this(Path.of(System.getProperty("user.dir"), "data", "binaryContents"));
    }

    public FileBinaryContentRepository(Path directory) {
        this.directory = directory;

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException("바이너리 콘텐츠 저장 폴더 생성에 실패했습니다.", e);
        }
    }

    private Path fileFor(UUID binaryContentId) {
        return directory.resolve(binaryContentId + ".ser");
    }

    private BinaryContent readBinaryContent(Path path) {
        try (ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(path))) {
            return (BinaryContent) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("바이너리 콘텐츠 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Optional<BinaryContent> findById(UUID binaryContentId) {
        Path path = fileFor(binaryContentId);

        if (!Files.exists(path)) {
            return Optional.empty();
        }

        return Optional.of(readBinaryContent(path));
    }

    @Override
    public List<BinaryContent> findAll() {
        try (var paths = Files.list(directory)) {
            return paths
                    .filter(path -> path.toString().endsWith(".ser"))
                    .map(this::readBinaryContent)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("바이너리 콘텐츠 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        Path path = fileFor(binaryContent.getId());

        try (ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(path))) {
            oos.writeObject(binaryContent);
            return binaryContent;
        } catch (IOException e) {
            throw new RuntimeException("바이너리 콘텐츠 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID binaryContentId) {
        Path path = fileFor(binaryContentId);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("바이너리 콘텐츠 삭제에 실패했습니다.", e);
        }
    }
}