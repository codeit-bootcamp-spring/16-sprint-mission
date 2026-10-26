package discodeit.service.jcf;

import discodeit.entity.Channel;
import discodeit.entity.User;
import discodeit.repository.ChannelRepository;
import discodeit.service.ChannelService;
import discodeit.service.UserService;

import java.util.*;
import java.util.function.Consumer;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public JCFChannelService(UserService userService, ChannelRepository channelRepository) {
        this.userService = userService;
        this.channelRepository = channelRepository;
        userService.addDeleteListener(this::deleteUserFromChannels);
    }

    @Override
    public Channel create(List<User> userList, String channelName) {
        Channel newChannel = new Channel(channelName);
        userList.stream()
                .forEach(user -> user.setChannel(newChannel));

        channelRepository.save(newChannel);
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
        Channel target = find(channelId).orElseThrow(() ->
                new NoSuchElementException("수정할 채널을 찾을 수 없습니다."));

        if(target.getId().equals(channelId)) {
            target.setChannelName(newName);
            target.setUpdatedAt(System.currentTimeMillis());
        }

        channelRepository.save(target);

        return target;
    }

    @Override
    public void delete(UUID channelId) {
        Channel target = find(channelId).orElseThrow(() ->
                new NoSuchElementException("삭제할 채널을 찾을 수 없습니다."));

        List<User> userList = userService.findAll();
        target.getUserList()
                .forEach(u -> u.deleteChannel(channelId));

        channelRepository.deleteById(channelId);
    }

    @Override
    public void deleteUserFromChannels(UUID userId) {
        userService.find(userId).orElseThrow(() ->
                new NoSuchElementException("해당 사용자가 없습니다."));

        channelRepository.findById(userId).ifPresent(channel -> {
            channel.getUserList().forEach(u -> u.deleteChannel(userId));
        });
    }

    @Override
    public void addDeleteListener(Consumer<UUID> consumer) {

    }
}
