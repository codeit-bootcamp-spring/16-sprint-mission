package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(User user) {
        validateEmailUnique(user.getEmail(), null);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public List<User> findAllByChannelId(UUID channelId) {
        return userRepository.findAll().stream()
                .filter(user -> user.hasChannel(channelId))
                .toList();
    }

    @Override
    public User update(UUID id, String username, String email, String password) {
        User user = findById(id);
        validateEmailUnique(email, id);
        user.update(username, email, password);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);
        if (!user.getChannels().isEmpty()) {
            throw new IllegalArgumentException("참여 중인 채널이 있어 삭제할 수 없습니다.");
        }

        if (!user.getMessages().isEmpty()) {
            throw new IllegalArgumentException("작성한 메시지가 있어 삭제할 수 없습니다.");
        }
        userRepository.deleteById(id);
    }

    @Override
    public void addChannel(UUID id, UUID channelId) {
        User user = findById(id);
        user.addChannel(channelId);
        userRepository.save(user);
    }

    @Override
    public void removeChannel(UUID id, UUID channelId) {
        User user = findById(id);
        user.removeChannel(channelId);
        userRepository.save(user);
    }

    @Override
    public void addMessage(UUID id, UUID messageId) {
        User user = findById(id);
        user.addMessage(messageId);
        userRepository.save(user);
    }

    @Override
    public void removeMessage(UUID id, UUID messageId) {
        User user = findById(id);
        user.removeMessage(messageId);
        userRepository.save(user);
    }

    private void validateEmailUnique(String email, UUID excludeUserId) {
        for (User value : userRepository.findAll()) {
            if (value.getEmail().equals(email) && !value.getId().equals(excludeUserId)) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
        }
    }
}
