package gpig.group2.imageui.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import gpig.group2.imageui.model.StrandedPersonImage;
import gpig.group2.imageui.model.StrandedPersonPoi;
import gpig.group2.imageui.util.Utils;
import gpig.group2.maps.geographic.Point;
import gpig.group2.model.sensor.StrandedPerson;
import gpig.group2.models.alerts.Alert;
import gpig.group2.models.alerts.AlertMessage;
import gpig.group2.models.alerts.Priority;
import gpig.group2.models.drone.response.Image;
import gpig.group2.models.drone.response.ResponseData;

@Service
public class ImageVerificationService {

	// TODO GET URL from service lookup
	private static final String ALERTS_SERVICE_URL = "http://localhost:10080/GPIGGroup2UI/app/alerts";
	private static final String MAPS_SERVICE_URL = "http://localhost:10080/GPIGGroup2MapsServer/app/push/strandedPerson";
	private static final int IGNORE_SP_ESTIMATED_NUMBER = 0;

	private static final String IMAGE_TAG = "#image:";
	private static final String ADMIN_PASSWD = "YWRtaW46YWRtaW4=";

	private static <T> String marshallXml(T obj, Class<T> clazz) {

		StringWriter sw = new StringWriter();

		try {
			JAXBContext c = JAXBContext.newInstance(clazz);
			Marshaller m = c.createMarshaller();
			m.marshal(obj, sw);

		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return sw.toString();
	}

	private int peekCntr = 0;
	private List<StrandedPersonPoi> images = new LinkedList<>();
	private Map<Integer, StrandedPersonPoi> peeked = new HashMap<>();

	public synchronized void addPois(ResponseData pois) {

		for (Image img : pois.getImagesX()) {
			StrandedPersonPoi spImg = new StrandedPersonPoi();
			spImg.setImageUrl(img.getUrlX());
			spImg.setImageLoc(pois.getOriginX());
			Utils.unshift(images, spImg);
		}
	}

	public synchronized StrandedPersonImage getNextImg() {

		StrandedPersonPoi peekPoi = Utils.peek(images);
		StrandedPersonImage peekImg = (StrandedPersonImage) peekPoi;
		peekImg.setId(peekCntr);
		peeked.put(peekImg.getId(), peekImg);
		peekCntr++;
		return peekImg;
	}

	@Async
	public void forwardImageVer(StrandedPersonImage spi) {

		if (spi.isYes()) {
			StrandedPersonPoi spp = peeked.get(spi.getId());
			images.remove(spp);

			String alertXml = convertSppToAlertXml(spp);

			try {
				postToAlertsService(alertXml);
			} catch (UnirestException e) {
				e.printStackTrace();
			}

			String sppXml = convertSppToSpXml(spp);

			try {
				postToMapsService(sppXml);
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
	}

	private String convertSppToSpXml(StrandedPersonPoi spp) {

		StrandedPerson sp = new StrandedPerson(spp.getImageLoc(), IGNORE_SP_ESTIMATED_NUMBER, DateTime.now());
		String spXml = marshallXml(sp, StrandedPerson.class);
		return spXml;
	}

	private void postToMapsService(String sppXml) throws UnirestException {

		Unirest.post(MAPS_SERVICE_URL).header("Content-Type", "application/xml").body(sppXml).asString();
	}

	private String convertSppToAlertXml(StrandedPersonPoi spp) {

		Alert alert = new Alert();
		alert.priority = Priority.PRIORITY_LOW;
		alert.message = "New stranded person(s) confirmed at (lat: " + spp.getImageLoc().getLatitudeX() + ", long: "
				+ spp.getImageLoc().getLongitudeX() + ")." + IMAGE_TAG + spp.getImageUrl();
		AlertMessage alertMessage = new AlertMessage();
		alertMessage.alerts = new ArrayList<>(1);
		alertMessage.alerts.add(alert);

		String alertXml = marshallXml(alertMessage, AlertMessage.class);
		return alertXml;
	}

	private void postToAlertsService(String alertXml) throws UnirestException {

		Unirest.post(ALERTS_SERVICE_URL).header("Content-Type", "application/xml")
				.header("authorization", "Basic " + ADMIN_PASSWD).body(alertXml).asString();
	}
}
