/*const notification_el = document.getElementById("ticket");
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

const tk_more_el=document.getElementsByClassName(".tk_more_");

*/
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
/*待確認 */
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



function ticket_loaded() {
	
	

	fetch('/maven-tickeasy-v1/ticket-list', {
		method: `POST`,
		headers: { 'Content-Type': 'application/json' },
		body: JSON.stringify({
			memberId: 5,
			

		})
	})
		.then(resp => resp.json())
		.then(ticketsView => {
			for (let ticketView of ticketsView) {


		
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
																		<div class="tk_content_title">票號</div>
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
																		<div class="tk_content_text tk_content_qrcode"><img src="../../common/images/qrcode.png" alt="qrcode"></div>
																	</div>
																</div>
															</div>
															</div>
														</div>
										  
						                `)
}
})
}
ticket_loaded();