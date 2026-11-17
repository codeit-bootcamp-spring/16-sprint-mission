package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public User create(UserCreateRequest userCreateRequest, MultipartFile profile) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        UUID nullableProfileId = null;
        if (profile != null && !profile.isEmpty()) {
            try {
                String fileName = profile.getOriginalFilename();
                String contentType = profile.getContentType();
                byte[] bytes = profile.getBytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                nullableProfileId = binaryContentRepository.save(binaryContent).getId();
            } catch (IOException e) {
                throw new RuntimeException("회원가입 프로필 저장 실패", e);
            }
        }

        User user = new User(username, email, userCreateRequest.password(), nullableProfileId);
        User createdUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(createdUser.getId(), Instant.now());
        userStatusRepository.save(userStatus);

        return createdUser;
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest userUpdateRequest, MultipartFile profile) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();

        if (newEmail != null && !newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (newUsername != null && !newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        UUID nullableProfileId = user.getProfileId();
        if (profile != null && !profile.isEmpty()) {
            try {
                if (user.getProfileId() != null) {
                    binaryContentRepository.deleteById(user.getProfileId());
                }
                String fileName = profile.getOriginalFilename();
                String contentType = profile.getContentType();
                byte[] bytes = profile.getBytes();
                BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                nullableProfileId = binaryContentRepository.save(binaryContent).getId();
            } catch (IOException e) {
                throw new RuntimeException("프로필 수정 실패", e);
            }
        }

        user.update(newUsername, newEmail, userUpdateRequest.newPassword(), nullableProfileId);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        readStatusRepository.deleteAllByUserId(userId);
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    private UserDto toDto(User user) {
        Boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        return new UserDto(
                user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(), user.getProfileId(), online
        );
    }
}
