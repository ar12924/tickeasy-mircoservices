package user.buy.dao;

import java.math.BigDecimal;
import java.util.List;

import user.buy.vo.BookTypeDto;
import user.buy.vo.EventInfo;
import user.member.vo.Member;

public interface BookDao {

	public List<BookTypeDto> selectAllTypeById(Integer eventId);

	public BookTypeDto selectTypeById(Integer eventId, Integer typeId);

	public EventInfo selectEventById(Integer eventId);

	public Member selectMemberByUserName(String userName);

	public Integer insertBuyerOrderAndGetId(Integer eventId, Integer memberId, Integer isPaid, BigDecimal totalAmount);

	public Integer insertBuyerTicketAndGetId(Integer newOrderId, Member member, String eventName, BookTypeDto bookTypeDto);

	public Long countBuyerTicketByEventNameAndTypeId(String eventName, Integer typeId);
}
