package pe.edu.lamolina.pivot.controller.tramite.tipoConstancia;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.ConfiguracionFirmaDocumento;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.general.ConfiguracionFirmaDocumentoDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoOficinaDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.PrecioDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoConstanciaDAO;

@Service
@Transactional(readOnly = true)
public class TipoConstanciaServiceImpl implements TipoConstanciaService {

    @Autowired
    TipoConstanciaDAO tipoConstanciaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoOficinaDAO tipoOficinaDAO;

    @Autowired
    ConfiguracionFirmaDocumentoDAO configuracionFirmaDocumentoDAO;

    @Autowired
    AccionTramiteDocumentoDAO accionTramiteDocumentoDAO;

    @Autowired
    PrecioDocumentoDAO precioDocumentoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void update(TipoDocumentoAcademico tramiteDocumentoAcademicoForm, Usuario usuario) {

        TipoDocumentoAcademico documentoAcademicoDB = tipoConstanciaDAO.find(tramiteDocumentoAcademicoForm);
        documentoAcademicoDB.setNombre(tramiteDocumentoAcademicoForm.getNombre());
        documentoAcademicoDB.setTipo(tramiteDocumentoAcademicoForm.getTipo());

        if (tramiteDocumentoAcademicoForm.getCostoCiclo() == null) {
            documentoAcademicoDB.setCostoCiclo(0L);
        } else {
            documentoAcademicoDB.setCostoCiclo(tramiteDocumentoAcademicoForm.getCostoCiclo());
        }

        tipoConstanciaDAO.update(documentoAcademicoDB);

        List<ConfiguracionFirmaDocumento> configuracion = tramiteDocumentoAcademicoForm.getConfiguracionFirmaDocumento();
        List<ConfiguracionFirmaDocumento> configuracionDB = configuracionFirmaDocumentoDAO.allByTipoDocumentoAcademico(tramiteDocumentoAcademicoForm);

        logger.debug("existen  {} configuracion en db", configuracionDB.size());

        if (!configuracionDB.isEmpty()) {
            List<Long> configuracioness = new ArrayList();
            for (ConfiguracionFirmaDocumento firma : configuracion) {
                if (firma.getId() != null) {
                    configuracioness.add(firma.getId());
                }
            }

            Map<Long, ConfiguracionFirmaDocumento> configuracionFirmaDocumentoMap = TypesUtil.convertListToMap("id", configuracionDB);
            List<ConfiguracionFirmaDocumento> configuracionDelete = new ArrayList();
            for (Map.Entry<Long, ConfiguracionFirmaDocumento> entry : configuracionFirmaDocumentoMap.entrySet()) {
                Long key = entry.getKey();
                if (!configuracioness.contains(key)) {
                    configuracionDelete.add(entry.getValue());
                }
            }

            for (ConfiguracionFirmaDocumento firma : configuracionDelete) {
                logger.debug("remove ConfiguracionFirmaDocumento {}", firma.getId());
                configuracionFirmaDocumentoDAO.delete(firma);
            }
        }

        for (ConfiguracionFirmaDocumento configuracionFirmaDocumento : configuracion) {
            ObjectUtil.eliminarAttrSinId(configuracionFirmaDocumento, "oficina");
            ObjectUtil.eliminarAttrSinId(configuracionFirmaDocumento, "tipoOficina");
            configuracionFirmaDocumento.setTipoDocumentoAcademico(tramiteDocumentoAcademicoForm);
            if (configuracionFirmaDocumento.getId() != null) {
                configuracionFirmaDocumentoDAO.save(configuracionFirmaDocumento);
            } else {
                configuracionFirmaDocumentoDAO.update(configuracionFirmaDocumento);
            }
        }

    }

    @Override
    @Transactional
    public void save(TipoDocumentoAcademico tramiteDocumentoAcademico, Usuario usuario) {
        
        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.OERA.name());
        if (tramiteDocumentoAcademico.getCostoCiclo() == null) {
            tramiteDocumentoAcademico.setCostoCiclo(0L);
        }
        if (tramiteDocumentoAcademico.getPlazoDiasPago() == null) {
            tramiteDocumentoAcademico.setPlazoDiasPago(0);
        }
        if (tramiteDocumentoAcademico.getRequiereFoto() == null) {
            tramiteDocumentoAcademico.setRequiereFoto(1);
        }
        tramiteDocumentoAcademico.setOficinaEmisora(oficina);
        tramiteDocumentoAcademico.setConfigurado(0l);
        tipoConstanciaDAO.save(tramiteDocumentoAcademico);
        List<ConfiguracionFirmaDocumento> configuracion = tramiteDocumentoAcademico.getConfiguracionFirmaDocumento();
        for (ConfiguracionFirmaDocumento configuracionFirmaDocumento : configuracion) {
            ObjectUtil.eliminarAttrSinId(configuracionFirmaDocumento, "oficina");
            ObjectUtil.eliminarAttrSinId(configuracionFirmaDocumento, "tipoOficina");
            configuracionFirmaDocumento.setTipoDocumentoAcademico(tramiteDocumentoAcademico);
            configuracionFirmaDocumentoDAO.save(configuracionFirmaDocumento);
        }

        List<AccionTramiteDocumento> accionTramiteDocumentos = accionTramiteDocumentoDAO.allByTipo(tramiteDocumentoAcademico.getTipo());
        Long idTipoDoc = accionTramiteDocumentos.get(0).getTipoDocumentoAcademico().getId();
        for (AccionTramiteDocumento accionTramiteDocumentoDB : accionTramiteDocumentos) {
            if (Objects.equals(idTipoDoc, accionTramiteDocumentoDB.getTipoDocumentoAcademico().getId())) {

                AccionTramiteDocumento accionTramiteDocumento = new AccionTramiteDocumento();
                accionTramiteDocumento.setEsFinal(accionTramiteDocumentoDB.getEsFinal());
                accionTramiteDocumento.setEstadoTramite(accionTramiteDocumentoDB.getEstadoTramite());
                accionTramiteDocumento.setEstadoTramiteFinal(accionTramiteDocumentoDB.getEstadoTramiteFinal());
                accionTramiteDocumento.setIdioma(accionTramiteDocumentoDB.getIdioma());
                accionTramiteDocumento.setMotivo(accionTramiteDocumentoDB.getMotivo());
                accionTramiteDocumento.setOficinaDestino(accionTramiteDocumentoDB.getOficinaDestino());
                accionTramiteDocumento.setOficinaOrigen(accionTramiteDocumentoDB.getOficinaOrigen());
                accionTramiteDocumento.setOpcion(accionTramiteDocumentoDB.getOpcion());
                accionTramiteDocumento.setOrden(accionTramiteDocumentoDB.getOrden());
                accionTramiteDocumento.setRespuesta(accionTramiteDocumentoDB.getRespuesta());
                accionTramiteDocumento.setSolicitaMotivo(accionTramiteDocumentoDB.getSolicitaMotivo());
                accionTramiteDocumento.setTipoDocumentoAcademico(tramiteDocumentoAcademico);
                accionTramiteDocumento.setTipoOficinaDestino(accionTramiteDocumentoDB.getTipoOficinaDestino());
                accionTramiteDocumento.setTipoOficinaOrigen(accionTramiteDocumentoDB.getTipoOficinaOrigen());
                accionTramiteDocumento.setUrl(accionTramiteDocumentoDB.getUrl());
                accionTramiteDocumentoDAO.save(accionTramiteDocumento);
            } else {
                break;
            }
        }
    }

    @Override
    public List<TipoDocumentoAcademico> all(DynatableFilter filter) {
        return tipoConstanciaDAO.allDynatable(filter);
    }

    @Override
    public TipoDocumentoAcademico findById(TipoDocumentoAcademico tipoDocumentoAcademico) {
        return tipoConstanciaDAO.find(tipoDocumentoAcademico.getId());
    }

    @Override
    public List<TipoDocumentoAcademico> all() {
        return tipoConstanciaDAO.allTipoDocumento();
    }

    @Override
    @Transactional
    public void delete(TipoDocumentoAcademico tipoDocumento) {
        configuracionFirmaDocumentoDAO.deleteByTipoDocumentoAcademicos(tipoDocumento);
        List<AccionTramiteDocumento> accionTramiteDocumentos = accionTramiteDocumentoDAO.allByTipoDocumento(tipoDocumento);
        for (AccionTramiteDocumento accionTramiteDocumento : accionTramiteDocumentos) {
            accionTramiteDocumentoDAO.delete(accionTramiteDocumento);
        }
        List<PrecioDocumento> precioDocumentos = precioDocumentoDAO.allByTipoDocumentoAcademico(tipoDocumento);
        for (PrecioDocumento precioDocumento : precioDocumentos) {
            precioDocumentoDAO.delete(precioDocumento);
        }
        TipoDocumentoAcademico tipoDocumentoAcademicoDB = tipoConstanciaDAO.find(tipoDocumento);
        tipoConstanciaDAO.delete(tipoDocumentoAcademicoDB);
    }

    @Override
    public List<Oficina> allOficina(String nombre) {
        return oficinaDAO.allByName(nombre);
    }

    @Override
    public List<TipoOficina> allTipoOficina(String nombre) {
        return tipoOficinaDAO.allByName(nombre);
    }

    @Override
    public TipoDocumentoAcademico findTipoDocumentoAcademico(TipoDocumentoAcademico tipoDocumento) {
        TipoDocumentoAcademico tipoDocumentoAcademico = tipoConstanciaDAO.find(tipoDocumento);
        List<ConfiguracionFirmaDocumento> firmas = configuracionFirmaDocumentoDAO.allByTipoDocumentoAcademico(tipoDocumentoAcademico);
        tipoDocumentoAcademico.setConfiguracionFirmaDocumento(firmas);
        return tipoDocumentoAcademico;
    }

    @Override
    public ObjectNode toJson(Object object) {
        ObjectNode json = JsonHelper.createJson(object, JsonNodeFactory.instance);
        return json;
    }

}
