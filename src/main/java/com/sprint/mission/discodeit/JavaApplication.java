package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.NoSuchElementException;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();
        JCFChannelService jcfChannelService = new JCFChannelService();
        JCFMessageService jcfMessageService = new JCFMessageService();

        try {
            // 등록
            User user1 = jcfUserService.create("김철수");
            User user2 = jcfUserService.create("박영수");
            System.out.printf("유저 생성 --> %s\n", user1);
            System.out.printf("유저 생성 --> %s\n\n", user2);

            Channel channel1 = jcfChannelService.create("QR 코드");
            Channel channel2 = jcfChannelService.create("위클리 페이퍼");
            System.out.printf("채널 생성 --> %s\n", channel1);
            System.out.printf("채널 생성 --> %s\n\n", channel2);

            System.out.printf("유저 및 채널 관계 설정\n\n채널 이름: %s, 유저 가입: %s, %s\n채널 이름: %s, 유저 가입: %s\n\n"
                    , channel1.getName(), user1.getName(), user2.getName(), channel2.getName(), user1.getName());
            jcfChannelService.addUserToChannel(channel1.getId(), user1);
            jcfChannelService.addUserToChannel(channel1.getId(), user2);
            jcfChannelService.addUserToChannel(channel2.getId(), user1);

            Message message1 = jcfMessageService.create(user1, "안녕하세요 김철수입니다!", channel1);
            Message message2 = jcfMessageService.create(user2, "저는 박영수입니다!", channel1);
            Message message3 = jcfMessageService.create(user1, "안녕하세요!", channel2);
            System.out.printf("메세지 생성 --> %s\n", message1);
            System.out.printf("메세지 생성 --> %s\n", message2);
            System.out.printf("메세지 생성 --> %s\n\n", message3);

            // 조회 (단일)
            User foundUser = jcfUserService.findById(user1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 유저를 찾을 수 없습니다."));
            System.out.printf("유저 단일 조회 --> %s\n\n", foundUser);

            Channel foundChannel = jcfChannelService.findById(channel1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 채널을 찾을 수 없습니다."));
            System.out.printf("채널 단일 조회 --> %s\n\n", foundChannel);

            Message foundMessage = jcfMessageService.findById(message1.getId())
                    .orElseThrow(() -> new NoSuchElementException("조회할 메세지를 찾을 수 없습니다."));
            System.out.printf("메세지 단일 조회 --> %s\n\n", foundMessage);

            // 조회 (전체)
            List<User> users = jcfUserService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            users.forEach((user) -> System.out.printf("\t%s\n", user));
            System.out.print("]\n\n");

            System.out.print("채널 전체 조회 --> [\n");
            List<Channel> channels = jcfChannelService.findAll();
            channels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

            System.out.print("메세지 전체 조회 --> [\n");
            List<Message> messages = jcfMessageService.findAll();
            messages.forEach((message) -> System.out.printf("\t%s\n", message));
            System.out.print("]\n\n");

            // 수정
            System.out.printf("유저 수정 전 --> %s\n", user1);
            System.out.printf("유저 수정 후 --> %s\n\n", jcfUserService.updateName(user1.getId(), "이수철"));
            System.out.printf("채널 수정 전 --> %s\n", channel1);
            System.out.printf("채널 수정 후 --> %s\n\n", jcfChannelService.updateName(channel1.getId(), "QR 코드 2"));
            System.out.printf("메세지 수정 전 --> %s\n", message1);
            System.out.printf("메세지 수정 후 --> %s\n\n", jcfMessageService.updateContent(message1.getId(), "안녕하세요 " +
                    "이수철입니다!"));

            // 삭제
            System.out.printf("유저 삭제 --> %s\n", user1);
            jcfUserService.delete(user1.getId());
            users = jcfUserService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            users.forEach((user) -> System.out.printf("\t%s\n", user));
            System.out.print("]\n\n");

            System.out.printf("채널 삭제 --> %s\n", channel2);
            jcfChannelService.delete(channel2.getId());
            channels = jcfChannelService.findAll();
            System.out.print("채널 전체 조회 --> [\n");
            channels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

            System.out.printf("메세지 삭제 --> %s\n", message2);
            jcfMessageService.delete(message2.getId());
            messages = jcfMessageService.findAll();
            System.out.print("메세지 전체 조회 --> [\n");
            messages.forEach((message) -> System.out.printf("\t%s\n", message));
            System.out.print("]\n\n");

            // 채널 탈퇴
            System.out.printf("유저 채널 탈퇴 --> 유저 이름: %s, 채널 이름: %s\n", user2.getName(), channel1.getName());
            jcfChannelService.removeUserFromChannel(channel1.getId(), user2.getId());
            channels = jcfChannelService.findAll();
            System.out.print("채널 전체 조회 --> [\n");
            channels.forEach((channel) -> System.out.printf("\t%s\n", channel));
            System.out.print("]\n\n");

        } catch (NoSuchElementException e) {
            System.out.printf("해당 개체를 찾을 수 없습니다. 이유: %s\n", e.getMessage());
        } catch (IllegalStateException e) {
            System.out.printf("요청한 작업을 수행할 수 없는 상태입니다. 이유: %s\n", e.getMessage());
        }
    }
}
