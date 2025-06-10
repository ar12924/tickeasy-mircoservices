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
  const templateJQuery = $(templateHTML);
  $(".navbar").append(templateJQuery);
};

// ==================== 2. DOM 事件處理與頁面邏輯 (DOM Events & Page Logic) ====================
// 這是主要頁面邏輯的入口點，負責綁定事件和協調不同層級的函數。
export const initNavJSEvents = () => {
  // "漢堡" 按鈕下拉動畫
  $(".navbar").on("click", ".navbar-burger", (e) => {
    $(e.currentTarget).toggleClass("is-active");
    $(".navbar-menu").toggleClass("is-active");
  });
  // "會員中心/登入" 按鈕點擊事件
  $(".navbar").on("mouseenter mouseleave", ".navbar-item button", (e) => {
    $(e.target).toggleClass("is-focused");
  });
};
