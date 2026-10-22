package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicEntityService;

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
        BasicEntityService entities = new BasicEntityService(
                new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        User user = entities.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        Channel channel = entities.getChannels().stream().filter(c -> c.getId().equals(channelId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        Message message = new Message(comment, user, channel);
        entities.getMessages().add(message);
        entities.saveAll();
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
        BasicEntityService entities = new BasicEntityService(
                new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        Message message = entities.getMessages().stream().filter(m -> m.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        entities.getUsers().stream().filter(u -> u.getId().equals(message.getUser().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        entities.getChannels().stream().filter(c -> c.getId().equals(message.getChannel().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        message.update(comment);
        entities.saveAll();
        return message;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(
                new FileUserRepository(), new FileChannelRepository(), new FileMessageRepository());

        Message message = entities.getMessages().stream().filter(m -> m.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("메시지를 찾을 수 없음"));

        entities.getUsers().stream().filter(u -> u.getId().equals(message.getUser().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        entities.getChannels().stream().filter(c -> c.getId().equals(message.getChannel().getId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        entities.getMessages().remove(message);
        entities.saveAll();
    }
}