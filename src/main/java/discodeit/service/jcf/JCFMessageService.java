package discodeit.service.jcf;

import discodeit.entity.Channel;
import discodeit.entity.Message;
import discodeit.entity.User;
import discodeit.repository.MessageRepository;
import discodeit.service.MessageService;
import discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    public JCFMessageService(UserService userService, MessageRepository messageRepository) {
        this.userService = userService;
        this.messageRepository = messageRepository;
        userService.addDeleteListener(this::deleteMessageByUserId);
    }

    @Override
    public Message create(User user, Channel channel, String comment) {
        Message newMessage = new Message(user, channel, comment);
        Optional<User> checkUser = userService.find(user.getId());
        if (checkUser.isPresent()){
            messageRepository.save(newMessage);
        } else {
            throw new NoSuchElementException("해당 유저가 존재하지 않으므로 메세지 생성이 불가합니다.");
        }

        return newMessage;
    }

    @Override
    public Optional<Message> find(UUID messageId) {
       return messageRepository.findById(messageId);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
   public void delete(UUID messageId) {
        Message target = find(messageId).orElseThrow(() ->
                new NoSuchElementException("삭제하려는 메세지를 찾을 수 없습니다."));

        if(target.getId().equals(messageId)){
            messageRepository.deleteById(messageId);
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

        messageRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {

    }
}
