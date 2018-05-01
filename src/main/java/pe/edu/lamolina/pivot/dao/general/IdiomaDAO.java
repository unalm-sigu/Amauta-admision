package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Idioma;

public interface IdiomaDAO extends EasyDAO<Idioma> {

    public List<Idioma> allInglesAndEspañol();

    public List<Idioma> allByCodigo(List<String> codigos);

}
