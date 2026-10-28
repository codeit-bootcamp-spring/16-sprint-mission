package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    // 채널 데이터 파일이 저장될 경로를 지정한다.
    private static final Path FILE_PATH = Paths.get("data", "channel.ser");

    private final UserService userService;
    // 파일에서 읽어 온 채널 목록을 보관한다.
    private List<Channel> data;

    // 서비스가 생성될 때 데이터 폴더를 준비하고 파일에서 채널 목록을 불러온다.
    public FileChannelService(UserService userService) {
        this.userService = userService;
        createDataDirectory();

        // 파일에서 채널 목록을 불러와 data 필드에 저장한다.
        this.data = loadData();
    }

    // 데이터 파일을 저장할 폴더가 없으면 생성한다.
    private void createDataDirectory(){
        try {
            // 데이터 파일을 저장할 폴더가 없으면 생성한다.
            Files.createDirectories(FILE_PATH.getParent());
        } catch (IOException exception) {
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다.", exception);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Channel> loadData() {
        // 저장된 파일이 없으면 빈 채널 목록을 반환한다.
        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(FILE_PATH))) {
            // 파일에 저장된 채널 목록을 읽어 반환한다.
            return (List<Channel>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("채널 데이터를 불러올 수 없습니다.", exception);
        }
    }

    private void saveData() {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(Files.newOutputStream(FILE_PATH))) {
            // 현재 채널 목록 전체를 파일에 저장한다.
            outputStream.writeObject(data);
        } catch (IOException exception) {
            throw new RuntimeException("채널 데이터를 저장할 수 없습니다.", exception);
        }
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        userService.readUser(userId); // 유저가 존재하는지 확인한다.
        readChannel(channelId).addMember(userId);
        saveData();
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        userService.readUser(userId); // 유저가 존재하는지 확인한다.
        readChannel(channelId).exitMember(userId);
        saveData();
    }

    @Override
    public boolean isMember(UUID channelId, UUID userId) {
        return readChannel(channelId).hasMember(userId);
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
        saveData(); // 생성된 채널 목록을 파일에 저장한다.
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
        saveData(); // 수정된 채널 목록을 파일에 저장한다.
        return targetChannel; // 이름을 변경한 채널 객체를 반환한다.
    }

    // 전달받은 ID와 일치하는 채널을 찾아 삭제한다.
    @Override
    public void deleteChannel(UUID id) {
        Channel targetChannel = data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 채널을 찾을 수 없습니다."));

        data.remove(targetChannel); // stream 탐색이 끝난 후 해당 채널을 목록에서 삭제한다.
        saveData(); // 삭제된 채널 목록을 파일에 저장한다.
    }
}