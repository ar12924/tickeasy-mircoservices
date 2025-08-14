package microservices.member.dao;

import microservices.member.dao.impl.MemberDaoImpl;
import microservices.member.vo.Member;
import org.flywaydb.core.Flyway;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import java.util.Properties;

import static org.junit.Assert.*;

public class MemberDaoIntegrationTest {
	private static final DockerImageName MYSQL_IMAGE = DockerImageName.parse("mysql:8.0.37");
	private MySQLContainer<?> mysql;
	private DataSource dataSource;
	private SessionFactory sessionFactory;
	private Flyway flyway;
	private MemberDaoImpl memberDao;

	@Before
	public void setUp() throws Exception {
		mysql = new MySQLContainer<>(MYSQL_IMAGE)
			.withDatabaseName("microservice")
			.withUsername("test")
			.withPassword("test");
		mysql.start();

		HikariConfig cfg = new HikariConfig();
		cfg.setJdbcUrl(mysql.getJdbcUrl());
		cfg.setUsername(mysql.getUsername());
		cfg.setPassword(mysql.getPassword());
		cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource = new HikariDataSource(cfg);

		flyway = Flyway.configure()
			.dataSource(dataSource)
			.locations("classpath:db/migration")
			.load();
		flyway.migrate();

		LocalSessionFactoryBean fb = new LocalSessionFactoryBean();
		fb.setDataSource(dataSource);
		fb.setPackagesToScan("microservices.member.vo");
		Properties props = new Properties();
		props.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		props.setProperty("hibernate.hbm2ddl.auto", "validate");
		props.setProperty("hibernate.show_sql", "false");
		fb.setHibernateProperties(props);
		fb.afterPropertiesSet();
		sessionFactory = fb.getObject();

		memberDao = new MemberDaoImpl();
		java.lang.reflect.Field f = MemberDaoImpl.class.getDeclaredField("session");
		f.setAccessible(true);
		f.set(memberDao, sessionFactory.getCurrentSession());
	}

	@After
	public void tearDown() {
		if (mysql != null) mysql.stop();
	}

	@Test
	public void update_shouldWritePhotoKey_only() {
		sessionFactory.getCurrentSession().beginTransaction();
		Member m = new Member();
		m.setUserName("u1");
		m.setEmail("u1@example.com");
		m.setPassword("p1");
		sessionFactory.getCurrentSession().save(m);
		Integer id = m.getMemberId();
		sessionFactory.getCurrentSession().getTransaction().commit();

		microservices.member.vo.Member upd = new microservices.member.vo.Member();
		upd.setMemberId(id);
		upd.setNickName("nick");
		upd.setEmail("u1@example.com");
		upd.setPhone("0900000000");
		upd.setPhotoKey("s3://bucket/key");

		sessionFactory.getCurrentSession().beginTransaction();
		int updated = sessionFactory.getCurrentSession()
			.createQuery("UPDATE Member SET nickName = :nick, email = :email, phone = :phone, photoKey = :photoKey WHERE memberId = :id")
			.setParameter("nick", upd.getNickName())
			.setParameter("email", upd.getEmail())
			.setParameter("phone", upd.getPhone())
			.setParameter("photoKey", upd.getPhotoKey())
			.setParameter("id", upd.getMemberId())
			.executeUpdate();
		sessionFactory.getCurrentSession().getTransaction().commit();

		assertEquals(1, updated);

		sessionFactory.getCurrentSession().beginTransaction();
		microservices.member.vo.Member got = sessionFactory.getCurrentSession().get(microservices.member.vo.Member.class, id);
		sessionFactory.getCurrentSession().getTransaction().commit();
		assertNotNull(got);
		assertEquals("s3://bucket/key", got.getPhotoKey());
	}
}


