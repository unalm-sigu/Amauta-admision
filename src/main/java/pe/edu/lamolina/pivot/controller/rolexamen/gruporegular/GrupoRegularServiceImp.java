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
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.general.Aula;
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
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
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
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    CursoExcluidoDAO cursoExcluidoDAO;

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
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_MAS);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    @Override
    @Transactional(readOnly = false)
    public void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolBD);
        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(rolExamenes.isSituacionConfigurarCursoMasivo() || rolExamenes.isSituacionConfigurarGrupoRegular(), "Debe configurar los grupos masivos previamente.");

        List<CursoMasivoExamen> cursosMasivosByRolExamenes = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        if (cursosMasivosByRolExamenes.isEmpty()) {
            throw new PhobosException("Debe configurar los cursos masivos.");
        }
        grupoRegularConnector.fillActiveInfoCursosMasivos(cursosMasivosByRolExamenes);
        List<String> cursosMasivosValidations = this.validarCursosMasivos(cursosMasivosByRolExamenes);
        Assert.isTrue(cursosMasivosValidations.isEmpty(), String.join("\n", cursosMasivosValidations));

        List<Seccion> seccionesCursosMasivos = new ArrayList<>();
        for (CursoMasivoExamen cursosMasivosByRolExamene : cursosMasivosByRolExamenes) {
            for (SeccionCursoMasivo seccionesCursosMasivo : cursosMasivosByRolExamene.getSeccionesCursosMasivos()) {
                seccionesCursosMasivos.add(seccionesCursosMasivo.getSeccion());
            }
        }

        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByRolExamenesAndEstados(rolExamenes, SeccionRolExamenEstadoEnum.ACT);
        grupoRegularConnector.fillActiveInfoGrupoEspecial(seccionesGrupoEspecial);

        this.rolExamenesLogger.iniciarGrupoRegular();

        DateTime today = new DateTime(ds.getFechaAccionAudit());

        grupoEspecialService.deleteGrupoEspecial(rolExamenes);
        this.deleteGrupoRegular(rolExamenes);

        List<SeccionExcluido> seccionesExcluidasByRolExamen = seccionExcluidoDAO.allByRolExamenes(rolExamenes);

        List<LetraGrupoRegular> letrasGruposRegularesOnBD = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        logger.debug("letras grupos regulares en bd {}", letrasGruposRegularesOnBD.size());

        List<Seccion> seccionesEspecialesRecolected = new ArrayList<>();

        logger.debug("Crear grupos regulares");
        //secciones de los grupos regulares
        List<Seccion> secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.REGULAR);

        List<Seccion> seccionesExcluidas = seccionesExcluidasByRolExamen.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        seccionesExcluidas.addAll(seccionesCursosMasivos);

        List<CursoExcluido> cursosExcluidoz = cursoExcluidoDAO.allByRolExamenes(rolExamenes, EstadoEnum.ACT);
        List<Curso> cursosExcluidos = cursosExcluidoz.stream().map(x -> x.getCurso()).collect(Collectors.toList());

        this.quitarSeccionesExcluidas(secciones, seccionesExcluidas, cursosExcluidos);

        Map<String, List<Seccion>> grupoHorasLetrasRegularesMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        List<String> letras = new ArrayList<>(grupoHorasLetrasRegularesMap.keySet());
        logger.debug("Letras Grupos Regulares {}", String.join(",", letras));

        //creamos las letras regulares
        List<GrupoHorasExamen> gruposHorasExamen = this.allGrupoHorasExamenByRol(rolExamenes);
        List<LetraGrupoRegular> letrasGruposRegulares = this.convertLetraToLetraGpo(letras, rolExamenes, gruposHorasExamen, today, ds.getUsuario());

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
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
                    grupoHorasLetrasRegularesMap, seccionesEspecialesRecolected, ds);
        }

        logger.debug("Grupos Especiales");
        //secciones grupos especiales
        secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ESPECIAL);
        this.quitarSeccionesExcluidas(secciones, seccionesExcluidas, cursosExcluidos);

        Map<String, List<Seccion>> grupoHorasLetrasEspecialesMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        //hacemos encajar los grupos especiales en las letras regulares 
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            List<CursoMasivoExamen> cursosMasivosByRolExamenesAndGrupoHoras = cursosMasivosByRolExamenes.stream()
                    .filter(x -> x.getGrupoHorasExamen() != null)
                    .filter(x -> x.getGrupoHorasExamen().equals(letraGrupoRegular.getGrupoHorasExamen()))
                    .collect(Collectors.toList());
            grupoRegularConnector.crearLetraGrupoRegularByLetra(
                    letraGrupoRegular,
                    cursosMasivosByRolExamenesAndGrupoHoras,
                    seccionesGrupoEspecial,
                    grupoHorasLetrasEspecialesMap, seccionesEspecialesRecolected, ds);
        }
        //calculamos el resto de letras
        letras = new ArrayList<>(grupoHorasLetrasEspecialesMap.keySet());
        //quitamos las letras regulares los grupos especiales
        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            letras.removeIf(x -> x.equals(letraGruposRegular.getLetra()));
        }

        logger.debug("Letras Grupos Especiales {}", String.join(",", letras));
        for (String letra : letras) {
            this.calcularGruposEspeciales(
                    letra,
                    cursosMasivosByRolExamenes,
                    seccionesGrupoEspecial,
                    grupoHorasLetrasEspecialesMap,
                    letrasGruposRegulares, seccionesEspecialesRecolected, ds);
        }
        logger.debug("Secciones sin asignar {}", seccionesEspecialesRecolected.size());
        for (Seccion seccionEach : seccionesEspecialesRecolected) {
            logger.debug("seccionEach {}", seccionEach.getId());
        }

        this.saveSeccionesEspeciales(seccionesEspecialesRecolected, rolExamenes, ds);

        logger.debug("letras grupos regulares a guardar {}", letrasGruposRegulares.size());
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            logger.debug("guardara la letra {}", letraGrupoRegular.getLetra());
            letraGrupoRegularDAO.save(letraGrupoRegular);
        }

        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_REG);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    public void quitarSeccionesExcluidas(List<Seccion> secciones, List<Seccion> seccionesExcluidas, List<Curso> cursosExcluidos) {
        for (Seccion seccionExcluida : seccionesExcluidas) {
            secciones.removeIf(x -> x.equals(seccionExcluida));
        }
        for (Curso cursosExcluido : cursosExcluidos) {
            secciones.removeIf(x -> x.getGrupoSeccion().getCurso().equals(cursosExcluido));
        }
    }

    public List<String> validarCursosMasivos(List<CursoMasivoExamen> cursosMasivosByRolExamenes) {
        List<String> validations = new ArrayList<>();
        for (CursoMasivoExamen cursosMasivosByRolExamene : cursosMasivosByRolExamenes) {
            List<String> errors = new ArrayList<>();
            /*  if (cursosMasivosByRolExamene.getAulasCursosMasivos() == null || cursosMasivosByRolExamene.getAulasCursosMasivos().isEmpty()) {
                String msg = "Sin aulas asignadas.";
                errors.add(msg);
            }*/
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
            Map<String, List<Seccion>> grupoHorasLetrasEspecialesMap,
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<Seccion> seccionesEspecialesRecolected,
            DataSessionPivot ds) {

        List<Seccion> seccionesByLetra = grupoHorasLetrasEspecialesMap.get(letraEspeciales);
        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesByLetra);
        Map horariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);

        for (Seccion seccion : seccionesByLetra) {
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

                    boolean result = grupoRegularConnector.procesarSeccionesByLetra(
                            letraGrupoRegular,
                            cursosMasivosByRolExamenesAndGrupoHoras,
                            seccionesGrupoEspecialByRolExamenAndGrupoHorasExamen,
                            seccion, seccionesByLetra, ds);
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
            List<FechaHoraGrupoExamen> fechasHorasGrupo = fechasHorasGrupos.stream().filter(x -> x.getGrupoHorasExamen().equals(grupoHorasExamen)).collect(Collectors.toList());
            grupoHorasExamen.setFechasHorasGruposExamen(fechasHorasGrupo);
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
                    = gruposHorasExamenes.stream().filter(ghe -> ghe.getGrupoHoras().getLetra().equals(letra)).findFirst().orElse(null);
            letraGrupoRegular.setGrupoHorasExamen(grupoHorasExamen);
            letrasGruposRegulares.add(letraGrupoRegular);
        });
        return letrasGruposRegulares;
    }

    @Transactional(readOnly = false)
    @Override
    public void deleteGrupoRegular(RolExamenes rolExamenes) {
        RolExamenes rolBD = rolExamenesDAO.find(rolExamenes.getId());
        this.checkNoPublicado(rolBD);

        List<LetraGrupoRegular> letrasGruposRegular = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        logger.debug("Letras Grupos Regulares a eliminar {}", letrasGruposRegular.size());

        List<SeccionGrupoRegular> seccionesGruposRegularesExc = seccionGrupoRegularDAO.allByRolExamenes(rolExamenes, SeccionRolExamenEstadoEnum.EXC);
        List<Seccion> seccionesExcluidas = seccionesGruposRegularesExc.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        if (!seccionesExcluidas.isEmpty()) {
            seccionExcluidoDAO.deleteBySecciones(seccionesExcluidas);
        }
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegular) {
            alumnoGrupoRegularDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
            seccionGrupoRegularDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
            grupoRegularExamenDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
            //    letraGrupoRegularDAO.delete(letraGrupoRegular);
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

    public void crearLetraGrupoRegular(
            List<Seccion> seccionesEspeciales,
            List<LetraGrupoRegular> letrasGruposRegulares,
            Map<String, List<Seccion>> grupoHorasLetraMap,
            DateTime today,
            Usuario usuario) {

        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            //  grupoRegularConnector.crearLetraGrupoRegularByLetra(seccionesEspeciales, letraGrupoRegular, grupoHorasLetraMap, today, usuario);
        }

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

        SeccionGrupoRegular seccionGrupoRegularUpd = new SeccionGrupoRegular(seccionGrupoRegular.getId());
        seccionGrupoRegularUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
        seccionGrupoRegularDAO.updateEstado(seccionGrupoRegularUpd);

        this.activarValidarCruce(seccionGrupoRegular);

        SeccionExcluido seccionExcluido = seccionExcluidoDAO.findBySeccion(seccionGrupoRegular.getSeccion(), EstadoEnum.ACT);
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
        grupoRegularConnector.validarSituacion("incluir", "los grupos regulares", rolExamenes.isSituacionConfigurarGrupoRegular());
        Assert.isTrue(alumnoGrupoRegular.isEstadoExcluido(), "Solo se puede incluir las alumnos regulares excluidos");

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
