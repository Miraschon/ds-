package kattycandy.ds.model;

import java.time.Instant;

/**
 * @author Oleg Z. (cornknight@gmail.com)
 */
public class TextDTO {
	private String text;

	private int userId;
	private String createdAt;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
}
