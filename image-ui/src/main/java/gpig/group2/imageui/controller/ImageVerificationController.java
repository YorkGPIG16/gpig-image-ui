package gpig.group2.imageui.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public String pushVerImages(ResponseData pois) {

		imageVerificationService.addPois(pois);
		return "Accepted";
	}

	@RequestMapping(value = "/imgs")
	public ModelAndView getPostVerImages(@ModelAttribute("imgModel") StrandedPersonImage imgModel) {

		ModelAndView mv = new ModelAndView("verificationui");
		mv.addObject("imgModel", imageVerificationService.getNextImg());
		return mv;
	}
}
