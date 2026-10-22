package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.UUID;

public class BasicServiceTest {
    public static void main(String[] args) {
        System.out.println("===== JCF 테스트 =====");
        test(
                new JCFUserRepository(),
                new JCFChannelRepository(),
                new JCFMessageRepository()
        );

        System.out.println("===== File 테스트 =====");
        test(
                new FileUserRepository(),
                new FileChannelRepository(),
                new FileMessageRepository()
        );
    }

    static void test(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        UserService userService = new BasicUserService(userRepository, channelRepository);
        ChannelService channelService = new BasicChannelService(channelRepository, userRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);

        //기존 파일 데이터와 중복되지 않는 테스트 값
        String testId = UUID.randomUUID().toString();

        //User 생성 조회 수정
        User user = userService.create("테스트유저","1234",testId+"@example.com");
        System.out.println(userService.findById(user.getId()).getName());

        userService.update(user.getId(),"수정유저","5678",user.getEmail());
        System.out.println(userService.findById(user.getId()).getName());

        //Channel 생성 조회 수정
        Channel channel = channelService.create("테스트채널-"+testId);
        System.out.println("채널 생성 조회: "+channelService.findById(channel.getId()).getName().equals("테스트채널-"+testId));

        channelService.update(channel.getId(),"수정채널-"+testId);
        System.out.println("채널 수정 조회: "+channelService.findById(channel.getId()).getName().equals("수정채널-"+testId));

        //참여 관계 확인
        channelService.join(channel.getId(),user.getId());
        System.out.println("참여자 수: "+channelService.findById(channel.getId()).getUsers().size());
        System.out.println("참여채널 수: "+userService.findById(user.getId()).getChannels().size());

        //Message 생성 조회 수정
        Message message = messageService.create("안녕하세요",user.getId(),channel.getId());
        System.out.println(messageService.findById(message.getId()).getComment());

        messageService.update(message.getId(),"수정된 메시지");
        System.out.println(messageService.findById(message.getId()).getComment());

        //관계 목록에서도 수정 내용 확인
        User savedUser = userService.findById(user.getId());
        Channel savedChannel = channelService.findById(channel.getId());

        for(Message userMessage : savedUser.getMessages()){
            System.out.println("사용자 메시지: "+userMessage.getComment());
        }
        for(Message channelMessage : savedChannel.getMessages()){
            System.out.println("채널 메시지: "+channelMessage.getComment());
        }

        //Message 삭제 확인
        messageService.delete(message.getId());
        try{
            messageService.findById(message.getId());
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

        System.out.println("사용자 메시지 수: "+userService.findById(user.getId()).getMessages().size());
        System.out.println("채널 메시지 수: "+channelService.findById(channel.getId()).getMessages().size());

        //Channel 삭제 후 사용자 참여 관계 확인
        channelService.delete(channel.getId());
        System.out.println("삭제 후 참여채널 수: "+userService.findById(user.getId()).getChannels().size());

        try{
            channelService.findById(channel.getId());
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }

        //User 삭제 확인
        userService.delete(user.getId());
        try{
            userService.findById(user.getId());
        }catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
    }
}