package app.logicify.com.imageprocessing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Vadim on 10.03.2017.
 */

public class LoginActivity extends Activity {
    public EditText login;
    public EditText pass;
    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        Button btn = (Button) findViewById(R.id.button1);
        login = (EditText) findViewById(R.id.editText1);
        pass = (EditText) findViewById(R.id.editText2);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestTask().execute();
            }
        });
    }

        class RequestTask extends AsyncTask<String, String, String> {
            @Override
            protected String doInBackground(String... params) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://api.drivetogain.ga/user/login");

                try {
                    ResponseHandler<String> res = new BasicResponseHandler();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("email", login.getText().toString());
                    jsonObject.put("password", pass.getText().toString());
                    StringEntity se = new StringEntity(jsonObject.toString(), "UTF8");

                    httppost.setHeader("Content-type", "application/json");
                    httppost.setEntity(se);
                    HttpResponse response = httpclient.execute(httppost);

                    String json = EntityUtils.toString(response.getEntity());

                    //посылаем на вторую активность полученные параметры
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.putExtra(ProfileActivity.JsonURL, json);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Exp=" + e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                dialog.dismiss();
                super.onPostExecute(result);
            }

            @Override
            protected void onPreExecute() {
                dialog = new ProgressDialog(LoginActivity.this);
                dialog.setMessage("Загружаюсь...");
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.show();
                super.onPreExecute();
            }
        }
}
