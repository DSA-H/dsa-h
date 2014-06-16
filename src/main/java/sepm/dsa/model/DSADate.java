package sepm.dsa.model;

import sepm.dsa.exceptions.DSADateException;
import sepm.dsa.exceptions.DSAValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * Representing a Date in DSA. In DSA each month has 30 days, each year 12 months, and each year 365 days. The 5 remaining
 * days ("nameless days") are at the end of the year in a 13. month, which has only 5 days.
 */
public final class DSADate {
    private int year;
    private int month;   // every month has 30 days, month 13 has 5 days
    private int day;

    public DSADate(long timestamp) {
        setTimestamp(timestamp);
    }

    public DSADate(int year, int month, int day) {
        setDay(day);
        setMonth(month);
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        if (month > 13 || month < 1) {
            throw new DSAValidationException("Ungültiger Monat '" + month + "'. Monat muss zwischen 1 und 13 liegen.");
        }
        if (month == 13 && day > 5) {
            throw new DSAValidationException("Ungültiger Tag im Namenlosen Monat! Es gibt nur 5 Namenlose Tage.");
        }
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if (day > 30 || day < 1) {
            throw new DSAValidationException("Ungültiger Tag '" + day + "'. Tag muss zwischen 1 und 30 liegen");
        }
        this.day = day;
    }

    public boolean isAfter(DSADate date) {
        if (this.getYear() > date.getYear()) {
            return true;
        } else if (this.getYear() == date.getYear()) {
            if (this.getMonth() > date.getMonth()) {
                return true;
            } else if (this.getMonth() == date.getMonth()) {
                if (this.getDay() > date.getDay()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Returns the timestamp of this dsa Date. The Timestamp is the time in days.
     * @return timestamp (in days)
     */
    public long getTimestamp() {
        return year * 365 + (month - 1) * 30 + (day - 1);
    }

    /**
     *
     * @param timestamp timestamp in days
     */
    public void setTimestamp(long timestamp) {
        this.year = (int) timestamp / 365;
        timestamp = timestamp % 365;
        this.month = (int) (timestamp / 30) + 1;
        timestamp = timestamp % 30;
        this.day = (int) timestamp + 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DSADate dsaDate = (DSADate) o;

        if (day != dsaDate.day) return false;
        if (month != dsaDate.month) return false;
        if (year != dsaDate.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + month;
        result = 31 * result + day;
        return result;
    }

    public String getMonthName() {
        return getMonthNames().get(month-1);
    }

    public int parseMonthName(String month) {
        switch (month) {
            case "Firun": return 1;
            case "Tsa": return 2;
            case "Phex": return 3;
            case "Peraine": return 4;
            case "Ingerimm": return 5;
            case "Rahja": return 6;
            case "Praios": return 7;
            case "Rondra": return 8;
            case "Efferd": return 9;
            case "Travia": return 10;
            case "Boron": return 11;
            case "Hesinde": return 12;
            case "Namenloser": return 13;
            default: throw new DSADateException("Kein entsprechender Monat gefunden");
        }
    }

    public static List<String> getMonthNames() {
        List<String> result = new ArrayList<>();
        result.add("Firun");
        result.add("Tsa");
        result.add("Phex");
        result.add("Peraine");
        result.add("Ingerimm");
        result.add("Rahja");
        result.add("Praios");
        result.add("Rondra");
        result.add("Efferd");
        result.add("Travia");
        result.add("Boron");
        result.add("Hesinde");
        result.add("Namenloser");
        return result;
    }

    @Override
    public String toString() {
        return day + ". " + getMonthName() + "[" + month + "]" + " " + year + " BF";
    }
}
