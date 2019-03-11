package pe.edu.lamolina.pivot.dao.aporte;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.aporte.Aporte;
import pe.edu.lamolina.model.aporte.AporteCiclo;

public interface AporteCicloDAO extends EasyDAO<AporteCiclo> {

    List<AporteCiclo> allByCicloAcademico(CicloAcademico cicloAcademico);

    List<AporteCiclo> allByCicloAcademicoModalidadEstudio(CicloAcademico cicloAcademico, ModalidadEstudio modalidadEstudio);

    List<AporteCiclo> allByCicloAcademicoAporte(CicloAcademico cicloAcademico, Aporte aporte);

    AporteCiclo findByCicloAcademicoAporte(CicloAcademico cicloAcademico, Aporte aporte);
}
