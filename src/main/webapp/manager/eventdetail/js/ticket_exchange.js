
// 全域變數
let currentPage = 1;
let pageSize = 10;
let searchParams = {};

// DOM 元素快取
const elements = {
    eventSelect: null,
    statusSelect: null,
    keywordInput: null,
    startDateInput: null,
    endDateInput: null,
    searchBtn: null,
    resetBtn: null,
    backBtn: null,
    tableBody: null,
    pagination: null
};

function checkUserPermission() {
    const roleLevel = sessionStorage.getItem("roleLevel");
    const memberId = sessionStorage.getItem("memberId");

    if (!roleLevel || (roleLevel !== "2" && roleLevel !== "3")) {
        alert("您沒有權限訪問此頁面");
        window.location.href = "/maven-tickeasy-v1/user/member/login.html";
        return false;
    }

    if (!memberId) {
        alert("請先登入");
        window.location.href = "/maven-tickeasy-v1/user/member/login.html";
        return false;
    }
    return true;
}

/**
 * 初始化函數
 */
function initTicketExchange() {
    console.log('=== 換票列表管理初始化 ===');
    
    // 1. 快取 DOM 元素
    cacheElements();

    // 2. 綁定事件
    bindEvents();

    // 3. 設定預設值
    setDefaultValues();

    // 4. 載入初始資料
    loadInitialData();
}

/**
 * 快取 DOM 元素
 */
function cacheElements() {
    elements.eventSelect = document.getElementById('eventSelect');
    elements.statusSelect = document.getElementById('statusSelect');
    elements.keywordInput = document.getElementById('keywordInput');
    elements.startDateInput = document.getElementById('startDateInput');
    elements.endDateInput = document.getElementById('endDateInput');
    elements.searchBtn = document.getElementById('searchBtn');
    elements.resetBtn = document.getElementById('resetBtn');
    elements.backBtn = document.getElementById('backBtn');
    elements.tableBody = document.getElementById('exchangeTableBody');
    elements.pagination = document.getElementById('paginationContainer');
}

/**
 * 綁定事件處理器
 */
function bindEvents() {
    console.log('=== 開始綁定事件 ===');

    // 搜尋按鈕事件
    if (elements.searchBtn) {
        elements.searchBtn.addEventListener('click', function (e) {
            e.preventDefault();
            console.log('搜尋按鈕被點擊');
            performSearch();
        });
    }

    // 活動選擇事件
    if (elements.eventSelect) {
        elements.eventSelect.addEventListener('change', function () {
            console.log('活動選擇改變:', this.value);
            performSearch();
        });
    }

    // 狀態選擇事件
    if (elements.statusSelect) {
        elements.statusSelect.addEventListener('change', function () {
            console.log('狀態選擇改變:', this.value);
            performSearch();
        });
    }

    // 重置按鈕事件
    if (elements.resetBtn) {
        elements.resetBtn.addEventListener('click', function () {
            console.log('重置按鈕被點擊');
            resetSearch();
        });
    }

    // 返回按鈕事件
    if (elements.backBtn) {
        elements.backBtn.addEventListener('click', function () {
            console.log('返回按鈕被點擊');
            window.location.href = './index.html';
        });
    }

    // 關鍵字輸入按Enter搜尋
    if (elements.keywordInput) {
        elements.keywordInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault();
                console.log('Enter鍵被按下');
                performSearch();
            }
        });
    }

    // 日期變更事件
    if (elements.startDateInput) {
        elements.startDateInput.addEventListener('change', performSearch);
    }
    if (elements.endDateInput) {
        elements.endDateInput.addEventListener('change', performSearch);
    }

    console.log('=== 事件綁定完成 ===');
}

/**
 * 設定預設值
 */
function setDefaultValues() {
    // 設定預設日期範圍（最近3個月）
    const today = new Date();
    const threeMonthsAgo = new Date(today.getFullYear(), today.getMonth() - 3, today.getDate());

    if (elements.startDateInput) {
        elements.startDateInput.value = formatDate(threeMonthsAgo);
    }
    if (elements.endDateInput) {
        elements.endDateInput.value = formatDate(today);
    }
}

/**
 * 載入初始資料
 */
async function loadInitialData() {
    try {
        console.log('=== 開始載入初始資料 ===');

        // 依序載入資料
        await loadEventList();
        await loadSwapStatusList();

        // 最後載入交換列表
        await loadExchangeList();

    } catch (error) {
        console.error('載入初始資料失敗：', error);
        showError('載入初始資料失敗');
    }
}

/**
 * 載入活動列表
 */
async function loadEventList() {
    try {
        console.log('=== 載入活動列表 ===');
        const response = await fetch('/maven-tickeasy-v1/api/manager/ticket-exchange/events');
        const result = await response.json();

        if (result.success && elements.eventSelect) {
            // 清空現有選項
            elements.eventSelect.innerHTML = '<option value="">全部活動</option>';

            // 添加活動選項
            result.data.forEach(event => {
                const option = document.createElement('option');
                option.value = event.eventId;
                option.textContent = event.eventName;
                elements.eventSelect.appendChild(option);
            });

            console.log('活動列表載入成功，共', result.data.length, '個活動');
        } else {
            console.error('載入活動列表失敗：', result.message);
        }
    } catch (error) {
        console.error('載入活動列表錯誤：', error);
    }
}

/**
 * 載入換票狀態列表
 */
async function loadSwapStatusList() {
    try {
        console.log('=== 載入狀態列表 ===');
        const response = await fetch('/maven-tickeasy-v1/api/manager/ticket-exchange/swap-status');
        const result = await response.json();

        if (result.success && elements.statusSelect) {
            // 清空現有選項（保留預設選項）
            elements.statusSelect.innerHTML = '<option value="">全部</option>';

            // 添加狀態選項
            result.data.forEach(status => {
                const option = document.createElement('option');
                option.value = status.value;
                option.textContent = status.label;
                elements.statusSelect.appendChild(option);
            });

            console.log('狀態列表載入成功，共', result.data.length, '個狀態');
        } else {
            console.error('載入狀態列表失敗：', result.message);
        }
    } catch (error) {
        console.error('載入狀態列表錯誤：', error);
    }
}

/**
 * 執行搜尋
 */
function performSearch() {
    console.log('=== 執行搜尋 ===');
    currentPage = 1;

    // 收集搜尋參數
    searchParams = {};

    // 關鍵字
    if (elements.keywordInput && elements.keywordInput.value.trim()) {
        searchParams.keyword = elements.keywordInput.value.trim();
    }

    // 活動ID
    if (elements.eventSelect && elements.eventSelect.value) {
        searchParams.eventId = parseInt(elements.eventSelect.value);
    }
    // 狀態
    if (elements.statusSelect && elements.statusSelect.value !== '') {
        searchParams.swappedStatus = parseInt(elements.statusSelect.value);
    }

    // 日期範圍
    if (elements.startDateInput && elements.startDateInput.value) {
        searchParams.startDate = elements.startDateInput.value;
    }
    if (elements.endDateInput && elements.endDateInput.value) {
        searchParams.endDate = elements.endDateInput.value;
    }

    console.log('搜尋參數：', searchParams);
    loadExchangeList();
}

/**
 * 載入換票列表
 */
async function loadExchangeList() {
    try {
        console.log('=== 載入換票列表 ===');
        showLoading(true);

        // 建立查詢參數，只包含有值的參數
        const queryParams = {
            page: currentPage,
            size: pageSize
        };

        // 只添加有值的搜尋參數
        Object.keys(searchParams).forEach(key => {
            const value = searchParams[key];
            if (value !== null && value !== undefined && value !== '') {
                queryParams[key] = value;
            }
        });

        console.log('最終查詢參數：', queryParams);

        // 建立 URL 參數字串，過濾空值
        const params = new URLSearchParams();
        Object.entries(queryParams).forEach(([key, value]) => {
            if (value !== null && value !== undefined && value !== '') {
                params.append(key, value.toString());
            }
        });

        const url = `/maven-tickeasy-v1/api/manager/ticket-exchange/swaps?${params.toString()}`;
        console.log('請求 URL：', url);

        const response = await fetch(url);
        const result = await response.json();

        console.log('API 回應：', result);

        if (result.success) {
            renderExchangeTable(result.data.data);
            renderPagination(result.data);
        } else {
            showError('載入換票列表失敗：' + result.message);
        }
    } catch (error) {
        console.error('載入換票列表錯誤：', error);
        showError('載入換票列表時發生錯誤');
    } finally {
        showLoading(false);
    }
}

/**
 * 渲染換票表格
 */
function renderExchangeTable(data) {
    console.log('=== 渲染表格資料 ===', data);

    if (!elements.tableBody) {
        console.error('找不到表格 body 元素');
        return;
    }

    if (!data || data.length === 0) {
        elements.tableBody.innerHTML = '<tr><td colspan="7" class="text-center">暫無數據</td></tr>';
        return;
    }

    const tableHTML = data.map(item => {
        console.log('處理項目：', item);
        return `
            <tr>
                <td>${item.commentId || '-'}</td>
                <td>${item.commentTicketId || '-'}</td>
                <td>${item.commentMemberId || '-'}</td>
                <td>${item.postTicketId || '-'}</td>
                <td>${item.postMemberId || '-'}</td>
                <td>${formatDisplayTime(item)}</td>
                <td>
                    <span class="badge ${getStatusBadgeClass(item.swappedStatus)}">
                        ${getStatusText(item.swappedStatus)}
                    </span>
                </td>
            </tr>
        `;
    }).join('');

    elements.tableBody.innerHTML = tableHTML;
    console.log('表格渲染完成');
}

/**
 * 渲染分頁控制器
 */
function renderPagination(pageData) {
    console.log('=== 渲染分頁 ===', pageData);

    if (!elements.pagination) {
        console.error('找不到分頁容器元素');
        return;
    }

    const { currentPage: current, totalPages, hasPrevious, hasNext } = pageData;

    if (totalPages <= 1) {
        elements.pagination.innerHTML = '';
        return;
    }

    let paginationHTML = '';

    // 第一頁按鈕
    paginationHTML += `
        <li class="page-item ${current === 1 ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="goToPage(1)" ${current === 1 ? 'aria-disabled="true"' : ''}>第一頁</a>
        </li>
    `;

    // 上一頁按鈕
    paginationHTML += `
        <li class="page-item ${!hasPrevious ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="goToPage(${current - 1})" ${!hasPrevious ? 'aria-disabled="true"' : ''}>上一頁</a>
        </li>
    `;

    // 頁碼按鈕
    const startPage = Math.max(1, current - 2);
    const endPage = Math.min(totalPages, current + 2);

    for (let i = startPage; i <= endPage; i++) {
        paginationHTML += `
            <li class="page-item ${i === current ? 'active' : ''}">
                <a class="page-link" href="#" onclick="goToPage(${i})">${i}</a>
            </li>
        `;
    }

    // 下一頁按鈕
    paginationHTML += `
        <li class="page-item ${!hasNext ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="goToPage(${current + 1})" ${!hasNext ? 'aria-disabled="true"' : ''}>下一頁</a>
        </li>
    `;

    // 最末頁按鈕
    paginationHTML += `
        <li class="page-item ${current === totalPages ? 'disabled' : ''}">
            <a class="page-link" href="#" onclick="goToPage(${totalPages})" ${current === totalPages ? 'aria-disabled="true"' : ''}>最末頁</a>
        </li>
    `;

    elements.pagination.innerHTML = paginationHTML;
}

/**
 * 跳轉到指定頁面
 */
function goToPage(page) {
    console.log('跳轉到頁面：', page);
    if (page < 1) return;
    currentPage = page;
    loadExchangeList();
}

/**
 * 格式化顯示時間 - 根據狀態決定顯示哪個時間
 */
function formatDisplayTime(item) {
    let dateTime;
    // 如果是已換票狀態且有換票時間，顯示換票時間；否則顯示建立時間
    if (item.swappedStatus === 2 && item.swappedTime) {
        dateTime = item.swappedTime;
    } else {
        dateTime = item.createTime;
    }
    return formatDateTime(dateTime);
}

/**
 * 獲取狀態文字
 */
function getStatusText(status) {
    const statusMap = {
        0: '待換票',
        1: '待確認',
        2: '已換票',
        3: '已取消'
    };
    return statusMap[status] || '未知';
}

/**
 * 獲取狀態徽章樣式
 */
function getStatusBadgeClass(status) {
    const badgeMap = {
        0: 'bg-warning text-dark',
        1: 'bg-info',
        2: 'bg-success',
        3: 'bg-danger'
    };
    return badgeMap[status] || 'bg-secondary';
}

/**
 * 格式化日期時間
 */
function formatDateTime(dateTime) {
    if (!dateTime) return '-';

    try {
        const date = new Date(dateTime);

        // 檢查日期是否有效
        if (isNaN(date.getTime())) {
            return '-';
        }

        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');

        return `${year}/${month}/${day} ${hours}:${minutes}`;
    } catch (error) {
        console.error('日期格式化錯誤：', error);
        return '-';
    }
}

/**
 * 格式化日期（用於 input[type="date"]）
 */
function formatDate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

/**
 * 顯示載入狀態
 */
function showLoading(show) {
    if (show && elements.tableBody) {
        elements.tableBody.innerHTML = '<tr><td colspan="7" class="text-center"><div class="spinner-border spinner-border-sm me-2" role="status"></div>載入中...</td></tr>';
    }
}

/**
 * 顯示錯誤訊息
 */
function showError(message) {
    console.error('錯誤：', message);

    // 移除現有的錯誤訊息
    const existingAlert = document.querySelector('.alert.alert-danger');
    if (existingAlert) {
        existingAlert.remove();
    }

    const alertHTML = `
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    const container = document.querySelector('.app-content .container-fluid');
    if (container) {
        container.insertAdjacentHTML('afterbegin', alertHTML);

        // 自動移除錯誤訊息
        setTimeout(() => {
            const alert = container.querySelector('.alert.alert-danger');
            if (alert) {
                alert.remove();
            }
        }, 5000);
    }
}

/**
 * 顯示成功訊息
 */
function showSuccess(message) {
    // 移除現有的成功訊息
    const existingAlert = document.querySelector('.alert.alert-success');
    if (existingAlert) {
        existingAlert.remove();
    }

    const alertHTML = `
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill"></i> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    const container = document.querySelector('.app-content .container-fluid');
    if (container) {
        container.insertAdjacentHTML('afterbegin', alertHTML);

        // 自動移除成功訊息
        setTimeout(() => {
            const alert = container.querySelector('.alert.alert-success');
            if (alert) {
                alert.remove();
            }
        }, 3000);
    }
}

/**
 * 重置搜尋條件
 */
function resetSearch() {
    console.log('=== 重置搜尋條件 ===');

    // 清空表單欄位
    if (elements.keywordInput) elements.keywordInput.value = '';
    if (elements.statusSelect) elements.statusSelect.value = '';
    if (elements.eventSelect) elements.eventSelect.value = '';

    // 重設日期範圍
    setDefaultValues();

    // 清空搜尋參數
    searchParams = {};
    currentPage = 1;

    // 重新載入列表
    loadExchangeList();

    showSuccess('搜尋條件已重置');
}

/**
 * 安全檢查並執行初始化
 */
function safeInitialize() {
    // 先檢查權限
    if (!checkUserPermission()) {
        return;
    }

    if (typeof initTicketExchange === 'function') {
        initTicketExchange();
    } else {
        console.error('initTicketExchange 函數未找到');
    }
}

/**
 * 檢查 DOM 狀態並初始化應用程式
 */
function initializeApp() {
    // 檢查 document.readyState 以確保在正確的時機執行初始化
    if (document.readyState === 'loading') {
        // DOM 仍在載入中，等待 DOMContentLoaded 事件
        document.addEventListener('DOMContentLoaded', safeInitialize);
    } else {
        // DOM 已經載入完成，直接執行初始化
        safeInitialize();
    }
}

// 將需要全域存取的函數匯出
window.goToPage = goToPage;
window.resetSearch = resetSearch;
window.performSearch = performSearch;
window.loadExchangeList = loadExchangeList;
window.initTicketExchange = initTicketExchange;

// 自動初始化應用程式
initializeApp();