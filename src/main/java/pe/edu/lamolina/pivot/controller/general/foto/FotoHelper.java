package pe.edu.lamolina.pivot.controller.general.foto;

import org.thymeleaf.util.StringUtils;

public class FotoHelper {

    public String getRutaFoto(String foto, String sexo) {
        if (!StringUtils.isEmpty(foto)) {
            return "https://s3-us-west-1.amazonaws.com/public-innova/profile/" + foto;
        }

        if (!StringUtils.isEmpty(sexo)) {
            switch (sexo) {
                case "M":
                    return "/phobos/images/dm/male.png";
                case "F":
                    return "/phobos/images/dm/female.png";
            }
        }

        return "/phobos/images/dm/unknown-person.gif";
    }

}
