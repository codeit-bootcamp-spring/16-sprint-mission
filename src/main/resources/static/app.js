(() => {
  const $ = (selector, root = document) => root.querySelector(selector);
  const $$ = (selector, root = document) => [...root.querySelectorAll(selector)];
  const STORAGE = { api: "discodeit.apiBase", user: "discodeit.userId" };
  const state = { apiBase: localStorage.getItem(STORAGE.api) || "http://localhost:8080/api/v1", userId: localStorage.getItem(STORAGE.user) || "", users: [], channels: [], channelMemberships: new Map(), activeChannel: null, messages: [], readStatuses: [], files: new Map(), objectUrls: [] };
  let toastTimer;

  function toast(message, isError = false) {
    const node = $("#toast");
    node.textContent = message;
    node.classList.toggle("error", isError);
    node.classList.add("show");
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => node.classList.remove("show"), 3200);
  }
  function esc(value = "") {
    return String(value).replace(/[&<>"']/g, (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char]);
  }
  function base() {
    return state.apiBase.replace(/\/+$/, "");
  }
  async function request(path, options = {}) {
    const response = await fetch(`${base()}${path.startsWith("/") ? path : `/${path}`}`, options);
    const text = await response.text();
    let body = null;
    if (text) {
      try { body = JSON.parse(text); } catch { body = text; }
    }
    if (!response.ok) {
      const message = typeof body === "string" ? body : body?.message || body?.detail || body?.error || response.statusText;
      throw new Error(`${response.status} ${message}`);
    }
    return body;
  }
  const json = (method, value) => ({ method, headers: { "Content-Type": "application/json" }, body: JSON.stringify(value) });
  function idOf(value) {
    if (typeof value === "string") return value;
    return value?.userId || value?.id || value?.channelId || value?.user?.id || value?.memberId || "";
  }
  function initials(name = "?") { return [...String(name).trim()].slice(0, 1).join("") || "?"; }
  function userById(id) { return state.users.find(user => user.id === id); }
  function formatTime(value) {
    if (!value) return "시간 정보 없음";
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? String(value) : new Intl.DateTimeFormat("ko-KR", { month: "short", day: "numeric", hour: "2-digit", minute: "2-digit" }).format(date);
  }
  function releaseObjectUrls() {
    state.objectUrls.forEach(url => URL.revokeObjectURL(url));
    state.objectUrls = [];
  }
  function mimeFromName(name = "") {
    const ext = name.split(".").pop().toLowerCase();
    return ({ png: "image/png", jpg: "image/jpeg", jpeg: "image/jpeg", gif: "image/gif", webp: "image/webp", svg: "image/svg+xml", pdf: "application/pdf", txt: "text/plain", json: "application/json", zip: "application/zip" })[ext] || "application/octet-stream";
  }
  function bytesToBlob(content, fileName) {
    if (typeof content === "string") {
      const binary = atob(content);
      const bytes = new Uint8Array(binary.length);
      for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
      return new Blob([bytes], { type: mimeFromName(fileName) });
    }
    if (Array.isArray(content)) return new Blob([new Uint8Array(content)], { type: mimeFromName(fileName) });
    return new Blob([], { type: mimeFromName(fileName) });
  }
  async function fetchFiles(ids) {
    const uniqueIds = [...new Set((ids || []).filter(Boolean))].filter(id => !state.files.has(id));
    if (!uniqueIds.length) return [];
    const found = await request("/files", json("POST", { ids: uniqueIds }));
    (Array.isArray(found) ? found : []).forEach(file => state.files.set(file.id, file));
    return found || [];
  }
  function fileUrl(file) {
    if (!file) return "";
    if (!file._objectUrl) {
      file._objectUrl = URL.createObjectURL(bytesToBlob(file.content, file.fileName));
      state.objectUrls.push(file._objectUrl);
    }
    return file._objectUrl;
  }
  async function downloadFile(file) {
    const anchor = document.createElement("a");
    anchor.href = fileUrl(file);
    anchor.download = file.fileName || "download";
    document.body.append(anchor);
    anchor.click();
    anchor.remove();
  }
  async function loadUsers() {
    const response = await request("/users");
    state.users = Array.isArray(response) ? response : [];
    if (state.userId && !state.users.some(user => user.id === state.userId)) {
      state.userId = "";
      localStorage.removeItem(STORAGE.user);
    }
    state.files.clear();
    releaseObjectUrls();
    const profileIds = state.users.map(user => user.profileId).filter(Boolean);
    if (profileIds.length) {
      try { await fetchFiles(profileIds); } catch (error) { console.warn("프로필 파일 조회 실패", error); }
    }
    renderUsers();
    renderMemberPicker();
    renderSession();
  }
  function avatarMarkup(user, className = "avatar") {
    const file = user?.profileId ? state.files.get(user.profileId) : null;
    if (file?.content != null) return `<img class="${className}" src="${fileUrl(file)}" alt="${esc(user.name)} 프로필">`;
    return `<span class="${className} avatar-fallback" aria-label="${esc(user?.name || "사용자")}">${esc(initials(user?.name))}</span>`;
  }
  function renderUsers() {
    const grid = $("#userGrid");
    $("#userCount").textContent = state.users.length;
    $("#onlineCount").textContent = state.users.filter(user => user.isOnline === true || user.online === true).length;
    $("#usersSubheading").textContent = `${state.users.length}명의 사용자가 등록되어 있어요`;
    if (!state.users.length) {
      grid.innerHTML = `<div class="empty-state">아직 등록된 사용자가 없어요.<br><button class="button button-primary button-small" data-nav="register" style="margin-top:12px">첫 사용자 등록</button><p style="margin:11px 0 0">또는 상단의 샘플 데이터 만들기를 눌러 테스트 데이터를 생성하세요.</p></div>`;
      return;
    }
    grid.innerHTML = state.users.map(user => {
      const online = user.isOnline === true || user.online === true;
      return `<article class="user-card"><div class="user-card-top">${avatarMarkup(user)}<div><h3 class="user-name">${esc(user.name)}</h3><p class="user-email">${esc(user.email)}</p><span class="presence ${online ? "online" : "offline"}">${online ? "온라인" : "오프라인"}</span></div></div><div class="user-card-actions"><button class="button button-quiet button-small" data-refresh-status="${esc(user.id)}">접속 갱신</button><button class="button button-danger-quiet button-small" data-delete-user="${esc(user.id)}">삭제</button></div></article>`;
    }).join("");
  }
  function renderSession() {
    const user = userById(state.userId);
    const pill = $("#sessionPill");
    const loggedIn = Boolean(state.userId);
    pill.classList.toggle("logged-in", loggedIn);
    $("#sessionLabel").textContent = loggedIn ? `${user?.name || "사용자"} · 로그인 중` : "로그인 필요";
    $("#logoutButton").classList.toggle("hidden", !loggedIn);
    $("#loginSessionTitle").textContent = loggedIn ? `${user?.name || "사용자"}님, 로그인되어 있어요` : "로그인되어 있지 않아요";
    $("#loginSessionDescription").textContent = loggedIn ? "API 요청에 아래 사용자 ID를 사용합니다." : "로그인하면 사용자 ID를 저장하고 채널 목록 조회에 사용합니다.";
    $("#loginIdDisplay").classList.toggle("hidden", !loggedIn);
    $("#loginUserId").textContent = state.userId;
  }
  function showView(name) {
    $$(".view").forEach(view => view.classList.toggle("active", view.id === `view-${name}`));
    $$(".nav-link").forEach(button => button.classList.toggle("active", button.dataset.nav === name || (name === "messages" && button.dataset.nav === "channels")));
    $(".main-nav").classList.remove("open");
    window.scrollTo({ top: 0, behavior: "smooth" });
  }
  async function refreshStatus(userId) {
    await request(`/users/status/${encodeURIComponent(userId)}`, json("PATCH", { lastActiveTime: new Date().toISOString() }));
    await loadUsers();
    toast("접속 상태를 현재 시간으로 갱신했어요.");
  }
  async function loadChannels() {
    if (!state.userId) {
      state.channels = [];
      renderChannels();
      toast("채널을 보려면 먼저 로그인해주세요.", true);
      showView("login");
      return;
    }
    const response = await request(`/channels?userId=${encodeURIComponent(state.userId)}`);
    state.channels = Array.isArray(response) ? response : [];
    state.channelMemberships = new Map();
    const statusLists = await Promise.all(state.users.map(user =>
      request(`/read-status/users/${encodeURIComponent(user.id)}`).catch(() => [])
    ));
    statusLists.flat().forEach(status => {
      if (!status?.channelId || !status?.userId) return;
      if (!state.channelMemberships.has(status.channelId)) state.channelMemberships.set(status.channelId, new Set());
      state.channelMemberships.get(status.channelId).add(status.userId);
    });
    renderChannels();
  }
  function channelType(channel) { return String(channel?.type || "PUBLIC").toUpperCase(); }
  function channelName(channel, index = 0) {
    if (channel?.name) return channel.name;
    return `비공개 채널 ${index + 1}`;
  }
  function memberIdsFor(channel) {
    if (Array.isArray(channel.memberIds)) return channel.memberIds;
    return channelType(channel) === "PUBLIC" ? [...(state.channelMemberships.get(channel.id) || [])] : [];
  }
  function renderChannels() {
    const list = $("#channelList");
    $("#channelTotal").textContent = state.channels.length;
    $("#channelCount").textContent = state.channels.length;
    if (!state.userId) { list.innerHTML = `<div class="empty-state">로그인 후 채널을 불러올 수 있어요.</div>`; return; }
    if (!state.channels.length) { list.innerHTML = `<div class="empty-state">참여 중인 채널이 없어요.<br><button class="button button-primary button-small" data-nav="channel-create" style="margin-top:12px">채널 만들기</button></div>`; return; }
    list.innerHTML = state.channels.map((channel, index) => {
      const isPrivate = channelType(channel) === "PRIVATE";
      const members = memberIdsFor(channel).map(userById).filter(Boolean).slice(0, 5);
      const isJoined = state.channelMemberships.get(channel.id)?.has(state.userId) || false;
      const memberMarkup = members.length ? `<div class="member-stack">${members.map(user => {
        const file = user.profileId ? state.files.get(user.profileId) : null;
        return file?.content != null ? `<img src="${fileUrl(file)}" title="${esc(user.name)}" alt="">` : `<span class="mini-avatar" title="${esc(user.name)}">${esc(initials(user.name))}</span>`;
      }).join("")}</div>` : `<span class="muted small">멤버 없음</span>`;
      const membershipButton = !isPrivate ? `<button class="button ${isJoined ? "button-quiet" : "button-primary"} button-small channel-membership-button" data-channel-membership="${isJoined ? "leave" : "join"}" data-channel-id="${esc(channel.id)}">${isJoined ? "탈퇴" : "가입"}</button>` : "";
      return `<div class="channel-item" data-open-channel="${esc(channel.id)}" role="button" tabindex="0"><span class="channel-symbol ${isPrivate ? "private" : ""}">${isPrivate ? "◈" : "#"}</span><span class="channel-main"><span class="channel-title">${esc(channelName(channel,index))}<span class="type-badge ${isPrivate ? "private" : ""}">${isPrivate ? "비공개" : "공개"}</span></span><span class="channel-description">${esc(channel.description || (isPrivate ? "선택된 멤버만 참여" : "전체 사용자에게 공개"))}</span></span>${memberMarkup}${membershipButton}<button class="button button-danger-quiet button-small channel-delete-button" data-delete-channel="${esc(channel.id)}" aria-label="${esc(channelName(channel,index))} 삭제">삭제</button><span class="chevron">›</span></div>`;
    }).join("");
  }
  async function openChannel(channelId) {
    state.activeChannel = state.channels.find(channel => channel.id === channelId) || null;
    if (!state.activeChannel) return;
    showView("messages");
    $("#activeChannelTitle").textContent = channelName(state.activeChannel, state.channels.indexOf(state.activeChannel));
    const privateChannel = channelType(state.activeChannel) === "PRIVATE";
    $("#activeChannelType").textContent = privateChannel ? "비공개" : "공개";
    $("#activeChannelType").classList.toggle("private", privateChannel);
    $("#messagesTitle").textContent = channelName(state.activeChannel, state.channels.indexOf(state.activeChannel));
    $("#messagesDescription").textContent = state.activeChannel.description || (privateChannel ? "선택된 멤버만 참여하는 채널" : "공개 채널의 메시지 목록");
    $("#messageList").innerHTML = `<div class="loading-card">메시지를 불러오는 중…</div>`;
    $("#channelMemberCount").textContent = memberIdsFor(state.activeChannel).length;
    try {
      const [messages, statuses] = await Promise.all([
        request(`/messages/channels/${encodeURIComponent(channelId)}`),
        request(`/read-status/users/${encodeURIComponent(state.userId)}`).catch(() => [])
      ]);
      state.messages = Array.isArray(messages) ? messages : [];
      state.readStatuses = Array.isArray(statuses) ? statuses : [];
      const attachmentIds = state.messages.flatMap(message => message.attachmentIds || []);
      if (attachmentIds.length) await fetchFiles(attachmentIds);
      renderMessages();
    } catch (error) { $("#messageList").innerHTML = `<div class="empty-state">메시지를 불러오지 못했어요.<br>${esc(error.message)}</div>`; }
  }
  function statusForActiveChannel() {
    return state.readStatuses.find(status => status.channelId === state.activeChannel?.id && status.userId === state.userId);
  }
  function renderMessages() {
    const messages = state.messages;
    const list = $("#messageList");
    $("#messagesCountLabel").textContent = messages.length;
    $("#messageCount").textContent = messages.length;
    if (!messages.length) { list.innerHTML = `<div class="empty-state">아직 메시지가 없어요. 첫 메시지를 보내보세요.</div>`; $("#readDivider").classList.add("hidden"); return; }
    const status = statusForActiveChannel();
    const lastRead = status?.lastReadTime ? new Date(status.lastReadTime).getTime() : 0;
    let boundaryPlaced = false;
    const items = [];
    messages.forEach((message, index) => {
      const messageTime = new Date(message.updatedAt || 0).getTime();
      if (!boundaryPlaced && !lastRead && index === 0) {
        items.push(`<div class="read-divider"><span></span><b>아직 읽지 않은 메시지</b><span></span></div>`);
        boundaryPlaced = true;
      }
      if (!boundaryPlaced && lastRead && messageTime > lastRead) {
        items.push(`<div class="read-divider"><span></span><b>여기부터 아직 읽지 않았어요</b><span></span></div>`);
        boundaryPlaced = true;
      }
      const author = userById(message.authorId) || { name: "알 수 없는 사용자" };
      const fileIds = message.attachmentIds || [];
      const files = fileIds.map(id => state.files.get(id)).filter(Boolean);
      const attachments = files.length ? `<div class="attachment-row">${files.map((file, fileIndex) => `<button class="attachment-chip" data-download-file="${esc(file.id)}">↧ ${esc(file.fileName || `첨부 파일 ${fileIndex + 1}`)}</button>`).join("")}${files.length > 1 ? `<button class="attachment-chip" data-download-all="${esc(message.id)}">전체 다운로드 ↓</button>` : ""}</div>` : "";
      items.push(`<article class="message-card" data-message-card="${esc(message.id)}">${avatarMarkup(author,"message-avatar")}<div><div class="message-meta"><span class="message-author">${esc(author.name)}</span><span class="message-time">${esc(formatTime(message.updatedAt))}</span></div><p class="message-body" data-message-content>${esc(message.content || "(내용 없는 메시지)")}</p>${attachments}<div class="message-tools"><button class="text-action" data-edit-message="${esc(message.id)}">내용 수정</button><button class="text-action" data-delete-message="${esc(message.id)}">삭제</button></div></div></article>`);
    });
    if (lastRead && !boundaryPlaced) items.push(`<div class="read-divider"><span></span><b>여기까지 읽었어요</b><span></span></div>`);
    list.innerHTML = items.join("");
    $("#readDivider").classList.add("hidden");
  }
  async function markRead() {
    if (!state.activeChannel || !state.userId) return;
    const status = statusForActiveChannel();
    const now = new Date().toISOString();
    if (status) {
      const updated = await request(`/read-status/${encodeURIComponent(status.id)}`, json("PATCH", { lastReadTime: now }));
      Object.assign(status, updated || { lastReadTime: now });
    } else {
      const created = await request("/read-status", json("POST", { channelId: state.activeChannel.id, userId: state.userId }));
      state.readStatuses.push(created || { id: "", channelId: state.activeChannel.id, userId: state.userId, lastReadTime: now });
      const newStatus = statusForActiveChannel();
      if (newStatus && !newStatus.lastReadTime) newStatus.lastReadTime = now;
    }
    renderMessages();
    toast("읽음 위치를 현재 시간으로 갱신했어요.");
  }

  async function registerUser(form) {
    const data = new FormData(form);
    const user = { name: data.get("name").trim(), email: data.get("email").trim(), password: data.get("password") };
    if (user.password.length < 8 || user.password.length > 20) throw new Error("비밀번호는 8–20자로 입력해주세요.");
    const body = new FormData();
    body.append("user", new Blob([JSON.stringify(user)], { type: "application/json" }));
    const profile = data.get("profileImage");
    if (profile instanceof File && profile.size) body.append("profileImage", profile, profile.name);
    await request("/users", { method: "POST", body });
    form.reset();
    $("#profilePreview").classList.add("hidden");
    $("#profileFileName").textContent = "PNG, JPG 등 이미지 파일";
    await loadUsers();
    toast("사용자를 등록했어요.");
    showView("users");
  }
  async function login(form) {
    const payload = { email: $("#loginEmail").value.trim(), password: $("#loginPassword").value };
    const result = await request("/auth/users", json("POST", payload));
    const id = idOf(result);
    if (!id) throw new Error("로그인 응답에서 사용자 ID를 찾지 못했어요.");
    state.userId = id;
    localStorage.setItem(STORAGE.user, id);
    form.reset();
    renderSession();
    await loadChannels();
    toast("로그인했어요. 이 브라우저에서 상태를 유지합니다.");
    showView("channels");
  }
  async function createChannel(form) {
    const type = $("input[name=channelType]:checked").value;
    if (!state.userId) throw new Error("채널을 만들려면 먼저 로그인해주세요.");
    if (type === "PUBLIC") {
      const name = $("#channelName").value.trim();
      const description = $("#channelDescription").value.trim();
      if (!name || !description) throw new Error("공개 채널 이름과 설명을 입력해주세요.");
      await request("/channels/public", json("POST", { name, description }));
    } else {
      const memberIds = $$("#memberPicker input:checked").map(input => input.value);
      await request("/channels/private", json("POST", { memberIds }));
    }
    form.reset();
    $("#publicChoice").classList.add("selected");
    $("#privateChoice").classList.remove("selected");
    $("#publicFields").classList.remove("hidden");
    $("#privateFields").classList.add("hidden");
    await loadChannels();
    toast("채널을 만들었어요.");
    showView("channels");
  }
  async function sendMessage(form) {
    if (!state.activeChannel || !state.userId) throw new Error("메시지를 보낼 채널과 로그인 계정을 선택해주세요.");
    const content = $("#messageContent").value.trim();
    const attachments = $("#messageAttachments").files;
    if (!content && !attachments.length) throw new Error("메시지 내용이나 첨부파일을 추가해주세요.");
    const body = new FormData();
    const payload = { authorId: state.userId, content, channelId: state.activeChannel.id };
    body.append("message", new Blob([JSON.stringify(payload)], { type: "application/json" }));
    [...attachments].forEach(file => body.append("attachments", file, file.name));
    await request("/messages", { method: "POST", body });
    form.reset();
    $("#attachmentsLabel").textContent = "파일 선택 안 됨";
    await openChannel(state.activeChannel.id);
    toast("메시지를 보냈어요.");
  }
  async function deleteUser(id) {
    const user = userById(id);
    if (!window.confirm(`${user?.name || "이 사용자"}를 삭제할까요?`)) return;
    await request(`/users/${encodeURIComponent(id)}`, { method: "DELETE" });
    if (id === state.userId) { state.userId = ""; localStorage.removeItem(STORAGE.user); renderSession(); }
    await loadUsers();
    if (state.userId) await loadChannels();
    toast("사용자를 삭제했어요.");
  }
  async function deleteChannel(id) {
    if (!window.confirm("이 채널을 삭제할까요? 채널의 메시지도 함께 삭제될 수 있어요.")) return;
    await request(`/channels/${encodeURIComponent(id)}`, { method: "DELETE" });
    state.activeChannel = null;
    await loadChannels();
    toast("채널을 삭제했어요.");
    showView("channels");
  }
  async function deleteMessage(id) {
    if (!window.confirm("이 메시지를 삭제할까요?")) return;
    await request(`/messages/${encodeURIComponent(id)}`, { method: "DELETE" });
    await openChannel(state.activeChannel.id);
    toast("메시지를 삭제했어요.");
  }
  async function editMessage(id) {
    const message = state.messages.find(item => item.id === id);
    const content = window.prompt("메시지 내용을 수정하세요.", message?.content || "");
    if (content === null) return;
    await request(`/messages/${encodeURIComponent(id)}`, json("PATCH", { content }));
    await openChannel(state.activeChannel.id);
    toast("메시지를 수정했어요.");
  }
  function renderMemberPicker() {
    const picker = $("#memberPicker");
    if (!picker) return;
    if (!state.users.length) { picker.innerHTML = `<div class="empty-state">먼저 사용자를 등록해주세요.</div>`; return; }
    picker.innerHTML = state.users.map(user => `<label class="member-option"><input type="checkbox" value="${esc(user.id)}"><span>${avatarMarkup(user,"mini-avatar")}</span><span><strong>${esc(user.name)}</strong><small class="muted" style="display:block;margin-top:3px">${esc(user.email)}</small></span></label>`).join("");
  }
  async function seedData() {
    if (state.users.length) { toast("기존 사용자가 있어 샘플 데이터 중복 생성을 건너뛰었어요."); return; }
    const definitions = [
      { name: "하늘 김", email: "haneul.demo@example.com", password: "DemoPass123" },
      { name: "도윤 박", email: "doyun.demo@example.com", password: "DemoPass123" },
      { name: "서윤 이", email: "seoyun.demo@example.com", password: "DemoPass123" }
    ];
    const createdUsers = [];
    for (const definition of definitions) {
      const body = new FormData();
      body.append("user", new Blob([JSON.stringify({ name: definition.name, email: definition.email, password: definition.password })], { type: "application/json" }));
      const result = await request("/users", { method: "POST", body });
      createdUsers.push({ ...definition, id: idOf(result) });
    }
    await loadUsers();
    const publicChannel = await request("/channels/public", json("POST", { name: "동네 라운지", description: "모두가 편하게 이야기하는 공개 채널" }));
    const publicId = idOf(publicChannel);
    const publicMembers = [createdUsers[0], createdUsers[2]].filter(user => user?.id);
    if (publicId) {
      for (const user of publicMembers) {
        await request(`/channels/${encodeURIComponent(publicId)}/users`, json("POST", { userId: user.id }));
      }
    }
    const privateChannel = await request("/channels/private", json("POST", { memberIds: createdUsers.slice(0, 2).map(user => user.id).filter(Boolean) }));
    await request("/channels/private", json("POST", { memberIds: [] }));
    const privateId = idOf(privateChannel);
    const firstUserId = createdUsers[0].id;
    if (publicId && firstUserId) {
      const messageForm = new FormData();
      messageForm.append("message", new Blob([JSON.stringify({ authorId: firstUserId, channelId: publicId, content: "안녕하세요! 테스트용 공개 채널을 만들었어요 🐾" })], { type: "application/json" }));
      await request("/messages", { method: "POST", body: messageForm });
    }
    if (privateId && createdUsers[1]?.id) {
      const messageForm = new FormData();
      messageForm.append("message", new Blob([JSON.stringify({ authorId: createdUsers[1].id, channelId: privateId, content: "비공개 채널 메시지도 잘 보이나요?" })], { type: "application/json" }));
      await request("/messages", { method: "POST", body: messageForm });
    }
    if (state.userId) await loadChannels();
    $("#lastUpdated").textContent = "샘플 데이터를 준비했어요";
    toast("샘플 사용자와 채널을 만들었어요. 이메일: haneul.demo@example.com / 비밀번호: DemoPass123");
  }

  function bindEvents() {
    document.addEventListener("click", async event => {
      const nav = event.target.closest("[data-nav]");
      if (nav) {
        const view = nav.dataset.nav;
        if (view === "channels" && state.userId) await loadChannels().catch(error => toast(error.message, true));
        if (view === "channel-create") { await loadUsers().catch(() => {}); renderMemberPicker(); }
        showView(view);
      }
      const refresh = event.target.closest("[data-refresh-status]");
      if (refresh) try { await refreshStatus(refresh.dataset.refreshStatus); } catch (error) { toast(error.message, true); }
      const deleteUserButton = event.target.closest("[data-delete-user]");
      if (deleteUserButton) try { await deleteUser(deleteUserButton.dataset.deleteUser); } catch (error) { toast(error.message, true); }
      const open = event.target.closest("[data-open-channel]");
      const membershipButton = event.target.closest("[data-channel-membership]");
      if (membershipButton) {
        event.stopPropagation();
        try {
          const channelId = membershipButton.dataset.channelId;
          const action = membershipButton.dataset.channelMembership;
          await request(`/channels/${encodeURIComponent(channelId)}/users`, json(action === "join" ? "POST" : "DELETE", { userId: state.userId }));
          await loadChannels();
          toast(action === "join" ? "공개 채널에 가입했어요." : "공개 채널에서 탈퇴했어요.");
        } catch (error) { toast(error.message, true); }
        return;
      }
      const deleteChannelButton = event.target.closest("[data-delete-channel]");
      if (deleteChannelButton) { event.stopPropagation(); try { await deleteChannel(deleteChannelButton.dataset.deleteChannel); } catch (error) { toast(error.message, true); } return; }
      if (open) try { await openChannel(open.dataset.openChannel); } catch (error) { toast(error.message, true); }
      const messageDelete = event.target.closest("[data-delete-message]");
      if (messageDelete) try { await deleteMessage(messageDelete.dataset.deleteMessage); } catch (error) { toast(error.message, true); }
      const messageEdit = event.target.closest("[data-edit-message]");
      if (messageEdit) try { await editMessage(messageEdit.dataset.editMessage); } catch (error) { toast(error.message, true); }
      const fileButton = event.target.closest("[data-download-file]");
      if (fileButton) { const file = state.files.get(fileButton.dataset.downloadFile); if (file) downloadFile(file); }
      const allFilesButton = event.target.closest("[data-download-all]");
      if (allFilesButton) {
        const message = state.messages.find(item => item.id === allFilesButton.dataset.downloadAll);
        for (const file of (message?.attachmentIds || []).map(id => state.files.get(id)).filter(Boolean)) { await downloadFile(file); await new Promise(resolve => setTimeout(resolve, 180)); }
      }
    });
    document.addEventListener("keydown", event => {
      const row = event.target.closest("[data-open-channel]");
      if (row && event.target === row && (event.key === "Enter" || event.key === " ")) {
        event.preventDefault();
        openChannel(row.dataset.openChannel).catch(error => toast(error.message, true));
      }
    });
    $("#saveApiBase").addEventListener("click", async () => {
      const value = $("#apiBase").value.trim();
      if (!/^https?:\/\//i.test(value)) { toast("API 주소는 http:// 또는 https://로 시작해야 해요.", true); return; }
      state.apiBase = value;
      localStorage.setItem(STORAGE.api, value);
      await refreshAll();
      toast("API 주소를 적용했어요.");
    });
    $("#apiBase").value = state.apiBase;
    $("#refreshAll").addEventListener("click", () => refreshAll().catch(error => toast(error.message, true)));
    $("#refreshUsers").addEventListener("click", () => loadUsers().then(() => toast("사용자 목록을 갱신했어요.")).catch(error => toast(error.message, true)));
    $("#refreshChannels").addEventListener("click", () => loadChannels().then(() => toast("채널 목록을 갱신했어요.")).catch(error => toast(error.message, true)));
    $("#seedButton").addEventListener("click", () => seedData().catch(error => toast(`샘플 생성 중 오류: ${error.message}`, true)));
    $("#registerForm").addEventListener("submit", event => { event.preventDefault(); registerUser(event.currentTarget).catch(error => toast(error.message, true)); });
    $("#loginForm").addEventListener("submit", event => { event.preventDefault(); login(event.currentTarget).catch(error => toast(error.message, true)); });
    $("#channelForm").addEventListener("submit", event => { event.preventDefault(); createChannel(event.currentTarget).catch(error => toast(error.message, true)); });
    $("#messageForm").addEventListener("submit", event => { event.preventDefault(); sendMessage(event.currentTarget).catch(error => toast(error.message, true)); });
    $("#regProfile").addEventListener("change", event => {
      const file = event.target.files[0];
      $("#profileFileName").textContent = file?.name || "PNG, JPG 등 이미지 파일";
      const preview = $("#profilePreview");
      if (!file) { preview.classList.add("hidden"); return; }
      preview.src = URL.createObjectURL(file);
      preview.classList.remove("hidden");
    });
    $("#messageAttachments").addEventListener("change", event => {
      const files = [...event.target.files];
      $("#attachmentsLabel").textContent = files.length ? `${files.length}개 선택 · ${files.map(file => file.name).join(", ")}` : "파일 선택 안 됨";
    });
    $$("input[name=channelType]").forEach(input => input.addEventListener("change", () => {
      const isPublic = input.value === "PUBLIC" && input.checked;
      $("#publicChoice").classList.toggle("selected", isPublic);
      $("#privateChoice").classList.toggle("selected", !isPublic);
      $("#publicFields").classList.toggle("hidden", !isPublic);
      $("#privateFields").classList.toggle("hidden", isPublic);
      $("#channelPayloadHint").textContent = isPublic ? "공개 요청: { name, description }" : "비공개 요청: { memberIds: [사용자 ID, …] }";
    }));
    $("#selectAllMembers").addEventListener("click", () => $$("#memberPicker input[type=checkbox]").forEach(input => input.checked = true));
    $("#backToChannels").addEventListener("click", () => showView("channels"));
    $("#deleteActiveChannel").addEventListener("click", () => state.activeChannel && deleteChannel(state.activeChannel.id).catch(error => toast(error.message, true)));
    $("#markRead").addEventListener("click", () => markRead().catch(error => toast(error.message, true)));
    $("#logoutButton").addEventListener("click", () => { state.userId = ""; state.channels = []; state.activeChannel = null; localStorage.removeItem(STORAGE.user); renderSession(); renderChannels(); toast("로그아웃했어요."); showView("users"); });
    $("#mobileMenu").addEventListener("click", () => $(".main-nav").classList.toggle("open"));
  }
  async function refreshAll() {
    $("#lastUpdated").textContent = "연결 중…";
    await loadUsers();
    if (!state.users.length) {
      await seedData();
      return;
    }
    if (state.userId) await loadChannels();
    $("#lastUpdated").textContent = `갱신 ${new Intl.DateTimeFormat("ko-KR", { hour: "2-digit", minute: "2-digit" }).format(new Date())}`;
  }
  bindEvents();
  renderSession();
  refreshAll().catch(error => {
    $("#lastUpdated").textContent = "API에 연결할 수 없음";
    $("#userGrid").innerHTML = `<div class="empty-state">API 서버에 연결할 수 없어요.<br>${esc(error.message)}<br><br>상단의 서버 주소를 확인하고 다시 시도해주세요.</div>`;
    toast("API 서버 연결을 확인해주세요.", true);
  });
})();
