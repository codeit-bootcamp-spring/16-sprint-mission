package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.util.UUID;

public class BasicServiceTest2 {
    public static void main(String[] args) {
        FileUserService userService = new FileUserService();
        FileChannelService channelService = new FileChannelService(userService);
        FileMessageService messageService = new FileMessageService(userService, channelService);

        FileUserRepository userRepository = new FileUserRepository();
        FileChannelRepository channelRepository = new FileChannelRepository();
        FileMessageRepository messageRepository = new FileMessageRepository();

        String testId = UUID.randomUUID().toString();

        //테스트 데이터 생성



        User user = userService.create("관계테스트","1234",testId+"@example.com");
        User user2 = userService.create("참여자2","1234",testId+"-2@example.com");

        Channel channel = channelService.create("관계채널-"+testId);
        Channel channel2 = channelService.create("관계채널2-"+testId);

        channelService.join(channel.getId(),user.getId());
        channelService.join(channel.getId(),user2.getId());
        channelService.join(channel2.getId(),user.getId());

        Message message = messageService.create("원래 내용",user.getId(),channel.getId());
        //사용자 수정 후 파일 직접 조회
        userService.update(user.getId(),"변경된 사용자","5678",user.getEmail());

        Message savedMessage = messageRepository.findById(message.getId()).orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        Channel savedChannel = channelRepository.findById(channel.getId()).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        System.out.println("메시지 작성자 변경: "+savedMessage.getUser().getName().equals("변경된 사용자"));
        System.out.println("채널 참여자 변경: "+savedChannel.getUsers().stream().anyMatch(u ->
                u.getId().equals(user.getId())&&u.getName().equals("변경된 사용자")));
        System.out.println("중첩 작성자 변경: "+savedChannel.getMessages().get(0).getUser().getName().equals("변경된 사용자"));
        //채널 수정 후 파일 직접 조회
        channelService.update(channel.getId(),"변경된 채널-"+testId);

        User savedUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        savedMessage = messageRepository.findById(message.getId()).orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        System.out.println("참여채널 변경: "+savedUser.getChannels().stream().anyMatch(c ->
                c.getId().equals(channel.getId())&&c.getName().equals("변경된 채널-"+testId)));
        System.out.println("메시지 채널 변경: "+savedMessage.getChannel().getName().equals("변경된 채널-"+testId));
        System.out.println("중첩 채널 변경: "+savedUser.getMessages().get(0).getChannel().getName().equals("변경된 채널-"+testId));
        //메시지 수정 후 양쪽 파일 확인
        messageService.update(message.getId(),"변경된 내용");

        savedUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        savedChannel = channelRepository.findById(channel.getId()).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        System.out.println("사용자 메시지 수정: "+savedUser.getMessages().get(0).getComment().equals("변경된 내용"));
        System.out.println("채널 메시지 수정: "+savedChannel.getMessages().get(0).getComment().equals("변경된 내용"));

        //메시지 삭제 후 양쪽 파일 확인

        messageService.delete(message.getId());

        savedUser = userRepository.findById(user.getId()).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        savedChannel = channelRepository.findById(channel.getId()).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        System.out.println("메시지 삭제: "+messageRepository.findById(message.getId()).isEmpty());
        System.out.println("사용자 메시지 수: "+savedUser.getMessages().size());
        System.out.println("채널 메시지 수: "+savedChannel.getMessages().size());
        //참여 중인 사용자 삭제


        userService.delete(user.getId());

        savedChannel = channelRepository.findById(channel.getId()).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        Channel savedChannel2 = channelRepository.findById(channel2.getId()).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        System.out.println("사용자 삭제: "+userRepository.findById(user.getId()).isEmpty());
        System.out.println("첫 채널 참여자 수: "+savedChannel.getUsers().size());
        System.out.println("둘째 채널 참여자 수: "+savedChannel2.getUsers().size());


        channelService.delete(channel.getId());

        User savedUser2 = userRepository.findById(user2.getId()).orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        System.out.println("채널 삭제: "+channelRepository.findById(channel.getId()).isEmpty());
        System.out.println("남은 사용자의 참여채널 수: "+savedUser2.getChannels().size());


        userService.delete(user2.getId()); //이번 테스트에서 만든 나머지 데이터 정리
        channelService.delete(channel2.getId());
    }
}