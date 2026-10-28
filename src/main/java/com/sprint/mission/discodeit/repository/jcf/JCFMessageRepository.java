package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

public class JCFMessageRepository implements MessageRepository{
    private final List<Message> data;

    public JCFMessageRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public Message save(Message message) {

        if(existsByMessageId(message.getId())){
            // 같은 ID를 가진 메시지의 인덱스를 찾는다.
            // 해당 메시지를 찾지 못하면 예외를 발생시킨다.
            int index = IntStream.range(0, data.size())
                    .filter(i -> data.get(i).getId().equals(message.getId()))
                    .findFirst()
                    .orElseThrow(() ->
                            new IllegalArgumentException("수정할 메시지를 찾을 수 없습니다."));

            data.set(index, message); // 기존 메시지 객체를 새 메시지 객체로 교체한다. (수정)
        } else {
            data.add(message); // 새로운 메시지를 목록에 추가한다. (생성)
        }

        return message; // 저장하거나 수정한 메시지 내용을 반환한다.
    }

    @Override
    public Optional<Message> findByMessageId(UUID id) {
        // ID가 일치하는 메시지를 찾아 반환한다. (단건 조회)
        // 일치하는 메시지가 없으면 Optional.empty()를 반환한다.
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        // data에 저장된 메시지를 담은 새 목록을 반환한다. (전체 조회)
        return new ArrayList<>(data);
    }

    @Override
    public boolean existsByMessageId(UUID id) {
        // 해당 메시지의 ID가 존재하는지 확인한다.
        return data.stream().anyMatch(message -> message.getId().equals(id));
    }

    @Override
    public void deleteByMessageId(UUID id) {
        // ID가 일치하는 메시지를 삭제한다. (삭제)
        boolean remove = data.removeIf( message -> message.getId().equals(id));

        // 삭제된 메시지가 없으면 예외를 발생시킨다.
        if(!remove){
            throw new IllegalArgumentException("삭제할 메시지를 찾을 수 없습니다.");
        }
    }
}