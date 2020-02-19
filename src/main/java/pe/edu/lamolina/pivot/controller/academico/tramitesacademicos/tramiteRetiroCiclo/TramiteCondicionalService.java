package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.tramiteRetiroCiclo;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.ParametrosSistemasEnum;
import pe.edu.lamolina.model.general.Parametro;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface TramiteCondicionalService {

    public List<CicloAcademico> allCiclos(CicloAcademico academico);

    public List<Tramite> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter);

    public void saveRetiroCiclo(Tramite tramite, DataSessionPivot ds);

    public String updateRetiroCiclo(Tramite tramite, DataSessionPivot ds);

    public List<Alumno> allAlumnoByNombre(String nombre, DataSessionPivot ds);

    public void createToken(DataSessionPivot ds);

    public List<TipoTramite> allTipoTramite();

    public void saveReincorporacion(Tramite tramite, DataSessionPivot ds);

    public String updateReincorporacion(Tramite tramite, DataSessionPivot ds);

    public void saveCambioNota(Tramite tramite, DataSessionPivot ds);

    public String updateCambioNota(Tramite tramite, DataSessionPivot ds);

    public List<Curso> allCursosByName(String nombre, Alumno alumno, CicloAcademico academico, DataSessionPivot ds);

    void evaluarEliminarMatriculable(Alumno alumno, CicloAcademico cicloAcademico, DataSessionPivot ds);
}
