package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.basic.BasicEntityService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {

    private final Path filePath = Path.of("data", "channels.ser");
    private final FileUserService fileUserService;

    public FileChannelService(FileUserService fileUserService) {
        this.fileUserService = fileUserService;
        try {
            Files.createDirectories(filePath.getParent());
        } catch (IOException e) {
            throw new IllegalStateException("저장 폴더를 생성할 수 없습니다", e);
        }
    }

    private void saveChannel (List<Channel> channels) {
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
            return (List<Channel>)  in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("채널 목록을 읽을 수 없습니다",e);
        }
    }
    public void addMessage(UUID channelId, Message message) {
        List<Channel> channels = loadChannels();

        Channel channel = channels.stream().filter(c -> c.getId().equals(channelId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        channel.getMessages().add(message);
        saveChannel(channels);
    }

    public void removeMessage(UUID channelId, UUID messageId) {
        List<Channel> channels = loadChannels();

        Channel channel = channels.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없음"));

        channel.getMessages().removeIf(m -> m.getId().equals(messageId));
        saveChannel(channels);
    }

    public void updateMessage(UUID channelId, UUID messageId, String comment) {
        List<Channel> channels = loadChannels();

        Channel channel = channels.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없음"));

        Message message = channel.getMessages().stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("메시지를 찾을 수 없음"));

        message.update(comment);
        saveChannel(channels);
    }

    @Override
    public Channel create(String name) {
        BasicEntityService entities = new BasicEntityService(
                new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        List<Channel> channels = entities.getChannels();

        if(channels.stream().anyMatch(c -> c.getName().equals(name))){
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        Channel channel = new Channel(name);
        channels.add(channel);
        entities.saveAll();
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return loadChannels().stream().filter(channel -> channel.getId().equals(id)).findFirst().orElseThrow(()->
                new IllegalArgumentException("채널을 찾을 수 없음"));
    }

    @Override
    public List<Channel> findAll() {
        return loadChannels();
    }

    @Override
    public Channel update(UUID id, String name) {
        BasicEntityService entities = new BasicEntityService(
                new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        List<Channel> channels = entities.getChannels();

        Channel channel = channels.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        if(channels.stream().anyMatch(otherChannel ->
                !otherChannel.getId().equals(id)&&otherChannel.getName().equals(name))){
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        channel.update(name);
        entities.saveAll();
        return channel;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        List<Channel> channels = entities.getChannels();

        Channel channel = channels.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        channels.remove(channel);
        entities.saveAll();
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        BasicEntityService entities = new BasicEntityService(
                new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        Channel channel = entities.getChannels().stream().filter(c -> c.getId().equals(channelId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        User user = entities.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(channel.getUsers().stream().anyMatch(u -> u.getId().equals(userId))){
            throw new IllegalArgumentException("이미 참여한 사용자입니다");
        }

        channel.getUsers().add(user);
        user.getChannels().add(channel);
        entities.saveAll();
    }


}