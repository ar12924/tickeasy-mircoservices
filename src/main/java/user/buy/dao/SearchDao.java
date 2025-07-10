package user.buy.dao;

import java.util.List;

import common.dao.CommonDao;
import common.vo.Order;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.KeywordCategory;

public interface SearchDao extends CommonDao {
	public List<EventInfo> selectEventInfo(String keyword, Integer page, Order order, Integer pageSize);
	
	public Long countEventInfo(String searchTerm);
	
	public List<Favorite> selectAllFavoriteByMemberId(Integer memberId);

	public Favorite selectFavoriteByMemberIdAndEventId(Integer memberId, Integer eventId);
	
	public Integer insertFavorite(Integer eventId, Integer memberId);
	
	public Integer removeFavorite(Integer eventId, Integer memberId);
	
	public Integer updateFavorite(Integer eventId, Integer memberId, boolean isFollowed);
	
	public KeywordCategory selectKeywordByKeywordId(Integer keywordId);
	
	public List<EventInfo> selectEventByKeywordWithPages(String keyword, Integer pageNumber, Integer pageSize);

	public Long selectEventCountByKeyword(String keyword);

}
