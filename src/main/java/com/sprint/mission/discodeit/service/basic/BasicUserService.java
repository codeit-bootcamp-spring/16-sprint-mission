package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserDto create(UserCreateRequest request) {

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = new User(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        userRepository.save(user);

        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        if (request.getProfileData() != null) {
            BinaryContent profile = new BinaryContent(
                    request.getProfileFileName(),
                    request.getProfileContentType(),
                    request.getProfileData(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(profile);
        }

        return toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + userId));

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

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + request.getUserId()));

        if (request.getUsername() != null) {
            userRepository.findByUsername(request.getUsername())
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
                    });
        }

        if (request.getEmail() != null) {
            userRepository.findByEmail(request.getEmail())
                    .filter(existing -> !existing.getId().equals(user.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
                    });
        }

        user.update(
                request.getUsername(),
                request.getEmail(),
                request.getPassword()
        );

        userRepository.save(user);

        if (request.getProfileData() != null) {

            binaryContentRepository.findByUserId(user.getId())
                    .ifPresent(profile ->
                            binaryContentRepository.deleteById(profile.getId()));

            BinaryContent newProfile = new BinaryContent(
                    request.getProfileFileName(),
                    request.getProfileContentType(),
                    request.getProfileData(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(newProfile);
        }

        return toDto(user);
    }

    @Override
    public void delete(UUID userId) {

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User not found: " + userId));

        binaryContentRepository.findByUserId(userId)
                .ifPresent(profile ->
                        binaryContentRepository.deleteById(profile.getId()));

        userStatusRepository.findByUserId(userId)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId()));

        userRepository.deleteById(userId);
    }

    private UserDto toDto(User user) {

        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                online
        );
    }
}