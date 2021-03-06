package carpooltunnel.slugging;


import com.parse.ParseObject;

public class PassengerRouteClass {

    private String routeId;
    private String depDay;
    private String depTime;
    private String from;
    private String numPass;
    private String to;
    private String driverUser;
    private String createdAt;
    private String updatedAt;
    private String booker;

    public String getBooker() { return booker;}
    public void setBooker(String r){ this.booker = r;}

    public String getRouteId() { return routeId; }
    public void setRouteId(String r) {this.routeId = r;}
    public String getDepDay() { return depDay; }
    public void setDepDay(String depDay) { this.depDay = depDay; }
    public String getDepTime() { return depTime; }
    public void setDepTime(String depTime) { this.depTime = depTime; }
    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getNumPass() { return numPass; }
    public void setNumPass(String numPass) { this.numPass = numPass; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public String getDriverUser() { return driverUser; }
    public void setDriverUser(String driverUser) { this.driverUser = driverUser; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

}
