/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyCalendar;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author shane
 */
public class CalendarDateManager {

    static final int CAL_WIDTH = 7;
    static final int CAL_HEIGHT = 6;
    int[][] calDates = new int[6][7];
    int calYear;
    int calMonth;
    int calDayOfMon;
    final int[] calLastDateOfMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int calLastDate;
    Calendar today = Calendar.getInstance();
    Calendar cal;

    public CalendarDateManager() {
        setToday();
    }

    public void setToday() {
        calYear = today.get(Calendar.YEAR);
        calMonth = today.get(Calendar.MONTH);
        calDayOfMon = today.get(Calendar.DAY_OF_MONTH);
        makeCalData(today);
    }


    private void makeCalData(Calendar cal) {
        int calStartingPos = (cal.get(Calendar.DAY_OF_WEEK) + 7 - cal.get(Calendar.DAY_OF_MONTH) % 7) % 7;
        //取得第幾天開始  公式 (((禮拜+1)+7-日期)%7)%7
           
        if (calMonth == 1) { //如果選定2月就判斷是否閏年
            calLastDate = (calLastDateOfMonth[calMonth] + leapCheck(calYear));
        } else {
            calLastDate = calLastDateOfMonth[calMonth];
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                calDates[i][j] = 0;
            }
        }
        int k = 0;
        int num = 1;
        
        for (int i = 0; i < 6; i++) {
            if (i == 0) { //第一次從開頭算
                k = calStartingPos;
            } else {
                k = 0;
            }
            for (int j = k; j < 7; j++) {
                if (num <= calLastDate) { //避免超過日期長度
                    calDates[i][j] = (num++);
                }else break;
            }
        }
    }

    private int leapCheck(int year) { //閏年判斷
        if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
            return 1;
        }
        return 0;
    }

    public void moveMonth(int mon) {
        
        calMonth += mon;  

        if (calMonth >= 12) {
            calYear += 1;
            calMonth -= 12;
        }else if (calMonth < 0) {
            calYear -= 1;
            calMonth += 12;
        }

        cal = new GregorianCalendar(calYear, calMonth, calDayOfMon);
        makeCalData(cal);

    }
    
    
}
