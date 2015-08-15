package com.quark.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.droid.carson.City;
import com.droid.carson.DBHelper;
import com.parttime.utils.CheckUtils;
import com.quark.utils.Logger;

public class CityService {

    private static final String TAG = "CityService";

    /**
     * a-z排序
     */
    private static Comparator comparator = new Comparator<City>() {
        @Override
        public int compare(City lhs, City rhs) {
            String a = lhs.getPinyi().substring(0, 1);
            String b = rhs.getPinyi().substring(0, 1);
            int flag = a.compareTo(b);
            if (flag == 0) {
                return a.compareTo(b);
            } else {
                return flag;
            }

        }
    };

    public static ArrayList<City> getHotCity(Context context) {
        SQLiteDatabase db = CityDatabase.openDatabase(context);
        ArrayList<City> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from area where hot=1 order by id asc", null);
        int indexName = cursor.getColumnIndex("name");
        City city;
        while (cursor.moveToNext()) {
            city = new City(cursor.getString(indexName), "");
            list.add(city);
        }
        cursor.close();
        db.close();

        // Collections.sort(list, comparator);
        return list;
    }


    /**
     * 获取城市列表
     *
     * @param context
     * @return
     */
    public static ArrayList<City> getCityList(Context context) {
        SQLiteDatabase db = CityDatabase.openDatabase(context);
        ArrayList<City> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from area where deep=1 or deep=2 order by id asc", null);
        int indexName = cursor.getColumnIndex("name");
        int indexPinyin = cursor.getColumnIndex("pinyin");
        City city;
        while (cursor.moveToNext()) {
            city = new City(cursor.getString(indexName), cursor.getString(indexPinyin));
            if (CheckUtils.isEmpty(city.getPinyi())) {
                city.setPinyi(" ");
            }
            list.add(city);
        }
        cursor.close();
        db.close();

        Collections.sort(list, comparator);
        return list;
    }

    /**
     * 搜索城市
     * @param context
     * @param ccity
     * @return
     */
    public static ArrayList<City> getCityList(Context context, String ccity) {
        SQLiteDatabase db = CityDatabase.openDatabase(context);
        ArrayList<City> list = new ArrayList<>();

        String sql = "select * from area where name like '%" + ccity + "%' and (deep=1 or deep=2)";
        Cursor cursor = db.rawQuery(sql, null);
        int indexName = cursor.getColumnIndex("name");
        int indexPinyin = cursor.getColumnIndex("pinyin");
        City city;

        while (cursor.moveToNext()) {
            city = new City(cursor.getString(indexName), cursor.getString(indexPinyin));
            if (city.name.contains(ccity)) {
                Logger.i(TAG, city.toString());
                list.add(city);
            }
        }
        if (list.size() > 0) {
            list.add(new City("", "-"));
        }
        cursor.close();
        db.close();

        Collections.sort(list, comparator);
        return list;
    }


    public static ArrayList<String> getSubCitys(Context context, String city) {
        SQLiteDatabase mDbCity = CityDatabase.openDatabase(context);
        ArrayList<String> mCountries = new ArrayList<>();
        // 补充“全xxx”到第一条上
        mCountries.add("全" + city);

        int tempCityId = 1;
        String sql = "select * from area where name='" + city + "' order by id asc";
        Cursor cursor = mDbCity.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            // 必须使用moveToFirst方法将记录指针移动到第1条记录的位置
            cursor.moveToFirst();
            tempCityId = cursor.getInt(cursor.getColumnIndex("id"));
        }

        // **********根据parent_id 获取所有的区**************
        sql = "select * from area where parent_id=?";
        cursor = mDbCity.rawQuery(sql, new String[]{"" + tempCityId});
        // 遍历Cursor
        while (cursor.moveToNext()) {
            mCountries.add(cursor.getString(cursor
                    .getColumnIndex("name")));
        }
        return mCountries;
    }
}
