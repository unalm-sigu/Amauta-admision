package pe.edu.lamolina.amauta.dao.academico;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.AlumnoCursoPropedeutico;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Seccion;

public interface AlumnoCursoPropedeuticoDAO extends EasyDAO<AlumnoCursoPropedeutico> {

    List<AlumnoCursoPropedeutico> allBySeccion(Seccion seccion);

    List<AlumnoCursoPropedeutico> allBySeccionDynatable(DynatableFilter filter, CicloAcademico academico);

    public AlumnoCursoPropedeutico findAll(Long id);

}
