// ==================== 定義常數 (Constant Definition) ====================
/**
 * 訂票流程進度
 * @readonly
 * @enum {number}
 */
export const BOOKING_PROGRESS = Object.freeze({
  // 選票中
  TYPE_SELECTION: 1,
  // 填寫資料中
  INFO_FILLING: 2,
  // 確認訂單中
  ORDER_CONFIRM: 3,
  // 完成
  FINISHED: 4,
});

/**
 * 常用錯誤訊息
 * @readonly
 * @enum {string}
 */
export const ERROR_MESSAGES = Object.freeze({
  // 缺少活動 id 錯誤
  MISSING_EVENT_ID: "缺少活動id，無法繼續!!",
  // 未選擇票券錯誤
  NO_TICKETS_SELECTED: "請至少選擇1種票券!!",
  // 需要登入錯誤
  LOGIN_REQUIRED: "請先登入",
  // 未正常存取資料
  ACCESSED_FAILED: "存取資料失敗",
});
