package pe.edu.lamolina.pivot.controller.reporte;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.pivot.controller.reporte.dto.HorarioDTO;

public interface ReporteService {

    List<AlumnoHorario> allAlumnoHorario(CicloAcademico ciclo);

    Map<Long, HorarioDTO> allHorariosCachimbo(CicloAcademico ciclo);

    Map<Long, Oficina> allOficinaByConsejero();

}
