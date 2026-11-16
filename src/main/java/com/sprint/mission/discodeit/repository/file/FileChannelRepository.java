package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {

    private final Path channelDirectory;
    private final FileLockProvider fileLockProvider;

    public FileChannelRepository() {
        this(Path.of("data", "channels"), new FileLockProvider());
    }

    public FileChannelRepository(Path channelDirectory) {
        this(channelDirectory, new FileLockProvider());
    }

    public FileChannelRepository(
            Path channelDirectory,
            FileLockProvider fileLockProvider
    ) {
        this.channelDirectory = channelDirectory;
        this.fileLockProvider = fileLockProvider;

        try {
            Files.createDirectories(channelDirectory);
        } catch (IOException e) {
            throw new RuntimeException("채널 디렉터리 생성에 실패했습니다.", e);
        }
    }

    private Path fileFor(UUID channelId) {
        return channelDirectory.resolve(channelId + ".ser");
    }

    private Channel readChannel(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(file))) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        Path file = fileFor(channelId);
        ReentrantLock lock = fileLockProvider.getLock(file);

        lock.lock();
        try {
            if (Files.notExists(file)) {
                return Optional.empty();
            }

            return Optional.of(readChannel(file));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> allChannels = new ArrayList<>();

        try (Stream<Path> files = Files.list(channelDirectory)) {
            files.filter(file ->
                            file.getFileName().toString().endsWith(".ser"))
                    .forEach(file -> {
                        ReentrantLock lock =
                                fileLockProvider.getLock(file);

                        lock.lock();
                        try {
                            if (Files.exists(file)) {
                                allChannels.add(readChannel(file));
                            }
                        } finally {
                            lock.unlock();
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return allChannels;
    }

    @Override
    public Channel save(Channel channel) {
        Path file = fileFor(channel.getId());
        ReentrantLock lock = fileLockProvider.getLock(file);

        lock.lock();
        try {
            try (ObjectOutputStream out = new ObjectOutputStream(
                    Files.newOutputStream(file))) {
                out.writeObject(channel);
                return channel;
            } catch (IOException e) {
                throw new RuntimeException("채널 파일 저장에 실패했습니다.", e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void deleteById(UUID channelId) {
        Path file = fileFor(channelId);
        ReentrantLock lock = fileLockProvider.getLock(file);

        lock.lock();
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 삭제에 실패했습니다.", e);
        } finally {
            lock.unlock();
        }
    }
}
