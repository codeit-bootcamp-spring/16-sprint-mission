package com.sprint.mission.discodeit.service.basic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

@Service // 해당 클래스를 Spring의 Service 계층 Bean으로 등록하여, Spring IoC 컨테이너가 관리하도록 한다.
@RequiredArgsConstructor // final 필드와 @NonNull 필드를 매개변수로 받는 생성자를 Lombok이 자동으로 생성하도록 한다.
public class BasicUserService implements UserService {

    // 필드
    private final UserRepository userRepository; // 데이터를 저장하고 조회하는 작업을 담당할 Repository

    @Override
    public User createUser(User user) {
        // 기존 유저 목록에서 동일한 이름을 가진 유저가 있는지 검사한다.
        boolean duplicated = userRepository.findAll().stream()
                .anyMatch(targetUser ->
                        targetUser.getName().equals(user.getName()));
        // 이름이 중복되면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 이름입니다.");
        }

        // Repository에 저장을 요청한다.
        userRepository.save(user);
        return user;
    }

    @Override
    public User readUser(UUID id) {
        // ID에 해당하는 유저를 조회하고, 없으면 예외를 발생시킨다.
        return userRepository.findByUserId(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    @Override
    public List<User> readAllUser() {
        // Repository에 전체 유저 목록 조회를 요청한다.
        return userRepository.findAll();
    }

    @Override
    public User updateUser(UUID id, String name) {
        // 기존 유저를 조회한다.
        User targetUser = readUser(id);

        // 기존 이름과 변경할 이름이 같으면 예외를 발생시킨다.
        if (targetUser.getName().equals(name)) {
            throw new IllegalArgumentException("기존 이름과 동일합니다");
        }

        // 다른 유저가 변경할 이름을 사용하고 있는지 검사한다.
        boolean duplicated = userRepository.findAll().stream()
                .anyMatch(otherUser ->
                        !otherUser.getId().equals(id)
                                && otherUser.getName().equals(name));

        // 다른 유저와 이름이 중복되면 예외를 발생시킨다.
        if (duplicated) {
            throw new IllegalArgumentException("중복된 유저 이름입니다.");
        }

        // 기존 유저의 정보를 유지하면서 변경된 이름을 가진 새 객체를 생성한다.
        User updatedUser = new User(targetUser, name);

        // 수정된 유저 객체를 저장소에 저장한다.
        userRepository.save(updatedUser);

        // 수정된 유저 객체를 반환한다.
        return updatedUser;
    }

    @Override
    public void deleteUser(UUID id) {
        // 삭제할 유저가 존재하는지 확인한다.
        readUser(id);

        // Repository에 해당 유저의 삭제를 요청한다.
        userRepository.deleteByUserId(id);
    }
}