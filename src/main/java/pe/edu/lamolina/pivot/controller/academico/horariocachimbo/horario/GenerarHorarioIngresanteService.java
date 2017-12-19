package pe.edu.lamolina.pivot.controller.academico.horariocachimbo.horario;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.pivot.model.academico.AlumnoHorario;
import pe.edu.lamolina.pivot.model.academico.Carrera;
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.model.academico.ModalidadEstudio;
import pe.edu.lamolina.pivot.model.general.Compania;
import pe.edu.lamolina.pivot.model.general.Dia;
import pe.edu.lamolina.pivot.model.horario.Hora;
import pe.edu.lamolina.pivot.model.horario.HorarioCachimbos;
import pe.edu.lamolina.pivot.model.horario.HorarioSeccion;
import pe.edu.lamolina.pivot.model.horario.SeccionHorarioCachimbos;

public interface GenerarHorarioIngresanteService {

    public List<HorarioCachimbos> allHorarioCachimbos(DynatableFilter filter, CicloAcademico cicloAcademico);

    public void delete(HorarioCachimbos horarioCachimbos);

    public void delete(HorarioCachimboForm form);

    public List<AlumnoHorario> allAlumnoHorarioByName(String nombre, CicloAcademico cicloAcademico);

    public List<Carrera> allCarrera(ModalidadEstudio modalidadEstudio);

    public List<Curso> allCursoCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

    public List<HorarioCachimbos> allHorarioCachimbosByCicloAcademico(CicloAcademico cicloAcademico, Carrera carrera);

    public List<SeccionHorarioCachimbos> allSeccionHorarioCachimbosByCursoHora(Carrera carrera, List<Curso> cursos, CicloAcademico cicloAcademico);

    public String getClave(String codigo, List<SeccionHorarioCachimbos> shcHorario);

    public List<Dia> allDia();

    public List<Hora> allHora();

    public List<HorarioSeccion> allSeccionHorarioCachimbosByHorarioCachimbos(HorarioCachimbos horario);

    public void generar(CicloAcademico cicloAcademico, Compania compania);

}
