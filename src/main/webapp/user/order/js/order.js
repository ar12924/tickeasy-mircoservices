const order_nav_span_all_el = document.querySelector(".all_order");
const order_nav_span_pending_el = document.querySelector(".pending_order");
const order_nav_span_paid_el = document.querySelector(".paid_order");
const order_nav_span_cancelled_el = document.querySelector(".cancelled_order");

const order_el = document.getElementById("order");

// 點擊展開/收合訂單詳情
document.addEventListener("click", function(e) {
	if(e.target.classList.contains("order_more_")) {
		e.preventDefault();
		const order_more_target = e.target.closest(".order_item");
		const order_more_content = e.target.closest(".order_region").nextElementSibling;
		
		console.log(order_more_content);
		order_more_target.classList.toggle("-on");
		order_more_content.classList.toggle("-on");
		
		const allOrderElements = document.querySelectorAll(".order_item");
		const isLastOrder = order_more_target === allOrderElements[allOrderElements.length - 1];

		if (isLastOrder && order_more_target.classList.contains("-on")) {
			const scrollContainer = order_more_target.closest('#order'); 
			smoothScrollTo(scrollContainer, scrollContainer.scrollHeight, 1000);
		}
	}
});

// 平滑滾動函數
function smoothScrollTo(element, targetScrollTop, duration = 600) {
  const startScrollTop = element.scrollTop;
  const distance = targetScrollTop - startScrollTop;
  let startTime = null;

  function animation(currentTime) {
    if (!startTime) startTime = currentTime;
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);
    const ease = progress < 0.5 
      ? 2 * progress * progress 
      : -1 + (4 - 2 * progress) * progress;

    element.scrollTop = startScrollTop + distance * ease;

    if (elapsed < duration) {
      requestAnimationFrame(animation);
    }
  }

  requestAnimationFrame(animation);
}

// 載入訂單資料
function order_loaded(category) {
    var count = 0;
    
    // ✅ 從 sessionStorage 取得當前登入會員的 ID
    const memberId = sessionStorage.getItem("memberId");
    
    if (!memberId) {
        console.error('找不到會員ID，請重新登入');
        order_isEmpty(0);
        return;
    }
    
    console.log(`載入分類 ${category} 的訂單，會員ID: ${memberId}`);
    
    fetch('/maven-tickeasy-v1/order/order-list', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            memberId: parseInt(memberId)  // ✅ 使用動態會員ID
        })
    })
    .then(resp => {
        console.log('訂單API回應狀態:', resp.status);
        return resp.json();
    })
    .then(result => {
        console.log('訂單API完整回應:', result);
        
        // ✅ 修正：處理 API 回應格式
        let ordersView = [];
        if (result.successful && Array.isArray(result.data)) {
            ordersView = result.data;
        } else if (Array.isArray(result)) {
            ordersView = result;
        } else {
            console.error('無法解析的回應格式:', result);
            order_isEmpty(0);
            return;
        }
        
        console.log('處理後的訂單陣列:', ordersView);
        console.log('陣列長度:', ordersView.length);
        
        // ✅ 根據分類篩選並顯示訂單
        for (let orderView of ordersView) {
            console.log(`檢查訂單 ${orderView.orderId}，分類 ${category}:`, orderView);
            
            if(shouldShowOrder(orderView, category)) {
                count++;
                console.log(`✅ 顯示訂單: ${orderView.orderId}`, orderView);
                
                // ✅ 處理活動圖片顯示
                const eventImageSrc = orderView.eventImage 
                    ? orderView.eventImage 
                    : "../../common/images/activityPic.png"; // 預設圖片
                
                order_el.insertAdjacentHTML("afterbegin", `
                    <div class="order_item">
                        <div class="order_region order_left">
                            <img src="${eventImageSrc}" alt="活動圖片" onerror="this.src='../../common/images/activityPic.png'">
                        </div>
                        <div class="order_region order_center">
                            <div class="order_title">${orderView.eventName || `活動 ${orderView.eventId}`}</div>
                            <div class="order_content">
                                <div class="order_content_set">
                                    <div class="order_content_title">訂單編號：</div>
                                    <div class="order_content_text">${orderView.orderId || 'N/A'}</div>
                                </div>
                                <div class="order_content_set">
                                    <div class="order_content_title">活動時間：</div>
                                    <div class="order_content_text">${orderView.eventFromDate || 'N/A'}</div>
                                </div>
                                <div class="order_content_set">
                                    <div class="order_content_title">活動地點：</div>
                                    <div class="order_content_text">${orderView.place || 'N/A'}</div>
                                </div>
                                <div class="order_content_set">
                                    <div class="order_content_title">訂單金額：</div>
                                    <div class="order_content_text">NT$ ${orderView.totalAmount || '0'}</div>
                                </div>
                                <div class="order_content_set">
                                    <div class="order_content_title">付款狀態：</div>
                                    <div class="order_content_text">${orderView.isPaid ? '✅ 已付款' : '❌ 未付款'}</div>
                                </div>
                            </div>
                        </div>
                        <div class="order_region order_right">
                            <div class="order_status ${orderView.isPaid ? 'paid' : 'pending'}">${orderView.isPaid ? '已付款' : '未付款'}</div>
                            <div class="order_more"><a class="order_more_ -view" href="#">查看訂單詳情</a></div>
                            <div class="order_more"><a class="order_more_ -unview" href="#">收合訂單詳情</a></div>
                        </div>
                        <div class="inner_order_block">
                            <!-- 訂單詳細資訊 -->
                            <div class="order_detail_info">
                                <h5 style="margin-bottom: 15px; color: #333;">訂單詳細資訊</h5>
                                <div class="row" style="margin-bottom: 10px;">
                                    <div class="col-md-6">
                                        <strong>訂單編號：</strong> ${orderView.orderId || 'N/A'}
                                    </div>
                                    <div class="col-md-6">
                                        <strong>會員ID：</strong> ${orderView.memberId || 'N/A'}
                                    </div>
                                </div>
                                <div class="row" style="margin-bottom: 10px;">
                                    <div class="col-md-6">
                                        <strong>訂單成立：</strong> ${orderView.createTime || orderView.orderTime || 'N/A'}
                                    </div>
                                    <div class="col-md-6">
                                        <strong>訂單總額：</strong> NT$ ${orderView.totalAmount || '0'}
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6">
                                        <strong>活動ID：</strong> ${orderView.eventId || 'N/A'}
                                    </div>
                                    <div class="col-md-6">
                                        <strong>付款狀態：</strong> ${orderView.isPaid ? '✅ 已付款' : '❌ 未付款'}
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `);
            } else {
                console.log(`❌ 不顯示訂單: ${orderView.orderId}，不符合分類 ${category}`);
            }
        }
        
        console.log(`分類 ${category} 最終顯示了 ${count} 筆訂單`);
        order_isEmpty(count);
    })
    .catch(error => {
        console.error('載入訂單失敗:', error);
        order_isEmpty(0);
    });
}

// 生成票券明細行
function generateTicketRows(orderView) {
    // 如果有票券明細資料
    if (orderView.tickets && Array.isArray(orderView.tickets)) {
        return orderView.tickets.map(ticket => `
            <tr>
                <td>#${ticket.ticketNumber || 'N/A'}</td>
                <td>${ticket.categoryName || 'N/A'}</td>
                <td>${ticket.seatNumber || ticket.queueId || 'N/A'}</td>
                <td>${ticket.price || '0'}</td>
            </tr>
        `).join('');
    } else {
        // 沒有詳細票券資料時的預設顯示
        return `
            <tr>
                <td>#123456781</td>
                <td>一般區</td>
                <td>1</td>
                <td>1,100</td>
            </tr>
            <tr>
                <td>#123456782</td>
                <td>搖滾區</td>
                <td>2</td>
                <td>2,800</td>
            </tr>
        `;
    }
}

// ✅ 修正的判斷函數
function shouldShowOrder(orderView, category) {
    console.log('檢查訂單:', orderView, '分類:', category);
    
    switch(category) {
        case 1: // 全部訂單 - 顯示所有訂單
            return true;
        case 2: // 待付款 - 暫時不做，顯示空白
            return false;
        case 3: // 已付款 - 只顯示 is_paid = 1 的
            return orderView.isPaid === true || orderView.isPaid === 1;
        case 4: // 已取消 - 暫時不做，顯示空白
            return false;
        default:
            return false;
    }
}

// 取得狀態對應的 CSS 類別
function getStatusClass(status) {
    switch(status) {
        case 'PENDING':
        case 0:
            return 'pending';
        case 'PAID':
        case 1:
            return 'paid';
        case 'CANCELLED':
        case 2:
            return 'cancelled';
        default:
            return 'pending';
    }
}

// 取得狀態顯示文字
function getStatusText(status) {
    switch(status) {
        case 'PENDING':
        case 0:
            return '待付款';
        case 'PAID':
        case 1:
            return '已付款';
        case 'CANCELLED':
        case 2:
            return '已取消';
        default:
            return '待付款';
    }
}

// 判斷訂單列表是否為空
function order_isEmpty(count) {
    if (count == 0) {
        console.log("訂單列表為空");
        order_el.insertAdjacentHTML("beforeend", `
            <div class="order_empty">
                <div class="order_empty_text">
                    Oops~目前沒有訂單
                </div>
            </div>
        `);
    }
}

// 計算各分類的數量
function category_count() {
    const memberId = sessionStorage.getItem("memberId");
    
    if (!memberId) {
        console.error('找不到會員ID，請重新登入');
        order_nav_span_all_el.innerHTML = 0;
        order_nav_span_pending_el.innerHTML = 0;
        order_nav_span_paid_el.innerHTML = 0;
        order_nav_span_cancelled_el.innerHTML = 0;
        return;
    }
    
    console.log('準備查詢會員訂單統計，會員ID:', memberId);
    
    fetch('/maven-tickeasy-v1/order/order-list', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            memberId: parseInt(memberId)
        })
    })
    .then(resp => {
        console.log('統計API回應狀態:', resp.status);
        return resp.json();
    })
    .then(result => {
        console.log('統計API完整回應:', result);
        
        // ✅ 檢查 API 回應格式
        let orders = [];
        if (result.successful && Array.isArray(result.data)) {
            orders = result.data;
        } else if (Array.isArray(result)) {
            orders = result;
        }
        
        console.log('用於統計的訂單資料:', orders);
        
        let all_count = 0;
        let pending_count = 0;
        let paid_count = 0;
        let cancelled_count = 0;
        
        // ✅ 計算各狀態訂單數量
        for (let order of orders) {
            all_count++; // 全部訂單
            
            // 已付款：is_paid = 1 或 true
            if (order.isPaid === true || order.isPaid === 1) {
                paid_count++;
            } else {
                // 其他暫時不計算
                pending_count = 0;
                cancelled_count = 0;
            }
        }
        
        console.log('最終統計結果:', {
            total: all_count,
            pending: pending_count,
            paid: paid_count,
            cancelled: cancelled_count
        });
        
        order_nav_span_all_el.innerHTML = all_count;
        order_nav_span_pending_el.innerHTML = pending_count;
        order_nav_span_paid_el.innerHTML = paid_count;
        order_nav_span_cancelled_el.innerHTML = cancelled_count;
    })
    .catch(error => {
        console.error('載入訂單統計失敗:', error);
        order_nav_span_all_el.innerHTML = 0;
        order_nav_span_pending_el.innerHTML = 0;
        order_nav_span_paid_el.innerHTML = 0;
        order_nav_span_cancelled_el.innerHTML = 0;
    });
}

// 頁面載入完成後執行
document.addEventListener("DOMContentLoaded", function() {
    // ✅ 檢查是否有登入會員
    const memberId = sessionStorage.getItem("memberId");
    
    if (!memberId) {
        alert("請先登入");
        window.location.href = "/maven-tickeasy-v1/user/member/login.html";
        return;
    }
    
    console.log('當前登入會員ID:', memberId);
    
    // 清空訂單列表
    order_el.innerHTML = '';
    
    // 載入統計數據
    category_count();
    
    // 預設載入全部訂單
    order_loaded(1);
});

// 各頁籤切換
document.querySelectorAll(".order_tab").forEach(button => {
    button.addEventListener("click", () => {
        const tabId = button.getAttribute("data-tab");

        // 移除目前的 active 樣式
        document.querySelectorAll(".order_tab").forEach(btn => btn.closest("div.order_nav").classList.remove("-on"));

        // 加入新的 active 樣式
        button.closest("div.order_nav").classList.add("-on");
        let orderCategory = tabId.split("_")[1];

        if (orderCategory == 1) {
            order_el.innerHTML = '';
            order_loaded(1); // 全部訂單
        }
        if (orderCategory == 2) {
            order_el.innerHTML = '';
            order_loaded(2); // 待付款
        }
        if (orderCategory == 3) {
            order_el.innerHTML = '';
            order_loaded(3); // 已付款
        }
        if (orderCategory == 4) {
            order_el.innerHTML = '';
            order_loaded(4); // 已取消
        }
    });
});