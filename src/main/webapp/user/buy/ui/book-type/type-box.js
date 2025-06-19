// ==================== 載入模組 (All Imports At Top) ====================
import { getContextPath } from "../../../common/utils.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動。

/**
 * 從後端 api 獲取特定 event 的 type 數據。
 * @param {number} eventId - 活動 id。
 * @returns {Promise<Array<Object>>} type + event 數據的數組。
 */
export const fetchTicketType = async (eventId) => {
  const resp = await fetch(
    `${getContextPath()}/book-type/event/${eventId}/event-ticket-type`
  );
  return await resp.json();
};

// ==================== 2. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 typeBox.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchTypeBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-type/type-box.html`
  );
  return await resp.text();
};

/**
 * 根據提供的票種數據，動態生成並插入單個票種區塊的 HTML。
 * @param {Object} typeInfo - 單個票種的數據，包含 categoryName 和 price。
 * @param {string} templateHTML - HTML 模板。
 */
export const renderTypeBox = ({ categoryName, price }, templateHTML) => {
  const templateJQeury = $(templateHTML);

  templateJQeury.find(".type-name").text(categoryName); // 顯示票種
  templateJQeury.find(".type-price").text(`NT$ ${price}`); // 顯示價格

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
