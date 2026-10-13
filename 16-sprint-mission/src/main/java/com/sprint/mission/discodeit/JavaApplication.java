package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service_impl.DiscordService;
import com.sprint.mission.discodeit.service_impl.JCFChannelService;
import com.sprint.mission.discodeit.service_impl.JCFMessageService;
import com.sprint.mission.discodeit.service_impl.JCFUserService;

import java.util.List;
import java.util.Scanner;

public class JavaApplication {

    public static void main(String[] args) {
        DiscordService discordService = new DiscordService(new JCFUserService(), new JCFChannelService(), new JCFMessageService());

        Scanner scanner = new Scanner(System.in);
        while (true) {
            printMenu();
            int menu = scanner.nextInt();
            scanner.nextLine();
            if (menu == -1) {
                System.out.println("프로그램을 종료합니다.");
                break;
            }

            mainLogic(menu, discordService, scanner);
        }


    }

    private static void mainLogic(int menu, DiscordService discordService, Scanner scanner) {
        switch (menu) {
            case 1 -> {
                User user = setupUser(discordService, scanner);
                System.out.println("user 생성 완료: " + user);
            }
            case 2 -> {
                List<User> allUsers = discordService.findAllUsers();
                for (int i = 0; i < allUsers.size(); i++) {
                    System.out.println((i + 1) + ": " + allUsers.get(i));
                }
            }
            case 3 -> {
                Channel channel = setUpChannel(discordService, scanner);
                System.out.println("channel 생성 완료: " + channel);
            }
            case 4 -> {
                List<Channel> allChannels = discordService.findAllChannels();
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

    private static User setupUser(DiscordService discordService, Scanner scanner) {
        System.out.print("유저명을 입력하세요: ");
        String userName = scanner.nextLine();
        System.out.print("이메일을 입력하세요: ");
        String email = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
        String password = scanner.nextLine();

        System.out.println("\n==================");

        return discordService.createUser(userName, email, password);
    }

    private static Channel setUpChannel(DiscordService discordService, Scanner scanner) {
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

        return discordService.createChannel(channelType, title, description);
    }
}
