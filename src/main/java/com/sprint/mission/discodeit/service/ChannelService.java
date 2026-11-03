package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.List;
import java.util.UUID;


public interface ChannelService {

    Channel add(Channel channel);

    Channel getById(UUID id);

    List<Channel> getAll();

    Channel update(UUID id, String name);

    void delete(UUID id);


    default ChannelResponse createPublic(PublicChannelCreateRequest request) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default ChannelResponse createPrivate(PrivateChannelCreateRequest request) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default ChannelResponse find(UUID id) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default List<ChannelResponse> findAllByUserId(UUID userId) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

    default ChannelResponse update(ChannelUpdateRequest request) {
        throw new UnsupportedOperationException("지원하지 않는 기능입니다.");
    }

}