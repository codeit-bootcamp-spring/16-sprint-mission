package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User createUser(String name, int age) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다");
        }
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상이어야 합니다.");
        }

        User user = new User(name, age);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }


    @Override
    public User updateUser(UUID id, String name, int age) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 비어있을 수 없습니다.");
        }
        if (age < 0) {
            throw new IllegalArgumentException("나이는 0 이상이어야 합니다.");
        }

        user.update(name, age);
        userRepository.save(user);

        channelRepository.findAllByUserId(id)
                .forEach(channel -> {
                    User channelUser = channel.getUsers().stream()
                            .filter(u -> u.getId().equals(id))
                            .findFirst()
                            .orElseThrow(() ->
                                    new IllegalStateException(
                                            "채널에 사용자가 존재하지 않습니다."));

                    channelUser.update(name, age);
                    channelRepository.save(channel);
                });

        messageRepository.findAllByUserId(id)
                .forEach(message -> {
                    message.getUser().update(name, age);
                    messageRepository.save(message);
                });

        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );
        userRepository.delete(user);
    }

    @Override
    public void addChannelToUser(UUID id, Channel channel) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다.")
                );
        user.addChannel(channel);
        userRepository.save(user);
    }

    @Override
    public void removeChannelFromUser(UUID id, Channel channel) {
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다")
                );
        user.removeChannel(channel);
        userRepository.save(user);
    }

    @Override
    public void addMessageToUser(UUID id, Message message){
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다")
                );
        user.addMessage(message);
        userRepository.save(user);
    }

    @Override
    public void removeMessageFromUser(UUID id, Message message){
        User user = findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("존재하지 않는 사용자입니다")
                );
        user.removeMessage(message);
        userRepository.save(user);
    }
}
