package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel createChannel(Channel channel) {
        data.add(channel); // 전달받을 채널을 목록에 추가한다.
        return channel; // 추가한 채널 객체를 반환한다.
    }

    @Override
    public Channel readChannel(UUID id) {
        for(Channel ch : data){
            if(ch.getId().equals(id)){
                // 해당 채널 id를 가져와 요청받은 id와 일치하면 ch 를 반환한다.
                return ch;
            }
        }
        return null; // 해당하는 채널을 찾을 수 없으면 null 로 반환한다.
    }

    @Override
    public List<Channel> readAllChannel() {
        // return List.of();
        // data 에 저장된 채널을 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public Channel updateChannel(UUID id, Channel channel) {
        for(Channel targetChannel : data){
            if(targetChannel.getId().equals(targetChannel)){
                // 찾고자 하는 채널 id가 맞다면 index 에 대입.
                int index = data.indexOf(targetChannel);
                // 해당 인덱스의 채널 객체를 수정한 channel 객체로 교체한다.
                data.set(index, channel);
                // 그리고 수정한 channel 객체를 반환한다.
                return targetChannel;
            }
        }
        return null; // 수정할 채널 id를 찾을 수 없으면 null 로 반환한다.
    }

    @Override
    public Channel deleteChannel(UUID id) {
        for(Channel channel : data){
            if(channel.getId().equals(id)){
                // 찾은 channel 목록에서 제거한 후 반환한다.
                data.remove(channel);
                return channel;
            }
        }
        return null;
    }
}