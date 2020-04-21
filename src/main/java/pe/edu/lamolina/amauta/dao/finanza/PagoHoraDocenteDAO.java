package pe.edu.lamolina.amauta.dao.finanza;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.finanzas.PagoHoraDocente;

public interface PagoHoraDocenteDAO extends EasyDAO<PagoHoraDocente> {

    PagoHoraDocente findByCicloMatriculados(CicloAcademico cicloAcademico, Integer matriculados);

    List<PagoHoraDocente> allByCiclo(CicloAcademico cicloAcademico);

}
