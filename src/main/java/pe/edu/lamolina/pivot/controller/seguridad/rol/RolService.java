package pe.edu.lamolina.pivot.controller.seguridad.rol;

import java.util.List;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.model.seguridad.Rol;
import pe.edu.lamolina.pivot.model.seguridad.Sistema;

public interface RolService {

    void save(Rol rol);

    void update(Rol rol);

    List<Rol> allRol();

    List<Menu> allMenuSystemByRol(Sistema sistema, Long idRol);

    void delete(Rol rol);

    Rol findRol(Rol rol);

}
