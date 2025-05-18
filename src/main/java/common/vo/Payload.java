package common.vo;

import lombok.Data;

@Data
public class Payload<T> {
	private boolean successful;
	private String message;
	private Long count;
	private T data;
}