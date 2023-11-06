package de.jlo.talendcomp.cron;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ValueNode;

public class TMCExpression {

	private Locale locale = null;
	private Date startDate = null;
	private String tmcScheduleJson = null;
	private ObjectNode oneTriggerNode = null;
	private final static ObjectMapper objectMapper = new ObjectMapper();
	private String type = null;
	private ObjectNode atTimesNode = null;
	private ObjectNode atDaysNode = null;
	private TimeZone timeZone = null;
	private List<Time> listTimes = null;

	public TMCExpression(String triggerStr) throws Exception {
		if (triggerStr == null || triggerStr.trim().isEmpty()) {
			throw new IllegalArgumentException("triggerStr cannot be null or empty");
		}
		try {
			oneTriggerNode = (ObjectNode) objectMapper.readTree(triggerStr);
		} catch (Exception e) {
			throw new Exception("Error parsing trigger json. Error: " + e.getMessage() + "\njson: " + triggerStr, e);
		}
		this.type = getMandatoryValueString(oneTriggerNode, "type");
		this.atTimesNode = (ObjectNode) oneTriggerNode.get("atTimes");
		this.atDaysNode = (ObjectNode) oneTriggerNode.get("atDays");
		this.startDate = getMandatoryValueDate(oneTriggerNode, "startDate");
		JsonNode tzNode = oneTriggerNode.get("timeZone");
		if (tzNode != null && tzNode.isNull() == false) {
			String tz = tzNode.asText();
			timeZone = TimeZone.getTimeZone(tz);
		}
		configureTimeList();
	}
	
	private void configureTimeList() throws Exception {
		listTimes = new ArrayList<>();
		if (atTimesNode != null && atTimesNode.isNull() == false) {
			String atTimeType = getMandatoryValueString(atTimesNode, "type");
			if (atTimeType.equals("AT_TIME")) {
				String timeStr = getMandatoryValueString(atTimesNode, "time");
				listTimes.add(getTime(timeStr));
			} else if (atTimeType.equals("AT_SPECIFIC_TIMES")) {
				JsonNode timesNode = atTimesNode.get("times");
				if (timesNode != null && timesNode.isNull() == false) {
					if (timesNode.isArray()) {
						ArrayNode timeArray = (ArrayNode) timesNode;
						for (JsonNode oneTime : timeArray) {
							String timeStr = oneTime.asText();
							listTimes.add(getTime(timeStr));
						}
					} else if (timesNode.isTextual()) {
						String timeStr = timesNode.asText();
						listTimes.add(getTime(timeStr));
					} else {
						throw new Exception("Invalid times value in atTimes node: " + atTimesNode);
					}
				} else {
					throw new Exception("times attribute is missing in atTimes node: " + atTimesNode);
				}
			} else if (atTimeType.equals("AT_INTERVALS")) {
				// create times entries for intervals
				Date startTime = getMandatoryValueTime(atTimesNode, "startTime");
				Calendar c = Calendar.getInstance(timeZone);
				c.setTime(startTime);
				Date endTime = getMandatoryValueTime(atTimesNode, "endTime");
				int interval = getMandatoryValueInt(atTimesNode, "interval");
				boolean done = false;
				while (done == false) {
					Date d = c.getTime();
					if (d.after(endTime)) {
						done = true;
					} else {
						listTimes.add(getTime(d));
					}
					c.add(Calendar.MINUTE, interval);
				}
			}
		}
		
	}
	
	private Time getTime(String time) throws Exception {
		if (time == null || time.trim().isEmpty()) {
			throw new Exception("time value cannot be empty or null");
		}
		int pos = time.indexOf(':');
		if (pos == -1) {
			throw new Exception("time value: " + time + " does not contains a valid time");
		}
		String hours = time.substring(0, pos);
		String minutes = time.substring(pos + 1);
		Time t = new Time();
		t.hour = Integer.valueOf(hours);
		t.minute = Integer.valueOf(minutes);
		return t;
	}
	
	private Time getTime(Date time) throws Exception {
		if (time == null) {
			throw new Exception("time value cannot be empty or null");
		}
		Calendar c = Calendar.getInstance(timeZone);
		c.setTime(time);
		Time t = new Time();
		t.hour = c.get(Calendar.HOUR);
		t.minute = c.get(Calendar.MINUTE);
		return t;
	}

	public static class Time {
		int hour = 0;
		int minute = 0;
		@Override
		public String toString() {
			return (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute);
		}
	}
	
	public List<Time> getTimes() {
		return listTimes;
	}
	
	private String getMandatoryValueString(JsonNode node, String attribute) throws Exception {
		if (node == null || node.isNull()) {
			throw new Exception("node cannot be null");
		}
		JsonNode valueNode = node.get(attribute);
		if (valueNode == null || valueNode.isNull()) {
			throw new Exception("attribute: " + attribute + " is missing or null");
		}
		String value = valueNode.asText();
		if (value == null || value.trim().isEmpty()) {
			throw new Exception("attribute: " + attribute + " value is empty or null");
		}
		return value;
	}
	
	private int getMandatoryValueInt(JsonNode node, String attribute) throws Exception {
		try {
			String strValue = getMandatoryValueString(node, attribute);
			return Integer.parseInt(strValue);
		} catch (Exception e) {
			throw new Exception("Cannot get int value from attribute: " + attribute + ", error: " + e.getMessage(), e);
		}
	}
	
	private int getInterval() throws Exception {
		return getMandatoryValueInt(oneTriggerNode, "interval");
	}
	
	private Date getStartDate() throws Exception {
		return getMandatoryValueDate(oneTriggerNode, "startDate");
	}
	
	private Date getMandatoryValueDate(JsonNode node, String attribute) throws Exception {
		try {
			String strValue = getMandatoryValueString(node, attribute);
			return new SimpleDateFormat("yyyy-MM-dd").parse(strValue);
		} catch (Exception e) {
			throw new Exception("Cannot get Date value from attribute: " + attribute + ", error: " + e.getMessage(), e);
		}
	}
	
	private Date getMandatoryValueTime(JsonNode node, String attribute) throws Exception {
		try {
			String strValue = getMandatoryValueString(node, attribute);
			return new SimpleDateFormat("HH:mm").parse(strValue);
		} catch (Exception e) {
			throw new Exception("Cannot get Time value from attribute: " + attribute + ", error: " + e.getMessage(), e);
		}
	}

	/**
     * Returns the time zone for which this <code>CronExpression</code> 
     * will be resolved.
     */
    public TimeZone getTimeZone() {
        if (timeZone == null) {
            timeZone = TimeZone.getDefault();
        }

        return timeZone;
    }

    public Date getNextValidTimeAfter(Date after) {
		return getTimeAfter(after);
	}
    
    private Calendar getOnlyDate(Date ts) {
    	Calendar cl = new java.util.GregorianCalendar(getTimeZone());
        cl.setTime(ts);
        cl.set(Calendar.HOUR, 0);
        cl.set(Calendar.MINUTE, 0);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        return cl;
    }

    private Calendar getOnlyTime(Date ts) {
    	Calendar cl = new java.util.GregorianCalendar(getTimeZone());
        cl.setTime(ts);
        cl.set(Calendar.YEAR, 0);
        cl.set(Calendar.DAY_OF_YEAR, 1);
        return cl;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Computation Functions
    //
    ////////////////////////////////////////////////////////////////////////////

    public Date getTimeAfter(Date afterTime) {

        // Computation is based on Gregorian year only.
        Calendar cl = new java.util.GregorianCalendar(getTimeZone()); 

        // move ahead one second, since we're computing the time *after* the
        // given time
        afterTime = new Date(afterTime.getTime() + 1000);
        // CronTrigger does not deal with milliseconds
        cl.setTime(afterTime);
        cl.set(Calendar.SECOND, 0);
        cl.set(Calendar.MILLISECOND, 0);
        int week = 1;
        int dayOfMonth = 1;
        int dayOfYear = 1;
        int month = 1;
        int dayOfWeek = Calendar.MONDAY;
        int minute = 0;
        int hour = 0;
        Calendar clTime = null;
        Calendar clDate = null;
        boolean gotOne = false;
        // loop until we've computed the next time, or we've past the endTime
        while (!gotOne) {

            //if (endTime != null && cl.getTime().after(endTime)) return null;
            if(cl.get(Calendar.YEAR) > 2999) { // prevent endless loop...
                return null;
            }
            // check the date and time matches and continue if not matched
            week = cl.get(Calendar.WEEK_OF_YEAR);
            dayOfWeek = cl.get(Calendar.DAY_OF_WEEK);
            month = cl.get(Calendar.MONTH) + 1;
            dayOfMonth = cl.get(Calendar.DAY_OF_MONTH);
            dayOfYear = cl.get(Calendar.DAY_OF_YEAR);
            hour = cl.get(Calendar.HOUR);
            minute = cl.get(Calendar.MINUTE);
            if (type.equals("DAILY")) {
            	// check 
            }
            
            gotOne = true;  
        }
        
        return cl.getTime();
    }
    
}
