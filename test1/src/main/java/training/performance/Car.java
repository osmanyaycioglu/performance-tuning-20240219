package training.performance;


public class Car {

    private String  name;
    private Integer hp;
    private String  model;
    private Integer year;
    private String  location;

    public Car(final String name,
               final Integer hp,
               final String model,
               final Integer year,
               final String location) {
        this.name     = name;
        this.hp       = hp;
        this.model    = model;
        this.year     = year;
        this.location = location;
    }

    public Car() {
    }

    public static CarBuilder builder() {
        return new CarBuilder();
    }

    public String getName() {
        return this.name;
    }

    public Integer getHp() {
        return this.hp;
    }

    public String getModel() {
        return this.model;
    }

    public Integer getYear() {
        return this.year;
    }

    public String getLocation() {
        return this.location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Car)) {
            return false;
        }
        final Car other = (Car) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$name  = this.getName();
        final Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        final Object this$hp  = this.getHp();
        final Object other$hp = other.getHp();
        if (this$hp == null ? other$hp != null : !this$hp.equals(other$hp)) {
            return false;
        }
        final Object this$model  = this.getModel();
        final Object other$model = other.getModel();
        if (this$model == null ? other$model != null : !this$model.equals(other$model)) {
            return false;
        }
        final Object this$year  = this.getYear();
        final Object other$year = other.getYear();
        if (this$year == null ? other$year != null : !this$year.equals(other$year)) {
            return false;
        }
        final Object this$location  = this.getLocation();
        final Object other$location = other.getLocation();
        if (this$location == null ? other$location != null : !this$location.equals(other$location)) {
            return false;
        }
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Car;
    }

    public int hashCode() {
        final int    PRIME  = 59;
        int          result = 1;
        final Object $name  = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $hp = this.getHp();
        result = result * PRIME + ($hp == null ? 43 : $hp.hashCode());
        final Object $model = this.getModel();
        result = result * PRIME + ($model == null ? 43 : $model.hashCode());
        final Object $year = this.getYear();
        result = result * PRIME + ($year == null ? 43 : $year.hashCode());
        final Object $location = this.getLocation();
        result = result * PRIME + ($location == null ? 43 : $location.hashCode());
        return result;
    }

    public String toString() {
        return "Car(name="
               + this.getName()
               + ", hp="
               + this.getHp()
               + ", model="
               + this.getModel()
               + ", year="
               + this.getYear()
               + ", location="
               + this.getLocation()
               + ")";
    }

    public static class CarBuilder {
        private String  name;
        private Integer hp;
        private String  model;
        private Integer year;
        private String  location;

        CarBuilder() {
        }

        public CarBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CarBuilder withHp(Integer hp) {
            this.hp = hp;
            return this;
        }

        public CarBuilder withModel(String model) {
            this.model = model;
            return this;
        }

        public CarBuilder withYear(Integer year) {
            this.year = year;
            return this;
        }

        public CarBuilder withLocation(String location) {
            this.location = location;
            return this;
        }

        public Car build() {
            return new Car(this.name,
                           this.hp,
                           this.model,
                           this.year,
                           this.location);
        }

        public String toString() {
            return "Car.CarBuilder(name="
                   + this.name
                   + ", hp="
                   + this.hp
                   + ", model="
                   + this.model
                   + ", year="
                   + this.year
                   + ", location="
                   + this.location
                   + ")";
        }
    }
}
