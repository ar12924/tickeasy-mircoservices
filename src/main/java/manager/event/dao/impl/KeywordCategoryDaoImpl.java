package manager.event.dao.impl;

import java.io.Serializable;

import javax.persistence.PersistenceContext;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import manager.event.dao.KeywordCategoryDao;
import manager.event.vo.MngKeywordCategory;

@Repository
public class KeywordCategoryDaoImpl implements KeywordCategoryDao {
	@PersistenceContext
	private Session session;
	
//	@Autowired
//	private JdbcTemplate jdbcTemplate;

	@Override
	public Integer createKeywordCategory(MngKeywordCategory category) {
		Serializable generatedId = session.save(category);
		return (Integer) generatedId;
	}

}
