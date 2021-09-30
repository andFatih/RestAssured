package POJO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Place {
    private String placename;
    private String longitude;
    private String state;
    private String latitude;
    private String stateabbreviation;

    public String getPlacename() {
        return placename;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getState() {
        return state;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getStateabbreviation() {
        return stateabbreviation;
    }

    @JsonProperty("place name")
    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("state abbreviation")
    public void setStateabbreviation(String stateabbreviation) {
        this.stateabbreviation = stateabbreviation;
    }

    @Override
    public String toString() {
        return "Place{" +
                "placename='" + placename + '\'' +
                ", longitude='" + longitude + '\'' +
                ", state='" + state + '\'' +
                ", latitude='" + latitude + '\'' +
                ", stateabbreviation='" + stateabbreviation + '\'' +
                '}';
    }
}
