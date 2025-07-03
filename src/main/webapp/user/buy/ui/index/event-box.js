// ==================== 載入模組 (All Imports At Top) ====================
import { getContextPath } from "../../../common/utils.js";
import { fetchKeyword } from "../../js/index.js";

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
 * 動態插入近期 9 筆活動資料的 HTML。
 *
 * @param {string} templateHTML - HTML模板。
 * @param {Array<Object>} recentEvent - 9筆近期活動資料。
 */
export const renderRecentEventBox = async (templateHTML, recentEvent) => {
  $(".event-container").empty();

  // 依序查找每個活動資料
  for (const eventOne of recentEvent) {
    const $template = $(templateHTML);
    // 解構需要的欄位
    const { eventName, place, eventFromDate, eventHost, keywordId, eventId } =
      eventOne;
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
    // 插入 DOM 根元素
    $(".event-container").append($template);
  }
};
