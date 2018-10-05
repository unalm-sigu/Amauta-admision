package pe.edu.lamolina.pivot.controller.tramite.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.aws.S3Service;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import pe.edu.lamolina.model.enums.EstadoAcreenciaTramiteEnum;
import pe.edu.lamolina.model.enums.EstadoMatriculaEnum;
import pe.edu.lamolina.model.enums.OrigenDataSituacionAcademicaEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoSolicitanteEnum;
import pe.edu.lamolina.model.enums.TipoTramiteEnum;
import pe.edu.lamolina.model.enums.TramiteEstadoEnum;
import pe.edu.lamolina.model.finanzas.AcreenciaTramiteDocumento;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.EstadoTramite;
import pe.edu.lamolina.model.tramite.FlujoTramiteAcademico;
import pe.edu.lamolina.model.tramite.FlujoTramiteDocumento;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TipoTramite;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.controller.academico.promedio.PromedioService;
import pe.edu.lamolina.pivot.controller.academico.situacionacademica.SituacionAcademicaService;
import pe.edu.lamolina.pivot.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.pivot.controller.test.VisorCalculoNotas;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.CursoDAO;
import pe.edu.lamolina.pivot.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.pivot.dao.finanza.AcreenciaTramiteDocumentoDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.ContenidoCartaDAO;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.tramite.EstadoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.FlujoTramiteDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.PrecioDocumentoDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoConstanciaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.pivot.dao.tramite.TipoTramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.TramiteDAO;
import pe.edu.lamolina.pivot.dao.tramite.hibernate.TramiteDocumentoAcademicoDAOH;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.mail.MailerService;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

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
    TramiteDocumentoAcademicoDAOH tramiteDocumentoAcademicoDAO;

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

                alumnoCiclo.setEstado(EstadoMatriculaEnum.MAT);
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
            promedioService.calulcarSituacionAcademica(alumno, ds.getUsuario());

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

        AcreenciaTramiteDocumento acreencia = acreenciaTramiteDocumentoDAO.findByTramiteDocumentoAcademico(tramiteDocumentoAcademico);
        if (acreencia == null) {
            acreencia = new AcreenciaTramiteDocumento();
            acreencia.setEstado(EstadoAcreenciaTramiteEnum.ACT.name());
            acreencia.setTramiteDocumentoAcademico(tramiteDocumentoAcademico);
            acreencia.setUserRegistro(ds.getUsuario());
            acreencia.setFechaRegistro(new Date());
            LocalDate localDate = LocalDate.now();
            LocalDate fechaVencimiento = localDate.plusDays(3);
            acreencia.setFechaVencimiento(fechaVencimiento.toDate());
            TipoDocumentoAcademico tipo = tipoDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getTipoDocumentoAcademico());
            Idioma idioma = tramiteDocumentoAcademico.getIdioma();
            PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipo, idioma);
            if (precio != null) {
                if (precio.getPrecio() != null) {
                    acreencia.setPrecio(new BigDecimal(precio.getPrecio()));
                }
            }
            acreenciaTramiteDocumentoDAO.save(acreencia);
        } else {
            acreencia.setEstado(EstadoAcreenciaTramiteEnum.ACT.name());
            LocalDate localDate = LocalDate.now();
            LocalDate fechaVencimiento = localDate.plusDays(3);
            acreencia.setFechaVencimiento(fechaVencimiento.toDate());
            TipoDocumentoAcademico tipo = tipoDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getTipoDocumentoAcademico());
            Idioma idioma = tramiteDocumentoAcademico.getIdioma();
            PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipo, idioma);
            if (precio != null) {
                if (precio.getPrecio() != null) {
                    acreencia.setPrecio(new BigDecimal(precio.getPrecio()));
                }
            }
            acreenciaTramiteDocumentoDAO.update(acreencia);
        }
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
    public TramiteDocumentoAcademico findTramite(TramiteDocumentoAcademico tramiteDocumentoAcademicoForm) {
        return tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademicoForm);
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
    public void updateFotoTemporal(Persona imagenForm) {
        if (!Strings.isNullOrEmpty(imagenForm.getRutaFotoTemporal())) {
            Persona persona = personaDAO.find(imagenForm.getId());
            persona.setRutaFotoTemporal(imagenForm.getRutaFotoTemporal());
            personaDAO.update(persona);
            this.uploadS3(persona.getRutaFotoTemporal());
        }
    }

    @Override
    public void save(TramiteDocumentoAcademico tramiteDocumentoAcademico, DataSessionPivot ds) {

        Usuario usuario = ds.getUsuario();
        CicloAcademico cicloAcademico = ds.getCicloAcademico();
        Compania compania = ds.getCompania();
        DateTime today = new DateTime();

        TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(TipoDocumentoCompaniaEnum.TRAM);
        SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), usuario);
        TipoTramite tipoTramite = tipoTramiteDAO.findByCodigo(TipoTramiteEnum.CONS.name());

        Tramite tramite = tramiteDocumentoAcademico.getTramite();
        Alumno alumno = alumnoDAO.find(tramite.getAlumno());
        Persona persona = alumno.getPersona();


        tramite.setAlumno(alumno);
        tramite.setTipoSolicitante(TipoSolicitanteEnum.ALU.name());
        tramite.setCicloAcademico(cicloAcademico);
        tramite.setCompania(compania);
        tramite.setEstadoEnum(TramiteEstadoEnum.CRE);
        tramite.setFechaRegistro(today.toDate());
        tramite.setNumero(Long.valueOf(serieDocumento.getNumeroDocumento()));
        tramite.setSerie(Long.valueOf(serieDocumento.getNumeroSerie()));
        tramite.setTipoTramite(tipoTramite);
        tramite.setUserRegistro(usuario);
        tramite.setPersona(persona);
        tramiteDAO.save(tramite);

        String rutaFotoTemporal = (String) ObjectUtil.getParentTree(tramite, "persona.rutaFotoTemporal");
        if (!Strings.isNullOrEmpty(rutaFotoTemporal)) {
            persona.setRutaFotoTemporal(rutaFotoTemporal);
            personaDAO.update(persona);
            this.uploadS3(persona.getRutaFotoTemporal());
        }
        tramiteDocumentoAcademico.setTramite(tramite);
//        tramiteDocumentoAcademico.setEstadoEnum(TramiteEstadoEnum.CRE);
        tramiteDocumentoAcademico.setCantidadCiclos(1);
        tramiteDocumentoAcademicoDAO.save(tramiteDocumentoAcademico);

        TipoDocumentoAcademico tipo = tipoDocumentoAcademicoDAO.find(tramiteDocumentoAcademico.getTipoDocumentoAcademico());
        Idioma idioma = tramiteDocumentoAcademico.getIdioma();
        PrecioDocumento precio = precioDocumentoDAO.findByTipoIdioma(tipo, idioma);

        AcreenciaTramiteDocumento acreencia = new AcreenciaTramiteDocumento();
        acreencia.setEstado(EstadoAcreenciaTramiteEnum.ACT.name());
        acreencia.setTramiteDocumentoAcademico(tramiteDocumentoAcademico);
        acreencia.setUserRegistro(usuario);
        acreencia.setFechaRegistro(new Date());
        LocalDate localDate = LocalDate.now();
        LocalDate fechaVencimiento = localDate.plusDays(3);
        acreencia.setFechaVencimiento(fechaVencimiento.toDate());

        acreencia.setPrecio(BigDecimal.ZERO);
        if (precio != null) {
            if (precio.getPrecio() != null) {
                acreencia.setPrecio(new BigDecimal(precio.getPrecio()));
            }
        }

        acreenciaTramiteDocumentoDAO.save(acreencia);
//        AccionTramiteDocumento estadoTramite = estadoTramiteDAO.find(15l);
        
        FlujoTramiteDocumento flujo = new FlujoTramiteDocumento();
//        flujo.setEstadoTramite(estadoTramite);
        flujo.setOficinaOrigen(ds.getOficinaMain());
        flujo.setOficinaDestino(ds.getOficinaMain());
        flujo.setUserRegistro(ds.getUsuario());
        flujo.setTramiteDocumentoAcademico(tramiteDocumentoAcademico);
        flujo.setFechaRegistro(new Date());
        flujoTramiteDocumentoDAO.save(flujo);

        this.enviarNotificacionSolicitudConstanciaCreacion(tramiteDocumentoAcademico);
    }

    @Override
    public List<TramiteDocumentoAcademico> allTramiteDocumentoAcademico(DynatableFilter filter) {
        return tramiteDocumentoAcademicoDAO.allTramiteDocumentoAcademico(filter);
    }

    @Override
    public List<PrecioDocumento> allPrecioDocumento() {
        return precioDocumentoDAO.allPrecioDocumento();
    }

    @Override
    public List<TipoDocumentoAcademico> allTipoDocumentoAcademico() {
        return tipoDocumentoAcademicoDAO.all();
    }
}
