package pe.edu.lamolina.pivot.controller.academico.matriculable;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface MatriculableService {

    List<MatriculaResumen> allAlumnosByCicloRolDynatable(DynatableFilter filter, CicloAcademico cicloAcademico, String codigo, List<Long> filtros);

    MatriculableResumen findResumenByCiclo(CicloAcademico cicloAcademico);

    List<ModalidadEstudio> allModalidadEstudioByCodigos(List<String> codigos);

    void generar(CicloAcademico cicloAcademico, DataSessionPivot ds);

}
