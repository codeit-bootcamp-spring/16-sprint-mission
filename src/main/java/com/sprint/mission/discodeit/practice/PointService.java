package com.sprint.mission.discodeit.practice;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserRepository userRepository;

    @Transactional
    public void charge(Long userId, int amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // TODO: 엔티티의 메서드를 호출하여 충전 로직을 실행하세요.
        // user.????(amount);

    }
}