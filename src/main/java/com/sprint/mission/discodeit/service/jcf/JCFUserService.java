package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userData;

    private ChannelService channelService;
    private MessageService messageService;

    public JCFUserService() {
        this.userData = new HashMap<>();
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    // 비밀번호 검증
    private void validatePassword(String origin, String entered) {
        if (origin == null || !origin.equals(entered)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    // 이메일 중복 검증
    private void validateDuplicateEmail(String email) {
        if (email == null) return;

        boolean exists = userData.values().stream()
                .anyMatch(u -> email.equals(u.getEmail()));
        if (exists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    // 생성
    @Override
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("유저가 null입니다.");
        }
        if (userData.containsKey(user.getId())) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }
        validateDuplicateEmail(user.getEmail());

        userData.put(user.getId(), user);
        return user;
    }

    // 조회
    @Override
    public User getUserById(UUID userId) {
        User user = userData.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return userData.values().stream()
                .toList();
    }

    // 수정
    @Override
    public User updateUsername(UUID userId, String username) {
        User user = getUserById(userId);

        user.changeUsername(username);
        return user;
    }

    @Override
    public User updatePassword(UUID userId, String originPassword, String newPassword) {
        User user = getUserById(userId);
        validatePassword(user.getPassword(), originPassword);

        user.changePassword(newPassword);
        return user;
    }

    @Override
    public User updateEmail(UUID userId, String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해 주세요.");
        }
        User user = getUserById(userId);
        if (email.equals(user.getEmail())) {
            throw new IllegalArgumentException("현재 사용 중인 이메일과 같습니다.");
        }
        validateDuplicateEmail(email);

        user.changeEmail(email);
        return user;
    }

    // 삭제
    @Override
    public void deleteUser(UUID userId, String password) {
        User user = getUserById(userId);
        validatePassword(user.getPassword(), password);

        messageService.deleteMessagesByUserId(userId);

        List<Channel> channels = channelService.getChannelsByParticipantId(userId);
        for (Channel channel : channels) {
            if (channel.getCreator().getId().equals(userId)) {
                // 내가 만든 채널 삭제
                channelService.deleteChannel(channel.getId(), userId);
            } else {
                // 참여만 한 채널은 퇴장만
                channel.leaveChannel(user);
            }
        }

        userData.remove(userId);
    }

    @Override
    public void deleteEmail(UUID userId) {
        User user = getUserById(userId);
        user.deleteEmail();
    }
}
