package discodeit.repository.jcf;

import discodeit.entity.Message;
import discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data;

    public JCFMessageRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {
        int idx = data.indexOf(message);
        if (idx >= 0) {
            data.set(idx, message);
        } else {
            data.add(message);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return data.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }

    @Override
    public void deleteById(UUID messageId) {
        Optional<Message> target = Optional.of(findById(messageId).get());
        data.remove(target);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.removeIf(m -> m.getUser().getId().equals(userId));
    }

    @Override
    public void deleteByChannelId(UUID channelId) {

    }
}
