package com.sprint.mission.discodeit.service.basic.v1;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;

import java.util.List;
import java.util.Scanner;

public class BasicChannelServiceV1 implements BasicChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BasicChannelServiceV1(ChannelRepository channelRepository, UserRepository userRepository,
                                 MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
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
                userRepository.addChannel(user.getId(), newChannel);
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

    // null인 값은 변경하지 않는다. Channel 저장 후, User가 보관 중인 Channel 복사본도 최신 정보로 맞춘다.
    private void saveChannelUpdate(Channel channel, ChannelType channelType, String title, String description) {
        Channel updated = channelRepository.update(channel.getId(), channelType, title, description);
        userRepository.refreshChannel(updated);
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
        if (!channelRepository.addUser(channel.getId(), user)) {
            System.out.println("이미 참여 중인 채널입니다.");
            return;
        }
        userRepository.addChannel(user.getId(), channel);
        System.out.println("채널에 참여했습니다.");
    }

    @Override
    public void leaveChannel(User user, Channel channel) {
        if (channelRepository.findById(channel.getId()).isEmpty()) {
            System.out.println("존재하지 않는 채널입니다.");
            return;
        }

        // Channel의 참여자 목록과 User의 채널 목록에서 서로를 제거하고 저장
        if (!channelRepository.removeUser(channel.getId(), user.getId())) {
            System.out.println("참여 중인 채널이 아닙니다.");
            return;
        }
        userRepository.removeChannel(user.getId(), channel.getId());
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


    private void removeChannelData(Channel channel) {
        messageRepository.findAll()
                .stream()
                .filter(message -> message.getChannel().getId().equals(channel.getId()))
                .forEach(message -> messageRepository.delete(message.getId()));
        userRepository.removeChannel(channel.getId());
        channelRepository.delete(channel.getId());
    }
}
