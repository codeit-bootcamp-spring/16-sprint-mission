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

import java.util.List;
import java.util.Set;

public class JavaApplication4 {
    public static void main(String[] args) {
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
        UserRepository userRepository = new JCFUserRepository();
        UserService userService = new JCFUserService(userRepository, messageRepository, channelRepository);
        ChannelService channelService = new JCFChannelService(userService, userRepository, channelRepository);
        MessageService messageService = new JCFMessageService(userService, channelService, messageRepository);

        User user1 = userService.createUser("user01");
        Channel channel1 = channelService.createChannel("channel01");
        Message msg1 = messageService.createMessage("Hello, Java!", user1, channel1);
        Set<User> userList = channel1.getUsers();
        User theUser = userList.iterator().next();
        System.out.println(theUser.getUsername());
        userService.updateUsername(user1.getId(), "user0101");
        System.out.println(theUser.getUsername());
    }
}
