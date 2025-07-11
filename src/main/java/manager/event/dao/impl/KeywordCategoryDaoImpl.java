package manager.event.dao.impl;

import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import manager.event.dao.KeywordCategoryDao;
import manager.event.vo.MngKeywordCategory;

@Repository
public class KeywordCategoryDaoImpl implements KeywordCategoryDao {
    
    @PersistenceContext
    private Session session;
    
    @Override
    public Integer createKeywordCategory(MngKeywordCategory kCategory) {
        try {
            System.out.println("=== DAO 層建立關鍵字分類 ===");
            System.out.println("準備儲存: " + kCategory);
            
            session.persist(kCategory);
            session.flush(); // 確保立即執行
            
            Integer keywordId = kCategory.getKeywordId();
            System.out.println("關鍵字分類建立成功，生成ID: " + keywordId);
            
            return keywordId;
        } catch (Exception e) {
            System.err.println("DAO 層建立關鍵字分類失敗: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}