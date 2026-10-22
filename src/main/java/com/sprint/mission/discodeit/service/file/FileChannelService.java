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

    public FileChannelService() { this(Path.of("data", "channels")); }

    public FileChannelService(Path channelDirectory) {
        this.channelDirectory = channelDirectory;
        try { Files.createDirectories(channelDirectory); }
        catch (IOException e) { throw new RuntimeException("채널 디렉터리 생성에 실패했습니다.", e); }
    }

    public void setUserService(FileUserService userService) { this.userService = userService; }
    public void setMessageService(FileMessageService messageService) { this.messageService = messageService; }

    private Path fileFor(UUID id) { return channelDirectory.resolve(id + ".ser"); }

    private void writeChannel(Channel channel) {
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(fileFor(channel.getId())))) {
            out.writeObject(channel);
        } catch (IOException e) { throw new RuntimeException("채널 저장에 실패했습니다.", e); }
    }

    void saveChannel(Channel channel) { writeChannel(channel); }

    private Channel readChannel(Path file) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (Channel) in.readObject();
        } catch (IOException | ClassNotFoundException e) { throw new RuntimeException("채널 파일을 읽는 데 실패했습니다.", e); }
    }

    private List<Channel> readAllChannels() {
        List<Channel> channels = new ArrayList<>();
        try (Stream<Path> files = Files.list(channelDirectory)) {
            files.filter(path -> path.getFileName().toString().endsWith(".ser"))
                    .forEach(path -> channels.add(readChannel(path)));
        } catch (IOException e) { throw new RuntimeException("채널 파일 목록을 읽는 데 실패했습니다.", e); }
        return channels;
    }

    private void validateDuplicateChannelName(String name, UUID excludedId) {
        if (readAllChannels().stream().anyMatch(channel -> channel.getChannelName().equals(name)
                && !channel.getId().equals(excludedId))) {
            throw new IllegalArgumentException("\"" + name + "\" 라는 채널이 이미 존재합니다.");
        }
    }

    private void validateCreator(Channel channel, UUID userId, String action) {
        if (!channel.getCreatorId().equals(userId)) throw new IllegalArgumentException("방장만 채널을 " + action + "할 수 있습니다.");
    }

    private void postSystemMessage(UUID userId, UUID channelId, String content) {
        messageService.createMessage(new Message(content, userId, channelId));
    }

    @Override
    public Channel createChannel(Channel channel) {
        if (channel == null) throw new IllegalArgumentException("채널이 null입니다.");
        if (Files.exists(fileFor(channel.getId()))) throw new IllegalArgumentException("이미 존재하는 채널입니다.");
        User creator = userService.getUserById(channel.getCreatorId());
        validateDuplicateChannelName(channel.getChannelName(), null);
        channel.joinChannel(creator.getId());
        creator.addChannelId(channel.getId());
        userService.saveUser(creator);
        writeChannel(channel);
        postSystemMessage(creator.getId(), channel.getId(), "[" + channel.getChannelName() + "] 채널이 생성되었습니다.");
        return getChannelById(channel.getId());
    }

    @Override
    public Channel joinChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);
        channel.joinChannel(userId);
        user.addChannelId(channelId);
        userService.saveUser(user);
        writeChannel(channel);
        postSystemMessage(userId, channelId, user.getUsername() + "님이 입장하셨습니다.");
        return getChannelById(channelId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        User user = userService.getUserById(userId);
        Channel channel = getChannelById(channelId);
        if (!isParticipant(channelId, userId)) throw new IllegalArgumentException("참여 중인 채널이 아닙니다.");
        if (channel.getCreatorId().equals(userId)) throw new IllegalArgumentException("방장은 채널을 나갈 수 없습니다. 채널을 삭제해 주세요.");
        postSystemMessage(userId, channelId, user.getUsername() + "님이 퇴장하셨습니다.");
        User latestUser = userService.getUserById(userId);
        Channel latestChannel = getChannelById(channelId);
        latestChannel.leaveChannel(userId);
        latestUser.removeChannelId(channelId);
        userService.saveUser(latestUser);
        writeChannel(latestChannel);
    }

    @Override
    public boolean isParticipant(UUID channelId, UUID userId) {
        userService.getUserById(userId);
        return getChannelById(channelId).getParticipantIds().contains(userId);
    }

    @Override
    public Channel getChannelById(UUID channelId) {
        Path file = fileFor(channelId);
        if (Files.notExists(file)) throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        return readChannel(file);
    }

    @Override
    public List<Channel> getAllChannels() { return List.copyOf(readAllChannels()); }

    @Override
    public List<Channel> getChannelsByCreatorId(UUID creatorId) {
        userService.getUserById(creatorId);
        return readAllChannels().stream().filter(channel -> channel.getCreatorId().equals(creatorId)).toList();
    }

    @Override
    public List<Channel> getChannelsByParticipantId(UUID userId) {
        userService.getUserById(userId);
        return readAllChannels().stream().filter(channel -> channel.getParticipantIds().contains(userId)).toList();
    }

    @Override
    public List<User> getParticipants(UUID channelId) {
        List<User> users = new ArrayList<>();
        for (UUID id : getChannelById(channelId).getParticipantIds()) users.add(userService.getUserById(id));
        return List.copyOf(users);
    }

    @Override
    public Channel updateChannelName(UUID channelId, UUID userId, String name) {
        userService.getUserById(userId);
        Channel channel = getChannelById(channelId);
        validateCreator(channel, userId, "수정");
        validateDuplicateChannelName(name, channelId);
        channel.changeChannelName(name);
        writeChannel(channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId, UUID userId) {
        Channel channel = getChannelById(channelId);
        validateCreator(channel, userId, "삭제");
        messageService.deleteMessagesByChannelId(channelId);
        channel = getChannelById(channelId);
        for (UUID participantId : channel.getParticipantIds()) {
            User participant = userService.getUserById(participantId);
            channel.leaveChannel(participantId);
            participant.removeChannelId(channelId);
            userService.saveUser(participant);
        }
        try { Files.delete(fileFor(channelId)); }
        catch (IOException e) { throw new RuntimeException("채널 파일 삭제에 실패했습니다.", e); }
    }
}
