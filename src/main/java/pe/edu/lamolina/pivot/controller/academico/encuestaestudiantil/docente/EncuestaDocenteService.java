package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.docente;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.ResumenEncuestaDocente;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EncuestaDocenteService {

    EncuestaEstudiantil findEncuestaDocente(CicloAcademico cicloAcademico);

    List<EncuestaDocente> allEncuestaDocente(DynatableFilter filter, CicloAcademico ciclo);

    void activarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

    String generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void cambiarEstadoEncuesta(EncuestaDocente encuesta);

    void saveDetalleConfigEncuesta(EncuestaEstudiantil encuestaEstudiantil, CicloAcademico ciclo, DataSessionPivot ds);

    List<ResumenEncuestaDocente> resumenPreguntasLikert(EncuestaDocente encuestaDocente);

    List<String> resumenComentarios(EncuestaDocente encuestaDocente);

    List<PuntajeEncuestaDocente> resumenPuntajeTemas(EncuestaDocente encuestaDocente);

    void delete(EncuestaEstudiantil encuesta);

    void publicar(EncuestaEstudiantil encuesta);

}
