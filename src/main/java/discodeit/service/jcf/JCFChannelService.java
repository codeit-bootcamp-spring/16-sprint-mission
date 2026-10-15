package discodeit.service.jcf;

import discodeit.entity.Channel;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        userService.addDeleteListener(this::deleteUserFromChannels);
    }

    @Override
    public Channel create(List<User> userList, String channelName) {
        Channel newChannel = new Channel(channelName);
        userList.stream()
                .forEach(user -> user.setChannel(newChannel));
        if (!data.contains(newChannel)) {
            data.add(newChannel);
        }
        return newChannel;
    }

    @Override
    public Optional<Channel> find(UUID channelId) {
        Optional<Channel> channel = data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst();
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        System.out.println("모든 채널을 조회합니다.");
        return List.copyOf(data);
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        Channel target = find(channelId).orElseThrow(() ->
                new NoSuchElementException("수정할 채널을 찾을 수 없습니다."));

        if(target.getId().equals(channelId)) {
            target.setChannelName(newName);
            target.setUpdatedAt(System.currentTimeMillis());
        }

        return target;
    }

    @Override
    public void delete(UUID channelId) {
        Channel target = find(channelId).orElseThrow(() ->
                new NoSuchElementException("삭제할 채널을 찾을 수 없습니다."));

        List<User> userList = userService.findAll();
        target.getUserList()
                .forEach(u -> u.deleteChannel(channelId));

        if(data.contains(target)){
            data.remove(target);
        }
    }

    @Override
    public void deleteUserFromChannels(UUID userId) {
        userService.find(userId).orElseThrow(() ->
                new NoSuchElementException("해당 사용자가 없습니다."));

        data.forEach(c -> c.getUserList()
                .removeIf(u -> u.getId().equals(userId)));
    }
}
