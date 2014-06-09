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

        String result = cas.get(0).getAmount() + " " + cas.get(0).getCurrency().getName();
        for (int i=1; i<cas.size(); i++) {
            CurrencyAmount ca = cas.get(i);
            result +=  delimiter + ca.getAmount() + " " + ca.getCurrency().getName();
        }
        return result;
    }

    public static String currencySetShortString(List<CurrencyAmount> cas) {
        return currencySetShortString(cas, ", ");
    }

    public static String currencySetShortString(List<CurrencyAmount> cas, final String delimiter) {

        if (cas.size() == 0) {
            throw new IllegalArgumentException("currencyAmounts size is zero");
        }

        String result = cas.get(0).getAmount() + " " + cas.get(0).getCurrency().getShortName();
        for (int i=1; i<cas.size(); i++) {
            CurrencyAmount ca = cas.get(i);
            result +=  delimiter + ca.getAmount() + " " + ca.getCurrency().getShortName();
        }
        return result;
    }

}