package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserResponseDto createUser(
            UserCreateRequestDto dto,
            MultipartFile profile
    ) throws IOException {

        validateUsernameDuplicate(dto.getUsername(), null);
        validateEmailDuplicate(dto.getEmail(), null);

        User user = new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail()
        );

        if (profile != null) {
            BinaryContent binaryContent =
                    new BinaryContent(
                            profile.getOriginalFilename(),
                            profile.getContentType(),
                            profile.getBytes()
                    );

            BinaryContent savedContent =
                    binaryContentRepository.save(binaryContent);

            user.changeProfileId(savedContent.getId());
        }

        User savedUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(savedUser.getId());
        userStatusRepository.save(userStatus);

        return toResponseDto(savedUser);
    }

    @Override
    public UserDto findUser(UUID userId) {
        User user = findUserEntity(userId);
        return toUserDto(user);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toUserDto(user));
        }

        return result;
    }

    @Override
    public UserResponseDto updateUser(
            UUID userId,
            UserUpdateRequestDto dto,
            MultipartFile profile
    ) throws IOException {
        User user = findUserEntity(userId);

        if (dto.getNewUsername() != null) {
            validateUsernameDuplicate(dto.getNewUsername(), userId);
            user.changeUsername(dto.getNewUsername());
        }

        if (dto.getNewEmail() != null) {
            validateEmailDuplicate(dto.getNewEmail(), userId);
            user.changeEmail(dto.getNewEmail());
        }

        if (dto.getNewPassword() != null) {
            user.changePassword(dto.getNewPassword());
        }

        UUID oldProfileId = user.getProfileId();

        if (profile != null && !profile.isEmpty()) {
            BinaryContent newProfile = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes()
            );

            BinaryContent savedProfile = binaryContentRepository.save(newProfile);
            user.changeProfileId(savedProfile.getId());
        }

        User savedUser = userRepository.save(user);

        if (profile != null && !profile.isEmpty() && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        return toResponseDto(savedUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserEntity(userId);

        // 1. 참여 채널 정리
        List<UUID> channelIds = new ArrayList<>(
                user.getMyChannelIds()
        );

        for (UUID channelId : channelIds) {
            Channel channel = channelRepository.findById(channelId)
                    .orElse(null);

            if (channel == null) {
                user.removeChannelId(channelId);
                continue;
            }

            // 사용자 메시지 삭제
            List<Message> messages =
                    messageRepository.findByUserIdAndChannelId(
                            userId,
                            channelId
                    );

            for (Message message : messages) {
                deleteMessageData(message);
            }

            // 채널 탈퇴
            channel.leaveChannel(userId);
            user.removeChannelId(channelId);

            // 읽음 상태 삭제
            readStatusRepository.findByUserIdAndChannelId(userId, channelId)
                    .ifPresent(status ->
                            readStatusRepository.deleteById(status.getId())
                    );

            // 참여자가 없으면 채널 삭제
            if (channel.getParticipantIds().isEmpty()) {
                deleteChannelData(channel);
            } else {
                channelRepository.save(channel);
            }
        }

        // 2. 남은 사용자 메시지 정리
        List<UUID> messageIds = new ArrayList<>(
                user.getMyMessageIds()
        );

        for (UUID messageId : messageIds) {
            messageRepository.findById(messageId)
                    .ifPresentOrElse(
                            this::deleteMessageData,
                            () -> user.removeMessageId(messageId)
                    );
        }

        // 3. 프로필 이미지 삭제
        UUID profileId = user.getProfileId();

        if (profileId != null) {
            binaryContentRepository.deleteById(profileId);
        }

        // 4. 사용자 상태 삭제
        userStatusRepository.findByUserId(userId)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId())
                );

        // 5. 사용자 삭제
        userRepository.deleteById(userId);
    }

    // 메시지와 연관 데이터 삭제
    private void deleteMessageData(Message message) {
        UUID messageId = message.getId();

        // 작성자의 메시지 목록에서 제거
        userRepository.findById(message.getAuthorId())
                .ifPresent(sender -> {
                    sender.removeMessageId(messageId);
                    userRepository.save(sender);
                });

        // 채널의 메시지 목록에서 제거
        channelRepository.findById(message.getChannelId())
                .ifPresent(channel -> {
                    channel.removeMessageId(messageId);
                    channelRepository.save(channel);
                });

        // 첨부 파일 삭제
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(messageId);
    }

    // 채널과 채널에 속한 연관 데이터 삭제
    private void deleteChannelData(Channel channel) {
        UUID channelId = channel.getId();

        // 1. 채널에 속한 모든 메시지 삭제
        List<Message> messages =
                messageRepository.findByChannelId(channelId);

        for (Message message : messages) {
            deleteMessageData(message);
        }

        // 2. 채널의 읽음 상태 삭제
        List<ReadStatus> readStatuses =
                readStatusRepository.findByChannelId(channelId);

        for (ReadStatus status : readStatuses) {
            readStatusRepository.deleteById(status.getId());
        }

        // 3. 모든 참여자의 채널 목록에서 제거
        for (UUID participantId :
                new ArrayList<>(channel.getParticipantIds())) {

            userRepository.findById(participantId)
                    .ifPresent(participant -> {
                        participant.removeChannelId(channelId);
                        userRepository.save(participant);
                    });
        }

        channelRepository.deleteById(channelId);
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userId)
                );
    }

    private void validateUsernameDuplicate(
            String username,
            UUID excludeUserId
    ) {
        boolean duplicated = excludeUserId == null
                ? userRepository.existsByUsername(username)
                : userRepository.existsByUsernameAndIdNot(username, excludeUserId);

        if (duplicated) {
            throw new BusinessLogicException(ExceptionCode.USERNAME_ALREADY_EXISTS);
        }
    }

    private void validateEmailDuplicate(
            String email,
            UUID excludeUserId
    ) {
        if (email == null) {
            return;
        }

        boolean duplicated = excludeUserId == null
                ? userRepository.existsByEmail(email)
                : userRepository.existsByEmailAndIdNot(email, excludeUserId);

        if (duplicated) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .profileId(user.getProfileId())
                .build();
    }

    private UserDto toUserDto(User user) {
        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return UserDto.builder()
                .id(user.getId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileId(user.getProfileId())
                .online(online)
                .build();
    }
}