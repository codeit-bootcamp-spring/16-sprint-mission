package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;

import java.time.Instant;

import java.util.List;
import java.util.UUID;

/** 사용자 업무 규칙을 처리하며, 실제 저장 방법은 UserRepository 구현체에 맡깁니다. */
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public UserDto create(UserCreateRequest request) {
        List<User> users = userRepository.findAll();

        boolean usernameExists = users.stream()
                .anyMatch(user ->
                        user.getUsername().equalsIgnoreCase(request.username())
                );
        if (usernameExists) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        boolean emailExists = users.stream()
                .anyMatch(user ->
                        user.getEmail().equalsIgnoreCase(request.email())
                );
        if (emailExists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UUID profileId = null;
        BinaryContentCreateRequest profileRequest = request.profileImage();

        if (profileRequest != null) {
            BinaryContent profile = new BinaryContent(
                    profileRequest.fileName(),
                    profileRequest.contentType(),
                    profileRequest.data()
            );
            binaryContentRepository.save(profile);
            profileId = profile.getId();
        }

        User user = new User(
                request.username(),
                request.email(),
                request.password(),
                profileId
        );
        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        return toDto(user);
    }

    private UserDto toDto(User user) {
        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                online
        );
    }

    @Override
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return toDto(user);
    }


    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto update(UserUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<User> users = userRepository.findAll();

        boolean usernameExists = users.stream()
                .anyMatch(other -> !other.getId().equals(user.getId())
                        && other.getUsername().equalsIgnoreCase(request.username()));
        if (usernameExists) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        boolean emailExists = users.stream()
                .anyMatch(other -> !other.getId().equals(user.getId())
                        && other.getEmail().equalsIgnoreCase(request.email()));
        if (emailExists) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        UUID profileId = user.getProfileId();
        UUID oldProfileId = profileId;
        BinaryContentCreateRequest profileRequest = request.profileImage();

        if (profileRequest != null) {
            BinaryContent profile = new BinaryContent(
                    profileRequest.fileName(),
                    profileRequest.contentType(),
                    profileRequest.data()
            );
            binaryContentRepository.save(profile);
            profileId = profile.getId();
        }

        user.update(request.username(), request.email(), request.password(), profileId);
        userRepository.save(user);

        if (profileRequest != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        return toDto(user);
    }

    @Override
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        userStatusRepository.findByUserId(id)
                .ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

        userRepository.deleteById(id);
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
