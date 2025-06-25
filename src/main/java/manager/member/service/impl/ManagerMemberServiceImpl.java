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
 * 會員業務邏輯實作類
 * 創建者: archchang
 * 創建日期: 2025-06-25
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
    public Map<String, Object> getMemberPage(String userName, String startDate, String endDate,
                                           Integer roleLevel, Integer isActive, int page, int size) {
        // 業務邏輯：參數驗證和預處理
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        int offset = (page - 1) * size;

        // 業務邏輯：構建查詢條件
        QueryCondition condition = buildQueryCondition(userName, startDate, endDate, roleLevel, isActive);

        // 調用 DAO 層進行數據查詢
        List<Member> members = memberDao.findByDynamicQuery(condition.getHql(), condition.getParameters(), offset, size);
        long totalCount = memberDao.countByDynamicQuery(condition.getCountHql(), condition.getParameters());

        // 業務邏輯：構建返回結果
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

    // 業務邏輯：構建查詢條件
    private QueryCondition buildQueryCondition(String userName, String startDate, String endDate, 
                                              Integer roleLevel, Integer isActive) {
        StringBuilder hql = new StringBuilder("FROM Member WHERE 1=1");
        StringBuilder countHql = new StringBuilder("SELECT COUNT(*) FROM Member WHERE 1=1");
        Map<String, Object> parameters = new HashMap<>();

        // 業務規則：用戶名稱搜索邏輯
        if (userName != null && !userName.trim().isEmpty()) {
            String condition = " AND (userName LIKE :userName OR nickName LIKE :userName)";
            hql.append(condition);
            countHql.append(condition);
            parameters.put("userName", "%" + userName.trim() + "%");
        }

        // 業務規則：日期範圍搜索邏輯
        if (startDate != null && !startDate.trim().isEmpty()) {
            String condition = " AND DATE(createTime) >= :startDate";
            hql.append(condition);
            countHql.append(condition);
            parameters.put("startDate", startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            String condition = " AND DATE(createTime) <= :endDate";
            hql.append(condition);
            countHql.append(condition);
            parameters.put("endDate", endDate);
        }

        // 業務規則：角色等級搜索邏輯
        if (roleLevel != null) {
            String condition = " AND roleLevel = :roleLevel";
            hql.append(condition);
            countHql.append(condition);
            parameters.put("roleLevel", roleLevel);
        }

        // 業務規則：啟用狀態搜索邏輯
        if (isActive != null) {
            String condition = " AND isActive = :isActive";
            hql.append(condition);
            countHql.append(condition);
            parameters.put("isActive", isActive);
        }

        // 業務規則：排序邏輯
        hql.append(" ORDER BY createTime DESC");

        return new QueryCondition(hql.toString(), countHql.toString(), parameters);
    }

    // 業務邏輯：構建分頁結果
    private Map<String, Object> buildPageResult(List<Member> members, long totalCount, int page, int size) {
        int totalPages = (int) Math.ceil((double) totalCount / size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("members", members);
        result.put("totalCount", totalCount);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        result.put("pageSize", size);
        result.put("hasNext", page < totalPages);
        result.put("hasPrevious", page > 1);
        
        return result;
    }

    // 內部類：查詢條件封裝
    private static class QueryCondition {
        private final String hql;
        private final String countHql;
        private final Map<String, Object> parameters;

        public QueryCondition(String hql, String countHql, Map<String, Object> parameters) {
            this.hql = hql;
            this.countHql = countHql;
            this.parameters = parameters;
        }

        public String getHql() { return hql; }
        public String getCountHql() { return countHql; }
        public Map<String, Object> getParameters() { return parameters; }
    }
}