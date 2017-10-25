package pe.edu.lamolina.pivot.controller.seguridad.usuario;

import org.thymeleaf.util.StringUtils;

public class FotoHelper {

    public String getRutaFoto(String foto, String sexo) {
        if (!StringUtils.isEmpty(foto)) {
            return "https://s3-us-west-1.amazonaws.com/public-unalm/profile/" + foto;
        }

        if (!StringUtils.isEmpty(sexo)) {
            switch (sexo) {
                case "M":
                    return "/phobos/images/unalm/male.png";
                case "F":
                    return "/phobos/images/unalm/female.png";
            }
        }

        return "/phobos/images/unalm/unknown-person.gif";
    }

}
