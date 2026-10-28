package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;
    private final UserService userService;

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        userService.readUser(userId); // 유저가 존재하는지 확인한다.
        readChannel(channelId).addMember(userId);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        userService.readUser(userId); // 유저가 존재하는지 확인한다.
        readChannel(channelId).exitMember(userId);
    }

    @Override
    public boolean isMember(UUID channelId, UUID userId) {
        return readChannel(channelId).hasMember(userId);
    }

    // 생성자 초기화
    public JCFChannelService(UserService userService) {
        this.data = new ArrayList<>();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(Channel channel) {
        // 등록하려는 채널과 이름이 같은 기존 채널이 있는지 확인한다.
        boolean duplicated = data.stream()
                .anyMatch(targetChannel
                        -> targetChannel.getName().equals(channel.getName()));

        // 중복된 채널이 있으면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 채널 이름입니다.");
        }
        data.add(channel); // 채널을 목록에 추가한다.
        return channel; // 추가한 채널을 반환한다.
    }

    @Override
    public Channel readChannel(UUID id) {
        // 요청받은 ID와 일치하는 채널을 찾아 반환한다.
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst() // 조건에 맞는 첫 채널을 가져온다.
                // 일치하는 채널이 없으면 예외를 발생시킨다.
                .orElseThrow(
                        () -> new IllegalArgumentException("채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> readAllChannel() {
        // data에 저장된 채널들을 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public Channel updateChannel(UUID id, String name) {
        // 수정할 채널을 찾는다.
        Channel targetChannel = readChannel(id);

        // 기존의 채널명과 변경할 채널 이름이 같은지 검사한다.
        if(targetChannel.getName().equals(name)){
            throw new IllegalArgumentException("기존 채널명과 동일합니다.");
        }

        // 수정 대상이 아닌 다른 채널이 변경할 이름을 사용 중인지 확인한다.
        boolean duplicated = data.stream()
                .anyMatch(otherChannel
                        -> otherChannel != targetChannel &&
                        otherChannel.getName().equals(name));

        // 중복된 채널 이름이 있으면 예외를 발생시킨다.
        if(duplicated){
            throw new IllegalArgumentException("중복된 채널 이름입니다.");
        }
        targetChannel.changeName(name); // 이름을 변경하고 수정 시각을 갱신한다.
        return targetChannel;           // 이름을 변경한 채널 객체를 반환한다.
    }

    // 전달받은 ID와 일치하는 채널을 찾아 삭제한다.
    @Override
    public void deleteChannel(UUID id) {
        Channel targetChannel = data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다."));

        // stream 탐색이 끝난 후 해당 채널을 목록에서 삭제한다.
        data.remove(targetChannel);
    }
}