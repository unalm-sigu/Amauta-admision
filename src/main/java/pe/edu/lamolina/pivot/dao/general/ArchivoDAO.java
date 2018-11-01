package pe.edu.lamolina.pivot.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;

public interface ArchivoDAO extends EasyDAO<Archivo> {

    public List<Archivo> allByInstanciaTipoInstancia(Long idInstancia, InstanciaEnum instanciaEnum);

}

