package pe.edu.lamolina.pivot.zelper;

import org.apache.commons.lang3.text.WordUtils;

public class Laboratory {

    public static void main666(String[] args) {

        String nom = "Juan cArlis\td'OnofriO, jesús ÑOÑO 3453453 françois \n"
                + "   tudela-quispe";
        System.out.println(WordUtils.capitalize(nom.toLowerCase()));

        nom = nom.toLowerCase();
        nom = nom.replaceAll("[^a-zçñáéíóúü\\s'\\-]", "");
        nom = nom.replaceAll("[\\n\\r|,\\t]", " ");
        nom = nom.replaceAll(" +", " ");
        String[] arr = nom.split(" ");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1);
        }
        nom = String.join(" ", arr);

        arr = nom.split("'");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1);
        }
        nom = String.join("'", arr);

        arr = nom.split("-");
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i].substring(0, 1).toUpperCase() + arr[i].substring(1);
        }
        nom = String.join("-", arr);

        System.out.println(nom);

    }
}
