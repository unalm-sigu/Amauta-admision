package pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto.CursoCicloGrupoDTO;
import pe.edu.lamolina.amauta.controller.nivelacioneegg.programacionnivelacion.dto.PeriodoDTO;
import pe.edu.lamolina.amauta.dao.academico.CursoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.general.DiaDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasNivelacionDAO;
import pe.edu.lamolina.amauta.dao.horario.HoraDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioCursoDAO;
import pe.edu.lamolina.amauta.dao.nivelacioneegg.CursoNivelacionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.CursoCicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.DOCENTE_INDETERMINADO;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SeccionEstadoEnum;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.ACT;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.BLO;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.CAN;
import static pe.edu.lamolina.model.enums.SeccionEstadoEnum.CRE;
import pe.edu.lamolina.model.enums.TipoHoraEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.GrupoHorasNivelacion;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioCurso;
import pe.edu.lamolina.model.nivelacioneegg.CursoNivelacion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ProgramacionNivelacionServiceImpl implements ProgramacionNivelacionService {

    private final AulaDAO aulaDAO;
    private final CursoCicloAcademicoDAO cursoCicloAcademicoDAO;
    private final CursoDAO cursoDAO;
    private final CursoNivelacionDAO cursoNivelacionDAO;
    private final DiaDAO diaDAO;
    private final DocenteDAO docenteDAO;
    private final GrupoHorasNivelacionDAO grupoHorasNivelacionDAO;
    private final HoraDAO horaDAO;
    private final HorarioAulaDAO horarioAulaDAO;
    private final HorarioCursoDAO horarioCursoDAO;

    @Override
    public List<GrupoHorasNivelacion> allGruposHoras() {
        return grupoHorasNivelacionDAO.all();
    }

    @Override
    public List<CursoNivelacion> allCursosNivelacionByDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        long t1 = System.currentTimeMillis();
        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allByDynatable(filter, ciclo);
        long t2 = System.currentTimeMillis();
        log.info("[allDynatable] cursoNivelacionDAO.allByDynatable {} mseg", (t2 - t1));

        List<CursoCicloGrupoDTO> cursosGrupos = cursosNiv.stream()
                .map(cn -> {
                    return new CursoCicloGrupoDTO(cn.getCursoCiclo(), cn.getGrupoHoras());
                })
                .distinct()
                .collect(Collectors.toList());

        long t3 = System.currentTimeMillis();
        Map<String, List<HorarioCurso>> mapHorarios = new HashMap();
        for (CursoCicloGrupoDTO ccg : cursosGrupos) {
            List<HorarioCurso> horariosCurso = horarioCursoDAO.allByCursoCicloHorario(ccg.getCursoCiclo(), ccg.getGrupoHoras());
            for (HorarioCurso hc : horariosCurso) {
                hc.setCurso(hc.getCursoCiclo().getCurso());
            }
            String key = ccg.getCursoCiclo().getId() + "-" + ccg.getGrupoHoras().getId();
            mapHorarios.put(key, horariosCurso);
        }
        long t4 = System.currentTimeMillis();
        log.info("[allDynatable] horarioCursoDAO.allByCursoCicloHorario {} mseg", (t4 - t3));

        cursosNiv.forEach(cn -> {
            String key = cn.getCursoCiclo().getId() + "-" + cn.getGrupoHoras().getId();
            cn.setHorariosCurso(mapHorarios.get(key));
        });

        long t5 = System.currentTimeMillis();
        log.info("[allDynatable] total {} mseg", (t5 - t1));
        return cursosNiv;
    }

    @Override
    public List<Curso> allCursos(String nombre, CicloAcademico ciclo) {
        List<Curso> cursos = cursoDAO.allByModalidadEstudioNombre(ModalidadEstudioEnum.NIV_ING, nombre);
        List<CursoCicloAcademico> cursosCiclo = cursoCicloAcademicoDAO.allByCursosCiclo(cursos, ciclo);
        Map<Long, CursoCicloAcademico> mapCurso = cursosCiclo.stream()
                .collect(Collectors.toMap(cc -> cc.getCurso().getId(), Function.identity()));

        cursos.forEach(cur -> {
            CursoCicloAcademico cursoCiclo = mapCurso.get(cur.getId());
            if (cursoCiclo == null) {
                cursoCiclo = new CursoCicloAcademico();
                cursoCiclo.setHorasCiclo(cur.getHorasCiclo());
            }
            if (cursoCiclo.getHorasCiclo() == null) {
                cursoCiclo.setHorasCiclo(0);
            }
            cur.setCursoCicloActivo(cursoCiclo);
        });

        return cursos;
    }

    @Override
    public List<HorarioCurso> getHorarioGrupo(GrupoHorasNivelacion form, CicloAcademico ciclo) {
        GrupoHorasNivelacion grupoHoras = this.getGrupoHoras(form);
        List<HorarioCurso> horarios = horarioCursoDAO.allByCicloHorario(ciclo, grupoHoras);
        horarios.forEach(hc -> hc.setCurso(hc.getCursoCiclo().getCurso()));
        return horarios;
    }

    @Override
    public List<HorarioCurso> getHorario(CursoNivelacion cursoNiv, CicloAcademico ciclo) {
        GrupoHorasNivelacion grupoHoras = this.getGrupoHoras(cursoNiv.getGrupoHoras());
        Curso curso = this.getCurso(cursoNiv.getCursoCiclo());

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
        if (cursoCiclo == null) {
            return new ArrayList();
        }

        return horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);
    }

    @Override
    public PeriodoDTO getPeriodo(CursoNivelacion cursoNiv, CicloAcademico ciclo) {
        GrupoHorasNivelacion grupoHoras = this.getGrupoHoras(cursoNiv.getGrupoHoras());
        Curso curso = this.getCurso(cursoNiv.getCursoCiclo());

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
        if (cursoCiclo == null) {
            return new PeriodoDTO();
        }

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allByCursoCiclo(cursoCiclo, grupoHoras);
        if (cursosNiv.isEmpty()) {
            return new PeriodoDTO();
        }

        Date fechaInicio = cursosNiv.stream()
                .map(cn -> cn.getFechaInicio())
                .min(Comparator.naturalOrder())
                .get();

        return new PeriodoDTO(fechaInicio);
    }

    @Override
    public List<Aula> allAulas(String nombre) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        return aulaDAO.searchByNombreFilter(nombre, 15);
    }

    @Override
    public List<Docente> allDocentes(String nombre) {
        Docente docente = new Docente();
        docente.setCodigo(nombre.toUpperCase());
        if (docente.isCodigoNN()) {
            List<Docente> docentes = new ArrayList();
            docentes.add(docenteDAO.findByCode(DOCENTE_INDETERMINADO));
            return docentes;
        }

        return docenteDAO.allByName(nombre);
    }

    @Override
    public String verificarCruceAula(CursoNivelacion form, CicloAcademico ciclo) {
        Assert.isNotNull(form.getCursoCiclo(), "No ha indicado el curso-ciclo");
        Assert.isNotNull(form.getAula(), "No ha indicado el aula");
        Assert.isNotNull(form.getAula().getId(), "No ha indicado el aula");
        Assert.isNotNull(form.getGrupoHoras(), "No ha indicado el grupo de horarios");
        Assert.isNotNull(form.getGrupoHoras().getId(), "No ha indicado el grupo de horario");

        Curso curso = this.getCurso(form.getCursoCiclo());
        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
        if (cursoCiclo == null) {
            return null;
        }

        GrupoHorasNivelacion grupoHoras = this.getGrupoHoras(form.getGrupoHoras());
        List<HorarioCurso> horarios = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);
        if (horarios.isEmpty()) {
            return null;
        }

        Aula aula = this.getAula(form);
        Assert.isNotNull(aula, "El aula que ha indicado, no existe en el sistema");

        Map<String, List<HorarioAula>> mapHorarios = this.getMapHorarioAula(horarios, aula);
        for (HorarioCurso horario : horarios) {
            Dia dia = horario.getDia();
            Hora hora = horario.getHora();

            LocalDate fecha = new LocalDate(horario.getSemana());
            String cruce = this.getCruceAula(mapHorarios, fecha, aula, dia, hora);
            if (cruce != null) {
                return cruce;
            }
        }

        return null;
    }

    @Override
    public String verificarCruceDocente(CursoNivelacion form, CicloAcademico ciclo) {
        Assert.isNotNull(form.getCursoCiclo(), "No ha indicado el curso-ciclo");
        Assert.isNotNull(form.getGrupoHoras(), "No ha indicado el grupo de horarios");
        Assert.isNotNull(form.getGrupoHoras().getId(), "No ha indicado el grupo de horario");
        Assert.isNotNull(form.getDocente(), "No ha indicado el docente");
        Assert.isNotNull(form.getDocente().getId(), "No ha indicado el docente");

        Curso curso = this.getCurso(form.getCursoCiclo());
        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
        if (cursoCiclo == null) {
            return null;
        }

        GrupoHorasNivelacion grupoHoras = this.getGrupoHoras(form.getGrupoHoras());
        List<HorarioCurso> horarios = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);
        if (horarios.isEmpty()) {
            return null;
        }

        Docente docente = this.getDocente(form.getDocente());
        if (docente.isCodigoNN()) {
            return null;
        }

        List<HorarioCurso> horariosCurso = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);
        if (horariosCurso.isEmpty()) {
            return null;
        }

        return this.getCruceDocente(docente, ciclo, horariosCurso);
    }

    @Override
    @Transactional
    public void addCurso(CursoNivelacion form, CicloAcademico ciclo, DataSessionPivot ds) {
        Assert.isNotNull(form.getDocente(), "No ha indicado el docente");
        Assert.isNotNull(form.getDocente().getId(), "No ha indicado el docente");
        Assert.isNotNull(form.getDocente().getCodigo(), "No ha indicado el docente");

        Assert.isNotNull(form.getFechaInicio(), "No ha indicado la fecha de inicio de la semana");
        Assert.isNotNull(form.getFechaFin(), "No ha indicado la fecha final de la semana");
        Assert.isNotNull(form.getHorasDictado(), "No ha indicado la cantidad de horas de dictado");

        Assert.isNotNull(form.getVacantes(), "No ha indicado las vacantes");
        Assert.isTrue(form.getVacantes() > 0, "Debe indicar las vacantes");

        Curso curso = this.getCurso(form.getCursoCiclo());
        GrupoHorasNivelacion grupoHoras = this.getGrupoHoras(form.getGrupoHoras());

        Aula aula = this.getAula(form);
        if (aula != null) {
            Assert.isNotNull(aula.getCapacidadAula(), "Esta aula no tiene configurada su capacidad");
            Assert.isTrue(form.getVacantes() <= aula.getCapacidadAula(), "La cantidad de vacantes no puede ser mayor que la capacidad del aula");
        }

        if (curso.getHorasCiclo() == null || (curso.getHorasCiclo() != null && curso.getHorasCiclo() == 0)) {
            curso.setHorasCiclo(form.getHorasDictado());
            cursoDAO.update(curso);
        }

        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.findByCursoCiclo(curso, ciclo);
        if (cursoCiclo == null) {
            cursoCiclo = new CursoCicloAcademico();
            cursoCiclo.setCicloAcademico(ciclo);
            cursoCiclo.setCurso(curso);
            cursoCiclo.setEstadoEnum(EstadoEnum.ACT);
            cursoCiclo.setHorasCiclo(form.getHorasDictado());
            cursoCiclo.setHorasSemanalesTeoria(0);
            cursoCiclo.setHorasSemanalesPractica(0);
            cursoCiclo.setCreditos(0);
            cursoCicloAcademicoDAO.save(cursoCiclo);

        } else {
            if (cursoCiclo.getHorasCiclo() != form.getHorasDictado().intValue()) {
                cursoCiclo.setHorasCiclo(form.getHorasDictado());
                cursoCicloAcademicoDAO.update(cursoCiclo);
            }
        }

        form.setCursoCiclo(cursoCiclo);

        Docente docente = this.getDocente(form.getDocente());
        form.setDocente(docente);

        List<HorarioCurso> horarios = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);
        if (!horarios.isEmpty()) {
            Assert.isFalse(form.getHorasDictado() == 0, "La cantidad de horas no puede ser igual a CERO");
            Assert.isTrue(form.getHorasDictado() + 1 == horarios.size(), "La cantidad de horas no corresponde con el horario");
        }

        String codigo = this.getCode(ciclo);

        form.setCodigo(codigo);
        form.setMatriculados(0);
        form.setDisponibles(form.getVacantes());
        form.setEstadoEnum(SeccionEstadoEnum.CRE);
        form.setUserRegistro(ds.getUsuario());
        form.setFechaRegistro(new Date());
        cursoNivelacionDAO.save(form);

        if (horarios.isEmpty() || aula == null) {
            return;
        }

        Map<String, List<HorarioAula>> mapHorarios = this.getMapHorarioAula(horarios, aula);

        for (HorarioCurso horario : horarios) {
            Dia dia = horario.getDia();
            Hora hora = horario.getHora();

            Date fechaInicio = getLunes(horario.getSemana());
            Date fechaFin = getDomingo(horario.getSemana());

            LocalDate fecha = new LocalDate(horario.getSemana());
            this.verificarCruce(mapHorarios, fecha, aula, dia, hora);

            HorarioAula horarioAula = new HorarioAula();
            horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
            horarioAula.setTipoEnum(TipoHorarioAulaEnum.DICT);
            horarioAula.setAula(aula);
            horarioAula.setCursoNivelacion(form);
            horarioAula.setDia(dia);
            horarioAula.setHora(hora);
            horarioAula.setFechaInicio(fechaInicio);
            horarioAula.setFechaFin(fechaFin);
            horarioAulaDAO.save(horarioAula);
        }
    }

    private String getCode(CicloAcademico ciclo) {
        CursoNivelacion cursoUltimo = cursoNivelacionDAO.findLastByCiclo(ciclo);
        if (cursoUltimo == null) {
            return "4000";
        }

        Integer numero = Integer.parseInt(cursoUltimo.getCodigo()) + 1;
        return numero + "";
    }

    @Override
    @Transactional
    public void setHorario(CursoCicloAcademico form, CicloAcademico ciclo, DataSessionPivot ds) {
        CursoCicloAcademico cursoCiclo = cursoCicloAcademicoDAO.find(form.getId());
        Assert.isNotNull(cursoCiclo, "No existe el registro del curso y ciclo seleccionado");
        Assert.isTrue(ciclo.getId().equals(cursoCiclo.getCicloAcademico().getId()), "El registro no corresponde al ciclo correcto");
        Assert.isNotNull(form.getHorarios(), "No ha indicado el horario del curso");
        Assert.isNotNull(form.getGrupoHoras(), "No ha indicado el grupo de horario");
        Assert.isNotNull(form.getGrupoHoras().getId(), "No ha indicado el grupo de horario");
        List<HorarioCurso> horarios = form.getHorarios();

        GrupoHorasNivelacion grupoHoras = grupoHorasNivelacionDAO.find(form.getGrupoHoras().getId());
        Assert.isNotNull(grupoHoras, "No existe el grupo horario del curso");

        Assert.isFalse(horarios.isEmpty(), "El horario está vacío");
        Assert.isFalse(cursoCiclo.getHorasCiclo() == 0, "La cantidad de horas del curso no puede ser igual a CERO");
        Assert.isTrue(cursoCiclo.getHorasCiclo() + 1 == horarios.size(), "La cantidad de horas no corresponde con el horario");

        horarios.forEach(hor -> {
            Assert.isNotNull(hor.getDia(), "No ha indicado el día del horario");
            Assert.isNotNull(hor.getDia().getId(), "No ha indicado el día del horario");
            Assert.isNotNull(hor.getHora(), "No ha indicado la hora del horario");
            Assert.isNotNull(hor.getHora().getId(), "No ha indicado la hora del horario");

            Date fechaInicio = getLunes(hor.getSemana());
            hor.setSemana(fechaInicio);
        });

        Map<String, HorarioCurso> mapHorarioCurso = horarios.stream()
                .collect(Collectors.toMap(hor -> hor.getKey(), Function.identity()));

        List<HorarioCurso> horariosGpoHorario = horarioCursoDAO.allByCicloHorario(ciclo, grupoHoras);
        List<HorarioCurso> horariosOtros = horariosGpoHorario.stream()
                .filter(hor -> !hor.getCursoCiclo().equals(cursoCiclo))
                .collect(Collectors.toList());

        horariosOtros.forEach(hor -> {
            Dia dia = hor.getDia();
            Hora hora = hor.getHora();
            String key = hor.getKey();
            String fecha = TypesUtil.getStringDate(hor.getSemana(), "dd 'de' MMM", "es");
            HorarioCurso hc = mapHorarioCurso.get(key);
            Assert.isNull(hc, "Hay cruce de horario el " + dia.getSimboloAbr() + " a las " + hora.getDescripcion() + " de la semana del " + fecha);
        });

        List<HorarioCurso> horariosBD = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);
        horariosBD.forEach(hc -> System.out.println("bd-key=" + hc.getKey()));
        horarios.forEach(hc -> System.out.println("form-key=" + hc.getKey()));

        ListsInspector inspector = TypesUtil.analizeLists(horariosBD, horarios, "key");

        List<HorarioCurso> nuevos = inspector.getNewList();
        nuevos.forEach(hc -> System.out.println("new-key=" + hc.getKey()));
        List<HorarioCurso> eliminables = inspector.getDeadList();
        eliminables.forEach(hc -> System.out.println("del-key=" + hc.getKey()));

        Date fechaInicio = horarios.stream()
                .map(hor -> hor.getSemana())
                .min(Comparator.naturalOrder())
                .orElse(null);
        Date fechaMax = horarios.stream()
                .map(hor -> hor.getSemana())
                .max(Comparator.naturalOrder())
                .orElse(null);
        Date fechaFin = getDomingo(fechaMax);

        List<CursoNivelacion> cursosNiv = cursoNivelacionDAO.allByCursoCiclo(cursoCiclo, grupoHoras);
        cursosNiv.forEach(cn -> {
            cn.setFechaInicio(fechaInicio);
            cn.setFechaFin(fechaFin);
            cursoNivelacionDAO.update(cn);
        });

        List<Docente> docentes = cursosNiv.stream()
                .map(cn -> cn.getDocente())
                .distinct()
                .collect(Collectors.toList());

        for (Docente docente : docentes) {
            if (docente.isCodigoNN()) {
                continue;
            }
            String cruce = this.getCruceDocente(docente, ciclo, nuevos);
        }

        List<HorarioAula> horariosAulas = horarioAulaDAO.allByCursosNivelacion(cursosNiv);

        eliminables.forEach(hor -> {
            List<HorarioAula> horariosAulasDelete = horariosAulas.stream()
                    .filter(ha -> ha.getDia().equals(hor.getDia()))
                    .filter(ha -> ha.getHora().equals(hor.getHora()))
                    .filter(ha -> ha.getFechaInicio().equals(hor.getSemana()))
                    .collect(Collectors.toList());

            horarioAulaDAO.deleteAllInList(horariosAulasDelete);
            horarioCursoDAO.delete(hor);
        });

        List<Aula> aulas = cursosNiv.stream()
                .filter(cur -> cur.getAula() != null)
                .map(cur -> cur.getAula())
                .distinct()
                .collect(Collectors.toList());

        Map<String, List<HorarioAula>> mapHorarioAula = getMapHorarioAulas(nuevos, aulas);

        nuevos.forEach(hc -> {
            hc.setGrupoHoras(grupoHoras);
            hc.setCursoCiclo(cursoCiclo);
            hc.setUserRegistro(ds.getUsuario());
            hc.setFechaRegistro(new Date());
            horarioCursoDAO.save(hc);

            Dia dia = diaDAO.find(hc.getDia().getId());
            Hora hora = horaDAO.find(hc.getHora().getId());

            Date inicioSemana = getLunes(hc.getSemana());
            Date finSemana = getDomingo(hc.getSemana());
            LocalDate fecha = new LocalDate(hc.getSemana());

            for (CursoNivelacion cursoNiv : cursosNiv) {
                Aula aula = cursoNiv.getAula();
                if (aula == null) {
                    continue;
                }

                this.verificarCruceAula(mapHorarioAula, fecha, aula, dia, hora);

                HorarioAula horarioAula = new HorarioAula();
                horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
                horarioAula.setTipoEnum(TipoHorarioAulaEnum.DICT);
                horarioAula.setAula(cursoNiv.getAula());
                horarioAula.setCursoNivelacion(cursoNiv);
                horarioAula.setDia(hc.getDia());
                horarioAula.setHora(hc.getHora());
                horarioAula.setFechaInicio(inicioSemana);
                horarioAula.setFechaFin(finSemana);
                horarioAulaDAO.save(horarioAula);
            }
        });

    }

    @Override
    @Transactional
    public void changeGrupo(CursoNivelacion form, DataSessionPivot ds) {
        Assert.isNotNull(form.getGrupoHoras(), "No ha indicado el grupo nuevo");
        Assert.isNotNull(form.getGrupoHoras().getId(), "No ha indicado el grupo nuevo");
        GrupoHorasNivelacion grupoHoras = grupoHorasNivelacionDAO.find(form.getGrupoHoras().getId());
        Assert.isNotNull(grupoHoras, "No existe el registro del nuevo grupo");

        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(cursoNiv, "No existe el registro que desea modificar");
        GrupoHorasNivelacion grupoHorasBD = cursoNiv.getGrupoHoras();
        Assert.isFalse(grupoHorasBD.getId().equals(grupoHoras.getId()), "El grupo debe ser distinto");

        List<HorarioAula> horariosAntes = horarioAulaDAO.allByCursoNivelacion(cursoNiv);
        for (HorarioAula horario : horariosAntes) {
            horarioAulaDAO.delete(horario);
        }

        cursoNiv.setGrupoHoras(grupoHoras);
        cursoNivelacionDAO.update(cursoNiv);

        CursoCicloAcademico cursoCiclo = cursoNiv.getCursoCiclo();
        List<HorarioCurso> horarios = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);

        Aula aula = cursoNiv.getAula();
        if (horarios.isEmpty() || aula == null) {
            return;
        }

        Map<String, List<HorarioAula>> mapHorarios = this.getMapHorarioAula(horarios, aula);

        for (HorarioCurso horario : horarios) {
            Dia dia = horario.getDia();
            Hora hora = horario.getHora();

            Date fechaInicio = getLunes(horario.getSemana());
            Date fechaFin = getDomingo(horario.getSemana());
            LocalDate fecha = new LocalDate(horario.getSemana());

            this.verificarCruce(mapHorarios, fecha, aula, dia, hora);

            HorarioAula horarioAula = new HorarioAula();
            horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
            horarioAula.setTipoEnum(TipoHorarioAulaEnum.DICT);
            horarioAula.setAula(aula);
            horarioAula.setCursoNivelacion(cursoNiv);
            horarioAula.setDia(horario.getDia());
            horarioAula.setHora(horario.getHora());
            horarioAula.setFechaInicio(fechaInicio);
            horarioAula.setFechaFin(fechaFin);
            horarioAulaDAO.save(horarioAula);
        }

    }

    @Override
    @Transactional
    public void changeAula(CursoNivelacion form, DataSessionPivot ds) {
        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(cursoNiv, "No existe el registro que desea modificar");
        Assert.isFalse(sonAulasIguales(form.getAula(), cursoNiv.getAula()), "El aula nueva es la que ya estaba asignada");

        Aula aula = this.getAula(form);
        if (aula != null) {
            Assert.isNotNull(aula.getCapacidadAula(), "Esta aula no tiene configurada su capacidad");
            Assert.isTrue(cursoNiv.getVacantes() <= aula.getCapacidadAula(), "La cantidad de vacantes no puede ser mayor que la capacidad del aula");
        }

        cursoNiv.setAula(aula);
        cursoNivelacionDAO.update(cursoNiv);

        List<HorarioAula> horariosAntes = horarioAulaDAO.allByCursoNivelacion(cursoNiv);
        if (aula == null && horariosAntes.isEmpty()) {
            return;
        }

        for (HorarioAula horario : horariosAntes) {
            horarioAulaDAO.delete(horario);
        }

        if (aula == null) {
            return;
        }

        GrupoHorasNivelacion grupoHoras = cursoNiv.getGrupoHoras();
        CursoCicloAcademico cursoCiclo = cursoNiv.getCursoCiclo();
        List<HorarioCurso> horarios = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);

        Map<String, List<HorarioAula>> mapHorarios = getMapHorarioAula(horarios, aula);

        for (HorarioCurso horario : horarios) {
            Dia dia = horario.getDia();
            Hora hora = horario.getHora();

            Date fechaInicio = getLunes(horario.getSemana());
            Date fechaFin = getDomingo(horario.getSemana());
            LocalDate fecha = new LocalDate(horario.getSemana());

            this.verificarCruce(mapHorarios, fecha, aula, dia, hora);

            HorarioAula horarioAula = new HorarioAula();
            horarioAula.setEstadoEnum(EstadoHorarioAulaEnum.ACT);
            horarioAula.setTipoEnum(TipoHorarioAulaEnum.DICT);
            horarioAula.setAula(aula);
            horarioAula.setCursoNivelacion(cursoNiv);
            horarioAula.setDia(horario.getDia());
            horarioAula.setHora(horario.getHora());
            horarioAula.setFechaInicio(fechaInicio);
            horarioAula.setFechaFin(fechaFin);
            horarioAulaDAO.save(horarioAula);
        }

    }

    @Override
    @Transactional
    public void changeDocente(CursoNivelacion form, DataSessionPivot ds) {
        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(cursoNiv, "No existe el registro que desea modificar");
        Assert.isNotNull(form.getDocente(), "No ha indicado el docente nuevo");
        Assert.isNotNull(form.getDocente().getId(), "No ha indicado el docente nuevo");

        Docente docente = this.getDocente(form.getDocente());
        Docente docenteAntes = cursoNiv.getDocente();
        Assert.isFalse(docente.equals(docenteAntes), "El docente es el mismo que ya está asignado");

        if (docente.isCodigoNN()) {
            cursoNiv.setDocente(docente);
            cursoNivelacionDAO.update(cursoNiv);
            return;
        }

        CursoCicloAcademico cursoCiclo = cursoNiv.getCursoCiclo();
        GrupoHorasNivelacion grupoHoras = cursoNiv.getGrupoHoras();
        List<HorarioCurso> horariosCurso = horarioCursoDAO.allByCursoCicloHorario(cursoCiclo, grupoHoras);

        if (horariosCurso.isEmpty()) {
            cursoNiv.setDocente(docente);
            cursoNivelacionDAO.update(cursoNiv);
            return;
        }

        CicloAcademico ciclo = cursoNiv.getCursoCiclo().getCicloAcademico();
        String cruce = this.getCruceDocente(docente, ciclo, horariosCurso);
        Assert.isNull(cruce, cruce);

        cursoNiv.setDocente(docente);
        cursoNivelacionDAO.update(cursoNiv);
    }

    @Override
    @Transactional
    public void changeEstado(CursoNivelacion form, SeccionEstadoEnum estadoEnum, DataSessionPivot ds) {
        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(cursoNiv, "No existe el registro que desea modificar");

        switch (estadoEnum) {
            case ACT:
                Assert.isFalse(cursoNiv.getEstadoEnum() == ACT, "Esta sección ya se encuentra activa");
                Assert.isTrue(cursoNiv.getEstadoEnum() == CRE, "Esta sección debe encontrarse en estado CREADO");
                Assert.isNotNull(cursoNiv.getAula(), "Esta sección debe tener un aula asignada");
                this.registrarCambio(cursoNiv, estadoEnum, ds);
                break;

            case R_ACT:
                Assert.isFalse(cursoNiv.getEstadoEnum() == ACT, "Esta sección ya se encuentra activa");
                Assert.isTrue(cursoNiv.getEstadoEnum() == CAN, "Solo se puede reactivar secciones canceladas");
                Assert.isNotNull(cursoNiv.getAula(), "Esta sección debe tener un aula asignada");
                this.registrarCambio(cursoNiv, ACT, ds);
                break;

            case D_BLO:
                Assert.isFalse(cursoNiv.getEstadoEnum() == ACT, "Esta sección ya se encuentra activa");
                Assert.isTrue(cursoNiv.getEstadoEnum() == BLO, "Solo se puede desbloquear secciones bloqueadas");
                Assert.isNotNull(cursoNiv.getAula(), "Esta sección debe tener un aula asignada");
                this.registrarCambio(cursoNiv, ACT, ds);
                break;

            case BLO:
                Assert.isFalse(cursoNiv.getEstadoEnum() == BLO, "Esta sección ya se encuentra bloqueada");
                Assert.isTrue(cursoNiv.getEstadoEnum() == ACT, "Solo se puede bloquear secciones activas");
                this.registrarCambio(cursoNiv, estadoEnum, ds);
                break;

            case CAN:
                Assert.isFalse(cursoNiv.getEstadoEnum() == CAN, "Esta sección ya se encuentra cancelada");
                Assert.isTrue(cursoNiv.getEstadoEnum() == ACT, "Solo se puede cancelar secciones activas");
                Assert.isTrue(cursoNiv.getMatriculados() == 0, "Esta sección tiene matriculados, no puede cancelarse");

                int edad = this.getEdadMinutos(cursoNiv);
                if (edad < 30) {
                    this.eliminarCursoNivelacion(cursoNiv, ds);
                } else {
                    this.registrarCambio(cursoNiv, estadoEnum, ds);
                }
                break;

            default:
                Assert.isTrue(false, "Tipo de cambio no considerado");
        }
    }

    @Override
    public List<Dia> allDias() {
        return diaDAO.all();
    }

    @Override
    public List<Hora> allHoras() {
        return horaDAO.allByTipo(TipoHoraEnum.H60);
    }

    @Override
    public List<PeriodoDTO> allSemanas(CursoNivelacion form, DataSessionPivot ds) {
        CursoNivelacion cursoNiv = cursoNivelacionDAO.find(form.getId());
        Assert.isNotNull(cursoNiv, "No existe el registro que desea modificar");

        List<PeriodoDTO> semanas = new ArrayList();

        List<HorarioCurso> horarios = horarioCursoDAO.allByCursoCicloHorario(cursoNiv.getCursoCiclo(), cursoNiv.getGrupoHoras());
        if (horarios.isEmpty()) {
            PeriodoDTO periodo = new PeriodoDTO(cursoNiv.getFechaInicio());
            periodo.calcular();
            semanas.add(periodo);

        } else {
            List<Date> fechas = horarios.stream()
                    .map(hc -> hc.getSemana())
                    .distinct()
                    .collect(Collectors.toList());

            for (Date fecha : fechas) {
                PeriodoDTO periodo = new PeriodoDTO(fecha);
                periodo.calcular();
                semanas.add(periodo);
            }
        }

        return semanas;
    }

    @Override
    public List<PeriodoDTO> addSemana(List<PeriodoDTO> semanas, String direccion) {
        if (direccion.equalsIgnoreCase("antes")) {
            LocalDate fecha = new LocalDate(semanas.get(0).getFechaInicio()).minusDays(1);
            PeriodoDTO nuevo = new PeriodoDTO(fecha.toDate());
            nuevo.calcular();
            semanas.add(0, nuevo);
            return semanas;
        }

        if (direccion.equalsIgnoreCase("despues")) {
            LocalDate fecha = new LocalDate(semanas.get(semanas.size() - 1).getFechaFin()).plusDays(1);
            PeriodoDTO nuevo = new PeriodoDTO(fecha.toDate());
            nuevo.calcular();
            semanas.add(nuevo);
            return semanas;
        }

        return null;
    }

    private Integer getEdadMinutos(CursoNivelacion cursoNiv) {
        if (cursoNiv.getCambios() != null) {
            return 7 * 24 * 60;
        }

        DateTime fechaRegistro = new DateTime(cursoNiv.getFechaRegistro());
        DateTime ahora = new DateTime();
        return Minutes.minutesBetween(fechaRegistro, ahora).getMinutes();
    }

    private void registrarCambio(CursoNivelacion cursoNiv, SeccionEstadoEnum estadoEnum, DataSessionPivot ds) {
        cursoNiv.setEstadoEnum(estadoEnum);
        cursoNiv.setUserModificacion(ds.getUsuario());
        cursoNiv.setFechaModificacion(new Date());
        cursoNivelacionDAO.update(cursoNiv);
    }

    private void eliminarCursoNivelacion(CursoNivelacion cursoNiv, DataSessionPivot ds) {
        Assert.isTrue(cursoNiv.getMatriculados() == 0, "Esta sección tiene matriculados, no puede cancelarse");
        Assert.isNull(cursoNiv.getCambios(), "Esta sección ya tuvo modificaciones, no puede ser eliminado");

        List<HorarioAula> horarios = horarioAulaDAO.allByCursoNivelacion(cursoNiv);
        for (HorarioAula horario : horarios) {
            horarioAulaDAO.delete(horario);
        }

        cursoNivelacionDAO.delete(cursoNiv);
    }

    private void verificarCruceAula(Map<String, List<HorarioAula>> mapHorarios, LocalDate fecha, Aula aula, Dia dia, Hora hora) {
        Date fechaInicio = fecha.withDayOfWeek(DateTimeConstants.MONDAY).toDate();

        String key = aula.getId() + "-" + fecha.toString("yyyyMMdd");
        List<HorarioAula> horariosSemana = mapHorarios.get(key);
        List<HorarioAula> horariosCruce = horariosSemana.stream()
                .filter(ha -> ha.getDia().getId().equals(dia.getId()))
                .filter(ha -> ha.getHora().getId().equals(hora.getId()))
                .collect(Collectors.toList());

        String fechaSemana = TypesUtil.getStringDate(fechaInicio, "dd 'de' MMM", "es");
        Assert.isTrue(horariosCruce.isEmpty(), "Hay cruce de horario en el aula " + aula.getCodigo()
                + " el " + dia.getSimboloAbr() + " a las " + hora.getDescripcion()
                + " de la semana del " + fechaSemana);
    }

    private void verificarCruce(Map<String, List<HorarioAula>> mapHorarios, LocalDate fecha, Aula aula, Dia dia, Hora hora) {
        String cruce = this.getCruceAula(mapHorarios, fecha, aula, dia, hora);
        Assert.isNull(cruce, cruce);
    }

    private String getCruceAula(Map<String, List<HorarioAula>> mapHorarios, LocalDate fecha, Aula aula, Dia dia, Hora hora) {
        Date fechaInicio = fecha.withDayOfWeek(DateTimeConstants.MONDAY).toDate();

        List<HorarioAula> horariosSemana = mapHorarios.get(fecha.toString("yyyyMMdd"));
        List<HorarioAula> horariosCruce = horariosSemana.stream()
                .filter(hor -> hor.getDia().getId().equals(dia.getId()))
                .filter(hor -> hor.getHora().getId().equals(hora.getId()))
                .collect(Collectors.toList());

        String fechaSemana = TypesUtil.getStringDate(fechaInicio, "dd 'de' MMM", "es");

        if (!horariosCruce.isEmpty()) {
            return "Hay cruce de horario en el aula " + aula.getCodigo()
                    + " el " + dia.getSimboloAbr() + " a las " + hora.getDescripcion()
                    + " de la semana del " + fechaSemana;
        }

        return null;
    }

    private String getCruceDocente(Docente docente, CicloAcademico ciclo, List<HorarioCurso> horariosCurso) {
        List<CursoNivelacion> cursosNivDoc = cursoNivelacionDAO.allByDocenteCiclo(docente, ciclo);

        List<CursoCicloGrupoDTO> cursosDocente = cursosNivDoc.stream()
                .map(cn -> {
                    return new CursoCicloGrupoDTO(cn.getCursoCiclo(), cn.getGrupoHoras());
                })
                .distinct()
                .collect(Collectors.toList());

        Map<String, HorarioCurso> mapHorarios = new HashMap();
        for (CursoCicloGrupoDTO ccg : cursosDocente) {
            List<HorarioCurso> horariosOtroCurso = horarioCursoDAO.allByCursoCicloHorario(ccg.getCursoCiclo(), ccg.getGrupoHoras());
            for (HorarioCurso horarioCur : horariosOtroCurso) {
                String key = horarioCur.getKey();
                mapHorarios.put(key, horarioCur);
            }
        }

        for (HorarioCurso horario : horariosCurso) {
            String fechaSemana = TypesUtil.getStringDate(horario.getSemana(), "dd 'de' MMM", "es");
            String key = horario.getKey();
            HorarioCurso otroHorario = mapHorarios.get(key);

            if (otroHorario != null) {
                Dia dia = this.getDiaBD(horario.getDia());
                Hora hora = this.getHoraBD(horario.getHora());

                return "Hay cruce de horario del docente " + docente.getCodigo()
                        + " el " + dia.getSimboloAbr() + " a las " + hora.getDescripcion()
                        + " de la semana del " + fechaSemana;
            }
        }
        return null;
    }

    private Dia getDiaBD(Dia dia) {
        if (dia.getSimbolo() == null) {
            return diaDAO.find(dia.getId());
        }
        return dia;
    }

    private Hora getHoraBD(Hora hora) {
        if (hora.getDescripcion() == null) {
            return horaDAO.find(hora.getId());
        }
        return hora;
    }

    private Map<String, List<HorarioAula>> getMapHorarioAulas(List<HorarioCurso> horarios, List<Aula> aulas) {
        Map<String, List<HorarioAula>> mapHorarioAula = new HashMap();
        horarios.stream()
                .map(hc -> hc.getSemana())
                .distinct()
                .forEach(fechaInicio -> {
                    LocalDate fecha = new LocalDate(fechaInicio);
                    Date fechaFin = getDomingo(fechaInicio);
                    for (Aula aula : aulas) {
                        List<HorarioAula> horariosSemana = horarioAulaDAO.allByRango(fechaInicio, fechaFin, aula);
                        String key = aula.getId() + "-" + fecha.toString("yyyyMMdd");
                        mapHorarioAula.put(key, horariosSemana);
                    }
                });

        return mapHorarioAula;
    }

    private Map<String, List<HorarioAula>> getMapHorarioAula(List<HorarioCurso> horarios, Aula aula) {
        Map<String, List<HorarioAula>> mapHorarios = new HashMap();

        horarios.stream()
                .map(hc -> hc.getSemana())
                .distinct()
                .forEach(fechaInicio -> {
                    LocalDate fecha = new LocalDate(fechaInicio);
                    Date fechaFin = getDomingo(fechaInicio);
                    List<HorarioAula> horariosSemana = horarioAulaDAO.allByRango(fechaInicio, fechaFin, aula);
                    mapHorarios.put(fecha.toString("yyyyMMdd"), horariosSemana);
                });

        return mapHorarios;
    }

    private Aula getAula(CursoNivelacion cursoNiv) {
        Aula aula = null;
        if (cursoNiv.getAula() != null && cursoNiv.getAula().getId() != null) {
            aula = aulaDAO.find(cursoNiv.getAula().getId());
        }
        return aula;
    }

    private Curso getCurso(CursoCicloAcademico cursoCiclo) {
        Assert.isNotNull(cursoCiclo, "Debe indicar el curso");
        Assert.isNotNull(cursoCiclo.getCurso(), "Debe indicar el curso");
        Assert.isNotNull(cursoCiclo.getCurso().getId(), "Debe indicar el curso");

        Curso curso = cursoDAO.find(cursoCiclo.getCurso().getId());
        Assert.isNotNull(curso, "El ID del curso no existe en el sistema");
        Assert.isNotNull(curso.getModalidadEstudio(), "El curso seleccionado no tiene modalidad de estudio");
        Assert.isTrue(curso.getModalidadEstudio().isNivelaIngresantes(), "El curso seleccionado debe corresponder a Cursos de Nivelación");

        return curso;
    }

    private GrupoHorasNivelacion getGrupoHoras(GrupoHorasNivelacion form) {
        Assert.isNotNull(form, "No ha indicado el grupo de horario");
        Assert.isNotNull(form.getId(), "No ha indicado el grupo de horario");
        GrupoHorasNivelacion grupoHoras = grupoHorasNivelacionDAO.find(form.getId());
        Assert.isNotNull(grupoHoras, "No existe el registro del grupo de horario");

        return grupoHoras;
    }

    private Docente getDocente(Docente docenteForm) {
        Assert.isNotNull(docenteForm, "No ha indicado el docente");
        Assert.isNotNull(docenteForm.getCodigo(), "No ha indicado el docente");

        Docente docente = docenteDAO.findByCode(docenteForm.getCodigo());
        Assert.isNotNull(docente, "No se pudo ubicar el registro del docente seleccionado");
        Assert.isNotNull(docente.getModalidadEstudio(), "El docente seleccionado debe pertenecer a alguna modalidad");
        Assert.isTrue(docente.getModalidadEstudio().isPregrado(), "El docente seleccionado debe pertenecer a pregrado");

        return docente;
    }

    private boolean sonAulasIguales(Aula aulaForm, Aula aulaBD) {
        if (aulaBD == null && aulaForm == null) {
            return true;
        }
        if (aulaBD != null && aulaForm == null) {
            return false;
        }
        if (aulaBD == null && aulaForm != null) {
            return false;
        }

        Assert.isNotNull(aulaForm.getId(), "No ha indicado el ID del aula nueva");
        return aulaBD.getId().equals(aulaForm.getId());
    }

    private Date getLunes(Date fechaReferencia) {
        LocalDate fecha = new LocalDate(fechaReferencia);
        return fecha.withDayOfWeek(DateTimeConstants.MONDAY).toDate();
    }

    private Date getDomingo(Date fechaReferencia) {
        LocalDate fecha = new LocalDate(fechaReferencia);
        return fecha.withDayOfWeek(DateTimeConstants.SUNDAY).toDate();
    }

}
