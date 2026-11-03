package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateBinaryContentRequest;
import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserResponse;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

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
    private final ChannelRepository channelRepository;

    @Override
    public UserResponse create(CreateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "사용자 생성 정보가 없습니다."
            );
        }

        validateUser(
                request.username(),
                request.email(),
                request.password(),
                null
        );

        UUID profileId = null;

        if (request.profile() != null) {
            BinaryContent profile = binaryContentRepository.save(
                    createBinaryContent(request.profile())
            );

            profileId = profile.getId();
        }

        User user = new User(
                request.username().trim(),
                request.email().trim(),
                request.password()
        );

        if (profileId != null) {
            user.changeProfile(profileId);
        }

        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(
                savedUser.getId(),
                Instant.now()
        );

        userStatusRepository.save(userStatus);

        return toResponse(savedUser);
    }

    @Override
    public UserResponse findById(UUID id) {
        User user = requireUser(id);
        return toResponse(user);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse update(UpdateUserRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "사용자 수정 정보가 없습니다."
            );
        }

        User user = requireUser(request.id());

        validateUser(
                request.username(),
                request.email(),
                request.password(),
                request.id()
        );

        UUID oldProfileId = user.getProfileId();
        UUID newProfileId = null;

        if (request.profile() != null) {
            BinaryContent newProfile = binaryContentRepository.save(
                    createBinaryContent(request.profile())
            );

            newProfileId = newProfile.getId();
        }

        user.update(
                request.username().trim(),
                request.email().trim(),
                request.password()
        );

        if (newProfileId != null) {
            user.changeProfile(newProfileId);
        }

        User updatedUser = userRepository.save(user);

        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.delete(oldProfileId);
        }

        return toResponse(updatedUser);
    }

    @Override
    public void delete(UUID id) {
        User user = requireUser(id);

        for (ReadStatus readStatus :
                readStatusRepository.findAllByUserId(id)) {

            readStatusRepository.delete(readStatus.getId());
        }

        for (Channel channel : channelRepository.findAll()) {
            if (channel.getMemberIds().contains(id)) {
                channel.removeMember(id);
                channelRepository.save(channel);
            }
        }

        UserStatus userStatus =
                userStatusRepository.findByUserId(id);

        if (userStatus != null) {
            userStatusRepository.delete(userStatus.getId());
        }

        if (user.getProfileId() != null) {
            binaryContentRepository.delete(user.getProfileId());
        }

        userRepository.delete(id);
    }

    private User requireUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 없습니다."
            );
        }

        User user = userRepository.findById(id);

        if (user == null) {
            throw new IllegalArgumentException(
                    "사용자를 찾을 수 없습니다."
            );
        }

        return user;
    }

    private UserResponse toResponse(User user) {
        UserStatus userStatus =
                userStatusRepository.findByUserId(user.getId());

        boolean online = userStatus != null
                && userStatus.isOnline();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                online,
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    private BinaryContent createBinaryContent(
            CreateBinaryContentRequest request
    ) {
        if (request.fileName() == null
                || request.fileName().isBlank()
                || request.contentType() == null
                || request.contentType().isBlank()
                || request.bytes() == null) {

            throw new IllegalArgumentException(
                    "프로필 파일 정보가 올바르지 않습니다."
            );
        }

        return new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
    }

    private void validateUser(
            String username,
            String email,
            String password,
            UUID excludeId
    ) {
        if (username == null
                || username.isBlank()
                || email == null
                || email.isBlank()
                || !email.contains("@")
                || password == null
                || password.isBlank()) {

            throw new IllegalArgumentException(
                    "사용자 이름, 이메일, 비밀번호를 확인하세요."
            );
        }

        for (User user : userRepository.findAll()) {
            if (user.getId().equals(excludeId)) {
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