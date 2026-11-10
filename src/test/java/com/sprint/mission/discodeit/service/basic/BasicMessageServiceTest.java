package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.request.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.request.UpdateMessageRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFBinaryContentRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BasicMessageServiceTest {
    private MessageRepository messageRepository;
    private ChannelRepository channelRepository;
    private UserRepository userRepository;
    private BinaryContentRepository binaryContentRepository;
    private BasicMessageService messageService;

    private User author;
    private Channel channel;

    @BeforeEach
    void setUp() {
        messageRepository = new JCFMessageRepository();
        channelRepository = new JCFChannelRepository();
        userRepository = new JCFUserRepository();
        binaryContentRepository = new JCFBinaryContentRepository();
        messageService = new BasicMessageService(messageRepository, channelRepository, userRepository, binaryContentRepository);

        author = userRepository.save(new User("tester", "tester@test.com", "password"));
        channel = saveChannel();
    }

    private Channel saveChannel() {
        return channelRepository.save(new Channel(ChannelType.PUBLIC, "channel", "description"));
    }

    private BinaryContentRequest fileRequest() {
        byte[] bytes = {1, 2, 3};
        return new BinaryContentRequest("file.txt", (long) bytes.length, "text/plain", bytes);
    }

    private MessageResponse createMessage(UUID channelId) {
        return messageService.create(new CreateMessageRequest("hello", channelId, author.getId(), null));
    }

    @Nested
    @DisplayName("create")
    class Create {
        @Test
        @DisplayName("메시지를 생성하고 저장한다")
        void success() {
            MessageResponse response = createMessage(channel.getId());

            assertThat(response.content()).isEqualTo("hello");
            assertThat(response.channelId()).isEqualTo(channel.getId());
            assertThat(response.authorId()).isEqualTo(author.getId());
            assertThat(response.attachmentIds()).isEmpty();
            assertThat(messageRepository.existsById(response.id())).isTrue();
        }

        @Test
        @DisplayName("첨부파일을 함께 보내면 BinaryContent로 저장되고 메시지와 연결된다")
        void withAttachments() {
            CreateMessageRequest request = new CreateMessageRequest("hello", channel.getId(), author.getId(),
                    List.of(fileRequest(), fileRequest()));

            MessageResponse response = messageService.create(request);

            assertThat(response.attachmentIds()).hasSize(2);
            assertThat(response.attachmentIds()).allMatch(binaryContentRepository::existsById);
        }

        @Test
        @DisplayName("채널이 존재하지 않으면 예외가 발생한다")
        void channelNotFound() {
            assertThatThrownBy(() -> createMessage(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("작성자가 존재하지 않으면 예외가 발생한다")
        void authorNotFound() {
            CreateMessageRequest request = new CreateMessageRequest("hello", channel.getId(), UUID.randomUUID(), null);

            assertThatThrownBy(() -> messageService.create(request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("find")
    class Find {
        @Test
        @DisplayName("id로 메시지를 조회한다")
        void success() {
            MessageResponse created = createMessage(channel.getId());

            MessageResponse found = messageService.find(created.id());

            assertThat(found.id()).isEqualTo(created.id());
            assertThat(found.content()).isEqualTo("hello");
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> messageService.find(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("findAllByChannelId")
    class FindAllByChannelId {
        @Test
        @DisplayName("해당 채널의 메시지만 조회한다")
        void success() {
            MessageResponse first = createMessage(channel.getId());
            MessageResponse second = createMessage(channel.getId());
            createMessage(saveChannel().getId());

            List<MessageResponse> result = messageService.findAllByChannelId(channel.getId());

            assertThat(result).extracting(MessageResponse::id)
                    .containsExactlyInAnyOrder(first.id(), second.id());
        }

        @Test
        @DisplayName("메시지가 없으면 빈 리스트를 반환한다")
        void empty() {
            assertThat(messageService.findAllByChannelId(channel.getId())).isEmpty();
        }

        @Test
        @DisplayName("채널이 존재하지 않으면 예외가 발생한다")
        void channelNotFound() {
            assertThatThrownBy(() -> messageService.findAllByChannelId(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {
        @Test
        @DisplayName("메시지 내용을 수정한다")
        void success() {
            MessageResponse created = createMessage(channel.getId());

            MessageResponse updated = messageService.update(created.id(), new UpdateMessageRequest("updated"));

            assertThat(updated.content()).isEqualTo("updated");
            assertThat(messageService.find(created.id()).content()).isEqualTo("updated");
        }

        @Test
        @DisplayName("수정할 내용이 null이면 기존 내용을 유지한다")
        void nullContent() {
            MessageResponse created = createMessage(channel.getId());

            MessageResponse updated = messageService.update(created.id(), new UpdateMessageRequest(null));

            assertThat(updated.content()).isEqualTo("hello");
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            UpdateMessageRequest request = new UpdateMessageRequest("updated");

            assertThatThrownBy(() -> messageService.update(UUID.randomUUID(), request))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {
        @Test
        @DisplayName("메시지를 삭제하면 첨부파일도 함께 삭제된다")
        void success() {
            MessageResponse created = messageService.create(new CreateMessageRequest("hello", channel.getId(),
                    author.getId(), List.of(fileRequest())));
            UUID attachmentId = created.attachmentIds().get(0);

            messageService.delete(created.id());

            assertThat(messageRepository.existsById(created.id())).isFalse();
            assertThat(binaryContentRepository.existsById(attachmentId)).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 id면 예외가 발생한다")
        void notFound() {
            assertThatThrownBy(() -> messageService.delete(UUID.randomUUID()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
