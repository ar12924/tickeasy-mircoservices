// ==================== 載入模組 (All Imports At Top) ====================
import { getContextPath } from "../../../common/utils.js";
import { fetchMember } from "../../js/book-info.js";

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
  selected.forEach(({ quantity, categoryName }) => {
    for (let i = 0; i < quantity; i++) {
      const templateJQuery = $(templateHTML);
      num++; // 票券標題附加編號(以區別不同張)
      templateJQuery.find(".info-title").text(`${categoryName}(${num})`);
      $(".attendee-container").append(templateJQuery);
    }
  }); // 直接解構 selected 中的物件
};

// ==================== 2. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

export const initAttendeeBoxJSEvents = async (book) => {
  // 取得購票人資料
  const buyer = await fetchMember(book.userName);

  // ”同購票人資料“ 按鈕
  $(".same-buyer-checkbox").on("click", (e) => {
    const parentElement = $(e.target).closest(".custom-box");
    const isChecked = e.target.checked;

    // 欄位對應關係
    const fieldMap = [
      { selector: ".account", buyerField: "userName" },
      { selector: ".email", buyerField: "email" },
      { selector: ".nick-name", buyerField: "nickName" },
      { selector: ".phone", buyerField: "phone" },
      { selector: ".id-card", buyerField: "idCard" },
    ];

    // 根據 isChecked ? 決定是否放入值
    fieldMap.forEach(({ selector, buyerField }) => {
      const value = isChecked ? buyer[buyerField] : "";
      parentElement.find(selector).val(value);
    });
  });
};
