package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

public class FileChannelService implements ChannelService {

    // private final List<Channel> data;
    private final UserService userService;

    public FileChannelService(UserService userService) {
        // this.data = new ArrayList<>();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel Name cannot be null or empty");
        }

        Channel channel = new Channel(channelName);

        String fileName = "./data/channels/" + channel.getId() + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(channel);
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        // System.out.println(channel.getId());

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        List<UUID> channelIds;
        List<Channel> channels;

        try (Stream<Path> files = Files.list(Path.of("./data/channels/"))) {
            channelIds = files.filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.endsWith(".ser"))
                    .map(name -> name.substring(0, name.length() - 4))
                    .map(UUID::fromString)
                    .toList();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        channels = channelIds.stream()
                .map(this::findById)
                .toList();

        // System.out.println("All channels: " + channels);

        return channels;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = null;

        try (FileInputStream fis = new FileInputStream("./data/channels/" + id + ".ser");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            channel = (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            exception.printStackTrace();
        }

        return channel;
    }

    @Override
    public Channel updateChannelName(UUID id, String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new IllegalArgumentException("Channel Name cannot be null or empty");
        }

        Channel channel = this.findById(id);
        channel.updateChannelName(channelName);

        String fileName = "./data/channels/" + id + ".ser";

        try (FileOutputStream fos = new FileOutputStream(fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(channel); // 수정된 객체로 덮어쓰기

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        try {
            Files.deleteIfExists(Path.of("./data/channels/" + id + ".ser"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }


    @Override
    public void joinChannel(UUID id, UUID userId) {
        Channel channel = this.findById(id);
        User user = this.userService.findById(userId);

        channel.addUser(user);

        String channelFileName = "./data/channels/" + id + ".ser";

        try (FileOutputStream fos = new FileOutputStream(channelFileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(channel); // 수정된 객체로 덮어쓰기

        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        user.addChannel(channel);

        String userFileName = "./data/users/" + userId + ".ser";

        try (FileOutputStream fos = new FileOutputStream(userFileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(user); // 수정된 객체로 덮어쓰기

        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

    }

    @Override
    public void leaveChannel(UUID id, UUID userId) {
        Channel channel = this.findById(id);
        User user = this.userService.findById(userId);

        channel.removeUser(user);

        // System.out.println(channel.getUsers().contains(user)); // false를 반환하면 안된다.
        /*
        역직렬화된 객체는 기존 객체와 UUID가 같더라도 서로 다른 인스턴스이므로,
        equals()와 hashCode()를 재정의하지 않으면 HashSet에서 서로 다른 객체로 판단하여
        contains()나 remove()가 정상적으로 동작하지 않는 문제가 발생했다.
         */

        String channelFileName = "./data/channels/" + id + ".ser";

        try (FileOutputStream fos = new FileOutputStream(channelFileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(channel); // 수정된 객체로 덮어쓰기

        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }

        user.removeChannel(channel);

        String userFileName = "./data/users/" + userId + ".ser";

        try (FileOutputStream fos = new FileOutputStream(userFileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(user); // 수정된 객체로 덮어쓰기

        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

}
