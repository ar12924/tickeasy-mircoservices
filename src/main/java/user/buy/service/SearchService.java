package user.buy.service;

import java.util.List;

import common.vo.Core;
import user.buy.vo.EventInfo;
import user.buy.vo.Favorite;
import user.buy.vo.KeywordCategory;
import user.member.vo.Member;

public interface SearchService {
	public List<EventInfo> getRecentEventInfo(Integer n);

	public Core<List<Favorite>> getFavorite(Member member);
	
	public KeywordCategory getKeyword(Integer keywordId);

	public Core<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize);
}
