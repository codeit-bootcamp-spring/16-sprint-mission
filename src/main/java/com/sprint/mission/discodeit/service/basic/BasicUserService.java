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

import com.sprint.mission.discodeit.exception.FileStorageException;
import com.sprint.mission.discodeit.exception.ResourceNotFoundException;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public UserResponse create(
            CreateUserRequest request,
            MultipartFile profile
    ) {
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

        BinaryContent profileContent = null;

        if (profile != null) {
            profileContent = convertProfile(profile);
        }

        User user = new User(
                request.username().trim(),
                request.email().trim(),
                request.password()
        );

        if (profileContent != null) {
            BinaryContent savedProfile =
                    binaryContentRepository.save(profileContent);

            user.changeProfile(savedProfile.getId());
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
        return toResponse(requireUser(id));
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
            BinaryContent profileContent =
                    binaryContentRepository.save(
                            convertProfileRequest(request.profile())
                    );

            newProfileId = profileContent.getId();
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

    private BinaryContent convertProfile(MultipartFile profile) {
        if (profile.isEmpty()) {
            throw new IllegalArgumentException(
                    "빈 프로필 파일은 등록할 수 없습니다."
            );
        }

        String fileName = profile.getOriginalFilename();

        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException(
                    "프로필 파일 이름이 없습니다."
            );
        }

        String contentType = profile.getContentType();

        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        if (!contentType.toLowerCase().startsWith("image/")) {
            throw new IllegalArgumentException(
                    "프로필은 이미지 파일만 등록할 수 있습니다."
            );
        }

        try {
            return new BinaryContent(
                    fileName,
                    contentType,
                    profile.getBytes()
            );
        } catch (IOException exception) {
            throw new FileStorageException(
                    "프로필 파일을 읽는 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    private BinaryContent convertProfileRequest(
            CreateBinaryContentRequest request
    ) {
        if (request.fileName() == null
                || request.fileName().isBlank()
                || request.contentType() == null
                || request.contentType().isBlank()
                || request.bytes() == null) {
            throw new IllegalArgumentException(
                    "프로필 수정 파일 정보가 올바르지 않습니다."
            );
        }

        return new BinaryContent(
                request.fileName(),
                request.contentType(),
                request.bytes()
        );
    }

    private User requireUser(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "사용자 ID가 없습니다."
            );
        }

        User user = userRepository.findById(id);

        if (user == null) {
            throw new ResourceNotFoundException(
                    "사용자를 찾을 수 없습니다. ID: " + id
            );
        }

        return user;
    }

    private UserResponse toResponse(User user) {
        UserStatus status =
                userStatusRepository.findByUserId(user.getId());

        boolean online =
                status != null && status.isOnline();

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

        for (User existing : userRepository.findAll()) {
            if (existing.getId().equals(excludeId)) {
                continue;
            }

            if (existing.getUsername()
                    .equalsIgnoreCase(username.trim())) {
                throw new IllegalStateException(
                        "이미 사용 중인 사용자 이름입니다."
                );
            }

            if (existing.getEmail()
                    .equalsIgnoreCase(email.trim())) {
                throw new IllegalStateException(
                        "이미 사용 중인 이메일입니다."
                );
            }
        }
    }
}