package sepm.dsa.service;

import java.util.Comparator;

/**
 * Created by Jotschi on 13.01.2015.
 */
public class PriceComperator implements Comparator<String> {
    @Override
    public int compare(String o1, String o2) {
        int v1 = 0;
        String f1[] = o1.split(" ");
        for(String s : f1) {
            if(s.endsWith("D")) {
                v1 += Integer.parseInt(s.substring(0, s.length()-1)) * 1000;
            }else if(s.endsWith("S")) {
                v1 += Integer.parseInt(s.substring(0, s.length()-1)) *100;
            }else if(s.endsWith("H")) {
                v1 += Integer.parseInt(s.substring(0, s.length()-1)) *10;
            }else if(s.endsWith("K")) {
                v1 += Integer.parseInt(s.substring(0, s.length()-1));
            }
        }
        int v2 = 0;
        String f2[] = o2.split(" ");
        for(String s : f2) {
            if(s.endsWith("D")) {
                v2 += Integer.parseInt(s.substring(0, s.length()-1)) * 1000;
            }else if(s.endsWith("S")) {
                v2 += Integer.parseInt(s.substring(0, s.length()-1)) *100;
            }else if(s.endsWith("H")) {
                v2 += Integer.parseInt(s.substring(0, s.length()-1)) *10;
            }else if(s.endsWith("K")) {
                v2 += Integer.parseInt(s.substring(0, s.length()-1));
            }
        }
        return v1-v2;
    }
}
