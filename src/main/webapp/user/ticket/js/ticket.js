/*const notification_el = document.getElementById("ticket");
const ntf_link_el = document.getElementsByClassName("tk_link");
const ntf_el = document.getElementsByClassName("tk");
const ntf_regio_el = document.getElementsByClassName("tk_region");
const ntf_title_el = document.getElementsByClassName("tk_title");
const ntf_content_el = document.getElementsByClassName("tk_content");
const ntf_time_el = document.getElementsByClassName("tk_time");
*/

const tk_nav_span_coming_el = document.querySelector(".coming_tk");
const tk_nav_span_history_el = document.querySelector(".history_tk");
const tk_nav_span_change_el = document.querySelector(".allchange_tk");

const now = new Date();

const tk_more_el=document.getElementsByClassName(".tk_more_");


const ticket_el = document.getElementById("ticket");
document.addEventListener("click",function(e){
	if(e.target.classList.contains("tk_more_")){
		e.preventDefault();
	const tk_more_target = e.target.closest(".tk");
	
	const tk_more_content= e.target.closest(".tk_region").nextElementSibling;
	console.log(tk_more_content);
	tk_more_target.classList.toggle("-on");
	tk_more_content.classList.toggle("-on");
	
	const allTkElements = document.querySelectorAll(".tk");
	   const isLastTk = tk_more_target === allTkElements[allTkElements.length - 1];

	   if (isLastTk && tk_more_target.classList.contains("-on")) {
		const scrollContainer = tk_more_target.closest('#ticket'); 

		smoothScrollTo(scrollContainer, scrollContainer.scrollHeight, 1000);
		
	   }
		
	}
})
/*點擊時的動畫效果修正 */
function smoothScrollTo(element, targetScrollTop, duration = 600) {
  const startScrollTop = element.scrollTop;
  const distance = targetScrollTop - startScrollTop;
  let startTime = null;

  function animation(currentTime) {
    if (!startTime) startTime = currentTime;
    const elapsed = currentTime - startTime;
    const progress = Math.min(elapsed / duration, 1);
    // 簡單 ease-in-out 函數
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


//所有票券的load

function ticket_loaded(category) {
	var count=0;
	
	fetch('/maven-tickeasy-v1/ticket/ticket-list', {
		method: `POST`,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5,
			

		})
	})
		.then(resp => resp.json())
		.then(ticketsView => {
			if(!Array.isArray(ticketsView)){
												ticketsView = [];
									}
			/*if(category==1){
				var category_count_get = document.querySelector("span.coming_tk").innerHTML;
				tk_isEmpty(category_count_get);
			}
			if(category==2){
				var category_count_get = document.querySelector("span.history_tk").innerHTML;
				console.log(category_count_get);
				tk_isEmpty(category_count_get);
			}
			if(category=3){
				var category_count_get = document.querySelector("span.allchange_tk").innerHTML;
				tk_isEmpty(category_count_get);
			}
*/
			for (let ticketView of ticketsView) {
			
			if(ticketView.viewCategoryType==category){
				count++;
				var qrData = "http://192.168.8.183:8080/maven-tickeasy-v1/ticket/use?Id=" + ticketView.ticketId + "&code=" + ticketView.qrCodeHashCode + "&now=" + sessionStorage.getItem("memberId");
				// 對整個 URL 編碼
				const encodedQrData = encodeURIComponent(qrData);
				// 產生 QR Code 圖片網址
				var qrImgUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + encodedQrData;
				if((category==1 || category==2 )&&(ticketView.memberId==ticketView.currentHolderMemberId)){
									ticket_el.insertAdjacentHTML("afterbegin", `
										<div class="tk">
															<div class="tk_region tk_left">
																<img src="../../common/images/activityPic.png" alt="ticket">
															</div>
															<div class="tk_region tk_center">
																<div class="tk_title">${ticketView.eventName}</div>
																<div class="tk_content">																	
																	<div class="tk_content_set">
																		<div class="tk_content_title ">活動時間</div>
																		<div class="tk_content_text tk_content_time">${ticketView.eventFromDate}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">活動地點</div>
																		<div class="tk_content_text tk_content_palce">${ticketView.place}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">訂單編號</div>
																		<div class="tk_content_text tk_content_tkNo">${ticketView.orderId}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">票種</div>
																		<div class="tk_content_text tk_content_tkType">${ticketView.categoryName}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">序號</div>
																		<div class="tk_content_text tk_content_No">${ticketView.queueId}</div>
																	</div>
																</div>
															</div>
															<div class="tk_region tk_right">
																<div class="tk_status">${ticketView.statusText}</div>
																<div class="tk_more"><a class="tk_more_ -view"href="#">查看票券詳情</a></div>
																<div class="tk_more"><a class="tk_more_ -unview" href="#">收合票券詳情</a></div>
															</div>
															<div class="inner_tk_block">
															<hr style="border: none; border-top: 1px solid #ccc; margin: 16px auto; width:80%;">
															<div class="tk_region_open tk_botton_left">
																<div class="tk_content">
																	<div class="tk_content_set">
																		<div class="tk_content_title ">價格</div>
																		<div class="tk_content_text tk_content_price">${ticketView.price}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">是否使用</div>
																		<div class="tk_content_text tk_content_used">${ticketView.isUsedText}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">姓名</div>
																		<div class="tk_content_text tk_content_name">${ticketView.participantName}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">手機號碼</div>
																		<div class="tk_content_text tk_content_phone">${ticketView.phone}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">電子郵件</div>
																		<div class="tk_content_text tk_content_email">${ticketView.email}</div>
																	</div>
																	<div class="tk_content_set">
																		<div class="tk_content_title">身份證字號</div>
																		<div class="tk_content_text tk_content_id">${ticketView.idCard}</div>
																	</div>
																</div>
															</div>
															<div class="tk_region_open tk_botton_right">
																<div class="tk_content">
																	<div class="tk_content_set">
																		<div class="tk_content_title">QR code</div>
																		<div class="tk_content_text tk_content_qrcode"><img src="${qrImgUrl}" data-url="${qrData}" alt="qrcode"></div>
																	</div>
																</div>
															</div>
															</div>
														</div>
										  
						                `)}
		if((category==1 || category==2 )&&(ticketView.memberId!=ticketView.currentHolderMemberId)){
											ticket_el.insertAdjacentHTML("afterbegin", `
												<div class="tk">
																	<div class="tk_region tk_left">
																		<img src="../../common/images/activityPic.png" alt="ticket">
																	</div>
																	<div class="tk_region tk_center">
																		<div class="tk_title">${ticketView.eventName}</div>
																		<div class="tk_content">																
																			<div class="tk_content_set">
																				<div class="tk_content_title ">活動時間</div>
																				<div class="tk_content_text tk_content_time">${ticketView.eventFromDate}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">活動地點</div>
																				<div class="tk_content_text tk_content_palce">${ticketView.place}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">訂單編號</div>
																				<div class="tk_content_text tk_content_tkNo">${ticketView.orderId}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">票種</div>
																				<div class="tk_content_text tk_content_tkType">${ticketView.categoryName}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">序號</div>
																				<div class="tk_content_text tk_content_No">${ticketView.queueId}</div>
																			</div>
																		</div>
																	</div>
																	<div class="tk_region tk_right">
																	<div class="tk_ticket_type">轉讓票</div>
																		<div class="tk_status">${ticketView.statusText}</div>
																		<div class="tk_more"><a class="tk_more_ -view"href="#">查看票券詳情</a></div>
																		<div class="tk_more"><a class="tk_more_ -unview" href="#">收合票券詳情</a></div>
																	</div>
																	<div class="inner_tk_block">
																	<hr style="border: none; border-top: 1px solid #ccc; margin: 16px auto; width:80%;">
																	<div class="tk_region_open tk_botton_left">
																		<div class="tk_content">
																			<div class="tk_content_set">
																				<div class="tk_content_title ">價格</div>
																				<div class="tk_content_text tk_content_price">${ticketView.price}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">是否使用</div>
																				<div class="tk_content_text tk_content_used">${ticketView.isUsedText}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">姓名</div>
																				<div class="tk_content_text tk_content_name">${ticketView.currentHolderChangeNickName}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">手機號碼</div>
																				<div class="tk_content_text tk_content_phone">${ticketView.currentHolderChangePhone}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">電子郵件</div>
																				<div class="tk_content_text tk_content_email">${ticketView.currentHolderChangeEmail}</div>
																			</div>
																			<div class="tk_content_set">
																				<div class="tk_content_title">身份證字號</div>
																				<div class="tk_content_text tk_content_id">${ticketView.currentHolderChangeIdCard}</div>
																			</div>
																		</div>
																	</div>
																	<div class="tk_region_open tk_botton_right">
																		<div class="tk_content">
																			<div class="tk_content_set">
																				<div class="tk_content_title">QR code</div>
																				<div class="tk_content_text tk_content_qrcode"><img src="${qrImgUrl}" data-url="${qrData}" alt="qrcode"></div>
																			</div>
																		</div>
																	</div>
																	</div>
																</div>
												  
								                `)}
	if(category==3){
										ticket_el.insertAdjacentHTML("afterbegin", `
											<div class="tk">
																<div class="tk_region tk_left">
																	<img src="../../common/images/activityPic.png" alt="ticket">
																</div>
																<div class="tk_region tk_center">
																	<div class="tk_title">${ticketView.eventName}</div>
																	<div class="tk_content">																	
																		<div class="tk_content_set">
																			<div class="tk_content_title ">活動時間</div>
																			<div class="tk_content_text tk_content_time">${ticketView.eventFromDate}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">活動地點</div>
																			<div class="tk_content_text tk_content_palce">${ticketView.place}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">訂單編號</div>
																			<div class="tk_content_text tk_content_tkNo">${ticketView.orderId}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">票種</div>
																			<div class="tk_content_text tk_content_tkType">${ticketView.categoryName}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">序號</div>
																			<div class="tk_content_text tk_content_No">${ticketView.queueId}</div>
																		</div>
																	</div>
																</div>
																<div class="tk_region tk_right">
																	<div class="tk_status">${ticketView.statusText}</div>
																	<div class="tk_more"><a class="tk_more_ -view"href="#">查看票券詳情</a></div>
																	<div class="tk_more"><a class="tk_more_ -unview" href="#">收合票券詳情</a></div>
																</div>
																<div class="inner_tk_block">
																<hr style="border: none; border-top: 1px solid #ccc; margin: 16px auto; width:80%;">
																<div class="tk_region_open tk_botton_left">
																	<div class="tk_content">
																		<div class="tk_content_set">
																			<div class="tk_content_title ">價格</div>
																			<div class="tk_content_text tk_content_price">${ticketView.price}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">是否使用</div>
																			<div class="tk_content_text tk_content_used">${ticketView.isUsedText}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">姓名</div>
																			<div class="tk_content_text tk_content_name">${ticketView.participantName}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">手機號碼</div>
																			<div class="tk_content_text tk_content_phone">${ticketView.phone}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">電子郵件</div>
																			<div class="tk_content_text tk_content_email">${ticketView.email}</div>
																		</div>
																		<div class="tk_content_set">
																			<div class="tk_content_title">身份證字號</div>
																			<div class="tk_content_text tk_content_id">${ticketView.idCard}</div>
																		</div>
																	</div>
																</div>
																<div class="tk_region_open tk_botton_right">
																	<div class="tk_content">
																		<div class="tk_content_set">
																			<div class="tk_content_title">QR code</div>
																			<div class="tk_content_text tk_content_qrcode"><img src="${qrImgUrl}" data-url="${qrData}" alt="qrcode"></div>
																		</div>
																	</div>
																</div>
																</div>
															</div>
											  
							                `)}											

}
}

console.log(count);
tk_isEmpty(count);
})

}



//判斷通知中心的分類是否為空需要顯示為空的畫面
function tk_isEmpty(count) {
	if (count == 0) {
		console.log("123");
		ticket_el.insertAdjacentHTML("beforeend", `<div class="tk_empty">
																				            <div class="tk_empty_text">
																							Oops~目前沒有票券
																				           </div>
																				                           
																				      </div>
																					  
																				  
																                `)
	}

}


//計算各分類的數量
function category_count() {
	fetch('/maven-tickeasy-v1/ticket/ticket-list', {
		method: `POST`,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5,


		})
	})
		.then(resp => resp.json())
		.then(tickets => {
			let coming_count = 0;
			let history_count = 0;
			let allchange_count = 0;
			
			
			//處理tikcets為空的狀況
			if(!Array.isArray(tickets)){
						notifications = [];}
			/*let ticketCount = tickets.length;
			tk_nav_span_all_el.innerHTML = ticketCount;*/


			//處理ticket各頁籤顯示數量
			for (let ticket of tickets) {
				/*let eventDate = new Date(ticket.eventFromDate);*/
				
				if (ticket.viewCategoryType==1) {
					coming_count++;
					
				}

				if (ticket.viewCategoryType==2) {
					history_count++;
					
				}

				if (ticket.viewCategoryType==3) {
					allchange_count++;
				
				}

				
			}
			tk_nav_span_coming_el.innerHTML = coming_count;
			tk_nav_span_history_el.innerHTML = history_count;
			tk_nav_span_change_el.innerHTML = allchange_count;
		})
}

document.addEventListener("DOMContentLoaded", function() {
	
	//先判斷有無登入
			fetch('/maven-tickeasy-v1/notify/check-login')
			    .then(response => response.json())
			    .then(isLoggedIn => {
			        if (!isLoggedIn) {
			            window.location.href = "/maven-tickeasy-v1/user/member/login.html";  // 如果未登入，跳轉到登入頁
						console.log("未登入");
			        } else {
	
	
	
	
	ticket_el.innerHTML = '';
	category_count();
	ticket_loaded(1);
	createWebSocket();
	/*testPush();*/
	}
						})
})

/*function testPush(){

fetch('/maven-tickeasy-v1/notify/test-push', {
  method: 'POST'
})
.then(resp => resp.text())
.then(msg => console.log("後端回應：", msg));
}*/
//各頁籤切換
document.querySelectorAll(".tk_tab").forEach(button => {
	button.addEventListener("click", () => {
		const tabId = button.getAttribute("data-tab");

		// 移除目前的 active 樣式
		document.querySelectorAll(".tk_tab").forEach(btn => btn.closest("div.tk_nav").classList.remove("-on"));
		document.querySelectorAll(".tab_content").forEach(tab => tab.closest("div.tk_nav").classList.remove("-on"));

		// 加入新的 active 樣式
		button.closest("div.tk_nav").classList.add("-on");
		let ticketCategory = tabId.split("_")[1];
		/*console.log(tabId);
		console.log(notificationCategory);*/

		/*document.getElementById(tabId).classList.add("-on");*/
		/*	document.querySelector(`[data-id="${tabId}"]`).classList.add("-on");*/
		if (ticketCategory == 1) {
			ticket_el.innerHTML = '';
			ticket_loaded(1);
		}
		if (ticketCategory == 2) {
			ticket_el.innerHTML = '';
			ticket_loaded(2);
		}
		if (ticketCategory == 3) {
			ticket_el.innerHTML = '';
			ticket_loaded(3);
		}

	})
})
/*
function createWebSocket() {
	notificationQueue = [];
	var memberId = sessionStorage.getItem("memberId");  // 使用者的 memberId 
        var socket = new WebSocket("ws://localhost:8080/maven-tickeasy-v1/notify/notification?memberId=" + memberId);

		// 監聽 WebSocket 連接成功事件
		socket.addEventListener('open', e => {
		    console.log("WebSocket 連接已建立！");  // 可以在控制台中打印這條信息確認連接成功
		    // 可以在這裡執行其他邏輯，比如發送消息到後端等
		});
		
        socket.addEventListener('message', e => {
            var message = e.data;
			console.log(message);
			// 如果隊列中沒有通知，直接顯示，否則將其加入隊列
			const notificationBox = document.getElementById('notification_box');
	        if (notificationBox && !document.getElementById('notification_box').classList.contains('show')) {
	            showNotification(message);
	        } else {
	            // 把新通知加入隊列
	            notificationQueue.push({ content: message });
	        };
        });
		// 當 WebSocket 連接錯誤時
		socket.addEventListener('error', function (e) {
		    console.error("WebSocket 發生錯誤:", e);
		});

		// 當 WebSocket 連接關閉時
		socket.addEventListener('close', function (e) {
		    console.log("WebSocket 連接已關閉");
		    // 這裡可以執行一些清理工作，或者根據需求重連
		    reconnectWebSocket();  // 重連邏輯
		});
		}
		// 重連 WebSocket 連接
function reconnectWebSocket() {
			var reconnectTimeout;
			// 如果已有重連操作，取消之前的重試
			   if (reconnectTimeout) {
			       clearTimeout(reconnectTimeout);
			   }

			   // 5秒後嘗試重新建立 WebSocket 連接
			   reconnectTimeout = setTimeout(function() {
			       console.log("正在重試 WebSocket 連接...");
			       createWebSocket();  // 重新創建 WebSocket 連接
			   }, 5000);  // 5秒後重試
		}*/
		
