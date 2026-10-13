package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class JavaApplication {
    public static void main(String[] args) {
        JCFUserService jcfUserService = new JCFUserService();
        JCFChannelService jcfChannelService = new JCFChannelService();
        UserChannelManager userChannelManager = new UserChannelManager(jcfUserService, jcfChannelService);

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

            System.out.print("유저 및 채널 관계 설정\n\n");
            userChannelManager.addUserToChannel(channel1.getId(), user1.getId());
            userChannelManager.addUserToChannel(channel1.getId(), user2.getId());
            userChannelManager.addChannelToUser(user1.getId(), channel2.getId());

            // 조회 (단일)
            Optional<User> foundUser = jcfUserService.findById(user1.getId());
            foundUser.ifPresent(user -> System.out.printf("유저 단일 조회 --> %s\n\n", user));

            Optional<Channel> foundChannel = jcfChannelService.findById(channel1.getId());
            foundChannel.ifPresent(channel -> System.out.printf("채널 단일 조회 --> %s\n\n", channel));

            // 조회 (전체)
            Optional<List<User>> users = jcfUserService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            users.ifPresent(list -> list.forEach((user) -> System.out.printf("\t%s\n", user)));
            System.out.print("]\n\n");

            System.out.print("채널 전체 조회 --> [\n");
            Optional<List<Channel>> channels = jcfChannelService.findAll();
            channels.ifPresent(list -> list.forEach((channel) -> System.out.printf("\t%s\n", channel)));
            System.out.print("]\n\n");

            // 수정
            System.out.printf("유저 수정 전 --> %s\n", user1);
            System.out.printf("유저 수정 후 --> %s\n\n", jcfUserService.update(user1.getId(), "이수철"));
            System.out.printf("채널 수정 전 --> %s\n", channel1);
            System.out.printf("채널 수정 후 --> %s\n\n", jcfChannelService.update(channel1.getId(), "QR 코드 2"));

            // 삭제
            System.out.printf("유저 삭제 --> %s\n", user1);
            userChannelManager.deleteUser(user1.getId());
            users = jcfUserService.findAll();
            System.out.print("유저 전체 조회 --> [\n");
            users.ifPresent(list -> list.forEach((user) -> System.out.printf("\t%s\n", user)));
            System.out.print("]\n\n");

            System.out.printf("채널 삭제 --> %s\n", channel2);
            userChannelManager.deleteChannel(channel2.getId());
            channels = jcfChannelService.findAll();
            System.out.print("채널 전체 조회 --> [\n");
            channels.ifPresent(list -> list.forEach((channel) -> System.out.printf("\t%s\n", channel)));
            System.out.print("]\n\n");

        } catch (NoSuchElementException e) {
            System.out.printf("오류 발생: %s\n", e.getMessage());
        }
    }
}
