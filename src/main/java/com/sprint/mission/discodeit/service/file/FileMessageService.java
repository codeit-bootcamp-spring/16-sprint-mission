package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final Path filePath = Path.of("data", "messages.ser");
    private final FileUserService userService;
    private final FileChannelService channelService;

    public FileMessageService(FileUserService userService,FileChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
        try{
            Files.createDirectories(filePath.getParent());
        }catch (IOException e){
            throw new IllegalStateException("저장폴더 생성 못함",e);
        }
    }


    private void saveMessage(List<Message> messages) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))){
            out.writeObject(messages);
        } catch (IOException e) {
            throw new IllegalStateException("메시지 목록을 저장할 수 없습니다", e);
        }
    }
    private List<Message> loadMessages() {
        if(!Files.exists(filePath)){
            return new ArrayList<>();
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filePath.toFile()))){
            return (List<Message>)  in.readObject();
        }catch (IOException | ClassNotFoundException e){
            throw new IllegalStateException("메시지 목록을 읽을 수 없습니다",e);
        }
    }

    @Override
    public Message create(String comment, UUID userId, UUID channelId) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        List<Message> messages = loadMessages();
        Message message = new Message(comment, user, channel);

        messages.add(message);
        userService.addMessage(userId, message);
        channelService.addMessage(channelId, message);
        saveMessage(messages);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return loadMessages().stream().filter(m -> m.getId().equals(id)).findFirst().orElseThrow(()->
                new IllegalArgumentException("메시지를 찾을 수 없음"));
    }

    @Override
    public List<Message> findAll() {
        return loadMessages();
    }

    @Override
    public Message update(UUID id, String comment) {
        List<Message> messages = loadMessages();
        Message message = messages.stream().filter(m -> m.getId().equals(id)).findFirst().orElseThrow(()->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        message.update(comment);
        userService.updateMessage(message.getUser().getId(), id, comment);
        channelService.updateMessage(message.getChannel().getId(), id, comment);
        saveMessage(messages);
        return message;
    }

    @Override
    public void delete(UUID id) {
        List<Message> messages = loadMessages();

        Message message = messages.stream().filter(m->m.getId().equals(id)).findFirst().orElseThrow(()->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        userService.removeMessage(message.getUser().getId(), id);
        channelService.removeMessage(message.getChannel().getId(), id);
        messages.remove(message);
        saveMessage(messages);
    }
}