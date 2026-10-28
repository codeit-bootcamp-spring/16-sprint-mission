package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    // 생성자 초기화
    public JCFUserService(){
        this.data = new ArrayList<>();
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

        // stream 탐색이 끝난 후 해당 유저를 목록에서 삭제한다.
        data.remove(targetUser);
    }
}