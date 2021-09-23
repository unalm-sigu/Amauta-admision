package pe.edu.lamolina.amauta.controller.academico.graduado;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteTituloDAO;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.SituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.BACHI;
import static pe.edu.lamolina.model.enums.TipoTramiteEnum.TIT;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

@Service
@Transactional(readOnly = true)
public class GraduadoServiceImp implements GraduadoService {

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    TramiteTituloDAO tramiteTituloDAO;

    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<ObtencionGrado> allEgresadoByDynatable(DynatableFilter filter, List<Carrera> carreras, String todo) {
        return obtencionGradoDAO.allByCarrerasDynatable(filter, carreras, todo);
    }

    @Override
    public GraduadoResumen findResumenEgresado(List<Carrera> carreras, String todo) {
        return obtencionGradoDAO.findResumenGraduados(carreras, todo);
    }

    @Override
    @Transactional
    public void anular(ObtencionGrado obtencionGrado, Usuario usuario) {

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);
        obtencionGrado = obtencionGradoDAO.find(obtencionGrado.getId());
        obtencionGrado.setEstadoTramite(estadoTramite);
        obtencionGrado.setFechaAnula(new Date());
        obtencionGrado.setUserAnula(usuario);
        obtencionGradoDAO.updateColumns(obtencionGrado, "estadoTramite", "fechaAnula", "userAnula");

        Tramite tramite = obtencionGrado.getTramite();
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(usuario);
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramiteDAO.updateEstado(tramite);

        if (!Arrays.asList(BACHI, TIT).contains(tramite.getTipoTramite().getCodigoEnum())) {
            throw new PhobosException("Solo se pueden anular tramites bachiller o título.");
        }

        if (tramite.getTipoTramite().getCodigoEnum() == TIT) {
            TramiteTitulo tramiteTitulo = tramiteTituloDAO.findByTramite(tramite);
            if (tramiteTitulo == null) {
                return;
            }
            tramiteTitulo.setEstado(TramiteEstadoEnum.ANU.name());
            tramiteTituloDAO.updateColumns(tramiteTitulo, "estado");
        } else if (tramite.getTipoTramite().getCodigoEnum() == TipoTramiteEnum.BACHI) {
            TramiteBachiller tramiteBachiller = tramiteBachillerDAO.findByTramite(tramite);
            if (tramiteBachiller == null) {
                return;
            }
            tramiteBachiller.setEstado(TramiteEstadoEnum.ANU.name());
            tramiteBachillerDAO.updateColumns(tramiteBachiller, "estado");
        }
    }

    @Override
    @Transactional
    public void cambiarSituacionAcademica(Long idAlumno) {

        Alumno alumno = alumnoDAO.find(new Alumno(idAlumno));

        if (alumno == null) {
            throw new PhobosException("No se ha encontrado el alumno");
        }

        SituacionAcademica situacionAcademica = alumno.getSituacionAcademica();

        if (situacionAcademica == null || (!situacionAcademica.isEgresado())) {

            alumno.setSituacionAcademica(new SituacionAcademica(SituacionAcademicaEnum.S_E.getId()));
            alumnoDAO.updateColumns(alumno, "situacionAcademica");

            AlumnoCiclo alumnoCicloDb = alumnoCicloDAO.findLastActiveEstudiadoByAlumno(alumno);

            if (alumnoCicloDb.getSituacionFinal() == null
                    || !alumnoCicloDb.getSituacionFinal().isEgresado()) {

                alumnoCicloDb.setSituacionFinal(new SituacionAcademica(SituacionAcademicaEnum.S_E.getId()));
                alumnoCicloDAO.updateColumns(alumnoCicloDb, "situacionFinal");
            }
        }

    }

}
