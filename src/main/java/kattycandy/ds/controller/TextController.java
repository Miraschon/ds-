package kattycandy.ds.controller;

import kattycandy.ds.entity.UserEntity;
import kattycandy.ds.model.TextDTO;
import kattycandy.ds.repository.UsersRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.xml.soap.Text;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class TextController {

	private final UsersRepository usersRepository;

	public TextController(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@GetMapping("/")
	public String index(Model model) {
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		TextDTO text = new TextDTO();

		if (userOptional.isPresent()) {
			UserEntity userEntity = userOptional.get();
			text.setText(userEntity.getText());
		}
		model.addAttribute("message", "Привет.");
		model.addAttribute("textForm", text);

		return "index";
	}

	@PostMapping(value = { "/addText"})
	public String saveText(Model model, @ModelAttribute("textForm") TextDTO txt) {
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		String text = txt.getText();

		if (userOptional.isPresent()) {
			UserEntity userEntity = userOptional.get();
			userEntity.setText(text);
			usersRepository.save(userEntity);
		}

		String[] words = text.split(" ");

	model.addAttribute("text", "Количество слов: "+words.length+", количество букаф: "+text.length());
		return "index";
	}
}
