import { getContextPath } from "../../../common/utils.js";
// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 contact-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchContactBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-info/contact-box.html`
  );
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateHTML - HTML模板。
 * @param {string} userName - 購票人 userName。
 */
export const renderContactBox = (
  templateHTML,
  { userName, nickName, email, phone }
) => {
  const templateJQeury = $(templateHTML);

  templateJQeury.find(".account").val(userName);
  templateJQeury.find(".email").val(email);
  templateJQeury.find(".nick-name").val(nickName);
  templateJQeury.find(".phone").val(phone);
  $(".contact-container").html(templateJQeury);
};
