package comdailyweightfatcontrol.httpsgithub.dailyfatcontrol_androidapp;

public class Measurement {
    private int date; // UTC Unix
    private int HRValue;
    private int caloriesOut; // value * 1000
    private int caloriesOutSum;  // value * 1000
    private int isManualCalories;
    private int userBirthYear; //ex: 1980
    private int userGender; // 0 = female; 1 = male
    private int userHeight; // centimeters, ex: 170
    private int userWeight; // grams, ex (82kg): 82000
    private int userActivityClass; // activity level from 0-100; 20 is low activity, 50 is medium, 80 is high (athlete)

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

    public int getCaloriesOut() {
        return caloriesOut;
    }

    public void setCaloriesOut(int caloriesOut) {
        this.caloriesOut = caloriesOut;
    }

    public int getIsManualCalories() {
        return isManualCalories;
    }

    public void setIsManualCalories(int isManualCalories) {
        this.isManualCalories = isManualCalories;
    }

    public int getUserBirthYear() {
        return userBirthYear;
    }

    public void setUserBirthYear(int userBirthYear) {
        this.userBirthYear = userBirthYear;
    }

    public int getUserGender() {
        return userGender;
    }

    public void setUserGender(int userGender) {
        this.userGender = userGender;
    }

    public int getUserHeight() {
        return userHeight;
    }

    public void setUserHeight(int userHeight) {
        this.userHeight = userHeight;
    }

    public int getUserWeight() {
        return userWeight;
    }

    public void setUserWeight(int userWeight) {
        this.userWeight = userWeight;
    }

    public int getUserActivityClass() {
        return userActivityClass;
    }

    public void setUserActivityClass(int userActivityClass) {
        this.userActivityClass = userActivityClass;
    }

    public int getCaloriesOutSum() {
        return caloriesOutSum;
    }

    public void setCaloriesOutSum(int caloriesOutSum) {
        this.caloriesOutSum = caloriesOutSum;
    }
}
