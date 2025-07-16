(() => {
    let eventId = null;
    let orderTable = null;
    let orderDetailModal = null;

    // DOM 元素
    const loadingMessage = document.querySelector('#loadingMessage');
    const orderContainer = document.querySelector('#orderContainer');
    const msg = document.querySelector('#msg');
    const eventTitle = document.querySelector('#eventTitle');
    
    // 統計元素
    const totalOrdersEl = document.querySelector('#totalOrders');
    const paidOrdersEl = document.querySelector('#paidOrders');
    const unpaidOrdersEl = document.querySelector('#unpaidOrders');
    const totalRevenueEl = document.querySelector('#totalRevenue');

    // 從 URL 獲取活動ID
    function getEventIdFromUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        return urlParams.get('eventId');
    }

    // 格式化訂單編號為9位數字
    function formatOrderNumber(orderId) {
        return orderId.toString().padStart(9, '0');
    }

    // 格式化日期時間
    function formatDateTime(dateString) {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleString('zh-TW', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    // 格式化金額
    function formatCurrency(amount) {
        return `NT$ ${parseFloat(amount).toLocaleString()}`;
    }

    // 取得付款狀態徽章
    function getPaymentStatusBadge(isPaid) {
        if (isPaid) {
            return '<span class="badge bg-success order-status-badge">已付款</span>';
        } else {
            return '<span class="badge bg-warning order-status-badge">未付款</span>';
        }
    }

    // 取得訂單狀態徽章
    function getOrderStatusBadge(status) {
        const statusConfig = {
            '已完成': 'bg-success',
            '進行中': 'bg-primary',
            '已取消': 'bg-danger',
            '待付款': 'bg-warning',
            '已退款': 'bg-secondary'
        };
        
        const badgeClass = statusConfig[status] || 'bg-secondary';
        return `<span class="badge ${badgeClass} order-status-badge">${status}</span>`;
    }

    // 載入活動資訊
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
                eventTitle.textContent = `活動：${result.data.eventName}`;
            } else {
                throw new Error(result.message || '載入活動資訊失敗');
            }
        } catch (error) {
            console.error('載入活動資訊錯誤:', error);
            eventTitle.textContent = '活動資訊載入失敗';
        }
    }

    // 載入訂單資料
    // async function loadOrders() {
    //     try {
    //         console.log('開始載入訂單資料，活動ID:', eventId);

    //         const response = await fetch(`/maven-tickeasy-v1/manager/orders/event/${eventId}`);

    //         if (!response.ok) {
    //             throw new Error(`載入失敗，狀態碼：${response.status}`);
    //         }

    //         const result = await response.json();
    //         console.log('訂單資料回應:', result);

    //         if (result.successful) {
    //             const orders = result.data || [];
    //             initializeDataTable(orders);
    //             updateStatistics(orders);

    //             // 隱藏載入訊息，顯示內容
    //             loadingMessage.style.display = 'none';
    //             orderContainer.style.display = 'block';
    //         } else {
    //             throw new Error(result.message || '載入訂單資料失敗');
    //         }
    //     } catch (error) {
    //         console.error('載入訂單資料錯誤:', error);
    //         loadingMessage.innerHTML = `
    //             <i class="bi bi-exclamation-triangle text-danger"></i>
    //             <p class="text-danger">載入失敗：${error.message}</p>
    //             <a href="../index.html" class="btn btn-secondary">返回活動列表</a>
    //         `;
    //     }
    // }

    async function loadOrders() {
    try {
        console.log('開始載入訂單資料，活動ID:', eventId);

        const response = await fetch(`/maven-tickeasy-v1/manager/orders/event/${eventId}`);

        if (!response.ok) {
            throw new Error(`載入失敗，狀態碼：${response.status}`);
        }

        const result = await response.json();
        console.log('訂單資料回應:', result);
        
        // 詳細除錯輸出
        console.log('=== 除錯資訊 ===');
        console.log('result:', result);
        console.log('result.successful:', result.successful);
        console.log('successful 型別:', typeof result.successful);
        console.log('result.data:', result.data);
        console.log('data 型別:', typeof result.data);
        console.log('data 是陣列:', Array.isArray(result.data));
        console.log('data 長度:', result.data?.length);
        console.log('================');

        // 暫時跳過檢查，直接處理資料
        // let orders = [];
        
        // if (result.data && Array.isArray(result.data)) {
        //     orders = result.data;
        // } else if (result.data && typeof result.data === 'object') {
        //     // 如果 data 是物件但不是陣列，看看是否有其他屬性包含陣列
        //     console.log('data 物件的屬性:', Object.keys(result.data));
        //     orders = [];
        // } else {
        //     console.log('無法識別的 data 格式');
        //     orders = [];
        // }

        // console.log('最終處理的訂單資料:', orders);


        // ✅ 由於後端直接回傳陣列，所以直接使用
        const orders = Array.isArray(result) ? result : [];

        
        // 不管成功失敗，都嘗試顯示資料
        initializeDataTable(orders);
        updateStatistics(orders);

        // 隱藏載入訊息，顯示內容
        loadingMessage.style.display = 'none';
        orderContainer.style.display = 'block';
        
        // 如果有資料但檢查失敗，顯示警告
        // if (orders.length === 0 && result.data) {
        //     msg.innerHTML = `
        //         <div class="alert alert-warning">
        //             <i class="bi bi-exclamation-triangle"></i>
        //             資料格式異常，請檢查後端回傳格式
        //         </div>
        //     `;
        // }

    } catch (error) {
        console.error('載入訂單資料錯誤:', error);
        loadingMessage.innerHTML = `
            <i class="bi bi-exclamation-triangle text-danger"></i>
            <p class="text-danger">載入失敗：${error.message}</p>
            <a href="../index.html" class="btn btn-secondary">返回活動列表</a>
        `;
    }
}






    // 初始化 DataTable
    function initializeDataTable(orders) {
        // 準備表格資料
        const tableData = orders.map(order => [
            `<span class="order-number">${formatOrderNumber(order.orderId)}</span>`,
            order.eventName || '未知活動',
            formatDateTime(order.orderTime),
            formatCurrency(order.totalAmount),
            getPaymentStatusBadge(order.isPaid),
            getOrderStatusBadge(order.orderStatus),
            `<button class="btn btn-sm btn-outline-primary btn-order-detail" data-order-id="${order.orderId}">
                <i class="bi bi-list-ul"></i> 明細
            </button>`
        ]);

        // 如果 DataTable 已存在，先銷毀
        if (orderTable) {
            orderTable.destroy();
        }

        // 初始化 DataTable
        orderTable = $('#orderTable').DataTable({
            data: tableData,
            pageLength: 25,
            lengthMenu: [[10, 25, 50, 100], [10, 25, 50, 100]],
            order: [[2, 'desc']], // 按訂單時間降序排列
            language: {
                url: 'https://cdn.datatables.net/plug-ins/1.13.7/i18n/zh-HANT.json'
            },
            columnDefs: [
                { orderable: false, targets: -1 }, // 最後一列（操作）不可排序
                { className: 'text-center', targets: [4, 5, 6] } // 置中對齊
            ],
            drawCallback: function() {
                // 重新綁定明細按鈕事件
                bindDetailButtons();
            }
        });

        console.log('DataTable 初始化完成，共', orders.length, '筆訂單');
    }

    // 綁定明細按鈕事件
    function bindDetailButtons() {
        document.querySelectorAll('.btn-order-detail').forEach(btn => {
            btn.addEventListener('click', function() {
                const orderId = this.getAttribute('data-order-id');
                showOrderDetail(orderId);
            });
        });
    }

    // 顯示訂單明細
    async function showOrderDetail(orderId) {
        try {
            console.log('載入訂單明細，訂單ID:', orderId);

            // 顯示載入中
            document.querySelector('#orderDetailContent').innerHTML = `
                <div class="text-center p-4">
                    <i class="bi bi-hourglass-split"></i>
                    <p>載入明細中...</p>
                </div>
            `;

            // 顯示 Modal
            orderDetailModal.show();

            // 載入訂單明細
            const response = await fetch(`/maven-tickeasy-v1/manager/orders/${orderId}/detail`);

            if (!response.ok) {
                throw new Error(`載入明細失敗，狀態碼：${response.status}`);
            }

            const result = await response.json();
            console.log('訂單明細回應:', result);

            // if (result.successful) {
            //     displayOrderDetail(result.data);
            // } else {
            //     throw new Error(result.message || '載入訂單明細失敗');
            // }
            // ✅ 由於後端直接回傳物件，所以直接使用
        if (orderDetail) {
            displayOrderDetail(orderDetail);
        } else {
            throw new Error('找不到訂單明細');
        }

        } catch (error) {
            console.error('載入訂單明細錯誤:', error);
            document.querySelector('#orderDetailContent').innerHTML = `
                <div class="alert alert-danger">
                    <i class="bi bi-exclamation-triangle"></i>
                    載入明細失敗：${error.message}
                </div>
            `;
        }
    }

    // 顯示訂單明細內容
    function displayOrderDetail(orderDetail) {
        const content = `
            <div class="row mb-3">
                <div class="col-md-6">
                    <h6><i class="bi bi-receipt"></i> 訂單資訊</h6>
                    <table class="table table-borderless table-sm">
                        <tr>
                            <td class="fw-bold">訂單編號：</td>
                            <td class="order-number">${formatOrderNumber(orderDetail.orderId)}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">活動名稱：</td>
                            <td>${orderDetail.eventName}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">訂單時間：</td>
                            <td>${formatDateTime(orderDetail.orderTime)}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">付款狀態：</td>
                            <td>${getPaymentStatusBadge(orderDetail.isPaid)}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">訂單狀態：</td>
                            <td>${getOrderStatusBadge(orderDetail.orderStatus)}</td>
                        </tr>
                    </table>
                </div>
                <div class="col-md-6">
                    <h6><i class="bi bi-person"></i> 購買者資訊</h6>
                    <table class="table table-borderless table-sm">
                        <tr>
                            <td class="fw-bold">會員ID：</td>
                            <td>${orderDetail.memberId || '未知'}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">購買時間：</td>
                            <td>${formatDateTime(orderDetail.createTime)}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold">最後更新：</td>
                            <td>${formatDateTime(orderDetail.updateTime)}</td>
                        </tr>
                    </table>
                </div>
            </div>

            <hr>

            <div class="row">
                <div class="col-12">
                    <h6><i class="bi bi-list-ul"></i> 訂單明細項目</h6>
                    <div class="table-responsive">
                        <table class="table table-striped">
                            <thead>
                                <tr>
                                    <th>票種名稱</th>
                                    <th class="text-center">數量</th>
                                    <th class="text-end">單價</th>
                                    <th class="text-end">小計</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${orderDetail.items ? orderDetail.items.map(item => `
                                    <tr>
                                        <td>${item.ticketTypeName}</td>
                                        <td class="text-center">${item.quantity}</td>
                                        <td class="text-end">${formatCurrency(item.unitPrice)}</td>
                                        <td class="text-end">${formatCurrency(item.subtotal)}</td>
                                    </tr>
                                `).join('') : '<tr><td colspan="4" class="text-center text-muted">無明細資料</td></tr>'}
                            </tbody>
                            <tfoot>
                                <tr class="fw-bold">
                                    <td colspan="3" class="text-end">總計：</td>
                                    <td class="text-end">${formatCurrency(orderDetail.totalAmount)}</td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        `;

        document.querySelector('#orderDetailContent').innerHTML = content;
    }

    // 更新統計資訊
    function updateStatistics(orders) {
        const totalOrders = orders.length;
        const paidOrders = orders.filter(order => order.isPaid).length;
        const unpaidOrders = totalOrders - paidOrders;
        const totalRevenue = orders.reduce((sum, order) => sum + parseFloat(order.totalAmount || 0), 0);

        totalOrdersEl.textContent = totalOrders;
        paidOrdersEl.textContent = paidOrders;
        unpaidOrdersEl.textContent = unpaidOrders;
        totalRevenueEl.textContent = formatCurrency(totalRevenue);

        console.log('統計資訊更新:', {
            totalOrders,
            paidOrders,
            unpaidOrders,
            totalRevenue
        });
    }

    // 初始化
    function init() {
        console.log('event-order-list.js 開始初始化');

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
        orderDetailModal = new bootstrap.Modal(document.getElementById('orderDetailModal'));

        // 載入資料
        loadEventInfo();
        loadOrders();
    }

    // 頁面載入後初始化
    document.addEventListener('DOMContentLoaded', function() {
        console.log('DOM 載入完成，準備初始化 event-order-list');
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