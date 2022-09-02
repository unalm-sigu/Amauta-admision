package pe.edu.lamolina.amauta.dao.tramite;

import java.util.List;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;

public interface TramiteSubvencionDAO extends EasyDAO<TramiteSubvencion> {

    TramiteSubvencion find(TramiteSubvencion tramiteSubvencion);

    List<TramiteSubvencion> allSubvencionByColaboradorCiclo(List<Colaborador> colaborador, CicloAcademico cicloAcademico);

    TramiteSubvencion findByAlumnoCiclo(Alumno alumno, CicloAcademico ciclo);

}
