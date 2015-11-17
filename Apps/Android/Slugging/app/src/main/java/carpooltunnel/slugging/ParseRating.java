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

    public int getThumbsUpCount(){ return Array.getLength(getThumbsUp());}
    public int getThumbsDownCount(){ return Array.getLength(getThumbsDown());}
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

/***

 Goes Into User Profile (Pseudocode)

 ParseUser pUser; //Profile's User
 ParseRating pRating = pUser.get("ParseRating");
 int thumbsUp = pRating.getThumbsUpCount();
 int thumbsDown = pRating.getThumbsDownCount();

 On Thumbs Up Click:

 if pRating.thumbsUp Contains currentUserName{
 continue;
 }
 else if pRating.thumbsDown Contains currentUserName{
 List<String> remUser = Array.asList(currentUserName);
 pRating.removeAll("thumbsDown", remUser);
 pRating.setThumbsUp(currentUserName);
 pRating.setRating();
 }
 else{
 pRating.setThumbsUp(currentUserName);
 pRating.setRating();
 }

 On Thumbs Down Click:

 if pRating.thumbsDown Contains currentUserName{
 continue;
 }
 else if pRating.thumbsUp Contains currentUserName{
 List<String> remUser = Array.asList(currentUserName);
 pRating.removeAll("thumbsUp", remUser);
 pRating.setThumbsDown(currentUserName);
 pRating.setRating();
 }
 else{
 pRating.setThumbsDown(currentUserName);
 pRating.setRating();
 }


 **/
