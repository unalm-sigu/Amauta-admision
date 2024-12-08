package pe.edu.lamolina.amauta.controller.nivelacioneegg.carganivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

public interface CargaNivelacionService {

    List<CursoNivelacion> allCargaAcademica(DynatableFilter filter, CicloAcademico ciclo, Docente docente);

    List<HorarioAula> getHorarioGrupo(Docente docente, CicloAcademico ciclo);

}
