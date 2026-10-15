package discodeit;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.jcf.JCFChannelService;
import discodeit.service.jcf.JCFMessageService;
import discodeit.service.jcf.JCFUserService;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {
        // User CRUD
        UserService userService = new JCFUserService();

        System.out.println("User 관련 출력 시작");
        // 저장
        User user1 = userService.create("삼성");
        User user2 = userService.create("애플");
        User user3 = userService.create("LG");
        User user4 = userService.create("화웨이");
        // 단건 조회
        Optional<User> user = userService.find(user1.getId());
        System.out.println(user.toString());
        // 전체 조회
        System.out.println(userService.findAll());
        // 수정
        userService.update(user1.getId(), "Samsung");
        // 수정된 데이터 조회
        Optional<User> updatedUser = userService.find(user1.getId());
        System.out.println(updatedUser.toString());
        // 삭제
        userService.delete(user4.getId());
        // 전체 조회
        System.out.println(userService.findAll());
        System.out.println("User 관련 출력 끝");


        System.out.println();
        System.out.println();


        // Channel CRUD
        ChannelService channelService = new JCFChannelService(userService);
        List<User> users = Arrays.asList(user1, user2, user3);

        System.out.println("Channel 관련 출력 시작");
        // 채널 저장
        Channel ch1 = channelService.create(users, "Seoul");
        Channel ch2 = channelService.create(users, "Toyko");
        Channel ch3 = channelService.create(users, "Hongkong");
        // 채널 단건 조회
        Optional<Channel> findCh = channelService.find(ch1.getId());
        System.out.println(findCh.toString());
        // 채널 전체 조회
        System.out.println(channelService.findAll());
        // 채널 수정
        Channel updateCh = channelService.update(ch1.getId(), "Busan");
        Optional<Channel> updatedCh = channelService.find(ch1.getId());
        System.out.println(updatedCh.toString());
        // 채널 삭제
        channelService.delete(ch1.getId());
        System.out.println(channelService.findAll());
        System.out.println("Channel 관련 출력 끝");

        System.out.println();
        System.out.println();


        // Message CRUD
        MessageService messageService = new JCFMessageService(userService);

        System.out.println("Message 관련 출력 시작");
        // 메세지 저장
        Message comment1 = messageService.create(user1, ch1, "첫번째 테스트 코멘트 입니다.");
        Message comment2 = messageService.create(user2, ch2, "I want to buy Samsung Galaxy Fold 8");
        Message comment2_1 = messageService.create(user2, ch2, "I want to buy IPhone Duo too");
        Message comment3 = messageService.create(user3, ch3, "아 발로란트 하고싶다.");
        // 메세지 단건 조회
        Optional<Message> findMessage = messageService.find(comment1.getId());
        System.out.println(findMessage.get().getComment().toString());
        // 메세지 전체 조회
        System.out.println(messageService.findAll());
        // 메세지 수정 및 출력
        Optional<Message> updatedComment = messageService.find(comment1.getId());
        System.out.println(updatedComment.get().getComment().toString());
        // 메세지 삭제
        messageService.delete(comment1.getId());
        // 메세지 전체 조회
        System.out.println(messageService.findAll());

        System.out.println("Message 관련 출력 끝");

        System.out.println();
        System.out.println();

        System.out.println("유저 삭제 시 해당 유저가 작성한 모든 메세지 삭제 테스트");
        userService.delete(user2.getId());
        System.out.println(userService.findAll());
        System.out.println(messageService.findAll());

        testCascadeDelete();
    }

    private static void testCascadeDelete() {
        System.out.println("===== 연쇄 삭제 테스트 시작 =====");

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService); // 리스너 등록됨
        MessageService messageService = new JCFMessageService(userService); // 리스너 등록됨

        // 준비: 유저 3명, 채널 2개, 메시지 4개
        User a = userService.create("A");
        User b = userService.create("B");
        User c = userService.create("C");

        Channel chX = channelService.create(List.of(a, b, c), "chX");
        Channel chY = channelService.create(List.of(a, b), "chY");

        messageService.create(a, chX, "a1 (chX)");
        messageService.create(a, chY, "a2 (chY)");
        messageService.create(b, chX, "b1 (chX)");
        messageService.create(c, chX, "c1 (chX)");

        System.out.println("[초기 상태]");
        printState(a, b, c, chX, chY, messageService);

        // 시나리오 1: 채널 삭제 -> 유저의 channelList에서 제거
        System.out.println("\n[시나리오 1] chY 삭제");
        channelService.delete(chY.getId());
        printState(a, b, c, chX, chY, messageService);
        // 기대: A, B의 채널 목록에서 chY가 사라지고 chX만 남음. C는 그대로 [chX]

        // 시나리오 2: 유저 삭제 -> 메시지 삭제 + 채널 userList 정리
        System.out.println("\n[시나리오 2] 유저 A 삭제");
        userService.delete(a.getId());
        printState(a, b, c, chX, chY, messageService);
        // 기대: A가 쓴 메시지(a1, a2) 삭제, 남은 메시지는 b1, c1
        //       chX의 userList에서 A가 빠져 [B, C]

        // 시나리오 3: 예외 케이스
        System.out.println("\n[시나리오 3] 예외 케이스");
        try {
            messageService.create(a, chX, "삭제된 유저의 메시지");
        } catch (NoSuchElementException e) {
            System.out.println("메시지 생성 거부: " + e.getMessage());
        }
        try {
            userService.delete(a.getId());
        } catch (NoSuchElementException e) {
            System.out.println("이미 삭제된 유저 재삭제 거부: " + e.getMessage());
        }

        System.out.println("===== 연쇄 삭제 테스트 끝 =====");
    }

    private static void printState(User a, User b, User c, Channel chX, Channel chY,
                                   MessageService messageService) {
        System.out.println("A의 채널: " + names(a.getChannelList()));
        System.out.println("B의 채널: " + names(b.getChannelList()));
        System.out.println("C의 채널: " + names(c.getChannelList()));
        System.out.println("chX 유저: " + chX.getUserList().stream().map(User::getUserName).toList());
        System.out.println("chY 유저: " + chY.getUserList().stream().map(User::getUserName).toList());
        System.out.println("메시지: " + messageService.findAll().stream().map(Message::getComment).toList());
    }

    private static List<String> names(List<Channel> channels) {
        return channels.stream().map(Channel::getChannelName).toList();
    }
}
