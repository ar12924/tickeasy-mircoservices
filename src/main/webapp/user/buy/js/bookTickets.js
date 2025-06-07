// ==================== 1. 工具函數 (Utilities) ====================
// 這些函數負責處理一些通用的、無關特定業務邏輯的任務

/**
 * 從 URL 查詢參數中獲取指定參數的值。
 * @param {string} paramName - 要獲取的參數名稱。
 * @returns {string|null} 參數的值，如果不存在則為 null。
 */
const getUrlParam = (paramName) => {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  return urlParams.get(paramName);
};

// ==================== 2. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

/**
 * 將選定的票種和數量數據 POST 到後端 Redis 進行保存。
 * @param {Array<Object>} selectionList - 包含票種選擇信息的數組。
 * @param {number} eventId - 事件 ID。
 */
const saveBookTicketSelections = async (selectionLst, eventId) => {
  const resp = await fetch(
    `http://localhost:8080/maven-tickeasy-v1/user/buy/book-tickets/save?eventId=${eventId}`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(selectionLst),
    }
  );
  const respBody = await resp.json();
  console.log("Redis Save Response: ", respBody);
};

// ==================== 3. 數據處理層 (Data Processing) ====================
// 這些函數負責從 DOM 中提取數據，並對數據進行格式化或轉換。

/**
 * 從頁面中抓取所有票種輸入框的數值和相關信息。
 * @returns {Array<Object>} 包含 quantity, categoryName, price 的數組。
 */
const getTicketInputsValues = () => {
  const inputsValues = $(".type-quantity")
    .map((i, input) => {
      const parentNode = $(el).closest(".level");
      const categoryName = parentNode.find(".type-name").text();
      const price = parentNode
        .find(".type-price")
        .text()
        .replace(/[^0-9.]/g, ""); // 過濾非數字符號
      return {
        quantity: $(el).val(),
        categoryName,
        price,
      };
    })
    .get(); // .get() 將 jQuery 物件轉換為原生 JavaScript 數組
  return inputsValues;
};

// ==================== 4. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

const initBookTicketsJSEvents = () => {
  // ====== "更新票券" 按鈕點擊事件 ======
  $(".update").on("mouseenter mouseleave", (e) => {
    $(e.target).closest(".update").toggleClass("is-focused");
  });
  // ====== "上一步" 按鈕點擊事件 ======
  $(".back").on("click", () => {
    location.href = "https://www.google.com";
  });
  // ====== "下一步" 按鈕點擊事件 ======
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", () => {
    const eventId = getUrlParam("eventId");
    if (!eventId) {
      alert("缺少活動id，無法繼續!!");
      return;
    }
    const selectedBookTickets = getTicketInputsValues();
    if (selectedBookTickets.length <= 0) {
      alert("請至少選擇1種票券!!");
      return;
    }
    saveBookTicketSelections(selectionLst, eventId); // post 選擇到的票種至 Redis
    location.href = `bookDetails.html?eventId=${eventId}`;
  });
};

// ==================== 5. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

$(() => {
  initBookTicketsJSEvents(); // 載入 JS 事件監聽
});
