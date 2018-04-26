package pe.edu.lamolina.pivot.controller.academico.asistenciaacademica;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.InasistenciaAlumno;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TemaLeccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.InasistenciaAlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.TemaLeccionDAO;
import pe.edu.lamolina.pivot.dao.general.DiaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;

@Service
@Transactional(readOnly = true)
public class AsistenciaAcademicaServiceImp implements AsistenciaAcademicaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    DiaDAO diaDAO;

    @Autowired
    InasistenciaAlumnoDAO inasistenciaAlumnoDAO;

    @Autowired
    TemaLeccionDAO temaLeccionDAO;

    @Override
    public TemaLeccion findTemaLeccionSeccionDocenteFecha(Seccion seccion, Docente docente, DateTime today) {
        TemaLeccion temaLeccion = temaLeccionDAO.findBySeccionDocenteFecha(seccion, docente, today.toDate());
        return temaLeccion;
    }

    @Override
    public List<TemaLeccion> allTemaLeccionBySeccionDocenteDyna(Seccion seccion, Docente docente, DynatableFilter filter) {
        return temaLeccionDAO.allBySeccionDocenteDyna(seccion, docente, filter);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaSeccion> allMatriculaSeccionBySeccion(Seccion seccion, Docente docente, DateTime today) {
        TemaLeccion temaLeccion = temaLeccionDAO.findBySeccionDocenteFecha(seccion, docente, today.toDate());
        seccion = this.findSeccionDia(seccion, today);

        List<InasistenciaAlumno> inasistenciasAlumnos = null;
        if (temaLeccion != null) {
            inasistenciasAlumnos = inasistenciaAlumnoDAO.allByTemaLeccionActives(temaLeccion);
        }
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);

        for (MatriculaSeccion matriculaSeccionEach : matriculasSeccion) {
            Seccion seccionClone = seccion.clone();
            seccionClone.setHorarioSeccion(new ArrayList<>());
            for (HorarioSeccion horSeccion : seccion.getHorarioSeccion()) {
                seccionClone.getHorarioSeccion().add(horSeccion.clone());
            }

            matriculaSeccionEach.setSeccion(seccionClone);

            for (HorarioSeccion horaSeccion : matriculaSeccionEach.getSeccion().getHorarioSeccion()) {
                horaSeccion.setSeleccionado(true);
                if (inasistenciasAlumnos != null) {
                    InasistenciaAlumno inasistencia = inasistenciasAlumnos.stream()
                            .filter(x -> x.getMatriculaCurso().getMatriculaResumen().getAlumno().getId().compareTo(matriculaSeccionEach.getMatriculaResumen().getAlumno().getId()) == 0)
                            .filter(x -> x.getHora().getId().compareTo(horaSeccion.getHora().getId()) == 0).findFirst().orElse(null);
                    if (inasistencia != null) {
                        horaSeccion.setSeleccionado(false);
                    }
                }
            }
        }

        return matriculasSeccion;
    }

    @Override
    public Seccion findSeccionDia(Seccion seccion, DateTime today) {
        seccion = seccionDAO.find(seccion.getId());
        Dia dia = diaDAO.findByNumeroDia(today.getDayOfWeek() - 3);

        List<HorarioSeccion> horarioSeccion = horarioSeccionDAO.allBySeccionDia(seccion, dia);
        Collections.sort(horarioSeccion, (p1, p2) -> p1.getHora().getHora().compareTo(p2.getHora().getHora()));

        seccion.setHorarioSeccion(horarioSeccion);

        return seccion;
    }

    @Override
    public Seccion findSeccion(Long idSeccion) {
        Seccion seccion = seccionDAO.find(idSeccion);
        return seccion;
    }

    @Override
    @Transactional(readOnly = false)
    public void saveInasistencia(TemaLeccion temaLeccion, Docente docente, Usuario usuario, CicloAcademico cicloAcademico) {
        DateTime today = new DateTime();

        temaLeccion.setDocente(docente);
        temaLeccion.setFecha(today.toDate());
        temaLeccion.setUsuarioRegistro(usuario);
        temaLeccion.setFechaRegistro(today.toDate());

        Seccion seccion = temaLeccion.getSeccion();
        List<MatriculaSeccion> matriculasSeccion = seccion.getMatriculaSeccion();
        Curso curso = seccion.getGrupoSeccion().getCurso();

        InasistenciaAlumno inasistenciaAlumno;

        temaLeccionDAO.save(temaLeccion);

        for (MatriculaSeccion matriculaSeccion : matriculasSeccion) {

            if (matriculaSeccion.getSeccion().getHorarioSeccion() != null
                    && !matriculaSeccion.getSeccion().getHorarioSeccion().isEmpty()) {

                MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(
                        matriculaSeccion.getMatriculaResumen().getAlumno(),
                        curso,
                        cicloAcademico);

                for (HorarioSeccion horarioSeccion : matriculaSeccion.getSeccion().getHorarioSeccion()) {
                    inasistenciaAlumno = new InasistenciaAlumno();
                    inasistenciaAlumno.setEsReprogramado(BigDecimal.ZERO.intValue());
                    inasistenciaAlumno.setEstadoEnum(EstadoEnum.ACT);
                    inasistenciaAlumno.setHora(horarioSeccion.getHora());
                    inasistenciaAlumno.setMatriculaCurso(matriculaCurso);
                    inasistenciaAlumno.setTemaLeccion(temaLeccion);

                    inasistenciaAlumno.setFechaRegistro(today.toDate());
                    inasistenciaAlumno.setFechaModificacion(today.toDate());
                    inasistenciaAlumno.setUsuarioModificacion(usuario);
                    inasistenciaAlumno.setUsuarioRegistro(usuario);
                    inasistenciaAlumnoDAO.save(inasistenciaAlumno);
                }

            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateInasistencia(TemaLeccion temaLeccion, Docente docente, Usuario usuario, CicloAcademico cicloAcademico) {
        DateTime today = new DateTime();

        Seccion seccion = temaLeccion.getSeccion();
        List<MatriculaSeccion> matriculasSeccionForm = seccion.getMatriculaSeccion();
        Curso curso = seccion.getGrupoSeccion().getCurso();

        TemaLeccion temaLeccionUpd = new TemaLeccion();
        temaLeccionUpd.setId(temaLeccion.getId());
        temaLeccionUpd.setTema(temaLeccion.getTema());
        temaLeccionDAO.updateTema(temaLeccionUpd);

        List<InasistenciaAlumno> inasistenciasDB = inasistenciaAlumnoDAO.allByTemaLeccionActives(temaLeccion);

        //Anulamos los deseleccionados
        for (InasistenciaAlumno inasistenciaAlumno : inasistenciasDB) {
            MatriculaSeccion matriculaSeccionByAlumno = matriculasSeccionForm
                    .stream()
                    .filter(x -> x.getMatriculaResumen().getAlumno().getId().compareTo(inasistenciaAlumno.getMatriculaCurso().getMatriculaResumen().getAlumno().getId()) == 0)
                    .findFirst().orElse(null);
            HorarioSeccion horarioSeccion = matriculaSeccionByAlumno.getSeccion().getHorarioSeccion().stream()
                    .filter(x -> x.getHora().getId().compareTo(inasistenciaAlumno.getHora().getId()) == 0)
                    .findFirst().orElse(null);
            if (horarioSeccion == null) {
                InasistenciaAlumno inasistenciaAlumnoUpd = new InasistenciaAlumno();
                inasistenciaAlumnoUpd.setId(inasistenciaAlumno.getId());
                inasistenciaAlumnoUpd.setEstadoEnum(EstadoEnum.ANU);
                inasistenciaAlumnoUpd.setFechaModificacion(today.toDate());
                inasistenciaAlumnoUpd.setUsuarioModificacion(usuario);
                inasistenciaAlumnoDAO.updateEstado(inasistenciaAlumnoUpd);
            }

        }

        //agregamos las nuevas inasistencias
        for (MatriculaSeccion matriculaSeccionEach : matriculasSeccionForm) {
            if (matriculaSeccionEach.getSeccion().getHorarioSeccion() != null
                    && !matriculaSeccionEach.getSeccion().getHorarioSeccion().isEmpty()) {

                MatriculaCurso matriculaCurso = matriculaCursoDAO.findByAlumnoCursoCiclo(
                        matriculaSeccionEach.getMatriculaResumen().getAlumno(),
                        curso,
                        cicloAcademico);

                for (HorarioSeccion horarioSeccion : matriculaSeccionEach.getSeccion().getHorarioSeccion()) {

                    InasistenciaAlumno inasistenciaByAlumno = inasistenciasDB.stream()
                            .filter(x -> x.getMatriculaCurso().getMatriculaResumen().getAlumno().getId()
                            .compareTo(matriculaSeccionEach.getMatriculaResumen().getAlumno().getId()) == 0)
                            .filter(x -> x.getHora().getId().compareTo(horarioSeccion.getHora().getId()) == 0)
                            .findFirst().orElse(null);
                    if (inasistenciaByAlumno == null) {
                        inasistenciaByAlumno = new InasistenciaAlumno();
                        inasistenciaByAlumno.setEsReprogramado(BigDecimal.ZERO.intValue());
                        inasistenciaByAlumno.setEstadoEnum(EstadoEnum.ACT);
                        inasistenciaByAlumno.setHora(horarioSeccion.getHora());
                        inasistenciaByAlumno.setMatriculaCurso(matriculaCurso);
                        inasistenciaByAlumno.setTemaLeccion(temaLeccion);

                        inasistenciaByAlumno.setFechaRegistro(today.toDate());
                        inasistenciaByAlumno.setFechaModificacion(today.toDate());
                        inasistenciaByAlumno.setUsuarioModificacion(usuario);
                        inasistenciaByAlumno.setUsuarioRegistro(usuario);
                        inasistenciaAlumnoDAO.save(inasistenciaByAlumno);
                    }
                }

            }
        }

    }

}
