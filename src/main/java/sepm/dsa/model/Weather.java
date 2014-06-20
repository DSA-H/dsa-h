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

    private static final int weatherTypeCount = 10;
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
        ArrayList<Weather> ret = new ArrayList<Weather>();
        for (int i = 0; i < weatherTypeCount; i++){
            ret.add(Weather.parse(i));//add every weather once
        }
        Temperature t = temperature;
        RainfallChance r = rainFallChance;

        if (t.equals(Temperature.VERY_LOW)){//remove all weather types that need temperature > 0°
            ret.remove(Weather.HOT);
            ret.remove(Weather.TROPIC);
            ret.remove(Weather.SUNNY);
            ret.remove(Weather.WET);
            ret.remove(Weather.RAINY);
        }else if (t.equals(Temperature.LOW)){//remove all really hot weather types
            ret.remove(Weather.HOT);
            ret.remove(Weather.TROPIC);
            ret.remove(Weather.SUNNY);
        }else if (t.equals(Temperature.MEDIUM)){
            //nothing
        }else if (t.equals(Temperature.HIGH)){
            ret.remove(Weather.ICY);
        }else if (t.equals(Temperature.VERY_HIGH)){
            ret.remove(Weather.ICY);
            ret.remove(Weather.COLD);
            ret.remove(Weather.SNOW);
        }

        Weather val = Weather.SNOW;

        //take the value of the rainfallChance as a factor how often a random weather is calculated if it is not a rainy one
        for (int i = 0; i<r.getValue()+1;i++){
            val = ret.get((int) (Math.random() * ret.size())); //[0,ret.size]
            if (val.getValue() < 5) { //rainy state
                return val;
            }
        }

        return val;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

}
