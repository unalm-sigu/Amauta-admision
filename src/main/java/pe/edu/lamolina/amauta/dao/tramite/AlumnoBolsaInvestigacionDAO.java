package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;

public interface AlumnoBolsaInvestigacionDAO extends EasyDAO<AlumnoBolsaInvestigacion> {

    List<AlumnoBolsaInvestigacion> allByBolsaInvestigacion(BolsaInvestigacion bolsa);

    List<AlumnoBolsaInvestigacion> allByDynatableBolsaInvestigacion(DynatableFilter filter, BolsaInvestigacion bolsa);

    AlumnoBolsaInvestigacion findByBolsaInvestigacionAlumno(BolsaInvestigacion bolsa, Alumno alumno);

    List<AlumnoBolsaInvestigacion> allByTramitesSubvenciones(List<TramiteSubvencion> subvenciones);

}
