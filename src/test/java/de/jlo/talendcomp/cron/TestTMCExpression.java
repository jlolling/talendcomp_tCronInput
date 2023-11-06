package de.jlo.talendcomp.cron;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.jlo.talendcomp.cron.TMCExpression.Time;

public class TestTMCExpression {

	@Test
	public void testTMCExpressionInterval() throws Exception {
		String json = "	{\r\n"
				+ "		\"type\": \"DAILY\",\r\n"
				+ "		\"interval\": 1,\r\n"
				+ "		\"startDate\": \"2023-07-18\",\r\n"
				+ "		\"timeZone\": \"Europe/Berlin\",\r\n"
				+ "		\"atTimes\": {\r\n"
				+ "			\"type\": \"AT_INTERVALS\",\r\n"
				+ "			\"startTime\": \"01:03\",\r\n"
				+ "			\"endTime\": \"06:00\",\r\n"
				+ "			\"interval\": 10\r\n"
				+ "		},\r\n"
				+ "		\"name\": \"daily_every_10min\"\r\n"
				+ "	}";
		TMCExpression ex = new TMCExpression(json);
		int expectedCount = 30;
		List<Time> result = ex.getTimes();
		for (Time t : result) {
			System.out.println(t);
		}
		assertEquals(expectedCount, result.size());
		
	}

}
