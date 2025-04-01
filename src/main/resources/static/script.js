// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
  USERS: `${API_BASE_URL}/users`,
  CHANNELS: `${API_BASE_URL}/channels`,
  BINARY_CONTENT: `${API_BASE_URL}/binaryContents/{binaryContentId}`
};

let currentPage = 1; // 현재 페이지 (기본값: 1)
const itemsPerPage = 3; // 페이지당 항목 수

// Initialize the application
document.addEventListener('DOMContentLoaded', () => {
  fetchAndRenderUsers();
  fetchAndRenderChannels();
});

// Fetch users from the API
async function fetchAndRenderUsers() {
  try {
    const response = await fetch(ENDPOINTS.USERS);
    if (!response.ok) {
      throw new Error('Failed to fetch users');
    }
    const users = await response.json();

    if (users.length === 0) {
      userListElement.innerHTML = '<p>등록된 사용자가 없습니다.</p>';
    }

    renderUserList(users);
  } catch (error) {
    console.error('Error fetching users:', error);
  }
}

async function fetchAndRenderChannels() {
  const channelListElement = document.getElementById('channelList');

  try {
    const response = await fetch(ENDPOINTS.CHANNELS);
    if (!response.ok) {
      throw new Error('채널 목록을 불러오는 데 실패했습니다.');
    }

    const channels = await response.json();

    if (channels.length === 0) {
      channelListElement.innerHTML = '<p>등록된 채널이 없습니다.</p>';
    }

    renderChannelList(channels);
  } catch (error) {
    console.error('채널 목록을 불러오는 데 오류 발생:', error);
  }
}

// Fetch user profile image
async function fetchUserProfile(profileId) {
  try {
    const response = await fetch(
        `${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
    if (!response.ok) {
      throw new Error('Failed to fetch profile');
    }
    const profile = await response.json();

    return `data:${profile.contentType};base64,${profile.bytes}`;
  } catch (error) {
    console.error('Error fetching profile:', error);
    return '/default-avatar.png';
  }
}

// Render user list
async function renderUserList(users) {
  const userListElement = document.getElementById('userList');
  userListElement.innerHTML = ''; // Clear existing content

  for (const user of users) {
    const userElement = document.createElement('div');
    userElement.className = 'user-item';

    // Get profile image URL
    const profileUrl = user.profileId ?
        await fetchUserProfile(user.profileId) :
        '/default-avatar.png';

    userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.username}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.username}</div>
                <div class="user-email">${user.email}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

    userListElement.appendChild(userElement);
  }
}

function renderChannelList(channels) {
  const channelListElement = document.getElementById('channelList');
  channelListElement.innerHTML = '';

  const startIndex = (currentPage - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  const channelsToDisplay = channels.slice(startIndex, endIndex);

  for (const channel of channelsToDisplay) {
    const channelElement = document.createElement('div');
    channelElement.className = 'channel-item';

    const channelType = channel.channelType === 'PUBLIC' ? '🔵 공개 채널'
        : '🟢 비공개 채널';

    channelElement.innerHTML = `
            <div class="channel-info">
                <div class="channel-type">${channelType}</div>
                <div class="channel-name">${channel.name || '채널 이름 없음'}</div>
                <div class="channel-description">${channel.description
    || "NO description"}</div>
            </div>
        `;

    channelListElement.appendChild(channelElement);
  }
  renderPaginationButtons(channels.length);
}

function renderPaginationButtons(totalItems) {
  const paginationElement = document.getElementById('pagination');
  paginationElement.innerHTML = '';

  const totalPages = Math.ceil(totalItems / itemsPerPage);

  if (currentPage > 1) {
    const prevButton = document.createElement('button');
    prevButton.textContent = '◀ 이전';
    prevButton.addEventListener('click', () => {
      currentPage--;
      fetchAndRenderUsers();
      fetchAndRenderChannels();
    });
    paginationElement.appendChild(prevButton);
  }

  if (currentPage < totalPages) {
    const nextButton = document.createElement('button');
    nextButton.textContent = '다음 ▶';
    nextButton.addEventListener('click', () => {
      currentPage++;
      fetchAndRenderUsers();
      fetchAndRenderChannels();
    });
    paginationElement.appendChild(nextButton);
  }
}
