package kattycandy.ds.controller;

import kattycandy.ds.entity.TextEntity;
import kattycandy.ds.entity.UserEntity;
import kattycandy.ds.model.TextDTO;
import kattycandy.ds.repository.TextRepository;
import kattycandy.ds.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/")
@Slf4j
public class TextController {

	private final UsersRepository usersRepository;
	private final TextRepository textRepository;

	public TextController(UsersRepository usersRepository, TextRepository textRepository) {
		this.usersRepository = usersRepository;
		this.textRepository = textRepository;
	}

	@GetMapping("/")
	public String index(Model model) {
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		TextDTO text = new TextDTO();

		if (userOptional.isPresent()) {
			UserEntity userEntity = userOptional.get();
			List<TextEntity> textEntities = textRepository.findAllByUserId(userEntity.getId());

			textEntities.stream().max(Comparator.comparing(TextEntity::getCreatedAt)).ifPresent(
					textEntity -> model.addAttribute("textForm", TextEntity.toDto(textEntity))
			);

			model.addAttribute("selectForm",new TextDTO());
			model.addAttribute("textEntities",textEntities);
		}

		model.addAttribute("message", "Привет.");

		return "index";
	}

	@PostMapping(value = {"/selectDate"})
	public String selectDate(Model model, @ModelAttribute("selectForm") TextDTO txt) {
		return "index";
	}

	@PostMapping(value = {"/addText"})
	public String saveText(Model model, @ModelAttribute("textForm") TextDTO txt) {
		User                 user         = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String               username     = user.getUsername();
		Optional<UserEntity> userOptional = usersRepository.findByUsername(username);

		String text = txt.getText();

		if (userOptional.isPresent()) {
			UserEntity userEntity = userOptional.get();
			//userEntity.setText(text);
			usersRepository.save(userEntity);
		}

		String[] words = text.replaceAll("[,.:?;\"!]"," ")
		                     .replaceAll(" {2}+", " ")
		                     .split(" ");

		String[] sentences = text.split("\\.");

		// Sentences
		long sentenceCount = text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> c.equals(".")).count();
        //paragraphs
		long paragraphCount = text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> c.equals("\n")).count() + 1;

		Pattern pattern = Pattern.compile("[a-zA-Z0-9]");

		long alphaNumCount = text
				.codePoints()
				.mapToObj(c -> String.valueOf((char) c))
				.filter(c -> pattern.matcher(c).matches())
				.count();

		//words per sentence
		long wordsPerSentence = words.length / sentenceCount;

		//words per paragraph
		long wordsPerParagraph = words.length / paragraphCount;

		long maxSentenceLen =
				Arrays.stream(sentences)
				      .map(this::countWordsPerSentence)
				      .max(Long::compareTo)
				      .orElse(0L);

		long minSentenceLen =
				Arrays.stream(sentences)
				      .map(this::countWordsPerSentence)
				      .min(Long::compareTo)
				      .orElse(0L);

		long questionsCount = text.codePoints().mapToObj(c -> String.valueOf((char) c)).filter(c -> c.equals("?")).count();

		Map<String,Long> wordsMap = new HashMap<>();

		for(String word: words) {
			if (wordsMap.containsKey(word)) {
				long prevCount = wordsMap.get(word);
				wordsMap.put(word,prevCount+1);
			} else {
				wordsMap.put(word, 1L);
			}
		}

		long uniqueWordsCount = wordsMap.values().stream().filter((e)->e.equals(1L)).count();

		model.addAttribute("sentences", sentenceCount);
		model.addAttribute("wordsPerSentence", wordsPerSentence);
		model.addAttribute("paragraphs", paragraphCount);
		model.addAttribute("wordsPerParagraph", wordsPerParagraph);
		model.addAttribute("lettersPerSentence", alphaNumCount/sentenceCount);
		model.addAttribute("letters", alphaNumCount);
		model.addAttribute("words", words.length);
		model.addAttribute("maxSentenceLen", maxSentenceLen);
		model.addAttribute("minSentenceLen", minSentenceLen);
		model.addAttribute("questions", questionsCount);
		model.addAttribute("uniqueWords", uniqueWordsCount);

		model.addAttribute("selectForm",new TextDTO());
		return "index";
	}

	private Long countWordsPerSentence(String sentence) {
		String[] words = sentence.trim().split(" ");
		return (long) words.length;
	}
}
