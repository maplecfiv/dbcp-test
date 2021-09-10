package app.testing.mybatis;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import org.apache.log4j.Logger;

public class CompareUtils {

	private Logger logger = Logger.getLogger(CompareUtils.class);

	public BigDecimal generateInsertValue(int idx) {
		// .10999999940395355224609375 should be rounded to .11
		String value = idx * 100 + ".10999999940395355224609375";
		return new BigDecimal(value);
	}

	public BigDecimal generateCheckValue(int idx) {
		// .10999999940395355224609375 should be rounded to .11
		String value = idx * 100 + ".11";
		return new BigDecimal(value);
	}

	public void isValueInSet(Integer insertCount, Set<BigDecimal> decTests) {
		logger.info("*******************************");
		logger.info("* Value set: " + decTests);
		logger.info("*******************************");

		BigDecimal bigDecimalValue;
		Boolean isContains;
		String message;
		for (int i = 0; i < insertCount; i++) {
			bigDecimalValue = this.generateCheckValue(i);

			isContains = decTests.contains(bigDecimalValue);
			message = "[Perform " + (i + 1) + " validate] Is set contains " + bigDecimalValue + "? " + isContains;

			if (isContains) {
				logger.info(message);
			} else {
				logger.error(message);
			}

			assertTrue(decTests.contains(bigDecimalValue));

		}
	}
}
