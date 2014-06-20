package sepm.dsa.service;

import sepm.dsa.model.DSADate;

public interface TimeService {
    DSADate getCurrentDate();
    void setCurrentDate(DSADate dsaDate);

    /**
     * Forward time at the given days. Changes offers of all traders (depentet on the timespan) and usage of taverns.
     * Moves movingTraders.
     * @param days
     */
    void forwardTime(int days);

    public int getForwardProgress();

    public int getForwardMaxProgress();

    public String getForwardMessage();

    public void resetProgress();
}
