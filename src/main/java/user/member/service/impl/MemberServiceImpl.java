package user.member.service.impl;

import java.sql.Date;
import java.util.List;
import java.util.UUID;

import user.member.dao.MemberDao;
import user.member.dao.VerificationDao;
import user.member.dao.impl.MemberDaoImpl;
import user.member.dao.impl.VerificationDaoImpl;
import user.member.entity.Member;
import user.member.entity.VerificationToken;
import user.member.service.MailService;
import user.member.service.MemberService;

public class MemberServiceImpl implements MemberService{
	private final MemberDao memberDao;
	private final VerificationDao verifyDao;
	private final MailService mailService;
	
    public MemberServiceImpl() {
        memberDao = new MemberDaoImpl();
		verifyDao = new VerificationDaoImpl();
		mailService = new MailServiceImpl();
    }
    
	@Override
	public Member register(Member member) {
        String username = member.getUserName();
        if (username == null || username.length() < 5 || username.length() > 50) {
            member.setMessage("使用者名稱長度須介於 5 到 50 字元");
            member.setSuccessful(false);
            return member;
        }

        String password = member.getPassword();
        if (password == null || password.length() < 6 ) {
            member.setMessage("密碼長度須至少 6 字元");
            member.setSuccessful(false);
            return member;
        }
        
        String phone = member.getPhone();
        if (phone == null || !phone.matches("^09\\d{8}$")) {
            member.setMessage("手機格式錯誤，需為台灣手機號碼 09 開頭共 10 碼");
            member.setSuccessful(false);
            return member;
        }

        String gender = member.getGender();
        if (gender == null || !(gender.equals("M") || gender.equals("F"))) {
            member.setMessage("性別請選擇男 (M) 或 女 (F)");
            member.setSuccessful(false);
            return member;
        }

        Date birthDate = member.getBirthDate();
        if (birthDate == null) {
            member.setMessage("請選擇出生日期");
            member.setSuccessful(false);
            return member;
        }


        if (memberDao.findByUserName(username) != null) {
            member.setMessage("此帳號已被註冊");
            member.setSuccessful(false);
            return member;
        }
        
        
        String unicode = member.getUnicode();
        if (unicode != null && !unicode.matches("\\d{8}")) {
            member.setMessage("統一編號格式錯誤，應為 8 碼數字");
            member.setSuccessful(false);
            return member;
        }
        
        String idCard = member.getIdCard();
        if (idCard != null && !idCard.matches("[A-Za-z].*")) {
            member.setMessage("身分證開頭應為英文字母");
            member.setSuccessful(false);
            return member;
        }
        
        String email = member.getEmail();
        if (email != null && !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            member.setMessage("電子郵件格式錯誤");
            member.setSuccessful(false);
            return member;
        }
        
        // 預設roleLevel = 0
        member.setRoleLevel(0);
        boolean inserted = memberDao.insert(member);
        member.setMessage(inserted ? "註冊成功！請查收驗證信以開通會員" : "註冊失敗");
        member.setSuccessful(inserted);
        
        // 寄出驗證信的完整步驟
        if (inserted) {
        	try {
            // Step 1：產生驗證用 token
            String tokenStr = UUID.randomUUID().toString();

            // Step 2：存進 verification_token 資料表
            VerificationToken token = new VerificationToken();
            token.setTokenId(UUID.randomUUID());
            token.setToken(tokenStr);
            token.setMember(member);
            token.setUsed(false);
            verifyDao.insert(token);

            // 確認是否可以執行            
            System.out.println("驗證 token 已建立：" + tokenStr);

            // Step 3：寄信
            mailService.sendActivationNotification(member.getEmail(), member.getUserName(), tokenStr);
        }
        	catch (Exception e) {
                System.err.println("驗證信發送失敗：" + e.getMessage());
        	}
        }
        return member;
	}
	
	@Override
	public Member editMember(Member member) {
        if (member.getPassword() != null &&
               (member.getPassword().length() < 6 )) {
                member.setMessage("密碼長度須大於 6 字元");
                member.setSuccessful(false);
                return member;
            }
        
        String unicode = member.getUnicode();
        if (unicode != null && !unicode.matches("\\d{8}")) {
            member.setMessage("統一編號格式錯誤，應為 8 碼數字");
            member.setSuccessful(false);
            return member;
        }
        
        String email = member.getEmail();
        if (email != null && !email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            member.setMessage("電子郵件格式錯誤");
            member.setSuccessful(false);
            return member;
        }


            boolean updated = memberDao.update(member);
            member.setMessage(updated ? "更新成功" : "系統錯誤，更新失敗");
            member.setSuccessful(updated);
            return member;
	}

	@Override
	public Member login(Member member) {
        String username = member.getUserName();
        String password = member.getPassword();

        if (username == null || username.isEmpty()) {
            member.setMessage("請輸入使用者名稱");
            member.setSuccessful(false);
            return member;
        }

        if (password == null || password.isEmpty()) {
            member.setMessage("請輸入密碼");
            member.setSuccessful(false);
            return member;
        }

        Member found = memberDao.findByUserName(username);
        if (found != null && password.equals(found.getPassword())) {
            found.setMessage("登入成功");
            found.setSuccessful(true);
            return found;
        }

        Member fail = new Member();
        fail.setMessage("使用者名稱或密碼錯誤");
        fail.setSuccessful(false);
        return fail;
	}


	@Override
	public Member getById(Integer memberId, Member loginMember) {
        if (loginMember == null || loginMember.getRoleLevel() == null || loginMember.getRoleLevel() < 3) {
            Member m = new Member();
            m.setMessage("權限不足，無法查詢");
            m.setSuccessful(false);
            return m;
        }
        Member found = memberDao.findById(memberId);
        if (found != null) {
            found.setSuccessful(true);
        }
        return found;
	}

	@Override
	public Member getByUsername(String username) {
        return memberDao.findByUserName(username);
	}

	@Override
	public List<Member> getAll() {
		return memberDao.listAll();
	}

	@Override
	public String getRoleById(Integer memberId) {
	     Member member = memberDao.findById(memberId);
	     return member != null ? String.valueOf(member.getRoleLevel()) : null;
	}

	@Override
	public boolean removeMemberById(Integer memberId) {
        return memberDao.delete(memberId);
	}

	@Override
	public boolean activateMemberByToken(String tokenStr) {
        System.out.println("開始驗證 token: " + tokenStr);

		VerificationToken token = verifyDao.findByToken(tokenStr);
        if (token == null || token.isUsed()) {
            return false;
        }

        Member member = token.getMember();
        member.setRoleLevel(1); // 設為啟用等級 1=一般會員
        token.setUsed(true);

        return verifyDao.update(token) && memberDao.update(member);
	
	}
}
