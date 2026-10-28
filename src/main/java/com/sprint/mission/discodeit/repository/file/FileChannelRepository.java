
package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private final File directory;

    public FileChannelRepository() {
        this.directory = new File("data/channels");

        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("채널 폴더를 생성할 수 없습니다.");
        }
    }

    private File getFile(UUID id) {
        return new File(directory, id + ".ser");
    }

    @Override
    public void save(Channel channel) {
        File file = getFile(channel.getId());

        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(channel);
        } catch (IOException e) {
            throw new IllegalStateException("채널을 저장할 수 없습니다.", e);
        }
    }

    private Channel load(File file) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            return (Channel) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("채널을 불러올 수 없습니다.", e);
        }
    }

    @Override
    public Channel getById(UUID id) {
        File file = getFile(id);

        if (!file.exists()) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        return load(file);
    }

    @Override
    public List<Channel> getAll() {
        List<Channel> channels = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files == null) {
            throw new IllegalStateException("채널 폴더를 읽을 수 없습니다.");
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".ser")) {
                channels.add(load(file));
            }
        }

        return channels;
    }

    @Override
    public void delete(UUID id) {
        File file = getFile(id);

        if (!file.exists()) {
            throw new IllegalArgumentException("채널을 찾을 수 없습니다.");
        }

        if (!file.delete()) {
            throw new IllegalStateException("채널을 삭제할 수 없습니다.");
        }
    }
}
