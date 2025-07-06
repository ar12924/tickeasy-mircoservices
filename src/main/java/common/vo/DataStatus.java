package common.vo;

public enum DataStatus {
	// 資料是否合法？
	VALID, INVALID,
	// 資料找到與否？
	FOUND, NOT_FOUND,
	// 比較成功與否？
	COMPARISON_PASSED, COMPARISON_FAILED,
	// 權限不足
	FORBIDDEN,
	// 操作成功與否？
	EXECUTION_PASSED, EXECUTION_FAILED
}
