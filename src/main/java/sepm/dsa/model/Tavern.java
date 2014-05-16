package sepm.dsa.model;

import java.io.Serializable;

public class Tavern implements Serializable {

    private static final long serialVersionUID = -2259554288598225744L;

    private Integer id;
    private String name;
    private Integer xPos;
    private Integer yPos;
    private Integer usage;
//    private ??? overnightStayPrice;
    private DSADate onDate;
    private Location location;

}
