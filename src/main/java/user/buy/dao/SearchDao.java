package user.buy.dao;

import java.util.List;

import common.dao.CommonDao;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.KeywordCategory;

public interface SearchDao extends CommonDao {
	public List<EventInfo> selectRecentEventInfo(Integer n);
	
	public List<Favorite> selectAllFavoriteByMemberId(Integer memberId);

	public Integer insertFavorite(Integer eventId, Integer memberId);
	
	public Integer removeFavorite(Integer eventId, Integer memberId);
	
	public KeywordCategory selectKeywordByKeywordId(Integer keywordId);
	
	public List<EventInfo> selectEventByKeywordWithPages(String keyword, Integer pageNumber, Integer pageSize);

	public Long selectEventCountByKeyword(String keyword);

}
