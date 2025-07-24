// ==================== 載入模組 (All Imports At Top) ====================
import {
  fetchMemberFromSession,
  getContextPath,
  getUrlParam,
} from "../../../common/utils.js";
import {
  deleteFavorite,
  fetchFavorite,
  fetchKeyword,
  saveFavorite,
  searchEventInfo,
} from "../../js/search.js";

// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 event-box.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchEventInfoTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/search/event-box.html`
  );
  return await resp.text();
};

/**
 * 預先載入 empty.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchEmptyTemplate = async () => {
  const resp = await fetch(`${getContextPath()}/user/buy/ui/search/empty.html`);
  return await resp.text();
};

/**
 * 預先載入 pagination.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchPaginationTemplate = async () => {
  const resp = await fetch(
    `${getContextPath()}/user/buy/ui/search/pagination.html`
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

  // 如果活動資料是空陣列，則顯示空白頁
  if (eventResponse.data.length === 0) {
    // 載入 empty.html
    const emptyHTML = await fetchEmptyTemplate();
    const $empty = $(emptyHTML);
    // 直接插入 DOM 並結束渲染
    $(".event-container").append($empty);
    return;
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

/**
 * 動態插入 分頁(pagination)的 HTML。
 *
 * @param {string} templateHTML - HTML模板。
 * @param {number} totalItemCount - 查詢活動資料之個數。
 * @param {number} currentPage - 當前點擊頁數。
 * @param {number} PageSize - 每頁活動框之個數。
 */
export const renderPagination = (
  templateHTML,
  totalItemCount,
  currentPage,
  PageSize
) => {
  const totalPage = Math.ceil(totalItemCount / PageSize);
  if (totalPage <= 1) {
    // 只有一頁，不顯示分頁條
    $(".pagination-container").html("");
    return;
  }

  const $template = $(templateHTML); // 分頁主模板

  // 上一頁按鈕
  const $prevBtn = $template.find(".pagination-previous");
  if (currentPage === 1) {
    $prevBtn.addClass("is-disabled");
  }
  $prevBtn.attr("data-page", currentPage - 1);

  // 下一頁按鈕
  const $nextBtn = $template.find(".pagination-next");
  if (currentPage === totalPage) {
    $nextBtn.addClass("is-disabled");
  }
  $nextBtn.attr("data-page", currentPage + 1);

  // 頁碼列表(所有 li 的父元素)
  const $paginationList = $template.find(".pagination-list li");

  // 第一頁(1, 2, curr = 3)
  if (currentPage > 2) {
    $paginationList
      .eq(0)
      .removeClass("is-hidden")
      .find(".pagination-link")
      .attr("data-page", 1)
      .attr("aria-label", `Goto page ${1}`)
      .text(`${1}`);
    if (currentPage > 3) {
      $paginationList.eq(1).removeClass("is-hidden");
    }
  }

  // 當前頁面附近的頁碼(currentPage - 1, cuurentPage, currentPage + 1)
  for (let i = currentPage - 1; i <= currentPage + 1; i++) {
    const listIndex = i - currentPage + 3;

    // 無效頁碼，隱藏
    if (i < 1 || i > totalPage) {
      $paginationList.eq(listIndex).addClass("is-hidden");
      continue;
    }

    // 有效頁碼，顯示並設定內容
    $paginationList
      .eq(listIndex)
      .removeClass("is-hidden")
      .find(".pagination-link")
      .attr("data-page", i)
      .attr("aria-label", `Goto page ${i}`)
      .text(`${i}`);

    // 如果是當前頁，加上 is-current 類別
    if (i === currentPage) {
      $paginationList
        .eq(listIndex)
        .find(".pagination-link")
        .attr("data-page", i)
        .attr("aria-label", `Page ${i}`)
        .addClass("is-current");
    }
  }

  // 最後一頁(curr = 8, 9, 10)
  if (currentPage < totalPage - 1) {
    if (currentPage < totalPage - 2) {
      $paginationList.eq(5).removeClass("is-hidden");
    }
    $paginationList
      .eq(6)
      .removeClass("is-hidden")
      .find(".pagination-link")
      .attr("data-page", totalPage)
      .attr("aria-label", `Goto page ${totalPage}`)
      .text(`${totalPage}`);
  }

  // 插入 DOM 當中
  $(".pagination-container").html($template);
};

/**
 * 顯示指定頁面(含主內容、pagination)
 * @param {number} currentPage - 當前頁數。
 * @param {Object} eventResponse - 查詢活動結果。
 * (含 data, PageSize, count)
 */
export const showPage = async (currentPage, eventResponse) => {
  const eventTemplate = await fetchEventInfoTemplate();
  // 資料已在後端 eventResponse
  await renderEventInfoBox(eventTemplate, eventResponse);
  const paginationTemplate = await fetchPaginationTemplate();
  renderPagination(
    paginationTemplate,
    eventResponse.count,
    currentPage,
    eventResponse.pageSize
  ); // pageSize 固定為 9 (同後端寫死!!)

  // 滾動到頂部
  $("html, body").animate(
    {
      scrollTop: $(".event-container").offset().top,
    },
    300
  );
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

  // 分頁點擊事件
  $(document).on(
    "click",
    ".pagination-link, .pagination-previous, .pagination-next",
    async (e) => {
      e.preventDefault();
      const currentPage = parseInt($(e.currentTarget).data("page"));
      console.log(currentPage);
      // 載入後續分頁(再打一次 API)
      const searchTerm = getUrlParam("searchTerm");
      const order = $(".time-filter").attr("data-filter");
      const eventResponse = await searchEventInfo({
        searchTerm,
        page: currentPage,
        order,
      });
      console.log(eventResponse); // ok!!
      if (
        currentPage &&
        currentPage >= 1 &&
        currentPage <=
          Math.ceil(eventResponse.count / eventResponse.pageSize) &&
        !$(e.currentTarget).hasClass("is-disabled")
      ) {
        await showPage(currentPage, eventResponse);
      }
    }
  );

  // 時間篩選功能
  $(".filter-condition").on("click", async (e) => {
    e.preventDefault();
    const filterElement = $(e.currentTarget).closest(".time-filter");

    // 抽換篩選器文字
    const conditionText = $(e.currentTarget).text();
    filterElement.find(".filter-name").text(conditionText);

    // filter 屬性切換
    if (conditionText === "由近到遠") {
      filterElement.attr("data-filter", "ASC");
    }
    if (conditionText === "由遠到近") {
      filterElement.attr("data-filter", "DESC");
    }

    // 載入第1頁
    const searchTerm = getUrlParam("searchTerm");
    const order = $(".time-filter").attr("data-filter");
    const eventResponse = await searchEventInfo({ searchTerm, order });
    console.log(eventResponse);
    await showPage(1, eventResponse);
  });
};
