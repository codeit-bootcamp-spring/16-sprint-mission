package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserWithdrawalService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.file.FileUserWithdrawalService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserWithdrawalService;

import java.util.UUID;

public class JavaApplication {

    public static void main(String[] args) {
        // 의존 방향: User <- Channel <- Message <- UserWithdrawal (아래 서비스부터 생성)
        //UserService userService = new JCFUserService();
        //ChannelService channelService = new JCFChannelService(userService);
        //MessageService messageService = new JCFMessageService(userService, channelService);
        //UserWithdrawalService withdrawalService =
        //        new JCFUserWithdrawalService(userService, channelService, messageService);

        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService(userService);
        MessageService messageService = new FileMessageService(userService, channelService);
        UserWithdrawalService withdrawalService =
                new FileUserWithdrawalService(userService, channelService, messageService);

        // =========================
        // 1. User 테스트
        // =========================
        System.out.println("===== User 테스트 =====");

        User user1 = userService.create(new User("user1", "user1@test.com", "password123"));
        User user2 = userService.create(new User("user2", "user2@test.com", "password456"));

        System.out.println("생성된 사용자: " + user1.getUsername());
        System.out.println("ID 조회: " + userService.findById(user1.getId()).getEmail());
        System.out.println("전체 사용자 수: " + userService.findAll().size());

        // 본인 이메일을 유지하면서 수정
        User updatedUser = userService.update(
                user1.getId(), "updatedUser1", "user1@test.com", "password123");
        System.out.println("사용자 수정: " + updatedUser.getUsername());
        System.out.println("수정 후 조회: " + userService.findById(user1.getId()).getUsername());

        expectFail("중복 이메일",
                () -> userService.create(new User("user3", "user1@test.com", "password789")));
        expectFail("짧은 비밀번호",
                () -> userService.create(new User("user3", "user3@test.com", "123")));
        expectFail("존재하지 않는 사용자 조회",
                () -> userService.findById(UUID.randomUUID()));

        // =========================
        // 2. Channel 테스트
        // =========================
        System.out.println("\n===== Channel 테스트 =====");

        Channel channel = channelService.create(
                new Channel(user1.getId(), "general", "일반 채널"));

        System.out.println("생성된 채널: " + channel.getName());
        System.out.println("채널 ID 조회: " + channelService.findById(channel.getId()).getId());
        System.out.println("전체 채널 수: " + channelService.findAll().size());
        System.out.println("소유자 자동 가입 (Channel.users): " + channel.hasUser(user1.getId()));
        System.out.println("소유자 자동 가입 (User.channels): " + user1.hasChannel(channel.getId()));

        // 입장
        channelService.join(channel.getId(), user2.getId());
        System.out.println("입장 후 멤버 수: "
                + userService.findAllByChannelId(channel.getId()).size());
        System.out.println("양쪽 기록 일치: "
                + (channel.hasUser(user2.getId()) && user2.hasChannel(channel.getId())));

        expectFail("존재하지 않는 사용자의 입장",
                () -> channelService.join(channel.getId(), UUID.randomUUID()));
        expectFail("이미 가입한 사용자의 입장",
                () -> channelService.join(channel.getId(), user2.getId()));
        expectFail("일반 사용자의 채널 수정",
                () -> channelService.update(channel.getId(), user2.getId(), "hacked", "x"));

        // 소유자의 채널 수정
        Channel updatedChannel = channelService.update(
                channel.getId(), user1.getId(), "general-updated", "수정된 설명");
        System.out.println("채널 수정: " + updatedChannel.getName() + " / " + updatedChannel.getDescription());

        // =========================
        // 3. Message 테스트
        // =========================
        System.out.println("\n===== Message 테스트 =====");

        Message message = messageService.create(
                new Message(user1.getId(), channel.getId(), "안녕하세요!"));

        System.out.println("메시지 생성: " + message.getContent());
        System.out.println("메시지 ID 조회: " + messageService.findById(message.getId()).getId());
        System.out.println("전체 메시지 수: " + messageService.findAll().size());
        System.out.println("채널별 메시지 수: "
                + messageService.findAllByChannelId(channel.getId()).size());
        System.out.println("양쪽 기록 일치: "
                + (user1.getMessages().contains(message.getId())
                && channel.getMessages().contains(message.getId())));

        // 채널 멤버가 아닌 사용자의 메시지 작성
        User user3 = userService.create(new User("user3", "user3@test.com", "password789"));
        expectFail("채널 멤버가 아닌 사용자의 메시지 작성",
                () -> messageService.create(new Message(user3.getId(), channel.getId(), "몰래 작성")));

        expectFail("존재하지 않는 채널에 메시지 작성",
                () -> messageService.create(new Message(user1.getId(), UUID.randomUUID(), "없는 채널")));

        // 작성자의 수정
        Message updatedMessage = messageService.update(
                message.getId(), user1.getId(), "수정된 메시지입니다.");
        System.out.println("수정된 메시지 내용: " + updatedMessage.getContent());

        expectFail("다른 사용자의 메시지 수정",
                () -> messageService.update(message.getId(), user2.getId(), "권한 없는 수정"));
        System.out.println("권한 없는 수정 후 내용 유지: "
                + messageService.findById(message.getId()).getContent());

        expectFail("다른 사용자의 메시지 삭제",
                () -> messageService.delete(message.getId(), user2.getId()));

        // 작성자의 삭제
        messageService.delete(message.getId(), user1.getId());
        System.out.println("삭제 후 메시지 수: " + messageService.findAll().size());
        System.out.println("삭제 후 양쪽 기록 정리: "
                + (user1.getMessages().isEmpty() && channel.getMessages().isEmpty()));
        expectFail("삭제된 메시지 조회",
                () -> messageService.findById(message.getId()));

        // =========================
        // 4. 소유권 / 퇴장 테스트
        // =========================
        System.out.println("\n===== 소유권 / 퇴장 테스트 =====");

        expectFail("소유자가 아닌 사용자의 소유권 양도",
                () -> channelService.transferOwnership(channel.getId(), user2.getId(), user2.getId()));
        expectFail("채널 멤버가 아닌 사용자에게 양도",
                () -> channelService.transferOwnership(channel.getId(), user1.getId(), user3.getId()));

        channelService.transferOwnership(channel.getId(), user1.getId(), user2.getId());
        System.out.println("소유권 양도 결과: " + channel.isOwner(user2.getId()));

        expectFail("새 소유자의 퇴장",
                () -> channelService.leave(channel.getId(), user2.getId()));
        System.out.println("소유자 퇴장 거부 후에도 멤버인가: " + channel.hasUser(user2.getId()));

        channelService.leave(channel.getId(), user1.getId());
        System.out.println("기존 소유자 퇴장 후 멤버 수: "
                + userService.findAllByChannelId(channel.getId()).size());
        System.out.println("퇴장 후 양쪽 기록 정리: "
                + (!channel.hasUser(user1.getId()) && !user1.hasChannel(channel.getId())));

        expectFail("이미 퇴장한 사용자의 퇴장",
                () -> channelService.leave(channel.getId(), user1.getId()));

        // =========================
        // 5. 삭제 테스트 (관계가 남아 있으면 삭제 거부)
        // =========================
        System.out.println("\n===== 삭제 테스트 =====");

        Message message2 = messageService.create(
                new Message(user2.getId(), channel.getId(), "삭제 테스트용 메시지"));

        expectFail("참여 중인 채널이 있는 사용자 삭제",
                () -> userService.delete(user2.getId()));
        expectFail("메시지가 남아 있는 채널 삭제",
                () -> channelService.delete(channel.getId(), user2.getId()));
        expectFail("소유자가 아닌 사용자의 채널 삭제",
                () -> channelService.delete(channel.getId(), user1.getId()));

        // 올바른 삭제 순서: 메시지 -> 채널 -> 사용자
        messageService.delete(message2.getId(), user2.getId());
        System.out.println("삭제 후 메시지 수: " + messageService.findAll().size());

        channelService.delete(channel.getId(), user2.getId());
        System.out.println("삭제 후 채널 수: " + channelService.findAll().size());
        System.out.println("채널 삭제 후 user2의 채널 목록 정리: " + user2.getChannels().isEmpty());

        // 관계가 없는 사용자는 바로 삭제 가능
        userService.delete(user1.getId());
        System.out.println("삭제 후 사용자 수: " + userService.findAll().size());
        expectFail("삭제된 사용자 조회",
                () -> userService.findById(user1.getId()));

        // =========================
        // 6. 탈퇴 테스트 (UserWithdrawalService)
        // =========================
        System.out.println("\n===== 탈퇴 테스트 =====");

        Channel withdrawChannel = channelService.create(
                new Channel(user2.getId(), "withdraw-test", "탈퇴 테스트 채널"));
        channelService.join(withdrawChannel.getId(), user3.getId());

        Message user3Message = messageService.create(
                new Message(user3.getId(), withdrawChannel.getId(), "탈퇴할 사용자의 메시지"));
        Message user2Message = messageService.create(
                new Message(user2.getId(), withdrawChannel.getId(), "채널 소유자의 메시지"));

        System.out.println("탈퇴 전 채널 멤버 수: "
                + userService.findAllByChannelId(withdrawChannel.getId()).size());
        System.out.println("탈퇴 전 채널 메시지 수: " + withdrawChannel.getMessages().size());

        // 소유한 채널이 있으면 거부되고, 아무것도 바뀌지 않아야 함
        expectFail("소유한 채널이 있는 사용자 탈퇴",
                () -> withdrawalService.withdraw(user2.getId()));
        System.out.println("탈퇴 거부 후 메시지 유지: " + (messageService.findAll().size() == 2));
        System.out.println("탈퇴 거부 후 채널 멤버 유지: " + withdrawChannel.hasUser(user2.getId()));

        // 소유한 채널이 없는 사용자는 탈퇴 가능
        withdrawalService.withdraw(user3.getId());
        System.out.println("탈퇴 후 사용자 삭제: "
                + userService.findAll().stream().noneMatch(u -> u.getId().equals(user3.getId())));
        System.out.println("탈퇴 후 채널 멤버 정리: " + !withdrawChannel.hasUser(user3.getId()));
        System.out.println("탈퇴 후 채널 메시지 정리: "
                + (withdrawChannel.getMessages().size() == 1
                && !withdrawChannel.getMessages().contains(user3Message.getId())));
        System.out.println("탈퇴 후 전체 메시지 수: " + messageService.findAll().size());

        expectFail("이미 탈퇴한 사용자의 재탈퇴",
                () -> withdrawalService.withdraw(user3.getId()));

        // 정리: 메시지 -> 채널 -> 사용자
        messageService.delete(user2Message.getId(), user2.getId());
        channelService.delete(withdrawChannel.getId(), user2.getId());
        userService.delete(user2.getId());
        System.out.println("최종 사용자 수: " + userService.findAll().size());
        System.out.println("최종 채널 수: " + channelService.findAll().size());
        System.out.println("최종 메시지 수: " + messageService.findAll().size());

        System.out.println("\n===== 테스트 종료 =====");
    }

    // 예외가 발생해야 정상인 상황을 검증하는 헬퍼
    private static void expectFail(String label, Runnable action) {
        try {
            action.run();
            System.out.println("실패: " + label + "이(가) 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println(label + " 예외 확인: " + e.getMessage());
        }
    }
}