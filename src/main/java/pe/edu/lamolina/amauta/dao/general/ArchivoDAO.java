package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.general.Archivo;

public interface ArchivoDAO extends EasyDAO<Archivo> {

    List<Archivo> allByInstanciaTipoInstancia(Long idInstancia, InstanciaEnum instanciaEnum);

    List<Archivo> allByInstanciasTipoInstancia(List<Long> idInstancias, InstanciaEnum instanciaEnum);

    Archivo findFirstByInstanciasTipoInstancia(Long idInstancia, InstanciaEnum instanciaEnum);

}
