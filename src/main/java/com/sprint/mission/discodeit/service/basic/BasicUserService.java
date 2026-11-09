package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest request, MultipartFile profile) {

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());

        UUID newProfileId = null;

        if (profile != null) {
            // BinaryContentCreateRequest profile = request.profile();
            try {
                newProfileId = binaryContentRepository.save(
                        new BinaryContent(
                                profile.getOriginalFilename(), profile.getContentType(), profile.getBytes()
                        )
                ).getId();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        User user = new User(request.username(), request.email(), request.password(), newProfileId);

        UserStatus userStatus = new UserStatus(user.getId());
        this.userStatusRepository.save(userStatus);

        return userRepository.save(user);
    }

    private void validateUniqueUsername(String username) {
        boolean isUsernameTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
        if (isUsernameTaken) {
            throw new DuplicateResourceException("이미 사용 중인 사용자 이름입니다: " + username);
        }
    }

    private void validateUniqueEmail(String email) {
        boolean isEmailTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (isEmailTaken) {
            throw new DuplicateResourceException("이미 사용 중인 이메일입니다: " + email);
        }
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = this.findUserStatus(user.getId());

        return this.toResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userList) {
            UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new UserStatusNotFoundException("ID가 " + user.getId() + "인 user의 userStatus를 찾을 수 없습니다."));
            userResponseList.add(this.toResponse(user, userStatus));
        }

        return userResponseList;
    }

    private UserResponse toResponse(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline()
        );
    }

    @Override
    public User update(UUID userId, UserUpdateRequest request, MultipartFile profile) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        if (!Objects.equals(request.newUsername(), user.getUsername())) {
            validateUniqueUsername(request.newUsername());
        }
        if (!Objects.equals(request.newEmail(), user.getEmail())) {
            validateUniqueEmail(request.newEmail());
        }

        UUID oldProfileId = user.getProfileId();
        UUID newProfileId = null;

        if (profile != null) {
            try {
                newProfileId = binaryContentRepository.save(
                        new BinaryContent(
                                profile.getOriginalFilename(), profile.getContentType(), profile.getBytes()
                        )
                ).getId();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        user.update(request.newUsername(), request.newEmail(), request.newPassword(), newProfileId);

        User saved = userRepository.save(user);

        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        return saved;
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = this.findUserStatus(userId);
        UUID profileId = user.getProfileId();

        if (profileId != null) {
            this.binaryContentRepository.deleteById(profileId);
        }
        this.userStatusRepository.deleteById(userStatus.getId());
        userRepository.deleteById(userId);
    }

    @Override
    public UserStatus findUserStatus(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다.");
        }

        Optional<UserStatus> userStatus = this.userStatusRepository.findByUserId(userId);

        return userStatus.orElseThrow(() ->
                new UserStatusNotFoundException(
                        "사용자 ID " + userId + "에 해당하는 UserStatus를 찾을 수 없습니다."
                )
        );
    }

    public Optional<BinaryContent> findBinaryContent(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));
        UUID profileId = user.getProfileId();

        if (profileId == null) return Optional.empty();

        return binaryContentRepository.findById(profileId);
    }
}
