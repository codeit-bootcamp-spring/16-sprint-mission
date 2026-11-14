package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.DataCleanupService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicDataCleanupService implements DataCleanupService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public void deleteUserData(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));

        // 사용자가 작성한 메시지 삭제
        List<Message> messages = messageRepository.findByUserId(userId);
        for (Message message : messages) {
            deleteMessage(message);
        }

        // 사용자의 ReadStatus 삭제
        List<ReadStatus> readStatuses =
                readStatusRepository.findByUserId(userId);

        for (ReadStatus readStatus : readStatuses) {
            UUID channelId = readStatus.getChannelId();

            Channel channel = channelRepository.findById(channelId)
                    .orElse(null);

            readStatusRepository.deleteById(readStatus.getId());

            // 참여자가 없는 비공개 채널 삭제
            if (channel != null
                    && channel.getType() == ChannelType.PRIVATE
                    && readStatusRepository.findByChannelId(channelId).isEmpty()) {
                deleteChannel(channel);
            }
        }

        // 프로필 이미지 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }

        // 사용자 상태 삭제
        userStatusRepository.findByUserId(userId)
                .ifPresent(status ->
                        userStatusRepository.deleteById(status.getId()));

        // 사용자 삭제
        userRepository.deleteById(userId);
    }

    @Override
    public void deleteMessageData(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.MESSAGE_NOT_FOUND));

        deleteMessage(message);
    }

    @Override
    public void deleteChannelData(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND));

        deleteChannel(channel);
    }

    private void deleteMessage(Message message) {
        for (UUID attachmentId : message.getAttachmentIds()) {
            binaryContentRepository.deleteById(attachmentId);
        }

        messageRepository.deleteById(message.getId());
    }

    private void deleteChannel(Channel channel) {
        UUID channelId = channel.getId();

        List<Message> messages =
                messageRepository.findByChannelId(channelId);

        for (Message message : messages) {
            deleteMessage(message);
        }

        List<ReadStatus> readStatuses =
                readStatusRepository.findByChannelId(channelId);

        for (ReadStatus readStatus : readStatuses) {
            readStatusRepository.deleteById(readStatus.getId());
        }

        channelRepository.deleteById(channelId);
    }
}
