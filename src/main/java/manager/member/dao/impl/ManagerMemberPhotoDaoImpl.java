package manager.member.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import manager.member.dao.ManagerMemberPhotoDao;

import java.sql.Blob;
/**
 * 會員照片資料存取實作類
 * 創建者: archchang
 * 創建日期: 2025-06-27
 */
@Repository
public class ManagerMemberPhotoDaoImpl implements ManagerMemberPhotoDao{
	@Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public int uploadPhoto(Integer memberId, byte[] photoData) {
        String sql = "UPDATE member SET photo = :photoData WHERE member_id = :memberId";
        NativeQuery<?> query = getCurrentSession().createNativeQuery(sql);
        query.setParameter("photoData", photoData);
        query.setParameter("memberId", memberId);
        
        return query.executeUpdate();
    }

    @Override
    public byte[] getPhoto(Integer memberId) {
        String sql = "SELECT photo FROM member WHERE member_id = :memberId";
        NativeQuery<?> query = getCurrentSession().createNativeQuery(sql);
        query.setParameter("memberId", memberId);
        
        Object result = query.uniqueResult();
        if (result == null) {
            return null;
        }
        
        if (result instanceof byte[]) {
            return (byte[]) result;
        } else if (result instanceof Blob) {
            try {
                Blob blob = (Blob) result;
                return blob.getBytes(1, (int) blob.length());
            } catch (Exception e) {
                throw new RuntimeException("照片資料讀取失敗", e);
            }
        }
        
        return null;
    }
}
