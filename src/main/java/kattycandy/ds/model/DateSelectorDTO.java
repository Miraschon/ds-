package kattycandy.ds.model;

/**
 * @author Oleg Z. (cornknight@gmail.com)
 */
public class DateSelectorDTO {
	private final Integer textId;
	private final String date;

	public DateSelectorDTO(Integer textId, String date) {
		this.textId = textId;
		this.date = date;
	}

	public Integer getTextId() {
		return textId;
	}

	public String getDate() {
		return date;
	}
}
