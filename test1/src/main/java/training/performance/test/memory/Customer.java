package training.performance.test.memory;

public class Customer {

    private String name;
    private String surname;
    private Integer age;

    public Customer(final String nameParam,
                    final String surnameParam,
                    final Integer ageParam) {
        name    = nameParam;
        surname = surnameParam;
        age     = ageParam;
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

    public Integer getAge() {
        return age;
    }

    public void setAge(final Integer ageParam) {
        age = ageParam;
    }
}
