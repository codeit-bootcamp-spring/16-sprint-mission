package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();

        User user = userService.create("아무1","1234","010-123-5678","abc@dfg");
        System.out.println("등록: "+ user.getUsername());

        User found = userService.read(user.getId());
        System.out.println("단건조회"+ found.getUsername());

        userService.update(user.getId(),"아무01","5678","010-987-4321","cfd@bba");

        User updated = userService.read(user.getId());
        System.out.println("수정후조회: "+updated.getUsername());

        List<User> all = userService.readAll();
        System.out.println("전체회원 수: "+all.size());

        userService.delete(user.getId());

        User deleted = userService.read(user.getId());
        System.out.println("삭제후 조회" + deleted);


        ChannelService channelService = new JCFChannelService();

        Channel channel = channelService.create("자유채널","아무나와요","text","server1");
        System.out.println("채널 등록: "+ channel.getChannelName());

        Channel channel1found = channelService.read(channel.getId());
        System.out.println("채널 단건조회: "+ channel1found.getChannelName());

        channelService.update(channel.getId(),"공지채널","공지사항","text","server001");

        Channel channel1updated = channelService.read(channel.getId());
        System.out.println("채널 수정후조회: "+channel1updated.getChannelName());

        List<Channel> allchannel = channelService.readAll();
        System.out.println("전체 채널 수: "+allchannel.size());

        channelService.delete(channel.getId());

        Channel channel1deleted = channelService.read(channel.getId());
        System.out.println("채널 삭제후 조회" + channel1deleted);


        MessageService messageService = new JCFMessageService(userService, channelService);

        Message message = messageService.create(channel.getId(),user.getId(),"안녕하세욥");
        System.out.println("메세지 등록: "+ message.getContent());

        Message message1found = messageService.read(message.getId());
        System.out.println("메세지 단건조회: "+ message1found.getContent());

        messageService.update(message.getId(),"안녕?");

        Message message1updated = messageService.read(message.getId());
        System.out.println("메세지 수정후조회: "+message1updated.getContent());

        List<Message> allmessage = messageService.readAll();
        System.out.println("전체 메세지 수: "+allmessage.size());

        messageService.delete(message.getId());

        Message message1deleted = messageService.read(message.getId());
        System.out.println("메세지 삭제후 조회" + message1deleted);
    }

}
