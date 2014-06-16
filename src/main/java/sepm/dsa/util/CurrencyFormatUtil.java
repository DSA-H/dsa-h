package sepm.dsa.util;

import sepm.dsa.dao.CurrencyAmount;

import java.util.*;

public class CurrencyFormatUtil {


    public static String currencySetString(List<CurrencyAmount> cas) {
        return currencySetString(cas, ", ");
    }

    /**
     * @param cas ordered list of Currency - Amount mappings
     * @return a formatted string representing the currencies + amounts (e.g. '7 Silbertaler 14 Kreuzer')
     */
    public static String currencySetString(List<CurrencyAmount> cas, final String delimiter) {

        if (cas.size() == 0) {
            throw new IllegalArgumentException("currencyAmounts size is zero");
        }

        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (int i=0; i<cas.size(); i++) {
            CurrencyAmount ca = cas.get(i);
            if(ca.getAmount() != 0) {
                if(j > 0) {
                    sb.append(delimiter);
                }
                j++;
                sb.append(ca.getAmount()).append(" ").append(ca.getCurrency().getName());
            }
        }
        if(sb.length() == 0) {
            sb.append("0 ").append(cas.get(0).getCurrency().getName());
        }
        return sb.toString();
    }

    public static String currencySetShortString(List<CurrencyAmount> cas) {
        return currencySetShortString(cas, " ");
    }

    public static String currencySetShortString(List<CurrencyAmount> cas, final String delimiter) {

        if (cas.size() == 0) {
            throw new IllegalArgumentException("currencyAmounts size is zero");
        }
        StringBuilder sb = new StringBuilder();
        int j = 0;
        for (int i=0; i<cas.size(); i++) {
            CurrencyAmount ca = cas.get(i);
            if(ca.getAmount() != 0) {
                if(j > 0) {
                    sb.append(delimiter);
                }
                j++;
                sb.append(ca.getAmount()).append(ca.getCurrency().getShortName());
            }
        }
        if(sb.length() == 0) {
            sb.append("0").append(cas.get(0).getCurrency().getShortName());
        }
        return sb.toString();
    }

}
