package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.request.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.dto.response.ReadStatusResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicReadStatusServiceTest {
    private ReadStatusRepository readStatusRepository;
    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private BasicReadStatusService readStatusService;

    @BeforeEach
    void setUp() {
        readStatusRepository = new JCFReadStatusRepository();
        userRepository = new JCFUserRepository();
        channelRepository = new JCFChannelRepository();
        readStatusService = new BasicReadStatusService(readStatusRepository, userRepository, channelRepository);
    }

    private User saveUser() {
        return userRepository.save(new User("tester", "tester@test.com", "password"));
    }

    private Channel saveChannel() {
        return channelRepository.save(new Channel(ChannelType.PUBLIC, "channel", "description"));
    }

    private ReadStatusResponse createReadStatus(User user, Channel channel) {
        return readStatusService.create(new CreateReadStatusRequest(user.getId(), channel.getId()));
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("유저와 채널이 존재하면 ReadStatus를 생성하고 저장한다")
        void success() {
            User user = saveUser();
            Channel channel = saveChannel();

            ReadStatusResponse response = createReadStatus(user, channel);

            assertThat(response.userId()).isEqualTo(user.getId());
            assertThat(response.channelId()).isEqualTo(channel.getId());
            assertThat(readStatusRepository.existsById(response.id())).isTrue();
        }

        @Test
        @DisplayName("id가 null이면 예외가 발생한다")
        void nullId() {
            CreateReadStatusRequest request = new CreateReadStatusRequest(null, saveChannel().getId());

            assertThatThrownBy(() -> readStatusService.create(request))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 예외가 발생한다")
        void userNotFound() {
            CreateReadStatusRequest request = new CreateReadStatusRequest(UUID.randomUUID(), saveChannel().getId());

            assertThatThrownBy(() -> readStatusService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("채널이 존재하지 않으면 예외가 발생한다")
        void channelNotFound() {
            CreateReadStatusRequest request = new CreateReadStatusRequest(saveUser().getId(), UUID.randomUUID());

            assertThatThrownBy(() -> readStatusService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("같은 유저와 채널의 ReadStatus가 이미 존재하면 예외가 발생한다")
        void duplicated() {
            User user = saveUser();
            Channel channel = saveChannel();
            createReadStatus(user, channel);

            assertThatThrownBy(() -> createReadStatus(user, channel))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("find")
    class Find {
        @Test
        @DisplayName("id로 ReadStatus를 조회한다")
        void success() {
            ReadStatusResponse created = createReadStatus(saveUser(), saveChannel());

            ReadStatusResponse found = readStatusService.find(created.id());

            assertThat(found.id()).isEqualTo(created.id());
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> readStatusService.find(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("findAllByUserId")
    class FindAllByUserId {
        @Test
        @DisplayName("해당 유저의 ReadStatus만 조회한다")
        void success() {
            User user = saveUser();
            ReadStatusResponse first = createReadStatus(user, saveChannel());
            ReadStatusResponse second = createReadStatus(user, saveChannel());
            createReadStatus(saveUser(), saveChannel());

            List<ReadStatusResponse> result = readStatusService.findAllByUserId(user.getId());

            assertThat(result).extracting(ReadStatusResponse::id)
                    .containsExactlyInAnyOrder(first.id(), second.id());
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 예외가 발생한다")
        void userNotFound() {
            assertThatThrownBy(() -> readStatusService.findAllByUserId(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("마지막으로 읽은 시간을 수정한다")
        void success() {
            ReadStatusResponse created = createReadStatus(saveUser(), saveChannel());
            Instant newLastReadAt = Instant.now().minusSeconds(600);

            ReadStatusResponse updated = readStatusService.update(new UpdateReadStatusRequest(created.id(), newLastReadAt));

            assertThat(updated.lastReadAt()).isEqualTo(newLastReadAt);
            assertThat(readStatusService.find(created.id()).lastReadAt()).isEqualTo(newLastReadAt);
        }

        @Test
        @DisplayName("수정할 값이 null이면 예외가 발생한다")
        void nullValue() {
            ReadStatusResponse created = createReadStatus(saveUser(), saveChannel());

            assertThatThrownBy(() -> readStatusService.update(new UpdateReadStatusRequest(created.id(), null)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("수정할 시간이 현재보다 이후면 예외가 발생한다")
        void futureValue() {
            ReadStatusResponse created = createReadStatus(saveUser(), saveChannel());
            Instant future = Instant.now().plusSeconds(600);

            assertThatThrownBy(() -> readStatusService.update(new UpdateReadStatusRequest(created.id(), future)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            UpdateReadStatusRequest request = new UpdateReadStatusRequest(UUID.randomUUID(), Instant.now());

            assertThatThrownBy(() -> readStatusService.update(request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("id로 ReadStatus를 삭제한다")
        void success() {
            ReadStatusResponse created = createReadStatus(saveUser(), saveChannel());

            readStatusService.delete(created.id());

            assertThat(readStatusRepository.existsById(created.id())).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> readStatusService.delete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
