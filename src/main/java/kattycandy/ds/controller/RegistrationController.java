package kattycandy.ds.controller;

import kattycandy.ds.entity.UserEntity;
import kattycandy.ds.model.UserDTO;
import kattycandy.ds.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@Slf4j
public class RegistrationController {

	private final UsersRepository usersRepository;

	public RegistrationController(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@GetMapping("/registration")
	public String registration(Model model) {

		model.addAttribute("registerForm", new UserDTO());
		model.addAttribute("error", "");

		return "registration";
	}

	@PostMapping(value = {"/registration/signup"})
	public String register(HttpServletResponse response,
	                    Model model,
	                    @ModelAttribute("registerForm") UserDTO userDTO) throws IOException {
		log.info("invoked");
		List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority("USER"));

		if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
			model.addAttribute("error", "enter the username");
			return "registration";
		}
		if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
			model.addAttribute("error", "enter the password");
			return "registration";
		}

		Optional<UserEntity> userOptional = usersRepository.findByUsername(userDTO.getUsername());
		if (userOptional.isPresent()) {
			model.addAttribute("error", "user already exists");
			return "registration";
		}

		UserEntity user = new UserEntity();
		user.setUsername(userDTO.getUsername());
		user.setPassword(userDTO.getPassword());
		usersRepository.save(user);

		org.springframework.security.core.userdetails.User principal =
				new org.springframework.security.core.userdetails.User(userDTO.getUsername(), "", roles);
		Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", roles);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		response.sendRedirect("/");

		return "registration";
	}
}
