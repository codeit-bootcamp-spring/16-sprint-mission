package com.sprint.mission.discodeit.service.basic.v1;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BasicMessageServiceV1 implements BasicMessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final BasicChannelService channelService;
    private final BasicUserService userService;

    public BasicMessageServiceV1(MessageRepository messageRepository, ChannelRepository channelRepository,
                                 BasicChannelService channelService, BasicUserService userService) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.channelService = channelService;
        this.userService = userService;
    }

    @Override
    public void sendMessage(User user, Channel channel, String message) {
        // 메시지가 비어있는지 확인
        if (message == null || message.isBlank()) {
            System.out.println("메시지 내용을 입력해주세요.");
            return;
        }

        // 전달받은 Channel 객체가 아니라 저장소에 있는 Channel로 존재 여부와 참여 여부를 확인한다.
        Channel storedChannel = channelRepository.findById(channel.getId()).orElse(null);
        boolean isVerified = verify(user, storedChannel, "전송");

        if (isVerified) {
            // Message를 저장하고, 작성한 User와 Channel이 보관하는 메시지 목록에도 반영한다.
            Message newMessage = new Message(message, storedChannel, user);
            messageRepository.create(newMessage);
            userService.addMessage(newMessage);
            channelService.addMessage(newMessage);
        }
    }

    @Override
    public void deleteMessage(User user, Channel channel, Message message) {
        Message storedMessage = findOwnMessage(user, channel, message, "삭제");

        if (storedMessage != null) {
            messageRepository.delete(storedMessage.getId());
            userService.removeMessage(storedMessage.getId());
            channelService.removeMessage(storedMessage.getId());
        }
    }

    @Override
    public void modifyMessage(User user, Channel channel, Message message, Scanner scanner) {
        Message storedMessage = findOwnMessage(user, channel, message, "수정");
        if (storedMessage == null) {
            return;
        }

        System.out.print("수정할 내용을 작성해주세요: ");
        String modifiedMessage = scanner.nextLine();
        if (modifiedMessage.isBlank()) {
            System.out.println("메시지 내용을 입력해주세요.");
            return;
        }

        storedMessage.update(modifiedMessage);
        messageRepository.update(storedMessage);
        userService.refreshMessage(storedMessage);
        channelService.refreshMessage(storedMessage);
    }

    @Override
    public void listMessages(User user, Channel channel) {
        Channel storedChannel = channelRepository.findById(channel.getId()).orElse(null);
        boolean isVerified = verify(user, storedChannel, "조회");
        if (!isVerified) return;

        if (storedChannel.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            System.out.println("본인이 속한 채널의 메시지만 조회 할 수 있습니다.");
            return;
        }

        List<Message> list = messageRepository.findAll()
                .stream()
                .filter(message -> message.getChannel().getId().equals(channel.getId()))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .toList();

        for (Message message : list) {
            String username = message.getUser().getUserName();
            String text = message.getMessage();
            String channelTitle = message.getChannel().getTitle();
            System.out.println("[" + channelTitle + "]" + username + ": " + text);
        }
    }

    @Override
    public void refreshUser(User user) {
        messageRepository.findAll().stream()
                .filter(message -> message.getUser().getId().equals(user.getId()))
                .forEach(message -> {
                    message.getUser().sync(user);
                    messageRepository.update(message);
                });
    }

    @Override
    public void refreshChannel(Channel channel) {
        messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channel.getId()))
                .forEach(message -> {
                    message.getChannel().sync(channel);
                    messageRepository.update(message);
                });
    }

    // 채널 참여자인지 확인, action은 안내 문구에 쓰인다. (전송/수정/삭제)
    private boolean verify(User user, Channel storedChannel, String action) {
        if (storedChannel == null) {
            System.out.println("존재하지 않는 채널입니다.");
            return false;
        } else if (storedChannel.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            System.out.println("참여 중인 채널에서만 메시지를 " + action + "할 수 있습니다.");
            return false;
        } else {
            return true;
        }
    }

    // 채널 참여자이고 해당 채널의 Message이면서 요청한 User가 작성자일 때만 반환하고 아니면 안내 후 null을 반환
    private Message findOwnMessage(User user, Channel channel, Message message, String action) {
        Channel storedChannel = channelRepository.findById(channel.getId()).orElse(null);
        if (!verify(user, storedChannel, action)) {
            return null;
        }

        Message storedMessage = messageRepository.findById(message.getId()).orElse(null);
        if (storedMessage == null) {
            System.out.println("존재하지 않는 메시지입니다.");
            return null;
        }
        if (!storedMessage.getChannel().getId().equals(storedChannel.getId())) {
            System.out.println("해당 채널의 메시지가 아닙니다.");
            return null;
        }
        if (!storedMessage.getUser().getId().equals(user.getId())) {
            System.out.println("본인이 작성한 메시지만 " + action + "할 수 있습니다.");
            return null;
        }

        return storedMessage;
    }
}
