package chooseTeams;

public abstract class Person {

    protected String name;
    protected String email;

    public Person( String name, String email) {

        this.name = name;
        this.email = email;
    }

    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }

    public abstract void setName(String name);
    public abstract void setEmail(String email);

}
