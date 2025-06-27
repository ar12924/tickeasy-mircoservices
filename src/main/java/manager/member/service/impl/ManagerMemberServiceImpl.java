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
    public Member createMember(Member member) {
        if (member == null) {
            throw new IllegalArgumentException("會員資料不能為空");
        }

        validateMember(member, false);

        // 檢查重複
        if (memberDao.findByUserName(member.getUserName()) != null) {
            throw new IllegalArgumentException("使用者名稱已存在");
        }

        if (memberDao.findByEmail(member.getEmail()) != null) {
            throw new IllegalArgumentException("電子郵件已存在");
        }

        // 密碼加密
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            member.setPassword(HashUtil.hashpw(member.getPassword()));
        }

        return memberDao.save(member);
    }

    @Override
    public Member updateMember(Member member) {
        if (member == null || member.getMemberId() == null) {
            throw new IllegalArgumentException("會員資料或會員ID不能為空");
        }

        Member existingMember = memberDao.findById(member.getMemberId());
        if (existingMember == null) {
            throw new IllegalArgumentException("找不到指定的會員");
        }

        validateMember(member, true);

        // 檢查重複（排除自己）
        Member userNameCheck = memberDao.findByUserName(member.getUserName());
        if (userNameCheck != null && !userNameCheck.getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("使用者名稱已被其他會員使用");
        }

        Member emailCheck = memberDao.findByEmail(member.getEmail());
        if (emailCheck != null && !emailCheck.getMemberId().equals(member.getMemberId())) {
            throw new IllegalArgumentException("電子郵件已被其他會員使用");
        }

        // 密碼處理
        if (member.getPassword() != null && !member.getPassword().isEmpty()) {
            member.setPassword(HashUtil.hashpw(member.getPassword()));
        } else {
            member.setPassword(existingMember.getPassword());
        }

        // 保持原有照片
        member.setPhoto(existingMember.getPhoto());

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
    private void validateMember(Member member, boolean isUpdate) {
        if (member.getUserName() == null || member.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("使用者名稱不能為空");
        }

        if (member.getNickName() == null || member.getNickName().trim().isEmpty()) {
            throw new IllegalArgumentException("會員暱稱不能為空");
        }

        if (member.getEmail() == null || member.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("電子郵件不能為空");
        }

        if (!isUpdate && (member.getPassword() == null || member.getPassword().trim().isEmpty())) {
            throw new IllegalArgumentException("密碼不能為空");
        }

        if (member.getRoleLevel() == null) {
            throw new IllegalArgumentException("會員等級不能為空");
        }
    }
}