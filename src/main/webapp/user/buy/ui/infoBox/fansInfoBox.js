// ==================== 1. UI 渲染層 (UI Rendering Layer) ====================
// 這些函數負責動態生成或更新 HTML 內容。

/**
 * 預先載入 infoBox.html 模板。
 * @return {Promise<string>} HTML 模板。
 */
export const fetchFansInfoBoxTemplate = async () => {
  const resp = await fetch("./ui/infoBox/fansInfoBox.html");
  return await resp.text();
};

/**
 * 動態生成並插入導覽列的 HTML。
 * @param {string} templateHTML - HTML模板。
 */
export const renderFansInfoBox = async (templateHTML) => {
  const templateJQeury = $(templateHTML);
  $(".fans-info-container").append(templateJQeury);
};
