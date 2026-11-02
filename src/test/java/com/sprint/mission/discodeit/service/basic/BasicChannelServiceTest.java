package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.request.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.request.UpdateChannelRequest;
import com.sprint.mission.discodeit.dto.response.ChannelResponse;
import com.sprint.mission.discodeit.dto.response.CreatePrivateChannelResponse;
import com.sprint.mission.discodeit.dto.response.FindChannelResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFReadStatusRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicChannelServiceTest {
    private ChannelRepository channelRepository;
    private ReadStatusRepository readStatusRepository;
    private MessageRepository messageRepository;
    private BinaryContentRepository binaryContentRepository;
    private UserRepository userRepository;
    private BasicChannelService channelService;

    @BeforeEach
    void setUp() {
        channelRepository = new JCFChannelRepository();
        readStatusRepository = new JCFReadStatusRepository();
        messageRepository = new JCFMessageRepository();
        binaryContentRepository = new JCFBinaryContentRepository();
        userRepository = new JCFUserRepository();
        channelService = new BasicChannelService(channelRepository, readStatusRepository,
                messageRepository, binaryContentRepository, userRepository);
    }

    private User saveUser() {
        return userRepository.save(new User("tester", "tester@test.com", "password"));
    }

    private ChannelResponse createPublicChannel() {
        return channelService.createPublicChannel(new CreatePublicChannelRequest("public", "description"));
    }

    private CreatePrivateChannelResponse createPrivateChannel(UUID... userIds) {
        return channelService.createPrivateChannel(new CreatePrivateChannelRequest(Arrays.asList(userIds)));
    }

    private Message saveMessage(UUID channelId, List<UUID> attachmentIds) {
        return messageRepository.save(new Message("hello", channelId, UUID.randomUUID(), attachmentIds));
    }

    @Nested
    @DisplayName("createPublicChannel")
    class CreatePublicChannel {
        @Test
        @DisplayName("PUBLIC 채널을 생성하고 저장한다")
        void success() {
            ChannelResponse response = createPublicChannel();

            assertThat(response.channelType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(response.name()).isEqualTo("public");
            assertThat(response.description()).isEqualTo("description");
            assertThat(channelRepository.existsById(response.id())).isTrue();
        }
    }

    @Nested
    @DisplayName("createPrivateChannel")
    class CreatePrivateChannel {
        @Test
        @DisplayName("PRIVATE 채널을 생성하고 참여자마다 ReadStatus를 만든다")
        void success() {
            User first = saveUser();
            User second = saveUser();

            CreatePrivateChannelResponse response = createPrivateChannel(first.getId(), second.getId());

            assertThat(response.channelType()).isEqualTo(ChannelType.PRIVATE);
            assertThat(channelRepository.existsById(response.id())).isTrue();
            assertThat(readStatusRepository.findAll())
                    .filteredOn(status -> response.id().equals(status.getChannelId()))
                    .extracting(ReadStatus::getUserId)
                    .containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("같은 참여자가 여러 번 들어오면 한 번만 등록한다")
        void duplicatedParticipant() {
            User user = saveUser();

            CreatePrivateChannelResponse response = createPrivateChannel(user.getId(), user.getId());

            assertThat(readStatusRepository.findAll())
                    .filteredOn(status -> response.id().equals(status.getChannelId()))
                    .hasSize(1);
        }

        @Test
        @DisplayName("참여자 목록이 비어 있으면 예외가 발생한다")
        void emptyParticipants() {
            assertThatThrownBy(() -> createPrivateChannel())
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("참여자 목록이 null이면 예외가 발생한다")
        void nullParticipants() {
            assertThatThrownBy(() -> channelService.createPrivateChannel(new CreatePrivateChannelRequest(null)))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("참여자 목록에 null이 섞여 있으면 예외가 발생한다")
        void nullInParticipants() {
            User user = saveUser();

            assertThatThrownBy(() -> createPrivateChannel(user.getId(), null))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("존재하지 않는 유저가 있으면 예외가 발생하고 채널을 만들지 않는다")
        void userNotFound() {
            User user = saveUser();

            assertThatThrownBy(() -> createPrivateChannel(user.getId(), UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
            assertThat(channelRepository.findAll()).isEmpty();
            assertThat(readStatusRepository.findAll()).isEmpty();
        }
    }

    @Nested
    @DisplayName("find")
    class Find {
        @Test
        @DisplayName("PUBLIC 채널은 참여자 목록 없이 조회된다")
        void publicChannel() {
            ChannelResponse created = createPublicChannel();

            FindChannelResponse found = channelService.find(created.id());

            assertThat(found.id()).isEqualTo(created.id());
            assertThat(found.channelType()).isEqualTo(ChannelType.PUBLIC);
            assertThat(found.participantIds()).isNull();
        }

        @Test
        @DisplayName("PRIVATE 채널은 참여자 목록과 함께 조회된다")
        void privateChannel() {
            User first = saveUser();
            User second = saveUser();
            CreatePrivateChannelResponse created = createPrivateChannel(first.getId(), second.getId());

            FindChannelResponse found = channelService.find(created.id());

            assertThat(found.participantIds()).containsExactlyInAnyOrder(first.getId(), second.getId());
        }

        @Test
        @DisplayName("메시지가 없으면 마지막 메시지 시간은 null이다")
        void noMessage() {
            ChannelResponse created = createPublicChannel();

            assertThat(channelService.find(created.id()).lastMessageAt()).isNull();
        }

        @Test
        @DisplayName("마지막 메시지 시간은 해당 채널의 가장 최근 메시지 시간이다")
        void lastMessageAt() throws InterruptedException {
            ChannelResponse created = createPublicChannel();
            saveMessage(created.id(), new ArrayList<>());
            Thread.sleep(5);
            Message latest = saveMessage(created.id(), new ArrayList<>());

            assertThat(channelService.find(created.id()).lastMessageAt()).isEqualTo(latest.getCreatedAt());
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> channelService.find(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("findAllByUserId")
    class FindAllByUserId {
        @Test
        @DisplayName("모든 PUBLIC 채널과 유저가 참여한 PRIVATE 채널만 조회한다")
        void success() {
            User user = saveUser();
            User other = saveUser();
            ChannelResponse publicChannel = createPublicChannel();
            CreatePrivateChannelResponse joined = createPrivateChannel(user.getId(), other.getId());
            createPrivateChannel(other.getId());

            List<FindChannelResponse> result = channelService.findAllByUserId(user.getId());

            assertThat(result).extracting(FindChannelResponse::id)
                    .containsExactlyInAnyOrder(publicChannel.id(), joined.id());
        }

        @Test
        @DisplayName("PUBLIC 채널에 ReadStatus가 있어도 중복으로 조회되지 않는다")
        void publicChannelWithReadStatus() {
            User user = saveUser();
            ChannelResponse publicChannel = createPublicChannel();
            readStatusRepository.save(new ReadStatus(user.getId(), publicChannel.id()));

            List<FindChannelResponse> result = channelService.findAllByUserId(user.getId());

            assertThat(result).extracting(FindChannelResponse::id)
                    .containsExactly(publicChannel.id());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("PUBLIC 채널의 이름과 설명을 수정한다")
        void success() {
            ChannelResponse created = createPublicChannel();

            ChannelResponse updated = channelService.update(new UpdateChannelRequest(created.id(), "newName", "newDescription"));

            assertThat(updated.name()).isEqualTo("newName");
            assertThat(updated.description()).isEqualTo("newDescription");
            assertThat(channelRepository.findById(created.id()).orElseThrow().getName()).isEqualTo("newName");
        }

        @Test
        @DisplayName("PRIVATE 채널은 수정할 수 없다")
        void privateChannel() {
            CreatePrivateChannelResponse created = createPrivateChannel(saveUser().getId());
            UpdateChannelRequest request = new UpdateChannelRequest(created.id(), "newName", "newDescription");

            assertThatThrownBy(() -> channelService.update(request))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            UpdateChannelRequest request = new UpdateChannelRequest(UUID.randomUUID(), "newName", "newDescription");

            assertThatThrownBy(() -> channelService.update(request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("채널을 삭제하면 ReadStatus, 메시지, 첨부파일도 함께 삭제된다")
        void success() {
            User user = saveUser();
            CreatePrivateChannelResponse created = createPrivateChannel(user.getId());
            BinaryContent attachment = binaryContentRepository.save(
                    new BinaryContent("file.txt", 1L, "text/plain", new byte[]{1}));
            Message message = saveMessage(created.id(), new ArrayList<>(List.of(attachment.getId())));

            channelService.delete(created.id());

            assertThat(channelRepository.existsById(created.id())).isFalse();
            assertThat(readStatusRepository.findAll()).isEmpty();
            assertThat(messageRepository.existsById(message.getId())).isFalse();
            assertThat(binaryContentRepository.existsById(attachment.getId())).isFalse();
        }

        @Test
        @DisplayName("다른 채널의 메시지와 ReadStatus는 삭제하지 않는다")
        void keepOthers() {
            User user = saveUser();
            CreatePrivateChannelResponse target = createPrivateChannel(user.getId());
            CreatePrivateChannelResponse other = createPrivateChannel(user.getId());
            Message otherMessage = saveMessage(other.id(), new ArrayList<>());

            channelService.delete(target.id());

            assertThat(channelRepository.existsById(other.id())).isTrue();
            assertThat(messageRepository.existsById(otherMessage.getId())).isTrue();
            assertThat(readStatusRepository.findAll())
                    .extracting(ReadStatus::getChannelId)
                    .containsExactly(other.id());
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> channelService.delete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
