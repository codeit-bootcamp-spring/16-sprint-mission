package discodeit;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.repository.jcf.JCFUserRepository;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;
import discodeit.service.file.FileChannelService;
import discodeit.service.file.FileMessageService;
import discodeit.service.file.FileUserService;
import discodeit.service.jcf.JCFChannelService;
import discodeit.service.jcf.JCFMessageService;
import discodeit.service.jcf.JCFUserService;

import java.util.*;

public class JCFJavaApplication {
    public static void main(String[] args)  {
        // User CRUD
        UserService userService = new JCFUserService(new JCFUserRepository());

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



    }
}
