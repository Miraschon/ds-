package kattycandy.ds.controller;

import kattycandy.ds.entity.TextEntity;
import kattycandy.ds.entity.UserEntity;
import kattycandy.ds.model.DateSelectorDTO;
import kattycandy.ds.model.TextDTO;
import kattycandy.ds.repository.TextRepository;
import kattycandy.ds.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
@Slf4j
public class TextController {

	private final UsersRepository usersRepository;
	private final TextRepository  textRepository;

	public TextController(UsersRepository usersRepository, TextRepository textRepository) {
		this.usersRepository = usersRepository;
		this.textRepository = textRepository;
	}

	@GetMapping("/")
	public String index(Model model) {
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		if (userOptional.isPresent()) {
			UserEntity       userEntity   = userOptional.get();
			List<TextEntity> textEntities = textRepository.findAllByUserId(userEntity.getId());

			textEntities.stream()
			            .max(Comparator.comparing(TextEntity::getCreatedAt))
			            .ifPresent(textEntity -> model.addAttribute("textForm", TextEntity.toDto(textEntity)));

			updateDatesSelector(model, userEntity.getId());
			model.addAttribute("selectForm", new DateSelectorDTO(null, ""));
		}

		model.addAttribute("message", "");

		return "index";
	}

	private void updateDatesSelector(Model model, Integer userId) {
		List<TextEntity>      textEntities = textRepository.findAllByUserId(userId);
		textEntities.forEach(t->log.info(t.toString()));
		List<DateSelectorDTO> dateSelector =
				textEntities.stream().map(TextEntity::toDateSelector).collect(Collectors.toList());
		model.addAttribute("dateSelector", dateSelector);
	}

	@PostMapping(value = {"/selectDate"})
	public String selectDate(Model model, @ModelAttribute("selectForm") DateSelectorDTO selectedDate) {
		log.info("invoked selectDate");
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		TextEntity found = textRepository.getOne(selectedDate.getTextId());
		log.info("found {}", found.toString());
		model.addAttribute("textForm", TextEntity.toDto(found));
		userOptional.ifPresent(userEntity -> updateDatesSelector(model, userEntity.getId()));
		model.addAttribute("selectForm", selectedDate);
		return "index";
	}

	@PostMapping(value = {"/analyseText"})
	public String saveText(@RequestParam(value = "action") String action,
	                       Model model,
	                       @ModelAttribute("textForm") TextDTO txt) {
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		String text = txt.getText();

		if (userOptional.isPresent() && action.equals("Save")) {
			UserEntity       userEntity   = userOptional.get();
			List<TextEntity> textEntities = textRepository.findAllByUserId(userEntity.getId());
			List<String>     texts        = textEntities.stream().map(TextEntity::getText).collect(Collectors.toList());
			if (texts.stream().noneMatch(t -> t.equals(text))) {
				TextEntity newText = new TextEntity();
				newText.setCreatedAt(Instant.now());
				newText.setText(text);
				newText.setUserId(userEntity.getId());
				textRepository.save(newText);
			}
		}

		userOptional.ifPresent(userEntity -> updateDatesSelector(model, userEntity.getId()));

		String[] words = text.replaceAll("[,.:?;\"!]", " ").replaceAll(" {2}+", " ").split(" ");

		String[] sentences = text.split("\\.");

		// Sentences
		long sentenceCount = text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> c.equals(".")).count();
		//paragraphs
		long paragraphCount =
				text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> c.equals("\n")).count() + 1;

		Pattern pattern = Pattern.compile("[a-zA-Z0-9]");

		long alphaNumCount =
				text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> pattern.matcher(c).matches()).count();

		//words per sentence
		long wordsPerSentence = words.length / sentenceCount;

		//words per paragraph
		long wordsPerParagraph = words.length / paragraphCount;

		long maxSentenceLen = Arrays.stream(sentences).map(this::countWordsPerSentence).max(Long::compareTo).orElse(0L);

		long minSentenceLen = Arrays.stream(sentences).map(this::countWordsPerSentence).min(Long::compareTo).orElse(0L);

		long questionsCount = text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> c.equals("?")).count();

		Map<String, Long> wordsMap = new HashMap<>();

		for (String word : words) {
			if (wordsMap.containsKey(word)) {
				long prevCount = wordsMap.get(word);
				wordsMap.put(word, prevCount + 1);
			} else {
				wordsMap.put(word, 1L);
			}
		}

		long uniqueWordsCount = wordsMap.values().stream().filter((e) -> e.equals(1L)).count();

		model.addAttribute("sentences", sentenceCount);
		model.addAttribute("wordsPerSentence", wordsPerSentence);
		model.addAttribute("paragraphs", paragraphCount);
		model.addAttribute("wordsPerParagraph", wordsPerParagraph);
		model.addAttribute("lettersPerSentence", alphaNumCount / sentenceCount);
		model.addAttribute("letters", alphaNumCount);
		model.addAttribute("words", words.length);
		model.addAttribute("maxSentenceLen", maxSentenceLen);
		model.addAttribute("minSentenceLen", minSentenceLen);
		model.addAttribute("questions", questionsCount);
		model.addAttribute("uniqueWords", uniqueWordsCount);

		model.addAttribute("selectForm", new DateSelectorDTO(null, ""));
		return "index";
	}

	private Long countWordsPerSentence(String sentence) {
		String[] words = sentence.trim().split(" ");
		return (long) words.length;
	}
}
