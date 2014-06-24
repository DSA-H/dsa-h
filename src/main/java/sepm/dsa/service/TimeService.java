package sepm.dsa.service;

import sepm.dsa.model.DSADate;

public interface TimeService {
    DSADate getCurrentDate();

    void setCurrentDate(DSADate dsaDate);

    /**
     * Forward time at the given days. Changes offers of all traders (depentet on the timespan) and usage of taverns.
     * Moves movingTraders.
     *
     * @param days the amount of days to forward
     */
    void forwardTime(int days);

    /**
     * Used for progress notifications whilst forwardTime
     *
     * @return the progress amount
     */
    public int getForwardProgress();

    /**
     * Used for progress notifications whilst forwardTime
     *
     * @return the max amount of stuff to do
     */
    public int getForwardMaxProgress();

    /**
     * Used for progress notifications whilst forwardTime
     *
     * @return the progress notification messages for nice notifications
     */
    public String getForwardMessage();

    /**
     * Used for progress notifications whilst forwardTime
     * called to reset load window after OK
     */
    public void resetProgress();
}
