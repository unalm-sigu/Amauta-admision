package pe.edu.lamolina.amauta.dao.consejeria;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.tutoria.InformeFinalTutoria;

public interface InformeFinalTutoriaDAO extends EasyDAO<InformeFinalTutoria> {

    InformeFinalTutoria findActivoByConsejeroCiclo(Consejero consejero, CicloAcademico ciclo);

    InformeFinalTutoria findPendienteByConsejeroCiclo(Consejero consejero, CicloAcademico ciclo);

    List<InformeFinalTutoria> allActivosByConsejerosCiclo(List<Consejero> consejeros, CicloAcademico ciclo);

}
