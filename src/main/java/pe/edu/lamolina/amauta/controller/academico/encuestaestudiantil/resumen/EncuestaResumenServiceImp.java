package pe.edu.lamolina.amauta.controller.academico.encuestaestudiantil.resumen;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.lamolina.amauta.dao.encuesta.ConfiguraEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.CursoSinEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaCursoDAO;
import pe.edu.lamolina.amauta.dao.encuesta.EncuestaEstudiantilDAO;
import pe.edu.lamolina.amauta.dao.encuesta.ExamenVirtualDAO;
import pe.edu.lamolina.amauta.dao.encuesta.PeriodoEncuestaDAO;
import pe.edu.lamolina.amauta.dao.encuesta.TipoExamenVirtualDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.GrupoSeccion;
import pe.edu.lamolina.model.encuestaestudiantil.ConfiguraEncuesta;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaCurso;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaEstudiantil;
import pe.edu.lamolina.model.enums.EncuestaEstadoEnum;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.MOD;
import static pe.edu.lamolina.model.enums.TipoDictadoGrupoSeccionEnum.SEM;
import pe.edu.lamolina.model.enums.TipoExamenVirtualEnum;
import pe.edu.lamolina.model.examen.ExamenVirtual;
import pe.edu.lamolina.model.examen.TipoExamenVirtual;

@Service
@Transactional(readOnly = true)
public class EncuestaResumenServiceImp implements EncuestaResumenService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoExamenVirtualDAO tipoExamenVirtualDAO;

    @Autowired
    ExamenVirtualDAO examenVirtualDAO;

    @Autowired
    EncuestaEstudiantilDAO encuestaEstudiantilDAO;

    @Autowired
    PeriodoEncuestaDAO periodoEncuestaDAO;

    @Autowired
    ConfiguraEncuestaDAO configuraEncuestaDAO;

    @Autowired
    CursoSinEncuestaDAO cursoSinEncuestaDAO;

    @Autowired
    EncuestaCursoDAO encuestaCursoDAO;

    @Override
    public EncuestaEstudiantil findEncuestaCursoWithResumen(CicloAcademico cicloAcademico) {
        logger.debug("ciclo encuesta {}",cicloAcademico.getId());
        logger.debug("ciclo encuesta {}",cicloAcademico.getCodigo());
        logger.debug("TipoExamenVirtual");
        TipoExamenVirtual tipoEncuesta = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_CUR);
        ExamenVirtual encuestaModelo = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuesta);
        logger.debug("encuestaModelo");
        EncuestaEstudiantil encuesta = null;
        if (encuestaModelo != null) {
            logger.debug("encuestaModelo not null find by ciclo");
            encuesta = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModelo);
        }

        if (encuesta == null) {
            logger.debug("EncuestaEstudiantil no creado");
            encuesta = new EncuestaEstudiantil();
            encuesta.setEstadoEnum(EncuestaEstadoEnum.NCRE);
        }

        encuesta.setPeriodosEncuesta(new ArrayList());
        encuesta.setConfiguraEncuesta(new ArrayList());
        encuesta.setCursosNoEncuestar(new ArrayList());

        if (encuesta.getId() == null) {
            return encuesta;
        }
        logger.debug("all periodo encuesta");
        encuesta.setPeriodosEncuesta(periodoEncuestaDAO.allByEncuesta(encuesta));
        logger.debug("ConfiguraEncuesta");
        ConfiguraEncuesta cfg = configuraEncuestaDAO.findByEncuesta(encuesta);
        boolean esSimultaneo = false;
        logger.debug("esSimultaneo {}", esSimultaneo);
        if (cfg != null) {
            if (cfg.getSimultaneo() == 1) {
                TipoExamenVirtual tipoEncuestaDoc = tipoExamenVirtualDAO.findByEnum(TipoExamenVirtualEnum.ENC_DOC);
                ExamenVirtual encuestaModeloDoc = examenVirtualDAO.findEncuestaActivaByTipo(tipoEncuestaDoc);
                EncuestaEstudiantil encuestaDoc = encuestaEstudiantilDAO.findByCicloEncuesta(cicloAcademico, encuestaModeloDoc);
                cfg = configuraEncuestaDAO.findByEncuesta(encuestaDoc);
                esSimultaneo = true;
            }
            encuesta.getConfiguraEncuesta().add(cfg);
        }

        logger.debug("esSimultaneo {}", esSimultaneo);

        encuesta.setCursosNoEncuestar(cursoSinEncuestaDAO.allByEncuestaEstudiantil(encuesta));

        logger.debug("esSimultaneo {}", esSimultaneo);

        List<EncuestaCurso> encCursos = encuestaCursoDAO.allByEncuestaEstudiantil(encuesta, esSimultaneo);

        logger.debug("encCursos {}", encCursos.size());

        int activos = 0;
        int anulados = 0;
        int innecesa = 0;
        int cerrados = 0;
        int sinperio = 0;
        int posgrados = 0;
        int pregrados = 0;
        int modulares = 0;
        int semestrales = 0;

        for (EncuestaCurso encCurso : encCursos) {
            switch (encCurso.getEstadoEnum()) {
                case ACT:
                    activos++;
                    if (encCurso.getModalidadEstudio().isPostgrado()) {
                        posgrados++;
                    }
                    if (encCurso.getModalidadEstudio().isPregrado()) {
                        pregrados++;
                    }

                    GrupoSeccion gpoSeccion;
                    if (esSimultaneo) {
                        gpoSeccion = encCurso.getEncuestaDocente().getDocenteSeccion().getSeccion().getGrupoSeccion();
                    } else {
                        gpoSeccion = encCurso.getGrupoSeccion();
                    }

                    if (gpoSeccion.getTipoDictadoEnum() == MOD) {
                        modulares++;
                    }
                    if (gpoSeccion.getTipoDictadoEnum() == SEM) {
                        semestrales++;
                    }

                    break;
                case ANU:
                    anulados++;
                    break;
                case TEO:
                    innecesa++;
                    break;
                case CER:
                    cerrados++;
                    break;
                case FECH:
                    sinperio++;
                    break;
            }

        }

        encuesta.setEncuestasActivas(activos);
        encuesta.setEncuestasAnuladas(anulados);
        encuesta.setEncuestasCerradas(cerrados);
        encuesta.setEncuestasInnecesarias(innecesa);
        encuesta.setEncuestasSinPeriodo(sinperio);
        encuesta.setEncuestasPosgrado(posgrados);
        encuesta.setEncuestasPregrado(pregrados);
        encuesta.setEncuestasModulares(modulares);
        encuesta.setEncuestasSemestrales(semestrales);

        Long totalEncuestaAlumnoPregrado = encuestaEstudiantilDAO.countEncuestaAlumno(cicloAcademico, ModalidadEstudioEnum.PRE, null).longValue();
        Long pendienteEncuestaAlumnoPregrado = encuestaEstudiantilDAO.countEncuestaAlumno(cicloAcademico, ModalidadEstudioEnum.PRE, EncuestaEstudiantilEstadoEnum.PEND).longValue();
        Long totalEncuestaAlumnoPosgrado = encuestaEstudiantilDAO.countEncuestaAlumno(cicloAcademico, ModalidadEstudioEnum.EPG, null).longValue();
        Long pendienteEncuestaAlumnoPosgrado = encuestaEstudiantilDAO.countEncuestaAlumno(cicloAcademico, ModalidadEstudioEnum.EPG,  EncuestaEstudiantilEstadoEnum.PEND).longValue();

        logger.debug("totalEncuestaAlumnoPregrado {}", totalEncuestaAlumnoPregrado);
        logger.debug("pendienteEncuestaAlumnoPregrado {}", pendienteEncuestaAlumnoPregrado);
        logger.debug("totalEncuestaAlumnoPosgrado {}", totalEncuestaAlumnoPosgrado);
        logger.debug("pendienteEncuestaAlumnoPosgrado {}", pendienteEncuestaAlumnoPosgrado);

        encuesta.setTotalEncuestaAlumnoPregrado(totalEncuestaAlumnoPregrado);
        encuesta.setPendienteEncuestaAlumnoPregrado(pendienteEncuestaAlumnoPregrado);
        
        encuesta.setTotalEncuestaAlumnoPosgrado(totalEncuestaAlumnoPosgrado);
        encuesta.setPendienteEncuestaAlumnoPosgrado(pendienteEncuestaAlumnoPosgrado);
                
        logger.debug("activos {}", activos);
        logger.debug("anulados {}", anulados);
        logger.debug("innecesa {}", innecesa);
        logger.debug("cerrados {}", cerrados);
        logger.debug("sinperio {}", sinperio);
        logger.debug("posgrados {}", posgrados);
        logger.debug("pregrados {}", pregrados);
        logger.debug("modulares {}", modulares);
        logger.debug("semestrales {}", semestrales);

        logger.debug("getObjetivosEncuestados {}", encuesta.getObjetivosEncuestados());
        logger.debug("getEncuestasProgramadas {}", encuesta.getEncuestasProgramadas());
        logger.debug("getObjetivosEncuesta {}", encuesta.getObjetivosEncuesta());
        logger.debug("getEncuestasEjecutadas {}", encuesta.getEncuestasEjecutadas());

        return encuesta;
    }

}
