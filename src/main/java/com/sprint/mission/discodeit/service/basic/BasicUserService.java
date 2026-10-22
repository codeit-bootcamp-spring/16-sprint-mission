package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicUserService(
            UserRepository userRepository,
            ChannelRepository channelRepository,
            MessageRepository messageRepository
    ) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    private void validatePassword(String origin, String entered) {
        if (origin == null || !origin.equals(entered)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private void validateDuplicateEmail(String email) {
        if (email == null) return;

        boolean exists = userRepository.findAll().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
        if (exists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("유저가 null입니다.");
        }
        if (userRepository.findById(user.getId()) != null) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        validateDuplicateEmail(user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUsername(UUID id, String username) {
        User user = getUserById(id);
        user.changeUsername(username);
        return userRepository.save(user);
    }

    @Override
    public User updatePassword(UUID id, String originPassword, String newPassword) {
        User user = getUserById(id);
        validatePassword(user.getPassword(), originPassword);
        user.changePassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public User updateEmail(UUID id, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }

        User user = getUserById(id);

        if (email.equals(user.getEmail())) {
            throw new IllegalArgumentException("현재 사용 중인 이메일과 같습니다.");
        }

        validateDuplicateEmail(email);

        user.changeEmail(email);
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id, String password) {
        User user = getUserById(id);
        validatePassword(user.getPassword(), password);

        // 사용자가 작성한 메시지를 채널과 사용자 데이터에서도 제거
        for (Message message : messageRepository.findByUserId(id)) {
            Channel channel = channelRepository.findById(message.getChannel().getId());
            if (channel != null) {
                channel.removeChannelMessages(message);
                channelRepository.save(channel);
            }
            user.removeMessage(message);
            messageRepository.deleteById(message.getId());
        }
        userRepository.save(user);

        // 참여 중인 채널에서 사용자를 제거하거나 방장 채널을 삭제
        for (Channel channel : channelRepository.findByParticipantId(id)) {
            if (channel.getCreator().getId().equals(id)) {
                deleteChannelData(channel);
            } else {
                channel.leaveChannel(user);
                channelRepository.save(channel);
                userRepository.save(user);
            }
        }

        userRepository.deleteById(id);
    }

    private void deleteChannelData(Channel channel) {
        for (Message message : messageRepository.findByChannelId(channel.getId())) {
            User sender = userRepository.findById(message.getSender().getId());
            if (sender != null) {
                sender.removeMessage(message);
                userRepository.save(sender);
            }
            channel.removeChannelMessages(message);
            messageRepository.deleteById(message.getId());
        }

        for (User participant : channel.getChannelParticipants()) {
            channel.leaveChannel(participant);
            User savedParticipant = userRepository.findById(participant.getId());
            if (savedParticipant != null) {
                savedParticipant.removeChannel(channel);
                userRepository.save(savedParticipant);
            }
        }

        channelRepository.deleteById(channel.getId());
    }

    @Override
    public void deleteEmail(UUID id) {
        User user = getUserById(id);
        user.deleteEmail();
        userRepository.save(user);
    }
}
