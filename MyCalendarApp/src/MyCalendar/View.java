package MyCalendar;

import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.util.ArrayList;

/**
 * @author Shane
 */
public class View {

    private static String workId = "";
    private static String selfId = "" ;
    private static ArrayList<String> groupCls = new ArrayList<String>();

    static void header(String name) {
        System.out.println();
        System.out.println("============== " + name + " ==============");
        System.out.println();
    }

    static void display(CalendarList feed) {
        if (feed.getItems() != null) {
            for (CalendarListEntry entry : feed.getItems()) {
                System.out.println();
                System.out.println("-----------------------------------------------");
                display(entry);
            }
        }
    }

    static void display(Events feed) {
        if (feed.getItems() != null) {
            for (Event entry : feed.getItems()) {
                System.out.println();
                System.out.println("-----------------------------------------------");
                display(entry);

            }
        }
    }

    static void display(CalendarListEntry entry) {
        System.out.println("ID: " + entry.getId());
        System.out.println("Summary: " + entry.getSummary());

        if (entry.getSummary().indexOf("Shane@app") != -1) {
            setWorkId(entry.getId());
        } else if (entry.getId().indexOf("group.v.calendar.google.com") != -1) {
            setGroupCls(entry.getId());
        } else{
            setSelfId(entry.getId());
        }

        if (entry.getDescription() != null) {
            System.out.println("Description: " + entry.getDescription());
        }
    }

    static ArrayList<String> displayListEntry(CalendarList feed) {
        if (feed.getItems() != null) {
            ArrayList<String> lists = new ArrayList<String>();

            for (CalendarListEntry entry : feed.getItems()) {
                lists.add(entry.getId());
            }
            return lists;
        }
        return null;
    }

    static ArrayList<Event> displayEvents(Events feed) {

        if (feed.getItems() != null) {
            ArrayList<Event> lists = new ArrayList<Event>();
            for (Event entry : feed.getItems()) {
                lists.add(entry);
                //display(entry);
            }
            return lists;
        }
        return null;
    }

    static void display(Calendar entry) {
        System.out.println("ID: " + entry.getId());
        System.out.println("Summary: " + entry.getSummary());
        if (entry.getDescription() != null) {
            System.out.println("Description: " + entry.getDescription());
        }
    }

    static void display(Event event) {

        if (event.getSummary() != null) {
            System.out.println("Summary: " + event.getSummary());
        }
        if (event.getStart() != null) {
            System.out.println("Start Time: " + event.getStart());
        }
        if (event.getEnd() != null) {
            System.out.println("End Time: " + event.getEnd());
        }
    }

    public static void setWorkId(String id) {
        workId = id;
    }

    public static String getWorkId() {
        return workId;
    }

    public static String getSelfId() {
        return selfId;
    }
    
        public static void setSelfId(String id) {
        selfId = id;
    }

    public static void setGroupCls(String id) {
        groupCls.add(id);
    }

    public static ArrayList<String> getGroupCls() {
        return groupCls;
    }

}
