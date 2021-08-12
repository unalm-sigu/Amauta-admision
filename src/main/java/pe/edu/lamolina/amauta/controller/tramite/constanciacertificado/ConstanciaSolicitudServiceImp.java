package pe.edu.lamolina.amauta.controller.tramite.constanciacertificado;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import java.io.File;
import java.math.BigDecimal;
import static java.math.BigDecimal.ZERO;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.cloud.storage.StorageService;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.PlantillaIncrustacionGeneralBean;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.TipoConstanciaEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.FlujoTramiteDocumento;
import pe.edu.lamolina.model.tramite.FormularioEstadoTramite;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.PlantillaIncrustacionDocumento;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.amauta.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.amauta.controller.academico.situacionacademica.SituacionAcademicaService;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.controller.tramite.plantilla.PlantillaGenerica;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.ControlOrdenMeritoDAO;
import pe.edu.lamolina.amauta.dao.academico.CursoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.amauta.dao.finanza.AcreenciaTramiteDocumentoDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.amauta.dao.general.IdiomaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.tramite.AccionTramiteDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.FlujoTramiteDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.FormularioEstadoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.PlantillaDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.PrecioDocumentoDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.VariablePlantillaDAO;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.mail.MailerService;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.dao.tramite.PlantillaIncrustacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDocumentoParametroDAO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.verificadorSolicitud.VerificadorSolicitudService;
import pe.edu.lamolina.amauta.dao.general.ArchivoDAO;
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.CODIGO_ALIANZA_ESTRATEGICA;
import static pe.edu.lamolina.model.constantines.GlobalConstantine.VARIABLE_TABLE;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.ResolucionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.FECHAS_BACH;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.ALUMNO_REGULAR;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.APELLIDOS;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.APELLIDO_PERSONA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CANTIDAD_ALUMNOS;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CANTIDAD_CREDITOS_APROBADOS;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CARRERA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CICLOS_CURSADOS;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CICLO_EGRESO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CICLO_MATRICULA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CICLO_PROMOCION;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CORRELATIVO_DOC;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.ESPECIALIDAD;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.FACULTAD;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.FECHA_CONSTANCIA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.FECHA_EGRESO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.FECHA_PRIMERA_MATRICULA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.FECHA_ULTIMA_MATRICULA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.INCRUSTACION;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.JEFE_OFICINA_OERA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.JEFE_URA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.MATRICULA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.MEJOR_PROMEDIO_PONDERADO_GRADUACION;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.NIVEL_ACADEMICO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.NOMBRE_PERSONA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.NUMERO_DOCUMENTO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.ORDEN_MERITO_EGRESADO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.PRIMER_CICLO_MATRICULADO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.PROGRAMA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.PROMEDIO_PONDERADO_GRADUACION;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.RESOL_EGRESO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.RESOL_FECHA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.RESOL_TITULO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.RESOL_TITULO_FECHA;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.SENOR_A;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.SEX_ALUM;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.SEX_IDENT;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.TIPO_DOCUMENTO;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.TITULO_PROFESIONAL;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.ULTIMO_CICLO_MATRICULADO;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoAcademicoDAO;
import static pe.edu.lamolina.model.enums.InstanciaEnum.TRAM_DOCUMENTO;

@Service
@Transactional(readOnly = true)
public class ConstanciaSolicitudServiceImp implements ConstanciaSolicitudService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;

    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;

    @Autowired
    SituacionAcademicaService situacionAcademicaService;

    @Autowired
    CursoDAO cursoDAO;

    @Autowired
    TramiteDocumentoAcademicoDAO tramiteDocumentoAcademicoDAO;

    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Autowired
    TipoDocumentoAcademicoDAO tipoDocumentoAcademicoDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    TramiteDAO tramiteDAO;

    @Autowired
    PrecioDocumentoDAO precioDocumentoDAO;

    @Autowired
    SerieDocumentoService serieDocumentoService;

    @Autowired
    TipoTramiteDAO tipoTramiteDAO;

    @Autowired
    ContenidoCartaDAO contenidoCartaDAO;

    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;

    @Autowired
    MailerService mailerService;

    @Autowired
    PromedioService promedioService;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    EstadoTramiteDAO estadoTramiteDAO;

    @Autowired
    AcreenciaTramiteDocumentoDAO acreenciaTramiteDocumentoDAO;

    @Autowired
    FlujoTramiteDocumentoDAO flujoTramiteDocumentoDAO;

    @Autowired
    AccionTramiteDocumentoDAO accionTramiteDocumentoDAO;

    @Autowired
    PlantillaDocumentoAcademicoDAO plantillaDocumentoAcademicoDAO;

    @Autowired
    VariablePlantillaDAO variablePlantillaDAO;

    @Autowired
    PlantillaIncrustacionDAO plantillaIncrustacionDAO;

    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;

    @Autowired
    ControlOrdenMeritoDAO controlOrdenMeritoDAO;

    @Autowired
    TramiteDocumentoParametroDAO tramiteDocumentoParamtroDAO;

    @Autowired
    FormularioEstadoTramiteDAO formularioEstadoTramiteDAO;

    @Autowired
    EgresadoDAO egresadoDAO;

    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;

    @Autowired
    AcreenciaDAO acreenciaDAO;

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    StorageService swiftService;

    @Autowired
    ArchivoDAO archivoDAO;

    @Autowired
    ResolucionDAO resolucionDAO;

    @Autowired
    VerificadorSolicitudService verificadorSolicitudService;

    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;

    @Autowired
    UploadFileS3 uploadFileS3;

    @Override
    public List<AlumnoCiclo> allPromediosByAlumno(Alumno alumno) {

        List<AlumnoCicloCurso> cursosCiclos = alumnoCicloCursoDAO.allByAlumnoCicloAsc(alumno);
        Map<Long, AlumnoCiclo> mapAlumnoCiclo = TypesUtil.convertListToMap("alumnoCiclo.id", "alumnoCiclo", cursosCiclos);
        Map<Long, List<AlumnoCicloCurso>> mapAlumnoCicloCurso = TypesUtil.convertListToMapList("alumnoCiclo.id", cursosCiclos);

        List<AlumnoCiclo> promedios = new ArrayList(mapAlumnoCiclo.values());
        for (AlumnoCiclo promedio : promedios) {
            List<AlumnoCicloCurso> cursos = mapAlumnoCicloCurso.get(promedio.getId());
            promedio.setAlumnoCicloCurso(cursos);
        }

        return promedios;
    }

    @Override
    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter) {
        List<FormularioEstadoTramite> formulariosEstadoTramite = formularioEstadoTramiteDAO.all();
        List<TramiteDocumentoAcademico> documentoAcademicos = tramiteDocumentoAcademicoDAO.allTramiteDocumentoAcademico(filter);
        for (TramiteDocumentoAcademico documentoAcademico : documentoAcademicos) {
            FormularioEstadoTramite formularioEstadoTramite = formulariosEstadoTramite.stream().filter(x
                    -> x.getEstadoTramite().equals(documentoAcademico.getEstadoTramite())
                    && x.getTipoTramite().equals(documentoAcademico.getTramite().getTipoTramite())).findFirst().orElse(null);

            documentoAcademico.getTramite().setFormularioEstadoTramite(formularioEstadoTramite);
        }
        return tramiteDocumentoAcademicoDAO.allTramiteDocumentoAcademico(filter);
    }

    private void enviarNotificacionSolicitudConstanciaCreacion(TramiteDocumentoAcademico tramiteDocumentoAcademico) {
        ContenidoCarta contenidoCarta = contenidoCartaDAO.findByCodigoEnum(ContenidoCartaEnum.NOTIFYSOLICITUD);
        mailerService.enviarNotificacionSolicitudConstanciaCreacion(tramiteDocumentoAcademico, contenidoCarta);
    }

    @Override
    @Transactional
    public void updateTramiteDocumentoAcademico(TramiteDocumentoAcademico tramiteDocumentoAcademico, DataSessionPivot ds) {

        TramiteDocumentoAcademico tda = tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademico);
        Tramite tramite = tda.getTramite();
        tramite.setUserModificacion(ds.getUsuario());
        tramite.setFechaModificacion(new Date());
        tramiteDAO.update(tramite);

        tda.setPersonaContacto(tramiteDocumentoAcademico.getPersonaContacto());
        tda.setEmail(tramiteDocumentoAcademico.getEmail());
        tda.setTelefono(tramiteDocumentoAcademico.getTelefono());
        tda.setCelular(tramiteDocumentoAcademico.getCelular());
        tramiteDocumentoAcademicoDAO.updateColumns(tda, "personaContacto", "email", "telefono", "celular");

    }

    @Override
    public List<MatriculaResumen> allMatriculaResumenByAlumno(Alumno alumno) {
        return matriculaResumenDAO.allMatriculaResumenByAlumno(alumno);
    }

    @Override
    public List<Idioma> allIdiomas() {
        return idiomaDAO.allByCodigo(Arrays.asList(AcademicoConstantine.CODE_IDIOMA_ESPANOL, AcademicoConstantine.CODE_IDIOMA_INGLES));
    }

    @Override
    public Alumno findAlumno(Alumno alumno) {
        return alumnoDAO.find(alumno);
    }

    @Override
    public List<Alumno> allAlumnoByPersona(Persona persona) {
        return alumnoDAO.allByPersona(persona);
    }

    @Override
    public Persona findPersona(Persona persona) {
        return personaDAO.find(persona.getId());
    }

    private ObjectNode toJson(Object object) {
        ObjectNode json = JsonHelper.createJson(object, JsonNodeFactory.instance);
        return json;
    }

    @Override
    public void fillTipoDocumentoAcademico(ArrayNode array) {
        JsonNodeFactory factory = JsonNodeFactory.instance;
        List<TipoDocumentoAcademico> tipos = tipoDocumentoAcademicoDAO.all();
        List<PrecioDocumento> precios = precioDocumentoDAO.allPrecioDocumento();
        Map<Long, List<PrecioDocumento>> precioByDocumento = TypesUtil.convertListToMapList("tipoDocumento.id", precios);
        for (TipoDocumentoAcademico tipo : tipos) {
            ObjectNode nodeTipoDocumento = this.toJson(tipo);
            ArrayNode arrayCostos = new ArrayNode(factory);
            List<PrecioDocumento> preciosDocumento = precioByDocumento.get(tipo.getId());
            if (preciosDocumento != null) {
                for (PrecioDocumento precioDocumento : preciosDocumento) {
                    ObjectNode objectCostos = this.toJson(precioDocumento);
                    ObjectNode objectIdioma = this.toJson(precioDocumento.getIdioma());
                    objectCostos.set("idioma", objectIdioma);
                    arrayCostos.add(objectCostos);
                }
            }
            nodeTipoDocumento.set("precios", arrayCostos);
            array.add(nodeTipoDocumento);
        }
    }

    @Override
    public List<PrecioDocumento> allPrecioDocumento() {
        return precioDocumentoDAO.allPrecioDocumento();
    }

    @Override
    public TramiteDocumentoAcademico findTramite(TramiteDocumentoAcademico tramiteDocumentoAcademicoForm) {
        TramiteDocumentoAcademico documentoAcademico = tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademicoForm);
        if (documentoAcademico != null) {
            Tramite tramite = documentoAcademico.getTramite();

            tramite.setAccionesTramitesDocumentos(accionTramiteDocumentoDAO.allByTipoTramiteAndEstadoTramiteInicial(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getEstadoTramite()));

            tramite.setFormularioEstadoTramite(formularioEstadoTramiteDAO.findByTipoTramiteAndEstadoTramite(tramite.getTipoTramite(), documentoAcademico.getEstadoTramite()));
        }
        return documentoAcademico;
    }

    @Override
    public ContenidoCarta findContenidoBoletaByCodigoEnum(ContenidoCartaEnum contenidoCartaEnum) {
        return contenidoCartaDAO.findByCodigoEnum(contenidoCartaEnum);
    }

    @Override
    public PrecioDocumento findPrecioDocumentoByTipoIdioma(TipoDocumentoAcademico tipoDocumento, Idioma idioma) {
        return precioDocumentoDAO.findByTipoIdioma(tipoDocumento, idioma);
    }

    @Override
    public List<Alumno> allAlumnoByName(String nombre) {
        return alumnoDAO.allByName(nombre);
    }

    @Override
    public List<Colaborador> allColaboradorByName(String nombre) {
        return colaboradorDAO.allByName(nombre);
    }

    public void uploadS3(String fileName) {
        logger.debug("upload to s3    {}  {}   {}  {} {}", AcademicoConstantine.S3_BUCKET_ACADEMICO, AcademicoConstantine.S3_DIR_FOTO_TMP, GlobalConstantine.TMP_DIR, fileName, true);
        File f = new File(GlobalConstantine.TMP_DIR + fileName);
        if (f.exists() && !f.isDirectory()) {
            swiftService.uploadFile(AcademicoConstantine.S3_BUCKET_ACADEMICO, AcademicoConstantine.S3_DIR_FOTO_TMP, GlobalConstantine.TMP_DIR, fileName, true);
        }
    }

    @Override
    @Transactional
    public void updateFotoTemporal(TramiteDocumentoAcademico documentoAcademico, DataSessionPivot ds) {
        Persona persona = documentoAcademico.getTramite().getAlumno().getPersona();
        if (!Strings.isNullOrEmpty(persona.getRutaFotoTemporal())) {
            Persona personaDB = personaDAO.find(persona.getId());
            personaDB.setRutaFotoTemporal(persona.getRutaFotoTemporal());
            personaDAO.update(personaDB);
            this.uploadS3(personaDB.getRutaFotoTemporal());
        }
        if (documentoAcademico.getId() != null) {

            List<AccionTramiteDocumento> accion = accionTramiteDocumentoDAO.allNextByEstadoInicio(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getEstadoTramite());
            EstadoTramite estadoTramite = new EstadoTramite();
            for (AccionTramiteDocumento accionTramiteDocumento : accion) {
                estadoTramite = accionTramiteDocumento.getEstadoTramiteFinal();
            }

            Tramite tramiteDb = tramiteDAO.find(documentoAcademico.getTramite().getId());
            tramiteDb.setEstado(estadoTramite.getCodigo());
            tramiteDAO.update(tramiteDb);

            FlujoTramiteDocumento flujo = new FlujoTramiteDocumento();
            flujo.setEstadoTramite(estadoTramite);
            flujo.setOficinaOrigen(ds.getOficinaMain());
            flujo.setOficinaDestino(ds.getOficinaMain());
            flujo.setUserRegistro(ds.getUsuario());
            flujo.setTramiteDocumentoAcademico(documentoAcademico);
            flujo.setFechaRegistro(new Date());
            flujoTramiteDocumentoDAO.save(flujo);

            TramiteDocumentoAcademico academico = tramiteDocumentoAcademicoDAO.find(documentoAcademico.getId());
            academico.setEstadoTramite(estadoTramite);
            tramiteDocumentoAcademicoDAO.update(academico);
        }
    }

    @Override
    @Transactional
    public void save(TramiteDocumentoAcademico tramiteDocumentoAcademico, DataSessionPivot ds) {

        Tramite tramite = tramiteDocumentoAcademico.getTramite();
        Alumno alumno = alumnoDAO.find(tramite.getAlumno());

        verificadorSolicitudService.verificarDocumentoAlumno(tramiteDocumentoAcademico, alumno);

        Usuario usuario = ds.getUsuario();
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        Compania compania = ds.getCompania();
        DateTime today = new DateTime();

        AccionTramiteDocumento accion = accionTramiteDocumentoDAO.findOrderOneByTipoDocumento(tramiteDocumentoAcademico.getTipoDocumentoAcademico(), 1L);
        EstadoTramite estadoTramite = accion.getEstadoTramite();
        TipoDocumentoCompaniaEnum tipoConEnum = tramiteDocumentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == TipoConstanciaEnum.CONS ? TipoDocumentoCompaniaEnum.TRAM_CONS : TipoDocumentoCompaniaEnum.TRAM_CERT;
        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(tipoConEnum);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), usuario);
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CONS.name());

        Persona persona = alumno.getPersona();

        String rutaFotoTemporal = (String) ObjectUtil.getParentTree(alumno, "persona.rutaFotoTemporal");
        if (!Strings.isNullOrEmpty(rutaFotoTemporal)) {
            persona.setRutaFotoTemporal(rutaFotoTemporal);
            personaDAO.update(persona);
            this.uploadS3(persona.getRutaFotoTemporal());

            estadoTramite = estadoTramiteDAO.find(16L);

        }

        tramite.setAlumno(alumno);
        tramite.setTipoSolicitante(TipoSolicitanteEnum.ALU.name());
        tramite.setCicloAcademico(cicloAcademico);
        tramite.setCompania(compania);
        tramite.setEstado(estadoTramite.getCodigo());
        tramite.setFechaRegistro(today.toDate());
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setTipoTramite(tipoTramite);
        tramite.setUserRegistro(usuario);
        tramite.setPersona(persona);
        tramiteDAO.save(tramite);

        TipoDocumentoAcademico tipo = tipoDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getTipoDocumentoAcademico());
        Idioma idioma = tramiteDocumentoAcademico.getIdioma();
        PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipo, idioma);
        tramiteDocumentoAcademico.setCantidadCiclos(1);
        BigDecimal monto = new BigDecimal(precio.getPrecio());
        if (tipo.getTipoConstanciaEnum() == TipoConstanciaEnum.CERT) {
            Long count = alumnoCicloDAO.countCiclosRegularTotal(alumno);
            tramiteDocumentoAcademico.setCantidadCiclos(count.intValue());
            monto = new BigDecimal(precio.getPrecio()).multiply(new BigDecimal(count));
        }

        OficinaEnum oficinaEnum = tramiteDocumentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == TipoConstanciaEnum.CONS ? OficinaEnum.UR : OficinaEnum.OERA;
        Oficina oficina = oficinaDAO.findByCode(oficinaEnum.name());

        tramiteDocumentoAcademico.setCorrelativoDocumento(serieDocumento.getNumeroDocumento() + "-" + oficina.getCodigoDocumento() + "/" + serieDocumento.getNumeroSerie());

        tramiteDocumentoAcademico.setTramite(tramite);
        tramiteDocumentoAcademico.setEstadoTramite(estadoTramite);
        tramiteDocumentoAcademico.setCostoTotal(monto);
        tramiteDocumentoAcademico.setCostoUnitario(new BigDecimal(precio.getPrecio()));
        tramiteDocumentoAcademicoDAO.save(tramiteDocumentoAcademico);

    }

    @Override
    public List<TipoDocumentoAcademico> allTipoDocumentoAcademico() {
        return tipoDocumentoAcademicoDAO.allWhyPrecios();
    }

    @Override
    public List<AccionTramiteDocumento> findEstadoByEstadoInicio(TipoDocumentoAcademico academico, EstadoTramite estadoTramite) {
        return accionTramiteDocumentoDAO.allByTipoTramiteAndEstadoTramiteInicial(academico, estadoTramite);
    }

    private String remplazarTablas(String htmlContent, Alumno alumno, List<VariablePlantilla> variable) {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        Document html = Jsoup.parse(htmlContent);
        if (!html.getElementsByClass(VARIABLE_TABLE).isEmpty()) {
            String tableOrigin = html.getElementsByClass(VARIABLE_TABLE).html();
            String tableClone = html.getElementsByClass(VARIABLE_TABLE).html();
            int idx = 1;
            int indexHtml = 1;
            for (AlumnoCicloCurso alumnoCicloCurso : alumnoCicloCursos) {
                for (VariablePlantilla var : variable) {
                    switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                        case TABLA_CODIGO_CURSO:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getCurso().getCodigo());
                            break;
                        case TABLA_CURSO:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getCurso().getNombre());
                            break;
                        case TABLA_CURSO_NOTA:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getNota());
                            break;
                        case TABLA_CURSO_CREDITO:
                            tableOrigin = tableOrigin.replace(var.getVariableGenerica().getCodigo(), alumnoCicloCurso.getCreditos().toString());
                            break;
                    }
                }

                if (indexHtml == idx) {
                    Element tr = html.select("tr").get(indexHtml);
                    tr.replaceWith(new Element("tr").append(tableOrigin));
                    indexHtml = 1;
                } else {
                    Element table = html.select("tbody").get(0);
                    Element trNew = new Element("tr");
                    trNew.append(tableOrigin);
                    table.insertChildren(indexHtml, trNew);
                    indexHtml++;
                }
                if (idx < alumnoCicloCursos.size()) {

                    tableOrigin = "";
                    tableOrigin = tableOrigin.concat(tableClone);
                    idx++;

                }
            }

            htmlContent = html.html();
        }
        return htmlContent;
    }

    @Override
    public PlantillaGenerica findPlantillaHtml(TramiteDocumentoAcademico documentoAcademico, Usuario usuario) {
        documentoAcademico = tramiteDocumentoAcademicoDAO.find(documentoAcademico);
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());

        String htmlContent = plantilla.getContenido();
        List<PlantillaIncrustacionDocumento> incrustacionDocumentos = plantillaIncrustacionDAO.allIncrustacionesByTramite(documentoAcademico);
        List<PlantillaDocumentoAcademico> plantillaDocumentoIncrustacion = incrustacionDocumentos.stream().map(x -> x.getPlatillaIncrustacion()).collect(Collectors.toList());
        List<VariablePlantilla> variablePlantillasIncrustacion = variablePlantillaDAO.allByPlantillas(plantillaDocumentoIncrustacion);
        List<VariablePlantilla> variables = variablePlantillaDAO.allByPlantilla(plantilla);
        variables.addAll(variablePlantillasIncrustacion);
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);

        Egresado egresado = egresadoDAO.findByAlumno(alumno);
        CicloAcademico cicloAcademicoAct = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
        htmlContent = this.addIncrustaciones(htmlContent, incrustacionDocumentos);
        htmlContent = this.recorrerVariables(htmlContent, variables, alumno, egresado, alumnoCiclos, documentoAcademico, cicloAcademicoAct, usuario);
        htmlContent = this.remplazarTablas(htmlContent, alumno, variables);

        Document html = Jsoup.parse(htmlContent);

        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        plantillaGene.setContenido(html.html());
        plantillaGene.setNombre(documentoAcademico.getTipoDocumentoAcademico().getNombre());
        return plantillaGene;
    }

    private String recorrerVariables(String htmlContent, List<VariablePlantilla> variables, Alumno alumno, Egresado egresado,
            List<AlumnoCiclo> alumnoCiclos, TramiteDocumentoAcademico documentoAcademico, CicloAcademico cicloAcademicoAct, Usuario usuario) {
        int idx = alumnoCiclos.size() - 1;

        OficinaEnum oficinaEnum = documentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == TipoConstanciaEnum.CONS ? OficinaEnum.UR : OficinaEnum.OERA;
        Oficina oficina = oficinaDAO.findByCode(oficinaEnum.name());
        ObtencionGrado obtencionGradoBachi = obtencionGradoDAO.findByAlumnoAndTipo(alumno, TipoGradoAcademicoEnum.BACH);
        ObtencionGrado obtencionGradoTitulo = obtencionGradoDAO.findByAlumnoAndTipo(alumno, TipoGradoAcademicoEnum.TIT);

        EventoCicloAcademico eventoAcademico = null;
        EventoCicloAcademico eventoFinAcademico = null;

        if (!alumnoCiclos.isEmpty()) {
            eventoAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(alumnoCiclos.get(0).getCicloAcademico(), FECHAS_BACH);
            eventoFinAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(alumnoCiclos.get(idx).getCicloAcademico(), FECHAS_BACH);
        }

        for (VariablePlantilla var : variables) {
            switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                case JEFE_OFICINA_OERA:
                case JEFE_URA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), oficina.getJefeEncargado() == null ? oficina.getPersonaJefe().getNombreCompleto() : oficina.getJefeEncargado().getNombreCompleto());
                    break;
                case CORRELATIVO_DOC:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getCorrelativoDocumento());
                    break;
                case SEX_IDENT:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getEstimado());
                    break;
                case SEX_ALUM:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getSexoEnum() == SexoEnum.F ? "a" : "o");
                    break;
                case TIPO_DOCUMENTO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getTipoDocumento().getNombre());
                    break;
                case NUMERO_DOCUMENTO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getNumeroDocIdentidad());
                    break;
                case MATRICULA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCodigo());
                    break;
                case FACULTAD:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                    break;

                case ESPECIALIDAD:
                case CARRERA:
                    if (!alumno.getCarrera().getFacultad().getCodigo().equals(alumno.getCarrera().getCodigo())) {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), " - Carrera de " + alumno.getCarrera().getNombre());
                    } else {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "");
                    }
                    break;
                case APELLIDO_PERSONA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getApellidosNombres());
                    break;
                case NOMBRE_PERSONA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getNombreCompleto());
                    break;

                case FECHA_CONSTANCIA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), TypesUtil.getStringDate(new Date(), "dd 'de' MMMM 'del' yyyy", "es"));
                    break;
                case APELLIDOS:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getApellidos());
                    break;
                case SENOR_A:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getSenior());
                    break;
                case ALUMNO_REGULAR:
                    MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademicoAct);
                    if (matriculaResumen != null && matriculaResumen.getCreditosMatriculados() >= 12) {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "regular");
                    } else {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "");
                    }

                    break;
                case PRIMER_CICLO_MATRICULADO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumnoCiclos.get(0).getCicloAcademico().getDescripcion());
                    break;
                case FECHA_PRIMERA_MATRICULA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), TypesUtil.getStringDate(eventoAcademico.getFechaInicio(), "dd/MM/yyyy"));
                    break;
                case ULTIMO_CICLO_MATRICULADO:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                    break;
                case NIVEL_ACADEMICO:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumnoCiclos.get(idx).getNivel() + "");

                    break;
                case CICLO_MATRICULA:
                    if (alumnoCiclos.get(idx).getCicloAcademico().getCodigo().equals(cicloAcademicoAct.getCodigo())) {

                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "Se encuentra matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                    } else {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "Estuvo matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());

                    }
                    break;
                case CANTIDAD_CREDITOS_APROBADOS:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumnoCiclos.get(0).getCreditosAprobadosAcumulados().toString());
                    break;
                case CANTIDAD_CURSOS_APROBADOS:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCursosAprobados().toString());
                    break;
                case PROMEDIO_PONDERADO_ACADEMICO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPromedioAcumulado().toString());
                    break;
                case FECHA_ULTIMA_MATRICULA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), TypesUtil.getStringDate(eventoFinAcademico.getFechaFin(), "dd/MM/yyyy"));
                    break;
                case CICLOS_CURSADOS:

                    String ciclos = alumnoCiclos.size() > 2 ? "los ciclos " : "el ciclo ";
                    int i = 1;
                    for (AlumnoCiclo ac : alumnoCiclos) {
                        if (i == alumnoCiclos.size()) {
                            ciclos = ciclos.concat("y " + ac.getCicloAcademico().getDescripcion());
                            continue;
                        }
                        ciclos = ciclos.concat(", " + ac.getCicloAcademico().getDescripcion());
                        i++;
                    }
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), ciclos);
                    break;
                case TITULO_PROFESIONAL:
                    if (obtencionGradoTitulo != null && obtencionGradoTitulo.getGradoAcademico() != null) {

                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), obtencionGradoTitulo.getGradoAcademico().getNombre());
                    } else if (obtencionGradoBachi != null && obtencionGradoBachi.getGradoAcademico() != null) {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), obtencionGradoBachi.getGradoAcademico().getNombre());

                    } else if (egresado != null && egresado.getTitulo() != null) {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getTitulo().getNombre());

                    }

                    break;
                case CICLO_PROMOCION:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getCicloAcademico().getCodigo());
                    break;
                case CICLO_EGRESO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getCicloAcademico().getDescripcion());
                    break;

                case PROGRAMA:
                    String programa = "";
                    if (alumno.getCarrera().getCodigo().equals(CODIGO_ALIANZA_ESTRATEGICA)) {
                        programa = programa.concat("por el Convenio de la " + alumno.getCarrera().getNombre());
                    } else {

                        programa = programa.concat("como " + alumno.getPersona().getGeneroAlumno("alter") + " " + alumno.getCarrera().getNombre());
                    }

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), programa);
                    break;
                case FECHA_EGRESO:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), TypesUtil.getStringDate(eventoFinAcademico.getFechaFin(), "dd/MM/yyyy"));

                    break;
                case RESOL_EGRESO:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), obtencionGradoBachi.getResolucion().getDescripcion());

                    break;
                case RESOL_FECHA:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), TypesUtil.getStringDate(obtencionGradoBachi.getResolucion().getFecha(), "dd/MM/yyyy"));

                    break;
                case RESOL_TITULO:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), obtencionGradoTitulo.getResolucion().getDescripcion());

                    break;
                case RESOL_TITULO_FECHA:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), TypesUtil.getStringDate(obtencionGradoTitulo.getResolucion().getFecha(), "dd/MM/yyyy"));

                    break;
                case ORDEN_MERITO_EGRESADO:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getOrdenMeritoFacultad() + "");

                    break;
                case CANTIDAD_ALUMNOS:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getControlMeritoFacultad().getTotalAlumnos() + "");

                    break;
                case PROMEDIO_PONDERADO_GRADUACION:
                    if (egresado.getPromedioGraduacion() == null) {
                        BigDecimal ppg = this.promedioGraduacion(alumno);
                        egresado.setPromedioGraduacion(ppg);
                        egresadoDAO.updateColumns(egresado, "promedioGraduacion");
                    }
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getPromedioGraduacion().toString());

                    break;
                case MEJOR_PROMEDIO_PONDERADO_GRADUACION:

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getPromedioGraduacion().toString());

                    break;

            }
        }
        return htmlContent;
    }

    private BigDecimal promedioGraduacion(Alumno alumno) {
        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;

        List<AlumnoCicloCurso> alumnosCiclosCursosActivos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        for (AlumnoCicloCurso cursoAluCicloEach : alumnosCiclosCursosActivos) {
            if (cursoAluCicloEach.getCreditos() > 0
                    && cursoAluCicloEach.isAprobado()
                    && !Arrays.asList("AP", "TE").contains(cursoAluCicloEach.getNota())) {

                BigDecimal notaBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getNota());
                BigDecimal creditosBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getCreditos());

                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);

            }
        }
        BigDecimal ppg = sumNotasCreditos.divide(sumCreditos, 2, RoundingMode.HALF_UP);

        return ppg;
    }

    @Override
    public List<PlantillaDocumentoAcademico> allPlantillas() {
        return plantillaDocumentoAcademicoDAO.allIncrustaciones();
    }

    @Override
    public void validVariables(PlantillaIncrustacionGeneralBean plantillaGeneralBean, DataSessionPivot ds) {
        PlantillaIncrustacionDocumento pid = plantillaIncrustacionDAO.findTramiteAndPlantilla(plantillaGeneralBean.getTramiteDocumentoAcademico(), plantillaGeneralBean.getPlantillaDocumentoAcademico());
        Assert.isNull(pid, "Ya se agregó la incrustación " + plantillaGeneralBean.getPlantillaDocumentoAcademico().getNombre() + " para este Tramite");

        TramiteDocumentoAcademico academico = tramiteDocumentoAcademicoDAO.find(plantillaGeneralBean.getTramiteDocumentoAcademico());
        verificadorSolicitudService.verificarDocumentoAlumno(plantillaGeneralBean.getPlantillaDocumentoAcademico(), academico, academico.getTramite().getAlumno());

        List<PlantillaIncrustacionDocumento> incrustacionDocumentos = plantillaIncrustacionDAO.allIncrustacionesByTramite(plantillaGeneralBean.getTramiteDocumentoAcademico());

        Integer orden = incrustacionDocumentos == null ? 1 : incrustacionDocumentos.size() + 1;

        PlantillaIncrustacionDocumento incrustacionDocumento = new PlantillaIncrustacionDocumento();
        incrustacionDocumento.setContenido(plantillaGeneralBean.getPlantillaDocumentoAcademico().getContenido());
        incrustacionDocumento.setPlatillaIncrustacion(plantillaGeneralBean.getPlantillaDocumentoAcademico());
        incrustacionDocumento.setFechaRegistro(new Date());
        incrustacionDocumento.setOrden(orden);
        incrustacionDocumento.setUsuarioRegistro(ds.getUsuario());
        incrustacionDocumento.setTramiteDocumento(academico);
        plantillaIncrustacionDAO.save(incrustacionDocumento);
    }

    @Override
    public List<CicloAcademico> allCicloAcademicoByName(String nombre) {
        return cicloAcademicoDAO.allCicloByName(nombre);
    }

    @Override
    public List<PlantillaIncrustacionDocumento> allTramiteIncrustaciones(TramiteDocumentoAcademico documentoAcademico) {
        return plantillaIncrustacionDAO.allIncrustacionesByTramite(documentoAcademico);
    }

    @Override
    @Transactional
    public TramiteDocumentoAcademico deleteIncrustacion(PlantillaIncrustacionDocumento plantillaIncrustacionDocumento) {
        PlantillaIncrustacionDocumento incrustacionDocumentoBD = plantillaIncrustacionDAO.find(plantillaIncrustacionDocumento.getId());
        TramiteDocumentoAcademico documentoAcademico = incrustacionDocumentoBD.getTramiteDocumento();
        List<PlantillaIncrustacionDocumento> incrustacionDocumentos = plantillaIncrustacionDAO.allIncrustacionesByTramite(documentoAcademico);

        for (PlantillaIncrustacionDocumento incrustacionDocumento : incrustacionDocumentos) {
            if (incrustacionDocumento.getOrden() > plantillaIncrustacionDocumento.getOrden()) {
                Integer orden = incrustacionDocumento.getOrden() - 1;
                incrustacionDocumento.setOrden(orden);
                plantillaIncrustacionDAO.update(incrustacionDocumento);
            }
        }
        plantillaIncrustacionDAO.delete(plantillaIncrustacionDocumento.getId());

        return incrustacionDocumentoBD.getTramiteDocumento();
    }

    @Override
    public List<VariablePlantilla> allParametros(PlantillaDocumentoAcademico pid) {

        TipoDocumentoAcademico tipoDocumentoAcademico = tipoDocumentoAcademicoDAO.find(pid.getTipoDocumentoAcademico());
        pid = plantillaDocumentoAcademicoDAO.findTipoDocumento(tipoDocumentoAcademico, pid.getIdioma());
        if (tipoDocumentoAcademico.getTipoConstanciaEnum() == TipoConstanciaEnum.CONS) {
            Assert.isNotNull(pid, "No existe una plantilla para el documento.");
        }
        return variablePlantillaDAO.allByPlantillaParametro(pid);
    }

    @Override
    public List<AlumnoCiclo> allAlumnoCiclo(Alumno alumno) {

        return alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
    }

    @Override
    public List<AlumnoCiclo> allAlumnoCiclo(TramiteDocumentoAcademico tramiteDocumentoAcademico) {

        Alumno alumno = tramiteDocumentoAcademico.getTramite().getAlumno();
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);

        Map<Long, List<AlumnoCicloCurso>> map = TypesUtil.convertListToMapList("alumnoCiclo.id", alumnoCicloCursos);
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
        for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
            alumnoCiclo.setAlumnoCicloCurso(map.get(alumnoCiclo.getId()));
        }

        return alumnoCiclos;
    }

    @Override
    public Archivo findBoletas(Long idTramiteDocumento) {

        return archivoDAO.findFirstByInstanciasTipoInstancia(idTramiteDocumento, InstanciaEnum.TRAM_DOCUMENTO);
    }

    private String addIncrustaciones(String htmlContent, List<PlantillaIncrustacionDocumento> incrustacionDocumentos) {
        String htmlIncrustacion = "";
        for (PlantillaIncrustacionDocumento incrustacionDocumento : incrustacionDocumentos) {
            htmlIncrustacion = htmlIncrustacion + incrustacionDocumento.getContenido() + "\n";
        }
        htmlContent = htmlContent.replace(INCRUSTACION.getValue(), htmlIncrustacion);
        return htmlContent;
    }

    @Override
    @Transactional
    public void saveArchivoTramite(Archivo archivo, Alumno alumno, DataSessionPivot ds) {

        TramiteDocumentoAcademico tramite = tramiteDocumentoAcademicoDAO.find(archivo.getIdInstancia());

        Archivo archivoDB = archivoDAO.findFirstByInstanciasTipoInstancia(tramite.getId(), TRAM_DOCUMENTO);
        if (!Objects.equal(archivoDB, null)) {
            archivoDAO.delete(archivoDB);
        }

        uploadFileS3.uploadSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.TMP_DIR, archivo.getNombre(), true);
        String path = uploadFileS3.getPathFile(AcademicoConstantine.S3_TRAMITE_DOCUMENTO, archivo.getNombre());

        Archivo newarchivo = new Archivo();
        newarchivo.setFechaRegistro(new Date());
        newarchivo.setInstancia(TRAM_DOCUMENTO.name());
        newarchivo.setIdInstancia(tramite.getId());
        newarchivo.setTipo(archivo.getTipo());
        newarchivo.setUsuarioRegistro(ds.getUsuario());
        newarchivo.setNombre(archivo.getNombre());
        newarchivo.setRuta(path);
        archivoDAO.save(newarchivo);
        tramite.setArchivo(newarchivo);
        tramiteDocumentoAcademicoDAO.update(tramite);
    }

    @Override
    @Transactional
    public void validarBoletaTramite(TramiteDocumentoAcademico idTramiteDocumentoAcademico) {

        TramiteDocumentoAcademico tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.find(idTramiteDocumentoAcademico);

        if (tramiteDocumentoAcademico == null) {
            throw new PhobosException("No se ha encontrado el trámite");
        }

        if (tramiteDocumentoAcademico.getEstadoTramite().getCodigoEnum() != TramiteEstadoEnum.CRE) {
            throw new PhobosException("El pago ya fue validado");
        }

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ACEP);
        tramiteDocumentoAcademico.setEstadoTramite(estadoTramite);
        tramiteDocumentoAcademico.setNumeroBoleta(idTramiteDocumentoAcademico.getNumeroBoleta());
        tramiteDocumentoAcademicoDAO.update(tramiteDocumentoAcademico);
    }

    @Override
    @Transactional
    public void entregarTramite(TramiteDocumentoAcademico idTramiteDocumentoAcademico) {

        TramiteDocumentoAcademico tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.find(idTramiteDocumentoAcademico.getId());

        if (tramiteDocumentoAcademico == null) {
            throw new PhobosException("No se ha encontrado el trámite");
        }

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.COMP);
        tramiteDocumentoAcademico.setEstadoTramite(estadoTramite);
        tramiteDocumentoAcademico.setFechaEntrega(new Date());
        tramiteDocumentoAcademico.setNumeroFormato(idTramiteDocumentoAcademico.getNumeroFormato());
        tramiteDocumentoAcademicoDAO.update(tramiteDocumentoAcademico);
    }

    @Override
    @Transactional
    public void anularTramite(Long idTramiteDocumentoAcademico) {

        TramiteDocumentoAcademico tramiteDocumentoAcademico
                = tramiteDocumentoAcademicoDAO.find(new TramiteDocumentoAcademico(idTramiteDocumentoAcademico));

        if (tramiteDocumentoAcademico == null) {
            throw new PhobosException("No se ha encontrado el trámite");
        }

        EstadoTramite estadoTramite = estadoTramiteDAO.findByCodigoEnum(TramiteEstadoEnum.ANU);
        tramiteDocumentoAcademico.setEstadoTramite(estadoTramite);
        tramiteDocumentoAcademicoDAO.update(tramiteDocumentoAcademico);
    }

    @Override
    public BigDecimal calcularPrecio(TramiteDocumentoAcademico tramiteDocumentoAcademico, Long cantidadCiclos) {

        TipoDocumentoAcademico tipoDocumentoAcademico = tipoDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getTipoDocumentoAcademico());

        if (tipoDocumentoAcademico.getTipoConstanciaEnum() == TipoConstanciaEnum.CONS) {
            PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipoDocumentoAcademico, tramiteDocumentoAcademico.getIdioma());
            return new BigDecimal(precio.getPrecio());
        }

        Idioma idioma = tramiteDocumentoAcademico.getIdioma();

        PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipoDocumentoAcademico, idioma);

        BigDecimal monto = new BigDecimal(precio.getPrecio());

        if (tipoDocumentoAcademico.getTipoConstanciaEnum() == TipoConstanciaEnum.CERT) {
            tramiteDocumentoAcademico.setCantidadCiclos(cantidadCiclos.intValue());
            return new BigDecimal(precio.getPrecio()).multiply(new BigDecimal(cantidadCiclos));
        }

        return null;

    }

    @Override
    public Egresado getEgresadoByIdPersona(Long idAlumno) {
        return egresadoDAO.findByAlumno(new Alumno(idAlumno));
    }

    @Override
    public Long cantidadCiclosRegularAprobado(Alumno alumno) {
        return alumnoCicloDAO.countCiclosRegularTotal(alumno);
    }

    @Override
    public BigDecimal costoDocumento(TramiteDocumentoAcademico tramiteDocumentoAcademico) {

        TipoDocumentoAcademico tipoDocumentoAcademico = tipoDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getTipoDocumentoAcademico());

        if (tipoDocumentoAcademico.getTipoConstanciaEnum() == TipoConstanciaEnum.CONS) {
            PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipoDocumentoAcademico, tramiteDocumentoAcademico.getIdioma());
            return new BigDecimal(precio.getPrecio());
        }

        Idioma idioma = tramiteDocumentoAcademico.getIdioma();

        PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipoDocumentoAcademico, idioma);

        return new BigDecimal(precio.getPrecio());
    }

    @Override
    public BigDecimal getPrecioDocumento(TramiteDocumentoAcademico documentoAcademico) {
        PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        if (precio == null) {
            return ZERO;
        }
        try {
            return new BigDecimal(precio.getPrecio());
        } catch (Exception e) {
            return ZERO;
        }
    }

}
