
package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User add(User user) {
        userRepository.save(user);
        return user;
    }

    @Override
    public User getById(UUID id) {
        return userRepository.getById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User update(UUID id, String username) {
        User user = userRepository.getById(id);
        user.update(username);
        userRepository.save(user);
        return user;
    }


    @Override
    public void delete(UUID id) {
        User user = userRepository.getById(id);

        userStatusRepository.findByUserId(id)
                .ifPresent(status ->
                        userStatusRepository.delete(status.getId())
                );

        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }

        userRepository.delete(id);
    }


    @Override
    public UserResponse create(UserCreateRequest request) {
        if (request == null
                || request.username() == null
                || request.username().isBlank()
                || request.email() == null
                || request.email().isBlank()
                || request.password() == null) {
            throw new IllegalArgumentException("필수 정보가 없습니다.");
        }

        String username = request.username().trim();
        String email = request.email().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        UUID profileImageId = null;

        if (request.profileImage() != null) {
            BinaryContentCreateRequest image = request.profileImage();

            if (image.fileName() == null
                    || image.contentType() == null
                    || image.bytes() == null) {
                throw new IllegalArgumentException("프로필 이미지 정보가 올바르지 않습니다.");
            }

            BinaryContent binaryContent = new BinaryContent(
                    image.fileName(),
                    image.contentType(),
                    image.bytes()
            );

            binaryContentRepository.save(binaryContent);
            profileImageId = binaryContent.getId();
        }

        User user = new User(
                username,
                email,
                request.password(),
                profileImageId
        );

        userRepository.save(user);

        UserStatus userStatus = new UserStatus(
                user.getId(),
                Instant.now()
        );

        userStatusRepository.save(userStatus);

        return UserResponseMapper.toResponse(user, userStatus);
    }

    @Override
    public UserResponse find(UUID id) {
        User user = userRepository.getById(id);

        UserStatus userStatus = userStatusRepository
                .findByUserId(id)
                .orElse(null);

        return UserResponseMapper.toResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.getAll().stream()
                .map(user -> {
                    UserStatus userStatus = userStatusRepository
                            .findByUserId(user.getId())
                            .orElse(null);

                    return UserResponseMapper.toResponse(user, userStatus);
                })
                .toList();
    }

        @Override
        public UserResponse update(UserUpdateRequest request) {
            if (request == null || request.id() == null) {
                throw new IllegalArgumentException("사용자 ID가 필요합니다.");
            }

            User user = userRepository.getById(request.id());

            if (user == null) {
                throw new IllegalArgumentException("사용자를 찾을 수 없습니다.");
            }

            if (request.username() != null) {
                if (request.username().isBlank()) {
                    throw new IllegalArgumentException("사용자명은 비어 있을 수 없습니다.");
                }

                userRepository.findByUsername(request.username())
                        .filter(found -> !found.getId().equals(user.getId()))
                        .ifPresent(found -> {
                            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
                        });
            }

            if (request.email() != null) {
                if (request.email().isBlank()) {
                    throw new IllegalArgumentException("이메일은 비어 있을 수 없습니다.");
                }

                userRepository.findByEmail(request.email())
                        .filter(found -> !found.getId().equals(user.getId()))
                        .ifPresent(found -> {
                            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                        });
            }

            UUID oldProfileImageId = user.getProfileImageId();
            UUID newProfileImageId = null;

            if (request.profileImage() != null) {
                BinaryContentCreateRequest image = request.profileImage();

                if (image.fileName() == null
                        || image.contentType() == null
                        || image.bytes() == null) {
                    throw new IllegalArgumentException("프로필 이미지 정보가 올바르지 않습니다.");
                }

                BinaryContent binaryContent = new BinaryContent(
                        image.fileName(),
                        image.contentType(),
                        image.bytes()
                );

                binaryContentRepository.save(binaryContent);
                newProfileImageId = binaryContent.getId();
            }

            user.update(
                    request.username(),
                    request.email(),
                    null,
                    newProfileImageId
            );

            userRepository.save(user);

            if (newProfileImageId != null && oldProfileImageId != null) {
                binaryContentRepository.delete(oldProfileImageId);
            }

            UserStatus userStatus = userStatusRepository
                    .findByUserId(user.getId())
                    .orElse(null);

            return UserResponseMapper.toResponse(user, userStatus);
        }

    }
