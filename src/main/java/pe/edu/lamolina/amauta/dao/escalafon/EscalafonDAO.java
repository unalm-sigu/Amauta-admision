package pe.edu.lamolina.amauta.dao.escalafon;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.escalafon.Escalafon;
import pe.edu.lamolina.model.general.Persona;

public interface EscalafonDAO extends EasyDAO<Escalafon> {

    List<Escalafon> allDynatableFilter(DynatableFilter filter);

    Escalafon find(Escalafon escalafon);

    Escalafon findByPersona(Persona persona);
}
