package sepm.dsa.service;

import sepm.dsa.model.DSADate;

public interface TimeService {
    DSADate getCurrentDate();

    void setCurrentDate(DSADate dsaDate);

    /**
     * Forward time at the given days. Changes offers of all traders (depentet on the timespan) and usage of taverns.
     * Moves movingTraders.
     *
     * @param days
     */
    void forwardTime(int days);

    //TODO @Johannes hier mein versuch an JAVADOC -- aber teilweise ist mir nicht ganz klar was die Methoden tun

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
     * called if bad stuff happens to go back to clean state
     * //TODO unklar
     */
    public void resetProgress();
}
