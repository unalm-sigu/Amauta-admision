package pe.edu.lamolina.amauta.controller.tramite.bolsatrabajo;

import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.tramite.AccionTramiteBienestar;
import pe.edu.lamolina.model.tramite.FlujoTramiteBienestar;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteBienestarDAO;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteBienestarDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteSubvencionDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SUPERV_APR;
import static pe.edu.lamolina.model.enums.TramiteEstadoEnum.SUPERV_ASIGN;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.TramiteSubvencion;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class BolsaTrabajoServiceImpl implements BolsaTrabajoService {

    private final AccionTramiteBienestarDAO accionTramiteBienestarDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final FlujoTramiteBienestarDAO flujoTramiteBienestarDAO;
    private final TramiteDAO tramiteDAO;
    private final TramiteSubvencionDAO subvencionDAO;

    private final OficinaService oficinaService;

    private final long UNO = 1L;

    @Override
    public List<TramiteSubvencion> allSubvencionesBySupervisor(Persona persona, CicloAcademico ciclo) {
        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(persona);
        List<Oficina> oficinas = oficinaService.allOficinasOrganizadas();

        List<AccionTramiteBienestar> accionesAll = accionTramiteBienestarDAO.all();
        Map<String, List<AccionTramiteBienestar>> mapAcciones = TypesUtil.convertListToMapList("key", accionesAll);

        List<TramiteSubvencion> subvenciones = subvencionDAO.allSubvencionByColaboradorCiclo(colaboradores, ciclo);
        subvenciones.forEach(subvencion -> {
            Oficina oficina = subvencion.getSupervisor().getOficina();
            Oficina oficinaMain = oficinaService.findOficinaMain(oficina, oficinas);
            oficina.setOficinaSuperior(oficinaMain);

            Tramite tramite = subvencion.getTramite();
            String key = tramite.getTipoTramite().getId() + "-";
            key += subvencion.getTipoSubvencion().getId() + "-";
            key += tramite.getEstado();

            List<AccionTramiteBienestar> acciones = TypesUtil.getListNotNull(mapAcciones.get(key));
            for (AccionTramiteBienestar accion : acciones) {
                if (accion.getEsRegular() == UNO) {
                    tramite.setAccionTramiteBienestar(accion);
                }
            }
        });

        return subvenciones;
    }

    @Override
    @Transactional
    public void updateTramiteSubvencion(TramiteSubvencion subvencionForm, DataSessionPivot ds) {
        Tramite tramiteForm = subvencionForm.getTramite();
        Tramite tramiteBD = tramiteDAO.findById(subvencionForm.getTramite());
        TramiteSubvencion subvencion = subvencionDAO.find(subvencionForm);
        Persona supervisor = subvencion.getSupervisor().getPersona();
        Assert.isEqual(supervisor.getId(), ds.getPersona().getId(), "Usted no está autorizado responder esta subvención");

        if (tramiteBD.getEstadoEnum() != SUPERV_ASIGN) {
            List<FlujoTramiteBienestar> flujosTramite = flujoTramiteBienestarDAO.allByTramite(tramiteForm);
            flujosTramite.stream()
                    .filter(flujo -> flujo.getTramite().getEstadoEnum() == SUPERV_APR)
                    .forEach(flujo -> Assert.isTrue(Boolean.FALSE, "Esta solicitud ya fue aprobada por el supervisor"));
            Assert.isTrue(Boolean.FALSE, "Esta solicitud aún no está disponible para ser aprobada por el supervisor");
            return;
        }

        AccionTramiteBienestar accion = accionTramiteBienestarDAO.findByTipoSubvencion(subvencion.getTipoSubvencion(), tramiteBD.getEstado(), subvencionForm.getRespuesta());
        if (subvencionForm.getVoboSupervisor()) {
            subvencion.setFechaVoBo(new Date());
        }

        subvencion.setLaborRealizar(subvencionForm.getLaborRealizar());
        subvencion.setLugarLabores(subvencionForm.getLugarLabores());
        subvencionDAO.update(subvencion);

        tramiteBD.setEstado(accion.getEstadoFinal());
        tramiteBD.setFechaModificacion(new Date());
        tramiteBD.setUserModificacion(ds.getUsuario());
        tramiteBD.setObservacion(subvencionForm.getComentario());
        tramiteDAO.update(tramiteBD);

        FlujoTramiteBienestar flujoTramite = new FlujoTramiteBienestar();
        flujoTramite.setEstado(accion.getEstadoFinal());
        flujoTramite.setTramite(tramiteForm);
        flujoTramite.setComentario(subvencionForm.getComentario());
        flujoTramite.setUserRegistro(ds.getUsuario());
        flujoTramite.setFechaRegistro(new Date());
        flujoTramiteBienestarDAO.save(flujoTramite);
    }

}
