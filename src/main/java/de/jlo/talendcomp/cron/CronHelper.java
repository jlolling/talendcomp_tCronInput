/**
 * Copyright 2015 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jlo.talendcomp.cron;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.quartz.CronExpression;

public class CronHelper {
	
	private CronExpression ce = null;
	private Date rangeStartDate = null;
	private Date rangeEndDate = new Date();
	private int index = 0;
	private int maxIndex = 0;
	private Date nextStartDate = null;
	private Date prevStartDate = null;
	private Calendar nc = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private Locale locale = null;

	/**
	 * Constructs a helper class
	 * @param expression a cron expression
	 * @throws ParseException
	 */
	public CronHelper(String expression) throws ParseException {
		if (expression == null || expression.isEmpty()) {
			throw new IllegalArgumentException("Cron expression cannot be null or empty!");
		}
		ce = new CronExpression(expression);
	}
	
	/**
	 * steps to the next possible start date 
	 * @return true if the next start date is not after the range end date
	 */
	public boolean next() {
		if (nextStartDate == null) {
			nextStartDate = rangeStartDate;
		}
		if (nc == null) {
			nc = java.util.Calendar.getInstance();
		}
		prevStartDate = nextStartDate;
		nextStartDate = ce.getNextValidTimeAfter(prevStartDate);
		nc.setTime(nextStartDate);
		if (nextStartDate.after(rangeEndDate)) {
			return false;
		} else {
			index++;
			if (maxIndex > 0 && index > maxIndex) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	public void reset() {
		index = 0;
		nextStartDate = null;
	}
	
    public int getNextMinute() {
    	return nc.get(java.util.Calendar.MINUTE);
    }

    public int getNextHour() {
    	return nc.get(java.util.Calendar.HOUR_OF_DAY);
    }

    public int getNextMonth() {
    	return nc.get(java.util.Calendar.MONTH) + 1;
    }

    public int getNextYear() {
    	return nc.get(java.util.Calendar.YEAR);
    }

    public int getNextDayOfWeek() {
    	return nc.get(java.util.Calendar.DAY_OF_WEEK);
    }

    public int getNextDayOfMonth() {
    	return nc.get(java.util.Calendar.DAY_OF_MONTH);
    }

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

	public Date getRangeStartDate() {
		return rangeStartDate;
	}
	
	public void setRangeStartDate(String datestr) throws ParseException {
		if (datestr != null && datestr.trim().isEmpty() == false) {
			this.rangeStartDate = sdf.parse(datestr);
		}
	}

	public void setRangeStartDate(Date rangeStartDate) {
		if (rangeStartDate == null) {
			throw new IllegalArgumentException("Range start date cannot be null");
		}
		this.rangeStartDate = rangeStartDate;
	}

	public Date getRangeEndDate() {
		return rangeEndDate;
	}

	public void setRangeEndDate(String datestr) throws ParseException {
		if (datestr != null && datestr.trim().isEmpty() == false) {
			this.rangeEndDate = sdf.parse(datestr);
		}
	}

	public void setRangeEndDate(Date rangeEndDate) {
		if (rangeEndDate == null) {
			throw new IllegalArgumentException("Range end date cannot be null");
		}
		this.rangeEndDate = rangeEndDate;
	}

	public int getIndex() {
		return index - 1;
	}

	public Date getNextStartDate() {
		return nextStartDate;
	}

	public int getMaxIndex() {
		return maxIndex;
	}

	public void setMaxIndex(Integer maxIndex) {
		if (maxIndex != null) {
			this.maxIndex = maxIndex;
		}
	}

	public Date getPrevStartDate() {
		return prevStartDate;
	}

	public void setLocale(String localeString) {
		locale = createLocale(localeString);
		nc = java.util.Calendar.getInstance(locale);
	}
	
    private static Locale createLocale(String localeName) {
        if (localeName == null || localeName.trim().length() < 2) {
        	return Locale.getDefault();
        } else {
        	localeName = localeName.trim();
            Locale locale = null;
            int pos = localeName.indexOf('_');
            if (pos > 1) {
                String language = localeName.substring(0, pos);
                String country = localeName.substring(pos + 1);
                locale = new Locale(language, country);
            } else {
                locale = new Locale(localeName);
            }
            return locale;
        }
    }

}
