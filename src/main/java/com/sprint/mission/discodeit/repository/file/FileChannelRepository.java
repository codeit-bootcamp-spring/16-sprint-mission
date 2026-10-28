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
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelRepository implements ChannelRepository {

    private final Path channelDirectory;

    public FileChannelRepository() {
        this(Path.of("data", "channels"));
    }

    public FileChannelRepository(Path channelDirectory) {
        this.channelDirectory = channelDirectory;

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
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 파일을 읽는 데 실패했습니다.", e);
        }
    }

    @Override
    public Channel findById(UUID channelId) {
        Path file = fileFor(channelId);

        if (Files.notExists(file)) {
            return null;
        }

        return readChannel(file);
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();

        try (Stream<Path> files = Files.list(channelDirectory)) {
            files.filter(file -> file.getFileName().toString().endsWith(".ser"))
                    .forEach(file -> channels.add(readChannel(file)));
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return channels;
    }

    @Override
    public Channel save(Channel channel) {
        Path file = fileFor(channel.getId());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(channel);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 저장에 실패했습니다.", e);
        }
    }

    @Override
    public void deleteById(UUID channelId) {
        try {
            Files.deleteIfExists(fileFor(channelId));
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 삭제에 실패했습니다.", e);
        }
    }

    @Override
    public List<Channel> findByCreatorId(UUID creatorId) {
        return findAll().stream()
                .filter(channel -> channel.getCreatorId().equals(creatorId))
                .toList();
    }

    @Override
    public List<Channel> findByParticipantId(UUID userId) {
        return findAll().stream()
                .filter(channel -> channel.getParticipantIds().contains(userId))
                .toList();
    }
}
