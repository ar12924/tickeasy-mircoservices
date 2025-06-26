package manager.member.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import manager.member.dao.ManagerMemberDao;
import manager.member.service.ManagerMemberService;
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
		return memberDao.findAll();
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

}