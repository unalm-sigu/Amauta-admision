package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.tramite.AlumnoBolsaInvestigacion;
import pe.edu.lamolina.model.tramite.BolsaInvestigacion;

public interface AlumnoBolsaInvestigacionDAO extends EasyDAO<AlumnoBolsaInvestigacion> {

    public List<AlumnoBolsaInvestigacion> allByBolsaInvestigacion(BolsaInvestigacion bi);

    public List<AlumnoBolsaInvestigacion> allByDynatableBolsaInvestigacion(DynatableFilter filter, BolsaInvestigacion bi);

    public AlumnoBolsaInvestigacion findByBolsaInvestigacionAlumno(BolsaInvestigacion bi, Alumno alumno);

}

