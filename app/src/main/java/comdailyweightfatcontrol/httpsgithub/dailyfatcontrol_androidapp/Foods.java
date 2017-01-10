package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

public class Foods {
    private long date = 0;
    private long lastUsageDate = 0;
    private long usageFrequency = 0;
    private String name = null;
    private String brand = null;
    private int unit = 0;
    private String unitType = null;
    private int calories = 0;
    private int unityLogged = 0;
    private int caloriesLogged = 0;
    private int mealTime = 0;
    private boolean isAddedCalories = false;

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getUnityLogged() {
        return unityLogged;
    }

    public void setUnityLogged(int unityLogged) {
        this.unityLogged = unityLogged;
    }

    public int getCaloriesLogged() {
        return caloriesLogged;
    }

    public void setCaloriesLogged(int caloriesLogged) {
        this.caloriesLogged = caloriesLogged;
    }

    public int getMealTime() {
        return mealTime;
    }

    public void setMealTime(int mealTime) {
        this.mealTime = mealTime;
    }

    public boolean isAddedCalories() {
        return isAddedCalories;
    }

    public void setAddedCalories(boolean addedCalories) {
        isAddedCalories = addedCalories;
    }

    public long getUsageFrequency() {
        return usageFrequency;
    }

    public void setUsageFrequency(long usageFrequency) {
        this.usageFrequency = usageFrequency;
    }

    public long getLastUsageDate() {
        return lastUsageDate;
    }

    public void setLastUsageDate(long lastUsageDate) {
        this.lastUsageDate = lastUsageDate;
    }
}


