package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

/** 사용자 업무 규칙을 처리하며, 실제 저장 방법은 UserRepository 구현체에 맡깁니다. */
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    /** 생성자 주입: 실행하는 쪽에서 JCF 또는 File 저장소를 선택해 전달합니다. */
    public BasicUserService(UserRepository userRepository) {
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null.");
        }
        this.userRepository = userRepository;
    }

    @Override
    public void create(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        if (userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("이미 등록된 ID입니다: " + user.getId());
        }

        validateEmail(user.getEmail());
        if (isEmailDuplicate(user.getEmail(), null)) {
            throw new IllegalArgumentException("Email is already in use: " + user.getEmail());
        }

        // 모든 업무 규칙을 통과한 뒤에만 Repository에 저장을 요청합니다.
        userRepository.save(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not Found user id: " + id));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void update(UUID id, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }

        User existingUser = findById(id);
        validateEmail(user.getEmail());
        if (isEmailDuplicate(user.getEmail(), id)) {
            throw new IllegalArgumentException("Email is already in use: " + user.getEmail());
        }

        existingUser.update(user.getUsername(), user.getEmail(), user.getPassword());
        // 수정된 객체를 같은 ID로 저장합니다. 저장소가 Map 교체 또는 파일 덮어쓰기를 수행합니다.
        userRepository.save(existingUser);
    }

    @Override
    public void delete(UUID id) {
        if (!userRepository.deleteById(id)) {
            throw new IllegalArgumentException("Not Found User: " + id);
        }
    }

    private boolean isEmailDuplicate(String email, UUID excludedUserId) {
        return userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email)
                        && !user.getId().equals(excludedUserId));
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
        }

        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!email.matches(emailPattern)) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다: " + email);
        }
    }
}
