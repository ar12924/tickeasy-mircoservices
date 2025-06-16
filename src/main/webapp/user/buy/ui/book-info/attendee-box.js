// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 attendee-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
import { getContextPath } from "../../js/book-info.js";
export const fetchAttendeeBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-info/attendee-box.html`
  );
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateHTML - HTML模板。
 */
export const renderAttendeeBox = (templateHTML) => {
  const templateJQeury = $(templateHTML);
  $(".attendee-container").append(templateJQeury);
};
