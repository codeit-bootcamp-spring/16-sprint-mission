package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private UserService userService;

    public FileChannelService() {
        this.writeChannelFile(new HashMap<>());
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        Map<UUID, Channel> channels = this.readChannelFile();
        channels.put(channel.getId(), channel);
        this.writeChannelFile(channels);
        return channel;
    }

    @Override
    public Channel update(UUID id, Channel newChannel) {
        Map<UUID, Channel> channels = this.readChannelFile();
        Channel channel = Optional.ofNullable(channels.get(id)).orElseThrow(
                () -> new NoSuchElementException("수정할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        channel.update(newChannel);
        this.writeChannelFile(channels);
        return channel;
    }

    @Override
    public void addUserToChannel(UUID channelId, User user) {
        if (user == null) throw new NullPointerException("채널에 추가할 유저가 참조하는 값이 없습니다.");

        // 유저가 존재하는지 확인
        this.userService.findById(user.getId())
                .orElseThrow(() -> new NoSuchElementException("채널에 추가할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Map<UUID, Channel> channels = this.readChannelFile();
        Channel channel = Optional.ofNullable(channels.get(channelId)).orElseThrow(
                () -> new NoSuchElementException("유저를 추가할 채널이 존재하지 않습니다."));

        channel.validateNotDeleted();
        channel.addUser(user);

        // 채널에 유저 추가 시 유저에도 채널 추가
        user.addChannel(channel);
        this.userService.update(user.getId(), user);

        this.update(channelId, channel);
    }

    @Override
    public void removeUserFromChannel(UUID channelId, UUID userId) {
        // 유저가 존재하는지 확인
        User user = this.userService.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("채널에서 탈퇴시킬 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        Channel channel = this.findById(channelId).orElseThrow(() -> new NoSuchElementException("유저가 탈퇴할 채널이 " +
                "존재하지 않습니다."));
        channel.validateNotDeleted();

        // 채널의 유저 탈퇴 시 유저에도 채널 지우기
        channel.getUsers().values()
                .stream()
                .filter(joinedUser -> joinedUser.getId().equals(userId))
                .findFirst()
                .ifPresent(joinedUser -> {
                    joinedUser.removeChannel(channelId);
                    this.userService.update(joinedUser.getId(), joinedUser);
                });

        channel.removeUser(userId);
        this.update(channelId, channel);
    }


    @Override
    public Optional<Channel> findById(UUID id) {
        Map<UUID, Channel> channels = this.readChannelFile();
        return Optional.ofNullable(channels.get(id));
    }

    @Override
    public List<Channel> findAll() {
        Map<UUID, Channel> channels = this.readChannelFile();
        return channels.values().stream().toList();
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Channel> channels = this.readChannelFile();
        Channel channel = Optional.ofNullable(channels.get(id)).orElseThrow(
                () -> new NoSuchElementException("삭제할 채널이 존재하지 않습니다."));
        channel.validateNotDeleted();

        // 채널 삭제 시 유저에도 채널 삭제
        channel.getUsers().values().forEach(user -> {
            user.removeChannel(id);
            this.userService.update(user.getId(), user);
        });

        channel.softDelete();
        this.update(id, channel);
    }

    public Map<UUID, Channel> readChannelFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./data/channel.ser"))) {
            @SuppressWarnings("unchecked")
            Map<UUID, Channel> channels = (Map<UUID, Channel>) ois.readObject();
            return channels;
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError("직렬화된 객체의 클래스를 찾을 수 없습니다.");
        }
    }

    public void writeChannelFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./data/channel.ser"))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new UncheckedIOException("파일 입출력 도중 예외가 발생했습니다.", e);
        }
    }

    public void setUserService(UserService userService) {
        if (userService == null) throw new NullPointerException("유저 서비스가 참조하는 값이 없습니다.");
        this.userService = userService;
    }
}
