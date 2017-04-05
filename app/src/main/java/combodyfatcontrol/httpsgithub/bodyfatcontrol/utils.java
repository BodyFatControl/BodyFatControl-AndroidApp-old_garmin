package combodyfatcontrol.httpsgithub.bodyfatcontrol;

import java.util.Calendar;

/**
 * Created by cas on 13-01-2017.
 */

public class utils {

    static public int returnMealTimeRadioButtonNumber () {
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int radioButtonNumber;
        
        if      (hourOfDay >= 0  && hourOfDay < 10) radioButtonNumber = 0; // breakfast
        else if (hourOfDay >= 10 && hourOfDay < 12) radioButtonNumber = 1; // morning snack
        else if (hourOfDay >= 12 && hourOfDay < 15) radioButtonNumber = 2; // lunch
        else if (hourOfDay >= 15 && hourOfDay < 19) radioButtonNumber = 3; // afternoon snack
        else if (hourOfDay >= 19 && hourOfDay < 22) radioButtonNumber = 4; // dinner
        else                                        radioButtonNumber = 5; // evening snack

        return radioButtonNumber;
    }
}
