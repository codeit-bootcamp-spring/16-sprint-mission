package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.IOException;
import java.util.NoSuchElementException;

public class JavaApplication {

    private static int failCount = 0;

    public static void main(String[] args) throws IOException {
        System.out.println("################ JCF 구현체 ################");
        UserService jcfUser = new JCFUserService();
        ChannelService jcfChannel = new JCFChannelService();
        MessageService jcfMessage = new JCFMessageService(jcfUser, jcfChannel);
        testServices(jcfUser, jcfChannel, jcfMessage);

        System.out.println("\n################ File 구현체 ################");
        UserService fileUser = new FileUserService();
        ChannelService fileChannel = new FileChannelService();
        MessageService fileMessage = new FileMessageService(fileUser, fileChannel);
        testServices(fileUser, fileChannel, fileMessage);

        System.out.println("\n===== 결과: " + (failCount == 0 ? "모두 PASS" : "FAIL " + failCount + "건") + " =====");
    }

    // 등록 → 조회(단건/다건) → 수정 → 수정된 데이터 조회 → 삭제 → 삭제 확인 → 연관 검증
    private static void testServices(UserService userService, ChannelService channelService,
                                     MessageService messageService) {
        String suffix = String.valueOf(System.currentTimeMillis() % 100000);

        System.out.println("===== 등록 =====");
        User user = userService.create(new User("kim" + suffix, "kim" + suffix + "@test.com", "1234"));
        Channel channel = channelService.create(new Channel(ChannelType.PUBLIC, "general" + suffix, "자유 채널"));
        Message message = messageService.create(new Message("안녕하세요" + suffix, channel, user));
        check("User 등록", userService.findById(user.getId()).isPresent());
        check("Channel 등록", channelService.findById(channel.getId()).isPresent());
        check("Message 등록", messageService.findById(message.getId()).isPresent());

        System.out.println("===== 조회 =====");
        check("User 단건 조회", userService.findById(user.getId()).get().getUserName().equals("kim" + suffix));
        check("Channel 단건 조회", channelService.findById(channel.getId()).get().getTitle().equals("general" + suffix));
        check("Message 단건 조회", messageService.findById(message.getId()).get().getMessage().equals("안녕하세요" + suffix));
        check("User 다건 조회", userService.findAll().stream().anyMatch(u -> u.getId().equals(user.getId())));
        check("Channel 다건 조회", channelService.findAll().stream().anyMatch(c -> c.getId().equals(channel.getId())));
        check("Message 다건 조회", messageService.findAll().stream().anyMatch(m -> m.getId().equals(message.getId())));

        System.out.println("===== 수정 및 수정된 데이터 조회 =====");
        userService.update(user.getId(), "lee" + suffix, null, null);
        channelService.update(channel.getId(), ChannelType.PRIVATE, "study" + suffix, null);
        messageService.update(message.getId(), "수정됨" + suffix);
        User updatedUser = userService.findById(user.getId()).get();
        Channel updatedChannel = channelService.findById(channel.getId()).get();
        check("User 수정 반영 (null 필드는 유지)",
                updatedUser.getUserName().equals("lee" + suffix) && updatedUser.getEmail().equals("kim" + suffix + "@test.com"));
        check("Channel 수정 반영", updatedChannel.getTitle().equals("study" + suffix)
                && updatedChannel.getChannelType() == ChannelType.PRIVATE
                && updatedChannel.getDescription().equals("자유 채널"));
        check("Message 수정 반영", messageService.findById(message.getId()).get().getMessage().equals("수정됨" + suffix));

        System.out.println("===== 연관 도메인 검증 =====");
        check("존재하지 않는 User의 Message 생성은 실패",
                fails(() -> messageService.create(new Message("x", channel, new User("ghost", "g@t.com", "1")))));
        check("존재하지 않는 Channel의 Message 생성은 실패",
                fails(() -> messageService.create(new Message("x", new Channel(ChannelType.PUBLIC, "ghost", ""), user))));

        System.out.println("===== 삭제 및 삭제 확인 =====");
        messageService.delete(message.getId());
        channelService.delete(channel.getId());
        userService.delete(user.getId());
        check("Message 삭제 확인", messageService.findById(message.getId()).isEmpty());
        check("Channel 삭제 확인", channelService.findById(channel.getId()).isEmpty());
        check("User 삭제 확인", userService.findById(user.getId()).isEmpty());
        check("없는 User 삭제는 실패", fails(() -> userService.delete(user.getId())));
    }

    private static boolean fails(Runnable action) {
        try {
            action.run();
            return false;
        } catch (NoSuchElementException e) {
            return true;
        }
    }

    private static void check(String description, boolean condition) {
        if (!condition) {
            failCount++;
        }
        System.out.println("  [" + (condition ? "PASS" : "FAIL") + "] " + description);
    }
}
