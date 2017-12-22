package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

interface GenerarHorarioIngresanteService {

    ModalidadEstudio findModalidadPregrado();

    List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    void delete(HorarioCachimbos horarioCachimbos);

    void delete(HorarioCachimboForm form);

    List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico);

    List<Carrera> allCarrera(ModalidadEstudio modalidadEstudio);

    List<Curso> allCursoCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

    List<HorarioCachimbos> allHorarioCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

    List<SeccionHorarioCachimbos> allSeccionHorarioCachimbosByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico);

    String getClave(String codigo, List<SeccionHorarioCachimbos> shcHorario);

    List<Dia> allDia();

    List<Hora> allHora();

    List<HorarioSeccion> allSeccionHorarioCachimbosByHorarioCachimbos(HorarioCachimbos horario);

    void generar(CicloAcademico cicloAcademico, ModalidadEstudio modalidad, DataSessionPivot ds);

    String getClave(SeccionHorarioCachimbos shc);

}
