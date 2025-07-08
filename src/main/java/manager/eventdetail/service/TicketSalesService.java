package manager.eventdetail.service;

import java.util.Map;

public interface TicketSalesService {

    Map<String, Object> getTicketSalesStatus(Integer eventId);

}