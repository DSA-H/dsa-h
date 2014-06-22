package sepm.dsa.service;


import javafx.scene.paint.Color;
import org.controlsfx.dialog.Dialogs;

public class RollDiceServiceImpl implements RollDiceService{
    @Override
    public String talentThrow(int baseA, int baseB, int baseC, int talenvalue, int difficulty) {
        int dice1 = (int) (Math.random() * 20) + 1;
        int dice2 = (int) (Math.random() * 20) + 1;
        int dice3 = (int) (Math.random() * 20) + 1;
        int result = talenvalue - difficulty;
        if (result < 0) {
            difficulty = result;
            result = 0;
        } else {
            difficulty = 0;
        }
        if (dice1 > (baseA + difficulty)) {
            result -= (dice1 - (baseA + difficulty));
        }
        if (dice2 > (baseB + difficulty)) {
            result -= (dice2 - (baseB + difficulty));
        }
        if (dice3 > (baseC + difficulty)) {
            result -= (dice3 - (baseC + difficulty));
        }
        if (dice1 == 20 && dice2 == 20 || dice2 == 20 && dice3 == 20 || dice1 == 20 && dice3 == 20) {
            return "PATZER";
        } else if (dice1 == 1 && dice2 == 1 || dice2 == 1 && dice3 == 1 || dice1 == 1 && dice3 == 1) {
            return "MEISTERHAFT";
        }
        return result + "";
    }
}
