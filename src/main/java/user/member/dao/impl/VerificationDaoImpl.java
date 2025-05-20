package user.member.dao.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;

import user.member.dao.VerificationDao;
import user.member.vo.VerificationToken;

public class VerificationDaoImpl implements VerificationDao {

	@Override
	public VerificationToken findByToken(String tokenName) {
        String hql = "FROM VerificationToken t WHERE t.tokenName = :tokenName";
        return getSession()
                .createQuery(hql, VerificationToken.class)
                .setParameter("tokenName", tokenName)
                .uniqueResult();
	}

	@Override
	public VerificationToken findById(Integer tokenId) {
		return getSession().get(VerificationToken.class, tokenId);
	}

	@Override
	public boolean insert(VerificationToken token) {
        getSession().save(token);
        return true;
	}

	@Override
	public boolean update(VerificationToken token) {
        Session session = getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<VerificationToken> cu = cb.createCriteriaUpdate(VerificationToken.class);
        Root<VerificationToken> root = cu.from(VerificationToken.class);
        cu.set(root.get("tokenName"), token.getTokenName())
          .set(root.get("tokenType"), token.getTokenType())
          .set(root.get("expiredTime"), token.getExpiredTime())
          .where(cb.equal(root.get("tokenId"), token.getTokenId()));
        int updated = session.createQuery(cu).executeUpdate();
        return updated > 0;
	}

	@Override
	public boolean deleteById(Integer tokenId) {
        Session session = getSession();
        VerificationToken t = session.get(VerificationToken.class, tokenId);
        if (t == null) return false;
        session.remove(t);
        return true;
	}
}
