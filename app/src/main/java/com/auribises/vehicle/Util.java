package com.auribises.vehicle;

import android.net.Uri;

public class Util {

    // Information for my Database
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Vehicles.db";

    // Information for my Table
    public static final String TAB_NAME = "Vehicle";
    public static final String COL_ID = "_ID";
    public static final String COL_NAME = "NAME";
    public static final String COL_PHONE = "PHONE";
    public static final String COL_EMAIL = "EMAIL";
    public static final String COL_GENDER = "GENDER";
    public static final String COL_VEHICLE = "VEHICLE";
    public static final String COL_VEHICLENUMBER = "VEHICLENUMBER";

    public static final String CREATE_TAB_QUERY = "create table Vehicle(" +
            "_ID integer primary key autoincrement," +
            "NAME varchar(256)," +
            "PHONE varchar(20)," +
            "EMAIL varchar(256)," +
            "GENDER varchar(10)," +
            "VEHICLE varchar(256)" +
            "VEHICLENUMBER varchar(256)" +
            ")";


    public static final String PREFS_NAME = "visitorbook";
    public static final String KEY_NAME = "keyName";
    public static final String KEY_PHONE = "keyPhone";
    public static final String KEY_EMAIL = "keyEmail";
    public static final String KEY_VEHICLE = "keyVehicle";
    public static final String KEY_VEHICLENUMBER = "keyVehicleNumber";

    // URI
    public static final Uri VEHICLE_URI = Uri.parse("content://com.auribises.vehicle.teacherprovider/"+TAB_NAME);


    final static String URI = "http://tajinderj.esy.es/enc2017/";
    // URL
    public static final String INSERT_VEHICLE_PHP = "http://tajinderj.esy.es/enc2017/insert.php";

    public static final String RETRIEVE_VEHICLE_PHP = "http://tajinderj.esy.es/enc2017/retrieve.php";

    public static final String DELETE_VEHICLE_PHP = "http://tajinderj.esy.es/enc2017/delete.php";

    public static final String UPDATE_VEHICLE_PHP = "http://tajinderj.esy.es/enc2017/update.php";
}
