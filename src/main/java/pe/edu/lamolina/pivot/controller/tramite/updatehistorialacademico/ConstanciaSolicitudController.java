package pe.edu.lamolina.pivot.controller.tramite.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.akquinet.commons.image.io.Image;
import de.akquinet.commons.image.io.ImageMetadata;
import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.ESTIMADO;
import static pe.edu.lamolina.model.enums.VariableContenidoEnum.NOMBRE_PERSONA;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.misc.FotoHelper;
import pe.edu.lamolina.model.tramite.AccionTramiteDocumento;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia.PlantillaGenerica;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PDFFormatoEnum;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PdfHtmlView;

@Controller
@RequestMapping("tramite/solicitudconstancia")
public class ConstanciaSolicitudController {

    @Autowired
    ConstanciaSolicitudService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PdfHtmlView pdfHtmlView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "tramite/tramiteConstancia/solicitudConstancia";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter) {

        DynatableResponse json = new DynatableResponse();
        try {
            List<TramiteDocumentoAcademico> tipos = service.allTramiteDocumentoAcademico(filter);
            List<PrecioDocumento> precios = service.allPrecioDocumento();
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (TramiteDocumentoAcademico tramiteDoc : tipos) {

                ObjectNode node = JsonHelper.createJson(tramiteDoc, JsonNodeFactory.instance, new String[]{
                    "*",
                    "idioma.*",
                    "estadoTramite.*",
                    "tramite.*",
                    "tramite.alumno.*",
                    "tramite.alumno.carrera.*",
                    "tramite.alumno.carrera.facultad.*",
                    "tramite.alumno.persona.*",
                    "tramite.alumno.persona.tipoDocumento.*",
                    "tipoDocumentoAcademico.*"});

                List<AccionTramiteDocumento> acciones = service.findEstadoByEstadoInicio(tramiteDoc.getTipoDocumentoAcademico(), tramiteDoc.getEstadoTramite());
                ArrayNode arrayAcciones = new ArrayNode(JsonNodeFactory.instance);
                for (AccionTramiteDocumento accion : acciones) {
                    if (!accion.getEstadoTramiteFinal().getCodigo().equals("PAG")) {
                        arrayAcciones.add(JsonHelper.createJson(accion, JsonNodeFactory.instance, new String[]{
                            "*",
                            "estadoTramiteFinal.*"}));
                    }
                }
                node.set("estados", arrayAcciones);
                array.add(node);
            }
            json.setData(array);
            json.setTotal(array.size());
            json.setFiltered(array.size());
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("searchalumno")
    public JsonResponse searchalumno(@RequestParam("nombre") String nombre) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            FotoHelper helper = new FotoHelper();
            List<Alumno> alumnos = service.allAlumnoByName(nombre);
            ArrayNode jAlumno = new ArrayNode(jsonFactory);
            for (Alumno alumno : alumnos) {

                ObjectNode json = JsonHelper.createJson(alumno, jsonFactory, new String[]{
                    "*",
                    "persona.*",
                    "persona.nombreCompleto",
                    "persona.emailCompania",
                    "persona.telefono",
                    "persona.numeroDocIdentidad",
                    "persona.tipoFoto",
                    "persona.rutaFoto",
                    "persona.fullRutaFotoTemporal",
                    "persona.tipoDocumento.*",
                    "persona.tipoDocumento.simbolo",
                    "carrera.*",
                    "carrera.nombre",
                    "carrera.facultad.*",
                    "carrera.facultad.nombre",});

                jAlumno.add(json);
            }
            response.setData(jAlumno);
            response.setTotal(jAlumno.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("imprimir")
    public ModelAndView imprimir(TramiteDocumentoAcademico tramiteDocumentoAcademicoForm, Model model, HttpSession session) {

        TramiteDocumentoAcademico tramiteDocumentoAcademico = service.findTramite(tramiteDocumentoAcademicoForm);
        Persona persona = tramiteDocumentoAcademico.getTramite().getPersona();
        Idioma idioma = tramiteDocumentoAcademico.getIdioma();
        TipoDocumentoAcademico tipoDocumento = tramiteDocumentoAcademico.getTipoDocumentoAcademico();

        String estimado = persona.esFemenino() ? "Estimada" : "Estimado";

        ContenidoCarta headBoletaPdf = service.findContenidoBoletaByCodigoEnum(ContenidoCartaEnum.BOLETA001);
        ContenidoCarta footBoletaPdf = service.findContenidoBoletaByCodigoEnum(ContenidoCartaEnum.BOLETA002);

        String cabecera = headBoletaPdf.getContenido();
        String pieBoleta = footBoletaPdf.getContenido();
        PrecioDocumento precioDocumento = service.findPrecioDocumentoByTipoIdioma(tipoDocumento, idioma);
        CuentaBancaria cuenta = precioDocumento.getCuentaBancaria();
        String montoString = precioDocumento.getPrecio().toString();

        cabecera = cabecera.replaceAll(NOMBRE_PERSONA.getValue(), persona.getNombreCompleto());
        cabecera = cabecera.replaceAll(ESTIMADO.getValue(), estimado);

        model.addAttribute("cabecera", cabecera);
        model.addAttribute("pieBoleta", pieBoleta);
        model.addAttribute("estimado", estimado);
        model.addAttribute("persona", persona);
        model.addAttribute("numero", tramiteDocumentoAcademico.getTramite().getSerie() + "-" + tramiteDocumentoAcademico.getTramite().getNumero());
        model.addAttribute("cuenta", cuenta);
        model.addAttribute("numeroDocIdentidad", persona.getNumeroDocIdentidad());
        model.addAttribute("montoString", montoString);
        model.addAttribute("formatoEnum", PDFFormatoEnum.BOLETA_PAGO_SOL);
        model.addAttribute("nombrePdf", "BoletaPagoSolicitudConstancia");

        return new ModelAndView(pdfHtmlView);
    }

    @ResponseBody
    @RequestMapping("searchcolaborador")
    public JsonResponse searchcolaborador(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            FotoHelper helper = new FotoHelper();
            List<Colaborador> colaboradores = service.allColaboradorByName(nombre);
            ArrayNode jColaborador = new ArrayNode(jsonFactory);
            for (Colaborador colaborador : colaboradores) {

                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", colaborador.getId());
                json.put("nombre", colaborador.getPersona().getNombreCompleto());
                json.put("email", colaborador.getPersona().getEmailCompania());
                json.put("telefono", colaborador.getPersona().getTelefono());
                json.put("celular", colaborador.getPersona().getCelular());
                json.put("codigo", colaborador.getCodigo());
                json.put("tipo", colaborador.getPersona().getTipoDocumento().getSimbolo());
                json.put("numero", colaborador.getPersona().getNumeroDocIdentidad());
                json.put("oficina", colaborador.getOficina().getNombre());
                json.put("rutaFoto", helper.getRutaFoto(colaborador.getPersona().getFoto(), colaborador.getPersona().getSexo()));
                jColaborador.add(json);
            }
            response.setData(jColaborador);
            response.setTotal(jColaborador.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("upload")
    public JsonResponse upload(@RequestParam("file") MultipartFile archivo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(Boolean.FALSE);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            ObjectNode json = new ObjectNode(jsonFactory);

            String fileExt = TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())).toLowerCase();
            String fileName = TypesUtil.getUnixTime() + "." + fileExt;
            String absoluteName = Constantine.TMP_DIR + fileName;
            logger.debug("guardando imagen ...");
            FileHelper.saveToDisk(archivo, absoluteName);
            Boolean formatook = Boolean.TRUE;
            StringBuilder nocumplerequisito = new StringBuilder();

            Image img = new Image(new File(absoluteName));
            ImageMetadata metadata = img.getMetadata();

            logger.debug("validando dpi...");
            logger.debug("DpiHeight {}", metadata.getDpiHeight());
//            if (Constantine.IMAGE_DPIHEIGHT > metadata.getDpiHeight()) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_DPIHEIGHT_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_DPIHEIGHT_MSG);
//            }
//            logger.debug("DpiWidth {}", metadata.getDpiWidth());
//            if (Constantine.IMAGE_DPIWIDTH > metadata.getDpiWidth()) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_DPIWIDTH_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_DPIWIDTH_MSG);
//            }
//            logger.debug("Height {}", metadata.getHeight());
//            int sizeHeight = Math.abs(Constantine.IMAGE_HEIGHT - metadata.getHeight());
//            if (sizeHeight > Constantine.IMAGE_DELTA_SIZE) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_HEIGHT_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_HEIGHT_MSG);
//            }
//            logger.debug("Width {}", metadata.getWidth());
//            int sizeWidth = Math.abs(Constantine.IMAGE_WIDTH - metadata.getWidth());
//            if (sizeWidth > Constantine.IMAGE_DELTA_SIZE) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_WIDTH_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_WIDTH_MSG);
//            }
//            logger.debug("Format {}", metadata.getFormat());
//            if (!Arrays.asList(Constantine.IMAGE_FORMAT).contains(metadata.getFormat().toString())) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_FORMAT_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_FORMAT_MSG);
//            }
//            logger.debug("ColorType {}", metadata.getColorType());
//            if (!Constantine.IMAGE_COLORTYPE.equalsIgnoreCase(metadata.getColorType().toString())) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_COLORTYPE_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_COLORTYPE_MSG);
//            }
//            logger.debug("BitsPerPixel {}", metadata.getBitsPerPixel());
//            if (Constantine.IMAGE_BITSPERPIXEL > metadata.getBitsPerPixel()) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_BITSPERPIXEL_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_BITSPERPIXEL_MSG);
//            }
//            logger.debug("Transparent {}", metadata.isTransparent());
//            if (metadata.isTransparent()) {
//                formatook = Boolean.FALSE;
//                nocumplerequisito.append(Constantine.IMAGE_TRANSPARENT_MSG);
//                nocumplerequisito.append(" , ");
//                logger.debug("{}", Constantine.IMAGE_TRANSPARENT_MSG);
//            }

            json.put("ok", formatook);
            json.put("nocumplerequisito", nocumplerequisito.toString());
            json.put("ruta", fileName);
            json.put("mime", TypesUtil.getClean(FilenameUtils.getExtension(archivo.getOriginalFilename())));
            json.put("size", archivo.getSize());

            response.setData(json);
            response.setSuccess(true);
            response.setMessage("Carga satisfactoria del archivo");

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;

    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody TramiteDocumentoAcademico documentoAcademico, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            if (documentoAcademico.getId() == null) {

                service.save(documentoAcademico, ds);
            } else {
                service.updateTramiteDocumentoAcademico(documentoAcademico, ds);
            }
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("onlyfoto")
    public JsonResponse onlyfoto(@RequestBody TramiteDocumentoAcademico tramiteDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.updateFotoTemporal(tramiteDocumentoAcademico, ds);
            response.setSuccess(Boolean.TRUE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(@RequestBody TramiteDocumentoAcademico tramiteDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.update(tramiteDocumentoAcademico, ds);
            response.setSuccess(Boolean.TRUE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("downloadWord/{id}")
    public void downloadWord(@PathVariable Long id, HttpSession session, HttpServletResponse respons, RedirectAttributes redirectAttr) {

            service.downloadWord(new TramiteDocumentoAcademico(id), respons);
    }

    @RequestMapping("solicitud/{idSolicitud}")
    public String nuevo(@PathVariable(value = "idSolicitud") Long idSolicitud, Model model, HttpSession session, RedirectAttributes redirectAttr) {

        try {

//            List<Idioma> idiomas = service.allIdiomas();
            List<TipoDocumentoAcademico> tiposDocumentoAcademico = service.allTipoDocumentoAcademico();
            TramiteDocumentoAcademico documentoAcademico = idSolicitud == null ? null : service.findTramite(new TramiteDocumentoAcademico(idSolicitud));
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            if (documentoAcademico != null) {
                node = JsonHelper.createJson(documentoAcademico, JsonNodeFactory.instance, new String[]{
                    "*",
                    "idioma.*",
                    "tramite.*",
                    "estadoTramite.*",
                    "tramite.alumno.*",
                    "tramite.alumno.carrera.*",
                    "tramite.alumno.carrera.facultad.*",
                    "tramite.alumno.persona.*",
                    "tramite.alumno.persona.tipoDocumento.*",
                    "tipoDocumentoAcademico.*"
                });
            }

            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (TipoDocumentoAcademico tipoDocumentoAcademico : tiposDocumentoAcademico) {
                ArrayNode arrayIdiomas = new ArrayNode(JsonNodeFactory.instance);
                ObjectNode objectNode = JsonHelper.createJson(tipoDocumentoAcademico, JsonNodeFactory.instance, new String[]{
                    "*"
                });
                ArrayNode arrayPrecios = new ArrayNode(JsonNodeFactory.instance);
                for (PrecioDocumento precioDocumento : tipoDocumentoAcademico.getPrecioDocumento()) {
                    arrayPrecios.add(JsonHelper.createJson(precioDocumento, JsonNodeFactory.instance, new String[]{
                        "*",
                        "idioma.*"
                    }));
                    arrayIdiomas.add(JsonHelper.createJson(precioDocumento.getIdioma(), JsonNodeFactory.instance, new String[]{
                        "*"
                    }));
                }
                objectNode.set("idiomas", arrayIdiomas);
                objectNode.set("precioDocumento", arrayPrecios);
                arrayNode.add(objectNode);
            }

            model.addAttribute("tiposDocumentoAcademico", arrayNode);
            model.addAttribute("solicitud", node);

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
            return "redirect:/tramite/tramiteConstancia/solicitudConstancia";
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/tramite/tramiteConstancia/solicitudConstancia";
        }

        return "tramite/tramiteConstancia/solicitud";
    }

    @RequestMapping("view/{idSolicitud}")
    public String view(@PathVariable(value = "idSolicitud") Long idSolicitud, Model model, HttpSession session, RedirectAttributes redirectAttr) {

        try {

            TramiteDocumentoAcademico documentoAcademico = service.findTramite(new TramiteDocumentoAcademico(idSolicitud));
            PlantillaGenerica plantilla = service.findPlantillaHtml(documentoAcademico);
            model.addAttribute("id", idSolicitud);
            model.addAttribute("contenido", plantilla.getContenido());
        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
            return "redirect:/tramite/tramiteConstancia/solicitudConstancia";
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/tramite/tramiteConstancia/solicitudConstancia";
        }

        return "tramite/tramiteConstancia/viewContenido";
    }
}
