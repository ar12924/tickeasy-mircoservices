// ===== 全域變數 =====
let currentPage = 1;          // 目前頁面
let pageSize = 10;            // 每頁顯示筆數
let isLoading = false;        // 是否正在載入中

// ===== DOM 載入完成後執行 =====
document.addEventListener('DOMContentLoaded', function () {
    // 初始化會員管理功能
    initMemberManagement();
});

// ===== 會員管理初始化 =====
function initMemberManagement() {
    loadMemberList();
    bindEvents();
}

// ===== 事件綁定 =====
function bindEvents() {
    // 搜索按鈕點擊事件
    $('#searchBtn').on('click', function () {
        currentPage = 1;
        loadMemberList();
    });

    // 重置按鈕點擊事件
    $('#resetBtn').on('click', function () {
        resetSearchForm();
        currentPage = 1;
        loadMemberList();
    });

    // 新增會員按鈕點擊事件
    $('#addMemberBtn').on('click', function () {
        window.location.href = 'member_add.html';
    });

    // 搜尋框 Enter 鍵事件
    $('#searchUserName').on('keypress', function (e) {
        if (e.which === 13) { // Enter鍵
            $('#searchBtn').click();
        }
    });
}

// ===== 重置搜索表單 =====
function resetSearchForm() {
    $('#searchUserName').val('');
    $('#searchStartDate').val('');
    $('#searchEndDate').val('');
    $('#searchRoleLevel').val('');
    $('#searchIsActive').val('');
}

// ===== 載入會員列表 =====
function loadMemberList() {
    // 防止重複載入
    if (isLoading) return;

    isLoading = true;
    showLoading();

    // 收集搜尋參數
    const searchParams = {
        userName: $('#searchUserName').val().trim(),
        startDate: $('#searchStartDate').val(),
        endDate: $('#searchEndDate').val(),
        roleLevel: $('#searchRoleLevel').val(),
        isActive: $('#searchIsActive').val(),
        page: currentPage,
        size: pageSize
    };

    // 移除空值參數
    Object.keys(searchParams).forEach(key => {
        if (searchParams[key] === '' || searchParams[key] === null) {
            delete searchParams[key];
        }
    });

    // 建立查詢字串
    const queryString = new URLSearchParams(searchParams).toString();
    const url = `/maven-tickeasy-v1/api/manager/member/page?${queryString}`;

    // 發送 API 請求
    fetch(url, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        hideLoading();
        if (data.success) {
            displayMemberList(data.data);
        } else {
            showError(data.message || '載入失敗');
        }
    })
    .catch(error => {
        hideLoading();
        console.error('載入會員列表失敗:', error);
        showError('載入會員列表失敗，請檢查網路連線或稍後再試');
    })
    .finally(() => {
        isLoading = false;
    });
}

// ===== 顯示會員列表 =====
function displayMemberList(data) {
    const tbody = $('#memberTableBody');
    const noDataDiv = $('#noDataMessage');
    const paginationContainer = $('#paginationContainer');

    // 檢查是否有資料
    if (!data.members || data.members.length === 0) {
        tbody.empty();
        noDataDiv.removeClass('d-none');
        paginationContainer.addClass('d-none');
        return;
    }

    // 隱藏無資料提示，顯示分頁
    noDataDiv.addClass('d-none');
    paginationContainer.removeClass('d-none');

    // 建立表格內容
    let html = '';
    data.members.forEach(member => {
        html += `
            <tr class="text-center">
                <td>${escapeHtml(member.userName || '')}</td>
                <td>${escapeHtml(member.nickName || '')}</td>
                <td>${escapeHtml(member.email || '')}</td>
                <td>${formatDate(member.createTime)}</td>
                <td>${getRoleLevelBadge(member.roleLevel)}</td>
                <td>${getActiveBadge(member.isActive)}</td>
                <td>
                    <button type="button" class="btn btn-sm btn-warning me-1" 
                            onclick="editMember(${member.memberId})" title="編輯">
                        <i class="bi bi-pencil"></i> 編輯
                    </button>
                    <button type="button" class="btn btn-sm btn-danger" 
                            onclick="deleteMember(${member.memberId})" title="刪除">
                        <i class="bi bi-trash"></i> 刪除
                    </button>
                </td>
            </tr>
        `;
    });

    tbody.html(html);
    updatePagination(data);
}

// ===== 更新分頁 =====
function updatePagination(data) {
    const pageInfo = $('#pageInfo');
    const paginationList = $('#paginationList');

    // 更新分頁資訊
    const startItem = data.totalCount > 0 ? (data.currentPage - 1) * data.pageSize + 1 : 0;
    const endItem = Math.min(data.currentPage * data.pageSize, data.totalCount);
    pageInfo.text(`顯示第 ${startItem} 到 ${endItem} 筆，共 ${data.totalCount} 筆資料`);

    // 生成分頁按鈕
    let paginationHtml = '';

    // 上一頁按鈕
    const prevDisabled = !data.hasPrevious ? 'disabled' : '';
    paginationHtml += `
        <li class="page-item ${prevDisabled}">
            <a class="page-link" href="#" onclick="changePage(${data.currentPage - 1})" tabindex="-1">上一頁</a>
        </li>
    `;

    // 頁碼按鈕邏輯
    const startPage = Math.max(1, data.currentPage - 2);
    const endPage = Math.min(data.totalPages, data.currentPage + 2);

    // 如果不是從第一頁開始，顯示第一頁和省略號
    if (startPage > 1) {
        paginationHtml += `<li class="page-item"><a class="page-link" href="#" onclick="changePage(1)">1</a></li>`;
        if (startPage > 2) {
            paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
    }

    // 顯示當前頁面附近的頁碼
    for (let i = startPage; i <= endPage; i++) {
        const activeClass = i === data.currentPage ? 'active' : '';
        paginationHtml += `
            <li class="page-item ${activeClass}">
                <a class="page-link" href="#" onclick="changePage(${i})">${i}</a>
            </li>
        `;
    }

    // 如果不是到最後一頁，顯示省略號和最後一頁
    if (endPage < data.totalPages) {
        if (endPage < data.totalPages - 1) {
            paginationHtml += `<li class="page-item disabled"><span class="page-link">...</span></li>`;
        }
        paginationHtml += `
            <li class="page-item">
                <a class="page-link" href="#" onclick="changePage(${data.totalPages})">${data.totalPages}</a>
            </li>
        `;
    }

    // 下一頁按鈕
    const nextDisabled = !data.hasNext ? 'disabled' : '';
    paginationHtml += `
        <li class="page-item ${nextDisabled}">
            <a class="page-link" href="#" onclick="changePage(${data.currentPage + 1})">下一頁</a>
        </li>
    `;

    paginationList.html(paginationHtml);
}

// ===== 變更頁面 =====
function changePage(page) {
    if (page < 1 || isLoading) return;
    currentPage = page;
    loadMemberList();
}

// ===== 顯示載入中狀態 =====
function showLoading() {
    $('#loadingIndicator').removeClass('d-none');
    $('#memberTableBody').empty();
    $('#noDataMessage').addClass('d-none');
    $('#paginationContainer').addClass('d-none');
}

// ===== 隱藏載入中狀態 =====
function hideLoading() {
    $('#loadingIndicator').addClass('d-none');
}

// ===== 顯示錯誤訊息 =====
function showError(message) {
    const tbody = $('#memberTableBody');
    tbody.html(`
        <tr>
            <td colspan="7" class="text-center text-danger py-4">
                <i class="bi bi-exclamation-circle display-4"></i>
                <h5 class="mt-2">載入失敗</h5>
                <p>${escapeHtml(message)}</p>
                <button class="btn btn-primary" onclick="loadMemberList()">重新載入</button>
            </td>
        </tr>
    `);
    $('#noDataMessage').addClass('d-none');
    $('#paginationContainer').addClass('d-none');
}

// ===== 工具函數 =====

/**
 * HTML 特殊字元轉換，防止 XSS 攻擊
 * @param {string} text - 需要轉換的文字
 * @returns {string} 轉換後的安全文字
 */
function escapeHtml(text) {
    if (text == null) return '';
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.toString().replace(/[&<>"']/g, function (m) { return map[m]; });
}

/**
 * 格式化日期顯示
 * @param {string} dateString - 日期字串
 * @returns {string} 格式化後的日期
 */
function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-TW', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

/**
 * 取得會員等級標籤
 * @param {number} roleLevel - 會員等級
 * @returns {string} 會員等級 HTML 標籤
 */
function getRoleLevelBadge(roleLevel) {
    const roleMap = {
        0: '<span class="badge bg-secondary">未開通</span>',
        1: '<span class="badge bg-info">購票方</span>',
        2: '<span class="badge bg-warning">活動方</span>',
        3: '<span class="badge bg-success">平台方</span>'
    };
    return roleMap[roleLevel] || '<span class="badge bg-secondary">未知</span>';
}

/**
 * 取得啟用狀態標籤
 * @param {number} isActive - 啟用狀態
 * @returns {string} 啟用狀態 HTML 標籤
 */
function getActiveBadge(isActive) {
    return isActive === 1
        ? '<span class="badge bg-primary">啟用</span>'
        : '<span class="badge bg-secondary">停用</span>';
}

// ===== 會員操作函數 =====

/**
 * 編輯會員
 * @param {number} memberId - 會員ID
 */
function editMember(memberId) {
    if (!memberId) {
        alert('會員ID無效');
        return;
    }
    window.location.href = `member_edit.html?id=${memberId}`;
}

/**
 * 刪除會員
 * @param {number} memberId - 會員ID
 */
function deleteMember(memberId) {
    if (!memberId) {
        alert('會員ID無效');
        return;
    }

    // 確認刪除操作
    if (!confirm('確定要刪除此會員嗎？此操作無法復原。')) {
        return;
    }

    const url = `/maven-tickeasy-v1/api/manager/member/${memberId}`;

    // 發送刪除請求
    fetch(url, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        if (data.success) {
            alert('會員刪除成功！');
            // 重新載入當前頁面，如果當前頁沒有資料則回到第一頁
            loadMemberList();
        } else {
            alert('刪除失敗：' + (data.message || '未知錯誤'));
        }
    })
    .catch(error => {
        console.error('刪除會員失敗:', error);
        alert('刪除會員失敗，請稍後再試');
    });
}

// ===== 匯出函數供全域使用 =====
window.changePage = changePage;
window.editMember = editMember;
window.deleteMember = deleteMember;