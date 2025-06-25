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

/**
 * 身分證格式驗證用。
 * @param {string} idCard - 身分證字號。
 * @returns {boolean} 驗證成功/失敗。
 */
export const validateIdCard = (idCard) => {
  return /^[A-Z][0-9]{9}$/.test(idCard);
};
