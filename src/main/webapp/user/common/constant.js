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
  MISSING_EVENT_ID: "缺少活動識別碼，請重新選擇活動！",
  // 資料活動 id 與當前活動 id 不一致
  EVENT_ID_INCONSISTENT: "資料與活動識別碼不符，請重新選擇活動！",
  // 未選擇票券錯誤
  NO_TICKETS_SELECTED: "請至少選擇1種票券！",
});
