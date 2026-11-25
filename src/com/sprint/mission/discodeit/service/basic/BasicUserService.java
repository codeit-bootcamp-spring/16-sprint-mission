package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.user.UserDto;
import com.sprint.mission.discodeit.dto.response.binarycontent.BinaryContentDto;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentService binaryContentService;
    private final UserStatusRepository userStatusRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserDto create(UserCreateRequest request) {
        return create(request, null);
    }

    @Override
    public UserDto create(UserCreateRequest request, MultipartFile profile) {
        if (request == null) {
            throw new IllegalArgumentException("사용자 정보는 필수입니다.");
        }

        validateRequired(request.username(), "사용자 이름");
        validateRequired(request.email(), "이메일");
        validateRequired(request.password(), "비밀번호");

        validateUnique(request.username(), request.email(), null);

        User user = new User(
                request.username(),
                request.email(),
                request.password()
        );

        if (profile != null && !profile.isEmpty()) {
            BinaryContentDto image = binaryContentService.create(profile);
            user.updateProfileImage(image.id());
        }

        userRepository.save(user);
        userStatusRepository.save(new UserStatus(user.getId()));

        return toDto(user);
    }

    @Override
    public Optional<UserDto> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        return userRepository.findById(id).map(this::toDto);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto update(
            UUID userId,
            UserUpdateRequest request,
            MultipartFile profile
    ) {
        if (userId == null || request == null) {
            throw new IllegalArgumentException("수정할 사용자 정보는 필수입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("사용자를 찾을 수 없습니다.")
                );

        String username = request.newUsername() == null
                ? user.getUsername()
                : request.newUsername();

        String email = request.newEmail() == null
                ? user.getEmail()
                : request.newEmail();

        String password = request.newPassword() == null
                ? user.getPassword()
                : request.newPassword();

        validateRequired(username, "사용자 이름");
        validateRequired(email, "이메일");
        validateRequired(password, "비밀번호");

        validateUnique(username, email, userId);

        UUID oldImageId = user.getProfileImageId();

        if (profile != null && !profile.isEmpty()) {
            BinaryContentDto newImage = binaryContentService.create(profile);
            user.updateProfileImage(newImage.id());

            if (oldImageId != null) {
                binaryContentRepository.deleteById(oldImageId);
            }
        }

        user.update(
                username,
                email,
                password,
                user.getProfileImageId()
        );

        userRepository.save(user);

        return toDto(user);
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("사용자를 찾을 수 없습니다.")
                );

        for (Message message : messageRepository.findAll()) {
            if (id.equals(message.getAuthorId())) {
                for (UUID attachmentId : message.getAttachmentIds()) {
                    binaryContentRepository.deleteById(attachmentId);
                }

                messageRepository.deleteById(message.getId());
            }
        }

        for (ReadStatus readStatus : readStatusRepository.findAllByUserId(id)) {
            readStatusRepository.deleteById(readStatus.getId());
        }

        UUID profileImageId = user.getProfileImageId();

        if (profileImageId != null) {
            binaryContentRepository.deleteById(profileImageId);
        }

        userStatusRepository.findByUserId(id)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId())
                );

        userRepository.deleteById(id);
    }

    @Override
    public void addChannel(UUID userId, UUID channelId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("사용자를 찾을 수 없습니다.")
                );

        channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new NoSuchElementException("채널을 찾을 수 없습니다.")
                );

        if (readStatusRepository.findByUserIdAndChannelId(userId, channelId).isPresent()) {
            throw new IllegalArgumentException("이미 참여 중인 채널입니다.");
        }

        readStatusRepository.save(new ReadStatus(userId, channelId));
    }

    @Override
    public void removeChannel(UUID userId, UUID channelId) {
        if (userId == null) {
            throw new IllegalArgumentException("사용자 ID는 필수입니다.");
        }

        if (channelId == null) {
            throw new IllegalArgumentException("채널 ID는 필수입니다.");
        }

        userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("사용자를 찾을 수 없습니다.")
                );

        ReadStatus readStatus = readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "참여하지 않은 채널에서는 나갈 수 없습니다."
                        )
                );

        readStatusRepository.deleteById(readStatus.getId());
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
                user.getProfileImageId(),
                online
        );
    }

    private void validateUnique(
            String username,
            String email,
            UUID excludedUserId
    ) {
        boolean usernameExists = userRepository.findAll()
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(excludedUserId)
                                && existing.getUsername().equals(username)
                );

        if (usernameExists) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 사용자 이름입니다."
            );
        }

        boolean emailExists = userRepository.findAll()
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(excludedUserId)
                                && existing.getEmail().equals(email)
                );

        if (emailExists) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은(는) 필수입니다."
            );
        }
    }
}