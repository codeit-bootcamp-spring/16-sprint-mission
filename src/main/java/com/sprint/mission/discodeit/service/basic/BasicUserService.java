package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public User create(
            String username,
            String email,
            String password
    ) {
        return createInternal(
                new CreateUserRequest(
                        username,
                        email,
                        password,
                        null
                )
        );
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        return toResponse(createInternal(request));
    }

    private User createInternal(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }

        validate(
                request.username(),
                request.email(),
                request.password(),
                null
        );

        User user = new User(
                request.username().trim(),
                request.email().trim(),
                request.password()
        );

        if (request.profile() != null) {
            BinaryContent profile = binaryContentRepository.save(
                    makeBinary(request.profile())
            );

            user.changeProfile(profile.getId());
        }

        userRepository.save(user);

        userStatusRepository.save(
                new UserStatus(user.getId(), Instant.now())
        );

        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse findResponseById(UUID id) {
        return toResponse(findById(id));
    }

    @Override
    public List<UserResponse> findAllResponses() {
        return findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public User update(
            UUID id,
            String username,
            String email,
            String password
    ) {
        updateInternal(
                new UpdateUserRequest(
                        id,
                        username,
                        email,
                        password,
                        null
                )
        );

        return findById(id);
    }

    @Override
    public UserResponse update(UpdateUserRequest request) {
        return toResponse(updateInternal(request));
    }

    private User updateInternal(UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("수정 정보가 없습니다.");
        }

        User user = findById(request.id());

        validate(
                request.username(),
                request.email(),
                request.password(),
                request.id()
        );

        user.update(
                request.username().trim(),
                request.email().trim(),
                request.password()
        );

        if (request.profile() != null) {
            BinaryContent replacement = binaryContentRepository.save(
                    makeBinary(request.profile())
            );

            UUID oldProfileId = user.getProfileId();

            user.changeProfile(replacement.getId());

            if (oldProfileId != null) {
                binaryContentRepository.delete(oldProfileId);
            }
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID id) {
        User user = findById(id);

        if (!user.getChannelIds().isEmpty()
                || !user.getMessageIds().isEmpty()) {
            throw new IllegalStateException(
                    "참여 중인 채널이나 작성한 메시지가 남아 있습니다."
            );
        }

        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        UserStatus userStatus =
                userStatusRepository.findByUserId(id);

        if (userStatus != null) {
            userStatusRepository.delete(userStatus.getId());
        }

        for (ReadStatus readStatus :
                readStatusRepository.findAllByUserId(id)) {
            readStatusRepository.delete(readStatus.getId());
        }

        userRepository.delete(id);
    }

    @Override
    public void save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보가 없습니다.");
        }

        findById(user.getId());
        userRepository.save(user);
    }

    private UserResponse toResponse(User user) {
        UserStatus status =
                userStatusRepository.findByUserId(user.getId());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                status != null && status.isOnline(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private BinaryContent makeBinary(
            CreateBinaryContentRequest request
    ) {
        if (request == null
                || request.fileName() == null
                || request.fileName().isBlank()
                || request.contentType() == null
                || request.contentType().isBlank()
                || request.bytes() == null) {
            throw new IllegalArgumentException(
                    "파일 이름, 타입, 데이터는 필수입니다."
            );
        }

        return new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
    }

    private void validate(
            String username,
            String email,
            String password,
            UUID excludingId
    ) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "사용자 이름을 입력하세요."
            );
        }

        if (email == null
                || email.isBlank()
                || !email.contains("@")) {
            throw new IllegalArgumentException(
                    "올바른 이메일을 입력하세요."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "비밀번호를 입력하세요."
            );
        }

        for (User user : userRepository.findAll()) {
            if (user.getId().equals(excludingId)) {
                continue;
            }

            if (user.getUsername()
                    .equalsIgnoreCase(username.trim())) {
                throw new IllegalArgumentException(
                        "이미 사용 중인 사용자 이름입니다."
                );
            }

            if (user.getEmail()
                    .equalsIgnoreCase(email.trim())) {
                throw new IllegalArgumentException(
                        "이미 사용 중인 이메일입니다."
                );
            }
        }
    }
}