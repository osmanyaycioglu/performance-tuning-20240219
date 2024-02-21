package training.performance.string.test;

public class Strings {
    public static void main(String[] args) {
        String stringLoc = "osman" + args + "deneme" + 100;
        stringLoc += "birşey daha";

        for (int i = 0; i < 100; i++) {
            stringLoc += " " + i;
        }

        StringBuilder builderLoc = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            builderLoc.append(" ").append(i);
        }

    }
}
