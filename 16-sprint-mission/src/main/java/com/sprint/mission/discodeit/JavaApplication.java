package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

public class JavaApplication {
    public static void main(String[] args) {

        //User 테스트
        UserService userService = new JCFUserService();

        User user = userService.create("김신중","1234");//C
        User user2 = userService.create("김길똥","4852");

        User findUser = userService.findById(user.getId()); //R
        System.out.println(findUser.getName());
        for(User allUser:userService.findAll()){
            System.out.println(allUser.getName());
        }
        System.out.println(userService.findAll().size());

        userService.update(user.getId(),"홍길동","1234");//U
        User updateUser = userService.findById(user.getId());
        System.out.println(updateUser.getName());

        userService.delete(user.getId()); //D
        System.out.println(userService.findById(user.getId()));
        System.out.println(userService.findAll().size());

        //Channel 테스트
        ChannelService channelService = new JCFChannelService();

        Channel channel = channelService.create("대화채널");//C
        Channel channel2 = channelService.create("수다채널");

        Channel findChannel = channelService.findById(channel.getId());//R
        System.out.println(findChannel.getName());
        System.out.println(channelService.findAll().size());

        channelService.update(channel.getId(),"잡담채널");//U
        Channel updateChannel = channelService.findById(channel.getId());
        System.out.println(updateChannel.getName());

        channelService.delete(channel.getId());//D
        System.out.println(channelService.findById(channel.getId()));
        System.out.println(channelService.findAll().size());

        //Message 테스트
        MessageService messageService = new JCFMessageService();

        Message message = messageService.create("안녕하세요", user2.getId(), channel2.getId());//C
        Message message2 = messageService.create("반갑습니다", user2.getId(), channel2.getId());

        Message findMessage = messageService.findById(message.getId());//R
        System.out.println(findMessage.getComment());
        for(Message allMessage:messageService.findAll()){
            System.out.println(allMessage.getComment());
        }
        System.out.println(messageService.findAll().size());

        messageService.update(message.getId(),"수정된 메시지입니다");//U
        Message updateMessage = messageService.findById(message.getId());
        System.out.println(updateMessage.getComment());

        messageService.delete(message.getId());//D
        System.out.println(messageService.findById(message.getId()));
        System.out.println(messageService.findAll().size());

    }
}
