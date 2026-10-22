package discodeit.repository;

import discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    public User save(User user);               // 저장 (생성 + 수정 겸용)
    public Optional<User> findById(UUID userId);   // 단건 조회
    public List<User> findAll();               // 전체 조회
    public void deleteById(UUID userId);
}
