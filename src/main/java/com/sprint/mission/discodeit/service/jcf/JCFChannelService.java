package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelService implements ChannelService {

    private final List<Channel> channels = new ArrayList<>();

    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, List<UUID> userIds) {
        List<User> users = userIds.stream() // userId들을 받아서 값이 실제로 존재하는지 확인 후 List<User>로 담기
                .map(userId -> userService.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")))
                .toList();

        Channel channel = new Channel(name, users); // 새로운 채널 생성
        users.forEach(user ->
                userService.addChannelToUser(user.getId(), channel)); // 유저 도메인에게도 해당 채널 생성(연결)
        channels.add(channel); // 채널 리스트에 채널 추가
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channels.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return channels.stream()
                .filter(channel -> channel.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return channels;
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return channels.stream()
                .filter(channel ->
                        channel.getUsers().stream()
                                .anyMatch(user ->
                                        user.getId().equals(userId))
                )
                .toList();
    }

    @Override
    public Channel updateChannel(UUID id, String name) {
        Channel channel = findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채널입니다.")
        );
        channel.update(name);
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채널입니다.")
        );
        channel.getUsers()
                        .forEach(user -> userService.removeChannelFromUser(user.getId(), channel));

        channels.remove(channel);
    }

    @Override
    public void addUserToChannel(UUID id, List<UUID> userIds) {
        Channel channel = findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채널입니다.")
        ); //입력 받은 채널이 존재하는지 확인

        List<User> users = userIds.stream()
                .map(userId -> userService.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")))
                .toList(); // 입력받은 userId들이 존재하는지 확인

        users.forEach(user -> {
            channel.addUser(user); // 위에서 받은 채널에 각 유저들을 추가하기
            userService.addChannelToUser(user.getId(), channel); // 각 유저들에게 채널 추가하기
        });
    }

    @Override
    public void removeUserFromChannel(UUID id, List<UUID> userIds) {
        Channel channel = findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 채널입니다.")
        );

        List<User> users = userIds.stream()
                .map(userId -> userService.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다.")))
                .toList();

        for (User user : users) {
            if (!channel.getUsers().contains(user)) {
                throw new IllegalArgumentException("채널에 존재하는 사용자만 제거 가능합니다.");
            }
        }

        if (users.size() >= channel.getUsers().size()){
            throw new IllegalArgumentException("채널에는 최소 1명이 있어야 합니다.");
        }

        users.forEach(user -> {
            channel.removeUser(user);
            userService.removeChannelFromUser(user.getId(), channel);
        });
    }

    @Override
    public void addMessageToChannel(UUID id, Message message) {
        Channel channel = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 채널입니다.")
                );
        channel.addMessage(message);
    }

    @Override
    public void removeMessageFromChannel(UUID id, Message message) {
        Channel channel = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 채널입니다.")
                );
        channel.removeMessage(message);
    }
}
