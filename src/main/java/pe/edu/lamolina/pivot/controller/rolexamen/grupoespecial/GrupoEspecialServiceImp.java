package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularConnector;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GrupoEspecialServiceImp implements GrupoEspecialService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Autowired
    SeccionExcluidoDAO seccionExcluidoDAO;

    @Autowired
    GrupoHorasExamenDAO grupoHorasExamenDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    GrupoRegularConnector grupoRegularConnector;

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    public RolExamenes findRolExamenes(long rolExamenId) {
        RolExamenes rolExamenes = rolExamenesDAO.find(rolExamenId);
        List<SemanaExamen> semanaExamens = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanaExamens);
        return rolExamenes;
    }

    @Override
    public List<SeccionGrupoEspecial> allSeccionesGrupoEspecialByRolExamenes(DynatableFilter filter, RolExamenes rolExamenes) {
        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByDynatableAndRolExamenes(filter, rolExamenes);

        List<FechaHoraGrupoExamen> fechasHorasGrupos = fechaHoraGrupoExamenDAO
                .allByGrupoHorasExamen(seccionesGrupoEspecial.stream()
                        .filter(x -> ObjectUtil.getParentTree(x, "grupoHorasExamen.id") != null)
                        .map(x -> x.getGrupoHorasExamen()).collect(Collectors.toList()));
        Map<Long, List<FechaHoraGrupoExamen>> mapFechasHorasGruposByGrupoHoras = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHorasGrupos);

        Map<Long, Integer> mapAlumnosBySeccion = alumnoGrupoEspecialDAO.countBySeccionesGrupoEspecial(seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum.ACT);
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecial) {
            seccionGrupoEspecial.setAlumnosEspecialesActivosCount(mapAlumnosBySeccion.get(seccionGrupoEspecial.getId()) == null ? 0 : mapAlumnosBySeccion.get(seccionGrupoEspecial.getId()));

            if (ObjectUtil.getParentTree(seccionGrupoEspecial, "grupoHorasExamen.id") != null) {
                List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = mapFechasHorasGruposByGrupoHoras.get(seccionGrupoEspecial.getGrupoHorasExamen().getId());
                seccionGrupoEspecial.getGrupoHorasExamen().setSemanaExamen(fechasHorasGrupoExamen.get(0).getSemanaExamen());
            }
        }
        return seccionesGrupoEspecial;
    }

    @Override
    public void deleteGrupoEspecial(RolExamenes rolExamenes) {
        List<SeccionGrupoEspecial> seccionesExcluidas = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.EXC);
        List<Seccion> secciones = seccionesExcluidas.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        if (!secciones.isEmpty()) {
            seccionExcluidoDAO.deleteBySecciones(secciones);
        }
        alumnoGrupoEspecialDAO.deleteByRolExamenes(rolExamenes);
        seccionGrupoEspecialDAO.deleteByRolExamenes(rolExamenes);
    }

    @Override
    @Transactional(readOnly = false)
    public void calcularExamenesGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(rolExamenes.isSituacionAsignarHorarioCursosMasivos(), "Debe asignar horario de examen a los cursos masivos.");

        this.rolExamenesLogger.iniciarCursoMasivo();

        List<SeccionExcluido> seccionesExcluidasByRolExamen = seccionExcluidoDAO.allByRolExamenes(rolExamenes);

        List<SemanaExamen> semanasByRolExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        List<SeccionGrupoEspecial> seccionesGrupoEspeciales = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        for (SeccionExcluido seccionExcluido : seccionesExcluidasByRolExamen) {
            seccionesGrupoEspeciales.removeIf(x -> x.getSeccion().equals(seccionExcluido.getSeccion()));
        }
        grupoRegularConnector.fillActiveInfoGrupoEspecial(seccionesGrupoEspeciales);

        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesGrupoEspeciales.stream().map(x -> x.getSeccion()).collect(Collectors.toList()));
        Map horariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);

        List<LetraGrupoRegular> letrasGrupoRegular = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        grupoRegularConnector.fillActiveInfoLetrasGruposRegulares(letrasGrupoRegular);
        List<CursoMasivoExamen> cursosMasivos = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        grupoRegularConnector.fillActiveInfoCursosMasivos(cursosMasivos);
        List<GrupoHorasExamen> gruposHorasExamenByRolExamenes = this.allGrupoHorasExamenByRolExamen(rolExamenes);

        int cont = 0;
        SECCIONES_ESPECIALES:
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspeciales) {
            logger.debug("################################################################################################");
            logger.debug("Seccion Grupo Especial {}, {} de {}", seccionGrupoEspecial.getId(), ++cont, seccionesGrupoEspeciales.size());
            List<Alumno> alumnos = seccionGrupoEspecial.getAlumnosGrupoEspecial().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
            List<Aula> aulas = Arrays.asList(seccionGrupoEspecial.getAula());
            List<Docente> docentes = Arrays.asList(seccionGrupoEspecial.getDocente());

            List<SeccionGrupoEspecial> othersSeccionesGruposEspecialesEspeciales = new ArrayList<>(seccionesGrupoEspeciales);
            othersSeccionesGruposEspecialesEspeciales.removeIf(x -> x.equals(seccionGrupoEspecial));

            Seccion seccion = seccionGrupoEspecial.getSeccion().clone();
            List<HorarioSeccion> horariosSeccion = this.allHorarioSeccionWithHours(seccion, rolExamenes, horariosBySeccion);
            seccion.setHorarioSeccion(horariosSeccion);
            seccionGrupoEspecial.setSeccion(seccion);

            SemanaExamen semanaExamenByHorSec = this.findSemanaExamenByHorarioSeccion(seccion.getHorarioSeccion(), semanasByRolExamen);

            boolean matching = this.processSeccionEspecialByWeek(semanaExamenByHorSec, seccionGrupoEspecial, gruposHorasExamenByRolExamenes, letrasGrupoRegular, cursosMasivos, othersSeccionesGruposEspecialesEspeciales, docentes, aulas, alumnos);
            if (matching) {
                continue;
            }

            for (SemanaExamen semanaExamen : semanasByRolExamen) {
                if (semanaExamen.equals(semanaExamenByHorSec)) {
                    continue;
                }
                matching = this.processSeccionEspecialByWeek(semanaExamen, seccionGrupoEspecial, gruposHorasExamenByRolExamenes, letrasGrupoRegular, cursosMasivos, othersSeccionesGruposEspecialesEspeciales, docentes, aulas, alumnos);
                if (matching) {
                    continue SECCIONES_ESPECIALES;
                }
            }

        }
        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_ESP);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    public boolean processSeccionEspecialByWeek(SemanaExamen semana, SeccionGrupoEspecial seccionGrupoEspecial,
            List<GrupoHorasExamen> gruposHorasExamenByRolExamenes,
            List<LetraGrupoRegular> letrasGrupoRegular, List<CursoMasivoExamen> cursosMasivos, List<SeccionGrupoEspecial> otherSeccionesGrupoEspeciales,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        Seccion seccion = seccionGrupoEspecial.getSeccion();

        Integer originalDay = seccion.getHorarioSeccion().get(0).getDia().getNumeroDia();
        int currentDay = -1;
        WHILE_MATCHING:
        while (originalDay != currentDay) {
            if (currentDay == -1) {
                currentDay = originalDay;
            }
            final int finalCurrentDay = currentDay;

            logger.debug("########################");
            logger.debug("Semana {}, Dia {}", semana.getNumeroSemana(), currentDay);

            List<GrupoHorasExamen> grupoHorasExamenByDayAndWeek = gruposHorasExamenByRolExamenes.stream()
                    .filter(x -> x.getSemanaExamen().equals(semana))
                    .filter(x -> x.getDia().getNumeroDia().compareTo(finalCurrentDay) == 0)
                    .collect(Collectors.toList());

            for (GrupoHorasExamen grupoHorasExamen : grupoHorasExamenByDayAndWeek) {
                logger.debug("Se intentara el match con {}", grupoHorasExamen.getGrupoHoras().getCodigo());
                LetraGrupoRegular letraGrupoRegularByGrupoExamen = letrasGrupoRegular.stream().filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen)).findFirst().orElse(null);
                List<CursoMasivoExamen> cursosMasivosByGrupoExamen = cursosMasivos.stream()
                        .filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen)).collect(Collectors.toList());
                List<SeccionGrupoEspecial> otherSeccionesGrupoEspecialByGrupoExamen = otherSeccionesGrupoEspeciales.stream()
                        .filter(x -> x.getGrupoHorasExamen() != null)
                        .filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen))
                        .collect(Collectors.toList());

                boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(cursosMasivosByGrupoExamen, docentes, aulas, alumnos);
                boolean validacionGrupoRegular = true;
                if (letraGrupoRegularByGrupoExamen != null) {
                    validacionGrupoRegular = grupoRegularConnector.validarGrupoRegular(letraGrupoRegularByGrupoExamen, alumnos, docentes, aulas);
                }
                boolean validacionSeccionesGpoEspecial = grupoRegularConnector.validarGrupoEspecial(otherSeccionesGrupoEspecialByGrupoExamen, alumnos, docentes, aulas);

                if (validacionCursosMasivos && validacionGrupoRegular && validacionSeccionesGpoEspecial) {
                    logger.debug("Se hizo el match con {}", grupoHorasExamen.getGrupoHoras().getCodigo());
                    SeccionGrupoEspecial seccionGrupoEspecialUpd = new SeccionGrupoEspecial();
                    seccionGrupoEspecialUpd.setId(seccionGrupoEspecial.getId());
                    seccionGrupoEspecialUpd.setGrupoHorasExamen(grupoHorasExamen);
                    seccionGrupoEspecialDAO.updateFechaExamen(seccionGrupoEspecialUpd);

                    return true;
                }
            }
            currentDay++;
            DateTime fechaIni = new DateTime(semana.getFechaInicio());
            DateTime fechaFina = new DateTime(semana.getFechaFin());
            if (currentDay > fechaFina.getDayOfWeek()) {
                currentDay = fechaIni.getDayOfWeek();
            }
        }
        return false;
    }

    public List<GrupoHorasExamen> allGrupoHorasExamenByRolExamen(RolExamenes rolExamenes) {
        //ordenar los grupos horas
        List<GrupoHorasExamen> gruposHorasExamen = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<FechaHoraGrupoExamen> fechasHorasExamens = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(gruposHorasExamen);
        Map< Long, List<FechaHoraGrupoExamen>> mapFechasHorasExamenes = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHorasExamens);

        for (GrupoHorasExamen grupoHorasExamen : gruposHorasExamen) {
            List<FechaHoraGrupoExamen> fechasHorasGruposByGrupoHoraExamen = mapFechasHorasExamenes.get(grupoHorasExamen.getId());
            grupoHorasExamen.setFechasHorasGruposExamen(fechasHorasGruposByGrupoHoraExamen);
            grupoHorasExamen.setSemanaExamen(fechasHorasGruposByGrupoHoraExamen.get(0).getSemanaExamen());
        }
        return gruposHorasExamen;
    }

    private List<HorarioSeccion> allHorarioSeccionWithHours(Seccion seccion, RolExamenes rolExamenes, Map horariosBySeccion) {
        List<HorarioSeccion> horariosSeccion = (List<HorarioSeccion>) horariosBySeccion.get(seccion.getId());

        Map<Long, List<HorarioSeccion>> mapGroupByDia = TypesUtil.convertListToMapList("dia.id", horariosSeccion);
        HorarioSeccion firsFound = horariosSeccion.get(0).clone();
        for (Map.Entry<Long, List<HorarioSeccion>> entry : mapGroupByDia.entrySet()) {
            Long key = entry.getKey();
            List<HorarioSeccion> value = entry.getValue();
            if (value.size() < rolExamenes.getHorasExamen()) {
                horariosSeccion.removeIf(x -> x.getDia().getId().compareTo(key) == 0);
            }
        }
        if (horariosSeccion.isEmpty()) {
            horariosSeccion.add(firsFound);
        }
        /*
        if (horariosSeccion.size() > rolExamenes.getHorasExamen()) {
            List<Dia> removeDays = new ArrayList<>();
            for (int i = rolExamenes.getHorasExamen(); i < horariosSeccion.size(); i++) {
                HorarioSeccion horarioSeccion = horariosSeccion.get(i);
                removeDays.add(horarioSeccion.getDia());
            }
            for (Dia removeDay : removeDays) {
                horariosSeccion.removeIf(x -> x.getDia().equals(removeDay));
            }
        }*/
        return horariosSeccion;
    }

    public SemanaExamen findSemanaExamenByHorarioSeccion(List<HorarioSeccion> horariosSeccion, List<SemanaExamen> semanasExamen) {

        SEMANA_EXAMEN:
        for (SemanaExamen semanaExamen : semanasExamen) {
            int wrong = 0;
            for (HorarioSeccion horarioSeccion : horariosSeccion) {
                if (horarioSeccion.getHora().getNumero() < semanaExamen.getHoraInicio().getNumero()
                        || horarioSeccion.getHora().getNumero() > semanaExamen.getHoraFin().getNumero()) {
                    wrong++;
                }
            }
            if (wrong != horariosSeccion.size()) {
                return semanaExamen;
            }
        }
        return null;
    }

    public List<HorarioSeccion> reasignarHorarioSeccion(List<HorarioSeccion> horariosSeccion, Integer numeroDia, Integer numeroHoraInicio) {
        List<HorarioSeccion> horariosSeccionClone = new ArrayList<>();
        HorarioSeccion previousHorarioSeccion = null;
        for (HorarioSeccion horarioSeccion : horariosSeccion) {
            HorarioSeccion horarioSeccionClone = horarioSeccion.clone();
            Dia dia = horarioSeccionClone.getDia().clone();
            dia.setNumeroDia(numeroDia);
            horarioSeccionClone.setDia(dia);
            if (previousHorarioSeccion == null) {
                Hora hora = horarioSeccion.getHora().clone();
                hora.setNumero(numeroHoraInicio);
                horarioSeccionClone.setHora(hora);
            } else {
                Hora hora = horarioSeccion.getHora().clone();
                hora.setNumero(previousHorarioSeccion.getHora().getNumero() + 1);
                horarioSeccionClone.setHora(hora);
            }
            previousHorarioSeccion = horarioSeccionClone;
            horariosSeccionClone.add(horarioSeccionClone);
        }
        return horariosSeccionClone;
    }

    @Override
    public List<AlumnoGrupoEspecial> allAlumnosGrupoEspecialDynaBySecGpoEsp(DynatableFilter filter, SeccionGrupoEspecial seccionGrupoEspecial) {
        List<AlumnoGrupoEspecial> alumnosGrupoEspecial = alumnoGrupoEspecialDAO.allByDynatableAndSeccionGrupoEsp(filter, seccionGrupoEspecial);
        return alumnosGrupoEspecial;
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirSeccionEspecial(SeccionGrupoEspecial seccionGrupoEspecial, DataSessionPivot ds) {
        seccionGrupoEspecial = seccionGrupoEspecialDAO.find(seccionGrupoEspecial.getId());

        RolExamenes rolExamenes = seccionGrupoEspecial.getRolExamenes();
        grupoRegularConnector.validarSituacion("excluir", "los grupos especiales", rolExamenes.isSituacionConfigurarGrupoEspecial());
        Assert.isTrue(seccionGrupoEspecial.isEstadoActivo(), "Solo se puede excluir las secciones especiales activas");

        SeccionGrupoEspecial seccionGrupoEspecialUpd = new SeccionGrupoEspecial(seccionGrupoEspecial.getId());
        //  seccionGrupoEspecialUpd.setUsuarioExclusion(ds.getUsuario());
        //   seccionGrupoEspecialUpd.setFechaExclusion(ds.getFechaAccionAudit());
        seccionGrupoEspecialDAO.updateEstadoExclusion(seccionGrupoEspecialUpd);

        SeccionExcluido seccionExcluido = new SeccionExcluido();
        seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
        seccionExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        seccionExcluido.setRolExamenes(seccionGrupoEspecial.getRolExamenes());
        seccionExcluido.setSeccion(seccionGrupoEspecial.getSeccion());
        seccionExcluido.setUserRegistro(ds.getUsuario());
        seccionExcluidoDAO.save(seccionExcluido);

        List<AlumnoGrupoEspecial> alumnosGrupoEspecialBySeccion = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionGrupoEspecial, AlumnoRolExamenEstadoEnum.ACT);
        for (AlumnoGrupoEspecial alumnoGrupoEspecial : alumnosGrupoEspecialBySeccion) {
            this.excluirAlumnoEspecial(alumnoGrupoEspecial, ds);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirAlumnoEspecial(AlumnoGrupoEspecial alumnoGrupoEspecial, DataSessionPivot ds) {
        alumnoGrupoEspecial = alumnoGrupoEspecialDAO.find(alumnoGrupoEspecial.getId());
        RolExamenes rolExamenes = alumnoGrupoEspecial.getSeccionGrupoEspecial().getRolExamenes();
        grupoRegularConnector.validarSituacion("excluir", "los grupos especiales", rolExamenes.isSituacionConfigurarGrupoEspecial());
        Assert.isTrue(alumnoGrupoEspecial.isEstadoActivo(), "Solo se puede excluir los alumnos especiales activos");

        AlumnoGrupoEspecial alumnoGrupoEspecialUpd = new AlumnoGrupoEspecial(alumnoGrupoEspecial.getId());
        alumnoGrupoEspecialDAO.updateEstadoExclusion(alumnoGrupoEspecialUpd);
    }

    @Override
    @Transactional(readOnly = false)
    public void activarSeccionEspecial(SeccionGrupoEspecial seccionGrupoEspecial, DataSessionPivot ds) {
        seccionGrupoEspecial = seccionGrupoEspecialDAO.find(seccionGrupoEspecial.getId());

        RolExamenes rolExamenes = seccionGrupoEspecial.getRolExamenes();
        grupoRegularConnector.validarSituacion("excluir", "los grupos especiales", rolExamenes.isSituacionConfigurarGrupoEspecial());
        Assert.isTrue(seccionGrupoEspecial.isEstadoExcluido(), "Solo se puede incluir las secciones especiales excluidas");

        SeccionGrupoEspecial seccionGrupoEspecialUpd = new SeccionGrupoEspecial(seccionGrupoEspecial.getId());
        seccionGrupoEspecialUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
        seccionGrupoEspecialDAO.updateEstado(seccionGrupoEspecialUpd);

        SeccionExcluido seccionExcluido = seccionExcluidoDAO.findBySeccion(seccionGrupoEspecial.getSeccion(), EstadoEnum.ACT);
        if (seccionExcluido != null) {
            seccionExcluido.setEstadoEnum(EstadoEnum.ANU);
            seccionExcluidoDAO.update(seccionExcluido);
        }

        List<AlumnoGrupoEspecial> alumnosGrupoEspecialBySeccion = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionGrupoEspecial, AlumnoRolExamenEstadoEnum.EXC);
        for (AlumnoGrupoEspecial alumnoGrupoEspecial : alumnosGrupoEspecialBySeccion) {
            this.activarAlumnoEspecial(alumnoGrupoEspecial, ds);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void activarAlumnoEspecial(AlumnoGrupoEspecial alumnoGrupoEspecial, DataSessionPivot ds) {
        alumnoGrupoEspecial = alumnoGrupoEspecialDAO.find(alumnoGrupoEspecial.getId());
        RolExamenes rolExamenes = alumnoGrupoEspecial.getSeccionGrupoEspecial().getRolExamenes();
        grupoRegularConnector.validarSituacion("excluir", "los grupos especiales", rolExamenes.isSituacionConfigurarGrupoEspecial());
        Assert.isTrue(alumnoGrupoEspecial.isEstadoExcluido(), "Solo se puede incluir los alumnos especiales excluidos");
        

        AlumnoGrupoEspecial alumnoGrupoEspecialUpd = new AlumnoGrupoEspecial(alumnoGrupoEspecial.getId());
        alumnoGrupoEspecialUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
        alumnoGrupoEspecialDAO.updateEstado(alumnoGrupoEspecialUpd);
    }
    
    public void validarActivarAlumno(){
        
    }

}
