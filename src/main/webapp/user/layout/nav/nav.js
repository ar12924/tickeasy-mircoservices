// ==================== import all ====================
import { fetchMemberFromSession, getContextPath } from "../../common/utils.js";

// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 nav.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchNavTemplate = async () => {
  const resp = await fetch("../layout/nav/nav.html");
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateHTML - HTML模板。
 */
export const renderNav = async (templateHTML) => {
  const $template = $(templateHTML);

  // 抓取每個按鈕
  const $homeBtn = $template.find(".go-home");
  const $registerBtn = $template.find(".register");
  const $loginBtn = $template.find(".login");
  const $orderBtn = $template.find(".order");
  const $concernBtn = $template.find(".concern");
  const $ticketBtn = $template.find(".ticket");
  const $notifyBtn = $template.find(".notify");
  const $userBtn = $template.find(".user");

  // 加入 URL 連結
  $homeBtn.attr("href", `${getContextPath()}/user/buy/index.html`);
  $registerBtn.attr("href", `${getContextPath()}/user/member/register.html`);
  $loginBtn.attr("href", `${getContextPath()}/user/member/login.html`);
  $orderBtn.attr("href", `${getContextPath()}/user/order/order.html`);
  $concernBtn.attr("href", `${getContextPath()}/222`);
  $ticketBtn.attr("href", `${getContextPath()}/user/ticket/ticket.html`);
  $notifyBtn.attr("href", `${getContextPath()}/user/notify/notification.html`);
  $userBtn
    .find(".member")
    .attr("href", `${getContextPath()}/user/member/edit.html`);

  // 判斷會員是否登入？(且 roleLevel == 1)
  const authenticateCore = await fetchMemberFromSession();
  const isLoggedIn = authenticateCore.successful;
  const memberData = authenticateCore.data;
  if (isLoggedIn && memberData && memberData.roleLevel === 1) {
    // 已登入
    $registerBtn.addClass("is-hidden");
    $loginBtn.addClass("is-hidden");
    $orderBtn.removeClass("is-hidden");
    $concernBtn.removeClass("is-hidden");
    $ticketBtn.removeClass("is-hidden");
    $notifyBtn.removeClass("is-hidden");
    $userBtn.removeClass("is-hidden");
    $userBtn.find(".user-name").text(memberData.nickName); // 添加 userName
  } else {
    // 未登入
    $registerBtn.removeClass("is-hidden");
    $loginBtn.removeClass("is-hidden");
    $orderBtn.addClass("is-hidden");
    $concernBtn.addClass("is-hidden");
    $ticketBtn.addClass("is-hidden");
    $notifyBtn.addClass("is-hidden");
    $userBtn.addClass("is-hidden");
  }

  // 插入 DOM
  $(".navbar").html($template);
};

// ==================== 2. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。
export const initNavJSEvents = () => {
  // "漢堡" 按鈕下拉動畫
  $(".navbar").on("click", ".navbar-burger", (e) => {
    $(e.currentTarget).toggleClass("is-active");
    $(".navbar-menu").toggleClass("is-active");
  });

  // "使用者名稱" 下拉選單
  $(".dropdown").on("click", () => {
    $(".dropdown").toggleClass("is-active");
  });

  // "登出" 按鈕點擊
  $(".logout").on("click", async () => {
    const resp = await fetch(`${getContextPath()}/common/logout`);
    location.reload();
  });
};
