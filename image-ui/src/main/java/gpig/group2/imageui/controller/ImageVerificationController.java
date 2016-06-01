package gpig.group2.imageui.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import gpig.group2.imageui.model.StrandedPersonImage;
import gpig.group2.imageui.service.ImageVerificationService;
import gpig.group2.models.drone.response.ResponseData;

@Controller
@RequestMapping("/")
public class ImageVerificationController {

	@Autowired
	private ImageVerificationService imageVerificationService;

	@RequestMapping(value = "/push", consumes = "application/xml", method = RequestMethod.POST)
	@ResponseBody
	public String pushVerImages(@RequestBody ResponseData pois) {

		imageVerificationService.addPois(pois);
		return "Accepted";
	}

	@RequestMapping(value = "/imgs", method = RequestMethod.POST)
	public ModelAndView postVerImages(HttpServletRequest req,
			@ModelAttribute("imgModel") StrandedPersonImage imgModel) {

		imageVerificationService.forwardImageVer(imgModel);
		return getVerImages(req);
	}

	@RequestMapping(value = "/imgs", method = RequestMethod.GET)
	public ModelAndView getVerImages(HttpServletRequest req) {

		StrandedPersonImage nextImg = imageVerificationService.getNextImg();
		String baseUrl = req.getProtocol().split("/")[0] + "://" + req.getRemoteHost() + ":" + req.getServerPort()
				+ req.getContextPath() + "/";
		nextImg.setImageUrl(String.format(baseUrl + "images/%s", nextImg.getImageUrl()));


		ModelAndView mv = new ModelAndView("verificationui");
		mv.addObject("imgModel", nextImg);

		return mv;
	}
}
