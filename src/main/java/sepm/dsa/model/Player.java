package sepm.dsa.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name="players")
public class Player implements Serializable {
	private static final long serialVersionUID = 3102323283798472344L;

	@Id
	@GeneratedValue
	@Column(nullable = false, unique = true)
	private Integer id;

	@NotNull
	@Column(nullable = false)
	private String name;

	@Column
	private String comment;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Player)) return false;

		Player player = (Player) o;

		if (id != null ? !id.equals(player.id) : player.id != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}
}
