package user.buy.service;

import java.util.List;

import common.vo.Core;
import user.buy.vo.EventInfo;
import user.buy.vo.KeywordCategory;

public interface SearchService {
	public List<EventInfo> getRecentEventInfo(Integer n);

	public KeywordCategory getKeyword(Integer keywordId);

	public Core<List<EventInfo>> searchEventByKeyword(String keyword, Integer pageNumber, Integer pageSize);
}
