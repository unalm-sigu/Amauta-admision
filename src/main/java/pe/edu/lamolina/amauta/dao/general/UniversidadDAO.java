package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.Universidad;

public interface UniversidadDAO extends EasyDAO<Universidad> {

    List<Universidad> allUniversidadByName(String nombre);

    List<Universidad> allUniversidadByNamePais(String nombre, Pais pais);

    Universidad findNombrePais(String nombre, Pais pais);

    Universidad findLastCodigoEntranjero();

}
