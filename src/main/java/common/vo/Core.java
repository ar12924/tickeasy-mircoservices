package common.vo;

import java.util.List;

import lombok.Data;

@Data
public class Core<T> {
	// 訊息放 here ~
	private boolean successful;
	private String message;
	private Long count;
	// 資料放 here ~
	private List<T> data;
}