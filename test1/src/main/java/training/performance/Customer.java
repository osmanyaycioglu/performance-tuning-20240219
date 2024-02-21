package training.performance;

public class Customer {

    private String name;
    private String surname;
    private Integer age;
    private int year;
    private String gender;
    private EGender genderE;


    public Customer(final String nameParam,
                    final String surnameParam) {
        name    = nameParam;
        surname = surnameParam;
    }

    public String getName() {
        return name;
    }

    public void setName(final String nameParam) {
        name = nameParam;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(final String surnameParam) {
        surname = surnameParam;
    }
}
