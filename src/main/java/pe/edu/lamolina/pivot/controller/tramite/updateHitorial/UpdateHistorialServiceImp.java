package pe.edu.lamolina.pivot.controller.tramite.updateHitorial;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EstadoTramiteEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.tramite.AccionTramiteAcademico;
import pe.edu.lamolina.model.tramite.AutorizacionRegistro;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.FormularioEstadoTramite;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteCorreccionHistorial;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.general.ArchivoDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.AutorizacionRegistroDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.FormularioEstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteCorreccionHistorialDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class UpdateHistorialServiceImp implements UpdateHistorialService {

    @Autowired
    TramiteCorreccionHistorialDAO correccionHistorialDAO;

    @Autowired
    AutorizacionRegistroDAO autorizacionRegistroDAO;

    @Autowired
    FormularioEstadoTramiteDAO formularioEstadoTramiteDAO;

    @Autowired
    AccionTramiteAcademicoDAO accionTramiteAcademicoDAO;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    ArchivoDAO archivoDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Override
    public List<TramiteCorreccionHistorial> allByCiclo(CicloAcademico cicloAcademico, DynatableFilter filter) {
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CORR_HISTO.name());
        List<AccionTramiteAcademico> accionTramiteAcademicos = accionTramiteAcademicoDAO.allByTipoTramite(tipoTramite);
        Map<Long, List<AccionTramiteAcademico>> mapAcciones = TypesUtil.convertListToMapList("estadoTramiteInicio.id", accionTramiteAcademicos);

        List<FormularioEstadoTramite> formulariosEstadoTramite = formularioEstadoTramiteDAO.all();

        List<TramiteCorreccionHistorial> correccionHistorials = correccionHistorialDAO.allByCicloDynatable(cicloAcademico, filter);
        for (TramiteCorreccionHistorial correccionHistorial : correccionHistorials) {
            FormularioEstadoTramite formularioEstadoTramite = formulariosEstadoTramite.stream().filter(x
                    -> x.getEstadoTramite().equals(correccionHistorial.getEstadoTramite())
                    && x.getTipoTramite().equals(correccionHistorial.getTramite().getTipoTramite())).findFirst().orElse(null);

            correccionHistorial.getTramite().setFormularioEstadoTramite(formularioEstadoTramite);
            correccionHistorial.getTramite().setAccionesTramitesAcademico(mapAcciones.get(correccionHistorial.getEstadoTramite().getId()));
        }
        return correccionHistorials;
    }

    private String guardarArchivo(MultipartFile file) {
        try {
            String fileName = TypesUtil.getUnixTime() + "." + TypesUtil.getClean(file.getOriginalFilename());
            FileHelper.createDirectory(GlobalConstantine.TMP_DIR);
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;

            FileHelper.saveToDisk(file, absoluteName);
            return absoluteName;
        } catch (IOException ex) {
            throw new PhobosException("Archivo no puede ser ubicado en el servidor");
        }
    }

    @Override
    @Transactional
    public void save(TramiteCorreccionHistorial correccionHistorialForm, DataSessionPivot ds) {
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CORR_HISTO.name());
//        String ruta = guardarArchivo(file);
//        Archivo archivo = new Archivo();
//        archivo.setFechaRegistro(new Date());
//        archivo.setNombre(file.getName());
//        archivo.setInstancia("TRAMTRAMITECORRECCIONHISTORIAL");
//        archivo.setUsuarioRegistro(ds.getUsuario());
//        archivo.setRuta(ruta);
//        archivo.setTipo(file.getContentType());
//        archivoDAO.save(archivo);

        DateTime today = new DateTime();
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.valueOf(correccionHistorialForm.getTipoDocumento()));
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), ds.getUsuario());

        Alumno alumno = alumnoDAO.findAllInfo(correccionHistorialForm.getAlumno().getId());

        Tramite tramite = new Tramite();
        tramite.setAlumno(alumno);
        tramite.setPersona(alumno.getPersona());
        tramite.setActivo(Boolean.TRUE);
        tramite.setCicloAcademico(ds.getCicloAcademico());
        tramite.setEstadoEnum(TramiteEstadoEnum.ACT);
        tramite.setCompania(ds.getCompania());
        tramite.setFechaRegistro(new Date());
        tramite.setUserRegistro(ds.getUsuario());
        tramite.setTipoTramite(tipoTramite);
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramiteDAO.save(tramite);

        List<AccionTramiteAcademico> accionTramiteAcademico = accionTramiteAcademicoDAO.allByTipoTramite(tipoTramite);
        AccionTramiteAcademico tramiteAcademico = accionTramiteAcademico.stream().filter(x -> x.getOrdenOpcion() == 1).findAny().orElse(null);
        TramiteCorreccionHistorial correccionHistorial = new TramiteCorreccionHistorial();
//        correccionHistorial.setArchivo(archivo);
        correccionHistorial.setEstadoTramite(tramiteAcademico.getEstadoTramiteInicio());
        correccionHistorial.setTramite(tramite);
        correccionHistorial.setAlumno(alumno);
        correccionHistorial.setFechaRegistro(new Date());
        correccionHistorial.setTipoDocumento(correccionHistorialForm.getTipoDocumento());
        correccionHistorial.setDescripcion(correccionHistorialForm.getDescripcion());
        correccionHistorial.setUserRegistro(ds.getUsuario());
        correccionHistorialDAO.save(correccionHistorial);

    }

    @Override
    public void anular(TramiteCorreccionHistorial correccionHistorial, DataSessionPivot ds) {

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigo(EstadoTramiteEnum.SOL_ANU);
        correccionHistorial.setEstadoTramite(estadoTramite);
        correccionHistorial.setFechaModificacion(new Date());
        correccionHistorial.setUserModificacion(ds.getUsuario());
        correccionHistorialDAO.updateColumns(correccionHistorial, "estadoTramite", "fechaModificacion", "userModificacion");

        Tramite tramite = correccionHistorial.getTramite();
        tramite.setEstadoEnum(TramiteEstadoEnum.ANU);
        tramite.setFechaModificacion(new Date());
        tramite.setUserModificacion(ds.getUsuario());
        tramiteDAO.updateEstado(tramite);

        AutorizacionRegistro autorizacionRegistro = autorizacionRegistroDAO.findByTramite(tramite);
        if (autorizacionRegistro != null) {
            autorizacionRegistro.setEstadoEnum(EstadoEnum.ANU);
            autorizacionRegistro.setFechaCierre(new Date());
            autorizacionRegistro.setIdUserCierre(ds.getUsuario().getId());
            autorizacionRegistroDAO.updateColumns(autorizacionRegistro, "estado", "fechaCierre", "idUserCierre");
        }
    }

}
