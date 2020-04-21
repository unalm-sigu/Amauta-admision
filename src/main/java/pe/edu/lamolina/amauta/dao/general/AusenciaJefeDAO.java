package pe.edu.lamolina.amauta.dao.general;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Oficina;

public interface AusenciaJefeDAO extends EasyDAO<AusenciaJefe> {

    AusenciaJefe findSinCerrar(AusenciaJefe ausencia);

    List<AusenciaJefe> allNoCerradasByOficinas(List<Oficina> oficinas);

}
