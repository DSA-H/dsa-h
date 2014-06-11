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

//        List<CurrencyAmount> cas = new ArrayList<>(currencyAmounts);
//        Collections.sort(cas, (o1, o2) -> {
//            int valueToBaseRateResult = o1.getCurrency().getValueToBaseRate().compareTo(o2.getCurrency().getValueToBaseRate()));
//            if (valueToBaseRateResult != 0) {
//                return valueToBaseRateResult;
//            }
//            int nameResult = o1.getCurrency().getName().compareTo(o2.getCurrency().getName());
//            if (nameResult != 0) {
//                return nameResult;
//            }
//            return 0;
//        });

        StringBuilder sb = new StringBuilder();
        sb.append(cas.get(0).getAmount()).append(" ").append(cas.get(0).getCurrency().getName());
        for (int i=1; i<cas.size(); i++) {
            CurrencyAmount ca = cas.get(i);
            sb.append(delimiter).append(ca.getAmount()).append(" ").append(ca.getCurrency().getName());
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
        for (int i=0; i<cas.size(); i++) {
            CurrencyAmount ca = cas.get(i);
            if(ca.getAmount() > 0) {
                sb.append(ca.getAmount()).append(ca.getCurrency().getShortName()).append(delimiter);
            }
        }
        if(sb.length() == 0) {
            sb.append("0").append(cas.get(0).getCurrency().getShortName());
        }
        return sb.toString();
    }

}
