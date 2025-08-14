package microservices.notify.dao.impl;

import microservices.notify.dao.NotificationDao;
import microservices.notify.vo.Notification;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class NotificationDaoImpl implements NotificationDao {

    @PersistenceContext
    private Session session;

    @Override
    public List<Notification> selectAllByMemberId(int memberId) {
        return session.createQuery("FROM Notification WHERE memberId=:memberId AND isVisible=1", Notification.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public Integer updateIsRead(int memberId, int memberNotificationId) {
        int result = session.createQuery("UPDATE Notification SET isRead=:isRead,readTime=:readTime,updateTime=:updateTime WHERE memberId=:memberId AND memberNotificationId=:mid")
                .setParameter("isRead", 1)
                .setParameter("readTime", new Timestamp(System.currentTimeMillis()))
                .setParameter("updateTime", new Timestamp(System.currentTimeMillis()))
                .setParameter("memberId", memberId)
                .setParameter("mid", memberNotificationId)
                .executeUpdate();
        return result > 0 ? result : null;
    }

    @Override
    public Integer updateUnvisible(int memberNotificationId) {
        int result = session.createQuery("UPDATE Notification SET isVisible=:vis,updateTime=:updateTime WHERE memberNotificationId=:mid")
                .setParameter("vis", 0)
                .setParameter("updateTime", new Timestamp(System.currentTimeMillis()))
                .setParameter("mid", memberNotificationId)
                .executeUpdate();
        return result > 0 ? result : null;
    }

    @Override
    public Integer updateListClear(int memberId) {
        int result = session.createQuery("UPDATE Notification SET isVisible=:vis,updateTime=:updateTime WHERE memberId=:memberId")
                .setParameter("vis", 0)
                .setParameter("updateTime", new Timestamp(System.currentTimeMillis()))
                .setParameter("memberId", memberId)
                .executeUpdate();
        return result > 0 ? result : null;
    }

    @Override
    public List<Object[]> sendReminderNotificationForTomorrowList() {
        String sql = "SELECT DISTINCT bt.current_holder_member_id, bo.event_id, ei.event_name, ei.event_from_date FROM buyer_order bo JOIN buyer_ticket bt ON bo.order_id = bt.order_id JOIN event_info ei ON bo.event_id = ei.event_id WHERE DATEDIFF(ei.event_from_date, CURDATE()) = 1 AND ei.is_posted=1";
        return session.createNativeQuery(sql).getResultList();
    }

    @Override
    public int sendReminderNotification(int memberId, int eventId, String eventName, Timestamp eventDate) {
        String sql = "INSERT INTO member_notification (notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";
        String title_template = titleTemplateNotification(5).replace("{event_name}", eventName);
        String message_template = messageTemplateNotification(5).replace("{event_name}", eventName);
        String link_template = linkTemplateNotification(5).replace("{event_id}", String.valueOf(eventId));
        return session.createNativeQuery(sql)
                .setParameter("notificationId", 5)
                .setParameter("memberId", memberId)
                .setParameter("isRead", 0)
                .setParameter("isVisible", 1)
                .setParameter("status", 1)
                .setParameter("title", title_template)
                .setParameter("message", message_template)
                .setParameter("linkUrl", link_template)
                .executeUpdate();
    }

    @Override
    public List<Object[]> sendFavoriteSellReminderNotificationForTomorrowList() {
        String sql = "SELECT f.member_id,f.event_id ,eiett.event_name,eiett.sell_from_time ,eiett.sell_to_time ,eiett.category_name FROM favorite f JOIN (SELECT ei.event_id,ei.event_name,ett.sell_from_time ,ett.sell_to_time ,ett.category_name FROM event_info ei JOIN event_ticket_type ett ON ett.event_id=ei.event_id ) AS eiett ON f.event_id=eiett.event_id WHERE DATEDIFF(eiett.sell_from_time, CURDATE()) = 1 AND f.is_followed = 1";
        return session.createNativeQuery(sql).getResultList();
    }

    @Override
    public int sendFavoriteSellReminderNotification(int memberId, int eventId, String eventName, Timestamp eventSellFromTime, Timestamp eventSellToTime, String categoryName) {
        String sql = "INSERT INTO member_notification (notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";
        String title_template = titleTemplateNotification(2).replace("{event_name}", eventName);
        String message_template = messageTemplateNotification(2)
                .replace("{event_name}", eventName)
                .replace("{category_name}", categoryName)
                .replace("{event_sell_from_time}", String.valueOf(eventSellFromTime))
                .replace("{event_sell_to_time}", String.valueOf(eventSellToTime));
        String link_template = linkTemplateNotification(2).replace("{event_id}", String.valueOf(eventId));
        return session.createNativeQuery(sql)
                .setParameter("notificationId", 2)
                .setParameter("memberId", memberId)
                .setParameter("isRead", 0)
                .setParameter("isVisible", 1)
                .setParameter("status", 1)
                .setParameter("title", title_template)
                .setParameter("message", message_template)
                .setParameter("linkUrl", link_template)
                .executeUpdate();
    }

    @Override
    public List<Object[]> sendFavoriteSoldOutReminderList() {
        String sql = "SELECT DISTINCT fi.member_id,mb.user_name, fi.event_id, ei.event_name,ett.sell_to_time FROM favorite fi JOIN event_info ei ON fi.event_id = ei.event_id JOIN MEMBER mb ON fi.member_id =mb.member_id JOIN event_ticket_type ett ON ett.event_id=fi.event_id WHERE fi.is_followed=1 AND DATEDIFF(ett.sell_to_time, CURDATE()) = 1";
        return session.createNativeQuery(sql).getResultList();
    }

    @Override
    public int sendFavoriteSoldOutReminderNotification(int memberId, String userName, int eventId, String eventName, Timestamp eventToDate) {
        String sql = "INSERT INTO member_notification (notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";
        String title_template = titleTemplateNotification(4).replace("{event_name}", eventName);
        String message_template = messageTemplateNotification(4).replace("{event_name}", eventName);
        String link_template = linkTemplateNotification(4).replace("{event_id}", String.valueOf(eventId));
        return session.createNativeQuery(sql)
                .setParameter("notificationId", 4)
                .setParameter("memberId", memberId)
                .setParameter("isRead", 0)
                .setParameter("isVisible", 1)
                .setParameter("status", 1)
                .setParameter("title", title_template)
                .setParameter("message", message_template)
                .setParameter("linkUrl", link_template)
                .executeUpdate();
    }

    @Override
    public List<Object[]> sendFavoriteLeftPercentReminderList() {
        String sql = "SELECT e1.event_id,e1.cou,e2.cap FROM(SELECT cc.event_id,COUNT(cc.ticket_id) AS cou FROM( SELECT bt.ticket_id,bt.type_id,ett.event_id FROM buyer_ticket bt JOIN event_ticket_type ett ON bt.type_id=ett.type_id )AS cc GROUP BY cc.event_id)AS e1 JOIN(SELECT ett.event_id,SUM(ett.capacity) AS cap FROM event_ticket_type ett GROUP BY event_id)AS e2 ON e1.event_id=e2.event_id";
        return session.createNativeQuery(sql).getResultList();
    }

    @Override
    public int sendFavoriteLeftPercentReminderNotification(int memberId, String userName, int eventId, String eventName, int percent) {
        String sql = "INSERT INTO member_notification (notification_id, member_id, is_read, is_visible, notification_status, title, message, link_url, send_time, create_time, update_time) VALUES (:notificationId, :memberId, :isRead, :isVisible, :status, :title, :message, :linkUrl, NOW(), NOW(), NOW())";
        String title_template = titleTemplateNotification(3).replace("{event_name}", eventName).replace("{percent}", String.valueOf(percent));
        String message_template = messageTemplateNotification(3).replace("{event_name}", eventName).replace("{percent}", String.valueOf(percent));
        String link_template = linkTemplateNotification(4).replace("{event_id}", String.valueOf(eventId));
        return session.createNativeQuery(sql)
                .setParameter("notificationId", 3)
                .setParameter("memberId", memberId)
                .setParameter("isRead", 0)
                .setParameter("isVisible", 1)
                .setParameter("status", 1)
                .setParameter("title", title_template)
                .setParameter("message", message_template)
                .setParameter("linkUrl", link_template)
                .executeUpdate();
    }

    @Override
    public List<Object[]> sendFavoriteLeftPercentReminderMemList(int eventId) {
        String sql = "SELECT fi.member_id,mb.user_name, fi.event_id, ei.event_name FROM favorite fi JOIN event_info ei ON fi.event_id = ei.event_id JOIN MEMBER mb ON fi.member_id =mb.member_id WHERE fi.is_followed=1 AND fi.event_id=:eventId";
        return session.createNativeQuery(sql).setParameter("eventId", eventId).getResultList();
    }
    @Override
    public String titleTemplateNotification(int notificationId) {
        return (String) session.createNativeQuery("SELECT nt.title_template FROM notification_template nt WHERE nt.notification_id=:nid")
                .setParameter("nid", notificationId)
                .getSingleResult();
    }

    @Override
    public String messageTemplateNotification(int notificationId) {
        return (String) session.createNativeQuery("SELECT nt.message_template FROM notification_template nt WHERE nt.notification_id=:nid")
                .setParameter("nid", notificationId)
                .getSingleResult();
    }

    @Override
    public String linkTemplateNotification(int notificationId) {
        return (String) session.createNativeQuery("SELECT nt.link_url FROM notification_template nt WHERE nt.notification_id=:nid")
                .setParameter("nid", notificationId)
                .getSingleResult();
    }
}


