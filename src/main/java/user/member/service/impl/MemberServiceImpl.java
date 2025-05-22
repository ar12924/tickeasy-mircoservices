package user.member.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import user.member.dao.MemberDao;
import user.member.dao.VerificationDao;
import user.member.dao.impl.MemberDaoImpl;
import user.member.dao.impl.VerificationDaoImpl;
import user.member.service.MailService;
import user.member.service.MemberService;
import user.member.util.HashUtil;
import user.member.vo.Member;
import user.member.vo.VerificationToken;
import static user.member.util.MemberConstants.*;

public class MemberServiceImpl implements MemberService {
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
		if (username == null || username.length()< 5 || username.length() > 50) {
			member.setMessage("使用者名稱長度須介於 5 到 50 字元");
			member.setSuccessful(false);
			return member;
		}

		String password = member.getPassword();
		if (password == null || password.length() < 6) {
			member.setMessage("密碼長度須至少 6 字元");
			member.setSuccessful(false);
			return member;
		}

		if (member.getRePassword() == null || !member.getRePassword().equals(member.getPassword())) {
			member.setSuccessful(false);
			member.setMessage("兩次密碼輸入不一致");
			return member;
		}

		String phone = member.getPhone();
		if (phone == null || !phone.matches(PHOHE_PATTERN)) {
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
		if (memberDao.findByPhone(phone) != null) {
			member.setMessage("此手機號碼已被註冊");
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
		if (unicode != null && !unicode.matches(UNICODE_PATTERN)) {
			member.setMessage("統一編號格式錯誤，應為 8 碼數字");
			member.setSuccessful(false);
			return member;
		}

		String idCard = member.getIdCard();
		if (idCard != null && !idCard.matches(ID_PATTERN)) {
			member.setMessage("身分證開頭應為英文字母");
			member.setSuccessful(false);
			return member;
		}

		String email = member.getEmail();
		if (email != null && !email.matches(EMAIL_PATTERN)) {
			member.setMessage("電子郵件格式錯誤");
			member.setSuccessful(false);
			return member;
		}
		
		if (member.getAgree() == null || !member.getAgree()) {
			member.setSuccessful(false);
			member.setMessage("請先同意服務條款");
			return member;
		}
		
		// 寫入DAO之前進行密碼雜湊、預設member為0		
		member.setPassword(HashUtil.hashpw(password));
		member.setRoleLevel(0);

		boolean wantHost = Boolean.TRUE.equals(member.getHostApply());
		member.setRoleLevel(wantHost ? 2 : 0);

		// 1. 寫入 member，
		try {
			memberDao.insert(member);
		// 2. 寫入 token
			String tokenName = UUID.randomUUID().toString();
			VerificationToken token = new VerificationToken();
			token.setTokenName(tokenName);
			token.setTokenType("EMAIL_VERIFY");
			token.setExpiredTime(new Timestamp(System.currentTimeMillis() + TOKEN_EXPIRATION));
			token.setMember(member); // 關聯 Member
			verifyDao.insert(token); // token永久化(是否為必要??)
			
		// 3. 寄認證信，如果產生例外，觸發rollback
			mailService.sendActivationNotification(member.getEmail(), member.getUserName(), tokenName);

			member.setSuccessful(true);
			member.setMessage("註冊成功！請查收驗證信以開通會員");

		} catch (Exception e) {
			// DAO失敗rollback交易、mailService失敗也rollback資料	
			e.printStackTrace();
			member.setSuccessful(false);
			member.setMessage("註冊成功，但驗證信寄送失敗，請稍後聯絡客服");
		}
		return member;
	}

	@Override
	public Member editMember(Member member) {
		final Member existingMemberInDB = memberDao.findById(member.getMemberId());
		if (existingMemberInDB == null) {
			member.setSuccessful(false); // 可以直接修改傳入的 member 來返回狀態
			member.setMessage("查無此會員");
			return member;
		}
		
		String newPassword = member.getPassword();
		if (newPassword != null && !newPassword.isEmpty()) {
			if (newPassword.length() < 6) { // 假設密碼最小長度為6
				member.setSuccessful(false); // 修改傳入的 member 以返回錯誤訊息
				member.setMessage("密碼長度須至少 6 字元");
				return member;
			}
			member.setPassword(HashUtil.hashpw(newPassword)); // 有新密碼依然要雜湊
		} else {
			member.setPassword(existingMemberInDB.getPassword()); // 如未輸入要修改密碼，則維持原密碼
		}
		
		String unicode = member.getUnicode();
		if (unicode != null && !unicode.matches(UNICODE_PATTERN)) {
			member.setMessage("統一編號格式錯誤，應為 8 碼數字");
			member.setSuccessful(false);
			return member;
		}

		String email = member.getEmail();
		if (email != null && !email.matches(EMAIL_PATTERN)) {
			member.setMessage("電子郵件格式錯誤");
			member.setSuccessful(false);
			return member;
		}
		
		try {
			boolean updated = memberDao.update(member);
			if (updated) {
				member.setSuccessful(true);
				member.setMessage("更新成功");
			}else {
				member.setSuccessful(false);
				member.setMessage("更新失敗");
			}
		} catch (Exception e) {
			member.setSuccessful(false);
			member.setMessage("更新失敗：" + e.getMessage());
		}
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
		if (found != null) {
			String stored = found.getPassword();
			if (password.equals(stored)) {
				String newHash = HashUtil.hashpw(password);
	            found.setPassword(newHash);
	            memberDao.update(found);
	            found.setSuccessful(true);
	            found.setMessage("登入成功");
	            return found;
			}
			if (HashUtil.verify(password,stored)) {
	            found.setSuccessful(true);
	            found.setMessage("登入成功");
	            return found;
			}
				
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
		Member m = memberDao.findById(memberId);
		return m != null ? String.valueOf(m.getRoleLevel()) : null;
	}

	@Override
	public boolean removeMemberById(Integer memberId) {
		return memberDao.delete(memberId);
	}

	@Override
	public boolean activateMemberByToken(String tokenName) {
		System.out.println("開始驗證 token: " + tokenName);

		// 1. 會員點連結過來字串
		VerificationToken token = verifyDao.findByToken(tokenName);
		// 2. 檢查：驗證是否存在、未過期、且類型為EMAIL_VERIFY
		if (token == null || token.getExpiredTime().before(new Timestamp(System.currentTimeMillis()))
				|| !"EMAIL_VERIFY".equals(token.getTokenType())) {
			return false;
		}
		Member m = token.getMember();
		m.setRoleLevel(1);
		memberDao.update(m);
		verifyDao.deleteById(token.getTokenId());
		return true;
	}
}
