package pe.edu.lamolina.pivot.dao.seguridad;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.edu.lamolina.pivot.model.general.PerfilCompania;
import pe.edu.lamolina.pivot.model.seguridad.PerfilRol;

public interface PerfilRolDAO extends Crud<PerfilRol> {

    List<PerfilRol> allByPerfilCompania(PerfilCompania perfilCompania);

}

