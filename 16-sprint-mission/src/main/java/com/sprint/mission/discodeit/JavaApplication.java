package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service_impl_v1.JCFChannelServiceV1;
import com.sprint.mission.discodeit.service_impl_v1.JCFMessageServiceV1;
import com.sprint.mission.discodeit.service_impl_v1.JCFUserServiceV1;

import java.util.Scanner;

public class JavaApplication {

    public static void main(String[] args) {
        UserService userService = new JCFUserServiceV1();
        MessageService messageService = new JCFMessageServiceV1();
        ChannelService channelService = new JCFChannelServiceV1();

        setupUser(userService);
        setupUser(userService);
        setupUser(userService);

        userService.findAll();

        /*User user = setupUser(userService);
        Channel channel = setUpChannel(channelService);

        messageCreateTest(messageService, channel, user);*/
    }

    private static User setupUser(UserService userService) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("유저명을 입력하세요: ");
        String userName = scanner.nextLine();
        System.out.print("이메일을 입력하세요: ");
        String email = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        System.out.println("\n==================");

        return userService.create(userName, email, password);
    }

    private static Channel setUpChannel(ChannelService channelService) {
        Scanner scanner = new Scanner(System.in);
        ChannelType channelType = null;

        while (true) {
            System.out.print("채널을 Public으로 생성하시겠습니까?(y/n): ");
            String s = scanner.nextLine().toLowerCase();
            if (s.equals("y")) {
                channelType = ChannelType.PUBLIC;
                break;
            } else if (s.equals("n")) {
                channelType = ChannelType.PRIVATE;
                break;
            }
            System.out.println("y또는 n만 입력해주세요");
        }

        System.out.print("채널명을 입력해주세요: ");
        String title = scanner.nextLine();
        System.out.print("채널 설명을 입력해주세요:");
        String description = scanner.nextLine();

        return channelService.create(channelType, title, description);
    }

    private static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", channel.getId(), author.getId());
        System.out.println("메시지 생성: "  + message.getId());
    }
}
