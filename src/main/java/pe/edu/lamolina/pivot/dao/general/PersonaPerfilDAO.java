package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaPerfil;

public interface PersonaPerfilDAO extends EasyDAO<PersonaPerfil> {

    List<PersonaPerfil> allByFiltersDynaTable(DynatableFilter filter);

    List<PersonaPerfil> allByPersona(Persona persona);

    PersonaPerfil findSinCerrar(Oficina oficina, Compania cia);

}
