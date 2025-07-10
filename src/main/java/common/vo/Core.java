package common.vo;

import lombok.Data;

@Data
public class Core<T> {
	// === 操作結果 ===
	/** 操作是否成功 */
	private boolean successful;
	/** 身份驗證狀態 */
	private AuthStatus authStatus;
	/** 資料處理狀態 */
	private DataStatus dataStatus;
	/** 結果訊息 */
	private String message;

	// === 分頁資訊 ===
	/** 總資料筆數 */
	private Long count;
	/** 當前頁面大小 */
	private Integer pageSize;
	
	// === 時間資訊 ===
	/** 剩餘時間 */
	private Long remainingTime;
	
	// === 傳輸資料 ===
	/** 核心資料內容 */
	private T data;
}