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
import com.sprint.mission.discodeit.entity.Group;
import com.sprint.mission.discodeit.service.GroupService;
import com.sprint.mission.discodeit.service.jcf.JCFGroupService;

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


        ChannelService channelService = new JCFChannelService();

        Channel channel = channelService.create("자유채널","아무나와요","text");
        System.out.println("채널 등록: "+ channel.getChannelName());

        Channel channel1found = channelService.read(channel.getId());
        System.out.println("채널 단건조회: "+ channel1found.getChannelName());

        channelService.update(channel.getId(),"공지채널","공지사항","text");

        Channel channel1updated = channelService.read(channel.getId());
        System.out.println("채널 수정후조회: "+channel1updated.getChannelName());

        List<Channel> allchannel = channelService.readAll();
        System.out.println("전체 채널 수: "+allchannel.size());

        channelService.addParticipant(channel.getId(), user.getId());
        Channel channelWithParticipant = channelService.read(channel.getId());
        System.out.println("채널 참여자 수: " + channelWithParticipant.getParticipantIds().size());

        channelService.removeParticipant(channel.getId(), user.getId());
        Channel channelAfterRemove = channelService.read(channel.getId());
        System.out.println("참여자 제거후 참여자 수: " + channelAfterRemove.getParticipantIds().size());

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


        List<Message> searchResult = messageService.findByContent("안녕?");
        System.out.println("검섹 결과 : "+searchResult.size());
        for(Message m : searchResult) {
            System.out.println("-" +m.getContent() );
        }

        List<Message> searchResult2 = messageService.findByContent("안녕");
        System.out.println("검섹 결과 : "+searchResult2.size());
        for(Message m : searchResult2) {
            System.out.println("-" +m.getContent() );
        }

        List<Message> searchResult3 = messageService.findByContent("빠이");
        System.out.println("검색 결과 : "+searchResult3.size());
        for(Message m : searchResult3) {
            System.out.println("-" +m.getContent() );
        }

        System.out.println("북마크 여부(전): " + message.isBookmarked());

        messageService.toggleBookmark(message.getId());
        Message messageAfterBookmark = messageService.read(message.getId());
        System.out.println(("북마크 여부(후): "+ messageAfterBookmark.isBookmarked()));

        messageService.delete(message.getId());
        Message message1deleted = messageService.read(message.getId());
        System.out.println("메세지 삭제후 조회" + message1deleted);

        GroupService groupService = new JCFGroupService();
        Group gameGroup = groupService.create(user.getId(),"게임채널 모음", "CHANNEL");
        System.out.println("그룹등록: " + gameGroup.getName());

        groupService.addItem(gameGroup.getId(), channel.getId());
        Group foundGroup = groupService.read(gameGroup.getId());
        System.out.println("그룹에 속한 채널 수: " + foundGroup.getItemIds().size());

        groupService.update(gameGroup.getId(), "친구 채널 모음");
        Group updatedGroup = groupService.read(gameGroup.getId());
        System.out.println("그룹 이름 수정후: " + updatedGroup.getName());

        List<Group> channelGroups = groupService.findByType("CHANNEL");
        System.out.println("CHANNEL 타입 그룹 수: " + channelGroups.size());

        groupService.removeItem(gameGroup.getId(), channel.getId());
        Group afterRemove = groupService.read(gameGroup.getId());
        System.out.println("채널 제거후 그룹 아이템 수: " + afterRemove.getItemIds().size());

        groupService.delete(gameGroup.getId());
        Group deletedGroup = groupService.read(gameGroup.getId());
        System.out.println("그룹 삭제후 조회: " + deletedGroup);

        channelService.delete(channel.getId());

        Channel channel1deleted = channelService.read(channel.getId());
        System.out.println("채널 삭제후 조회" + channel1deleted);

        userService.delete(user.getId());

        User deleted = userService.read(user.getId());
        System.out.println("삭제후 조회" + deleted);
    }

}
