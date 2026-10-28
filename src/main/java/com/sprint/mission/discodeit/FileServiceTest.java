package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

public class FileServiceTest {
    public static void main(String[] args) {
//        UserService userService = new FileUserService();

//
//        User user = userService.create("김파일", "7254", "kimfile@gmail.com");
//
//        User foundUser = userService.findById(user.getId());
//        System.out.println(foundUser.getName());
//        System.out.println(foundUser.getEmail());

//        for (User savedUser : userService.findAll()) {
//            System.out.println(savedUser.getName() + " / " + savedUser.getEmail());
//        }
//
//        User user = userService.findAll().get(0);
//        userService.update(
//                user.getId(), "김파일수정", user.getPassword(), user.getEmail());
//
//        User updatedUser = userService.findById(user.getId());
//        System.out.println(updatedUser.getName());
//        System.out.println(updatedUser.getEmail());
//
//        User user = userService.findAll().get(0);
//        userService.delete(user.getId());
//
//        try {
//            userService.findById(user.getId());
//        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
//        }
//
//        System.out.println(userService.findAll().size());

//        System.out.println(userService.findAll().size());
        FileUserService userService = new FileUserService();
        FileChannelService channelService = new FileChannelService(userService);
        FileMessageService messageService = new FileMessageService(userService, channelService);
//
//        User user = userService.findAll().stream().filter(u -> u.getEmail().equals("join-test@example.com")).findFirst().orElseGet(() ->
//                userService.create("참여테스트", "1234", "join-test@example.com"));
//
//        //테스트 채널 준비
//        Channel channel = channelService.findAll().stream().filter(c -> c.getName().equals("파일 참여 테스트 채널")).findFirst().orElseGet(() ->
//                channelService.create("파일 참여 테스트 채널"));
//
//        //처음 실행할 때만 참여
//        boolean alreadyJoined = channel.getUsers().stream()
//                .anyMatch(u -> u.getId().equals(user.getId()));
//
//        if (!alreadyJoined) {
//            channelService.join(channel.getId(), user.getId());
//        }
//
//        //파일에서 다시 조회하여 양쪽 관계 확인
//        User savedUser = userService.findById(user.getId());
//        Channel savedChannel = channelService.findById(channel.getId());
//
//        System.out.println("사용자의 참여 채널:");
//        for (Channel joinedChannel : savedUser.getChannels()) {
//            System.out.println(joinedChannel.getName());
//        }
//
//        System.out.println("채널의 참여자:");
//        for (User participant : savedChannel.getUsers()) {
//            System.out.println(participant.getName());
//        }
//
//        //중복 참여 차단 확인
//        try {
//            channelService.join(channel.getId(), user.getId());
//        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
//        }

        User user = userService.findAll().stream()
                .filter(u -> u.getEmail().equals("message-test@example.com"))
                .findFirst()
                .orElseGet(() -> userService.create(
                        "메시지테스트", "1234", "message-test@example.com"));

        Channel channel = channelService.findAll().stream()
                .filter(c -> c.getName().equals("파일 메시지 테스트 채널"))
                .findFirst()
                .orElseGet(() -> channelService.create("파일 메시지 테스트 채널"));

        boolean joined = channel.getUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (!joined) {
            channelService.join(channel.getId(), user.getId());
        }

        Message savedMessage = messageService.findAll().stream()
                .filter(m -> m.getUser().getId().equals(user.getId())
                        && m.getChannel().getId().equals(channel.getId()))
                .findFirst()
                .orElse(null);

        if (savedMessage == null) {

            Message message = messageService.create(
                    "파일 저장 테스트", user.getId(), channel.getId());

            System.out.println(messageService.findById(message.getId()).getComment());

            messageService.update(message.getId(), "파일 수정 테스트");

            System.out.println(messageService.findById(message.getId()).getComment());

            System.out.println("수정 내용으로 다시 실행하세요.");
        } else {

            System.out.println("재실행 조회: " + savedMessage.getComment());

            messageService.delete(savedMessage.getId());

            try {
                messageService.findById(savedMessage.getId());
                throw new IllegalStateException("삭제한 메시지가 조회됩니다.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

            boolean userHasMessage = userService.findById(user.getId())
                    .getMessages().stream()
                    .anyMatch(m -> m.getId().equals(savedMessage.getId()));

            boolean channelHasMessage = channelService.findById(channel.getId())
                    .getMessages().stream()
                    .anyMatch(m -> m.getId().equals(savedMessage.getId()));

            System.out.println("사용자 목록에 남음: " + userHasMessage);
            System.out.println("채널 목록에 남음: " + channelHasMessage);
        }
    }
}