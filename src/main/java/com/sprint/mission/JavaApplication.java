package com.sprint.mission;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService, channelService);

        // USER CRUD
        User user = userService.createUser("세훈", 26);
        User user2 = userService.createUser("아름", 24);

        System.out.println(userService.findAll());
        System.out.println(userService.findById(user.getId()));

        userService.updateUser(user.getId(), "태풍", 25);
        System.out.println(userService.findAll());

        userService.deleteUser(user2.getId());
        System.out.println(userService.findAll());


        // CHANNEL CRUD

    }
}