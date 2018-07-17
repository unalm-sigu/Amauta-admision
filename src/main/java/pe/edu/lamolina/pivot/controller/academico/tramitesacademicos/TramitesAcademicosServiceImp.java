package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramiteAcademico;
import pe.edu.lamolina.model.tramite.FlujoTramiteAcademico;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.FlujoTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;

@Service
@Transactional(readOnly = true)
public class TramitesAcademicosServiceImp implements TramitesAcademicosService {

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    EstadoTramiteAcademicoDAO estadoTramiteAcademicoDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    FlujoTramiteAcademicoDAO flujoTramiteAcademicoDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Override
    public List<Tramite> allTramitesByFilter(DynatableFilter filter) {
        List<Tramite> tramites = tramiteDAO.allByFilter(filter);
        return tramites;
    }

    @Override
    public void aceptarSolReincorporacion(Tramite tramite, Usuario usuario) {
        DateTime today = new DateTime();

        tramite = tramiteDAO.find(tramite.getId());
        Alumno alumnoTramite = alumnoDAO.find(tramite.getAlumno());

        List<Reincorporacion> reincorporaciones = reincorporacionDAO.allByTramite(tramite);
        Reincorporacion reincorporacion = reincorporaciones.get(0);

        if (!reincorporacion.getEstadoTramite().isSolicitudReincorporacion()) {
            throw new PhobosException("Estado incorrecto");
        }

        TipoTramite tipoTramiteReincorporacion = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.REI.name());
        EstadoTramiteAcademico estadoTramiteAcademico
                = estadoTramiteAcademicoDAO.findByTipoTramiteOrden(tipoTramiteReincorporacion, (BigDecimal.ONE.intValue() + 1));

        Map oficinas = this.findOficinaOrigenDestinoByEstadoTramiteAcad(estadoTramiteAcademico, alumnoTramite);

        FlujoTramiteAcademico flujoTramiteAcademico = new FlujoTramiteAcademico();
        flujoTramiteAcademico.setEstadoTramite(estadoTramiteAcademico.getEstadoTramite());
        flujoTramiteAcademico.setFechaRegistro(today.toDate());
        flujoTramiteAcademico.setOficinaOrigen((Oficina) oficinas.get("oficinaOrigen"));
        flujoTramiteAcademico.setOficinaDestino((Oficina) oficinas.get("oficinaDestino"));
        flujoTramiteAcademico.setTramiteAcademico(tramite);
        flujoTramiteAcademico.setUserRegistro(usuario);
        flujoTramiteAcademico.setOrden(estadoTramiteAcademico.getOrden());
        flujoTramiteAcademicoDAO.save(flujoTramiteAcademico);

        Reincorporacion reincorporacionUpd = new Reincorporacion();
        reincorporacionUpd.setId(reincorporacion.getId());
        reincorporacionUpd.setEstadoTramite(estadoTramiteAcademico.getEstadoTramite());
        reincorporacionDAO.updateEstado(reincorporacionUpd);

        Tramite tramiteUpd = new Tramite();
        tramiteUpd.setId(tramite.getId());
        tramiteUpd.setEstadoEnum(TramiteEstadoEnum.ACEP);
        tramiteUpd.setUserModificacion(usuario);
        tramiteUpd.setFechaModificacion(today.toDate());
        tramiteDAO.updateEstado(tramiteUpd);
    }

    public Map findOficinaOrigenDestinoByEstadoTramiteAcad(EstadoTramiteAcademico estadoTramiteAcademico, Alumno alumno) {
        Oficina oficinaOrigen = null;
        if (ObjectUtil.getParentTree(estadoTramiteAcademico, "oficinaOrigen.id") != null) {
            oficinaOrigen = oficinaDAO.find(estadoTramiteAcademico.getOficinaOrigen().getId());
        } else {
            if (estadoTramiteAcademico.getTipoOficinaOrigen().isTipoFacultad()) {
                oficinaOrigen = oficinaDAO.findByTipoAndFacultad(
                        TipoOficinaEnum.valueOf(estadoTramiteAcademico.getTipoOficinaOrigen().getCodigo()),
                        alumno.getCarrera().getFacultad());
            }
        }
        Oficina oficinaDestino = null;
        if (ObjectUtil.getParentTree(estadoTramiteAcademico, "oficinaDestino.id") != null) {
            oficinaDestino = oficinaDAO.find(estadoTramiteAcademico.getOficinaDestino().getId());
        } else {
            if (estadoTramiteAcademico.getTipoOficinaDestino().isTipoFacultad()) {
                oficinaDestino = oficinaDAO.findByTipoAndFacultad(
                        TipoOficinaEnum.valueOf(estadoTramiteAcademico.getTipoOficinaDestino().getCodigo()),
                        alumno.getCarrera().getFacultad());
            }
        }
        Map resultado = new HashMap();
        resultado.put("oficinaOrigen", oficinaOrigen);
        resultado.put("oficinaDestino", oficinaDestino);
        return resultado;
    }

}
