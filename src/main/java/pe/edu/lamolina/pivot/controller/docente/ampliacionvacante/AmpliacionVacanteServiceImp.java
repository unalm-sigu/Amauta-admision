package pe.edu.lamolina.pivot.controller.docente.ampliacionvacante;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.academico.MatriculaSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.CursoCurriculaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import static pe.edu.lamolina.model.enums.EstadoEnum.ACT;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.EventoAcademicoEnum;
import pe.edu.lamolina.model.enums.TipoAmpliacionEnum;
import pe.edu.lamolina.model.enums.TipoSeccionEnum;
import pe.edu.lamolina.model.matricula.AlumnoCursoCurricula;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCursoCurriculaDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.GrupoSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaSeccionDAO;
import pe.edu.lamolina.pivot.dao.academico.PlanCalificacionCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.SeccionDAO;
import pe.edu.lamolina.pivot.zelper.misc.MapUtil;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class AmpliacionVacanteServiceImp implements AmpliacionVacanteService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AmpliacionVacanteRestService ampliacionVacanteRestService;

    @Autowired
    GrupoSeccionDAO grupoSeccionDAO;

    @Autowired
    DocenteSeccionDAO docenteSeccionDAO;

    @Autowired
    SeccionDAO seccionDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    PlanCalificacionCursoDAO planCalificacionCursoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaSeccionDAO matriculaSeccionDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Autowired
    AlumnoCursoCurriculaDAO alumnoCursoCurriculaDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Override
    public List<GrupoSeccion> allGrupoByDocente(Docente docente, CicloAcademico ciclo, DataSessionPivot ds) {

        List<DocenteSeccion> docentesSecciones = docenteSeccionDAO.allByDocente(docente, ciclo);

        List<Long> idsGpoSecc = docentesSecciones.stream()
                .map(x -> x.getSeccion().getGrupoSeccion().getId())
                .collect(Collectors.toList());

        List<GrupoSeccion> gruposSeccion = grupoSeccionDAO.allByFilter(idsGpoSecc, ciclo, null, EstadoEnum.ACT);
        List<DocenteSeccion> responsablesgrupo = docenteSeccionDAO.allResponsablesByGpoSecciones(gruposSeccion, ciclo);

        Map<Long, DocenteSeccion> mapResponsables = MapUtil.storeItems("seccion.grupoSeccion.id", responsablesgrupo);

        List<Seccion> secciones = seccionDAO.allActivosByGposSeccion(gruposSeccion);
        List<DocenteSeccion> responsablesseccion = docenteSeccionDAO.allResponsableBySeccionCiclo(secciones, ciclo);
        Map<Long, DocenteSeccion> mapResponsableSeccion = TypesUtil.convertListToMap("seccion.id", responsablesseccion);

        Map<Long, Seccion> grupoSeccionTcurMap = secciones
                .stream()
                .filter(y -> y.getTipoSeccionEnum() == TipoSeccionEnum.TCUR)
                .collect(Collectors.toMap(x -> x.getGrupoSeccion().getId(), x -> x, (f, s) -> s));

        for (Seccion seccion : secciones) {
            DocenteSeccion docPrincipal = mapResponsableSeccion.get(seccion.getId());
            seccion.setDocentePrincipal(docPrincipal != null ? docPrincipal.getDocente() : null);
            seccion.setDocentePrincipalLogeado(ds.getDocente().equals(seccion.getDocentePrincipal()));

            if (seccion.getIsTipoSeccionPCUR()) {
                Seccion seccionSuper = seccion.getSeccionSuperior();
                if (seccionSuper == null) {
                    seccionSuper = grupoSeccionTcurMap.get(seccion.getGrupoSeccion().getId());
                    seccion.setSeccionSuperior(seccionSuper);
                }
                seccion.setDocentePrincipaTcurLogeado(ds.getDocente().equals(seccionSuper.getDocentePrincipal()));
            }
        }
        Map<Long, List<Seccion>> mapSeccionesByGrupoSeccion = TypesUtil.convertListToMapList("grupoSeccion.id", secciones);

        for (GrupoSeccion grupoSeccion : gruposSeccion) {
            grupoSeccion.setSecciones(new ArrayList());
            DocenteSeccion responsable = mapResponsables.get(grupoSeccion.getId());
            grupoSeccion.setDocenteResponsable(responsable.getDocente());
            List<Seccion> seccionesByGrupo = mapSeccionesByGrupoSeccion.get(grupoSeccion.getId());
            grupoSeccion.setSecciones(seccionesByGrupo);
        }

        return gruposSeccion;
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre, CicloAcademico cicloAcademico, Seccion seccionForm) {

        Seccion seccion = seccionDAO.find(seccionForm);
        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();

        List<Alumno> alumnos = alumnoDAO.allByName(nombre);

        List<MatriculaResumen> matriculas = matriculaResumenDAO.allByAlumnosCiclo(alumnos, cicloAcademico);
        Map<Long, MatriculaResumen> matriculasMap = TypesUtil.convertListToMap("alumno.id", matriculas);

        List<MatriculaCurso> matriculaCursos = matriculaCursoDAO.allByMatriculaResumenCurso(matriculas, curso);
        Map<Long, MatriculaCurso> matriculaCursosMap = TypesUtil.convertListToMap("matriculaResumen.id", matriculaCursos);

        List<MatriculaSeccion> matriculaSecciones = matriculaSeccionDAO.allByMatriculaMatSeccion(matriculas, seccion);
        Map<Long, MatriculaSeccion> matriculaSeccionesMap = TypesUtil.convertListToMap("matriculaResumen.id", matriculaSecciones);

        List<AlumnoCursoCurricula> alumnosCursoCurricula = alumnoCursoCurriculaDAO.allByAlumnosCurso(alumnos, curso);
        Map<Long, AlumnoCursoCurricula> alumnosCursoCurriculaMap = TypesUtil.convertListToMap("alumno.id", alumnosCursoCurricula);

        for (Alumno alumno : alumnos) {

            MatriculaResumen matriculaResumen = matriculasMap.get(alumno.getId());
            alumno.setMotivoMatriculable("No cuenta con registro en matricula para el presente ciclo académico");

            alumno.setSituacion("0");
            if (matriculaResumen == null) {
                continue;
            }

            if (!Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matriculaResumen.getEstadoEnum())) {
                alumno.setMotivoMatriculable("No matriculable");
                continue;
            }

            MatriculaCurso matriculaCurso = matriculaCursosMap.get(matriculaResumen.getId());

            if (matriculaCurso != null && matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT) {
                alumno.setMotivoMatriculable("Ya se matriculó");
                continue;
            }

            MatriculaSeccion matriculaSeccion = matriculaSeccionesMap.get(matriculaResumen.getId());
            if (matriculaSeccion != null) {
                alumno.setMotivoMatriculable("Ya se matriculó");
                continue;
            }

            AlumnoCursoCurricula alumnoCursoCurricula = alumnosCursoCurriculaMap.get(alumno.getId());

            if (alumnoCursoCurricula == null) {
                alumno.setMotivoMatriculable("Curso no disponible en su curricula en este momento.");
                continue;
            }

            if (!Arrays.asList(
                    CursoCurriculaEstadoEnum.HAB,
                    CursoCurriculaEstadoEnum.SIM,
                    CursoCurriculaEstadoEnum.SUL).contains(alumnoCursoCurricula.getEstadoEnum())) {
                alumno.setMotivoMatriculable("El curso no cumple requisito en su curricula.");
                continue;
            }
            alumno.setSituacion("1");
        }

        return alumnos;
    }

    @Override
    public void solicitarAmpliacion(Seccion seccionPCUR, AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        Docente docenteLogeado = ds.getDocente();
        logger.debug("seccion {} ", seccionPCUR.getId());

        GrupoSeccion grupoSeccion = seccionPCUR.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();
        Assert.isTrue(curso.isTipoCursoTEOPRA(), "El curso debe ser teórico practico");
        Assert.isTrue(seccionPCUR.isTipoSeccionPCUR(), "La sección debe ser practica");

        Map<Long, DocenteSeccion> mapDocentePrincipalBySeccion = this.getMapDocentesSeccionGroupBySeccion(grupoSeccion);
        DocenteSeccion docenteSeccionCurrent = mapDocentePrincipalBySeccion.get(seccionPCUR.getId());
        seccionPCUR.setDocentePrincipal(docenteSeccionCurrent.getDocente());

        Seccion seccionTCUR = null;
        boolean isDocentePrincipalTcurLogged = false;

        List<Seccion> seccionesByGrupo = seccionDAO.allByGpoSeccion(grupoSeccion);
        //Seccion tcur info - ini 
        seccionTCUR = seccionesByGrupo.stream().filter(x -> x.isTipoSeccionTCUR()).findFirst().orElse(null);
        Assert.isTrue(seccionTCUR != null, "La sección teoria no configurada.");
        DocenteSeccion docenteSeccionTCUR = mapDocentePrincipalBySeccion.get(seccionTCUR.getId());
        seccionTCUR.setDocentePrincipal(docenteSeccionTCUR.getDocente());
        isDocentePrincipalTcurLogged = docenteLogeado.equals(seccionTCUR.getDocentePrincipal());
        //Seccion tcur info - fin

        if (!isDocentePrincipalTcurLogged) {
            Assert.isTrue(docenteLogeado.equals(seccionPCUR.getDocentePrincipal()), "Error, No es el docente principal.");
        }

        List<Alumno> alumnos = alumnoDAO.allByAlumnos(ampliacionVacanteForm.getAlumnos());
        for (Alumno alumno : alumnos) {
            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);

            Assert.isTrue(matriculaResumen != null,
                    String.format("alumno %S no es matriculable", alumno.getPersona().getApellidosNombres()));
            Assert.isTrue(Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matriculaResumen.getEstadoEnum()),
                    String.format("alumno %S no es matriculable", alumno.getPersona().getApellidosNombres()));

            MatriculaCurso matriculaCurso = matriculaCursoDAO.findByMatriculaCurso(matriculaResumen, curso);

            if (matriculaCurso == null) {
                matriculaCurso = new MatriculaCurso(curso, matriculaResumen, EstadoMatriculaEnum.SOL);
                //  matriculaCursoDAO.save(matriculaCurso);
            } else {
                Assert.isFalse(matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT,
                        String.format("alumno %S ya se matriculo", alumno.getPersona().getApellidosNombres()));
                Assert.isFalse(matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.SOL,
                        String.format("alumno %S ya solicito matricularse", alumno.getPersona().getApellidosNombres()));
                Assert.isTrue(Arrays.asList(EstadoMatriculaEnum.RET, EstadoMatriculaEnum.NVAC).contains(matriculaCurso.getEstadoEnum()),
                        String.format("alumno %S no es matriculable", alumno.getPersona().getApellidosNombres()));

                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.SOL);
                //   matriculaCursoDAO.update(matriculaCurso);
            }
            String tipoAmpliacion = null;
            if (!seccionTCUR.getDocentePrincipal().equals(seccionPCUR.getDocentePrincipal())) {
                JsonResponse response = ampliacionVacanteRestService.solicitarAmpliacionVacante(seccionPCUR, matriculaResumen.getAlumno(), isDocentePrincipalTcurLogged, ds);
                tipoAmpliacion = (String) response.getData();
            } else {
                ampliacionVacanteRestService.matricularAmpliacionVacante(seccionPCUR, matriculaResumen.getAlumno(), ds);
            }

            if (matriculaCurso.getId() == null) {
                matriculaCurso = new MatriculaCurso(curso, matriculaResumen, EstadoMatriculaEnum.SOL);
                matriculaCursoDAO.save(matriculaCurso);
            } else {
                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.SOL);
                matriculaCursoDAO.update(matriculaCurso);
            }

            MatriculaSeccion matriculaSeccionPCUR = matriculaSeccionDAO.findByMatriculaMatSeccion(matriculaResumen, seccionPCUR);
            Assert.isTrue(matriculaSeccionPCUR == null, String.format("alumno %S ya se matriculo", alumno.getPersona().getApellidosNombres()));
            matriculaSeccionPCUR = new MatriculaSeccion(curso, matriculaResumen, seccionPCUR, EstadoMatriculaEnum.SOL, ds.getUsuario(), ds.getFechaAccionAudit());
            matriculaSeccionPCUR.setEsAmpliacionVacante(Boolean.TRUE);
            if (isDocentePrincipalTcurLogged && !seccionPCUR.getDocentePrincipal().equals(seccionTCUR.getDocentePrincipal())) {
                matriculaSeccionPCUR.setEnSolicitud(Boolean.TRUE);
            }
            matriculaSeccionDAO.save(matriculaSeccionPCUR);

            MatriculaSeccion matriculaSeccionTCUR = matriculaSeccionDAO.findByMatriculaMatSeccion(matriculaResumen, seccionTCUR);
            Assert.isTrue(matriculaSeccionTCUR == null, String.format("alumno %s ya se matriculo", alumno.getPersona().getApellidosNombres()));
            matriculaSeccionTCUR = new MatriculaSeccion(curso, matriculaResumen, seccionTCUR, EstadoMatriculaEnum.SOL, ds.getUsuario(), ds.getFechaAccionAudit());
            matriculaSeccionTCUR.setEsAmpliacionVacante(Boolean.TRUE);
            if (!isDocentePrincipalTcurLogged) {
                matriculaSeccionTCUR.setEnSolicitud(Boolean.TRUE);
            }
            matriculaSeccionDAO.save(matriculaSeccionTCUR);

            if (!seccionTCUR.getDocentePrincipal().equals(seccionPCUR.getDocentePrincipal())) {
                matriculaSeccionPCUR = matriculaSeccionDAO.find(matriculaSeccionPCUR.getId());
                matriculaSeccionPCUR.setTipoAmpliacionEnum(TipoAmpliacionEnum.valueOf(tipoAmpliacion));
                matriculaSeccionDAO.update(matriculaSeccionPCUR);
            }
            this.calcularMatriculaResumenInfoMatriculas(matriculaResumen, EstadoMatriculaEnum.MAT);
        }
    }

    public Map<Long, DocenteSeccion> getMapDocentesSeccionGroupBySeccion(GrupoSeccion grupoSeccion) {
        List<DocenteSeccion> docentesSeccionesPrincipales = docenteSeccionDAO.allByGrupoSeccion(grupoSeccion);
        docentesSeccionesPrincipales = docentesSeccionesPrincipales.stream().filter(x -> x.getPrincipal() == 1 && x.getEstadoEnum() == ACT).collect(Collectors.toList());
        Map<Long, DocenteSeccion> mapDocentePrincipalBySeccion = TypesUtil.convertListToMap("seccion.id", docentesSeccionesPrincipales);
        return mapDocentePrincipalBySeccion;
    }

    public void validarEventoAcademico(Date fecha, CicloAcademico cicloAcademico) {
        DateTime fechaAudit = new DateTime(fecha);
        EventoCicloAcademico eventoCicloAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(cicloAcademico, EventoAcademicoEnum.MAT_AMP_DOC);

        Assert.isTrue(eventoCicloAcademico != null, "El evento de ampliación de vacantes no configurado.");
        Assert.isFalse(fechaAudit.isBefore(eventoCicloAcademico.getFechaInicioDateTime()) || fechaAudit.isAfter(eventoCicloAcademico.getFechaFinDateTime()),
                "No se permite ampliar vacante en este momento");
    }

    @Override
    @Transactional
    public void matricular(AmpliacionVacanteForm ampliacionVacanteForm, CicloAcademico cicloAcademico, DataSessionPivot ds) {
        Seccion seccion = seccionDAO.find(ampliacionVacanteForm.getSeccion());
        logger.debug("seccion {} ", seccion.getId());
        this.validarEventoAcademico(ds.getFechaAccionAudit(), ds.getCicloAcademico());

        GrupoSeccion grupoSeccion = seccion.getGrupoSeccion();
        Curso curso = grupoSeccion.getCurso();
        if (curso.isTipoCursoTEOPRA()) {
            this.solicitarAmpliacion(seccion, ampliacionVacanteForm, cicloAcademico, ds);
            return;
        }

        DocenteSeccion docenteSeccionPrincipalBySec = docenteSeccionDAO.findPrincipalBySeccion(seccion);
        seccion.setDocentePrincipal(docenteSeccionPrincipalBySec.getDocente());
        Assert.isTrue(ds.getDocente().equals(seccion.getDocentePrincipal()),
                String.format("%s No es el docente principal", ds.getDocente().getPersona().getApellidosNombres()));

        List<Alumno> alumnos = alumnoDAO.allByAlumnos(ampliacionVacanteForm.getAlumnos());

        for (Alumno alumno : alumnos) {
            String alumnoNoMatriculable = String.format("El alumno de código de matricula %s, no es matriculable", alumno.getPersona().getApellidosNombres());
            String alumnoYaMatriculado = String.format("El alumno de código de matricula %s, ya esta matriculado", alumno.getPersona().getApellidosNombres());

            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            Assert.isTrue(matriculaResumen != null, alumnoNoMatriculable);
            Assert.isTrue(
                    !Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.NMAT).contains(matriculaResumen.getEstadoEnum()),
                    alumnoNoMatriculable
            );

            MatriculaCurso matriculaCurso = matriculaCursoDAO.findByMatriculaCurso(matriculaResumen, curso);

            if (matriculaCurso == null) {
                matriculaCurso = new MatriculaCurso(curso, matriculaResumen, EstadoMatriculaEnum.MAT);
                matriculaCursoDAO.save(matriculaCurso);
            } else {
                Assert.isFalse(matriculaCurso.getEstadoEnum() == EstadoMatriculaEnum.MAT, alumnoYaMatriculado);
                Assert.isTrue(Arrays.asList(EstadoMatriculaEnum.RET, EstadoMatriculaEnum.NVAC, EstadoMatriculaEnum.RHZ).contains(
                        matriculaCurso.getEstadoEnum()),
                        alumnoNoMatriculable);

                matriculaCurso.setEstadoEnum(EstadoMatriculaEnum.MAT);
                matriculaCursoDAO.update(matriculaCurso);
            }

            MatriculaSeccion matriculaSeccion = matriculaSeccionDAO.findByMatriculaMatSeccion(matriculaResumen, seccion);
            Assert.isTrue(matriculaSeccion == null, alumnoYaMatriculado);

            matriculaSeccion = new MatriculaSeccion(curso,
                    matriculaResumen,
                    seccion,
                    EstadoMatriculaEnum.MAT,
                    ds.getUsuario(),
                    ds.getFechaAccionAudit());
            matriculaSeccion.setEsAmpliacionVacante(Boolean.TRUE);

            JsonResponse responseRest = ampliacionVacanteRestService.matricularAmpliacionVacante(seccion, matriculaResumen.getAlumno(), ds);
            if (responseRest.getSuccess()) {
                matriculaSeccionDAO.save(matriculaSeccion);
                this.calcularMatriculaResumenInfoMatriculas(matriculaResumen, EstadoMatriculaEnum.MAT);
            } else {
                throw new PhobosException("Error en el rest");
            }
        }
    }

    @Override
    public List<MatriculaSeccion> allSolicitudesBySeccion(Seccion seccion, DataSessionPivot ds) {
        List<MatriculaSeccion> matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
        matriculasSeccion = matriculasSeccion.stream().filter(x -> x.isEstadoSOL()).collect(Collectors.toList());
        return matriculasSeccion;
    }

    @Override
    @Transactional
    public void aceptarSolicitudMatricula(MatriculaSeccion matriculaSeccion, DataSessionPivot ds) {
        this.validarEventoAcademico(ds.getFechaAccionAudit(), ds.getCicloAcademico());
        matriculaSeccion = matriculaSeccionDAO.find(matriculaSeccion.getId());
        Seccion seccion = seccionDAO.find(matriculaSeccion.getSeccion());

        DocenteSeccion docenteSeccion = docenteSeccionDAO.findPrincipalBySeccion(seccion);
        seccion.setDocentePrincipal(docenteSeccion.getDocente());
        Assert.isTrue(ds.getDocente().equals(seccion.getDocentePrincipal()), String.format("%s, no es el docente principal de la sección", ds.getDocente().getPersona().getApellidosNombres()));

        MatriculaCurso matriculaCurso = matriculaCursoDAO.findByMatriculaCurso(matriculaSeccion.getMatriculaResumen(), seccion.getGrupoSeccion().getCurso());
        Curso curso = seccion.getGrupoSeccion().getCurso();
        Assert.isTrue(curso.isTipoCursoTEOPRA(), "El curso debe ser teórico practico, verifique.");
        Assert.isTrue(matriculaCurso.isEstadoSOL(), String.format("El curso %s no esta en solicitud", matriculaCurso.getCurso().getNombre()));

        MatriculaCurso matriculaCursoUpd = new MatriculaCurso(matriculaCurso.getId());
        matriculaCursoUpd.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaCursoDAO.updateColumns(matriculaCursoUpd, "estado");

        this.aceptarMatriculaSeccion(matriculaSeccion, ds);
        boolean esDocenteTcurLogged = false;
        if (seccion.isTipoSeccionPCUR()) {
            Seccion seccionTCUR = seccion.getSeccionSuperior();
            esDocenteTcurLogged = seccionTCUR.getDocentePrincipal().equals(ds.getDocente());
            MatriculaSeccion matriculaSeccionTCUR = matriculaSeccionDAO.findByMatriculaMatSeccionAndNoEstado(matriculaCurso.getMatriculaResumen(), seccionTCUR, EstadoMatriculaEnum.RHZ);
            this.aceptarMatriculaSeccion(matriculaSeccionTCUR, ds);
        }
        if (seccion.isTipoSeccionTCUR()) {
            esDocenteTcurLogged = true;
            MatriculaSeccion matriculaSeccionPCUR = matriculaSeccionDAO.findByMatResumenAndTipoSecAndNoEstado(matriculaCurso.getMatriculaResumen(), TipoSeccionEnum.PCUR, EstadoMatriculaEnum.RHZ);
            this.aceptarMatriculaSeccion(matriculaSeccionPCUR, ds);
        }

        ampliacionVacanteRestService.confirmarAmpliacionVacante(matriculaSeccion, esDocenteTcurLogged, ds);
        //this.calcularSeccionInfoMatriculas(seccion);
        this.calcularMatriculaResumenInfoMatriculas(matriculaCurso.getMatriculaResumen(), EstadoMatriculaEnum.MAT);
        //      throw new PhobosException("no pasaras papu");
    }

    public void aceptarMatriculaSeccion(MatriculaSeccion matriculaSeccion, DataSessionPivot ds) {
        //  matriculaSeccion = matriculaSeccionDAO.find(matriculaSeccion.getId());
        Assert.isTrue(matriculaSeccion.isEstadoSOL(), String.format("La matricula de la sección %s no esta en solicitud", matriculaSeccion.getSeccion().getCodigo2()));

        MatriculaSeccion matriculaSeccionUpd = new MatriculaSeccion();
        matriculaSeccionUpd.setId(matriculaSeccion.getId());
        matriculaSeccionUpd.setEnSolicitud(Boolean.FALSE);
        matriculaSeccionUpd.setEstadoEnum(EstadoMatriculaEnum.MAT);
        matriculaSeccionDAO.updateColumns(matriculaSeccionUpd, "enSolicitud", "estado");
    }

    @Override
    @Transactional
    public void rechazarSolicitudMatricula(MatriculaSeccion matriculaSeccion, DataSessionPivot ds) {
        this.validarEventoAcademico(ds.getFechaAccionAudit(), ds.getCicloAcademico());
        matriculaSeccion = matriculaSeccionDAO.find(matriculaSeccion.getId());
        Seccion seccion = seccionDAO.find(matriculaSeccion.getSeccion());

        DocenteSeccion docenteSeccion = docenteSeccionDAO.findPrincipalBySeccion(seccion);
        seccion.setDocentePrincipal(docenteSeccion.getDocente());
        Assert.isTrue(ds.getDocente().equals(seccion.getDocentePrincipal()), String.format("%s, no es el docente principal de la sección", ds.getDocente().getPersona().getApellidosNombres()));

        MatriculaCurso matriculaCurso = matriculaCursoDAO.findByMatriculaCurso(matriculaSeccion.getMatriculaResumen(), seccion.getGrupoSeccion().getCurso());
        Curso curso = seccion.getGrupoSeccion().getCurso();
        Assert.isTrue(curso.isTipoCursoTEOPRA(), "El curso debe ser teórico practico, verifique.");

        matriculaCursoDAO.delete(matriculaCurso);
        this.rechazarMatriculaSeccion(matriculaSeccion, ds);
        boolean esDocenteTcurLogged = false;
        if (seccion.isTipoSeccionPCUR()) {
            Seccion seccionTCUR = seccion.getSeccionSuperior();
            esDocenteTcurLogged = seccionTCUR.getDocentePrincipal().equals(ds.getDocente());
            MatriculaSeccion matriculaSeccionTCUR = matriculaSeccionDAO.findByMatriculaMatSeccionAndNoEstado(matriculaCurso.getMatriculaResumen(), seccionTCUR, EstadoMatriculaEnum.RHZ);
            this.rechazarMatriculaSeccion(matriculaSeccionTCUR, ds);
        }
        if (seccion.isTipoSeccionTCUR()) {
            esDocenteTcurLogged = true;
            MatriculaSeccion matriculaSeccionPCUR = matriculaSeccionDAO.findByMatResumenAndTipoSecAndNoEstado(matriculaCurso.getMatriculaResumen(), TipoSeccionEnum.PCUR, EstadoMatriculaEnum.RHZ);
            this.rechazarMatriculaSeccion(matriculaSeccionPCUR, ds);
        }
        ampliacionVacanteRestService.rechazarAmpliacionVacante(matriculaSeccion, esDocenteTcurLogged, ds);
        this.calcularSeccionInfoMatriculas(seccion);
//        throw new PhobosException("no pasaras papu");
    }

    public void rechazarMatriculaSeccion(MatriculaSeccion matriculaSeccion, DataSessionPivot ds) {
        // matriculaSeccion = matriculaSeccionDAO.find(matriculaSeccion.getId());
        Assert.isTrue(matriculaSeccion.isEstadoSOL(), String.format("La matricula de la sección %s no esta en solicitud", matriculaSeccion.getSeccion().getCodigo2()));

        MatriculaSeccion matriculaSeccionUpd = new MatriculaSeccion();
        matriculaSeccionUpd.setId(matriculaSeccion.getId());
        matriculaSeccionUpd.setEnSolicitud(Boolean.FALSE);
        matriculaSeccionUpd.setEstadoEnum(EstadoMatriculaEnum.RHZ);
        matriculaSeccionDAO.updateColumns(matriculaSeccionUpd, "enSolicitud", "estado");
    }

    public void calcularSeccionInfoMatriculas(Seccion seccion) {
        this.calcularSeccionInfoMatriculas(seccion, null);
    }

    public void calcularSeccionInfoMatriculas(Seccion seccion, List<MatriculaSeccion> matriculasSeccion) {
        if (matriculasSeccion == null) {
            matriculasSeccion = matriculaSeccionDAO.allBySeccion(seccion);
        }
        List<MatriculaSeccion> solicitudesMatricula = matriculasSeccion.stream().filter(x -> x.isEstadoSOL()).collect(Collectors.toList());
        List<MatriculaSeccion> matriculas = matriculasSeccion.stream().filter(x -> x.isEstadoMAT()).collect(Collectors.toList());
        List<MatriculaSeccion> ampliacionesMatricula = matriculasSeccion.stream()
                .filter(x -> x.isEstadoMAT())
                .filter(x -> x.getEsAmpliacionVacante())
                .collect(Collectors.toList());

        Seccion seccionUpd = new Seccion(seccion.getId());
        seccionUpd.setSolicitudesMatricula(solicitudesMatricula != null ? solicitudesMatricula.size() : BigDecimal.ZERO.intValue());
        seccionUpd.setAmpliacionVacante(ampliacionesMatricula != null ? ampliacionesMatricula.size() : BigDecimal.ZERO.intValue());
        seccionUpd.setMatriculados(matriculas != null ? matriculas.size() : BigDecimal.ZERO.intValue());
        seccionDAO.updateColumns(seccionUpd, "solicitudesMatricula", "ampliacionVacante", "matriculados");
    }

    public void calcularMatriculaResumenInfoMatriculas(MatriculaResumen matriculaResumen, EstadoMatriculaEnum estadoMatriculaEnum) {
        List<MatriculaCurso> matriculasCurso = matriculaCursoDAO.allByMatriculaResumen(matriculaResumen);
        List<MatriculaCurso> matriculados = matriculasCurso.stream().filter(x -> x.isEstadoMAT()).collect(Collectors.toList());
        Integer creditosMatriculados = BigDecimal.ZERO.intValue();
        if (matriculados != null) {
            matriculados.stream().mapToInt(x -> x.getCreditos()).sum();
        }

        MatriculaResumen matriculaResumenUpd = new MatriculaResumen(matriculaResumen.getId());
        matriculaResumenUpd.setCursosMatriculados(matriculados != null ? matriculados.size() : BigDecimal.ZERO.intValue());
        matriculaResumenUpd.setCreditosMatriculados(creditosMatriculados);
        matriculaResumenUpd.setEstadoEnum(estadoMatriculaEnum);
        matriculaResumenDAO.updateColumns(matriculaResumenUpd, "cursosMatriculados", "creditosMatriculados", "estado");
    }

}
