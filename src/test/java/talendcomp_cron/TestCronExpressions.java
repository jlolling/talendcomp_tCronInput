package talendcomp_cron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;

import de.jlo.talendcomp.cron.CronHelper;

public class TestCronExpressions {

	public static void main(String[] args) {
		try {
			testCronHelperRun();
//			testCronHelperList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void testCronExpressions() {
		String cronstr = "0 10,20,30 * * * ?";
		try {
			System.out.println("start....");
			CronExpression ex = new CronExpression(cronstr);
			System.out.println(ex.getCronExpression());
			System.out.println("-----------------");
			System.out.println(ex.getExpressionSummary());
			System.out.println("-----------------");
			System.out.println(ex.getNextValidTimeAfter(new java.util.Date()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testCronHelperRun() throws Exception {
		String cronstr = "0 0,30 * * * ?";
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CronHelper ch = new CronHelper(cronstr);
		ch.setRangeStartDate(sdfDate.parse("2015-08-27"));
		ch.setRangeEndDate(sdfDate.parse("2015-08-28"));
		ch.setMaxIndex(4);
		while (ch.next()) {
			System.out.println(ch.getIndex() + ": " + sdfTs.format(ch.getPrevStartDate()) + " - " + sdfTs.format(ch.getNextStartDate()));
		}
	}

}
