package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
    //생성
    User save(User user);
    //조회
    List<User> findId(UUID id);
    List<User> findAll();
    //유저아이디로 바로 조회 findId랑 비슷함
    List<Message> findMessagesByUserId(UUID userId);
    //수정
    User update(UUID id,String name);
    //삭제
    void deleteId(UUID id);
    //유저객체 반환
    User findEntityId(UUID id);
}
