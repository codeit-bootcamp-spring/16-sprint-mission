package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final List<User> data;

    public JCFUserService(){
        this.data = new ArrayList<>();
    }

    @Override
    public User createUser(User user) {
        data.add(user); // 전달받을 유저를 목록에 추가한다.
        return user; // 추가한 유저 객체를 반환한다.
    }

    @Override
    public User readUser(UUID id) {
        for(User user : data){
            if(user.getId().equals(id)){
                // 해당 유저 id를 가져와 요청받은 id와 일치하면 user 를 반환한다.
                return user;
            }
        }
        return null; // 해당하는 user id를 찾을 수 없으면 null 로 반환한다.
    }

    @Override
    public List<User> readAllUser() {
        // return List.of();
        // data 에 저장된 유저들을 담은 새 목록을 반환한다.
        return new ArrayList<>(data);
    }

    @Override
    public User updateUser(UUID id, User user) {
        for(User targetUser : data){
            if(targetUser.getId().equals(id)){
                // 찾고자 하는 유저 id가 맞다면 index 에 대입.
                int index = data.indexOf(targetUser);
                // 해당 인덱스의 유저 객체를 수정할 user 객체로 교체한다.
                data.set(index, user);
                // 그리고 수정한 user 객체를 반환한다.
                return user;
            }
        }
        return null; // 수정할 유저 id를 찾을 수 없으면 null 로 반환한다.
    }

    @Override
    public User deleteUser(UUID id) {
        for(User user : data){
            if (user.getId().equals(id)){
                // 찾은 user 목록에서 제거한 후 반환한다.
                data.remove(user);
                return user;
            }
        }
        return null; // 삭제할 user id를 찾을수 없으면 null 로 반환한다.
    }
}