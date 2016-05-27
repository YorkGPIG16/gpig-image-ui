package gpig.group2.imageui.model;

public class StrandedPersonImage extends StrandedPersonPoi {

	private int id;
	private boolean yes;
	private boolean no;
	private StrandedPersonPoi original;
	
	public StrandedPersonImage() {
		
	}
	
	public StrandedPersonImage(StrandedPersonPoi spp) {
		original = spp;
		setImageLoc(spp.getImageLoc());
		setImageUrl(spp.getImageUrl());
	}

	
	public StrandedPersonPoi getOriginal() {
	
		return original;
	}

	public int getId() {

		return id;
	}

	public void setId(int id) {

		this.id = id;
	}

	public boolean isYes() {

		return yes;
	}

	public void setYes(boolean yes) {

		this.yes = yes;
	}

	public boolean isNo() {

		return no;
	}

	public void setNo(boolean no) {

		this.no = no;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		StrandedPersonImage other = (StrandedPersonImage) obj;
		if (id != other.id)
			return false;
		return true;
	}
}