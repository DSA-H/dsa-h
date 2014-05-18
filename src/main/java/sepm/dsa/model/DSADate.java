package sepm.dsa.model;

import sepm.dsa.exceptions.DSADateException;

public final class DSADate {
    private int year;
    private int month;
    private int day;

    public DSADate(int month, int day, int year) {
        setMonth(month);
        setDay(day);
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
        if(day > 12 || day < 1) {
            throw new DSADateException("Invalid Month '" + month + "'. Month has to be between 1 and 12");
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
