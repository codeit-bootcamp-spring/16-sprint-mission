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

        /* 1. User 객체 업데이트 */

        User user = this.findById(id);
        user.updateUsername(username);

        /* 2. Channel 객체의 User 객체들 업데이트 */
        // Channel 객체에는 Set<User> users로 User 객체들이 저장됩니다.
        List<Channel> channelList = this.channelRepository.getChannels();

        channelList.stream()
                // 각 Channel에 모든 User들 중에 업데이트의 대상이 되는 User가 있는 Channel만 선택
                .filter(channel -> channel.getUsers().contains(user))
                // 각 Channel에서 모든 User를 가져온 다음에, Userdml ID가 업데이트의 대상이 되는
                // User의 ID와 같다면
                // 이 User들의 이름을 업데이트합니다.
                .forEach(channel -> {
                    channel.getUsers().stream()
                            .filter(u -> u.getId().equals(user.getId()))
                            .forEach(u -> u.updateUsername(username));
                    // JCF 방식에서는 굳이 save()를 호출할 필요가 없지만
                    // File 방식에서는 변경된 Channel객체들을 다시 직렬화해서 저장해야하기 때문에
                    // save()를 호출해야합니다.
                    this.channelRepository.save(channel);
                });

        List<Message> messageList = this.messageRepository.getMessages();

        /**
         * JCF 방식은 객체를 사용하기 때문에, User를 한번 업데이트하면
         * User를 참조하고 있는 다른 곳에서 업데이트할 필요는없습니다.
         * 하지만 File 방식은 데이터가 기록되는 것 이기 때문에
         * User를 참조하는 모든곳을 바꿔줘야합니다.
         */

        /* 3. Message 객체에 User객체를 업데이트 */
        messageList.stream()
                .filter(message -> message.getSender().equals(user))
                .forEach(message -> {
                    message.getSender().updateUsername(username);

                    this.messageRepository.save(message);
                });

        // 각 메세지의, 채널의, 유저
        messageList.stream()
                .filter(message -> message.getChannel() != null)
                .filter(message -> message.getChannel().getUsers().stream()
                        .anyMatch(u -> u.getId().equals(user.getId())))
                .forEach(message -> {
                    message.getChannel().getUsers().stream()
                            .filter(u -> u.getId().equals(user.getId()))
                            .forEach(u -> u.updateUsername(username));

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
