package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.ChannelService;
import discodeit.service.MessageService;
import discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileMessageService implements MessageService {

    private static final String FILE_PATH = "messages.ser";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        userService.addDeleteListener(this::deleteMessageByUserId);
        channelService.addDeleteListener(this::deleteMessagesByChannelId);
    }

    @Override
    public Message create(User user, Channel channel, String comment) {
        Optional<User> checkUser = userService.find(user.getId());
        if (!checkUser.isPresent()) {
            throw new NoSuchElementException("해당 유저가 존재하지 않으므로 메세지 생성이 불가합니다.");
        }
        Optional<Channel> checkChannel = channelService.find(channel.getId());
        if (!checkChannel.isPresent()) {
            throw new NoSuchElementException("해당 채널이 존재하지 않으므로 메세지 생성이 불가합니다.");
        }

        List<Message> messages = load();
        Message newMessage = new Message(user, channel, comment);
        messages.add(newMessage);
        save(messages);

        return newMessage;
    }

    @Override
    public Optional<Message> find(UUID messageId) {
       return load().stream()
               .filter(m -> m.getId().equals(messageId))
               .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return load();
    }

    @Override
   public void delete(UUID messageId) {
        List<Message> messages = load();
        Message target = messages.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() ->
                        new NoSuchElementException("삭제할 메세지를 찾을 수 없습니다."));
        messages.removeIf(m -> m.getId().equals(target.getId()));
        save(messages);
    }

    @Override
    public Message update(UUID messageId, String newMessage) {
        List<Message> messages = load();
        Message target = messages.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() ->
                new NoSuchElementException("수정할 메세지를 찾을 수 없습니다."));

        if(!target.getComment().equals(newMessage)){
            target.setComment(newMessage);
            target.setUpdatedAt(System.currentTimeMillis());
            save(messages);
        }
        return target;
    }

    @Override
    public void deleteMessageByUserId(UUID userId) {
        List<Message> messages = load();
        userService.find(userId).orElseThrow(() ->
                new NoSuchElementException("해당 사용자가 없습니다."));

        messages.removeIf(m -> m.getUser().getId().equals(userId));
        save(messages);
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        List<Message> messages = load();
        messages.removeIf(m -> m.getChannel().getId().equals(channelId));
        save(messages);
    }

    private void save(List<Message> messageList) {
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
