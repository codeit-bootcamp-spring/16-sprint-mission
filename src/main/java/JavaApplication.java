
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

        MessageService messageService = new JCFMessageService();
        UserService userService = new JCFUserService(messageService);
        ChannelService channelService = new JCFChannelService(messageService, userService);

        System.out.println("1. 기본 데이터 (유저 2명, 채널 2개) ");
        User haha = new User("haha");
        User baba = new User("baba");
        userService.save(haha);
        userService.save(baba);

        Channel firstChannel = new Channel("1번 채널");
        Channel secondChannel = new Channel("2번 채널");
        channelService.save(firstChannel);
        channelService.save(secondChannel);

        //채널참가
        channelService.joinChannel(haha.getId(), firstChannel.getId());
        channelService.joinChannel(baba.getId(), firstChannel.getId());
        channelService.joinChannel(haha.getId(), secondChannel.getId());
        channelService.joinChannel(baba.getId(), secondChannel.getId());

        System.out.println("채널 세팅 완료" + "\n");
        System.out.println(" 2.메시지 발송 ");
        // 1번채널 메시지
        messageService.save(new Message(haha.getId(), firstChannel.getId(), "안녕하세요."));
        messageService.save(new Message(baba.getId(), firstChannel.getId(), "반갑습니다"));
        // 2번채널 메시지
        messageService.save(new Message(haha.getId(), secondChannel.getId(), "질문좀 하겠습니다."));
        messageService.save(new Message(baba.getId(), secondChannel.getId(), "결과확인"));
        //모든메시지 확인
        List<Message> messageTest = messageService.findAll();
        messageTest.forEach(msg -> {
            System.out.println(msg.getContent());
        });
        System.out.println();
        System.out.println("채널안의 메시지 확인");
        List<Message> messageList = messageService.findChannelId(firstChannel.getId());
        System.out.println("[" + firstChannel.getName() + "] 메시지 존재 여부: " + !messageList.isEmpty() + " (총 " + messageList.size() + "건)");
        messageList.forEach(msg -> {
            String sender = msg.getChannelId().equals(firstChannel.getId()) ? "haha" : "실패";
            System.out.println("   - [" + sender + "]: " + msg.getContent());
        });
        System.out.println();

        System.out.println(" 3.현재 등록된 전체 채널 목록 조회 ");
        List<Channel> allChannels = channelService.findAll();
        allChannels.forEach(ch -> System.out.println("채널명: " + ch.getName() + " (ID: " + ch.getId() + ")"));
        System.out.println();

        System.out.println("채널 나갔을때 실시간 유저 목록 변화 확인 ");
        System.out.println("haha가 1번채널에서 퇴장합니다");
        channelService.leaveChannel(haha.getId(), firstChannel.getId());

        System.out.println("채널 나간후 결과 확인");
        System.out.println(haha.getName()+"님이 참여 중인 채널 목록:");

        if (haha.getChannels().isEmpty()) {
            System.out.println("참여 채널 없음");
        } else {
            for (UUID channelId : haha.getChannels()) {
                List<Channel> channelList = channelService.findId(channelId);

                if (!channelList.isEmpty()) {
                    Channel ch = channelList.get(0);
                    System.out.println(" - " + ch.getName());
                }
            }
        }
        System.out.println();
        System.out.println("다시 모든메세지 확인");//채널이 삭제되어도 메세지는 남아있게
        //다시 메세지 확인
        List<Message> messageTest2 = messageService.findAll();
        messageTest2.forEach(msg -> {
            System.out.println(msg.getContent());
        });
        System.out.println();
        System.out.println("4.유저 탈퇴 시 메시지 삭제");
        System.out.println("유저 haha가 탈퇴합니다.");
        userService.deleteId(haha.getId()); // haha 유저 제거 및 haha가 쓴 메시지 삭제 작동
        System.out.println();
        System.out.println("1번 채널' 메시지 재조회");
        List<Message> messageDelete = messageService.findChannelId(firstChannel.getId());
        messageDelete.forEach(msg -> {
            String sender = msg.getUserId().equals(haha.getId()) ? "haha" : "haha 메시지없음";
            System.out.println("(" + sender + ") ");
            System.out.println("채널에 남은 메시지: " + msg.getContent());
        });

        boolean hasMessage = messageDelete.stream()
                .anyMatch(msg -> msg.getUserId().equals(haha.getId()));
        System.out.println("haha의 메시지가 남는지 확인 : " + hasMessage + "\n");

        System.out.println("1번 채널 삭제.");
        // deleteById 호출 ->  messageService.deleteByChannelId()실행
        channelService.deleteById(firstChannel.getId());
        System.out.println("=없어진 1번 채널의 메시지 목록 조회=");

        List<Message> channelMessageDelete = messageService.findChannelId(firstChannel.getId());
        System.out.println(channelMessageDelete);

        //다시 모든 메시지 확인
        List<Message> allMessage = messageService.findAll();
        allMessage.forEach(msg -> {
            System.out.println(msg.getContent());
        });
        //첫번째 채널 메세지 다시 확인
        if (channelMessageDelete.isEmpty()) {
            System.out.println("채널 내부의 메시지가 삭제되었습니다.");
        } else {

            System.out.println("실패: 메시지가 여전히 존재합니다.");
        }
        System.out.println();

        System.out.println("=남은 채널 목록=");
        channelService.findAll().forEach(ch -> System.out.println("- " + ch.getName()));
        System.out.println("최종 점검");
        //hasMessage haha 삭제후 haha메시지, channelMessageDelete-1번 채널 삭제,2번 채널만 남아야함
        if (!hasMessage && channelMessageDelete.isEmpty() && channelService.findAll().size() == 1) {
            System.out.println("성공: 삭제 되었습니다");
        } else {
            System.out.println("실패: 다시 검증.");
        }
    }
}

