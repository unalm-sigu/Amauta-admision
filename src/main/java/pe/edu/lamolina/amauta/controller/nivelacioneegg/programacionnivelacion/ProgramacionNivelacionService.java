package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto.PeriodoDTO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

public interface ProgramacionNivelacionService {

    CursoNivelacion findCursoNivelacion(CursoNivelacion form);

    List<GrupoHorasNivelacion> allGruposHoras();

    List<CursoNivelacion> allCursosNivelacionByDynatable(DynatableFilter filter, CicloAcademico ciclo);

    List<Curso> allCursos(String nombre, CicloAcademico ciclo);

    List<HorarioCurso> getHorarioGrupo(GrupoHorasNivelacion gproHoras, CicloAcademico ciclo);

    List<HorarioCurso> getHorario(CursoNivelacion cursoNivelacion, CicloAcademico ciclo);

    PeriodoDTO getPeriodo(CursoNivelacion cursoNivelacion, CicloAcademico ciclo);

    List<Aula> allAulas(String nombre);

    List<Docente> allDocentes(String nombre);

    String verificarCruceAula(CursoNivelacion cursoNivelacion, CicloAcademico ciclo);

    String verificarCruceDocente(CursoNivelacion cursoNivelacion, CicloAcademico cicloAcademico);

    void addCurso(CursoNivelacion cursoNivelacion, CicloAcademico ciclo, DataSessionPivot ds);

    void setHorario(CursoCicloAcademico cursoCiclo, CicloAcademico ciclo, DataSessionPivot ds);

    void changeGrupo(CursoNivelacion cursoNivelacion, DataSessionPivot ds);

    void changeVacantes(CursoNivelacion cursoNivelacion, DataSessionPivot ds);

    void changeAula(CursoNivelacion cursoNivelacion, DataSessionPivot ds);

    void changeDocente(CursoNivelacion cursoNivelacion, DataSessionPivot ds);

    void changeEstado(CursoNivelacion cursoNiv, SeccionEstadoEnum estadoEnum, DataSessionPivot ds);

    List<Dia> allDias();

    List<Hora> allHoras();

    List<PeriodoDTO> allSemanas(CursoNivelacion cursoNivelacion, DataSessionPivot ds);

    List<PeriodoDTO> addSemana(List<PeriodoDTO> semanasForm, String direccion);

}
