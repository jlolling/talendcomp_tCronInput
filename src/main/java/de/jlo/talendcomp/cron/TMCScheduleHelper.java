package de.jlo.talendcomp.cron;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TMCScheduleHelper implements ScheduleHelper {
	
	private Date rangeStartDate = null;
	private Date rangeEndDate = new Date();
	private int index = 0;
	private int maxIndex = 0;
	private Date nextStartDate = null;
	private Date prevStartDate = null;
	private Calendar nc = null;
	private Locale locale = null;
	private Date startDate = null;
	private TMCExpression ce = null;
	
	public TMCScheduleHelper(String tmcScheduleJson) throws Exception {
		if (tmcScheduleJson == null || tmcScheduleJson.trim().isEmpty()) {
			throw new IllegalArgumentException("tcmScheduleJson cannot be null or empty!");
		}
		this.ce = new TMCExpression(tmcScheduleJson);
	}

	public void reset() {
		index = 0;
		nextStartDate = null;
	}
	
    @Override
	public int getNextMinute() {
    	return nc.get(java.util.Calendar.MINUTE);
    }

    @Override
	public int getNextHour() {
    	return nc.get(java.util.Calendar.HOUR_OF_DAY);
    }

    @Override
	public int getNextMonth() {
    	return nc.get(java.util.Calendar.MONTH) + 1;
    }

    @Override
	public int getNextYear() {
    	return nc.get(java.util.Calendar.YEAR);
    }

    @Override
	public int getNextDayOfWeek() {
    	return nc.get(java.util.Calendar.DAY_OF_WEEK);
    }

    @Override
	public int getNextDayOfMonth() {
    	return nc.get(java.util.Calendar.DAY_OF_MONTH);
    }

	public Date getRangeStartDate() {
		return rangeStartDate;
	}
	
	public void setRangeStartDate(String datestr) throws ParseException {
		if (datestr != null && datestr.trim().isEmpty() == false) {
			this.rangeStartDate = GenericDateUtil.parseDate(datestr);
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
			this.rangeEndDate = GenericDateUtil.parseDate(datestr);
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

	/**
	 * steps to the next possible start date 
	 * @return true if the next start date is not after the range end date
	 */
	@Override
	public boolean next() {
		if (nextStartDate == null) {
			nextStartDate = rangeStartDate;
		}
		if (nc == null) {
			nc = java.util.Calendar.getInstance();
		}
		prevStartDate = nextStartDate;
		if (prevStartDate.after(rangeEndDate)) {
			return false;
		} else {
			index++;
			if (maxIndex > 0 && index > maxIndex) {
				return false;
			} else {
				nextStartDate = ce.getNextValidTimeAfter(prevStartDate);
				nc.setTime(nextStartDate);
				return true;
			}
		}
	}

}
