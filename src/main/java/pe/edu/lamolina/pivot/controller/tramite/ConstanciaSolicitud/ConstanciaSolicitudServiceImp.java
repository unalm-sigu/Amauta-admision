package pe.edu.lamolina.pivot.controller.tramite.ConstanciaSolicitud;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.aws.S3Service;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ControlOrdenMerito;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.bean.PlantillaIncrustacionGeneralBean;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.CuentaBancariaEnum;
import pe.edu.lamolina.model.enums.DeudaEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoAcreenciaTramiteEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.NombreTablasEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoConstanciaEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.CICLO_ACADEMICO;
import pe.edu.lamolina.model.finanzas.Acreencia;
import pe.edu.lamolina.model.finanzas.AcreenciaTramiteDocumento;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
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
import pe.edu.lamolina.model.tramite.TramiteDocumentoParametro;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.academico.situacionacademica.SituacionAcademicaService;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia.PlantillaGenerica;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ControlOrdenMeritoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.EgresadoDAO;
import pe.edu.lamolina.pivot.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaTramiteDocumentoDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.tramite.AccionTramiteDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.FlujoTramiteDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.FormularioEstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaDocumentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.PrecioDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoConstanciaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDocumentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.tramite.VariablePlantillaDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.mail.MailerService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaIncrustacionDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDocumentoParametroDAO;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.VARIABLE_TABLE;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.CODIGO_ALIANZA_ESTRATEGICA;
import static pe.edu.lamolina.pivot.zelper.constant.Constantine.VARIABLE_INCRUSTACION;

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
    TipoConstanciaDAO tipoDocumentoAcademicoDAO;

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
    VisorCalculoNotas visorCalculoNotas;

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
    AcreenciaDAO acreenciaDAO;

    @Autowired
    S3Service s3Service;

    @Override
    @Transactional
    public void updateHistorialAcademico(Alumno alumnoForm, DataSessionPivot ds) {

        Usuario usuario = ds.getUsuario();
        Alumno alumno = alumnoDAO.find(alumnoForm);
        logger.debug("alumno id   {} codigo {} ", alumno.getId(), alumno.getCodigo());
        List<AlumnoCiclo> alumnosCiclo = alumnoForm.getAlumnoCiclo();
        List<AlumnoCiclo> alumnosCicloDb = alumnoCicloDAO.allByAlumno(alumno);
        logger.debug("existen  {} alumnoCiclo en db", alumnosCicloDb.size());
        if (!alumnosCicloDb.isEmpty()) {
            List<Long> alumnoCicloss = new ArrayList();
            for (AlumnoCiclo alumnoCiclo : alumnosCiclo) {
                if (alumnoCiclo.getId() != null) {
                    alumnoCicloss.add(alumnoCiclo.getId());
                }
            }
            Map<Long, AlumnoCiclo> alumnosCicloMap = TypesUtil.convertListToMap("id", alumnosCicloDb);
            List<AlumnoCiclo> alumnosCicloDelete = new ArrayList();
            for (Map.Entry<Long, AlumnoCiclo> entry : alumnosCicloMap.entrySet()) {
                Long key = entry.getKey();
                if (!alumnoCicloss.contains(key)) {
                    alumnosCicloDelete.add(entry.getValue());
                }
            }
            for (AlumnoCiclo alumnoCiclo : alumnosCicloDelete) {
                if (alumnoCiclo.getEstadoEnum() != EstadoMatriculaEnum.NMAT) {
                    logger.debug("remove alumnoCiclo {}", alumnoCiclo.getId());
                    alumnoCicloCursoDAO.deleteByAlumnoCiclo(alumnoCiclo);
                    alumnoCicloDAO.delete(alumnoCiclo);
                }
            }
        }

        for (AlumnoCiclo alumnoCicloForm : alumnosCiclo) {

            CicloAcademico cicloAcademico = cicloAcademicoDAO.find(alumnoCicloForm.getCicloAcademico());

            AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findByAlumnoCiclo(alumno, cicloAcademico);
            DateTime today = new DateTime();

            if (alumnoCiclo == null) {
                alumnoCiclo = new AlumnoCiclo();
                alumnoCiclo.setAlumno(alumno);
                alumnoCiclo.setCarrera(alumno.getCarrera());
                alumnoCiclo.setCicloAcademico(cicloAcademico);

                alumnoCiclo.setCreditosAcumulados(BigDecimal.ZERO.intValue());
                alumnoCiclo.setCreditosAprobadosAcumulados(BigDecimal.ZERO.intValue());

                alumnoCiclo.setCreditosAprobadosCiclo(BigDecimal.ZERO.intValue());
                alumnoCiclo.setCreditosCursadosCiclo(BigDecimal.ZERO.intValue());
                alumnoCiclo.setCursosAprobados(BigDecimal.ZERO.intValue());
                alumnoCiclo.setCursosInscritos(BigDecimal.ZERO.intValue());

                alumnoCiclo.setEstadoEnum(EstadoMatriculaEnum.MAT);
                alumnoCiclo.setUserRegistro(usuario);
                alumnoCiclo.setUserModificacion(usuario);
                alumnoCiclo.setFechaModificacion(today.toDate());
                alumnoCiclo.setFechaRegistro(today.toDate());
                alumnoCiclo.setOrientacionCarrera(alumno.getOrientacionCarrera());

                alumnoCiclo.setSituacionInicio(alumno.getSituacionAcademica());
                alumnoCiclo.setEstaAprobado(BigDecimal.ZERO.intValue());

                alumnoCiclo.setPromedioAcumulado(BigDecimal.ZERO);
                alumnoCiclo.setPromedioCiclo(BigDecimal.ZERO);
                alumnoCicloDAO.save(alumnoCiclo);
                alumno.getId();
            }

            List<AlumnoCicloCurso> alumnosCicloCurso = alumnoCicloForm.getAlumnoCicloCurso();
            List<AlumnoCicloCurso> alumnosCicloCursoDb = alumnoCicloCursoDAO.allByAlumnoCiclo(alumnoCiclo);
            logger.debug("existen  {} AlumnoCicloCurso en db", alumnosCicloCursoDb.size());
            if (!alumnosCicloCursoDb.isEmpty()) {
                List<Long> alumnoCicloCursoss = new ArrayList();
                for (AlumnoCicloCurso alumnoCicloCurso : alumnosCicloCurso) {
                    if (alumnoCicloCurso.getId() != null) {
                        alumnoCicloCursoss.add(alumnoCicloCurso.getId());
                    }
                }
                Map<Long, AlumnoCicloCurso> alumnosCicloCursoMap = TypesUtil.convertListToMap("id", alumnosCicloCursoDb);
                List<AlumnoCicloCurso> alumnosCicloCursoDelete = new ArrayList();
                for (Map.Entry<Long, AlumnoCicloCurso> entry : alumnosCicloCursoMap.entrySet()) {
                    Long key = entry.getKey();
                    if (!alumnoCicloCursoss.contains(key)) {
                        alumnosCicloCursoDelete.add(entry.getValue());
                    }
                }
                for (AlumnoCicloCurso alumnoCicloCurso : alumnosCicloCursoDelete) {
                    if (alumnoCicloCurso.getEstadoEnum() != EstadoMatriculaEnum.NMAT) {
                        logger.debug("remove alumnoCiclo {}", alumnoCicloCurso.getId());
                        alumnoCicloCursoDAO.delete(alumnoCicloCurso);
                    }
                }
            }

            for (AlumnoCicloCurso alumnoCicloCursoForm : alumnosCicloCurso) {

                Curso curso = cursoDAO.find(alumnoCicloCursoForm.getCurso().getId());
                AlumnoCicloCurso alumnoCicloCurso = null;
                if (alumnoCicloCursoForm.getId() != null) {
                    alumnoCicloCurso = alumnoCicloCursoDAO.find(alumnoCicloCursoForm);
                } else {
                    alumnoCicloCurso = alumnoCicloCursoDAO.findByAlumnoCicloCurso(alumno, cicloAcademico, curso);
                }

                if (alumnoCicloCurso == null) {

                    alumnoCicloCurso = new AlumnoCicloCurso();
                    alumnoCicloCurso.setAlumnoCiclo(alumnoCiclo);

                    alumnoCicloCurso.setCreditos(alumnoCicloCursoForm.getCreditos());
                    alumnoCicloCurso.setCurso(curso);
                    alumnoCicloCurso.setEstaAprobado(BigDecimal.ZERO.intValue());

                    alumnoCicloCurso.setEstado(EstadoMatriculaEnum.MAT);
                    alumnoCicloCurso.setFechaModificacion(today.toDate());
                    alumnoCicloCurso.setFechaRegistro(today.toDate());

                    alumnoCicloCurso.setNota(alumnoCicloCursoForm.getNota());
                    alumnoCicloCurso.setOrigenData(OrigenDataSituacionAcademicaEnum.ACTA);
                    alumnoCicloCurso.setRegistroActivo(BigDecimal.ONE.intValue());
                    alumnoCicloCurso.setUserModificacion(usuario);
                    alumnoCicloCurso.setUsuarioRegistro(usuario);

                    alumnoCicloCurso.setVecesCursado(1);
                    alumnoCicloCursoDAO.save(alumnoCicloCurso);
                    alumnoCicloCurso.getId();
                } else {

                    alumnoCicloCurso.setFechaModificacion(today.toDate());
                    alumnoCicloCurso.setNota(alumnoCicloCursoForm.getNota());
                    alumnoCicloCurso.setUserModificacion(usuario);
                    alumnoCicloCurso.setCurso(curso);

                    alumnoCicloCursoDAO.update(alumnoCicloCurso);
                    alumnoCicloCurso.getId();
                }

            }

            visorCalculoNotas.setActivo(false);
            promedioService.calulcarSituacionAcademica(alumno, ds);

        }
    }

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
        Tramite tramiteForm = tramiteDocumentoAcademico.getTramite();
        Alumno alumno = alumnoDAO.find(tramiteForm.getAlumno());
        tramite.setAlumno(alumno);
        tramite.setUserModificacion(ds.getUsuario());
        tramite.setFechaModificacion(new Date());
        tramiteDAO.update(tramite);

        Persona persona = alumno.getPersona();
        String rutaFotoTemporal = (String) ObjectUtil.getParentTree(tramiteForm, "persona.rutaFotoTemporal");
        if (!Strings.isNullOrEmpty(rutaFotoTemporal)) {
            persona.setRutaFotoTemporal(rutaFotoTemporal);
            personaDAO.update(persona);
            this.uploadS3(persona.getRutaFotoTemporal());
        }

        tda.setPersonaContacto(tramiteDocumentoAcademico.getPersonaContacto());
        tda.setEmail(tramiteDocumentoAcademico.getEmail());
        tda.setTelefono(tramiteDocumentoAcademico.getTelefono());
        tda.setCelular(tramiteDocumentoAcademico.getCelular());
        tda.setIdioma(tramiteDocumentoAcademico.getIdioma());
        tda.setTipoDocumentoAcademico(tramiteDocumentoAcademico.getTipoDocumentoAcademico());
        tramiteDocumentoAcademicoDAO.update(tda);

        this.enviarNotificacionSolicitudConstanciaCreacion(tda);
    }

    @Override
    public List<MatriculaResumen> allMatriculaResumenByAlumno(Alumno alumno) {
        return matriculaResumenDAO.allMatriculaResumenByAlumno(alumno);
    }

    @Override
    public List<Idioma> allIdiomas() {
        return idiomaDAO.allByCodigo(Arrays.asList(Constantine.CODE_IDIOMA_ESPANOL, Constantine.CODE_IDIOMA_INGLES));
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
        logger.debug("upload to s3    {}  {}   {}  {} {}", Constantine.S3_BUKET, Constantine.S3_DIR_FOTO_TMP, Constantine.TMP_DIR, fileName, true);
        File f = new File(Constantine.TMP_DIR + fileName);
        if (f.exists() && !f.isDirectory()) {
            s3Service.uploadFile(Constantine.S3_BUKET, Constantine.S3_DIR_FOTO_TMP, Constantine.TMP_DIR, fileName, true);
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

        Tramite tramite = tramiteDocumentoAcademico.getTramite();
        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
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

        tramiteDocumentoAcademico.setTramite(tramite);
        tramiteDocumentoAcademico.setEstadoTramite(estadoTramite);
        tramiteDocumentoAcademico.setCantidadCiclos(1);
        tramiteDocumentoAcademicoDAO.save(tramiteDocumentoAcademico);

        TipoDocumentoAcademico tipo = tipoDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getTipoDocumentoAcademico());
        Idioma idioma = tramiteDocumentoAcademico.getIdioma();
        PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipo, idioma);

        BigDecimal monto = new BigDecimal(precio.getPrecio());
        if (tipo.getTipoConstanciaEnum() == TipoConstanciaEnum.CERT) {
            Long count = alumnoCicloDAO.countCiclosRegular(alumno);
            monto = new BigDecimal(precio.getPrecio()).multiply(new BigDecimal(count));
        }

        AcreenciaTramiteDocumento acreenciaTram = new AcreenciaTramiteDocumento();
        acreenciaTram.setEstado(EstadoAcreenciaTramiteEnum.ACT.name());
        acreenciaTram.setTramiteDocumentoAcademico(tramiteDocumentoAcademico);
        acreenciaTram.setUserRegistro(usuario);
        acreenciaTram.setFechaRegistro(new Date());
        LocalDate localDate = LocalDate.now();
        LocalDate fechaVencimiento = localDate.plusDays(3);
        acreenciaTram.setFechaVencimiento(fechaVencimiento.toDate());
        acreenciaTram.setPrecio(BigDecimal.ZERO);
        acreenciaTram.setPrecio(monto);
        acreenciaTramiteDocumentoDAO.save(acreenciaTram);

        CuentaBancaria ctaBanco = precio.getCuentaBancaria();

        Acreencia acreencia = new Acreencia();
        if (ctaBanco.getCodigo().equals(CuentaBancariaEnum.MAT_UNALM.getCodigoServ())) {//credipago matricula
            acreencia.setDescripcion("Deuda Académica");
        } else if (ctaBanco.getCodigo().equals(CuentaBancariaEnum.MAT_FDA.getCodigoServ())) {//credipago bienestar
            acreencia.setDescripcion("Deuda Bienestar");
        }

        acreencia.setOficina(new Oficina(OficinaEnum.OBUAE.getId()));
        acreencia.setTablaEnum(NombreTablasEnum.FIN_ACREENCIA_TRAMITE_DOCUMENTO);
        acreencia.setInstanciaTabla(acreenciaTram.getId());
        acreencia.setEstadoEnum(DeudaEstadoEnum.DEU);

        acreencia.setMonto(monto);
        acreencia.setAbono(BigDecimal.ZERO);
        acreencia.setPersona(alumno.getPersona());
        acreencia.setCuentaBancaria(ctaBanco);
        acreencia.setFechaDocumento(new Date());
        acreencia.setUsuarioRegistro(ds.getUsuario());
        acreencia.setFechaVencimiento(fechaVencimiento.toDate());
        acreencia.setFechaRegistro(new Date());
        acreenciaDAO.save(acreencia);

        FlujoTramiteDocumento flujo = new FlujoTramiteDocumento();
        flujo.setEstadoTramite(estadoTramite);
        flujo.setOficinaOrigen(ds.getOficinaMain());
        flujo.setOficinaDestino(ds.getOficinaMain());
        flujo.setUserRegistro(ds.getUsuario());
        flujo.setTramiteDocumentoAcademico(tramiteDocumentoAcademico);
        flujo.setFechaRegistro(new Date());
        flujoTramiteDocumentoDAO.save(flujo);

        if (tramiteDocumentoAcademico.getValorParametro() != null) {
            PlantillaDocumentoAcademico plantillaDocumentoAcademico = plantillaDocumentoAcademicoDAO.findTipoDocumento(tipo, idioma);
            List<VariablePlantilla> plantillas = allParametros(plantillaDocumentoAcademico);
            TramiteDocumentoParametro documentoParametro = new TramiteDocumentoParametro();
            documentoParametro.setPlantillaDocumento(plantillaDocumentoAcademico);
            documentoParametro.setTipoDocumentoAcademico(tipo);
            documentoParametro.setValor(tramiteDocumentoAcademico.getValorParametro());
            documentoParametro.setFecharegistro(new Date());
            documentoParametro.setUsuario(usuario);
            documentoParametro.setVariableGenerica(plantillas.get(0).getVariableGenerica());
            tramiteDocumentoParamtroDAO.save(documentoParametro);
        }
        this.enviarNotificacionSolicitudConstanciaCreacion(tramiteDocumentoAcademico);
    }

    @Override
    public List<TipoDocumentoAcademico> allTipoDocumentoAcademico() {
        return tipoDocumentoAcademicoDAO.allWhyPrecios();
    }

    @Override
    public List<AccionTramiteDocumento> findEstadoByEstadoInicio(TipoDocumentoAcademico academico, EstadoTramite estadoTramite) {
        return accionTramiteDocumentoDAO.allByTipoTramiteAndEstadoTramiteInicial(academico, estadoTramite);
    }

    @Override
    public void update(TramiteDocumentoAcademico tramiteDocumentoAcademicoForm, DataSessionPivot ds) {
        TramiteDocumentoAcademico tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademicoForm);
        tramiteDocumentoAcademicoDAO.updateColumns(tramiteDocumentoAcademicoForm, "estadoTramite");

        Tramite tramite = tramiteDocumentoAcademico.getTramite();
        tramite.setEstadoEnum(TramiteEstadoEnum.PROC);
        tramite.setUserModificacion(ds.getUsuario());
        tramite.setFechaModificacion(new Date());
        tramiteDAO.updateEstado(tramite);

        FlujoTramiteDocumento flujo = new FlujoTramiteDocumento();
        flujo.setEstadoTramite(tramiteDocumentoAcademicoForm.getEstadoTramite());
        flujo.setOficinaOrigen(ds.getOficinaMain());
        flujo.setOficinaDestino(ds.getOficinaMain());
        flujo.setUserRegistro(ds.getUsuario());
        flujo.setTramiteDocumentoAcademico(tramiteDocumentoAcademico);
        flujo.setFechaRegistro(new Date());
        flujoTramiteDocumentoDAO.save(flujo);

    }

    @Override
    public void downloadWord(TramiteDocumentoAcademico tramiteDocumentoAcademico, HttpServletResponse response) throws PhobosException {
        tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademico);
        PlantillaGenerica generica = findPlantillaHtml(tramiteDocumentoAcademico, null);

        try {

            response.setBufferSize(Constantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "inline; filename=\"" + generica.getNombre() + ".doc\"");

            OutputStream outputStream = response.getOutputStream();
            outputStream.write((Constantine.HTML_PRE + generica.getContenido() + Constantine.HTML_SUB).getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            logger.error("(downloadTemporal)Error Descarga de Archivo: {}, fileName: {}", ex.getLocalizedMessage(), generica.getNombre());
        }
    }

    private String recorrerVariables(String htmlContent, List<VariablePlantilla> variables, Alumno alumno, Egresado egresado, List<AlumnoCiclo> alumnoCiclos, TramiteDocumentoAcademico documentoAcademico, Usuario usuario) {
        for (VariablePlantilla var : variables) {
            switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                case MATRICULA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCodigo());
                    break;
                case FACULTAD:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                    break;
                case ESPECIALIDAD:
                    if (!alumno.getCarrera().getFacultad().getCodigo().equals(alumno.getCarrera().getCodigo())) {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "Esp. de " + alumno.getCarrera().getNombre());
                    } else {
                        htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), "");
                    }
                    break;
                case APELLIDO_PERSONA:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getApellidosNombres());
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
                case CICLO_PROMOCION:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.getCicloAcademico().getCodigo());
                    break;
                case EPG_PROMEDIO_PONDERADO:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), egresado.returnPromedioGraduacionTrunc(2).toString());
                    break;
                case CORRELATIVO_DOC:
                    if (documentoAcademico.getCorrelativoDocumento() == null) {
                        DateTime today = new DateTime();

                        TipoDocumentoCompaniaEnum tipoConEnum = documentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == TipoConstanciaEnum.CONS ? TipoDocumentoCompaniaEnum.DOC_CONS : TipoDocumentoCompaniaEnum.DOC_CERT;
                        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(tipoConEnum);
                        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), usuario);
                        
                        documentoAcademico.setCorrelativoDocumento(serieDocumento.getNumeroDocumento() + "-UR/" + serieDocumento.getNumeroSerie());
                        tramiteDocumentoAcademicoDAO.updateColumns(documentoAcademico, "correlativoDocumento");
                    }
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getCorrelativoDocumento());
                    break;
                case CICLO_ROM_FIN:
                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), alumno.getCicloActivo().getDescripcion());
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

                case PROGRAMA:
                    String programa = "";
                    if (alumno.getCarrera().getCodigo().equals(CODIGO_ALIANZA_ESTRATEGICA)) {
                        programa = programa.concat("por el Convenio de la " + alumno.getCarrera().getNombre());
                    } else {

                        programa = programa.concat("como " + alumno.getPersona().getGeneroAlumno() + " " + alumno.getCarrera().getNombre());
                    }

                    htmlContent = htmlContent.replace(var.getVariableGenerica().getCodigo(), programa);
                    break;
            }
        }
        return htmlContent;
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
        System.out.println("CANTIDAD: ---- >" + incrustacionDocumentos.size());

        List<VariablePlantilla> variables = variablePlantillaDAO.allByPlantilla(plantilla);
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);

        Egresado egresado = egresadoDAO.findByAlumno(alumno);

        htmlContent = this.recorrerVariables(htmlContent, variables, alumno, egresado, alumnoCiclos, documentoAcademico, usuario);
        htmlContent = this.remplazarTablas(htmlContent, alumno, variables);

        Document html = Jsoup.parse(htmlContent);
        if (!incrustacionDocumentos.isEmpty()) {
            int idx = 0;
            for (PlantillaIncrustacionDocumento incrustacionDocumento : incrustacionDocumentos) {

                if (html.getElementById(VARIABLE_INCRUSTACION) != null) {
                    String tableOrigin = html.getElementById(VARIABLE_INCRUSTACION).html();
                    String tableClone = html.getElementById(VARIABLE_INCRUSTACION).html();
                    List<Element> divs = html.select("div");
                    Element elementDiv = null;
                    for (Element div : divs) {
                        if (div.hasAttr("id")) {
                            elementDiv = div;
                        }
                    }
                    Document htmlInc = Jsoup.parse(incrustacionDocumento.getContenido());
                    tableOrigin = htmlInc.select("body").html();

                    Element element = new Element("div");
                    element.append(tableOrigin);
                    elementDiv.insertChildren(idx, element);
                    idx++;
                }
            }
        }
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        plantillaGene.setContenido(html.html());
        plantillaGene.setNombre(documentoAcademico.getTipoDocumentoAcademico().getNombre());
        return plantillaGene;
    }

    private PlantillaGenerica alianzaEstrategicaEspecial(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        Assert.isNotNull(plantilla, "No existe Plantilla para este documento");
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica alumnoRegular(TramiteDocumentoAcademico documentoAcademico) {
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());

        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {

                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    case FACULTAD:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                        break;
                    case CICLO_MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCicloActivo().getDescripcion2());
                        break;
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica alumno(TramiteDocumentoAcademico documentoAcademico) {
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());
        AlumnoCiclo alumnoCiclo = alumnoCicloDAO.findLastByAlumno(alumno);
        ControlOrdenMerito orden = controlOrdenMeritoDAO.findByFac(alumno.getCarrera().getFacultad(), alumnoCiclo.getCicloAcademico());

        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {

                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    case FACULTAD:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                        break;
                    case ESPECIALIDAD:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                        break;
                    case CICLO_ACADEMICO:
                        TramiteDocumentoParametro parametro = tramiteDocumentoParamtroDAO.findByTipoDocAndPlantilla(documentoAcademico, plantilla, CICLO_ACADEMICO);
                        html = html.replace(var.getVariableGenerica().getCodigo(), parametro != null ? parametro.getValor() : "Sin datos");
                        break;
                    case CICLO_MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCicloActivo().getDescripcion2());
                        break;
                    case ORDEN_MERITO_NUMERICO:
                        if (alumnoCiclo.getOrdenMeritoCiclo() != null) {
                            html = html.replace(var.getVariableGenerica().getCodigo(), alumnoCiclo.getOrdenMeritoCiclo().toString() + " de " + orden.getAlumnosComputados());
                        } else {
                            html = html.replace(var.getVariableGenerica().getCodigo(), "Sin datos");
                        }
                        break;
                    case NIVEL_ACADEMICO:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumnoCiclo.getNivel() != null ? alumnoCiclo.getNivel().toString() : "Sin datos");
                        break;
                    case FECHA_CONSTANCIA:
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        String fechaFin = df.format(new Date());
                        html = html.replace(var.getVariableGenerica().getCodigo(), fechaFin);
                        break;
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    } // ok

    private PlantillaGenerica alumnoEspecial(TramiteDocumentoAcademico documentoAcademico) {
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());

        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {

                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    case FACULTAD:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                        break;
                    case CICLO_MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCicloActivo().getDescripcion2());
                        break;
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica alumnoVisitante(TramiteDocumentoAcademico documentoAcademico) {
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());

        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {

                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    case FACULTAD:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                        break;
                    case CICLO_MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCicloActivo().getDescripcion2());
                        break;
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica bachillerConFechaEgreso(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica colegiatura(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica convinadoTercioQuinto(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica comparativo(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica cuadroHonor(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica escuelaNacionalAgriculturaEspecial(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica especialComparativoPorcentaje(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica ordenMeritoTercioQuinto(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica ordenMeritoEgresadoVarios(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica especialContinuarEstudiosExtranjero(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica especialConversionSistemaCalificacion(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica especialDuracionCiclo(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica especialPrimeraMatricula(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica especialPromedioAcumuladoCiclos(TramiteDocumentoAcademico documentoAcademico) {
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());

        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();

        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    case CICLO_PROMOCION:
                        Egresado egresado = egresadoDAO.findByAlumno(alumno);
                        html = html.replace(var.getVariableGenerica().getCodigo(), egresado.getCicloAcademico().getDescripcion());
                        break;

                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica especialPromedioVigecimal(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica estudiosIninterumpidosContinuos(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica nivelAcademico(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica nivelAcademicoExAlumno(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica noSeparado(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica ordenMeritoAlumno(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica ordenMeritoAlumnosVarios(TramiteDocumentoAcademico documentoAcademico) {
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());

        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        List<AlumnoCiclo> alumnoCiclo = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);

        String html = plantilla.getContenido();
        int posStartTr = html.indexOf("<tr>");
        int posEndTr = html.indexOf("</tr>");
        String subStr = html.substring(posStartTr, posEndTr + 5);
        String[] array = new String[alumnoCiclo.size()];

        for (int j = 0; j < alumnoCiclo.size(); j++) {
            array[j] = subStr;
        }
        int i = 0;
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCodigo());
                        break;
                    case FACULTAD:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCarrera().getFacultad().getNombre());
                        break;
                    case SITUACION_ALUMNO:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getSituacionAcademica().getNombre());
                        break;
                    case CICLO_MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), alumno.getCicloActivo().getDescripcion2());
                    case FECHA_CONSTANCIA:
                        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                        String fecha = df.format(new Date());
                        html = html.replace(var.getVariableGenerica().getCodigo(), fecha);
                        break;
                    case CICLO_ACADEMICO:
                        for (AlumnoCiclo alum : alumnoCiclo) {
                            array[i] = array[i].replace(var.getVariableGenerica().getCodigo(), alum.getCicloAcademico().getDescripcion());
                            i++;
                        }
                        break;
                    case ORDEN_MERITO:
                        i = 0;
                        for (AlumnoCiclo alum : alumnoCiclo) {
                            array[i] = array[i].replace(var.getVariableGenerica().getCodigo(), alum.getOrdenMeritoFacultad().toString());
                            i++;
                        }
                        break;
                    case NIVEL_ACADEMICO:
                        i = 0;
                        for (AlumnoCiclo alum : alumnoCiclo) {
                            array[i] = array[i].replace(var.getVariableGenerica().getCodigo(), alum.getNivel().toString());
                            i++;
                        }
                        break;
                }
            }
        }
        String table = "";
        for (String string : array) {
            table = table.concat(string);
        }
        html = html.replace(subStr, table);
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica ordenMeritoEgresado(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica quintoSuperiorAlumno(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica quintoSuperioVarios(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica sistemaCalificacion(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica teoriaPracticaCredito(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica tercioCiclos(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica tercioSuperior(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica tercioQuintoCombinados(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica titulo(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
    }

    private PlantillaGenerica cursosDelPrimeroCiclo(TramiteDocumentoAcademico documentoAcademico) {
        PlantillaGenerica plantillaGene = new PlantillaGenerica();
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(documentoAcademico.getTipoDocumentoAcademico(), documentoAcademico.getIdioma());
        List<VariablePlantilla> variable = variablePlantillaDAO.allByPlantilla(plantilla);
        String html = plantilla.getContenido();
        for (VariablePlantilla var : variable) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                switch (var.getVariableGenerica().getCodigoVaribleEnum()) {
                    case NOMBRE_PERSONA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getPersona().getApellidosNombres());
                        break;
                    case MATRICULA:
                        html = html.replace(var.getVariableGenerica().getCodigo(), documentoAcademico.getTramite().getAlumno().getCodigo());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }
        String nombreDoc = plantilla.getTipoDocumentoAcademico().getNombre().concat("-" + plantilla.getId());
        plantillaGene.setContenido(html);
        plantillaGene.setNombre(nombreDoc);
        return plantillaGene;
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
//        List<VariablePlantilla> variables = variablePlantillaDAO.allByPlantilla(plantillaGeneralBean.getPlantillaDocumentoAcademico());
//        String htmlContent = plantillaGeneralBean.getPlantillaDocumentoAcademico().getContenido();

//        Alumno alumno = alumnoDAO.findAllInfo(academico.getTramite().getAlumno().getId());
//        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
//
//        Egresado egresado = egresadoDAO.findByAlumno(alumno);
//
//        htmlContent = this.recorrerVariables(htmlContent, variables, alumno, egresado, alumnoCiclos);
//        htmlContent = this.remplazarTablas(htmlContent, alumno, variables);
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
        } else {
            return new ArrayList<VariablePlantilla>();
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

}
