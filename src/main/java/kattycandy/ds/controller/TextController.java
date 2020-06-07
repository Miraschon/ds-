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

import java.io.IOException;
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

			model.addAttribute("textForm",
			                   textEntities.stream()
			                               .max(Comparator.comparing(TextEntity::getCreatedAt))
			                               .map(TextEntity::toDto)
			                               .orElse(new TextDTO()));

			updateDatesSelector(model, userEntity.getId(), null);
			model.addAttribute("selectForm", new DateSelectorDTO(null, ""));
		}

		model.addAttribute("message", "");

		return "index";
	}

	private void updateDatesSelector(Model model, Integer userId, Integer selectedId) {
		List<TextEntity> textEntities = textRepository.findAllByUserId(userId);

		List<DateSelectorDTO> dateSelector = textEntities.stream()
		                                                 .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
		                                                 .map(TextEntity::toDateSelector)
		                                                 .collect(Collectors.toList());

		if (dateSelector.isEmpty()) {
			DateSelectorDTO empty = new DateSelectorDTO(null, "Nothing selected");
			dateSelector.add(empty);
		} else {
			dateSelector.stream()
			            .filter(ds -> ds.getTextId().equals(selectedId))
			            .findFirst()
			            .orElse(dateSelector.get(0))
			            .setSelected(true);
		}

		model.addAttribute("dateSelector", dateSelector);
	}

	@PostMapping(value = {"/selectDate"})
	public String selectDate(@RequestParam(value = "dateSelector") String textId, Model model) {
		log.info("invoked selectDate");
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		if (textId == null || textId.isEmpty()) {
			model.addAttribute("textForm", new TextDTO());
		} else {
			TextEntity found = textRepository.getOne(Integer.parseInt(textId.split(",")[0]));
			log.info("found {}", found.toString());
			model.addAttribute("textForm", TextEntity.toDto(found));
			userOptional.ifPresent(userEntity -> updateDatesSelector(model, userEntity.getId(), Integer.parseInt(textId)));
		}
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
			if (!text.isEmpty() && texts.stream().noneMatch(t -> t.equals(text))) {
				TextEntity newText = new TextEntity();
				newText.setCreatedAt(Instant.now());
				newText.setText(text);
				newText.setUserId(userEntity.getId());
				textRepository.save(newText);
			}
		}

		userOptional.ifPresent(userEntity -> updateDatesSelector(model, userEntity.getId(), null));

		String[] words = text.replaceAll("[,.:?;\"!]", " ").replaceAll(" {2,}", " ").trim().split(" ");

		String[] sentences = text.split("\\.");

		// Sentences
		long sentenceCount = text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> c.equals(".")).count();
		//paragraphs
		long paragraphCount = text.isEmpty() ? 0 : text.codePoints()
		                                               .mapToObj(c -> String.valueOf((char) c))
		                                               .filter(c -> c.equals("\n"))
		                                               .count() + 1;

		Pattern pattern = Pattern.compile("[a-zA-Z0-9]");

		long alphaNumCount =
				text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> pattern.matcher(c).matches()).count();

		//words per sentence
		long wordsPerSentence = 0;
		if (sentenceCount != 0)
			wordsPerSentence = words.length / sentenceCount;

		//words per paragraph
		long wordsPerParagraph = 0;
		if (paragraphCount != 0)
			wordsPerParagraph = words.length / paragraphCount;

		long maxSentenceLen = text.isEmpty() ? 0 : Arrays.stream(sentences)
		                                                 .map(this::countWordsPerSentence)
		                                                 .max(Long::compareTo)
		                                                 .orElse(0L);

		long minSentenceLen = text.isEmpty() ? 0 : Arrays.stream(sentences)
		                                                 .map(this::countWordsPerSentence)
		                                                 .min(Long::compareTo)
		                                                 .orElse(0L);

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

		long uniqueWordsCount = text.isEmpty() ? 0 : wordsMap.values().stream().filter((e) -> e.equals(1L)).count();

		model.addAttribute("sentences", sentenceCount);
		model.addAttribute("wordsPerSentence", wordsPerSentence);
		model.addAttribute("paragraphs", paragraphCount);
		model.addAttribute("wordsPerParagraph", wordsPerParagraph);
		model.addAttribute("lettersPerSentence", sentenceCount == 0 ? 0 : alphaNumCount / sentenceCount);
		model.addAttribute("letters", alphaNumCount);
		model.addAttribute("words", text.isEmpty() ? 0 : words.length);
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
