package sepm.dsa.model;

import sepm.dsa.exceptions.DSADateException;

public final class DSADate {
    private int year;
    private int month;   // every month has 30 days, month 13 has 5 days
    private int day;

    public DSADate(int month, int day, int year) {
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
        if(month > 13 || month < 1) {
            throw new DSADateException("Invalid Month '" + month + "'. Month has to be between 1 and 13");
        }
        if(month == 13 && day > 5) {
            throw new DSADateException("Invalid Day in Month 13 '" + day + "'. Month 13 has only 5 days");
        }
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        if(day > 30 || day < 1) {
            throw new DSADateException("Invalid Day '" +day + "'. Day has to be between 1 and 30");
        }
        this.day = day;
    }

    public boolean isAfter(DSADate date) {
        if(this.getYear()>date.getYear()) {
            return true;
        }else if(this.getYear()==date.getYear()) {
            if(this.getMonth()>date.getMonth()) {
                return true;
            }else if(this.getMonth()==date.getMonth()) {
                if(this.getDay()>this.getDay()) {
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }
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
}
