(() => {
    let eventId = null;
    let currentTicketTypes = [];
    let editingTicketType = null;
    
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
            btn.addEventListener('click', function() {
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
        
        // 載入票種資料
        loadTicketTypes();
    }

    // 頁面載入後初始化
    document.addEventListener('DOMContentLoaded', function() {
        console.log('DOM 載入完成，準備初始化 EditTicketType');
        init();
    });

    // jQuery 載入後也執行一次（備用）
    $(document).ready(function() {
        console.log('jQuery ready，確保初始化');
        if (!eventId) {
            init();
        }
    });
})();