package kattycandy.ds.controller;

import kattycandy.ds.model.TextDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class RegistrationController {

	@GetMapping("/registration")
	public String registration(Model model) {

		// add `message` attribute
		model.addAttribute("message", "Привет.");
		model.addAttribute("textForm", new TextDTO());

		// return view name
		return "registration";
	}
}
