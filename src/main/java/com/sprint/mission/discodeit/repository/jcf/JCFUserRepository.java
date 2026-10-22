package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserRepository implements UserRepository {
    final Map<UUID, User> data = new ConcurrentHashMap<>();

    @Override
    public void save(User user) {data.put(user.getId(),user);}

    @Override
    public User findId(UUID id) {return data.get(id);}

    @Override
    public List<User> findAll() {return new ArrayList<>(data.values());}

    @Override
    public void deleteId(UUID id) {data.remove(id);}
}
