package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.DocenteRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.DocenteCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
public class GrupoRegularConnectorImp implements GrupoRegularConnector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    AulaCursoMasivoDAO aulaCursoMasivoDAO;

    @Autowired
    DocenteCursoMasivoDAO docenteCursoMasivoDAO;

    @Autowired
    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savedLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        letraGrupoRegularDAO.save(letraGrupoRegular);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void crearLetraGrupoRegularByLetra(
            LetraGrupoRegular letraGrupoRegular,
            Map<String, List<Seccion>> grupoHorasLetraMap,
            List<Seccion> seccionesEspeciales,
            DataSessionPivot ds) {

        long ini = System.currentTimeMillis();
        List<Seccion> seccionesByLetra = grupoHorasLetraMap.get(letraGrupoRegular.getLetra());
        if (seccionesByLetra == null) {
            return;
        }
        List<DocenteSeccion> docentesPrincipales = docenteSeccionDAO.allPrincipalesBySecciones(seccionesByLetra);

        letraGrupoRegular.setContadorSecciones(BigDecimal.ZERO.intValue());

        for (Seccion seccion : seccionesByLetra) {
            Seccion seccionClone = seccion.clone();
            List<DocenteSeccion> docenteSecciones = docentesPrincipales.stream().filter(x -> x.getSeccion().equals(seccionClone)).collect(Collectors.toList());
            Assert.isFalse(docenteSecciones.isEmpty(), String.format("La sección (%s) de código %s, no tiene docente principal", seccionClone.getId(), seccionClone.getCodigo2()));
            Assert.isTrue(docenteSecciones.size() == 1, String.format("La sección (%s) de código %s, tiene mas de un docente principal", seccionClone.getId(), seccionClone.getCodigo2()));
            seccionClone.setDocenteSeccion(docenteSecciones);

            boolean result = this.procesarSeccionesByLetra(letraGrupoRegular, seccionClone, seccionesByLetra, ds);
            if (!result) {
                seccionesEspeciales.add(seccionClone);
            }
        }
        long end = System.currentTimeMillis();

        long milis = end - ini;
        logger.debug("Termino en Segundos {}, MiliSeconds {}", TimeUnit.MILLISECONDS.toSeconds(milis), milis);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean procesarSeccionesByLetra(
            LetraGrupoRegular letraGrupoRegular, Seccion seccion,
            List<Seccion> seccionesByLetraOnlyInformative,
            DataSessionPivot ds) {
        letraGrupoRegular.setContadorSecciones(letraGrupoRegular.getContadorSecciones() + 1);

        List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);

        logger.debug("Letra {}, seccion {}, cant. alumnos {}, numero {}",
                letraGrupoRegular.getLetra(),
                seccion.getId(),
                matriculadosPorSeccion.size(),
                letraGrupoRegular.getContadorSecciones() + " de " + seccionesByLetraOnlyInformative.size());

        List<Alumno> alumnos = matriculadosPorSeccion.stream().map(x -> x.getMatriculaResumen().getAlumno()).collect(Collectors.toList());
        List<Aula> aulas = new ArrayList<>();
        aulas.add(seccion.getAula());
        List<Docente> docentes = new ArrayList<>();
        docentes.add(seccion.getDocenteSeccion().get(0).getDocente());

        boolean validacionCursosMasivos = this.validarCursosMasivos(letraGrupoRegular.getRolExamenes(), docentes, aulas, alumnos, letraGrupoRegular.getGrupoHorasExamen());
        boolean validacionGrupoRegular = this.validarGrupoRegular(letraGrupoRegular, alumnos, docentes, aulas);
        //especiales
        if (!validacionGrupoRegular || !validacionCursosMasivos) {
            return false;
        }

        this.crearSeccionGrupoRegular(seccion, letraGrupoRegular, matriculadosPorSeccion, ds);
        return true;
    }

    @Override
    public boolean validarGrupoRegular(LetraGrupoRegular letraGrupoRegular,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas) {
        List<SeccionGrupoRegular> seccionesGruposRegularesByLetra = letraGrupoRegular.getSeccionesGruposRegulares();

        //validar conflicto alumno
        boolean alumnoConflicto = false;
        //  MATRICULAS_BY_SEC:
        for (Alumno alumno : alumnos) {
            for (SeccionGrupoRegular seccionGrupoRegular : seccionesGruposRegularesByLetra) {
                AlumnoGrupoRegular alumnoSeccionRegularFound = seccionGrupoRegular.getAlumnosGruposRegulares()
                        .stream().filter(x -> x.getAlumno().equals(alumno)).findFirst().orElse(null);
                if (alumnoSeccionRegularFound != null) {
                    alumnoConflicto = true;
                    logger.debug("conflicto alumno {}, con el grupo letra {} y la seccion {}",
                            alumnoSeccionRegularFound.getAlumno().getId(),
                            letraGrupoRegular.getId(),
                            seccionGrupoRegular.getSeccion().getId());
                    rolExamenesLogger.cruceAlumno(alumno, letraGrupoRegular, seccionGrupoRegular.getSeccion());
                    // break MATRICULAS_BY_SEC;
                }
            }
        }

        //validar conflicto docentes
        boolean docenteConflicto = false;
        for (Docente docente : docentes) {
            SeccionGrupoRegular seccionGrupoRegularWithDocente = seccionesGruposRegularesByLetra.stream()
                    .filter(x -> x.getDocente().equals(docente)).findFirst().orElse(null);
            if (seccionGrupoRegularWithDocente != null) {
                docenteConflicto = true;
                logger.debug("conflicto docente {}, con el gruporegular de seccion {}",
                        docente.getId(),
                        seccionGrupoRegularWithDocente.getSeccion().getId());
                rolExamenesLogger.cruceDocente(docente, letraGrupoRegular, seccionGrupoRegularWithDocente.getSeccion());
                // break;
            }
        }

        //valida conflicto aula
        boolean aulaConConflicto = false;
        for (Aula aula : aulas) {
            SeccionGrupoRegular seccionGrupoRegularWithAula = seccionesGruposRegularesByLetra.stream()
                    .filter(x -> x.getAula().equals(aula)).findFirst().orElse(null);
            if (seccionGrupoRegularWithAula != null) {
                aulaConConflicto = true;
                logger.debug("Conflicto Aula {}, con el gruporegular seccion {}",
                        aula.getId(),
                        seccionGrupoRegularWithAula.getSeccion().getId());
                rolExamenesLogger.cruceAula(aula, letraGrupoRegular, seccionGrupoRegularWithAula.getSeccion());
                //  break;
            }
        }

        if (alumnoConflicto || docenteConflicto || aulaConConflicto) {
            return false;
        }
        return true;
    }

    public boolean validarCursosMasivos(RolExamenes rolExamenes, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos, GrupoHorasExamen grupoHorasExamen) {
        List<CursoMasivoExamen> cursosMasivosByRolExamen = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        cursosMasivosByRolExamen.removeIf(x -> x.getGrupoHorasExamen() == null || !x.getGrupoHorasExamen().equals(grupoHorasExamen));
        return validarCursosMasivos(rolExamenes, cursosMasivosByRolExamen, docentes, aulas, alumnos, grupoHorasExamen);
    }

    @Override
    public boolean validarCursosMasivos(RolExamenes rolExamenes,
            List<CursoMasivoExamen> cursosMasivosByRolExamen,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos,
            GrupoHorasExamen grupoHorasExamen) {
        if (cursosMasivosByRolExamen.isEmpty()) {
            return true;
        }
        List<AulaCursoMasivo> aulasCursosMasivos = aulaCursoMasivoDAO.allByCursosMasivos(cursosMasivosByRolExamen);
        List<DocenteCursoMasivo> docentesCursoMasivo = docenteCursoMasivoDAO.allByCursosMasivos(cursosMasivosByRolExamen, DocenteRolExamenEstadoEnum.ACT);
        List<AlumnoCursoMasivo> alumnosCursosMasivos = alumnoCursoMasivoDAO.allByCursosMasivos(cursosMasivosByRolExamen, AlumnoRolExamenEstadoEnum.ACT);

        Map<Long, List<AulaCursoMasivo>> mapAulaCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", aulasCursosMasivos);
        Map<Long, List<DocenteCursoMasivo>> mapDocenteCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", docentesCursoMasivo);
        Map<Long, List<AlumnoCursoMasivo>> mapAlumnosCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", alumnosCursosMasivos);

        boolean docenteConflicto = false;
        boolean aulaConConflicto = false;
        boolean alumnoConflicto = false;
        for (CursoMasivoExamen cursoMasivoByRolExamen : cursosMasivosByRolExamen) {
            //  cursoMasivoByRolExamen = cursoMasivoByRolExamen.clone();
            cursoMasivoByRolExamen.setAulasCursosMasivos(mapAulaCursoMasivoByCursoMasivo.get(cursoMasivoByRolExamen.getId()));
            cursoMasivoByRolExamen.setDocentesCursosMasivos(mapDocenteCursoMasivoByCursoMasivo.get(cursoMasivoByRolExamen.getId()));
            cursoMasivoByRolExamen.setAlumnosCursosMasivos(mapAlumnosCursoMasivoByCursoMasivo.get(cursoMasivoByRolExamen.getId()));

            //validar conflicto docentes
            for (Docente docente : docentes) {
                DocenteCursoMasivo docenteCursoMasivo = cursoMasivoByRolExamen.getDocentesCursosMasivos().stream()
                        .filter(x -> x.getDocente().equals(docente))
                        .findFirst().orElse(null);
                if (docenteCursoMasivo != null) {
                    docenteConflicto = true;
                    logger.debug("conflicto docente {} , con el curso masivo  {}",
                            docente.getId(),
                            cursoMasivoByRolExamen.getId());
                    rolExamenesLogger.cruceDocente(docente, cursoMasivoByRolExamen.getCurso());
                    //  break;
                }
            }

            //Validar Aula
            if (cursoMasivoByRolExamen.getAulasCursosMasivos() != null && !cursoMasivoByRolExamen.getAulasCursosMasivos().isEmpty()) {
                for (Aula aula : aulas) {
                    AulaCursoMasivo aulaCursoMasivo = cursoMasivoByRolExamen.getAulasCursosMasivos().stream().
                            filter(x -> x.getAula().equals(aula)).findFirst().orElse(null);
                    if (aulaCursoMasivo != null) {
                        aulaConConflicto = true;
                        logger.debug("Conflicto Aula {} con el curso masivo {}",
                                aula.getId(),
                                cursoMasivoByRolExamen.getId());
                        rolExamenesLogger.cruceAula(aula, cursoMasivoByRolExamen.getCurso());
                        // break;
                    }
                }
            }

            for (Alumno alumno : alumnos) {
                AlumnoCursoMasivo alumnoCursoMasivo = cursoMasivoByRolExamen.getAlumnosCursosMasivos().stream()
                        .filter(x -> x.getAlumno().equals(alumno))
                        .findFirst().orElse(null);
                if (alumnoCursoMasivo != null) {
                    alumnoConflicto = true;
                    logger.debug("conflicto alumno {}, con el curso masivo {}",
                            alumno.getId(),
                            cursoMasivoByRolExamen.getId());
                    rolExamenesLogger.cruceAlumno(alumno, cursoMasivoByRolExamen.getCurso());
                    //  break;
                }
            }

        }
        if (docenteConflicto || aulaConConflicto || alumnoConflicto) {
            return false;
        }
        return true;
    }

    public void crearSeccionGrupoRegular(Seccion seccion,
            LetraGrupoRegular letraGrupoRegular,
            List<MatriculaSeccion> matriculadosPorSeccion,
            DataSessionPivot ds) {
        SeccionGrupoRegular seccionGrupoRegular = new SeccionGrupoRegular();
        seccionGrupoRegular.setSeccion(seccion);
        seccionGrupoRegular.setDocente(seccion.getDocenteSeccion().get(0).getDocente());
        seccionGrupoRegular.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
        seccionGrupoRegular.setFechaRegistro(ds.getFechaAccionAudit());
        seccionGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
        seccionGrupoRegular.setUserRegistro(ds.getUsuario());
        seccionGrupoRegular.setAlumnosGruposRegulares(new ArrayList<>());
        seccionGrupoRegular.setAula(seccion.getAula());
        letraGrupoRegular.getSeccionesGruposRegulares().add(seccionGrupoRegular);

        GrupoRegularExamen grupoRegularExamen = letraGrupoRegular.getGruposRegularesExamenes()
                .stream().filter(x -> x.getGrupoHoras().equals(seccion.getGrupoHoras()))
                .findFirst().orElse(null);

        if (grupoRegularExamen == null) {
            grupoRegularExamen = new GrupoRegularExamen();
            grupoRegularExamen.setEstadoEnum(GrupoHorasRolExamenEstadoEnum.ACT);
            grupoRegularExamen.setFechaRegistro(ds.getFechaAccionAudit());
            grupoRegularExamen.setGrupoHoras(seccion.getGrupoHoras());
            grupoRegularExamen.setLetraGrupoRegular(letraGrupoRegular);
            grupoRegularExamen.setUserRegistro(ds.getUsuario());
            letraGrupoRegular.getGruposRegularesExamenes().add(grupoRegularExamen);
        }

        matriculadosPorSeccion.forEach(x -> {
            AlumnoGrupoRegular alumnoGrupoRegular = new AlumnoGrupoRegular();
            alumnoGrupoRegular.setAlumno(x.getMatriculaResumen().getAlumno());
            alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
            alumnoGrupoRegular.setFechaRegistro(ds.getFechaAccionAudit());
            alumnoGrupoRegular.setSeccionGrupoRegular(seccionGrupoRegular);
            //   alumnoGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
            alumnoGrupoRegular.setUserRegistro(ds.getUsuario());

            //  letraGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
            seccionGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
        });
    }

}
