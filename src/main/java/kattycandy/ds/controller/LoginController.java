package kattycandy.ds.controller;

import kattycandy.ds.entity.UserEntity;
import kattycandy.ds.model.UserDTO;
import kattycandy.ds.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/")
@Slf4j
public class LoginController {

	private final UsersRepository usersRepository;

	public LoginController(UsersRepository usersRepository) {
		this.usersRepository = usersRepository;
	}

	@GetMapping("/login")
	public String getLogin(Model model) {
		model.addAttribute("loginForm", new UserDTO());
		model.addAttribute("error", "");
		return "login";
	}

	@PostMapping(value = { "/login/check"})
	public String login(HttpServletResponse response, Model model, @ModelAttribute("loginForm") UserDTO userDTO) throws IOException {
		log.info("invoked");
		List<SimpleGrantedAuthority> roles = new ArrayList<>();
		roles.add(new SimpleGrantedAuthority("USER"));

		Optional<UserEntity> userOptional = usersRepository.findByUsernameAndPassword(userDTO.getUsername(), userDTO.getPassword());

		if (userOptional.isPresent()) {
			User           principal      = new User(userDTO.getUsername(), "", roles);
			Authentication authentication = new UsernamePasswordAuthenticationToken(principal, "", roles);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			response.sendRedirect("/");
		} else {
			model.addAttribute("error", "Incorrect login and password");
		}
		return "login";
	}
}
