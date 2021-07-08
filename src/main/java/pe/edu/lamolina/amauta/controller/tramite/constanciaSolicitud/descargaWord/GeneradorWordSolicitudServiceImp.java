package pe.edu.lamolina.amauta.controller.tramite.constanciaSolicitud.descargaWord;

import com.google.common.base.Objects;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBody;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColumns;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocument1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STSectionMark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.config.DespliegueConfig;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.controller.tramite.plantillaConstancia.IdiomaEnum;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreCarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreFacultadDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreGradoDAO;
import pe.edu.lamolina.amauta.dao.academico.NombreTituloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.general.ArchivoDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.tramite.ObtencionGradoDAO;
import pe.edu.lamolina.amauta.dao.tramite.PlantillaDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.PlantillaIncrustacionDAO;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoCompaniaDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteBachillerDAO;
import pe.edu.lamolina.amauta.dao.tramite.TramiteDocumentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.tramite.VariablePlantillaDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.EventoCicloAcademico;
import pe.edu.lamolina.model.academico.MatriculaResumen;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import static pe.edu.lamolina.model.constantines.AcademicoConstantine.CODIGO_ALIANZA_ESTRATEGICA;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import static pe.edu.lamolina.model.enums.EventoAcademicoEnum.FECHAS_BACH;
import static pe.edu.lamolina.model.enums.InstanciaEnum.TRAM_PLANTILLA_DOCUMENTO_ACADEMICO;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.TipoConstanciaEnum;
import pe.edu.lamolina.model.enums.TipoDocumentoCompaniaEnum;
import pe.edu.lamolina.model.enums.TipoGradoAcademicoEnum;
import pe.edu.lamolina.model.enums.VariableGenericaEnum;
import static pe.edu.lamolina.model.enums.VariableGenericaEnum.INCRUSTACION;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.SerieDocumento;
import pe.edu.lamolina.model.general.TipoDocumentoCompania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.PlantillaIncrustacionDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.amauta.dao.tramite.TipoDocumentoAcademicoDAO;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.NombreCarrera;
import pe.edu.lamolina.model.academico.NombreCiclo;
import pe.edu.lamolina.model.academico.NombreCurso;
import pe.edu.lamolina.model.academico.NombreFacultad;
import pe.edu.lamolina.model.academico.NombreGrado;
import pe.edu.lamolina.model.academico.NombreTituloAcademico;
import static pe.edu.lamolina.model.enums.EstadoMatriculaEnum.MAT;
import pe.edu.lamolina.model.enums.TipoCarreraEnum;
import static pe.edu.lamolina.model.enums.TipoConstanciaEnum.CERT;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;

@Service
@Transactional(readOnly = true)
public class GeneradorWordSolicitudServiceImp implements GeneradorWordSolicitudService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    DespliegueConfig despliegueConfig;
    
    @Autowired
    EgresadoDAO egresadoDAO;
    
    @Autowired
    TramiteDocumentoAcademicoDAO tramiteDocumentoAcademicoDAO;
    
    @Autowired
    PlantillaDocumentoAcademicoDAO plantillaDocumentoAcademicoDAO;
    
    @Autowired
    PlantillaIncrustacionDAO plantillaIncrustacionDAO;
    
    @Autowired
    VariablePlantillaDAO variablePlantillaDAO;
    
    @Autowired
    AlumnoCicloDAO alumnoCicloDAO;
    
    @Autowired
    AlumnoDAO alumnoDAO;
    
    @Autowired
    TramiteBachillerDAO tramiteBachillerDAO;
    
    @Autowired
    OficinaDAO oficinaDAO;
    
    @Autowired
    AlumnoCicloCursoDAO alumnoCicloCursoDAO;
    
    @Autowired
    ObtencionGradoDAO obtencionGradoDAO;
    
    @Autowired
    MatriculaResumenDAO matriculaResumenDAO;
    
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    
    @Autowired
    EventoCicloAcademicoDAO eventoCicloAcademicoDAO;
    
    @Autowired
    TipoDocumentoCompaniaDAO tipoDocumentoCompaniaDAO;
    
    @Autowired
    TipoDocumentoAcademicoDAO documentoAcademicoDAO;
    
    @Autowired
    NombreFacultadDAO nombreFacultadDAO;
    
    @Autowired
    NombreCarreraDAO nombreCarreraDAO;
    
    @Autowired
    NombreCicloDAO nombreCicloDAO;
    
    @Autowired
    NombreGradoDAO nombreGradoDAO;
    
    @Autowired
    NombreCursoDAO nombreCursoDAO;
    
    @Autowired
    NombreTituloAcademicoDAO nombreTituloAcademicoDAO;
    
    @Autowired
    SerieDocumentoService serieDocumentoService;
    
    @Autowired
    UploadFileS3 uploadFileS3;
    
    @Autowired
    ArchivoDAO archivoDAO;
    
    @Override
    @Transactional
    public void saveWordTramiteDocumento(Archivo archivo, DataSessionPivot ds) {
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.find(archivo.getIdInstancia());
        
        Archivo archivoDB = archivoDAO.findFirstByInstanciasTipoInstancia(plantilla.getId(), TRAM_PLANTILLA_DOCUMENTO_ACADEMICO);
        if (!Objects.equal(archivoDB, null)) {
            archivoDAO.delete(archivoDB);
        }
        
        uploadFileS3.uploadSync(AcademicoConstantine.S3_PLANTILLA_WORD, GlobalConstantine.TMP_DIR, archivo.getNombre(), true);
        String path = uploadFileS3.getPathFile(AcademicoConstantine.S3_PLANTILLA_WORD, archivo.getNombre());
        
        Archivo newarchivo = new Archivo();
        newarchivo.setFechaRegistro(new Date());
        newarchivo.setInstancia(TRAM_PLANTILLA_DOCUMENTO_ACADEMICO.name());
        newarchivo.setIdInstancia(plantilla.getId());
        newarchivo.setTipo(archivo.getTipo());
        newarchivo.setUsuarioRegistro(ds.getUsuario());
        newarchivo.setNombre(archivo.getNombre());
        newarchivo.setRuta(path);
        archivoDAO.save(newarchivo);
        
        plantilla.setArchivo(newarchivo);
        plantillaDocumentoAcademicoDAO.update(plantilla);
        
        TipoDocumentoAcademico documentoAcademico = plantilla.getTipoDocumentoAcademico();
        documentoAcademico.setConfigurado(1l);
        documentoAcademicoDAO.update(documentoAcademico);
        
    }
    
    @Override
    public void downloadWord(TramiteDocumentoAcademico tramiteDocumentoAcademico, HttpServletResponse response) throws PhobosException {
        
        tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademico);
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(tramiteDocumentoAcademico.getTipoDocumentoAcademico(), tramiteDocumentoAcademico.getIdioma());
        
        try {
            
            XWPFDocument doc = new XWPFDocument(new URL(plantilla.getArchivo().getRuta()).openStream());
            this.generateWord(doc, tramiteDocumentoAcademico, plantilla, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            
            out.close();
            doc.close();
            
            String codigoAlumno = tramiteDocumentoAcademico.getTramite().getAlumno().getCodigo();
            String nombreTramite = tramiteDocumentoAcademico.getTipoDocumentoAcademico().getNombre();
            response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "inline; filename=" + codigoAlumno + " - " + nombreTramite + ".docx");
            
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(out.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("(downloadTemporal)Error Descarga de Archivo: {}, fileName: {}", ex.getLocalizedMessage(), "prueba");
        } catch (XmlException ex) {
            java.util.logging.Logger.getLogger(GeneradorWordSolicitudServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void generateWord(XWPFDocument doc, TramiteDocumentoAcademico documentoAcademico, PlantillaDocumentoAcademico plantilla, Usuario usuario) throws IOException, XmlException {
        
        Alumno alumno = alumnoDAO.findAllInfo(documentoAcademico.getTramite().getAlumno().getId());
        List<AlumnoCiclo> alumnoCiclos = alumnoCicloDAO.allActivesByAlumnoAsc(alumno);
        
        Egresado egresado = egresadoDAO.findByAlumno(alumno);
        CicloAcademico cicloAcademicoAct = cicloAcademicoDAO.findActivo(ModalidadEstudioEnum.PRE);
        
        List<PlantillaIncrustacionDocumento> incrustacionDocumentos = plantillaIncrustacionDAO.allIncrustacionesByTramite(documentoAcademico);
        List<PlantillaDocumentoAcademico> plantillaDocumentoIncrustacion = incrustacionDocumentos.stream().map(x -> x.getPlatillaIncrustacion()).collect(Collectors.toList());
        List<VariablePlantilla> variablePlantillasIncrustacion = variablePlantillaDAO.allByPlantillas(plantillaDocumentoIncrustacion);
        List<VariablePlantilla> variables = variablePlantillaDAO.allByPlantilla(plantilla);
        Facultad facultadAlumno = alumno.getCarrera().getFacultad();
        Boolean isEspanol = plantilla.getIdioma().getCodigo().equals(IdiomaEnum.ES.getAcronimo());
        NombreFacultad nombreFacultads = null;
        List<NombreTituloAcademico> nombresTituloAcademico = null;
        List<NombreGrado> nombreGrados = null;
        List<NombreCurso> nombreCurso = null;
        List<NombreCiclo> nombresCiclos = null;
        Map<String, NombreCiclo> mapNombreCiclo = null;
        Map<Long, NombreTituloAcademico> mapNombreTitulo = null;
        Map<Long, NombreCurso> mapNombreCurso = null;
        Map<Long, NombreGrado> mapNombreGrados = null;
        NombreCarrera nombresCarrera = null;
        
        if (!isEspanol) {
            nombreCurso = nombreCursoDAO.allByIdioma(plantilla.getIdioma());
            nombreFacultads = nombreFacultadDAO.findByIdioma(facultadAlumno, plantilla.getIdioma());
            nombresCarrera = nombreCarreraDAO.findByIdioma(alumno.getCarrera(), plantilla.getIdioma());
            nombresCiclos = nombreCicloDAO.allByIdioma(plantilla.getIdioma());
            nombresTituloAcademico = nombreTituloAcademicoDAO.allByIdioma(plantilla.getIdioma());
            mapNombreTitulo = TypesUtil.convertListToMap("tituloAcademico.id", nombresTituloAcademico);
            nombreGrados = nombreGradoDAO.allByIdioma(plantilla.getIdioma());
            mapNombreGrados = TypesUtil.convertListToMap("gradoAcademico.id", nombreGrados);
            mapNombreCiclo = TypesUtil.convertListToMap("codigoCiclo", nombresCiclos);
            mapNombreCurso = TypesUtil.convertListToMap("curso.id", nombreCurso);
        }
        
        variables.addAll(variablePlantillasIncrustacion);
        
        List<XWPFParagraph> paragraphList = doc.getParagraphs();
        this.addIncrustaciones(paragraphList, incrustacionDocumentos);
        this.recorrerVariableWord(alumno, alumnoCiclos, documentoAcademico, usuario, cicloAcademicoAct, egresado, paragraphList, variables,
                nombreFacultads, nombresCarrera, mapNombreTitulo, mapNombreGrados, mapNombreCiclo, isEspanol, facultadAlumno
        );
        if (documentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == CERT) {
            this.remplazarTablasCertificados(doc, alumno, variables, alumnoCiclos,
                    nombreFacultads, nombresCarrera, mapNombreTitulo, mapNombreGrados, mapNombreCiclo, mapNombreCurso, isEspanol, facultadAlumno);
        } else {
            this.remplazarTablas(doc, alumno, variables, alumnoCiclos,
                    nombreFacultads, nombresCarrera, mapNombreTitulo, mapNombreGrados, mapNombreCiclo, mapNombreCurso, isEspanol, facultadAlumno);
        }
    }
    
    private void addIncrustaciones(List<XWPFParagraph> paragraphList, List<PlantillaIncrustacionDocumento> incrustacionDocumentos) {
        String htmlIncrustacion = "";
        for (PlantillaIncrustacionDocumento incrustacionDocumento : incrustacionDocumentos) {
            htmlIncrustacion = htmlIncrustacion + incrustacionDocumento.getContenido() + "\n";
        }
        for (XWPFParagraph para : paragraphList) {
            for (XWPFRun run : para.getRuns()) {
                String text = run.text();
                if (text.isEmpty()) {
                    continue;
                }
                text = text.replace(INCRUSTACION.getValue(), htmlIncrustacion);
                run.setText(text, 0);
            }
        }
    }
    
    private void recorrerVariableWord(Alumno alumno, List<AlumnoCiclo> alumnoCiclos,
            TramiteDocumentoAcademico documentoAcademico, Usuario usuario,
            CicloAcademico cicloAcademicoAct, Egresado egresado, List<XWPFParagraph> paragraphList, List<VariablePlantilla> variables,
            NombreFacultad nombreFacultads,
            NombreCarrera nombresCarrera,
            Map<Long, NombreTituloAcademico> mapNombreTitulo,
            Map<Long, NombreGrado> mapNombreGrados,
            Map<String, NombreCiclo> mapNombreCiclo,
            Boolean isEspanol,
            Facultad facultadAlumno) {
        
        int idx = alumnoCiclos.size() - 1;
        
        Oficina oficinaEPG = oficinaDAO.findByCode(OficinaEnum.EPG.name());
        Oficina oficinaUR = oficinaDAO.findByCode(OficinaEnum.UR.name());
        Oficina oficinaOREA = oficinaDAO.findByCode(OficinaEnum.OERA.name());
        Oficina oficinaFacultad = oficinaDAO.findByTipoAndFacultad(TipoOficinaEnum.FAC, alumno.getCarrera().getFacultad());
        ObtencionGrado obtencionGradoBachi = obtencionGradoDAO.findByAlumnoAndTipo(alumno, TipoGradoAcademicoEnum.BACH);
        ObtencionGrado obtencionGradoTitulo = obtencionGradoDAO.findByAlumnoAndTipo(alumno, TipoGradoAcademicoEnum.TIT);
        
        EventoCicloAcademico eventoAcademico = null;
        EventoCicloAcademico eventoFinAcademico = null;
        
        if (!alumnoCiclos.isEmpty()) {
            eventoAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(alumnoCiclos.get(0).getCicloAcademico(), FECHAS_BACH);
            eventoFinAcademico = eventoCicloAcademicoDAO.findActivoByCicloTipoEvento(alumnoCiclos.get(idx).getCicloAcademico(), FECHAS_BACH);
        }
        
        for (XWPFParagraph para : paragraphList) {
            
            for (XWPFRun run : para.getRuns()) {
                String text = run.text();
                
                if (null == text) {
                    continue;
                }
                if (text.isEmpty()) {
                    continue;
                }
                
                logger.debug("***** {} ", text);
                
                for (VariablePlantilla variablePlantilla : variables) {
                    
                    VariableGenericaEnum enums = null;
                    
                    if (!text.contains(variablePlantilla.getVariableGenerica().getCodigo())) {
                        continue;
                    }
                    
                    enums = VariableGenericaEnum.valueOf(variablePlantilla.getVariableGenerica().getCodigoEnum());
                    
                    logger.debug("----- {} ", enums);
                    
                    switch (enums) {
                        case FIRMA_JEFE_FACULTAD:
                            text = text.replace(enums.getValue(), oficinaFacultad.getJefeEncargado() == null ? oficinaFacultad.getPersonaJefe().getNombreConTitulo() : oficinaFacultad.getJefeEncargado().getNombreConTitulo());
                            break;
                        case JEFE_OFICINA_OERA:
                            text = text.replace(enums.getValue(), oficinaOREA.getJefeEncargado() == null ? oficinaOREA.getPersonaJefe().getNombreConTitulo() : oficinaOREA.getJefeEncargado().getNombreConTitulo());
                        case JEFE_URA:
                            text = text.replace(enums.getValue(), oficinaUR.getJefeEncargado() == null ? oficinaUR.getPersonaJefe().getNombreConTitulo() : oficinaUR.getJefeEncargado().getNombreConTitulo());
                            break;
                        case JEFE_POS_GRADO:
                            text = text.replace(enums.getValue(), oficinaEPG.getJefeEncargado() == null ? oficinaEPG.getPersonaJefe().getNombreConTitulo() : oficinaEPG.getJefeEncargado().getNombreConTitulo());
                            break;
                        case CORRELATIVO_DOC:
                            if (documentoAcademico.getCorrelativoDocumento() == null) {
                                DateTime today = new DateTime();
                                
                                TipoDocumentoCompaniaEnum tipoConEnum = documentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == TipoConstanciaEnum.CONS ? TipoDocumentoCompaniaEnum.DOC_CONS : TipoDocumentoCompaniaEnum.DOC_CERT;
                                TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(tipoConEnum);
                                SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), usuario);
                                
                                documentoAcademico.setCorrelativoDocumento(serieDocumento.getNumeroDocumento() + "-" + oficinaUR.getCodigoDocumento() + "/" + serieDocumento.getNumeroSerie());
                                tramiteDocumentoAcademicoDAO.updateColumns(documentoAcademico, "correlativoDocumento");
                            }
                            text = text.replace(enums.getValue(), documentoAcademico.getCorrelativoDocumento());
                            break;
                        case SEX_IDENT:
                            text = text.replace(enums.getValue(), alumno.getPersona().getIdentificacion());
                            break;
                        case SEX_ALUM:
                            text = text.replace(enums.getValue(), alumno.getPersona().getSexoEnum() == SexoEnum.F ? "a" : "o");
                            break;
                        case TIPO_DOCUMENTO:
                            text = text.replace(enums.getValue(), alumno.getPersona().getTipoDocumento().getNombre());
                            break;
                        case NUMERO_DOCUMENTO:
                            text = text.replace(enums.getValue(), alumno.getPersona().getNumeroDocIdentidad());
                            break;
                        case MATRICULA:
                            text = text.replace(enums.getValue(), alumno.getCodigo());
                            break;
                        case FACULTAD:
                            if (isEspanol) {
                                text = text.replace(enums.getValue(), alumno.getCarrera().getFacultad().getNombre().toUpperCase());
                            } else {
                                text = text.replace(enums.getValue(), nombreFacultads.getNombre());
                            }
                            break;
                        
                        case ESPECIALIDAD:
                            if (isEspanol) {
                                String tipo = TipoCarreraEnum.valueOf(alumno.getCarrera().getTipo()).getValue();
                                text = text.replace(enums.getValue(), tipo + " en " + alumno.getCarrera().getNombre().toUpperCase());
                            } else {
                                text = text.replace(enums.getValue(), nombresCarrera.getNombre().toUpperCase());
                            }
                            break;
                        case CARRERA:
                            if (!facultadAlumno.getCodigo().equals(alumno.getCarrera().getCodigo()) && isEspanol) {
                                text = text.replace(enums.getValue(), " - Carrera de " + alumno.getCarrera().getNombre().toUpperCase());
                            } else if (!facultadAlumno.getCodigo().equals(alumno.getCarrera().getCodigo()) && !isEspanol) {
                                text = text.replace(enums.getValue(), " - Career of " + nombresCarrera.getNombre().toUpperCase());
                            } else {
                                text = text.replace(enums.getValue(), "");
                            }
                            break;
                        case APELLIDO_PERSONA:
                            text = text.replace(enums.getValue(), alumno.getPersona().getApellidosNombres().toUpperCase());
                            break;
                        case NOMBRE_PERSONA:
                            text = text.replace(enums.getValue(), alumno.getPersona().getNombreCompleto());
                            break;
                        
                        case FECHA_CONSTANCIA:
                            text = text.replace(enums.getValue(), TypesUtil.getStringDate(new Date(), "dd 'de' MMMM 'del' yyyy", "es"));
                            break;
                        case APELLIDOS:
                            text = text.replace(enums.getValue(), alumno.getPersona().getApellidos());
                            break;
                        case SENOR_A:
                            text = text.replace(enums.getValue(), alumno.getPersona().getSenior());
                            break;
                        case ALUMNO_REGULAR:
                            MatriculaResumen matriculaResumen = matriculaResumenDAO.findByAlumnoCiclo(alumno, cicloAcademicoAct);
                            if (matriculaResumen != null && matriculaResumen.getCreditosMatriculados() >= 12) {
                                text = text.replace(enums.getValue(), "regular");
                            } else {
                                text = text.replace(enums.getValue(), "");
                            }
                            
                            break;
                        case PRIMER_CICLO_MATRICULADO:
                            if (isEspanol) {
                                text = text.replace(enums.getValue(), alumnoCiclos.get(0).getCicloAcademico().getDescripcion());
                            } else {
                                NombreCiclo nombreCiclo = mapNombreCiclo.get(alumnoCiclos.get(0).getCicloAcademico().getCodigo());
                                text = text.replace(enums.getValue(), nombreCiclo.getNombreCorto());
                            }
                            break;
                        case FECHA_PRIMERA_MATRICULA:
                            text = text.replace(enums.getValue(), TypesUtil.getStringDate(eventoAcademico.getFechaInicio(), "dd/MM/yyyy"));
                            break;
                        case ULTIMO_CICLO_MATRICULADO:
                            if (isEspanol) {
                                text = text.replace(enums.getValue(), alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                            } else {
                                NombreCiclo nombreCiclo = mapNombreCiclo.get(alumnoCiclos.get(idx).getCicloAcademico().getCodigo());
                                text = text.replace(enums.getValue(), nombreCiclo.getNombreCorto());
                            }
                            break;
                        case NIVEL_ACADEMICO:
                            
                            text = text.replace(enums.getValue(), alumnoCiclos.get(idx).getNivel() + "");
                            
                            break;
                        case CICLO_MATRICULA:
                            if (alumnoCiclos.get(idx).getCicloAcademico().getCodigo().equals(cicloAcademicoAct.getCodigo())) {
                                if (isEspanol) {
                                    text = text.replace(enums.getValue(), "Se encuentra matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                                } else {
                                    text = text.replace(enums.getValue(), "Se encuentra matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                                }
                            } else {
                                if (isEspanol) {
                                    text = text.replace(enums.getValue(), "Estuvo matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                                } else {
                                    text = text.replace(enums.getValue(), "Estuvo matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                                }
                            }
                            break;
                        case CANTIDAD_CREDITOS_APROBADOS:
                            text = text.replace(enums.getValue(), alumno.getCreditosAprobados().toString());
                            break;
                        case CANTIDAD_CURSOS_APROBADOS:
                            text = text.replace(enums.getValue(), alumno.getCursosAprobados().toString());
                            break;
                        case CANTIDAD_CREDITOS_CURSADOS:
                            text = text.replace(enums.getValue(), alumno.getCreditosCursados().toString());
                            break;
                        case CANTIDAD_CREDITOS_CONVALIDADOS:
                            text = text.replace(enums.getValue(), alumno.getCreditosConvalidados().toString());
                            break;
                        case FECHA_ULTIMA_MATRICULA:
                            text = text.replace(enums.getValue(), TypesUtil.getStringDate(eventoFinAcademico.getFechaFin(), "dd/MM/yyyy"));
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
                            text = text.replace(enums.getValue(), ciclos);
                            break;
                        case TITULO_PROFESIONAL:
                            if (obtencionGradoTitulo != null && obtencionGradoTitulo.getGradoAcademico() != null) {
                                if (isEspanol) {
                                    text = text.replace(enums.getValue(), obtencionGradoTitulo.getGradoAcademico().getNombre());
                                } else {
                                    NombreGrado nombreGrado = mapNombreGrados.get(obtencionGradoTitulo.getGradoAcademico().getId());
                                    text = text.replace(enums.getValue(), nombreGrado.getNombre());
                                }
                            } else if (obtencionGradoBachi != null && obtencionGradoBachi.getGradoAcademico() != null) {
                                if (isEspanol) {
                                    
                                    text = text.replace(enums.getValue(), obtencionGradoBachi.getGradoAcademico().getNombre());
                                } else {
                                    NombreGrado nombreGrado = mapNombreGrados.get(obtencionGradoBachi.getGradoAcademico().getId());
                                    text = text.replace(enums.getValue(), nombreGrado.getNombre());
                                }
                                
                            } else if (egresado != null && egresado.getTitulo() != null) {
                                if (isEspanol) {
                                    text = text.replace(enums.getValue(), egresado.getTitulo().getNombre());
                                } else {
                                    NombreTituloAcademico tituloAcademico = mapNombreTitulo.get(egresado.getTitulo().getId());
                                    text = text.replace(enums.getValue(), tituloAcademico.getNombre());
                                }
                            }
                            
                            break;
                        case CICLO_PROMOCION:
                            if (isEspanol) {
                                text = text.replace(enums.getValue(), egresado.getCicloAcademico().getCodigo());
                            } else {
                                NombreCiclo nombreCiclo = mapNombreCiclo.get(egresado.getCicloAcademico().getCodigo());
                                text = text.replace(enums.getValue(), nombreCiclo.getNombre());
                            }
                            break;
                        case CICLO_EGRESO:
                            if (isEspanol) {
                                text = text.replace(enums.getValue(), egresado.getCicloAcademico().getDescripcion());
                            } else {
                                NombreCiclo nombreCiclo = mapNombreCiclo.get(egresado.getCicloAcademico().getCodigo());
                                text = text.replace(enums.getValue(), nombreCiclo.getNombre());
                            }
                            break;
                        
                        case PROGRAMA:
                            String programa = "";
                            if (alumno.getCarrera().getCodigo().equals(CODIGO_ALIANZA_ESTRATEGICA)) {
                                programa = programa.concat("por el Convenio de la " + alumno.getCarrera().getNombre().toUpperCase());
                            } else {
                                
                                programa = programa.concat("como " + alumno.getPersona().getGeneroAlumno("alter") + " " + alumno.getCarrera().getNombre().toUpperCase());
                            }
                            
                            text = text.replace(enums.getValue(), programa);
                            break;
                        case FECHA_EGRESO:
                            
                            text = text.replace(enums.getValue(), TypesUtil.getStringDate(eventoFinAcademico.getFechaFin(), "dd/MM/yyyy"));
                            
                            break;
                        case RESOL_EGRESO:
                            
                            text = text.replace(enums.getValue(), obtencionGradoBachi.getResolucion().getDescripcion());
                            
                            break;
                        case RESOL_FECHA:
                            
                            text = text.replace(enums.getValue(), TypesUtil.getStringDate(obtencionGradoBachi.getResolucion().getFecha(), "dd/MM/yyyy"));
                            
                            break;
                        case RESOL_TITULO:
                            
                            text = text.replace(enums.getValue(), obtencionGradoTitulo.getResolucion().getDescripcion());
                            
                            break;
                        case RESOL_TITULO_FECHA:
                            
                            text = text.replace(enums.getValue(), TypesUtil.getStringDate(obtencionGradoTitulo.getResolucion().getFecha(), "dd/MM/yyyy"));
                            
                            break;
                        case ORDEN_MERITO_EGRESADO:
                            
                            text = text.replace(enums.getValue(), egresado.getOrdenMeritoFacultad() + "");
                            
                            break;
                        case CANTIDAD_ALUMNOS:
                            
                            text = text.replace(enums.getValue(), egresado.getControlMeritoFacultad().getTotalAlumnos() + "");
                            
                            break;
                        case PROMEDIO_PONDERADO_GRADUACION:
                            if (egresado != null && egresado.getPromedioGraduacion() == null) {
                                this.generarPromedioGraduacion(egresado, alumno);
                            }
                            text = text.replace(enums.getValue(), egresado.getPromedioGraduacion() != null ? egresado.getPromedioGraduacion().toString() : alumnoCiclos.size() + "No hay data");
                            
                            break;
                        case EPG_PROMEDIO_PONDERADO:
                            
                            text = text.replace(enums.getValue(), alumno.getPromedioAcumulado().setScale(2, RoundingMode.HALF_UP).toString());
                            
                            break;
                        case MEJOR_PROMEDIO_PONDERADO_GRADUACION:
                            
                            text = text.replace(enums.getValue(), egresado.getPromedioGraduacion().toString());
                            
                            break;
                        case PROMEDIO_PONDERADO_ACADEMICO:
                            
                            text = text.replace(enums.getValue(), alumno.getPromedioAcumulado().setScale(2, RoundingMode.HALF_UP).toString());
                            
                            break;
                        
                    }
                    run.setText(text, 0);
                    
                }
                
            }
        }
        
    }
    
    private void remplazarTablasCertificados(XWPFDocument doc, Alumno alumno, List<VariablePlantilla> variables, List<AlumnoCiclo> alumnoCiclos,
            NombreFacultad nombreFacultads,
            NombreCarrera nombresCarrera,
            Map<Long, NombreTituloAcademico> mapNombreTitulo,
            Map<Long, NombreGrado> mapNombreGrados,
            Map<String, NombreCiclo> mapNombreCiclo,
            Map<Long, NombreCurso> mapNombreCurso,
            Boolean isEspanol,
            Facultad facultadAlumno
    ) throws XmlException, IOException {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        Map<Long, List<AlumnoCicloCurso>> mapalumnoCicloCursos = TypesUtil.convertListToMapList("alumnoCiclo.cicloAcademico.id", alumnoCicloCursos);
        List<XWPFTable> tbl = doc.getTables();
        
        for (XWPFTable fTable : tbl) {
            
            int countRowsInicial = fTable.getRows().size();
            
            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                List<AlumnoCicloCurso> cicloCursos = TypesUtil.getListNotNull(mapalumnoCicloCursos.get(alumnoCiclo.getCicloAcademico().getId()));
                if (cicloCursos.stream().anyMatch(x -> x.getEstaAprobado() == 1)) {
                    
                    XWPFTableRow oldRowCiclo = fTable.getRow(0);
                    CTRow ctrowCiclo = CTRow.Factory.parse(oldRowCiclo.getCtRow().newInputStream());
                    XWPFTableRow newRowCiclo = new XWPFTableRow(ctrowCiclo, fTable);
                    this.switchValue(null, newRowCiclo, variables, alumnoCiclo, mapNombreCurso, mapNombreCiclo, isEspanol);
                    fTable.addRow(newRowCiclo);
                    for (AlumnoCicloCurso alumnoCicloCurso : cicloCursos) {
                        if (alumnoCicloCurso.getEstaAprobado() != 1) {
                            continue;
                        }
                        XWPFTableRow oldRow = fTable.getRow(1);
                        CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                        XWPFTableRow newRow = new XWPFTableRow(ctrow, fTable);
                        this.switchValue(alumnoCicloCurso, newRow, variables, alumnoCiclo, mapNombreCurso, mapNombreCiclo, isEspanol);
                        
                        fTable.addRow(newRow);
                    }
                }
                
            }
            for (int i = countRowsInicial - 1; i >= 0; i--) {
                fTable.removeRow(i);
            }
        }
        CTSectPr ctSectPr = doc.getDocument().getBody().addNewSectPr();
        CTColumns ctColumns = ctSectPr.addNewCols();
        CTDocument1 ctDocument = doc.getDocument();
        CTBody ctBody = ctDocument.getBody();
        ctSectPr = ctBody.addNewSectPr();
        ctSectPr.addNewType().setVal(STSectionMark.NEXT_PAGE);
        ctColumns = ctSectPr.addNewCols();
        ctColumns.setNum(BigInteger.valueOf(0));
        
    }
    
    private void remplazarTablas(XWPFDocument doc, Alumno alumno, List<VariablePlantilla> variables, List<AlumnoCiclo> alumnoCiclos,
            NombreFacultad nombreFacultads,
            NombreCarrera nombresCarrera,
            Map<Long, NombreTituloAcademico> mapNombreTitulo,
            Map<Long, NombreGrado> mapNombreGrados,
            Map<String, NombreCiclo> mapNombreCiclo,
            Map<Long, NombreCurso> mapNombreCurso,
            Boolean isEspanol,
            Facultad facultadAlumno) throws XmlException, IOException {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        Map<Long, List<AlumnoCicloCurso>> mapalumnoCicloCursos = TypesUtil.convertListToMapList("alumnoCiclo.cicloAcademico.id", alumnoCicloCursos);
        List<XWPFTable> tbl = doc.getTables();
        
        for (XWPFTable fTable : tbl) {
            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                List<AlumnoCicloCurso> cicloCursos = TypesUtil.getListNotNull(mapalumnoCicloCursos.get(alumnoCiclo.getCicloAcademico().getId()));
                
                XWPFTableRow oldRowCiclo = fTable.getRow(1);
                CTRow ctrowCiclo = CTRow.Factory.parse(oldRowCiclo.getCtRow().newInputStream());
                XWPFTableRow newRowCiclo = new XWPFTableRow(ctrowCiclo, fTable);
                
                this.switchValue(null, newRowCiclo, variables, alumnoCiclo, mapNombreCurso, mapNombreCiclo, isEspanol);
                
                for (AlumnoCicloCurso alumnoCicloCurso : cicloCursos) {
                    
                    this.switchValue(alumnoCicloCurso, newRowCiclo, variables, alumnoCiclo, mapNombreCurso, mapNombreCiclo, isEspanol);
                    
                }
            }
            fTable.removeRow(1);
        }
    }
    
    private void switchValue(AlumnoCicloCurso alumnoCicloCurso, XWPFTableRow pFTableRow, List<VariablePlantilla> variables, AlumnoCiclo alumnoCiclo,
            Map<Long, NombreCurso> mapNombreCurso,
            Map<String, NombreCiclo> mapNombreCiclo,
            Boolean isEspanol) {
        
        for (XWPFTableCell tableCell : pFTableRow.getTableCells()) {
            
            for (XWPFParagraph paragraph : tableCell.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    VariablePlantilla variablePlantilla = variables.stream().filter(x -> run.getText(0).contains(x.getVariableGenerica().getCodigo())).findAny().orElse(null);
                    if (variablePlantilla == null) {
                        continue;
                    }
                    VariableGenericaEnum enums = null;
                    enums = VariableGenericaEnum.valueOf(variablePlantilla.getVariableGenerica().getCodigoEnum());
                    
                    switch (enums) {
                        case TABLA_CODIGO_CURSO:
                            if (alumnoCicloCurso != null) {
                                
                                run.setText(alumnoCicloCurso.getCurso().getCodigo().toUpperCase(), 0);
                            }
                            
                            break;
                        case TABLA_CURSO:
                            if (alumnoCicloCurso != null) {
                                if (isEspanol) {
                                    text = text.replace(enums.getValue(), alumnoCicloCurso.getCurso().getNombre().toUpperCase());
                                    
                                } else {
                                    if (mapNombreCurso.get(alumnoCicloCurso.getCurso().getId()) != null) {
                                        text = text.replace(enums.getValue(), mapNombreCurso.get(alumnoCicloCurso.getCurso().getId()).getNombre().toUpperCase());
                                    } else {
                                        logger.debug("no existe nombre curso {} en ingles ", alumnoCicloCurso.getCurso().getId());
                                        text = text.replace(enums.getValue(), "no data");
                                    }
                                }
                                
                                run.setText(text, 0);
                            }
                            break;
                        case TABLA_CURSO_NOTA:
                            if (alumnoCicloCurso != null) {
                                text = text.replace(enums.getValue(), alumnoCicloCurso.getNota());
                                run.setText(text, 0);
                            }
                            break;
                        case TABLA_CURSO_CREDITO:
                            if (alumnoCicloCurso != null) {
                                text = text.replace(enums.getValue(), alumnoCicloCurso.getCreditos().toString());
                                run.setText(text, 0);
                            }
                            break;
                        case TABLA_CICLO_CURSADO:
                            if (isEspanol) {
                                
                                text = text.replace(enums.getValue(), alumnoCiclo.getCicloAcademico().getDescripcion2().toUpperCase());
                            } else {
                                logger.debug(" **** {}", alumnoCiclo.getCicloAcademico().getCodigo());
                                text = text.replace(enums.getValue(), mapNombreCiclo.get(alumnoCiclo.getCicloAcademico().getCodigo()).getNombre().toUpperCase());
                            }
                            run.setText(text, 0);
                            break;
                        case TABLA_CICLO_ROM_CURSADO:
                            text = text.replace(enums.getValue(), alumnoCiclo.getCicloAcademico().getDescripcion());
                            run.setText(text, 0);
                            break;
                        case NIVEL_ACADEMICO:
                            text = text.replace(enums.getValue(), alumnoCiclo.getNivel().toString());
                            run.setText(text, 0);
                            break;
                    }
                }
            }
            
        }
        
    }
    
    private void generarPromedioGraduacion(Egresado egresado, Alumno alumno) {
        
        BigDecimal sumNotasCreditos = BigDecimal.ZERO;
        BigDecimal sumCreditos = BigDecimal.ZERO;
        
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allActivosByAlumno(alumno);
        for (AlumnoCicloCurso cursoAluCicloEach : alumnoCicloCursos) {
            if (cursoAluCicloEach.getCreditos() > 0
                    && cursoAluCicloEach.isAprobado()
                    && cursoAluCicloEach.isBooleanRegistroActivo()
                    && cursoAluCicloEach.getEstadoEnum() == MAT
                    && !Arrays.asList("AP", "TE").contains(cursoAluCicloEach.getNota())) {
                
                BigDecimal notaBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getNota());
                BigDecimal creditosBig = TypesUtil.getBigDecimal(cursoAluCicloEach.getCreditos());
                
                sumNotasCreditos = sumNotasCreditos.add(notaBig.multiply(creditosBig));
                sumCreditos = sumCreditos.add(creditosBig);
                
            }
        }
        BigDecimal ppg = sumNotasCreditos.divide(sumCreditos);
        egresado.setPromedioGraduacion(ppg);
        egresadoDAO.update(egresado);
    }
    
}
