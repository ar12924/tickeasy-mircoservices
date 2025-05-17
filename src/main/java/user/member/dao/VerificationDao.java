package user.member.dao;

import user.member.entity.VerificationToken;

public interface VerificationDao {
	 boolean insert(VerificationToken token);
	 VerificationToken findByToken(String tokenStr);
	 boolean update(VerificationToken token);
}
