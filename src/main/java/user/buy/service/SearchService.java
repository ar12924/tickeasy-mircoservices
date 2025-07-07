package user.buy.service;

import java.util.List;

import common.vo.Core;
import common.vo.Order;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.KeywordCategory;
import user.member.vo.Member;

public interface SearchService {
	public Core<List<EventInfo>> getEventInfo(String keyword, Integer page, Order order, Integer pageSize);

	public Core<List<Favorite>> getAllFavorite(Member member);

	public Core<Integer> saveFavorite(Member member, Integer eventId);
	
	public Core<Integer> deleteFavorite(Member member, Integer eventId);

	public KeywordCategory getKeyword(Integer keywordId);

	public Core<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize);
}
