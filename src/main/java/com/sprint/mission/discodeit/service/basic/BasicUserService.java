package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public User create(UserCreateRequest request) {

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());

        UUID newProfileId = null;

        if (request.profile() != null) {
            var profile = request.profile();
            newProfileId = binaryContentRepository.save(
                    new BinaryContent(
                            profile.fileName(), profile.contentType(), profile.bytes()
                    )
            ).getId();
        }

        User user = new User(request.username(), request.email(), request.password(), newProfileId);

        UserStatus userStatus = new UserStatus(user.getId());
        this.userStatusRepository.save(userStatus);

        return userRepository.save(user);
    }

    public void validateUniqueUsername(String username) {
         boolean isUniqueEmail = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
         if (isUniqueEmail) {
             throw new IllegalArgumentException("이미 사용 중인 사용자 이름입니다: " + username);
         }
    }

    public void validateUniqueEmail(String email) {
        boolean isUniqueEmail = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (isUniqueEmail) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + email);
        }
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = this.findUserStatus(user.getId());

        return this.toResponse(user, userStatus);
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userList) {
            UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new NoSuchElementException("ID가 " + user.getId() + "인 user의 userStatus를 찾을 수 없습니다." ));
            userResponseList.add(this.toResponse(user, userStatus));
        }

        return userResponseList;
    }

    // UserController에서 이와 같은 메서드가 필요합니다.
    // 이 메서드를 public을 바꾸는 것 대신,
    // Mapper 클래스를 작성해 사용하는것이 좋습니다.
    /*
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(), user.getProfileId(),
                user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(),
                // DTO로 변환하는 것 뿐만 아니라, 사용자 상탸를 조회하는 책임까지 수행하기 때문에
                // 아래 코드를 수정하는 것이 좋습니다.
                // findUserStatus()가 Repository에 접근한다면,
                // 사용자 목록을 변환할 때 사용자마다 추가 조회가 발생할 수도 있습니다.
                this.findUserStatus(user.getId()).isOnline()
        );
    }
     */

    private UserResponse toResponse(User user, UserStatus userStatus) {
        return new UserResponse(
                user.getId(),
                user.getProfileId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                userStatus.isOnline()
        );
    }

    @Override
    public User update(UUID userId, UserUpdateRequest request) {
        this.validateUniqueUsername(request.newUsername());
        this.validateUniqueEmail(request.newEmail());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + userId + "인 사용자를 찾을 수 없습니다."));

        UUID oldProfileId = user.getProfileId();
        UUID newProfileId = null;

        if (request.profile() != null) {
            var profile = request.profile();
            newProfileId = binaryContentRepository.save(
                    new BinaryContent(
                            profile.fileName(), profile.contentType(), profile.bytes()
                    )
            ).getId();
        }

        user.update(request.newUsername(), request.newEmail(), request.newPassword(), newProfileId);

        User saved = userRepository.save(user);

        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }
        return saved;
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new NoSuchElementException("User with id: " + userId + " not found."));

        UserStatus userStatus = this.findUserStatus(userId);
        UUID profileId = user.getProfileId();

        if (profileId != null) {
            this.binaryContentRepository.deleteById(profileId);
        }
        this.userStatusRepository.deleteById(userStatus.getId());
        userRepository.deleteById(userId);
    }

    @Override
    public UserStatus findUserStatus(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found.");
        }

        Optional<UserStatus> userStatus = this.userStatusRepository.findByUserId(userId);

        return userStatus.orElseThrow(() ->
                new NoSuchElementException(
                        "사용자 ID " + userId + "에 해당하는 UserStatus를 찾을 수 없습니다."
                )
        );
    }

    public Optional<BinaryContent> findBinaryContent(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id: " + userId + " not found."));
        UUID profileId = user.getProfileId();

        return binaryContentRepository.findById(profileId);
    }
}
