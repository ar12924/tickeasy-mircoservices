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

/**
 * 從後端 Session 抓取會員資料。
 * @return {Object} member 和驗證結果。
 */
export const fetchMemberFromSession = async () => {
  const resp = await fetch(`${getContextPath()}/common/authenticate`);
  return await resp.json();
};

/**
 * 秒數格式化為 mm:ss。
 * @param {number} seconds - 秒數。
 * @return {string} 轉換為 mm:ss 字串。
 */
export const formatTime = (seconds) => {
  // 計算(分、秒)
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;

  // 回傳格式化時間(padStart() 來填充 0 開頭)
  return `${minutes.toString().padStart(2, "0")}:${remainingSeconds
    .toString()
    .padStart(2, "0")}`;
};
