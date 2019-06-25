package MyCalendar;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * @author Shane
 */
public class CalendarController {

    CalendarCore_API core;
    CalendarGUI myGUI;
    EventEditShow newEvent;
    EventShow showEvent;

    MyAttendees.AttendessChooser attendessChooser;

    JPanel calOpPanel;
    JPanel eventDiaplay;//活動視窗
    JPanel attendressPanel;//與會者視窗
    JMenuBar menuBar;

    ArrayList<String> cals = new ArrayList<String>(); //日曆儲存用
    ArrayList<Event> events = new ArrayList<Event>(); //活動清單儲存用
    ArrayList<String> eventsDad = new ArrayList<String>(); //存活動清單日歷id用
    ArrayList<String> todayEventsDad = new ArrayList<String>(); //存活動清單日歷id用
    ArrayList<Event> todayEvents = new ArrayList<Event>();//儲存當日的活動
    ArrayList<Event> temp = new ArrayList<Event>(); //戰存於顯示本月所有
    ArrayList<String> tempDad = new ArrayList<String>(); //戰存於顯示本月所有(儲存日歷id)

    int eventCount = 0;//儲存上一個活動個數(用來比對主日歷個數用)

    int viewMode = 0; //0全 1私人 2工作

    final Event loadingW = new Event().setSummary("Loading");

    private int focusList = 0;

    //按鈕監聽兵
    ListenForMenus MenusHandler = new ListenForMenus();
    ListenForCalOpButtons ButtonsHandler = new ListenForCalOpButtons();
    ListenForFeaButtons FeaturesHandler = new ListenForFeaButtons();
    ListenForDateButs DateButsHandler = new ListenForDateButs();
    ListenForjList jListHandler = new ListenForjList();
    ListenForEventDiaplay eventDiaplayHandler = new ListenForEventDiaplay();
    ListenForAttendess attendessHandler = new ListenForAttendess();

    //  利用 DateFormat 來parse 日期的字串
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final SimpleDateFormat df = new SimpleDateFormat("dd");
    private final SimpleDateFormat df2 = new SimpleDateFormat("MM");

    public CalendarController() {

        core = new CalendarCore_API();
        myGUI = new CalendarGUI();

        calOpPanel = myGUI.getOpPanel();
        menuBar = myGUI.getMenuBar();

        showEvent = new EventShow();

        newEvent = new EventEditShow();
        eventDiaplay = newEvent.getPanel();
        for (int i = 0; i < eventDiaplay.getComponentCount(); i++) {
            if (eventDiaplay.getComponent(i) instanceof JButton) {
                JButton button = (JButton) eventDiaplay.getComponent(i);
                button.addActionListener(eventDiaplayHandler); //埋監聽
            }
        }

        attendessChooser = new MyAttendees.AttendessChooser();//與會者
        attendressPanel = attendessChooser.getPanel();
        for (int i = 0; i < attendressPanel.getComponentCount(); i++) {
            if (attendressPanel.getComponent(i) instanceof JButton) {
                JButton button = (JButton) attendressPanel.getComponent(i);
                button.addActionListener(attendessHandler); //埋監聽
            }
        }

        myGUI.jList1.addListSelectionListener(jListHandler);
        myGUI.showBut.addActionListener(FeaturesHandler);
        myGUI.addBut.addActionListener(FeaturesHandler);
        myGUI.updateBut.addActionListener(FeaturesHandler);
        myGUI.delBut.addActionListener(FeaturesHandler);
        enableButInfo(false);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                myGUI.dateButs[i][j].addActionListener(this.DateButsHandler);
            }
        }

        for (int i = 0; i < calOpPanel.getComponentCount(); i++) {
            if (calOpPanel.getComponent(i) instanceof JButton) {
                JButton button = (JButton) calOpPanel.getComponent(i);
                button.addActionListener(ButtonsHandler); //埋監聽
            }
        }

        for (int i = 0; i < menuBar.getMenuCount(); i++) { //埋監聽給Menu

            if (menuBar.getMenu(i) instanceof JMenu) {
                JMenu menu = (JMenu) menuBar.getComponent(i);

                for (int j = 0; j < menu.getItemCount(); j++) {
                    if (menu.getItem(j) instanceof JMenuItem) {
                        JMenuItem menuItem = (JMenuItem) menu.getItem(j);
                        menuItem.addActionListener(MenusHandler);
                    }
                }

            }
        }

        showInitCal(0); //初始化內容的樣式
        focusToday();

        setEventsList();
        //日曆載入
        cals.clear();
        //將全部的放入工作篩選(看有沒有)

        try {
            cals.addAll(core.checkWorkCals(core.getAllList()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "查詢動作失敗，請檢查網路連線");
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }

        loadAllEvents(false);//本月
        loadEvents();//載入本天
    }

    private void loadEvents() {

                    temp.clear();//整月事件清除
            tempDad.clear();
            
        //System.out.println("--------我是分隔線----------");
        todayEvents.clear();
        todayEventsDad.clear();
        int start = 0, end = 0;
        String tmp = "";

        for (int i = 0; i < events.size(); i++) {

            for (int j = 0; j < 2; j++) {
                switch (j) {
                    case 0:
                        tmp = events.get(i).getStart().toString();
                        break;
                    case 1:
                        tmp = events.get(i).getEnd().toString();
                        break;
                }

                Date tmpDate = core.getFormatDate(tmp); //格式化不齊的格式

                if (j == 0) {  //轉為比較式
                    start = Integer.parseInt(df2.format(tmpDate) + df.format(tmpDate));
                } else {
                    end = Integer.parseInt(df2.format(tmpDate) + df.format(tmpDate));
                }

            }

            if (core.getOnlyDate()) {
                end = start;
            }

            String tmpW = "";
            if (myGUI.calDayOfMon < 10) {
                tmpW = String.valueOf(myGUI.calMonth + 1) + "0" + String.valueOf(myGUI.calDayOfMon);
            } else {
                tmpW = String.valueOf(myGUI.calMonth + 1) + String.valueOf(myGUI.calDayOfMon);
            }
            //System.out.println(tmpW);
            int runDay = Integer.parseInt(tmpW);

            if (start <= runDay && runDay <= end) {
//                System.out.println("主題:" + events.get(i).getSummary());
//                System.out.println("s:" + start);
//                System.out.println("t:" + runDay);
//                System.out.println("e:" + end);

                todayEvents.add(events.get(i));
                todayEventsDad.add(eventsDad.get(i));
            }

        }
        setEventsList(todayEvents);
//        for (int i = 0; i < todayEventsDad.size(); i++) {
//            System.out.println(todayEventsDad.get(i));
//        }

    }

    private void loadAllEvents(Boolean show) {

        try {
            Date startDate = sdf.parse(String.valueOf(myGUI.calYear) + "-" + String.valueOf(myGUI.calMonth + 1) + "-" + 01 + " 00:00:00");
            Date endDate = sdf.parse(String.valueOf(myGUI.calYear) + "-" + String.valueOf(myGUI.calMonth + 1) + "-" + String.valueOf(myGUI.calLastDate) + " 23:59:59");

            DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
            DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));

            eventCount = 0; //活動數量累計

            if(!show){
            events.clear();
            eventsDad.clear();}
            temp.clear();//整月事件
            tempDad.clear();

            switch (viewMode) {

                case 0: //全部
                    for (int i = 0; i < cals.size(); i++) {

                        boolean isGroup = false;
                        if (View.getGroupCls().contains(cals.get(i))) {
                            isGroup = true;
                        }

                        if (show) {
                            temp.addAll(core.getEvents(cals.get(i), start, end, false));

                            //有多幾個出來要跑Xd(全)
                            for (int j = 0; j < temp.size() - eventCount; j++) {
                                if (isGroup) {
                                    tempDad.add("default");
                                } else {
                                    tempDad.add(cals.get(i));
                                }
                            }
                            eventCount = temp.size();

                            setEventsList(temp);
                        } else {
                            events.addAll(core.getEvents(cals.get(i), start, end, true));

                            //有多幾個出來要跑Xd(全)
                            for (int j = 0; j < events.size() - eventCount; j++) {
                                if (isGroup) {
                                    eventsDad.add("default");
                                } else {
                                    eventsDad.add(cals.get(i));
                                }
                            }
                            eventCount = events.size();

                            markDay(); //重複事件擷取mark才對
                        }
                        //System.out.println(events.size());
                    }
                    break;

                case 1:
                    if (show) {

                        temp.addAll(core.addGroupEvents(start, end));
                        temp.addAll(core.getEvents(View.getSelfId(), start, end, false));
                        setEventsList(temp);
                    } else {
                        events.addAll(core.addGroupEvents(start, end)); //新增內建活動

                        for (int j = 0; j < events.size() - eventCount; j++) {
                            eventsDad.add("default");
                        }
                        eventCount = events.size();

                        events.addAll(core.getEvents(View.getSelfId(), start, end, true));

                        for (int j = 0; j < events.size() - eventCount; j++) {
                            eventsDad.add(View.getSelfId());
                        }
                        eventCount = events.size();

                        markDay(); //重複事件擷取mark才對
                        //System.out.println(events.size());
                    }
                    break;

                case 2:
                    if (show) {
                        temp.addAll(core.addGroupEvents(start, end));
                        temp.addAll(core.getEvents(View.getWorkId(), start, end, false));
                        setEventsList(temp);
                    } else {
                        events.addAll(core.addGroupEvents(start, end)); //新增內建活動

                        for (int j = 0; j < events.size() - eventCount; j++) {
                            eventsDad.add("default");
                        }
                        eventCount = events.size();

                        events.addAll(core.getEvents(View.getWorkId(), start, end, true));

                        for (int j = 0; j < events.size() - eventCount; j++) {
                            eventsDad.add(View.getSelfId());
                        }
                        eventCount = events.size();

                        System.out.println("------------");
                        markDay(); //重複事件擷取mark才對
                        //System.out.println(events.size());
                    }
                    break;
            }

            showInitCal(1);//開始markToday

        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "查詢動作失敗，請檢查網路連線");
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private class ListenForjList implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (myGUI.jList1.getSelectedIndex() >= 0) {
                focusList = myGUI.jList1.getSelectedIndex();
                enableButInfo(true); //被選打開按鈕
            } else {
                enableButInfo(false);
            }

        }

    }

    private class ListenForFeaButtons implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == myGUI.showBut) {
                if (temp.isEmpty()) { //temp為空代表正在一般模式
                    ActivateShow(todayEvents, todayEventsDad);
                } else {
                    ActivateShow(temp, tempDad);
                }
            } else if (e.getSource() == myGUI.addBut) {
                ActivateAdd();
            } else if (e.getSource() == myGUI.updateBut) {
                if (temp.isEmpty()) { //temp為空代表正在一般模式
                    ActivateUpdate(todayEvents, todayEventsDad);
                } else {
                    ActivateUpdate(temp, tempDad);
                }
            } else if (e.getSource() == myGUI.delBut) {
                if (temp.isEmpty()) { //temp為空代表正在一般模式
                    ActivateDel(todayEvents, todayEventsDad);
                } else {
                    ActivateDel(temp, tempDad);
                }
            }
        }

    }

    private void ActivateShow(ArrayList<Event> me, ArrayList<String> dad) {
        myGUI.bottomInfo.setText(me.get(focusList).getSummary());
        core.initEventShow(showEvent, me.get(focusList), dad.get(focusList));
        showEvent.setVisible(true);
    }

    private void ActivateAdd() {
        try {

            Date ss = new Date(); //準備預設時間
            SimpleDateFormat format0 = new SimpleDateFormat("HH:mm:ss");
            String time = format0.format(ss);
            Date inputDate = sdf.parse(String.valueOf(myGUI.calYear) + "-" + String.valueOf(myGUI.calMonth + 1) + "-" + String.valueOf(myGUI.calDayOfMon) + " " + time);
            core.initEventEdit(newEvent, inputDate);
            newEvent.setVisible(true);
        } catch (ParseException ex) {
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void ActivateUpdate(ArrayList<Event> me, ArrayList<String> dad) {
        core.initEventUpdate(newEvent, me.get(focusList), dad.get(focusList));

        try {
            if (me.get(focusList).getAttendees().size() > 0) {
                ArrayList<String> setAtt = new ArrayList<String>();
                for (int i = 0; i < me.get(focusList).getAttendees().size(); i++) {
                    setAtt.add(me.get(focusList).getAttendees().get(i).getEmail() + ",(" + me.get(focusList).getAttendees().get(i).getDisplayName() + " "
                            + me.get(focusList).getAttendees().get(i).getResponseStatus() + ")");
                }
                attendessChooser.setAttList(setAtt);
            }
        } catch (Exception ex) {
        }
        newEvent.setVisible(true);
    }

    private void ActivateDel(ArrayList<Event> me, ArrayList<String> dad) {
        if (dad.get(focusList) != "default") {
            int close = JOptionPane.showConfirmDialog(
                    JOptionPane.getRootFrame(),
                    "確定要刪除「" + me.get(focusList).getSummary() + "」事件嗎？",
                    "請再次確認",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (close == JOptionPane.YES_OPTION) {
                try {
                    core.delEvent(dad.get(focusList), me.get(focusList).getId());
                    showInitCal(0); //初始化內容的樣式
                    loadAllEvents(false);//本月
                    loadEvents();//載入本天
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "刪除動作失敗，請檢查網路連線");
                    Logger
                            .getLogger(CalendarController.class
                                    .getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else {
            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "內定日歷無法移除，若需要請至官方版移除日歷");

        }
    }

    private class ListenForCalOpButtons implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == myGUI.todayBut) {
                myGUI.setToday();
                DateButsHandler.actionPerformed(e);
                focusToday();
            } else if (e.getSource() == myGUI.lYearBut) {
                myGUI.moveMonth(-12);
            } else if (e.getSource() == myGUI.lMonBut) {
                myGUI.moveMonth(-1);
            } else if (e.getSource() == myGUI.nMonBut) {
                myGUI.moveMonth(1);
            } else if (e.getSource() == myGUI.nYearBut) {
                myGUI.moveMonth(12);
            }
            myGUI.curMMYYYYLab.setText("<html><table width=100><tr><th><font size=5>" + (myGUI.calMonth + 1 < 10 ? "&nbsp;" : "") + (myGUI.calMonth + 1) + " / " + myGUI.calYear + "</th></tr></table></html>");

            showInitCal(0);
            loadAllEvents(false);
            loadEvents();
        }
    }

    private class ListenForMenus implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == myGUI.aboutMe) {
                JOptionPane.showMessageDialog(null, "程式開發by Shane @nkust MIS\n心血製作，請取得本人同意使用本程式"
                        + "\ne-mail：shane871112@mis.nkfust.edu.tw\n Ver 1.0");
            } else if (e.getSource() == myGUI.userInfo) {

            } else if (e.getSource() == myGUI.logout) {
                core.logoutAccount();
                System.exit(0);
            } else if (e.getSource() == myGUI.all_Kind) {
                viewMode = 0;
                showInitCal(0); //初始化內容的樣式
                loadAllEvents(false);
                loadEvents();
            } else if (e.getSource() == myGUI.self_Kind) {
                viewMode = 1;
                showInitCal(0); //初始化內容的樣式
                loadAllEvents(false);
                loadEvents();
            } else if (e.getSource() == myGUI.work_Kind) {
                viewMode = 2;
                showInitCal(0); //初始化內容的樣式
                loadAllEvents(false);
                loadEvents();
            } else if (e.getSource() == myGUI.userManager) {
                new MyAttendees.CollaboratorManagement().setVisible(true);
            } else if (e.getSource() == myGUI.showAllEvents) {
                loadAllEvents(true);
            }
        }
    }

    private class ListenForDateButs implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            int k = 0;
            int l = 0;
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 7; j++) {
                    if (e.getSource() == myGUI.dateButs[i][j]) {
                        k = i;
                        l = j;
                    }
                }
            }
            if ((k != 0) || (l != 0)) {
                myGUI.calDayOfMon = myGUI.calDates[k][l];
            }
            myGUI.cal = new GregorianCalendar(myGUI.calYear, myGUI.calMonth, myGUI.calDayOfMon);

            String dDayString = new String();
            int dDay = (int) ((myGUI.cal.getTimeInMillis() - myGUI.today.getTimeInMillis()) / 1000L / 60L / 60L / 24L);
            if ((dDay == 0) && (myGUI.cal.get(Calendar.YEAR) == myGUI.today.get(Calendar.YEAR))
                    && (myGUI.cal.get(Calendar.MONTH) == myGUI.today.get(Calendar.MONTH))
                    && (myGUI.cal.get(Calendar.DAY_OF_MONTH) == myGUI.today.get(Calendar.DAY_OF_MONTH))) {
                dDayString = "Today";
            } else if (dDay >= 0) {
                dDayString = "Day+" + (dDay + 1);
            } else if (dDay < 0) {
                dDayString = "Day -" + dDay * -1;
            }
            myGUI.selectedDate.setText("<Html><font size=3>" + myGUI.calYear + "/" + (myGUI.calMonth + 1) + "/" + myGUI.calDayOfMon + "&nbsp;(" + dDayString + ")</html>");
            loadEvents();

        }
    }

    private class ListenForAttendess implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand() == "確定選擇") {

                core.setAttendessList(attendessChooser.getAttList());
                attendessChooser.setVisible(false);
                newEvent.setMyData(core.getAttendessList());
            }
        }
    }

    private class ListenForEventDiaplay implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getActionCommand() == "新增活動") {

                if (core.addEventEdit()) {
                    showInitCal(0); //初始化內容的樣式
                    loadAllEvents(false);//本月
                    loadEvents();//載入本天
                    newEvent.setVisible(false);
                } else {
                    newEvent.msgbox("錯誤! 格式有誤或連線失敗");
                }

            } else if (e.getActionCommand() == "修改活動") {

                if (core.updateEventEdit(todayEvents.get(focusList))) {
                    showInitCal(0); //初始化內容的樣式
                    loadAllEvents(false);//本月
                    loadEvents();//載入本天
                    newEvent.setVisible(false);
                } else {
                    newEvent.msgbox("錯誤! 格式有誤或連線失敗");
                }
            } else if (e.getActionCommand() == "與會者視窗") {

                attendessChooser.setVisible(true);

            } else if (e.getActionCommand() == "Upload") {

            }

        }
    }

    private void markDay() {

        int start = 0, end = 0;
        String tmp = "";

        for (int i = 0; i < events.size(); i++) {

            for (int j = 0; j < 2; j++) {
                switch (j) {
                    case 0:
                        tmp = events.get(i).getStart().toString();
                        break;
                    case 1:
                        tmp = events.get(i).getEnd().toString();
                        break;
                }

                Date tmpDate = core.getFormatDate(tmp); //格式化不齊的格式

                if (j == 0) {  //轉為比較式
                    start = Integer.parseInt(df2.format(tmpDate) + df.format(tmpDate));
                } else {
                    end = Integer.parseInt(df2.format(tmpDate) + df.format(tmpDate));
                    if (core.getOnlyDate()) {
                        end = start;
                    }
                }

            }

            //開始填粗體上去
            for (int c = 0; c < 6; c++) {
                for (int k = 0; k < 7; k++) {
                    String tmpW = "";
                    if (myGUI.calDates[c][k] < 10) {
                        tmpW = String.valueOf(myGUI.calMonth + 1) + "0" + String.valueOf(myGUI.calDates[c][k]);
                    } else {
                        tmpW = String.valueOf(myGUI.calMonth + 1) + String.valueOf(myGUI.calDates[c][k]);
                    }

                    //System.out.println(myGUI.calDates[c][k]);
                    int runDay = Integer.parseInt(tmpW);
                    if (start <= runDay && runDay <= end) {

                        myGUI.dateButs[c][k].setText("<html><b><font color=" + core.getColor(k) + ">" + myGUI.calDates[c][k] + "</font></b></html>");
                    }
                }
            }

        }

    }

    private void showInitCal(int mode) {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                if (mode == 0) {
                    myGUI.dateButs[i][j].setText("<html><font color=" + core.getColor(j) + ">" + myGUI.calDates[i][j] + "</font></html>");

                    if (myGUI.calDates[i][j] == 0) {
                        myGUI.dateButs[i][j].setVisible(false); //沒有設日期關閉顯示按鈕
                    } else {
                        myGUI.dateButs[i][j].setVisible(true);
                    }

                } else {
                    JLabel todayMark = new JLabel("<html><font color=green>*</html>");
                    myGUI.dateButs[i][j].removeAll();
                    if ((myGUI.calMonth == myGUI.today.get(Calendar.MONTH))
                            && (myGUI.calYear == myGUI.today.get(Calendar.YEAR))
                            && (myGUI.calDates[i][j] == myGUI.today.get(Calendar.DAY_OF_MONTH))) {
                        myGUI.dateButs[i][j].add(todayMark);
                        myGUI.dateButs[i][j].setToolTipText("Today");
                    }

                }
            }
        }
    }

    private void focusToday() {
        if (myGUI.today.get(Calendar.DAY_OF_WEEK) == 1) { //星期日在下一個
            myGUI.dateButs[myGUI.today.get(Calendar.WEEK_OF_MONTH) - 1][(myGUI.today.get(Calendar.DAY_OF_WEEK) - 1)].requestFocusInWindow();
        } else {
            myGUI.dateButs[(myGUI.today.get(Calendar.WEEK_OF_MONTH) - 1)][(myGUI.today.get(Calendar.DAY_OF_WEEK) - 1)].requestFocusInWindow();
        }
    }

    public void setEventsList(ArrayList<Event> lists) {
        myGUI.jList1.setModel(new javax.swing.AbstractListModel<String>() {

            public int getSize() {
                return lists.size();
            }

            public String getElementAt(int i) {

                String tmp1 = "";
                String tmp2 = "";

                for (int j = 0; j < 2; j++) {
                    switch (j) {
                        case 0:
                            tmp1 = lists.get(i).getStart().toString();
                            break;
                        case 1:
                            tmp2 = lists.get(i).getEnd().toString();
                            break;
                    }
                }

                Date StDate = core.getFormatDate(tmp1); //格式化不齊的格式
                Date EdDate = core.getFormatDate(tmp2); //格式化不齊的格式

                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                String timeS = format1.format(StDate);
                String timeE = format1.format(EdDate);

                String showEvent = "";

                if (!core.onlyDate) {
                    showEvent = " " + lists.get(i).getSummary() + " (" + timeS + " - " + timeE + ")";
                } else {
                    showEvent = " " + lists.get(i).getSummary();
                }

                return showEvent;
            }
        });
    }

    public void setEventsList() {
        myGUI.jList1.setModel(new javax.swing.AbstractListModel<String>() {

            public int getSize() {
                return 1;
            }

            public String getElementAt(int i) {
                return " Loading...";
            }
        });
    }

    public void enableButInfo(boolean input) {
        myGUI.showBut.setEnabled(input);
        myGUI.updateBut.setEnabled(input);
        myGUI.delBut.setEnabled(input);
    }

    public static void main(String[] args) {
        new CalendarController();
    }

}
