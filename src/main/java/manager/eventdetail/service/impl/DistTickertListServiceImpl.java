package manager.eventdetail.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import manager.eventdetail.dao.DistTicketListDao;
import manager.eventdetail.service.DistTicketListService;
import manager.eventdetail.vo.DistTicket;


@Service
public class DistTickertListServiceImpl implements DistTicketListService{

	@Autowired
	private DistTicketListDao distTicketListDao;

	/*
	 * public DistTickertListServiceImpl() throws NamingException {
	 * distTicketListDao = new DistTicketListDaoImpl(); }
	 */
	@Transactional
	@Override
	public List<DistTicket> distTicketListService(Timestamp startTime,Timestamp endTime,Integer selectedEventId) {
		System.out.println("查到資料筆數：" + distTicketListDao.selectAllDistTicketList(startTime,endTime,selectedEventId).size());
		return distTicketListDao.selectAllDistTicketList(startTime,endTime,selectedEventId);
	}

}
