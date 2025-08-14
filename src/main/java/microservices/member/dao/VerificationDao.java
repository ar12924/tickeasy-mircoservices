package microservices.member.dao;

import common.dao.CommonDao;
import microservices.member.vo.VerificationToken;

public interface VerificationDao extends CommonDao {
    VerificationToken findByToken(String tokenName);

    VerificationToken findById(Integer tokenId);

    boolean insert(VerificationToken token);

    boolean update(VerificationToken token);

    boolean deleteById(Integer tokenId);

    VerificationToken findByTokenPrefix(String tokenPrefix);
}


