package combodyfatcontrol.httpsgithub.bodyfatcontrol;

public class Measurement {
    private int date; // UTC Unix BUT in minutes
    private int calories;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }
}
