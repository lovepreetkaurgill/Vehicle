package com.auribises.vehicle;

import android.app.ProgressDialog;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    @InjectView(R.id.editTextName)
    EditText eTxtName;

    @InjectView(R.id.editTextPhone)
    EditText eTxtPhone;

    @InjectView(R.id.editTextEmail)
    EditText eTxtEmail;

    @InjectView(R.id.radioButtonMale)
    RadioButton rbMale;

    @InjectView(R.id.radioButtonFemale)
    RadioButton rbFemale;

    @InjectView(R.id.editTextVehicle)
    EditText eTxtVehicle;

    @InjectView(R.id.editTextVehicleNumber)
    EditText eTxtVehicleNumber;

    @InjectView(R.id.buttonSubmit)
    Button btnSubmit;

    Vehicle vehicle, rcvVehicle;


    boolean updateMode;

    RequestQueue requestQueue;

    ProgressDialog progressDialog;

    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        preferences = getSharedPreferences(Util.PREFS_NAME,MODE_PRIVATE);
        editor = preferences.edit();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait..");
        progressDialog.setCancelable(false);

        vehicle = new Vehicle();

    /*  adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item);
        adapter.add("--Select branch--");
        adapter.add("computer science and engineering");
        adapter.add("electrical engineering");
        adapter.add("information technology");
        adapter.add("civil engineering");
        adapter.add("mechanical engineering");
        adapter.add("production engineering");

        spbranch.setAdapter(adapter);

        spbranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i != 0){
                    vehicle.setbranch(adapter.getItem(i));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/

        rbMale.setOnCheckedChangeListener(this);
        rbFemale.setOnCheckedChangeListener(this);


        requestQueue = Volley.newRequestQueue(this);

        Intent rcv = getIntent();
        updateMode = rcv.hasExtra("keyVehicle");


        if(updateMode){
            rcvVehicle = (Vehicle)rcv.getSerializableExtra("keyVehicle");
            eTxtName.setText(rcvVehicle.getName());
            eTxtPhone.setText(rcvVehicle.getPhone());
            eTxtEmail.setText(rcvVehicle.getEmail());
            eTxtVehicle.setText(rcvVehicle.getVehicle());
            eTxtVehicleNumber.setText(rcvVehicle.getVehiclenumber());

            if(rcvVehicle.getGender().equals("Male")){
                rbMale.setChecked(true);
            }else{
                rbFemale.setChecked(true);
            }


            btnSubmit.setText("Update");
        }
    }

    boolean isNetworkConected(){

        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();


        return (networkInfo!=null && networkInfo.isConnected());

    }

    public void clickHandler(View view){
        if(view.getId() == R.id.buttonSubmit){

            vehicle.setName(eTxtName.getText().toString().trim());
            vehicle.setPhone(eTxtPhone.getText().toString().trim());
            vehicle.setEmail(eTxtEmail.getText().toString().trim());
            vehicle.setVehicle(eTxtVehicle.getText().toString().trim());
            vehicle.setVehiclenumber(eTxtVehicleNumber.getText().toString().trim());



            if(validateFields()) {
                if (isNetworkConected())
                    insertIntoCloud();
                else
                    Toast.makeText(this, "Please connect to Internet", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(this, "Please correct Input", Toast.LENGTH_LONG).show();
            }
        }
    }

    void insertIntoCloud(){

        String url="";

        if(!updateMode){
            url = Util.INSERT_VEHICLE_PHP;
        }else{
            url = Util.UPDATE_VEHICLE_PHP;
        }

        progressDialog.show();



        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");
                    String message = jsonObject.getString("message");

                    if(success == 1){
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();

                        if(!updateMode){

                            editor.putString(Util.KEY_NAME, vehicle.getName());
                            editor.putString(Util.KEY_PHONE, vehicle.getPhone());
                            editor.putString(Util.KEY_EMAIL, vehicle.getEmail());
                            editor.putString(Util.KEY_VEHICLE, vehicle.getVehicle());
                            editor.putString(Util.KEY_VEHICLENUMBER, vehicle.getVehiclenumber());

                            editor.commit();

                            Intent home = new Intent(MainActivity.this,MainActivity.class);
                            startActivity(home);
                            finish();
                        }

                        if(updateMode)
                            finish();

                    }else{
                        Toast.makeText(MainActivity.this,message,Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }catch (Exception e){
                    e.printStackTrace();
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Some Exception",Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this,"Some Error"+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<>();

                if(updateMode)
                    map.put("id",String.valueOf(rcvVehicle.getId()));

                map.put("name", vehicle.getName());
                map.put("phone", vehicle.getPhone());
                map.put("email", vehicle.getEmail());
                map.put("gender", vehicle.getGender());
                map.put("vehicle", vehicle.getVehicle());
                map.put("vehiclenumber", vehicle.getVehiclenumber());
                return map;
            }
        };

        requestQueue.add(request);

        clearFields();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int id = compoundButton.getId();

        if(b) {
            if (id == R.id.radioButtonMale) {
                vehicle.setGender("Male");
            } else {
                vehicle.setGender("Female");
            }
        }
    }



    void clearFields(){
        eTxtName.setText("");
        eTxtEmail.setText("");
        eTxtPhone.setText("");
        rbMale.setChecked(false);
        rbFemale.setChecked(false);
        eTxtVehicle.setText("");
        eTxtVehicleNumber.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(0,101,0,"All Vehicles");


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == 101){
            Intent i = new Intent(MainActivity.this,AllVehicleActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    boolean validateFields(){
        boolean flag = true;

        if(vehicle.getName().isEmpty()){
            flag = false;
            eTxtName.setError("Please Enter Name");
        }

        if(vehicle.getPhone().isEmpty()){
            flag = false;
            eTxtPhone.setError("Please Enter Phone");
        }else{
            if(vehicle.getPhone().length()<10){
                flag = false;
                eTxtPhone.setError("Please Enter 10 digits Phone Number");
            }
        }

        if(vehicle.getEmail().isEmpty()){
            flag = false;
            eTxtEmail.setError("Please Enter Email");
        }else{
            if(!(vehicle.getEmail().contains("@") && vehicle.getEmail().contains("."))){
                flag = false;
                eTxtEmail.setError("Please Enter correct Email");
            }
        }
        if(vehicle.getVehicle().isEmpty()){
            flag = false;
            eTxtVehicle.setError("Please Enter correct Vehicle");
        }
        if(vehicle.getVehiclenumber().isEmpty()){
            flag = false;
            eTxtVehicleNumber.setError("Please Enter correct VehicleNumber");
        }


        return flag;

    }
}
