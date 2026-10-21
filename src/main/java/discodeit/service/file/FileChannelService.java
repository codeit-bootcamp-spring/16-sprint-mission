package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileChannelService implements ChannelService {

    private static final String FILE_PATH = "channels.ser";
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.userService = userService;
        userService.addDeleteListener(this::deleteUserFromChannels);
    }

    @Override
    public Channel create(List<User> userList, String channelName) {
        List<Channel> channels = load();
        Channel newChannel = new Channel(channelName);
        return newChannel;
    }

    @Override
    public Optional<Channel> find(UUID channelId) {
        return load().stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return load();
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        List<Channel> channels = load();
        Channel target = channels.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() ->
                new NoSuchElementException("수정할 채널을 찾을 수 없습니다."));

        if(target.getId().equals(channelId)) {
            target.setChannelName(newName);
            target.setUpdatedAt(System.currentTimeMillis());
            save(channels);
        }
        return target;
    }

    @Override
    public void delete(UUID channelId) {
        List<Channel> channels = load();
        Channel target = channels.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() ->
                new NoSuchElementException("삭제할 채널을 찾을 수 없습니다."));

        List<User> userList = userService.findAll();
        target.getUserList()
                .forEach(u -> u.deleteChannel(channelId));

        if(channels.contains(target)){
            channels.remove(target);
            save(channels);
        }
    }

    @Override
    public void deleteUserFromChannels(UUID userId) {
        List<Channel> channels = load();
        userService.find(userId).orElseThrow(() ->
                new NoSuchElementException("해당 사용자가 없습니다."));

        channels.forEach(c -> c.getUserList()
                .removeIf(u -> u.getId().equals(userId)));
    }

    private void save(List<Channel> channelList) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH)) {
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(channelList);
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Channel> load() {
        List<Channel> channelList = new ArrayList<>();
        if (!Files.exists(Path.of(FILE_PATH))) {
            return channelList;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            channelList = (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return channelList;
    }
}
