package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.GroupRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileGroupRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.GroupService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicGroupService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

public class JavaApplication {
    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody1234", "010-1234-5678", "woody@codeit.com");
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create("공지", "공지 채널입니다.", "PUBLIC");
        return channel;
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(channel.getId(), author.getId(), "안녕하세요.");
        System.out.println("메시지 생성: " + message.getId());
}

    public static void main(String[] args) {
        UserRepository userRepo = new FileUserRepository();
        ChannelRepository channelRepo = new FileChannelRepository();
        MessageRepository messageRepo = new FileMessageRepository();
        GroupRepository groupRepository = new FileGroupRepository();

        UserService userService = new BasicUserService(userRepo);
        ChannelService channelService = new BasicChannelService(channelRepo, userRepo);
        MessageService messageService = new BasicMessageService(messageRepo, channelRepo, userRepo);
        GroupService groupService = new BasicGroupService(groupRepository);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, user);
        groupService.create(user.getId(), "게임채널 모음", "CHANNEL");
    }
}