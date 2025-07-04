const notification_el = document.getElementById("notification");
const ntf_link_el = document.getElementsByClassName("ntf_link");
const ntf_el = document.getElementsByClassName("ntf");
const ntf_regio_el = document.getElementsByClassName("ntf_region");
const ntf_title_el = document.getElementsByClassName("ntf_title");
const ntf_content_el = document.getElementsByClassName("ntf_content");
const ntf_time_el = document.getElementsByClassName("ntf_time");
const ntf_nav_span_all_el = document.querySelector(".all_ntf");
const ntf_nav_span_eventMind_el = document.querySelector(".eventMind_ntf");
const ntf_nav_span_sold_el = document.querySelector(".sold_ntf");
const ntf_nav_span_swap_el = document.querySelector(".swap_ntf");
const ntf_nav_span_change_el = document.querySelector(".change_ntf");
const now = new Date();


//計算通知中心各分類頁籤的通知數
function category_count() {
	fetch('/maven-tickeasy-v1/notify/notification-list', {
		method: `POST`,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5,


		})
	})
		.then(resp => resp.json())
		.then(notifications => {
			let eventMind_count = 0;
			let sold_count = 0;
			let swap_count = 0;
			let change_count = 0;
			
			if(!Array.isArray(notifications)){
						notifications = [];}
			let notificationCount = notifications.length;
			ntf_nav_span_all_el.innerHTML = notificationCount;



			for (let notification of notifications) {
				if ([3, 4, 5].includes(notification.notificationId)) {
					eventMind_count++;
				}

				if (notification.notificationId == 2) {
					sold_count++;
				}

				if (notification.notificationId == 6) {
					swap_count++;
				}

				if (notification.notificationId == 7) {
					change_count++;
				}
			}
			ntf_nav_span_eventMind_el.innerHTML = eventMind_count;
			ntf_nav_span_sold_el.innerHTML = sold_count;
			ntf_nav_span_swap_el.innerHTML = swap_count;
			ntf_nav_span_change_el.innerHTML = change_count;
		})
}


//通知中心各分類的load資料
function notification_loaded(category) {
	/*const category_type=category;*/
	notification_el.innerHTML = "";

	fetch('/maven-tickeasy-v1/notify/notification-list', {
		method: `POST`,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5
		})
	})
		.then(resp => resp.json())
		.then(notifications => {
			if(!Array.isArray(notifications)){
									notifications = [];
						}


			if (category == 1) {
				/*var category_count_get = document.querySelector("span.all_ntf").innerHTML;

				console.log(category_count_get+"test");*/

				ntf_isEmpty(notifications.length);

			}
			if (category == 2) {
				var category_count_get = document.querySelector("span.eventMind_ntf").innerHTML;

				console.log(category_count_get);

				ntf_isEmpty(category_count_get);
			}

			if (category == 3) {
				var category_count_get = document.querySelector("span.sold_ntf").innerHTML;

				console.log(category_count_get);

				ntf_isEmpty(category_count_get);
			}

			if (category == 4) {
				var category_count_get = document.querySelector("span.swap_ntf").innerHTML;

				console.log(category_count_get);

				ntf_isEmpty(category_count_get);
			}

			if (category == 5) {
				var category_count_get = document.querySelector("span.change_ntf").innerHTML;

				console.log(category_count_get);

				ntf_isEmpty(category_count_get);
			}


			for (let notification of notifications) {



				if (category == 1) {

					let displayTimeCount = time_count(notification.sendTime);

					notification_el.insertAdjacentHTML("afterbegin", `
						<a href="http://localhost:8080/maven-tickeasy-v1${notification.linkURL}" class="ntf_link" target="blank" >
						    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
								<div class="ntf_region ntf_left">
						            <div class="ntf_title">${notification.title}</div>
						                       
						             <div class="ntf_content">${notification.message}</div>
						                       </div>
											  
						                       <div class="ntf_region ntf_right">
						                           <div class="ntf_delete"><img src="../../common/images/delete.png" alt="刪除" ></div>
						                           <div class="ntf_time">${displayTimeCount}</div>
						           </div>
						                           
						      </div>
							  </a>
						  
		                `)
					const ntf_el = document.querySelector('[data-ntf-id="' + notification.memberNotificationId + '"]');

					if (notification.isRead) {
						ntf_el.classList.remove("-unread");
					}



					ntf_el.addEventListener("click", function() {
						if (notification.isRead != 1) {
							fetch('/maven-tickeasy-v1/notify/notification-read', {
								method: `POST`,
								headers: { 'Content-Type': 'application/json' },
								body: JSON.stringify({
									memberId: 5,
									memberNotificationId: notification.memberNotificationId


								})
							})
								.then(resp => resp.json())
								.then(notificationRead => {
									if (notificationRead.success) {
										/*let ntf_id_el=document.querySelector(`[data-ntf-id="${notification.memberNotificationId}"]`);*/
										// location.href='edit.html';
										alert("訊息已閱讀");
										ntf_el.classList.remove("-unread");

									} else {
										alert("訊息閱讀更新錯誤");
									}
								})
						}
					})



					/*}*/
				}
				if (category == 2) {
					/*var category_count_get = document.querySelector("span.eventMind_ntf").innerHTML;

					console.log(category_count_get);

					ntf_isEmpty(category_count_get);*/
					/*							console.log("13");*/
					if (notification.notificationId == 3 || notification.notificationId == 4 || notification.notificationId == 5) {




						let displayTimeCount = time_count(notification.sendTime);
						notification_el.insertAdjacentHTML("afterbegin", `
												<a href="http://localhost:8080/maven-tickeasy-v1${notification.linkURL}" class="ntf_link" target="blank" >
												    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
														<div class="ntf_region ntf_left">
												            <div class="ntf_title">${notification.title}</div>
												                       
												             <div class="ntf_content">${notification.message}</div>
												                       </div>
																	  
												                       <div class="ntf_region ntf_right">
												                           <div class="ntf_delete"><img src="../../common/images/delete.png" alt="刪除" ></div>
												                           <div class="ntf_time">${displayTimeCount}</div>
												           </div>
												                           
												      </div>
													  </a>
												  
								                `)
						const ntf_el = document.querySelector('[data-ntf-id="' + notification.memberNotificationId + '"]');

						if (notification.isRead) {
							ntf_el.classList.remove("-unread");
						}



						ntf_el.addEventListener("click", function() {
							if (notification.isRead != 1) {
								fetch('/maven-tickeasy-v1/notify/notification-read', {
									method: `POST`,
									headers: { 'Content-Type': 'application/json' },
									body: JSON.stringify({
										memberId: 5,
										memberNotificationId: notification.memberNotificationId


									})
								})
									.then(resp => resp.json())
									.then(notificationRead => {
										if (notificationRead.success) {
											// location.href='edit.html';
											alert("訊息已閱讀");
											ntf_el.classList.remove("-unread");
										} else {
											alert("訊息閱讀更新錯誤");
										}
									})
							}
						})

					}

					/*ntf_nav_span_eventMind_el.innerHTML = count;*/

				}
				if (category == 3) {
					/*	var category_count_get = document.querySelector("span.sold_ntf").innerHTML;
	
						console.log(category_count_get);
	
						ntf_isEmpty(category_count_get);*/
					/*							console.log("13");*/
					if (notification.notificationId == 2) {
						let displayTimeCount = time_count(notification.sendTime);
						notification_el.insertAdjacentHTML("afterbegin", `
															<a href="http://localhost:8080/maven-tickeasy-v1${notification.linkURL}" class="ntf_link" target="blank" >
															    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
																	<div class="ntf_region ntf_left">
															            <div class="ntf_title">${notification.title}</div>
															                       
															             <div class="ntf_content">${notification.message}</div>
															                       </div>
																				  
															                       <div class="ntf_region ntf_right">
															                           <div class="ntf_delete"><img src="../../common/images/delete.png" alt="刪除" ></div>
															                           <div class="ntf_time">${displayTimeCount}</div>
															           </div>
															                           
															      </div>
																  </a>
															  
											                `)
						const ntf_el = document.querySelector('[data-ntf-id="' + notification.memberNotificationId + '"]');

						if (notification.isRead) {
							ntf_el.classList.remove("-unread");
						}



						ntf_el.addEventListener("click", function(e) {
							if (e.target.closest(".ntf_delete")) return;
							if (notification.isRead != 1) {
								fetch('/maven-tickeasy-v1/notify/notification-read', {
									method: `POST`,
									headers: { 'Content-Type': 'application/json' },
									body: JSON.stringify({
										memberId: 5,
										memberNotificationId: notification.memberNotificationId


									})
								})
									.then(resp => resp.json())
									.then(notificationRead => {
										if (notificationRead.success) {
											// location.href='edit.html';
											alert("訊息已閱讀");
											ntf_el.classList.remove("-unread");
										} else {
											alert("訊息閱讀更新錯誤");
										}
									})
							}
						})

					}
				}

				if (category == 4) {
					/*var category_count_get = document.querySelector("span.swap_ntf").innerHTML;

					console.log(category_count_get);

					ntf_isEmpty(category_count_get);
												consle.log("13");*/
					if (notification.notificationId == 6) {



						let displayTimeCount = time_count(notification.sendTime);
						notification_el.insertAdjacentHTML("afterbegin", `
																			<a href="http://localhost:8080/maven-tickeasy-v1${notification.linkURL}" class="ntf_link" target="blank" >
																			    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
																					<div class="ntf_region ntf_left">
																			            <div class="ntf_title">${notification.title}</div>
																			                       
																			             <div class="ntf_content">${notification.message}</div>
																			                       </div>
																								  
																			                       <div class="ntf_region ntf_right">
																			                           <div class="ntf_delete"><img src="../../common/images/delete.png" alt="刪除" ></div>
																			                           <div class="ntf_time">${displayTimeCount}</div>
																			           </div>
																			                           
																			      </div>
																				  </a>
																			  
															                `)
						const ntf_el = document.querySelector('[data-ntf-id="' + notification.memberNotificationId + '"]');

						if (notification.isRead) {
							ntf_el.classList.remove("-unread");
						}



						ntf_el.addEventListener("click", function() {
							if (notification.isRead != 1) {
								fetch('/maven-tickeasy-v1/notify/notification-read', {
									method: `POST`,
									headers: { 'Content-Type': 'application/json' },
									body: JSON.stringify({
										memberId: 5,
										memberNotificationId: notification.memberNotificationId


									})
								})
									.then(resp => resp.json())
									.then(notificationRead => {
										if (notificationRead.success) {
											// location.href='edit.html';
											alert("訊息已閱讀");
											ntf_el.classList.remove("-unread");
										} else {
											alert("訊息閱讀更新錯誤");
										}
									})
							}
						})

					}
				}
				if (category == 5) {
					/*var category_count_get = document.querySelector("span.change_ntf").innerHTML;

					console.log(category_count_get);

					ntf_isEmpty(category_count_get);*/








					/*							console.log("13");*/
					if (notification.notificationId == 7) {




						let displayTimeCount = time_count(notification.sendTime);
						notification_el.insertAdjacentHTML("afterbegin", `
																							<a href="http://localhost:8080/maven-tickeasy-v1${notification.linkURL}" class="ntf_link" target="blank" >
																							    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
																									<div class="ntf_region ntf_left">
																							            <div class="ntf_title">${notification.title}</div>
																							                       
																							             <div class="ntf_content">${notification.message}</div>
																							                       </div>
																												  
																							                       <div class="ntf_region ntf_right">
																							                           <div class="ntf_delete"><img src="../../common/images/delete.png" alt="刪除" ></div>
																							                           <div class="ntf_time">${displayTimeCount}</div>
																							           </div>
																							                           
																							      </div>
																								  </a>
																							  
																			                `)
						const ntf_el = document.querySelector('[data-ntf-id="' + notification.memberNotificationId + '"]');

						if (notification.isRead) {
							ntf_el.classList.remove("-unread");
						}



						ntf_el.addEventListener("click", function(e) {
							/*if (e.target.closest(".ntf_delete")) return;*/
							if (notification.isRead != 1) {
								fetch('/maven-tickeasy-v1/notify/notification-read', {
									method: `POST`,
									headers: { 'Content-Type': 'application/json' },
									body: JSON.stringify({
										memberId: 5,
										memberNotificationId: notification.memberNotificationId


									})
								})
									.then(resp => resp.json())
									.then(notificationRead => {
										if (notificationRead.success) {
											// location.href='edit.html';
											alert("訊息已閱讀");
											ntf_el.classList.remove("-unread");
										} else {
											alert("訊息閱讀更新錯誤");
										}
									})
							}
						})

					}
				}

			}

		})

}


//判斷通知中心的分類是否為空需要顯示為空的畫面
function ntf_isEmpty(count) {
	if (count == 0) {
		console.log("123");
		notification_el.insertAdjacentHTML("beforeend", `<div class="ntf_empty">
																				            <div class="ntf_empty_text">
																							Oops~目前沒有通知訊息
																				           </div>
																				                           
																				      </div>
																					  
																				  
																                `)
	}

}

// div.ntf.-unread
//計算通知中心的各訊息的顯示時間
function time_count(sendtime) {
	const sendTime = new Date(sendtime);
	let diffMs = now - sendTime; // 毫秒差
	let diffSec = Math.floor(diffMs / 1000);
	let diffMin = Math.floor(diffSec / 60);
	let diffHr = Math.floor(diffMin / 60);
	let diffDay = Math.floor(diffHr / 24);


	let displayTime;
	if (diffSec < 60) {
		displayTime = '剛剛';
	} else if (diffMin < 60) {
		displayTime = `${diffMin} 分鐘前`;
	} else if (diffHr < 24) {
		displayTime = `${diffHr} 小時前`;
	} else {
		displayTime = `${diffDay} 天前`;
	}
	return displayTime
}



//分析資料庫的部份

//           `member_notification_id` INT NOT NULL AUTO_INCREMENT COMMENT '用戶通知ID',
//暫不理  `notification_id` INT NOT NULL COMMENT '通知ID',
//暫不接  `member_id` INT NOT NULL COMMENT '用戶ID',
//JS `is_read` TINYINT(1) NOT NULL COMMENT '是否已讀，0:未讀，1:已讀',
//-SQL    `is_visible` TINYINT(1) NOT NULL COMMENT '是否可見，0:不可見，1:可見',
//-SQL  `notification_status` TINYINT(1) NOT NULL COMMENT '會員通知狀態，0:未發送，1:已發送',
//JS   `title` VARCHAR(255) NOT NULL COMMENT '通知標題',
//JS   `message` TEXT NOT NULL COMMENT '通知內容',
//JS  `link_url` VARCHAR(255) NULL COMMENT '相關連結',
//暫不理  `read_time` DATETIME NULL COMMENT '閱讀時間',
//JS `send_time` DATETIME NULL COMMENT '發送時間',

//頁面加載的初始值
document.addEventListener("DOMContentLoaded", function() {
	//先判斷有無登入
	fetch('/maven-tickeasy-v1/notify/check-login')
	    .then(response => response.json())
	    .then(isLoggedIn => {
	        if (!isLoggedIn) {
	            window.location.href = "/maven-tickeasy-v1/user/member/login.html";  // 如果未登入，跳轉到登入頁
				console.log("未登入");
	        } else {
				notification_el.innerHTML = '';
				category_count();
				notification_loaded(1);
	        }
	    });
	
})


//處理點擊各通知訊息的delete鈕
document.addEventListener("click", function(e) {

	const deleteTarget = e.target.closest(".ntf_delete");
	//console.log( e.target.closest("li").previousElementSibling );
	if (deleteTarget) {
		e.preventDefault();
		e.stopPropagation();
		let ntf_delete_el = deleteTarget.closest("a.ntf_link");
		let ntf_el = deleteTarget.closest(".ntf");
		/*console.log(ntf_el);*/
		let ntf_unvisible_el = ntf_el.dataset.ntfId;
		/*console.log(ntf_unvisible_el);*/
		ntf_delete_el.remove();

		fetch('/maven-tickeasy-v1/notify/notification-unvisible', {
			method: `POST`,
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({
				memberNotificationId: ntf_unvisible_el

			})
		})
			.then(resp => resp.json())
			.then(body => {
				if (body.success) {
					// location.href='edit.html';
					alert("訊息已改成隱藏");
					category_count();
					let ntf_nav_get = document.querySelector(".ntf_nav.-on");
					/*console.log(ntf_nav_get);*/
					let ntf_tab_dataId_get = ntf_nav_get.querySelector(".ntf_tab").getAttribute("data-tab");
					console.log(ntf_tab_dataId_get);
					let notificationCategory_get = ntf_tab_dataId_get.split("_")[1];

					notification_loaded(notificationCategory_get);

				} else {
					alert("訊息隱藏更新錯誤");
				}
			})

		category_count();
	}

})


//各頁籤切換的頁面顯示
document.querySelectorAll(".ntf_tab").forEach(button => {
	button.addEventListener("click", () => {
		const tabId = button.getAttribute("data-tab");

		// 移除目前的 active 樣式
		document.querySelectorAll(".ntf_tab").forEach(btn => btn.closest("div.ntf_nav").classList.remove("-on"));
		document.querySelectorAll(".tab_content").forEach(tab => tab.closest("div.ntf_nav").classList.remove("-on"));

		// 加入新的 active 樣式
		button.closest("div.ntf_nav").classList.add("-on");
		let notificationCategory = tabId.split("_")[1];
		/*console.log(tabId);
		console.log(notificationCategory);*/

		/*document.getElementById(tabId).classList.add("-on");*/
		/*	document.querySelector(`[data-id="${tabId}"]`).classList.add("-on");*/
		if (notificationCategory == 1) {
			notification_el.innerHTML = '';
			notification_loaded(1);
		}
		if (notificationCategory == 2) {
			notification_el.innerHTML = '';
			notification_loaded(2);
		}
		if (notificationCategory == 3) {
			notification_el.innerHTML = '';
			notification_loaded(3);
		}
		if (notificationCategory == 4) {
			notification_el.innerHTML = '';
			notification_loaded(4);
		}
		if (notificationCategory == 5) {
			notification_el.innerHTML = '';
			notification_loaded(5);
		}

	})
})