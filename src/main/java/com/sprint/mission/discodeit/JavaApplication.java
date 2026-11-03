package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.basic.*;

public class JavaApplication {

    public static void main(String[] args) {
        UserRepository users = new JCFUserRepository();
        ChannelRepository channels = new JCFChannelRepository();
        MessageRepository messages = new JCFMessageRepository();
        ReadStatusRepository reads = new JCFReadStatusRepository();
        UserStatusRepository statuses = new JCFUserStatusRepository();
        BinaryContentRepository binaries =
                new JCFBinaryContentRepository();

        DiscodeitApplication.runScenario(
                "JCFRepository 직접 생성",
                new BasicUserService(
                        users,
                        statuses,
                        binaries,
                        reads,
                        channels
                ),
                new BasicChannelService(
                        channels,
                        users,
                        messages,
                        reads,
                        binaries
                ),
                new BasicMessageService(
                        messages,
                        users,
                        channels,
                        binaries,
                        reads
                ),
                new BasicReadStatusService(
                        reads,
                        users,
                        channels
                ),
                new BasicUserStatusService(
                        statuses,
                        users
                ),
                new BasicBinaryContentService(
                        binaries
                ),
                new BasicAuthService(
                        users,
                        statuses
                )
        );
    }
}