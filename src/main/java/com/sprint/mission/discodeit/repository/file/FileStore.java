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
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

public class FileStore<T> {
  private final Path path;
  private final ReentrantLock lock;

  public FileStore(String directory, String fileName, FileLockProvider lockProvider) {
    this.path = Paths.get(directory, fileName);
    this.lock = lockProvider.getLock(path);
  }

  private void write(Map<UUID, T> data) {
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
    lock.lock();
    try {
      if (!Files.exists(path)) return new HashMap<>();
      try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
        return (Map<UUID, T>) ois.readObject();
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException(e);
      }
    } finally {
      lock.unlock();
    }
  }

  public <R> R update(Function<Map<UUID, T>, R> action) {
    lock.lock();
    try {
      Map<UUID, T> data = load(); // ReentrantLock이라 이미 잠근 상태에서 또 잠가도 막히지 않음
      R result = action.apply(data);
      write(data);
      return result;
    } finally {
      lock.unlock();
    }
  }
}
