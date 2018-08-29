package pe.edu.lamolina.pivot.controller.academico.encuestaestudiantil.curso;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface EncuestaCursoService {

    List<EncuestaCurso> allEncuestaCurso(DynatableFilter filter, CicloAcademico ciclo);

    void generarEncuesta(CicloAcademico ciclo, DataSessionPivot ds);

    void cambiarEstadoEncuesta(EncuestaCurso encuesta);

}
