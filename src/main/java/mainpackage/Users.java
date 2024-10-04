package mainpackage;

public class Users {

    private String username;
    private String name;
    private String surname;
    private UserType id;
    private static int usersCounter = 0;

    Users() {
        usersCounter++;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setId(UserType id) {
        this.id = id;
    }

    public UserType getIdType() {
        return id;
    }

    public int getUsersCounter() {
        return usersCounter;
    }

}



