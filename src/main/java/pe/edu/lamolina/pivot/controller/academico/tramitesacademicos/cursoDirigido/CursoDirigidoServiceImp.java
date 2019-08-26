package pe.edu.lamolina.pivot.controller.academico.tramitesacademicos.cursoDirigido;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class CursoDirigidoServiceImp implements CursoDirigidoService {

    @Autowired
    CursoDirigidoDAO cursoDirigidoDAO;

    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Override
    public List<CursoDirigido> allByFacultades(DynatableFilter filters, Docente docente) {
//        if (docente == null) {
//            return new ArrayList<>();
//        }
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByfacultades(filters, docente);
//        for (CursoDirigido cursoDirigido : cursoDirigidos) {
//            Tramite tramite = cursoDirigido.getTramite();
//            List<AccionTramiteAcademico> accionesTramitesAcademicos = accionTramiteAcademicoDAO.allByTipoTramiteAndEstadoTramiteInicial(tramite.getTipoTramite(), cursoDirigido.getEstado());
//            cursoDirigido.setAccionTramiteAcademicos(accionesTramitesAcademicos);
//        }

        return cursoDirigidos;
    }

    @Override
    @Transactional
    public void update(CursoDirigido cursoDirigido, DataSessionPivot ds) {

        Tramite tramite = cursoDirigido.getTramite();
        AccionTramiteAcademico accionTramiteAcademico = cursoDirigido.getAccionTramiteAcademicos().get(0);

        tramite.setEstadoEnum(TramiteEstadoEnum.valueOf(accionTramiteAcademico.getEstadoTramiteFinal().getCodigo()));
        tramiteDAO.updateEstado(tramite);

        cursoDirigido.setEstado(accionTramiteAcademico.getEstadoTramiteFinal());
        cursoDirigidoDAO.updateEstado(cursoDirigido);
    }

}
