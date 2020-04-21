package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Idioma;

public interface IdiomaDAO extends EasyDAO<Idioma> {

    List<Idioma> allInglesAndEspañol();

    List<Idioma> allByCodigo(List<String> codigos);

    List<Idioma> allDynatable(DynatableFilter filter);

}
