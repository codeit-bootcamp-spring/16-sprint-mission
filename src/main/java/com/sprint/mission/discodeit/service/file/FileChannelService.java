package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final MessageService messageService;

    public FileChannelService(ChannelRepository channelRepository, MessageService messageService) {
        this.channelRepository = channelRepository;
        this.messageService = messageService;
    }

    @Override
    public Channel save(Channel channel) {
        if (channel == null || channel.getId() == null) {
            throw new NullPointerException("채널 데이터 오류.");
        }
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public List<Channel> findId(UUID id) {
        Channel channel = channelRepository.findId(id);
        if (channel == null) {
            return Collections.emptyList();
        }
        List<Channel> result = new ArrayList<>();
        result.add(channel);
        return result;
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return messageService.findChannelId(channelId);
    }

    @Override
    public Channel findEntityChannel(UUID id) {
        return channelRepository.findId(id);
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = channelRepository.findId(id);
        if (channel == null) {
            throw new NullPointerException("해당 채널이 없습니다. ID: " + id);
        }
        channel.setName(name);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void deleteById(UUID id) {
        messageService.deleteByChannelId(id);
        channelRepository.deleteId(id);
    }
}
