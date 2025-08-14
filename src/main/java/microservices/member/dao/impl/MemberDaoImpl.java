package microservices.member.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import microservices.member.dao.MemberDao;
import microservices.member.vo.Member;

@Repository
public class MemberDaoImpl implements MemberDao {
    @PersistenceContext
    private Session session;

    private microservices.member.vo.Member toEntity(Member src) {
        if (src == null) return null;
        microservices.member.vo.Member e = new microservices.member.vo.Member();
        e.setMemberId(src.getMemberId());
        e.setUserName(src.getUserName());
        e.setNickName(src.getNickName());
        e.setEmail(src.getEmail());
        e.setPhone(src.getPhone());
        e.setBirthDate(src.getBirthDate());
        e.setGender(src.getGender());
        e.setRoleLevel(src.getRoleLevel());
        e.setIsActive(src.getIsActive());
        e.setUnicode(src.getUnicode());
        e.setIdCard(src.getIdCard());
        e.setPassword(src.getPassword());
        e.setPhoto(src.getPhoto());
        e.setPhotoKey(src.getPhotoKey());
        return e;
    }

    private Member toDto(microservices.member.vo.Member e) {
        if (e == null) return null;
        Member d = new Member();
        d.setMemberId(e.getMemberId());
        d.setUserName(e.getUserName());
        d.setNickName(e.getNickName());
        d.setEmail(e.getEmail());
        d.setPhone(e.getPhone());
        d.setBirthDate(e.getBirthDate());
        d.setGender(e.getGender());
        d.setRoleLevel(e.getRoleLevel());
        d.setIsActive(e.getIsActive());
        d.setUnicode(e.getUnicode());
        d.setIdCard(e.getIdCard());
        d.setPassword(e.getPassword());
        d.setPhoto(e.getPhoto());
        d.setPhotoKey(e.getPhotoKey());
        return d;
    }

    @Override
    public boolean insert(Member member) {
        microservices.member.vo.Member e = toEntity(member);
        session.save(e);
        member.setMemberId(e.getMemberId());
        return true;
    }

    @Override
    public boolean update(Member member) {
        int result;
        if (member.getPassword() != null && !member.getPassword().isBlank()) {
            result = session
                    .createQuery("UPDATE Member SET nickName = :nick, email = :email, phone = :phone, "
                            + "birthDate = :birth, gender = :gender, password = :pwd, photoKey = :photoKey "
                            + "WHERE memberId = :id")
                    .setParameter("nick", member.getNickName()).setParameter("email", member.getEmail())
                    .setParameter("phone", member.getPhone()).setParameter("birth", member.getBirthDate())
                    .setParameter("gender", member.getGender()).setParameter("pwd", member.getPassword())
                    .setParameter("photoKey", member.getPhotoKey()).setParameter("id", member.getMemberId()).executeUpdate();
        } else {
            result = session
                    .createQuery("UPDATE Member SET nickName = :nick, email = :email, phone = :phone, "
                            + "birthDate = :birth, gender = :gender, photoKey = :photoKey " + "WHERE memberId = :id")
                    .setParameter("nick", member.getNickName()).setParameter("email", member.getEmail())
                    .setParameter("phone", member.getPhone()).setParameter("birth", member.getBirthDate())
                    .setParameter("gender", member.getGender())
                    .setParameter("photoKey", member.getPhotoKey())
                    .setParameter("id", member.getMemberId()).executeUpdate();
        }

        return result > 0;
    }

    @Override
    public Member findByUserName(String userName) {
        List<microservices.member.vo.Member> list = session.createQuery("FROM Member m WHERE m.userName = :userName", microservices.member.vo.Member.class)
                .setParameter("userName", userName).getResultList();
        return list.isEmpty() ? null : toDto(list.get(0));
    }

    @Override
    public Member findById(int memberId) {
        microservices.member.vo.Member e = session.get(microservices.member.vo.Member.class, memberId);
        return toDto(e);
    }

    @Override
    public boolean delete(int memberId) {
        CriteriaBuilder cb = session.getCriteriaBuilder();

        CriteriaDelete<microservices.member.vo.Member> delete = cb.createCriteriaDelete(microservices.member.vo.Member.class);
        Root<microservices.member.vo.Member> root = delete.from(microservices.member.vo.Member.class);

        delete.where(cb.equal(root.get("memberId"), memberId));

        int result = session.createQuery(delete).executeUpdate();
        return result > 0;
    }

    @Override
    public Member findByEmail(String email) {
        List<microservices.member.vo.Member> list = session.createQuery("FROM Member m WHERE m.email = :email", microservices.member.vo.Member.class)
                .setParameter("email", email).getResultList();
        return list.isEmpty() ? null : toDto(list.get(0));

    }

    @Override
    public Member findByPhone(String phone) {
        List<microservices.member.vo.Member> list = session.createQuery("FROM Member m WHERE m.phone = :phone", microservices.member.vo.Member.class)
                .setParameter("phone", phone).getResultList();
        return list.isEmpty() ? null : toDto(list.get(0));
    }
}


