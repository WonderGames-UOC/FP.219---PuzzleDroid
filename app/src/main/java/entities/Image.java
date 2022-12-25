package entities;

import com.google.gson.annotations.SerializedName;

public class Image {
    @SerializedName("Name")
    private String Name;
    @SerializedName("URL")
    private String url;

    public String getName() {
        return Name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
