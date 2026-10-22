package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication3 {
    public static void main(String[] args) {
        /*
        UserRepository userRepository = new JCFUserRepository();
        UserService userService = new JCFUserService(userRepository);

        ChannelRepository channelRepository = new JCFChannelRepository();
        ChannelService channelService = new JCFChannelService(userService, channelRepository);

        MessageRepository messageRepository = new JCFMessageRepository();
        MessageService messageService = new JCFMessageService(userService, channelService, messageRepository);

        User user01 = userService.createUser("user01");
        User user02 = userService.createUser("user02");
        UUID user01Id = user01.getId();
        UUID user02Id = user02.getId();

        System.out.println("All users: " + userService.findAll());
        System.out.println("Get user01: " + userService.findById(user01Id).getUsername());
        userService.updateUsername(user01Id, "user0101");
        System.out.println("Updated user01 to user1010: " + userService.findById(user01Id).getUsername());
        userService.updateUsername(user01Id, "user01");
        System.out.println("Updated user01 to user01: " + userService.findById(user01Id).getUsername());
        userService.deleteUser(user01Id);
        System.out.print("Deleted user01: ");
        System.out.println("All users: " + userService.findAll());

        // ---

        Channel channel01 = channelService.createChannel("channel01");
        Channel channel02 = channelService.createChannel("channel02");
        User user03 = userService.createUser("user03");
        UUID user03Id = user03.getId();
        Message msg1 = messageService.createMessage("Hello, Java!", user02, channel01);
        Message msg2 = messageService.createMessage("Hello, Kotlin!", user02, channel01);
        Message msg3 = messageService.createMessage("Hello, C#!", user03, channel02);

        System.out.println("All messages: " + messageService.findAll());
        System.out.println("Get msg1: " + messageService.findById(msg1.getId()).getContent());
        System.out.println("Update msg1 to Hello, JJava!: " + messageService.updateContent(msg1.getId(), "Hello, JJava!"));
        System.out.println("Get msg1: " + messageService.findById(msg1.getId()).getContent());
        messageService.deleteMessage(msg1.getId());
        System.out.println("All messages: " + messageService.findAll());

        System.out.println("All channels: " + channelService.findAll());
        channelService.deleteChannel(channel02.getId());
        messageService.deleteMessageWithoutChannel();
        System.out.println("All channels: " + channelService.findAll());
        System.out.println("All messages: " + messageService.findAll());

        // ---

        System.out.println("channel01: " + channelService.findById(channel01.getId()).getChannelName());
        System.out.println("Update channel01 to channel0101: " + channelService.updateChannelName(channel01.getId(), "channel0101"));
        System.out.println(channelService.findById(channel01.getId()).getChannelName());
        channelService.deleteChannel(channel01.getId());
        System.out.println("All channels: " + channelService.findAll());
        */
    }
}
