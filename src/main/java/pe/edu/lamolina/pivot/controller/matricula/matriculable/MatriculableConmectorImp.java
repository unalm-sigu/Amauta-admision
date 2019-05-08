package pe.edu.lamolina.pivot.controller.matricula.matriculable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.CAPA_ULTIMO_CICLO;

@Service
@Transactional(readOnly = true)
public class MatriculableConmectorImp implements MatriculableConnector {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    EgresadoDAO egresadoDAO;

    @Override
    @Transactional
    public MatriculaResumen procesarPrioridadAlumno(MatriculaResumen matriculaResumen, AlumnoCiclo alumnoCiclo) {
        BigDecimal capa = new BigDecimal(alumnoCiclo.getCreditosAprobadosAcumulados());
        BigDecimal cca = new BigDecimal(alumnoCiclo.getCreditosAcumulados());
        BigDecimal caps = new BigDecimal(alumnoCiclo.getCreditosAprobadosCiclo());
        BigDecimal ccs = new BigDecimal(alumnoCiclo.getCreditosCursadosCiclo());
        BigDecimal pps = alumnoCiclo.getPromedioCiclo();

        if (alumnoCiclo.getCreditosAcumulados().compareTo(BigDecimal.ZERO.intValue()) == 0) {
            matriculaResumen.setPuntajePrioridad(BigDecimal.ZERO);
            matriculaResumen.setCreditosAprobadosAcumulados(0);
            matriculaResumen.setCreditosAcumulados(0);
            matriculaResumen.setCreditosAprobadosCiclo(0);
            matriculaResumen.setCreditosCursadosCiclo(0);
            matriculaResumen.setPromedioSemestral(BigDecimal.ZERO);
            return matriculaResumen;
        }
        caps = caps.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : caps;
        capa = capa.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : capa;
        pps = pps.equals(BigDecimal.ZERO) ? new BigDecimal(0.004) : pps;

        BigDecimal factor1 = capa.multiply(caps).multiply(pps);// capa.divide(cca, 12, RoundingMode.HALF_UP);
        BigDecimal factor2 = ccs.multiply(cca);//caps.divide(ccs, 12, RoundingMode.HALF_UP);
        factor2 = factor2.equals(BigDecimal.ZERO) ? BigDecimal.ONE : factor2;
     
        BigDecimal puntajePrioridad = factor1.divide(factor2, 12, RoundingMode.HALF_UP);

        matriculaResumen.setCreditosAprobadosAcumulados(Integer.parseInt(capa.toString()));
        matriculaResumen.setCreditosAcumulados(Integer.parseInt(cca.toString()));
        matriculaResumen.setCreditosAprobadosCiclo(Integer.parseInt(caps.toString()));
        matriculaResumen.setCreditosCursadosCiclo(Integer.parseInt(ccs.toString()));
        matriculaResumen.setPromedioSemestral(pps);
        if (matriculaResumen.getCreditosAprobadosAcumulados() >= CAPA_ULTIMO_CICLO) {
            matriculaResumen.setEsUltimoCiclo(Boolean.TRUE);
        }
        matriculaResumen.setCicloAcademicoInfo(alumnoCiclo.getCicloAcademico());
        matriculaResumen.setPuntajePrioridad(puntajePrioridad);
        return matriculaResumen;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void procesarEgresado(String codigoAlumno, String codigoCarrera, String codigoFacultad, String codigoCiclo, Egresado egresado) {

        Alumno alumno = alumnoDAO.findByCodigo(codigoAlumno);
        if (alumno == null) {
            logger.error(" alumno no encontrado, codigo {} ", codigoAlumno);
            return;
        }
        Egresado egresadoDB = egresadoDAO.findByAlumno(alumno);
        if (egresadoDB != null) {
            return;
        }
        Carrera carrera = null;
        if (StringUtils.isNotBlank(codigoCarrera)) {
            carrera = carreraDAO.findByCodigo(codigoCarrera);
        }
        Facultad facultad = null;
        if (StringUtils.isNotBlank(codigoFacultad)) {
            facultad = facultadDAO.findByCodigo(codigoFacultad);
        }
        CicloAcademico cicloAcademico = null;
        if (StringUtils.isNotBlank(codigoCiclo)) {
            cicloAcademico = cicloAcademicoDAO.findByCodigo(codigoCiclo + "0");
        }

        egresado.setAlumno(alumno);
        egresado.setCarrera(carrera);
        egresado.setCicloAcademico(cicloAcademico);
        egresado.setFacultad(facultad);
        egresadoDAO.save(egresado);
    }

}
