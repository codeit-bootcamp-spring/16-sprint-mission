
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JavaApplication {
    public static void main(String[] args) {

/*      UserService userRepository= new JCFUserService();
        User user1 = new User("haha");
        User user2 = new User("baba");

        UUID hahaId = user1.getId();
        UUID babaId = user2.getId();

        userService.save(user1);
        userService.save(user2);
        System.out.println("테스트  ID: " + hahaId + ", baba ID: " + babaId);
        System.out.println();*/

        MessageService messageService = new JCFMessageService();

        // UserService와 ChannelService 모두 삭제를 위해 messageService를 파라미터로 사용.

        UserService userService = new JCFUserService(messageService);
        ChannelService channelService = new JCFChannelService(messageService);

        System.out.println("1. 기본 데이터 (유저 2명, 채널 2개) ");
        User haha = new User("haha");
        User baba = new User("baba");
        userService.save(haha);
        userService.save(baba);

        Channel firstChannel = new Channel("1번 채널");
        Channel secondChannel = new Channel("2번 채널");
        channelService.save(firstChannel);
        channelService.save(secondChannel);
        System.out.println("채널 세팅 완료"+"\n");


        System.out.println(" 2.메시지 발송 ");
        // 1번채널 메시지
        messageService.save(new Message(haha.getId(), firstChannel.getId(), "안녕하세요."));
        messageService.save(new Message(baba.getId(), firstChannel.getId(), "반갑습니다"));
        // 2번채널 메시지
        messageService.save(new Message(haha.getId(), secondChannel.getId(), "질문좀 하겠습니다."));
        messageService.save(new Message(baba.getId(),secondChannel.getId(),"결과확인"));
        //모든메시지 확인
        List<Message> messageTest=messageService.findAll();
        messageTest.forEach(msg->{
            System.out.println(msg.getContent());
        });


        System.out.println("채널안의 메시지 확인");
        List<Message> messageList = messageService.findChannelId(firstChannel.getId());
        System.out.println("[" + firstChannel.getName() + "] 메시지 존재 여부: " + !messageList.isEmpty() + " 총 " + messageList.size() + "건)");
        messageList.forEach(msg -> {
            String sender = msg.getChannelId().equals(firstChannel.getId()) ? "haha" : "실패";
            System.out.println("   - [" + sender + "]: " + msg.getContent());
        });
        System.out.println();



        System.out.println(" 3.현재 등록된 전체 채널 목록 조회 ");
        List<Channel> allChannels = channelService.findAll();
        allChannels.forEach(ch -> System.out.println("채널명: " + ch.getName() + " (ID: " + ch.getId() + ")"));
        System.out.println();


        System.out.println("4.유저 탈퇴 시 메시지 삭제");
        System.out.println("유저 haha가 탈퇴합니다.");
        userService.deleteId(haha.getId()); // haha 유저 제거 및 haha가 쓴 메시지 삭제 작동

        System.out.println("'1번 채널' 메시지 재조회");
        List<Message> messageDelete = messageService.findChannelId(firstChannel.getId());
        messageDelete.forEach(msg -> {
            String sender = msg.getUserId().equals(haha.getId()) ? "haha" : "haha 메시지없음";
            System.out.println("(" + sender + ") ");
            System.out.println("채널에 남은 메시지: " + msg.getContent()+"\n");
        });


        boolean hasMessage = messageDelete.stream()
                .anyMatch(msg -> msg.getUserId().equals(haha.getId()));
        System.out.println(" haha의 메시지가 남는지 확인 : " + hasMessage+"\n" );

        System.out.println("1번 채널 삭제.");
        // deleteById 호출 ->  messageService.deleteByChannelId()실행
        channelService.deleteById(firstChannel.getId());
        System.out.println("없어진 1번 채널의 메시지 목록 조회");

        List<Message> channelMessageDelete = messageService.findChannelId(firstChannel.getId());
        System.out.println(channelMessageDelete);

        //다시 모든 메시지 확인 2번채널 메시지까지 안보임

        List<Message> allMessage= messageService.findAll();
        allMessage.forEach(msg->{
            System.out.println(msg.getContent());
        });



        if (channelMessageDelete.isEmpty()) {
            System.out.println("채널 내부의 메시지가 삭제되었습니다.");
        } else {

            System.out.println("실패: 메시지가 여전히 존재합니다.");
        }
        System.out.println();
        System.out.println();


        System.out.println("남은 채널 목록");
        channelService.findAll().forEach(ch -> System.out.println("- " + ch.getName()));

        System.out.println("최종 점검");
        if (!hasMessage && channelMessageDelete.isEmpty() && channelService.findAll().size() == 1) {
            System.out.println("성공: 삭제 되었습니다");
        } else {
            System.out.println("실패: 다시 검증.");
        }
    }




    }

