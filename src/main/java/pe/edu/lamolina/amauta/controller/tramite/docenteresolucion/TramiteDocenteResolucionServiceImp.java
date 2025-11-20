package pe.edu.lamolina.amauta.controller.tramite.docenteresolucion;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.*;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.enums.tramite.TipoTramiteEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.DocenteResolucion;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;

import java.util.Collections;
import java.util.Date;
import java.util.List;


@Service
@Transactional(readOnly=true)
public class TramiteDocenteResolucionServiceImp implements TramiteDocenteResolucionService {

    @Autowired
    DocenteResolucionDAO docenteResolucionDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    TramiteDAO tramiteDAO;

    @Override
    public List<DocenteResolucion> allTramiteByFilter(DynatableFilter filter) {
        return docenteResolucionDAO.allTramiteByFilter(filter);
    }

    @Override
    public List<CicloAcademico> getCiclos() {
        ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.PRE);
        return cicloAcademicoDAO.all();
    }

    @Override
    @Transactional(readOnly=false)
    public String saveDocenteResolucion(DocenteResolucion docenteResolucion, DataSessionPivot ds) {
        String mensajeJson = "OK";

        Docente docenteDB = docenteDAO.findByDocente(docenteResolucion.getDocente());

        DocenteResolucion tieneResolucion = docenteResolucionDAO.findByCicloAndTipo(docenteResolucion.getCiclo(),docenteDB);

        if(tieneResolucion != null){
            throw new RuntimeException("El docente ya tiene resolucion en el ciclo seleccionado");
        }

        DateTime today = new DateTime();
        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.SOL);
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.SUSP_DISCIPLI.name());
        Oficina oficina = oficinaDAO.findByCode(OficinaEnum.UPH.name());
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM_DOCENTE_RESOLUCION);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());
        CicloAcademico cicloSession = ds.getCicloAcademico();

        Tramite tramite = new Tramite();
        tramite.setActivo(true);
        tramite.setCompania(ds.getCompania());
        tramite.setDocente(docenteDB);
        tramite.setCicloAcademico(cicloSession);
        tramite.setEstadoEnum(TramiteEstadoEnum.SOL);
        tramite.setEstadoTramite(estadoTramite);
        tramite.setFechaRegistro(new Date());
        tramite.setPersona(docenteDB.getPersona());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setOficina(oficina);
        tramite.setNumeroVisible(tramite.getDescripcion());
        tramiteDAO.save(tramite);

        DocenteResolucion docenteReso = new DocenteResolucion();
        docenteReso.setDocente(docenteDB);
        docenteReso.setCiclo(docenteResolucion.getCiclo());
        docenteReso.setTramite(tramite);
        docenteReso.setResolucionFacultad(docenteResolucion.getResolucionFacultad());
        docenteReso.setResolucionConsejo(docenteResolucion.getResolucionConsejo());
        docenteReso.setMotivo(docenteResolucion.getMotivo());
        docenteReso.setEstadoConsejoEnum(TramiteEstadoEnum.SOL);
        docenteReso.setEstadoFacultadEnum(TramiteEstadoEnum.SOL);
        docenteReso.setUsuarioRegistro(ds.getUsuario());
        docenteReso.setFechaRegistro(new Date());
        docenteResolucionDAO.save(docenteReso);

        return mensajeJson;

    }

    @Override
    public List<Docente> allDocenteByNombre(String nombre, DataSessionPivot ds) {
        return docenteDAO.allByName(nombre);
    }

    @Override
    @Transactional
    public void anular(Long idDocente, Usuario usuarioAnulacion) {
        DocenteResolucion docenteResolucion = docenteResolucionDAO.find(idDocente);

        if (docenteResolucion == null) {
            throw new PhobosException("El tramite no fue encontrado");
        }

        if(docenteResolucion.getEstadoConsejoEnum().equals(TramiteEstadoEnum.ACEP)){
            throw new PhobosException("El tramite ya fue Aceptado");
        }

        if(docenteResolucion.getEstadoConsejo().equalsIgnoreCase(TramiteEstadoEnum.ANU.name())){
            throw new PhobosException("El tramite ya fue Anulado");
        }

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);
        Tramite tramite = docenteResolucion.getTramite();
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(usuarioAnulacion);
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setEstadoTramite(estadoTramite);
        tramiteDAO.update(tramite);

        docenteResolucion.setMotivo(docenteResolucion.getMotivo());
        docenteResolucion.setEstadoConsejoEnum(TramiteEstadoEnum.ANU);
        docenteResolucion.setUsuarioActualizacion(usuarioAnulacion);
        docenteResolucion.setFechaActualizacion(new Date());
        docenteResolucionDAO.update(docenteResolucion);
    }
}
