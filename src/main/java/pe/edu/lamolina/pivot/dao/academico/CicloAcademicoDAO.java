package pe.edu.lamolina.pivot.dao.academico;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;

public interface CicloAcademicoDAO extends EasyDAO<CicloAcademico> {

    CicloAcademico findActivo();

    List<CicloAcademico> allForChanges(Integer maxResultado);

    CicloAcademico findAnteriorRegular(CicloAcademico ciclo);

    List<CicloAcademico> allUltimos(Integer cantidadCiclos);

}
