package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.jcf.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.*;

public class JavaApplication {

    public static void main(String[] args) {

        JCFUserRepository userRepository =
                new JCFUserRepository();

        JCFChannelRepository channelRepository =
                new JCFChannelRepository();

        JCFMessageRepository messageRepository =
                new JCFMessageRepository();

        JCFReadStatusRepository readStatusRepository =
                new JCFReadStatusRepository();

        JCFUserStatusRepository userStatusRepository =
                new JCFUserStatusRepository();

        JCFBinaryContentRepository binaryContentRepository =
                new JCFBinaryContentRepository();

        UserService userService = new BasicUserService(
                userRepository,
                userStatusRepository,
                binaryContentRepository,
                readStatusRepository
        );

        ChannelService channelService = new BasicChannelService(
                channelRepository,
                userRepository,
                messageRepository,
                readStatusRepository,
                binaryContentRepository
        );

        MessageService messageService = new BasicMessageService(
                messageRepository,
                userRepository,
                channelRepository,
                binaryContentRepository
        );

        User user = userService.create(
                "장치욱",
                "spsb7319@naver.com",
                "7319"
        );

        Channel channel = channelService.create(
                "공지",
                "공지 채널입니다."
        );

        channelService.joinChannel(
                channel.getId(),
                user.getId()
        );

        Message message = messageService.create(
                "안녕하세요.",
                user.getId(),
                channel.getId()
        );

        System.out.println(
                "기존 방식 메시지 생성: " + message.getId()
        );

        messageService.delete(message.getId());

        channelService.delete(channel.getId());

        userService.delete(user.getId());

        System.out.println("JavaApplication CRUD 테스트 성공");
    }
}