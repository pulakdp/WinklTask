
package io.github.pulakdp.winkltask.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PhotoResponse {

    @SerializedName("photos")
    @Expose
    private Photos photos;

    @SerializedName("stat")
    private String stat;

    public Photos getPhotos() {
        return photos;
    }

    public static class Photos {

        @SerializedName("page")
        @Expose
        private long page;

        @SerializedName("pages")
        @Expose
        private long pages;

        @SerializedName("perpage")
        @Expose
        private long perpage;

        @SerializedName("total")
        @Expose
        private String total;

        @SerializedName("photo")
        @Expose
        private List<Photo> photos;

        public List<Photo> getPhotoList() {
            return photos;
        }
    }

    public static class Photo implements Serializable {

        @SerializedName("id")
        @Expose
        private String id;

        @SerializedName("owner")
        private String owner;

        @SerializedName("secret")
        @Expose
        private String secret;

        @SerializedName("server")
        @Expose
        private String server;

        @SerializedName("farm")
        @Expose
        private long farm;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("ispublic")
        private long ispublic;

        @SerializedName("isfriend")
        private long isfriend;

        @SerializedName("isfamily")
        private long isfamily;

        public String getTitle() {
            return title;
        }

        public String getPhotoUrl() {
            return "https://farm" +
                    farm +
                    ".staticflickr.com/" +
                    server +
                    "/" +
                    id +
                    "_" +
                    secret +
                    "_b.jpg";
        }
    }
}
