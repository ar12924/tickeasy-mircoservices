(() => {
    let eventId = null;
    let currentTicketTypes = [];
    let editingTicketType = null;
    let eventInfo = null;

    // DOM 元素
    const loadingMessage = document.querySelector('#loadingMessage');
    const ticketTypeContainer = document.querySelector('#ticketTypeContainer');
    const ticketTypeList = document.querySelector('#ticketTypeList');
    const emptyState = document.querySelector('#emptyState');
    const msg = document.querySelector('#msg');

    // 新增票種相關元素
    const addTicketForm = document.querySelector('#addTicketForm');
    const addButtonContainer = document.querySelector('#addButtonContainer');
    const btnShowAddForm = document.querySelector('#btnShowAddForm');
    const btnAddFirstTicket = document.querySelector('#btnAddFirstTicket');
    const btnCancelAdd = document.querySelector('#btnCancelAdd');
    const btnCancelAdd2 = document.querySelector('#btnCancelAdd2');
    const btnSaveNewTicket = document.querySelector('#btnSaveNewTicket');
    const addFormMsg = document.querySelector('#addFormMsg');

    // 新增表單元素
    const addCategoryName = document.querySelector('#add_category_name');
    const addSellFromTime = document.querySelector('#add_sell_from_time');
    const addSellToTime = document.querySelector('#add_sell_to_time');
    const addPrice = document.querySelector('#add_price');
    const addCapacity = document.querySelector('#add_capacity');

    // Modal 元素
    let ticketTypeModal;
    const modalTitle = document.querySelector('#ticketTypeModalLabel');
    const modalMsg = document.querySelector('#modalMsg');
    const btnSaveTicket = document.querySelector('#btnSaveTicket');
    const btnDeleteTicket = document.querySelector('#btnDeleteTicket');

    // Modal 表單元素
    const modalTypeId = document.querySelector('#modal_type_id');
    const modalEventId = document.querySelector('#modal_event_id');
    const modalCategoryName = document.querySelector('#modal_category_name');
    const modalSellFromTime = document.querySelector('#modal_sell_from_time');
    const modalSellToTime = document.querySelector('#modal_sell_to_time');
    const modalPrice = document.querySelector('#modal_price');
    const modalCapacity = document.querySelector('#modal_capacity');

    // 從 URL 獲取活動ID
    function getEventIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('eventId');
    }

    // 格式化日期時間為 datetime-local 格式
    function formatDateTimeLocal(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toISOString().slice(0, 16);
    }

    // 格式化顯示日期時間
    function formatDisplayDateTime(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleString('zh-TW');
    }


    // ✅ 新增：載入活動資訊
    async function loadEventInfo() {
        try {
            console.log('載入活動資訊，活動ID:', eventId);

            const response = await fetch(`/maven-tickeasy-v1/manager/event/${eventId}/info`);

            if (!response.ok) {
                throw new Error(`載入活動資訊失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('活動資訊回應:', result);

            if (result.successful) {
                eventInfo = result.data;
                console.log('活動資訊:', eventInfo);
            } else {
                throw new Error(result.message || '載入活動資訊失敗');
            }
        } catch (error) {
            console.error('載入活動資訊錯誤:', error);
            eventInfo = null;
        }
    }

    // ✅ 新增：計算目前所有票種的總數量
    function getCurrentTotalCapacity() {
        return currentTicketTypes.reduce((total, ticket) => {
            return total + (ticket.capacity || 0);
        }, 0);
    }

    // ✅ 新增：監聽票種數量輸入
    function setupCapacityValidation() {
        addCapacity.addEventListener('input', function () {
            const currentTotal = getCurrentTotalCapacity();
            const newCapacity = parseInt(this.value, 10) || 0;
            const totalWithNew = currentTotal + newCapacity;
            const eventTotalCapacity = eventInfo ? eventInfo.totalCapacity : 0;

            console.log('=== 票種數量驗證 ===');
            console.log('目前票種總數量:', currentTotal);
            console.log('新增票種數量:', newCapacity);
            console.log('總計數量:', totalWithNew);
            console.log('活動總人數上限:', eventTotalCapacity);

            // 清除之前的錯誤訊息
            const existingError = document.querySelector('#capacity-error');
            if (existingError) {
                existingError.remove();
            }

            // 如果活動有設定總人數上限且超過限制
            if (eventTotalCapacity > 0 && totalWithNew > eventTotalCapacity) {
                // 顯示錯誤訊息
                const errorDiv = document.createElement('div');
                errorDiv.id = 'capacity-error';
                errorDiv.className = 'text-danger mt-1';
                errorDiv.innerHTML = `
                    <small><i class="bi bi-exclamation-triangle"></i> 
                    票種總數量 (${totalWithNew}) 不能超過活動總人數上限 (${eventTotalCapacity})<br>
                    目前已有票種數量：${currentTotal}，最多還能新增：${eventTotalCapacity - currentTotal}</small>
                `;

                // 將錯誤訊息插入到 capacity 輸入框後面
                this.parentElement.appendChild(errorDiv);

                // 設定輸入框為錯誤狀態
                this.classList.add('is-invalid');

                // 禁用新增按鈕
                btnSaveNewTicket.disabled = true;
            } else {
                // 移除錯誤狀態
                this.classList.remove('is-invalid');

                // 啟用新增按鈕
                btnSaveNewTicket.disabled = false;
            }
        });
    }


    // 載入票種資料
    async function loadTicketTypes() {
        try {
            console.log('開始載入票種資料，活動ID:', eventId);

            const response = await fetch(`/maven-tickeasy-v1/manager/ticket-type/event/${eventId}`);

            if (!response.ok) {
                throw new Error(`載入失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('票種資料回應:', result);

            if (result.successful) {
                currentTicketTypes = result.data || [];
                displayTicketTypes();

                // ✅ 新增：顯示票種統計資訊
                displayTicketSummary();

                // 隱藏載入訊息，顯示內容
                loadingMessage.style.display = 'none';
                ticketTypeContainer.style.display = 'block';
            } else {
                throw new Error(result.message || '載入票種資料失敗');
            }
        } catch (error) {
            console.error('載入票種資料錯誤:', error);
            loadingMessage.innerHTML = `
                <i class="bi bi-exclamation-triangle text-danger"></i>
                <p class="text-danger">載入失敗：${error.message}</p>
                <a href="../index.html" class="btn btn-secondary">返回活動列表</a>
            `;
        }
    }


    // ✅ 新增：顯示票種統計資訊
    function displayTicketSummary() {
        const currentTotal = getCurrentTotalCapacity();
        const eventTotalCapacity = eventInfo ? eventInfo.totalCapacity : 0;

        // 在票種列表上方顯示統計資訊
        let summaryHtml = `
            <div class="alert alert-info mb-3">
                <div class="row">
                    <div class="col-md-6">
                        <strong><i class="bi bi-info-circle"></i> 票種統計</strong><br>
                        目前票種總數量：<span class="badge bg-primary">${currentTotal}</span>
                    </div>
        `;

        if (eventTotalCapacity > 0) {
            const remaining = eventTotalCapacity - currentTotal;
            const percentage = Math.round((currentTotal / eventTotalCapacity) * 100);

            summaryHtml += `
                    <div class="col-md-6">
                        活動總人數上限：<span class="badge bg-secondary">${eventTotalCapacity}</span><br>
                        剩餘可分配：<span class="badge ${remaining > 0 ? 'bg-success' : 'bg-danger'}">${remaining}</span>
                        <small class="text-muted">(${percentage}% 已分配)</small>
                    </div>
            `;
        }

        summaryHtml += `
                </div>
            </div>
        `;

        // 移除舊的統計資訊
        const existingSummary = document.querySelector('#ticket-summary');
        if (existingSummary) {
            existingSummary.remove();
        }

        // 加入新的統計資訊
        const summaryDiv = document.createElement('div');
        summaryDiv.id = 'ticket-summary';
        summaryDiv.innerHTML = summaryHtml;
        ticketTypeList.parentNode.insertBefore(summaryDiv, ticketTypeList);
    }




    // 顯示票種列表
    function displayTicketTypes() {
        if (currentTicketTypes.length === 0) {
            ticketTypeList.innerHTML = '';
            emptyState.style.display = 'block';
            addButtonContainer.style.display = 'none';
            hideAddForm();
            return;
        }

        emptyState.style.display = 'none';
        addButtonContainer.style.display = 'block';

        const ticketTypeCards = currentTicketTypes.map(ticketType => `
            <div class="ticket-type-card" data-type-id="${ticketType.typeId}">
                <div class="ticket-type-header">
                    <h5 class="mb-0">${ticketType.categoryName}</h5>
                    <div>
                        <button class="btn btn-sm btn-outline-primary btn-edit-ticket" data-type-id="${ticketType.typeId}">
                            <i class="bi bi-pencil"></i> 編輯
                        </button>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <p class="mb-1"><strong>販售時間：</strong></p>
                        <p class="text-muted">${formatDisplayDateTime(ticketType.sellFromTime)} <br>
                        至 ${formatDisplayDateTime(ticketType.sellToTime)}</p>
                    </div>
                    <div class="col-md-3">
                        <p class="mb-1"><strong>票價：</strong></p>
                        <p class="text-muted">NT$ ${ticketType.price}</p>
                    </div>
                    <div class="col-md-3">
                        <p class="mb-1"><strong>數量：</strong></p>
                        <p class="text-muted">${ticketType.capacity} 張</p>
                    </div>
                </div>
            </div>
        `).join('');

        ticketTypeList.innerHTML = ticketTypeCards;

        // 綁定編輯按鈕事件
        document.querySelectorAll('.btn-edit-ticket').forEach(btn => {
            btn.addEventListener('click', function () {
                const typeId = parseInt(this.getAttribute('data-type-id'));
                editTicketType(typeId);
            });
        });
    }

    // 編輯票種
    function editTicketType(typeId) {
        const ticketType = currentTicketTypes.find(t => t.typeId === typeId);
        if (!ticketType) {
            console.error('找不到票種ID:', typeId);
            return;
        }

        editingTicketType = ticketType;

        // 設定 Modal 標題和按鈕
        modalTitle.textContent = '編輯票種';
        btnDeleteTicket.style.display = 'block';

        // 填入表單資料
        modalTypeId.value = ticketType.typeId;
        modalEventId.value = eventId;
        modalCategoryName.value = ticketType.categoryName;
        modalSellFromTime.value = formatDateTimeLocal(ticketType.sellFromTime);
        modalSellToTime.value = formatDateTimeLocal(ticketType.sellToTime);
        modalPrice.value = ticketType.price;
        modalCapacity.value = ticketType.capacity;

        // 清除訊息
        modalMsg.innerHTML = '';

        // 顯示 Modal
        ticketTypeModal.show();
    }

    // 顯示新增表單
    function showAddForm() {
        // 清空表單
        addCategoryName.value = '';
        addSellFromTime.value = '';
        addSellToTime.value = '';
        addPrice.value = '';
        addCapacity.value = '';

        // 如果有現有票種，自動帶入第一個票種的販售時間
        if (currentTicketTypes.length > 0) {
            const firstTicketType = currentTicketTypes[0];

            function formatDateTimeLocal(dateString) {
                if (!dateString) return '';

                // 創建日期物件
                let date = new Date(dateString);

                // 如果資料庫回傳的是 UTC 時間，需要轉換為本地時間
                // 檢查是否需要加上時區偏移
                const timezoneOffset = date.getTimezoneOffset() * 60000; // 轉換為毫秒
                date = new Date(date.getTime() - timezoneOffset);

                return date.toISOString().slice(0, 16);
            }

            console.log('自動帶入第一個票種的販售時間:', firstTicketType);

            addSellFromTime.value = formatDateTimeLocal(firstTicketType.sellFromTime);
            addSellToTime.value = formatDateTimeLocal(firstTicketType.sellToTime);

            console.log('設定販售開始時間:', addSellFromTime.value);
            console.log('設定販售結束時間:', addSellToTime.value);
        }

        // 清除訊息
        addFormMsg.innerHTML = '';

        // 顯示表單，隱藏按鈕
        addTicketForm.classList.add('show');
        addButtonContainer.style.display = 'none';

        // 聚焦到第一個輸入框
        addCategoryName.focus();
    }

    // 隱藏新增表單
    function hideAddForm() {
        addTicketForm.classList.remove('show');
        if (currentTicketTypes.length > 0) {
            addButtonContainer.style.display = 'block';
        }
        addFormMsg.innerHTML = '';
    }

    // 驗證新增表單
    function validateAddForm() {
        if (!addCategoryName.value.trim()) {
            return '票種名稱不可為空';
        }

        if (!addSellFromTime.value || !addSellToTime.value) {
            return '請填寫販售時間';
        }

        if (new Date(addSellFromTime.value) >= new Date(addSellToTime.value)) {
            return '販售結束時間必須大於開始時間';
        }

        if (!addPrice.value || parseFloat(addPrice.value) < 0) {
            return '票價不可為負數';
        }

        if (!addCapacity.value || parseInt(addCapacity.value) <= 0) {
            return '票券數量必須大於0';
        }

        // ✅ 新增：檢查票種總數量限制
        const currentTotal = getCurrentTotalCapacity();
        const newCapacity = parseInt(addCapacity.value, 10) || 0;
        const totalWithNew = currentTotal + newCapacity;
        const eventTotalCapacity = eventInfo ? eventInfo.totalCapacity : 0;

        if (eventTotalCapacity > 0 && totalWithNew > eventTotalCapacity) {
            return `票種總數量 (${totalWithNew}) 不能超過活動總人數上限 (${eventTotalCapacity})`;
        }

        return null;
    }

    // 儲存新票種
    async function saveNewTicketType() {
        const validationError = validateAddForm();
        if (validationError) {
            addFormMsg.innerHTML = `<div class="alert alert-danger">${validationError}</div>`;
            return;
        }

        try {
            addFormMsg.innerHTML = '<div class="alert alert-info">新增中...</div>';
            btnSaveNewTicket.disabled = true;

            const appendSeconds = (t) => t.length === 16 ? t + ':00' : t;

            const ticketTypeData = {
                categoryName: addCategoryName.value.trim(),
                sellFromTime: appendSeconds(addSellFromTime.value),
                sellToTime: appendSeconds(addSellToTime.value),
                price: parseFloat(addPrice.value),
                capacity: parseInt(addCapacity.value),
                eventId: parseInt(eventId)
            };

            console.log('準備送出新票種資料:', ticketTypeData);

            const response = await fetch('/maven-tickeasy-v1/manager/ticket-type', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(ticketTypeData)
            });

            if (!response.ok) {
                throw new Error(`新增失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('新增票種回應:', result);

            if (result.successful) {
                addFormMsg.innerHTML = `<div class="alert alert-success">${result.message}</div>`;

                // 重新載入票種列表
                setTimeout(async () => {
                    hideAddForm();
                    await loadTicketTypes();

                    // 顯示成功訊息
                    msg.innerHTML = `<div class="alert alert-success">${result.message}</div>`;
                    setTimeout(() => {
                        msg.innerHTML = '';
                    }, 3000);
                }, 1000);
            } else {
                throw new Error(result.message || '新增失敗');
            }

        } catch (error) {
            console.error('新增票種錯誤:', error);
            addFormMsg.innerHTML = `<div class="alert alert-danger">新增失敗：${error.message}</div>`;
        } finally {
            btnSaveNewTicket.disabled = false;
        }
    }

    // 驗證表單
    function validateForm() {
        if (!modalCategoryName.value.trim()) {
            return '票種名稱不可為空';
        }

        if (!modalSellFromTime.value || !modalSellToTime.value) {
            return '請填寫販售時間';
        }

        if (new Date(modalSellFromTime.value) >= new Date(modalSellToTime.value)) {
            return '販售結束時間必須大於開始時間';
        }

        if (!modalPrice.value || parseFloat(modalPrice.value) < 0) {
            return '票價不可為負數';
        }

        if (!modalCapacity.value || parseInt(modalCapacity.value) <= 0) {
            return '票券數量必須大於0';
        }

        return null;
    }

    // 儲存票種
    async function saveTicketType() {
        const validationError = validateForm();
        if (validationError) {
            modalMsg.innerHTML = `<div class="alert alert-danger">${validationError}</div>`;
            return;
        }

        try {
            modalMsg.innerHTML = '<div class="alert alert-info">處理中...</div>';
            btnSaveTicket.disabled = true;

            const appendSeconds = (t) => t.length === 16 ? t + ':00' : t;

            const ticketTypeData = {
                categoryName: modalCategoryName.value.trim(),
                sellFromTime: appendSeconds(modalSellFromTime.value),
                sellToTime: appendSeconds(modalSellToTime.value),
                price: parseFloat(modalPrice.value),
                capacity: parseInt(modalCapacity.value),
                eventId: parseInt(eventId)
            };

            let response, url, method;

            if (editingTicketType) {
                // 更新票種
                ticketTypeData.typeId = editingTicketType.typeId;
                url = `/maven-tickeasy-v1/manager/ticket-type/${editingTicketType.typeId}`;
                method = 'PUT';
            } else {
                // 新增票種
                url = '/maven-tickeasy-v1/manager/ticket-type';
                method = 'POST';
            }

            console.log('準備送出票種資料:', ticketTypeData);

            response = await fetch(url, {
                method: method,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(ticketTypeData)
            });

            if (!response.ok) {
                throw new Error(`操作失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('票種操作回應:', result);

            if (result.successful) {
                modalMsg.innerHTML = `<div class="alert alert-success">${result.message}</div>`;

                // 重新載入票種列表
                setTimeout(async () => {
                    ticketTypeModal.hide();
                    await loadTicketTypes();

                    // 顯示成功訊息
                    msg.innerHTML = `<div class="alert alert-success">${result.message}</div>`;
                    setTimeout(() => {
                        msg.innerHTML = '';
                    }, 3000);
                }, 1000);
            } else {
                throw new Error(result.message || '操作失敗');
            }

        } catch (error) {
            console.error('儲存票種錯誤:', error);
            modalMsg.innerHTML = `<div class="alert alert-danger">操作失敗：${error.message}</div>`;
        } finally {
            btnSaveTicket.disabled = false;
        }
    }

    // 刪除票種
    async function deleteTicketType() {
        if (!editingTicketType) return;

        if (!confirm(`確定要刪除票種「${editingTicketType.categoryName}」嗎？此操作無法復原。`)) {
            return;
        }

        try {
            modalMsg.innerHTML = '<div class="alert alert-info">刪除中...</div>';
            btnDeleteTicket.disabled = true;

            const response = await fetch(`/maven-tickeasy-v1/manager/ticket-type/${editingTicketType.typeId}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                throw new Error(`刪除失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('刪除票種回應:', result);

            if (result.successful) {
                modalMsg.innerHTML = `<div class="alert alert-success">${result.message}</div>`;

                // 重新載入票種列表
                setTimeout(async () => {
                    ticketTypeModal.hide();
                    await loadTicketTypes();

                    // 顯示成功訊息
                    msg.innerHTML = `<div class="alert alert-success">${result.message}</div>`;
                    setTimeout(() => {
                        msg.innerHTML = '';
                    }, 3000);
                }, 1000);
            } else {
                throw new Error(result.message || '刪除失敗');
            }

        } catch (error) {
            console.error('刪除票種錯誤:', error);
            modalMsg.innerHTML = `<div class="alert alert-danger">刪除失敗：${error.message}</div>`;
        } finally {
            btnDeleteTicket.disabled = false;
        }
    }

    // 初始化
    function init() {
        console.log('EditTicketType.js 開始初始化');

        eventId = getEventIdFromUrl();
        console.log('從URL取得活動ID:', eventId);

        if (!eventId) {
            loadingMessage.innerHTML = `
                <i class="bi bi-exclamation-triangle text-danger"></i>
                <p class="text-danger">無效的活動ID</p>
                <a href="../index.html" class="btn btn-secondary">返回活動列表</a>
            `;
            return;
        }

        // 初始化 Modal
        ticketTypeModal = new bootstrap.Modal(document.getElementById('ticketTypeModal'));

        // 綁定事件
        btnShowAddForm.addEventListener('click', showAddForm);
        btnAddFirstTicket.addEventListener('click', showAddForm);
        btnCancelAdd.addEventListener('click', hideAddForm);
        btnCancelAdd2.addEventListener('click', hideAddForm);
        btnSaveNewTicket.addEventListener('click', saveNewTicketType);
        btnSaveTicket.addEventListener('click', saveTicketType);
        btnDeleteTicket.addEventListener('click', deleteTicketType);
        // ✅ 新增：設定票種數量驗證
        setupCapacityValidation();

        // ✅ 修改：先載入活動資訊，再載入票種資料
        loadEventInfo().then(() => {
            loadTicketTypes();
        });
    }

    // 頁面載入後初始化
    document.addEventListener('DOMContentLoaded', function () {
        console.log('DOM 載入完成，準備初始化 EditTicketType');
        init();
    });

    // jQuery 載入後也執行一次（備用）
    $(document).ready(function () {
        console.log('jQuery ready，確保初始化');
        if (!eventId) {
            init();
        }
    });
})();