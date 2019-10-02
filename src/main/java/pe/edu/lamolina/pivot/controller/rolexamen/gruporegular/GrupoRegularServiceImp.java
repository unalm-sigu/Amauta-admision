package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
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
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.horario.HorarioSeccion;
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
import pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial.GrupoEspecialService;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SemanaExamenDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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
    @Transactional(readOnly = false)
    public void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolBD);

        List<CursoMasivoExamen> cursosMasivosByRolExamenes = new ArrayList();
        this.validationsCalculoExamenesGrupoRegular(rolExamenes, cursosMasivosByRolExamenes);
//        if (true) {
//            return;
//        }
        grupoRegularConnector.fillActiveInfoCursosMasivos(cursosMasivosByRolExamenes);
        List<String> cursosMasivosValidations = this.validarCursosMasivos(cursosMasivosByRolExamenes);
        Assert.isTrue(cursosMasivosValidations.isEmpty(), String.join("\n", cursosMasivosValidations));

        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        grupoRegularConnector.fillActiveInfoGrupoEspecial(seccionesGrupoEspecial);

        this.rolExamenesLogger.iniciarGrupoRegular();

        DateTime today = new DateTime(ds.getFechaAccionAudit());

        List<SeccionExcluido> seccionesExcluidasByRolExamen = seccionExcluidoDAO.allByRolExamenes(rolExamenes);
        List<CursoExcluido> cursosExcluidos = cursoExcluidoDAO.allByRolExamenes(rolExamenes, EstadoEnum.ACT);
        List<Seccion> seccionesEspecialesRecolected = new ArrayList<>();

        rolExamenesLogger.setAulasOera(grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolBD, OficinaEnum.OERA));
        rolExamenesLogger.setAulas(grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolBD, null));

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
        List<Seccion> secciones = this.allSeccionesRegulares(cicloAcademico, TipoGrupoHorasEnum.REGULAR);
        this.quitarSeccionesExcluidas(secciones, seccionesExcluidasByRolExamen, cursosExcluidos);
        this.quitarSeccionesCursosMasivos(secciones, cursosMasivosByRolExamenes);

        List<Seccion> seccionesOera = secciones
                .stream().filter(x -> x.getAula().getOficinaSupervisora().isOficinaOera())
                .collect(Collectors.toList());

        List<Seccion> seccionesOthersOfi = secciones
                .stream().filter(x -> !x.getAula().getOficinaSupervisora().isOficinaOera())
                .collect(Collectors.toList());

        Map<String, List<Seccion>> seccionesGroupByLetra = TypesUtil.convertListToMapList("grupoHoras.letra", seccionesOera);
        //   logger.info("Letras Grupos Regulares Oera {}, Secciones {}", String.join(",", letras), seccionesOera.size());
        this.rolExamenesLogger.addMessageLevel1("Calculo grupos regulares de secciones oera");
        this.crearLetrasGruposRegulares(letrasGruposRegulares, cursosMasivosByRolExamenes, seccionesGrupoEspecial, seccionesGroupByLetra, seccionesEspecialesRecolected, ds);

        seccionesGroupByLetra = TypesUtil.convertListToMapList("grupoHoras.letra", seccionesOthersOfi);
        //    logger.info("Letras Grupos Regulares No Oera {}, Secciones {}", String.join(",", letras), seccionesOthersOfi.size());
        this.rolExamenesLogger.addMessageLevel1("Calculo grupos regulares de secciones no oera");
        this.crearLetrasGruposRegulares(letrasGruposRegulares, cursosMasivosByRolExamenes, seccionesGrupoEspecial, seccionesGroupByLetra, seccionesEspecialesRecolected, ds);

        //  logger.info("Grupos Especiales");
        secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ESPECIAL);
        this.quitarSeccionesExcluidas(secciones, seccionesExcluidasByRolExamen, cursosExcluidos);
        this.quitarSeccionesCursosMasivos(secciones, cursosMasivosByRolExamenes);

        Map<String, List<Seccion>> mapSeccionesGroupByLetra = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);

        //  logger.info("Haciendo encajar grupos especiales en las letras regulares");
        this.rolExamenesLogger.addMessageLevel1("Calculo de grupos especiales en las letras puras");
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            this.rolExamenesLogger.addMessageLevel2("Letra %s ", letraGrupoRegular.getLetra());
            List<CursoMasivoExamen> cursosMasivosByRolExamenesAndGrupoHoras = cursosMasivosByRolExamenes.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                    .collect(Collectors.toList());
            grupoRegularConnector.crearLetraGrupoRegularByLetra(
                    letraGrupoRegular,
                    cursosMasivosByRolExamenesAndGrupoHoras,
                    seccionesGrupoEspecial,
                    mapSeccionesGroupByLetra,
                    seccionesEspecialesRecolected,
                    ds);
        }
        //calculamos el resto de letras
        letras = new ArrayList<>(mapSeccionesGroupByLetra.keySet());
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
                    mapSeccionesGroupByLetra,
                    letrasGruposRegulares, seccionesEspecialesRecolected, ds);
        }
        logger.info("Secciones Especiales sin asignar {}", seccionesEspecialesRecolected.size());
        this.saveSeccionesEspeciales(seccionesEspecialesRecolected, rolExamenes, ds);

        logger.info("letras grupos regulares a guardar {}", letrasGruposRegulares.size());
        List<Seccion> listSeccionesNoOera = new ArrayList<>();
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            logger.debug("guardara la letra {}", letraGrupoRegular.getLetra());
            letraGrupoRegularDAO.save(letraGrupoRegular);

            List<SeccionGrupoRegular> seccionesGpoRegular = letraGrupoRegular.getSeccionesGruposRegulares();
            List<SeccionGrupoRegular> seccionGpoRegularOera = seccionesGpoRegular.stream()
                    .filter(x -> seccionesOera.contains(x.getSeccion()))
                    .collect(Collectors.toList());
            seccionGrupoRegularDAO.createForLetraGrupoRegular(seccionGpoRegularOera, letraGrupoRegular, ds.getFechaAccionAudit(), ds.getUsuario());

            List<SeccionGrupoRegular> seccionGpoRegularNoOera = new ArrayList<>(seccionesGpoRegular);
            for (SeccionGrupoRegular seccionGrupoRegularOera : seccionGpoRegularOera) {
                seccionGpoRegularNoOera.removeIf(x -> x.getSeccion().equals(seccionGrupoRegularOera.getSeccion()));
            }
            for (SeccionGrupoRegular seccionGrupoRegular : seccionGpoRegularNoOera) {
                seccionGrupoRegularDAO.save(seccionGrupoRegular);
            }

            List<Seccion> seccionesAfectadas = seccionesGpoRegular.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
            List<SeccionGrupoRegular> seccionesGpoRegularBD = seccionGrupoRegularDAO.allByLetraGrupoRegularAndSecciones(letraGrupoRegular, seccionesAfectadas);
            Map<Long, SeccionGrupoRegular> mapSeccionGpoRegularBD = TypesUtil.convertListToMap("seccion.id", seccionesGpoRegularBD);
            for (SeccionGrupoRegular seccionGpoRegular : seccionesGpoRegular) {
                SeccionGrupoRegular seccionGpoRegularBD = mapSeccionGpoRegularBD.get(seccionGpoRegular.getSeccion().getId());
                alumnoGrupoRegularDAO.createForSeccionGrupoRegular(seccionGpoRegular.getAlumnosGruposRegulares(), seccionGpoRegularBD, ds.getFechaAccionAudit(), ds.getUsuario());

                List<HorarioAula> horariosAulaBySeccionGpoRegular = seccionGpoRegular.getHorariosAula();
                for (HorarioAula horarioAula : horariosAulaBySeccionGpoRegular) {
                    horarioAula.setSeccionGrupoRegular(seccionGpoRegularBD);
                    horarioAulaDAO.save(horarioAula);
                }
                if (!seccionGpoRegular.getAula().getOficinaSupervisora().isOficinaOera()) {
                    listSeccionesNoOera.add(seccionGpoRegular.getSeccion());
                }
            }
        }

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

    private List<Seccion> allSeccionesRegulares(CicloAcademico cicloAcademico, TipoGrupoHorasEnum tipoGrupoHorasEnum) {
        List<Seccion> secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, tipoGrupoHorasEnum);
        List<Seccion> seccionesZetas = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ZETA);
        seccionesZetas.removeIf(x -> !x.getGrupoHoras().getConHorario().equals("FLXHOR"));
        secciones.addAll(seccionesZetas);
        //Ordernar por horas semanalaes de mayor a menor
        Collections.sort(secciones, (p1, p2) -> p2.getHorasSemanales().compareTo(p1.getHorasSemanales()));

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

    public List<String> validarCursosMasivos(List<CursoMasivoExamen> cursosMasivosByRolExamenes) {
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

    public void saveSeccionesEspeciales(List<Seccion> seccionesEspeciales, RolExamenes rolExamenes, DataSessionPivot ds) {
        logger.debug("secciones especiales a guardar {}", seccionesEspeciales.size());
        int contSecciones = 0;
        for (Seccion seccion : seccionesEspeciales) {
            SeccionGrupoEspecial seccionGrupoEspecial = new SeccionGrupoEspecial(
                    rolExamenes,
                    seccion, ds.getUsuario(),
                    ds.getFechaAccionAudit()
            );
            List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
            logger.debug(" seccion {}, cant. alumnos {}, numero {}",
                    seccion.getId(),
                    matriculadosPorSeccion.size(),
                    (++contSecciones) + " de " + seccionesEspeciales.size());
            for (MatriculaSeccion matriculaSeccion : matriculadosPorSeccion) {
                AlumnoGrupoEspecial alumnoGrupoEspecial
                        = new AlumnoGrupoEspecial(
                                matriculaSeccion.getMatriculaResumen().getAlumno(),
                                AlumnoRolExamenEstadoEnum.ACT,
                                ds.getUsuario(),
                                ds.getFechaAccionAudit(),
                                seccionGrupoEspecial
                        );
                seccionGrupoEspecial.getAlumnosGrupoEspecial().add(alumnoGrupoEspecial);
            }
            seccionGrupoEspecialDAO.save(seccionGrupoEspecial);
            alumnoGrupoEspecialDAO.createForSeccionGrupoEspecial(seccionGrupoEspecial.getAlumnosGrupoEspecial(), seccionGrupoEspecial, ds.getFechaAccionAudit(), ds.getUsuario());
        }

    }

    public List<String> getDiaHoraList(List<FechaHoraGrupoExamen> fechasHorasGpoExamen) {
        List<String> result = fechasHorasGpoExamen
                .stream().map(x -> x.getDia().getNumeroDia() + "-" + x.getHora().getNumero()).collect(Collectors.toList());
        return result;
    }

    public void calcularGruposEspeciales(String letraEspeciales,
            List<CursoMasivoExamen> cursosMasivosByRolExamenes,
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
                    List<CursoMasivoExamen> cursosMasivosByRolExamenesAndGrupoHoras = cursosMasivosByRolExamenes.stream()
                            .filter(x -> x.getGrupoHorasExamen() != null)
                            .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                            .collect(Collectors.toList());

                    List<SeccionGrupoEspecial> seccionesGrupoEspecialByRolExamenAndGrupoHorasExamen = seccionesGrupoEspecialByRolExamen.stream()
                            .filter(x -> x.getGrupoHorasExamen() != null)
                            .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                            .collect(Collectors.toList());

                    boolean result = false;
                    //    if (seccion.getAula().getOficinaSupervisora().isOficinaOera()) {
                    result = grupoRegularConnector.procesarSeccionesByLetra(letraGrupoRegular,
                            cursosMasivosByRolExamenesAndGrupoHoras, seccionesGrupoEspecialByRolExamenAndGrupoHorasExamen, seccion, seccionesByLetra, ds);
                    /*   } else {
                        GrupoHorasExamen grupoHorasExamen = letraGrupoRegular.getGrupoHorasExamen();
                        Aula seccionAulaOriginal = seccion.getAula();
                        AULA_EACH:
                        for (Aula aula : this.rolExamenesLogger.getAulasOera()) {
                            for (String diaHora : grupoHorasExamen.getDiaHoraList()) {
                                if (aula.getHorariosAula().contains(diaHora)) {
                                    continue AULA_EACH;
                                }
                            }
                            seccion.setAula(aula);
                            result = grupoRegularConnector.procesarSeccionesByLetra(letraGrupoRegular,
                                    cursosMasivosByRolExamenesAndGrupoHoras, seccionesGrupoEspecialByRolExamenAndGrupoHorasExamen, seccion, seccionesByLetra, ds);
                            if (result) {
                                break AULA_EACH;
                            }
                            seccion.setAula(seccionAulaOriginal);
                        }
                    }*/
 /*
                    boolean result = grupoRegularConnector.procesarSeccionesByLetra(
                            letraGrupoRegular,
                            cursosMasivosByRolExamenesAndGrupoHoras,
                            seccionesGrupoEspecialByRolExamenAndGrupoHorasExamen,
                            seccion, seccionesByLetra, ds);*/
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
        List<LetraGrupoRegular> letrasGruposRegulares = new ArrayList<>();
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

    public void crearLetrasGruposRegulares(
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<CursoMasivoExamen> cursosMasivosByRolExamenes,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Map<String, List<Seccion>> seccionesGroupByLetra,
            List<Seccion> seccionesEspecialesRecolected,
            DataSessionPivot ds) {
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            this.rolExamenesLogger.addMessageLevel2("Letra %s ", letraGrupoRegular.getLetra());
            List<CursoMasivoExamen> cursosMasivosByRolExamenesAndGrupoHoras = cursosMasivosByRolExamenes.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                    .collect(Collectors.toList());
            List<SeccionGrupoEspecial> seccionesGrupoEspecialByGrupoHoras = seccionesGrupoEspecial.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                    .collect(Collectors.toList());

            grupoRegularConnector.crearLetraGrupoRegularByLetra(
                    letraGrupoRegular,
                    cursosMasivosByRolExamenesAndGrupoHoras, seccionesGrupoEspecialByGrupoHoras,
                    seccionesGroupByLetra, seccionesEspecialesRecolected, ds);
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
    public List<SeccionGrupoRegular> allSeccionesGrupoRegularDynaByLetraGrupoReg(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular) {
        List<SeccionGrupoRegular> seccionesLetraGrupoRegular = seccionGrupoRegularDAO.allByDynatableAndLetraGrupoRegular(filter, letraGrupoRegular);
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

}
