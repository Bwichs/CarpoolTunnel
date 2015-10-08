package carpooltunnel.slugging;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Routes")
public class ParseRoute extends ParseObject {

    public String getFrom() {
        return getString("from");
    }
    public void setFrom(String value) {
        put("from", value);
    }

    public String getTo() {
        return getString("to");
    }
    public void setTo(String value) {
        put("to", value);
    }

    public int getNumPass() {
        return getInt("numPass");
    }
    public void setNumPass(int value) {
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

    ParseUser user = ParseUser.getCurrentUser();
    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }



    public static ParseQuery<ParseRoute> getQuery() {
        return ParseQuery.getQuery(ParseRoute.class);
    }
}