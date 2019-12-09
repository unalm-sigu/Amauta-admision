package pe.edu.lamolina.pivot.controller.academico.situacionacademica;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.academico.SituacionConfig;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
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

    @Autowired
    AlumnoDAO alumnoDAO;

    @Override
    public SituacionAcademica findSituacionFinal(
            AlumnoCiclo alumnoCiclo,
            SituacionAcademica situacionAcademicaIni,
            Integer ciclosEstudiados,
            Integer capa,
            CicloAcademico cicloAcademico) {
        /* if (situacionAcademicaIni == null) {
            alumno = alumnoDAO.find(alumno);
            if (alumno.getModalidadEstudio().isPregrado()) {
                situacionAcademicaIni = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_8.getValue());
            } else {
                situacionAcademicaIni = situacionAcademicaDAO.findByCodigo(SituacionAcademicaEnum.S_N.getValue());
            }
        }*/
        SituacionConfig config = new SituacionConfig();
        config.setSituacionInicial(situacionAcademicaIni);
        config.setAprobado(alumnoCiclo.getEstaAprobado());
        config.setCiclosEstudiados(ciclosEstudiados);
        config.setAutorizado(BigDecimal.ZERO.intValue());
        config.setCapa(capa);
        config.setSiguienteCiclo(BigDecimal.ONE.intValue());
        config.setTramite(-1);
        config.setCicloRegular(cicloAcademico.isTipoRegular() ? BigDecimal.ONE.intValue() : BigDecimal.ZERO.intValue());
        /*
        logger.debug("Situacion Inicial {}, Esta Aprobado {}, Ciclos Estudiados {}, Capa {}, Ciclo Regular {}",
                situacionAcademicaIni.getId(), alumnoCiclo.getEstaAprobado(),
                ciclosEstudiados, situacionConfig.getCapa(), situacionConfig.getCicloRegular());
         */
        //   SituacionConfig situacionFinal = situacionConfigDAO.findForSituacionFinal(situacionConfig);
        SituacionConfig situacionFinal = situacionConfigDAO.findsSituacionConfig(config);
        if (situacionFinal == null) {
            ObjectUtil.printAttr(config);
        }
        return situacionFinal != null ? situacionAcademicaDAO.find(situacionFinal.getSituacionFinal().getId()) : null;
    }

}
