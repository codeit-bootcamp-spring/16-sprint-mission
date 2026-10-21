package discodeit.service.file;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.service.MessageService;
import discodeit.service.UserService;

import java.util.*;

public class FileMessageService implements MessageService {
    private final List<Message> data;
    private final UserService userService;

    public FileMessageService(UserService userService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        userService.addDeleteListener(this::deleteMessageByUserId);
    }

    @Override
    public Message create(User user, Channel channel, String comment) {
        Message newMessage = new Message(user, channel, comment);
        Optional<User> checkUser = userService.find(user.getId());
        if (checkUser.isPresent()){
            if (!data.contains(newMessage)) {
                data.add(newMessage);
            }
        } else {
            throw new NoSuchElementException("해당 유저가 존재하지 않으므로 메세지 생성이 불가합니다.");
        }

        return newMessage;
    }

    @Override
    public Optional<Message> find(UUID messageId) {
       Optional<Message> message = data.stream()
               .filter(m -> m.getId().equals(messageId))
               .findFirst();
       return message;
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }

    @Override
   public void delete(UUID messageId) {
        Message target = find(messageId).orElseThrow(() ->
                new NoSuchElementException("삭제하려는 메세지를 찾을 수 없습니다."));

        if(target.getId().equals(messageId)){
            data.remove(target);
        }
    }

    @Override
    public Message update(UUID messageId, String newMessage) {
        Message target = find(messageId).orElseThrow(() ->
                new NoSuchElementException());

        if(!target.getComment().equals(newMessage)){
            target.setComment(newMessage);
            target.setUpdatedAt(System.currentTimeMillis());
        }
        return target;
    }

    @Override
    public void deleteMessageByUserId(UUID userId) {
        userService.find(userId).orElseThrow(() ->
                new NoSuchElementException("해당 사용자가 없습니다."));

        data.removeIf(m -> m.getUser().getId().equals(userId));
    }
}
