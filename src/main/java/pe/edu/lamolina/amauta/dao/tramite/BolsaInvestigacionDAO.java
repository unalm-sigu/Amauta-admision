package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.BolsaInvestigacionEstadoEnum;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;

public interface BolsaInvestigacionDAO extends EasyDAO<BolsaInvestigacion> {

    BolsaInvestigacion findByFacultadCicloAcademico(Facultad facultad, CicloAcademico cicloAcademico);
    
    List<BolsaInvestigacion> allByCicloAcademicoEstado(CicloAcademico cicloAcademico, BolsaInvestigacionEstadoEnum bolsaInvestigacionEstadoEnum);

    List<BolsaInvestigacion> allByDynatable(DynatableFilter dynatableFilter, CicloAcademico cicloAcademico);

    List<BolsaInvestigacion> allByCicloAcademico(CicloAcademico cicloAcademico);

    Boolean allEstado(CicloAcademico cicloAcademico, BolsaInvestigacionEstadoEnum estado);

}
