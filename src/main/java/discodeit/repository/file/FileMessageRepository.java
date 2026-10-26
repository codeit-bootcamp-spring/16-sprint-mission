package discodeit.repository.file;

import discodeit.entity.Message;
import discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final static String FILE_PATH = "messages.ser";

    @Override
    public Message save(Message message) {
        List<Message> messages = load();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                messages.set(i, message);
                persist(messages);
                return message;
            }
        }
        messages.add(message);
        persist(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return load().stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return load();
    }

    @Override
    public void deleteById(UUID messageId) {
        List<Message> messages = load();
        messages.removeIf(m -> m.getId().equals(messageId));
        persist(messages);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        List<Message> messages = load();
        messages.removeIf(m -> m.getUser().getId().equals(userId));
        persist(messages);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        List<Message> messages = load();
        messages.removeIf(m -> m.getChannel().getId().equals(channelId));
        persist(messages);
    }

    private void persist(List<Message> messageList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messageList);
        }  catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<Message> load() {
        List<Message> messageList = new ArrayList<>();
        if (!Files.exists(Path.of(FILE_PATH))) {
            return messageList;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            messageList = (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return messageList;
    }
}
