package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        // 유저 ID를 키로 사용하여 저장하거나 수정한다.
        data.put(user.getId(), user);

        // 저장하거나 수정한 유저를 반환한다.
        return user;
    }

    @Override
    public Optional<User> findByUserId(UUID id) {
        // ID가 일치하는 유저를 찾아 반환한다. (단건 조회)
        // 일치하는 유저가 없으면 Optional.empty()를 반환한다.
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        // data에 저장된 유저들을 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean existsByUserId(UUID id) {
        // 해당 유저의 ID가 존재하는지 확인한다.
        return data.containsKey(id);
    }

    @Override
    public void deleteByUserId(UUID id) {
        // ID가 일치하는 유저를 삭제한다. (삭제)
        User removed = data.remove(id);

        // 삭제된 유저가 없으면 예외를 발생시킨다.
        if(removed == null){
            throw new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다.");
        }
    }
}