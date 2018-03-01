package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaCurso;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.NotaLetraEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.situacionacademica.SituacionAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;

@Service
@Transactional(readOnly = true)
public class PromedioServiceImp implements PromedioService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    SituacionAcademicaService situacionAcademicaService;

    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    MatriculaCursoDAO matriculaCursoDAO;

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void trasladoPromediosSource(MatriculaCurso matriculaCurso, Usuario usuario) {
        DateTime today = new DateTime();
        Alumno alumno = alumnoDAO.find(matriculaCurso.getMatriculaResumen().getAlumno());
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        CicloAcademico cicloAcademicoAnterior = cicloAcademicoDAO.findAnteriorActivo(cicloAcademico);

        Curso curso = cursoDAO.find(matriculaCurso.getCurso().getId());

        AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademicoAnterior);
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findActiveByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);

        if (alumnoCiclo == null) {
            alumnoCiclo = new AlumnoCiclo();
            alumnoCiclo.setAlumno(alumno);
            alumnoCiclo.setCarrera(alumno.getCarrera());
            alumnoCiclo.setCicloAcademico(cicloAcademico);
            //todos los ciclos
            alumnoCiclo.setCreditosAcumulados(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCreditosAprobadosAcumulados(BigDecimal.ZERO.intValue());

            //por ciclo
            alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCreditosCursadosCiclo(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCursosAprobados(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCursosInscritos(BigDecimal.ZERO.intValue());
            //
            alumnoCiclo.setEstado(EstadoMatriculaEnum.MAT);
            alumnoCiclo.setUserRegistro(usuario);
            alumnoCiclo.setUserModificacion(usuario);
            alumnoCiclo.setFechaModificacion(today.toDate());
            alumnoCiclo.setFechaRegistro(today.toDate());
            alumnoCiclo.setOrientacionCarrera(alumno.getOrientacionCarrera());

            SituacionAcademica situacionInicio = alumnoCicloAnterior == null ? alumno.getSituacionAcademica() : alumnoCicloAnterior.getSituacionFinal();
            alumnoCiclo.setSituacionInicio(situacionInicio);
            alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
            // alumnoCiclo.setSituacionFinal(situacionAcademica);

            //calcular
            alumnoCiclo.setPromedioAcumulado(BigDecimal.ZERO);
            alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
            alumnoCicloDAO.save(alumnoCiclo);
            alumno.getId();
        }

        if (alumnoCicloCurso == null) {
            alumnoCicloCurso = new AlumnoCicloCurso();
            alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
            //  alumnoCicloCurso.setAutorizacionRegistro(autorizacionRegistro); wtf
            alumnoCicloCurso.setCreditos(matriculaCurso.getCreditos());
            alumnoCicloCurso.setCurso(curso);

            Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
            alumnoCicloCurso.setEstaAprobado(aprobado);

            alumnoCicloCurso.setEstado(EstadoMatriculaEnum.MAT);
            alumnoCicloCurso.setFechaModificacion(today.toDate());
            alumnoCicloCurso.setFechaRegistro(today.toDate());
            alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
            alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.ACTA);
            alumnoCicloCurso.setRegistroActivo(BigDecimal.ONE.intValue());
            alumnoCicloCurso.setUserModificacion(usuario);
            alumnoCicloCurso.setUsuarioRegistro(usuario);
            alumnoCicloCursoDAO.save(alumnoCicloCurso);
            alumnoCicloCurso.getId();
        } else {
            if (!alumnoCicloCurso.getNota().equals(matriculaCurso.getNotaFinal())) {
                alumnoCicloCurso.setFechaModificacion(today.toDate());
                alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
                alumnoCicloCurso.setUserModificacion(usuario);
                Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
                alumnoCicloCurso.setEstaAprobado(aprobado);

                alumnoCicloCursoDAO.update(alumnoCicloCurso);
                alumnoCicloCurso.getId();
            }
        }
        CicloAcademico cicloAcademicoSiguiente = cicloAcademicoDAO.findSiguienteActivo(cicloAcademico);
        MatriculaCurso matriculaCursoSiguiente = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, cicloAcademicoSiguiente);
        if (matriculaCursoSiguiente != null) {
            this.trasladoPromediosSource(matriculaCursoSiguiente, usuario);
        } else {
            this.promediarTraslados(alumno, usuario, today);
        }
    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void promediarAsync(Alumno alumno, Usuario usuario) {
        DateTime today = new DateTime();
        this.promediarTraslados(alumno, usuario, today);
    }

    public void promediarTraslados(Alumno alumno, Usuario usuario, DateTime today) {

        List<AlumnoCiclo> alumnosCiclosByAlumno = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
        for (AlumnoCiclo alumnoCicloEach : alumnosCiclosByAlumno) {
            logger.debug("Ciclo {}", alumnoCicloEach.getId());
            //todos los ciclos anteriores
            Integer credAcumuladosAlumno = BigDecimal.ZERO.intValue();
            Integer credAprAcumuladosAlumno = BigDecimal.ZERO.intValue();

            //por ciclo actual
            Integer credCursadosAlumnoCiclo = BigDecimal.ZERO.intValue();
            Integer credCursadosAproAlumnoCiclo = BigDecimal.ZERO.intValue();

            Integer cursosInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();
            Integer cursosAprInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();

            /*Obtenemos la informacion del ciclo actual*/
            List<AlumnoCicloCurso> alumnosCicloCursoByAlumnoCiclo = alumnoCicloCursoDAO.allOperativesByAlumnoCiclo(alumno, alumnoCicloEach.getCicloAcademico());

            BigDecimal sumNotasCreditos = BigDecimal.ZERO;
            BigDecimal sumCreditos = BigDecimal.ZERO;
            for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursoByAlumnoCiclo) {
                credCursadosAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
                cursosInscritosAlumnoCiclo += 1;
                credAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();

                if (alumnoCicloCursoEach.isAprobado()) {
                    credCursadosAproAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
                    cursosAprInscritosAlumnoCiclo += 1;
                    credAprAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
                }
                BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
                BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
                if (notaBig != null) {
                    sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                    sumCreditos = sumCreditos.add(creditosBig);
                }
            }

            //
            List<AlumnoCicloCurso> alumnosCicloCursosCiclosAnteriores = alumnoCicloCursoDAO.allOperativesByAlumnoCicloAnteriores(alumno, alumnoCicloEach.getCicloAcademico());
            BigDecimal sumNotasCreditosTotal = sumNotasCreditos;
            BigDecimal sumCreditosTotal = sumCreditos;
            for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursosCiclosAnteriores) {
                credAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
                if (alumnoCicloCursoEach.isAprobado()) {
                    credAprAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
                }
                BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
                BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
                if (notaBig != null) {
                    sumNotasCreditosTotal = sumNotasCreditosTotal.add(notaBig.multiply(creditosBig));
                    sumCreditosTotal = sumCreditosTotal.add(creditosBig);
                }
            }

            BigDecimal promedio = BigDecimal.ZERO;
            if (sumNotasCreditos.compareTo(BigDecimal.ZERO) != 0 && sumCreditos.compareTo(BigDecimal.ZERO) != 0) {
                promedio = sumNotasCreditos.divide(sumCreditos, 2, RoundingMode.HALF_UP);
            }

            BigDecimal promedioAcumulado = BigDecimal.ZERO;
            if (sumNotasCreditosTotal.compareTo(BigDecimal.ZERO) != 0 && sumCreditosTotal.compareTo(BigDecimal.ZERO) != 0) {
                promedioAcumulado = sumNotasCreditosTotal.divide(sumCreditosTotal, 2, RoundingMode.HALF_UP);
            }

            alumnoCicloEach.setPromedioCiclo(promedio);
            alumnoCicloEach.setPromedioAcumulado(promedioAcumulado);

            alumnoCicloEach.setCreditosAcumulados(credAcumuladosAlumno);
            alumnoCicloEach.setCreditosAprobadosAcumulados(credAprAcumuladosAlumno);

            alumnoCicloEach.setCreditosAprobadosCiclo(credCursadosAproAlumnoCiclo);
            alumnoCicloEach.setCreditosCursadosCiclo(credCursadosAlumnoCiclo);
            alumnoCicloEach.setCursosAprobados(cursosAprInscritosAlumnoCiclo);
            alumnoCicloEach.setCursosInscritos(cursosInscritosAlumnoCiclo);

            alumnoCicloEach.setUserModificacion(usuario);
            alumnoCicloEach.setFechaModificacion(today.toDate());

            if (alumnosCicloCursoByAlumnoCiclo.size() == BigDecimal.ONE.intValue()) {
                alumnoCicloEach.setEstaAprobado(alumnosCicloCursoByAlumnoCiclo.get(0).getEstaAprobado());
            } else {
                Integer aprobado = evaluateEstaAprobado(promedio, alumno);
                alumnoCicloEach.setEstaAprobado(aprobado);
            }
            alumnoCicloDAO.update(alumnoCicloEach);
            alumnoCicloEach.getId();

            Long ciclosEstudiados = alumnoCicloDAO.countCiclosEstudiados(alumno, alumnoCicloEach.getCicloAcademico());

            SituacionAcademica situacionAcademicaFinal = null;
            if (ciclosEstudiados.intValue() == 1 || ciclosEstudiados.intValue() == 2) {
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
            } else if (alumnoCicloEach.getCicloAcademico().isTipoNivelacion()) {
                situacionAcademicaFinal = alumnoCicloEach.getSituacionInicio();
            } else {
                situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCicloEach, alumnoCicloEach.getSituacionInicio(), -1, alumnoCicloEach.getCreditosAprobadosAcumulados(), alumnoCicloEach.getCicloAcademico());
            }

            alumnoCicloEach.setSituacionFinal(situacionAcademicaFinal);
            alumnoCicloDAO.update(alumnoCicloEach);

            Alumno alumnoUpd = new Alumno();
            alumnoUpd.setId(alumno.getId());
            alumnoUpd.setCicloActivo(alumnoCicloEach.getCicloAcademico());
            alumnoUpd.setSituacionAcademica(situacionAcademicaFinal);
            alumnoDAO.updateCicloActivoSituacionAcad(alumnoUpd);

        }

    }

    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void promedio(MatriculaCurso matriculaCurso, Usuario usuario, boolean calcularSituacionAcadFinal) {
        Alumno alumno = alumnoDAO.find(matriculaCurso.getMatriculaResumen().getAlumno());
        CicloAcademico cicloAcademico = matriculaCurso.getMatriculaResumen().getCicloAcademico();
        Curso curso = cursoDAO.find(matriculaCurso.getCurso().getId());

        CicloAcademico cicloAcademicoAnterior = cicloAcademicoDAO.findAnteriorActivo(cicloAcademico);
        AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademicoAnterior);

        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);
        DateTime today = new DateTime();

        if (alumnoCiclo == null) {
            alumnoCiclo = new AlumnoCiclo();
            alumnoCiclo.setAlumno(alumno);
            alumnoCiclo.setCarrera(alumno.getCarrera());
            alumnoCiclo.setCicloAcademico(cicloAcademico);
            //todos los ciclos
            alumnoCiclo.setCreditosAcumulados(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCreditosAprobadosAcumulados(BigDecimal.ZERO.intValue());

            //por ciclo
            alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCreditosCursadosCiclo(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCursosAprobados(BigDecimal.ZERO.intValue());
            alumnoCiclo.setCursosInscritos(BigDecimal.ZERO.intValue());
            //
            alumnoCiclo.setEstado(EstadoMatriculaEnum.MAT);
            alumnoCiclo.setUserRegistro(usuario);
            alumnoCiclo.setUserModificacion(usuario);
            alumnoCiclo.setFechaModificacion(today.toDate());
            alumnoCiclo.setFechaRegistro(today.toDate());
            alumnoCiclo.setOrientacionCarrera(alumno.getOrientacionCarrera());

            alumnoCiclo.setSituacionInicio(alumno.getSituacionAcademica());
            alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
            // alumnoCiclo.setSituacionFinal(situacionAcademica);

            //calcular
            alumnoCiclo.setPromedioAcumulado(BigDecimal.ZERO);
            alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
            alumnoCicloDAO.save(alumnoCiclo);
            alumno.getId();
        }
        if (alumnoCicloCurso == null) {
            alumnoCicloCurso = new AlumnoCicloCurso();
            alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
            //  alumnoCicloCurso.setAutorizacionRegistro(autorizacionRegistro); wtf
            alumnoCicloCurso.setCreditos(matriculaCurso.getCreditos());
            alumnoCicloCurso.setCurso(curso);

            Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
            alumnoCicloCurso.setEstaAprobado(aprobado);

            alumnoCicloCurso.setEstado(EstadoMatriculaEnum.MAT);
            alumnoCicloCurso.setFechaModificacion(today.toDate());
            alumnoCicloCurso.setFechaRegistro(today.toDate());
            alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
            alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.ACTA);
            alumnoCicloCurso.setRegistroActivo(BigDecimal.ONE.intValue());
            alumnoCicloCurso.setUserModificacion(usuario);
            alumnoCicloCurso.setUsuarioRegistro(usuario);
            alumnoCicloCursoDAO.save(alumnoCicloCurso);
            alumnoCicloCurso.getId();
        } else {
            alumnoCicloCurso.setFechaModificacion(today.toDate());
            alumnoCicloCurso.setNota(matriculaCurso.getNotaFinal());
            alumnoCicloCurso.setUserModificacion(usuario);
            Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
            alumnoCicloCurso.setEstaAprobado(aprobado);

            alumnoCicloCursoDAO.update(alumnoCicloCurso);
            alumnoCicloCurso.getId();
        }

        //todos los ciclos
        Integer credAcumuladosAlumno = BigDecimal.ZERO.intValue();
        Integer credAprAcumuladosAlumno = BigDecimal.ZERO.intValue();

        //por ciclo
        Integer credCursadosAlumnoCiclo = BigDecimal.ZERO.intValue();
        Integer credCursadosAproAlumnoCiclo = BigDecimal.ZERO.intValue();

        Integer cursosInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();
        Integer cursosAprInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();

        List<AlumnoCicloCurso> allByAlumnoCiclo = alumnoCicloCursoDAO.allOperativesByAlumnoCiclo(alumno, cicloAcademico);

        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;
        for (AlumnoCicloCurso alumnoCicloCursoEach : allByAlumnoCiclo) {
            credCursadosAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
            cursosInscritosAlumnoCiclo += 1;
            if (alumnoCicloCursoEach.isAprobado()) {
                credCursadosAproAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
                cursosAprInscritosAlumnoCiclo += 1;
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);
            }
        }

        List<AlumnoCicloCurso> allByAlumno = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        BigDecimal sumNotasCreditosTotal = BigDecimal.ZERO;
        BigDecimal sumCreditosTotal = BigDecimal.ZERO;
        for (AlumnoCicloCurso alumnoCicloCursoEach : allByAlumno) {

            credAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
            if (alumnoCicloCursoEach.isAprobado()) {
                credAprAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
            }
            BigDecimal notaBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getNota());
            BigDecimal creditosBig = TypesUtil.getBigDecimal(alumnoCicloCursoEach.getCreditos());
            if (notaBig != null) {
                sumNotasCreditosTotal = sumNotasCreditosTotal.add(notaBig.multiply(creditosBig));
                sumCreditosTotal = sumCreditosTotal.add(creditosBig);
            }
        }

        BigDecimal promedio = BigDecimal.ZERO;
        if (sumNotasCreditos.compareTo(BigDecimal.ZERO) != 0 && sumCreditos.compareTo(BigDecimal.ZERO) != 0) {
            promedio = sumNotasCreditos.divide(sumCreditos, 2, RoundingMode.HALF_UP);
        }

        BigDecimal promedioAcumulado = BigDecimal.ZERO;
        if (sumNotasCreditosTotal.compareTo(BigDecimal.ZERO) != 0 && sumCreditosTotal.compareTo(BigDecimal.ZERO) != 0) {
            promedioAcumulado = sumNotasCreditosTotal.divide(sumCreditosTotal, 2, RoundingMode.HALF_UP);
        }
        alumnoCiclo.setPromedioCiclo(promedio);
        alumnoCiclo.setPromedioAcumulado(promedioAcumulado);

        alumnoCiclo.setCreditosAcumulados(credAcumuladosAlumno);
        alumnoCiclo.setCreditosAprobadosAcumulados(credAprAcumuladosAlumno);

        alumnoCiclo.setCreditosAprobadosCiclo(credCursadosAproAlumnoCiclo);
        alumnoCiclo.setCreditosCursadosCiclo(credCursadosAlumnoCiclo);
        alumnoCiclo.setCursosAprobados(cursosAprInscritosAlumnoCiclo);
        alumnoCiclo.setCursosInscritos(cursosInscritosAlumnoCiclo);

        alumnoCiclo.setUserModificacion(usuario);
        alumnoCiclo.setFechaModificacion(today.toDate());

        if (allByAlumnoCiclo.size() == BigDecimal.ONE.intValue()) {
            alumnoCiclo.setEstaAprobado(allByAlumnoCiclo.get(0).getEstaAprobado());
        } else {
            Integer aprobado = evaluateEstaAprobado(promedio, alumno);
            alumnoCiclo.setEstaAprobado(aprobado);
        }
        alumnoCicloDAO.update(alumnoCiclo);
        alumnoCiclo.getId();
        if (calcularSituacionAcadFinal) {
            SituacionAcademica situacionAcademicaIni = alumnoCicloAnterior != null ? alumnoCicloAnterior.getSituacionFinal() : alumno.getSituacionAcademica();
            SituacionAcademica situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCiclo, situacionAcademicaIni, alumno.getCiclosEstudiados(), alumno.getCreditosAprobados(), cicloAcademico);

            alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
            alumnoCicloDAO.update(alumnoCiclo);

            Alumno alumnoUpd = new Alumno();
            alumnoUpd.setId(alumno.getId());
            alumnoUpd.setCicloActivo(cicloAcademico);
            alumnoUpd.setSituacionAcademica(situacionAcademicaFinal);
            alumnoDAO.updateCicloActivoSituacionAcad(alumnoUpd);
        }
    }

    public Integer evaluateEstaAprobado(MatriculaCurso matriculaCurso, Alumno alumno) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (matriculaCurso.getNotaFinal().equals(NotaLetraEnum.APROBADO.getValor1())) {
            aprobado = BigDecimal.ONE.intValue();
        } else if (TypesUtil.getBigDecimal(matriculaCurso.getNotaFinal()) != null) {
            BigDecimal notaBig = TypesUtil.getBigDecimal(matriculaCurso.getNotaFinal());
            aprobado = evaluateEstaAprobado(notaBig, alumno);
        }
        return aprobado;
    }

    public Integer evaluateEstaAprobado(BigDecimal nota, Alumno alumno) {
        Integer aprobado = BigDecimal.ZERO.intValue();
        if (alumno.isPostgrado()) {
            if (nota.compareTo(new BigDecimal(13)) >= 0) {
                aprobado = BigDecimal.ONE.intValue();
            }
        } else {
            if (nota.compareTo(new BigDecimal(11)) >= 0) {
                aprobado = BigDecimal.ONE.intValue();
            }
        }
        return aprobado;
    }

}
