package pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Dia;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
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
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
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

    @Autowired
    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    AulaDAO aulaDAO;

    private void checkNoPublicado(RolExamenes rol) {
        Assert.isTrue(rol.getEstadoEnum() != RolExamenesEstadoEnum.PUB, "El rol de exámenes ya ha sido publicado");
    }

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
    @Transactional
    public void deleteGrupoEspecial(RolExamenes rolExamenes) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolBD);

        List<SeccionGrupoEspecial> seccionesExcluidas = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.EXC);
        List<Seccion> secciones = seccionesExcluidas.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        if (!secciones.isEmpty()) {
            seccionExcluidoDAO.deleteBySecciones(secciones);
        }
        alumnoGrupoEspecialDAO.deleteByRolExamenes(rolExamenes);
        horarioAulaDAO.deleteSeccionesEspecialesByRolExamenes(rolExamenes);
        seccionGrupoEspecialDAO.deleteByRolExamenes(rolExamenes);
    }

    //Calcular secciones especiales, primer metodo
    @Override
    @Transactional
    public void calcularExamenesGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        //this.checkNoPublicado(rolExamenes);

//        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
//                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
//        if (!rolExamenes.isSituacionConfigurarGrupoEspecial()) {
//            Assert.isTrue(rolExamenes.isSituacionAsignarHorarioCursosMasivos(), "Debe asignar horario de examen a los cursos masivos.");
//        }
//        this.rolExamenesLogger.iniciarGrupoEspecial();
        List<Aula> aulasOera = grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolExamenes, OficinaEnum.OERA);
        rolExamenesLogger.setAulasOera(new ArrayList(aulasOera));

        Aula aulaMaxAforo = aulaDAO.findAulaMaxAforo(OficinaEnum.OERA, EstadoEnum.ACT);
        rolExamenesLogger.setMaximoAforoAula(aulaMaxAforo.getAforo());

        List<SeccionExcluido> seccionesExcluidasByRolExamen = seccionExcluidoDAO.allByRolExamenes(rolExamenes);

        List<SemanaExamen> semanasByRolExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        List<SeccionGrupoEspecial> seccionesGrupoEspeciales = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        for (SeccionExcluido seccionExcluido : seccionesExcluidasByRolExamen) {
            seccionesGrupoEspeciales.removeIf(x -> x.getSeccion().equals(seccionExcluido.getSeccion()));
        }
        grupoRegularConnector.fillActiveInfoGrupoEspecial(seccionesGrupoEspeciales);

        List<Aula> aulasAll = grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolExamenes, null);
        Date fechaInicio = semanasByRolExamen.stream().min(Comparator.comparing(SemanaExamen::getFechaInicio)).map(x -> x.getFechaInicio()).get();
        Date fechaFin = semanasByRolExamen.stream().max(Comparator.comparing(SemanaExamen::getFechaFin)).map(x -> x.getFechaFin()).get();
        List<HorarioAula> horariosAulasByRango = horarioAulaDAO.allByRangoNotByTipo(fechaInicio, fechaFin, TipoHorarioAulaEnum.EXAM, aulasAll);
        Map<Long, List<HorarioAula>> mapHorarioAulas = TypesUtil.convertListToMapList("aula.id", horariosAulasByRango);
        rolExamenesLogger.setHorarioAulas(mapHorarioAulas);

        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesGrupoEspeciales.stream().map(x -> x.getSeccion()).collect(Collectors.toList()));
        Map<Long, List<HorarioSeccion>> mapHorariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);

        List<LetraGrupoRegular> letrasGrupoRegular = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        grupoRegularConnector.fillActiveInfoLetrasGruposRegulares(letrasGrupoRegular);

        List<CursoMasivoExamen> cursosMasivos = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        grupoRegularConnector.fillActiveInfoCursosMasivos(cursosMasivos);

        List<GrupoHorasExamen> gruposHorasExamenByRolExamenes = this.allGrupoHorasExamenByRolExamen(rolExamenes);
        List<SeccionGrupoEspecial> seccionesGrupoEspecialesOut = new ArrayList();

        int cont = 0;
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspeciales) {
            logger.debug("################################################################################################");
            logger.debug("Seccion Grupo Especial {}, {} de {}", seccionGrupoEspecial.getId(), ++cont, seccionesGrupoEspeciales.size());
            if (seccionGrupoEspecial.getGrupoHorasExamen() != null) {
                logger.debug("\tYa tiene horario");
                continue;
            }

            List<Alumno> alumnos = seccionGrupoEspecial.getAlumnosGrupoEspecial().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
            List<Aula> aulas = Arrays.asList(seccionGrupoEspecial.getAula());
            List<Docente> docentes = Arrays.asList(seccionGrupoEspecial.getDocente());

            List<SeccionGrupoEspecial> othersSeccionesGruposEspecialesEspeciales = new ArrayList(seccionesGrupoEspeciales);
            othersSeccionesGruposEspecialesEspeciales.removeIf(x -> x.equals(seccionGrupoEspecial));

            Seccion seccion = seccionGrupoEspecial.getSeccion().clone();
            List<HorarioSeccion> horariosSeccion = this.allHorarioSeccionWithHours(seccion, rolExamenes, mapHorariosBySeccion);
            seccion.setHorarioSeccion(horariosSeccion);
            seccionGrupoEspecial.setSeccion(seccion);

            //semana que le corresponde segun el horario de la seccion
            SemanaExamen semanaExamenByHorSec = this.findSemanaExamenByHorarioSeccion(seccion.getHorarioSeccion(), semanasByRolExamen);
            boolean matching = this.processSeccionEspecialByWeek(
                    semanaExamenByHorSec,
                    seccionGrupoEspecial,
                    gruposHorasExamenByRolExamenes,
                    letrasGrupoRegular,
                    cursosMasivos,
                    othersSeccionesGruposEspecialesEspeciales,
                    docentes,
                    aulas,
                    alumnos, true, ds
            );
            if (matching) {
                continue;
            }
            seccionesGrupoEspecialesOut.add(seccionGrupoEspecial);

        }

        logger.debug("###### FIN PRIMER LOOP ESPECIAL ######");

        cont = 0;
        List<SeccionGrupoEspecial> seccionesGrupoEspeciales2Out = new ArrayList();
        SECCIONES_ESPECIALES:
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecialesOut) {
            logger.debug("################################################################################################");
            logger.debug("Seccion Grupo Especial {}, {} de {}", seccionGrupoEspecial.getId(), ++cont, seccionesGrupoEspecialesOut.size());
            if (seccionGrupoEspecial.getGrupoHorasExamen() != null) {
                logger.debug("\tYa tiene horario");
                continue;
            }

            List<Alumno> alumnos = seccionGrupoEspecial.getAlumnosGrupoEspecial().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
            List<Aula> aulas = Arrays.asList(seccionGrupoEspecial.getAula());
            List<Docente> docentes = Arrays.asList(seccionGrupoEspecial.getDocente());

            List<SeccionGrupoEspecial> othersSeccionesGruposEspecialesEspeciales = new ArrayList(seccionesGrupoEspeciales);
            othersSeccionesGruposEspecialesEspeciales.removeIf(x -> x.equals(seccionGrupoEspecial));

            Seccion seccion = seccionGrupoEspecial.getSeccion().clone();
            List<HorarioSeccion> horariosSeccion = this.allHorarioSeccionWithHours(seccion, rolExamenes, mapHorariosBySeccion);
            seccion.setHorarioSeccion(horariosSeccion);
            seccionGrupoEspecial.setSeccion(seccion);

            //semana que le corresponde segun el horario de la seccion
            SemanaExamen semanaExamenByHorSec = this.findSemanaExamenByHorarioSeccion(seccion.getHorarioSeccion(), semanasByRolExamen);
            boolean matching = this.processSeccionEspecialByWeek(
                    semanaExamenByHorSec,
                    seccionGrupoEspecial,
                    gruposHorasExamenByRolExamenes,
                    letrasGrupoRegular,
                    cursosMasivos,
                    othersSeccionesGruposEspecialesEspeciales,
                    docentes,
                    aulas,
                    alumnos, false, ds);
            if (matching) {
                continue;
            }

            //resto de semanas
            for (SemanaExamen semanaExamen : semanasByRolExamen) {
                if (semanaExamen.equals(semanaExamenByHorSec)) {
                    continue;
                }
                matching = this.processSeccionEspecialByWeek(
                        semanaExamen,
                        seccionGrupoEspecial,
                        gruposHorasExamenByRolExamenes,
                        letrasGrupoRegular,
                        cursosMasivos,
                        othersSeccionesGruposEspecialesEspeciales,
                        docentes,
                        aulas,
                        alumnos, false, ds);
                if (matching) {
                    continue SECCIONES_ESPECIALES;
                }
            }
            seccionesGrupoEspeciales2Out.add(seccionGrupoEspecial);
        }

        logger.debug("###### FIN SEGUNDO LOOP ESPECIAL ######");

//        if (1 == 1) {
//            return;
//        }
        cont = 0;
        SECCIONES_ESPECIALES:
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspeciales2Out) {
            logger.debug("################################################################################################");
            logger.debug("Seccion Grupo Especial {}, {} de {}", seccionGrupoEspecial.getId(), ++cont, seccionesGrupoEspeciales2Out.size());
            if (seccionGrupoEspecial.getGrupoHorasExamen() != null) {
                logger.debug("\tYa tiene horario");
                continue;
            }

            List<Alumno> alumnos = seccionGrupoEspecial.getAlumnosGrupoEspecial().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
            List<Aula> aulas = aulasOera;
            List<Docente> docentes = Arrays.asList(seccionGrupoEspecial.getDocente());

            List<SeccionGrupoEspecial> othersSeccionesGruposEspecialesEspeciales = new ArrayList(seccionesGrupoEspeciales);
            othersSeccionesGruposEspecialesEspeciales.removeIf(x -> x.equals(seccionGrupoEspecial));

            Seccion seccion = seccionGrupoEspecial.getSeccion().clone();
            List<HorarioSeccion> horariosSeccion = this.allHorarioSeccionWithHours(seccion, rolExamenes, mapHorariosBySeccion);
            seccion.setHorarioSeccion(horariosSeccion);
            seccionGrupoEspecial.setSeccion(seccion);

            //semana que le corresponde segun el horario de la seccion
            SemanaExamen semanaExamenByHorSec = this.findSemanaExamenByHorarioSeccion(seccion.getHorarioSeccion(), semanasByRolExamen);
            boolean matching = this.processSeccionEspecialByWeek(
                    semanaExamenByHorSec,
                    seccionGrupoEspecial,
                    gruposHorasExamenByRolExamenes,
                    letrasGrupoRegular,
                    cursosMasivos,
                    othersSeccionesGruposEspecialesEspeciales,
                    docentes,
                    aulas,
                    alumnos, false, ds);
            if (matching) {
                continue;
            }

            //resto de semanas
            for (SemanaExamen semanaExamen : semanasByRolExamen) {
                if (semanaExamen.equals(semanaExamenByHorSec)) {
                    continue;
                }
                matching = this.processSeccionEspecialByWeek(
                        semanaExamen,
                        seccionGrupoEspecial,
                        gruposHorasExamenByRolExamenes,
                        letrasGrupoRegular,
                        cursosMasivos,
                        othersSeccionesGruposEspecialesEspeciales,
                        docentes,
                        aulas,
                        alumnos, false, ds);
                if (matching) {
                    continue SECCIONES_ESPECIALES;
                }
            }
        }

        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CFG_ESP);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    //Calcular secciones especiales, segundo metodo
    private boolean processSeccionEspecialByWeek(
            SemanaExamen semana,
            SeccionGrupoEspecial seccionGrupoEspecial,
            List<GrupoHorasExamen> gruposHorasExamenByRolExamenes,
            List<LetraGrupoRegular> letrasGrupoRegular,
            List<CursoMasivoExamen> cursosMasivos,
            List<SeccionGrupoEspecial> otherSeccionesGrupoEspeciales,
            List<Docente> docentes,
            List<Aula> aulas,
            List<Alumno> alumnos,
            boolean busquedaNivel1, DataSessionPivot ds) {

        Seccion seccion = seccionGrupoEspecial.getSeccion();
        final int AFORO_INCREMENTO = 5;
        //ordenamos los dias de la semana, de acuerda al dia de la seccion
        Integer originalDay = seccion.getHorarioSeccion().get(0).getDia().getNumeroDia();
        List<Integer> orderedDays = this.listOrderedDays(originalDay);

        for (Integer currentDay : orderedDays) {
            //logger.debug("########################");
            List<GrupoHorasExamen> grupoHorasExamenByDayAndWeek = gruposHorasExamenByRolExamenes.stream()
                    .filter(x -> x.getSemanaExamen().equals(semana))
                    .filter(x -> x.getDia().getNumeroDia().compareTo(currentDay) == 0)
                    .collect(Collectors.toList());

            for (GrupoHorasExamen grupoHorasExamen : grupoHorasExamenByDayAndWeek) {
                //Aula aulaSeccionOriginal = aulas.get(0);
                for (Aula aulaSeccionOriginal : aulas) {

                    if (aulaSeccionOriginal.getOficinaSupervisora().isOficinaOera()) {
                        boolean result = this.processSeccionEspecialByWeekGrupoHoras(
                                seccionGrupoEspecial,
                                grupoHorasExamen,
                                letrasGrupoRegular,
                                cursosMasivos,
                                otherSeccionesGrupoEspeciales,
                                docentes,
                                aulaSeccionOriginal,
                                alumnos);
                        if (result) {
                            return true;
                        }
                    } else {

                        if (busquedaNivel1) {
                            continue;
                        }

                        //logger.info("seccion {}, aula {}, no es aula oera", seccion.getCodigo2(), aulaSeccionOriginal.getId());
                        //logger.info("aula superior {}", ObjectUtil.getParentTree(aulaSeccionOriginal, "aulaSuperior.id"));
                        Map<Long, List<Aula>> mapAulasAgrupadasPorModulo = grupoRegularConnector.aulasAgrupadasPorModulo(aulaSeccionOriginal);
                        //logger.info("Buscando en {} modulos", mapAulasAgrupadasPorModulo.size());

                        for (int i = seccion.getMatriculados(); i <= rolExamenesLogger.getMaximoAforoAula(); i += AFORO_INCREMENTO) {
                            int inicio = i;
                            int fin = i + AFORO_INCREMENTO - 1;
                            Aula aulaResult = this.buscarAulaOeraBySeccion(
                                    seccion,
                                    seccionGrupoEspecial,
                                    grupoHorasExamen,
                                    mapAulasAgrupadasPorModulo,
                                    letrasGrupoRegular,
                                    cursosMasivos,
                                    otherSeccionesGrupoEspeciales,
                                    docentes,
                                    alumnos,
                                    inicio,
                                    fin, ds);
                            if (!aulaResult.equals(aulaSeccionOriginal)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private Aula buscarAulaOeraBySeccion(
            Seccion seccion,
            SeccionGrupoEspecial seccionGrupoEspecial,
            GrupoHorasExamen grupoHorasExamen,
            Map<Long, List<Aula>> aulasAgrupadasPorModulo,
            List<LetraGrupoRegular> letrasGrupoRegular,
            List<CursoMasivoExamen> cursosMasivos,
            List<SeccionGrupoEspecial> otherSeccionesGrupoEspeciales,
            List<Docente> docentes,
            List<Alumno> alumnos,
            Integer inicio,
            Integer fin,
            DataSessionPivot ds) {

        //logger.info("Entro a busca raula");
        Seccion seccionClone = seccion.clone();
        Aula aulaSeccionOriginal = seccion.getAula();

        for (Map.Entry<Long, List<Aula>> entry : aulasAgrupadasPorModulo.entrySet()) {
            List<Aula> aulasByModulo = entry.getValue();
            AULA_EACH:
            for (Aula aula : aulasByModulo) {
                if (aula.getCapacidadAula() == null) {
                    System.out.println("Aula " + aula.getCodigo() + " sin capacidad");
                    continue;
                }
                //if (!(aula.getAforo() >= inicio && aula.getAforo() < fin)) {
                if (!(aula.getCapacidadAula() >= inicio && aula.getCapacidadAula() < fin)) {
                    continue;
                }
                for (String diaHora : grupoHorasExamen.getDiaHoraList()) {
                    if (aula.getDiaHoraList().contains(diaHora)) {
                        continue AULA_EACH;
                    }
                }
                seccionClone.setAula(aula);
                boolean result = this.processSeccionEspecialByWeekGrupoHoras(
                        seccionGrupoEspecial,
                        grupoHorasExamen,
                        letrasGrupoRegular,
                        cursosMasivos,
                        otherSeccionesGrupoEspeciales,
                        docentes,
                        aula,
                        alumnos);
                if (result) {
                    return aula;
                }
            }
        }
        return aulaSeccionOriginal;
    }

    private boolean processSeccionEspecialByWeekGrupoHoras(
            SeccionGrupoEspecial seccionGrupoEspecial,
            GrupoHorasExamen grupoHorasExamen,
            List<LetraGrupoRegular> letrasGrupoRegular,
            List<CursoMasivoExamen> cursosMasivos,
            List<SeccionGrupoEspecial> otherSeccionesGrupoEspeciales,
            List<Docente> docentes,
            Aula aula,
            List<Alumno> alumnos) {

        //logger.debug("Se intentara el match con gpoHor {} - gpoHorExam {} - aula {}", grupoHorasExamen.getGrupoHoras().getCodigo(), grupoHorasExamen.getId(), aulas.get(0).getId());
        System.out.println("grupoHorasExamen.id=" + grupoHorasExamen.getId());
        for (LetraGrupoRegular letra : letrasGrupoRegular) {
            System.out.println("\tletra.grupoHorasExamen.id=" + letra.getGrupoHorasExamen().getId());
            System.out.println("\tletra.grupoHorasExamen.id=" + letra.getGrupoHorasExamen().getId());
        }
        LetraGrupoRegular letraGrupoRegularByGrupoExamen = letrasGrupoRegular.stream()
                .filter(x -> x.getGrupoHorasExamen().getId().equals(grupoHorasExamen.getId())).findFirst().orElse(null);
        List<CursoMasivoExamen> cursosMasivosByGrupoExamen = cursosMasivos.stream()
                .filter(x -> x.getGrupoHorasExamen().getId().equals(grupoHorasExamen.getId())).collect(Collectors.toList());
//        List<SeccionGrupoEspecial> otherSeccionesGrupoEspecialByGrupoExamen = otherSeccionesGrupoEspeciales.stream()
//                .filter(x -> x.getGrupoHorasExamen() != null)
//                .filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen))
//                .collect(Collectors.toList());
        List<SeccionGrupoEspecial> otherSeccionesGrupoEspecialByGrupoExamen = new ArrayList();
        for (SeccionGrupoEspecial otraSeccionGpoEsp : otherSeccionesGrupoEspeciales) {
            if (otraSeccionGpoEsp.getGrupoHorasExamen() != null) {
                if (grupoHorasExamen.getId().longValue() == otraSeccionGpoEsp.getGrupoHorasExamen().getId()) {
                    otherSeccionesGrupoEspecialByGrupoExamen.add(otraSeccionGpoEsp);
                }
            }
        }

        List<Aula> aulasRevision = new ArrayList();
        aulasRevision.add(aula);

        boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(cursosMasivosByGrupoExamen, docentes, aulasRevision, alumnos);
        boolean validacionGrupoRegular = true;
        System.out.println("letraGrupoRegularByGrupoExamen=" + letraGrupoRegularByGrupoExamen);
        if (letraGrupoRegularByGrupoExamen != null) {
            System.out.println("\tentro grupoRegularConnector.validarGrupoRegular");
            validacionGrupoRegular = grupoRegularConnector.validarGrupoRegular(letraGrupoRegularByGrupoExamen, alumnos, docentes, aulasRevision);
        }
        boolean validacionSeccionesGpoEspecial = grupoRegularConnector.validarGrupoEspecial(otherSeccionesGrupoEspecialByGrupoExamen, alumnos, docentes, aulasRevision);

        boolean validarTripleExamen = this.validarMaximoExamenesByAlumno(
                alumnos,
                grupoHorasExamen.getFecha(),
                cursosMasivos,
                otherSeccionesGrupoEspeciales,
                letrasGrupoRegular);

        boolean validarCruceAulas = this.validarCrucesAulas(grupoHorasExamen, aulasRevision);

        if (validacionCursosMasivos && validacionGrupoRegular
                && validacionSeccionesGpoEspecial && validarTripleExamen
                && validarCruceAulas) {
            logger.debug("Se hizo el match con gpoHor {} - gpoHorExam {} - aula {}", grupoHorasExamen.getGrupoHoras().getCodigo(), grupoHorasExamen.getId(), aula.getId());
            // logger.debug("Se hizo el match con {}", grupoHorasExamen.getGrupoHoras().getCodigo());
            SeccionGrupoEspecial seccionGrupoEspecialUpd = new SeccionGrupoEspecial();
            seccionGrupoEspecialUpd.setId(seccionGrupoEspecial.getId());
            seccionGrupoEspecialUpd.setGrupoHorasExamen(grupoHorasExamen);
            seccionGrupoEspecialUpd.setAula(aula);
            seccionGrupoEspecialDAO.updateFechaExamenAndAula(seccionGrupoEspecialUpd);

            seccionGrupoEspecial.setGrupoHorasExamen(grupoHorasExamen);
            seccionGrupoEspecial.setAula(aula);
            seccionGrupoEspecial.getSeccion().setAula(aula);

            for (FechaHoraGrupoExamen fechaHoraGrupoExamen : grupoHorasExamen.getFechasHorasGruposExamen()) {
                Aula aulaMemory = rolExamenesLogger.getAulasOera().stream().filter(x -> x.equals(aula)).findFirst().orElse(null);
                HorarioAula horarioAula = new HorarioAula(fechaHoraGrupoExamen, seccionGrupoEspecial.getSeccion());
                horarioAula.setSeccionGrupoEspecial(seccionGrupoEspecialUpd);
                horarioAula.setRolExamenes(seccionGrupoEspecial.getRolExamenes());
                horarioAulaDAO.save(horarioAula);

                aulaMemory.getHorariosAula().add(horarioAula.clone());
            }

            return true;
        }
        System.out.println("\tMasivos:" + validacionCursosMasivos + ", GpoReg:" + validacionGrupoRegular + ", GpoEsp:" + validacionSeccionesGpoEspecial + ", Triple:" + validarTripleExamen);
        return false;
    }

    private boolean validarCrucesAulas(GrupoHorasExamen grupoHorasExamen, List<Aula> aulas) {

        Date fecha = grupoHorasExamen.getFecha();
        Map<Long, List<HorarioAula>> mapHorariosAula = this.rolExamenesLogger.getHorarioAulas();

        boolean aulaConConflicto = true;
        for (Aula aula : aulas) {
            int cruces = 0;
            TipoHorarioAulaEnum tipo = null;
            List<HorarioAula> horariosAulas = TypesUtil.getListNotNull(mapHorariosAula.get(aula.getId()));
            for (FechaHoraGrupoExamen fechaHorGru : grupoHorasExamen.getFechasHorasGruposExamen()) {

                for (HorarioAula ha : horariosAulas) {
                    if (ha.getDia().getId().compareTo(fechaHorGru.getDia().getId()) != 0) {
                        continue;
                    }
                    if (ha.getHora().getId().compareTo(fechaHorGru.getHora().getId()) != 0) {
                        continue;
                    }
                    if (fecha.compareTo(ha.getFechaInicio()) >= 0 && fecha.compareTo(ha.getFechaFin()) <= 0) {
                        aulaConConflicto = false;
                        tipo = ha.getTipoEnum();
                        cruces++;
                    }
                }
            }
            if (cruces > 0) {
                System.out.println("\tCruce con aula " + aula.getId() + " - tipo " + tipo.name());
            }
        }

        return aulaConConflicto;

    }

    private boolean validarMaximoExamenesByAlumno(
            List<Alumno> alumnos,
            Date fecha,
            List<CursoMasivoExamen> cursosMasivosExamen,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<LetraGrupoRegular> letrasGruposRegulares) {

        //System.out.println("Analizando max-3exam de fecha " + new DateTime(fecha).toString("dd/MM/yyyy"));
        List<CursoMasivoExamen> cursosMasivosFecha = new ArrayList();
        for (CursoMasivoExamen cursoMasivo : cursosMasivosExamen) {
            if (cursoMasivo.getGrupoHorasExamen() == null) {
                continue;
            }
            Date fechaExamen = cursoMasivo.getGrupoHorasExamen().getFecha();
            if (fecha.equals(fechaExamen)) {
                cursosMasivosFecha.add(cursoMasivo);
            }
        }
        //System.out.println("\tcursos masivos con misma fecha " + cursosMasivosFecha.size());

        List<SeccionGrupoEspecial> seccionEspecialesFecha = new ArrayList();
        for (SeccionGrupoEspecial seccionGE : seccionesGrupoEspecial) {
            if (seccionGE.getGrupoHorasExamen() == null) {
                continue;
            }
            Date fechaExamen = seccionGE.getGrupoHorasExamen().getFecha();
            if (fecha.equals(fechaExamen)) {
                seccionEspecialesFecha.add(seccionGE);
            }
        }
        //System.out.println("\tsecciones especiales con misma fecha " + seccionEspecialesFecha.size());

        List<SeccionGrupoRegular> seccionGpoRegFecha = new ArrayList();
        for (LetraGrupoRegular letraGR : letrasGruposRegulares) {
            if (letraGR.getGrupoHorasExamen() == null) {
                continue;
            }
            Date fechaExamen = letraGR.getGrupoHorasExamen().getFecha();
            if (fecha.equals(fechaExamen)) {
                List<SeccionGrupoRegular> seccionGR = letraGR.getSeccionesGruposRegulares();
                seccionGpoRegFecha.addAll(seccionGR);
            }
        }
        //System.out.println("\tsecciones regulares con misma fecha " + seccionEspecialesFecha.size());

        for (Alumno alumno : alumnos) {
            int contador = 0;
            for (CursoMasivoExamen cursoMasivoExamen : cursosMasivosFecha) {
                List<AlumnoCursoMasivo> alumnosCursoM = cursoMasivoExamen.getAlumnosCursosMasivos();
                for (AlumnoCursoMasivo alumnoCursoMasivo : alumnosCursoM) {
                    if (alumnoCursoMasivo.getAlumno().getId() == alumno.getId().longValue()) {
                        contador++;
                    }
                }
            }
            if (contador >= 2) {
                return false;
            }

            for (SeccionGrupoEspecial seccionGE : seccionEspecialesFecha) {
                List<AlumnoGrupoEspecial> alumnosGE = seccionGE.getAlumnosGrupoEspecial();
                for (AlumnoGrupoEspecial alumnoGE : alumnosGE) {
                    if (alumnoGE.getAlumno().getId() == alumno.getId().longValue()) {
                        contador++;
                    }
                }
            }

            if (contador >= 2) {
                return false;
            }

            for (SeccionGrupoRegular seccionGR : seccionGpoRegFecha) {
                List<AlumnoGrupoRegular> alumnosGR = seccionGR.getAlumnosGruposRegulares();
                for (AlumnoGrupoRegular alumnoGR : alumnosGR) {
                    if (alumnoGR.getAlumno().getId() == alumno.getId().longValue()) {
                        contador++;
                    }
                }
            }
            if (contador >= 2) {
                return false;
            }
        }

        return true;
    }

    private List<Integer> listOrderedDays(int initDay) {
        List<Integer> dias = new ArrayList<>();
        int sumaPositiva = 0;
        int sumaNegativa = 0;
        int maxDayWeek = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_WEEK); //7
        while (true) {
            if (sumaPositiva == 0) {
                dias.add(initDay);
            } else {
                int positiveResult = initDay + sumaPositiva;
                if (positiveResult <= maxDayWeek) {
                    dias.add(positiveResult);
                }
                int negativeResult = initDay - sumaNegativa;
                if (negativeResult > 0) {
                    dias.add(negativeResult);
                }

            }
            sumaPositiva++;
            sumaNegativa++;
            if (dias.size() == maxDayWeek) {
                break;
            }
        }
        return dias;
    }

    private List<GrupoHorasExamen> allGrupoHorasExamenByRolExamen(RolExamenes rolExamenes) {
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

    private List<HorarioSeccion> allHorarioSeccionWithHours(Seccion seccion, RolExamenes rolExamenes, Map<Long, List<HorarioSeccion>> horariosBySeccion) {
        List<HorarioSeccion> horariosSeccion = horariosBySeccion.get(seccion.getId());

        Map<Long, List<HorarioSeccion>> mapGroupByDia = TypesUtil.convertListToMapList("dia.id", horariosSeccion);
        HorarioSeccion firsFound = horariosSeccion.get(0).clone();
        for (Map.Entry<Long, List<HorarioSeccion>> entry : mapGroupByDia.entrySet()) {
            Long idDia = entry.getKey();
            List<HorarioSeccion> horariosByDia = entry.getValue();
            if (horariosByDia.size() < rolExamenes.getHorasExamen()) {
                horariosSeccion.removeIf(x -> x.getDia().getId().compareTo(idDia) == 0);
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

    private SemanaExamen findSemanaExamenByHorarioSeccion(List<HorarioSeccion> horariosSeccion, List<SemanaExamen> semanasExamen) {

        SEMANA_EXAMEN:
        for (SemanaExamen semanaExamen : semanasExamen) {
            int wrong = 0;
            for (HorarioSeccion horarioSeccion : horariosSeccion) {
                if (horarioSeccion.getHora().getNumero() <= semanaExamen.getHoraInicio().getNumero()
                        || horarioSeccion.getHora().getNumero() >= semanaExamen.getHoraFin().getNumero()) {
                    wrong++;
                }
            }
            if (wrong != horariosSeccion.size()) {
                return semanaExamen;
            }
        }
        return null;
    }

    private List<HorarioSeccion> reasignarHorarioSeccion(List<HorarioSeccion> horariosSeccion, Integer numeroDia, Integer numeroHoraInicio) {
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
    @Transactional
    public void limpiarExamenGrupoEspecial(RolExamenes rolExamenes, DataSessionPivot ds) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolBD);

        List<SeccionGrupoEspecial> seccionesExcluidas = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.EXC);
        List<Seccion> secciones = seccionesExcluidas.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        if (!secciones.isEmpty()) {
            seccionExcluidoDAO.deleteBySecciones(secciones);
        }
        //alumnoGrupoEspecialDAO.deleteByRolExamenes(rolExamenes);
        horarioAulaDAO.deleteSeccionesEspecialesByRolExamenes(rolExamenes);

        List<SeccionGrupoEspecial> seccionesActivas = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        for (SeccionGrupoEspecial seccionGE : seccionesActivas) {
            seccionGE.setGrupoHorasExamen(null);
            seccionGrupoEspecialDAO.update(seccionGE);
        }
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

        SeccionExcluido seccionExcluido = seccionExcluidoDAO.findByRolExamenesAndSeccion(rolExamenes, seccionGrupoEspecial.getSeccion(), EstadoEnum.ACT);
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

        this.validarActivarAlumno(alumnoGrupoEspecial.getSeccionGrupoEspecial().getGrupoHorasExamen(), alumnoGrupoEspecial.getAlumno());

        AlumnoGrupoEspecial alumnoGrupoEspecialUpd = new AlumnoGrupoEspecial(alumnoGrupoEspecial.getId());
        alumnoGrupoEspecialUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
        alumnoGrupoEspecialDAO.updateEstado(alumnoGrupoEspecialUpd);
    }

    private void validarActivarAlumno(GrupoHorasExamen grupoHorasExamen, Alumno alumno) {
        List<AlumnoCursoMasivo> alumnosCursosMasivos = alumnoCursoMasivoDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoEspecial> alumnosGrupoEspeciales = alumnoGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoRegular> alumnoGrupoRegulares = alumnoGrupoRegularDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);

        List<AlumnoCursoMasivo> alumnosCursosMasivosConflicts = alumnosCursosMasivos.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());
        List<AlumnoGrupoEspecial> alumnosCursosEspecialesConflicts = alumnosGrupoEspeciales.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());
        List<AlumnoGrupoRegular> alumnosGrupoRegularesConflicts = alumnoGrupoRegulares.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());
        if (!alumnosCursosMasivosConflicts.isEmpty() || !alumnosCursosEspecialesConflicts.isEmpty() || !alumnosGrupoRegularesConflicts.isEmpty()) {
            throw new PhobosException("Cruce de horario.");
        }
    }

    @Override
    @Transactional
    public void removerAula(SeccionGrupoEspecial grupoSpecial) {
        SeccionGrupoEspecial grupoDB = seccionGrupoEspecialDAO.find(grupoSpecial.getId());
        if (grupoDB == null) {
            throw new PhobosException("El grupo no existe");
        }
        grupoDB.setAula(null);

        horarioAulaDAO.deleteBySeccionGrupoEspecial(grupoDB);
        seccionGrupoEspecialDAO.update(grupoDB);
    }

    @Override
    @Transactional
    public void removerGrupo(SeccionGrupoEspecial grupoSpecial) {
        SeccionGrupoEspecial grupoDB = seccionGrupoEspecialDAO.find(grupoSpecial.getId());
        if (grupoDB == null) {
            throw new PhobosException("El grupo no existe");
        }
        grupoDB.setGrupoHorasExamen(null);

        horarioAulaDAO.deleteBySeccionGrupoEspecial(grupoDB);
        seccionGrupoEspecialDAO.update(grupoDB);
    }

    @Override
    public List<GrupoHorasExamen> allGrupoHEForSelect(SeccionGrupoEspecial grupoSpecial) {
//grupoSpecial.getRolExamenes();
        List<GrupoHorasExamen> gHoras = grupoHorasExamenDAO.allForGrupoEspecial(grupoSpecial.getRolExamenes());
        logger.debug("SIZE OF GHORAS {}", gHoras.size());
        return gHoras;
    }

}
