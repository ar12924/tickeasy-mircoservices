// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動。

/**
 * 從後端 api 獲取特定 event 的活動資訊。
 * @param {number} eventId - 活動 id。
 * @returns {Promise<Object>} event 數據的物件。
 */
import { getContextPath } from "../../js/book-type.js";
export const fetchTicketEvent = async (eventId) => {
  const resp = await fetch(`${getContextPath()}/book-type/event/${eventId}`);
  return await resp.json();
};

// ==================== 2. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 header.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchHeaderTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-type/header.html`
  );
  return await resp.text();
};

/**
 * 根據提供的票種數據，動態生成並插入單個票種區塊的 HTML。
 * @param {Object} typeInfo - 單個票種的數據，包含 categoryName 和 price。
 * @param {string} templateHTML - HTML 模板。
 */
export const renderHeader = (templateHTML) => {
  const templateJQeury = $(templateHTML);

  $("header").append(templateJQeury); // 插入整塊元素到父容器
};

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

export const initHeaderJSEvents = () => {};
