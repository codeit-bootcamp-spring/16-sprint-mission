package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public FileChannelService(ChannelRepository channelRepository, UserService userService
    ) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String name, List<UUID> userIds) {

        List<User> users = userIds.stream()
                .map(userId -> userService.findById(userId)
                        .orElseThrow(
                                () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                        ))
                .toList();

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어있을 수 없습니다.");
        }

        if (userIds.isEmpty()) {
            throw new IllegalArgumentException("채널에는 최소 1명의 사용자가 존재해야 합니다.");
        }

        if (userIds.stream()
                .distinct()
                .count() != userIds.size()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        Channel channel = new Channel(name, users);

        users.forEach(user ->
                userService.addChannelToUser(user.getId(), channel)
        );

        return channelRepository.save(channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channelRepository.findById(id);
    }

    @Override
    public Optional<Channel> findByName(String name) {
        return channelRepository.findByName(name);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return channelRepository.findAllByUserId(userId);
    }

    @Override
    public Channel updateChannel(UUID id, String name) {
        Channel channel = findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어있을 수 없습니다.");
        }

        channel.update(name);
        return channelRepository.save(channel);
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 채널입니다."));

        channel.getUsers()
                .forEach(user ->
                        userService.removeChannelFromUser(
                                user.getId(), channel));

        channelRepository.delete(channel);
    }

    @Override
    public void addUserToChannel(UUID id, List<UUID> userIds) {
        Channel channel = findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        List<User> users = userIds.stream()
                .map(userId -> userService.findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 사용자입니다.")))
                .toList();

        if (userIds.stream().distinct().count() != userIds.size()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        for (User user : users) {
            boolean exists = channel.getUsers().stream()
                    .anyMatch(channelUser ->
                            channelUser.getId().equals(user.getId()));

            if (exists) {
                throw new IllegalArgumentException(
                        "이미 채널에 존재하는 사용자입니다.");
            }
        }

        users.forEach(user -> {
            channel.addUser(user);
            userService.addChannelToUser(user.getId(), channel);
        });

        channelRepository.save(channel);
    }

    @Override
    public void removeUserFromChannel(UUID id, List<UUID> userIds) {
        Channel channel = findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        List<User> users = userIds.stream()
                .map(userId -> userService.findById(userId)
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 사용자입니다.")))
                .toList();

        if (userIds.stream().distinct().count() != userIds.size()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        for (User user : users) {
            boolean exists = channel.getUsers().stream()
                    .anyMatch(channelUser ->
                            channelUser.getId().equals(user.getId()));

            if (!exists) {
                throw new IllegalArgumentException(
                        "채널에 존재하는 사용자만 제거 가능합니다.");
            }
        }

        if (users.size() >= channel.getUsers().size()) {
            throw new IllegalArgumentException(
                    "채널에는 최소 1명의 사용자가 존재해야 합니다.");
        }

        users.forEach(user -> {
            channel.removeUser(user);
            userService.removeChannelFromUser(user.getId(), channel);
        });

        channelRepository.save(channel);
    }

    @Override
    public void addMessageToChannel(UUID id, Message message) {
        Channel channel = findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        channel.addMessage(message);

        channelRepository.save(channel);
    }

    @Override
    public void removeMessageFromChannel(UUID id, Message message) {
        Channel channel = findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 채널입니다."));

        channel.removeMessage(message);

        channelRepository.save(channel);
    }
}
