package pe.edu.lamolina.amauta.controller.seguridad.verificarurl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;

public class VerificarUrlControServiceImp implements VerificarUrlControService {

    @Override
    public Boolean accesoSessionUrl(DataSessionPivot ds, String rutaModulo) {

        Optional<Rol> rolInformaticaDERA = ds.getRoles().stream().filter(x -> x.getCodigoEnum() == RolEnum.IOREA).findFirst();

        if (rolInformaticaDERA.isPresent()) {
            return Boolean.TRUE;
        }

        List<String> rutaMenu = ds.getMenusAcceso().stream().map(Menu::getRuta).collect(Collectors.toList());
        if (rutaMenu.contains("/".concat(rutaModulo))) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;

    }

}
