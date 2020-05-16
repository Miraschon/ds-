package kattycandy.ds.controller;

import kattycandy.ds.model.Text;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class IndexController {

	@GetMapping("/")
	public String index(Model model) {

		// add `message` attribute
		model.addAttribute("message", "Привет.");
		model.addAttribute("textForm", new Text());

		// return view name
		return "test";
	}

	@PostMapping(value = { "/addText" })
	public String savePerson(Model model, @ModelAttribute("textForm") Text txt) {

		String text = txt.getText();

		String[] words = text.split(" ");

	model.addAttribute("text", "Количество слов: "+words.length+", количество букаф: "+text.length());
		return "test";
	}
}
