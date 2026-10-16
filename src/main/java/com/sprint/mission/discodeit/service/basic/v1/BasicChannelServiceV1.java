package com.sprint.mission.discodeit.service.basic.v1;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

public class BasicChannelServiceV1 implements BasicChannelService {

    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BasicUserService userService;
    private BasicMessageService messageService;

    public BasicChannelServiceV1(ChannelRepository channelRepository, MessageRepository messageRepository,
                                 BasicUserService userService) {
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    // BasicMessageService도 이 서비스를 사용하므로(순환) 생성자 대신 setter로 연결한다.
    public void setMessageService(BasicMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(User user, Scanner scanner) {
        System.out.println("========== 채널 생성 창 ========== ");
        System.out.print("채널명을 입력해주세요: ");
        String title = scanner.nextLine();
        System.out.print("채널에 대한 설명을 입력해주세요: ");
        String description = scanner.nextLine();
        Channel newChannel = null;
        while (true) {
            System.out.print("채널타입을 선택해주세요(1은 Public, 2는 Private): ");
            int channelType = scanner.nextInt();
            scanner.nextLine();
            if (channelType == 1 || channelType == 2) {
                ChannelType type = channelType == 1 ? ChannelType.PUBLIC : ChannelType.PRIVATE;
                newChannel = new Channel(type, title, description);

                // Channel을 저장하고, 이미 저장된 User의 채널 목록에도 반영한다.
                newChannel.addUser(user);
                channelRepository.create(newChannel);
                userService.addChannel(user.getId(), newChannel);
                break;
            }
            System.out.println("1 또는 2만 입력해주세요.");
        }
        return newChannel;
    }

    @Override
    public void listChannel(User user) {
        List<Channel> channels = user.getChannels();
        for (Channel channel : channels) {
            System.out.println("채널명: " + channel.getTitle());
            System.out.println("채널 설명: " + channel.getDescription());
            System.out.println("채널 유형: " + channel.getChannelType());
            System.out.println();
        }
    }

    @Override
    public void modifyChannelInfo(User user, Channel channel, Scanner scanner) {
        System.out.println("========== 채널정보 변경 창 ========== ");
        if (channel.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            System.out.println("참여 중인 채널만 수정할 수 있습니다.");
            return;
        }

        while (true) {
            System.out.print("변경할 정보를 선택하세요.\n" +
                    "1. 채널타입\n" +
                    "2. 채널명\n" +
                    "3. 채널 설명\n" +
                    "4. 종료\n" +
                    ": "
            );
            int menu = scanner.nextInt();
            scanner.nextLine();

            switch (menu) {
                case 1 -> modifyChannelType(channel, scanner);
                case 2 -> modifyTitle(channel, scanner);
                case 3 -> modifyDescription(channel, scanner);
                case 4 -> {
                    System.out.println("채널정보 변경을 종료합니다.");
                }
                default -> {
                    System.out.println("1~4 중에서 선택해주세요.");
                    continue;
                }
            }
            return;
        }
    }

    private void modifyChannelType(Channel channel, Scanner scanner) {
        while (true) {
            System.out.print("변경할 채널타입을 선택해주세요(1은 Public, 2는 Private): ");
            int channelType = scanner.nextInt();
            scanner.nextLine();
            if (channelType == 1 || channelType == 2) {
                ChannelType type = channelType == 1 ? ChannelType.PUBLIC : ChannelType.PRIVATE;
                saveChannelUpdate(channel, type, null, null);
                return;
            }
            System.out.println("1 또는 2만 입력해주세요.");
        }
    }

    private void modifyTitle(Channel channel, Scanner scanner) {
        System.out.print("변경할 채널명을 입력하세요: ");
        String newTitle = scanner.nextLine();
        saveChannelUpdate(channel, null, newTitle, null);
    }

    private void modifyDescription(Channel channel, Scanner scanner) {
        System.out.print("변경할 채널 설명을 입력하세요: ");
        String newDescription = scanner.nextLine();
        saveChannelUpdate(channel, null, null, newDescription);
    }

    // null인 값은 변경하지 않는다. Channel 저장 후, User/Message가 보관 중인 Channel 복사본도 최신 정보로 맞춘다.
    private void saveChannelUpdate(Channel channel, ChannelType channelType, String title, String description) {
        Channel stored = channelRepository.findById(channel.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 Channel입니다."));
        stored.update(channelType, title, description);
        channelRepository.update(stored);

        userService.refreshChannel(stored);
        if (messageService != null) {
            messageService.refreshChannel(stored);
        }
    }

    @Override
    public void deleteChannel(User user, Channel channel) {
        System.out.println("========== 채널 삭제 ========== ");
        if (channel.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId()))) {
            System.out.println("참여 중인 채널만 삭제할 수 있습니다.");
            return;
        }

        removeChannelData(channel);
        System.out.println("채널이 삭제되었습니다.");
    }

    // 채널의 Message 삭제 -> 참여 중인 User들의 채널 목록에서 제거 -> Channel 삭제
    @Override
    public void joinChannel(User user, Channel channel) {
        if (channelRepository.findById(channel.getId()).isEmpty()) {
            System.out.println("존재하지 않는 채널입니다.");
            return;
        }
        if (channel.getChannelType() == ChannelType.PRIVATE) {
            System.out.println("PRIVATE 채널에는 참여할 수 없습니다.");
            return;
        }

        // Channel의 참여자 목록과 User의 채널 목록을 양쪽 모두 갱신하고 저장한다.
        if (!addUser(channel.getId(), user)) {
            System.out.println("이미 참여 중인 채널입니다.");
            return;
        }
        userService.addChannel(user.getId(), channel);
        System.out.println("채널에 참여했습니다.");
    }

    @Override
    public void leaveChannel(User user, Channel channel) {
        if (channelRepository.findById(channel.getId()).isEmpty()) {
            System.out.println("존재하지 않는 채널입니다.");
            return;
        }

        // Channel의 참여자 목록과 User의 채널 목록에서 서로를 제거하고 저장
        if (!removeUser(channel.getId(), user.getId())) {
            System.out.println("참여 중인 채널이 아닙니다.");
            return;
        }
        userService.removeChannel(user.getId(), channel.getId());
        System.out.println("채널에서 나갔습니다.");

        // 참여자가 아무도 남지 않으면 채널을 삭제ㅈ
        boolean empty = channelRepository.findById(channel.getId())
                .map(c -> c.getUsers().isEmpty())
                .orElse(false);
        if (empty) {
            removeChannelData(channel);
            System.out.println("참여자가 없어 채널이 삭제되었습니다.");
        }
    }


    // 채널의 Message 삭제 -> 참여 중인 User들의 채널 목록에서 제거 -> Channel 삭제
    private void removeChannelData(Channel channel) {
        messageRepository.findAll().stream()
                .filter(message -> message.getChannel().getId().equals(channel.getId()))
                .forEach(message -> {
                    messageRepository.delete(message.getId());
                    userService.removeMessage(message.getId());
                });
        userService.removeChannel(channel.getId());
        channelRepository.delete(channel.getId());
    }

    @Override
    public boolean addUser(UUID channelId, User user) {
        return channelRepository.findById(channelId)
                .filter(channel -> channel.getUsers().stream().noneMatch(u -> u.getId().equals(user.getId())))
                .map(channel -> {
                    channel.addUser(user);
                    channelRepository.update(channel);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean removeUser(UUID channelId, UUID userId) {
        return channelRepository.findById(channelId)
                .filter(channel -> channel.getUsers().stream().anyMatch(u -> u.getId().equals(userId)))
                .map(channel -> {
                    channel.getUsers().removeIf(u -> u.getId().equals(userId));
                    channelRepository.update(channel);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public void removeUser(UUID userId) {
        channelRepository.findAll().stream()
                .filter(channel -> channel.getUsers().stream().anyMatch(u -> u.getId().equals(userId)))
                .forEach(channel -> {
                    channel.getUsers().removeIf(u -> u.getId().equals(userId));
                    channelRepository.update(channel);
                });
    }

    @Override
    public void refreshUser(User user) {
        channelRepository.findAll().stream()
                .filter(channel -> channel.getUsers().stream().anyMatch(u -> u.getId().equals(user.getId())))
                .forEach(channel -> {
                    channel.getUsers().stream()
                            .filter(u -> u.getId().equals(user.getId()))
                            .forEach(u -> u.sync(user));
                    channelRepository.update(channel);
                });
    }

    @Override
    public void addMessage(Message message) {
        channelRepository.findById(message.getChannel().getId())
                .filter(channel -> channel.getChannelMessages().stream().noneMatch(m -> m.getId().equals(message.getId())))
                .ifPresent(channel -> {
                    channel.addMessage(message);
                    channelRepository.update(channel);
                });
    }

    @Override
    public void removeMessage(UUID messageId) {
        channelRepository.findAll().stream()
                .filter(channel -> channel.getChannelMessages().stream().anyMatch(m -> m.getId().equals(messageId)))
                .forEach(channel -> {
                    channel.getChannelMessages().removeIf(m -> m.getId().equals(messageId));
                    channelRepository.update(channel);
                });
    }

    @Override
    public void refreshMessage(Message message) {
        channelRepository.findAll().stream()
                .filter(channel -> channel.getChannelMessages().stream().anyMatch(m -> m.getId().equals(message.getId())))
                .forEach(channel -> {
                    channel.getChannelMessages().stream()
                            .filter(m -> m.getId().equals(message.getId()))
                            .forEach(m -> m.sync(message));
                    channelRepository.update(channel);
                });
    }


}
