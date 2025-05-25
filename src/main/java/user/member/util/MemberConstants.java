package user.member.util;

//import user.member.service.MemberService;
//import user.member.service.impl.MemberServiceImpl;

public class MemberConstants {
//    public static final MemberService SERVICE = new MemberServiceImpl();
    
	// 驗證有效時間 - TOKEN_EXPIRATION
	public static final long TOKEN_EXPIRATION = 24 * 60 * 60 * 1000; // 24小時

	// 會員資料相關常量
	public static final String PHOHE_PATTERN = "^09\\d{8}$";
	public static final String UNICODE_PATTERN = "\\d{8}";
	public static final String EMAIL_PATTERN = "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$";
	public static final String ID_PATTERN = "[A-Za-z].*";

}
