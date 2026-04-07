package pe.edu.lamolina.amauta.dao.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.horario.GrupoNivelacion;

public interface GrupoNivelacionDAO extends EasyDAO<GrupoNivelacion> {

    List<GrupoNivelacion> allByDynatable(DynatableFilter filter);
    
    GrupoNivelacion findByCodigo(String codigo);

    List<GrupoNivelacion> allByCiclo(CicloAcademico ciclo);
}
