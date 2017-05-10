package app.logicify.com.imageprocessing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Vadim on 01.03.2017.
 */

public class ProfileActivity extends AppCompatActivity {

    public static String JsonURL;

    private static final String ID = MainActivity.getContext().getResources().getString(R.string.personID);
    private final String FIRSTNAME = MainActivity.getContext().getResources().getString(R.string.personFIRSTNAME);
    private final String LASTNAME = MainActivity.getContext().getResources().getString(R.string.personLASTNAME);
    private final String EMAIL = MainActivity.getContext().getResources().getString(R.string.personEMAIL);
    private final String AVATARURL = MainActivity.getContext().getResources().getString(R.string.personAVATARURL);

    private TextView userName;
    private TextView userEmail;
    private TextView userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_screen);

        userName = (TextView) findViewById(R.id.user_profile_name);
        userEmail = (TextView) findViewById(R.id.user_profile_email);
        userId = (TextView) findViewById(R.id.user_profile_id);

        Bundle extras = getIntent().getExtras();
        String json = extras.getString(JsonURL);
        //передаем в метод парсинга
        JSONURL(json);
        getSupportActionBar().hide();

    }

    public void JSONURL(String result) {
        try {
            //создали json объектов и отдали ему строку - result
            JSONObject json = new JSONObject(result);
            JSONObject payload = json.getJSONObject("payload");

            userName.setText(payload.getString(FIRSTNAME) + " " + payload.getString(LASTNAME));
            userEmail.setText(payload.getString(EMAIL));
            userId.setText(ID + ": " + payload.getString(ID));

        } catch (JSONException e) {
            System.out.println("Exp=" + e);
        }
    }

}
