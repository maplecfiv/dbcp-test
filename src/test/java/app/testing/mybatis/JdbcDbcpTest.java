package app.testing.mybatis;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import app.testing.mybatis.model.DecTest;

public class JdbcDbcpTest {

	private Logger logger = Logger.getLogger(JdbcDbcpTest.class);
	private Connection conn = null;

	@After
	public void emptyRecords() throws Exception {
		logger.info("");
		logger.info("start cleanup");
		PreparedStatement pt = null;
		pt = conn.prepareStatement("DELETE FROM TBL_DECTEST");
		logger.info(pt.executeUpdate() + " record(s) deleted");
		conn.commit();
		pt.close();
		logger.info("finish cleanup");
		logger.info("");
	}

	@Test
	public void processJdbc() {
		try {
			Properties properties = new Properties();
			properties.load(JdbcDbcpTest.class.getClassLoader().getResourceAsStream("config.properties"));
			conn = DriverManager.getConnection(properties.getProperty("dataSource.url"),
					properties.getProperty("dataSource.username"), properties.getProperty("dataSource.password"));
			conn.setAutoCommit(false);
			assertNotNull(conn);
			process(conn);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void processDbcp() {
		try {
			conn = DBCPDataSource.getConnection();
			conn.setAutoCommit(false);
			assertNotNull(conn);
			process(conn);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Test
	public void processDbcp2() {
		try {
			conn = DBCP2DataSource.getConnection();
			conn.setAutoCommit(false);
			assertNotNull(conn);
			process(conn);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void process(Connection conn) throws Exception {
		
		logger.info("JDK version "+System.getProperty("java.version"));
		
		logger.info("[Connection class: "+conn.getClass().getName()+"] connected to " + conn.getMetaData().getURL().toString());
		
		CompareUtils compareUtils = new CompareUtils();

		Integer insertCount = 2;

		PreparedStatement pt = null;

		DecTest d;

		try {

			for (int i = 0; i < insertCount; i++) {

				pt = conn.prepareStatement("INSERT INTO TBL_DECTEST (DECTEST) VALUES (?)");

				d = new DecTest();
				d.setDecTest(compareUtils.generateInsertValue(i));

				pt.setBigDecimal(1, d.getDecTest());
				logger.debug("[Perform " + (i + 1) + " insert] "+pt.executeUpdate() + " record inserted (value = " + d.getDecTest() + ")");

				pt.close();

			}

			conn.commit();

			String selectQuery = "SELECT DECTEST FROM TBL_DECTEST";

			logger.info("Execute query " + selectQuery);

			ResultSet rs = conn.createStatement().executeQuery(selectQuery);

			Set<BigDecimal> decTests = new HashSet<>();
			while (rs.next()) {
				decTests.add(rs.getBigDecimal("DECTEST"));
			}

			compareUtils.isValueInSet(insertCount, decTests);

			rs.close();
		} catch(Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}


class DBCPDataSource {

	private static org.apache.commons.dbcp.BasicDataSource ds = new org.apache.commons.dbcp.BasicDataSource();

	static {
		Properties properties = new Properties();
		try {
			properties.load(DBCPDataSource.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ds.setUrl(properties.getProperty("dataSource.url"));
		ds.setUsername(properties.getProperty("dataSource.username"));
		ds.setPassword(properties.getProperty("dataSource.password"));
		ds.setPoolPreparedStatements(Boolean.valueOf(properties.getProperty("dataSource.poolPreparedStatements")));
		ds.setDriverClassName(properties.getProperty("dataSource.driverClassName"));
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}

class DBCP2DataSource {

	private static org.apache.commons.dbcp2.BasicDataSource ds = new org.apache.commons.dbcp2.BasicDataSource();

	static {
		Properties properties = new Properties();
		try {
			properties.load(DBCP2DataSource.class.getClassLoader().getResourceAsStream("config.properties"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		ds.setUrl(properties.getProperty("dataSource.url"));
		ds.setUsername(properties.getProperty("dataSource.username"));
		ds.setPassword(properties.getProperty("dataSource.password"));
		ds.setPoolPreparedStatements(Boolean.valueOf(properties.getProperty("dataSource.poolPreparedStatements")));
		ds.setDriverClassName(properties.getProperty("dataSource.driverClassName"));
	}

	public static Connection getConnection() throws SQLException {
		return ds.getConnection();
	}
}