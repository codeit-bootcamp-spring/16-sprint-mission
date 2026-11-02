package com.sprint.mission.discodeit.repository.file;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileStore<T> {
    private final Path path;

    public FileStore(String directory, String fileName) {
        this.path = Paths.get(directory, fileName);
    }

    public void write(Map<UUID, T> data) {
        try {
            Files.createDirectories(path.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<UUID, T> load() {
        if (!Files.exists(path)) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            return (Map<UUID, T>) ois.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}