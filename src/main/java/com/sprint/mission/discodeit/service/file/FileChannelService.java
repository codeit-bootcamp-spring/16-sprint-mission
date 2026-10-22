package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelService implements ChannelService {

    private final Path channelDirectory;

    private FileUserService userService;
    private FileMessageService messageService;

    public FileChannelService() {
        this(Path.of("data", "channels"));
    }

    public FileChannelService(Path channelDirectory) {
        this.channelDirectory = channelDirectory;

        try {
            Files.createDirectories(channelDirectory);
        } catch (IOException e) {
            throw new RuntimeException("채널 디렉터리 생성에 실패했습니다.", e);
        }
    }

    public void setUserService(FileUserService userService) {
        this.userService = userService;
    }

    public void setMessageService(FileMessageService messageService) {
        this.messageService = messageService;
    }

    private Path fileFor(UUID channelId) {
        return channelDirectory.resolve(channelId + ".ser");
    }

    private void writeChannel(Channel channel) {
        Path file = fileFor(channel.getId());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장에 실패했습니다.", e);
        }
    }

    // 같은 패키지의 File 서비스가 연관 객체 변경 후 채널 파일을 저장할 때 사용
    void saveChannel(Channel channel) {
        writeChannel(channel);
    }

    private Channel readChannel(Path file) {
        try (ObjectInputStream in =
                     new ObjectInputStream(Files.newInputStream(file))) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 파일을 읽는 데 실패했습니다.", e);
        }
    }

    private List<Channel> readAllChannels() {
        List<Channel> channels = new ArrayList<>();

        try (Stream<Path> files = Files.list(channelDirectory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> channels.add(readChannel(path)));
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 목록을 읽는 데 실패했습니다.", e);
        }

        return channels;
    }

    private void validateDuplicateChannelName(String channelName) {
        boolean exists = readAllChannels().stream()
                .anyMatch(channel -> channel.getChannelName().equals(channelName));

        if (exists) {
            throw new IllegalArgumentException("\"" + channelName + "\" 라는 채널이 이미 존재합니다.");
        }
    }

    private void validateCreator(Channel channel, UUID creatorId, String action) {
        if (!channel.getCreator().getId().equals(creatorId)) {
            throw new IllegalArgumentException("방장만 채널을 " + action + "할 수 있습니다.");
        }
    }

    private void postSystemMessage(User user, Channel channel, String content) {
        messageService.createMessage(new Message(content, user, channel));
    }

    @Override
    public Channel createChannel(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("채널이 null입니다.");
        }

        if (Files.exists(fileFor(channel.getId()))) {
            throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        }

        User creator = userService.getUserById(channel.getCreator().getId());
        validateDuplicateChannelName(channel.getChannelName());

        channel.joinChannel(creator);

        userService.saveUser(creator);
        writeChannel(channel);

        postSystemMessage(
                creator,
                channel,
                "[" + channel.getChannelName() + "] 채널이 생성되었습니다."
        );

        return getChannelById(channel.getId());
    }

    @Override
    public Channel joinChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);

        channel.joinChannel(user);

        // 참여 목록은 사용자와 채널 양쪽에 있으므로 둘 다 저장
        userService.saveUser(user);
        writeChannel(channel);

        postSystemMessage(
                user,
                channel,
                user.getUsername() + "님이 입장하셨습니다."
        );

        return getChannelById(channelId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);

        if (!isParticipant(channelId, userId)) {
            throw new IllegalArgumentException("참여 중인 채널이 아닙니다.");
        }

        if (channel.getCreator().getId().equals(user.getId())) {
            throw new IllegalArgumentException(
                    "방장은 채널을 나갈 수 없습니다. 채널을 삭제해 주세요."
            );
        }

        postSystemMessage(
                user,
                channel,
                user.getUsername() + "님이 퇴장하셨습니다."
        );

        channel.leaveChannel(user);

        userService.saveUser(user);
        writeChannel(channel);
    }

    @Override
    public boolean isParticipant(UUID channelId, UUID userId) {
        userService.getUserById(userId);
        Channel channel = getChannelById(channelId);

        return channel.getChannelParticipants().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        Path file = fileFor(channelId);

        if (Files.notExists(file)) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }

        return readChannel(file);
    }

    @Override
    public List<Channel> getAllChannels() {
        return List.copyOf(readAllChannels());
    }

    @Override
    public List<Channel> getChannelsByCreatorId(UUID creatorId) {
        userService.getUserById(creatorId);

        return readAllChannels().stream()
                .filter(channel -> channel.getCreator().getId().equals(creatorId))
                .toList();
    }

    @Override
    public List<Channel> getChannelsByParticipantId(UUID participantId) {
        userService.getUserById(participantId);

        return readAllChannels().stream()
                .filter(channel -> channel.getChannelParticipants().stream()
                        .anyMatch(user -> user.getId().equals(participantId)))
                .toList();
    }

    @Override
    public List<User> getParticipants(UUID channelId) {
        return getChannelById(channelId).getChannelParticipants();
    }

    @Override
    public Channel updateChannelName(UUID channelId, UUID userId, String channelName) {
        userService.getUserById(userId);
        Channel channel = getChannelById(channelId);

        validateCreator(channel, userId, "수정");
        validateDuplicateChannelName(channelName);

        channel.changeChannelName(channelName);
        writeChannel(channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelById(channelId);
        validateCreator(channel, userId, "삭제");

        messageService.deleteMessagesByChannelId(channelId);

        // 채널에 저장된 참여자 객체 대신 각 사용자의 최신 파일 객체를 갱신
        for (User participant : channel.getChannelParticipants()) {
            User savedParticipant = userService.getUserById(participant.getId());

            channel.leaveChannel(savedParticipant);
            userService.saveUser(savedParticipant);
        }

        try {
            Files.delete(fileFor(channelId));
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 삭제에 실패했습니다.", e);
        }
    }
}