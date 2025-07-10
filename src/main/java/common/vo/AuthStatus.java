package common.vo;

public enum AuthStatus {
	// 已登入
	LOGGED_IN,
	// 未登入
	NOT_LOGGED_IN,
	// token 已過期
	TOKEN_EXPIRED,
	// 已登出
	LOGOUT,
	// 權限不足
	FORBIDDEN
}
