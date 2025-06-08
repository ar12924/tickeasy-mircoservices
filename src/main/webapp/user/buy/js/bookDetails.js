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

// ==================== 4. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

const initBookDetailsJSEvents = () => {
  // 共同變數，url 後方的活動 id
  const eventId = getUrlParam("eventId");

  // ====== "上一步" 按鈕點擊事件 ======
  $(".back").on("click", () => {
    location.href = `bookTickets.html?eventId=${eventId}`;
  });
  // ====== "下一步" 按鈕點擊事件 ======
  $(".next").on("mouseenter mouseleave", (e) => {
    $(e.target).toggleClass("is-focused");
  });
  $(".next").on("click", () => {
    if (!eventId) {
      alert("缺少活動id，無法繼續!!");
      return;
    }
    location.href = "#";
  });
};

// ==================== 5. 頁面初始化 (Initialization) ====================
// 確保 DOM 加載完成後再執行初始化邏輯

// Nav 部分
import { fetchNavTemplate } from "../../layout/nav/nav.js";
import { renderNav } from "../../layout/nav/nav.js";
import { initNavJSEvents } from "../../layout/nav/nav.js";
$(async () => {
  const template = await fetchNavTemplate();
  await renderNav(template);
  initNavJSEvents();
});

// bookDetails 部分
$(() => {
  initBookDetailsJSEvents(); // 載入 JS 事件監聽
});

// customBox 部分
import { fetchCustomBoxTemplate } from "../ui/customBox/customBox.js";
import { renderCustomBox } from "../ui/customBox/customBox.js";
$(async () => {
  const template = await fetchCustomBoxTemplate();
  await renderCustomBox(template);
});

// footer 部分
import { fetchFooterTemplate } from "../../layout/footer/footer.js";
import { renderFooter } from "../../layout/footer/footer.js";
$(async () => {
  const template = await fetchFooterTemplate();
  await renderFooter(template);
});
