package gpig.group2.imageui.service;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import co.j6mes.infra.srf.query.QueryResponse;
import co.j6mes.infra.srf.query.ServiceQuery;
import co.j6mes.infra.srf.query.SimpleServiceQuery;
import gpig.group2.imageui.model.StrandedPersonImage;
import gpig.group2.imageui.model.StrandedPersonPoi;
import gpig.group2.imageui.util.Utils;
import gpig.group2.model.sensor.StrandedPerson;
import gpig.group2.models.alerts.Alert;
import gpig.group2.models.alerts.AlertMessage;
import gpig.group2.models.alerts.Priority;
import gpig.group2.models.drone.response.Image;
import gpig.group2.models.drone.response.ResponseData;

@Service
public class ImageVerificationService {

	private static final String ALERTS_PUSH_OPERATION = "alerts";
	private static final String STRANDED_PERSON_PUSH_OPERATION = "push/strandedPerson";

	private String alertsPushEndpoint;
	private String strandedPersonPushEndpoint;

	private static String getEndpoint(String serviceName, String topicName) {

		ServiceQuery sq = new SimpleServiceQuery();
		QueryResponse qr = sq.query(serviceName, topicName);
		String endpoint = "http://" + qr.IP + ":" + qr.Port + "/" + qr.Path;

		return endpoint;
	}

	@PostConstruct
	public void postConstruct() {

		alertsPushEndpoint = getEndpoint("c2", "alerts") + ALERTS_PUSH_OPERATION;
		strandedPersonPushEndpoint = getEndpoint("c2", "maps") + STRANDED_PERSON_PUSH_OPERATION;

		System.out.println("alertsPushEndpoint: " + alertsPushEndpoint);
		System.out.println("strandedPersonPushEndpoint: " + strandedPersonPushEndpoint);
	}

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
	private Map<Integer, StrandedPersonImage> peeked = new HashMap<>();

	public synchronized void addPois(ResponseData pois) {

		for (Image img : pois.getImagesX()) {
			StrandedPersonPoi spImg = new StrandedPersonPoi();
			spImg.setImageUrl(img.getUrlX());
			spImg.setImageLoc(pois.getOriginX());
			spImg.setTaskId(pois.getTaskIdX());
			Utils.unshift(images, spImg);
		}
	}

	public synchronized StrandedPersonImage getNextImg() {

		StrandedPersonPoi peekPoi = Utils.peek(images);

		if (peekPoi == null) {
			return null;
		}

		StrandedPersonImage peekImg = new StrandedPersonImage(peekPoi);
		peekImg.setId(peekCntr);
		peeked.put(peekImg.getId(), peekImg);
		peekCntr++;

		return peekImg;
	}

	@Async
	public synchronized void forwardImageVer(StrandedPersonImage spi) {

		if (spi.isYes()) {
			StrandedPersonImage spp2 = peeked.get(spi.getId());
			images.remove(spp2.getOriginal());
			peeked.remove(spp2);
			String alertXml = convertSppToAlertXml(spp2);

			try {
				postToAlertsService(alertXml);
			} catch (UnirestException e) {
				e.printStackTrace();
			}

			String sppXml = convertSppToSpXml(spp2);

			try {
				postToMapsService(sppXml);
			} catch (UnirestException e) {
				e.printStackTrace();
			}
		}
	}

	private String convertSppToSpXml(StrandedPersonPoi spp) {

		StrandedPerson sp = new StrandedPerson(spp.getImageLoc(), IGNORE_SP_ESTIMATED_NUMBER, DateTime.now(),
				spp.getImageUrl());
		sp.setImageUrl(spp.getImageUrl());
		sp.setOwningTask(spp.getTaskId());
		String spXml = marshallXml(sp, StrandedPerson.class);
		return spXml;
	}

	private void postToMapsService(String sppXml) throws UnirestException {

		Unirest.post(strandedPersonPushEndpoint).header("Content-Type", "application/xml").body(sppXml).asString();
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

		Unirest.post(alertsPushEndpoint).header("Content-Type", "application/xml")
				.header("authorization", "Basic " + ADMIN_PASSWD).body(alertXml).asString();
	}
}
