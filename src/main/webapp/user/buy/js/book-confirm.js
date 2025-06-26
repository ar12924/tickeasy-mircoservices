// ==================== 載入模組 (All Imports At Top) ====================
import { getUrlParam, getContextPath } from "../../common/utils.js";
import { BOOKING_PROGRESS, ERROR_MESSAGES } from "../../common/constant.js";
import {
  fetchNavTemplate,
  renderNav,
  initNavJSEvents,
} from "../../layout/nav/nav.js";
import {
  fetchHeaderTemplate,
  fetchTicketEvent,
  renderHeader,
} from "../ui/header.js";
import {
  fetchFooterTemplate,
  renderFooter,
} from "../../layout/footer/footer.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

/**
 * 從 Redis 中，取得購票頁資訊。
 *
 * @return {Object} book 購票頁資訊。
 */
const fetchBook = async () => {
  const resp = await fetch(`${getContextPath()}/book-info`);
  const core = await resp.json();
  // 要求使用者，請先登入
  if (!core.successful) {
    alert(core.message);
    sessionStorage.setItem("core-message", core.message);
    location.href = `${getContextPath()}/user/member/login.html`;
  }
  // 已經登入
  const eventId = getUrlParam("eventId");
  const {
    message,
    successful,
    data: [book],
  } = core;
  if (!eventId || eventId != book.eventId) {
    alert("活動選擇錯誤");
    // 跳回首頁...
    return;
  }
  sessionStorage.setItem("core-message", message);
  return book;
};

/**
 * 查詢購票人資訊。
 *
 * @param {string} userName - 購票人使用者名稱。
 * @return {Object} member 購票人資訊。
 */
export const fetchMember = async (userName) => {
  const resp = await fetch(`${getContextPath()}/book-info/member/${userName}`);
  return await resp.json();
};

// ==================== 2. 數據處理層 (Data Processing) ====================
// 這些函數負責從 DOM 中提取數據，並對數據進行格式化或轉換。

/**
 * 從頁面中抓取所有票種輸入框的數值和相關信息。
 * @returns {Array<Object>} 包含 quantity, categoryName, price 的數組。
 */
// const getTicketInputsValues = () => {
//   const inputsValues = $(".type-quantity")
//     .map((i, input) => {
//       const parentNode = $(input).closest(".level");
//       const categoryName = parentNode.find(".type-name").text();
//       const price = parentNode
//         .find(".type-price")
//         .text()
//         .replace(/[^0-9.]/g, ""); // 過濾非數字符號
//       return {
//         quantity: $(input).val(),
//         categoryName,
//         price,
//       };
//     })
//     .get(); // .get() 將 jQuery 物件轉換為原生 JavaScript 數組
//   return inputsValues;
// };

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

const initBookConfirmJSEvents = () => {
  // 共同變數，url 後方的活動 id
  const eventId = getUrlParam("eventId");

  // ====== "上一步" 按鈕點擊事件 ======
  $(".back").on("click", () => {
    location.href = `${getContextPath()}/user/buy/book-info.html?eventId=${eventId}`;
  });
  // ====== "下一步" 按鈕點擊事件 ======
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", () => {
    location.href = "#";
  });
};

// ==================== 4. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

(async () => {
  // ====== 資料儲存變數區 ======
  // const book = await fetchBook();
  const book = {
    eventId: "1",
    eventName: "2025 春季搖滾音樂節",
    userName: "buyer2",
    progress: BOOKING_PROGRESS.ORDER_CONFIRM,
    selected: [
      {
        typeId: 1,
        categoryName: "VIP區",
        quantity: 0,
      },
      {
        typeId: 2,
        categoryName: "搖滾區",
        quantity: 0,
      },
      {
        typeId: 3,
        categoryName: "一般區",
        quantity: 1,
      },
    ],
    contact: {
      userName: "buyer2",
      email: "buyer2@example.com",
      nickName: "Music Fan",
      phone: "0967890123",
    },
    attendee: [
      { userName: "buyer2", idCard: "F123456789" },
      { userName: "buyer2", idCard: "F123456789" },
    ],
  };

  // ====== nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  renderNav(navTemplate);
  initNavJSEvents();

  // ====== header 部分 ======
  const eventId = getUrlParam("eventId");
  const headerTemplate = await fetchHeaderTemplate();
  const eventInfo = await fetchTicketEvent(eventId);
  // 存入 book 變數中，儲存 eventName
  book.eventName = eventInfo.eventName;
  // 輸出 header.html 模板(顯示對應進度條、活動名稱)
  renderHeader(eventInfo, book, headerTemplate);

  // ====== book-confirm 部分 ======
  initBookConfirmJSEvents();

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
