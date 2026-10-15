package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.file_service_impl.FileChannelService;
import com.sprint.mission.discodeit.service.file.file_service_impl.FileMessageService;
import com.sprint.mission.discodeit.service.file.file_service_impl.FileUserService;
import com.sprint.mission.discodeit.service.service_impl.JCFChannelService;
import com.sprint.mission.discodeit.service.service_impl.JCFMessageService;
import com.sprint.mission.discodeit.service.service_impl.JCFUserService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JavaApplication {

    public static void main(String[] args) throws IOException {
        MessageService messageService = new FileMessageService();
        UserService userService = new FileUserService(messageService);
        ChannelService channelService = new FileChannelService(userService, messageService);

        // 1. User 생성
        System.out.println("===== 1. User 생성 =====");
        User kim = userService.create("kim", "kim@test.com", "1234");
        User lee = userService.create("lee", "lee@test.com", "1234");
        User park = userService.create("park", "park@test.com", "1234");
        printUsers(userService);

        // 2. Channel 생성 및 참여 (kim이 두 채널 생성, lee와 park이 참여)
        System.out.println("\n===== 2. Channel 생성 및 참여 =====");
        Channel general = channelService.create(kim, ChannelType.PUBLIC, "general", "자유 채널");
        Channel study = channelService.create(kim, ChannelType.PRIVATE, "study", "스터디 채널");
        joinChannel(lee, general);
        joinChannel(park, general);
        joinChannel(lee, study);
        printChannels(channelService);

        // 3. Message 생성
        System.out.println("\n===== 3. Message 생성 =====");
        messageService.create("안녕하세요 kim입니다", general, kim);
        messageService.create("안녕하세요 lee입니다", general, lee);
        messageService.create("안녕하세요 park입니다", general, park);
        messageService.create("스터디 언제 하나요?", study, lee);
        Message kimStudyMessage = messageService.create("내일 해요", study, kim);
        printState(userService, channelService, messageService);
    }

    // User와 Channel 양쪽에 참여 정보를 기록
    private static void joinChannel(User user, Channel channel) {
        user.addChannel(channel);
        channel.addUser(user);
    }

    private static void printState(UserService userService, ChannelService channelService,
                                   MessageService messageService) {
        printUsers(userService);
        printChannels(channelService);
        System.out.println("[전체 Message 수] " + messageService.findAll().size());
    }

    private static void printUsers(UserService userService) {
        System.out.println("[User 목록]");
        for (User user : userService.findAll()) {
            List<String> messages = new ArrayList<>();
            for (Message message : user.getMyMessages()) {
                messages.add(message.getMessage());
            }
            System.out.println("  " + user.getUserName()
                    + " | 참여 채널 수: " + user.getChannels().size()
                    + " | 작성 Message: " + messages);
        }
    }

    private static void printChannels(ChannelService channelService) {
        System.out.println("[Channel 목록]");
        for (Channel channel : channelService.findAll()) {
            List<String> userNames = new ArrayList<>();
            for (User user : channel.getUsers()) {
                userNames.add(user.getUserName());
            }
            List<String> messages = new ArrayList<>();
            for (Message message : channel.getChannelMessages()) {
                messages.add(message.getMessage());
            }
            System.out.println("  " + channel.getTitle()
                    + " | 참여 User: " + userNames
                    + " | Message: " + messages);
        }
    }

    // 기존 콘솔 메뉴 (main에서 직접 입력으로 테스트하고 싶을 때 호출)
    private static void runMenu(UserService userService, MessageService messageService,
                                ChannelService channelService) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            int menu = scanner.nextInt();
            scanner.nextLine();
            if (menu == -1) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            mainLogic(menu, userService, messageService, channelService, scanner);
        }
    }

    private static void mainLogic(int menu, UserService userService, MessageService messageService,
                                  ChannelService channelService, Scanner scanner) {
        switch (menu) {
            case 1 -> {
                User user = setupUser(userService, scanner);
                System.out.println("user 생성 완료: " + user);
            }
            case 2 -> {
                List<User> allUsers = userService.findAll();
                for (int i = 0; i < allUsers.size(); i++) {
                    System.out.println((i + 1) + ": " + allUsers.get(i));
                }
            }
            case 3 -> {
                User user = new User(null, null, null);
                Channel channel = setUpChannel(user, channelService, scanner);
                System.out.println("channel 생성 완료: " + channel);
            }
            case 4 -> {
                List<Channel> allChannels = channelService.findAll();
                for (int i = 0; i < allChannels.size(); i++) {
                    System.out.println((i + 1) + ": " + allChannels.get(i));
                }
            }
            default -> {
                System.out.println("해당하는 메뉴가 없습니다.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("== 메뉴를 고르세요 ==");
        System.out.println("1. 유저 생성");
        System.out.println("2. 모든 유저 조회");
        System.out.println("3. 채널 생성");
        System.out.println("4. 모든 채널 조회");
        System.out.print("메뉴 선택(종료는 -1): ");

    }

    private static User setupUser(UserService userService, Scanner scanner) {
        System.out.print("유저명을 입력하세요: ");
        String userName = scanner.nextLine();
        System.out.print("이메일을 입력하세요: ");
        String email = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        System.out.println("\n==================");

        return userService.create(userName, email, password);
    }

    private static Channel setUpChannel(User user, ChannelService channelService, Scanner scanner) {
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

        return channelService.create(user, channelType, title, description);
    }
}
