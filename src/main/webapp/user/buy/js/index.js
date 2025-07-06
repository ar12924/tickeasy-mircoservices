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
  renderRecentEventBox,
  initEventBoxJSEvents,
} from "../ui/index/event-box.js";

// ==================== 1. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

/**
 * 查詢熱門活動資料。
 *
 * @return {Object} 近期9筆活動資料。
 */
export const fetchRecentEventInfo = async () => {
  const resp = await fetch(`${getContextPath()}/search-event/recent`);
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
 */
export const fetchFavorite = async () => {
  const resp = await fetch(`${getContextPath()}/search-event/like`);
  return await resp.json();
};

/** 點擊頁面愛心按鈕新增關注時，將關注資料存入 Favorite 表。
 *
 * @param {number} eventId - 欲新增關注資料(僅一個欄位)。
 * @return {Object} 關注資料儲存後操作結果。
 */
export const saveFavorite = async (eventId) => {
  const favoriteDto = { eventId }; // 將關注資料包裝成 Dto
  const resp = await fetch(`${getContextPath()}/search-event/like`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(favoriteDto),
  }); // 傳至後端儲存至 Favorite 表
  return await resp.json();
};

/** 再次點擊移除頁面愛心按鈕關注時，從 Favorite 表移除對應的一筆關注。
 *
 * @param {number} eventId - 欲刪除關注資料。
 * @return {Object} 關注資料刪除後操作結果。
 */
export const deleteFavorite = async (eventId) => {
  const resp = await fetch(`${getContextPath()}/search-event/like/${eventId}`, {
    method: "DELETE",
  }); // 傳至後端移除 Favorite 表中一筆資料
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
  // 愛心按鈕點擊效果
  // $(".favorite-btn").each((i, btn) => {
  //   const $btn = $(btn);
  //   $btn.on("click", (e) => {
  //     const $icon = $btn.find("i");
  //     if ($icon.hasClass("far")) {
  //       $icon.removeClass("far");
  //       $icon.addClass("fas");
  //       $btn.css("background", "#ff6b9d");
  //       $btn.css("color", "white");
  //     } else {
  //       $icon.removeClass("fas");
  //       $icon.addClass("far");
  //       $btn.css("background", "white");
  //       $btn.css("color", "#333");
  //     }
  //   });
  // });
  // $(".favorite-btn").on("click", (e) => {
  //   const $icon = $(e.target).closest(".favorite-btn").find("i");
  //   if ($icon.hasClass("far")) {
  //     $icon.removeClass("far");
  //     $icon.addClass("fas");
  //     $btn.css("background", "#ff6b9d");
  //     $btn.css("color", "white");
  //   } else {
  //     $icon.removeClass("fas");
  //     $icon.addClass("far");
  //     $btn.css("background", "white");
  //     $btn.css("color", "#333");
  //   }
  // });

  // 搜尋功能
  $(".search-btn").on("click", () => {
    const $searchInput = $(".search-input");
    if ($searchInput.val().trim()) {
      alert("搜尋功能：" + $searchInput.val());
    }
  });
};

// ==================== 4. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

(async () => {
  // ====== 資料儲存變數區 ======
  const recentEvent = await fetchRecentEventInfo();
  console.log(recentEvent); // ok!!!

  // ====== nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  await renderNav(navTemplate);
  initNavJSEvents();

  // ====== event-box 部分 ======
  const eventTemplate = await fetchEventInfoTemplate();
  await renderRecentEventBox(eventTemplate, recentEvent);
  initEventBoxJSEvents();

  // ====== index 部分 ======
  initIndexJSEvents(); // 載入 index 主要事件

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  renderFooter(footerTemplate);
})();
