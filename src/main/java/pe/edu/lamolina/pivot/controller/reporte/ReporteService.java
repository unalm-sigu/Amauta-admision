package pe.edu.lamolina.pivot.controller.reporte;

import java.util.List;
import java.util.Map;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.pivot.controller.reporte.dto.HorarioDTO;

public interface ReporteService {

    List<Hora> allHorasEscuela();

    List<Dia> allDiaForPrinter();

    List<AlumnoHorario> allAlumnoHorario(CicloAcademico ciclo);

    Map<Long, HorarioDTO> allHorariosCachimbo(CicloAcademico ciclo);

}
