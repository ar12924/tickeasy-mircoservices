package user.buy.dao;

import java.util.List;

import common.dao.CommonDao;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.KeywordCategory;

public interface SearchDao extends CommonDao {
	List<EventInfo> selectRecentEventInfo(Integer n);
	
	List<Favorite> selectFavoriteByMemberId(Integer memberId);

	KeywordCategory selectKeywordByKeywordId(Integer keywordId);
	
	List<EventInfo> selectEventByKeywordWithPages(String keyword, Integer pageNumber, Integer pageSize);

	public Long selectEventCountByKeyword(String keyword);

}
