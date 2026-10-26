package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.User;
import discodeit.repository.ChannelRepository;
import discodeit.service.ChannelService;
import discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public class FileChannelService implements ChannelService {

    private final UserService userService;
    private final ChannelRepository channelRepository;
    private final List<Consumer<UUID>> deleteListeners = new ArrayList<>();

    public FileChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
        userService.addDeleteListener(this::deleteUserFromChannels);
    }

    @Override
    public Channel create(List<User> userList, String channelName) {
        List<User> users = userList.stream()
                .map(u -> userService.find(u.getId())
                        .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다.")))
                        .toList();
        Channel newChannel = new Channel(channelName);
        users.forEach(newChannel::setUser);
        channelRepository.save(newChannel);
        userService.addChannelToUsers(newChannel);

        return newChannel;
    }

    @Override
    public Optional<Channel> find(UUID channelId) {
        return channelRepository.findById(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        List<Channel> channels = channelRepository.findAll();
        Channel target = channels.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() ->
                new NoSuchElementException("수정할 채널을 찾을 수 없습니다."));

        if(target.getId().equals(channelId)) {
            target.setChannelName(newName);
            target.setUpdatedAt(System.currentTimeMillis());
            channelRepository.save(target);
        }
        return target;
    }

    @Override
    public void delete(UUID channelId) {
        find(channelId).orElseThrow(() -> new NoSuchElementException("삭제할 채널이 없습니다."));
        deleteListeners.forEach(l -> l.accept(channelId));
        userService.removeChannelFromUsers(channelId);
        channelRepository.deleteById(channelId);
    }

    @Override
    public void deleteUserFromChannels(UUID userId) {
        List<Channel> channels = channelRepository.findAll();
        userService.find(userId).orElseThrow(() ->
                new NoSuchElementException("해당 사용자가 없습니다."));

        for (Channel channel : channels) {
            channel.deleteUser(userId);
            channelRepository.save(channel);
        }
    }

    @Override
    public void addDeleteListener(Consumer<UUID> listener) {
        deleteListeners.add(listener);
    }

}
