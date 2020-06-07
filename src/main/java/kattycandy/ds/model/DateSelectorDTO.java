package kattycandy.ds.model;

public class DateSelectorDTO {
	private final Integer textId;
	private final String date;
	private boolean selected = false;

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

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
