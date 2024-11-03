package model;

public class User {
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String role ;

    public User() {
	}

	public User(String username, String password, String firstName, String lastName,String role ) {
		this.username = username;
		this.password = password;
		this.firstName= firstName;
		this.lastName = lastName;
		this.role=role;
	}

	// Getter , Setter
	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getFirstName() {return firstName;}

	public String getLastName() {return lastName;}

	public String getRole() {return role;}

	public void setFirstName(String firstName) {this.firstName = firstName;}

	public void setLastName(String lastName) {this.lastName = lastName;}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {this.password = password;}
}
