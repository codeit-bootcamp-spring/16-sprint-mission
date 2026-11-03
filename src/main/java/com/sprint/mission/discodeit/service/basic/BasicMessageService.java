package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(MessageCreateDto dto) {
        if (!channelRepository.existsById(dto.channelId())) { //
            throw new NoSuchElementException("Channel not found with id " + dto.channelId()); //
        }
        if (!userRepository.existsById(dto.authorId())) { //
            throw new NoSuchElementException("Author not found with id " + dto.authorId()); //
        }

        // 첨부파일이 전달되었다면 유효한 바이너리 콘텐츠 ID인지 확인
        if (dto.attachmentIds() != null && !dto.attachmentIds().isEmpty()) {
            // 저장소에 실존하는 파일 ID 개수와 요청받은 ID 개수가 같은지 확인
            int existingFilesCount = binaryContentRepository.findByIdIn(dto.attachmentIds()).size();
            if (existingFilesCount != dto.attachmentIds().size()) {
                throw new IllegalArgumentException("존재하지 않거나 유효하지 않은 첨부파일 ID가 포함되어 있습니다.");
            }
        }

        Message message = new Message(dto.content(), dto.channelId(), dto.authorId(), dto.attachmentIds());
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        //존재하지 않는 채널의 메시지를 조회하려고 하면 예외를 발생
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel not found with id " + channelId);
        }

        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(MessageUpdateDto dto) {
        Message message = messageRepository.findById(dto.id())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + dto.id() + " not found"));

        //값 수정 및 저장
        message.update(dto.content());
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        //삭제 대상 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        // 해당 메시지에 첨부파일(BinaryContent) 전체 삭제
        // 메세지 도메인 내부에 선언된 attachmentIds 리스트를 순회하며 삭제 처리
        if (message.getAttachmentIds() != null) {
            for (UUID binaryId : message.getAttachmentIds()) {
                binaryContentRepository.deleteById(binaryId);
            }
        }
        //최종 대상 메시지 삭제
        messageRepository.deleteById(messageId);
    }
}
