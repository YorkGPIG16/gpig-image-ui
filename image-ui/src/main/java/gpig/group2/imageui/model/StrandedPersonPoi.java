package gpig.group2.imageui.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import gpig.group2.maps.geographic.Point;

@XmlRootElement
public class StrandedPersonPoi {
	
	@XmlElement
	private String imageUrl;
	
	@XmlElement
	private Point imageLoc;

	@XmlElement
	private Integer taskId;

	public String getImageUrl() {

		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {

		this.imageUrl = imageUrl;
	}

	public Point getImageLoc() {

		return imageLoc;
	}

	public void setImageLoc(Point imageLoc) {

		this.imageLoc = imageLoc;
	}

	public void setTaskId(int id) {
		this.taskId = id;
	}

	@XmlTransient
	public Integer getTaskId() {
		return this.taskId;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((imageLoc == null) ? 0 : imageLoc.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StrandedPersonPoi other = (StrandedPersonPoi) obj;
		if (imageLoc == null) {
			if (other.imageLoc != null)
				return false;
		} else if (!imageLoc.equals(other.imageLoc))
			return false;
		if (imageUrl == null) {
			if (other.imageUrl != null)
				return false;
		} else if (!imageUrl.equals(other.imageUrl))
			return false;
		return true;
	}
}
