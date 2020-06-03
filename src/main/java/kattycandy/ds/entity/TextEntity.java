package kattycandy.ds.entity;

import kattycandy.ds.model.DateSelectorDTO;
import kattycandy.ds.model.TextDTO;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * @author Oleg Z. (cornknight@gmail.com)
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "text")
public class TextEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", insertable = false, nullable = false)
	private Integer id;

	@Column(name = "user_id", nullable = false)
	private Integer userId;

	@Column(name = "text", nullable = false)
	private String text;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public static TextDTO toDto(TextEntity entity) {
		TextDTO result = new TextDTO();
		result.setText(entity.getText());
		result.setCreatedAt(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
		                                     .withZone(ZoneId.systemDefault())
		                                     .format(entity.getCreatedAt()));
		result.setUserId(entity.getUserId());
		result.setId(entity.getId());
		return result;
	}

	public static DateSelectorDTO toDateSelector(TextEntity entity) {
		return new DateSelectorDTO(entity.getId(),
		                           DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
		                                                              .withZone(ZoneId.systemDefault())
		                                                              .format(entity.getCreatedAt()));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		TextEntity that = (TextEntity) o;
		return Objects.equals(id, that.id) &&
		       Objects.equals(userId, that.userId) &&
		       Objects.equals(text, that.text) &&
		       Objects.equals(createdAt, that.createdAt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, userId, text, createdAt);
	}

	@Override
	public String toString() {
		return "TextEntity{" +
		       "id=" +
		       id +
		       ", userId=" +
		       userId +
		       ", text='" +
		       text +
		       '\'' +
		       ", createdAt=" +
		       createdAt +
		       '}';
	}
}
