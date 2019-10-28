package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EncuestaDocenteService {

    EncuestaEstudiantil findEncuestaDocente(CicloAcademico cicloAcademico);

    EncuestaEstudiantil findEncuestaDocenteWithResumen(CicloAcademico cicloAcademico, DataSessionPivot ds, HttpServletRequest request);

    List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo, List<DepartamentoAcademico> departamentos, DataSessionPivot ds);

    void activarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

    String generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void cambiarEstadoEncuesta(EncuestaDocente encuesta, DataSessionPivot ds);

    void saveDetalleConfigEncuesta(EncuestaEstudiantil encuestaEstudiantil, CicloAcademico ciclo, DataSessionPivot ds);

    List<ResumenEncuestaDocente> resumenPreguntasLikert(EncuestaDocente encuestaDocente);

    List<String> resumenComentarios(EncuestaDocente encuestaDocente);

    List<PuntajeEncuestaDocente> resumenPuntajeTemas(EncuestaDocente encuestaDocente);

    void delete(EncuestaEstudiantil encuesta, DataSessionPivot ds);

    void publicar(EncuestaEstudiantil encuesta, DataSessionPivot ds);

    List<Facultad> allFacultadesFromDocentes(CicloAcademico cicloAcademico, DataSessionPivot ds, HttpServletRequest request);

    List<DepartamentoAcademico> allDepartamentosFromDocentes(CicloAcademico cicloAcademico, List<Facultad> facultades, DataSessionPivot ds, HttpServletRequest request);

    List<Facultad> allAccesoFacultades(DataSessionPivot ds, HttpServletRequest request);

    List<DepartamentoAcademico> allAccesoDepartamentos(DataSessionPivot ds, List<Facultad> facultades, CicloAcademico cicloAcademico, HttpServletRequest request);

}
