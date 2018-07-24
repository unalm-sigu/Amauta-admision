package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;

public interface PersonaCargoDAO extends EasyDAO<PersonaCargo> {

    List<PersonaCargo> allByFiltersDynaTable(DynatableFilter filter);

    List<PersonaCargo> allByPersona(Persona persona);

    PersonaCargo findSinCerrar(Oficina oficina, Compania cia);

    public PersonaCargo findCargoByPersona(Oficina oficina, Persona persona);

}
