package rs.ac.bg.etf.diplomski;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RelatedPicture {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Integer Id;
	
	private String pictureBlob;
	
//	@ManyToOne(fetch = FetchType.EAGER)
//  @JoinColumn(name = "picture_id")
//	private Picture picture;
	
	public RelatedPicture() { }
	
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
