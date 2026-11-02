package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserRequest;
import com.sprint.mission.discodeit.dto.response.UserResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.sprint.mission.discodeit.dto.response.UserResponse.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;

    @Override
    public UserResponse create(UserRequest newUser) {
        boolean duplicated = userRepository.findAll().stream()
                .anyMatch(other ->
                        newUser.username().equals(other.getUsername())
                                || newUser.email().equals(other.getEmail()));

        if (duplicated) {
            throw new IllegalStateException("중복된 유저명 또는 이메일");
        }

        User user = new User(newUser.username(), newUser.email(), newUser.password());
        if (newUser.profile() != null) {
            BinaryContent binaryContent = createBinaryContent(newUser);
            user.setProfileId(binaryContent.getId());
            binaryContentRepository.save(binaryContent);
        }

        User savedUser = userRepository.save(user);
        UserStatus userStatus = new UserStatus(savedUser.getId());
        userStatusRepository.save(userStatus);
        return from(savedUser, userStatus);
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저를 찾을 수 없음"));
        return from(user, getUserStatus(user));
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> from(user, getUserStatus(user)))
                .toList();
    }

    @Override
    public UserResponse update(UUID id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저를 찾을 수 없음"));

        // 변경하지 않는 필드(null)는 중복 검사에서 제외
        boolean duplicated = userRepository.findAll().stream()
                .filter(other -> !other.getId().equals(id))
                .anyMatch(other ->
                        (request.username() != null
                                && request.username().equals(other.getUsername()))
                                || (request.email() != null
                                && request.email().equals(other.getEmail())));

        if (duplicated) {
            throw new IllegalStateException("중복된 유저명 또는 이메일");
        }

        UUID oldProfileId = user.getProfileId();
        UUID newProfileId = Optional.ofNullable(request.profile())
                .map(profile -> createBinaryContent(request))
                .map(binaryContentRepository::save)
                .map(BinaryContent::getId)
                .orElse(null);

        // 새 프로필로 교체된 경우에만 기존 프로필 삭제
        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }
        user.update(request.username(), request.email(), request.password(), newProfileId);
        User updatedUser = userRepository.save(user);
        UserStatus status = getUserStatus(updatedUser);

        return from(updatedUser, status);
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저를 찾을 수 없음"));

        userRepository.deleteById(userId);
        // 프로필 이미지가 있으면 삭제
        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.findByUserId(userId)
                .ifPresent(userStatus -> userStatusRepository.deleteById(userStatus.getId()));

        readStatusRepository.findAll().stream()
                .filter(status -> userId.equals(status.getUserId()))
                .forEach(status -> readStatusRepository.deleteById(status.getId()));


    }

    private BinaryContent createBinaryContent(UserRequest request) {
        return new BinaryContent(request.profile().filename(), request.profile().size(),
                request.profile().contentType(), request.profile().bytes());
    }

    private UserStatus getUserStatus(User user) {
        return userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 id의 유저 상태를 찾을 수 없음"));
    }

}
