// ==================== 1. 工具函數 (Utilities) ====================
// 這些函數負責處理一些通用的、無關特定業務邏輯的任務

/**
 * 從 URL 查詢參數中獲取指定參數的值。
 * @param {string} paramName - 要獲取的參數名稱。
 * @returns {string|null} 參數的值，如果不存在則為 null。
 */
export const getUrlParam = (paramName) => {
  const queryString = window.location.search;
  const urlParams = new URLSearchParams(queryString);
  return urlParams.get(paramName);
};

/**
 * 擷取 http 到專案名稱部分網址。
 * @returns {string|null} 參數的值，如果不存在則為 null。
 */
export const getContextPath = () => {
  return window.location.pathname.substring(
    0,
    window.location.pathname.indexOf("/", 2)
  );
};

// ==================== 2. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動，處理請求的發送和響應的接收。

/**
 * 將選定的票種和數量數據 POST 到後端 Redis 進行保存。
 * @param {Object} book - 包含票種選擇訊息的物件。
 */
const saveBook = async (book) => {
  const resp = await fetch(`${getContextPath()}/book-type`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(book),
  });
  const respBody = await resp.json();
  console.log("Redis Save Response: ", respBody);
};

// ==================== 3. 數據處理層 (Data Processing) ====================
// 這些函數負責從 DOM 中提取數據，並對數據進行格式化或轉換。

/**
 * 從頁面中抓取所有票種輸入框的 quantity 並加入 book 物件中
 * @param {Array<Object>} selected - book 物件中的 selected 屬性陣列。
 * @returns {boolean} 如果成功更新 selected 屬性則返回 true，否則返回 false。
 */
const addTicketTypeToSelected = ({ selected }) => {
  const quantityElArr = document.querySelectorAll(".type-quantity");
  // 判斷輸入框值總和為0，停止事件執行
  let sum = 0;
  quantityElArr.forEach((quan, i) => {
    sum += Number(quan.value) || 0;
  });
  if (sum === 0) {
    alert("請至少選擇1種票券!!");
    return false;
  }
  // selected 中加上 quantity 屬性值
  if (quantityElArr.length === selected.length) {
    quantityElArr.forEach((quan, i) => {
      selected[i]["quantity"] = Number(quan.value) || 0;
    });
  } else {
    alert("選擇的票種和頁面的票種不一致，請聯絡管理人員!!");
    return false;
  }
  return true;
};

// ==================== 4. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

/**
 * 架設 book-type 頁面上所有事件監聽器。
 * @param {Object} book - 儲存票券訂購資訊。
 */
const initBookTypeJSEvents = (book) => {
  // 共同變數，url 後方的活動 id
  const eventId = getUrlParam("eventId");

  // ====== "更新票券" 按鈕點擊事件 ======
  $(".update").on("mouseenter mouseleave", (e) => {
    $(e.target).closest(".update").toggleClass("is-focused");
  });

  // ====== "上一步" 按鈕點擊事件 ======
  $(".back").on("click", () => {
    location.href = "https://www.google.com";
  });

  // ====== "下一步" 按鈕點擊事件 ======
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", async () => {
    if (!eventId) {
      alert("缺少活動id，無法繼續!!");
      return;
    }
    const success = addTicketTypeToSelected(book); // 添加購票人選擇的數量
    if (!success) {
      return;
    } else {
      book.progress = 1; // 表示購票頁資料OK，可以送到後端
      console.log(book);
      saveBook(book); // post 使用者選的票種至 Redis
      // location.href = `bookDetails.html?eventId=${eventId}`;
      return;
    }
  });
};

// ==================== 5. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

import { fetchNavTemplate } from "../../layout/nav/nav.js";
import { renderNav } from "../../layout/nav/nav.js";
import { initNavJSEvents } from "../../layout/nav/nav.js";
import { fetchHeaderTemplate } from "../ui/book-type/header.js";
import { renderHeader } from "../ui/book-type/header.js";
import { fetchTicketType } from "../ui/book-type/type-box.js";
import { fetchTypeBoxTemplate } from "../ui/book-type/type-box.js";
import { renderTypeBox } from "../ui/book-type/type-box.js";
import { initTypeBoxJSEvents } from "../ui/book-type/type-box.js";
import { fetchFooterTemplate } from "../../layout/footer/footer.js";
import { renderFooter } from "../../layout/footer/footer.js";

(async () => {
  // ====== 資料儲存變數區 ======
  // const book = {
  //   eventId: -1, // 活動 id
  //   eventName: null, // 活動名稱
  //   selected: [], // [{票種1}, {票種2}, ...]
  //   progress: -1, // 1: 購票頁完成; 2: 填寫完成; 3: 下訂完成
  //   contact: {}, // {聯絡人}
  //   attendees: [], // [{入場者1}, {入場者2}, ...]
  // };
  const book = {
    eventId: -1, // 活動 id
    eventName: null, // 活動名稱
    selected: [], // [{票種1}, {票種2}, ...]
    progress: -1, // 1: 購票頁完成; 2: 填寫完成; 3: 下訂完成
  };

  // ====== Nav 部分 ======
  const navTemplate = await fetchNavTemplate();
  renderNav(navTemplate);
  initNavJSEvents();

  // ====== Header 部分 ======
  const headerTemplate = await fetchHeaderTemplate();
  renderHeader(headerTemplate);

  // ====== typeBox 部分 ======
  const eventId = getUrlParam("eventId");
  const ticketType = await fetchTicketType(eventId);
  const TypeBoxTemplate = await fetchTypeBoxTemplate();
  if (ticketType.length > 0) {
    for (const typeInfo of ticketType) {
      // 存入 book 變數中，並儲存 typeId 編號
      book.eventId = eventId;
      book.selected.push({
        typeId: typeInfo.typeId,
        categoryName: typeInfo.categoryName,
      });
      // 輸出 typeBox.html 模板
      renderTypeBox(typeInfo, TypeBoxTemplate);
    }
  } else {
    alert("載入票種失敗!!");
    return;
  }
  initTypeBoxJSEvents();

  // ====== bookType 部分 ======
  initBookTypeJSEvents(book); // book 傳入，接收購票人選擇的票數 quantity 並送到後端

  // ====== footer 部分 ======
  const footerTemplate = await fetchFooterTemplate();
  await renderFooter(footerTemplate);
})();
