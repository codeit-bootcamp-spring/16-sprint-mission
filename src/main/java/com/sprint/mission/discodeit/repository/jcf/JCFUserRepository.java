package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** UserRepository를 JCF의 Map으로 구현합니다. 저장 데이터는 실행 중 메모리에만 있습니다. */
public class JCFUserRepository implements UserRepository {

    // UUID를 키로, User 객체를 값으로 보관하는 메모리 저장소입니다.
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public void save(User user) {
        // put은 같은 ID가 이미 있으면 기존 값을 덮어씁니다.
        // 중복 ID를 거부할지 여부는 호출하는 서비스가 결정합니다.
        data.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID id) {
        // 데이터가 있으면 Optional에 담고, 없으면 Optional.empty()를 반환합니다.
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        // 내부 Map을 직접 바꿀 수 없도록 불변 목록으로 반환합니다.
        return List.copyOf(data.values());
    }

    @Override
    public boolean existsById(UUID id) {
        return data.containsKey(id);
    }

    @Override
    public boolean deleteById(UUID id) {
        // 삭제한 항목이 있었으면 true, 없었으면 false를 반환합니다.
        return data.remove(id) != null;
    }
}
