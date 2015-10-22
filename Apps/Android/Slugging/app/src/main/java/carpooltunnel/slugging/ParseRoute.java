package carpooltunnel.slugging;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;

@ParseClassName("ParseRoute")
public class ParseRoute extends ParseObject {
    public String getFrom() {
        return getString("from");
    }
    public void setFrom(String value) { put("from", value); }

    public String getTo() {
        return getString("to");
    }
    public void setTo(String value) {
        put("to", value);
    }

    public String getNumPass() {
        return getString("numPass");
    }
    public void setNumPass(String value) {
        put("numPass", value);
    }

    public String getDepTime() {
        return getString("depTime");
    }
    public void setDepTime(String value) {
        put("depTime", value);
    }

    public String getDepDay() {
        return getString("depDay");
    }
    public void setDepDay(String value) {
        put("depDay", value);
    }

    public String getUser() {
        return getString ("user");
    }
    public void setDriverUser(String value) {
        put("user", value);
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public Array getBookers() { return (Array) get("bookers");}
    public void addBooker(String value) { add("booker", value);}


    public static ParseQuery<ParseRoute> getQuery() {
        return ParseQuery.getQuery(ParseRoute.class);
    }
}