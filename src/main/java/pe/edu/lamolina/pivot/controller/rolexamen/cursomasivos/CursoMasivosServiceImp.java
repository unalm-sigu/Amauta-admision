package pe.edu.lamolina.pivot.controller.rolexamen.cursomasivos;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.enums.SituacionRolExamenesEnum;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.AulaCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.model.rolexamen.DocenteCursoMasivo;
import pe.edu.lamolina.model.rolexamen.FechaHoraGrupoExamen;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionCursoMasivo;
import pe.edu.lamolina.model.rolexamen.SeccionExcluido;
import pe.edu.lamolina.pivot.controller.rolexamen.gruporegular.GrupoRegularConnector;
import pe.edu.lamolina.pivot.controller.rolexamen.util.RolExamenesLogger;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.dao.general.AulaDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoEspecialDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoGrupoRegularDAO;
import pe.edu.lamolina.pivot.dao.rolexamen.AulaCursoMasivoDAO;
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
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CursoMasivosServiceImp implements CursoMasivosService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Override
    public List<RolExamenes> allRolExamenesByCicloActivo(CicloAcademico cicloAcademico) {
        return cursoMasivoExamenDAO.allRolExamenesByCicloActivo(cicloAcademico);
    }

    @Override
    @Transactional
    public void save(CursoMasivoExamen cursoMasivosExamen, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        RolExamenes rolExamenes = rolExamenesDAO.find(cursoMasivosExamen.getRolExamenes().getId());
        Assert.isTrue(rolExamenes.isSituacionConfigurarHorario() || rolExamenes.isSituacionConfigurarCursoMasivo(), "Debe configurar los horarios de examenes previamente.");
        List<String> validationsHorariosExamen = this.validarHorariosExamen(rolExamenes);
        Assert.isTrue(validationsHorariosExamen.isEmpty(), String.join("\n", validationsHorariosExamen));

        cursoMasivosExamen.setUserRegistro(ds.getUsuario());
        cursoMasivosExamen.setFechaRegistro(new Date());
        cursoMasivosExamen.setEstadoEnum(EstadoCursoMasivoEnum.ACT);
        cursoMasivosExamen.setAulas(0);
        cursoMasivosExamen.setCapacidadAulas(0);
        cursoMasivosExamen.setAlumnos(0);
        cursoMasivoExamenDAO.save(cursoMasivosExamen);

        List<Seccion> secciones = seccionDAO.allByCicloAndCurso(cicloAcademico, cursoMasivosExamen.getCurso());

        int alus = 0;
        List<DocenteSeccion> docentesPrincipales = docenteSeccionDAO.allPrincipalesBySecciones(secciones);
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
            docenteCursoMasivoDAO.save(docenteCursoMasivo);

            SeccionCursoMasivo seccionCursoMasivo = new SeccionCursoMasivo();
            seccionCursoMasivo.setCursoMasivoExamen(cursoMasivosExamen);
            seccionCursoMasivo.setEstadoEnum(SeccionRolExamenEstadoEnum.ACT);
            seccionCursoMasivo.setSeccion(seccion);
            seccionCursoMasivo.setFechaRegistro(new Date());
            seccionCursoMasivo.setUserRegistro(ds.getUsuario());

            seccionCursoMasivoDAO.save(seccionCursoMasivo);
            List<MatriculaSeccion> matriculadosPorSeccion = matriculaSeccionDAO.allMatriculadosBySeccion(seccion);
            alus += matriculadosPorSeccion.size();
            for (MatriculaSeccion matriculaSeccion : matriculadosPorSeccion) {
                Alumno alumno = matriculaSeccion.getMatriculaResumen().getAlumno();
                AlumnoCursoMasivo alumnoCursoMasivo = new AlumnoCursoMasivo();
                alumnoCursoMasivo.setAlumno(alumno);
                alumnoCursoMasivo.setCursoMasivoExamen(cursoMasivosExamen);
                alumnoCursoMasivo.setEstadoEnum(AlumnoRolExamenEstadoEnum.ACT);
                alumnoCursoMasivo.setFechaRegistro(new Date());
                alumnoCursoMasivo.setUserRegistro(ds.getUsuario());
                alumnoCursoMasivoDAO.save(alumnoCursoMasivo);
            }
        }

        cursoMasivosExamen.setAlumnos(alus);
        cursoMasivosExamen.setSecciones(secciones.size());
        cursoMasivoExamenDAO.update(cursoMasivosExamen);

        RolExamenes rolExamenesUpd = new RolExamenes(rolExamenes.getId());
        rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_MAS);
        rolExamenesDAO.updateSituacion(rolExamenesUpd);
    }

    public List<String> validarHorariosExamen(RolExamenes rolExamenes) {
        List<String> validations = new ArrayList<>();
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

            cursoMasivo.setDocentesCount(mapDocentesCursosMasivos.get(cursoMasivo.getId()));
            cursoMasivo.setAlumnosCount(mapAlumnosCursosMasivos.get(cursoMasivo.getId()));
            cursoMasivo.setSeccionesCount(mapSeccionesCursosMasivos.get(cursoMasivo.getId()));

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
    public Oficina findOficinaOera() {
        return oficinaDAO.findByCode("OERA");
    }

    @Override
    public List<Aula> allPabellonesByOficina(Oficina oficinaOERA) {
        return aulaDAO.allPabellonesByOficina(oficinaOERA);
    }

    @Override
    @Transactional
    public void eliminarCursoMasivoExamen(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds) {
        CursoMasivoExamen cursoMasivoExamenBD = cursoMasivoExamenDAO.find(cursoMasivoExamen.getId());
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
    public List<Aula> allAulasByOficinaModulo(Oficina oficinaOERA, Aula modulo) {
        return aulaDAO.allByOficinaModulo(oficinaOERA, modulo);
    }

    @Override
    @Transactional
    public void saveAula(CursoMasivoExamen cursoMasivo, CicloAcademico cicloAcademico, DataSessionPivot ds) {

        CursoMasivoExamen cursoMasivoBD = cursoMasivoExamenDAO.find(cursoMasivo.getId());

        List<AulaCursoMasivo> aulasCurso = cursoMasivo.getAulasCursosMasivos();
        List<AulaCursoMasivo> aulasCursoBD = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoBD);
        ListsInspector inspector = TypesUtil.analizeLists(aulasCursoBD, aulasCurso, "aula.id");

        int total1 = 0;
        for (Object obj : inspector.getNewList()) {
            AulaCursoMasivo aulaCurso = (AulaCursoMasivo) obj;
            aulaCurso.setCursoMasivoExamen(cursoMasivoBD);
            aulaCurso.setAula(aulaCurso.getAula());
            aulaCurso.setUserRegistro(ds.getUsuario());
            aulaCurso.setFechaRegistro(new Date());
            aulaCursoMasivoDAO.save(aulaCurso);
            total1 += aulaCurso.getAula().getCapacidadAula();
        }
        int total2 = 0;
        for (Object obj : inspector.getDeadList()) {
            AulaCursoMasivo aulaCurso = (AulaCursoMasivo) obj;
            aulaCursoMasivoDAO.delete(aulaCurso);
            total1 += aulaCurso.getAula().getCapacidadAula();
        }
        int total = total1 - total2;
        cursoMasivoBD.setCapacidadAulas(total);
        cursoMasivoBD.setAulas(aulasCurso.size());
        cursoMasivoExamenDAO.update(cursoMasivoBD);
    }

    @Override
    @Transactional
    public void excluirCursoMasivo(CursoMasivoExamen cursoMasivoExamen, DataSessionPivot ds) {
        CursoMasivoExamen cursoMasivoExamenUpd = new CursoMasivoExamen();
        cursoMasivoExamen = cursoMasivoExamenDAO.find(cursoMasivoExamen.getId());
        cursoMasivoExamenUpd.setId(cursoMasivoExamen.getId());
        cursoMasivoExamenUpd.setUsuarioExclusion(ds.getUsuario());
        cursoMasivoExamenUpd.setFechaExclusion(ds.getFechaAccionAudit());
        cursoMasivoExamenDAO.updateEstadoExcluido(cursoMasivoExamen);

        List<SeccionCursoMasivo> seccionesCursoMasivo = seccionCursoMasivoDAO.allSeccionByCursoMasivo(cursoMasivoExamen);
        for (SeccionCursoMasivo seccionCursoMasivo : seccionesCursoMasivo) {
            SeccionExcluido seccionExcluido = new SeccionExcluido();
            seccionExcluido.setEstadoEnum(EstadoEnum.ACT);
            seccionExcluido.setFechaRegistro(ds.getFechaAccionAudit());
            seccionExcluido.setRolExamenes(cursoMasivoExamen.getRolExamenes());
            seccionExcluido.setSeccion(seccionCursoMasivo.getSeccion());
            seccionExcluido.setUserRegistro(ds.getUsuario());
            seccionExcluidoDAO.save(seccionExcluido);
            this.excluirCursoMasivo(cursoMasivoExamen, ds);
        }
        throw new PhobosException("no pasaras");
    }

    @Override
    @Transactional(readOnly = false)
    public void excluirSeccionCursoMasivo(SeccionCursoMasivo seccionCursoMasivo, DataSessionPivot ds) {
        seccionCursoMasivo = seccionCursoMasivoDAO.find(seccionCursoMasivo.getId());

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
        seccionExcluidoDAO.save(seccionExcluido);
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
    public void excluirAlumnoCursoMasivo(AlumnoCursoMasivo alumnoCursoMasivo, DataSessionPivot ds) {
        AlumnoCursoMasivo alumnoCursoMasivoUpd = new AlumnoCursoMasivo(alumnoCursoMasivo.getId());
        alumnoCursoMasivoDAO.updateEstadoExclusion(alumnoCursoMasivo);
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
        //   SemanaExamen semanaExamen = cursoMasivoExamen.getGrupoHorasExamen().getSemanaExamen();
        RolExamenes rolExamenes = rolExamenesDAO.find(cursoMasivoExamen.getRolExamenes().getId());

        Assert.isFalse(this.rolExamenesLogger.isRunning(), String.format("El proceso calculo de %s se esta ejecutando, espere que termine.",
                rolExamenesLogger.getTipoEnum() != null ? rolExamenesLogger.getTipoEnum().getValue() : ""));
        Assert.isTrue(rolExamenes.isSituacionConfiguraGrupoRegular(), "Debe configurar los grupos regulares previamente.");

        this.rolExamenesLogger.iniciarCursoMasivo();

        GrupoHorasExamen grupoHorasExamen = cursoMasivoExamen.getGrupoHorasExamen();

        List<AlumnoCursoMasivo> alumnosCursoMasivo = alumnoCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, AlumnoRolExamenEstadoEnum.ACT);
        List<AulaCursoMasivo> aulasCursoMasivo = aulaCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen);
        List<DocenteCursoMasivo> docenteCursoMasivo = docenteCursoMasivoDAO.allByCursoMasivo(cursoMasivoExamen, DocenteRolExamenEstadoEnum.ACT);

        cursoMasivoExamen.setAlumnosCursosMasivos(alumnosCursoMasivo);
        cursoMasivoExamen.setAulasCursosMasivos(aulasCursoMasivo);
        cursoMasivoExamen.setDocentesCursosMasivos(docenteCursoMasivo);

        List<Alumno> alumnos = cursoMasivoExamen.getAlumnosCursosMasivos().stream().map(x -> x.getAlumno()).collect(Collectors.toList());
        List<Aula> aulas = cursoMasivoExamen.getAulasCursosMasivos().stream().map(x -> x.getAula()).collect(Collectors.toList());
        List<Docente> docentes = cursoMasivoExamen.getDocentesCursosMasivos().stream().map(x -> x.getDocente()).collect(Collectors.toList());

        //validar cruce horario docentes !!!!!!!!!!!!!!!!!!!!!!!!!!!!
        boolean validacionCursosMasivos = this.validateCruceCursosMasivos(cursoMasivoExamen, alumnos, docentes, aulas);
        boolean validacionGruposRegulares = grupoRegularConnector.validarGrupoRegular(grupoHorasExamen, alumnos, docentes, aulas);
        boolean validacionSeccionesEspeciales = grupoRegularConnector.validarGrupoEspecial(cursoMasivoExamen.getRolExamenes(), cursoMasivoExamen.getGrupoHorasExamen(), docentes, aulas, alumnos);

        if (validacionCursosMasivos && validacionGruposRegulares && validacionSeccionesEspeciales) {
            CursoMasivoExamen cursoMasivoUpd = new CursoMasivoExamen();
            cursoMasivoUpd.setId(cursoMasivoExamen.getId());
            cursoMasivoUpd.setGrupoHorasExamen(grupoHorasExamen);
            cursoMasivoExamenDAO.updateFechaExamen(cursoMasivoExamen);

            List<CursoMasivoExamen> cursosMasivosByRolExamen = cursoMasivoExamenDAO.allByRolExamenes(rolExamenes, EstadoCursoMasivoEnum.ACT);
            CursoMasivoExamen cursoMasivoSinHorario = cursosMasivosByRolExamen.stream()
                    .filter(x -> x.getGrupoHorasExamen() == null)
                    .filter(x -> !x.equals(cursoMasivoExamen))
                    .findFirst().orElse(null);
            if (cursoMasivoSinHorario == null) {
                RolExamenes rolExamenesUpd = new RolExamenes();
                rolExamenesUpd.setId(cursoMasivoExamen.getRolExamenes().getId());
                rolExamenesUpd.setSituacionEnum(SituacionRolExamenesEnum.CONF_HOR_MAS);
                rolExamenesDAO.updateSituacion(rolExamenesUpd);
            }
        } else {
            throw new PhobosException("Conflictos encontrados.");
        }
    }

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
        return grupoRegularConnector.validarCursosMasivos(cursosMasivosOthers,
                docentes, aulas, alumnos);
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
        return seccionCursoMasivoDAO.allByDynatableAndCursoMasivo(filter, cursoMasivoExamen);
    }

}
