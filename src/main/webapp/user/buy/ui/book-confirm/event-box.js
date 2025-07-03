// ==================== 載入模組 (All Imports At Top) ====================
import { getContextPath } from "../../../common/utils.js";

// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 event-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchEventBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-confirm/event-box.html`
  );
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateHTML - HTML模板。
 * @param {string} eventInfoAgain - 解構出 { eventName, eventFromDate, eventHost, place }。
 */
export const renderEventBox = (
  { eventName, eventFromDate, eventHost, place },
  templateHTML
) => {
  const $template = $(templateHTML);

  $template.find(".event-title").text(eventName);
  $template.find(".event-time").text(eventFromDate);
  $template.find(".event-place").text(place);
  $template.find(".event-host").text(eventHost);
  $(".event-container").html($template);
};

// ==================== 2. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動。

/**
 * 從後端 api 獲取特定 event 的活動資訊。
 * @param {number} eventId - 活動 id。
 * @returns {Promise<Object>} event 數據的物件。
 */
export const fetchEventInfo = async (eventId) => {
  const resp = await fetch(`${getContextPath()}/book-confirm/event/${eventId}`);
  return await resp.json();
};
