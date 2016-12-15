package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.zelpers.dao.Crud;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.general.PersonaPerfil;

public interface PersonaPerfilDAO extends Crud<PersonaPerfil> {

    List<PersonaPerfil> allByFiltersDynaTable(DynatableFilter filter);

}
