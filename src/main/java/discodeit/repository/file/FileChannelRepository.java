package discodeit.repository.file;

import discodeit.entity.Channel;
import discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PATH = "channels.ser";

    @Override
    public Channel save(Channel channel) {
        List<Channel> channels = load();
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getId().equals(channel.getId())) {
                channels.set(i, channel);
                persist(channels);
                return channel;
            }
        }
        channels.add(channel);
        persist(channels);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return load().stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return load();
    }

    @Override
    public void deleteById(UUID id) {
        List<Channel> channels = load();
        channels.removeIf(c -> c.getId().equals(id));
        persist(channels);
    }

    private void persist(List<Channel> channelList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
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
