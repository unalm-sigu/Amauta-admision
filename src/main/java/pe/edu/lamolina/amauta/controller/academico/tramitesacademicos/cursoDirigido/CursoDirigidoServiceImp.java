package pe.edu.lamolina.amauta.controller.academico.tramitesacademicos.cursoDirigido;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.CursoDirigidoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Facultad;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SOL_ANU;

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

    @Autowired
    FacultadDAO facultadDAO;

    @Override
    public List<CursoDirigido> allByFacultades(DynatableFilter filters, CicloAcademico ciclo) {
//        if (docente == null) {
//            return new ArrayList<>();
//        }
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByfacultades(filters, ciclo);
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

    @Override
    @Transactional
    public void anular(CursoDirigido cursoDirigido, DataSessionPivot ds) {

        cursoDirigido = cursoDirigidoDAO.find(cursoDirigido.getId());
        Tramite tramite = cursoDirigido.getTramite();

        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramiteDAO.updateEstado(tramite);

        cursoDirigido.setEstado(new EstadoTramite(SOL_ANU.getId()));
        cursoDirigidoDAO.updateEstado(cursoDirigido);
    }

    @Override
    public Context alllistCursoDirigidoFac(Facultad facultad, DataSessionPivot ds) {
        
        facultad = facultadDAO.find(facultad.getId());
        List<CursoDirigido> cursoDirigidos = cursoDirigidoDAO.allByfacultades(facultad, ds.getCicloAcademico());

        Context ctx = new Context();

        ctx.setVariable("fecha", TypesUtil.getStringDate(new DateTime().toDate(), " dd 'de' MMMM 'del' yyyy", "es"));
        ctx.setVariable("cursoDirigido", cursoDirigidos);
        ctx.setVariable("facultad", facultad.getNombre().toUpperCase(Locale.ROOT));

        ctx.setVariable("nombrePdf", "Reporte Facultad " + facultad.getId());
        ctx.setVariable("templatePdf", "listDetalleCursoDirigido");

        return ctx;
    }

}
