package user.member.dao;

import common.dao.CommonDao;
import user.member.vo.VerificationToken;

public interface VerificationDao extends CommonDao{
	VerificationToken findByToken(String tokenName);

	VerificationToken findById(Integer tokenId);

	boolean insert(VerificationToken token);

	boolean update(VerificationToken token);

	boolean deleteById(Integer tokenId);
}
