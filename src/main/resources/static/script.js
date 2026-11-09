// API 주소
const API_BASE_URL = '/api';

const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

// 기본 프로필 이미지 (외부 파일 필요 없음)
const DEFAULT_AVATAR = `data:image/svg+xml,${encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" viewBox="0 0 80 80"><rect width="80" height="80" rx="40" fill="#e5e7eb"/><circle cx="40" cy="29" r="14" fill="#9ca3af"/><path d="M15 72c0-16 11-26 25-26s25 10 25 26" fill="#9ca3af"/></svg>'
)}`;

// 페이지가 열리면 사용자 목록 조회
document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

// 사용자 목록 API 호출
async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);

        if (!response.ok) {
            throw new Error('사용자 목록 조회 실패');
        }

        const users = await response.json();

        renderUserList(users);

    } catch (error) {
        console.error('사용자 목록 조회 오류:', error);
    }
}

// 프로필 이미지 조회
async function fetchUserProfile(profileId) {
    if (!profileId) {
        return DEFAULT_AVATAR;
    }

    try {
        const response = await fetch(
            `${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`
        );

        if (!response.ok) {
            throw new Error('프로필 이미지 조회 실패');
        }

        const profile = await response.json();

        // 이미지 데이터가 없으면 기본 이미지 사용
        if (!profile.bytes || !profile.contentType) {
            return DEFAULT_AVATAR;
        }

        // Base64 이미지 데이터 변환
        return `data:${profile.contentType};base64,${profile.bytes}`;

    } catch (error) {
        console.error('프로필 이미지 조회 오류:', error);

        return DEFAULT_AVATAR;
    }
}

// 사용자 목록 화면 출력
async function renderUserList(users) {
    const userListElement = document.getElementById('userList');

    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');

        userElement.className = 'user-item';

        // 프로필 이미지 가져오기
        const profileUrl = await fetchUserProfile(user.profileId);

        userElement.innerHTML = `
            <img
                src="${profileUrl}"
                alt="${user.username}"
                class="user-avatar"
            >

            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>

            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        // 이미지 로딩 실패 시 기본 이미지 표시
        const avatar = userElement.querySelector('.user-avatar');

        avatar.onerror = () => {
            avatar.onerror = null;
            avatar.src = DEFAULT_AVATAR;
        };

        userListElement.appendChild(userElement);
    }
}
