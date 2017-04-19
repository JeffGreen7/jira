package com.adaptavist.sr.cloud.samples.events
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp
import java.text.SimpleDateFormat

def dtime = 'customfield_11406'
def dday = 'customfield_12501'
def p = 'customfield_11402'
def deptime = issue.fields[dtime] as String
def priority = issue.fields[p] as String
def depday = issue.fields[dday] as String
def projectKey = "CM"

logger.info("dday is ${depday} dtime is ${deptime} priority is ${priority}")

if (issue == null || ((Map)issue.fields.project).key != projectKey) {
    logger.info("Wrong Project ${issue.fields.project.key}")
    return
}

SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
Date depdate=formatter.parse(deptime)

TimeZone.setDefault(TimeZone.getTimeZone("PST"));
Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("PST"))
cal1.setFirstDayOfWeek(Calendar.SUNDAY)
cal1.setTime(depdate)

def day = cal1.get(Calendar.DAY_OF_WEEK)
def time = cal1.get(Calendar.HOUR_OF_DAY)

logger.info("Deploy date is ${day} time is ${time}")

def output = 'True'
def isEmergency = priority.contains('Emergency')

logger.info("isEmergency ${isEmergency}")

if (isEmergency || ((Calendar.SATURDAY != day)
&& (Calendar.SUNDAY != day) && (Calendar.FRIDAY != day))
&& ((time >= 10) && (time <= 13)))  {
    output = 'True'
}
else
{
    output = 'False'
}
logger.info("Output is ${output}")

put("/rest/api/2/issue/${issue.key}")
        .header("Content-Type", "application/json")
        .body([
        fields:[
                (dday): output
        ]
])
        .asString()

logger.info("Project is ${issue.fields.project.key}")

logger.info("Date/Time is ${deptime}")
return
