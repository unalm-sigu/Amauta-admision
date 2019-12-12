package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.Pais;
import pe.edu.lamolina.model.general.TipoDocIdentidad;

public interface TipoDocIdentidadDAO extends EasyDAO<TipoDocIdentidad> {

    List<TipoDocIdentidad> allForPersonaNatural();

    TipoDocIdentidad findBySimbolo(String name);

    public TipoDocIdentidad findBySimboloAndPais(String name, Pais pais);

}
