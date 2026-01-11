package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.clonar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ClonarProgramacionNivelacionServiceImpl implements ClonarProgramacionNivelacionService {

    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final HorarioCursoDAO horarioCursoDAO;
    private final ModalidadEstudioDAO modalidadEstudioDAO;

    private final int SEMANAS_MAS = 25;

    @Override
    @Transactional
    public int clonar(CicloAcademico ciclo, DataSessionPivot ds) {
        ModalidadEstudio modalidad = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.NIV_ING);
        CicloAcademico cicloAntes = cicloAcademicoDAO.findAnteriorRegular(ciclo).get(0);

        List<CursoCicloAcademico> cursosCicloAntes = cursoCicloAcademicoDAO.allByCicloModalidad(cicloAntes, modalidad);
//        Map<Long, CursoCicloAcademico> mapCursoCicloAntes = cursosCicloAntes.stream()
//                .collect(Collectors.toMap(cc -> cc.getCurso().getId(), Function.identity()));

        List<CursoCicloAcademico> cursosCiclo = cursoCicloAcademicoDAO.allByCicloModalidad(ciclo, modalidad);
        Map<Long, CursoCicloAcademico> mapCursoCiclo = cursosCiclo.stream()
                .collect(Collectors.toMap(cc -> cc.getCurso().getId(), Function.identity()));

        List<HorarioCurso> horariosCursosAntes = horarioCursoDAO.allByCiclo(cicloAntes);
        Map<Long, List<HorarioCurso>> mapHorarioCursoAntes = horariosCursosAntes.stream()
                .collect(Collectors.groupingBy(hc -> hc.getCursoCiclo().getCurso().getId()));

        List<HorarioCurso> horariosCursos = horarioCursoDAO.allByCiclo(ciclo);
        Map<Long, List<HorarioCurso>> mapHorarioCurso = horariosCursos.stream()
                .collect(Collectors.groupingBy(hc -> hc.getCursoCiclo().getCurso().getId()));

        int cambios = 0;
        for (CursoCicloAcademico cursoCicloAntes : cursosCicloAntes) {
            Curso curso = cursoCicloAntes.getCurso();
            CursoCicloAcademico cursoCiclo = mapCursoCiclo.get(curso.getId());

            if (cursoCiclo == null) {
                cursoCiclo = new CursoCicloAcademico();
                cursoCiclo.setCicloAcademico(ciclo);
                cursoCiclo.setCurso(curso);
                cursoCiclo.setEstadoEnum(EstadoEnum.ACT);
                cursoCiclo.setHorasCiclo(cursoCicloAntes.getHorasCiclo());
                cursoCiclo.setHorasSemanalesTeoria(cursoCicloAntes.getHorasSemanalesTeoria());
                cursoCiclo.setHorasSemanalesPractica(cursoCicloAntes.getHorasSemanalesPractica());
                cursoCiclo.setCreditos(cursoCicloAntes.getCreditos());
                cursoCicloAcademicoDAO.save(cursoCiclo);

                mapCursoCiclo.put(curso.getId(), cursoCiclo);
                cambios++;
            }

            List<HorarioCurso> horariosCursoActual = mapHorarioCurso.get(curso.getId());
            if (horariosCursoActual == null) {
                List<HorarioCurso> horariosCursoAntes = mapHorarioCursoAntes.get(curso.getId());

                for (HorarioCurso horarioCursoAntes : horariosCursoAntes) {
                    HorarioCurso horarioCurso = new HorarioCurso();
                    horarioCurso.setCursoCiclo(cursoCiclo);
                    horarioCurso.setPlantilla(horarioCursoAntes.getPlantilla());
                    horarioCurso.setDia(horarioCursoAntes.getDia());
                    horarioCurso.setHora(horarioCursoAntes.getHora());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(horarioCursoAntes.getSemana());
                    cal.add(Calendar.WEEK_OF_YEAR, SEMANAS_MAS);
                    horarioCurso.setSemana(cal.getTime());

                    horarioCurso.setUserRegistro(ds.getUsuario());
                    horarioCurso.setFechaRegistro(new Date());
                    horarioCursoDAO.save(horarioCurso);
                    cambios++;
                }
            }
        }

        List<CursoNivelacion> nuevos = new ArrayList();

        List<CursoNivelacion> cursosNivelacion = cursoNivelacionDAO.allByCiclo(cicloAntes);
        cursosNivelacion.stream()
                .filter(cursoNiv -> cursoNiv.getEstadoEnum() == SeccionEstadoEnum.ACT)
                .forEach(cursoNiv -> {
                    Curso curso = cursoNiv.getCursoCiclo().getCurso();
                    CursoCicloAcademico cursoCiclo = mapCursoCiclo.get(curso.getId());

                    CursoNivelacion form = new CursoNivelacion();
                    form.setCursoCiclo(cursoCiclo);
                    form.setDocente(cursoNiv.getDocente());
                    form.setAula(cursoNiv.getAula());
                    form.setPlantilla(cursoNiv.getPlantilla());
                    form.setCodigo(cursoNiv.getCodigo());
                    form.setVacantes(cursoNiv.getVacantes());
                    form.setDisponibles(cursoNiv.getVacantes());
                    form.setHorasDictado(cursoNiv.getHorasDictado());

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(cursoNiv.getFechaInicio());
                    cal.add(Calendar.WEEK_OF_YEAR, SEMANAS_MAS);
                    form.setFechaInicio(cal.getTime());

                    cal.setTime(cursoNiv.getFechaFin());
                    cal.add(Calendar.WEEK_OF_YEAR, SEMANAS_MAS);
                    form.setFechaFin(cal.getTime());

                    form.setEstadoEnum(SeccionEstadoEnum.ACT);
                    form.setEstadoNotasEnum(EstadoGrupoSeccionEnum.ABI);
                    form.setMatriculados(0);
                    form.setUserRegistro(ds.getUsuario());
                    form.setFechaRegistro(new Date());
                    cursoNivelacionDAO.save(form);

                    nuevos.add(form);
                });

        return nuevos.size() + cambios;
    }

}
