package combodyfatcontrol.httpsgithub.bodyfatcontrol;


import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Gravity;

/**
 * Custom drawer extends from DrawerLayout but passes touch events.
 */
public class CustomDrawerLayout extends DrawerLayout {
    private static final int DRAWER_BAR_TOUCH_SIZE = 30;

    public CustomDrawerLayout(Context context) {
        super(context);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if (event.getX() > DRAWER_BAR_TOUCH_SIZE && event.getAction() == MotionEvent.ACTION_DOWN){
            return isDrawerOpen(Gravity.START) || isDrawerVisible(Gravity.START);
        }
        return true;
    }
}