package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;


    @Override
    public Channel createPublic(PublicChannelRequest request) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<Channel> channels = entities.getChannels();

        if(channels.stream().anyMatch(c -> c.getType() == ChannelType.PUBLIC&&c.getName().equals(request.name()))) {
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        Channel channel = new Channel(ChannelType.PUBLIC,request.name(),request.description());
        channels.add(channel);
        entities.saveAll();
        return channel;
    }

    @Override
    public Channel createPrivate(PrivateChannelRequest request) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        for(UUID userId : request.userIds()){
            userRepository.findById(userId).orElseThrow(() ->
                    new IllegalArgumentException("사용자를 찾을 수 없음"));
        }

        Channel channel = new Channel(ChannelType.PRIVATE,null,null);
        entities.getChannels().add(channel);
        entities.saveAll();

        List<UUID> participantIds = new ArrayList<>();

        for(UUID userId : request.userIds()){
            if(!participantIds.contains(userId)){
                ReadStatus readStatus = new ReadStatus(userId,channel.getId(), Instant.now());
                readStatusRepository.save(readStatus);
                participantIds.add(userId);
            }
        }

        return channel;
    }

    @Override
    public ChannelDto findById(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<UUID> userIds = new ArrayList<>();
        Channel channel = entities.getChannels().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));
        // 요구사항 최근 메시지 시간정보 포함합니다 기능
        Instant lastMessageAt = entities.getMessages().stream().filter(m ->
                m.getChannel().getId().equals(channel.getId())).map(m->
                m.getCreatedAt()).max(Instant::compareTo).orElse(null);
        // 요구사항 Priavte 채널인경우 참여한 정보 포함 기능
        if(channel.getType().equals(ChannelType.PRIVATE)){
            for(ReadStatus readStatus : readStatusRepository.findAll()){
                if(readStatus.getChannelId().equals(id)){
                    userIds.add(readStatus.getUserId());
                }
            }
        }

        return new ChannelDto(channel.getId(),channel.getType(),channel.getName(), channel.getDescription(),lastMessageAt,userIds);
    }

    @Override
    public List<ChannelDto> findAllByUserId(UUID userId) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<ChannelDto> channelDtos = new ArrayList<>();
        List<ReadStatus> readStatuses = readStatusRepository.findAll();

        for(Channel channel : entities.getChannels()){
            boolean joined = readStatuses.stream().anyMatch(s ->
                    s.getUserId().equals(userId)&&s.getChannelId().equals(channel.getId()));

            if(channel.getType() == ChannelType.PUBLIC || joined){
                ChannelDto channelDto = findById(channel.getId());
                channelDtos.add(channelDto);
            }
        }

        return channelDtos;
    }

    @Override
    public Channel update(ChannelUpdateRequest request) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<Channel> channels = entities.getChannels();



        Channel channel = channels.stream().filter(c -> c.getId().equals(request.channelId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        //요구사항 PRIVATE 채널은 수정불가능 기능
        if(channel.getType().equals(ChannelType.PRIVATE)){
            throw new IllegalArgumentException("PRIVATE 채널은 수정불가");
        }

        if(channels.stream()
                .filter(c -> c.getType() == ChannelType.PUBLIC)
                .anyMatch(otherChannel -> !otherChannel.getId().equals(request.channelId())
                                && otherChannel.getName().equals(request.name()))) {
            throw new IllegalArgumentException("사용 중인 채널명");
        }


        channel.update(request.name(),request.description());
        entities.saveAll();
        return channel;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<Channel> channels = entities.getChannels();

        Channel channel = channels.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        channels.remove(channel);
        entities.saveAll();
    }

    @Override
    public void join(UUID channelId, UUID userId) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);

        Channel channel = entities.getChannels().stream().filter(c -> c.getId().equals(channelId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        User user = entities.getUsers().stream().filter(u -> u.getId().equals(userId)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(channel.getUsers().stream().anyMatch(u -> u.getId().equals(userId))){
            throw new IllegalArgumentException("이미 참여한 사용자입니다");
        }

        channel.getUsers().add(user);
        user.getChannels().add(channel);
        entities.saveAll();
    }
}