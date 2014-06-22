package sepm.dsa.service;

/**
 * Contains Methods to roll Dices
 */
public interface RollDiceService {

    /**
     * Rolles 3 dices (1-20) on a talent with A, B and C as base values and adds the diffeculty and returns the result.
     * @param baseA first baseValue of the talent
     * @param baseB second baseValue of the talent
     * @param baseC thired baseValue of the talent
     * @param talenvalue of the character on that talent
     * @param difficulty
     * @return the result value or "MEISTERHAFT" in case of at least two 1 or "PATZER" in case of at least two 20
     */
    String talentThrow(int baseA, int baseB, int baseC, int talenvalue,  int difficulty);
}
