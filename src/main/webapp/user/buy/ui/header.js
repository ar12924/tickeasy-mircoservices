import { getContextPath } from "../../common/utils.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動。

/**
 * 從後端 api 獲取特定 event 的活動資訊。
 * @param {number} eventId - 活動 id。
 * @returns {Promise<Object>} event 數據的物件。
 */
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
  const resp = await fetch(`${getContextPath()}/user/buy/ui/header.html`);
  return await resp.text();
};

/**
 * 根據提供的活動資訊，動態生成 header。
 * @param {string} eventName - 活動名稱。
 * @param {number} progress - 當前購票進度。
 * @param {string} templateHTML - HTML 模板。
 */
export const renderHeader = ({ eventName }, { progress }, templateHTML) => {
  const templateJQeury = $(templateHTML);

  // 當前步驟，加入 class = "passing-*"
  templateJQeury
    .find(".progress-number" + progress.toString())
    .addClass("passing-circle");
  templateJQeury
    .find(".progress-label" + progress.toString())
    .addClass("passing-step");
  // 已過步驟，加入 class = "passed-*"
  if (progress > 1) {
    for (let i = 1; i < progress; i++) {
      templateJQeury
        .find(".progress-number" + i.toString())
        .addClass("passed-circle")
        .find("span")
        .html(`<i class="fa-solid fa-check"></i>`);
      templateJQeury
        .find(".progress-label" + i.toString())
        .addClass("passed-step");
    }
  }

  // 插入整塊元素到父容器
  templateJQeury.find(".event-name").text(eventName);
  $("header").html(templateJQeury);
};

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

export const initHeaderJSEvents = () => {};
