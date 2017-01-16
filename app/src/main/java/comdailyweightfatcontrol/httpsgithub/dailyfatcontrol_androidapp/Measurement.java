package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

public class Measurement {
    private int date; // UTC Unix
    private int HRValue;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getHRValue() {
        return HRValue;
    }

    public void setHRValue(int HRValue) {
        this.HRValue = HRValue;
    }
}
