package kattycandy.ds.model;

/**
 * @author Oleg Z. (cornknight@gmail.com)
 */
public class LanguageDTO {
	private String language;
	private boolean current;

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}
}
