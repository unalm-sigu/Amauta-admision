package pe.edu.lamolina.amauta.controller.nivelacioneegg.carganivelacion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import static org.joda.time.DateTimeConstants.MONDAY;
import static org.joda.time.DateTimeConstants.SUNDAY;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.carganivelacion.dto.PeriodoDiaDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto.PeriodoDTO;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.ExamenCursoNivelacionDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.TemaAsistenciaDAO;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import static pe.edu.lamolina.model.enums.EstadoGrupoSeccionEnum.CER;
import pe.edu.lamolina.model.enums.TipoHoraEnum;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.ExamenCursoNivelacion;
import pe.edu.lamolina.model.nivelacioneegg.TemaAsistencia;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class CargaNivelacionServiceImpl implements CargaNivelacionService {

    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final DiaDAO diaDAO;
    private final ExamenCursoNivelacionDAO examenCursoNivelacionDAO;
    private final HoraDAO horaDAO;
    private final HorarioAulaDAO horarioAulaDAO;
    private final HorarioCursoDAO horarioCursoDAO;
    private final TemaAsistenciaDAO temaAsistenciaDAO;

    @Override
    public List<CursoNivelacion> allCargaAcademica(DynatableFilter filter, CicloAcademico ciclo, Docente docente) {
        List<CursoNivelacion> secciones = cursoNivelacionDAO.allDocenteByDynatable(filter, ciclo, docente);

        List<CursoCicloAcademico> cursosCiclo = secciones.stream()
                .map(cniv -> cniv.getCursoCiclo())
                .distinct()
                .collect(Collectors.toList());

        List<HorarioCurso> horariosAll = horarioCursoDAO.allByCursosCiclo(cursosCiclo);
        List<TemaAsistencia> leccionesAll = temaAsistenciaDAO.allByCursosNivelaciones(secciones);

        List<ExamenCursoNivelacion> examenesSecciones = examenCursoNivelacionDAO.allByCursosNivelaciones(secciones);
        Map<Long, List<ExamenCursoNivelacion>> mapExamen = examenesSecciones.stream()
                .collect(Collectors.groupingBy(excn -> excn.getCursoNivelacion().getId()));

        for (CursoNivelacion seccion : secciones) {
            List<TemaAsistencia> lecciones = leccionesAll.stream()
                    .filter(lec -> lec.getCursoNivelacion().equals(seccion))
                    .collect(Collectors.toList());

            CursoCicloAcademico cursoCiclo = seccion.getCursoCiclo();
            GrupoHorasNivelacion gpoHoras = seccion.getGrupoHoras();

            List<HorarioCurso> horarios = horariosAll.stream()
                    .filter(hor -> hor.getCursoCiclo().getId().equals(cursoCiclo.getId()))
                    .filter(hor -> gpoHoras != null)
                    .filter(hor -> hor.getGrupoHoras().getId().equals(gpoHoras.getId()))
                    .collect(Collectors.toList());

            seccion.setControlesConfigurados(this.getCantidadDias(horarios));
            seccion.setControlesEjecutados(lecciones.size());

            List<ExamenCursoNivelacion> examenes = TypesUtil.getListNotNull(mapExamen.get(seccion.getId()));
            List<ExamenCursoNivelacion> ejecutados = examenes.stream()
                    .filter(excn -> excn.getEstadoEnum() == CER)
                    .collect(Collectors.toList());

            seccion.setExamenesConfigurados(examenes.size());
            seccion.setExamenesEjecutados(ejecutados.size());

        }

        return secciones;
    }

    private int getCantidadDias(List<HorarioCurso> horarios) {
        if (horarios.isEmpty()) {
            return 0;
        }

        Map<String, PeriodoDiaDTO> mapPeriodoDias = new HashMap();
        List<PeriodoDiaDTO> periodosDias = new ArrayList();

        for (HorarioCurso ha : horarios) {
            DateTime semana = new DateTime(ha.getSemana());
            Date lunes = semana.withDayOfWeek(MONDAY).withTimeAtStartOfDay().toDate();
            Date domingo = semana.withDayOfWeek(SUNDAY).withTimeAtStartOfDay().toDate();

            PeriodoDiaDTO item = new PeriodoDiaDTO(lunes, domingo, ha.getDia(), ha.getHora());
            String key = item.getKey();
            PeriodoDiaDTO previo = mapPeriodoDias.get(key);

            if (previo == null) {
                LocalDate fechaPivote = new LocalDate(lunes);
                LocalDate fechaFin = new LocalDate(domingo);

                while (!fechaPivote.isAfter(fechaFin)) {
                    int diaSemana = fechaPivote.getDayOfWeek();
                    if (diaSemana == ha.getDia().getNumeroDia()) {
                        Date fecha = fechaPivote.toDate();
                        item.setFecha(fecha);
                        break;
                    }
                    fechaPivote = fechaPivote.plusDays(1);
                }

                mapPeriodoDias.put(key, item);
                periodosDias.add(item);

            } else {
                previo.getHoras().add(ha.getHora());
            }
        }

        Collections.sort(periodosDias, (p1, p2) -> p1.getFecha().compareTo(p2.getFecha()));
        PeriodoDiaDTO ultimo = periodosDias.get(periodosDias.size() - 1);
        if (ultimo.getHoras().size() > 1) {
            return periodosDias.size();
        } else {
            return periodosDias.size() - 1;
        }

    }

    @Override
    public List<HorarioCurso> getHorarioGrupo(Docente docente, CicloAcademico ciclo) {
        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allByDocenteCiclo(docente, ciclo);
        List<CursoCicloAcademico> cursosCiclo = cursosNiv.stream()
                .map(cn -> cn.getCursoCiclo())
                .distinct()
                .collect(Collectors.toList());
        List<HorarioCurso> horariosAll = horarioCursoDAO.allByCursosCiclo(cursosCiclo);

        List<HorarioCurso> horarios = new ArrayList();
        for (CursoNivelacion cursoNiv : cursosNiv) {
            CursoCicloAcademico cursoCiclo = cursoNiv.getCursoCiclo();
            GrupoHorasNivelacion gpoHoras = cursoNiv.getGrupoHoras();

            List<HorarioCurso> horariosCurso = horariosAll.stream()
                    .filter(hor -> hor.getCursoCiclo().getId().equals(cursoCiclo.getId()))
                    .filter(hor -> gpoHoras != null)
                    .filter(hor -> hor.getGrupoHoras().getId().equals(gpoHoras.getId()))
                    .collect(Collectors.toList());

            horariosCurso.forEach(hcur -> {
                hcur.setCursoNivelacion(cursoNiv);
                hcur.setCurso(cursoCiclo.getCurso());
            });

            horarios.addAll(horariosCurso);
        }

        return horarios;
    }

    @Override
    public List<Dia> allDias() {
        return diaDAO.all();
    }

    @Override
    public List<Hora> allHoras(Docente docente, CicloAcademico ciclo) {
        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allByDocenteCiclo(docente, ciclo);
        List<HorarioAula> horarios = horarioAulaDAO.allByCursosNivelacion(cursosNiv);

        List<Hora> horas = horarios.stream()
                .map(hor -> hor.getHora())
                .distinct()
                .collect(Collectors.toList());

        if (horas.isEmpty()) {
            return horaDAO.allByTipo(TipoHoraEnum.H60);
        }

        Collections.sort(horas, (h1, h2) -> h1.getNumero().compareTo(h2.getNumero()));
        return horas;
    }

    @Override
    public List<PeriodoDTO> allSemanas(Docente docente, CicloAcademico ciclo) {
        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allByDocenteCiclo(docente, ciclo);
        List<CursoCicloAcademico> cursosCiclo = cursosNiv.stream()
                .map(cniv -> cniv.getCursoCiclo())
                .distinct()
                .collect(Collectors.toList());

        List<HorarioCurso> horarios = horarioCursoDAO.allByCursosCiclo(cursosCiclo);
        log.info("[allSemanas] horarios={}", horarios.size());

        List<PeriodoDTO> periodos = horarios.stream()
                .map(hor -> {
                    return new PeriodoDTO(hor.getSemana());
                })
                .distinct()
                .collect(Collectors.toList());
        log.info("[allSemanas] periodos={}", periodos.size());

        return periodos;
    }

}
