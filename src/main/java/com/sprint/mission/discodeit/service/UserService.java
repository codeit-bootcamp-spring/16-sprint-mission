package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    User save(User user);
    //조회
    List<User> findId(UUID id);

    List<User> findAll();

    //수정
    User update(UUID id,String name);

    //삭제
    void deleteId(UUID id);
}
