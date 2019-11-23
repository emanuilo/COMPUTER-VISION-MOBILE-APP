package com.example.emanu.diplomskiadmin.DB;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;

import java.io.Serializable;
import java.util.List;

/**
 * Created by emanu on 8/23/2018.
 */

public class Picture implements Serializable{
    private Integer id;

    private Integer registerNumber;

//    private String title;
//
//    private String artist;
//
//    private String description;
//
//    private String htmlDescription;

    private String videoId;

    private String pictureBlob;

    private List<RelatedPicture> relatedPictures;

    private List<Content> contentList;

    private Mat imgObject;

    private MatOfKeyPoint keypointsObject;

    private Mat descriptorsObject;

    public Picture() { }

    public Picture(String videoId, String pictureBlob, List<RelatedPicture> relatedPictures, List<Content> contentList) {
        this.videoId = videoId;
        this.pictureBlob = pictureBlob;
        this.relatedPictures = relatedPictures;
        this.contentList = contentList;
    }

    public void addRelatedPicture(RelatedPicture relatedPicture){
        relatedPictures.add(relatedPicture);
    }

    public int removeRelatedPicture(int position){
        int id = relatedPictures.get(position).getId();
        relatedPictures.remove(position);

        return id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPictureBlob() {
        return pictureBlob;
    }

    public void setPictureBlob(String pictureBlob) {
        this.pictureBlob = pictureBlob;
    }

    public Mat getImgObject() {
        return imgObject;
    }

    public void setImgObject(Mat imgObject) {
        this.imgObject = imgObject;
    }

    public MatOfKeyPoint getKeypointsObject() {
        return keypointsObject;
    }

    public void setKeypointsObject(MatOfKeyPoint keypointsObject) {
        this.keypointsObject = keypointsObject;
    }

    public Mat getDescriptorsObject() {
        return descriptorsObject;
    }

    public void setDescriptorsObject(Mat descriptorsObject) {
        this.descriptorsObject = descriptorsObject;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public List<RelatedPicture> getRelatedPictures() {
        return relatedPictures;
    }

    public void setRelatedPictures(List<RelatedPicture> relatedPictures) {
        this.relatedPictures = relatedPictures;
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public Integer getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(Integer registerNumber) {
        this.registerNumber = registerNumber;
    }
}
