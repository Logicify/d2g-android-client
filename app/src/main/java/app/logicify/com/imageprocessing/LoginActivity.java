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
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vadim on 10.03.2017.
 */

public class LoginActivity extends Activity {
    public EditText login;
    public EditText pass;
    private ProgressDialog dialog;
    private InputStream is;
    ProfileActivity url;

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

                //тут указываем куда будем конектится, для примера я привел удаленных хост если у вас не получилось освоить wamp (:
                //new RequestTask().execute("http://myhomepage.hol.es/login.php");
                //new RequestTask().execute("https://d2g.com/api/user/login");
                //new RequestTask().execute("http://ec2-54-245-23-39.us-west-2.compute.amazonaws.com/user/login");

                new RequestTask().execute("http://api.drivetogain.ga/user/login");
            }
        });
    }

        class RequestTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... params) {

                try {
                    //создаем запрос на сервер
                    DefaultHttpClient hc = new DefaultHttpClient();
                    ResponseHandler<String> res = new BasicResponseHandler();
                    //он у нас будет посылать post запрос
                    HttpPost postMethod = new HttpPost(params[0]);
                    //будем передавать два параметра
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    //передаем параметры из наших текстбоксов
                    //лоигн
                    nameValuePairs.add(new BasicNameValuePair("email", login.getText().toString()));
                    //пароль
                    nameValuePairs.add(new BasicNameValuePair("password", pass.getText().toString()));
                    //собераем их вместе и посылаем на сервер
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    //получаем ответ от сервера
                    String response = hc.execute(postMethod, res);
                    //посылаем на вторую активность полученные параметры
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.putExtra(ProfileActivity.JsonURL, response.toString());
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("Exp=" + e);
                    //Toast.makeText(url, "ErRoR", Toast.LENGTH_SHORT).show();
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
