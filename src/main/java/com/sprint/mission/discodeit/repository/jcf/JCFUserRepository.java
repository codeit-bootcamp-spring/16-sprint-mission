package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

public class JCFUserRepository implements UserRepository {
    private final List<User> data;

    public JCFUserRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public User save(User user) {

        if(existsByUserId(user.getId())) {
            // 같은 ID를 가진 유저의 인덱스를 찾는다.
            // 해당 유저를 찾지 못하면 예외를 발생시킨다.
            int index = IntStream.range(0, data.size())
                    .filter(i -> data.get(i).getId().equals(user.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("수정할 유저를 찾을 수 없습니다."));

            data.set(index, user); // 기존 유저를 새 유저 정보로 교체한다. (수정)
        } else {
            data.add(user); // 새로운 유저를 목록에 추가한다. (생성)
        }

        return user; // 저장하거나 수정한 유저를 반환한다.
    }

    @Override
    public Optional<User> findByUserId(UUID id) {
        // ID가 일치하는 유저를 찾아 반환한다. (단건 조회)
        // 일치하는 유저가 없으면 Optional.empty()를 반환한다.
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        // data에 저장된 유저들을 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsByUserId(UUID id) {
        // 해당 유저의 ID가 존재하는지 확인한다.
        return data.stream().anyMatch(user -> user.getId().equals(id));
    }

    @Override
    public void deleteByUserId(UUID id) {
        // ID가 일치하는 유저를 삭제한다. (삭제)
        boolean remove = data.removeIf(user -> user.getId().equals(id));

        // 삭제된 유저가 없으면 예외를 발생시킨다.
        if(!remove){
            throw new IllegalArgumentException("삭제할 유저를 찾을 수 없습니다.");
        }
    }
}