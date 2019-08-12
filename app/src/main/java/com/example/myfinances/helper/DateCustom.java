package com.example.myfinances.helper;

import java.text.SimpleDateFormat;

public class DateCustom {

    public static String currentDate(){
        long date = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String dataString = simpleDateFormat.format(date);
        return dataString;
    }

    public static String transformSelectedDate(String date){
        //01/02/2000 -> 022000
        String dateReturn[] = date.split("/");
        String day = dateReturn[0];
        String month = dateReturn[1];
        String year = dateReturn[2];

        String monthYear = month + year;
        return monthYear;
    }
}
