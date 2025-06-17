// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 footer.html 模板。
 */
export const fetchFooterTemplate = async () => {
  const resp = await fetch("../layout/footer/footer.html");
  return await resp.text();
};

/**
 * 動態生成並插入頁腳的 HTML。
 */
export const renderFooter = async (templateHTML) => {
  templateHTML = $(await fetchFooterTemplate());
  $("footer").append(templateHTML);
};
