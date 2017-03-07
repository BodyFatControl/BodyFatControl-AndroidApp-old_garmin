package combodyfatcontrol.httpsgithub.bodyfatcontrol;

public class Foods {
    private int id = 0;
    private long date = 0;
    private long lastUsageDate = 0;
    private long usageFrequency = 0;
    private String name = null;
    private String brand = null;
    private float units = 0;
    private String unitType = null;
    private int calories = 0;
    private float unitLogged = 0;
    private int caloriesLogged = 0;
    private String mealTime = null;
    private boolean isCustomCalories = false;

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

    public float getUnits() {
        return units;
    }

    public void setUnits(float units) {
        this.units = units;
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

    public float getUnitsLogged() {
        return unitLogged;
    }

    public void setUnitsLogged(float unitsLogged) {
        this.unitLogged = unitsLogged;
    }

    public int getCaloriesLogged() {
        return caloriesLogged;
    }

    public void setCaloriesLogged(int caloriesLogged) {
        this.caloriesLogged = caloriesLogged;
    }

    public String getMealTime() {
        return mealTime;
    }

    public void setMealTime(String mealTime) {
        this.mealTime = mealTime;
    }

    public boolean getIsCustomCalories() {
        return isCustomCalories;
    }

    public void setIsCustomCalories(boolean customCalories) {
        isCustomCalories = customCalories;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isCustomCalories() {
        return isCustomCalories;
    }

    public void setCustomCalories(boolean customCalories) {
        isCustomCalories = customCalories;
    }
}


