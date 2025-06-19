// ==================== 1. 載入模組 (All Imports At Top) ====================
import { getUrlParam } from "../../common/utils.js";
import { getContextPath } from "../../common/utils.js";
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
  fetchContactBoxTemplate,
  renderContactBox,
} from "../ui/book-info/contact-box.js";
import {
  fetchAttendeeBoxTemplate,
  renderAttendeeBox,
} from "../ui/book-info/attendee-box.js";
import {
  fetchFooterTemplate,
  renderFooter,
} from "../../layout/footer/footer.js";

// ==================== 2. API 服務層 (API Service Layer) ====================
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
 * @param {string} userName - 購票人使用者名稱。
 * @return {Object} member 購票人資訊。
 */
const fetchMember = async (userName) => {
  const resp = await fetch(`${getContextPath()}/book-info/member/${userName}`);
  return await resp.json();
};

// ==================== 3. 數據處理層 (Data Processing) ====================
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

// ==================== 4. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

const initBookInfoJSEvents = () => {
  // 共同變數，url 後方的活動 id
  const eventId = getUrlParam("eventId");
  if (!eventId) {
    alert("缺少活動id，無法繼續!!");
    return;
  }

  // ====== "上一步" 按鈕點擊事件 ======
  $(".back").on("click", () => {
    const url = `${getContextPath()}/user/buy/book-type.html?eventId=${eventId}`;
    console.log(url);
    location.href = url;
  });
  // ====== "下一步" 按鈕點擊事件 ======
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", () => {
    location.href = "#";
  });
};

// ==================== 5. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

(async () => {
  // ====== 資料儲存變數區 ======
  // const book = await fetchBook();
  const book = {
    eventId: "1",
    eventName: "2025 春季搖滾音樂節",
    userName: "buyer1",
    progress: 2,
    selected: [
      {
        typeId: 1,
        categoryName: "VIP區",
        quantity: 1,
      },
      {
        typeId: 2,
        categoryName: "搖滾區",
        quantity: 0,
      },
      {
        typeId: 3,
        categoryName: "一般區",
        quantity: 2,
      },
    ],
    contact: null,
    attendee: null,
  };

  // ====== nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  renderNav(navTemplate);
  initNavJSEvents();

  // ====== header 部分 ======
  const eventId = getUrlParam("eventId");
  const headerTemplate = await fetchHeaderTemplate();
  const eventInfo = await fetchTicketEvent(eventId);
  if (!eventInfo) {
    alert("載入活動名稱失敗!!");
  } else {
    // 存入 book 變數中，儲存 eventName
    book.eventName = eventInfo.eventName;
    // 存取 progress 以顯示對應進度條
    // 存取 eventName 以顯示當前活動名稱
    // 輸出 header.html 模板
    renderHeader(eventInfo, book, headerTemplate);
  }

  // ====== book-info 部分 ======
  initBookInfoJSEvents(); // 載入 JS 事件監聽

  // ====== contact-box 部分 ======
  const buyer = await fetchMember(book.userName); // 查操作人自己
  const contactBoxTemplate = await fetchContactBoxTemplate();
  renderContactBox(contactBoxTemplate, buyer);

  // ====== attendee-box 部分 ======
  const attendeeBoxTemplate = await fetchAttendeeBoxTemplate();
  const { selected } = book;
  renderAttendeeBox(attendeeBoxTemplate, selected);

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
