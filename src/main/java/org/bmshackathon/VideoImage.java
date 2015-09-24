package org.bmshackathon;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class VideoImage {
    @Id
    Long id;
    String imageUrl;

    public VideoImage() {
    }

    public VideoImage(Long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
