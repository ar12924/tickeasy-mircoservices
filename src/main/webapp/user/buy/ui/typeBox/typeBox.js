// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動。

/**
 * 從後端 API 獲取特定事件的票種數據。
 * @param {number} eventId - 事件 ID。
 * @returns {Promise<Array<Object>>} "票種" + "活動資訊" 數據的數組。
 */
export const fetchTypeAndEvents = async (eventId) => {
  const resp = await fetch(
    `http://localhost:8080/maven-tickeasy-v1/user/buy/book-tickets?eventId=${eventId}`
  ); // api 取得 "票種表" 資料
  return await resp.json();
};

// ==================== 2. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 typeBox.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchTypeBoxTemplate = async () => {
  const resp = await fetch("./ui/typeBox/typeBox.html");
  return await resp.text();
};

/**
 * 根據提供的票種數據，動態生成並插入單個票種區塊的 HTML。
 * @param {Object} ticketType - 單個票種的數據，包含 categoryName 和 price。
 * @param {string} templateHTML - HTML 模板。
 */
export const renderTypeBox = async (ticketType, templateHTML) => {
  const templateJQeury = $(templateHTML);

  templateJQeury.find(".type-name").text(ticketType.categoryName); // 顯示票種
  templateJQeury.find(".type-price").text(`NT$ ${ticketType.price}`); // 顯示價格

  $(".type-container").append(templateJQeury); // 插入整塊元素到父容器
};

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

export const initTypeBoxJSEvents = () => {
  // "-" 按鈕點擊事件
  $(document).on("click", ".substract", (e) => {
    const control = $(e.target).parent();
    const input = control.next().find("input");
    let count = parseInt(input.val() || "0");
    if (count > 0) {
      count--;
      input.val(count);
    }
  });
  // "+" 按鈕點擊事件
  $(document).on("click", ".add", (e) => {
    const control = $(e.target).parent();
    const input = control.prev().find("input");
    let count = parseInt(input.val() || "0");
    count++;
    input.val(count);
  });
};
