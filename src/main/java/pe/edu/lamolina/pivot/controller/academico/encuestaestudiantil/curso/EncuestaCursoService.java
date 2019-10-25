package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EncuestaCursoService {

    List<EncuestaCurso> allEncuestaCurso(DynatableFilter filter, CicloAcademico ciclo);

    void cambiarEstadoEncuesta(EncuestaCurso encuesta);

    EncuestaEstudiantil findEncuestaCurso(CicloAcademico cicloAcademico);

    void activarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void saveDetalleConfigEncuesta(EncuestaEstudiantil encuestaEstudiantil, CicloAcademico ciclo, DataSessionPivot ds);

    String generarEncuesta(CicloAcademico cicloAcademico, DataSessionPivot ds);

    void delete(EncuestaEstudiantil encuesta);

    void publicar(EncuestaEstudiantil encuesta);

    ConfiguraEncuesta findConfigEncuestaCurso(CicloAcademico ciclo);

}
