package carpooltunnel.slugging;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import java.lang.reflect.Array;

@ParseClassName("ParseRating")
public class ParseRating extends ParseObject {
    public Array getThumbsUp() {
        return (Array) get("thumbsUp");
    }
    public void setThumbsUp(String value) { add("thumbsUp", value); }

    public Array getThumbsDown() {
        return (Array) get("thumbsDown");
    }
    public void setThumbsDown(String value) { add("thumbsDown", value); }

    public double getRating(){
        int thumbsUp = Array.getLength(getThumbsUp());
        int thumbsDown = Array.getLength(getThumbsDown());
        int total = thumbsUp+thumbsDown;
        double rating = (thumbsUp/total)*100;

        return rating;
    }
    public void setRating(){
        put("rating", getRating());
    }


    public static ParseQuery<ParseRating> getQuery() {
        return ParseQuery.getQuery(ParseRating.class);
    }
}