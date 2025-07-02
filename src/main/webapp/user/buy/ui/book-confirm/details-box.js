// ==================== 載入模組 (All Imports At Top) ====================
import { getContextPath, getUrlParam } from "../../../common/utils.js";

// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 details-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchDetailBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-confirm/details-box.html`
  );
  return await resp.text();
};

/**
 * 預先載入 ticket-details-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchTicketDetailBoxTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/book-confirm/ticket-details-box.html`
  );
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateSelected - 票種選擇結果HTML模板。
 * @param {string} templateAttendee - 入場者資料HTML模板。
 * @param {Object} selected - 票種選擇結果。
 * @param {Object} attendee - 入場者資料。
 */
export const renderDetailBox = async (
  templateSelected,
  templateAttendee,
  { selected, attendee }
) => {
  // 抓取票種資料
  const eventId = getUrlParam("eventId");
  const typeData = await findEventType(eventId);

  $(".details-container").empty();
  // 開始填充模板(selected 部分)
  selected.forEach((selectOne) => {
    if (selectOne.quantity > 0) {
      const $templateSelected = $(templateSelected);
      $templateSelected.find(".type-name").text(selectOne.categoryName);
      $templateSelected.find(".type-quantity").text(selectOne.quantity);
      typeData.forEach((typeOne) => {
        if (selectOne.typeId === typeOne.typeId) {
          $templateSelected
            .find(".type-price")
            .text(`NT$ ${typeOne.price.toLocaleString("en-US")}`);
          const totalPrice = selectOne.quantity * typeOne.price;
          $templateSelected
            .find(".total-price")
            .text(`NT$ ${totalPrice.toLocaleString("en-US")}`);
          $(".details-container").append($templateSelected); // 插在中間

          // 開始填充模板(attendee 部分)
          attendee.forEach((attendeeOne) => {
            if (attendeeOne.typeId === typeOne.typeId) {
              const $templateAttendee = $(templateAttendee);
              $templateAttendee
                .find(".ticket-type")
                .text(`票券 #${selectOne.categoryName}`);
              $templateAttendee
                .find(".ticket-price")
                .text(`NT$ ${typeOne.price.toLocaleString("en-US")}`);
              $templateAttendee
                .find(".ticket-username")
                .text(attendeeOne.userName);
              $templateAttendee
                .find(".ticket-id-card")
                .text(attendeeOne.idCard);
              //
              $("tr:last-child").after($templateAttendee); // 接在後
            }
          });
        }
      });
    }
  });
};

// ==================== 2. API 服務層 (API Service Layer) ====================
// 這些函數負責與後端 API 進行互動。

/**
 * 從後端 api 獲取特定 event 的 type 數據。
 * @param {number} eventId - 活動 id。
 * @returns {Promise<Array<Object>>} 某活動 id 下的 type 數據的陣列。
 */
export const findEventType = async (eventId) => {
  const resp = await fetch(
    `${getContextPath()}/book-confirm/event/${eventId}/event-ticket-type`
  );
  return await resp.json();
};

// ==================== 3. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

export const initDetailBoxJSEvents = () => {
  // ====== "dropdow-arraow" 元素點擊事件 ======
  $(".dropdown-arrow").on("click", (e) => {
    // 抓取元素
    const $clickElement = $(e.target).closest(".dropdown-arrow");
    const $details = $clickElement.closest("tr").next();
    const $arrow = $clickElement.find("i");

    if ($details.hasClass("is-active")) {
      // 隱藏詳細資訊 - 先動畫再隱藏
      $details.transition({ opacity: 0, y: "-10px" }, 300, () => {
        $details.removeClass("is-active");
      });
      $arrow.transition({ rotate: "0deg" }, 200); // 箭頭旋轉動畫
    } else {
      // 顯示詳細資訊 - 先顯示再動畫
      $details.addClass("is-active");
      $details.transition({ opacity: 1, y: "0px" }, 300);
      $arrow.transition({ rotate: "180deg" }, 200); // 箭頭旋轉動畫
    }
  });
};
