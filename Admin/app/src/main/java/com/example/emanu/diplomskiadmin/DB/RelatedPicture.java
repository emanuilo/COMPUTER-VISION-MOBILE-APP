package com.example.emanu.diplomskiadmin.DB;

import java.io.Serializable;

/**
 * Created by emanu on 8/23/2018.
 */

public class RelatedPicture implements Serializable{

    private Integer Id;

    private String pictureBlob;

    public RelatedPicture(Integer id, String pictureBlob) {
        Id = id;
        this.pictureBlob = pictureBlob;
    }

    public RelatedPicture(String pictureBlob) {
        this.pictureBlob = pictureBlob;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getPictureBlob() {
        return pictureBlob;
    }

    public void setPictureBlob(String pictureBlob) {
        this.pictureBlob = pictureBlob;
    }
}
