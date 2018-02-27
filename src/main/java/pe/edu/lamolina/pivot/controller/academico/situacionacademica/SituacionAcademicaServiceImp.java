package pe.edu.lamolina.pivot.controller.academico.situacionacademica;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.SituacionConfig;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionConfigDAO;

@Service
@Transactional(readOnly = true)
public class SituacionAcademicaServiceImp implements SituacionAcademicaService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;

    @Autowired
    SituacionConfigDAO situacionConfigDAO;

    @Override
    public SituacionAcademica findSituacionFinal(AlumnoCiclo alumnoCiclo, Alumno alumno, CicloAcademico cicloAcademico) {
        SituacionConfig situacionConfig = new SituacionConfig();
        situacionConfig.setSituacionInicial(alumno.getSituacionAcademica());
        situacionConfig.setAprobado(alumnoCiclo.getEstaAprobado());
        situacionConfig.setCiclosEstudiados(alumno.getCiclosEstudiados());
        situacionConfig.setAutorizado(BigDecimal.ZERO.intValue());
        situacionConfig.setCapa(alumno.getCreditosAprobados());
        situacionConfig.setSiguienteCiclo(BigDecimal.ONE.intValue());
        situacionConfig.setTramite(-1);
        situacionConfig.setCicloRegular(cicloAcademico.isTipoRegular() ? BigDecimal.ONE.intValue() : BigDecimal.ZERO.intValue());

        logger.debug("Alumno {}, Situacion Inicial {}, Esta Aprobado {}, Ciclos Estudiados {}, Capa {}, Ciclo Regular {}",
                alumno.getId(), alumno.getSituacionAcademica().getId(), alumnoCiclo.getEstaAprobado(),
                alumno.getCiclosEstudiados(), situacionConfig.getCapa(), situacionConfig.getCicloRegular());

        //   SituacionConfig situacionFinal = situacionConfigDAO.findForSituacionFinal(situacionConfig);
        SituacionConfig situacionFinal = situacionConfigDAO.findsSituacionConfig(situacionConfig);
        return situacionFinal.getSituacionFinal();
    }

}
