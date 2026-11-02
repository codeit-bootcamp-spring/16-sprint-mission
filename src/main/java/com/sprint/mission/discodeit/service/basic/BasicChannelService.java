package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelRequest;
import com.sprint.mission.discodeit.dto.PublicChannelRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
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
    private final BinaryContentRepository binaryContentRepository;


    @Override
    public Channel createPublic(PublicChannelRequest request) {
        List<Channel> channels = channelRepository.findAll();

        if(channels.stream().anyMatch(c -> c.getType() == ChannelType.PUBLIC&&c.getName().equals(request.name()))) {
            throw new IllegalArgumentException("사용 중인 채널명");
        }

        Channel channel = new Channel(ChannelType.PUBLIC,request.name(),request.description());
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel createPrivate(PrivateChannelRequest request) {

        for(UUID userId : request.userIds()){
            userRepository.findById(userId).orElseThrow(() ->
                    new IllegalArgumentException("사용자를 찾을 수 없음"));
        }

        Channel channel = new Channel(ChannelType.PRIVATE,null,null);
        channelRepository.save(channel);

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
        List<UUID> userIds = new ArrayList<>();
        Channel channel = channelRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));
        // 요구사항 최근 메시지 시간정보 포함합니다 기능
        Instant lastMessageAt = messageRepository.findAll().stream().filter(m ->
                m.getChannelId().equals(channel.getId())).map(m->
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
        List<ChannelDto> channelDtos = new ArrayList<>();
        List<ReadStatus> readStatuses = readStatusRepository.findAll();

        for(Channel channel :channelRepository.findAll()){
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
        List<Channel> channels = channelRepository.findAll();

        Channel channel = channels.stream().filter(c -> c.getId().equals(request.channelId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));
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
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        List<Channel> channels = channelRepository.findAll();

        Channel channel = channels.stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        for(Message message : messageRepository.findAll()){
            if(message.getChannelId().equals(id)) {
                //채널삭제할때 메시지랑 첨부파일도 삭제하게함
                for(UUID attachmentId : message.getAttachmentIds())
                    binaryContentRepository.deleteById(attachmentId);
                messageRepository.deleteById(message.getId());
            }
        }
        for(ReadStatus readStatus : readStatusRepository.findAll()){
            if(readStatus.getChannelId().equals(id)){
                readStatusRepository.deleteById(readStatus.getId());
            }
        }

        channelRepository.deleteById(id);
    }

    @Override
    public void join(UUID channelId, UUID userId) {

        channelRepository.findById(channelId).orElseThrow(() ->
                new IllegalArgumentException("채널을 찾을 수 없음"));

        userRepository.findById(userId).orElseThrow(()->
                new IllegalArgumentException("유저를 찾을 수 없음"));

        for(ReadStatus readStatus : readStatusRepository.findAll()){
            if(readStatus.getChannelId().equals(channelId)&&readStatus.getUserId().equals(userId)){
                throw new IllegalArgumentException("중복참여 불가");
            }
        }
        ReadStatus readStatus = new ReadStatus(userId,channelId, Instant.now());

        readStatusRepository.save(readStatus);
    }
}