package combodyfatcontrol.httpsgithub.bodyfatcontrol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {
    private TextView mTextViewAppVersion;
    private TextView mTextViewAbout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("About");

        mTextViewAppVersion = (TextView) findViewById(R.id.app_version);
        mTextViewAppVersion.setText(Html.fromHtml(getString(R.string.app_version)));

        mTextViewAbout = (TextView) findViewById(R.id.about_text);
        mTextViewAbout.setText(Html.fromHtml(getString(R.string.about_text)));
        mTextViewAbout.setMovementMethod(LinkMovementMethod.getInstance()); // this makes links work on the TextView
    }
}
