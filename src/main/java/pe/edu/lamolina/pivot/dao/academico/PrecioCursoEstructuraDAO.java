package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.PrecioCursoEstructura;

public interface PrecioCursoEstructuraDAO extends EasyDAO<PrecioCursoEstructura> {

    public List<PrecioCursoEstructura> allByCiclo(CicloAcademico cicloDestino);

}
