package com.sprint.mission.discodeit.service.file.file_service_impl;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelService implements ChannelService {

    private static final String FILE_PATH = "temp/channels.ser";

    private final Map<UUID, Channel> data;
    private final UserService userService;
    private final MessageService messageService;

    public FileChannelService(UserService userService, MessageService messageService) throws IOException {
        this.data = new HashMap<>();
        this.userService = userService;
        this.messageService = messageService;

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            Object findObject = ois.readObject();
            Map<UUID, Channel> channelMap = (Map<UUID, Channel>) findObject;
            data.putAll(channelMap);
        } catch (FileNotFoundException e) {
            Path channelFile = Path.of(FILE_PATH);
            Files.createFile(channelFile);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel create(User user, ChannelType channelType, String title, String description) {
        if (userService.findById(user.getId()).isEmpty()) {
            throw new NoSuchElementException("등록되지 않은 User는 Channel을 생성할 수 없습니다.");
        }

        Channel newChannel = new Channel(channelType, title, description);
        data.put(newChannel.getId(), newChannel);
        user.addChannel(newChannel);
        newChannel.addUser(user);
        save();

        return newChannel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            System.out.println("해당 채널은 존재하지 않습니다.");
            return Optional.empty();
        }
        return Optional.of(channel);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<Channel> updateChannelType(UUID id, ChannelType newChannelType) {
        Optional<Channel> channel = findById(id);
        if (channel.isEmpty()) {
            System.out.println("존재하지 않는 채널의 타입을 바꿀 수 없습니다.");
            return channel;
        }
        channel.get().setChannelType(newChannelType);
        save();
        return channel;
    }

    @Override
    public Optional<Channel> updateTitle(UUID id, String newTitle) {
        Optional<Channel> channel = findById(id);
        if (channel.isEmpty()) {
            System.out.println("존재하지 않는 채널의 제목을 바꿀 수 없습니다.");
            return channel;
        }
        channel.get().setTitle(newTitle);
        save();
        return channel;
    }

    @Override
    public Optional<Channel> updateDescription(UUID id, String newDescription) {
        Optional<Channel> channel = findById(id);
        if (channel.isEmpty()) {
            System.out.println("존재하지 않는 채널의 설명을 바꿀 수 없습니다.");
            return channel;
        }
        channel.get().setDescription(newDescription);
        save();
        return channel;
    }

    @Override
    public void delete(UUID id) {
        Channel removed = data.get(id);
        if (removed == null) {
            throw new NoSuchElementException();
        }

        for (Message message : new ArrayList<>(removed.getChannelMessages())) {
            messageService.delete(message.getId());
        }

        for (User user : removed.getUsers()) {
            user.deleteChannel(removed);
        }
        save();
        data.remove(id);
    }

    private void save() {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
