package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String password, String phoneNumber,String email);
    User read(UUID id);
    List<User> readAll();
    User update(UUID id, String username, String password, String phoneNumber, String email);
    void delete(UUID id);
}
