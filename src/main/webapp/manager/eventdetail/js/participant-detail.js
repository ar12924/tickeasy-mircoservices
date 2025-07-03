document.addEventListener("DOMContentLoaded", function () {
  const urlParams = new URLSearchParams(window.location.search);
  const ticketId = urlParams.get("ticketId");

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
    .then((result) => {
      // result 是完整的 JSON 物件, e.g., {successful: true, data: {...}}
      if (!result.successful) {
        throw new Error(result.message || "無法獲取詳細資料");
      }

      const data = result.data; // 從 result 中提取出真正的資料物件

      if (!data || !data.ticket) {
        throw new Error("回傳的資料格式不正確或缺少票券資訊");
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
        // 從 data.order 中獲取 orderTime
        document.querySelector("#o-time").value = new Date(
          data.order.orderTime
        ).toLocaleString();
        document.querySelector("#o-quantity").value = data.ticketQuantity;
      }
    })
    .catch((error) => {
      console.error("獲取詳細資料時出錯:", error);
      detailContentEl.innerHTML = `<div class="alert alert-danger">無法載入資料: ${error.message}</div>`;
    });
});
