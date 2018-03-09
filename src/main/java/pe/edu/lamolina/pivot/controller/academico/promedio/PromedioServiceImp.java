package pe.edu.lamolina.pivot.controller.academico.promedio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum.*;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_7;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_EM;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_X;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_XD;
import static pe.edu.lamolina.model.enums.SituacionAcademicaEnum.S_D;
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
        Curso curso = cursoDAO.find(matriculaCurso.getCurso().getId());
        
        generarHistorialNotas(alumno, curso, matriculaCurso, cicloAcademico, usuario, today);
        
        AlumnoCiclo alumnoCicloSiguiente = alumnoCicloDAO.findActiveSiguienteByAlumno(alumno, cicloAcademico);
        MatriculaCurso matriculaCursoSiguiente = null;
        if (alumnoCicloSiguiente != null) {
            matriculaCursoSiguiente = matriculaCursoDAO.findByAlumnoCursoCiclo(alumno, curso, alumnoCicloSiguiente.getCicloAcademico());
        }
        if (alumnoCicloSiguiente != null && matriculaCursoSiguiente != null) {
            this.trasladoPromediosSource(matriculaCursoSiguiente, usuario);
        } else {
            //   this.promediarTrasladosAllCiclos(alumno, usuario, today);
            this.promediarHistorialNotas(alumno, cicloAcademico, usuario, today);
        }
    }
    
    @Async
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void promediarAllCicloAsync(Alumno alumno, Usuario usuario) {
        DateTime today = new DateTime();
        this.promediarTrasladosAllCiclos(alumno, usuario, today);
    }
    
    public void promediarTrasladosAllCiclos(Alumno alumno, Usuario usuario, DateTime today) {
        
        List<AlumnoCiclo> alumnosCiclosByAlumno = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
        
        for (AlumnoCiclo alumnoCicloEach : alumnosCiclosByAlumno) {
            this.promediarHistorialNotas(alumno, alumnoCicloEach.getCicloAcademico(), usuario, today);
        }
        /*
        Alumno alumnoFinal = alumnoDAO.find(alumno);
        if (!Arrays.asList(S_X.getValue(), S_XD.getValue(), S_EM.getValue(), S_7.getValue()).contains(alumnoFinal.getSituacionAcademica().getCodigo())) {

            AlumnoCiclo alumnoCicloSiguienteRegular = alumnoCicloDAO.findByAlumnoCiclo(alumno, lastCicloAcademico);
            if (alumnoCicloSiguienteRegular == null) {
                alumnoCicloSiguienteRegular = new AlumnoCiclo();
                alumnoCicloSiguienteRegular.defaultValuesToCreate(alumno, lastCicloAcademico, usuario, today);
                alumnoCicloSiguienteRegular.setSituacionInicio(alumnoFinal.getSituacionAcademica());
                alumnoCicloSiguienteRegular.setSituacionFinal(alumnoFinal.getSituacionAcademica());
                alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.NMAT);
                alumnoCicloDAO.save(alumnoCicloSiguienteRegular);
            } else {
                alumnoCicloSiguienteRegular.setSituacionInicio(alumnoFinal.getSituacionAcademica());
                alumnoCicloSiguienteRegular.setSituacionFinal(alumnoFinal.getSituacionAcademica());
                alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.NMAT);
            }
        }
         */
    }
    
    public SituacionAcademica calculateSitutacionAcadFinal(Alumno alumno,
            AlumnoCiclo alumnoCiclo, SituacionAcademica situacionInicial,
            Integer ciclosEstudiados, boolean tieneTrika) {
        SituacionAcademica situacionAcademicaFinal = null;
        
        if ((ciclosEstudiados.intValue() == 1 || ciclosEstudiados.intValue() == 2) && alumno.isPregrado()) {
            if (TypesUtil.getInt(alumnoCiclo.getCicloAcademico().getCodigo()) >= 201710) {
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
            } else {
                if (alumnoCiclo.isAprobado() || ciclosEstudiados.intValue() == 1) {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
                } else {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_1.getValue());
                }
            }
        } else if (tieneTrika) {
            situacionAcademicaFinal = situacionInicial;
            if (alumnoCiclo.isAprobado()) {
                if (situacionAcademicaFinal.isCodigoS6()) {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_3.getValue());
                }
            } else {
                SituacionAcademica situacionSeparado = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_4.getValue());
                situacionAcademicaFinal = situacionSeparado;
            }
            //     tieneTrika = false;
        } else if (alumnoCiclo.getCicloAcademico().isTipoNivelacion()) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
        } else {
            situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCiclo, alumnoCiclo.getSituacionInicio(), -1, alumnoCiclo.getCreditosAprobadosAcumulados(), alumnoCiclo.getCicloAcademico());
        }
        return situacionAcademicaFinal;
    }
    
    public SituacionAcademica calculateSitutacionAcadFinal(Alumno alumno,
            AlumnoCiclo alumnoCiclo, SituacionAcademica situacionInicial,
            Integer ciclosEstudiados, AlumnoCiclo alumnoCicloInhaAnterior) {
        SituacionAcademica situacionAcademicaFinal = null;
        
        if (alumnoCiclo.getCicloAcademico().isTipoNivelacion()) {
            situacionAcademicaFinal = alumnoCiclo.getSituacionInicio();
        } else if ((ciclosEstudiados.intValue() == 1 || ciclosEstudiados.intValue() == 2) && alumno.isPregrado()) {
            if (TypesUtil.getInt(alumnoCiclo.getCicloAcademico().getCodigo()) >= 201710) {
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
            } else {
                if (alumnoCiclo.isAprobado() || ciclosEstudiados.intValue() == 1) {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
                } else {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_1.getValue());
                }
            }
        } else if (alumnoCiclo.getSituacionInicio().isCodigoS4()) {
            if (alumnoCiclo.isAprobado()) {
                //normal con antecedentes
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_5.getValue());
            } else {
                //separado definitivo
                situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_X.getValue());
            }
        } else {
            situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCiclo, alumnoCiclo.getSituacionInicio(), -1, alumnoCiclo.getCreditosAprobadosAcumulados(), alumnoCiclo.getCicloAcademico());
        }
        if (alumnoCicloInhaAnterior != null && alumnoCicloInhaAnterior.getSituacionFinal().isTrikeado()) {
            situacionAcademicaFinal = situacionInicial;
            if (alumnoCiclo.isAprobado()) {
                if (situacionAcademicaFinal.isCodigoS6()) {
                    situacionAcademicaFinal = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_3.getValue());
                }
            } else {
                SituacionAcademica situacionSeparado = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_4.getValue());
                situacionAcademicaFinal = situacionSeparado;
            }
        }
        
        return situacionAcademicaFinal;
    }
    
    @Transactional(propagation = Propagation.MANDATORY)
    public void generarHistorialNotas(Alumno alumno,
            Curso curso,
            MatriculaCurso matriculaCurso,
            CicloAcademico cicloAcademico,
            Usuario usuario,
            DateTime today) {
        AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findActiveAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCicloEstado(alumno, cicloAcademico, Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.INH, EstadoMatriculaEnum.RCI));
        
        AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCursoEstados(alumno, cicloAcademico, curso, Arrays.asList(EstadoMatriculaEnum.MAT, EstadoMatriculaEnum.INH, EstadoMatriculaEnum.RCI));
        /*
        if (alumnoCiclo != null && (alumnoCiclo.isEstadoInhabilitado() || alumnoCiclo.isEstadoRetiradoCic())) {
            return;
        }
         */
        if (alumnoCiclo == null) {
            alumnoCiclo = new AlumnoCiclo();
            alumnoCiclo.defaultValuesToCreate(alumno, cicloAcademico, usuario, today);
            alumnoCiclo.setEstado(matriculaCurso.getMatriculaResumen().getEstadoEnum());
            SituacionAcademica situacionInicio = alumnoCicloAnterior == null ? alumno.getSituacionAcademica() : alumnoCicloAnterior.getSituacionFinal();
            alumnoCiclo.setSituacionInicio(situacionInicio);
            alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());
            alumnoCicloDAO.save(alumnoCiclo);
            alumno.getId();
        }
        
        if (alumnoCicloCurso == null) {
            alumnoCicloCurso = new AlumnoCicloCurso();
            alumnoCicloCurso.defaultValuesToCreate(alumnoCiclo, curso, matriculaCurso, usuario, today);
            Integer aprobado = evaluateEstaAprobado(matriculaCurso, alumno);
            alumnoCicloCurso.setEstaAprobado(aprobado);
            alumnoCicloCurso.setVecesCursado(BigDecimal.ONE.intValue());
            alumnoCicloCurso.setEstado(alumnoCicloCurso.getEstadoEnum());
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
        this.promediarHistorialNotas(alumno, cicloAcademico, usuario, today);
    }
    
    public void promediarHistorialNotas(Alumno alumno, CicloAcademico cicloAcademico, Usuario usuario, DateTime today) {
        logger.debug("#########################");
        logger.debug("Alumno Id {}", alumno.getId());
        CicloAcademico siguienteCicloReg = cicloAcademicoDAO.findSiguienteRegularActivo(cicloAcademico);
        CicloAcademico cicloActivo = cicloAcademicoDAO.findActivo();
        
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findActiveAnteriorByAlumno(alumno, cicloAcademico);
        
        AlumnoCiclo alumnoCicloAnteriorInha = alumnoCicloDAO.findInhaAnteriorByAlumno(alumno, cicloAcademico);
        AlumnoCiclo alumnoCicloSiguienteInha = alumnoCicloDAO.findInhaSiguienteByAlumno(alumno, cicloAcademico);
        
        if (alumnoCiclo.isNoMatriculado()) {
            int maxConsecutivos = cicloAcademico.getCodigoInt() <= 201710 ? 2 : 3;
            int maxIntercalados = 6;
            
            List<AlumnoCiclo> alumnosCiclosAnteriores = alumnoCicloDAO.allAnterioresEQByCicloAlumno(alumno, cicloAcademico, 20);
            int contadorConsecutivo = 0;
            int contadorIntercalado = 0;
            
            for (AlumnoCiclo alumnoCicloEach : alumnosCiclosAnteriores) {
                if (alumnoCicloEach.isNoMatriculado()) {
                    contadorIntercalado++;
                    contadorConsecutivo++;
                } else if (alumnoCicloEach.isMatriculado()) {
                    contadorConsecutivo = 0;
                }
            }
            if (contadorConsecutivo == maxConsecutivos || contadorIntercalado == maxIntercalados) {
                SituacionAcademica situacionDesertor = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_D.getValue());
                alumnoCiclo.setSituacionFinal(situacionDesertor);
                alumno.setSituacionAcademica(situacionDesertor);
                alumnoCicloDAO.update(alumnoCiclo);
                return;
            }
            
        } else {

            //AlumnoCicloCurso alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);
            Long ciclosEstudiados = alumnoCicloDAO.countCiclosEstudiados(alumno, cicloAcademico);

            //todos los ciclos anteriores
            Integer credAcumuladosAlumno = BigDecimal.ZERO.intValue();
            Integer credAprAcumuladosAlumno = BigDecimal.ZERO.intValue();

            //por ciclo actual
            Integer credCursadosAlumnoCiclo = BigDecimal.ZERO.intValue();
            Integer credCursadosAproAlumnoCiclo = BigDecimal.ZERO.intValue();
            
            Integer cursosInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();
            Integer cursosAprInscritosAlumnoCiclo = BigDecimal.ZERO.intValue();

            /*Obtenemos la informacion del ciclo actual*/
            List<AlumnoCicloCurso> alumnosCicloCursoByAlumnoCiclo = alumnoCicloCursoDAO.allOperativesByAlumnoCiclo(alumno, cicloAcademico);
            
            BigDecimal sumNotasCreditos = BigDecimal.ZERO;
            BigDecimal sumCreditos = BigDecimal.ZERO;
            
            boolean generarTrika = false;
            //procesamos la informacion del ciclo actual
            for (AlumnoCicloCurso alumnoCicloCursoEach : alumnosCicloCursoByAlumnoCiclo) {
                credCursadosAlumnoCiclo += alumnoCicloCursoEach.getCreditos();
                cursosInscritosAlumnoCiclo += 1;
                credAcumuladosAlumno += alumnoCicloCursoEach.getCreditos();
                Integer vecesEstudiadoCurso = alumnoCicloCursoDAO.countByCursoAlumnoAnterioresCiclo(alumnoCicloCursoEach.getCurso(), alumno, cicloAcademico).intValue();
                vecesEstudiadoCurso++;
                alumnoCicloCursoEach.setVecesCursado(vecesEstudiadoCurso);
                
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
                
                if (vecesEstudiadoCurso == 3 && !alumnoCicloCursoEach.isAprobado()) {
                    generarTrika = true;
                }
            }
            //si la nota se modificó y un alumno trikeado deja de serlo
            if (!generarTrika && (alumnoCicloSiguienteInha != null && alumnoCicloSiguienteInha.getSituacionFinal().isTrikeado())) {
                alumnoCicloDAO.delete(alumnoCicloSiguienteInha);
            }

            //obtenemos la informacion de los ciclos anteriores para los acumulados
            List<AlumnoCicloCurso> alumnosCicloCursosCiclosAnteriores = alumnoCicloCursoDAO.allOperativesByAlumnoAnterioresCiclo(alumno, cicloAcademico);
            BigDecimal sumNotasCreditosTotal = sumNotasCreditos;
            BigDecimal sumCreditosTotal = sumCreditos;

            //procesamos la informacion de los ciclos anteriores
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
            if (alumnoCicloAnterior != null) {
                alumnoCiclo.setSituacionInicio(alumnoCicloAnterior.getSituacionFinal());
            }
            if (alumnoCicloAnteriorInha != null) {
                if ((TypesUtil.getInt(alumnoCicloAnteriorInha.getCicloAcademico().getCodigo()) > TypesUtil.getInt(alumnoCicloAnterior.getCicloAcademico().getCodigo()))) {
                    alumnoCiclo.setSituacionInicio(alumnoCicloAnteriorInha.getSituacionFinal());
                }
            }

            //falta evaluar que sucede cuando todos los cursos son de evaluacion letras
            if (alumnosCicloCursoByAlumnoCiclo.size() == BigDecimal.ONE.intValue()) {
                alumnoCiclo.setEstaAprobado(alumnosCicloCursoByAlumnoCiclo.get(0).getEstaAprobado());
            } else {
                Integer aprobado = evaluateEstaAprobado(promedio, alumno);
                alumnoCiclo.setEstaAprobado(aprobado);
            }
            alumnoCicloDAO.update(alumnoCiclo);
            alumnoCiclo.getId();
            
            logger.debug("Ciclo Academico {} {}, Situacion Inicial Id {} Codigo {} Nombre {}", cicloAcademico.getId(), cicloAcademico.getDescripcion(), alumnoCiclo.getSituacionInicio().getId(), alumnoCiclo.getSituacionInicio().getCodigo(), alumnoCiclo.getSituacionInicio().getNombre());
            
            SituacionAcademica situacionAcademicaFinal = calculateSitutacionAcadFinal(alumno, alumnoCiclo, alumnoCiclo.getSituacionInicio(), ciclosEstudiados.intValue(), alumnoCicloAnteriorInha);
            if (situacionAcademicaFinal != null) {
                logger.debug("Nueva situacion academica id {}, codigo {} {}", situacionAcademicaFinal.getId(), situacionAcademicaFinal.getCodigo(), situacionAcademicaFinal.getNombre());
            } else {
                logger.debug("No se pudo hallar su situacion final");
            }
            alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
            alumnoCicloDAO.update(alumnoCiclo);
            /*
                if (generarTrika && (alumnoCicloSiguienteInha == null || !alumnoCicloSiguienteInha.getSituacionFinal().isTrikeado())
                && situacionAcademicaFinal.isCodigoS4()) {
             */
            if (generarTrika && situacionAcademicaFinal.isCodigoS6()) {
                logger.debug("Generara registro fantasma trika");
                //   CicloAcademico siguienteCicloReg = cicloAcademicoDAO.findSiguienteRegularActivo(alumnoCiclo.getCicloAcademico());
                AlumnoCiclo alumnoCicloSiguienteRegular = alumnoCicloDAO.findByAlumnoCicloEstado(alumno, siguienteCicloReg, Arrays.asList(EstadoMatriculaEnum.INH, EstadoMatriculaEnum.MAT));
                SituacionAcademica situacionTrika = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_T.getValue());
                
                if (alumnoCicloSiguienteRegular == null) {
                    alumnoCicloSiguienteRegular = new AlumnoCiclo();
                    alumnoCicloSiguienteRegular.defaultValuesToCreate(alumno, siguienteCicloReg, usuario, today);
                    alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.INH);
                    alumnoCicloSiguienteRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloSiguienteRegular.setSituacionFinal(situacionTrika);
                    alumnoCicloDAO.save(alumnoCicloSiguienteRegular);
                } else {
                    alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.INH);
                    alumnoCicloSiguienteRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloSiguienteRegular.setSituacionFinal(situacionTrika);
                    alumnoCicloDAO.update(alumnoCicloSiguienteRegular);
                }
            } else if (situacionAcademicaFinal.isCodigoS6() && cicloAcademico.isTipoRegular()) {
                logger.debug("Generara registro fantasma prueba codigo situacion 3");
                //   CicloAcademico siguienteCicloReg = cicloAcademicoDAO.findSiguienteRegularActivo(alumnoCiclo.getCicloAcademico());
                AlumnoCiclo alumnoCicloSiguienteRegular = alumnoCicloDAO.findByAlumnoCicloEstado(alumno, siguienteCicloReg, Arrays.asList(EstadoMatriculaEnum.INH, EstadoMatriculaEnum.MAT));
                SituacionAcademica situacionAcademicaS3 = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_3.getValue());
                
                if (alumnoCicloSiguienteRegular == null) {
                    alumnoCicloSiguienteRegular = new AlumnoCiclo();
                    alumnoCicloSiguienteRegular.defaultValuesToCreate(alumno, siguienteCicloReg, usuario, today);
                    alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.INH);
                    alumnoCicloSiguienteRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloSiguienteRegular.setSituacionFinal(situacionAcademicaS3);
                    alumnoCicloDAO.save(alumnoCicloSiguienteRegular);
                } else {
                    alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.INH);
                    alumnoCicloSiguienteRegular.setSituacionInicio(situacionAcademicaFinal);
                    alumnoCicloSiguienteRegular.setSituacionFinal(situacionAcademicaS3);
                    alumnoCicloDAO.update(alumnoCicloSiguienteRegular);
                }
            }
            
            Alumno alumnoUpd = new Alumno();
            alumnoUpd.setId(alumno.getId());
            alumnoUpd.setCicloActivo(alumnoCiclo.getCicloAcademico());
            alumnoUpd.setCreditosAprobados(alumnoCiclo.getCreditosAprobadosAcumulados());
            alumnoUpd.setSituacionAcademica(situacionAcademicaFinal);
            alumnoDAO.updateSituacionCicloCapa(alumno);
        }
        
        if (!Arrays.asList(S_X.getValue(), S_XD.getValue(), S_EM.getValue(), S_7.getValue(), S_D.getValue()).contains(alumno.getSituacionAcademica().getCodigo())) {
            
            AlumnoCiclo alumnoCicloSiguienteRegular = alumnoCicloDAO.findByAlumnoCiclo(alumno, siguienteCicloReg);
            if (alumnoCicloSiguienteRegular == null) {
                if (siguienteCicloReg.getCodigoInt() < cicloActivo.getCodigoInt()) {
                    if (alumnoCicloSiguienteRegular == null) {
                        alumnoCicloSiguienteRegular = new AlumnoCiclo();
                        alumnoCicloSiguienteRegular.defaultValuesToCreate(alumno, siguienteCicloReg, usuario, today);
                        alumnoCicloSiguienteRegular.setSituacionInicio(alumno.getSituacionAcademica());
                        alumnoCicloSiguienteRegular.setSituacionFinal(alumno.getSituacionAcademica());
                        alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.NMAT);
                        alumnoCicloDAO.save(alumnoCicloSiguienteRegular);
                    }
                    /*else {
                        alumnoCicloSiguienteRegular.setSituacionInicio(alumno.getSituacionAcademica());
                        alumnoCicloSiguienteRegular.setSituacionFinal(alumno.getSituacionAcademica());
                        alumnoCicloSiguienteRegular.setEstado(EstadoMatriculaEnum.NMAT);
                        alumnoCicloDAO.update(alumnoCicloSiguienteRegular);
                    }*/
                    promediarHistorialNotas(alumno, siguienteCicloReg, usuario, today);
                }
            }
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
