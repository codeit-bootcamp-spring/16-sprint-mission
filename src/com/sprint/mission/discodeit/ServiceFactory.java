package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileBinaryContentRepository;
import com.sprint.mission.discodeit.repository.file.FileUserStatusRepository;
import com.sprint.mission.discodeit.repository.file.FileReadStatusRepository;

import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicBinaryContentService;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.BinaryContentService;

public class ServiceFactory {

    private static final UserRepository USER_REPOSITORY =
            new FileUserRepository();

    private static final ChannelRepository CHANNEL_REPOSITORY =
            new FileChannelRepository();

    private static final MessageRepository MESSAGE_REPOSITORY =
            new FileMessageRepository();

    private static final BinaryContentRepository BINARY_CONTENT_REPOSITORY =
            new FileBinaryContentRepository();

    private static final UserStatusRepository USER_STATUS_REPOSITORY =
            new FileUserStatusRepository();

    private static final ReadStatusRepository READ_STATUS_REPOSITORY =
            new FileReadStatusRepository();

    public static final BinaryContentService BINARY_CONTENT_SERVICE =
            new BasicBinaryContentService(
                    BINARY_CONTENT_REPOSITORY
            );

    public static final UserService USER_SERVICE =
            new BasicUserService(
                    USER_REPOSITORY,
                    MESSAGE_REPOSITORY,
                    BINARY_CONTENT_REPOSITORY,
                    USER_STATUS_REPOSITORY,
                    CHANNEL_REPOSITORY,
                    READ_STATUS_REPOSITORY
            );

    public static final BasicMessageService MESSAGE_SERVICE =
            new BasicMessageService(
                    MESSAGE_REPOSITORY,
                    USER_REPOSITORY,
                    CHANNEL_REPOSITORY,
                    BINARY_CONTENT_SERVICE
            );

    public static final BasicChannelService CHANNEL_SERVICE =
            new BasicChannelService(
                    CHANNEL_REPOSITORY,
                    USER_REPOSITORY,
                    MESSAGE_REPOSITORY,
                    READ_STATUS_REPOSITORY,
                    BINARY_CONTENT_REPOSITORY
            );

    private ServiceFactory() {
        // 인스턴스 생성을 방지합니다.
    }
}