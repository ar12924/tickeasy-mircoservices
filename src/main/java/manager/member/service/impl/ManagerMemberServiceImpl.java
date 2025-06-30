package manager.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.member.dao.ManagerMemberDao;
import manager.member.service.ManagerMemberService;
import user.member.util.HashUtil;
import user.member.vo.Member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 會員業務邏輯實作類 創建者: archchang 創建日期: 2025-06-25
 */
@Service
@Transactional
public class ManagerMemberServiceImpl implements ManagerMemberService {

	@Autowired
	private ManagerMemberDao memberDao;

	@Override
	@Transactional(readOnly = true)
	public List<Member> listMembers() {
		List<Member> members = memberDao.findAll();
		members.forEach(member -> member.setPhoto(null));
        return members;
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getMemberPage(String userName, String startDate, String endDate, Integer roleLevel,
			Integer isActive, int page, int size) {
		// 參數驗證和預處理
		if (page < 1)
			page = 1;
		if (size < 1)
			size = 10;

		if (size > 100) {
			size = 100;
		}

		int offset = (page - 1) * size;

		// 調用 DAO 層進行數據查詢
		List<Member> members = memberDao.findMembersWithConditions(userName, startDate, endDate, roleLevel, isActive,
				offset, size);
		
		members.forEach(member -> member.setPhoto(null));

		long totalCount = memberDao.countMembersWithConditions(userName, startDate, endDate, roleLevel, isActive);

		// 構建返回結果
		return buildPageResult(members, totalCount, page, size);
	}

	@Override
	@Transactional(readOnly = true)
	public Member getMemberById(Integer memberId) {
		if (memberId == null) {
			throw new IllegalArgumentException("會員ID不能為空");
		}
		return memberDao.findById(memberId);
	}


	//構建分頁結果
	private Map<String, Object> buildPageResult(List<Member> members, long totalCount, int page, int size) {
		int totalPages = (int) Math.ceil((double) totalCount / size);

		boolean hasNext = page < totalPages;
        boolean hasPrevious = page > 1;
        
        Map<String, Object> result = new HashMap<>();
        result.put("members", members);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        result.put("pageSize", size);
        result.put("hasNext", hasNext);
        result.put("hasPrevious", hasPrevious);

		return result;
	}
	
	@Override
	@Transactional
    public Member createMember(Member member, String rePassword, Boolean hostApply, Boolean agree) {
        if (member == null) {
            throw new IllegalArgumentException("會員資料不能為空");
        }

        validateMember(member, false, rePassword, hostApply, agree);

        // 檢查重複
        if (memberDao.findByUserName(member.getUserName()) != null) {
            throw new IllegalArgumentException("此帳號已被註冊");
        }

        if (memberDao.findByEmail(member.getEmail()) != null) {
            throw new IllegalArgumentException("電子郵件已被其他會員使用");
        }
        
        if (memberDao.findByPhone(member.getPhone()) != null) {
            throw new IllegalArgumentException("此手機號碼已被註冊");
        }

        // 密碼加密
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            member.setPassword(HashUtil.hashpw(member.getPassword()));
        }
        
        if (Boolean.TRUE.equals(hostApply)) {
            member.setRoleLevel(2); 
        }

        return memberDao.save(member);
    }

    @Override
    public Member updateMember(Member member, String rePassword) {
        if (member == null || member.getMemberId() == null) {
            throw new IllegalArgumentException("會員資料或會員ID不能為空");
        }

        Member existingMember = memberDao.findById(member.getMemberId());
        if (existingMember == null) {
            throw new IllegalArgumentException("找不到指定的會員");
        }

        validateMember(member, true, rePassword, null, null);

        // 檢查重複（排除自己）
        Member userNameCheck = memberDao.findByUserName(member.getUserName());
        if (userNameCheck != null && !userNameCheck.getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("使用者名稱已被其他會員使用");
        }

        Member emailCheck = memberDao.findByEmail(member.getEmail());
        if (emailCheck != null && !emailCheck.getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("電子郵件已被其他會員使用");
        }
        
        Member phoneCheck = memberDao.findByPhone(member.getPhone());
        if (phoneCheck != null && !phoneCheck.getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("手機號碼已被其他會員使用");
        }

        // 密碼處理
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            member.setPassword(HashUtil.hashpw(member.getPassword()));
        } else {
            member.setPassword(existingMember.getPassword());
        }

        // 保持原有照片
        member.setPhoto(existingMember.getPhoto());
        
        member.setCreateTime(existingMember.getCreateTime());

        return memberDao.update(member);
    }

    @Override
    public void deleteMember(Integer memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("會員ID不能為空");
        }

        Member member = memberDao.findById(memberId);
        if (member == null) {
            throw new IllegalArgumentException("找不到指定的會員");
        }

        memberDao.deleteById(memberId);
    }

    /**
     * 驗證會員資料
     */
    private void validateMember(Member member, boolean isUpdate, String rePassword, Boolean hostApply, Boolean agree) {
    	
        String userName = member.getUserName();
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("使用者名稱不能為空");
        }
        if (userName.length() < 5 || userName.length() > 50) {
            throw new IllegalArgumentException("使用者名稱長度須介於 5 到 50 字元");
        }

        
        if (member.getNickName() == null || member.getNickName().trim().isEmpty()) {
            throw new IllegalArgumentException("會員暱稱不能為空");
        }

        
        String email = member.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("電子郵件不能為空");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,6}$")) {
            throw new IllegalArgumentException("電子郵件格式錯誤");
        }

        
        if (!isUpdate && (member.getPassword() == null || member.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("密碼不能為空");
        }
        if (member.getPassword() != null && !member.getPassword().isEmpty() && member.getPassword().length() < 6) {
            throw new IllegalArgumentException("密碼長度須至少 6 字元");
        }

        
        if (!isUpdate) {
            // 新增時：密碼和確認密碼都必須填寫
            if (rePassword == null || !rePassword.equals(member.getPassword())) {
                throw new IllegalArgumentException("兩次密碼輸入不一致");
            }
        } else {
            // 編輯時：只有當要修改密碼時才驗證確認密碼
            if (member.getPassword() != null && !member.getPassword().isEmpty()) {
                if (rePassword == null || !rePassword.equals(member.getPassword())) {
                    throw new IllegalArgumentException("兩次密碼輸入不一致");
                }
            }
            // 如果密碼為空，表示不修改密碼，不需要驗證確認密碼
        }

        
        String phone = member.getPhone();
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("手機號碼不能為空");
        }
        if (!phone.matches("^09\\d{8}$")) {
            throw new IllegalArgumentException("手機格式錯誤，需為台灣手機號碼 09 開頭共 10 碼");
        }

        
        String gender = member.getGender();
        if (gender == null || !(gender.equals("M") || gender.equals("F"))) {
            throw new IllegalArgumentException("性別請選擇男 (M) 或 女 (F)");
        }

        
        if (member.getBirthDate() == null) {
            throw new IllegalArgumentException("請選擇出生日期");
        }

        
        if (member.getRoleLevel() == null) {
            throw new IllegalArgumentException("會員等級不能為空");
        }

        
        String unicode = member.getUnicode();
        if (unicode != null && !unicode.isEmpty() && !unicode.matches("\\d{8}")) {
            throw new IllegalArgumentException("統一編號格式錯誤，應為 8 碼數字");
        }

        
        String idCard = member.getIdCard();
        if (idCard != null && !idCard.isEmpty() && !idCard.matches("[A-Za-z].*")) {
            throw new IllegalArgumentException("身分證開頭應為英文字母");
        }

        
        if (!isUpdate && (agree == null || !agree)) {
            throw new IllegalArgumentException("請先同意服務條款");
        }
    }
}