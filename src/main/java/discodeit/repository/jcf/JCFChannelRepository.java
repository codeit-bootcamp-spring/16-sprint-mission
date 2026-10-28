package discodeit.repository.jcf;

import discodeit.entity.Channel;
import discodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data;

    public JCFChannelRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel save(Channel channel) {
        int idx = data.indexOf(channel);
        if (idx >= 0) {
            data.set(idx, channel);
        } else {
            data.add(channel);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.of(data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널을 찾을 수 없습니다.")));
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void deleteById(UUID id) {
        Channel target = findById(id).get();
        data.remove(target);
    }
}
