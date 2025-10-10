package getPlayerDetails;

public abstract class Person {
    public String id;
    public String name;
    public String email;

    public Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public String getId(){
        return id;
    }
    public String getName(){
        return name;
    }
    public String getEmail(){
        return email;
    }
    public abstract void setId(String id);
    public abstract void setName(String name);
    public abstract void setEmail(String email);

}
