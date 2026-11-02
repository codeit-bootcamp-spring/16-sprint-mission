package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)

@Repository
public class FileBinaryContentRepository implements BinaryContentRepository {
    private final Path filePath;

    public FileBinaryContentRepository(
            @Value("${discodeit.repository.file-directory}") String directory) {
        this.filePath = Path.of(directory,"binaryContents.ser");

        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }

    private void saveBinaryContents(List<BinaryContent> binaryContents) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(binaryContents);
        } catch (IOException e) {
            throw new IllegalStateException("파일 목록을 저장할 수 없습니다", e);
        }
    }

    private List<BinaryContent> loadBinaryContents() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<BinaryContent>) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("파일 목록을 읽을 수 없습니다",e);
        }
    }

    @Override
    public BinaryContent save(BinaryContent binaryContent) {
        List<BinaryContent> binaryContents = loadBinaryContents();
        binaryContents.removeIf(b ->
                b.getId().equals(binaryContent.getId()));
        binaryContents.add(binaryContent);
        saveBinaryContents(binaryContents);
        return binaryContent;
    }

    @Override
    public Optional<BinaryContent> findById(UUID id) {
        return loadBinaryContents().stream().filter(b ->
                b.getId().equals(id)).findFirst();
    }

    @Override
    public List<BinaryContent> findAll() {
        return loadBinaryContents();
    }

    @Override
    public void deleteById(UUID id) {
        List<BinaryContent> binaryContents = loadBinaryContents();
        binaryContents.removeIf(b -> b.getId().equals(id));
        saveBinaryContents(binaryContents);
    }
}