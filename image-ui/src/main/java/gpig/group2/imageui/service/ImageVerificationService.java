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

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import gpig.group2.imageui.model.StrandedPersonImage;
import gpig.group2.imageui.model.StrandedPersonPoi;
import gpig.group2.imageui.util.Utils;
import gpig.group2.maps.geographic.Point;
import gpig.group2.models.alerts.Alert;
import gpig.group2.models.alerts.AlertMessage;
import gpig.group2.models.alerts.Priority;
import gpig.group2.models.drone.response.Image;
import gpig.group2.models.drone.response.ResponseData;

@Service
public class ImageVerificationService {

	// TODO GET URL from service lookup
	private static final String ALERTS_SERVICE_URL = "http://localhost:10080/GPIGGroup2UI/app/alerts";

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

	// Test data
	{
		makeTestData();
	}

	public void makeTestData() {

		StrandedPersonImage spi = new StrandedPersonImage();
		Point point = new Point();
		point.setLatitude(100);
		point.setLongitude(200);
		spi.setImageLoc(point);
		spi.setImageUrl("imajURL");
		images.add(spi);
	}

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
			makeTestData(); // TODO: Remove me
			
			StrandedPersonPoi spp = peeked.get(spi.getId());
			images.remove(spp);

			Alert alert = new Alert();
			alert.priority = Priority.PRIORITY_LOW;
			alert.message = "New stranded person(s) confirmed at (lat: " + spp.getImageLoc().getLatitudeX() + ", long: "
					+ spp.getImageLoc().getLongitudeX() + ").#image:" + spp.getImageUrl();
			AlertMessage alertMessage = new AlertMessage();
			alertMessage.alerts = new ArrayList<>(1);
			alertMessage.alerts.add(alert);

			String alertXml = marshallXml(alertMessage, AlertMessage.class);

			try {
				Unirest.post(ALERTS_SERVICE_URL).header("Content-Type", "application/xml").header("authorization", "Basic YWRtaW46YWRtaW4=").body(alertXml).asString();
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
	}
}
