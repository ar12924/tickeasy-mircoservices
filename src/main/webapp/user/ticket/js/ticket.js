const notification_el = document.getElementById("ticket");
const ntf_link_el = document.getElementsByClassName("tk_link");
const ntf_el = document.getElementsByClassName("tk");
const ntf_regio_el = document.getElementsByClassName("tk_region");
const ntf_title_el = document.getElementsByClassName("tk_title");
const ntf_content_el = document.getElementsByClassName("tk_content");
const ntf_time_el = document.getElementsByClassName("tk_time");
const ntf_nav_span_all_el = document.querySelector(".all_tk");
const ntf_nav_span_coming_el = document.querySelector(".coming_tk");
const ntf_nav_span_history_el = document.querySelector(".history_tk");
const ntf_nav_span_allchange_el = document.querySelector(".allchange_tk");

const now = new Date();



/*document.addEventListener("DOMContentLoaded", function() {
	notification_el.innerHTML = '';
	notification_refresh(1);
	category_count();



})*/
function category_count() {
	fetch('/tickettest/tikcketList', {
		method: `POST`,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5,
			/*category: category*/

		})
	})
		.then(resp => resp.json())
		.then(notifications => {
			let eventMind_count = 0;
			let sold_count = 0;
			let swap_count = 0;
			let change_count = 0;
			let notificationCount = notifications.length;
			ntf_nav_span_all_el.innerHTML = notificationCount;
			/*for (let notification of notifications) {
				var count = 0;
				
				if (category == 1) {
					var notificationCount = notifications.length;
					ntf_nav_span_all_el.innerHTML = notificationCount;
				}
				if (category == 2) {
					if (notification.notificationId == 3 || notification.notificationId == 4 || notification.notificationId == 5) {
						count = count + 1;
					}
					var eventMind_count=count;
					ntf_nav_span_eventMind_el.innerHTML = count;
					
					
				}
				
				if (category == 3) {
					if (notification.notificationId == 2) {
						count = count + 1;
					}
					var sold_count=count;
					ntf_nav_span_sold_el.innerHTML = count;
				}
				if (category == 4) {
					if (notification.notificationId == 6) {
						count = count + 1;
					}
					var swap_count=count;
					ntf_nav_span_swap_el.innerHTML = count;
				}
				if (category == 5) {
					if (notification.notificationId == 7) {
						count = count + 1;
					}
					var change_count=count;
					ntf_nav_span_change_el.innerHTML = count;
				}
				
			}
			

			ntf_nav_span_eventMind_el.innerHTML = eventMind_count;
			ntf_nav_span_sold_el.innerHTML = sold_count;
			ntf_nav_span_swap_el.innerHTML = swap_count;
			ntf_nav_span_change_el.innerHTML = change_count;*/







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




function notification_loaded(category) {
	/*const category_type=category;*/
	notification_el.innerHTML = "";

	fetch('/tickettest/notificationList', {
		method: `POST`,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5,
			/*category: category*/

		})
	})
		.then(resp => resp.json())
		.then(notifications => {

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


				/*if(notification.isVisible==1){
					all_count=all_count+1;*/
				if (category == 1) {
					/*<span class="badge swap_ntf">1</span>*/
					/*const notificationCount=notifications.length;*/
					/*count = count + 1;*/
					/*ntf_nav_span_all_el.innerHTML=notificationCount;*/
					let displayTimeCount = time_count(notification.sendTime);
					/*const sendTime = new Date(notification.sendTime); 
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
								}*/

					/* ntf_title_el.innerHTML=body.title;
					 ntf_content_el.innerHTML=displayTime;
					 not_time=ntf_link_el.setAttribute("href",notification.linkURL);
					 ntf_time_el.innerHTML=body.time;*/


					/*var message=notification.message;
					var memberId =notification.memberId;
					var updateMessage=message.replace("${notification.memberId}",memberId);*/
					notification_el.insertAdjacentHTML("beforeend", `
						<a href="http://localhost:8080/tickettest${notification.linkURL}.html" class="tk_link" target="blank" >
						    <div class="tk -unread" data-tk-id="${notification.memberNotificationId}">
								<div class="tk_region tk_left">
						            <div class="tk_title">${notification.title}</div>
						                       
						             <div class="tk_content">${notification.message}</div>
						                       </div>
											  
						                       <div class="tk_region tk_right">
						                           <div class="tk_delete"><img src="./images/delete.png" alt="刪除" ></div>
						                           <div class="tk_time">${displayTimeCount}</div>
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
							fetch('/tickettest/notificationRead', {
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
						notification_el.insertAdjacentHTML("beforeend", `
												<a href="http://localhost:8080/tickettest${notification.linkURL}.html" class="ntf_link" target="blank" >
												    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
														<div class="ntf_region ntf_left">
												            <div class="ntf_title">${notification.title}</div>
												                       
												             <div class="ntf_content">${notification.message}</div>
												                       </div>
																	  
												                       <div class="ntf_region ntf_right">
												                           <div class="ntf_delete"><img src="./images/delete.png" alt="刪除" ></div>
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
								fetch('/tickettest/notificationRead', {
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
						notification_el.insertAdjacentHTML("beforeend", `
															<a href="http://localhost:8080/tickettest${notification.linkURL}.html" class="ntf_link" target="blank" >
															    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
																	<div class="ntf_region ntf_left">
															            <div class="ntf_title">${notification.title}</div>
															                       
															             <div class="ntf_content">${notification.message}</div>
															                       </div>
																				  
															                       <div class="ntf_region ntf_right">
															                           <div class="ntf_delete"><img src="./images/delete.png" alt="刪除" ></div>
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
								fetch('/tickettest/notificationRead', {
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
						notification_el.insertAdjacentHTML("beforeend", `
																			<a href="http://localhost:8080/tickettest${notification.linkURL}.html" class="ntf_link" target="blank" >
																			    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
																					<div class="ntf_region ntf_left">
																			            <div class="ntf_title">${notification.title}</div>
																			                       
																			             <div class="ntf_content">${notification.message}</div>
																			                       </div>
																								  
																			                       <div class="ntf_region ntf_right">
																			                           <div class="ntf_delete"><img src="./images/delete.png" alt="刪除" ></div>
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
								fetch('/tickettest/notificationRead', {
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
						notification_el.insertAdjacentHTML("beforeend", `
																							<a href="http://localhost:8080/tickettest${notification.linkURL}.html" class="ntf_link" target="blank" >
																							    <div class="ntf -unread" data-ntf-id="${notification.memberNotificationId}">
																									<div class="ntf_region ntf_left">
																							            <div class="ntf_title">${notification.title}</div>
																							                       
																							             <div class="ntf_content">${notification.message}</div>
																							                       </div>
																												  
																							                       <div class="ntf_region ntf_right">
																							                           <div class="ntf_delete"><img src="./images/delete.png" alt="刪除" ></div>
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
								fetch('/tickettest/notificationRead', {
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
										} else {
											alert("訊息閱讀更新錯誤");
										}
									})
							}
						})

					}
				}

			}
			/*ntf_isEmpty(count);*/
			/*if(count==0){
								console.log("123");
								notification_el.insertAdjacentHTML("beforeend",`<div class="ntf_empty">
																							<div class="ntf_empty_text">
																							Oops~目前沒有通知訊息
																						   </div>
																										   
																					  </div>
																					  
																			  
																				`)
							}*/
			/*	category_count().sold_count
				if(category_count().sold_count){ntf_isEmpty(category_count.sold_count);
				console.log("12312");
				}*/
		})



	/*	ntf_nav_span_el.innerHTML=all_count;*/



	/*ntf_nav_span_el.innerHTML=all_count;*/
}




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
document.addEventListener("DOMContentLoaded", function() {
	notification_el.innerHTML = '';
	category_count();
	notification_loaded(1);
})
/*function notification_category(category){
	a
}*/



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

		fetch('/tickettest/notificationUnvisible', {
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
		/*let ntf_category_el=e.target.*/
		category_count();

		/*div class="ntf_nav -on*/
		/*	document.querySelectorAll(".ntf_tab").forEach(button
			const tabId = button.getAttribute("data-tab");
			let notificationCategory = tabId.split("_")[1];*/

		/*	notification_loaded(notificationCategory_get);
			*/


	}

})








/*const tbody=document.querySelector('tbody');
fetch('manage', { method: 'POST' })
	.then(resp => resp.json())
	.then(members =>{
		for(let member of members){
			tbody.insertAdjacentHTML("beforeend",`
				<tr>
					<td>${member.id}</td>
					<td>${member.username}</td>
					<td>${member.password}</td>
					<td>${member.nickname}</td>
					<td>${member.pass}</td>
					<td>${member.roleId}</td>
					<td>${member.creator}</td>
					<td>${member.createdDate}</td>
					<td>${member.updater}</td>
					<td>${member.lastUpdatedDate}</td>
					<td><button class="delete">刪除</button></td>

				</tr>
				`)
			    
		}
	})*/








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