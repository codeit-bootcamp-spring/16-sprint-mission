package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
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

public class FileMessageService implements MessageService {
    // 메시지 데이터 파일이 저장될 경로를 지정한다.
    private static final Path FILE_PATH = Paths.get("data", "message.ser");

    private final UserService userService;
    private final ChannelService channelService;
    // 파일에서 읽어 온 메시지 목록을 보관한다.
    private List<Message> data;

    // 서비스가 생성될 때 데이터 폴더를 준비하고 파일에서 메시지 목록을 불러온다.
    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;

        createDataDirectory();
        // 파일에서 메시지 목록을 불러와 data 필드에 저장한다.
        this.data = loadData();
    }

    private void createDataDirectory(){
        try {
            // 데이터 파일을 저장할 폴더가 없으면 생성한다.
            Files.createDirectories(FILE_PATH.getParent());
        } catch (IOException ioException){
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다.", ioException);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadData() {
        // 저장된 파일이 없으면 빈 메시지 목록을 반환한다.
        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }

        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(FILE_PATH))) {
            // 파일에 저장된 메시지 목록을 읽어 반환한다.
            return (List<Message>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("메시지 데이터를 불러올 수 없습니다.", exception);
        }
    }

    private void saveData() {
        try (ObjectOutputStream outputStream =
                     new ObjectOutputStream(Files.newOutputStream(FILE_PATH))) {
            // 현재 메시지 목록 전체를 파일에 저장한다.
            outputStream.writeObject(data);
        } catch (IOException exception) {
            throw new RuntimeException("메시지 데이터를 저장할 수 없습니다.", exception);
        }
    }

    @Override
    public Message createMessage(Message message) {
        // 작성자와 채널이 실제로 존재하는지 확인한다.
        User planner = userService.readUser(message.getPlannerId());
        channelService.readChannel(message.getChannelId());

        // 작성자가 해당 채널에 참여 중인지 확인한다.
        if (!channelService.isMember(message.getChannelId(), message.getPlannerId())){
            throw new IllegalArgumentException
                    ("'" + planner.getName() + "' (이)가 해당 채널에 참여 중이 아닙니다.");
        }

        data.add(message);  // 메시지를 목록에 추가한다.
        saveData();
        return message;     // 추가한 메시지를 반환한다.
    }

    @Override
    public Message readMessage(UUID id) {
        // 요청받은 ID와 일치하는 메시지를 찾아 반환한다.
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst() // 조건에 맞는 첫 메시지를 가져온다.
                // 일치하는 메시지가 없으면 예외를 발생시킨다.
                .orElseThrow(
                        () -> new IllegalArgumentException("메시지를 찾을 수 없습니다"));
    }

    @Override
    public List<Message> readAllMessage() {
        // data에 저장된 메시지들을 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        // 수정할 메시지를 찾는다.
        Message targetMessage = readMessage(id);

        // 기존 메시지 내용과 수정할 내용이 동일한지 확인한다.
        if (targetMessage.getContent().equals(content)){
            throw new IllegalArgumentException("기존 메시지 내용과 동일합니다.");
        }
        targetMessage.changeContent(content); // 내용이 다를 때만 변경한다.
        saveData();
        return targetMessage;   // 내용을 변경한 메시지 객체를 반환한다.
    }

    // 전달받은 ID와 일치하는 메시지를 찾아 삭제한다.
    @Override
    public void deleteMessage(UUID id) {
        Message targetMessage = data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다."));

        // stream 탐색이 끝난 후 해당 메시지를 목록에서 삭제한다.
        data.remove(targetMessage);
        saveData();
    }
}