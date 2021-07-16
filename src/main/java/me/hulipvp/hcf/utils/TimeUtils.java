package me.hulipvp.hcf.utils;

import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtils {

    private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
    public static final Random random = new Random();
    @Getter private static final DecimalFormat REMAINING_SECONDS = new DecimalFormat("0.#");
    @Getter private static final DecimalFormat REMAINING_SECONDS_TRAIL = new DecimalFormat("0.0");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z");

    public static String getFormatted(long time) {
        return DurationFormatUtils.formatDuration(time, (time >= TimeUnit.HOURS.toMillis(1L) ? "HH:" : "") + "mm:ss");
    }

    public static String getFormatted(long time, boolean milliseconds, boolean trail) {
        if((milliseconds) && (time < TimeUnit.MINUTES.toMillis(1L)))
            return ((trail ? REMAINING_SECONDS_TRAIL : REMAINING_SECONDS)).format(time * 0.001D) + 's';

        return DurationFormatUtils.formatDuration(time, (time >= TimeUnit.HOURS.toMillis(1L) ? "HH:" : "") + "mm:ss");
    }

    public static String getTimeString(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        return sdf.format(timestamp);
    }

    public static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;

        if(toDate.equals(fromDate))
            return "now";
        if(toDate.after(fromDate))
            future = true;

        StringBuilder sb = new StringBuilder();
        int[] types = {1, 2, 5, 11, 12, 13};
        String[] names = {"year", "years", "month", "months", "day", "days", "hour", "hours", "minute", "minutes", "second", "seconds"};
        int accuracy = 0;

        for(int i = 0; i < types.length && accuracy <= 2; ++i) {
            int diff = dateDiff(types[i], fromDate, toDate, future);

            if(diff > 0) {
                ++accuracy;
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + ((diff > 1) ? 1 : 0)]);
            }
        }
        return (sb.length() == 0) ? "now" : sb.toString().trim();
    }

    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            ++diff;
        }

        --diff;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }

    public static String getTimeTill(Timestamp ts) {
        Date d1;
        Date d2;

        try {
            d1 = new Date(System.currentTimeMillis());
            d2 = new Date(ts.getTime());

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if(diffDays > 0) {
                return diffDays + ((diffDays == 1) ? " day, " : " days, ") +
                        diffHours + ((diffHours == 1) ? " hour, " : " hours, ") +
                        diffMinutes + ((diffMinutes == 1) ? " minute, " : " minutes, ") +
                        diffSeconds + ((diffSeconds == 1) ? " second" : " seconds");
            }

            if(diffHours > 0) {
                return diffHours + ((diffHours == 1) ? " hour, " : " hours, ") +
                        diffMinutes + ((diffMinutes == 1) ? " minute, " : " minutes, ") +
                        diffSeconds + ((diffSeconds == 1) ? " second" : " seconds");
            }

            if(diffMinutes > 0) {
                return diffMinutes + ((diffMinutes == 1) ? " minute, " : " minutes, ") +
                        diffSeconds + ((diffSeconds == 1) ? " second" : " seconds");
            }

            if(diffSeconds > 0) {
                return diffSeconds + ((diffSeconds == 1) ? " second" : " seconds");
            }

            return null;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long timeFromString(String time, boolean future) throws Exception {
        Matcher m = timePattern.matcher(time);

        int years = 0;
        int months = 0;
        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        boolean found = false;

        while(m.find()) {
            if(m.group() == null || m.group().isEmpty()) {
                continue;
            }

            for(int i = 0; i < m.groupCount(); i++) {
                if(m.group(i) != null && !m.group(i).isEmpty()) {
                    found = true;
                    break;
                }
            }

            if(found) {
                if(m.group(1) != null && !m.group(1).isEmpty()) {
                    years = Integer.parseInt(m.group(1));
                }

                if(m.group(2) != null && !m.group(2).isEmpty()) {
                    months = Integer.parseInt(m.group(2));
                }

                if(m.group(3) != null && !m.group(3).isEmpty()) {
                    weeks = Integer.parseInt(m.group(3));
                }

                if(m.group(4) != null && !m.group(4).isEmpty()) {
                    days = Integer.parseInt(m.group(4));
                }

                if(m.group(5) != null && !m.group(5).isEmpty()) {
                    hours = Integer.parseInt(m.group(5));
                }

                if(m.group(6) != null && !m.group(6).isEmpty()) {
                    minutes = Integer.parseInt(m.group(6));
                }

                if(m.group(7) != null && !m.group(7).isEmpty()) {
                    seconds = Integer.parseInt(m.group(7));
                }
                break;
            }
        }

        if(!found) {
            throw new Exception("Illegal Date");
        }

        if(years > 20) {
            throw new Exception("Illegal Date");
        }

        Calendar c = new GregorianCalendar();
        if(years > 0)
            c.add(Calendar.YEAR, years * (future ? 1 : -1));
        if(months > 0)
            c.add(Calendar.MONTH, months * (future ? 1 : -1));
        if(weeks > 0)
            c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
        if(days > 0)
            c.add(Calendar.DAY_OF_MONTH, days * (future ? 1 : -1));
        if(hours > 0)
            c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
        if(minutes > 0)
            c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
        if(seconds > 0)
            c.add(Calendar.SECOND, seconds * (future ? 1 : -1));

        return c.getTimeInMillis();
    }

    public static String serializeTimestamp(Timestamp ts) {
        return dateFormat.format(ts);
    }

    public static Timestamp deserializeTimestamp(String ts) {
        try {
            long time = dateFormat.parse(ts).getTime();
            return new Timestamp(time);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String formatSeconds(int seconds) {
        int minutes = seconds / 60;
        if(minutes == 0)
            return seconds + " seconds";
        seconds %= 60;
        return minutes + " minutes and " + seconds + " seconds";
    }
}
