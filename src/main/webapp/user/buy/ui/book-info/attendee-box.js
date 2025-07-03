// ==================== 載入模組 (All Imports At Top) ====================
import { getContextPath, validateIdCard } from "../../../common/utils.js";
import { fetchMember, verifyMemberIdCard } from "../../js/book-info.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。
// none...

// ==================== 2. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 attendee-box.html 模板。
 *
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
 *
 * @param {string} templateHTML - HTML模板。
 * @param {Array<Object>} selected - 票種選擇結果(typeId, categoryName, quantity)。
 */
export const renderAttendeeBox = (templateHTML, selected) => {
  $(".attendee-container").empty(); // 清空子元素

  let num = 0;
  let totalNum = 0;

  // 合計選擇多少張
  selected.forEach(({ quantity }) => {
    totalNum += quantity;
  });

  // 渲染每個票種名(含帳號及身分證輸入框)
  selected.forEach(({ quantity, categoryName }) => {
    for (let i = 0; i < quantity; i++) {
      const templateJQuery = $(templateHTML);
      num++; // 票券標題附加編號(以區別不同張)
      templateJQuery
        .find(".info-title")
        .text(`${categoryName}(${num}/${totalNum})`);
      $(".attendee-container").append(templateJQuery);
    }
  });
};

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

export const initAttendeeBoxJSEvents = async (book) => {
  // "同購票人資料" 按鈕點擊
  $(".same-buyer-checkbox").on("click", async (e) => {
    const buyer = await fetchMember(book.userName);
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

    // 移除輸入框下方訊息
    $(".verify-account")
      .text("")
      .removeClass("has-text-success has-text-danger");
    $(".verify-id-card")
      .text("")
      .removeClass("has-text-success has-text-danger");
  });

  // "帳號" 輸入框驗證
  $(".account").on("blur", async (e) => {
    const parentElement = $(e.target).closest(".custom-box");
    const accountValue = $(e.target).val().trim();
    const accountValidateMessage = parentElement.find(".verify-account");

    // 去空格後覆蓋元素
    $(e.target).val(accountValue);

    // 輸入框為空的情形
    if (!accountValue) {
      accountValidateMessage.text("此欄位必填").addClass("has-text-danger");
      return;
    }

    // 最後移除訊息
    accountValidateMessage.text("").removeClass("has-text-danger");
  });

  // "身分證字號" 輸入框驗證
  $(".id-card").on("blur", async (e) => {
    const parentElement = $(e.target).closest(".custom-box");
    const userNameValue = parentElement.find(".account").val().trim();
    const idCardValue = $(e.target).val().trim();
    const idCardValidateMessage = parentElement.find(".verify-id-card");

    // 去空格後覆蓋元素
    $(e.target).val(idCardValue);

    // 輸入框為空的情形
    if (!idCardValue) {
      idCardValidateMessage.text("此欄位必填").addClass("has-text-danger");
      return;
    }

    // 判斷身分證基本格式
    const isIdCardValid = validateIdCard(idCardValue);
    if (!isIdCardValid) {
      idCardValidateMessage
        .text("身分證格式錯誤")
        .removeClass("has-text-success")
        .addClass("has-text-danger");
    } else {
      idCardValidateMessage
        .text("身分證格式正確")
        .removeClass("has-text-danger")
        .addClass("has-text-success");
    }
  });
};
