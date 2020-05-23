package kattycandy.ds.controller;

import kattycandy.ds.model.TextDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class TextController {

	@GetMapping("/")
	public String index(Model model) {

		// add `message` attribute
		model.addAttribute("message", "Привет.");
		model.addAttribute("textForm", new TextDTO());

		// return view name
		return "index";
	}

	@PostMapping(value = { "/addText"})
	public String savePerson(Model model, @ModelAttribute("textForm") TextDTO txt) {

		String text = txt.getText();

		String[] words = text.split(" ");

	model.addAttribute("text", "Количество слов: "+words.length+", количество букаф: "+text.length());
		return "index";
	}
}
