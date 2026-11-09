const API_BASE_URL = '/api';

const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

document.addEventListener('DOMContentLoaded', () => {
    const address = document.getElementById('browserUrl');

    if (address) {
        address.textContent = window.location.origin;
    }

    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    const userListElement = document.getElementById('userList');

    showMessage(
        userListElement,
        '사용자 목록을 불러오는 중입니다.'
    );

    try {
        const response = await fetch(ENDPOINTS.USERS, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error(
                `사용자 조회 실패: HTTP ${response.status}`
            );
        }

        const users = await response.json();

        if (!Array.isArray(users)) {
            throw new Error(
                '사용자 목록 응답 형식이 올바르지 않습니다.'
            );
        }

        renderUserList(userListElement, users);

    } catch (error) {
        console.error(
            '사용자 목록을 불러오지 못했습니다.',
            error
        );

        showMessage(
            userListElement,
            '사용자 목록을 불러오지 못했습니다. 서버와 API를 확인해 주세요.',
            true
        );
    }
}

function renderUserList(container, users) {
    container.replaceChildren();

    if (users.length === 0) {
        showMessage(
            container,
            '등록된 사용자가 없습니다.'
        );

        return;
    }

    for (const user of users) {
        container.appendChild(
            createUserItem(user)
        );
    }
}

function createUserItem(user) {
    const item = document.createElement('div');
    item.className = 'user-item';

    const avatar = document.createElement('div');
    avatar.className = 'user-avatar';
    avatar.textContent = getInitial(user.username);

    const info = document.createElement('div');
    info.className = 'user-info';

    const name = document.createElement('div');
    name.className = 'user-name';
    name.textContent = user.username || '이름 없음';

    const email = document.createElement('div');
    email.className = 'user-email';
    email.textContent = user.email || '이메일 없음';

    info.append(name, email);

    const status = document.createElement('span');

    const online = user.online === true;

    status.className =
        `status-badge ${online ? 'online' : 'offline'}`;

    status.textContent = online
        ? '온라인'
        : '오프라인';

    item.append(avatar, info, status);

    if (user.profileId) {
        loadProfileImage(
            user.profileId,
            user.username,
            avatar
        );
    }

    return item;
}

async function loadProfileImage(
    profileId,
    username,
    avatar
) {
    try {
        const url =
            `${ENDPOINTS.BINARY_CONTENT}?binaryContentId=` +
            encodeURIComponent(profileId);

        const response = await fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            return;
        }

        const binaryContent = await response.json();

        const contentType =
            String(binaryContent.contentType || '')
                .split(';')[0]
                .trim()
                .toLowerCase();

        const allowedImageTypes = [
            'image/jpeg',
            'image/png',
            'image/gif',
            'image/webp'
        ];

        if (!allowedImageTypes.includes(contentType)) {
            return;
        }

        if (typeof binaryContent.bytes !== 'string'
            || binaryContent.bytes.length === 0) {
            return;
        }

        const image = document.createElement('img');

        image.alt =
            `${username || '사용자'} 프로필`;

        image.onload = () => {
            if (avatar.isConnected) {
                avatar.replaceChildren(image);
            }
        };

        image.src =
            `data:${contentType};base64,${binaryContent.bytes}`;

    } catch (error) {
        console.warn(
            '프로필 이미지를 불러오지 못했습니다.',
            error
        );
    }
}

function getInitial(username) {
    const value =
        Array.from(
            String(username || '').trim()
        )[0] || '?';

    return /[\p{L}\p{N}]/u.test(value)
        ? value.toUpperCase()
        : '?';
}

function showMessage(
    container,
    message,
    isError = false
) {
    const paragraph = document.createElement('p');

    paragraph.className = isError
        ? 'state-message error'
        : 'state-message';

    paragraph.textContent = message;

    container.replaceChildren(paragraph);
}