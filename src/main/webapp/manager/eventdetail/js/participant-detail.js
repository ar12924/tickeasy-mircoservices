document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const ticketId = urlParams.get("ticketId");

  // 如果沒有 ticketId 參數，自動重定向到帶有 ticketId=1 的 URL
  if (!ticketId) {
    const currentUrl = window.location.href.split("?")[0];
    window.location.href = `${currentUrl}?ticketId=1`;
    return;
  }

  const detailContentEl = document.querySelector("#detail-content");

  const API_URL = `/maven-tickeasy-v1/manager/eventdetail/participants/detail?ticketId=${ticketId}`;

  fetch(API_URL, {
    credentials: "include", // 發送 cookie 以進行 session 驗證
  })
    .then((response) => {
      if (!response.ok) {
        throw new Error(`HTTP 錯誤! 狀態: ${response.status}`);
      }
      return response.json();
    })
    .then((data) => {
      // 後端直接返回數據對象，不再有 successful 和 data 封裝層
      // if (!result.successful) {
      //   alert(result.message || "無法獲取詳細資料");
      //   return;
      // }
      // const data = result.data;

      if (data.error) {
        alert(data.error);
        return;
      }

      const ticket = data.ticket;

      // 填充資料
      document.querySelector("#ticket-id").textContent = ticket.ticketId;

      // 個人資料
      document.querySelector("#p-name").value = ticket.participantName;
      document.querySelector("#p-email").value = ticket.email;
      document.querySelector("#p-phone").value = ticket.phone;
      document.querySelector("#p-id-card").value = ticket.idCard;

      // 票券資訊
      document.querySelector("#t-category").value =
        ticket.eventTicketType.categoryName;
      document.querySelector(
        "#t-price"
      ).value = `$ ${ticket.eventTicketType.price.toLocaleString()}`;
      document.querySelector("#t-status").value =
        ticket.status === 1 ? "有效" : "已取消";
      document.querySelector("#t-is-used").value =
        ticket.isUsed === 1 ? "是" : "否";

      // 訂單資訊
      if (data.order) {
        document.querySelector("#o-id").value = data.order.orderId;
        document.querySelector("#o-time").value = new Date(
          data.orderTime
        ).toLocaleString();
        document.querySelector("#o-quantity").value = data.ticketQuantity;
      }
    })
    .catch((error) => {
      console.error("獲取詳細資料時出錯:", error);
      detailContentEl.innerHTML = `<div class="alert alert-danger">無法載入資料: ${error.message}</div>`;
    });
});
