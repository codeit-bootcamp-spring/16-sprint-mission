package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class BasicEntityService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    private final List<User> users;
    private final List<Channel> channels;
    private final List<Message> messages;

    public BasicEntityService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;

        this.users = userRepository.findAll();
        this.channels = channelRepository.findAll();
        this.messages = messageRepository.findAll();
        syncRelations();
    }

    public void syncRelations() {
        for(User user : users){
            user.getChannels().clear();
            user.getMessages().clear();
        }

        for(Channel channel : channels){
            List<User> participants = new ArrayList<>(channel.getUsers());

            channel.getUsers().clear();
            channel.getMessages().clear();

            for(User participant : participants){
                for(User user : users){
                    if(user.getId().equals(participant.getId())){
                        boolean alreadyJoined = channel.getUsers().stream().anyMatch(u ->
                                u.getId().equals(user.getId()));

                        if(!alreadyJoined){
                            channel.getUsers().add(user);
                            user.getChannels().add(channel);
                        }
                    }
                }
            }
        }

        for(Message message : messages){
            boolean userFound = false;
            boolean channelFound = false;

            for(User user : users){
                if(user.getId().equals(message.getUser().getId())){
                    message.updateUser(user);
                    user.getMessages().add(message);
                    userFound = true;
                    break;
                }
            }

            for(Channel channel : channels){
                if(channel.getId().equals(message.getChannel().getId())){
                    message.updateChannel(channel);
                    channel.getMessages().add(message);
                    channelFound = true;
                    break;
                }
            }

            if(!userFound){
                message.getUser().getChannels().clear();
                message.getUser().getMessages().clear();
            }

            if(!channelFound){
                message.getChannel().getUsers().clear();
                message.getChannel().getMessages().clear();
            }
        }
    }

    public void saveAll() {
        syncRelations();

        for(User savedUser : userRepository.findAll()){
            if(users.stream().noneMatch(u -> u.getId().equals(savedUser.getId()))){
                userRepository.deleteById(savedUser.getId());
            }
        }

        for(Channel savedChannel : channelRepository.findAll()){
            if(channels.stream().noneMatch(c -> c.getId().equals(savedChannel.getId()))){
                channelRepository.deleteById(savedChannel.getId());
            }
        }

        for(Message savedMessage : messageRepository.findAll()){
            if(messages.stream().noneMatch(m -> m.getId().equals(savedMessage.getId()))){
                messageRepository.deleteById(savedMessage.getId());
            }
        }

        for(User user : users){
            userRepository.save(user);
        }

        for(Channel channel : channels){
            channelRepository.save(channel);
        }

        for(Message message : messages){
            messageRepository.save(message);
        }
    }

    public List<User> getUsers() {
        return this.users;
    }

    public List<Channel> getChannels() {
        return this.channels;
    }

    public List<Message> getMessages() {
        return this.messages;
    }
}