package sepm.dsa.model;


import java.util.ArrayList;

public enum Weather {

    SNOW(0,"Schneefall"),
    STORM(1, "Sturm"),
    WET(2, "Feucht"),
    RAINY(3, "Regen"),
    CLOUDY(4, "Bewölkt"),
    ICY(5,"Eisig"),
    COLD(6,"Kalt"),
    SUNNY(7, "Sonnig"),
    TROPIC(8,"Tropisch"),
    HOT(9, "Heiß");

    private int value;
    private String name;

    Weather(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public static Weather parse(int value) {
        Weather right = null;        // Default
        for (Weather item : Weather.values()) {
            if (item.getValue() == value) {
                right = item;
                break;
            }
        }
        return right;
    }

    public static Weather getNewWeather(Temperature temperature, RainfallChance rainFallChance){
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < 9; i++){
            ret.add(i);
        }
        int t = temperature.getValue();
        int r = rainFallChance.getValue();

        if (t==0){
            ret.remove(8);
            ret.remove(7);
            ret.remove(2);
            ret.remove(3);
        }else if (t==1){
            ret.remove(8);
            ret.remove(7);
        }else if (t==2){
            //nothing
        }else if (t==3){
            ret.remove(5);
        }else if (t==4){
            ret.remove(5);
            ret.remove(6);
            ret.remove(0);
        }

        int val = 0;

        for (int i = 0; i<r+1;i++){
            val = ret.get((int) (Math.random() * ret.size())); //[0,ret.size]
            if (val < 5) { //rainy state
                return Weather.parse(val);
            }
        }

        return Weather.parse(val);
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

}
