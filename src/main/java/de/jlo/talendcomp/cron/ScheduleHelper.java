package de.jlo.talendcomp.cron;

import java.util.Date;

public interface ScheduleHelper {
	
	/**
	 * returns true if a next timestamp is available
	 * @return true
	 */
	public boolean next();

	/**
	 * set the start date of the date range for the output
	 * @param startDate
	 */
	public void setRangeStartDate(Date startDate);
	
	/**
	 * set the end date of the date range for the output
	 * @param endDate
	 */
	public void setRangeEndDate(Date startDate);
	
	/**
	 * set max records for output
	 * @param index
	 */
	public void setMaxIndex(Integer index);

	int getNextDayOfMonth();

	int getNextDayOfWeek();

	int getNextYear();

	int getNextMonth();

	int getNextHour();

	int getNextMinute();

    public static java.util.Date truncateToDay(java.util.Date timestamp) {
    	if(timestamp == null) return null;
    	java.util.Calendar c = java.util.Calendar.getInstance();
    	c.setTime(timestamp);
    	// cut time
    	c.set(java.util.Calendar.MINUTE, 0);
    	c.set(java.util.Calendar.SECOND, 0);
    	c.set(java.util.Calendar.MILLISECOND, 0);
    	c.set(java.util.Calendar.HOUR_OF_DAY, 0);
    	return c.getTime();
    }

    public static java.util.Date truncateToMinute(java.util.Date timestamp) {
    	if(timestamp == null) return null;
    	java.util.Calendar c = java.util.Calendar.getInstance();
    	c.setTime(timestamp);
    	// cut time
    	c.set(java.util.Calendar.SECOND, 0);
    	c.set(java.util.Calendar.MILLISECOND, 0);
    	return c.getTime();
    }

    public static java.util.Date truncateToMonth(java.util.Date timestamp) {
    	if(timestamp == null) return null;
    	// cut time
    	timestamp = truncateToDay(timestamp);
    	java.util.Calendar c = java.util.Calendar.getInstance();
    	c.setTime(timestamp);
    	// cut day
    	c.set(java.util.Calendar.DAY_OF_MONTH, 1);
    	return c.getTime();
    }    

    public static java.util.Date truncateToWeek(java.util.Date timestamp, java.util.Locale locale) {
    	if(timestamp == null) return null;
    	// cut time
    	timestamp = truncateToDay(timestamp);
    	java.util.Calendar c = java.util.Calendar.getInstance(locale);
    	c.setTime(timestamp);
    	// cut day
    	c.set(java.util.Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
    	return c.getTime();
    }

    public static java.util.Date addDays(java.util.Date today, long daysToAdd) {
    	if (today != null) {
    		return new java.util.Date(today.getTime() + (1000 * 60 * 60 * 24 * daysToAdd));
    	} else {
    		return null;
    	}
    }

}
