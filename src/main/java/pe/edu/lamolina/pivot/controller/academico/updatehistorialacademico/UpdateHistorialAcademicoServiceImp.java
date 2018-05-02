package pe.edu.lamolina.pivot.controller.academico.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.controller.academico.situacionacademica.SituacionAcademicaService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class UpdateHistorialAcademicoServiceImp implements UpdateHistorialAcademicoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    SituacionAcademicaService situacionAcademicaService;

    @Autowired
    CursoDAO cursoDAO;

    @Override
    public Alumno allInfo(Alumno alumno) {
        Alumno alu = alumnoDAO.findAllInfo(alumno.getId());
        return alu;
    }

    @Override
    public List<CicloAcademico> allCicloAcademico() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        return cicloAcademicoDAO.allCicloAcademicoByRange((year - 10), (year + 1));
    }

    @Override
    @Transactional
    public void updateHistorialAcademico(Alumno alumnoForm, DataSessionPivot ds) {
        boolean calcularSituacionAcadFinal = Boolean.TRUE;
        Usuario usuario = ds.getUsuario();
        Alumno alumno = alumnoDAO.find(alumnoForm);
        logger.debug("alumno id   {} codigo {} ", alumno.getId(), alumno.getCodigo());
        List<AlumnoCiclo> alumnosCiclo = alumnoForm.getAlumnoCiclo();
        List<AlumnoCiclo> alumnosCicloDb = alumnoCicloDAO.allByAlumno(alumno);
        logger.debug("existen  {} alumnoCiclo en db", alumnosCicloDb.size());
        if (!alumnosCicloDb.isEmpty()) {
            List<Long> alumnoCicloss = new ArrayList();
            for (AlumnoCiclo alumnoCiclo : alumnosCiclo) {
                if (alumnoCiclo.getId() != null) {
                    alumnoCicloss.add(alumnoCiclo.getId());
                }
            }
            Map<Long, AlumnoCiclo> alumnosCicloMap = TypesUtil.convertListToMap("id", alumnosCicloDb);
            List<AlumnoCiclo> alumnosCicloDelete = new ArrayList();
            for (Map.Entry<Long, AlumnoCiclo> entry : alumnosCicloMap.entrySet()) {
                Long key = entry.getKey();
                if (!alumnoCicloss.contains(key)) {
                    alumnosCicloDelete.add(entry.getValue());
                }
            }
            for (AlumnoCiclo alumnoCiclo : alumnosCicloDelete) {
                if (alumnoCiclo.getEstadoEnum() != EstadoMatriculaEnum.NMAT) {
                    logger.debug("remove alumnoCiclo {}", alumnoCiclo.getId());
                    alumnoCicloCursoDAO.deleteByAlumnoCiclo(alumnoCiclo);
                    alumnoCicloDAO.delete(alumnoCiclo);
                }
            }
        }

        for (AlumnoCiclo alumnoCicloForm : alumnosCiclo) {

            CicloAcademico cicloAcademico = cicloAcademicoDAO.find(alumnoCicloForm.getCicloAcademico());

            CicloAcademico cicloAcademicoAnterior = cicloAcademicoDAO.findAnteriorActivo(cicloAcademico);
            AlumnoCiclo alumnoCicloAnterior = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademicoAnterior);

            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
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

            List<AlumnoCicloCurso> alumnosCicloCurso = alumnoCicloForm.getAlumnoCicloCurso();
            List<AlumnoCicloCurso> alumnosCicloCursoDb = alumnoCicloCursoDAO.allByAlumnoCiclo(alumnoCiclo);
            logger.debug("existen  {} AlumnoCicloCurso en db", alumnosCicloCursoDb.size());
            if (!alumnosCicloCursoDb.isEmpty()) {
                List<Long> alumnoCicloCursoss = new ArrayList();
                for (AlumnoCicloCurso alumnoCicloCurso : alumnosCicloCurso) {
                    if (alumnoCicloCurso.getId() != null) {
                        alumnoCicloCursoss.add(alumnoCicloCurso.getId());
                    }
                }
                Map<Long, AlumnoCicloCurso> alumnosCicloCursoMap = TypesUtil.convertListToMap("id", alumnosCicloCursoDb);
                List<AlumnoCicloCurso> alumnosCicloCursoDelete = new ArrayList();
                for (Map.Entry<Long, AlumnoCicloCurso> entry : alumnosCicloCursoMap.entrySet()) {
                    Long key = entry.getKey();
                    if (!alumnoCicloCursoss.contains(key)) {
                        alumnosCicloCursoDelete.add(entry.getValue());
                    }
                }
                for (AlumnoCicloCurso alumnoCicloCurso : alumnosCicloCursoDelete) {
                    if (alumnoCicloCurso.getEstadoEnum() != EstadoMatriculaEnum.NMAT) {
                        logger.debug("remove alumnoCiclo {}", alumnoCicloCurso.getId());
                        alumnoCicloCursoDAO.delete(alumnoCicloCurso);
                    }
                }
            }

            for (AlumnoCicloCurso alumnoCicloCursoForm : alumnosCicloCurso) {

                Curso curso = cursoDAO.find(alumnoCicloCursoForm.getCurso().getId());
                AlumnoCicloCurso alumnoCicloCurso = null;
                if (alumnoCicloCursoForm.getId() != null) {
                    alumnoCicloCurso = alumnoCicloCursoDAO.find(alumnoCicloCursoForm);
                } else {
                    alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);
                }

                if (alumnoCicloCurso == null) {
                    alumnoCicloCurso = new AlumnoCicloCurso();
                    alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);
                    //  update creditos from form
                    alumnoCicloCurso.setCreditos(alumnoCicloCursoForm.getCreditos());
                    alumnoCicloCurso.setCurso(curso);
                    //  aprobado from form
                    //Integer aprobado = this.evaluateEstaAprobado(new BigDecimal(alumnoCicloCursoForm.getNota()), alumno);
                    //alumnoCicloCurso.setEstaAprobado(aprobado);
                    alumnoCicloCurso.setEstaAprobado(0);

                    alumnoCicloCurso.setEstado(EstadoMatriculaEnum.MAT);
                    alumnoCicloCurso.setFechaModificacion(today.toDate());
                    alumnoCicloCurso.setFechaRegistro(today.toDate());
                    //  update nota from form
                    alumnoCicloCurso.setNota(alumnoCicloCursoForm.getNota());
                    alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.ACTA);
                    alumnoCicloCurso.setRegistroActivo(BigDecimal.ONE.intValue());
                    alumnoCicloCurso.setUserModificacion(usuario);
                    alumnoCicloCurso.setUsuarioRegistro(usuario);
                    //  update veces curso
                    alumnoCicloCurso.setVecesCursado(1);
                    alumnoCicloCursoDAO.save(alumnoCicloCurso);
                    alumnoCicloCurso.getId();
                } else {
                    alumnoCicloCurso.setFechaModificacion(today.toDate());
                    //  update nota from form
                    alumnoCicloCurso.setNota(alumnoCicloCursoForm.getNota());
                    alumnoCicloCurso.setUserModificacion(usuario);
                    alumnoCicloCurso.setCurso(curso);
                    //  aprobado from form
                    //Integer aprobado = this.evaluateEstaAprobado(new BigDecimal(alumnoCicloCursoForm.getNota()), alumno);
                    //alumnoCicloCurso.setEstaAprobado(aprobado);

                    alumnoCicloCursoDAO.update(alumnoCicloCurso);
                    alumnoCicloCurso.getId();
                }

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
                Integer aprobado = this.evaluateEstaAprobado(promedio, alumno);
                alumnoCiclo.setEstaAprobado(aprobado);
            }
            alumnoCicloDAO.update(alumnoCiclo);
            alumnoCiclo.getId();
//            if (calcularSituacionAcadFinal) {
//                SituacionAcademica situacionAcademicaIni = alumnoCicloAnterior != null ? alumnoCicloAnterior.getSituacionFinal() : alumno.getSituacionAcademica();
//                SituacionAcademica situacionAcademicaFinal = situacionAcademicaService.findSituacionFinal(alumnoCiclo, situacionAcademicaIni, alumno.getCiclosEstudiados(), alumno.getCreditosAprobados(), cicloAcademico);
//
//                alumnoCiclo.setSituacionFinal(situacionAcademicaFinal);
//                alumnoCicloDAO.update(alumnoCiclo);
//
//                Alumno alumnoUpd = new Alumno();
//                alumnoUpd.setId(alumno.getId());
//                alumnoUpd.setCicloActivo(cicloAcademico);
//                alumnoUpd.setSituacionAcademica(situacionAcademicaFinal);
//                alumnoDAO.updateCicloActivoSituacionAcad(alumnoUpd);
//            }

        }
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

    @Override
    public ObjectNode toJson(Object object) {
        ObjectNode json = JsonHelper.createJson(object, JsonNodeFactory.instance);
        return json;
    }

    @Override
    public List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno) {

        List<AlumnoCicloCurso> cursosCiclos = alumnoCicloCursoDAO.allByAlumno(alumno);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumnoCiclo.id", "alumnoCiclo", cursosCiclos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", cursosCiclos);

        List<AlumnoCiclo> promedios = new ArrayList(mapAlumnoCiclo.values());
        for (AlumnoCiclo promedio : promedios) {
            List<AlumnoCicloCurso> cursos = mapAlumnoCicloCurso.get(promedio.getId());
            promedio.setAlumnoCicloCurso(cursos);
        }

        return promedios;
    }

    @Override
    public List<Curso> allCursoByName(String nombre) {
        return cursoDAO.allCursoByName(nombre);
    }

}
