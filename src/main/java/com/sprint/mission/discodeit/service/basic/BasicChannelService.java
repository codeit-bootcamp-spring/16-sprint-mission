package com.sprint.mission.discodeit.service.basic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

@Service // 해당 클래스를 Spring의 Service 계층 Bean으로 등록하여, Spring IoC 컨테이너가 관리하도록 한다.
@RequiredArgsConstructor // final 필드와 @NonNull 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 생성하도록 한다.
public class BasicChannelService implements ChannelService {

    // 필드
    private final ChannelRepository channelRepository; // 데이터를 저장하고 조회하는 작업을 담당할 Repository
    private final UserService userService; // 유저의 존재 여부를 확인하기 위한 UserService

    @Override
    public Channel createChannel(Channel channel) {
        // 기존 채널 목록에서 동일한 이름을 가진 채널이 있는지 검사한다.
        boolean duplicated = channelRepository.findAll().stream()
                .anyMatch(targetChannel ->
                        targetChannel.getName().equals(channel.getName()));

        // 이름이 중복되면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 채널 이름입니다.");
        }

        // Repository에 채널 저장을 요청한다.
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        // ID에 해당하는 채널을 조회하고, 없으면 예외를 발생시킨다.
        return channelRepository.findByChannelId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> readAllChannel() {
        // Repository에 전체 채널 목록 조회를 요청한다.
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID id, String name) {
        // 수정할 채널을 조회한다.
        Channel targetChannel = readChannel(id);

        // 기존 채널명과 변경할 이름이 같으면 예외를 발생시킨다.
        if (targetChannel.getName().equals(name)) {
            throw new IllegalArgumentException("기존 채널명과 동일합니다.");
        }

        // 수정 대상 이외의 다른 채널이 변경할 이름을 사용 중인지 검사한다.
        boolean duplicated = channelRepository.findAll().stream()
                .anyMatch(otherChannel ->
                        !otherChannel.getId().equals(id)
                                && otherChannel.getName().equals(name));

        // 이름이 중복되면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 채널 이름입니다.");
        }

        // 채널 이름을 변경하고 수정 시각을 갱신한다.
        targetChannel.changeName(name);

        // 변경된 채널 정보를 Repository에 저장한다.
        channelRepository.save(targetChannel);
        return targetChannel;
    }

    @Override
    public void deleteChannel(UUID id) {
        // 삭제할 채널이 존재하는지 확인한다.
        readChannel(id);

        // Repository에 해당 채널의 삭제를 요청한다.
        channelRepository.deleteByChannelId(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        // 가입하려는 유저가 존재하는지 확인한다.
        userService.readUser(userId);

        // 가입하려는 채널을 조회한다.
        Channel channel = readChannel(channelId);

        // 채널에 유저를 추가한다.
        channel.addMember(userId);

        // 변경된 채널 정보를 Repository에 저장한다.
        channelRepository.save(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        // 탈퇴하려는 유저가 존재하는지 확인한다.
        userService.readUser(userId);

        // 탈퇴하려는 채널을 조회한다.
        Channel channel = readChannel(channelId);

        // 채널에서 유저를 제거한다.
        channel.exitMember(userId);

        // 변경된 채널 정보를 Repository에 저장한다.
        channelRepository.save(channel);
    }

    @Override
    public boolean isMember(UUID channelId, UUID userId) {
        // 채널을 조회하고 해당 유저가 멤버인지 확인한다.
        return readChannel(channelId).hasMember(userId);
    }
}