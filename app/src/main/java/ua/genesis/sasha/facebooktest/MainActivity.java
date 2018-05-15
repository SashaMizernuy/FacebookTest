package ua.genesis.sasha.facebooktest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;
import com.squareup.picasso.Target;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    LoginButton loginButton;
    CallbackManager callbackManager;

    Bundle parameters;
    NavigationView navigationView;

    List<MainModel> resultUsers;
    List<Geo> geoPos;
//    List<MainModel>list=new ArrayList<>();
    ListView listView;
    List<Main> arrList=new ArrayList<>();

    DBHelper dbHelper;

    class OnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

            Main main=(Main)parent.getItemAtPosition(position);
            Intent intent=new Intent(MainActivity.this,MapsActivity.class);
            startActivity(intent);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        listView=(ListView)findViewById(R.id.listView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        callbackManager = CallbackManager.Factory.create();//для обработки откликов входа.
        loginButton = (LoginButton) findViewById(R.id.login_button);
        //loginButton.setReadPermissions("email");

        loginButton.setReadPermissions(Arrays.asList("email","public_profile"));
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String name = object.getString("first_name");
                                    String image_url = "http://graph.facebook.com/" + id + "/picture?type=large";
                                    Toast.makeText(getApplicationContext(), "Email " + email+"Name " +name+"Image " +image_url, Toast.LENGTH_LONG).show();

                                    Log.i("Log","Image_URL= "+image_url);

                                    View headerView=navigationView.getHeaderView(0);
                                    TextView navUserName=(TextView) headerView.findViewById(R.id.name);
                                    navUserName.setText(name);

                                    View headerView1=navigationView.getHeaderView(0);
                                    TextView navUserEmail=(TextView) headerView1.findViewById(R.id.email);
                                    navUserEmail.setText(email);

                                    View headerView2=navigationView.getHeaderView(0);
                                    ImageView navUserImage=(ImageView) headerView2.findViewById(R.id.imageView);

                                    Picasso.get().load(image_url).into(navUserImage);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                parameters = new Bundle();
                parameters.putString("fields", "email,first_name,id,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
                // App code
                Log.i("Log","OnSuccess="+loginResult.toString());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        startAdapter();
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if(isLoggedIn==true)
            Log.i("Log","isLoggedIn="+isLoggedIn);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("Log","onActivityResult");
//        loginButton.setVisibility(View.INVISIBLE);

        final PlaceHolderApi usersApi= APIClient.getClient().create(PlaceHolderApi.class);

                final Call<List<MainModel>> users=usersApi.users();
        users.enqueue(new Callback<List<MainModel>>() {
            @Override
            public void onResponse(Call<List<MainModel>> call, Response<List<MainModel>> response) {
                resultUsers=response.body();
                if(response.isSuccessful()){



                    ContentValues cv = new ContentValues();
                    SQLiteDatabase db = dbHelper.getWritableDatabase();


                    Cursor c = db.query("mytable", null, null, null, null, null, null);
                    if (c.moveToFirst()) {
                        fillArray(c);
                    }
                    else {
                        Log.i("MyLog", "0 rows");

                        for(int i=0;i<resultUsers.size();i++){

                            String name=resultUsers.get(i).getName();
                            String email=resultUsers.get(i).getEmail();
                            String phone=resultUsers.get(i).getPhone();
                            String web=resultUsers.get(i).getWebsite();


                            cv.put("name", name);
                            cv.put("email", email);
                            cv.put("phone", phone);
                            cv.put("web", web);
                            long rowID = db.insert("mytable", null, cv);
                            Log.i("Log", "row inserted, ID = " + rowID);

                        }
                        Cursor cc = db.query("mytable", null, null, null, null, null, null);
                        cc.moveToFirst();
                        fillArray(cc);
                    }
                    c.close();
                    // закрываем подключение к БД
                    dbHelper.close();

                    startAdapter();
                }
                else{
                    Log.i("Log","response code" + response.code());
                }
            }
            @Override
            public void onFailure(Call<List<MainModel>> call, Throwable t) {
                Log.i("Script","failure " + t);
            }
        });
    }


    public void fillArray(Cursor c){
        int id = c.getColumnIndex("id");
        int name = c.getColumnIndex("name");
        int email = c.getColumnIndex("email");
        int phone = c.getColumnIndex("phone");
        int web = c.getColumnIndex("web");

        do {
            // получаем значения по номерам столбцов
            arrList.add(new Main(c.getInt(id),c.getString(name),c.getString(email),c.getString(phone),c.getString(web)));
            Log.i("Log","ARR_SIZE= "+arrList.size());
            Log.i("Log","COUNT_ID= "+id);
            // переход на следующую строку
            // а если следующей нет (текущая - последняя), то false - выходим из цикла
        }
        while (c.moveToNext());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startAdapter() {
        MyAdapter adapter=new MyAdapter(this,arrList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener());

    }
}
