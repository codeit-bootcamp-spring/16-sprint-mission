package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Group;
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
import com.sprint.mission.discodeit.repository.jcf.JCFGroupRepository;
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
        User user = userService.create("아무", "abc1234", "010-1234-5678", "abc@codit.com");
        System.out.println("유저 생성: " + user.getUsername());
        return user;
    }

    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create("공지", "공지 채널입니다.", "PUBLIC");
        System.out.println("채널 생성: " + channel.getChannelName());
        return channel;
    }

    static Message messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create(channel.getId(), author.getId(), "안녕하세요.");
        System.out.println("메시지 생성: " + message.getContent());
        return message;
    }

    static Group setupGroup(GroupService groupService, User owner) {
        Group group = groupService.create(owner.getId(), "게임채널 모음", "CHANNEL");
        System.out.println("그룹 생성: " + group.getName());
        return group;
    }

    static void userReadUpdateTest(UserService userService, User user) {
        System.out.println("유저 단건 조회: " + userService.read(user.getId()).getUsername());
        System.out.println("유저 전체 조회: " + userService.readAll().size() + "명");
        userService.update(user.getId(), "아무01", "chep1234", "010-9999-8888", "cdf@codeit.com");
        System.out.println("유저 수정 결과 조회: " + userService.read(user.getId()).getUsername());
    }

    static void channelReadUpdateTest(ChannelService channelService, Channel channel) {
        System.out.println("채널 단건 조회: " + channelService.read(channel.getId()).getChannelName());
        System.out.println("채널 전체 조회: " + channelService.readAll().size() + "개");
        channelService.update(channel.getId(), "공지사항", "수정된 공지 채널입니다.", "PUBLIC");
        System.out.println("채널 수정 결과 조회: " + channelService.read(channel.getId()).getChannelName());
    }

    static void messageReadUpdateTest(MessageService messageService, Message message) {
        System.out.println("메시지 단건 조회: " + messageService.read(message.getId()).getContent());
        System.out.println("메시지 전체 조회: " + messageService.readAll().size() + "개");
        messageService.update(message.getId(), "수정된 메시지입니다.");
        System.out.println("메시지 수정 결과 조회: " + messageService.read(message.getId()).getContent());
    }

    static void groupReadUpdateTest(GroupService groupService, Group group, User owner, Channel channel) {
        System.out.println("그룹 단건 조회: " + groupService.read(group.getId()).getName());
        System.out.println("그룹 전체 조회: " + groupService.readAll().size() + "개");
        System.out.println("타입별 조회(CHANNEL): " + groupService.findByType("CHANNEL").size() + "개");
        System.out.println("소유자별 조회: " + groupService.findByOwnerId(owner.getId()).size() + "개");

        groupService.addItem(group.getId(), channel.getId());
        System.out.println("그룹 아이템 추가 후: " + groupService.read(group.getId()).getItemIds().size() + "개");
        groupService.removeItem(group.getId(), channel.getId());
        System.out.println("그룹 아이템 제거 후: " + groupService.read(group.getId()).getItemIds().size() + "개");

        groupService.update(group.getId(), "친구 채널 모음");
        System.out.println("그룹 수정 결과 조회: " + groupService.read(group.getId()).getName());
    }

    // ===== 삭제 / 삭제 확인 =====
    static void deleteTest(UserService userService, ChannelService channelService,
                           MessageService messageService, GroupService groupService,
                           User user, Channel channel, Message message, Group group) {
        messageService.delete(message.getId());
        System.out.println("메시지 삭제 후 단건 조회: " + messageService.read(message.getId()));
        System.out.println("메시지 삭제 후 전체 조회: " + messageService.readAll().size() + "개");

        groupService.delete(group.getId());
        System.out.println("그룹 삭제 후 단건 조회: " + groupService.read(group.getId()));
        System.out.println("그룹 삭제 후 전체 조회: " + groupService.readAll().size() + "개");

        channelService.delete(channel.getId());
        System.out.println("채널 삭제 후 단건 조회: " + channelService.read(channel.getId()));
        System.out.println("채널 삭제 후 전체 조회: " + channelService.readAll().size() + "개");

        userService.delete(user.getId());
        System.out.println("유저 삭제 후 단건 조회: " + userService.read(user.getId()));
        System.out.println("유저 삭제 후 전체 조회: " + userService.readAll().size() + "명");
    }

    public static void main(String[] args) {
        UserRepository userRepo = new FileUserRepository();
        ChannelRepository channelRepo = new FileChannelRepository();
        MessageRepository messageRepo = new FileMessageRepository();
        GroupRepository groupRepo = new FileGroupRepository();

        UserService userService = new BasicUserService(userRepo);
        ChannelService channelService = new BasicChannelService(channelRepo, userRepo);
        MessageService messageService = new BasicMessageService(messageRepo, channelRepo, userRepo);
        GroupService groupService = new BasicGroupService(groupRepo);

        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        Message message = messageCreateTest(messageService, channel, user);
        Group group = setupGroup(groupService, user);

        userReadUpdateTest(userService, user);
        channelReadUpdateTest(channelService, channel);
        messageReadUpdateTest(messageService, message);
        groupReadUpdateTest(groupService, group, user, channel);

        deleteTest(userService, channelService, messageService, groupService, user, channel, message, group);
    }
}