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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoGrupoHorasEnum;
import pe.edu.lamolina.model.horario.HorarioSeccion;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.DiaHoraGrupoDAO;
import pe.edu.lamolina.pivot.dao.horario.GrupoHorasDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoRegularExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.RolExamenesDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class GrupoRegularServiceImp implements GrupoRegularService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
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
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
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
    
    @Override
    public List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico) {
        return rolExamenesDAO.allActiveByCiclo(cicloAcademico);
    }
    
    @Override
    @Transactional(readOnly = false)
    public void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        //    List<CursoMasivoExamen> allCursoMasivoExamenByGrupoRegular = cursoMasivoExamenDAO.allActiveByRolExamen(grupoRegularExamen.getRolExamen());
        //  List<SeccionCursoMasivo> allSeccionesCursoMasivosActives = seccionCursoMasivoDAO.allActiveByCursosMasivos(allCursoMasivoExamenByGrupoRegular);
        DateTime today = new DateTime(ds.getFechaAccionAudit());
        
        this.deleteGrupoRegular(rolExamenes);
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
        List<LetraGrupoRegular> letrasGruposRegularesOnBD = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        logger.debug("letras grupos regulares en bd {}", letrasGruposRegularesOnBD.size());
        
        List<Seccion> seccionesEspecialesRecolected = new ArrayList<>();
        
        logger.debug("Crear grupos regulares");
        List<Seccion> secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.REGULAR); //grupo horas regulares
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;
    
    @Autowired
        Map<String, List<Seccion>> grupoHorasLetrasRegularesMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        List<String> letras = new ArrayList<>(grupoHorasLetrasRegularesMap.keySet());
        logger.debug("Letras Grupos Regulares {}", String.join(",", letras));

        //creamos las letras regulares
        List<GrupoHorasExamen> gruposHorasExamen = this.allGrupoHorasExamenByRol(rolExamenes);
        List<LetraGrupoRegular> letrasGruposRegulares = this.convertLetraToLetraGpo(letras, rolExamenes, gruposHorasExamen, today, ds.getUsuario());
        
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            grupoRegularConnector.crearLetraGrupoRegularByLetra(letraGrupoRegular, grupoHorasLetrasRegularesMap, seccionesEspecialesRecolected, today, ds.getUsuario());
        }
        
        logger.debug("Grupos Especiales");
        secciones = seccionDAO.allForRolExamenAndTipoGrupoHora(cicloAcademico, TipoGrupoHorasEnum.ESPECIAL);
        
        List<SeccionExcluido> seccionesExcluidasByRolExamen = seccionExcluidoDAO.allByRolExamenes(rolExamenes);
        
        Map<String, List<Seccion>> grupoHorasLetrasEspecialesMap = TypesUtil.convertListToMapList("grupoHoras.letra", secciones);
        //hacemos encajar los grupos especiales en las letras regulares 
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
            grupoRegularConnector.crearLetraGrupoRegularByLetra(letraGrupoRegular, grupoHorasLetrasEspecialesMap, seccionesEspecialesRecolected, today, ds.getUsuario());
        }
        //calculamos el resto de letras
        letras = new ArrayList<>(grupoHorasLetrasEspecialesMap.keySet());
        //quitamos las letras regulares los grupos especiales
        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            letras.removeIf(x -> x.equals(letraGruposRegular.getLetra()));
        }
        
        logger.debug("Letras Grupos Especiales {}", String.join(",", letras));
        for (String letra : letras) {
            this.calcularGruposEspeciales(letra, grupoHorasLetrasEspecialesMap,
                    letrasGruposRegulares, seccionesEspecialesRecolected, ds.getUsuario(), today);
        }
        logger.debug("Secciones sin asignar {}", seccionesEspecialesRecolected.size());
        for (Seccion seccionEach : seccionesEspecialesRecolected) {
            logger.debug("seccionEach {}", seccionEach.getId());
        }
        
        for (SeccionExcluido seccionExcluido : seccionesExcluidasByRolExamen) {
            secciones.removeIf(x -> x.equals(seccionExcluido.getSeccion()));
        }
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
    
        for (SeccionExcluido seccionExcluido : seccionesExcluidasByRolExamen) {
            secciones.removeIf(x -> x.equals(seccionExcluido.getSeccion()));
        }
    public void calcularGruposEspeciales(String letraEspeciales,
            Map<String, List<Seccion>> grupoHorasLetrasEspecialesMap,
            List<LetraGrupoRegular> letrasGruposRegulares,
            List<Seccion> seccionesEspecialesRecolected,
            Usuario usuario,
            DateTime today) {
        
        List<Seccion> seccionesByLetra = grupoHorasLetrasEspecialesMap.get(letraEspeciales);
        List<HorarioSeccion> horarios = horarioSeccionDAO.allBySeccionesSortByDiaHora(seccionesByLetra);
        Map horariosBySeccion = TypesUtil.convertListToMapList("seccion.id", horarios);
        
        for (Seccion seccion : seccionesByLetra) {
            seccion.setHorarioSeccion((List<HorarioSeccion>) horariosBySeccion.get(seccion.getId()));
        this.saveSeccionesEspeciales(seccionesEspecialesRecolected, rolExamenes, ds);
        
            for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegulares) {
                //Arrays.equals((String[]) seccion.getDiaHoraList().toArray(new String[0]), (String[]) letraGrupoRegular.getGrupoHorasExamen().getDiaHoraList().toArray(new String[0]))
                if (seccion.getDiaHoraList().containsAll(letraGrupoRegular.getGrupoHorasExamen().getDiaHoraList())
                        || letraGrupoRegular.getGrupoHorasExamen().getDiaHoraList().containsAll(seccion.getDiaHoraList())) {
                    boolean result = grupoRegularConnector.procesarSeccionesByLetra(letraGrupoRegular, seccion, seccionesByLetra, usuario, today);
                    if (result) {
                        logger.debug("Grupo especial {}, encontro match con {}", letraEspeciales, letraGrupoRegular.getLetra());
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
    
                        break;
                    }
                }
            }
            boolean withMatch = false;
                seccionesEspecialesRecolected.add(seccion);
            }
        }
        
    }
    
    public List<GrupoHorasExamen> allGrupoHorasExamenByRol(
            RolExamenes rolExamenes) {
        List<GrupoHorasExamen> gruposHorasExamen = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        Collections.sort(gruposHorasExamen, (p1, p2) -> p1.getGrupoHoras().getLetra().compareTo(p2.getGrupoHoras().getLetra()));
        for (GrupoHorasExamen grupoHorasExamen : gruposHorasExamen) {
            List<FechaHoraGrupoExamen> fechasHorasGrupo = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(grupoHorasExamen);
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
    public void deleteGrupoRegular(RolExamenes rolExamenes) {
        List<LetraGrupoRegular> letrasGruposRegular = letraGrupoRegularDAO.allByRolExamenes(rolExamenes);
        logger.debug("Letras Grupos Regulares a eliminar {}", letrasGruposRegular.size());
        for (LetraGrupoRegular letraGrupoRegular : letrasGruposRegular) {
                        withMatch = true;
            grupoRegularExamenDAO.deleteByLetraGrupoRegular(letraGrupoRegular);
            letraGrupoRegularDAO.delete(letraGrupoRegular);
        }
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
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, Arrays.asList(SeccionRolExamenEstadoEnum.ACT, SeccionRolExamenEstadoEnum.EXC));
        return secciones;
    }
    
    @Override
    public List<AlumnoGrupoRegular> allAlumnosGrupoRegularByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        List<AlumnoGrupoRegular> alumnos = alumnoGrupoRegularDAO.
                allByLetraGrupoRegularAndEstados(letraGrupoRegular, Arrays.asList(AlumnoRolExamenEstadoEnum.ACT, AlumnoRolExamenEstadoEnum.EXC));
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
        
        DateTime today = new DateTime();
        SeccionGrupoRegular seccionGrupoRegularUpd = new SeccionGrupoRegular();
        seccionGrupoRegularUpd.setId(seccionGrupoRegular.getId());
        seccionGrupoRegularUpd.setUsuarioExclusion(ds.getUsuario());
        seccionGrupoRegularUpd.setFechaExclusion(ds.getFechaAccionAudit());
        seccionGrupoRegularUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.EXC);
        seccionGrupoRegularDAO.updateEstado(seccionGrupoRegularUpd);
        
        SeccionExcluido seccionExcluido = new SeccionExcluido();
        seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
        seccionExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        seccionExcluido.setRolExamenes(seccionGrupoRegular.getLetraGrupoRegular().getRolExamenes());
        seccionExcluido.setSeccion(seccionGrupoRegular.getSeccion());
        seccionExcluido.setUserRegistro(ds.getUsuario());
        seccionExcluidoDAO.save(seccionExcluido);
        
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccionGrupoRegular.getSeccion());
        List<Alumno> alumnos = matriculasSeccion.stream().map(x -> x.getMatriculaResumen().getAlumno()).collect(Collectors.toList());
        alumnoGrupoRegularDAO.updateEstado(alumnos, AlumnoRolExamenEstadoEnum.EXC, ds.getUsuario(), ds.getFechaAccionAudit());
    }
    
    @Override
    @Transactional(readOnly = false)
    public void excluirGrupoRegular(AlumnoGrupoRegular alumnoGrupoRegular, DataSessionPivot ds) {
        alumnoGrupoRegular.setUsuarioExclusion(ds.getUsuario());
        alumnoGrupoRegular.setFechaExclusion(ds.getFechaAccionAudit());
        alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.EXC);
        alumnoGrupoRegularDAO.updateEstado(alumnoGrupoRegular);
    }
    
}
            if (!withMatch) {
