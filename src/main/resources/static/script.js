const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('Failed to fetch users');
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('Error fetching users:', error);
    }
}

async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
        if (!response.ok) throw new Error('Failed to fetch profile');
        const profile = await response.json();

        return `data:${profile.contentType};base64,${profile.data}`;
    } catch (error) {
        console.error('Error fetching profile:', error);
        return null;
    }
}

async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const fallback = document.createElement('span');
        fallback.className = 'user-avatar avatar-fallback';
        fallback.textContent = user.username.trim().charAt(0).toUpperCase() || '?';
        fallback.setAttribute('aria-hidden', 'true');
        userElement.appendChild(fallback);

        const profileUrl = user.profileId ? await fetchUserProfile(user.profileId) : null;
        if (profileUrl) {
            const image = document.createElement('img');
            image.className = 'user-avatar';
            image.alt = '';
            image.addEventListener('error', () => image.replaceWith(fallback), { once: true });
            fallback.replaceWith(image);
            image.src = profileUrl;
        }

        const info = document.createElement('div');
        info.className = 'user-info';
        const name = document.createElement('div');
        name.className = 'user-name';
        name.textContent = user.username;
        const email = document.createElement('div');
        email.className = 'user-email';
        email.textContent = user.email;
        info.append(name, email);

        const status = document.createElement('div');
        status.className = user.online ? 'status-badge online' : 'status-badge offline';
        status.textContent = user.online ? '온라인' : '오프라인';
        userElement.append(info, status);

        userListElement.appendChild(userElement);
    }
}