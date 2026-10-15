package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.sql.SQLOutput;
import java.util.List;
import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        userService.createUser("Patrick1224"); // 새 사용자 생성 후 내부 컬렉션에 추가
        System.out.println("User List: " + userService.findAll()); // 모든(한 명) 사용자 출력
        userService.createUser("Young0927"); // 새 사용자 생성 후 내부 컬레션에 추가
        System.out.println("User List: " + userService.findAll()); // 모든(두 명) 사용자 출력

        // UUID user1_uuid = ((JCFUserService) userService).getUserUUID(0); // 첫 번째 사용자의 UUID 저장
        // 위 코드는 서비스의 구현체가 바뀌면 사용할 수 없습니다.
        // 객체 지향 설계의 핵심 원칙 중 하나는 인터페이스에 의존하는 프로그래밍입니다.
        User user = userService.createUser("Patrick1224");
        UUID user1_uuid = user.getId();

        System.out.println("첫 번째 User의 UUID: " + user1_uuid); // 첫 번째 사용자의 UUID 출력

        User user1 = userService.findById(user1_uuid); // 첫 번째 사용자 저장
//        User user1_updated = userService.updateUsername(user1_uuid, "Lee1224"); // 첫 번째 사용자의 username 변경
//        System.out.println("첫 번째 사용자의 변경된 username:"  + user1_updated.getUsername()); // 첫 번째 사용자의 username 출력
//        System.out.println("user1과 user1_updated는 같은 객체인가요? " + (user1 == user1_updated)); // true
//
//        userService.deleteUser(user1_uuid); // 첫 번째 사용자 삭제
//        System.out.println("User List: " + userService.findAll()); // 모든(한 명) 사용자 출력

        ChannelService channelService = new JCFChannelService(userService); // ChannelService 객체 생성
        Channel sp16Channel = channelService.createChannel("SP16"); // 채널 생성
        MessageService messageService = new JCFMessageService(userService, channelService); // MessageService 객체 생성
        messageService.createMessage("Hello, JungMin!", user1, sp16Channel); // 첫 번째 사용자가 첫 번째 메세지(최초) 작성
        messageService.createMessage("How are you doing?", user1, sp16Channel); // 두 번째 사용자가 두 번째 메세지 작성
        List<Message> messageList = messageService.findAll(); // 모든 메세지 객체가 담겨있는 리스트 반환
        System.out.println("메세지 객체들: " + messageList); // 모든 메세지 객체들 출략
        System.out.println("메시지 콘텐츠들: " + messageList.stream()
                                                    .map(Message::getContent)
                                                    .toList()); // 모든 메세지 콘텐츠 출력

        Message firstMessage = messageService.findById(messageList.get(0).getId()); // 첫 번째 메세지 객체를 반환합니다.
        System.out.println("첫 번째 메세지 객체: " + firstMessage); // 첫 번째 메세지 객체 출력
        System.out.println("첫 번째 메세지 콘텐츠: " + firstMessage.getContent()); // 첫 번째 메세지 콘텐츠 출력
        System.out.println("첫 번째 메세지의 콘텐츠를 수정합니다.");
        Message editedFirstMessage = messageService.updateContent(firstMessage.getId(), "Hello, Lucky!"); // 수정된 첫 번째 메세지 반환
        System.out.println("첫 번째 메세지와 수정된 첫 번째 메세지는 같은 객체인가요?: " + (firstMessage == editedFirstMessage));
        System.out.println("수정된 첫 번째 메세지 객체: " + editedFirstMessage); // 수정된 첫 번째 메세지 객체 출력
        System.out.println("수정된 첫 번째 메세지 콘텐츠: " + editedFirstMessage.getContent()); // 수정된 첫 번째 메세지 출력
        messageService.deleteMessage(messageList.get(0).getId()); // 첫 번째 메세지 객체를 삭제
        System.out.println("메세지 객체들: " + messageService.findAll()); // 하나의 메세지 객체만 남아있어야합니다

        // deleteMessageWithoutChannel() 테스트, 자신의 필드를 통해 채널 객체를 참조하고 있지 않은 메세지 객체를 삭제하는 메서드입니다.
        System.out.println("현재 모든 채널들 출력: " + channelService.findAll()); // 하나의 채널이 출력됩니다.
        Channel channel1 = channelService.findById(sp16Channel.getId());
        System.out.println("현재 채널의 ID: " + channel1.getId());
        channelService.deleteChannel(channel1.getId()); // 현재 채널 삭제
        System.out.println("현재 채널을 삭제합니다.");
        System.out.println("현재 모든 채널들 출력: " + channelService.findAll()); // 존재하는 채널이 없어야 합니다.
        System.out.println("현재 메시지들 출력: " + messageService.findAll()); // 하나의 메세지가 존재합니다.
        System.out.println("채널을 가지고있지않은 모든 메세지를 삭제합니다");// 채널을 가지고있지않은 모든 메세지를 삭제합니다.
        messageService.deleteMessageWithoutChannel();
        System.out.println("현재 메세지들 출력: " + messageService.findAll()); // 메세지가 없습니다.
    }
}
