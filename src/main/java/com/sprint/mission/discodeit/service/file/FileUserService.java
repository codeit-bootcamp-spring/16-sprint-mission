package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class FileUserService implements UserService {
    // 유저 데이터 파일이 저장될 경로를 지정한다.
    private static final Path FILE_PATH = Paths.get("data", "user.ser");
    // 파일에서 읽어 온 유저 목록을 보관한다.
    private List<User> data;

    public FileUserService(){
        createDataDirectory();
        // 파일에서 유저 목록을 불러와 data 필드에 저장한다.
        this.data = loadData();
    }

    // 데이터 파일을 저장할 폴더가 없으면 생성한다.
    private void createDataDirectory(){
        try {
            // 데이터 파일을 저장할 폴더가 없으면 생성한다.
            Files.createDirectories(FILE_PATH.getParent());
        } catch (IOException exception){
            throw new RuntimeException("데이터 폴더를 만들 수 없습니다.", exception);
        }
    }

    // 파일에서 읽는 객체를 List<User> 로 변환할 때 발생하는 unchecked 경고를 억제한다는 뜻입니다.
    @SuppressWarnings("unchecked")
    private List<User> loadData(){
        // 저장된 파일이 없으면 빈 유저 목록을 반환한다.
        if (Files.notExists(FILE_PATH)) {
            return new ArrayList<>();
        }

        // 파일에서 객체를 읽을 입력 스트림을 생성하고, 사용 후 자동으로 닫는다. (역직렬화)
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(Files.newInputStream(FILE_PATH))) {

            // 파일에 저장된 유저 목록을 읽어 반환한다.
            return (List<User>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException exception) {
            throw new RuntimeException("유저 데이터를 불러올 수 없습니다.", exception);
        }
    }

    private void saveData(){
        // 파일에 객체를 저장할 출력 스트림을 생성하고, 사용 후 자동으로 닫는다. (직렬화)
        try (ObjectOutputStream outputStream =
                new ObjectOutputStream(Files.newOutputStream(FILE_PATH))){

            // 현재 유저 목록 전체를 파일에 저장한다.
            outputStream.writeObject(data);
        } catch (IOException ioexception) {
            throw new RuntimeException("유저 데이터를 저장할 수 없습니다.", ioexception);
        }
    }

    @Override
    public User createUser(User user) {
        // 등록하려는 유저와 이름이 같은 기존 유저가 있는지 확인한다.
        boolean duplicated = data.stream()
                .anyMatch(targetUser
                        -> targetUser.getName().equals(user.getName()));

        // 중복된 이름이 있으면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 이름입니다.");
        }
        data.add(user); // 유저를 목록에 추가한다.
        saveData(); // 생성된 유저 목록을 파일에 저장한다.
        return user; // 추가한 유저를 반환한다.
    }

    @Override
    public User readUser(UUID id) {
        // 요청받은 ID와 일치하는 유저를 찾아 반환한다.
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst() // 조건에 맞는 첫 유저를 가져온다.
                // 일치하는 유저가 없으면 예외를 발생시킨다.
                .orElseThrow(
                        () -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> readAllUser() {
        // data에 저장된 유저들을 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public User updateUser(UUID id, String name) {
        // 수정할 유저를 찾는다.
        User targetUser = readUser(id);

        // 기존의 이름과 변경할 이름이 같은지 검사한다.
        if (targetUser.getName().equals(name)) {
            throw new IllegalArgumentException("기존 이름과 동일합니다");
        }

        // 수정 대상이 아닌 다른 유저가 변경할 이름을 사용 중인지 확인한다.
        boolean duplicated = data.stream()
                .anyMatch(otherUser
                        -> otherUser != targetUser &&
                        otherUser.getName().equals(name));

        // 중복된 유저 이름이 있으면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 유저 이름입니다.");
        }
        targetUser.changeName(name); // 이름을 변경하고 수정 시각을 갱신한다.
        saveData(); // 수정된 유저 목록을 파일에 저장한다.
        return targetUser; // 이름을 변경한 유저 객체를 반환한다.
    }

    // 전달받은 ID와 일치하는 유저를 찾아 삭제한다.
    @Override
    public void deleteUser(UUID id) {
        User targetUser = data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다"));

        data.remove(targetUser); // stream 탐색이 끝난 후 해당 유저를 목록에서 삭제한다.
        saveData(); // 삭제된 유저 목록을 파일에 저장한다.
    }
}