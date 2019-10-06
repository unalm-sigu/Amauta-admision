package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import pe.albatross.zelpers.miscelanea.ListsInspector;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
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
import pe.edu.lamolina.model.enums.DocenteRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoCursoMasivoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import static pe.edu.lamolina.model.enums.OficinaEnum.OERA;
import pe.edu.lamolina.model.enums.RolExamenesEstadoEnum;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.enums.TipoHorarioAulaEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.horario.Hora;
import pe.edu.lamolina.model.horario.HorarioAula;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoExcluido;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoEspecial;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.rolexamen.SemanaExamen;
import pe.edu.lamolina.pivot.controller.rolexamen.grupoespecial.GrupoEspecialService;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularConnector;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularService;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.horario.HorarioAulaDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoExcluidoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.CursoMasivoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.DocenteCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.FechaHoraGrupoExamenDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.GrupoHorasExamenDAO;
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
public class CursoMasivosServiceImp implements CursoMasivosService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoRegularService grupoRegularService;

    @Autowired
    GrupoEspecialService grupoEspecialService;

    @Autowired
    CursoMasivoExamenDAO cursoMasivoExamenDAO;

    @Autowired
    RolExamenesDAO rolExamenesDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    SeccionCursoMasivoDAO seccionCursoMasivoDAO;

    @Autowired
    AulaCursoMasivoDAO aulaCursoMasivoDAO;

    @Autowired
    AlumnoCursoMasivoDAO alumnoCursoMasivoDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    AulaDAO aulaDAO;

    @Autowired
    SeccionExcluidoDAO seccionExcluidoDAO;

    @Autowired
    GrupoHorasExamenDAO grupoHorasExamenDAO;

    @Autowired
    FechaHoraGrupoExamenDAO fechaHoraGrupoExamenDAO;

    @Autowired
    LetraGrupoRegularDAO letraGrupoRegularDAO;

    @Autowired
    SeccionGrupoRegularDAO seccionGrupoRegularDAO;

    @Autowired
    AlumnoGrupoRegularDAO alumnoGrupoRegularDAO;

    @Autowired
    SeccionGrupoEspecialDAO seccionGrupoEspecialDAO;

    @Autowired
    AlumnoGrupoEspecialDAO alumnoGrupoEspecialDAO;

    @Autowired
    DocenteCursoMasivoDAO docenteCursoMasivoDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    GrupoRegularConnector grupoRegularConnector;

    @Autowired
    RolExamenesLogger rolExamenesLogger;

    @Autowired
    SemanaExamenDAO semanaExamenDAO;

    @Autowired
    CursoExcluidoDAO cursoExcluidoDAO;

    @Autowired
    HorarioAulaDAO horarioAulaDAO;

    private void checkNoPublicado(RolExamenes rol) {
        Assert.isTrue(rol.getEstadoEnum() != RolExamenesEstadoEnum.PUB, "El rol de exámenes ya ha sido publicado");
    }

    @Override
    public List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allRolExamenesByCicloActivo(cicloAcademico);
    }

    @Override
    public RolExamenes findRolExamenes(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<SemanaExamen> semanasExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        rolExamenes.setSemanasExamen(semanasExamen);
        return rolExamenes;
    }

    @Override
    @Transactional
    public void save(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        RolExamenes rolExamenes = rolExamenesDAO.find(cursoMasivosExamen.getRolExamenes().getId());
        Assert.isTrue(rolExamenes.isEstadoConfigurando() && (rolExamenes.isSituacionHorarioConfirmado() || rolExamenes.isSituacionConfigurarCursoMasivo()), "No puede agregar cursos masivos en este momento");

        List<String> validationsHorariosExamen = this.validarHorariosExamen(rolExamenes);
        Assert.isTrue(validationsHorariosExamen.isEmpty(), String.join("\n", validationsHorariosExamen));

        CursoExcluido cursoExcluido = cursoExcluidoDAO.findActiveByCursoAndRolExamenes(cursoMasivosExamen.getCurso(), rolExamenes);
        Assert.isNull(cursoExcluido, "Este curso está excluido del rol de examen.");

        List<Seccion> secciones = seccionDAO.allByCicloAndCurso(cicloAcademico, cursoMasivosExamen.getCurso());
        List<MatriculaSeccion> matriculadosSecciones = matriculaSeccionDAO.allMatriculadosBySecciones(secciones);
        Map<Long, List<MatriculaSeccion>> mapMatriculadoSeccion = TypesUtil.convertListToMapList("seccion.id", matriculadosSecciones);

        cursoMasivosExamen.setUserRegistro(ds.getUsuario());
        cursoMasivosExamen.setFechaRegistro(new Date());
        cursoMasivosExamen.setEstadoEnum(EstadoCursoMasivoEnum.ACT);
        cursoMasivosExamen.setAulas(0);
        cursoMasivosExamen.setCapacidadAulas(0);
        cursoMasivosExamen.setAlumnos(matriculadosSecciones.size());
        cursoMasivosExamen.setSecciones(secciones.size());
        cursoMasivoExamenDAO.save(cursoMasivosExamen);

//        int alus = 0;
        List<DocenteSeccion> docentesPrincipales = docenteSeccionDAO.allPrincipalesBySecciones(secciones);
        List<DocenteCursoMasivo> docentesCursoMasivo = new ArrayList<>();

        for (Seccion seccion : secciones) {
            List<DocenteSeccion> docenteSecciones = docentesPrincipales.stream().filter(x -> x.getSeccion().equals(seccion)).collect(Collectors.toList());
            Assert.isFalse(docenteSecciones.isEmpty(), String.format("La sección (%s) de código %s, no tiene docente principal", seccion.getId(), seccion.getCodigo2()));
            Assert.isTrue(docenteSecciones.size() == 1, String.format("La sección (%s) de código %s, tiene mas de un docente principal", seccion.getId(), seccion.getCodigo2()));

            DocenteCursoMasivo docenteCursoMasivo = new DocenteCursoMasivo();
            docenteCursoMasivo.setCursoMasivoExamen(cursoMasivosExamen);
            docenteCursoMasivo.setDocente(docenteSecciones.get(0).getDocente());
            docenteCursoMasivo.setFechaRegistro(new Date());
            docenteCursoMasivo.setSecciones(BigDecimal.ZERO.intValue());
            docenteCursoMasivo.setUserRegistro(ds.getUsuario());
            docenteCursoMasivo.setEstadoEnum(DocenteRolExamenEstadoEnum.ACT);
            // docenteCursoMasivoDAO.save(docenteCursoMasivo);
            DocenteCursoMasivo docenteCursoMasivoFound = docentesCursoMasivo.stream()
                    .filter(x -> x.getDocente().equals(docenteCursoMasivo.getDocente()))
                    .findFirst().orElse(null);
            if (docenteCursoMasivoFound == null) {
                docentesCursoMasivo.add(docenteCursoMasivo);
            }

            SeccionCursoMasivo seccionCursoMasivo = new SeccionCursoMasivo();
            seccionCursoMasivo.setDocente(docenteCursoMasivo.getDocente());
            seccionCursoMasivo.setCursoMasivoExamen(cursoMasivosExamen);
            seccionCursoMasivo.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionCursoMasivo.setSeccion(seccion);
            seccionCursoMasivo.setFechaRegistro(new Date());
            seccionCursoMasivo.setUserRegistro(ds.getUsuario());

            seccionCursoMasivoDAO.save(seccionCursoMasivo);

            List<MatriculaSeccion> matriculadosPorSeccion = mapMatriculadoSeccion.get(seccion.getId());
//            alus += matriculadosPorSeccion.size();
            alumnoCursoMasivoDAO.createForCursoMasivo(matriculadosPorSeccion, cursoMasivosExamen, seccionCursoMasivo, ds.getUsuario());

//            for (MatriculaSeccion matriculaSeccion : matriculadosPorSeccion) {
//                Alumno alumno = matriculaSeccion.getMatriculaResumen().getAlumno();
//                AlumnoCursoMasivo alumnoCursoMasivo = new AlumnoCursoMasivo();
//                alumnoCursoMasivo.setAlumno(alumno);
//                alumnoCursoMasivo.setCursoMasivoExamen(cursoMasivosExamen);
//                alumnoCursoMasivo.setSeccionCursoMasivo(seccionCursoMasivo);
//                alumnoCursoMasivo.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
//                alumnoCursoMasivo.setFechaRegistro(new Date());
//                alumnoCursoMasivo.setUserRegistro(ds.getUsuario());
//                alumnoCursoMasivoDAO.save(alumnoCursoMasivo);
//            }
//
        }

        docenteCursoMasivoDAO.createDocentesCursoMasivo(docentesCursoMasivo, cursoMasivosExamen, ds.getUsuario());
//        for (DocenteCursoMasivo docCursoMasivo : docentesCursoMasivo) {
//            docenteCursoMasivoDAO.save(docCursoMasivo);
//        }

//        cursoMasivosExamen.setAlumnos(alus);
//        cursoMasivosExamen.setSecciones(secciones.size());
//        cursoMasivoExamenDAO.update(cursoMasivosExamen);
        RolExamenes rolExamenesUpd = new RolExamenes(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CFG_MAS);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    private List<String> validarHorariosExamen(RolExamenes rolExamenes) {
        List<String> validations = new ArrayList();
        List<GrupoHorasExamen> gruposHorasExamen = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        for (GrupoHorasExamen grupoHorasExamen : gruposHorasExamen) {
            if (!grupoHorasExamen.getVerificado()) {
                String error = String.format("Horario del Grupo %s, no completado.", grupoHorasExamen.getGrupoHoras().getCodigo());
                validations.add(error);
            }
        }
        return validations;
    }

    @Override
    public List<CursoMasivoExamen> listCursosMasivosExamenes(RolExamenes rolExamenes) {

        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        List<CursoMasivoExamen> cursosMasivos = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes);
        List<AulaCursoMasivo> aulasCursos = aulaCursoMasivoDAO.allByCursosMasivos(cursosMasivos);
        Map<Long, List<AulaCursoMasivo>> mapAulasCursos = TypesUtil.convertListToMapList("cursoMasivoExamen.id", aulasCursos);

        List<FechaHoraGrupoExamen> fechasHorasGrupos = fechaHoraGrupoExamenDAO
                .allByGrupoHorasExamen(cursosMasivos.stream()
                        .filter(x -> ObjectUtil.getParentTree(x, "grupoHorasExamen.id") != null)
                        .map(x -> x.getGrupoHorasExamen()).collect(Collectors.toList()));
        Map<Long, List<FechaHoraGrupoExamen>> mapFechasHorasGruposByGrupoHoras = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHorasGrupos);

        Map<Long, Integer> mapDocentesCursosMasivos = docenteCursoMasivoDAO.countByCursosMasivos(cursosMasivos, DocenteRolExamenEstadoEnum.ACT);
        Map<Long, Integer> mapAlumnosCursosMasivos = alumnoCursoMasivoDAO.countByCursosMasivos(cursosMasivos, AlumnoRolExamenEstadoEnum.ACT);
        Map<Long, Integer> mapSeccionesCursosMasivos = seccionCursoMasivoDAO.countByCursosMasivos(cursosMasivos, SeccionRolExamenEstadoEnum.ACT);

        for (CursoMasivoExamen cursoMasivo : cursosMasivos) {

            cursoMasivo.setDocentesCount(mapDocentesCursosMasivos.get(cursoMasivo.getId()) != null ? mapDocentesCursosMasivos.get(cursoMasivo.getId()) : BigDecimal.ZERO.intValue());
            cursoMasivo.setAlumnosCount(mapAlumnosCursosMasivos.get(cursoMasivo.getId()) != null ? mapAlumnosCursosMasivos.get(cursoMasivo.getId()) : BigDecimal.ZERO.intValue());
            cursoMasivo.setSeccionesCount(mapSeccionesCursosMasivos.get(cursoMasivo.getId()) != null ? mapSeccionesCursosMasivos.get(cursoMasivo.getId()) : BigDecimal.ZERO.intValue());

            List<AulaCursoMasivo> aulasByCurso = mapAulasCursos.get(cursoMasivo.getId());
            aulasByCurso = (aulasByCurso == null) ? new ArrayList() : aulasByCurso;
            if (ObjectUtil.getParentTree(cursoMasivo, "grupoHorasExamen.id") != null) {
                List<FechaHoraGrupoExamen> fechasHorasGrupoExamen = mapFechasHorasGruposByGrupoHoras.get(cursoMasivo.getGrupoHorasExamen().getId());
                cursoMasivo.getGrupoHorasExamen().setSemanaExamen(fechasHorasGrupoExamen.get(0).getSemanaExamen());
            }
            cursoMasivo.setAulasCursosMasivos(aulasByCurso);
        }

        List<SeccionCursoMasivo> seccionesCursos = seccionCursoMasivoDAO.allByCursosMasivos(cursosMasivos);

        Map<Long, List<SeccionCursoMasivo>> mapSeccionesCursos = TypesUtil.convertListToMapList("cursoMasivoExamen.id", seccionesCursos);

        for (CursoMasivoExamen cursoMasivo : cursosMasivos) {
            List<SeccionCursoMasivo> seccionesByCurso = mapSeccionesCursos.get(cursoMasivo.getId());
            seccionesByCurso = (seccionesByCurso == null) ? new ArrayList() : seccionesByCurso;
            cursoMasivo.setSeccionesCursosMasivos(seccionesByCurso);
        }

        return cursosMasivos;
    }

    @Override
    public List<Curso> allCursosByCiclo(String nombre, RolExamenes rolExamenes, CicloAcademico cicloAcademico) {
        nombre = "%" + nombre.replaceAll(" ", "%") + "%";
        return cursoDAO.allForExamenByCiclo(nombre, rolExamenes, cicloAcademico);
    }

    @Override
    public List<Aula> allPabellonesByOficina() {
        Oficina oera = oficinaDAO.findByCode(OERA.name());
        return aulaDAO.allPabellonesByOficina(oera);
    }

    @Override
    @Transactional
    public void eliminarCursoMasivoExamen(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds) {
        CursoMasivoExamen cursoMasivoExamenBD = cursoMasivoExamenDAO.find(cursoMasivoExamen.getId());
        RolExamenes rolExamenes = cursoMasivoExamenBD.getRolExamenes();
        this.checkNoPublicado(rolExamenes);

        grupoRegularConnector.validarSituacionBeforeOr("eliminar", "los grupos regulares", rolExamenes.isSituacionConfigurarRol(), rolExamenes.isSituacionConfigurarCursoMasivo());

        List<SeccionCursoMasivo> seccionCursoMasivos = seccionCursoMasivoDAO.allSeccionByCursoMasivo(cursoMasivoExamenBD);

        seccionExcluidoDAO.deleteBySecciones(seccionCursoMasivos.stream().map(x -> x.getSeccion()).collect(Collectors.toList()));

        for (SeccionCursoMasivo seccionCursoMasivo : seccionCursoMasivos) {
            seccionCursoMasivoDAO.delete(seccionCursoMasivo);
        }

        List<AlumnoCursoMasivo> alumnoCursoMasivos = alumnoCursoMasivoDAO.allAlumnoByCursoMasivo(cursoMasivoExamenBD);
        for (AlumnoCursoMasivo alumnoCursoMasivo : alumnoCursoMasivos) {
            alumnoCursoMasivoDAO.delete(alumnoCursoMasivo);
        }

        List<AulaCursoMasivo> aulaCursoMasivos = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamenBD);
        for (AulaCursoMasivo aulaCursoMasivo : aulaCursoMasivos) {
            aulaCursoMasivoDAO.delete(aulaCursoMasivo);
        }

        docenteCursoMasivoDAO.deleteByCursoMasivo(cursoMasivoExamenBD);

        cursoMasivoExamenDAO.delete(cursoMasivoExamenBD);
    }

    @Override
    public List<Aula> allAulasOERAByModulo(Aula modulo) {
        Oficina oera = oficinaDAO.findByCode(OERA.name());
        return aulaDAO.allByOficinaModulo(oera, modulo);
    }

    @Override
    @Transactional
    public void saveAula(CursoMasivoExamen cursoMasivo, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        CursoMasivoExamen cursoMasivoBD = cursoMasivoExamenDAO.find(cursoMasivo.getId());
        RolExamenes rolExamenes = rolExamenesDAO.find(cursoMasivoBD.getRolExamenes().getId());

        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(cursoMasivoBD.getRolExamenes().isSituacionConfigurarGrupoRegular()
                || cursoMasivoBD.getRolExamenes().isSituacionAsignarHorarioCursosMasivos()
                || cursoMasivoBD.getRolExamenes().isSituacionConfigurarGrupoEspecial(), "No puede realizar esta acción.");

        List<AulaCursoMasivo> aulasCurso = cursoMasivo.getAulasCursosMasivos();
        List<AulaCursoMasivo> aulasCursoBD = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD);
        ListsInspector inspector = TypesUtil.analizeLists(aulasCursoBD, aulasCurso, "aula.id");

        List<AulaCursoMasivo> aulasUtilizadas = new ArrayList();
        int capAulasSinMover = 0;
        for (Object obj : inspector.getOldListDB()) {
            AulaCursoMasivo aulaCurso = (AulaCursoMasivo) obj;
            capAulasSinMover += aulaCurso.getAula().getCapacidadAula();
            aulasUtilizadas.add(aulaCurso);
        }

        int capAulasNuevas = 0;
        List<Aula> aulas = new ArrayList();
        for (Object obj : inspector.getNewList()) {
            AulaCursoMasivo aulaCurso = (AulaCursoMasivo) obj;
            aulaCurso.setCursoMasivoExamen(cursoMasivoBD);
            aulaCurso.setAula(aulaCurso.getAula());
            aulaCurso.setUserRegistro(ds.getUsuario());
            aulaCurso.setFechaRegistro(new Date());
            aulaCursoMasivoDAO.save(aulaCurso);
            capAulasNuevas += aulaCurso.getAula().getCapacidadAula();

            aulas.add(aulaCurso.getAula());
            aulasUtilizadas.add(aulaCurso);
        }
        int capAulasFuera = 0;
        for (Object obj : inspector.getDeadList()) {
            AulaCursoMasivo aulaCurso = (AulaCursoMasivo) obj;
            aulaCursoMasivoDAO.delete(aulaCurso);
            capAulasFuera += aulaCurso.getAula().getCapacidadAula();
        }
        int total = capAulasSinMover + capAulasNuevas;
        cursoMasivoBD.setCapacidadAulas(total);
        cursoMasivoBD.setAulas(aulasCurso.size());
        cursoMasivoExamenDAO.update(cursoMasivoBD);

        this.rolExamenesLogger.iniciarCursoMasivo();

        ///eliminar horario aulas
        horarioAulaDAO.deleteByCursoMasivo(cursoMasivoBD);

        List<Aula> aulasDB = grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolExamenes, null);
        rolExamenesLogger.setAulas(new ArrayList(aulasDB));

        GrupoHorasExamen grupoHorasExamen = null;
        if (ObjectUtil.getParent(cursoMasivoBD, "grupoHorasExamen.id") != null) {
            grupoHorasExamen = cursoMasivoBD.getGrupoHorasExamen().clone();
            List<FechaHoraGrupoExamen> fechasHorasGrupos = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(grupoHorasExamen);
            grupoHorasExamen.setFechasHorasGruposExamen(fechasHorasGrupos);
            this.verificarAulas(cursoMasivo, grupoHorasExamen);
        }

        List<AlumnoCursoMasivo> alumnosCursoMasivo = alumnoCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD, AlumnoRolExamenEstadoEnum.ACT);
        List<DocenteCursoMasivo> docenteCursoMasivo = docenteCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD, DocenteRolExamenEstadoEnum.ACT);

        List<Alumno> alumnos = alumnosCursoMasivo.stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<Docente> docentes = docenteCursoMasivo.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        //validar cruce horario docentes !!!!!!!!!!!!!!!!!!!!!!!!!!!!
        boolean validacionCursosMasivos = cursoMasivoBD.getGrupoHorasExamen() == null ? true : this.validateCruceCursosMasivos(cursoMasivoBD, alumnos, docentes, aulas);
        boolean validacionGruposRegulares = cursoMasivoBD.getGrupoHorasExamen() == null ? true : grupoRegularConnector.validarGrupoRegular(cursoMasivoBD.getGrupoHorasExamen(), alumnos, docentes, aulas);
        boolean validacionSeccionesEspeciales = cursoMasivoBD.getGrupoHorasExamen() == null ? true : grupoRegularConnector.validarGrupoEspecial(cursoMasivoBD.getGrupoHorasExamen(), docentes, aulas, alumnos);

        if (!validacionCursosMasivos || !validacionGruposRegulares || !validacionSeccionesEspeciales) {
            throw new PhobosException("Conflictos encontrados.");
        }

        if (ObjectUtil.getParent(cursoMasivoBD, "grupoHorasExamen.id") != null) {
            //List<SeccionCursoMasivo> seccionesCursoMasivos = seccionCursoMasivoDAO.allByCursoMasivo(cursoMasivo, SeccionRolExamenEstadoEnum.ACT);
            //for (SeccionCursoMasivo seccionesCursoMasivo : seccionesCursoMasivos) {
            for (AulaCursoMasivo aulaCur : aulasUtilizadas) {
                for (FechaHoraGrupoExamen fechaHoraGrupoExamen : grupoHorasExamen.getFechasHorasGruposExamen()) {
                    HorarioAula horarioAula = new HorarioAula(fechaHoraGrupoExamen, aulaCur.getAula());
                    horarioAula.setRolExamenes(rolExamenes);
                    horarioAula.setCursoMasivoExamen(cursoMasivo);
                    horarioAula.setSeccion(null);
                    horarioAulaDAO.save(horarioAula);
                }
            }

            //}
        }
    }

    @Override
    @Transactional
    public void excluirCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds) {
        cursoMasivoExamen = cursoMasivoExamenDAO.find(cursoMasivoExamen.getId());
        RolExamenes rolExamenes = cursoMasivoExamen.getRolExamenes();

        grupoRegularConnector.validarSituacionBeforeOr("excluir", "los grupos regulares", rolExamenes.isSituacionConfigurarRol(), rolExamenes.isSituacionConfigurarCursoMasivo());
        Assert.isTrue(cursoMasivoExamen.isEstadoActivo(), "Solo se puede excluir los cursos masivos activos");

        CursoMasivoExamen cursoMasivoExamenUpd = new CursoMasivoExamen();
        cursoMasivoExamenUpd.setId(cursoMasivoExamen.getId());
        cursoMasivoExamenUpd.setUsuarioExclusion(ds.getUsuario());
        cursoMasivoExamenUpd.setFechaExclusion(ds.getFechaAccionAudit());
        cursoMasivoExamenDAO.updateEstadoExcluido(cursoMasivoExamen);

        CursoExcluido cursoExcluido = new CursoExcluido();
        cursoExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        cursoExcluido.setUserRegistro(ds.getUsuario());
        cursoExcluido.setRolExamenes(rolExamenes);
        cursoExcluido.setEstadoEnum(EstadoEnum.ACT);
        cursoExcluido.setCurso(cursoMasivoExamen.getCurso());
        cursoExcluido.setEsExclusionCompleta(Boolean.TRUE);
        cursoExcluidoDAO.save(cursoExcluido);

        List<SeccionCursoMasivo> seccionesCursoMasivo = seccionCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, SeccionRolExamenEstadoEnum.ACT);
        for (SeccionCursoMasivo seccionCursoMasivo : seccionesCursoMasivo) {
            this.excluirSeccionCursoMasivo(seccionCursoMasivo, cursoExcluido, ds);
        }
    }

    @Override
    @Transactional
    public void activarCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds) {
        cursoMasivoExamen = cursoMasivoExamenDAO.find(cursoMasivoExamen.getId());
        RolExamenes rolExamenes = cursoMasivoExamen.getRolExamenes();
        GrupoHorasExamen grupoHorasExamen = cursoMasivoExamen.getGrupoHorasExamen();

        grupoRegularConnector.validarSituacionBeforeOr("incluir", "los grupos regulares", rolExamenes.isSituacionConfigurarRol(), rolExamenes.isSituacionConfigurarCursoMasivo());
        Assert.isTrue(cursoMasivoExamen.isEstadoExcluido(), "Solo se puede incluir los cursos masivos excluidos");

        CursoMasivoExamen cursoMasivoExamenUpd = new CursoMasivoExamen(cursoMasivoExamen.getId());
        cursoMasivoExamenUpd.setEstadoEnum(EstadoCursoMasivoEnum.ACT);
        cursoMasivoExamenDAO.updateEstado(cursoMasivoExamenUpd);

        CursoExcluido cursoExcluido = cursoExcluidoDAO.findActiveByCursoAndRolExamenes(cursoMasivoExamen.getCurso(), rolExamenes);
        /*CursoExcluido cursoExcluidoUpd = new CursoExcluido(cursoExcluido.getId());
        cursoExcluidoUpd.setEstadoEnum(EstadoEnum.ANU);
        cursoExcluidoUpd.setEsExclusionCompleta(Boolean.FALSE);
        cursoExcluidoDAO.updateColumns(cursoExcluidoUpd, "estado", "esExclusionCompleta");
         */
        seccionExcluidoDAO.deleteByCursoExcluido(cursoExcluido);
        cursoExcluidoDAO.delete(cursoExcluido);

        this.rolExamenesLogger.activarCursoMasivo();

        List<AulaCursoMasivo> aulasCursosMasivos = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen);
        List<Aula> aulas = aulasCursosMasivos.stream().map(x -> x.getAula()).collect(Collectors.toList());

        List<DocenteCursoMasivo> docentesCursosMasivos = docenteCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, DocenteRolExamenEstadoEnum.EXC);
        List<Docente> docentesOrigen = docentesCursosMasivos.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoCursoMasivo> alumnosCursoMasivosOrigen = alumnoCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, AlumnoRolExamenEstadoEnum.EXC);
        List<Alumno> alumnosOrigen = alumnosCursoMasivosOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        boolean validacionCursosMasivos = this.validateCruceCursosMasivos(cursoMasivoExamen, alumnosOrigen, docentesOrigen, aulas);
        boolean validacionGruposRegulares = grupoHorasExamen != null ? grupoRegularConnector.validarGrupoRegular(grupoHorasExamen, alumnosOrigen, docentesOrigen, aulas) : true;
        boolean validacionSeccionesEspeciales = grupoHorasExamen != null ? grupoRegularConnector.validarGrupoEspecial(grupoHorasExamen, docentesOrigen, aulas, alumnosOrigen) : true;
        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            this.activarCursoMasivo(cursoMasivoExamen, docentesCursosMasivos, alumnosCursoMasivosOrigen);
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }

    }

    public void activarCursoMasivo(
            CursoMasivoExamen cursoMasivoExamen,
            List<DocenteCursoMasivo> docentesCursosMasivos,
            List<AlumnoCursoMasivo> alumnosCursoMasivos) {
        List<SeccionCursoMasivo> seccionCursoMasivos = seccionCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, SeccionRolExamenEstadoEnum.EXC);
        for (SeccionCursoMasivo seccionCursoMasivo : seccionCursoMasivos) {
            SeccionCursoMasivo seccionCursoMasivoUpd = new SeccionCursoMasivo(seccionCursoMasivo.getId());
            seccionCursoMasivoUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionCursoMasivoDAO.updateEstado(seccionCursoMasivoUpd);
        }
        for (DocenteCursoMasivo docentesCursosMasivo : docentesCursosMasivos) {
            DocenteCursoMasivo docenteCursoMasivoUpd = new DocenteCursoMasivo(docentesCursosMasivo.getId());
            docenteCursoMasivoUpd.setEstadoEnum(DocenteRolExamenEstadoEnum.ACT);
            docenteCursoMasivoDAO.updateEstado(docenteCursoMasivoUpd);
        }
        for (AlumnoCursoMasivo alumnosCursoMasivo : alumnosCursoMasivos) {
            AlumnoCursoMasivo alumnoCursoMasivoUpd = new AlumnoCursoMasivo(alumnosCursoMasivo.getId());
            alumnoCursoMasivoUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
            alumnoCursoMasivoDAO.updateEstado(alumnoCursoMasivoUpd);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivo, CursoExcluido cursoExcluido, DataSessionPivot ds) {
        seccionCursoMasivo = seccionCursoMasivoDAO.find(seccionCursoMasivo.getId());
        /*if (cursoExcluido == null) {
            cursoExcluido = cursoExcluidoDAO.findActiveByCursoAndRolExamenes(
                    seccionCursoMasivo.getCursoMasivoExamen().getCurso(),
                    seccionCursoMasivo.getCursoMasivoExamen().getRolExamenes()
            );
        }*/
        RolExamenes rolExamenes = seccionCursoMasivo.getCursoMasivoExamen().getRolExamenes();
        grupoRegularConnector.validarSituacionBeforeOr("excluir", "los grupos regulares", rolExamenes.isSituacionConfigurarRol(), rolExamenes.isSituacionConfigurarCursoMasivo());
        Assert.isTrue(seccionCursoMasivo.isEstadoActivo(), "Solo se puede excluir las secciones masivas activas");

        SeccionCursoMasivo seccionCursoMasivoUpd = new SeccionCursoMasivo();
        seccionCursoMasivoUpd.setId(seccionCursoMasivo.getId());
        seccionCursoMasivoUpd.setUsuarioExclusion(ds.getUsuario());
        seccionCursoMasivoUpd.setFechaExclusion(ds.getFechaAccionAudit());
        seccionCursoMasivoDAO.updateEstadoExcluido(seccionCursoMasivoUpd);

        SeccionExcluido seccionExcluido = new SeccionExcluido();
        seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
        seccionExcluido.setFechaRegistro(ds.getFechaAccionAudit());
        seccionExcluido.setRolExamenes(seccionCursoMasivo.getCursoMasivoExamen().getRolExamenes());
        seccionExcluido.setSeccion(seccionCursoMasivo.getSeccion());
        seccionExcluido.setUserRegistro(ds.getUsuario());
        seccionExcluido.setCursoExcluido(cursoExcluido);
        seccionExcluidoDAO.save(seccionExcluido);
        /*
        Integer countSeccionExcluidasAnu = seccionExcluidoDAO.countByCursoExcluido(cursoExcluido, EstadoEnum.ANU);
        if (countSeccionExcluidasAnu > 0) {
            CursoExcluido cursoExcluidoUpd = new CursoExcluido(cursoExcluido.getId());
            cursoExcluidoUpd.setEsExclusionCompleta(Boolean.FALSE);
            //  cursoExcluidoUpd.setEstadoEnum(EstadoEnum.ACT);
            cursoExcluidoDAO.updateColumns(cursoExcluidoUpd, "esExclusionCompleta");
        } else if (countSeccionExcluidasAnu == 0) {
            CursoExcluido cursoExcluidoUpd = new CursoExcluido(cursoExcluido.getId());
            //  cursoExcluidoUpd.setEstadoEnum(EstadoEnum.ACT);
            cursoExcluidoUpd.setEsExclusionCompleta(Boolean.TRUE);
            cursoExcluidoDAO.updateColumns(cursoExcluidoUpd, "esExclusionCompleta");
        }
         */
        List<AlumnoCursoMasivo> alumnosCursoMasivoBySeccion = alumnoCursoMasivoDAO.allBySeccionCursosMasivos(seccionCursoMasivo, AlumnoRolExamenEstadoEnum.ACT);
        for (AlumnoCursoMasivo alumnoCursoMasivo : alumnosCursoMasivoBySeccion) {
            this.excluirAlumnoCursoMasivo(alumnoCursoMasivo, ds);
        }

        //Excluir docentes
        Integer countDocentesByCursoMasivo = seccionCursoMasivoDAO.countDocenteByCursoMasivo(
                seccionCursoMasivo.getDocente(),
                seccionCursoMasivo.getCursoMasivoExamen(),
                SeccionRolExamenEstadoEnum.ACT);
        if (countDocentesByCursoMasivo == 1) {
            List<DocenteCursoMasivo> docentesCursosMasivos = docenteCursoMasivoDAO.allByCursoMasivoAndDocenteAndEstados(
                    seccionCursoMasivo.getCursoMasivoExamen(),
                    seccionCursoMasivo.getDocente(),
                    DocenteRolExamenEstadoEnum.ACT);
            for (DocenteCursoMasivo docentesCursosMasivo : docentesCursosMasivos) {
                this.excluirDocenteCursoMasivo(docentesCursosMasivo, ds);
            }

        }
    }

    @Override
    @Transactional
    public void activarSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivo, DataSessionPivot ds) {
        seccionCursoMasivo = seccionCursoMasivoDAO.find(seccionCursoMasivo.getId());
        CursoMasivoExamen cursoMasivoExamen = seccionCursoMasivo.getCursoMasivoExamen();
        Docente docente = seccionCursoMasivo.getDocente();

        seccionCursoMasivo = seccionCursoMasivoDAO.find(seccionCursoMasivo.getId());
        RolExamenes rolExamenes = seccionCursoMasivo.getCursoMasivoExamen().getRolExamenes();
        grupoRegularConnector.validarSituacionBeforeOr("incluir", "los grupos regulares", rolExamenes.isSituacionConfigurarRol(), rolExamenes.isSituacionConfigurarCursoMasivo());
        Assert.isTrue(seccionCursoMasivo.isEstadoExcluido(), "Solo se puede incluir las secciones masivas excluidss");

        SeccionCursoMasivo seccionCursoMasivoUpd = new SeccionCursoMasivo(seccionCursoMasivo.getId());
        seccionCursoMasivoUpd.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
        seccionCursoMasivoDAO.updateEstado(seccionCursoMasivoUpd);

        SeccionExcluido seccionExcluido = seccionExcluidoDAO.findByRolExamenesAndSeccion(rolExamenes, seccionCursoMasivo.getSeccion(), EstadoEnum.ACT);
        seccionExcluido.setEstadoEnum(EstadoEnum.ANU);
        seccionExcluidoDAO.update(seccionExcluido);

        //validar cruces
        List<AulaCursoMasivo> aulasCursosMasivos = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen);
        List<Aula> aulas = aulasCursosMasivos.stream().map(x -> x.getAula()).collect(Collectors.toList());

        List<DocenteCursoMasivo> docentesCursosMasivos = docenteCursoMasivoDAO.allByCursoMasivoAndDocenteAndEstados(cursoMasivoExamen, docente, DocenteRolExamenEstadoEnum.EXC);
        List<Docente> docentesOrigen = docentesCursosMasivos.stream().map(x -> x.getDocente()).collect(Collectors.toList());

        List<AlumnoCursoMasivo> alumnosCursoMasivosOrigen = alumnoCursoMasivoDAO.allBySeccionCursosMasivos(seccionCursoMasivo, AlumnoRolExamenEstadoEnum.EXC);
        List<Alumno> alumnosOrigen = alumnosCursoMasivosOrigen.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        this.rolExamenesLogger.activarCursoMasivo();
        boolean validacionCursosMasivos = this.validateCruceCursosMasivos(cursoMasivoExamen, alumnosOrigen, docentesOrigen, aulas);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(cursoMasivoExamen.getGrupoHorasExamen(), alumnosOrigen, docentesOrigen, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(cursoMasivoExamen.getGrupoHorasExamen(), docentesOrigen, aulas, alumnosOrigen);
        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {

            for (DocenteCursoMasivo docentesCursosMasivo : docentesCursosMasivos) {
                DocenteCursoMasivo docenteCursoMasivoUpd = new DocenteCursoMasivo(docentesCursosMasivo.getId());
                docenteCursoMasivoUpd.setEstadoEnum(DocenteRolExamenEstadoEnum.ACT);
                docenteCursoMasivoDAO.updateEstado(docenteCursoMasivoUpd);
            }
            for (AlumnoCursoMasivo alumnoCursoMasivo : alumnosCursoMasivosOrigen) {
                AlumnoCursoMasivo alumnoCursoMasivoUpd = new AlumnoCursoMasivo(alumnoCursoMasivo.getId());
                alumnoCursoMasivoUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                alumnoCursoMasivoDAO.updateEstado(alumnoCursoMasivo);
            }
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirAlumnoCursoMasivo(AlumnoCursoMasivo alumnoCursoMasivo, DataSessionPivot ds) {
        AlumnoCursoMasivo alumnoCursoMasivoUpd = new AlumnoCursoMasivo(alumnoCursoMasivo.getId());
        alumnoCursoMasivoDAO.updateEstadoExclusion(alumnoCursoMasivoUpd);
    }

    @Override
    public RolExamenesLogger activarAlumnoCursoMasivo(AlumnoCursoMasivo alumnoCursoMasivo, DataSessionPivot ds) {
        GrupoHorasExamen grupoHorasExamen = alumnoCursoMasivo.getCursoMasivoExamen().getGrupoHorasExamen();
        RolExamenesLogger rolExamenesLoggerResult = new RolExamenesLogger();
        if (grupoHorasExamen != null) {
            rolExamenesLoggerResult = grupoRegularConnector.validacionActivarAlumno(grupoHorasExamen, alumnoCursoMasivo.getAlumno());
            Assert.isFalse(rolExamenesLoggerResult.isCruce(), "Tiene cruce de horario.");
        }
        AlumnoCursoMasivo alumnoCursoMasivoUpd = new AlumnoCursoMasivo(alumnoCursoMasivo.getId());
        alumnoCursoMasivoUpd.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
        alumnoCursoMasivoDAO.updateEstado(alumnoCursoMasivoUpd);
        return rolExamenesLoggerResult;
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirDocenteCursoMasivo(DocenteCursoMasivo docenteCursoMasivo, DataSessionPivot ds) {
        DocenteCursoMasivo docenteCursoMasivoUpd = new DocenteCursoMasivo();
        docenteCursoMasivoUpd.setId(docenteCursoMasivo.getId());
        docenteCursoMasivoDAO.updateEstadoExclusion(docenteCursoMasivoUpd);
    }

    @Override
    @Transactional(readOnly = false)
    public RolExamenesLogger activarDocenteCursoMasivo(DocenteCursoMasivo docenteCursoMasivo, DataSessionPivot ds) {
        docenteCursoMasivo = docenteCursoMasivoDAO.find(docenteCursoMasivo.getId());
        CursoMasivoExamen cursoMasivoExamen = docenteCursoMasivo.getCursoMasivoExamen();
        GrupoHorasExamen grupoHorasExamen = cursoMasivoExamen.getGrupoHorasExamen();

        RolExamenesLogger rolExamenesLoggerResult = grupoRegularConnector.validacionActivarDocente(grupoHorasExamen, docenteCursoMasivo.getDocente());
        Assert.isFalse(rolExamenesLoggerResult.isCruce(), "Tiene cruce de horario.");
        DocenteCursoMasivo docenteCursoMasivoUpd = new DocenteCursoMasivo();
        docenteCursoMasivoUpd.setId(docenteCursoMasivo.getId());
        docenteCursoMasivoUpd.setEstadoEnum(DocenteRolExamenEstadoEnum.ACT);
        docenteCursoMasivoDAO.updateEstado(docenteCursoMasivoUpd);

        return rolExamenesLoggerResult;
    }

    @Override
    public CursoMasivoExamen findCursoMasivo(Long idCursoMasivo) {
        CursoMasivoExamen cursoMasivoExamen = cursoMasivoExamenDAO.find(idCursoMasivo);
        List<SeccionCursoMasivo> seccionesCursoMasivo = seccionCursoMasivoDAO.allSeccionByCursoMasivo(cursoMasivoExamen);
        cursoMasivoExamen.setSeccionesCursosMasivos(seccionesCursoMasivo);
        return cursoMasivoExamen;
    }

    @Override
    @Transactional
    public void saveHorarioExamen(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds) {
        RolExamenes rolExamenes = rolExamenesDAO.find(cursoMasivoExamen.getRolExamenes().getId());
        this.checkNoPublicado(rolExamenes);

        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(rolExamenes.isSituacionConfigurarGrupoRegular(), "Debe configurar los grupos regulares previamente.");

        this.rolExamenesLogger.iniciarCursoMasivo();

        CursoMasivoExamen cursoMasivoExamenDB = cursoMasivoExamenDAO.find(cursoMasivoExamen.getId());
        //System.out.println("gpoExamen/db=" + cursoMasivoExamenDB.getGrupoHorasExamen().getId());
        //System.out.println("gpoExamen/form=" + cursoMasivoExamen.getGrupoHorasExamen().getId());
        if (cursoMasivoExamenDB.getGrupoHorasExamen() != null) {
            if (!cursoMasivoExamen.getGrupoHorasExamen().getId().equals(cursoMasivoExamenDB.getGrupoHorasExamen().getId())) {
                this.deleteHorarioByCursoMasivo(cursoMasivoExamen);
            }
        }

        GrupoHorasExamen grupoHorasExamenDB = grupoHorasExamenDAO.find(cursoMasivoExamen.getGrupoHorasExamen().getId());
        GrupoHorasExamen grupoHorasExamen = cursoMasivoExamen.getGrupoHorasExamen();
        List<FechaHoraGrupoExamen> fechasHorasGrupos = fechaHoraGrupoExamenDAO.allByGrupoHorasExamenOrderByDiaHora(grupoHorasExamen);
        grupoHorasExamen.setFechasHorasGruposExamen(fechasHorasGrupos);
        grupoHorasExamen.setSemanaExamen(fechasHorasGrupos != null && !fechasHorasGrupos.isEmpty() ? fechasHorasGrupos.get(0).getSemanaExamen() : null);
        grupoHorasExamen.setGrupoHoras(grupoHorasExamenDB.getGrupoHoras());

        List<Aula> aulasDB = grupoRegularConnector.allAulasOeraWithHorarioByRolExamenes(rolExamenes, OficinaEnum.OERA);
        rolExamenesLogger.setAulas(new ArrayList(aulasDB));
        this.verificarAulas(cursoMasivoExamen, grupoHorasExamen);

        List<AlumnoCursoMasivo> alumnosCursoMasivo = alumnoCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<DocenteCursoMasivo> docenteCursoMasivo = docenteCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, DocenteRolExamenEstadoEnum.ACT);

        cursoMasivoExamen.setAlumnosCursosMasivos(alumnosCursoMasivo);
        cursoMasivoExamen.setDocentesCursosMasivos(docenteCursoMasivo);

        List<Alumno> alumnos = cursoMasivoExamen.getAlumnosCursosMasivos().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<Aula> aulas = cursoMasivoExamen.getAulasCursosMasivos().stream().map(x -> x.getAula()).collect(Collectors.toList());
        List<Docente> docentes = cursoMasivoExamen.getDocentesCursosMasivos().stream().map(x -> x.getDocente()).collect(Collectors.toList());

        boolean validacionCursosMasivos = this.validateCruceCursosMasivos(cursoMasivoExamen, alumnos, docentes, aulas);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(grupoHorasExamen, alumnos, docentes, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(cursoMasivoExamen.getGrupoHorasExamen(), docentes, aulas, alumnos);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            CursoMasivoExamen cursoMasivoUpd = new CursoMasivoExamen();
            cursoMasivoUpd.setId(cursoMasivoExamen.getId());
            cursoMasivoUpd.setGrupoHorasExamen(grupoHorasExamen);
            cursoMasivoExamenDAO.updateFechaExamen(cursoMasivoUpd);

            List<CursoMasivoExamen> cursosMasivosByRolExamen = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
            CursoMasivoExamen cursoMasivoSinHorario = cursosMasivosByRolExamen.stream()
                    .filter(x -> x.getGrupoHorasExamen() == null)
                    .filter(x -> !x.equals(cursoMasivoExamen))
                    .findFirst().orElse(null);
            if (cursoMasivoSinHorario == null) {
                RolExamenes rolExamenesUpd = new RolExamenes();
                rolExamenesUpd.setId(cursoMasivoExamen.getRolExamenes().getId());
                rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CFG_HOR_MAS);
                rolExamenesDAO.updateSituacion(rolExamenesUpd);
            }
            //List<SeccionCursoMasivo> seccionesCursoMasivos = seccionCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, SeccionRolExamenEstadoEnum.ACT);
            //for (SeccionCursoMasivo seccionesCursoMasivo : seccionesCursoMasivos) {
            List<AulaCursoMasivo> aulasCurso = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen);
            for (AulaCursoMasivo aulaCur : aulasCurso) {
                for (FechaHoraGrupoExamen fechaHoraGrupoExamen : grupoHorasExamen.getFechasHorasGruposExamen()) {
                    HorarioAula horarioAula = new HorarioAula(fechaHoraGrupoExamen, aulaCur.getAula());
                    horarioAula.setRolExamenes(rolExamenes);
                    horarioAula.setCursoMasivoExamen(cursoMasivoExamen);
                    horarioAula.setSeccion(null);
                    horarioAulaDAO.save(horarioAula);
                }
            }

            //}
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

    public void verificarAulas(CursoMasivoExamen cursoMasivoExamen, GrupoHorasExamen grupoHorasExamen) {
        List<AulaCursoMasivo> aulasCursoMasivo = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen);
        Assert.isFalse(aulasCursoMasivo.isEmpty(), "Debe asignar aulas al curso masivo.");
        cursoMasivoExamen.setAulasCursosMasivos(aulasCursoMasivo);

        for (AulaCursoMasivo aulaCursoMasivo : aulasCursoMasivo) {
            boolean checkDisponibilidadAula = grupoRegularConnector.checkDisponibilidadAula(aulaCursoMasivo.getAula(), grupoHorasExamen);
            if (!checkDisponibilidadAula) {
                rolExamenesLogger.aulaOcupada(aulaCursoMasivo.getAula(), grupoHorasExamen);
            }
        }
        if (rolExamenesLogger.isCruce()) {
            throw new PhobosException("Error al asignar horario");
        }

    }

    @Override
    public GrupoHorasExamen revisarGpoHorasExamenCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds) {
        //   SemanaExamen semanaExamen = cursoMasivoExamen.getGrupoHorasExamen().getSemanaExamen();
        RolExamenes rolExamenes = rolExamenesDAO.find(cursoMasivoExamen.getRolExamenes().getId());
        this.checkNoPublicado(rolExamenes);

        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(rolExamenes.isSituacionConfigurarGrupoRegular(), "Debe configurar los grupos regulares previamente.");

        this.rolExamenesLogger.iniciarCursoMasivo();

        GrupoHorasExamen grupoHorasExamen = cursoMasivoExamen.getGrupoHorasExamen();

        List<AulaCursoMasivo> aulasCursoMasivo = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen);
        Assert.isFalse(aulasCursoMasivo.isEmpty(), "Debe asignar aulas al curso masivo.");
        List<AlumnoCursoMasivo> alumnosCursoMasivo = alumnoCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<DocenteCursoMasivo> docenteCursoMasivo = docenteCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, DocenteRolExamenEstadoEnum.ACT);

        cursoMasivoExamen.setAlumnosCursosMasivos(alumnosCursoMasivo);
        cursoMasivoExamen.setAulasCursosMasivos(aulasCursoMasivo);
        cursoMasivoExamen.setDocentesCursosMasivos(docenteCursoMasivo);

        List<Alumno> alumnos = cursoMasivoExamen.getAlumnosCursosMasivos().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<Aula> aulas = cursoMasivoExamen.getAulasCursosMasivos().stream().map(x -> x.getAula()).collect(Collectors.toList());
        List<Docente> docentes = cursoMasivoExamen.getDocentesCursosMasivos().stream().map(x -> x.getDocente()).collect(Collectors.toList());

        //validar cruce horario docentes !!!!!!!!!!!!!!!!!!!!!!!!!!!!
        boolean validacionCursosMasivos = this.validateCruceCursosMasivos(cursoMasivoExamen, alumnos, docentes, aulas);
        //boolean validacionCursosMasivos = this.validateCruceCursosMasivos(cursoMasivoExamen, alumnos, docentes, aulas);

        grupoHorasExamen.setRevisado("NO");
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(grupoHorasExamen, alumnos, docentes, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(grupoHorasExamen, docentes, aulas, alumnos);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            grupoHorasExamen.setRevisado("SI");
        }

        return grupoHorasExamen;

    }

    @Override
    public boolean validateCruceCursosMasivos(CursoMasivoExamen cursoMasivoExamen, List<Alumno> alumnos, List<Docente> docentes, List<Aula> aulas) {
        List<CursoMasivoExamen> cursosMasivosOthers = cursoMasivoExamenDAO.allByRolExamenes(cursoMasivoExamen.getRolExamenes(), EstadoCursoMasivoEnum.ACT);
        cursosMasivosOthers.removeIf(
                x -> ObjectUtil.getParentTree(x, "grupoHorasExamen.id") == null
                || !x.getGrupoHorasExamen().equals(cursoMasivoExamen.getGrupoHorasExamen())
                || x.equals(cursoMasivoExamen)
        );

        if (cursosMasivosOthers.isEmpty()) {
            return true;
        }
        grupoRegularConnector.fillActiveInfoCursosMasivos(cursosMasivosOthers);
        return grupoRegularConnector.validarCursosMasivos(cursosMasivosOthers, docentes, aulas, alumnos);
    }

    @Override
    public List<DocenteCursoMasivo> allDocentesCursosMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen) {
        return docenteCursoMasivoDAO.allByDynatableAndCursoMasivo(filter, cursoMasivoExamen);
    }

    @Override
    public List<AlumnoCursoMasivo> allAlumnosCursoMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen) {
        return alumnoCursoMasivoDAO.allByDynatableAndCursoMasivo(filter, cursoMasivoExamen);
    }

    @Override
    public List<SeccionCursoMasivo> allSeccionesCursoMasivosDynaByCursoMasivo(DynatableFilter filter, CursoMasivoExamen cursoMasivoExamen) {
        List<SeccionCursoMasivo> seccionesCursoMasivos = seccionCursoMasivoDAO.allByDynatableAndCursoMasivo(filter, cursoMasivoExamen);
        Map<Long, Integer> countAlumnosSeccionCursosMasivos = alumnoCursoMasivoDAO.countBySeccionCursosMasivos(seccionesCursoMasivos, AlumnoRolExamenEstadoEnum.ACT, AlumnoRolExamenEstadoEnum.EXC);
        for (SeccionCursoMasivo seccionesCursoMasivo : seccionesCursoMasivos) {
            seccionesCursoMasivo.setAlumnosCount(countAlumnosSeccionCursosMasivos.get(seccionesCursoMasivo.getId()) != null ? countAlumnosSeccionCursosMasivos.get(seccionesCursoMasivo.getId()) : BigDecimal.ZERO.intValue());
        }
        return seccionesCursoMasivos;
    }

    @Override
    @Transactional
    public void eliminarCursosMasivos(RolExamenes rolExamenes) {
        rolExamenes = rolExamenesDAO.find(rolExamenes.getId());
        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));

        grupoEspecialService.deleteGrupoEspecial(rolExamenes);
        grupoRegularService.deleteGrupoRegular(rolExamenes);
        this.deleteCursosMasivos(rolExamenes);

        RolExamenes rolExamenesUpd = new RolExamenes();
        rolExamenesUpd.setId(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CFG_HOR);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    @Override
    @Transactional
    public void deleteCursosMasivos(RolExamenes rolExamenes) {
        List<SeccionCursoMasivo> seccionesCurMasivosExcluidas = seccionCursoMasivoDAO.allByRolExamenes(rolExamenes, SeccionRolExamenEstadoEnum.EXC);
        List<Seccion> seccionesExcluidas = seccionesCurMasivosExcluidas.stream().map(x -> x.getSeccion()).collect(Collectors.toList());
        if (!seccionesExcluidas.isEmpty()) {
            seccionExcluidoDAO.deleteBySecciones(seccionesExcluidas);
        }
        List<CursoMasivoExamen> cursosMasivos = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes);
        for (CursoMasivoExamen cursosMasivo : cursosMasivos) {
            docenteCursoMasivoDAO.deleteByCursoMasivo(cursosMasivo);
            alumnoCursoMasivoDAO.deleteByCursoMasivo(cursosMasivo);
            aulaCursoMasivoDAO.deleteByCursoMasivo(cursosMasivo);
            seccionCursoMasivoDAO.deleteByCursoMasivo(cursosMasivo);
        }
        cursoMasivoExamenDAO.deleteByRolExamenes(rolExamenes);
    }

    private void deleteHorarioByCursoMasivo(CursoMasivoExamen cursoMasivoExamen) {
        List<HorarioAula> horariosCurMas = horarioAulaDAO.allFlatByCursoMasivo(cursoMasivoExamen);
        for (HorarioAula horarioAula : horariosCurMas) {
            horarioAulaDAO.delete(horarioAula);
        }
    }

    @Override
    public List<GrupoHorasExamen> allGrupoHoraExamenByRolExamenes(RolExamenes rolExamenes) {
        List<GrupoHorasExamen> gHoras = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<FechaHoraGrupoExamen> fechasHoras = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gHoras);
        Map<Long, List<FechaHoraGrupoExamen>> mapFechaHora = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHoras);
        for (GrupoHorasExamen gHora : gHoras) {
            gHora.setFechasHorasGruposExamen(mapFechaHora.get(gHora.getId()));
        }
        return gHoras;
    }

    @Override
    @Transactional
    public List<String> cambiarAulasGrupoForCursoMasivo(CursoMasivoExamen cursoMasivosExamenForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        CursoMasivoExamen cursoMasivoBD = cursoMasivoExamenDAO.find(cursoMasivosExamenForm.getId());
        GrupoHorasExamen gpoExamDestinoForm = cursoMasivosExamenForm.getGrupoHorasExamen();

        List<AulaCursoMasivo> aulasCMDestinoForm = cursoMasivosExamenForm.getAulasCursosMasivos();
        Assert.isNotNull(aulasCMDestinoForm, "Debe indicar a que aulas va mover esta sección");
        Assert.isNotNull(aulasCMDestinoForm.isEmpty(), "Debe indicar a que aulas va mover esta sección");
        Assert.isNotNull(gpoExamDestinoForm, "Debe indicar a que grupo-horario va mover esta sección");
        Assert.isNotNull(gpoExamDestinoForm.getId(), "Debe indicar a que grupo-horario va mover esta sección");

        List<AulaCursoMasivo> aulasCMOldBD = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD);
        ListsInspector inspector = TypesUtil.analizeLists(aulasCMOldBD, aulasCMDestinoForm, "aula.id");

        List<AulaCursoMasivo> aulasCMNuevas = inspector.getNewList();
        List<AulaCursoMasivo> aulasCMDead = inspector.getDeadList();
        List<AulaCursoMasivo> aulasCMOld = inspector.getOldListDB();

        List<Aula> aulasAll = new ArrayList();
        for (AulaCursoMasivo aulaCM : aulasCMDestinoForm) {
            aulasAll.add(aulaCM.getAula());
        }

        GrupoHorasExamen gpoExamOld = cursoMasivoBD.getGrupoHorasExamen();
        GrupoHorasExamen gpoExamDestinoBD = grupoHorasExamenDAO.find(gpoExamDestinoForm.getId());
        List<FechaHoraGrupoExamen> fechasHorasGpo = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gpoExamDestinoBD);
        gpoExamDestinoBD.setFechasHorasGruposExamen(fechasHorasGpo);

        boolean mismasAulas = inspector.getNewList().isEmpty() && inspector.getDeadList().isEmpty();
        boolean mismoGpo = gpoExamOld == null ? false : (gpoExamOld.getId().compareTo(gpoExamDestinoForm.getId()) == 0);
        Assert.isFalse(mismoGpo && mismasAulas, "No ha indicado ningún cambio de aulas o grupo-horario");

        List<String> restricciones = new ArrayList();
        boolean verificado = verificarPosibleCambio(cursoMasivosExamenForm, restricciones, true);

        if (verificado) {
            saveCambioAulaGrupo(cursoMasivoBD, aulasCMNuevas, aulasCMDead, aulasAll, gpoExamDestinoBD, ds);
        }
        return restricciones;
    }

    private boolean verificarPosibleCambio(CursoMasivoExamen cursoMasivosExamenForm, List<String> restricciones, boolean crearError) {
        CursoMasivoExamen cursoMasivoBD = cursoMasivoExamenDAO.find(cursoMasivosExamenForm.getId());
        RolExamenes rolExamenes = cursoMasivoBD.getRolExamenes();
        GrupoHorasExamen gpoExamDestinoForm = cursoMasivosExamenForm.getGrupoHorasExamen();

        List<AulaCursoMasivo> aulasCMDestinoForm = cursoMasivosExamenForm.getAulasCursosMasivos();

        List<AulaCursoMasivo> aulasCursoMasivoBD = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD);
        System.out.println("aulasCursoMasivoBD.size=" + aulasCursoMasivoBD.size());
        System.out.println("aulasCMDestinoForm.size=" + aulasCMDestinoForm.size());

        ListsInspector inspector = TypesUtil.analizeLists(aulasCursoMasivoBD, aulasCMDestinoForm, "aula.id");
        List<AulaCursoMasivo> aulasCMNuevas = inspector.getNewList();

        GrupoHorasExamen gpoExamOld = cursoMasivoBD.getGrupoHorasExamen();

        boolean mismasAulas = inspector.getNewList().isEmpty() && inspector.getDeadList().isEmpty();
        boolean mismoGpo = gpoExamOld == null ? false : (gpoExamOld.getId().compareTo(gpoExamDestinoForm.getId()) == 0);
        if (crearError) {
            Assert.isFalse(mismoGpo && mismasAulas, "No ha indicado ningún cambio de aulas o grupo-horario");
        } else {
            if (mismoGpo && mismasAulas) {
                return false;
            }
        }

        List<AlumnoCursoMasivo> alumnosCursoMasivo = alumnoCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD, AlumnoRolExamenEstadoEnum.ACT);
        List<SeccionCursoMasivo> seccionesCM = seccionCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD, SeccionRolExamenEstadoEnum.ACT);
        cursoMasivoBD.setAlumnosCursosMasivos(alumnosCursoMasivo);
        cursoMasivoBD.setSeccionesCursosMasivos(seccionesCM);

        GrupoHorasExamen gpoExamDestinoBD = grupoHorasExamenDAO.find(gpoExamDestinoForm.getId());
        List<FechaHoraGrupoExamen> fechasHorasGpo = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gpoExamDestinoBD);
        gpoExamDestinoBD.setFechasHorasGruposExamen(fechasHorasGpo);

        List<SemanaExamen> semanasByRolExamen = semanaExamenDAO.allByRolExamenes(rolExamenes);
        Date fechaInicio = semanasByRolExamen.stream().min(Comparator.comparing(SemanaExamen::getFechaInicio)).map(x -> x.getFechaInicio()).get();
        Date fechaFin = semanasByRolExamen.stream().max(Comparator.comparing(SemanaExamen::getFechaFin)).map(x -> x.getFechaFin()).get();

        List<Aula> aulasAll = new ArrayList();
        List<Aula> aulasNuevas = new ArrayList();
        for (AulaCursoMasivo aulaCM : aulasCMNuevas) {
            aulasNuevas.add(aulaCM.getAula());
        }
        for (AulaCursoMasivo aulaCM : aulasCMDestinoForm) {
            aulasAll.add(aulaCM.getAula());
        }

        List<Alumno> alumnos = alumnosCursoMasivo.stream().map(x -> x.getAlumno()).collect(Collectors.toList());

        List<AlumnoCursoMasivo> alumnosCursoMasivoByFecha = alumnoCursoMasivoDAO.allByFechaEstados(gpoExamDestinoBD.getFecha(), AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoRegular> alumnosGpoRegByFecha = alumnoGrupoRegularDAO.allByFechaEstados(gpoExamDestinoBD.getFecha(), AlumnoRolExamenEstadoEnum.ACT);
        List<AlumnoGrupoEspecial> alumnosGpoEspByFecha = alumnoGrupoEspecialDAO.allByFechaEstados(gpoExamDestinoBD.getFecha(), AlumnoRolExamenEstadoEnum.ACT);
        Map<Long, List<AlumnoCursoMasivo>> mapAlumnoCursoMasivoByFecha = TypesUtil.convertListToMapList("alumno.id", alumnosCursoMasivoByFecha);
        Map<Long, List<AlumnoGrupoRegular>> mapAlumnoGpoRegularByFecha = TypesUtil.convertListToMapList("alumno.id", alumnosGpoRegByFecha);
        Map<Long, List<AlumnoGrupoEspecial>> mapAlumnoGpoEspecialByFecha = TypesUtil.convertListToMapList("alumno.id", alumnosGpoEspByFecha);

        List<GrupoHorasExamen> gposHoraExamenAll = grupoHorasExamenDAO.allByRolExamenes(rolExamenes);
        List<FechaHoraGrupoExamen> fechasHoras = fechaHoraGrupoExamenDAO.allByGrupoHorasExamen(gposHoraExamenAll);
        Map<Long, List<FechaHoraGrupoExamen>> mapFechaHora = TypesUtil.convertListToMapList("grupoHorasExamen.id", fechasHoras);
        for (GrupoHorasExamen gHora : gposHoraExamenAll) {
            gHora.setFechasHorasGruposExamen(mapFechaHora.get(gHora.getId()));
        }
        Map<Long, GrupoHorasExamen> mapGrupoHoraExamen = TypesUtil.convertListToMap("id", gposHoraExamenAll);

        boolean verificarTripleExamem
                = revisarTripleExamen(
                        gpoExamDestinoBD,
                        mapGrupoHoraExamen,
                        alumnos,
                        mapAlumnoCursoMasivoByFecha,
                        mapAlumnoGpoRegularByFecha,
                        mapAlumnoGpoEspecialByFecha,
                        restricciones);

        boolean verificarCruceAulas;
        boolean verificarCruceAlumno = true;
        boolean verificarCruceDocente = true;

        if (mismoGpo) {
            fillAulas(aulasNuevas, fechaInicio, fechaFin);
            verificarCruceAulas = revisarCrucesAula(gpoExamDestinoBD, aulasNuevas, restricciones);

        } else {
            fillAulas(aulasAll, fechaInicio, fechaFin);
            verificarCruceAulas = revisarCrucesAula(gpoExamDestinoBD, aulasAll, restricciones);

            List<AlumnoCursoMasivo> alumnosCursoMasivoByGpoExam = alumnoCursoMasivoDAO.allByGrupoHorasExamenAndEstados(gpoExamDestinoBD, AlumnoRolExamenEstadoEnum.ACT);
            List<AlumnoGrupoRegular> alumnosGpoRegByGpoExam = alumnoGrupoRegularDAO.allByGrupoHorasExamenAndEstados(gpoExamDestinoBD, AlumnoRolExamenEstadoEnum.ACT);
            List<AlumnoGrupoEspecial> alumnosGpoEspByGpoExam = alumnoGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(gpoExamDestinoBD, AlumnoRolExamenEstadoEnum.ACT);

            List<SeccionCursoMasivo> seccionesCMByGpoExam = seccionCursoMasivoDAO.allByGrupoHorasExamen(gpoExamDestinoBD, SeccionRolExamenEstadoEnum.ACT);
            List<SeccionGrupoRegular> seccionesGRByGpoExam = seccionGrupoRegularDAO.allByGrupoHorasExamenAndEstados(gpoExamDestinoBD, SeccionRolExamenEstadoEnum.ACT);
            List<SeccionGrupoEspecial> seccionesGEByGpoExam = seccionGrupoEspecialDAO.allByGrupoHorasExamenAndEstados(gpoExamDestinoBD, SeccionRolExamenEstadoEnum.ACT);

            verificarCruceAlumno = revisarCrucesAlumnos(gpoExamDestinoBD, cursoMasivoBD, alumnosCursoMasivoByGpoExam, alumnosGpoRegByGpoExam, alumnosGpoEspByGpoExam, restricciones);
            verificarCruceDocente = revisarCrucesDocentes(gpoExamDestinoBD, cursoMasivoBD, seccionesCMByGpoExam, seccionesGRByGpoExam, seccionesGEByGpoExam, restricciones);

        }

        return (verificarTripleExamem && verificarCruceAulas && verificarCruceAlumno && verificarCruceDocente);
    }

    private void saveCambioAulaGrupo(
            CursoMasivoExamen cursoMasivo,
            List<AulaCursoMasivo> aulasCMNuevas,
            List<AulaCursoMasivo> aulasCMDead,
            List<Aula> aulasDestino,
            GrupoHorasExamen gpoExamDestino,
            DataSessionPivot ds) {

        List<FechaHoraGrupoExamen> fechasHorasGpo = gpoExamDestino.getFechasHorasGruposExamen();

        cursoMasivo.setGrupoHorasExamen(gpoExamDestino);
        cursoMasivoExamenDAO.update(cursoMasivo);

        for (AulaCursoMasivo aulaCM : aulasCMNuevas) {
            aulaCM.setCursoMasivoExamen(cursoMasivo);
            aulaCM.setUserRegistro(ds.getUsuario());
            aulaCM.setFechaRegistro(new Date());
            aulaCursoMasivoDAO.save(aulaCM);
        }

        for (AulaCursoMasivo aulaCM : aulasCMDead) {
            aulaCursoMasivoDAO.delete(aulaCM);
        }

        horarioAulaDAO.deleteByCursoMasivo(cursoMasivo);

        List<HorarioAula> horarios = new ArrayList();
        for (Aula aula : aulasDestino) {
            for (FechaHoraGrupoExamen fechaGpoExam : fechasHorasGpo) {
                HorarioAula ha = new HorarioAula(fechaGpoExam, aula);
                ha.setCursoMasivoExamen(cursoMasivo);
                ha.setRolExamenes(cursoMasivo.getRolExamenes());
                horarios.add(ha);
            }
        }

        horarioAulaDAO.saveList(horarios);

    }

    private void fillAulas(List<Aula> aulas, Date fechaInicio, Date fechaFin) {
        List<HorarioAula> horariosAulas = horarioAulaDAO.allByRangoFechaAulas(fechaInicio, fechaFin, aulas);
        Map<Long, List<HorarioAula>> mapHorarioAula = TypesUtil.convertListToMapList("aula.id", horariosAulas);
        for (Aula aula : aulas) {
            List<HorarioAula> horarioAula = TypesUtil.getListNotNull(mapHorarioAula.get(aula.getId()));
            aula.setHorariosAula(horarioAula);
        }

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

    private boolean revisarCrucesDocentes(
            GrupoHorasExamen gpoExamDestino,
            CursoMasivoExamen cursoMasivo,
            List<SeccionCursoMasivo> seccionesCMByGpoExam,
            List<SeccionGrupoRegular> seccionesGRByGpoExam,
            List<SeccionGrupoEspecial> seccionesGEByGpoExam,
            List<String> restricciones) {

        List<FechaHoraGrupoExamen> fechasHorasGpo = gpoExamDestino.getFechasHorasGruposExamen();
        Date fechaDestino = fechasHorasGpo.get(0).getFecha();
        Hora horaIni = fechasHorasGpo.stream().map(x -> x.getHora()).min(Comparator.comparing(Hora::getCodigo)).get();
        Hora horaFin = fechasHorasGpo.stream().map(x -> x.getHora()).max(Comparator.comparing(Hora::getCodigo)).get();

        boolean existeCruce = false;

        List<Docente> docentes = new ArrayList();
        List<SeccionCursoMasivo> seccionesCM = cursoMasivo.getSeccionesCursosMasivos();
        for (SeccionCursoMasivo seccionCurMasivo : seccionesCM) {
            if (seccionCurMasivo.getDocente() != null) {
                docentes.add(seccionCurMasivo.getDocente());
            }
        }

        for (Docente docente : docentes) {
            for (SeccionCursoMasivo seccionCM : seccionesCMByGpoExam) {
                Docente docenteCM = seccionCM.getDocente();
                if (docente.getId().compareTo(docenteCM.getId()) == 0) {
                    existeCruce = true;
                    Curso curso = seccionCM.getCursoMasivoExamen().getCurso();

                    StringBuilder msg = new StringBuilder("El docente ").append(docente.getCodigo()).append(" ya tiene programado un examen de curso masivo [");
                    msg.append(curso.getCodigo()).append("] el ");
                    msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es"));
                    msg.append(" de ").append(horaIni.getDescripcion());
                    msg.append(" a ").append(horaFin.getDescripcion());

                    msg.append(". ");
                    restricciones.add(msg.toString());
                }
            }

            for (SeccionGrupoRegular seccionGpoReg : seccionesGRByGpoExam) {
                Docente docenteGR = seccionGpoReg.getDocente();
                if (docente.getId().compareTo(docenteGR.getId()) == 0) {
                    existeCruce = true;
                    Seccion seccion = seccionGpoReg.getSeccion();
                    Curso curso = seccion.getGrupoSeccion().getCurso();

                    StringBuilder msg = new StringBuilder("El docente ").append(docente.getCodigo()).append(" ya tiene programado un examen de grupo-regular ");
                    msg.append("[sección: ").append(seccion.getCodigo2()).append(", curso:").append(curso.getCodigo()).append("] el ");
                    msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es"));
                    msg.append(" de ").append(horaIni.getDescripcion());
                    msg.append(" a ").append(horaFin.getDescripcion());

                    msg.append(". ");
                    restricciones.add(msg.toString());
                }
            }

            for (SeccionGrupoEspecial seccionGE : seccionesGEByGpoExam) {
                Docente docenteGE = seccionGE.getDocente();
                if (docente.getId().compareTo(docenteGE.getId()) == 0) {
                    if (seccionGE.getId().compareTo(cursoMasivo.getId()) == 0) {
                        continue;
                    }

                    Seccion seccion = seccionGE.getSeccion();
                    Curso curso = seccion.getGrupoSeccion().getCurso();
                    existeCruce = true;

                    StringBuilder msg = new StringBuilder("El docente ").append(docente.getCodigo()).append(" ya tiene programado un examen de grupo-especial ");
                    msg.append("[sección: ").append(seccion.getCodigo2()).append(", curso:").append(curso.getCodigo()).append("] el ");
                    msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es"));
                    msg.append(" de ").append(horaIni.getDescripcion());
                    msg.append(" a ").append(horaFin.getDescripcion());

                    msg.append(". ");
                    restricciones.add(msg.toString());
                }
            }
        }

        return !existeCruce;

    }

    private boolean revisarCrucesAlumnos(
            GrupoHorasExamen gpoExamDestino,
            CursoMasivoExamen cursoMasivo,
            List<AlumnoCursoMasivo> alumnosCursoMasivoByGpoExam,
            List<AlumnoGrupoRegular> alumnosGpoRegByGpoExam,
            List<AlumnoGrupoEspecial> alumnosGpoEspByGpoExam,
            List<String> restricciones) {

        List<FechaHoraGrupoExamen> fechasHorasGpo = gpoExamDestino.getFechasHorasGruposExamen();
        Date fechaDestino = fechasHorasGpo.get(0).getFecha();
        Hora horaIni = fechasHorasGpo.stream().map(x -> x.getHora()).min(Comparator.comparing(Hora::getCodigo)).get();
        Hora horaFin = fechasHorasGpo.stream().map(x -> x.getHora()).max(Comparator.comparing(Hora::getCodigo)).get();

        Map<Long, List<AlumnoCursoMasivo>> mapAlumnoCM = TypesUtil.convertListToMapList("alumno.id", alumnosCursoMasivoByGpoExam);
        Map<Long, List<AlumnoGrupoRegular>> mapAlumnoGR = TypesUtil.convertListToMapList("alumno.id", alumnosGpoRegByGpoExam);
        Map<Long, List<AlumnoGrupoEspecial>> mapAlumnoGE = TypesUtil.convertListToMapList("alumno.id", alumnosGpoEspByGpoExam);

        boolean existeCruce = false;

        List<AlumnoCursoMasivo> alumnosCursoMasivo = cursoMasivo.getAlumnosCursosMasivos();
        for (AlumnoCursoMasivo alumnoCursoMasivo : alumnosCursoMasivo) {
            Alumno alumno = alumnoCursoMasivo.getAlumno();
            List<AlumnoCursoMasivo> aluCursosMasivos = TypesUtil.getListNotNull(mapAlumnoCM.get(alumno.getId()));
            for (AlumnoCursoMasivo alumnoCM : aluCursosMasivos) {
                CursoMasivoExamen seccGpoEspAlu = alumnoCM.getCursoMasivoExamen();
                if (seccGpoEspAlu.getId().compareTo(cursoMasivo.getId()) == 0) {
                    continue;
                }
                existeCruce = true;
                Curso curso = alumnoCM.getCursoMasivoExamen().getCurso();

                StringBuilder msg = new StringBuilder("El alumno ").append(alumno.getCodigo()).append(" ya tiene programado un examen de curso masivo [");
                msg.append(curso.getCodigo()).append("] el ");
                msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es"));
                msg.append(" de ").append(horaIni.getDescripcion());
                msg.append(" a ").append(horaFin.getDescripcion());

                msg.append(". ");
                restricciones.add(msg.toString());
            }

            List<AlumnoGrupoRegular> aluGpoRegulares = TypesUtil.getListNotNull(mapAlumnoGR.get(alumno.getId()));
            for (AlumnoGrupoRegular alumnoGR : aluGpoRegulares) {
                existeCruce = true;
                Seccion seccion = alumnoGR.getSeccionGrupoRegular().getSeccion();
                Curso curso = seccion.getGrupoSeccion().getCurso();

                StringBuilder msg = new StringBuilder("El alumno ").append(alumno.getCodigo()).append(" ya tiene programado un examen de grupo-regular ");
                msg.append("[sección: ").append(seccion.getCodigo2()).append(", curso:").append(curso.getCodigo()).append("] el ");
                msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es"));
                msg.append(" de ").append(horaIni.getDescripcion());
                msg.append(" a ").append(horaFin.getDescripcion());

                msg.append(". ");
                restricciones.add(msg.toString());
            }

            List<AlumnoGrupoEspecial> aluGpoEspeciales = TypesUtil.getListNotNull(mapAlumnoGE.get(alumno.getId()));
            for (AlumnoGrupoEspecial alumnoGpoEsp : aluGpoEspeciales) {
                Seccion seccion = alumnoGpoEsp.getSeccionGrupoEspecial().getSeccion();
                Curso curso = seccion.getGrupoSeccion().getCurso();
                existeCruce = true;

                StringBuilder msg = new StringBuilder("El alumno ").append(alumno.getCodigo()).append(" ya tiene programado un examen de grupo-especial ");
                msg.append("[sección: ").append(seccion.getCodigo2()).append(", curso:").append(curso.getCodigo()).append("] el ");
                msg.append(TypesUtil.getStringDate(fechaDestino, "EEEE dd 'de' MMMM", "es"));
                msg.append(" de ").append(horaIni.getDescripcion());
                msg.append(" a ").append(horaFin.getDescripcion());

                msg.append(". ");
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

    @Override
    public List<Aula> allAulasVerificadasByModulo(Aula modulo) {
        Oficina oera = oficinaDAO.findByCode(OERA.name());
        List<Aula> aulas = aulaDAO.allByOficinaModulo(oera, modulo);
        CursoMasivoExamen cursoMasivo = modulo.getCursoMasivo();
        if (cursoMasivo == null) {
            return aulas;
        }

        for (Aula aula : aulas) {
            List<AulaCursoMasivo> aulasCursoMasivo = new ArrayList();
            AulaCursoMasivo aulaCM = new AulaCursoMasivo();
            aulaCM.setAula(aula);
            aulaCM.setCursoMasivoExamen(cursoMasivo);
            aulasCursoMasivo.add(aulaCM);
            cursoMasivo.setAulasCursosMasivos(aulasCursoMasivo);

            List<String> restricciones = new ArrayList();
            boolean ok = verificarPosibleCambio(cursoMasivo, restricciones, false);
            aula.setTieneCruces(!ok);
            aula.setObservaciones(restricciones);
        }

        return aulas;
    }

}
