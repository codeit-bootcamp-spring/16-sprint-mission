package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();
        JCFChannelService jcfChannelService = new JCFChannelService();

        try {
            // 등록
            User user1 = jcfUserService.create("김철수");
            User user2 = jcfUserService.create("박영수");
            System.out.printf("가입 완료 --> %s\n", user1);
            System.out.printf("가입 완료 --> %s\n\n", user2);

            Channel channel1 = jcfChannelService.create("QR 코드");
            Channel channel2 = jcfChannelService.create("위클리 페이퍼");
            System.out.printf("채널 생성 완료 --> %s\n", channel1);
            System.out.printf("채널 생성 완료 --> %s\n\n", channel2);

            jcfChannelService.addUser(channel1.getId(), user1);
            jcfChannelService.addUser(channel1.getId(), user2);
            jcfChannelService.addUser(channel2.getId(), user1);


            // 조회 (단일)
            Optional<User> foundUser = jcfUserService.findById(user1.getId());
            foundUser.ifPresentOrElse(
                    user -> System.out.printf("단일 조회 완료 --> %s\n\n", user),
                    () -> System.out.printf("조회 실패 (유저 없음)\n\n")
            );
            Optional<Channel> foundChannel = jcfChannelService.findById(channel1.getId());
            foundChannel.ifPresentOrElse(
                    channel -> System.out.printf("단일 조회 완료 --> %s\n\n", channel),
                    () -> System.out.printf("조회 실패 (채널 없음)\n\n")
            );


            // 조회 (전체)
            Optional<List<User>> userList = jcfUserService.findAll();
            userList.ifPresentOrElse(
                    list -> {
                        System.out.printf("전체 조회 완료 --> [\n");
                        list.forEach((user) -> System.out.printf("\t%s\n", user));
                        System.out.printf("]\n\n");
                    },
                    () -> System.out.printf("조회 실패 (유저 없음)\n\n")
            );

            // 수정
            System.out.printf("수정 전 --> %s\n", user1);
            System.out.printf("수정 후 --> %s\n\n", jcfUserService.update(user1.getId(), "이수철"));

            // 삭제
            System.out.printf("삭제 시작 --> %s\n", user1);
            jcfUserService.delete(user1.getId());
            userList = jcfUserService.findAll();
            userList.ifPresentOrElse(
                    list -> {
                        System.out.printf("전체 조회 완료 --> [\n");
                        list.forEach((user) -> System.out.printf("\t%s\n", user));
                        System.out.printf("]\n\n");
                    },
                    () -> System.out.printf("조회 실패 (유저 없음)\n\n")
            );

            foundChannel = jcfChannelService.findById(channel1.getId());
            foundChannel.ifPresentOrElse(
                    channel -> System.out.printf("단일 조회 완료 --> %s\n\n", channel),
                    () -> System.out.printf("조회 실패 (채널 없음)\n\n")
            );

        } catch (NoSuchElementException e) {
            System.out.printf("오류 발생: %s\n", e.getMessage());
        }
    }
}
