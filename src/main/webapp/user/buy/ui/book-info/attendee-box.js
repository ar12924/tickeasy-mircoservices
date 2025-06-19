import { getContextPath } from "../../../common/utils.js";
// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 attendee-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchAttendeeBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-info/attendee-box.html`
  );
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateHTML - HTML模板。
 * @param {Array<Object>} selected - 票種選擇結果(typeId, categoryName, quantity)。
 */
export const renderAttendeeBox = (templateHTML, selected) => {
  $(".attendee-container").empty(); // 清空子元素
  let num = 0;
  selected.forEach((ticketType) => {
    for (let i = 0; i < ticketType.quantity; i++) {
      const templateJQeury = $(templateHTML);
      console.log(`i: ${i}`);
      console.log(`ticket-type: ${ticketType}`);
      num++;
      templateJQeury
        .find(".info-title")
        .text(`${ticketType.categoryName}(${num})`);
      $(".attendee-container").append(templateJQeury);
    }
  });
};
