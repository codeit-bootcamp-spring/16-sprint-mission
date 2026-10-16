package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import com.sprint.mission.discodeit.service.MessageService;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;
    private MessageService messageService;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public User create(User user) {

        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        // 이메일 중복 검사
        boolean emailExists = data.values()
                .stream()
                .anyMatch(existingUser ->
                        existingUser.getEmail().equals(user.getEmail()));

        if (emailExists) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }

        // 중복이 없으면 저장
        data.put(user.getId(), user);

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public User update(UUID id, String name, String email) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        // 본인 이외의 사용자가 같은 이메일을 사용하는지 확인
        boolean emailExists = data.values()
                .stream()
                .anyMatch(existingUser ->
                        !existingUser.getId().equals(id)
                                && existingUser.getEmail().equals(email));

        if (emailExists) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }

        user.update(name, email);

        return user;
    }


    @Override
    public void deleteById(UUID id) {
        User user = data.get(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        // 1. 사용자가 작성한 메시지를 실제 메시지 저장소에서도 삭제
        for (var message : new ArrayList<>(user.getMessages())) {
            messageService.delete(message.getId());
        }

        // 2. 가입한 채널에서 사용자 관계 제거
        for (var channel : new ArrayList<>(user.getChannels())) {
            channel.removeUser(user);
            user.removeChannel(channel);
        }

        // 3. 사용자 저장소에서 삭제
        data.remove(id);
    }
}