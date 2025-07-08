package manager.event.dao;

import common.dao.CommonDao;
import manager.event.vo.MngKeywordCategory;

public interface KeywordCategoryDao extends CommonDao {

//	建立Keyword
	public Integer createKeywordCategory(MngKeywordCategory category);
}
