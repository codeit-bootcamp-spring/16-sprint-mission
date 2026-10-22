package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;


import java.io.UncheckedIOException;
import java.util.List;
import java.util.NoSuchElementException;

public class JavaApplication {
    public static void main(String[] args) {
//        UserRepository userRepository = new JCFUserRepository();
//        ChannelRepository channelRepository = new JCFChannelRepository();
//        MessageRepository messageRepository = new JCFMessageRepository();
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();


        ChannelService channelService = new BasicChannelService(channelRepository, userRepository);
        UserService userService = new BasicUserService(userRepository, channelService);
        MessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);


        try {
            // 등록
            User user1 = userService.create("김철수");
            User user2 = userService.create("박영수");
            System.out.printf("유저 생성 --> %s\n", user1);
            System.out.printf("유저 생성 --> %s\n\n", user2);

            Channel channel1 = channelService.create("QR 코드");
            Channel channel2 = channelService.create("위클리 페이퍼");
            System.out.printf("채널 생성 --> %s\n", channel1);
            System.out.printf("채널 생성 --> %s\n\n", channel2);

            System.out.printf("유저 및 채널 관계 설정\n\n채널 이름: %s, 유저 가입: %s, %s\n채널 이름: %s, 유저 가입: %s\n\n"
                    , channel1.getName(), user1.getName(), user2.getName(), channel2.getName(), user1.getName());
            channelService.addUserToChannel(channel1.getId(), user1.getId());
            channelService.addUserToChannel(channel1.getId(), user2.getId());
            channelService.addUserToChannel(channel2.getId(), user1.getId());

            user1 = userService.findById(user1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 유저를 찾을 수 없습니다."));
            user2 = userService.findById(user2.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 유저를 찾을 수 없습니다."));
            channel1 = channelService.findById(channel1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 채널을 찾을 수 없습니다."));
            channel2 = channelService.findById(channel2.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 채널을 찾을 수 없습니다."));


            Message message1 = messageService.create(user1.getId(), "안녕하세요 김철수입니다!", channel1.getId());
            Message message2 = messageService.create(user2.getId(), "저는 박영수입니다!", channel1.getId());
            Message message3 = messageService.create(user1.getId(), "안녕하세요!", channel2.getId());
            System.out.printf("메세지 생성 --> %s\n", message1);
            System.out.printf("메세지 생성 --> %s\n", message2);
            System.out.printf("메세지 생성 --> %s\n\n", message3);

            // 조회 (단일)
            user1 = userService.findById(user1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 유저를 찾을 수 없습니다."));
            System.out.printf("유저 단일 조회 --> %s\n\n", user1);

            channel1 = channelService.findById(channel1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 채널을 찾을 수 없습니다."));
            System.out.printf("채널 단일 조회 --> %s\n\n", channel1);

            message1 = messageService.findById(message1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 메세지를 찾을 수 없습니다."));
            System.out.printf("메세지 단일 조회 --> %s\n\n", message1);

            // 조회 (전체)
            List<User> users = userService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            users.forEach((user) -> System.out.printf("\t%s\n", user));
            System.out.print("]\n\n");

            System.out.print("채널 전체 조회 --> [\n");
            List<Channel> channels = channelService.findAll();
            channels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

            System.out.print("메세지 전체 조회 --> [\n");
            List<Message> messages = messageService.findAll();
            messages.forEach((message) -> System.out.printf("\t%s\n", message));
            System.out.print("]\n\n");

            // 수정
            System.out.printf("유저 수정 전 --> %s\n", user1);
            user1.setName("이수철");
            System.out.printf("유저 수정 후 --> %s\n\n", userService.update(user1.getId(), user1));
            System.out.printf("채널 수정 전 --> %s\n", channel1);
            channel1.setName("QR 코드 2");
            System.out.printf("채널 수정 후 --> %s\n\n", channelService.update(channel1.getId(), channel1));
            System.out.printf("메세지 수정 전 --> %s\n", message1);
            message1.setContent("안녕하세요 이수철입니다!");
            System.out.printf("메세지 수정 후 --> %s\n\n", messageService.update(message1.getId(), message1));
            
            // 삭제
            System.out.printf("유저 삭제 --> %s\n", user1);
            userService.delete(user1.getId());
            users = userService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            users.forEach((user) -> System.out.printf("\t%s\n", user));
            System.out.print("]\n\n");

            System.out.printf("채널 삭제 --> %s\n", channel2);
            channelService.delete(channel2.getId());
            channels = channelService.findAll();
            System.out.print("채널 전체 조회 --> [\n");
            channels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

            System.out.printf("메세지 삭제 --> %s\n", message2);
            messageService.delete(message2.getId());
            messages = messageService.findAll();
            System.out.print("메세지 전체 조회 --> [\n");
            messages.forEach((message) -> System.out.printf("\t%s\n", message));
            System.out.print("]\n\n");

            // 채널 탈퇴
            System.out.printf("유저 채널 탈퇴 --> 유저 이름: %s, 채널 이름: %s\n", user2.getName(), channel1.getName());
            channelService.removeUserFromChannel(channel1.getId(), user2.getId());
            channels = channelService.findAll();
            System.out.print("채널 전체 조회 --> [\n");
            channels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

        } catch (NoSuchElementException e) {
            System.out.printf("해당 개체를 찾을 수 없습니다. 이유: %s\n", e.getMessage());
        } catch (IllegalStateException e) {
            System.out.printf("요청한 작업을 수행할 수 없는 상태입니다. 이유: %s\n", e.getMessage());
        } catch (NullPointerException e) {
            System.out.printf("개체가 참조하는 값이 없습니다. 이유: %s\n", e.getMessage());
        } catch (NoClassDefFoundError e) {
            System.out.printf("정의되지 않은 클래스입니다. 이유: %s\n", e.getMessage());
        } catch (UncheckedIOException e) {
            System.out.printf("파일 입출력 예외가 발생했습니다. 이유: %s\n", e.getMessage());
        }
    }
}
