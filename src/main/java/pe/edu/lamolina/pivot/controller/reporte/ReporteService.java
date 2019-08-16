package pe.edu.lamolina.pivot.controller.reporte;

import java.util.List;
import pe.edu.lamolina.model.academico.AlumnoHorario;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;

public interface ReporteService {

    List<Hora> allHorario(AlumnoHorario alumno);

    List<Hora> allHorasEscuela();

    List<Dia> allDiaForPrinter();

    List<AlumnoHorario> allAlumnoHorario(CicloAcademico ciclo);

     List<Oficina> allOficinaConsejero();

}
