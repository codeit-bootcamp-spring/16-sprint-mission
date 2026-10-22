package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.nio.file.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileChannelRepository implements ChannelRepository {
    final Path PATH = Paths.get("channels.dat");
    Map<UUID, Channel> data = new ConcurrentHashMap<>();

    public FileChannelRepository() {loadFile();}

    @Override
    public void save(Channel channel) {
        if (channel == null || channel.getId() == null) {
            throw new NullPointerException("채널 데이터가 올바르지 않거나 ID가 없습니다.");
        }
        data.put(channel.getId(), channel);
        saveFile();
    }

    @Override
    public Channel findId(UUID id) {
        if (id == null) {
            throw new NullPointerException("조회하려는 채널 ID가 null입니다.");
        }

        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteId(UUID id) {
        if (id == null) {
            throw new NullPointerException("삭제하려는 채널 ID가 null입니다.");
        }
        data.remove(id);
        saveFile();
    }

    private void saveFile() {
        try (OutputStream os = Files.newOutputStream(PATH);
                ObjectOutputStream oos = new ObjectOutputStream(os))
         {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        if (!Files.exists(PATH)) return;

        try (InputStream is = Files.newInputStream(PATH);
                ObjectInputStream ois = new ObjectInputStream(is))
         {
            this.data = (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
