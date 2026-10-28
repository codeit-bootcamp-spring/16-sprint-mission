package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final Path filePath = Path.of("data","channels.ser");

    public FileChannelRepository() {
        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }
    private void saveChannels(List<Channel> channels) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(channels);
        } catch (IOException e) {
            throw new IllegalStateException("채널 목록을 저장할 수 없습니다", e);
        }
    }
    private List<Channel> loadChannels() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<Channel>) in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("채널 목록을 읽을 수 없습니다",e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        List<Channel> channels = loadChannels();
        channels.removeIf(c -> c.getId().equals(channel.getId()));
        channels.add(channel);
        saveChannels(channels);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return loadChannels().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return loadChannels();
    }

    @Override
    public void deleteById(UUID id) {
        List<Channel> channels = loadChannels();
        channels.removeIf(c -> c.getId().equals(id));
        saveChannels(channels);
    }
}