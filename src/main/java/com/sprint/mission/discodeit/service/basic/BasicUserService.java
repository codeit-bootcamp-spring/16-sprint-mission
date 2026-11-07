package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
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

        // 일반적으로 유효성 검사를 중복 검사보다 먼저 수행합니다.
        // 이는 중복 검사가 비용이 더 크기 때문에, 유효성 검사에서 예외가 발생한다면
        // 중복 검사에서 소모하는 비용을 아낄 수 있습니다.
        /* 유효성 검증은 Spring-starter-validation을 사용합니다.
        this.validateUsername(request.username());
        this.validateEmail(request.email());
        this.validatePassword(request.password());

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());
         */

        UUID newProfileId = null;

        if (request.profile() != null) {
            // Java 10 부터 도입된 지역 변수 타입 추론입니다.
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

    /** 실제 Discord의 규칙을 적용하여 입력 받은 username이 사용가능한 지 검사합니다.
     * 길이: 최소 2자 ~ 최대 32자
     * 영문: 소문자 a-z만 허용
     * 숫자: 0-9 허용
     * 특수문자: _, .만 허용
     * 중복: 허용하지 않음
     * 연속된 마침표(..): 허용하지 않음
     * 한글: Username에는 사용할 수 없음
     */
    /*
    private void validateUsername(String username) {
        if (username == null
                || !username.matches("[a-z0-9._]{2,32}")
                || username.contains("..")) {
            throw new IllegalArgumentException("유효하지 않은 사용자 이름 형식입니다.");
        }
    }
     */

    /** 일반적으로 사용되는 email 형식을 사용하여 입력 받은 email이 사용가능한 지 검사합니다.
     * 전체 길이: 최대 254자
     * @ 앞부분: 최대 64자
     * 영문: 대소문자 허용
     * 숫자: 0~9 허용
     * 특수문자: ., _, %, +, - 허용
     * 연속된 마침표: 허용하지 않음
     * 마침표 위치: @ 앞부분의 시작과 끝에 사용 불가
     * 도메인: 연속된 마침표 및 하이픈의 잘못된 위치 차단
     * 공백 및 한글: 허용하지 않음
     */
    /*
    private void validateEmail(String email) {
        String regex =
                "^[A-Za-z0-9_%+-]+(?:\\.[A-Za-z0-9_%+-]+)*" +
                        "@[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?" +
                        "(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]{0,61}[A-Za-z0-9])?)*" +
                        "\\.[A-Za-z]{2,63}$";

        if (email == null
                || email.length() > 254
                || email.indexOf('@') > 64
                || !email.matches(regex)) {
            throw new IllegalArgumentException("유효하지 않은 이메일 형식입니다.");
        }
    }
     */

    /** 일반적으로 사용되는 password 형식을 사용하여 입력 받은 password가 사용가능한 지 검사합니다.
     * 길이: 12~64자
     * 영문: 대문자 또는 소문자 최소 1자
     * 숫자: 최소 1자
     * 특수문자: 최소 1자
     * 공백: 허용하지 않음
     */
    /*
    private void validatePassword(String password) {
        if (password == null) {
            throw new IllegalArgumentException("유효하지 않은 비밀번호 형식입니다.");
        }

        boolean hasLetter = password.matches(".*[A-Za-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[^A-Za-z0-9\\s].*");

        if (password.length() < 12
                || password.length() > 64
                || !hasLetter
                || !hasDigit
                || !hasSpecial
                || password.matches(".*\\s.*")) {
            throw new IllegalArgumentException("유효하지 않은 비밀번호입니다.");
        }
    }
     */

    private void validateUniqueUsername(String username) {
        // isUniqueUsername 을 변수명으로 사용하는 것은 옳지 않습니다.
        // isUniqueUsername 이 username이 중복이 있을 때 true가 되기 때문입니다.
        boolean isUsernameTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
        if (isUsernameTaken) {
            // IllegalStateException 대신에, 커스텀 예외인 DuplicateResourceException을 사용합니다.
            throw new DuplicateResourceException("이미 사용 중인 사용자 이름입니다: " + username);
        }
    }

    private void validateUniqueEmail(String email) {
        boolean isEmailTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
        if (isEmailTaken) {
            throw new DuplicateResourceException("이미 사용 중인 이메일입니다: " + email);
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
