package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.User.*;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserCreateResponse create(UserCreateRequest createRequest,
                                     UserProfileImageCreateRequest imageCreateRequest) {
        // TODO: 비밀번호 암호화

        String name = createRequest.name();
        String email = createRequest.email();

        this.userRepository.findByName(name).ifPresent((existingUser) -> {
            throw new IllegalArgumentException("이미 같은 이름의 유저가 존재합니다. 이름: %s".formatted(existingUser.getName()));
        });
        this.userRepository.findByEmail(email).ifPresent((existingUser) -> {
            throw new IllegalArgumentException("이미 같은 이메일의 유저가 존재합니다. 이메일: %s".formatted(existingUser.getEmail()));
        });

        User user = new User(name, email, createRequest.password());

        this.userStatusRepository.save(new UserStatus(user.getId(), null));

        if (imageCreateRequest != null) {
            BinaryContentCreateRequest binaryContentCreateRequest = imageCreateRequest.binaryContentCreateRequest();
            String fileName = binaryContentCreateRequest.fileName();
            String extension = binaryContentCreateRequest.extension();
            byte[] content = binaryContentCreateRequest.content();

            BinaryContent binaryContent = new BinaryContent(fileName, extension, content);
            user.assignProfileImage(binaryContent.getId());
            this.binaryContentRepository.save(binaryContent);
        }
        this.userRepository.save(user);

        return UserCreateResponse.from(user);
    }

    @Override
    public UserFindResponse findById(UUID id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));
        UserStatus userStatus = this.userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("유저의 접속 시간 정보가 존재하지 않습니다."));
        return UserFindResponse.of(user, userStatus.isUserOnline());
    }

    @Override
    public List<UserFindResponse> findAll() {
        return this.userRepository.findAll()
                .stream()
                .map(user -> {
                    UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId())
                            .orElseThrow(() -> new NoSuchElementException("유저의 접속 시간 정보가 존재하지 않습니다"));
                    return UserFindResponse.of(user, userStatus.isUserOnline());
                })
                .toList();
    }

    @Override
    public UserUpdateResponse update(UserUpdateRequest updateRequest, UserProfileImageUpdateRequest imageUpdateRequest) {
        UUID userId = updateRequest.userId();
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("수정할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        String newName = updateRequest.name();
        String newEmail = updateRequest.email();
        String newPassword = updateRequest.password();

        user.update(newName, newEmail, newPassword);

        if (imageUpdateRequest != null) {
            UUID oldProfileId = user.getProfileId();
            String fileName = imageUpdateRequest.fileName();
            String extension = imageUpdateRequest.extension();
            byte[] content = imageUpdateRequest.content();

            BinaryContent binaryContent = new BinaryContent(fileName, extension, content);
            this.binaryContentRepository.save(binaryContent);
            user.assignProfileImage(binaryContent.getId());

            if (oldProfileId != null) {
                this.binaryContentRepository.delete(oldProfileId);
            }
        }
        this.userRepository.update(userId, user);
        return UserUpdateResponse.from(user);
    }

    @Override
    public void delete(UUID id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 유저가 존재하지 않습니다."));
        user.validateNotDeleted();

        // 유저 삭제 관련된 엔티티 처리
        this.channelRepository.findAllByUserId(id).forEach(channel -> {
            channel.removeUser(id);
            this.channelRepository.update(channel.getId(), channel);
        });

        if (user.getProfileId() != null) {
            this.binaryContentRepository.findById(user.getProfileId())
                    .orElseThrow(() -> new IllegalStateException("삭제할 유저의 프로필 이미지가 없습니다."));
            this.binaryContentRepository.delete(user.getProfileId());
            user.assignProfileImage(null);
            this.userRepository.update(id, user);
        }

        UserStatus userStatus = this.userStatusRepository.findByUserId(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 유저의 접속 기록이 존재하지 않습니다."));
        this.userStatusRepository.delete(userStatus.getId());

        this.userRepository.delete(id);
    }
}
