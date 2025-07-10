// ==================== 載入模組 (All Imports At Top) ====================
import { getUrlParam, getContextPath, formatTime } from "../../common/utils.js";
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
  fetchContactBoxTemplate,
  renderContactBox,
} from "../ui/book-info/contact-box.js";
import {
  fetchAttendeeBoxTemplate,
  renderAttendeeBox,
  initAttendeeBoxJSEvents,
} from "../ui/book-info/attendee-box.js";
import {
  fetchFooterTemplate,
  renderFooter,
} from "../../layout/footer/footer.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

/**
 * 從 Redis 取得訂單資料(票種 + 個人資料)的 TTL。
 *
 * @return {Object} book 訂單填寫資訊。
 */
const findBookTTL = async () => {
  // 從 Redis 抓資料
  const resp = await fetch(`${getContextPath()}/book-info`);
  const { message, successful, remainingTime } = await resp.json();

  // 回傳 TTL 資料
  sessionStorage.setItem("core-message", message);
  sessionStorage.setItem("core-sucessful", successful);
  return remainingTime;
};

/**
 * 從 Redis 取得訂單選擇結果資料。 (票種 + 個人資料)
 * (包含驗證 session、活動 id 等)
 *
 * @return {Object} book 訂單填寫資訊。
 */
const findBook = async () => {
  // 從 Redis 抓資料
  const resp = await fetch(`${getContextPath()}/book-info`);
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
 * 查詢購票人資訊。
 *
 * @param {string} userName - 購票人使用者名稱。
 * @return {Object} member 購票人資訊。
 */
export const fetchMember = async (userName) => {
  const resp = await fetch(`${getContextPath()}/book-info/member/${userName}`);
  return await resp.json();
};

/**
 * 將一筆入場者資料(userName, idCard)送至後端，比對會員身分證資料正確性
 *
 * @param {Array<Object>} attendeeOne - 一位入場者資料。
 * 格式:
 * {
 *   userName: "buyer2",
 *   idCard: "F123456789",
 * }
 * @return {Object} 身分證驗證結果訊息。
 */
export const verifyMemberIdCard = async (attendeeOne) => {
  const resp = await fetch(`${getContextPath()}/book-info/member/verify`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(attendeeOne),
  });
  return await resp.json();
};

/**
 * 將聯絡人和入場者資訊儲存到後端 Redis，並跳轉至下一頁(book-confirm.html)
 * (不設定 TTL(分鐘))
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

  // 檢查每個 attendee 的身分證是否存在?
  for (const attendeeOne of book.attendee) {
    console.log(attendeeOne);
    const verifiedObject = await verifyMemberIdCard(attendeeOne);
    if (!verifiedObject.successful) {
      alert(verifiedObject.message);
      return;
    }
  }

  // 將 book 儲存至 Redis
  const resp = await fetch(`${getContextPath()}/book-info`, {
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

  // 並跳轉到下一頁(book-confirm.html)
  sessionStorage.setItem("core-message", message);
  sessionStorage.setItem("core-successful", successful);
  location.href = `${getContextPath()}/user/buy/book-confirm.html?eventId=${eventId}`;
};

// ==================== 2. 數據處理層 (Data Processing) ====================
// 這些函數負責從 DOM 中提取數據，並對數據進行格式化或轉換。

/**
 * 抓取所有個人資料輸入框的數值，並放入 book 物件當中。
 * @param {Object} book - book 物件，包含儲存聯絡人資訊的 contact 及儲存入場者資訊的 attendee 陣列。
 */
const addTicketInfoToContactAndAttendee = (book) => {
  // 聯絡人、入場者區塊父元素抓取
  const contactParentElement = $(".contact-container");
  const attendeeParentElement = $(".attendee-container");

  // 聯絡人輸入框抓取並儲存
  const $contactBox = contactParentElement.find(".box");
  book.contact = {
    userName: $contactBox.find(".account").val().trim(),
    email: $contactBox.find(".email").val().trim(),
    nickName: $contactBox.find(".nick-name").val().trim(),
    phone: $contactBox.find(".phone").val().trim(),
  };

  // 抓取 typeId 形成一個順序陣列
  const typeIdArray = book.selected.flatMap((selectOne) =>
    Array(selectOne.quantity).fill(selectOne.typeId)
  );

  // 入場者輸入框抓取並儲存
  attendeeParentElement.find(".box").each((i, boxElement) => {
    const $attendeeBox = $(boxElement);
    book.attendee[i] = {
      userName: $attendeeBox.find(".account").val().trim(),
      idCard: $attendeeBox.find(".id-card").val().trim(),
      typeId: typeIdArray[i],
    };
  });
};

/**
 * 傳入 TTL 定時倒數計時，每秒更新畫面的數字文字。
 * @param {number} TTL - 訂單的 TTL。
 * @param {function} renderTimer - 計時器渲染函數。
 */
const countDown = async (TTL, renderTimer) => {
  const eventId = getUrlParam("eventId");
  let remainingTime = TTL;

  // 立即執行第1次
  renderTimer(remainingTime);

  // 定期重複執行(1000 ms)
  const intervalId = setInterval(() => {
    remainingTime--;
    renderTimer(remainingTime); // 重新渲染

    // 時間到停止
    if (remainingTime <= 0) {
      clearInterval(intervalId);
      console.log("倒數結束!!");
      alert("請重新選擇票種!!");
      location.href = `${getContextPath()}/user/buy/book-type.html?eventId=${eventId}`;
      return;
    }
  }, 1000);
};

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

const initBookInfoJSEvents = async (book) => {
  // 共同變數，url 後方的活動 id
  const eventId = getUrlParam("eventId");

  // ====== "上一步" 按鈕點擊事件 ======
  $(".back").on("click", () => {
    const keepGoing = confirm("票種選擇結果會清空，要重新選擇?");
    location.href = `${getContextPath()}/user/buy/book-type.html?eventId=${eventId}`;
  });
  // ====== "下一步" 按鈕點擊事件 ======
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", async () => {
    addTicketInfoToContactAndAttendee(book);
    book.progress = BOOKING_PROGRESS.ORDER_CONFIRM; // 個人資料填寫完成，進入下一步
    await saveBook(book);
  });
};

// ==================== 4. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 渲染計時器
 * @param {number} bookTTL - 訂單剩餘時間(秒)
 */
const renderTimer = (bookTTL) => {
  // 抓取元素
  const $timeToShow = $(".counter-container").find(".time-remaining");

  // 插入時間文字
  const formattedTTL = formatTime(bookTTL);
  $timeToShow.text(formattedTTL);
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

  // ====== 計時器顯示部分 ======
  const bookTTL = await findBookTTL(); // 一次性取得 TTL
  await countDown(bookTTL, renderTimer); // 定期更新並渲染

  // ====== book-info 部分 ======
  initBookInfoJSEvents(book); // 載入事件

  // ====== contact-box 部分 ======
  const buyer = await fetchMember(book.userName); // 查操作人自己
  const contactBoxTemplate = await fetchContactBoxTemplate();
  renderContactBox(contactBoxTemplate, buyer); // 渲染模板

  // ====== attendee-box 部分 ======
  const attendeeBoxTemplate = await fetchAttendeeBoxTemplate();
  const { selected } = book;
  renderAttendeeBox(attendeeBoxTemplate, selected); // 渲染模板
  initAttendeeBoxJSEvents(book); // 載入事件

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
