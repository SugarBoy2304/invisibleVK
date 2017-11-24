package myvk;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Account {

    private StringProperty firstName;
    private StringProperty secondName;

    public Account() {
        this(null, null);
    }

    public Account(String firstName, String secondName) {
        this.firstName = new SimpleStringProperty(firstName);
        this.secondName = new SimpleStringProperty(secondName);
    }

    public String getFirstName() {
        return this.firstName.get();
    }

    public String getSecondName() {
        return this.secondName.get();
    }



}
