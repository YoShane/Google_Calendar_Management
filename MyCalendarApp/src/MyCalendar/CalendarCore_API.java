package MyCalendar;

import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.TimeChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.TimeChangeEvent;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.google.gson.Gson;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.RadioButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * @author Shane
 */
public class CalendarCore_API {

    /**
     * Be sure to specify the name of your application. If the application name
     * is {@code null} or blank, the application will log a warning. Suggested
     * format is "MyCompany-ProductName/1.0".
     */
    private static final String APPLICATION_NAME = "NKUST-ShaneAPP/1.0";
    public boolean onlyDate = false;

    /**
     * Directory to store user credentials.
     */
    private static final java.io.File DATA_STORE_DIR
            = new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");
    
       
    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to
     * make it a single globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport httpTransport;

    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static com.google.api.services.calendar.Calendar client;

    static final java.util.List<Calendar> addedCalendarsUsingBatch = Lists.newArrayList();

    public CalendarCore_API() {
        try {
            // initialize the transport
            httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // initialize the data store factory
            dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

            // System.out.println(CalendarSample.class.getClass().getResource("/client_secrets.json").getPath());
            // authorization
            Credential credential = authorize();

            // set up global Calendar instance
            client = new com.google.api.services.calendar.Calendar.Builder(
                    httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

            // run commands
            showCalendars();

            // showEvents("u0624011@gm.nkfust.edu.tw") ;
//            addCalendarsUsingBatch();
//            Calendar calendar = addCalendar();
//            updateCalendar(calendar);
//            addEvent(calendar);
//            showEvents(calendar);
//            deleteCalendarsUsingBatch();
//            deleteCalendar(calendar);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    private static Credential authorize() throws Exception {
        // load client secrets
        //請自行更改成自己的API OAuth 2.0 用戶端 ID
        String str = "{\"installed\":{\"client_id\":\"976081279599-4sbjbqa83oemuhthb3qnvjjp2d42r6f8.apps.googleusercontent.com\",\"project_id\":\"my-java-calendarapp\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://oauth2.googleapis.com/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_secret\":\"FT6U4f75IOokmU_zuG4UotaM\",\"redirect_uris\":[\"urn:ietf:wg:oauth:2.0:oob\",\"http://localhost\"]}}";  
        StringReader reader = new StringReader(str);
        
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                    + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
            System.exit(1);
        }
        // set up authorization code flow
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                .build();

        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    public ArrayList<String> getAllList() {
        try {
            CalendarList feed = client.calendarList().list().execute();
            // View.display(feed);
            return View.displayListEntry(feed);
        } catch (IOException ex) {
            Logger.getLogger(CalendarCore_API.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public ArrayList<Event> getEvents(String calendar_id, DateTime timeMin, DateTime timeMax, Boolean single) {
        try {
            Events feed = client.events().list(calendar_id).setTimeMin(timeMin).setTimeMax(timeMax).setSingleEvents(single).execute();
            //View.display(feed);
            return View.displayEvents(feed);
        } catch (IOException ex) {
            Logger.getLogger(CalendarCore_API.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public ArrayList<Event> getEvents(String calendar_id, Boolean single) {
        try {
            Events feed = client.events().list(calendar_id).setSingleEvents(single).execute();
            //View.display(feed);
            return View.displayEvents(feed);
        } catch (IOException ex) {
            Logger.getLogger(CalendarCore_API.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

    }

    public ArrayList<Event> addGroupEvents(DateTime timeMin, DateTime timeMax) {

        ArrayList<Event> newEvents = new ArrayList<Event>();

        for (int i = 0; i < View.getGroupCls().size(); i++) {
            try {
                Events feed = client.events().list(View.getGroupCls().get(i)).setTimeMin(timeMin).setTimeMax(timeMax).execute();
                //View.display(feed);
                newEvents.addAll(View.displayEvents(feed));
            } catch (IOException ex) {
                Logger.getLogger(CalendarCore_API.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        return newEvents;
    }

    private static void showCalendars() throws IOException {
        View.header("Show Calendars");
        CalendarList feed = client.calendarList().list().execute();
        View.display(feed);
    }

    private static Calendar addCalendar() throws IOException {
        View.header("Add Calendar");
        Calendar entry = new Calendar();
        entry.setSummary("Shane@app");
        Calendar result = client.calendars().insert(entry).execute();
        View.display(result);
        return result;
    }

    private static Calendar updateCalendar(Calendar calendar) throws IOException {
        View.header("Update Calendar");
        Calendar entry = new Calendar();
        entry.setSummary("Updated Calendar for Testing");
        Calendar result = client.calendars().patch(calendar.getId(), entry).execute();
        View.display(result);
        return result;
    }

    private static void addEvent(Calendar calendar) throws IOException {
        View.header("Add Event");
        Event event = newEvent();
        Event result = client.events().insert(calendar.getId(), event).execute();
        View.display(result);
    }

    static void delEvent(String calendar, String event) throws IOException {
        View.header("Del Event");
        client.events().delete(calendar, event).execute();
    }

    private static Event newEvent() {
        Event event = new Event();
        event.setSummary("New Event");
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 3600000);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));
        return event;
    }

    private static void showEvents(String calendar_id) throws IOException {
        View.header("Show Events");
        Events feed = client.events().list(calendar_id).execute();
        View.display(feed);
    }

    private static void showEvents(Calendar calendar) throws IOException {
        View.header("Show Events");
        Events feed = client.events().list(calendar.getId()).execute();
        View.display(feed);
    }

    private static void deleteCalendar(Calendar calendar) throws IOException {
        View.header("Delete Calendar");
        client.calendars().delete(calendar.getId()).execute();
    }

    public void logoutAccount() {

        if (CalendarCore_API.DATA_STORE_DIR.isFile() || CalendarCore_API.DATA_STORE_DIR.list().length == 0) {
            CalendarCore_API.DATA_STORE_DIR.delete();
        } else {
            for (File f : CalendarCore_API.DATA_STORE_DIR.listFiles()) {
                f.delete();
            }
            CalendarCore_API.DATA_STORE_DIR.delete(); // 删除文件夹
        }
    }

    public Date getFormatDate(String getW) {
        onlyDate = false; //只有日期
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-MM-dd");

        String tmp = getW;
        tmp = tmp.replace("\"}", "");
        String[] tt = tmp.split("\":\"|\",\"");
        Date tmpDate = new Date();

        //System.out.println(tt[1]);
        try {
            if (tt[1].length() > 10) {
                tmpDate = sdf2.parse(tt[1]);
            } else {
                tmpDate = sdf3.parse(tt[1]);
                onlyDate = true;
            }

        } catch (ParseException ex) {
            Logger.getLogger(CalendarController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tmpDate;
    }

    public Boolean getOnlyDate() {
        return onlyDate;
    }

    public String getColor(int i) {
        String fontColor = "black";
        if (i == 0) {
            fontColor = "red";
        } else if (i == 6) {
            fontColor = "blue";
        }
        return fontColor;
    }

    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    public ArrayList<String> checkWorkCals(ArrayList<String> cals) {
        ArrayList<String> newCals = new ArrayList<String>();

//        for (int i = 0; i < cals.size(); i++) {
//            System.out.println(cals.get(i));
//        }
        if (View.getWorkId() != "") {
            newCals.addAll(cals);
        } else {
            try {
                newCals.addAll(cals);
                View.setWorkId(this.addCalendar().getId());
                newCals.add(View.getWorkId());

                System.out.println("addCalendar_done!");
            } catch (IOException ex) {
                Logger.getLogger(CalendarCore_API.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return newCals;
    }

    ////////////////////////////////////////////////////////////////////Event core
    private DateTimePicker EndDateTimePicker;
    private DateTimePicker StartDateTimePicker;
    private JCheckBox AllDay;
    private JRadioButton Email;
    private JTextArea EventNote;
    private JRadioButton Event_app;
    private JRadioButton Event_private;
    private JButton Inviter;
    private JComboBox<String> InviterShow;
    private JTextField LocationText;
    private JButton OpenFile;
    private JRadioButton Popup;
    private JRadioButton Sms;
    private JComboBox<String> TimeBefore;
    private JTextField TitleText;
    private JComboBox<String> WatchState;
    private JLabel StateLabel;
    private JButton button_Add;

    private ArrayList<String> AttendessList = new ArrayList<String>();

    private String[] watch = new String[]{"default (預設)", "public (活動公開)", "private (只限參與)", "confidential (只限本人)"};
    private String[] pop_time = new String[]{"10分鐘前", "30分鐘前", "1小時前", "6小時前", "1天前", "1禮拜前"};

    public void setAttendessList(ArrayList<String> lists) {
        AttendessList.clear();
        AttendessList.addAll(lists);
    }

    public ArrayList<String> getAttendessList() {
        return AttendessList;
    }

    public void initEventEdit(EventEditShow display, Date startDate) {

        button_Add = display.getButton_Add();
        button_Add.setText("新增活動");
        StateLabel = display.getStateLabel();
        EndDateTimePicker = display.getEndDateTimePicker();
        StartDateTimePicker = display.getStartDateTimePicker();
        AllDay = display.getAllDay();
        Email = display.getEmail();
        EventNote = display.getEventNote();
        Event_app = display.getEvent_app(); //共用事件
        Event_private = display.getEvent_private();
        Event_app.setEnabled(true); //共用事件
        Event_private.setEnabled(true);
        Inviter = display.getInviter();
        InviterShow = display.getInviterShow();
        LocationText = display.getLocationText();
        OpenFile = display.getOpenFile();
        Popup = display.getPopup();
        Sms = display.getSms();
        TimeBefore = display.getTimeBefore();
        TitleText = display.getTitleText();
        WatchState = display.getWatchState();

        Date date = startDate;
        StartDateTimePicker.setDateTimePermissive(CalendarCore_API.convertToLocalDateTimeViaInstant(date));

        date.setHours(startDate.getHours() + 2);
        EndDateTimePicker.setDateTimePermissive(CalendarCore_API.convertToLocalDateTimeViaInstant(date));

        StartDateTimePicker.datePicker.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent event) {
                EndDateTimePicker.datePicker.setDate(StartDateTimePicker.datePicker.getDate());
            }
        });

        //要補時間在後面 ><  好累阿
        StartDateTimePicker.timePicker.addTimeChangeListener(new TimeChangeListener() {
            @Override
            public void timeChanged(TimeChangeEvent event) {
                EndDateTimePicker.timePicker.setTime(StartDateTimePicker.timePicker.getTime().plus(3, ChronoUnit.HOURS));
            }
        }
        );

        AllDay.setSelected(false);
        display.switchAllDay();
        Email.setSelected(false);
        EventNote.setText("");
        Event_app.setSelected(false);
        Event_private.setSelected(false);
        InviterShow.removeAllItems();
        InviterShow.insertItemAt("尚未新增", 0);
        InviterShow.setSelectedIndex(0);
        LocationText.setText("");

        Popup.setSelected(true);
        Sms.setSelected(false);
        TimeBefore.setModel(new javax.swing.DefaultComboBoxModel<>(pop_time));
        TimeBefore.setSelectedIndex(1);

        TitleText.setText("");
        WatchState.setModel(new javax.swing.DefaultComboBoxModel<>(watch));
        StateLabel.setText("");
    }

    public boolean addEventEdit() {

        if (!(TitleText.getText().equals("") || (!Event_app.isSelected() && !Event_private.isSelected()))) {

            StateLabel.setText("正在新增本活動...");

            Event event = new Event();
            event.setSummary(TitleText.getText());

            Date startDate = convertToDateViaInstant(StartDateTimePicker.getDateTimePermissive());
            Date endDate = new Date(startDate.getTime() + 86400000);

            if (AllDay.isSelected()) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String startDateStr = dateFormat.format(startDate);
                String endDateStr = dateFormat.format(endDate);

                // Out of the 6 methods for creating a DateTime object with no time element, only the String version works
                DateTime startDateTime = new DateTime(startDateStr);
                DateTime endDateTime = new DateTime(endDateStr);

                // Must use the setDate() method for an all-day event (setDateTime() is used for timed events)
                EventDateTime startEventDateTime = new EventDateTime().setDate(startDateTime);
                EventDateTime endEventDateTime = new EventDateTime().setDate(endDateTime);

                event.setStart(startEventDateTime);
                event.setEnd(endEventDateTime);
            } else {

                endDate = convertToDateViaInstant(EndDateTimePicker.getDateTimePermissive());
                DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
                event.setStart(new EventDateTime().setDateTime(start));
                DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
                event.setEnd(new EventDateTime().setDateTime(end));

            }

            event.setLocation(LocationText.getText());
            event.setDescription(EventNote.getText());

            String selectW = TimeBefore.getEditor().getItem().toString();
            selectW = selectW.replace("分鐘", "分鐘MMM");
            selectW = selectW.replace("小時", "小時HHH");
            selectW = selectW.replace("天", "天DDD");
            selectW = selectW.replace("禮拜", "禮拜WWW");
            String[] checkW = selectW.split("分鐘|小時|天|禮拜");
            int CheckTime = Integer.parseInt(checkW[0]);

            JRadioButton[] remin = new JRadioButton[3];
            remin[0] = Email;
            remin[1] = Popup;
            remin[2] = Sms;

            ArrayList<EventReminder> reminders = new ArrayList<>();

            for (int i = 0; i < remin.length; i++) {
                if (remin[i].isSelected()) {
                    EventReminder reminder = new EventReminder();

                    if (checkW[1].contains("MMM")) {
                        reminder.setMinutes(CheckTime);
                    } else if (checkW[1].contains("HHH")) {
                        reminder.setMinutes((int) TimeUnit.MINUTES.convert(CheckTime, TimeUnit.HOURS));
                    } else if (checkW[1].contains("DDD")) {
                        reminder.setMinutes((int) TimeUnit.MINUTES.convert(CheckTime, TimeUnit.DAYS));
                    } else if (checkW[1].contains("WWW")) {
                        reminder.setMinutes((int) TimeUnit.MINUTES.convert(CheckTime * 7, TimeUnit.DAYS));
                    }

                    reminder.setMethod(remin[i].getText());

                    reminders.add(reminder);
                }
            }

            Event.Reminders eRem = new Event.Reminders();
            eRem.setOverrides(reminders);
            eRem.setUseDefault(false);
            event.setReminders(eRem);


            if (AttendessList.size()>0) {
                ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
                for(int i = 0 ; i<AttendessList.size();i++){
                    String tmpW = AttendessList.get(i);
                    String []  splitW = tmpW.split(",");
                attendees.add(new EventAttendee().setEmail(splitW[0]));
                }
                event.setAttendees(attendees);
            }

            Event result;
            try {

                View.header("Add Event");

                if (Event_app.isSelected()) {
                    //判斷插入的日曆
                    result = client.events().insert(View.getWorkId(), event).execute();
                } else {
                    result = client.events().insert(View.getSelfId(), event).execute();
                }
                View.display(result);

            } catch (IOException ex) {
                Logger.getLogger(CalendarCore_API.class.getName()).log(Level.SEVERE, null, ex);
                StateLabel.setText("發生問題!(請檢查連線)");
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    public boolean updateEventEdit(Event event) {

        if (!(TitleText.getText().equals("") || (!Event_app.isSelected() && !Event_private.isSelected()))) {

            try {

                StateLabel.setText("正在更新本活動...");

                event.setSummary(TitleText.getText());

                Date startDate = convertToDateViaInstant(StartDateTimePicker.getDateTimePermissive());
                Date endDate = new Date(startDate.getTime() + 86400000);

                if (AllDay.isSelected()) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String startDateStr = dateFormat.format(startDate);
                    String endDateStr = dateFormat.format(endDate);

                    // Out of the 6 methods for creating a DateTime object with no time element, only the String version works
                    DateTime startDateTime = new DateTime(startDateStr);
                    DateTime endDateTime = new DateTime(endDateStr);

                    // Must use the setDate() method for an all-day event (setDateTime() is used for timed events)
                    EventDateTime startEventDateTime = new EventDateTime().setDate(startDateTime);
                    EventDateTime endEventDateTime = new EventDateTime().setDate(endDateTime);

                    event.setStart(startEventDateTime);
                    event.setEnd(endEventDateTime);
                } else {

                    endDate = convertToDateViaInstant(EndDateTimePicker.getDateTimePermissive());
                    DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
                    event.setStart(new EventDateTime().setDateTime(start));
                    DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
                    event.setEnd(new EventDateTime().setDateTime(end));

                }

                event.setLocation(LocationText.getText());
                event.setDescription(EventNote.getText());

                String selectW = TimeBefore.getItemAt(TimeBefore.getSelectedIndex());
                selectW = selectW.replace("分鐘", "分鐘MMM");
                selectW = selectW.replace("小時", "小時HHH");
                selectW = selectW.replace("天", "天DDD");
                selectW = selectW.replace("禮拜", "禮拜WWW");
                String[] checkW = selectW.split("分鐘|小時|天|禮拜");
                int CheckTime = Integer.parseInt(checkW[0]);

                JRadioButton[] remin = new JRadioButton[3];
                remin[0] = Email;
                remin[1] = Popup;
                remin[2] = Sms;

                ArrayList<EventReminder> reminders = new ArrayList<>();

                for (int i = 0; i < remin.length; i++) {
                    if (remin[i].isSelected()) {
                        EventReminder reminder = new EventReminder();

                        if (checkW[1].contains("MMM")) {
                            reminder.setMinutes(CheckTime);
                        } else if (checkW[1].contains("HHH")) {
                            reminder.setMinutes((int) TimeUnit.MINUTES.convert(CheckTime, TimeUnit.HOURS));
                        } else if (checkW[1].contains("DDD")) {
                            reminder.setMinutes((int) TimeUnit.MINUTES.convert(CheckTime, TimeUnit.DAYS));
                        } else if (checkW[1].contains("WWW")) {
                            reminder.setMinutes((int) TimeUnit.MINUTES.convert(CheckTime * 7, TimeUnit.DAYS));
                        }

                        reminder.setMethod(remin[i].getText());

                        reminders.add(reminder);
                    }
                }

                Event.Reminders eRem = new Event.Reminders();
                eRem.setOverrides(reminders);
                eRem.setUseDefault(false);
                event.setReminders(eRem);

                Event result;
                View.header("Edit Event");
                if (Event_app.isSelected()) {
                    //判斷插入的日曆
                    result = client.events().update(View.getWorkId(), event.getId(), event).execute();
                } else{
                     result = client.events().update(View.getSelfId(), event.getId(), event).execute();
                }
                View.display(result);

                return true;

            } catch (IOException ex) {
                Logger.getLogger(CalendarCore_API.class.getName()).log(Level.SEVERE, null, ex);
                StateLabel.setText("發生問題!(請檢查連線或設定)");
                return false;
            }

        } else {
            return false;
        }

    }

    public void initEventUpdate(EventEditShow display, Event event, String dadId) {

        button_Add = display.getButton_Add();
        button_Add.setText("修改活動");
        StateLabel = display.getStateLabel();
        EndDateTimePicker = display.getEndDateTimePicker();
        StartDateTimePicker = display.getStartDateTimePicker();
        AllDay = display.getAllDay();
        Email = display.getEmail();
        EventNote = display.getEventNote();
        Event_app = display.getEvent_app(); //共用事件
        Event_private = display.getEvent_private();
        Inviter = display.getInviter();
        InviterShow = display.getInviterShow();
        LocationText = display.getLocationText();
        OpenFile = display.getOpenFile();
        Popup = display.getPopup();
        Sms = display.getSms();
        TimeBefore = display.getTimeBefore();
        TitleText = display.getTitleText();
        WatchState = display.getWatchState();

        Event_app.setEnabled(false); //共用事件
        Event_private.setEnabled(false);

        Date date = this.getFormatDate(event.getStart().toString());
        StartDateTimePicker.setDateTimePermissive(CalendarCore_API.convertToLocalDateTimeViaInstant(date));

        date = this.getFormatDate(event.getEnd().toString());
        EndDateTimePicker.setDateTimePermissive(CalendarCore_API.convertToLocalDateTimeViaInstant(date));

        if (!onlyDate) {
            AllDay.setSelected(false);
        } else {
            AllDay.setSelected(true);
        }

        StartDateTimePicker.datePicker.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent event) {
                EndDateTimePicker.datePicker.setDate(StartDateTimePicker.datePicker.getDate());
            }
        });

        //要補時間在後面 ><  好累阿
        StartDateTimePicker.timePicker.addTimeChangeListener(new TimeChangeListener() {
            @Override
            public void timeChanged(TimeChangeEvent event) {
                EndDateTimePicker.timePicker.setTime(StartDateTimePicker.timePicker.getTime().plus(3, ChronoUnit.HOURS));
            }
        }
        );

        display.switchAllDay();

        Popup.setSelected(false);
        Sms.setSelected(false);
        Email.setSelected(false);

        try {
            for (int i = 0; i < event.getReminders().getOverrides().size(); i++) {
//            System.out.println(event.getReminders().getOverrides().get(i).getMethod());
//            System.out.println(event.getReminders().getOverrides().get(i).getMinutes());

                if (event.getReminders().getOverrides().get(i).getMethod().contains("popup")) {
                    Popup.setSelected(true);
                } else if (event.getReminders().getOverrides().get(i).getMethod().contains("email")) {
                    Email.setSelected(true);
                } else if (event.getReminders().getOverrides().get(i).getMethod().contains("sms")) {
                    Sms.setSelected(true);
                }
                pop_time[0] = convertMinute2Str(event.getReminders().getOverrides().get(i).getMinutes());//老師 不好意思 趕時間 真的偷懶阿...
            }
            
             InviterShow.removeAllItems();
        InviterShow.insertItemAt("尚未新增", 0);
        InviterShow.setSelectedIndex(0);
        
            ArrayList<String> setAtt = new  ArrayList<String>();
             for (int i = 0; i < event.getAttendees().size();i++){
                 setAtt.add(event.getAttendees().get(i).getEmail()+",("+event.getAttendees().get(i).getDisplayName()+" "
                         +event.getAttendees().get(i).getResponseStatus()+")");
             }
             if(event.getAttendees().size()>0){
                 InviterShow.setModel(new DefaultComboBoxModel(setAtt.toArray()));
             }
        } catch (Exception ex) {
            //就是意外阿 能怎辦 沒有時間分析了 = =
        }

        TimeBefore.setModel(new javax.swing.DefaultComboBoxModel<>(pop_time));
        TimeBefore.setSelectedIndex(0);

        if (dadId.equals(View.getSelfId())) {
            Event_private.setSelected(true);
        } else {
            Event_app.setSelected(true);
        }

        EventNote.setText(event.getDescription());
       
        LocationText.setText(event.getLocation());

        TitleText.setText(event.getSummary());
        WatchState.setModel(new javax.swing.DefaultComboBoxModel<>(watch));
        StateLabel.setText("");
    }

    public void initEventShow(EventShow showEvent, Event event, String dadId) {

        JCheckBox AllDay_show = showEvent.getAllDay();
        JRadioButton Email_show = showEvent.getEmail();
        JTextArea EventNote_show = showEvent.getEventNote();
        JRadioButton Event_app_show = showEvent.getEvent_app();
        JRadioButton Event_private_show = showEvent.getEvent_private();
        JComboBox<String> Inviter_show = showEvent.getInviterShow();
        JLabel LocationText_show = showEvent.getMyLocation();
        JLabel OpenFile_show = showEvent.getOpenDataLink();
        JRadioButton Popup_show = showEvent.getPopup();
        JRadioButton Sms_show = showEvent.getSms();
        JComboBox<String> TimeBefore_show = showEvent.getTimeBefore();
        JLabel TitleText_show = showEvent.getEventName();
        JComboBox<String> WatchState_show = showEvent.getWatchState();
        JLabel StateLabel_show = showEvent.getStateLabel();
        JLabel LocationLabel = showEvent.getLocationLabel();

        JLabel EndTime = showEvent.getEndTime();
        JLabel StartTime = showEvent.getStartTime();

        LocationLabel.setVisible(false);
        LocationText_show.setVisible(false);
        OpenFile_show.setVisible(false);

        TitleText_show.setText(event.getSummary());

        LocationLabel.setVisible(true);
        LocationText_show.setVisible(true);
        LocationText_show.setText(event.getLocation());

        EventNote_show.setText(event.getDescription());

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy年MM月dd日 ahh時mm分");
        Date date = this.getFormatDate(event.getStart().toString());
        StartTime.setText(dateFormatter.format(date));

        date = this.getFormatDate(event.getEnd().toString());
        EndTime.setText(dateFormatter.format(date));

        if (!onlyDate) {
            AllDay_show.setSelected(false);
        } else {
            AllDay_show.setSelected(true);
        }

            Inviter_show.removeAllItems();
        Inviter_show.insertItemAt("尚未新增", 0);
        Inviter_show.setSelectedIndex(0);
        
        Popup_show.setSelected(false);
        Sms_show.setSelected(false);
        Email_show.setSelected(false);

        try {
            for (int i = 0; i < event.getReminders().getOverrides().size(); i++) {
//            System.out.println(event.getReminders().getOverrides().get(i).getMethod());
//            System.out.println(event.getReminders().getOverrides().get(i).getMinutes());

                if (event.getReminders().getOverrides().get(i).getMethod().contains("popup")) {
                    Popup_show.setSelected(true);
                } else if (event.getReminders().getOverrides().get(i).getMethod().contains("email")) {
                    Sms_show.setSelected(true);
                } else if (event.getReminders().getOverrides().get(i).getMethod().contains("sms")) {
                    Email_show.setSelected(true);
                }
                pop_time[0] = convertMinute2Str(event.getReminders().getOverrides().get(i).getMinutes());//老師 不好意思 趕時間 真的偷懶阿...
            }
            
            ArrayList<String> setAtt = new  ArrayList<String>();
             for (int i = 0; i < event.getAttendees().size();i++){
                 setAtt.add(event.getAttendees().get(i).getEmail()+",("+event.getAttendees().get(i).getDisplayName()+" "
                         +event.getAttendees().get(i).getResponseStatus()+")");
             }
             if(event.getAttendees().size()>0)Inviter_show.setModel(new DefaultComboBoxModel(setAtt.toArray()));
        } catch (Exception ex) {
            //就是意外阿 能怎辦 沒有時間分析了 = =
        }

 
        TimeBefore_show.setModel(new javax.swing.DefaultComboBoxModel<>(pop_time));
        TimeBefore_show.setSelectedIndex(0);

        if (dadId.equals(View.getSelfId())) {
            Event_private_show.setSelected(true);
        } else {
            Event_app_show.setSelected(true);
        }

   
        WatchState_show.setModel(new javax.swing.DefaultComboBoxModel<>(watch));
        StateLabel_show.setText("");

    }


    ///時間格式化
    private static final String[] UNIT_DESC = new String[]{"天前", "小時前", "分鐘前", "秒前"};

    public static String convertMinute2Str(long minute) {
        StringBuilder sb = new StringBuilder();
        long[] date = {TimeUnit.SECONDS.toHours(minute) % 24, TimeUnit.SECONDS.toMinutes(minute) % 60, TimeUnit.SECONDS.toSeconds(minute) % 60};
        for (int i = 0; i < date.length; i++) {
            long l = date[i];
            if (l > 0) {
                sb.append(l).append(UNIT_DESC[i]);
            }
        }
        return sb.toString();
    }

}
