package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 기본 요구사항의 File 서비스 예제입니다.
 * 채널 업무 규칙과 파일 입출력을 한 구현체에서 보여줍니다.
 * 심화 과제에서는 BasicChannelService가 저장을 ChannelRepository에 맡깁니다.
 */
public class FileChannelService {

    private final Path directory;

    public FileChannelService() {
        this(Paths.get(System.getProperty("user.dir"), "data", "channels"));
    }

    /** 저장 폴더를 직접 지정할 수 있는 생성자입니다. */
    public FileChannelService(Path directory) {
        this.directory = directory;
        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new IllegalStateException("채널 저장 디렉터리를 만들 수 없습니다: " + directory, e);
        }
    }

    public void create(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel cannot be null.");
        }

        Path file = fileFor(channel.getId());
        if (Files.exists(file)) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + channel.getId());
        }

        write(file, channel);
    }

    public Channel findById(UUID id) {
        Path file = fileFor(id);
        if (!Files.exists(file)) {
            throw new IllegalArgumentException("Not Found channel id: " + id);
        }
        return read(file);
    }

    public List<Channel> findAll() {
        List<Channel> channels = new ArrayList<>();
        try (var files = Files.list(directory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> channels.add(read(path)));
        } catch (IOException e) {
            throw new IllegalStateException("채널 파일 목록을 읽을 수 없습니다: " + directory, e);
        }
        return List.copyOf(channels);
    }

    public void update(UUID id, String name, String description) {
        Channel existingChannel = findById(id);
        existingChannel.update(name, description);
        write(fileFor(id), existingChannel);
    }

    public void delete(UUID id) {
        try {
            if (!Files.deleteIfExists(fileFor(id))) {
                throw new IllegalArgumentException("Not Found channel id: " + id);
            }
        } catch (IOException e) {
            throw new IllegalStateException("채널 파일을 삭제할 수 없습니다: " + id, e);
        }
    }

    private Path fileFor(UUID id) {
        return directory.resolve(id + ".ser");
    }

    private void write(Path file, Channel channel) {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(channel);
        } catch (IOException e) {
            throw new IllegalStateException("채널 파일을 저장할 수 없습니다: " + file, e);
        }
    }

    private Channel read(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("채널 파일을 읽을 수 없습니다: " + file, e);
        }
    }
}
