package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;


    @Override
    public User create(UserCreateRequest request, BinaryContentRequest profile) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<User> users = entities.getUsers();


        if(users.stream().anyMatch(u->u.getName().equals(request.name()))) {
            throw new IllegalArgumentException("사용 중인 이름");
        }

        if(users.stream().anyMatch(u -> u.getEmail().equals(request.email()))){
            throw new IllegalArgumentException("사용 중인 이메일");
        }


        User user = new User(request.name(),request.password(),request.email());
        users.add(user);

        if(profile != null) {
            BinaryContent binaryContent = new BinaryContent(profile.fileName(),profile.contentType(), profile.bytes());
            binaryContentRepository.save(binaryContent);
            user.updateProfile(binaryContent.getId());
        }


        entities.saveAll();

        UserStatus userStatus = new UserStatus(user.getId(),Instant.now());
        userStatusRepository.save(userStatus);

        return user;
    }

    @Override
    public UserDto findById(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);


        User user = entities.getUsers().stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));
        UserStatus userStatus = userStatusRepository.findAll().stream().filter(s->s.getUserId().equals(id)).findFirst().orElseThrow(()->
            new IllegalArgumentException("접속 상태를 찾을 수 없음"));
        return new UserDto(user.getId(),user.getName(),user.getEmail(),user.getProfileId(),userStatus.isOnline());
    }

    @Override
    public List<UserDto> findAll() {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<UserDto> userDtos = new ArrayList<>();
        List<UserStatus> userStatuses = userStatusRepository.findAll();;

        for(User user : entities.getUsers()) {
            UserStatus userStatus = userStatuses.stream().filter(s->s.getUserId().equals(user.getId())).findFirst()
                    .orElseThrow(()->new IllegalArgumentException("접속상태를 찾을 수 없음"));
            UserDto userDto = new UserDto(
                    user.getId(),user.getName(),user.getEmail(),user.getProfileId(),userStatus.isOnline());

            userDtos.add(userDto);
        }
        return userDtos;   //여기해야됨
    }

    @Override
    public User update(UserUpdateRequest request, BinaryContentRequest profile) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<User> users = entities.getUsers();

        User user = users.stream().filter(u -> u.getId().equals(request.userId())).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(users.stream().anyMatch(otherUser ->
                !otherUser.getId().equals(request.userId())&&otherUser.getEmail().equals(request.email()))) {
            throw new IllegalArgumentException("사용 중인 이메일");
        }
        if(users.stream().anyMatch(otherUser ->
                !otherUser.getId().equals(request.userId())&&otherUser.getName().equals(request.name()))) {
            throw new IllegalArgumentException("사용 중인 이름");
        }

        user.update(request.name(), request.password(), request.email());

        if(profile != null) {
            BinaryContent binaryContent = new BinaryContent(profile.fileName(),profile.contentType(), profile.bytes());
            binaryContentRepository.save(binaryContent);
            UUID oldProfileId = user.getProfileId();
            user.updateProfile(binaryContent.getId());
            if(oldProfileId!=null) {
                binaryContentRepository.deleteById(oldProfileId);
            }
        }

        entities.saveAll();
        return user;
    }

    @Override
    public void delete(UUID id) {
        BasicEntityService entities = new BasicEntityService(userRepository, channelRepository, messageRepository);
        List<User> users = entities.getUsers();

        User user = users.stream().filter(u -> u.getId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("사용자를 찾을 수 없음"));

        if(user.getProfileId()!=null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        UserStatus userStatus = userStatusRepository.findAll().stream().filter(s->s.getUserId().equals(id)).findFirst().orElseThrow(() ->
                new IllegalArgumentException("접속 상태를 찾을 수 없음"));
        userStatusRepository.deleteById(userStatus.getId());

    users.remove(user);
        entities.saveAll();
    }
}