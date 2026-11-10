package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusListResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userstatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    // UserStatus 객체를 DTO로 매핑하는 매퍼를, 이 클래스 내부에 정의하지않고 외부에서 가져오는 구조로 변경합니다.
    private final UserStatusMapper userStatusMapper;

    /**
     * 사용자 상태 정보 생성 요청 DTO를 사용하여, 사용자 상태 정보를 생성합니다
     *
     * <p>처리 과정:
     * <ol>
     *     <li>유효한 사용자 ID 인지 확인합니다</li>
     *     <li>사용자 ID를 사용하여, 사용자 상태 정보를 가져옵니다.</li>
     *     <li>사용자 상태 정보를 저장합니다</li>
     *     <li>사용자 상태 정보를 DTO로 매핑해 반환합니다.</li>
     * </ol>
     */
    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        this.userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException("ID가 " + request.userId() + "인 사용자를 찾을 수 없습니다."));

        UserStatus userStatus = this.userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new DuplicateResourceException(
                    "해당 사용자의 사용자 상태 정보가 이미 존재합니다. 사용자 ID: " + request.userId()
            ));

        this.userStatusRepository.save(userStatus);

        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }

    /**
     * 사용자 상태 정보 ID를 기준으로, 일치하는 사용자 상태 정보를 반환합니다.
     *
     * <p>처리 과정:
     * <ol>
     *     <li>사용자 상태 정보 ID를 사용하여, 일치하는 사용자 상태 정보를 가져옵니다.</li>
     *     <li>이 사용자 상태 정보를 DTO로 매핑하여 반환합니다.</li>
     * </ol>
     */
    @Override
    public UserStatusResponse find(UUID userStatusId) {
        UserStatus userStatus = this.userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                        "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다."
                ));

        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }

    /**
     * 모든 사용자 상태 정보를 반환합니다.
     *
     * <p>처리 과정:
     * <ol>
     *     <li>모든 사용자 상태 정보를 가져와서 리스트에 저장합니다.</li>
     *     <li>매퍼를 사용하여 리스트의 모든 사용자 상태 정보를 DTO로 매핑합니다.</li>
     *     <li>이 리스트를 다시 DTO에 넣은 뒤 반환합니다.</li>
     * </ol>
     * <p>추가 설명:
     * 서비스 계층에서는 DTO를 반환하는게 권장 되기 때문에 반환타입을 List<UserStatusResponse> 에서
     * UserStatusListResponse로 변경합니다. DTO에 DTO들을 넣어서 반환하는 방식은 실무에서도 흔히 쓰입니다.
     */
    @Override
    public UserStatusListResponse findAll() {
        List<UserStatus> userStatuses = this.userStatusRepository.findAll();

        List<UserStatusResponse> userStatusResponses = userStatuses.stream()
                .map(this.userStatusMapper::toUserStatusResponse)
                .toList();

        return new UserStatusListResponse(userStatusResponses);
    }

    /**
     * 사용자 상태 정보 ID와 사용자 상태 정보 요청 DTO를 기준으로,
     * 사용자 상태 정보를 업데이트합니다.
     * <p>처리 과정:
     * <ol>
     *     <li>사용자 상태 정보를 가져옵니다.</li>
     *     <li>사용자 상태 정보를 업데이트합니다.</li>
     *     <li>사용자 상태 정보를 저장합니다.</li>
     *     <li>사용자 상태 정보를 DTO로 매핑하여 반환합니다.</li>
     * </ol>
     * <p>추가 설명: 수정 요청에서 DTO에 ID를 포함하지 않는 이유는,
     * ID는 보통 요청 API 엔드포인트에 전달되기 때문입니다.
     */
    @Override
    public UserStatusResponse update(UUID userStatusId, UserStatusUpdateRequest request) {
        UserStatus userStatus = this.userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new UserStatusNotFoundException(
                                "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다."
                        ));

        userStatus.update();
        this.userStatusRepository.save(userStatus);

        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }

    /**
     * 사용자 ID를 기준으로, UserStatus를 업데이트합니다. 그 후,
     * UserStatus의 필드 값의 일부를 UserStatusResponse DTO에 저장하여 반환합니다.
     *
     * <p>처리 과정:
     * <ol>
     *     <li>유효한 사용자인지 확인합니다.</li>
     *     <li>사용자 상태 정보를 가져옵니다.</li>
     *     <li>사용자 상태 정보를 업데이트합니다.</li>
     *     <li>사용자 상태 정보를 저장합니다.</li>
     *     <li>사용자 상테 정보중 일부의 필드를 DTO에 담아 반환합니다.</li>
     * </ol>
     * <p>추가 설명: 만약 저장소가 JCF 컬렉션에 객체라면, save()를 호출할 필요가 없습니다
     * 참조 변수는 변수들이 동일한 객체를 참조하기 때문에, 변경 내역이 공유됩니다.
     * 하지만 로컬 디렉터리에 저장된다면, 참조 변수처럼 공유되지못하기 때문에
     * save()를 호출하여 변경 내역을 로컬 디렉터리의 저장된 파일에 저장하여야 합니다.
     * <p>추가 설명: JPA의 영속성 컨텍스트는 엔티티 객체의 변경 사항을 추적하여,
     * 변경 내역을 DB에 반영할 수 있습니다. 때문에 명시적으로 save()를 호출하지 않아도
     * DB에 변경 사항이 반영됩니다. 이 조건으로는,
     * 1. 엔티티가 영속성 컨텍스트의 관리대상이여야하고
     * 2. 쓰기 가능한 트랜잭션 내에서 변경되고
     * 3. 커밋(DB에 변경 사항을 최종 확정)되어야합니다.
     * @param userId 업데이트할 사용자 UUID
     * @return Mapper를 사용하여, userStatus객체의 필드 값을 추출하여 DTO에 전달한 후 반환합니다.
     */
    @Override
    public UserStatusResponse updateByUserId(UUID userId) {
        // User 객체를 가져올 필요가 없으므로, findById() 대신 existsById()를 사용히야 User 객체의
        // 존재 여부만 확인합니다.
        if (!this.userRepository.existsById(userId)) {
            throw new UserNotFoundException("ID가 " + userId + "인 사용자를 찾을 수 없습니다.");
        }
        UserStatus userStatus = this.userStatusRepository.findByUserId(userId)
                        .orElseThrow(() -> new UserStatusNotFoundException(
                                        "사용자 ID가 " + userId + "인 사용자의 상태 정보를 찾을 수 없습니다."
                                ));

        userStatus.update();
        this.userStatusRepository.save(userStatus);
        // 이 클래스 내부에 정의된 매퍼를 더이상 사용하지 않고, 외부의 매퍼 클래스의 매퍼를 사용합니다.
        return this.userStatusMapper.toUserStatusResponse(userStatus);
    }

    /**
     * 사용자 상태 정보 ID를 기준으로, 사용자 상태 정보를 삭제합니다.
     *
     * <p>처리 과정:
     * <ol>
     *     <li>사용자 상태의 존재 여부를 확인합니다.</li>
     *     <li>존재하지 않는다면, 예외를 발생 시킵니다.</li>
     *     <li>존재한다면 사용자 상태 정보를 삭제합니다.</li>
     * </ol>
     * <p>추가 설명:
     * 삭제 대상이 없을 때도, 예외를 발생시키는 정책을 적용합니다.
     * <p>추가 설명: SQL DELETE에서, 삭제할 대상이 없는 것을 삭제하려 할 때는,
     * 기본적으로 예외를 발생시키지않습니다.
     * 하지만 이 애플리케이션의 비즈니스 로직에서는 이를 예외로 규정하여,
     * 예외를 발생시켜, 클라이언트에 대상이 존재하지 않아 삭제할 수 없다는 것을 알립니다.
     * <p> 추가 설명: delete() 메서드의 반환값이 없는 이유는
     * 해당 대상이 삭제되었기 때문에,
     * 삭제한 객체를 반환하여 다시 사용할 일이 없기 때문입니다.
     *
     * @param userStatusId 삭제할 사용자 상태 정보 UUID
     * @throws UserStatusNotFoundException 사용자가 존재하지 않는 경우
     */
    @Override
    public void delete(UUID userStatusId) {
        if (this.userStatusRepository.existsById(userStatusId)) {
            throw new UserStatusNotFoundException(
                    "ID가 " + userStatusId + "인 사용자 상태 정보를 찾을 수 없습니다.");
        }
        this.userStatusRepository.deleteById(userStatusId);
    }

}
