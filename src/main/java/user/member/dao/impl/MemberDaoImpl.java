package user.member.dao.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

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
		Session session = getSession();
		int result;
		// 已避免如不更改密碼，空密碼或舊密碼仍會寫入		
		if (member.getPassword() != null && !member.getPassword().isBlank()) {
			result = session
					.createQuery("UPDATE Member SET nickName = :nick, email = :email, phone = :phone, "
							+ "birthDate = :birth, gender = :gender, password = :pwd, photo = :photo "
							+ "WHERE memberId = :id")
					.setParameter("nick", member.getNickName()).setParameter("email", member.getEmail())
					.setParameter("phone", member.getPhone()).setParameter("birth", member.getBirthDate())
					.setParameter("gender", member.getGender()).setParameter("pwd", member.getPassword())
					.setParameter("photo", member.getPhoto()).setParameter("id", member.getMemberId()).executeUpdate();
		} else {
			result = session
					.createQuery("UPDATE Member SET nickName = :nick, email = :email, phone = :phone, "
							+ "birthDate = :birth, gender = :gender, photo = :photo " + "WHERE memberId = :id")
					.setParameter("nick", member.getNickName()).setParameter("email", member.getEmail())
					.setParameter("phone", member.getPhone()).setParameter("birth", member.getBirthDate())
					.setParameter("gender", member.getGender()).setParameter("photo", member.getPhoto())
					.setParameter("id", member.getMemberId()).executeUpdate();
		}

		return result > 0;
	}

	@Override
	public Member findByUserName(String userName) {
		Session session = getSession();
		List<Member> list = session.createQuery("FROM Member m WHERE m.userName = :userName", Member.class)
				.setParameter("userName", userName).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public Member findById(int memberId) {
		return getSession().get(Member.class, memberId);
	}

	@Override
	public boolean delete(int memberId) {
		Session session = getSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaDelete<Member> delete = cb.createCriteriaDelete(Member.class);
		Root<Member> root = delete.from(Member.class);

		delete.where(cb.equal(root.get("memberId"), memberId));

		int result = session.createQuery(delete).executeUpdate();
		return result > 0;
	}

	@Override
	public List<Member> listAll() {
		return getSession().createQuery("FROM Member m ORDER BY m.memberId", Member.class).getResultList();
	}

	@Override
	public Member findByEmail(String email) {
		List<Member> list = getSession().createQuery("FROM Member m WHERE m.email = :email", Member.class)
				.setParameter("email", email).getResultList();
		return list.isEmpty() ? null : list.get(0);

	}

	@Override
	public Member findByPhone(String phone) {
		List<Member> list = getSession().createQuery("FROM Member m WHERE m.phone = :phone", Member.class)
				.setParameter("phone", phone).getResultList();
		return list.isEmpty() ? null : list.get(0);
	}

}
