package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;

    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    private final BinaryContentRepository binaryContentRepository;

    private final UserMapper userMapper;

    @Override
    public UserResponse create(UserCreateRequest request, MultipartFile profile)
            throws IOException {

        this.validateUniqueUsername(request.username());
        this.validateUniqueEmail(request.email());

        UUID newProfileId = null;

        if (profile != null) {
            BinaryContent profileImage = this.createProfileImage(profile);
            newProfileId = profileImage.getId();
        }

        User user = new User(request.username(),
                request.email(),
                request.password(),
                newProfileId);
        UserStatus userStatus = new UserStatus(user.getId());

        this.userRepository.save(user);
        this.userStatusRepository.save(userStatus);

        return this.userMapper.toUserResponse(user, userStatus);
    }

    private BinaryContent createProfileImage(MultipartFile profile) throws IOException {
            BinaryContent profileImage = new BinaryContent(
                    profile.getOriginalFilename(),
                    profile.getContentType(),
                    profile.getBytes());

            this.binaryContentRepository.save(profileImage);

            return profileImage;
    }

    private void validateUniqueUsername(String username) {
        boolean isUsernameTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));

        if (isUsernameTaken) {
            throw new DuplicateResourceException(
                    "이미 사용 중인 사용자 이름입니다: " + username
            );
        }
    }

    private void validateUniqueEmail(String email) {
        boolean isEmailTaken = this.userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));

        if (isEmailTaken) {
            throw new DuplicateResourceException(
                    "이미 사용 중인 이메일입니다: " + email
            );
        }
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = findUserEntity(userId);
        UserStatus userStatus = this.findUserStatus(user.getId());

        return this.userMapper.toUserResponse(user, userStatus);
    }

    private User findUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
                ));
    }

    @Override
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            UserStatus userStatus = this.userStatusRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new UserStatusNotFoundException(
                            "ID가 " + user.getId() + "인 user의 userStatus를 찾을 수 없습니다."
                    ));

            userResponses.add(this.userMapper.toUserResponse(user, userStatus));
        }

        return userResponses;
    }

    @Override
    public UserResponse update(
            UUID userId,
            UserUpdateRequest request,
            MultipartFile profile
    ) throws IOException {

        User user =this.findUserEntity(userId);

        if (!Objects.equals(request.newUsername(), user.getUsername())) {
            validateUniqueUsername(request.newUsername());
        }
        if (!Objects.equals(request.newEmail(), user.getEmail())) {
            validateUniqueEmail(request.newEmail());
        }

        UUID oldProfileId = user.getProfileId();
        UUID newProfileId = null;

        if (profile != null) {
            BinaryContent profileImage = this.createProfileImage(profile);
            newProfileId = profileImage.getId();
        }

        user.update(
                request.newUsername(),
                request.newEmail(),
                request.newPassword(),
                newProfileId
        );

        User updatedUser = userRepository.save(user);
        UserStatus userStatus = this.findUserStatus(userId);

        if (newProfileId != null && oldProfileId != null) {
            binaryContentRepository.deleteById(oldProfileId);
        }

        return this.userMapper.toUserResponse(updatedUser, userStatus);
    }

    @Override
    public void delete(UUID userId) {
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다.");
        }

        deleteMessageAttachmentsByUserId(userId);
        deleteMessagesByUserId(userId);
        deleteReadStatusesByUserId(userId);
        deleteUserStatusesByUserId(userId);
        deleteProfileImageByUserId(userId);

        this.userRepository.deleteById(userId);
    }

    private void deleteMessageAttachmentsByUserId(UUID userId) {
        List<Message> messages = this.messageRepository.findAll();
        List<List<UUID>> attachmentIdsList = messages.stream()
                .filter(m -> m.getAuthorId().equals(userId))
                .map(Message::getAttachmentIds)
                .toList();

        attachmentIdsList.stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet())
                .forEach(this.binaryContentRepository::deleteById);
    }

    private void deleteMessagesByUserId(UUID userId) {
        List<Message> messages = this.messageRepository.findAll();

        messages.stream()
                .filter(m -> m.getAuthorId().equals(userId))
                .forEach(m -> this.messageRepository.deleteById(m.getId()));
    }

    private void deleteReadStatusesByUserId(UUID userId) {
        List<ReadStatus> readStatuses = this.readStatusRepository.findAllByUserId(userId);

        readStatuses.stream()
                .filter(r -> r.getUserId().equals(userId))
                .forEach(r -> this.readStatusRepository.deleteById(r.getId()));
    }

    private void deleteUserStatusesByUserId(UUID userId) {
        UserStatus userStatus = this.findUserStatus(userId);

        this.userStatusRepository.deleteById(userStatus.getId());
    }

    private void deleteProfileImageByUserId(UUID userId) {
        User user = this.findUserEntity(userId);
        UUID profileId = user.getProfileId();
        if (profileId != null) {
            this.binaryContentRepository.deleteById(profileId);
        }
    }

    @Override
    public UserStatus findUserStatus(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(
                    "ID가 " + userId + "인 사용자를 찾을 수 없습니다."
            );
        }

        Optional<UserStatus> userStatus = this.userStatusRepository.findByUserId(userId);

        return userStatus.orElseThrow(() ->
                new UserStatusNotFoundException(
                        "사용자 ID " + userId + "에 해당하는 UserStatus를 찾을 수 없습니다."
                )
        );
    }

    public Optional<BinaryContent> findBinaryContent(UUID userId) {
        User user = this.findUserEntity(userId);
        UUID profileId = user.getProfileId();

        if (profileId == null) return Optional.empty();

        return binaryContentRepository.findById(profileId);
    }
}
