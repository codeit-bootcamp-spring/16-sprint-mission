package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

public class ServiceFactory {

    private static final JCFUserService USER_SERVICE =
            new JCFUserService();

    private static final JCFChannelService CHANNEL_SERVICE =
            new JCFChannelService(USER_SERVICE);

    private static final JCFMessageService MESSAGE_SERVICE =
            new JCFMessageService(USER_SERVICE, CHANNEL_SERVICE);

    static {
        USER_SERVICE.setMessageService(MESSAGE_SERVICE);
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
