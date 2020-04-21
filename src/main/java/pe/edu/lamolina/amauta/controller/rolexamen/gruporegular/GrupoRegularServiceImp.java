package pe.edu.lamolina.amauta.controller.rolexamen.gruporegular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
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
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.DiaHoraGrupo;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.controller.rolexamen.grupoespecial.GrupoEspecialService;
import pe.edu.lamolina.amauta.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.amauta.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.amauta.dao.academico.SeccionDAO;
import pe.edu.lamolina.amauta.dao.general.AulaDAO;
import pe.edu.lamolina.amauta.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.amauta.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.amauta.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.CursoExcluidoDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.GrupoRegularExamenDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SeccionCursoMasivoDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SeccionExcluidoDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.amauta.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GrupoRegularServiceImp implements GrupoRegularService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoEspecialService grupoEspecialService;

    @Autowired
    RolExamenesDAO rolExamenesDAO;
    /*
    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;
     */
    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    GrupoHorasDAO grupoHorasDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    GrupoRegularExamenDAO grupoRegularExamenDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    GrupoRegularConnector grupoRegularConnector;

    @Autowired
    DiaHoraGrupoDAO diaHoraGrupoDAO;

    @Autowired
    HorarioSeccionDAO horarioSeccionDAO;

    @Autowired
    GrupoHorasExamenDAO grupoHorasExamenDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    SeccionExcluidoDAO seccionExcluidoDAO;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    CursoExcluidoDAO cursoExcluidoDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    private void checkNoPublicado(RolExamenes rol) {
        Assert.isTrue(rol.getEstadoEnum() != RolExamenesEstadoEnum.PUB, "El rol de exámenes ya ha sido publicado");
    }

    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }

    @Override
    public LetraGrupoRegular findLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        return letraGrupoRegularDAO.find(letraGrupoRegular.getId());
    }

    @Override
    public RolExamenes findRolExamenes(long rolExamenId) {
        RolExamenes rolExamenes = rolExamenesDAO.find(rolExamenId);
        List<SemanaExamen> semanaExamens = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanaExamens);
        return rolExamenes;
    }

    @Override
    public void eliminarGruposRegulares(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolExamenes);
        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(rolExamenes.isSituacionConfigurarGrupoRegular(), "No puede eliminar los grupos regulares.");

        grupoEspecialService.deleteGrupoEspecial(rolExamenes);
        this.deleteGrupoRegular(rolExamenes);

        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CFG_MAS);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    private void validationsCalculoExamenesGrupoRegular(RolExamenes rolExamenes, List<CursoMasivoExamen> cursosMasivosByRolExamenes) {

        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(rolExamenes.isSituacionConfigurarCursoMasivo() || rolExamenes.isSituacionConfigurarGrupoRegular(),
                "Debe configurar los grupos masivos previamente.");

        List<CursoMasivoExamen> masivos = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        Assert.isFalse(masivos.isEmpty(), "Debe configurar los cursos masivos.");
        List<SeccionCursoMasivo> seccionesCursosMasivos = seccionCursoMasivoDAO.allByCursosMasivos(masivos);
        Map<Long, List<SeccionCursoMasivo>> mapSeccionCursiMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", seccionesCursosMasivos);
        for (CursoMasivoExamen masivo : masivos) {
            masivo.setSeccionesCursosMasivos(TypesUtil.getListNotNull(mapSeccionCursiMasivo.get(masivo.getId())));
        }

        cursosMasivosByRolExamenes.addAll(masivos);
        for (CursoMasivoExamen masivo : masivos) {
            masivo.setSeccionesCursosMasivos(seccionesCursosMasivos);
        }

        grupoEspecialService.deleteGrupoEspecial(rolExamenes);
        this.deleteGrupoRegular(rolExamenes);
    }

    @Override
    @Transactional
    public void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolBD);

        List<CursoMasivoExamen> cursosMasivosByRolExamenes = new ArrayList();
        this.validationsCalculoExamenesGrupoRegular(rolExamenes, cursosMasivosByRolExamenes);

        grupoRegularConnector.fillActiveInfoCursosMasivos(cursosMasivosByRolExamenes);
        List<String> cursosMasivosValidations = this.validarCursosMasivos(cursosMasivosByRolExamenes);
        Assert.isTrue(cursosMasivosValidations.isEmpty(), String.join("\n", cursosMasivosValidations));

        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        grupoRegularConnector.fillActiveInfoGrupoEspecial(seccionesGrupoEspecial);

        this.rolExamenesLogger.iniciarGrupoRegular();

        DateTime today = new DateTime(ds.getFechaAccionAudit());

        List<SeccionExcluido> seccionesExcluidasByRolExamen = seccionExcluidoDAO.allByRolExamenes(rolExamenes);
        List<CursoExcluido> cursosExcluidos = cursoExcluidoDAO.allByRolExamenes(rolExamenes, EstadoEnum.ACT);
        List<Seccion> seccionesEspecialesRecolected = new ArrayList();

        rolExamenesLogger.setAulasOera(grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolBD, OficinaEnum.OERA));
        List<Aula> aulas = grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolBD, null);
        rolExamenesLogger.setAulas(aulas);

        List<SemanaExamen> semanas = semanaExamenDAO.allByRolExamenes(rolExamenes);
        Date fechaInicio = semanas.stream().min(Comparator.comparing(SemanaExamen::getFechaInicio)).map(x -> x.getFechaInicio()).get();
        Date fechaFin = semanas.stream().max(Comparator.comparing(SemanaExamen::getFechaFin)).map(x -> x.getFechaFin()).get();
        List<HorarioAula> horariosAulasByRango = horarioAulaDAO.allByRangoNotByTipo(fechaInicio, fechaFin, TipoHorarioAulaEnum.EXAM, aulas);
        Map<Long, List<HorarioAula>> mapHorarioAulas = TypesUtil.convertListToMapList("aula.id", horariosAulasByRango);
        rolExamenesLogger.setHorarioAulas(mapHorarioAulas);

        Aula aulaMaxAforo = aulaDAO.findAulaMaxAforo(OficinaEnum.OERA, EstadoEnum.ACT);
        rolExamenesLogger.setMaximoAforoAula(aulaMaxAforo.getAforo());

        //creamos las letras regulares
        List<String> letras = new ArrayList();
        List<GrupoHorasExamen> gruposHorasExamen = this.allGrupoHorasExamenByRol(rolExamenes);
        for (GrupoHorasExamen grupoHorasExamen : gruposHorasExamen) {
            if (!letras.contains(grupoHorasExamen.getGrupoHoras().getLetra())) {
                letras.add(grupoHorasExamen.getGrupoHoras().getLetra());
                //   break;
            }
        }

        List<LetraGrupoRegular> letrasGruposRegulares = this.convertLetraToLetraGpo(letras, rolExamenes, gruposHorasExamen, today, ds.getUsuario());

        // logger.info("Grupos regulares");
        List<Seccion> secciones = this.allSeccionesRegulares(cicloAcademico, TipoGrupoHorasEnum.REGULAR, rolExamenes);
        System.out.println("Secciones al inicio regular " + secciones.size() + " registros");
        this.quitarSeccionesExcluidas(secciones, seccionesExcluidasByRolExamen, cursosExcluidos);
        this.quitarSeccionesCursosMasivos(secciones, cursosMasivosByRolExamenes);
        //this.quitarSeccionesAsignadas(secciones, seccionesActivasGpoReg, seccionesActivasGpoEsp);
        System.out.println(" **** Secciones al final regular " + secciones.size() + " registros");
        System.out.println("*************************************************");

        List<Seccion> seccionesOera = secciones
                .stream().filter(x -> x.getAula().getOficinaSupervisora().isOficinaOera())
                .collect(Collectors.toList());

        List<Seccion> seccionesOthersOfi = secciones
                .stream().filter(x -> !x.getAula().getOficinaSupervisora().isOficinaOera())
                .collect(Collectors.toList());

        //Map<String, List<Seccion>> seccionesGroupByLetra = TypesUtil.convertListToMapList("grupoHoras.letra", seccionesOera);
        Map<String, List<Seccion>> seccionesGroupByLetra = this.agruparByLetra(seccionesOera, letrasGruposRegulares, cicloAcademico);
        //   logger.info("Letras Grupos Regulares Oera {}, Secciones {}", String.join(",", letras), seccionesOera.size());

        this.rolExamenesLogger.addMessageLevel1("Calculo grupos regulares de secciones oera");
        this.crearLetrasGruposRegulares(
                letrasGruposRegulares,
                cursosMasivosByRolExamenes,
                seccionesGrupoEspecial,
                seccionesGroupByLetra,
                seccionesEspecialesRecolected, ds);

        //seccionesGroupByLetra = TypesUtil.convertListToMapList("grupoHoras.letra", seccionesOthersOfi);
        seccionesGroupByLetra = this.agruparByLetra(seccionesOthersOfi, letrasGruposRegulares, cicloAcademico);
        //    logger.info("Letras Grupos Regulares No Oera {}, Secciones {}", String.join(",", letras), seccionesOthersOfi.size());

        this.rolExamenesLogger.addMessageLevel1("Calculo grupos regulares de secciones no oera");
        this.crearLetrasGruposRegulares(
                letrasGruposRegulares,
                cursosMasivosByRolExamenes,
                seccionesGrupoEspecial,
                seccionesGroupByLetra,
                seccionesEspecialesRecolected, ds);

        //  logger.info("Grupos Especiales");
        secciones = seccionDAO.allForRolExamenByTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ESPECIAL);
        System.out.println("Secciones al inicio especial " + secciones.size() + " registros");
        this.quitarSeccionesExcluidas(secciones, seccionesExcluidasByRolExamen, cursosExcluidos);
        this.quitarSeccionesCursosMasivos(secciones, cursosMasivosByRolExamenes);
        //this.quitarSeccionesAsignadas(secciones, seccionesActivasGpoReg, seccionesActivasGpoEsp);
        System.out.println(" **** Secciones al final regular " + secciones.size() + " registros");

        Map<String, List<Seccion>> mapSeccionesByLetra = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);

        //  logger.info("Haciendo encajar grupos especiales en las letras regulares");
        this.rolExamenesLogger.addMessageLevel1("Calculo de grupos especiales en las letras puras");
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            this.rolExamenesLogger.addMessageLevel2("Letra %s ", letraGrupoRegular.getLetra());
            List<CursoMasivoExamen> cursosMasivosByLetra = cursosMasivosByRolExamenes.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                    .collect(Collectors.toList());

            grupoRegularConnector.crearLetraGrupoRegularByLetra(
                    letraGrupoRegular,
                    cursosMasivosByLetra,
                    seccionesGrupoEspecial,
                    mapSeccionesByLetra,
                    seccionesEspecialesRecolected,
                    letrasGruposRegulares,
                    cursosMasivosByRolExamenes,
                    seccionesGrupoEspecial, ds);
        }

        //calculamos el resto de letras
        letras = new ArrayList(mapSeccionesByLetra.keySet());
        //quitamos las letras regulares de los grupos especiales
        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            letras.removeIf(x -> x.equals(letraGruposRegular.getLetra()));
        }
        logger.info("Letras Grupos Especiales {}", letras.toString());

        this.rolExamenesLogger.setRolExamenes(rolExamenes.getId());
        for (String letra : letras) {
            this.calcularGruposEspeciales(
                    letra,
                    cursosMasivosByRolExamenes,
                    seccionesGrupoEspecial,
                    mapSeccionesByLetra,
                    letrasGruposRegulares,
                    seccionesEspecialesRecolected, ds);
        }
        logger.info("Secciones Especiales sin asignar {}", seccionesEspecialesRecolected.size());
        this.saveSeccionesEspeciales(seccionesEspecialesRecolected, rolExamenes, ds);

        //List<SeccionGrupoRegular> seccionesActivasGpoReg = seccionGrupoRegularDAO.allByRolExamenes(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        //List<SeccionGrupoEspecial> seccionesActivasGpoEsp = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        //List<LetraGrupoRegular> letrasGpoRegBD = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        //Map<String, LetraGrupoRegular> mapLetraGpoReg = TypesUtil.convertListToMap("letra", letrasGpoRegBD);
        //
        //
        logger.info("letras grupos regulares a guardar {}", letrasGruposRegulares.size());

        List<SeccionGrupoRegular> seccionesGpoRegAll = new ArrayList();

        List<Seccion> seccionesForSave = new ArrayList();

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            List<SeccionGrupoRegular> seccionesGpoReg = letraGrupoRegular.getSeccionesGruposRegulares();
            for (SeccionGrupoRegular seccionGR : seccionesGpoReg) {
                seccionesForSave.add(seccionGR.getSeccion());
            }
        }
        List<DocenteSeccion> docentesSeccion = docenteSeccionDAO.allPrincipalesBySecciones(seccionesForSave);
        Map<Long, DocenteSeccion> mapDocenteSeccion = TypesUtil.convertListToMap("seccion.id", docentesSeccion);
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            List<SeccionGrupoRegular> seccionesGpoReg = letraGrupoRegular.getSeccionesGruposRegulares();
            for (SeccionGrupoRegular seccionGR : seccionesGpoReg) {
                DocenteSeccion docenteSecc = mapDocenteSeccion.get(seccionGR.getSeccion().getId());
                if (docenteSecc != null && docenteSecc.getDocente().getPersona() != null) {
                    seccionGR.setDocente(docenteSecc.getDocente());
                }
            }
        }

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            logger.debug("guardara la letra {}", letraGrupoRegular.getLetra());
            letraGrupoRegularDAO.save(letraGrupoRegular);

            List<SeccionGrupoRegular> seccionesGpoRegular = letraGrupoRegular.getSeccionesGruposRegulares();
//            List<SeccionGrupoRegular> seccionGpoRegularOera = seccionesGpoRegular.stream()
//                    .filter(x -> seccionesOera.contains(x.getSeccion()))
//                    .collect(Collectors.toList());
            for (SeccionGrupoRegular seccionGpoReg : seccionesGpoRegular) {
                seccionGpoReg.setUserRegistro(ds.getUsuario());
                seccionGpoReg.setFechaRegistro(ds.getFechaAccionAudit());
                seccionGpoReg.setLetraGrupoRegular(letraGrupoRegular);
                seccionGpoReg.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            }
            seccionesGpoRegAll.addAll(seccionesGpoRegular);
        }
        seccionGrupoRegularDAO.saveList(seccionesGpoRegAll);

        List<HorarioAula> horariosSeccionGpoRegAll = new ArrayList();
        List<AlumnoGrupoRegular> alumnosSeccGpoRegAll = new ArrayList();
        List<Seccion> listSeccionesNoOera = new ArrayList();

        List<Seccion> seccionesAfectadas = seccionesGpoRegAll.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        //List<SeccionGrupoRegular> seccionesGpoRegularBD = seccionGrupoRegularDAO.allByLetraGrupoRegularAndSecciones(letraGrupoRegular, seccionesAfectadas);
        List<SeccionGrupoRegular> seccionesGpoRegularBD = seccionGrupoRegularDAO.allBySecciones(seccionesAfectadas);
        Map<Long, SeccionGrupoRegular> mapSeccionGpoRegularBD = TypesUtil.convertListToMap("seccion.id", seccionesGpoRegularBD);

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            List<SeccionGrupoRegular> seccionesGpoRegular = letraGrupoRegular.getSeccionesGruposRegulares();
//            List<SeccionGrupoRegular> seccionGpoRegularOera = seccionesGpoRegular.stream()
//                    .filter(x -> seccionesOera.contains(x.getSeccion()))
//                    .collect(Collectors.toList());
//            for (SeccionGrupoRegular seccionGpoReg : seccionGpoRegularOera) {
//                seccionGpoReg.setUserRegistro(ds.getUsuario());
//                seccionGpoReg.setFechaRegistro(ds.getFechaAccionAudit());
//                seccionGpoReg.setLetraGrupoRegular(letraGrupoRegular);
//            }
            //seccionesGpoRegAll.addAll(seccionGpoRegularOera);
            //seccionGrupoRegularDAO.createForLetraGrupoRegular(seccionGpoRegularOera, letraGrupoRegular, ds.getFechaAccionAudit(), ds.getUsuario());

//            List<SeccionGrupoRegular> seccionGpoRegularNoOera = new ArrayList(seccionesGpoRegular);
//            for (SeccionGrupoRegular seccionGrupoRegularOera : seccionGpoRegularOera) {
//                seccionGpoRegularNoOera.removeIf(x -> x.getSeccion().equals(seccionGrupoRegularOera.getSeccion()));
//            }
//            for (SeccionGrupoRegular seccionGrupoRegular : seccionGpoRegularNoOera) {
//                seccionGrupoRegularDAO.save(seccionGrupoRegular);
//            }
            for (SeccionGrupoRegular seccionGpoRegular : seccionesGpoRegular) {
                SeccionGrupoRegular seccionGpoRegularBD = mapSeccionGpoRegularBD.get(seccionGpoRegular.getSeccion().getId());
                List<AlumnoGrupoRegular> alumnosSeccGpoReg = seccionGpoRegular.getAlumnosGruposRegulares();
                for (AlumnoGrupoRegular agr : alumnosSeccGpoReg) {
                    agr.setSeccionGrupoRegular(seccionGpoRegularBD);
                    agr.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                    agr.setFechaRegistro(ds.getFechaAccionAudit());
                    agr.setUserRegistro(ds.getUsuario());
                }
                alumnosSeccGpoRegAll.addAll(alumnosSeccGpoReg);
                //alumnoGrupoRegularDAO.createForSeccionGrupoRegular(seccionGpoRegular.getAlumnosGruposRegulares(), seccionGpoRegularBD, ds.getFechaAccionAudit(), ds.getUsuario());

                List<HorarioAula> horariosAulaSeccReg = seccionGpoRegular.getHorariosAula();
                for (HorarioAula horarioAula : horariosAulaSeccReg) {
                    horarioAula.setSeccionGrupoRegular(seccionGpoRegularBD);
                    //horarioAulaDAO.save(horarioAula);
                }
                horariosSeccionGpoRegAll.addAll(horariosAulaSeccReg);

                if (!seccionGpoRegular.getAula().getOficinaSupervisora().isOficinaOera()) {
                    listSeccionesNoOera.add(seccionGpoRegular.getSeccion());
                }
            }
        }
        alumnoGrupoRegularDAO.saveAll(alumnosSeccGpoRegAll);
        horarioAulaDAO.saveList(horariosSeccionGpoRegAll);

        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CFG_REG);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);

        for (Seccion seccion : listSeccionesNoOera) {
            logger.info("Seccion no oera : {} {}", seccion.getId(), seccion.getTipoSeccion());
        }
        for (RolExamenesLogger logDetail : this.rolExamenesLogger.getLogDetails()) {
            logger.debug("{}", logDetail.getMessage());

        }
        //  throw new PhobosException("No pasaras");
    }

    private Map<String, List<Seccion>> agruparByLetra(List<Seccion> secciones, List<LetraGrupoRegular> letrasGruposRegulares, CicloAcademico ciclo) {
        List<DiaHoraGrupo> horarioGpos = diaHoraGrupoDAO.allByCiclo(ciclo);
        Map<Long, List<DiaHoraGrupo>> mapHorarioGpo = TypesUtil.convertListToMapList("grupoHorario.id", horarioGpos);
        System.out.println("mapHorarioGpo.size=" + mapHorarioGpo.size());

        List<HorarioSeccion> horarioSeccs = horarioSeccionDAO.allByCiclo(ciclo);
        Map<Long, List<HorarioSeccion>> mapHorarioSecc = TypesUtil.convertListToMapList("seccion.id", horarioSeccs);
        System.out.println("mapHorarioSecc.size=" + mapHorarioSecc.size());

        List<GrupoHoras> grupos = grupoHorasDAO.allRegulares();
        Map<String, GrupoHoras> mapGrupos = TypesUtil.convertListToMap("codigo", grupos);
        System.out.println("mapGrupos.size=" + mapGrupos.size());

        Map<String, List<DiaHoraGrupo>> mapHorarioLetra = new LinkedHashMap();
        for (LetraGrupoRegular letraReg : letrasGruposRegulares) {
            GrupoHoras gpo = mapGrupos.get(letraReg.getLetra());
            if (gpo == null) {
                continue;
            }
            List<DiaHoraGrupo> horaGpo = mapHorarioGpo.get(gpo.getId());
            mapHorarioLetra.put(gpo.getColor(), horaGpo);
        }

        Map<String, LetraGrupoRegular> mapLetra = TypesUtil.convertListToMap("letra", letrasGruposRegulares);
        Map<String, List<Seccion>> mapSeccionFinal = new LinkedHashMap();
        Map<String, List<Seccion>> mapSeccion = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        Map<Long, Seccion> mapSeccionOk = new LinkedHashMap();

        for (Map.Entry<String, List<Seccion>> entry : mapSeccion.entrySet()) {
            String letra = entry.getKey();
            LetraGrupoRegular letraReg = mapLetra.get(letra);
            if (letraReg != null) {
                List<Seccion> seccionesLetra = entry.getValue();
                mapSeccionFinal.put(letra, seccionesLetra);
                for (Seccion seccion : seccionesLetra) {
                    mapSeccionOk.put(seccion.getId(), seccion);
                }
            }
        }

        for (Seccion seccion : secciones) {
            System.out.println("Analizando seccion " + seccion.getId());
            Seccion seccionVerifica = mapSeccionOk.get(seccion.getId());
            if (seccionVerifica != null) {
                //System.out.println("Ya no se analiza " + seccion.getId());
                continue;
            }

            List<HorarioSeccion> horariow = TypesUtil.getListNotNull(mapHorarioSecc.get(seccion.getId()));
            if (horariow.isEmpty()) {
                //System.out.println("No tiene horario la seccion " + seccion.getId());
                continue;
            }

            int comunes = 0;
            for (LetraGrupoRegular letraReg : letrasGruposRegulares) {
                GrupoHoras gpo = mapGrupos.get(letraReg.getLetra());
                if (gpo == null) {
                    continue;
                }
                //System.out.println("revisando con " + gpo.getCodigo());
                List<DiaHoraGrupo> horaGpoLetra = mapHorarioGpo.get(gpo.getId());
                ListsInspector inspector = TypesUtil.analizeLists(horaGpoLetra, horariow, "idDiaHora");
                int comunesCaso = inspector.getOldListDB().size();
                comunes = (comunesCaso > comunes) ? comunesCaso : comunes;
                //System.out.println("\ttiene horas comunes " + comunesCaso);
            }

            //System.out.println("Tiene horas comunes finales " + comunes);
            if (comunes > 0) {
                for (LetraGrupoRegular letraReg : letrasGruposRegulares) {

                    GrupoHoras gpo = mapGrupos.get(letraReg.getLetra());
                    if (gpo == null) {
                        //System.out.println("\tNo existe gpo para esta letra");
                        continue;
                    }
                    List<DiaHoraGrupo> horaGpoLetra = mapHorarioGpo.get(gpo.getId());
                    ListsInspector inspector = TypesUtil.analizeLists(horaGpoLetra, horariow, "idDiaHora");
                    int comunesCaso = inspector.getOldListDB().size();
                    if (comunesCaso == comunes) {
                        //System.out.println("\tColocando seccion con la letra " + letraReg.getLetra());
                        List<Seccion> seccionesLetra = mapSeccionFinal.get(letraReg.getLetra());
                        if (seccionesLetra == null) {
                            seccionesLetra = new ArrayList();
                            mapSeccionFinal.put(letraReg.getLetra(), seccionesLetra);
                        }
                        seccionesLetra.add(seccion);
                        mapSeccionOk.put(seccion.getId(), seccion);
                        break;
                    }
                }
            }

        }

        for (Seccion seccion : secciones) {
            Seccion seccionVerifica = mapSeccionOk.get(seccion.getId());
            if (seccionVerifica != null) {
                //System.out.println("Ya no se analiza " + seccion.getId());
                continue;
            }
            List<DiaHoraGrupo> horaGpoSecc = TypesUtil.getListNotNull(mapHorarioGpo.get(seccion.getGrupoHoras().getId()));
            if (horaGpoSecc.isEmpty()) {
                //System.out.println("No tiene horario el grupo " + seccion.getGrupoHoras().getId());
                continue;
            }
            int comunes = 0;
            for (LetraGrupoRegular letraReg : letrasGruposRegulares) {
                System.out.println("letra::" + letraReg.getLetra());
                GrupoHoras gpo = mapGrupos.get(letraReg.getLetra());
                if (gpo == null) {
                    continue;
                }
                System.out.println("revisando con " + gpo);

                List<DiaHoraGrupo> horaGpoLetra = mapHorarioGpo.get(gpo.getId());
                ListsInspector inspector = TypesUtil.analizeLists(horaGpoLetra, horaGpoSecc, "id");
                int comunesCaso = inspector.getOldListDB().size();
                comunes = (comunesCaso > comunes) ? comunesCaso : comunes;
                //System.out.println("\ttiene horas comunes " + comunesCaso);
            }

            //System.out.println("Tiene horas comunes finales " + comunes);
            if (comunes > 0) {
                for (LetraGrupoRegular letraReg : letrasGruposRegulares) {
                    GrupoHoras gpo = mapGrupos.get(letraReg.getLetra());
                    if (gpo == null) {
                        continue;
                    }
                    List<DiaHoraGrupo> horaGpoLetra = mapHorarioGpo.get(gpo.getId());
                    ListsInspector inspector = TypesUtil.analizeLists(horaGpoLetra, horaGpoSecc, "id");
                    int comunesCaso = inspector.getOldListDB().size();
                    if (comunesCaso == comunes) {
                        List<Seccion> seccionesLetra = mapSeccionFinal.get(letraReg.getLetra());
                        if (seccionesLetra == null) {
                            seccionesLetra = new ArrayList();
                            mapSeccionFinal.put(letraReg.getLetra(), seccionesLetra);
                        }
                        seccionesLetra.add(seccion);
                        mapSeccionOk.put(seccion.getId(), seccion);
                        break;
                    }
                }
            }
        }

        List<Seccion> seccionesSinLetra = new ArrayList();
        for (Seccion seccion : secciones) {
            Seccion seccionVerifica = mapSeccionOk.get(seccion.getId());
            if (seccionVerifica != null) {
                continue;
            }
            seccionesSinLetra.add(seccion);
        }

        if (!seccionesSinLetra.isEmpty()) {
            List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(seccionesSinLetra);
            Map<Long, List<HorarioSeccion>> mapHoarioSecc = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);
            for (Seccion seccion : seccionesSinLetra) {
                List<HorarioSeccion> horariosSeccion = TypesUtil.getListNotNull(mapHoarioSecc.get(seccion.getId()));
                if (horariosSeccion.isEmpty()) {
                    continue;
                }
                int comunes = 0;
                for (LetraGrupoRegular letraReg : letrasGruposRegulares) {
                    GrupoHoras gpo = mapGrupos.get(letraReg.getLetra());
                    if (gpo == null) {
                        continue;
                    }
                    List<DiaHoraGrupo> horaGpoLetra = mapHorarioGpo.get(gpo.getId());
                    ListsInspector inspector = TypesUtil.analizeLists(horaGpoLetra, horariosSeccion, "idDiaHora");
                    int comunesCaso = inspector.getOldListDB().size();
                    comunes = (comunesCaso > comunes) ? comunesCaso : comunes;
                }

                if (comunes > 0) {
                    for (LetraGrupoRegular letraReg : letrasGruposRegulares) {
                        GrupoHoras gpo = mapGrupos.get(letraReg.getLetra());
                        if (gpo == null) {
                            continue;
                        }
                        List<DiaHoraGrupo> horaGpoLetra = mapHorarioGpo.get(gpo.getId());
                        ListsInspector inspector = TypesUtil.analizeLists(horaGpoLetra, horariosSeccion, "idDiaHora");
                        int comunesCaso = inspector.getOldListDB().size();
                        if (comunesCaso == comunes) {
                            List<Seccion> seccionesLetra = mapSeccionFinal.get(letraReg.getLetra());
                            if (seccionesLetra == null) {
                                seccionesLetra = new ArrayList();
                                mapSeccionFinal.put(letraReg.getLetra(), seccionesLetra);
                            }
                            seccionesLetra.add(seccion);
                            mapSeccionOk.put(seccion.getId(), seccion);
                            break;
                        }
                    }
                }
            }
        }

        return mapSeccionFinal;
    }

    public void analizarAulaEstudios(List<Seccion> secciones, List<Aula> aulaEstudios) {
        for (Seccion seccion : secciones) {
            if (aulaEstudios.contains(seccion.getAula())) {
                continue;
            }
            for (Aula aula : aulaEstudios) {
                if (aula.getAforo() < seccion.getVacantes()) {
                    continue;
                }
                logger.debug("aula {}, aforo {}", aula.getCodigo(), aula.getAforo());
                boolean singleValidation = aula.getHorariosAula() == null || aula.getHorariosAula().isEmpty();
                if (singleValidation || !aula.getDiaHoraList().contains(seccion.getDiaHoraList())) {
                    seccion.setAula(aula);
                }
            }
        }

    }

    private List<Seccion> allSeccionesRegulares(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum, RolExamenes rolExamenes) {

        List<Seccion> secciones = seccionDAO.allForRolExamenByTipoGrupoHora(cicloAcademico, tipoGrupoHorasEnum);
        List<Seccion> seccionesZetas = seccionDAO.allForRolExamenByTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ZETA);
        seccionesZetas.removeIf(x -> !x.getGrupoHoras().getConHorario().equals("FLXHOR"));
        secciones.addAll(seccionesZetas);

        List<HorarioSeccion> horariosSecciones = horarioSeccionDAO.allBySecciones(secciones);
        Map<Long, List<HorarioSeccion>> mapHorarioSeccion = TypesUtil.convertListToMapList("seccion.id", horariosSecciones);
        for (Seccion seccion : secciones) {
            seccion.setHorarioSeccion(TypesUtil.getListNotNull(mapHorarioSeccion.get(seccion.getId())));
        }

        List<GrupoHorasExamen> gHoras = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<FechaHoraGrupoExamen> fechasHoras = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gHoras);
        Map<Long, List<FechaHoraGrupoExamen>> mapFechaHora = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHoras);
        for (GrupoHorasExamen gHora : gHoras) {
            gHora.setFechasHorasGruposExamen(mapFechaHora.get(gHora.getId()));
        }

        for (Seccion seccion : secciones) {
            seccion.validarGrupoExamen(gHoras);
        }

        //Ordernar por horas semanalaes de mayor a menor
        //Collections.sort(secciones, (p1, p2) -> p2.getHorasSemanales().compareTo(p1.getHorasSemanales()));
        Collections.sort(secciones, new Seccion.CompareOrdenExamen());

        int loop = 0;
        for (Seccion seccion : secciones) {
            System.out.print(seccion.getCodigo2() + ":" + seccion.getLetraExamen() + " / ");
            loop++;
            if (loop % 10 == 0) {
                System.out.println("");
            }
        }
        System.out.println("");

        /*  for (Seccion seccion : secciones) {
            List<HorarioSeccion> horariosSeccion = (List<HorarioSeccion>) horariosBySeccion.get(seccion.getId());
            seccion.setHorarioSeccion(horariosSeccion);
        }*/
        return secciones;
    }

    private void quitarSeccionesExcluidas(List<Seccion> secciones, List<SeccionExcluido> seccionesExcluidasByRolExamen, List<CursoExcluido> cursosExcluidoz) {
        List<Seccion> seccionesExcluidas = seccionesExcluidasByRolExamen.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        List<Curso> cursosExcluidos = cursosExcluidoz.stream().map(x -> x.getCurso()).collect(Collectors.toList());

        for (Seccion seccionExcluida : seccionesExcluidas) {
            secciones.removeIf(x -> x.equals(seccionExcluida));
        }
        for (Curso cursosExcluido : cursosExcluidos) {
            secciones.removeIf(x -> x.getGrupoSeccion().getCurso().equals(cursosExcluido));
        }
    }

    private void quitarSeccionesCursosMasivos(List<Seccion> secciones, List<CursoMasivoExamen> cursosMasivos) {
        for (CursoMasivoExamen cursoMasivo : cursosMasivos) {
            List<SeccionCursoMasivo> seccionesCM = cursoMasivo.getSeccionesCursosMasivos();
            for (SeccionCursoMasivo seccionCM : seccionesCM) {
                if (seccionCM.getEstadoEnum() == SeccionRolExamenEstadoEnum.ACT) {
                    secciones.removeIf(x -> x.equals(seccionCM.getSeccion()));
                }
            }
        }
    }

    private void quitarSeccionesAsignadas(
            List<Seccion> secciones,
            List<SeccionGrupoRegular> seccionesActivasGpoReg,
            List<SeccionGrupoEspecial> seccionesActivasGpoEsp) {
        for (SeccionGrupoRegular seccionGR : seccionesActivasGpoReg) {
            if (seccionGR.getEstadoEnum() == SeccionRolExamenEstadoEnum.ACT) {
                secciones.removeIf(x -> x.equals(seccionGR.getSeccion()));
            }
        }

        for (SeccionGrupoEspecial seccionGE : seccionesActivasGpoEsp) {
            if (seccionGE.getEstadoEnum() == SeccionRolExamenEstadoEnum.ACT) {
                secciones.removeIf(x -> x.equals(seccionGE.getSeccion()));
            }
        }
    }

    private List<String> validarCursosMasivos(List<CursoMasivoExamen> cursosMasivosByRolExamenes) {
        List<String> validations = new ArrayList<>();
        for (CursoMasivoExamen cursosMasivosByRolExamene : cursosMasivosByRolExamenes) {
            List<String> errors = new ArrayList<>();
            /*  if (cursosMasivosByRolExamene.getAulasCursosMasivos() == null || cursosMasivosByRolExamene.getAulasCursosMasivos().isEmpty()) {
                String msg = "Sin aulas asignadas.";
                errors.add(msg);
            }  */
            if (cursosMasivosByRolExamene.getAlumnosCursosMasivos() == null || cursosMasivosByRolExamene.getAlumnosCursosMasivos().isEmpty()) {
                String msg = "\t Sin alumnos asignados.";
                errors.add(msg);
            }
            if (cursosMasivosByRolExamene.getDocentesCursosMasivos() == null || cursosMasivosByRolExamene.getDocentesCursosMasivos().isEmpty()) {
                String msg = "\t Sin docentes asignados.";
                errors.add(msg);
            }
            if (!errors.isEmpty()) {
                Curso curso = cursosMasivosByRolExamene.getCurso();
                String msg = String.format("Curso Masivo %s - %s", curso.getCodigo(), curso.getNombre());
                errors.add(0, msg);
                validations.addAll(errors);
            }
        }
        return validations;
    }

    private void saveSeccionesEspeciales(List<Seccion> seccionesForEspecial, RolExamenes rolExamenes, DataSessionPivot ds) {
        logger.debug("secciones especiales a guardar {}", seccionesForEspecial.size());
        int contSecciones = 0;
        List<SeccionGrupoEspecial> seccionesEspecialesAll = new ArrayList();
        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allBySecciones(seccionesForEspecial);
        Map<Long, DocenteSeccion> mapDocenteSeccion = TypesUtil.convertListToMap("seccion.id", docentesSecciones);

        for (Seccion seccion : seccionesForEspecial) {

            SeccionGrupoEspecial seccionGrupoEspecial = new SeccionGrupoEspecial(
                    rolExamenes,
                    seccion, ds.getUsuario(),
                    ds.getFechaAccionAudit()
            );
            DocenteSeccion docenteSeccion = mapDocenteSeccion.get(seccion.getId());
            if (docenteSeccion != null & docenteSeccion.getDocente().getPersona() != null) {
                seccionGrupoEspecial.setDocente(docenteSeccion.getDocente());
            }
            seccionesEspecialesAll.add(seccionGrupoEspecial);
            //seccionGrupoEspecialDAO.save(seccionGrupoEspecial);
            //alumnoGrupoEspecialDAO.createForSeccionGrupoEspecial(seccionGrupoEspecial.getAlumnosGrupoEspecial(), seccionGrupoEspecial, ds.getFechaAccionAudit(), ds.getUsuario());
        }
        seccionGrupoEspecialDAO.saveList(seccionesEspecialesAll);

        List<SeccionGrupoEspecial> seccionesEspecialesBD = seccionGrupoEspecialDAO.allBySecciones(rolExamenes, seccionesForEspecial);
        Map<Long, SeccionGrupoEspecial> mapSeccionEspecial = TypesUtil.convertListToMap("seccion.id", seccionesEspecialesBD);

        List<MatriculaSeccion> matriculadosSeccionesAll = matriculaSeccionDAO.allMatriculadosBySecciones(seccionesForEspecial);
        Map<Long, List<MatriculaSeccion>> mapMatriculados = TypesUtil.convertListToMapList("seccion.id", matriculadosSeccionesAll);

        List<AlumnoGrupoEspecial> alumnosGpoEspAll = new ArrayList();
        for (Seccion seccion : seccionesForEspecial) {
            SeccionGrupoEspecial seccionGpoEspBD = mapSeccionEspecial.get(seccion.getId());
            //List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
            List<MatriculaSeccion> matriculadosSeccion = mapMatriculados.get(seccion.getId());
            logger.debug(" seccion {}, cant. alumnos {}, numero {}",
                    seccion.getId(),
                    matriculadosSeccion.size(),
                    (++contSecciones) + " de " + seccionesForEspecial.size());
            for (MatriculaSeccion matriculaSeccion : matriculadosSeccion) {
                AlumnoGrupoEspecial alumnoGrupoEspecial
                        = new AlumnoGrupoEspecial(
                                matriculaSeccion.getMatriculaResumen().getAlumno(),
                                AlumnoRolExamenEstadoEnum.ACT,
                                ds.getUsuario(),
                                ds.getFechaAccionAudit(),
                                seccionGpoEspBD
                        );
                //seccionGpoEspBD.getAlumnosGrupoEspecial().add(alumnoGrupoEspecial);
                alumnosGpoEspAll.add(alumnoGrupoEspecial);
            }
            //seccionesEspecialesAll.add(seccionGrupoEspecial);
            //seccionGrupoEspecialDAO.save(seccionGrupoEspecial);
            //
            //alumnoGrupoEspecialDAO.createForSeccionGrupoEspecial(seccionGrupoEspecial.getAlumnosGrupoEspecial(), seccionGrupoEspecial, ds.getFechaAccionAudit(), ds.getUsuario());
        }
        alumnoGrupoEspecialDAO.saveList(alumnosGpoEspAll);

    }

    private List<String> getDiaHoraList(List<FechaHoraGrupoExamen> fechasHorasGpoExamen) {
        List<String> result = fechasHorasGpoExamen
                .stream().map(x -> x.getDia().getNumeroDia() + "-" + x.getHora().getNumero()).collect(Collectors.toList());
        return result;
    }

    private void calcularGruposEspeciales(String letraEspeciales,
            List<CursoMasivoExamen> cursosMasivosAll,
            List<SeccionGrupoEspecial> seccionesGrupoEspecialByRolExamen,
            Map<String, List<Seccion>> mapSeccionesGroupByLetra,
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<Seccion> seccionesEspecialesRecolected,
            DataSessionPivot ds) {

        List<Seccion> seccionesByLetra = mapSeccionesGroupByLetra.get(letraEspeciales);
        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesByLetra);
        Map horariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);

        List<Seccion> seccionesOera = seccionesByLetra.stream().filter(x -> x.getAula().getOficinaSupervisora().isOficinaOera()).collect(Collectors.toList());
        List<Seccion> seccionesOtherOfi = seccionesByLetra.stream().filter(x -> !x.getAula().getOficinaSupervisora().isOficinaOera()).collect(Collectors.toList());
        seccionesOera.addAll(seccionesOtherOfi);

        for (Seccion seccion : seccionesOera) {
            seccion.setHorarioSeccion((List<HorarioSeccion>) horariosBySeccion.get(seccion.getId()));
            boolean withMatch = false;
            for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
                //Arrays.equals((String[]) seccion.getDiaHoraList().toArray(new String[0]), (String[]) letraGrupoRegular.getGrupoHorasExamen().getDiaHoraList().toArray(new String[0]))
                if (seccion.getDiaHoraList().containsAll(getDiaHoraList(letraGrupoRegular.getGrupoHorasExamen().getFechasHorasGruposExamen()))
                        //                        || letraGrupoRegular.getGrupoHorasExamen().getDiaHoraList().containsAll(seccion.getDiaHoraList())) {
                        || getDiaHoraList(letraGrupoRegular.getGrupoHorasExamen().getFechasHorasGruposExamen()).containsAll(seccion.getDiaHoraList())) {
                    List<CursoMasivoExamen> cursosMasivosByRolExamenesAndGrupoHoras = cursosMasivosAll.stream()
                            .filter(x -> x.getGrupoHorasExamen() != null)
                            .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                            .collect(Collectors.toList());

                    List<SeccionGrupoEspecial> seccionesGrupoEspecialByRolExamenAndGrupoHorasExamen = seccionesGrupoEspecialByRolExamen.stream()
                            .filter(x -> x.getGrupoHorasExamen() != null)
                            .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                            .collect(Collectors.toList());

                    boolean result
                            = grupoRegularConnector.procesarSeccionesByLetra(
                                    letraGrupoRegular,
                                    cursosMasivosByRolExamenesAndGrupoHoras,
                                    seccionesGrupoEspecialByRolExamenAndGrupoHorasExamen,
                                    seccion,
                                    seccionesByLetra,
                                    letrasGruposRegulares,
                                    cursosMasivosAll, ds);

                    if (result) {
                        logger.debug("Grupo especial {}, encontro match con {}", letraEspeciales, letraGrupoRegular.getLetra());
                        withMatch = true;
                        break;
                    }
                }
            }
            if (!withMatch) {
                seccionesEspecialesRecolected.add(seccion);
            }
        }

    }

    public List<GrupoHorasExamen> allGrupoHorasExamenByRol(
            RolExamenes rolExamenes) {
        List<GrupoHorasExamen> gruposHorasExamen = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        Collections.sort(gruposHorasExamen, (p1, p2) -> p1.getGrupoHoras().getLetra().compareTo(p2.getGrupoHoras().getLetra()));

        List<FechaHoraGrupoExamen> fechasHorasGrupos = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(gruposHorasExamen);
        for (GrupoHorasExamen grupoHorasExamen : gruposHorasExamen) {
            //   List<FechaHoraGrupoExamen> fechasHorasGrupo = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(grupoHorasExamen);
            List<FechaHoraGrupoExamen> fechasHorasGrupo
                    = fechasHorasGrupos.stream().filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen)).collect(Collectors.toList());
            grupoHorasExamen.setFechasHorasGruposExamen(fechasHorasGrupo);
            grupoHorasExamen.setSemanaExamen(fechasHorasGrupo != null && !fechasHorasGrupo.isEmpty() ? fechasHorasGrupo.get(0).getSemanaExamen() : null);
        }
        return gruposHorasExamen;
    }

    public List<LetraGrupoRegular> convertLetraToLetraGpo(List<String> letras,
            RolExamenes rolExamenes,
            List<GrupoHorasExamen> gruposHorasExamenes,
            DateTime today,
            Usuario usuario) {

        Collections.sort(letras, (p1, p2) -> p1.compareTo(p2));
        List<LetraGrupoRegular> letrasGruposRegulares = new ArrayList();
        letras.forEach(letra -> {
            LetraGrupoRegular letraGrupoRegular = new LetraGrupoRegular(letra, rolExamenes, today.toDate(), usuario);
            GrupoHorasExamen grupoHorasExamen
                    = gruposHorasExamenes.stream().filter(ghe -> ghe.getGrupoHoras().getCodigo().equals(letra)).findFirst().orElse(null);
            if (grupoHorasExamen == null) {
                grupoHorasExamen
                        = gruposHorasExamenes.stream().filter(ghe -> ghe.getGrupoHoras().getLetra().equals(letra)).findFirst().orElse(null);
            }
            letraGrupoRegular.setGrupoHorasExamen(grupoHorasExamen);
            letrasGruposRegulares.add(letraGrupoRegular);
        });
        return letrasGruposRegulares;
    }

    private void crearLetrasGruposRegulares(
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<CursoMasivoExamen> cursosMasivosByRolExamenes,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Map<String, List<Seccion>> seccionesGroupByLetra,
            List<Seccion> seccionesEspecialesRecolected, DataSessionPivot ds) {

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            System.out.println("Analizando letra " + letraGrupoRegular.getLetra());
            this.rolExamenesLogger.addMessageLevel2("Letra %s ", letraGrupoRegular.getLetra());
            List<CursoMasivoExamen> cursosMasivosByLetra = cursosMasivosByRolExamenes.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                    .collect(Collectors.toList());
            List<SeccionGrupoEspecial> seccionesGpoEspecialByLetra = seccionesGrupoEspecial.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                    .collect(Collectors.toList());

            grupoRegularConnector.crearLetraGrupoRegularByLetra(
                    letraGrupoRegular,
                    cursosMasivosByLetra,
                    seccionesGpoEspecialByLetra,
                    seccionesGroupByLetra,
                    seccionesEspecialesRecolected,
                    letrasGruposRegulares,
                    cursosMasivosByRolExamenes,
                    seccionesGrupoEspecial,
                    ds);
        }
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteGrupoRegular(RolExamenes rolExamenes) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolBD);

        List<LetraGrupoRegular> letrasGruposRegular = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        logger.debug("Letras Grupos Regulares a eliminar {}", letrasGruposRegular.size());

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegular) {
            horarioAulaDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
            grupoRegularExamenDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
            alumnoGrupoRegularDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
            seccionGrupoRegularDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
        }

        List<SeccionGrupoRegular> seccionesGruposRegularesExc = seccionGrupoRegularDAO.allByRolExamenes(rolExamenes, SeccionRolExamenEstadoEnum.EXC);
        List<Seccion> seccionesExcluidas = seccionesGruposRegularesExc.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        if (!seccionesExcluidas.isEmpty()) {
            seccionExcluidoDAO.deleteBySecciones(seccionesExcluidas);
        }

        letraGrupoRegularDAO.deleteByRolExamenes(rolExamenes);
    }

    @Override
    public List<LetraGrupoRegular> listGruposRegulares(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<LetraGrupoRegular> letrasGruposRegulares = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);

        Map<Long, Integer> countGruposRegulares = grupoRegularExamenDAO.countByLetrasGruposRegulares(letrasGruposRegulares, GrupoHorasRolExamenEstadoEnum.ACT);
        Map<Long, Integer> countSeccionesGruposRegulares = seccionGrupoRegularDAO.countByLetrasGruposRegulares(letrasGruposRegulares, SeccionRolExamenEstadoEnum.ACT);
        Map<Long, Integer> countAlumnosGruposRegulares = alumnoGrupoRegularDAO.countByLetrasGruposRegulares(letrasGruposRegulares, AlumnoRolExamenEstadoEnum.ACT);

        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            letraGruposRegular.setGruposRegularesActivosCount(countGruposRegulares.get(letraGruposRegular.getId()));
            letraGruposRegular.setSeccionesRegularesActivosCount(countSeccionesGruposRegulares.get(letraGruposRegular.getId()));
            letraGruposRegular.setAlumnosRegularesActivosCount(countAlumnosGruposRegulares.get(letraGruposRegular.getId()));
        }

        return letrasGruposRegulares;
    }

    @Override
    public List<GrupoRegularExamen> allGruposRegularExamenByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        List<GrupoRegularExamen> grupos = grupoRegularExamenDAO.
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, Arrays.asList(GrupoHorasRolExamenEstadoEnum.ACT, GrupoHorasRolExamenEstadoEnum.EXC));
        return grupos;
    }

    @Override
    public List<SeccionGrupoRegular> allSeccionesGrupoRegularExamenByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        List<SeccionGrupoRegular> secciones = seccionGrupoRegularDAO.
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, SeccionRolExamenEstadoEnum.ACT, SeccionRolExamenEstadoEnum.EXC);
        return secciones;
    }

    @Override
    public List<AlumnoGrupoRegular> allAlumnosGrupoRegularByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        List<AlumnoGrupoRegular> alumnos = alumnoGrupoRegularDAO.
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, AlumnoRolExamenEstadoEnum.ACT, AlumnoRolExamenEstadoEnum.EXC);
        return alumnos;
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirGrupoRegular(GrupoRegularExamen grupoRegularExamen, DataSessionPivot ds) {
        grupoRegularExamen = grupoRegularExamenDAO.find(grupoRegularExamen.getId());
        grupoRegularExamen.setUsuarioExclusion(ds.getUsuario());
        grupoRegularExamen.setFechaExclusion(new Date());
        grupoRegularExamen.setEstadoEnum(GrupoHorasRolExamenEstadoEnum.EXC);
        grupoRegularExamenDAO.updateEstado(grupoRegularExamen);

        List<Seccion> secciones = seccionDAO.allByCicloAndGrupoHoras(ds.getCicloAcademico(), grupoRegularExamen.getGrupoHoras());
        List<SeccionGrupoRegular> seccionesGruposRegulares
                = seccionGrupoRegularDAO.allByLetraGrupoRegularAndSecciones(grupoRegularExamen.getLetraGrupoRegular(), secciones);
        for (SeccionGrupoRegular seccionGrupoRegular : seccionesGruposRegulares) {
            this.excluirGrupoRegular(seccionGrupoRegular, ds);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirGrupoRegular(SeccionGrupoRegular seccionGrupoRegular, DataSessionPivot ds) {
        seccionGrupoRegular = seccionGrupoRegularDAO.find(seccionGrupoRegular.getId());

        RolExamenes rolExamenes = seccionGrupoRegular.getLetraGrupoRegular().getRolExamenes();
        grupoRegularConnector.validarSituacion("excluir", "los grupos regulares", rolExamenes.isSituacionConfigurarGrupoRegular());
        Assert.isTrue(seccionGrupoRegular.isEstadoActivo(), "Solo se puede excluir las secciones regulares activas");

        SeccionGrupoRegular seccionGrupoRegularUpd = new SeccionGrupoRegular(seccionGrupoRegular.getId());
        seccionGrupoRegularUpd.setUsuarioExclusion(ds.getUsuario());
        seccionGrupoRegularUpd.setFechaExclusion(ds.getFechaAccionAudit());
        seccionGrupoRegularDAO.updateEstadoExclusion(seccionGrupoRegularUpd);

        SeccionExcluido seccionExcluido = new SeccionExcluido();
        seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
        seccionExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        seccionExcluido.setRolExamenes(seccionGrupoRegular.getLetraGrupoRegular().getRolExamenes());
        seccionExcluido.setSeccion(seccionGrupoRegular.getSeccion());
        seccionExcluido.setUserRegistro(ds.getUsuario());
        seccionExcluidoDAO.save(seccionExcluido);

        horarioAulaDAO.deleteBySeccionGrupoRegular(seccionGrupoRegular);

        List<AlumnoGrupoRegular> alumnosGrupoRegularBySeccion = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionGrupoRegular, AlumnoRolExamenEstadoEnum.ACT);
        for (AlumnoGrupoRegular alumnoGrupoRegular : alumnosGrupoRegularBySeccion) {
            this.excluirGrupoRegular(alumnoGrupoRegular, ds);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void activarGrupoRegular(SeccionGrupoRegular seccionGrupoRegular, DataSessionPivot ds) {
        seccionGrupoRegular = seccionGrupoRegularDAO.find(seccionGrupoRegular.getId());

        RolExamenes rolExamenes = seccionGrupoRegular.getLetraGrupoRegular().getRolExamenes();
        grupoRegularConnector.validarSituacion("incluir", "los grupos regulares", rolExamenes.isSituacionConfigurarGrupoRegular());
        Assert.isTrue(seccionGrupoRegular.isEstadoExcluido(), "Solo se puede incluir las secciones regulares excluidas");

        this.activarValidarCruce(seccionGrupoRegular);

        SeccionGrupoRegular seccionGrupoRegularUpd = new SeccionGrupoRegular(seccionGrupoRegular.getId());
        seccionGrupoRegularUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
        seccionGrupoRegularDAO.updateEstado(seccionGrupoRegularUpd);

        List<FechaHoraGrupoExamen> fechasHorasGruposExamen = fechaHoraGrupoExamenDAO
                .allByGrupoHorasExamen(seccionGrupoRegular.getLetraGrupoRegular().getGrupoHorasExamen());
        for (FechaHoraGrupoExamen fechaHoraGrupoExamen : fechasHorasGruposExamen) {
            HorarioAula horarioAula = new HorarioAula(fechaHoraGrupoExamen, seccionGrupoRegular.getSeccion());
            horarioAula.setSeccionGrupoRegular(seccionGrupoRegular);
            horarioAula.setRolExamenes(seccionGrupoRegular.getLetraGrupoRegular().getRolExamenes());
            horarioAulaDAO.save(horarioAula);
        }

        SeccionExcluido seccionExcluido = seccionExcluidoDAO.findByRolExamenesAndSeccion(rolExamenes, seccionGrupoRegular.getSeccion(), EstadoEnum.ACT);
        if (seccionExcluido != null) {
            seccionExcluido.setEstadoEnum(EstadoEnum.ANU);
            seccionExcluidoDAO.update(seccionExcluido);
        }
        List<AlumnoGrupoRegular> alumnosGrupoRegularBySeccion = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionGrupoRegular, AlumnoRolExamenEstadoEnum.EXC);
        for (AlumnoGrupoRegular alumnoGrupoRegular : alumnosGrupoRegularBySeccion) {
            this.activarGrupoRegular(alumnoGrupoRegular, ds);
        }
    }

    public void activarValidarCruce(SeccionGrupoRegular seccionGrupoRegular) {
        rolExamenesLogger.activarGrupoRegular();
        rolExamenesLogger.setAulas(grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(seccionGrupoRegular.getLetraGrupoRegular().getRolExamenes(), null));
        GrupoHorasExamen grupoHorasExamen = seccionGrupoRegular.getLetraGrupoRegular().getGrupoHorasExamen();
        Seccion seccion = seccionDAO.find(seccionGrupoRegular.getSeccion());
        seccion = seccion.clone();
        List<Aula> aulas = Arrays.asList(seccion.getAula());

        List<Docente> docentesOrigen = Arrays.asList(seccionGrupoRegular.getDocente());

        List<AlumnoGrupoRegular> alumnosSeccionRegularOrigen = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionGrupoRegular, AlumnoRolExamenEstadoEnum.EXC);
        List<Alumno> alumnosOrigen = alumnosSeccionRegularOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        boolean validacionCursosMasivos = grupoRegularConnector.validarCursosMasivos(grupoHorasExamen, docentesOrigen, aulas, alumnosOrigen);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(grupoHorasExamen, alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(grupoHorasExamen, docentesOrigen, aulas, alumnosOrigen);

        if (!validacionCursosMasivos || !validacionGruposRegulares || !validacionSeccionesEspeciales) {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirGrupoRegular(AlumnoGrupoRegular alumnoGrupoRegular, DataSessionPivot ds) {
        alumnoGrupoRegular = alumnoGrupoRegularDAO.find(alumnoGrupoRegular.getId());
        RolExamenes rolExamenes = alumnoGrupoRegular.getSeccionGrupoRegular().getLetraGrupoRegular().getRolExamenes();
        grupoRegularConnector.validarSituacion("excluir", "los grupos regulares", rolExamenes.isSituacionConfigurarGrupoRegular());
        Assert.isTrue(alumnoGrupoRegular.isEstadoActivo(), "Solo se puede excluir las alumnos regulares activos");

        AlumnoGrupoRegular alumnoGrupoRegularUpd = new AlumnoGrupoRegular(alumnoGrupoRegular.getId());
        alumnoGrupoRegularUpd.setUsuarioExclusion(ds.getUsuario());
        alumnoGrupoRegularUpd.setFechaExclusion(ds.getFechaAccionAudit());
        alumnoGrupoRegularDAO.updateEstadoExclusion(alumnoGrupoRegularUpd);
    }

    public void activarGrupoRegular(AlumnoGrupoRegular alumnoGrupoRegular, DataSessionPivot ds) {
        AlumnoGrupoRegular alumnoGrupoRegularUpd = new AlumnoGrupoRegular(alumnoGrupoRegular.getId());
        alumnoGrupoRegularUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
        alumnoGrupoRegularDAO.updateEstado(alumnoGrupoRegularUpd);
    }

    @Override
    public List<SeccionGrupoRegular> allSeccionesGpoRegByDynatableLetra(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular) {
        List<SeccionGrupoRegular> seccionesLetraGrupoRegular = seccionGrupoRegularDAO.allByDynatableLetra(filter, letraGrupoRegular);
        Map<Long, Integer> mapCountAlumnosBySeccion = alumnoGrupoRegularDAO.countBySeccionesGruposRegulares(seccionesLetraGrupoRegular, AlumnoRolExamenEstadoEnum.ACT);
        for (SeccionGrupoRegular seccionGrupoRegular : seccionesLetraGrupoRegular) {
            seccionGrupoRegular.setAlumnosCount(mapCountAlumnosBySeccion.get(seccionGrupoRegular.getId()) != null ? mapCountAlumnosBySeccion.get(seccionGrupoRegular.getId()) : 0);
        }

        return seccionesLetraGrupoRegular;
    }

    @Override
    public List<SeccionGrupoRegular> allSeccionesGpoRegByDynatableRol(DynatableFilter filter, RolExamenes rolExamenes) {
        List<SeccionGrupoRegular> seccionesLetraGrupoRegular = seccionGrupoRegularDAO.allByDynatableRol(filter, rolExamenes);
        Map<Long, Integer> mapCountAlumnosBySeccion = alumnoGrupoRegularDAO.countBySeccionesGruposRegulares(seccionesLetraGrupoRegular, AlumnoRolExamenEstadoEnum.ACT);
        for (SeccionGrupoRegular seccionGrupoRegular : seccionesLetraGrupoRegular) {
            seccionGrupoRegular.setAlumnosCount(mapCountAlumnosBySeccion.get(seccionGrupoRegular.getId()) != null ? mapCountAlumnosBySeccion.get(seccionGrupoRegular.getId()) : 0);
        }

        return seccionesLetraGrupoRegular;
    }

    @Override
    public List<AlumnoGrupoRegular> allAlumnosGrupoRegularDynaByLetraGrupoReg(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular) {
        List<AlumnoGrupoRegular> alumnosLetraGrupoRegular = alumnoGrupoRegularDAO.allByDynatableAndLetraGrupoRegular(filter, letraGrupoRegular);
        return alumnosLetraGrupoRegular;
    }

    @Override
    @Transactional
    public void agregarGruposNuevos(RolExamenes rolExamenesForm, DataSessionPivot ds) {
        RolExamenes rolExamenesBD = rolExamenesDAO.find(rolExamenesForm.getId());
        CicloAcademico ciclo = rolExamenesBD.getEventoCicloAcademico().getCicloAcademico();
        List<Seccion> secciones = seccionDAO.allForExamenByCiclo(ciclo);
        this.excluirProcesados(secciones, rolExamenesForm);

        for (Seccion secc : secciones) {
            //System.out.println("secc:" + secc.getId() + "-" + secc.getCodigo2());
            System.out.println(secc.getId() + "-" + secc.getGrupoHoras().getCodigo());
        }

        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenesForm, SeccionRolExamenEstadoEnum.ACT);
        System.out.println("Llegaron " + seccionesGrupoEspecial.size() + " secciones especiales");
        grupoRegularConnector.fillActiveInfoGrupoEspecial(seccionesGrupoEspecial);

        List<Aula> aulas = grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolExamenesBD, null);
        rolExamenesLogger.setAulas(aulas);

        List<SemanaExamen> semanas = semanaExamenDAO.allByRolExamenes(rolExamenesForm);
        Date fechaInicio = semanas.stream().min(Comparator.comparing(SemanaExamen::getFechaInicio)).map(x -> x.getFechaInicio()).get();
        Date fechaFin = semanas.stream().max(Comparator.comparing(SemanaExamen::getFechaFin)).map(x -> x.getFechaFin()).get();
        List<HorarioAula> horariosAulasByRango = horarioAulaDAO.allByRangoNotByTipo(fechaInicio, fechaFin, TipoHorarioAulaEnum.EXAM, aulas);
        Map<Long, List<HorarioAula>> mapHorarioAulas = TypesUtil.convertListToMapList("aula.id", horariosAulasByRango);
        rolExamenesLogger.setHorarioAulas(mapHorarioAulas);

        List<LetraGrupoRegular> letrasGruposRegulares = letraGrupoRegularDAO.allByRolExamenes(rolExamenesForm);
        grupoRegularConnector.fillActiveInfoLetrasGruposRegulares(letrasGruposRegulares);

        Map<String, List<Seccion>> mapSeccionesByLetra = this.agruparByLetra(secciones, letrasGruposRegulares, ciclo);
        System.out.println("mapSeccionesByLetra.size=" + mapSeccionesByLetra.size());
        List<List<Seccion>> lista = new ArrayList(mapSeccionesByLetra.values());
        int contar = 0;
        for (List<Seccion> wer : lista) {
            contar += wer.size();
        }
        System.out.println("mapSeccionesByLetra.elementos=" + contar);

        List<CursoMasivoExamen> cursosMasivosByRolExamenes = new ArrayList();
        //this.validationsCalculoExamenesGrupoRegular(rolExamenesForm, cursosMasivosByRolExamenes);
        List<Seccion> seccionesEspecialesRecolected = new ArrayList();

        this.crearLetrasGruposRegularesNuevos(
                letrasGruposRegulares,
                cursosMasivosByRolExamenes,
                seccionesGrupoEspecial,
                mapSeccionesByLetra,
                seccionesEspecialesRecolected, ds);

        List<String> letras = new ArrayList(mapSeccionesByLetra.keySet());
        //quitamos las letras regulares de los grupos especiales
        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            letras.removeIf(x -> x.equals(letraGruposRegular.getLetra()));
        }
        logger.info("Letras Grupos Especiales {}", letras.toString());

        this.rolExamenesLogger.setRolExamenes(rolExamenesForm.getId());
        for (String letra : letras) {
            this.calcularGruposEspeciales(
                    letra,
                    cursosMasivosByRolExamenes,
                    seccionesGrupoEspecial,
                    mapSeccionesByLetra,
                    letrasGruposRegulares,
                    seccionesEspecialesRecolected, ds);
        }
        logger.info("Secciones Especiales sin asignar {}", seccionesEspecialesRecolected.size());
        this.saveSeccionesEspeciales(seccionesEspecialesRecolected, rolExamenesForm, ds);

        List<SeccionGrupoRegular> seccionesGpoRegAll = new ArrayList();
        List<Seccion> seccionesForSave = new ArrayList();

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            logger.debug("guardara la letra {}", letraGrupoRegular.getLetra());
            letraGrupoRegularDAO.save(letraGrupoRegular);

            List<SeccionGrupoRegular> seccionesGpoRegular = letraGrupoRegular.getSeccionesGruposRegulares();
//            List<SeccionGrupoRegular> seccionGpoRegularOera = seccionesGpoRegular.stream()
//                    .filter(x -> seccionesOera.contains(x.getSeccion()))
//                    .collect(Collectors.toList());
            for (SeccionGrupoRegular seccionGpoReg : seccionesGpoRegular) {
                if (seccionGpoReg.getId() != null) {
                    continue;
                }
                seccionGpoReg.setUserRegistro(ds.getUsuario());
                seccionGpoReg.setFechaRegistro(ds.getFechaAccionAudit());
                seccionGpoReg.setLetraGrupoRegular(letraGrupoRegular);
                seccionGpoReg.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
                seccionesGpoRegAll.add(seccionGpoReg);
            }
            //seccionesGpoRegAll.addAll(seccionesGpoRegular);
        }
        seccionGrupoRegularDAO.saveList(seccionesGpoRegAll);

        List<HorarioAula> horariosSeccionGpoRegAll = new ArrayList();
        List<AlumnoGrupoRegular> alumnosSeccGpoRegAll = new ArrayList();

        List<Seccion> seccionesAfectadas = seccionesGpoRegAll.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        //List<SeccionGrupoRegular> seccionesGpoRegularBD = seccionGrupoRegularDAO.allByLetraGrupoRegularAndSecciones(letraGrupoRegular, seccionesAfectadas);
        List<SeccionGrupoRegular> seccionesGpoRegularBD = seccionGrupoRegularDAO.allBySecciones(seccionesAfectadas);
        Map<Long, SeccionGrupoRegular> mapSeccionGpoRegularBD = TypesUtil.convertListToMap("seccion.id", seccionesGpoRegularBD);

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            List<SeccionGrupoRegular> seccionesGpoRegular = letraGrupoRegular.getSeccionesGruposRegulares();

            for (SeccionGrupoRegular seccionGpoRegular : seccionesGpoRegular) {
                SeccionGrupoRegular seccionGpoRegularBD = mapSeccionGpoRegularBD.get(seccionGpoRegular.getSeccion().getId());
                List<AlumnoGrupoRegular> alumnosSeccGpoReg = seccionGpoRegular.getAlumnosGruposRegulares();
                for (AlumnoGrupoRegular agr : alumnosSeccGpoReg) {
                    if (agr.getId() != null) {
                        continue;
                    }
                    agr.setSeccionGrupoRegular(seccionGpoRegularBD);
                    agr.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                    agr.setFechaRegistro(ds.getFechaAccionAudit());
                    agr.setUserRegistro(ds.getUsuario());
                    alumnosSeccGpoRegAll.add(agr);
                }

                List<HorarioAula> horariosAulaSeccReg = seccionGpoRegular.getHorariosAula();
                for (HorarioAula horarioAula : horariosAulaSeccReg) {
                    if (horarioAula.getId() != null) {
                        continue;
                    }
                    horarioAula.setSeccionGrupoRegular(seccionGpoRegularBD);
                    horariosSeccionGpoRegAll.add(horarioAula);
                    //horarioAulaDAO.save(horarioAula);
                }

            }
        }
        alumnoGrupoRegularDAO.saveAll(alumnosSeccGpoRegAll);
        horarioAulaDAO.saveList(horariosSeccionGpoRegAll);

        if (1 == 1) {
            //throw new PhobosException("3453-53-45-345-34-5");
        }

    }

    private void crearLetrasGruposRegularesNuevos(
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<CursoMasivoExamen> cursosMasivosByRolExamenes,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Map<String, List<Seccion>> mapSeccionesByLetra,
            List<Seccion> seccionesEspecialesRecolected, DataSessionPivot ds) {

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            //this.rolExamenesLogger.addMessageLevel2("Letra %s ", letraGrupoRegular.getLetra());
            List<Seccion> seccionesByLetra = TypesUtil.getListNotNull(mapSeccionesByLetra.get(letraGrupoRegular.getLetra()));
            if (seccionesByLetra.isEmpty()) {
                System.out.println("NO entro 3452345-2345-23-45-23-45");
                continue;
            }
            System.out.println("Entro a la letra " + letraGrupoRegular.getLetra());

            List<CursoMasivoExamen> cursosMasivosByLetra = cursosMasivosByRolExamenes.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().getId().equals(letraGrupoRegular.getGrupoHorasExamen().getId()))
                    .collect(Collectors.toList());
            List<SeccionGrupoEspecial> seccionesGpoEspecialByLetra = seccionesGrupoEspecial.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().getId().equals(letraGrupoRegular.getGrupoHorasExamen().getId()))
                    .collect(Collectors.toList());

            System.out.println("cursosMasivosByLetra.size = " + cursosMasivosByLetra.size());
            System.out.println("seccionesGpoEspecialByLetra.size = " + seccionesGpoEspecialByLetra.size());

            grupoRegularConnector.crearLetraGrupoRegularByLetra(
                    letraGrupoRegular,
                    cursosMasivosByLetra,
                    seccionesGpoEspecialByLetra,
                    mapSeccionesByLetra,
                    seccionesEspecialesRecolected,
                    letrasGruposRegulares,
                    cursosMasivosByRolExamenes,
                    seccionesGrupoEspecial,
                    ds);
        }
    }

    private void excluirProcesados(List<Seccion> secciones, RolExamenes rolExamenesForm) {
        Map<Long, Seccion> mapSeccion = TypesUtil.convertListToMap("id", secciones);
        List<SeccionCursoMasivo> seccionesCM = seccionCursoMasivoDAO.allByRolExamenes(rolExamenesForm, SeccionRolExamenEstadoEnum.ACT);
        int count = 0;
        System.out.println("sewcciones1=" + secciones.size());
        for (SeccionCursoMasivo seccionCurMas : seccionesCM) {
            Seccion sec = seccionCurMas.getSeccion();
            Seccion seccion = mapSeccion.get(sec.getId());
            if (seccion != null) {
                secciones.remove(seccion);
            }

        }
        System.out.println("sewcciones2=" + secciones.size());

        List<SeccionGrupoRegular> seccionesGR = seccionGrupoRegularDAO.allByRolExamenes(rolExamenesForm, SeccionRolExamenEstadoEnum.ACT);
        for (SeccionGrupoRegular seccionGpoReg : seccionesGR) {
            Seccion sec = seccionGpoReg.getSeccion();
            Seccion seccion = mapSeccion.get(sec.getId());
            if (seccion != null) {
                secciones.remove(seccion);
            }
        }

        System.out.println("sewcciones3=" + secciones.size());
        List<SeccionGrupoEspecial> seccionesGE = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenesForm, SeccionRolExamenEstadoEnum.ACT);
        for (SeccionGrupoEspecial seccionGpoEsp : seccionesGE) {
            Seccion sec = seccionGpoEsp.getSeccion();
            Seccion seccion = mapSeccion.get(sec.getId());
            if (seccion != null) {
                secciones.remove(seccion);
            }
        }

        System.out.println("sewcciones4=" + secciones.size());
        List<SeccionExcluido> seccionesExcluidas = seccionExcluidoDAO.allByRolExamenes(rolExamenesForm);
        List<CursoExcluido> cursosExcluidos = cursoExcluidoDAO.allByRolExamenes(rolExamenesForm, EstadoEnum.ACT);
        this.quitarSeccionesExcluidas(secciones, seccionesExcluidas, cursosExcluidos);
        System.out.println("sewcciones5=" + secciones.size());
    }

    @Override
    @Transactional
    public List<String> cambiarAula(SeccionGrupoRegular seccionGpoRegForm, DataSessionPivot ds) {
        SeccionGrupoRegular seccionGpoRegBD = seccionGrupoRegularDAO.find(seccionGpoRegForm.getId());
        RolExamenes rolExamenes = seccionGpoRegBD.getLetraGrupoRegular().getRolExamenes();
        Aula aulaDestinoForm = seccionGpoRegForm.getAula();

        Assert.isNotNull(aulaDestinoForm, "Debe indicar a que aula va mover esta sección");
        Assert.isNotNull(aulaDestinoForm.getCodigo(), "Debe indicar a que aula va mover esta sección");

        Aula aulaDestinoBD = aulaDAO.findByCode(aulaDestinoForm.getCodigo());
        Assert.isNotNull(aulaDestinoBD, "El aula " + aulaDestinoForm.getCodigo() + " no existe");
        Assert.isTrue(aulaDestinoBD.getEstadoEnum() == EstadoEnum.ACT, "El aula " + aulaDestinoForm.getCodigo() + " no se encuentra activa");

        Aula aulaOld = seccionGpoRegBD.getAula();

        boolean mismaAula = aulaOld == null ? false : (aulaOld.getId().compareTo(aulaDestinoBD.getId()) == 0);
        Assert.isFalse(mismaAula, "No ha indicado ningún cambio de aula");

        List<AlumnoGrupoRegular> alumnosGpoReg = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionGpoRegBD, AlumnoRolExamenEstadoEnum.ACT);
        seccionGpoRegBD.setAlumnosGruposRegulares(alumnosGpoReg);

        GrupoHorasExamen gpoHorasExamBD = seccionGpoRegBD.getLetraGrupoRegular().getGrupoHorasExamen();
        List<FechaHoraGrupoExamen> fechasHorasGpo = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gpoHorasExamBD);
        gpoHorasExamBD.setFechasHorasGruposExamen(fechasHorasGpo);

        List<SemanaExamen> semanasByRolExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        Date fechaInicio = semanasByRolExamen.stream().min(Comparator.comparing(SemanaExamen::getFechaInicio)).map(x -> x.getFechaInicio()).get();
        Date fechaFin = semanasByRolExamen.stream().max(Comparator.comparing(SemanaExamen::getFechaFin)).map(x -> x.getFechaFin()).get();
        List<Aula> aulasDestino = fillAulas(aulaDestinoBD, fechaInicio, fechaFin);

        List<GrupoHorasExamen> gposHoraExamenAll = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<FechaHoraGrupoExamen> fechasHoras = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gposHoraExamenAll);
        Map<Long, List<FechaHoraGrupoExamen>> mapFechaHora = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHoras);
        for (GrupoHorasExamen gHora : gposHoraExamenAll) {
            gHora.setFechasHorasGruposExamen(mapFechaHora.get(gHora.getId()));
        }

        List<String> restricciones = new ArrayList();

        boolean verificarCruceAulas = revisarCrucesAula(gpoHorasExamBD, aulasDestino, restricciones);

        if (verificarCruceAulas) {
            cambiarAulaForSeccionRegular(seccionGpoRegBD, aulaDestinoBD, gpoHorasExamBD);
        }
        return restricciones;
    }

    private void cambiarAulaForSeccionRegular(SeccionGrupoRegular seccionGpoReg, Aula aulaDestino, GrupoHorasExamen gpoExamDestino) {
        List<FechaHoraGrupoExamen> fechasHorasGpo = gpoExamDestino.getFechasHorasGruposExamen();

        seccionGpoReg.setAula(aulaDestino);
        seccionGrupoRegularDAO.update(seccionGpoReg);

        List<HorarioAula> horarios = new ArrayList();
        horarioAulaDAO.deleteBySeccionGrupoRegular(seccionGpoReg);
        for (FechaHoraGrupoExamen fechaGpoExam : fechasHorasGpo) {
            HorarioAula ha = new HorarioAula(fechaGpoExam, aulaDestino);
            ha.setSeccionGrupoRegular(seccionGpoReg);
            ha.setSeccion(seccionGpoReg.getSeccion());
            ha.setRolExamenes(seccionGpoReg.getLetraGrupoRegular().getRolExamenes());
            horarios.add(ha);
        }
        horarioAulaDAO.saveList(horarios);

    }

    private boolean revisarTripleExamen(
            GrupoHorasExamen gpoExamDestino,
            Map<Long, GrupoHorasExamen> mapGrupoHoraExamen,
            List<Alumno> alumnos,
            Map<Long, List<AlumnoCursoMasivo>> mapAlumnoCursoMasivoByFecha,
            Map<Long, List<AlumnoGrupoRegular>> mapAlumnoSeccionRegularByFecha,
            Map<Long, List<AlumnoGrupoEspecial>> mapAlumnoSeccionEspecialByFecha,
            List<String> restricciones) {

        Date fechaDestino = gpoExamDestino.getFecha();
        boolean existeCruce = false;

        for (Alumno alumno : alumnos) {
            List<AlumnoCursoMasivo> aluCursosMasivos = TypesUtil.getListNotNull(mapAlumnoCursoMasivoByFecha.get(alumno.getId()));
            List<AlumnoGrupoRegular> aluSeccionRegular = TypesUtil.getListNotNull(mapAlumnoSeccionRegularByFecha.get(alumno.getId()));
            List<AlumnoGrupoEspecial> aluSeccionEspecial = TypesUtil.getListNotNull(mapAlumnoSeccionEspecialByFecha.get(alumno.getId()));

            int total = aluCursosMasivos.size() + aluSeccionRegular.size() + aluSeccionEspecial.size();
            if (total >= 2) {
                existeCruce = true;

                StringBuilder msg = new StringBuilder("El alumno ").append(alumno.getCodigo()).append(" ya tiene programado ");
                msg.append(total).append(" examen(es) el ");
                msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es")).append(". ");

                for (AlumnoCursoMasivo alumnoCM : aluCursosMasivos) {
                    Curso curso = alumnoCM.getCursoMasivoExamen().getCurso();
                    GrupoHorasExamen gpoHoraExamenAlu = alumnoCM.getCursoMasivoExamen().getGrupoHorasExamen();
                    GrupoHorasExamen gpoHoraExamenCM = mapGrupoHoraExamen.get(gpoHoraExamenAlu.getId());
                    List<FechaHoraGrupoExamen> fechasHorasGpo = gpoHoraExamenCM.getFechasHorasGruposExamen();
                    Hora horaIni = fechasHorasGpo.stream().map(x -> x.getHora()).min(Comparator.comparing(Hora::getCodigo)).get();
                    Hora horaFin = fechasHorasGpo.stream().map(x -> x.getHora()).max(Comparator.comparing(Hora::getCodigo)).get();

                    msg.append("El curso masivo ").append(curso.getCodigo());
                    msg.append(" de ").append(horaIni.getDescripcion());
                    msg.append(" a ").append(horaFin.getDescripcion()).append(". ");
                }

                for (AlumnoGrupoRegular alumnoGpoReg : aluSeccionRegular) {
                    Seccion seccion = alumnoGpoReg.getSeccionGrupoRegular().getSeccion();
                    Curso curso = seccion.getGrupoSeccion().getCurso();
                    LetraGrupoRegular letraGR = alumnoGpoReg.getSeccionGrupoRegular().getLetraGrupoRegular();
                    GrupoHorasExamen gpoHoraExamenAlu = letraGR.getGrupoHorasExamen();
                    GrupoHorasExamen gpoHoraExamenCM = mapGrupoHoraExamen.get(gpoHoraExamenAlu.getId());
                    List<FechaHoraGrupoExamen> fechasHorasGpo = gpoHoraExamenCM.getFechasHorasGruposExamen();
                    Hora horaIni = fechasHorasGpo.stream().map(x -> x.getHora()).min(Comparator.comparing(Hora::getCodigo)).get();
                    Hora horaFin = fechasHorasGpo.stream().map(x -> x.getHora()).max(Comparator.comparing(Hora::getCodigo)).get();

                    msg.append("El grupo regular ").append(letraGR.getLetra());
                    msg.append(" sección ").append(seccion.getCodigo2());
                    msg.append(" curso ").append(curso.getCodigo());
                    msg.append(" de ").append(horaIni.getDescripcion());
                    msg.append(" a ").append(horaFin.getDescripcion()).append(". ");
                }

                for (AlumnoGrupoEspecial alumnoGE : aluSeccionEspecial) {
                    Seccion seccion = alumnoGE.getSeccionGrupoEspecial().getSeccion();
                    Curso curso = seccion.getGrupoSeccion().getCurso();
                    GrupoHorasExamen gpoHoraExamenAlu = alumnoGE.getSeccionGrupoEspecial().getGrupoHorasExamen();
                    GrupoHorasExamen gpoHoraExamenCM = mapGrupoHoraExamen.get(gpoHoraExamenAlu.getId());
                    List<FechaHoraGrupoExamen> fechasHorasGpo = gpoHoraExamenCM.getFechasHorasGruposExamen();
                    Hora horaIni = fechasHorasGpo.stream().map(x -> x.getHora()).min(Comparator.comparing(Hora::getCodigo)).get();
                    Hora horaFin = fechasHorasGpo.stream().map(x -> x.getHora()).max(Comparator.comparing(Hora::getCodigo)).get();

                    msg.append("El grupo especial ");
                    msg.append(" sección ").append(seccion.getCodigo2());
                    msg.append(" curso ").append(curso.getCodigo());
                    msg.append(" de ").append(horaIni.getDescripcion());
                    msg.append(" a ").append(horaFin.getDescripcion()).append(". ");
                }

                restricciones.add(msg.toString());
            }

        }

        return !existeCruce;
    }

    private boolean revisarCrucesAula(GrupoHorasExamen gpoExamDestino, List<Aula> aulasDestino, List<String> restricciones) {
        List<FechaHoraGrupoExamen> fechasHorasGpo = gpoExamDestino.getFechasHorasGruposExamen();
        Date fechaDestino = fechasHorasGpo.get(0).getFecha();

        boolean existeCruce = false;
        for (Aula aula : aulasDestino) {
            List<HorarioAula> horariosAulas = aula.getHorariosAula();
            for (HorarioAula ha : horariosAulas) {
                if (fechaDestino.compareTo(ha.getFechaInicio()) >= 0 && fechaDestino.compareTo(ha.getFechaFin()) <= 0) {
                    for (FechaHoraGrupoExamen fechaHora : fechasHorasGpo) {
                        if (fechaHora.getIdDiaHora().compareTo(ha.getIdDiaHora()) == 0) {
                            TipoHorarioAulaEnum tipo = ha.getTipoEnum();
                            Hora hora = ha.getHora();
                            existeCruce = true;

                            StringBuilder msg = new StringBuilder("El aula ").append(aula.getCodigo()).append(" se encuentra ocupada el ");
                            msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es"));
                            msg.append(" a las ").append(hora.getDescripcion());

                            if (tipo != null) {
                                msg.append(" con ");
                                switch (tipo) {
                                    case DICT:
                                        msg.append(" un dictado de clases");
                                        break;
                                    case EXAM:
                                        msg.append(" un examen pre-programado");
                                        break;
                                    case RESERV:
                                        msg.append(" una reserva");
                                        break;
                                    default:
                                        msg.append(" algo indefinido");
                                        break;
                                }
                            }

                            msg.append(". ");
                            restricciones.add(msg.toString());
                        }
                    }
                }
            }
        }

        return !existeCruce;

    }

    private List<Aula> fillAulas(Aula aula, Date fechaInicio, Date fechaFin) {
        List<HorarioAula> horariosAula = horarioAulaDAO.allByRango(fechaInicio, fechaFin, aula);
        aula.setHorariosAula(horariosAula);
        List<Aula> aulas = new ArrayList();
        aulas.add(aula);
        return aulas;
    }

}
