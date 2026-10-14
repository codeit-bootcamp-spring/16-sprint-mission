package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService(userService);
        JCFMessageService messageService = new JCFMessageService(userService, channelService);

        // =========================
        // 1. User 테스트
        // =========================
        System.out.println("===== User 테스트 =====");

        User user1 = userService.create(new User("user1", "user1@test.com", "password123")
        );
        User user2 = userService.create(new User("user2", "user2@test.com", "password456")
        );

        System.out.println("생성된 사용자: " + user1.getUsername());
        System.out.println("ID 조회: "
                + userService.findById(user1.getId()).getEmail());
        System.out.println("전체 사용자 수: " + userService.findAll().size());

        // 본인의 이메일을 유지하면서 사용자 정보 수정
        user1.updateUsername("updatedUser1");
        User updatedUser = userService.update(user1.getId(), user1);
        System.out.println("사용자 수정: " + updatedUser.getUsername());

        // 다른 사용자가 사용 중인 이메일로 생성 시도
        try {
            userService.create(
                    new User("user3", "user1@test.com", "password789")
            );
            System.out.println("실패: 중복 이메일이 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 이메일 예외 확인: " + e.getMessage());
        }

        // 짧은 비밀번호로 생성 시도
        try {
            userService.create(
                    new User("user3", "user3@test.com", "123")
            );
            System.out.println("실패: 짧은 비밀번호가 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("비밀번호 예외 확인: " + e.getMessage());
        }

        // 존재하지 않는 사용자 조회
        try {
            userService.findById(UUID.randomUUID());
            System.out.println("실패: 존재하지 않는 사용자가 조회됨");
        } catch (IllegalArgumentException e) {
            System.out.println("사용자 조회 예외 확인: " + e.getMessage());
        }

        // =========================
        // 2. Channel 테스트
        // =========================
        System.out.println("\n===== Channel 테스트 =====");

        Channel channel = channelService.create(
                new Channel(user1.getId(), "general", "일반 채널")
        );

        System.out.println("생성된 채널: " + channel.getChannelName());
        System.out.println("채널 ID 조회: "
                + channelService.findById(channel.getId()).getId());
        System.out.println("전체 채널 수: " + channelService.findAll().size());

        System.out.println("소유자 자동 가입: "
                + channel.getMembers().contains(user1.getId()));

        // 멤버 입장
        channelService.joinChannel(channel.getId(), user2.getId());
        System.out.println("멤버 수: "
                + channelService.findMember(channel.getId()).size());

        // 존재하지 않는 사용자의 채널 입장
        try {
            channelService.joinChannel(
                    channel.getId(), UUID.randomUUID()
            );
            System.out.println("실패: 존재하지 않는 사용자의 입장이 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 입장 예외 확인: " + e.getMessage());
        }

        // 이미 존재하는 사용자의 채널 입장
        try {
            channelService.joinChannel(
                    channel.getId(), user2.getId()
            );
            System.out.println("실패: 이미 존재하는 사용자의 입장이 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 입장 예외 확인: " + e.getMessage());
        }

        // 일반 사용자의 채널 수정 시도
        try {
            channelService.update(channel.getId(), user2.getId());
            System.out.println("실패: 일반 사용자에게 수정 권한이 있음");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 수정 권한 예외 확인: " + e.getMessage());
        }

        // 소유권 양도
        channelService.transformOwnership(
                channel.getId(), user1.getId(), user2.getId()
        );
        System.out.println("소유권 양도 결과: "
                + channel.getOwnerId().equals(user2.getId()));

        // 새 소유자의 퇴장 시도
        try {
            channelService.leaveChannel(channel.getId(), user2.getId());
            System.out.println("실패: 소유자 퇴장이 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("소유자 퇴장 예외 확인: " + e.getMessage());
        }

        // 소유자가 퇴장 시도 후에도 멤버로 남아 있는지 확인
        System.out.println("소유자 퇴장 거부 후에도 멤버인가: "
                + channel.getMembers().contains(user2.getId()));

        // 기존 소유자 퇴장
        channelService.leaveChannel(channel.getId(), user1.getId());
        System.out.println("기존 소유자 퇴장 후 멤버 수: "
                + channelService.findMember(channel.getId()).size());

        // 이미 퇴장한 사용자의 퇴장 시도
        try {
            channelService.leaveChannel(channel.getId(), user1.getId());
            System.out.println("실패: 존재하지 않는 사용자의 퇴장이 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("채널 퇴장 예외 확인: " + e.getMessage());
        }
        // =========================
        // 3. Message 테스트
        // =========================
        System.out.println("\n===== Message 테스트 =====");

        Message message = messageService.create(
                new Message(
                        user1.getId(),
                        user2.getId(),
                        channel.getId(),
                        "안녕하세요!"
                )
        );

        System.out.println("메시지 생성: " + message.getContent());
        System.out.println("메시지 ID 조회: "
                + messageService.findById(message.getId()).getId());
        System.out.println("전체 메시지 수: " + messageService.findAll().size());

        // 작성자의 메시지 수정
        Message updatedMessage = messageService.update(
                message.getId(), user1.getId(), "수정된 메시지입니다."
        );
        System.out.println("수정된 메시지 내용: " + updatedMessage.getContent());

        // 다른 사용자의 메시지 수정 시도
        try {
            messageService.update(
                    message.getId(), user2.getId(), "권한 없는 수정"
            );
            System.out.println("실패: 다른 사용자의 메시지 수정이 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("메시지 수정 권한 예외 확인: " + e.getMessage());
        }

        // 권한 없는 수정 후 내용이 유지되는지 확인
        System.out.println("권한 없는 수정 후 메시지 내용: "
                + messageService.findById(message.getId()).getContent());

        // 다른 사용자의 메시지 삭제 시도
        try {
            messageService.delete(message.getId(), user2.getId());
            System.out.println("실패: 다른 사용자의 메시지 삭제가 허용됨");
        } catch (IllegalArgumentException e) {
            System.out.println("메시지 삭제 권한 예외 확인: " + e.getMessage());
        }

        // 작성자의 메시지 삭제
        messageService.delete(message.getId(), user1.getId());
        System.out.println("삭제 후 메시지 수: " + messageService.findAll().size());

        // =========================
        // 4. 삭제 테스트
        // =========================
        System.out.println("\n===== 삭제 테스트 =====");

        channelService.delete(channel.getId(), user2.getId());
        System.out.println("삭제 후 채널 수: " + channelService.findAll().size());

        userService.delete(user1.getId());
        System.out.println("삭제 후 사용자 수: " + userService.findAll().size());

        System.out.println("\n===== 테스트 종료 =====");
    }
}