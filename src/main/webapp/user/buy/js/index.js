// ==================== 載入模組 (All Imports At Top) ====================
import { getUrlParam, getContextPath } from "../../common/utils.js";
import { BOOKING_PROGRESS, ERROR_MESSAGES } from "../../common/constant.js";
import {
  fetchNavTemplate,
  renderNav,
  initNavJSEvents,
} from "../../layout/nav/nav.js";
import {
  fetchFooterTemplate,
  renderFooter,
} from "../../layout/footer/footer.js";
import {
  fetchEventInfoTemplate,
  renderEventInfoBox,
  initEventBoxJSEvents,
} from "../ui/index/event-box.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

/**
 * 查詢熱門活動資料。
 *
 * @return {Object} 近期9筆活動資料。
 */
export const fetchEventInfo = async () => {
  const resp = await fetch(`${getContextPath()}/search-event`);
  return await resp.json();
};

/** 透過活動資料的 keywordId 值，查詢所有keyword名稱。
 *
 * @param {number} keywordId - 關鍵字 id。
 * @return {Object} 對應所有keyword名稱陣列。
 */
export const fetchKeyword = async (keywordId) => {
  const resp = await fetch(
    `${getContextPath()}/search-event/keyword/${keywordId}`
  );
  return await resp.json();
};

/** 透過 session.member 查詢我的關注資料。
 *
 * @return {Array<Object>} 我的關注資料。
 * (其中 data 空陣列代表查無資料)
 */
export const fetchFavorite = async () => {
  const resp = await fetch(`${getContextPath()}/search-event/like`);
  return await resp.json();
};

/** 點開頁面愛心按鈕，加入關注。
 *
 * @param {number} eventId - 新增關注 eventId。
 * @return {Object} 加入關注操作結果。
 */
export const saveFavorite = async (eventId) => {
  const favoriteDto = { eventId }; // 將關注資料包裝成 Dto
  const resp = await fetch(`${getContextPath()}/search-event/like`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(favoriteDto),
  }); // 傳至後端新增或加入關注
  return await resp.json();
};

/** 再次點開頁面愛心按鈕，移除關注。
 *
 * @param {number} eventId - 欲刪除關注資料。
 * @return {Object} 移除關注操作結果。
 */
export const deleteFavorite = async (eventId) => {
  const resp = await fetch(`${getContextPath()}/search-event/like/${eventId}`, {
    method: "DELETE",
  }); // 傳至後端移除關注
  return await resp.json();
};

// ==================== 2. 數據處理層 (Data Processing) ====================
// 這些函數負責從 DOM 中提取數據，並對數據進行格式化或轉換。

/**
 * 抓取所有個人資料輸入框的數值，並放入 book 物件當中。
 * @param {Object} book - book 物件，包含儲存聯絡人資訊的 contact 及儲存入場者資訊的 attendee 陣列。
 */
// const addTicketInfoToContactAndAttendee = (book) => {...}

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

const initIndexJSEvents = () => {
  // 搜尋功能
  $(".search-btn").on("click", () => {
    // 去輸入空白
    const $searchInput = $(".search-input");
    const searchTerm = $searchInput.val().trim();

    // 建立查詢參數物件
    const params = new URLSearchParams();
    params.append("searchTerm", searchTerm);

    // 將搜尋字串傳遞至 URL 後方並跳轉
    location.href = `${getContextPath()}/user/buy/search.html?${params.toString()}`;
  });

  // 支援 Enter 鍵搜尋(e.which 為 13)
  $(".search-input").on("keypress", (e) => {
    if (e.which === 13) {
      $(".search-btn").click();
    }
  });
};

// ==================== 4. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

(async () => {
  // ====== 資料儲存變數區 ======
  const eventResponse = await fetchEventInfo();
  console.log(eventResponse); // ok!!!

  // ====== nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  await renderNav(navTemplate);
  initNavJSEvents();

  // ====== event-box 部分 ======
  const eventTemplate = await fetchEventInfoTemplate();
  await renderEventInfoBox(eventTemplate, eventResponse);
  initEventBoxJSEvents();

  // ====== index 部分 ======
  initIndexJSEvents(); // 載入 index 主要事件

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
