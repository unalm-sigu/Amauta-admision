package pe.edu.lamolina.pivot.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;

public interface TramiteSubvencionDAO extends EasyDAO<TramiteSubvencion> {

    List<TramiteSubvencion> allSubvencionByColaboradorCicloAcademico(Colaborador colaborador, CicloAcademico cicloAcademico);

    public TramiteSubvencion findId(TramiteSubvencion tramiteSubvencion);

    public TramiteSubvencion findSubvencionByAlumnoCicloAcademico(Alumno alumno, CicloAcademico cicloAcademico);

}
