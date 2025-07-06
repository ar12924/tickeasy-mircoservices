package user.member.dao.impl;

import java.util.List;

import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import user.member.dao.VerificationDao;
import user.member.vo.VerificationToken;

@Repository
public class VerificationDaoImpl implements VerificationDao {

	@PersistenceContext
	private Session session;

	@Override
	public VerificationToken findByToken(String tokenName) {
		List<VerificationToken> list = session
	            .createQuery(
	                "SELECT t FROM VerificationToken t JOIN FETCH t.member WHERE t.tokenName = :tn",
	                VerificationToken.class)
	            .setParameter("tn", tokenName)
	            .setMaxResults(1)
	            .getResultList();
	        return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public VerificationToken findById(Integer tokenId) {
		return session.get(VerificationToken.class, tokenId);
	}

	@Override
	public boolean insert(VerificationToken token) {
		session.save(token);
		return true;
	}

	@Override
	public boolean update(VerificationToken token) {
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaUpdate<VerificationToken> cu = cb.createCriteriaUpdate(VerificationToken.class);
		Root<VerificationToken> root = cu.from(VerificationToken.class);
		cu.set(root.get("tokenName"), token.getTokenName()).set(root.get("tokenType"), token.getTokenType())
				.set(root.get("expiredTime"), token.getExpiredTime())
				.where(cb.equal(root.get("tokenId"), token.getTokenId()));
		int updated = session.createQuery(cu).executeUpdate();
		return updated > 0;
	}

	@Override
	public boolean deleteById(Integer tokenId) {
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaDelete<VerificationToken> cd = cb.createCriteriaDelete(VerificationToken.class);
		Root<VerificationToken> root = cd.from(VerificationToken.class);

		cd.where(cb.equal(root.get("tokenId"), tokenId));

		int deleted = session.createQuery(cd).executeUpdate();
		return deleted > 0;
	}
}
