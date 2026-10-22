package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;

public class ServiceFactory {

    private static final BasicUserService USER_SERVICE =
            new BasicUserService(new FileUserRepository());

    private static final BasicChannelService CHANNEL_SERVICE =
            new BasicChannelService(
                    new FileChannelRepository(),
                    USER_SERVICE
            );

    private static final BasicMessageService MESSAGE_SERVICE =
            new BasicMessageService(
                    new FileMessageRepository(),
                    USER_SERVICE,
                    CHANNEL_SERVICE
            );

    static {
        USER_SERVICE.setMessageService(MESSAGE_SERVICE);
        USER_SERVICE.setChannelService(CHANNEL_SERVICE);
        CHANNEL_SERVICE.setMessageService(MESSAGE_SERVICE);
    }

    private ServiceFactory() {
    }

    public static UserService userService() {
        return USER_SERVICE;
    }

    public static ChannelService channelService() {
        return CHANNEL_SERVICE;
    }

    public static MessageService messageService() {
        return MESSAGE_SERVICE;
    }
}
