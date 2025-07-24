// ==================== 載入模組 (All Imports At Top) ====================
import {
  fetchMemberFromSession,
  getContextPath,
} from "../../../common/utils.js";
import {
  deleteFavorite,
  fetchFavorite,
  fetchKeyword,
  saveFavorite,
} from "../../js/index.js";

// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 event-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchEventInfoTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/index/event-box.html`
  );
  return await resp.text();
};

/**
 * 動態插入活動資料的 HTML。
 *
 * @param {string} templateHTML - HTML模板。
 * @param {Array<Object>} eventResponse - 活動資料查詢結果。
 */
export const renderEventInfoBox = async (templateHTML, eventResponse) => {
  $(".event-container").empty();

  // 抓取關注資料
  let favoriteIdArr = [];
  const favoriteResult = await fetchFavorite();
  if (favoriteResult.successful) {
    // 拆解 data 為一個陣列
    favoriteIdArr = favoriteResult.data.map((item) => item.eventId);
  }

  // 依序查找每個活動資料
  for (const eventOne of eventResponse.data) {
    const $template = $(templateHTML);

    // 解構需要的欄位
    const {
      eventName,
      place,
      eventFromDate,
      eventHost,
      keywordId,
      eventId,
      image,
    } = eventOne;

    // 將數據放入標籤
    $template.find(".event-title").text(eventName);
    $template.find(".event-location").text(place);
    $template.find(".event-date").text(eventFromDate.substring(0, 16));
    $template.find(".event-holder").text(eventHost);
    const keywordData = await fetchKeyword(keywordId);
    $template.find(".keyword-tag").text(keywordData.keywordName1);
    $template
      .find(".btn-purchase")
      .prop(
        "href",
        `${getContextPath()}/user/buy/event_ticket_purchase.html?eventId=${eventId}`
      );
    // 自訂屬性標記活動 id
    $template.find(".event-card").attr("data-event-id", eventId);

    // 處理圖片
    if (image) {
      const imageSrcLink = `data:image/jpeg;base64,${image}`;
      $template.find(".event-image").attr("src", imageSrcLink);
    }

    // 檢查 favor 陣列，有 eventId 者，改變關注按鈕
    if (favoriteIdArr.includes(eventId)) {
      $template
        .find(".favorite-btn")
        .css("background", "#ff6b9d")
        .css("color", "white");
      $template
        .find(".favorite-btn")
        .find("i")
        .removeClass("far")
        .addClass("fas");
      $template.find(".event-card").addClass("favor-active");
    }

    // 插入 DOM 根元素
    $(".event-container").append($template);
  }
};

// ==================== 2. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。

export const initEventBoxJSEvents = () => {
  $(".event-container").on("click", ".favorite-btn", async (e) => {
    let result;
    const favorCard = $(e.target).closest(".event-card");
    const eventId = favorCard.attr("data-event-id");

    // 沒有會員身份，要求先行登入
    const memberResult = await fetchMemberFromSession();
    if (!memberResult.successful) {
      // alert("請先行登入");
      await Swal.fire("請先行登入");
      location.href = `${getContextPath()}/user/member/login.html`;
      return;
    }

    // 有會員身份後，判斷加入或移除關注資料
    if (favorCard.hasClass("favor-active")) {
      // 移除關注資料
      result = await deleteFavorite(eventId);
      console.log(result);
      favorCard.removeClass("favor-active");
    } else {
      // 加入關注資料
      result = await saveFavorite(eventId);
      console.log(result);
      favorCard.addClass("favor-active");
    }

    // 愛心按鈕點擊效果
    const $btn = $(e.target).closest(".favorite-btn");
    const $icon = $btn.find("i");
    if ($icon.hasClass("far")) {
      $icon.removeClass("far");
      $icon.addClass("fas");
      $btn.css("background", "#ff6b9d");
      $btn.css("color", "white");
    } else {
      $icon.removeClass("fas");
      $icon.addClass("far");
      $btn.css("background", "white");
      $btn.css("color", "#333");
    }
  });
};
