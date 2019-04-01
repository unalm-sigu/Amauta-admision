package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
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
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.DocenteRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoHorarioAulaEnum;
import pe.edu.lamolina.model.enums.GrupoHorasRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.DocenteCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.LetraGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.SeccionGrupoRegularDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
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

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void savedLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular) {
        letraGrupoRegularDAO.save(letraGrupoRegular);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void crearLetraGrupoRegularByLetra(
            LetraGrupoRegular letraGrupoRegular,
            List<CursoMasivoExamen> cursosMasivosExamen,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Map<String, List<Seccion>> mapSeccionesGroupByLetra,
            List<Seccion> seccionesEspeciales,
            DataSessionPivot ds) {

        long ini = System.currentTimeMillis();
        List<Seccion> seccionesByLetra = mapSeccionesGroupByLetra.get(letraGrupoRegular.getLetra());
        if (seccionesByLetra == null) {
            return;
        }
        List<DocenteSeccion> docentesPrincipales = docenteSeccionDAO.allPrincipalesBySecciones(seccionesByLetra);
        letraGrupoRegular.setContadorSecciones(BigDecimal.ZERO.intValue());
        /*
        List<Seccion> seccionesOera = seccionesByLetra
                .stream().filter(x -> x.getAula().getOficinaSupervisora().isOficinaOera())
                .collect(Collectors.toList());

        List<Seccion> seccionesOthersOfi = seccionesByLetra
                .stream().filter(x -> !x.getAula().getOficinaSupervisora().isOficinaOera())
                .collect(Collectors.toList());

        seccionesOera.addAll(seccionesOthersOfi);
         */
        for (Seccion seccion : seccionesByLetra) {
            Seccion seccionClone = seccion.clone();
            List<DocenteSeccion> docenteSecciones = docentesPrincipales.stream().filter(x -> x.getSeccion().equals(seccionClone)).collect(Collectors.toList());
            Assert.isFalse(docenteSecciones.isEmpty(), String.format("La sección (%s) de código %s, no tiene docente principal", seccionClone.getId(), seccionClone.getCodigo2()));
            Assert.isTrue(docenteSecciones.size() == 1, String.format("La sección (%s) de código %s, tiene mas de un docente principal", seccionClone.getId(), seccionClone.getCodigo2()));
            seccionClone.setDocenteSeccion(docenteSecciones);

            boolean result = false;
            if (seccionClone.getAula().getOficinaSupervisora().isOficinaOera()) {
                result = this.procesarSeccionesByLetra(letraGrupoRegular, cursosMasivosExamen, seccionesGrupoEspecial, seccionClone, seccionesByLetra, ds);
            } else {
                GrupoHorasExamen grupoHorasExamen = letraGrupoRegular.getGrupoHorasExamen();
                Aula aulaSeccionOriginal = seccionClone.getAula();

                Map<Long, List<Aula>> mapAulasAgrupadasPorModulo = TypesUtil.convertListToMapList("aulaSuperior.id", this.rolExamenesLogger.getAulasOera());
                Map<Long, List<Aula>> mapAulasAgrupadasPorModuloOrdered = new LinkedHashMap();
                mapAulasAgrupadasPorModuloOrdered.put(aulaSeccionOriginal.getAulaSuperior().getId(), mapAulasAgrupadasPorModulo.get(aulaSeccionOriginal.getAulaSuperior().getId()));
                for (Map.Entry<Long, List<Aula>> entry : mapAulasAgrupadasPorModulo.entrySet()) {
                    Long key = entry.getKey();
                    List<Aula> value = entry.getValue();
                    if (key.compareTo(aulaSeccionOriginal.getAulaSuperior().getId()) == 0) {
                        continue;
                    }
                    mapAulasAgrupadasPorModuloOrdered.put(key, value);
                }

                final int AFORO_INCREMENTO = 5;
                int inicio = seccion.getMatriculados();
                int fin = inicio + AFORO_INCREMENTO;
                while (true) {
                    Aula aulaResult = this.buscarAulaOeraBySeccion(seccion,
                            letraGrupoRegular,
                            mapAulasAgrupadasPorModulo,
                            cursosMasivosExamen,
                            seccionesGrupoEspecial,
                            seccionesByLetra,
                            inicio, fin,
                            ds);
                    if (!aulaResult.equals(seccion.getAula())) {
                        break;
                    }
                    if (fin > rolExamenesLogger.getMaximoAforoAula()) {
                        break;
                    }
                }
            }
            if (!result) {
                seccionesEspeciales.add(seccionClone);
            }
        }
        long end = System.currentTimeMillis();

        long milis = end - ini;
        logger.debug("Termino en Segundos {}, MiliSeconds {}", TimeUnit.MILLISECONDS.toSeconds(milis), milis);
    }

    public Aula buscarAulaOeraBySeccion(
            Seccion seccion,
            LetraGrupoRegular letraGrupoRegular,
            Map<Long, List<Aula>> aulasAgrupadasPorModulo,
            List<CursoMasivoExamen> cursosMasivosExamen,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<Seccion> seccionesByLetra,
            Integer inicio,
            Integer fin,
            DataSessionPivot ds) {
        Seccion seccionClone = seccion.clone();
        Aula aulaSeccionOriginal = seccion.getAula();
        GrupoHorasExamen grupoHorasExamen = letraGrupoRegular.getGrupoHorasExamen();

        for (Map.Entry<Long, List<Aula>> entry : aulasAgrupadasPorModulo.entrySet()) {
            List<Aula> aulasByModulo = entry.getValue();
            AULA_EACH:
            for (Aula aula : aulasByModulo) {
                if (!(aula.getAforo() >= inicio && aula.getAforo() < fin)) {
                    continue;
                }
                for (String diaHora : grupoHorasExamen.getDiaHoraList()) {
                    if (aula.getHorariosAula().contains(diaHora)) {
                        continue AULA_EACH;
                    }
                }
                seccionClone.setAula(aula);
                boolean result = this.procesarSeccionesByLetra(letraGrupoRegular, cursosMasivosExamen, seccionesGrupoEspecial, seccionClone, seccionesByLetra, ds);
                if (result) {
                    return aula;
                }
            }
        }
        return aulaSeccionOriginal;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean procesarSeccionesByLetra(
            LetraGrupoRegular letraGrupoRegular,
            List<CursoMasivoExamen> cursosMasivosExamen,
            List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            Seccion seccion,
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
        List<Aula> aulas = Arrays.asList(seccion.getAula());
        List<Docente> docentes = Arrays.asList(seccion.getDocenteSeccion().get(0).getDocente());

        boolean validacionCursosMasivos = this.validarCursosMasivos(cursosMasivosExamen, docentes, aulas, alumnos);
        boolean validacionGrupoRegular = this.validarGrupoRegular(letraGrupoRegular, alumnos, docentes, aulas);
        boolean validacionGrupoEspecial = this.validarGrupoEspecial(seccionesGrupoEspecial, alumnos, docentes, aulas);
        if (!validacionGrupoRegular || !validacionCursosMasivos || !validacionGrupoEspecial) {
            return false;
        }

        this.crearSeccionGrupoRegular(seccion, letraGrupoRegular, matriculadosPorSeccion, ds);
        return true;
    }

    @Override
    public boolean validarGrupoRegular(GrupoHorasExamen grupoHorasExamen,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas) {
        LetraGrupoRegular letraGrupoRegular = letraGrupoRegularDAO.findByGrupoHorasExamen(grupoHorasExamen);
        if (letraGrupoRegular == null) {
            return true;
        }
        this.fillActiveInfoLetrasGruposRegulares(Arrays.asList(letraGrupoRegular));
        return this.validarGrupoRegular(letraGrupoRegular, alumnos, docentes, aulas);
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
                rolExamenesLogger.cruceAula(aula, letraGrupoRegular, seccionGrupoRegularWithAula.getSeccion());
                //  break;
            }
        }

        if (alumnoConflicto || docenteConflicto || aulaConConflicto) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validarGrupoEspecial(GrupoHorasExamen grupoHorasExamen, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        List<SeccionGrupoEspecial> seccionesGrupoEspecial = seccionGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, SeccionRolExamenEstadoEnum.ACT);
        // seccionesGrupoEspecial.removeIf(x -> x.getGrupoHorasExamen() == null || !x.getGrupoHorasExamen().equals(grupoHorasExamen));
        this.fillActiveInfoGrupoEspecial(seccionesGrupoEspecial);
        return this.validarGrupoEspecial(seccionesGrupoEspecial, alumnos, docentes, aulas);
    }

    @Override
    public boolean validarGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial,
            List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas) {
        //validar conflicto alumno
        boolean alumnoConflicto = false;
        //  MATRICULAS_BY_SEC:
        for (Alumno alumno : alumnos) {
            for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecial) {
                AlumnoGrupoEspecial alumnoSeccionEspecialFound = seccionGrupoEspecial.getAlumnosGrupoEspecial()
                        .stream().filter(x -> x.getAlumno().equals(alumno)).findFirst().orElse(null);
                if (alumnoSeccionEspecialFound != null) {
                    alumnoConflicto = true;
                    rolExamenesLogger.cruceAlumno(alumno, seccionGrupoEspecial);
                    // break MATRICULAS_BY_SEC;
                }
            }
        }

        //validar conflicto docentes
        boolean docenteConflicto = false;
        for (Docente docente : docentes) {
            SeccionGrupoEspecial seccionGrupoEspecialWithDocente = seccionesGrupoEspecial.stream()
                    .filter(x -> x.getDocente().equals(docente)).findFirst().orElse(null);
            if (seccionGrupoEspecialWithDocente != null) {
                docenteConflicto = true;
                rolExamenesLogger.cruceDocente(docente, seccionGrupoEspecialWithDocente);
                // break;
            }
        }

        //valida conflicto aula
        boolean aulaConConflicto = false;
        for (Aula aula : aulas) {
            SeccionGrupoEspecial seccionGrupoEspecialWithAula = seccionesGrupoEspecial.stream()
                    .filter(x -> x.getAula().equals(aula)).findFirst().orElse(null);
            if (seccionGrupoEspecialWithAula != null) {
                aulaConConflicto = true;
                rolExamenesLogger.cruceAula(aula, seccionGrupoEspecialWithAula);
                //  break;
            }
        }

        if (docenteConflicto || aulaConConflicto || alumnoConflicto) {
            return false;
        }
        return true;
    }

    @Override
    public void fillActiveInfoCursosMasivos(List<CursoMasivoExamen> cursosMasivoExamen) {
        List<AulaCursoMasivo> aulasCursosMasivos = aulaCursoMasivoDAO.allByCursosMasivos(cursosMasivoExamen);
        List<DocenteCursoMasivo> docentesCursoMasivo = docenteCursoMasivoDAO.allByCursosMasivos(cursosMasivoExamen, DocenteRolExamenEstadoEnum.ACT);
        List<AlumnoCursoMasivo> alumnosCursosMasivos = alumnoCursoMasivoDAO.allByCursosMasivos(cursosMasivoExamen, AlumnoRolExamenEstadoEnum.ACT);

        Map<Long, List<AulaCursoMasivo>> mapAulaCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", aulasCursosMasivos);
        Map<Long, List<DocenteCursoMasivo>> mapDocenteCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", docentesCursoMasivo);
        Map<Long, List<AlumnoCursoMasivo>> mapAlumnosCursoMasivoByCursoMasivo = TypesUtil.convertListToMapList("cursoMasivoExamen.id", alumnosCursosMasivos);

        for (CursoMasivoExamen cursoMasivoByRolExamen : cursosMasivoExamen) {
            cursoMasivoByRolExamen.setAulasCursosMasivos(mapAulaCursoMasivoByCursoMasivo.get(cursoMasivoByRolExamen.getId()));
            cursoMasivoByRolExamen.setDocentesCursosMasivos(mapDocenteCursoMasivoByCursoMasivo.get(cursoMasivoByRolExamen.getId()));
            cursoMasivoByRolExamen.setAlumnosCursosMasivos(mapAlumnosCursoMasivoByCursoMasivo.get(cursoMasivoByRolExamen.getId()));
        }
    }

    @Override
    public void fillActiveInfoGrupoEspecial(List<SeccionGrupoEspecial> seccionesGrupoEspecial) {
        List<AlumnoGrupoEspecial> alumnosGruposEspeciales = alumnoGrupoEspecialDAO.allBySeccionGrupoEspecialAndEstados(seccionesGrupoEspecial, AlumnoRolExamenEstadoEnum.ACT);
        Map<Long, List<AlumnoGrupoEspecial>> mapAlumnosGruposEspecialesBySecGpoEspecial = TypesUtil.convertListToMapList("seccionGrupoEspecial.id", alumnosGruposEspeciales);
        for (SeccionGrupoEspecial seccionGrupoEspecial : seccionesGrupoEspecial) {
            List<AlumnoGrupoEspecial> alumnosGrupoEspecial = mapAlumnosGruposEspecialesBySecGpoEspecial.get(seccionGrupoEspecial.getId());
            seccionGrupoEspecial.setAlumnosGrupoEspecial(alumnosGrupoEspecial);
        }
    }

    @Override
    public void fillActiveInfoLetrasGruposRegulares(List<LetraGrupoRegular> letrasGruposRegulares) {
        //  List<FechaHoraGrupoExamen> fechasHorasExamens = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(gruposHorasExamen);
        List<SeccionGrupoRegular> seccionesGrupoRegular = seccionGrupoRegularDAO.allByLetraGrupoRegularAndEstados(letrasGruposRegulares, SeccionRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoRegular> alumnosGrupoRegular = alumnoGrupoRegularDAO.allBySeccionGrupoRegularAndEstados(seccionesGrupoRegular, AlumnoRolExamenEstadoEnum.ACT);

        Map<Long, List<SeccionGrupoRegular>> mapSeccionesGpoRegular = TypesUtil.convertListToMapList("letraGrupoRegular.id", seccionesGrupoRegular);
        Map<Long, List<AlumnoGrupoRegular>> mapAlumnosGrupoRegular = TypesUtil.convertListToMapList("seccionGrupoRegular.id", alumnosGrupoRegular);
        //    Map< Long, List<FechaHoraGrupoExamen>> mapFechasHorasExamenes = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHorasExamens);

        for (LetraGrupoRegular letraGruposRegular : letrasGruposRegulares) {
            //  List<FechaHoraGrupoExamen> fechasHorasGruposByGrupoHoraExamen = mapFechasHorasExamenes.get(letraGruposRegular.getGrupoHorasExamen().getId());
            //    letraGruposRegular.getGrupoHorasExamen().setFechasHorasGruposExamen(fechasHorasGruposByGrupoHoraExamen);
            //    letraGruposRegular.getGrupoHorasExamen().setSemanaExamen(fechasHorasGruposByGrupoHoraExamen.get(0).getSemanaExamen());

            List<SeccionGrupoRegular> seccionGrupoRegularByLetra = mapSeccionesGpoRegular.get(letraGruposRegular.getId());
            for (SeccionGrupoRegular seccionGrupoRegular : seccionGrupoRegularByLetra) {
                List<AlumnoGrupoRegular> alumnosGrupoRegularBySeccionGpoReg = mapAlumnosGrupoRegular.get(seccionGrupoRegular.getId());
                seccionGrupoRegular.setAlumnosGruposRegulares(alumnosGrupoRegularBySeccionGpoReg);
            }
            letraGruposRegular.setSeccionesGruposRegulares(seccionesGrupoRegular);
        }
    }

    @Override
    public boolean validarCursosMasivos(GrupoHorasExamen grupoHorasExamen, List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        List<CursoMasivoExamen> cursosMasivosByRolExamen = cursoMasivoExamenDAO.allByGrupoHorasExamen(grupoHorasExamen, EstadoCursoMasivoEnum.ACT);
        this.fillActiveInfoCursosMasivos(cursosMasivosByRolExamen);
        return validarCursosMasivos(cursosMasivosByRolExamen, docentes, aulas, alumnos);
    }

    @Override
    public boolean validarCursosMasivos(RolExamenes rolExamenes,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        List<CursoMasivoExamen> cursosMasivosByRolExamen = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
        this.fillActiveInfoCursosMasivos(cursosMasivosByRolExamen);
        return validarCursosMasivos(cursosMasivosByRolExamen, docentes, aulas, alumnos);
    }

    @Override
    public boolean validarCursosMasivos(List<CursoMasivoExamen> cursosMasivosByRolExamen,
            List<Docente> docentes, List<Aula> aulas, List<Alumno> alumnos) {
        ////,GrupoHorasExamen grupoHorasExamen
        if (cursosMasivosByRolExamen.isEmpty()) {
            return true;
        }

        boolean docenteConflicto = false;
        boolean aulaConConflicto = false;
        boolean alumnoConflicto = false;
        for (CursoMasivoExamen cursoMasivoByRolExamen : cursosMasivosByRolExamen) {

            //validar conflicto docentes
            for (Docente docente : docentes) {
                DocenteCursoMasivo docenteCursoMasivo = cursoMasivoByRolExamen.getDocentesCursosMasivos().stream()
                        .filter(x -> x.getDocente().equals(docente))
                        .findFirst().orElse(null);
                if (docenteCursoMasivo != null) {
                    docenteConflicto = true;
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
        SeccionGrupoRegular seccionGrupoRegular = this.crearObjectSeccionGrupoRegular(seccion, letraGrupoRegular, ds);
        Aula aulaSeccionLogger = this.rolExamenesLogger.getAulasOera()
                .stream().filter(x -> x.equals(seccion.getAula())).findFirst().orElse(null);

        for (FechaHoraGrupoExamen fechaHoraGrupoExamen : letraGrupoRegular.getGrupoHorasExamen().getFechasHorasGruposExamen()) {
            HorarioAula horarioAula = new HorarioAula(fechaHoraGrupoExamen, seccion);
            horarioAula.setSeccionGrupoRegular(seccionGrupoRegular);
            //  horarioAulaDAO.save(horarioAula);
            aulaSeccionLogger.getHorariosAula().add(horarioAula.clone());
            if (seccionGrupoRegular == null) {
                seccionGrupoRegular.setHorariosAula(new ArrayList<>());
            }
            seccionGrupoRegular.getHorariosAula().add(horarioAula);
        }
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
            AlumnoGrupoRegular alumnoGrupoRegular = this.crearObjectAlumnoGrupoRegular(x.getMatriculaResumen().getAlumno(), seccionGrupoRegular, ds);
            seccionGrupoRegular.getAlumnosGruposRegulares().add(alumnoGrupoRegular);
        });
    }

    @Override
    public SeccionGrupoRegular crearObjectSeccionGrupoRegular(Seccion seccion, LetraGrupoRegular letraGrupoRegular, DataSessionPivot ds) {
        SeccionGrupoRegular seccionGrupoRegular = new SeccionGrupoRegular();
        seccionGrupoRegular.setSeccion(seccion);
        seccionGrupoRegular.setDocente(seccion.getDocenteSeccion().get(0).getDocente());
        seccionGrupoRegular.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
        seccionGrupoRegular.setFechaRegistro(ds.getFechaAccionAudit());
        seccionGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
        seccionGrupoRegular.setUserRegistro(ds.getUsuario());
        seccionGrupoRegular.setAlumnosGruposRegulares(new ArrayList<>());
        seccionGrupoRegular.setAula(seccion.getAula());
        return seccionGrupoRegular;
    }

    @Override
    public AlumnoGrupoRegular crearObjectAlumnoGrupoRegular(Alumno alumno, SeccionGrupoRegular seccionGrupoRegular, DataSessionPivot ds) {
        AlumnoGrupoRegular alumnoGrupoRegular = new AlumnoGrupoRegular();
        alumnoGrupoRegular.setAlumno(alumno);
        alumnoGrupoRegular.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
        alumnoGrupoRegular.setFechaRegistro(ds.getFechaAccionAudit());
        alumnoGrupoRegular.setSeccionGrupoRegular(seccionGrupoRegular);
        //   alumnoGrupoRegular.setLetraGrupoRegular(letraGrupoRegular);
        alumnoGrupoRegular.setUserRegistro(ds.getUsuario());
        return alumnoGrupoRegular;
    }

    @Override
    public void validarSituacionBeforeOr(String accion, String situacion, Boolean... or) {
        String msg = String.format("Solo se puede %s antes de generar %s", accion, situacion);
        Assert.isTrue(Arrays.asList(or).contains(true), msg);
    }

    @Override
    public void validarSituacion(String accion, String situacion, Boolean... or) {
        String msg = String.format("Solo se puede %s al configurar %s", accion, situacion);
        Assert.isTrue(Arrays.asList(or).contains(true), msg);
    }

    @Override
    public RolExamenesLogger validacionActivarDocente(GrupoHorasExamen grupoHorasExamen, Docente docente) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.iniciarGeneric();

        List<DocenteCursoMasivo> docentesCursosMasivos = docenteCursoMasivoDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, DocenteRolExamenEstadoEnum.ACT);
        List<SeccionGrupoRegular> seccionGrupoRegulares = seccionGrupoRegularDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, SeccionRolExamenEstadoEnum.ACT);
        List<SeccionGrupoEspecial> seccionGrupoEspeciales = seccionGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, SeccionRolExamenEstadoEnum.ACT);

        List<DocenteCursoMasivo> docentesCursosMasivosFound = docentesCursosMasivos.stream()
                .filter(x -> x.getDocente().equals(docente))
                .collect(Collectors.toList());

        List<SeccionGrupoRegular> seccionGrupoRegularesFound = seccionGrupoRegulares.stream()
                .filter(x -> x.getDocente().equals(docente))
                .collect(Collectors.toList());

        List<SeccionGrupoEspecial> seccionGrupoEspecial = seccionGrupoEspeciales.stream()
                .filter(x -> x.getDocente().equals(docente))
                .collect(Collectors.toList());

        for (DocenteCursoMasivo docenteCursoMasivoEach : docentesCursosMasivosFound) {
            rolExamenesLogger.cruceDocente(docente, docenteCursoMasivoEach.getCursoMasivoExamen().getCurso());
        }
        for (SeccionGrupoRegular seccionGrupoRegularEach : seccionGrupoRegularesFound) {
            rolExamenesLogger.cruceDocente(docente, seccionGrupoRegularEach.getLetraGrupoRegular(), seccionGrupoRegularEach.getSeccion());
        }
        for (SeccionGrupoEspecial seccionGrupoEspecialEach : seccionGrupoEspecial) {
            rolExamenesLogger.cruceDocente(docente, seccionGrupoEspecialEach);
        }
        return rolExamenesLogger;
    }

    @Override
    public RolExamenesLogger validacionActivarAlumno(GrupoHorasExamen grupoHorasExamen, Alumno alumno) {
        RolExamenesLogger rolExamenesLogger = new RolExamenesLogger();
        rolExamenesLogger.iniciarGeneric();

        List<AlumnoCursoMasivo> alumnosCursosMasivos = alumnoCursoMasivoDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoRegular> alumnosGrupoRegular = alumnoGrupoRegularDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoEspecial> alumnosGrupoEspecial = alumnoGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(grupoHorasExamen, AlumnoRolExamenEstadoEnum.ACT);

        List<AlumnoCursoMasivo> alumnosCursosMasivosConflicts = alumnosCursosMasivos.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());
        List<AlumnoGrupoRegular> alumnosGrupoRegularConflicts = alumnosGrupoRegular.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());
        List<AlumnoGrupoEspecial> alumnosSeccionEspecialConflics = alumnosGrupoEspecial.stream()
                .filter(x -> x.getAlumno().equals(alumno))
                .collect(Collectors.toList());

        for (AlumnoCursoMasivo aCursoMasivo : alumnosCursosMasivosConflicts) {
            rolExamenesLogger.cruceAlumno(alumno, aCursoMasivo.getCursoMasivoExamen().getCurso());
        }

        for (AlumnoGrupoRegular aGrupoRegular : alumnosGrupoRegularConflicts) {
            rolExamenesLogger.cruceAlumno(
                    alumno,
                    aGrupoRegular.getSeccionGrupoRegular().getLetraGrupoRegular(),
                    aGrupoRegular.getSeccionGrupoRegular().getSeccion());
        }
        for (AlumnoGrupoEspecial aGrupoEspecial : alumnosSeccionEspecialConflics) {
            rolExamenesLogger.cruceAlumno(alumno, aGrupoEspecial.getSeccionGrupoEspecial());
        }

        return rolExamenesLogger;
    }

}
