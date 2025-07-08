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
import {
  fetchEventBoxTemplate,
  fetchEventInfo,
  renderEventBox,
} from "../ui/book-finished/event-box.js";
import {
  fetchDetailBoxTemplate,
  fetchTicketDetailBoxTemplate,
  initDetailBoxJSEvents,
  renderDetailBox,
} from "../ui/book-finished/details-box.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

/**
 * 從 Redis 取得訂單選擇結果資料。 (票種 + 個人資料)
 *
 * @return {Object} book 訂單填寫資訊。
 */
const findBook = async () => {
  // 從 Redis 抓資料
  const resp = await fetch(`${getContextPath()}/book-confirm`);
  const { authStatus, dataStatus, message, successful, data } =
    await resp.json();

  // 要求使用者，請先登入
  if (authStatus === "NOT_LOGGED_IN") {
    alert(message);
    sessionStorage.setItem("core-message", message);
    sessionStorage.setItem("core-sucessful", successful);
    location.href = `${getContextPath()}/user/member/login.html`;
    return;
  }

  // 判斷 Redis 資料的活動 id 與當前活動 id 不同時...
  const eventId = getUrlParam("eventId");
  const dataEventId = data?.eventId;
  if (eventId != dataEventId) {
    alert(ERROR_MESSAGES.EVENT_ID_INCONSISTENT);
    // 回到票種選擇頁...
    location.href = `${getContextPath()}/user/buy/book-type.html?eventId=${eventId}`;
    return;
  }

  // 通過所有檢查回傳資料
  sessionStorage.setItem("core-message", message);
  sessionStorage.setItem("core-sucessful", successful);
  return data;
};

/**
 * 將訂單資料(票種 + 個人資料)儲存到Redis。
 *
 * @param {Object} book - 包含票種選擇訊息的物件。
 */
const saveBook = async (book) => {
  const eventId = getUrlParam("eventId");

  // 如果 eventId 缺少
  if (book.eventId <= 0) {
    $(".book-info-message").text(ERROR_MESSAGES.MISSING_EVENT_ID);
    $(".book-info-message").closest("#error-message").removeClass("is-hidden");
    return;
  }

  // 將 book 儲存至 Redis
  const resp = await fetch(`${getContextPath()}/book-confirm`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(book),
  });
  const { authStatus, dataStatus, message, successful } = await resp.json();

  // 要求使用者，請先登入
  if (authStatus === "NOT_LOGGED_IN") {
    alert(message);
    sessionStorage.setItem("core-message", message);
    sessionStorage.setItem("core-successful", successful);
    location.href = `${getContextPath()}/user/member/login.html`;
    return;
  }

  // 通過
  sessionStorage.setItem("core-message", message);
  sessionStorage.setItem("core-successful", successful);
  return;
};

/**
 * 將訂單資料(票種 + 個人資料)儲存到資料庫(永久)。
 *
 * @param {Object} book - 包含票種選擇訊息的物件。
 */
const saveOrderAndTicket = async (book) => {
  const eventId = getUrlParam("eventId");

  // 如果 eventId 缺少
  if (book.eventId <= 0) {
    $(".book-info-message").text(ERROR_MESSAGES.MISSING_EVENT_ID);
    $(".book-info-message").closest("#error-message").removeClass("is-hidden");
    return;
  }

  // 將 book 儲存至資料庫
  const resp = await fetch(`${getContextPath()}/book-confirm/save`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(book),
  });
  const { authStatus, dataStatus, message, successful } = await resp.json();

  // 要求使用者，請先登入
  if (authStatus === "NOT_LOGGED_IN") {
    alert(message);
    sessionStorage.setItem("core-message", message);
    sessionStorage.setItem("core-successful", successful);
    location.href = `${getContextPath()}/user/member/login.html`;
    return;
  }

  // 通過
  sessionStorage.setItem("core-message", message);
  sessionStorage.setItem("core-successful", successful);
  return;
};

// ==================== 2. 數據處理層 (Data Processing) ====================
// 這些函數負責從 DOM 中提取數據，並對數據進行格式化或轉換。

/**
 * 從頁面中抓取所有單價，並合計。
 * @return {number} 合計價格。
 */
const getBookTotalPrice = () => {
  let total = 0;

  // 尋找所有金額的文字
  $(".total-price").each((i, item) => {
    const priceText = $(item).text();
    // 移除 "NT$ " 和逗號，轉換為數字
    const price = parseInt(priceText.replace(/NT\$\s|,/g, "") || 0);
    total += price;
  });
  return total;
};

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

const initBookConfirmJSEvents = async (book) => {
  // 共同變數，url 後方的活動 id
  const eventId = getUrlParam("eventId");

  // ====== "上一步" 按鈕點擊事件 ======
  $(".back").on("click", () => {
    const result = confirm("您將回到首頁");
    if (result) {
      location.href = `${getContextPath()}/user/buy/index.html`;
    }
  });
  // ====== "下一步" 按鈕點擊事件 ======
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", () => {
    location.href = "#";
  });
};

// ==================== 4. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 顯示金額合計值。
 * @param {number} totalPrice - 合計金額數字。
 */
const showBookTotalPrice = (totalPrice) => {
  $(".book-total-price").empty();
  $(".book-total-price").text(`NT$ ${totalPrice.toLocaleString("en-US")}`);
};

// ==================== 5. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

(async () => {
  // ====== 資料儲存變數區 ======
  const book = await findBook();

  // ====== nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  await renderNav(navTemplate);
  initNavJSEvents();

  // ====== header 部分 ======
  const eventId = getUrlParam("eventId");
  const headerTemplate = await fetchHeaderTemplate();
  const eventInfo = await fetchTicketEvent(eventId);
  // 存入 book 變數中，儲存 eventName
  book.eventName = eventInfo.eventName;
  // 輸出 header.html 模板(顯示對應進度條、活動名稱)
  renderHeader(eventInfo, book, headerTemplate);

  // ====== event-box 部分 ======
  const eventBoxTemplate = await fetchEventBoxTemplate();
  const eventInfoAgain = await fetchEventInfo(eventId);
  renderEventBox(eventInfoAgain, eventBoxTemplate);

  // ====== details-box 部分 ======
  const detailTemplate = await fetchDetailBoxTemplate();
  const ticketDetailTemplate = await fetchTicketDetailBoxTemplate();
  await renderDetailBox(detailTemplate, ticketDetailTemplate, book);
  initDetailBoxJSEvents();

  // ====== book-finished 部分 ======
  const totalPrice = getBookTotalPrice();
  showBookTotalPrice(totalPrice);
  initBookConfirmJSEvents(book);

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
