package pe.edu.lamolina.amauta.controller.tramite.constanciaSolicitud.descargaWord;

import com.google.common.base.Objects;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import static org.openxmlformats.schemas.spreadsheetml.x2006.main.STPhoneticType.Enum.table;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.controller.seriedocumento.SerieDocumentoService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloCursoDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoCicloDAO;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.EgresadoDAO;
import pe.edu.lamolina.amauta.dao.academico.EventoCicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.MatriculaResumenDAO;
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

@Service
@Transactional(readOnly = true)
public class GeneradorWordSolicitudServiceImp implements GeneradorWordSolicitudService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
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
        
        uploadFileS3.uploadSync(AcademicoConstantine.S3_BUCKET_ACADEMICO, GlobalConstantine.TMP_DIR, archivo.getNombre(), true);
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
        tramiteDocumentoAcademico = tramiteDocumentoAcademicoDAO.find(tramiteDocumentoAcademico);
        PlantillaDocumentoAcademico plantilla = plantillaDocumentoAcademicoDAO.findTipoDocumento(tramiteDocumentoAcademico.getTipoDocumentoAcademico(), tramiteDocumentoAcademico.getIdioma());
        
        try {

//            XWPFDocument doc = new XWPFDocument(new URL(plantilla.getArchivo().getRuta()).openStream());
            XWPFDocument doc = new XWPFDocument(OPCPackage.open(new FileInputStream("C:\\tmp\\Certificado.docx")));
            this.generateWord(doc, tramiteDocumentoAcademico, plantilla, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            
            out.close();
            doc.close();
            response.setBufferSize(GlobalConstantine.DEFAULT_BUFFER_SIZE_DOWNLOAD);
            response.setContentType("application/msword");
            response.setHeader("Content-Disposition", "inline; filename=" + "Prueba.docx");
            
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(out.toByteArray());
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            logger.error("(downloadTemporal)Error Descarga de Archivo: {}, fileName: {}", ex.getLocalizedMessage(), "prueba");
        } catch (InvalidFormatException ex) {
            java.util.logging.Logger.getLogger(GeneradorWordSolicitudServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
        variables.addAll(variablePlantillasIncrustacion);
        
        List<XWPFParagraph> paragraphList = doc.getParagraphs();
        this.addIncrustaciones(paragraphList, incrustacionDocumentos);
        this.recorrerVariableWord(alumno, alumnoCiclos, documentoAcademico, usuario, cicloAcademicoAct, egresado, paragraphList, variables);
        this.remplazarTablas(doc, alumno, variables, alumnoCiclos);
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
            CicloAcademico cicloAcademicoAct, Egresado egresado, List<XWPFParagraph> paragraphList, List<VariablePlantilla> variables) {
        
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
        
        for (XWPFParagraph para : paragraphList) {
            
            for (XWPFRun run : para.getRuns()) {
                String text = run.text();
                if (text.isEmpty()) {
                    continue;
                }
                
                String[] values = text.split(" ");
                
                for (String value : values) {
                    VariableGenericaEnum enums = null;
                    
                    VariablePlantilla variablePlantilla = variables.stream().filter(x -> value.contains(x.getVariableGenerica().getCodigo())).findAny().orElse(null);
                    if (variablePlantilla == null) {
                        continue;
                    }
                    enums = VariableGenericaEnum.valueOf(variablePlantilla.getVariableGenerica().getCodigoEnum());
                    switch (enums) {
                        case JEFE_OFICINA_OERA:
                        case JEFE_URA:
                            text = text.replace(enums.getValue(), oficina.getJefeEncargado() == null ? oficina.getPersonaJefe().getNombreCompleto() : oficina.getJefeEncargado().getNombreCompleto());
                            break;
                        case CORRELATIVO_DOC:
                            if (documentoAcademico.getCorrelativoDocumento() == null) {
                                DateTime today = new DateTime();
                                
                                TipoDocumentoCompaniaEnum tipoConEnum = documentoAcademico.getTipoDocumentoAcademico().getTipoConstanciaEnum() == TipoConstanciaEnum.CONS ? TipoDocumentoCompaniaEnum.DOC_CONS : TipoDocumentoCompaniaEnum.DOC_CERT;
                                TipoDocumentoCompania tipoDocumentoCompania = tipoDocumentoCompaniaDAO.findByCodigo(tipoConEnum);
                                SerieDocumento serieDocumento = serieDocumentoService.getCorrelativo(tipoDocumentoCompania, Long.valueOf(today.getYear()), usuario);
                                
                                documentoAcademico.setCorrelativoDocumento(serieDocumento.getNumeroDocumento() + "-" + oficina.getCodigoDocumento() + "/" + serieDocumento.getNumeroSerie());
                                tramiteDocumentoAcademicoDAO.updateColumns(documentoAcademico, "correlativoDocumento");
                            }
                            text = text.replace(enums.getValue(), documentoAcademico.getCorrelativoDocumento());
                            break;
                        case SEX_IDENT:
                            text = text.replace(enums.getValue(), alumno.getPersona().getEstimado());
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
                            text = text.replace(enums.getValue(), alumno.getCarrera().getFacultad().getNombre());
                            break;
                        
                        case ESPECIALIDAD:
                        case CARRERA:
                            if (!alumno.getCarrera().getFacultad().getCodigo().equals(alumno.getCarrera().getCodigo())) {
                                text = text.replace(enums.getValue(), " - Carrera de " + alumno.getCarrera().getNombre());
                            } else {
                                text = text.replace(enums.getValue(), "");
                            }
                            break;
                        case APELLIDO_PERSONA:
                            text = text.replace(enums.getValue(), alumno.getPersona().getApellidosNombres());
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
                            text = text.replace(enums.getValue(), alumnoCiclos.get(0).getCicloAcademico().getDescripcion());
                            break;
                        case FECHA_PRIMERA_MATRICULA:
                            text = text.replace(enums.getValue(), TypesUtil.getStringDate(eventoAcademico.getFechaInicio(), "dd/MM/yyyy"));
                            break;
                        case ULTIMO_CICLO_MATRICULADO:
                            
                            text = text.replace(enums.getValue(), alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                            break;
                        case NIVEL_ACADEMICO:
                            
                            text = text.replace(enums.getValue(), alumnoCiclos.get(idx).getNivel() + "");
                            
                            break;
                        case CICLO_MATRICULA:
                            if (alumnoCiclos.get(idx).getCicloAcademico().getCodigo().equals(cicloAcademicoAct.getCodigo())) {
                                
                                text = text.replace(enums.getValue(), "Se encuentra matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                            } else {
                                text = text.replace(enums.getValue(), "Estuvo matriculado en el Ciclo " + alumnoCiclos.get(idx).getCicloAcademico().getDescripcion());
                                
                            }
                            break;
                        case CANTIDAD_CREDITOS_APROBADOS:
                            text = text.replace(enums.getValue(), alumnoCiclos.get(idx).getCreditosAprobadosAcumulados().toString());
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
                                
                                text = text.replace(enums.getValue(), obtencionGradoTitulo.getGradoAcademico().getNombre());
                            } else if (obtencionGradoBachi != null && obtencionGradoBachi.getGradoAcademico() != null) {
                                text = text.replace(enums.getValue(), obtencionGradoBachi.getGradoAcademico().getNombre());
                                
                            } else if (egresado != null && egresado.getTitulo() != null) {
                                text = text.replace(enums.getValue(), egresado.getTitulo().getNombre());
                                
                            }
                            
                            break;
                        case CICLO_PROMOCION:
                            text = text.replace(enums.getValue(), egresado.getCicloAcademico().getCodigo());
                            break;
                        case CICLO_EGRESO:
                            text = text.replace(enums.getValue(), egresado.getCicloAcademico().getDescripcion());
                            break;
                        
                        case PROGRAMA:
                            String programa = "";
                            if (alumno.getCarrera().getCodigo().equals(CODIGO_ALIANZA_ESTRATEGICA)) {
                                programa = programa.concat("por el Convenio de la " + alumno.getCarrera().getNombre());
                            } else {
                                
                                programa = programa.concat("como " + alumno.getPersona().getGeneroAlumno("alter") + " " + alumno.getCarrera().getNombre());
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
                            
                            text = text.replace(enums.getValue(), egresado.getPromedioGraduacion().toString());
                            
                            break;
                        case MEJOR_PROMEDIO_PONDERADO_GRADUACION:
                            
                            text = text.replace(enums.getValue(), egresado.getPromedioGraduacion().toString());
                            
                            break;
                        
                    }
                    run.setText(text, 0);
                    
                }
                
            }
        }
        
    }
    
    private void remplazarTablas(XWPFDocument doc, Alumno alumno, List<VariablePlantilla> variables, List<AlumnoCiclo> alumnoCiclos) throws XmlException, IOException {
        List<AlumnoCicloCurso> alumnoCicloCursos = alumnoCicloCursoDAO.allOperativesByAlumno(alumno);
        Map<Long, List<AlumnoCicloCurso>> mapalumnoCicloCursos = TypesUtil.convertListToMapList("alumnoCiclo.cicloAcademico.id", alumnoCicloCursos);
        List<XWPFTable> tbl = doc.getTables();

//        tbl.addAll(fTables);
//        String tableOrigin = html.getElementsByClass(VARIABLE_TABLE).html();
//        String tableClone = html.getElementsByClass(VARIABLE_TABLE).html();
//        for (AlumnoCiclo ac : alumnoCiclos) {
//        }
        for (XWPFTable fTable : tbl) {
            int countRowsInicial = fTable.getRows().size();
            int lineAll = 34;
            for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                
                List<AlumnoCicloCurso> cicloCursos = TypesUtil.getListNotNull(mapalumnoCicloCursos.get(alumnoCiclo.getCicloAcademico().getId()));
                if (lineAll < cicloCursos.size()) {
                    for (int i = 0; i <= lineAll; i++) {
                        XWPFTableRow newrow = fTable.createRow();
                        newrow.getCell(0).setText("Libre");
                        fTable.addRow(newrow);
                    }
                    lineAll = 36;
                }
                XWPFTableRow oldRowCiclo = fTable.getRow(0);
                CTRow ctrowCiclo = CTRow.Factory.parse(oldRowCiclo.getCtRow().newInputStream());
                XWPFTableRow newRowCiclo = new XWPFTableRow(ctrowCiclo, fTable);
                this.switchValue(null, newRowCiclo, variables, alumnoCiclo);
                fTable.addRow(newRowCiclo);
                lineAll = lineAll - cicloCursos.size();
                for (AlumnoCicloCurso alumnoCicloCurso : cicloCursos) {
                    XWPFTableRow oldRow = fTable.getRow(1);
                    CTRow ctrow = CTRow.Factory.parse(oldRow.getCtRow().newInputStream());
                    XWPFTableRow newRow = new XWPFTableRow(ctrow, fTable);
                    this.switchValue(alumnoCicloCurso, newRow, variables, alumnoCiclo);
                    fTable.addRow(newRow);
                }
            }
            
            for (int i = 0; i < countRowsInicial; i++) {
                for (int j = 0; j < fTable.getRow(i).getTableCells().size(); j++) {
                    fTable.getRow(i).removeCell(j);
                }
            }
            
        }
        
    }
    
    private void switchValue(AlumnoCicloCurso alumnoCicloCurso, XWPFTableRow pFTableRow, List<VariablePlantilla> variables, AlumnoCiclo alumnoCiclo) {
        
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
                                run.setText(alumnoCicloCurso.getCurso().getCodigo(), 0);
                            }
                            
                            break;
                        case TABLA_CURSO:
                            if (alumnoCicloCurso != null) {
                                text = text.replace(enums.getValue(), alumnoCicloCurso.getCurso().getNombre());
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
                            text = text.replace(enums.getValue(), alumnoCiclo.getCicloAcademico().getDescripcion2());
                            run.setText(text, 0);
                            break;
                    }
                }
            }

//                        }
        }
        
    }
}
