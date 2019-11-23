package rs.ac.bg.etf.diplomski;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class Picture {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer id;
	
	private Integer registerNumber;
	
//	private String title;
//	
//	private String artist;
//	
//	private String description;
//	
//	@Column(columnDefinition="TEXT")
//	private String htmlDescription;
	
	private String pictureBlob;
	
	private String videoId;
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "picture_id")
	private List<RelatedPicture> relatedPictures = new ArrayList<>();
	
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinColumn(name = "picture_id")
	private List<Content> contentList = new ArrayList<>();
	
//	@ManyToMany
//	private List<Exhibition> exhibitions = new ArrayList<>(); 

	public void addRelatedPicture(RelatedPicture relatedPicture) {
		relatedPictures.add(relatedPicture);
	}
	
	public void addContent(Content content) {
		contentList.add(content);
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

//	public List<Exhibition> getExhibitions() {
//		return exhibitions;
//	}
//
//	public void setExhibitions(List<Exhibition> exhibitions) {
//		this.exhibitions = exhibitions;
//	}
	
	
}
