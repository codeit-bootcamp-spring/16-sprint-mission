package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFUserService implements UserService {

    // private final List<User> data;

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public JCFUserService(UserRepository userRepository, MessageRepository messageRepository, ChannelRepository channelRepository) {
        // this.data = new ArrayList<>();
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
    }

/*
    @Override
    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = new User(username);
        this.data.add(user);
        return user;
    }
*/

    @Override
    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = this.userRepository.save(new User(username));
        return user;
    }

    /* User의 UUID를 가져오기 위한 메서드입니다. */
    /*
    public UUID getUserUUID(int index) {
        List<User> list = this.data;
        if (index < 0 || index >= list.size()) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return list.get(index).getId();
    }
     */

    @Override
    public List<User> findAll() {
        /*
        List<User> userList = this.data.stream()
                .toList();

        return userList;
        */

        List<User> userList = this.userRepository.getAllUsers();

        return userList;
    }

    @Override
    public User findById(UUID id) {
        /*
        User user = this.data.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException());

        return user;
        */
        User user = this.userRepository.getUser(id);

        return user;

    }

    @Override
    public User updateUsername(UUID id, String username) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        User user = this.findById(id);
        user.updateUsername(username);

        List< Channel> channelList = this.channelRepository.getChannels();

        channelList.stream()
                .filter(channel -> channel.getUsers().contains(user))
                .forEach(channel -> {
                    channel.getUsers().stream()
                            .filter(u -> u.getId().equals(user.getId()))
                            .forEach(u -> u.updateUsername(username));

                    this.channelRepository.save(channel);
                });

        List<Message> messageList = this.messageRepository.getMessages();

        messageList.stream()
                .filter(message -> message.getSender().equals(user))
                .forEach(message -> {
                    message.getSender().updateUsername(username);

                    this.messageRepository.save(message);
                });

        return this.userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) {
        /*
        User user = this.findById(id);
        this.data.remove(user);
         */
        try {
            this.userRepository.removeUser(id);
        } catch (NoSuchElementException exception) {
            throw new NoSuchElementException("User with id " + id + " does not exist");
        }
    }
}
