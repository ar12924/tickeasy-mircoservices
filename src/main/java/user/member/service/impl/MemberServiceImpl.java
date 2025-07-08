package user.member.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import user.member.dao.MemberDao;
import user.member.dao.VerificationDao;
import user.member.service.MailService;
import user.member.service.MemberService;
import user.member.util.HashUtil;
import user.member.vo.Member;
import user.member.vo.VerificationToken;
import static user.member.util.MemberConstants.*;

@Service
public class MemberServiceImpl implements MemberService {

	@Autowired
	private MemberDao memberDao;
	@Autowired
	private VerificationDao verifyDao;
	@Autowired
	private MailService mailService;

	// 共用：產生驗證/重設 token
	private VerificationToken createToken(Member member, String type, long expireMillis, String tokenName) {
		VerificationToken token = new VerificationToken();
		token.setTokenName(tokenName);
		token.setTokenType(type);
		token.setExpiredTime(new Timestamp(System.currentTimeMillis() + expireMillis));
		token.setMember(member);
		verifyDao.insert(token);
		return token;
	}

	// 共用：寄送信件
	private void sendMail(Member member, String type, String tokenName) {
		if ("EMAIL_VERIFY".equals(type)) {
			mailService.sendActivationNotification(member.getEmail(), member.getUserName(), tokenName);
		} else if ("RESET_PASSWORD".equals(type)) {
			mailService.sendPasswordResetNotification(member.getEmail(), member.getNickName(), tokenName);
		}
	}

	@Transactional
	@Override
	public Member register(Member member) {
		// 格式驗證
		if (member.getUserName() == null || member.getUserName().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("使用者名稱不可為空");
			return member;
		}
		if (member.getNickName() == null || member.getNickName().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("暱稱不可為空");
			return member;
		}
		if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("Email 不可為空");
			return member;
		}
		if (member.getPassword() == null || member.getPassword().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("密碼不可為空");
			return member;
		}
		if (member.getRePassword() == null || member.getRePassword().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("請再次輸入密碼");
			return member;
		}
		if (member.getBirthDate() == null) {
			member.setSuccessful(false);
			member.setMessage("出生日期不可為空");
			return member;
		}
		if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("手機號碼不可為空");
			return member;
		}
		if (member.getGender() == null || member.getGender().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("性別不可為空");
			return member;
		}
		if (member.getIdCard() == null || member.getIdCard().trim().isEmpty()) {
			member.setSuccessful(false);
			member.setMessage("身分證不可為空");
			return member;
		}
		if (member.getAgree() == null || !member.getAgree()) {
			member.setSuccessful(false);
			member.setMessage("請同意服務條款");
			return member;
		}

		String username = member.getUserName();
		if (username == null || username.length() < 5 || username.length() > 50) {
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
		// 新增：檢查 email 是否重複
		String email = member.getEmail();
		if (email != null && memberDao.findByEmail(email) != null) {
			member.setMessage("此 email 已被註冊");
			member.setSuccessful(false);
			return member;
		}

		String unicode = member.getUnicode();
		if (unicode != null && !unicode.trim().isEmpty() && !unicode.matches(UNICODE_PATTERN)) {
			member.setMessage("統一編號格式錯誤，應為 8 碼數字");
			member.setSuccessful(false);
			return member;
		}

		String idCard = member.getIdCard();
		if (idCard != null && !idCard.trim().isEmpty() && !idCard.matches(ID_PATTERN)) {
			member.setMessage("身分證開頭應為英文字母");
			member.setSuccessful(false);
			return member;
		}

		if (email != null && !email.trim().isEmpty() && !email.matches(EMAIL_PATTERN)) {
			member.setMessage("電子郵件格式錯誤");
			member.setSuccessful(false);
			return member;
		}

		if (member.getAgree() == null || !member.getAgree()) {
			member.setSuccessful(false);
			member.setMessage("請先同意服務條款");
			return member;
		}

		// 註冊時預設未啟用
		member.setIsActive(0);

		// 寫入DAO之前進行密碼雜湊、預設member為0
		member.setPassword(HashUtil.hashpw(password));
		member.setRoleLevel(0);

		boolean wantHost = Boolean.TRUE.equals(member.getHostApply());
		member.setRoleLevel(wantHost ? 2 : 0);

		// 1. 寫入 member，
		try {
			memberDao.insert(member);
			
			// 重新從資料庫獲取 member 以確保有正確的 ID
			Member savedMember = memberDao.findByUserName(member.getUserName());
			if (savedMember == null) {
				throw new RuntimeException("會員插入後無法找到");
			}
			
			VerificationToken token = createToken(savedMember, "EMAIL_VERIFY", TOKEN_EXPIRATION, UUID.randomUUID().toString());
			sendMail(savedMember, "EMAIL_VERIFY", token.getTokenName());
			savedMember.setSuccessful(true);
			savedMember.setMessage("註冊成功！請查收驗證信以開通會員");
			return savedMember;

		} catch (Exception e) {
			// DAO失敗rollback交易、mailService失敗也rollback資料
			System.err.println("註冊過程中發生錯誤: " + e.getMessage());
			
			member.setSuccessful(false);
			if (e.getMessage() != null && e.getMessage().contains("郵件發送失敗")) {
				member.setMessage("註冊成功，但驗證信寄送失敗，請稍後聯絡客服或重新發送驗證信");
			} else {
				member.setMessage("註冊失敗: " + e.getMessage());
			}
		}
		return member;
	}

	@Transactional
	@Override
	public Member editMember(Member member) {
		final Member existingMemberInDB = memberDao.findById(member.getMemberId());
		if (existingMemberInDB == null) {
			member.setSuccessful(false);
			member.setMessage("查無此會員");
			return member;
		}

		String newPassword = member.getPassword();
		if (newPassword != null && !newPassword.isEmpty()) {
			if (newPassword.length() < 6) {
				member.setSuccessful(false);
				member.setMessage("密碼長度須至少 6 字元");
				return member;
			}
			existingMemberInDB.setPassword(HashUtil.hashpw(newPassword));
		} else {
			existingMemberInDB.setPassword(existingMemberInDB.getPassword());
		}

		String unicode = member.getUnicode();
		if (unicode != null && !unicode.trim().isEmpty() && !unicode.matches(UNICODE_PATTERN)) {
			member.setMessage("統一編號格式錯誤，應為 8 碼數字");
			member.setSuccessful(false);
			return member;
		}

		String email = member.getEmail();
		if (email != null && !email.trim().isEmpty() && !email.matches(EMAIL_PATTERN)) {
			member.setMessage("電子郵件格式錯誤");
			member.setSuccessful(false);
			return member;
		}

		// 合併欄位（有值才 set，否則保留原值）
		if (member.getNickName() != null) existingMemberInDB.setNickName(member.getNickName());
		if (member.getEmail() != null) existingMemberInDB.setEmail(member.getEmail());
		if (member.getPhone() != null) existingMemberInDB.setPhone(member.getPhone());
		if (member.getBirthDate() != null) existingMemberInDB.setBirthDate(member.getBirthDate());
		if (member.getGender() != null) existingMemberInDB.setGender(member.getGender());
		if (member.getUnicode() != null) existingMemberInDB.setUnicode(member.getUnicode());
		// 只有有新照片才 set，否則保留原本照片
		if (member.getPhoto() != null) {
			existingMemberInDB.setPhoto(member.getPhoto());
		}

		try {
			boolean updated = memberDao.update(existingMemberInDB);
			if (updated) {
				existingMemberInDB.setSuccessful(true);
				existingMemberInDB.setMessage("更新成功");
			} else {
				existingMemberInDB.setSuccessful(false);
				existingMemberInDB.setMessage("更新失敗");
			}
		} catch (Exception e) {
			existingMemberInDB.setSuccessful(false);
			existingMemberInDB.setMessage("更新失敗：" + e.getMessage());
		}
		return existingMemberInDB;
	}

	@Transactional
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
				// 檢查帳號是否已驗證
				if (found.getRoleLevel() == null || found.getRoleLevel() == 0) {
					found.setSuccessful(false);
					found.setMessage("帳號尚未驗證，請先驗證您的電子郵件");
					return found;
				}
				String newHash = HashUtil.hashpw(password);
				found.setPassword(newHash);
				memberDao.update(found);
				found.setSuccessful(true);
				found.setMessage("登入成功");
				return found;
			}
			if (HashUtil.verify(password, stored)) {
				// 檢查帳號是否已驗證
				if (found.getRoleLevel() == null || found.getRoleLevel() == 0) {
					found.setSuccessful(false);
					found.setMessage("帳號尚未驗證，請先驗證您的電子郵件");
					return found;
				}
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

	@Transactional
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

	@Transactional
	@Override
	public Member getByUsername(String username) {
		return memberDao.findByUserName(username);
	}

	@Transactional
	@Override
	public Member getByEmail(String email) {
		return memberDao.findByEmail(email);
	}

	@Transactional
	@Override
	public String getRoleById(Integer memberId) {
		Member m = memberDao.findById(memberId);
		return m != null ? String.valueOf(m.getRoleLevel()) : null;
	}

	@Transactional
	@Override
	public boolean removeMemberById(Integer memberId) {
		return memberDao.delete(memberId);
	}

	@Transactional
	@Override
	public boolean activateMemberByToken(String tokenName) {
		// 1. 會員點連結過來字串
		VerificationToken token = verifyDao.findByToken(tokenName);
		// 2. 檢查：驗證是否存在、未過期、且類型為EMAIL_VERIFY
		if (token == null || token.getExpiredTime().before(new Timestamp(System.currentTimeMillis()))
				|| !"EMAIL_VERIFY".equals(token.getTokenType())) {
			return false;
		}
		Member m = token.getMember();
		m.setRoleLevel(1);
		m.setIsActive(1);
		memberDao.update(m);
		verifyDao.deleteById(token.getTokenId());
		return true;
	}

	@Transactional
	@Override
	public Member requestPasswordResetByEmail(String email) {
		Member member = getByEmail(email);
		if (member == null) {
			member = new Member();
			member.setSuccessful(false);
			member.setMessage("找不到此 email 對應的會員帳號");
			return member;
		}
		try {
			VerificationToken token = createToken(member, "RESET_PASSWORD", 3600 * 1000, UUID.randomUUID().toString());
			sendMail(member, "RESET_PASSWORD", token.getTokenName());
			member.setSuccessful(true);
			member.setMessage("密碼重設郵件已發送，請檢查您的信箱");
		} catch (Exception e) {
			member.setSuccessful(false);
			member.setMessage("密碼重設郵件發送失敗：" + e.getMessage());
		}
		return member;
	}

	@Transactional
	@Override
	public Member resetPasswordByToken(String token, String newPassword) {
		Member member = new Member();
		try {
			VerificationToken resetToken = verifyDao.findByToken(token);
			if (resetToken == null ||
				resetToken.getExpiredTime().before(new Timestamp(System.currentTimeMillis())) ||
				!"RESET_PASSWORD".equals(resetToken.getTokenType())) {
				member.setSuccessful(false);
				member.setMessage("無效或已過期的重設連結");
				return member;
			}
			member = resetToken.getMember();
			member.setPassword(newPassword);
			Member updated = editMember(member);
			if (updated.isSuccessful()) {
				verifyDao.deleteById(resetToken.getTokenId());
				member.setSuccessful(true);
				member.setMessage("密碼重設成功");
			} else {
				member.setSuccessful(false);
				member.setMessage("密碼重設失敗");
			}
			return member;
		} catch (Exception e) {
			member.setSuccessful(false);
			member.setMessage("密碼重設失敗：" + e.getMessage());
			return member;
		}
	}

	@Transactional
	@Override
	public Member resendVerificationMail(String email) {
		Member member = getByEmail(email);
		if (member == null) {
			member = new Member();
			member.setSuccessful(false);
			member.setMessage("找不到此 email 對應的會員帳號");
			return member;
		}
		if (member.getRoleLevel() > 0) {
			member.setSuccessful(false);
			member.setMessage("此帳號已經驗證過了");
			return member;
		}
		try {
			VerificationToken token = createToken(member, "EMAIL_VERIFY", 24 * 60 * 60 * 1000, UUID.randomUUID().toString());
			sendMail(member, "EMAIL_VERIFY", token.getTokenName());
			member.setSuccessful(true);
			member.setMessage("驗證信已重新發送，請檢查您的信箱");
		} catch (Exception e) {
			member.setSuccessful(false);
			member.setMessage("重新發送驗證信失敗：" + e.getMessage());
		}
		return member;
	}

	@Transactional
	@Override
	public Member sendVerificationMail(Member member) {
		try {
			VerificationToken token = createToken(member, "EMAIL_VERIFY", 24 * 60 * 60 * 1000, UUID.randomUUID().toString());
			sendMail(member, "EMAIL_VERIFY", token.getTokenName());
			member.setSuccessful(true);
			member.setMessage("驗證信已發送，請至信箱收信");
		} catch (Exception e) {
			member.setSuccessful(false);
			member.setMessage("發送失敗：" + e.getMessage());
		}
		return member;
	}

	@Transactional
	@Override
	public Member sendPasswordUpdateMail(Member member, String newPassword) {
		try {
			// 產生 uuid
			String uuid = UUID.randomUUID().toString();
			// 密碼加密
			String encryptedPassword = HashUtil.hashpw(newPassword);
			// tokenName = uuid|加密密碼
			String tokenName = uuid + "|" + encryptedPassword;
			// 創建密碼更新認證 token
			VerificationToken token = createToken(member, "PASSWORD_UPDATE", 3600 * 1000, tokenName);
			// 發送密碼更新認證信（信件只帶 uuid）
			mailService.sendPasswordUpdateNotification(member.getEmail(), member.getNickName(), uuid);
			member.setSuccessful(true);
			member.setMessage("密碼更新認證信已發送");
		} catch (Exception e) {
			member.setSuccessful(false);
			member.setMessage("密碼更新認證信發送失敗：" + e.getMessage());
		}
		return member;
	}

	@Transactional
	@Override
	public Member updatePasswordAndDeleteToken(Member member, String newPassword, Integer tokenId) {
		member.setPassword(newPassword);
		Member result = editMember(member);
		if (result.isSuccessful()) {
			verifyDao.deleteById(tokenId);
		}
		return result;
	}

	// 提供給控制器使用的方法
	public VerificationDao getVerificationDao() {
		return verifyDao;
	}

}
