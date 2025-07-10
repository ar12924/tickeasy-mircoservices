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
	
	fetch('/maven-tickeasy-v1/order/order-list', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5
		})
	})
	.then(resp => resp.json())
	.then(ordersView => {
		if(!Array.isArray(ordersView)) {
			ordersView = [];
		}
		
		for (let orderView of ordersView) {
			// 根據分類篩選訂單
			if(shouldShowOrder(orderView, category)) {
				count++;
				order_el.insertAdjacentHTML("afterbegin", `
					<div class="order_item">
						<div class="order_region order_left">
							<img src="../../common/images/activityPic.png" alt="order">
						</div>
						<div class="order_region order_center">
							<div class="order_title">${orderView.eventName || '活動名稱'}</div>
							<div class="order_content">
								<div class="order_content_set">
									<div class="order_content_title">訂單編號</div>
									<div class="order_content_text order_content_id">${orderView.orderId || 'N/A'}</div>
								</div>
								<div class="order_content_set">
									<div class="order_content_title">活動時間</div>
									<div class="order_content_text order_content_time">${orderView.eventFromDate || 'N/A'}</div>
								</div>
								<div class="order_content_set">
									<div class="order_content_title">活動地點</div>
									<div class="order_content_text order_content_place">${orderView.place || 'N/A'}</div>
								</div>
								<div class="order_content_set">
									<div class="order_content_title">訂單金額</div>
									<div class="order_content_text order_content_amount">NT$ ${orderView.totalAmount || '0'}</div>
								</div>
								<div class="order_content_set">
									<div class="order_content_title">票券數量</div>
									<div class="order_content_text order_content_quantity">${orderView.ticketQuantity || '0'} 張</div>
								</div>
							</div>
						</div>
						<div class="order_region order_right">
							<div class="order_status ${getStatusClass(orderView.orderStatus)}">${getStatusText(orderView.orderStatus)}</div>
							<div class="order_more"><a class="order_more_ -view" href="#">查看訂單詳情</a></div>
							<div class="order_more"><a class="order_more_ -unview" href="#">收合訂單詳情</a></div>
						</div>
						<div class="inner_order_block">
							<hr style="border: none; border-top: 1px solid #ccc; margin: 16px auto; width:80%;">
							
							<!-- 訂單基本資訊 -->
							<div class="order_detail_info">
								<div class="order_detail_row">
									<div class="order_detail_item">
										<span class="order_detail_label">訂單編號</span>
										<span class="order_detail_value">${orderView.orderId || 'N/A'}</span>
									</div>
									<div class="order_detail_item order_status_container">
										<span class="order_status_badge ${getStatusClass(orderView.orderStatus)}">${getStatusText(orderView.orderStatus)}</span>
									</div>
								</div>
								<div class="order_detail_row">
									<div class="order_detail_item">
										<span class="order_detail_label">訂單成立</span>
										<span class="order_detail_value">${orderView.createTime || 'N/A'}</span>
									</div>
									<div class="order_detail_item">
										<span class="order_detail_label countdown_label">剩餘付款時間</span>
										<span class="order_detail_value countdown_value">12:15</span>
									</div>
								</div>
								<div class="order_detail_row">
									<div class="order_detail_item">
										<span class="order_detail_label">訂單總額</span>
										<span class="order_detail_value">${orderView.totalAmount || '0'}</span>
									</div>
								</div>
							</div>
							
							<!-- 票券明細表格 -->
							<div class="ticket_detail_table">
								<table>
									<thead>
										<tr>
											<th>票號</th>
											<th>票種</th>
											<th>序號</th>
											<th>金額</th>
										</tr>
									</thead>
									<tbody>
										${generateTicketRows(orderView)}
									</tbody>
									<tfoot>
										<tr class="total_row">
											<td colspan="3" class="total_label">合計</td>
											<td class="total_amount">${orderView.totalAmount || '0'}</td>
										</tr>
									</tfoot>
								</table>
							</div>
						</div>
					</div>
				`);
			}
		}
		
		console.log(count);
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
function shouldShowOrder(orderView, category) {
	switch(category) {
		case 1: // 全部訂單
			return true;
		case 2: // 待付款
			return orderView.orderStatus === 'PENDING' || orderView.orderStatus === 0;
		case 3: // 已付款
			return orderView.orderStatus === 'PAID' || orderView.orderStatus === 1;
		case 4: // 已取消
			return orderView.orderStatus === 'CANCELLED' || orderView.orderStatus === 2;
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
	fetch('/maven-tickeasy-v1/order/order-list', {
		method: 'POST',
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5
		})
	})
	.then(resp => resp.json())
	.then(orders => {
		let all_count = 0;
		let pending_count = 0;
		let paid_count = 0;
		let cancelled_count = 0;
		
		// 處理 orders 為空的狀況
		if(!Array.isArray(orders)) {
			orders = [];
		}
		
		// 處理各頁籤顯示數量
		for (let order of orders) {
			all_count++;
			
			if (order.orderStatus === 'PENDING' || order.orderStatus === 0) {
				pending_count++;
			}
			
			if (order.orderStatus === 'PAID' || order.orderStatus === 1) {
				paid_count++;
			}
			
			if (order.orderStatus === 'CANCELLED' || order.orderStatus === 2) {
				cancelled_count++;
			}
		}
		
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
	// 先判斷有無登入
	// fetch('/maven-tickeasy-v1/notify/check-login')
	// .then(response => response.json())
	// .then(isLoggedIn => {
		// if (!isLoggedIn) {
		// 	window.location.href = "/maven-tickeasy-v1/user/member/login.html";
		// 	console.log("未登入");
		// } else {
			order_el.innerHTML = '';
			category_count();
			order_loaded(1); // 預設載入全部訂單
		// }
	// })
	// .catch(error => {
	// 	console.error('檢查登入狀態失敗:', error);
	// });
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