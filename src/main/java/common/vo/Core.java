package common.vo;

import lombok.Data;

@Data
public class Core<T> {
	// 訊息部分
	private boolean successful;
	private AuthStatus authStatus;
	private DataStatus dataStatus;
	private String message;
	private Long count;
	
	// 資料部分
	private T data;
}