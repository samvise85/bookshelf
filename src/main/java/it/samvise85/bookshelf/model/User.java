package it.samvise85.bookshelf.model;

import it.samvise85.bookshelf.model.commons.Editable;
import it.samvise85.bookshelf.model.commons.EditableImpl;
import it.samvise85.bookshelf.model.commons.Projectable;
import it.samvise85.bookshelf.model.commons.StringIdentifiable;
import it.samvise85.bookshelf.persist.clauses.ProjectionClause;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Entity
public class User extends EditableImpl implements StringIdentifiable, Editable, Projectable {
	@Transient
	public static final ProjectionClause TOTAL_PROTECTION = ProjectionClause.createInclusionClause("id", "username", "admin");
	@Transient
	public static final ProjectionClause PASSWORD_PROTECTION = ProjectionClause.createExclusionClause("password", "resetCode", "activationCode");
	@Transient
	public static final ProjectionClause NO_PROTECTION = ProjectionClause.NO_PROJECTION;
	@Transient
	public static final ProjectionClause AUTHENTICATION_PROTECTION = ProjectionClause.createInclusionClause("id", "username", "password", "activationCode", "resetCode");
	
	//internal attributes
	@Id
	private String id;
	@Column
	private String username;
	@Column
	private String email;
	@Column
	private String password;
	@Column
	private Boolean admin = Boolean.FALSE;
	@Column
	private String firstname;
	@Column
	private String lastname;
	@Column
	private String country;
	@Column
	private String language;
	@Column
	private Integer birthYear;
	@Column
	private Date birthday;
	@Column
	private String activationCode;
	@Column
	private String resetCode;
	
	@Transient
	@JsonIgnore
	private ProjectionClause projection;
	
	public User() {};
	public User(ProjectionClause projection) {
		this.projection = projection;
	};
	public User(String username, String email) {
		super();
		this.username = username;
		this.email = email;
	}
	
	public User(String username, String password, String email) {
		super();
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return returnNullOrValue("email", email);
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return returnNullOrValue("password", password);
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getAdmin() {
		return returnNullOrValue("admin", admin);
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	public String getFirstname() {
		return returnNullOrValue("firstname", firstname);
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return returnNullOrValue("lastname", lastname);
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getCountry() {
		return returnNullOrValue("country", country);
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLanguage() {
		return returnNullOrValue("language", language);
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public Integer getBirthYear() {
		return returnNullOrValue("birthYear", birthYear);
	}
	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}
	public Date getBirthday() {
		return returnNullOrValue("birthday", birthday);
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getActivationCode() {
		return returnNullOrValue("activationCode", activationCode);
	}
	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}
	public String getResetCode() {
		return returnNullOrValue("resetCode", resetCode);
	}
	public void setResetCode(String resetCode) {
		this.resetCode = resetCode;
	}
	@Override
	public Date getCreation() {
		return returnNullOrValue("creation", super.getCreation());
	}
	@Override
	public Date getLastModification() {
		return returnNullOrValue("lastModification", super.getLastModification());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((activationCode == null) ? 0 : activationCode.hashCode());
		result = prime * result + ((admin == null) ? 0 : admin.hashCode());
		result = prime * result
				+ ((birthYear == null) ? 0 : birthYear.hashCode());
		result = prime * result
				+ ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result
				+ ((creation == null) ? 0 : creation.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime
				* result
				+ ((lastModification == null) ? 0 : lastModification.hashCode());
		result = prime * result + ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((resetCode == null) ? 0 : resetCode.hashCode());
		result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (activationCode == null) {
			if (other.activationCode != null)
				return false;
		} else if (!activationCode.equals(other.activationCode))
			return false;
		if (admin == null) {
			if (other.admin != null)
				return false;
		} else if (!admin.equals(other.admin))
			return false;
		if (birthYear == null) {
			if (other.birthYear != null)
				return false;
		} else if (!birthYear.equals(other.birthYear))
			return false;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (creation == null) {
			if (other.creation != null)
				return false;
		} else if (!creation.equals(other.creation))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (lastModification == null) {
			if (other.lastModification != null)
				return false;
		} else if (!lastModification.equals(other.lastModification))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (resetCode == null) {
			if (other.resetCode != null)
				return false;
		} else if (!resetCode.equals(other.resetCode))
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	@Override
	public ProjectionClause getProjection() {
		return projection;
	}
	@Override
	public User setProjection(ProjectionClause projection) {
		this.projection = projection;
		return this;
	}
	
	private <T> T returnNullOrValue(String fieldName, T fieldValue) {
		return ProjectionClause.returnNullOrValue(projection, fieldName, fieldValue);
	}
}
