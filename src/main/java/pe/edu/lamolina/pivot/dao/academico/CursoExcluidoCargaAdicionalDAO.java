package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.CursoExcluidoCargaAdicional;

public interface CursoExcluidoCargaAdicionalDAO extends EasyDAO<CursoExcluidoCargaAdicional> {

    List<CursoExcluidoCargaAdicional>  allByCicloAcademico(CicloAcademico cicloAcademico);
}

