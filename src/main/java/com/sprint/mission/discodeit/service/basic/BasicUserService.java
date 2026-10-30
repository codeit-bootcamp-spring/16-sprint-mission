package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
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

    // 프로필 이미지가 없는 회원 생성
    @Override
    public UserResponseDto createUser(UserCreateRequestDto dto) {
        return createUser(dto, null);
    }

    // 프로필 이미지가 있는 회원 생성
    @Override
    public UserResponseDto createUser(
            UserCreateRequestDto dto,
            byte[] profileImage
    ) {
        validateUsernameDuplicate(dto.getUsername(), null);
        validateEmailDuplicate(dto.getEmail(), null);

        User user = new User(
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail()
        );

        if (profileImage != null) {
            BinaryContent binaryContent = new BinaryContent(profileImage);
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
    public UserResponseDto findUser(UUID userId) {
        User user = findUserEntity(userId);
        return toResponseDto(user);
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponseDto> result = new ArrayList<>();

        for (User user : users) {
            result.add(toResponseDto(user));
        }

        return result;
    }

    @Override
    public UserResponseDto updateUser(
            UUID userId,
            UserUpdateRequestDto dto
    ) {
        return updateUser(userId, dto, null);
    }

    @Override
    public UserResponseDto updateUser(
            UUID userId,
            UserUpdateRequestDto dto,
            byte[] profileImage
    ) {
        User user = findUserEntity(userId);

        if (dto.getUsername() != null) {
            validateUsernameDuplicate(dto.getUsername(), userId);
            user.changeUsername(dto.getUsername());
        }

        if (dto.getPassword() != null) {
            user.changePassword(dto.getPassword());
        }

        if (dto.getEmail() != null) {
            validateEmailDuplicate(dto.getEmail(), userId);
            user.changeEmail(dto.getEmail());
        }

        if (profileImage != null) {
            UUID oldProfileId = user.getProfileId();

            BinaryContent newContent = new BinaryContent(profileImage);
            BinaryContent savedContent =
                    binaryContentRepository.save(newContent);

            user.changeProfileId(savedContent.getId());

            if (oldProfileId != null) {
                binaryContentRepository.deleteById(oldProfileId);
            }
        }

        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자 상태를 찾을 수 없습니다."));

        userStatus.updateLastActiveAt();
        userStatusRepository.save(userStatus);

        User savedUser = userRepository.save(user);

        return toResponseDto(savedUser);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserEntity(userId);

        // 1. 사용자가 참여한 채널을 복사해 순회
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

            // 내가 만든 채널이면 채널 전체 삭제
            if (channel.getCreatorId().equals(userId)) {
                deleteChannelData(channel);
            } else {
                // 다른 사람이 만든 채널이면
                // 해당 채널에 있는 내 메시지만 삭제
                List<Message> messages =
                        messageRepository.findByUserIdAndChannelId(
                                userId,
                                channelId
                        );

                for (Message message : messages) {
                    deleteMessageData(message);
                }

                // 채널에서 나가기
                if (channel.getParticipantIds().contains(userId)) {
                    channel.leaveChannel(userId);
                    channelRepository.save(channel);
                }

                // 사용자 참여 채널 ID 정리
                if (user.getMyChannelIds().contains(channelId)) {
                    user.removeChannelId(channelId);
                }

                // 해당 사용자의 읽음 상태 삭제
                readStatusRepository
                        .findByUserIdAndChannelId(userId, channelId)
                        .ifPresent(status ->
                                readStatusRepository.deleteById(
                                        status.getId()
                                )
                        );
            }
        }

        // 2. 남아 있는 메시지 ID도 정리
        // (채널 목록과 별도로 등록된 메시지가 있을 경우 대비)
        List<UUID> messageIds = new ArrayList<>(
                user.getMyMessageIds()
        );

        for (UUID messageId : messageIds) {
            Message message = messageRepository.findById(messageId)
                    .orElse(null);

            if (message != null) {
                deleteMessageData(message);
            } else {
                user.removeMessageId(messageId);
            }
        }

        userRepository.save(user);

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
        userRepository.findById(message.getSenderId())
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
                        if (participant.getMyChannelIds().contains(channelId)) {
                            participant.removeChannelId(channelId);
                            userRepository.save(participant);
                        }
                    });
        }

        channelRepository.deleteById(channelId);
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId)
                );
    }

    private void validateUsernameDuplicate(
            String username,
            UUID excludeUserId
    ) {
        boolean duplicated = userRepository.findAll().stream()
                .anyMatch(user ->
                        user.getUsername().equals(username)
                                && !user.getId().equals(excludeUserId));

        if (duplicated) {
            throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다.");
        }
    }

    private void validateEmailDuplicate(
            String email,
            UUID excludeUserId
    ) {
        if (email == null) {
            return;
        }

        boolean duplicated = userRepository.findAll().stream()
                .anyMatch(user ->
                        email.equals(user.getEmail())
                                && !user.getId().equals(excludeUserId));

        if (duplicated) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
    }

    private UserResponseDto toResponseDto(User user) {
        boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(false);

        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileId(user.getProfileId())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .online(online)
                .build();
    }
}