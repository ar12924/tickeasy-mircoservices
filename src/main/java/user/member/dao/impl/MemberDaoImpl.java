package user.member.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import user.member.dao.MemberDao;
import user.member.vo.Member;

public class MemberDaoImpl implements MemberDao {

	@Override
	public boolean insert(Member member) {
		getSession().save(member);
		return true;
	}

	@Override
	public boolean update(Member member) {
		String hql = "UPDATE Member m SET " + "m.nickName = :nickName, " + "m.email = :email, " + "m.phone = :phone, "
				+ "m.birthDate = :birthDate, " + "m.gender = :gender, " + "m.password = :password, "
				+ "m.photo = :photo " + "WHERE m.memberId = :id";
		Query<?> query = getSession().createQuery(hql);
		query.setParameter("nickName", member.getNickName());
		query.setParameter("email", member.getEmail());
		query.setParameter("phone", member.getPhone());
		query.setParameter("birthDate", member.getBirthDate());
		query.setParameter("gender", member.getGender());
		query.setParameter("password", member.getPassword());
		query.setParameter("photo", member.getPhoto());
		query.setParameter("id", member.getMemberId());
		return query.executeUpdate() > 0;
	}

	@Override
	public Member findByUserName(String userName) {
		Session session = getSession();
		return session.createQuery("FROM Member WHERE userName = :userName", Member.class)
				.setParameter("userName", userName).uniqueResult();
	}

	@Override
	public Member findById(int memberId) {
		return getSession().get(Member.class, memberId);
	}

	@Override
	public boolean delete(int memberId) {
		Session session = getSession();
		Member m = session.load(Member.class, memberId);
		if (m == null)
			return false;
		session.remove(m);
		return true;
	}

	@Override
	public List<Member> listAll() {
		return getSession().createQuery("FROM Member", Member.class).list();
	}

}
