
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;

import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;

import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;

public class JavaApplication {

    public static void main(String[] args) {

        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(userService, channelService);

        System.out.println("기존 사용자 수: " + userService.getAll().size());
        System.out.println("기존 채널 수: " + channelService.getAll().size());
        System.out.println("기존 메시지 수: " + messageService.getAll().size());

        User user = userService.add(new User("신지섭"));
        Channel channel = channelService.add(new Channel("일반"));

        Message message = messageService.add(
                new Message("안녕하세요!", user.getId(), channel.getId())
        );

        System.out.println("저장한 사용자: " + user.getUsername());
        System.out.println("저장한 채널: " + channel.getName());
        System.out.println("저장한 메시지: " + message.getContent());

        UserService newUserService = new FileUserService();
        ChannelService newChannelService = new FileChannelService();
        MessageService newMessageService = new FileMessageService(newUserService, newChannelService);

        System.out.println("불러온 사용자: " + newUserService.getById(user.getId()).getUsername());
        System.out.println("불러온 채널: " + newChannelService.getById(channel.getId()).getName());
        System.out.println("불러온 메시지: " + newMessageService.getById(message.getId()).getContent());

        System.out.println("전체 사용자 수: " + newUserService.getAll().size());
        System.out.println("전체 채널 수: " + newChannelService.getAll().size());
        System.out.println("전체 메시지 수: " + newMessageService.getAll().size());

        UserRepository jcfUserRepository = new JCFUserRepository();
        User jcfUser = new User("JCF 테스트");

        jcfUserRepository.save(jcfUser);
        System.out.println("JCF 사용자 저장: " + jcfUserRepository.getById(jcfUser.getId()).getUsername());

        jcfUserRepository.delete(jcfUser.getId());
        System.out.println("JCF 사용자 삭제 후 수: " + jcfUserRepository.getAll().size());

        UserRepository fileUserRepository = new FileUserRepository();
        User fileUser = new User("File 테스트");

        fileUserRepository.save(fileUser);

        UserRepository newFileUserRepository = new FileUserRepository();
        System.out.println("File 사용자 조회: " + newFileUserRepository.getById(fileUser.getId()).getUsername());

        newFileUserRepository.delete(fileUser.getId());

        try {
            newFileUserRepository.getById(fileUser.getId());
            System.out.println("File 사용자 삭제 확인: 실패");
        } catch (IllegalArgumentException e) {
            System.out.println("File 사용자 삭제 확인: 성공");
        }

        ChannelRepository jcfChannelRepository = new JCFChannelRepository();
        Channel jcfChannel = new Channel("JCF 채널");

        jcfChannelRepository.save(jcfChannel);
        System.out.println("JCF 채널 조회: " + jcfChannelRepository.getById(jcfChannel.getId()).orElseThrow().getName());

        jcfChannelRepository.delete(jcfChannel.getId());
        System.out.println("JCF 채널 삭제 후 수: " + jcfChannelRepository.getAll().size());

        ChannelRepository fileChannelRepository = new FileChannelRepository();
        Channel fileChannel = new Channel("File 채널");

        fileChannelRepository.save(fileChannel);

        ChannelRepository newFileChannelRepository = new FileChannelRepository();
        System.out.println("File 채널 조회: " + newFileChannelRepository.getById(fileChannel.getId()).orElseThrow().getName());

        newFileChannelRepository.delete(fileChannel.getId());

        if (newFileChannelRepository.getById(fileChannel.getId()).isEmpty()) {
            System.out.println("File 채널 삭제 확인: 성공");
        } else {
            System.out.println("File 채널 삭제 확인: 실패");
        }

        MessageRepository jcfMessageRepository = new JCFMessageRepository();
        Message jcfMessage = new Message("JCF 메시지", user.getId(), channel.getId());

        jcfMessageRepository.save(jcfMessage);
        System.out.println("JCF 메시지 조회: " + jcfMessageRepository.getById(jcfMessage.getId()).getContent());

        jcfMessageRepository.delete(jcfMessage.getId());
        System.out.println("JCF 메시지 삭제 후 수: " + jcfMessageRepository.getAll().size());

        MessageRepository fileMessageRepository = new FileMessageRepository();
        Message fileMessage = new Message("File 메시지", user.getId(), channel.getId());

        fileMessageRepository.save(fileMessage);

        MessageRepository newFileMessageRepository = new FileMessageRepository();
        System.out.println("File 메시지 조회: " + newFileMessageRepository.getById(fileMessage.getId()).getContent());

        newFileMessageRepository.delete(fileMessage.getId());

        try {
            newFileMessageRepository.getById(fileMessage.getId());
            System.out.println("File 메시지 삭제 확인: 실패");
        } catch (IllegalArgumentException e) {
            System.out.println("File 메시지 삭제 확인: 성공");
        }
    }
}
