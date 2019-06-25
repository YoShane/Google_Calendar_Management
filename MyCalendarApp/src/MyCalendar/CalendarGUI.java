/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyCalendar;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author shane
 */
public class CalendarGUI extends CalendarDateManager {

    JFrame mainFrame;
    JMenuBar menuBar;
    JMenu userItem, chooseKind, operating, info;
    JMenuItem userInfo, logout, all_Kind, self_Kind, work_Kind;
    JMenuItem userManager, showAllEvents,aboutMe;

    JPanel calOpPanel;
    JButton todayBut;
    JLabel todayLab;
    JButton lYearBut;
    JButton lMonBut;
    JLabel curMMYYYYLab;
    JButton nMonBut;
    JButton nYearBut;

    JPanel calPanel;
    JButton[] weekDaysName;
    JButton[][] dateButs = new JButton[6][7];

    JPanel infoPanel;
    JLabel infoClock;
    JPanel eventsPanel;
    JLabel selectedDate;
    JList<String> jList1;
    JScrollPane jListSP;
    JPanel eventActPanel;
    JButton showBut;
    JButton addBut;
    JButton updateBut;
    JButton delBut;
    JPanel frameBottomPanel;
    JLabel bottomInfo = new JLabel("Welcome to my Calendar!");
    JSplitPane split;

    final String[] WEEK_DAY_NAME = {"SUN", "MON", "TUE", "WED", "THR", "FRI", "SAT"};

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new CalendarGUI();
//            }
//        });
//    }
    public CalendarGUI() {

        mainFrame = new JFrame("MyCalendar");
        mainFrame.setDefaultCloseOperation(3);
        mainFrame.setSize(950, 600);
        mainFrame.setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            SwingUtilities.updateComponentTreeUI(mainFrame);
        } catch (Exception e) {
            bottomInfo.setText("ERROR : LookAndFeel setting failed");
        }

        menuBar = new JMenuBar();

        userItem = new JMenu("帳戶");
        menuBar.add(userItem);

        chooseKind = new JMenu("日曆");
        menuBar.add(chooseKind);

        operating = new JMenu("功能");
        menuBar.add(operating);

        info = new JMenu("關於");
        menuBar.add(info);

        userInfo = new JMenuItem("帳戶");
        userItem.add(userInfo);

        logout = new JMenuItem("登出");
        userItem.add(logout);

        all_Kind = new JMenuItem("所有");
        chooseKind.add(all_Kind);

        self_Kind = new JMenuItem("私人");
        chooseKind.add(self_Kind);

        work_Kind = new JMenuItem("工作/其它");
        chooseKind.add(work_Kind);

        userManager = new JMenuItem("與會者管理");
        operating.add(userManager);

        showAllEvents = new JMenuItem("本月所有活動");
        operating.add(showAllEvents);
        
        aboutMe = new JMenuItem("關於本程式");
        info.add(aboutMe);

        mainFrame.setJMenuBar(menuBar);

        calOpPanel = new JPanel();
        todayBut = new JButton("Today");
        todayBut.setToolTipText("Today");
        todayLab = new JLabel(today.get(Calendar.YEAR) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH));
        lYearBut = new JButton("<<");
        lYearBut.setToolTipText("Previous Year");
        lMonBut = new JButton("<");
        lMonBut.setToolTipText("Previous Month");
        curMMYYYYLab = new JLabel("<html><table width=100><tr><th><font size=5>" + calYear + " / " + (calMonth + 1 < 10 ? "&nbsp;" : "") + (calMonth + 1) + "</th></tr></table></html>");
        nMonBut = new JButton(">");
        nMonBut.setToolTipText("Next Month");
        nYearBut = new JButton(">>");
        nYearBut.setToolTipText("Next Year");
        calOpPanel.setLayout(new GridBagLayout());
        GridBagConstraints calOpGC = new GridBagConstraints();
        calOpGC.gridx = 1;
        calOpGC.gridy = 1;
        calOpGC.gridwidth = 2;
        calOpGC.gridheight = 1;
        calOpGC.weightx = 1.0D;
        calOpGC.weighty = 1.0D;
        calOpGC.insets = new Insets(5, 5, 0, 0);
        calOpGC.anchor = 17;
        calOpGC.fill = 0;
        calOpPanel.add(todayBut, calOpGC);
        calOpGC.gridwidth = 3;
        calOpGC.gridx = 2;
        calOpGC.gridy = 1;
        calOpPanel.add(todayLab, calOpGC);
        calOpGC.anchor = 10;
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 1;
        calOpGC.gridy = 2;
        calOpPanel.add(lYearBut, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 2;
        calOpGC.gridy = 2;
        calOpPanel.add(lMonBut, calOpGC);
        calOpGC.gridwidth = 2;
        calOpGC.gridx = 3;
        calOpGC.gridy = 2;
        calOpPanel.add(curMMYYYYLab, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 5;
        calOpGC.gridy = 2;
        calOpPanel.add(nMonBut, calOpGC);
        calOpGC.gridwidth = 1;
        calOpGC.gridx = 6;
        calOpGC.gridy = 2;
        calOpPanel.add(nYearBut, calOpGC);

        calPanel = new JPanel();

        weekDaysName = new JButton[7]; //第一行
        for (int i = 0; i < 7; i++) {
            weekDaysName[i] = new JButton(WEEK_DAY_NAME[i]);
            weekDaysName[i].setBorderPainted(false);
            weekDaysName[i].setContentAreaFilled(false);
            weekDaysName[i].setForeground(Color.WHITE);
            if (i == 0) {
                weekDaysName[i].setBackground(new Color(200, 50, 50)); //星期日
            } else if (i == 6) {
                weekDaysName[i].setBackground(new Color(50, 100, 200));
            } else {
                weekDaysName[i].setBackground(new Color(150, 150, 150));
            }
            weekDaysName[i].setOpaque(true);
            weekDaysName[i].setFocusPainted(false);
            calPanel.add(weekDaysName[i]);
        }
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                dateButs[i][j] = new JButton();
                dateButs[i][j].setBorderPainted(false);
                dateButs[i][j].setContentAreaFilled(false);
                dateButs[i][j].setBackground(Color.WHITE);
                dateButs[i][j].setOpaque(true);
                calPanel.add(dateButs[i][j]);
            }
        }
        calPanel.setLayout(new GridLayout(0, 7, 2, 2));
        calPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        // showCal();

        infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout());
        infoClock = new JLabel("", 4);
        infoClock.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoPanel.add(infoClock, "North");
        selectedDate = new JLabel("<Html><font size=3>" + today.get(Calendar.YEAR) + "/" + (today.get(Calendar.MONTH) + 1) + "/" + today.get(Calendar.DAY_OF_MONTH) + "&nbsp;(Today)</html>", 2);
        selectedDate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        eventsPanel = new JPanel();
        eventsPanel.setBorder(BorderFactory.createTitledBorder("Events"));

        jList1 = new javax.swing.JList<>();
        jList1.setFont(new java.awt.Font("Microsoft YaHei", 0, 22));
        jList1.setFixedCellHeight(45);
        jList1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        jListSP = new JScrollPane(jList1);
        Dimension jListSPSize = jListSP.getPreferredSize();
        jListSPSize.height = 80;
        jListSP.setPreferredSize(jListSPSize);

        eventActPanel = new JPanel();
        showBut = new JButton("顯示更多");
        showBut.setFont(new java.awt.Font("Microsoft YaHei", 0, 18));

        addBut = new JButton("新增");
        addBut.setFont(new java.awt.Font("Microsoft YaHei", 0, 18));

        updateBut = new JButton("更新");
        updateBut.setFont(new java.awt.Font("Microsoft YaHei", 0, 18));

        delBut = new JButton("刪除");
        delBut.setFont(new java.awt.Font("Microsoft YaHei", 0, 18));

        eventActPanel.add(showBut);
        eventActPanel.add(addBut);
        eventActPanel.add(updateBut);
        eventActPanel.add(delBut);
        eventsPanel.setLayout(new BorderLayout());
        eventsPanel.add(selectedDate, "North");
        eventsPanel.add(jListSP, "Center");
        eventsPanel.add(eventActPanel, "South");

        JPanel frameSubPanelWest = new JPanel();
        Dimension calOpPanelSize = calOpPanel.getPreferredSize();
        calOpPanelSize.height = 80;
        calOpPanel.setPreferredSize(calOpPanelSize);
        frameSubPanelWest.setLayout(new BorderLayout());
        frameSubPanelWest.add(calOpPanel, "North");
        frameSubPanelWest.add(calPanel, "Center");

        JPanel frameSubPanelEast = new JPanel();
        frameSubPanelEast.setLayout(new BorderLayout());
        frameSubPanelEast.add(infoPanel, "North");
        frameSubPanelEast.add(eventsPanel, "Center");

        Dimension frameSubPanelWestSize = frameSubPanelWest.getPreferredSize();
        frameSubPanelWestSize.width = 550;
        frameSubPanelWest.setPreferredSize(frameSubPanelWestSize);

        frameBottomPanel = new JPanel();
        frameBottomPanel.add(bottomInfo);

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(frameSubPanelWest, "West");
        mainFrame.add(frameSubPanelEast, "Center");
        mainFrame.add(frameBottomPanel, "South");
        mainFrame.setVisible(true);

        CalendarGUI.ThreadConrol threadCnl = new CalendarGUI.ThreadConrol();
        threadCnl.start();
    }

    public JPanel getOpPanel() {
        return calOpPanel;
    }
    
    public JMenuBar getMenuBar(){
    return menuBar;
}

   

    private class ThreadConrol extends Thread {

        private ThreadConrol() {
        }

        public void run() {
            boolean msgCntFlag = false;
            int num = 0;
            String curStr = new String();
            try {
                for (;;) {
                    CalendarGUI.this.today = Calendar.getInstance();
                    String amPm = CalendarGUI.this.today.get(9) == 0 ? "AM" : "PM";
                    String hour;

                    if (CalendarGUI.this.today.get(10) == 0) {
                        hour = "12";
                    } else {
                        if (CalendarGUI.this.today.get(10) == 12) {
                            hour = " 0";
                        } else {
                            hour = (CalendarGUI.this.today.get(10) < 10 ? " " : "") + CalendarGUI.this.today.get(10);
                        }
                    }
                    String min = (CalendarGUI.this.today.get(12) < 10 ? "0" : "") + CalendarGUI.this.today.get(12);
                    String sec = (CalendarGUI.this.today.get(13) < 10 ? "0" : "") + CalendarGUI.this.today.get(13);
                    CalendarGUI.this.infoClock.setText(amPm + " " + hour + ":" + min + ":" + sec);

                    sleep(1000L);
                    String infoStr = CalendarGUI.this.bottomInfo.getText();
                    if ((infoStr != " ") && ((!msgCntFlag) || (curStr != infoStr))) {
                        num = 5;
                        msgCntFlag = true;
                        curStr = infoStr;
                    } else if ((infoStr != " ") && (msgCntFlag)) {
                        if (num > 0) {
                            num--;
                        } else {
                            msgCntFlag = false;
                            CalendarGUI.this.bottomInfo.setText(" ");
                        }
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Thread:Error");
            }
        }
    }
}
