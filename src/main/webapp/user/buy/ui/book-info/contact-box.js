// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 contact-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
import { getContextPath } from "../../js/book-info.js";
export const fetchContactBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-info/contact-box.html`
  );
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateHTML - HTML模板。
 */
export const renderContactBox = (templateHTML) => {
  const templateJQeury = $(templateHTML);
  $(".contact-container").append(templateJQeury);
};
