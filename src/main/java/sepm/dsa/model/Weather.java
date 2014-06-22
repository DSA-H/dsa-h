package sepm.dsa.model;


import sepm.dsa.exceptions.DSARuntimeException;

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

    public Weather calcNextWeatehr(Temperature temperature, RainfallChance rainFallChance, int days) {
        // Weather changes not every day
        if(Math.random() < 1 - 0.33f*days) {
            return this;
        }
        return getNewWeather(temperature, rainFallChance);
    }

    public static Weather getNewWeather(Temperature temperature, RainfallChance rainFallChance){
        float weatherProbability[] = new float[Weather.values().length];

        if (temperature.equals(Temperature.VERY_LOW)){
            weatherProbability[Weather.CLOUDY.getValue()] = 1f;
            weatherProbability[Weather.SUNNY.getValue()] = 0.2f;
            weatherProbability[Weather.HOT.getValue()] = 0;
            weatherProbability[Weather.TROPIC.getValue()] = 0;
            weatherProbability[Weather.ICY.getValue()] = 1;
            weatherProbability[Weather.COLD.getValue()] = 1;
            weatherProbability[Weather.SNOW.getValue()] = 1;
            weatherProbability[Weather.WET.getValue()] = 0;
            weatherProbability[Weather.RAINY.getValue()] = 0;
            weatherProbability[Weather.STORM.getValue()] = 0.1f;
        }else if (temperature.equals(Temperature.LOW)){
            weatherProbability[Weather.CLOUDY.getValue()] = 1f;
            weatherProbability[Weather.SUNNY.getValue()] = 0.7f;
            weatherProbability[Weather.HOT.getValue()] = 0.05f;
            weatherProbability[Weather.TROPIC.getValue()] = 0;
            weatherProbability[Weather.ICY.getValue()] = 0.5f;
            weatherProbability[Weather.COLD.getValue()] = 1;
            weatherProbability[Weather.SNOW.getValue()] = 0.7f;
            weatherProbability[Weather.WET.getValue()] = 0.2f;
            weatherProbability[Weather.RAINY.getValue()] = 0.2f;
            weatherProbability[Weather.STORM.getValue()] = 0.1f;
        }else if (temperature.equals(Temperature.MEDIUM)){
            weatherProbability[Weather.CLOUDY.getValue()] = 1f;
            weatherProbability[Weather.SUNNY.getValue()] = 1f;
            weatherProbability[Weather.HOT.getValue()] = 0.6f;
            weatherProbability[Weather.TROPIC.getValue()] = 0.1f;
            weatherProbability[Weather.ICY.getValue()] = 0.1f;
            weatherProbability[Weather.COLD.getValue()] = 0.6f;
            weatherProbability[Weather.SNOW.getValue()] = 0.1f;
            weatherProbability[Weather.WET.getValue()] = 1f;
            weatherProbability[Weather.RAINY.getValue()] = 1f;
            weatherProbability[Weather.STORM.getValue()] = 0.1f;
        }else if (temperature.equals(Temperature.HIGH)){
            weatherProbability[Weather.CLOUDY.getValue()] = 0.7f;
            weatherProbability[Weather.SUNNY.getValue()] = 0.7f;
            weatherProbability[Weather.HOT.getValue()] = 1f;
            weatherProbability[Weather.TROPIC.getValue()] = 0.5f;
            weatherProbability[Weather.ICY.getValue()] = 0f;
            weatherProbability[Weather.COLD.getValue()] = 0.05f;
            weatherProbability[Weather.SNOW.getValue()] = 0f;
            weatherProbability[Weather.WET.getValue()] = 1f;
            weatherProbability[Weather.RAINY.getValue()] = 1f;
            weatherProbability[Weather.STORM.getValue()] = 0.1f;
        }else if (temperature.equals(Temperature.VERY_HIGH)){
            weatherProbability[Weather.CLOUDY.getValue()] = 1f;
            weatherProbability[Weather.SUNNY.getValue()] = 1f;
            weatherProbability[Weather.HOT.getValue()] = 1f;
            weatherProbability[Weather.TROPIC.getValue()] = 1f;
            weatherProbability[Weather.ICY.getValue()] = 0f;
            weatherProbability[Weather.COLD.getValue()] = 0f;
            weatherProbability[Weather.SNOW.getValue()] = 0f;
            weatherProbability[Weather.WET.getValue()] = 1f;
            weatherProbability[Weather.RAINY.getValue()] = 1f;
            weatherProbability[Weather.STORM.getValue()] = 0.1f;
        }

        if(rainFallChance.equals(RainfallChance.VERY_LOW)) {
            weatherProbability[Weather.CLOUDY.getValue()] *= 0.1f;
            weatherProbability[Weather.TROPIC.getValue()] *= 0;
            weatherProbability[Weather.SNOW.getValue()] *= 0.1f;
            weatherProbability[Weather.WET.getValue()] *= 0.1f;
            weatherProbability[Weather.RAINY.getValue()] *= 0.1f;
        }else if(rainFallChance.equals(RainfallChance.LOW)) {
            weatherProbability[Weather.CLOUDY.getValue()] *= 0.5f;
            weatherProbability[Weather.TROPIC.getValue()] *= 0.1f;
            weatherProbability[Weather.SNOW.getValue()] *= 0.5f;
            weatherProbability[Weather.WET.getValue()] *= 0.5f;
            weatherProbability[Weather.RAINY.getValue()] *= 0.5f;
        }else if(rainFallChance.equals(RainfallChance.MEDIUM)) {
            // *= 1;
        }else if(rainFallChance.equals(RainfallChance.HIGH)) {
            weatherProbability[Weather.CLOUDY.getValue()] *= 2f;
            weatherProbability[Weather.TROPIC.getValue()] *= 1.5f;
            weatherProbability[Weather.SNOW.getValue()] *= 2f;
            weatherProbability[Weather.WET.getValue()] *= 2f;
            weatherProbability[Weather.RAINY.getValue()] *= 2f;
        }else if(rainFallChance.equals(RainfallChance.VERY_HIGH)) {
            weatherProbability[Weather.CLOUDY.getValue()] *= 8f;
            weatherProbability[Weather.TROPIC.getValue()] *= 3f;
            weatherProbability[Weather.SNOW.getValue()] *= 8f;
            weatherProbability[Weather.WET.getValue()] *= 8f;
            weatherProbability[Weather.RAINY.getValue()] *= 8f;
        }

        float sumProbability = 0;
        for(float wp : weatherProbability) {
            sumProbability += wp;
        }

        double rand = Math.random() * sumProbability;

        int i = 0;
        float preWp = 0;
        for(float wp : weatherProbability) {
            preWp = wp + preWp;
            if(rand <= preWp) {
                return Weather.values()[i];
            }
            i++;
        }
        throw new DSARuntimeException("Kein gültiges Wetter generiert!");
    }

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

}
