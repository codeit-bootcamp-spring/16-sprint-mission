package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final Path DIRECTORY =
            Paths.get(System.getProperty("user.dir"), "data", "channels");

    public FileChannelRepository() {
        try {
            Files.createDirectories(DIRECTORY);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Channel load(Path filePath) {
        try (
                FileInputStream fis =
                        new FileInputStream(filePath.toFile());
                ObjectInputStream ois =
                        new ObjectInputStream(fis)
        ) {
            return (Channel) ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        Path filePath =
                DIRECTORY.resolve(channel.getId() + ".ser");

        try (
                FileOutputStream fos =
                        new FileOutputStream(filePath.toFile());
                ObjectOutputStream oos =
                        new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path filePath = DIRECTORY.resolve(id + ".ser");

        if (!Files.exists(filePath)) {
            return Optional.empty();
        }

        return Optional.of(load(filePath));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return findAll().stream()
                .filter(channel ->
                        channel.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        try (var paths = Files.list(DIRECTORY)) {

            return paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.toString().endsWith(".ser"))
                    .map(this::load)
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(channel ->
                        channel.getUsers().stream()
                                .anyMatch(user ->
                                        user.getId().equals(userId)))
                .toList();
    }

    @Override
    public void delete(Channel channel) {
        Path filePath =
                DIRECTORY.resolve(channel.getId() + ".ser");

        try {
            Files.delete(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}