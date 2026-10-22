package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserService implements UserService {

    private final UserRepository userRepository;
    private MessageService messageService;
    private ChannelService channelService;

    public FileUserService() {
        this.userRepository = new FileUserRepository();
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public User create(User user) {
        validateUser(user);

        boolean emailExists = userRepository.findAll()
                .stream()
                .anyMatch(existingUser ->
                        existingUser.getEmail().equals(user.getEmail()));

        if (emailExists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        return save(user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID id, String name, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        boolean emailExists = userRepository.findAll()
                .stream()
                .anyMatch(existingUser ->
                        !existingUser.getId().equals(id)
                                && existingUser.getEmail().equals(email));

        if (emailExists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        user.update(name, email);
        return save(user);
    }

    @Override
    public void addChannel(UUID userId, UUID channelId) {
        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.addChannel(channelId);
        save(user);
    }

    @Override
    public void removeChannel(UUID userId, UUID channelId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.removeChannel(channelId);
        save(user);
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }
    }

    private User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 해당 사용자가 작성한 메시지 삭제
        if (messageService != null) {
            for (Message message : new ArrayList<>(messageService.findAll())) {
                if (message.getAuthorId().equals(id)) {
                    messageService.delete(message.getId());
                }
            }
        }

        // 참여 중인 채널에서 사용자 관계 제거
        if (channelService != null) {
            for (UUID channelId : new ArrayList<>(user.getChannelIds())) {
                channelService.leave(channelId, id);
            }
        }

        userRepository.deleteById(id);
    }
}