package gpig.group2.imageui.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;

import gpig.group2.imageui.model.StrandedPersonImage;
import gpig.group2.imageui.util.Utils;
import gpig.group2.models.drone.response.Image;
import gpig.group2.models.drone.response.ResponseData;

@Service
public class ImageVerificationService {

	private List<StrandedPersonImage> images = new LinkedList<>();

	public synchronized void addPois(ResponseData pois) {

		for (Image img : pois.getImagesX()) {
			StrandedPersonImage spImg = new StrandedPersonImage();
			spImg.setImageUrl(img.getUrlX());
			spImg.setImageLoc(pois.getOriginX());
			Utils.unshift(images, spImg);
		}
	}

	public synchronized StrandedPersonImage getNextImg() {

		return Utils.pop(images);
	}
}
