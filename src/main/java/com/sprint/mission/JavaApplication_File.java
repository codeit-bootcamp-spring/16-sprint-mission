package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelParticipationService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelParticipationService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * File*Service를 실행해 파일 영속화를 확인하는 간단한 실행 예제입니다.
 * 채널 참여 관계만 기존 JCF 구현을 사용합니다.
 */
public class JavaApplication_File {

    public static void main(String[] args) {
        // System.getProperty("user.dir")는 JVM이 실행된 기준 디렉터리 경로를 문자열로 반환합니다.
        // Paths.get(...)은 전달한 경로 조각을 운영체제에 맞는 Path 객체로 합칩니다.
        // 결과적으로 테스트 데이터는 실행 위치 아래의 data/file-demo에 저장됩니다.
        Path dataDirectory = Paths.get(
                System.getProperty("user.dir"), "data", "file-demo"
        );

        // 기본 요구사항을 보여주기 위해 File*Service가 각각의 디렉터리를 직접 관리합니다.
        // 각 서비스 내부에서 Path, Files, ObjectInputStream/ObjectOutputStream을 사용합니다.
        UserService userService = new FileUserService(dataDirectory.resolve("users"));
        ChannelService channelService = new FileChannelService(
                dataDirectory.resolve("channels")
        );

        // 생성자는 UserService와 ChannelService를 받아 참여 시 두 대상의 존재 여부를 확인합니다.
        // 이 구현체의 참여 관계는 JCF 메모리에 보관되므로 프로그램을 종료하면 사라집니다.
        ChannelParticipationService participationService =
                new JCFChannelParticipationService(userService, channelService);

        // 메시지 서비스는 파일 경로와 작성자·채널 확인에 필요한 서비스를 받습니다.
        MessageService messageService = new FileMessageService(
                userService,
                channelService,
                dataDirectory.resolve("messages")
        );

        System.out.println("========== 기존 파일 데이터 ==========");
        // findAll()은 파일들을 읽어 객체 목록을 반환하고, List.size()는 목록의 개수를 반환합니다.
        System.out.println("저장된 사용자 수: " + userService.findAll().size());
        System.out.println("저장된 채널 수: " + channelService.findAll().size());
        System.out.println("저장된 메시지 수: " + messageService.findAll().size());

        // UUID.randomUUID()는 매번 새로운 UUID를 만듭니다.
        // toString()은 UUID를 문자열로 바꾸고, substring(0, 8)은 앞 8글자만 잘라냅니다.
        // 이 값을 이메일에 넣어 재실행 때 기존 이메일과 중복되지 않게 합니다.
        String runId = UUID.randomUUID().toString().substring(0, 8);
        User user = new User(
                "file tester " + runId,
                "file-tester-" + runId + "@example.com",
                "password123"
        );
        // create(user)는 이메일을 검증한 뒤 User 객체를 직렬화해 사용자 파일에 저장합니다.
        userService.create(user);

        System.out.println("\n========== 사용자 파일 저장 확인 ==========");
        // getId()로 객체 ID를 얻고, findById(...)로 파일에서 객체를 복원한 뒤,
        // getUsername()으로 복원된 객체의 사용자 이름을 읽습니다.
        System.out.println("생성한 사용자: " + userService.findById(user.getId()).getUsername());

        Channel channel = new Channel(
                "file channel " + runId,
                "File persistence demo channel"
        );
        // create(channel)는 Channel 객체를 UUID 기반 .ser 파일로 저장합니다.
        channelService.create(channel);

        System.out.println("\n========== 채널 파일 저장 확인 ==========");
        System.out.println("생성한 채널: " + channelService.findById(channel.getId()).getName());

        System.out.println("\n========== JCF 채널 참여 확인 ==========");
        // joinChannel(...)은 참여 관계를 등록하고, 내부에서 User와 Channel 존재 여부를 확인합니다.
        participationService.joinChannel(user.getId(), channel.getId());
        // findParticipantIds(...)는 채널의 참여자 ID 목록을 반환합니다.
        // List.contains(...)는 그 목록에 해당 사용자의 ID가 있는지 확인합니다.
        boolean joined = participationService
                .findParticipantIds(channel.getId())
                .contains(user.getId());
        System.out.println("채널 참여: " + (joined ? "성공" : "실패"));

        Message message = new Message(
                "first file message",
                user.getId(),
                channel.getId()
        );
        // create(message)는 User와 Channel 존재 여부를 확인한 뒤 메시지 파일을 저장합니다.
        messageService.create(message);

        System.out.println("\n========== 메시지 파일 저장 확인 ==========");
        System.out.println("생성한 메시지: " + messageService.findById(message.getId()).getContent());

        // update(...)는 파일에서 메시지를 읽어 내용을 바꾼 뒤 같은 ID 파일에 다시 저장합니다.
        messageService.update(message.getId(), "updated file message");
        System.out.println("수정한 메시지: " + messageService.findById(message.getId()).getContent());

        // 메시지 삭제 동작도 확인합니다. 사용자와 채널은 파일에 남겨 재실행 때 확인할 수 있습니다.
        // delete(...)는 ID에 해당하는 메시지 파일을 삭제합니다.
        messageService.delete(message.getId());
        // stream()은 목록을 순차 처리하는 스트림을 만들고,
        // noneMatch(...)는 조건에 맞는 항목이 하나도 없는지 검사합니다.
        boolean messageDeleted = messageService.findAll().stream()
                .noneMatch(savedMessage -> savedMessage.getId().equals(message.getId()));
        System.out.println("메시지 삭제: " + (messageDeleted ? "성공" : "실패"));

        // leaveChannel(...)은 참여 관계를 제거합니다. 이 JCF 구현의 변경은 파일에 저장되지 않습니다.
        participationService.leaveChannel(user.getId(), channel.getId());
        boolean left = !participationService
                .findParticipantIds(channel.getId())
                .contains(user.getId());
        System.out.println("채널 탈퇴: " + (left ? "성공" : "실패"));

        System.out.println("\n========== 재실행 확인 정보 ==========");
        System.out.println("사용자 ID: " + user.getId());
        System.out.println("채널 ID: " + channel.getId());
        System.out.println("사용자와 채널 파일은 data/file-demo 아래에 남아 있습니다.");
        System.out.println("프로그램을 다시 실행하면 시작 부분의 저장 개수에 누적 데이터가 표시됩니다.");
        System.out.println("채널 참여 관계는 JCF 구현이므로 재실행 후에는 유지되지 않습니다.");
    }
}
