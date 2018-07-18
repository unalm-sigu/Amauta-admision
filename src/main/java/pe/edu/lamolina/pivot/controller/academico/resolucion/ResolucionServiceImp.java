package pe.edu.lamolina.pivot.controller.academico.resolucion;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.aws.S3Service;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.ReincorporacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoResolucionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Service
@Transactional(readOnly = true)
public class ResolucionServiceImp implements ResolucionService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    ReincorporacionDAO reincorporacionDAO;

    @Autowired
    TipoResolucionDAO tipoResolucionDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    S3Service s3service;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Override
    public List<Resolucion> allResolucionesByFilter(DynatableFilter filter) {
        List<Resolucion> resoluciones = resolucionDAO.allByDyna(filter);
        return resoluciones;
    }

    @Override
    public List<Tramite> allTramitesByTipoEstadoTram(TipoTramiteEnum tipoTramiteEnum, EstadoTramiteEnum estadoTramiteEnum) {
        return tramiteDAO.allByTipoTramiteEstadoTramite(tipoTramiteEnum, estadoTramiteEnum);
    }

    @Override
    public List<TipoResolucion> allTiposResolucion() {
        return tipoResolucionDAO.all();
    }

    @Override
    @Transactional(readOnly = false)
    public void saveResolucion(Resolucion resolucion, Usuario usuario, CicloAcademico cicloAcademico, Oficina oficina) {
        DateTime today = new DateTime();

        boolean someChecked = false;
        for (Tramite tramite : resolucion.getTramites()) {
            if (tramite.getSeleccionado()) {
                someChecked = true;
            }
        }
        if (!someChecked) {
            throw new PhobosException("Debe seleccionar algun tramite");
        }

        //   String name = resolucion.getRutaUrl();
        //  String absoluteName = Constantine.TMP_DIR + name;
        resolucion.setUserRegistro(usuario);
        // resolucion.setRutaUrl(Constantine.S3_LINK + Constantine.PIVOT_DIR + Constantine.S3_RESOLUCIONES_DIR + name);
        // resolucion.setRutaUrl(Constantine.S3_LINK + Constantine.S3_DIR + Constantine.S3_RESOLUCIONES_DIR + name);
        resolucion.setFechaRegistro(today.toDate());
        resolucion.setOficina(oficina);
        resolucionDAO.save(resolucion);

        for (Tramite tramite : resolucion.getTramites()) {
            if (tramite.getActivo()) {
                Reincorporacion reincorporacion = reincorporacionDAO.findByTramiteEstadoTram(tramite, EstadoTramiteEnum.CON_FAC);
                if (tramite.getSeleccionado()) {
               //     EstadoTramite estadoTramiteAcpRecFac = estadoTramiteDAO.find(EstadoTramiteEnum.ACP_REC_FAC.getId());

                    reincorporacion.setResolucion(resolucion);
                  //  reincorporacion.setEstadoTramite(estadoTramiteAcpRecFac);
                    reincorporacionDAO.update(reincorporacion);
                } else {
                 //   EstadoTramite estadoTramiteAcpRhzFac = estadoTramiteDAO.find(EstadoTramiteEnum.RHZ_REC_FAC.getId());
                  //  reincorporacion.setEstadoTramite(estadoTramiteAcpRhzFac);
                    reincorporacionDAO.update(reincorporacion);
                }
            }
        }

        //  s3service.uploadFileSync(Constantine.S3_DIR, Constantine.S3_RESOLUCIONES_DIR, Constantine.TMP_DIR, name, true);
    }

}
