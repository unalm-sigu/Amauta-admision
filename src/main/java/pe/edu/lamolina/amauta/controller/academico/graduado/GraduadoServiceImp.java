package pe.edu.lamolina.amauta.controller.academico.graduado;

import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.Tramite;

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
    }

}
