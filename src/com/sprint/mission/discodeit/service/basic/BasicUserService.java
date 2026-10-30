package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
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
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserDto create(UserCreateRequest request) {
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

        boolean hasImage = validateImageFields(
                request.profileImageName(),
                request.profileImageContentType(),
                request.profileImageSize(),
                request.profileImageData()
        );

        if (hasImage) {
            BinaryContent image = new BinaryContent(
                    request.profileImageName(),
                    request.profileImageContentType(),
                    request.profileImageSize(),
                    request.profileImageData()
            );

            binaryContentRepository.save(image);
            user.updateProfileImage(image.getId());
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
    public UserDto update(UserUpdateRequest request) {
        if (request == null || request.id() == null) {
            throw new IllegalArgumentException("수정할 사용자 정보는 필수입니다.");
        }

        User user = userRepository.findById(request.id())
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        validateRequired(request.username(), "사용자 이름");
        validateRequired(request.email(), "이메일");

        String password = request.password() == null
                ? user.getPassword()
                : request.password();

        validateRequired(password, "비밀번호");
        validateUnique(request.username(), request.email(), request.id());

        boolean hasImage = validateImageFields(
                request.profileImageName(),
                request.profileImageContentType(),
                request.profileImageSize(),
                request.profileImageData()
        );

        if (hasImage) {
            UUID oldImageId = user.getProfileImageId();

            BinaryContent newImage = new BinaryContent(
                    request.profileImageName(),
                    request.profileImageContentType(),
                    request.profileImageSize(),
                    request.profileImageData()
            );

            binaryContentRepository.save(newImage);
            user.updateProfileImage(newImage.getId());

            if (oldImageId != null) {
                binaryContentRepository.deleteById(oldImageId);
            }
        }

        user.update(
                request.username(),
                request.email(),
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
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 사용자가 작성한 메시지와 첨부파일 삭제
        for (Message message : messageRepository.findAll()) {
            if (id.equals(message.getAuthorId())) {

                for (UUID attachmentId : message.getAttachmentIds()) {
                    binaryContentRepository.deleteById(attachmentId);
                }

                messageRepository.deleteById(message.getId());
            }
        }

        // 2. 사용자의 읽음 상태 삭제
        for (ReadStatus readStatus : readStatusRepository.findAllByUserId(id)) {
            readStatusRepository.deleteById(readStatus.getId());
        }

        // 3. 프로필 이미지 삭제
        UUID profileImageId = user.getProfileImageId();

        if (profileImageId != null) {
            binaryContentRepository.deleteById(profileImageId);
        }

        // 4. 사용자 상태 삭제
        userStatusRepository.findByUserId(id)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId()));

        // 5. 사용자 삭제
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
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));

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
                        new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        ReadStatus readStatus = readStatusRepository
                .findByUserIdAndChannelId(userId, channelId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "참여하지 않은 채널에서는 나갈 수 없습니다."
                        ));

        readStatusRepository.deleteById(readStatus.getId());
    }

    private UserDto toDto(User user) {
        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return new UserDto(
                user.getId(),
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
                                && existing.getUsername().equals(username));

        if (usernameExists) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 사용자 이름입니다."
            );
        }

        boolean emailExists = userRepository.findAll()
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(excludedUserId)
                                && existing.getEmail().equals(email));

        if (emailExists) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }
    }

    private boolean validateImageFields(
            String name,
            String contentType,
            Long size,
            byte[] data
    ) {
        boolean allAbsent = name == null
                && contentType == null
                && size == null
                && data == null;

        if (allAbsent) {
            return false;
        }

        if (name == null || name.isBlank()
                || contentType == null || contentType.isBlank()
                || size == null || size < 0
                || data == null
                || size != data.length) {
            throw new IllegalArgumentException(
                    "프로필 이미지 정보가 올바르지 않습니다."
            );
        }

        return true;
    }

    private void validateRequired(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    fieldName + "은(는) 필수입니다."
            );
        }
    }
}