package pe.edu.lamolina.pivot.controller.academico.matriculable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void procesarPrioridadAlumno(MatriculaResumen matriculaResumen) {
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastActiveRegByAlumno(matriculaResumen.getAlumno());

        if (matriculaResumen.getAlumno().getSituacionAcademica().isCodigoS8()) {
            MatriculaResumen matriculaResumenUpd = new MatriculaResumen();

            matriculaResumenUpd.setId(matriculaResumen.getId());
            matriculaResumenUpd.setPuntajePrioridad(BigDecimal.valueOf(6000));

            matriculaResumenDAO.updatePuntajePrioridad(matriculaResumenUpd);
            return;
        }
        if (alumnoCiclo != null) {
            if (alumnoCiclo.getCreditosAcumulados().compareTo(BigDecimal.ZERO.intValue()) == 0
                    || alumnoCiclo.getCreditosCursadosCiclo().compareTo(BigDecimal.ZERO.intValue()) == 0) {
                logger.debug("capa 0");
                return;
            }

            logger.debug("registro {}", matriculaResumen.getAlumno().getCicloActivo().getDescripcion());
            BigDecimal capa = new BigDecimal(matriculaResumen.getAlumno().getCreditosAprobados());
            BigDecimal cca = new BigDecimal(alumnoCiclo.getCreditosAcumulados());

            BigDecimal caps = new BigDecimal(alumnoCiclo.getCreditosAprobadosCiclo());
            BigDecimal ccs = new BigDecimal(alumnoCiclo.getCreditosCursadosCiclo());

            BigDecimal factor1 = capa.divide(cca, 12, RoundingMode.HALF_UP);
            BigDecimal factor2 = caps.divide(ccs, 12, RoundingMode.HALF_UP);

            MatriculaResumen matriculaResumenUpd = new MatriculaResumen();
            BigDecimal resultFactor = factor1.multiply(factor2);
            resultFactor = resultFactor.multiply(alumnoCiclo.getPromedioCiclo());
            matriculaResumenUpd.setId(matriculaResumen.getId());
            matriculaResumenUpd.setPuntajePrioridad(resultFactor);

            matriculaResumenDAO.updatePuntajePrioridad(matriculaResumenUpd);
        }
    }

}
