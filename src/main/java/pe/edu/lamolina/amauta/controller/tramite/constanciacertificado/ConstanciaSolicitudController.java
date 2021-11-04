package pe.edu.lamolina.amauta.controller.tramite.constanciacertificado;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.akquinet.commons.image.io.Image;
import de.akquinet.commons.image.io.ImageMetadata;
import java.io.File;
import static java.lang.Boolean.TRUE;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.file.system.FileHelper;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.descargaWord.GeneradorWordSolicitudService;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bean.PlantillaIncrustacionGeneralBean;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.PlantillaIncrustacionDocumento;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.plantilla.PlantillaGenerica;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.zelper.pdf.PdfHtml;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.general.Archivo;

@Controller
@RequestMapping("tramite/solicitudconstancia")
public class ConstanciaSolicitudController {

    @Autowired
    ConstanciaSolicitudService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PdfHtml boletaPagoSolicitudConstanciaPDF;

    @Autowired
    GeneradorWordSolicitudService generadorWordSolicitudService;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "tramite/tramiteConstancia/solicitudConstancia";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter) {

        DynatableResponse json = new DynatableResponse();

        try {

            List<TramiteDocumentoAcademico> tramitesDocumentos = service.allTramiteDocumentoAcademico(filter);

            ArrayNode array = JaneHelper.from(tramitesDocumentos)
                    .join("idioma")
                    .join("estadoTramite")
                    .join("tipoDocumentoAcademico")
                    .join("tramite")
                    .join("tramite.persona")
                    .join("tramite.persona.tipoDocumento")
                    .join("tramite.alumno")
                    .join("tramite.alumno.carrera")
                    .join("tramite.alumno.carrera.facultad")
                    .join("tramite.alumno.persona")
                    .join("tramite.alumno.persona.tipoDocumento")
                    .join("tramite.cicloAcademico")
                    .join("tramite.tipoTramite")
                    .join("tramite.tipoTramite.oficina")
                    .join("archivo")
                    .array();

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("searchalumno")
    public JsonResponse searchalumno(@RequestParam("nombre") String nombre) {

        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;

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
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;
            logger.debug("guardando imagen ...");
            FileHelper.saveToDisk(archivo, absoluteName);
            Boolean formatook = Boolean.TRUE;
            StringBuilder nocumplerequisito = new StringBuilder();

            Image img = new Image(new File(absoluteName));
            ImageMetadata metadata = img.getMetadata();

            logger.debug("validando dpi...");
            logger.debug("DpiHeight {}", metadata.getDpiHeight());

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

        JsonResponse response = new JsonResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
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

    @RequestMapping("downloadWord/{id}")
    public void downloadWord(@PathVariable Long id, HttpSession session, HttpServletResponse respons, RedirectAttributes redirectAttr) {

        generadorWordSolicitudService.downloadWord(new TramiteDocumentoAcademico(id), respons);
    }

    @RequestMapping("solicitud/{idSolicitud}")
    public String nuevo(@PathVariable(value = "idSolicitud") Long idSolicitud, Model model, HttpSession session, RedirectAttributes redirectAttr) {

        List<TipoDocumentoAcademico> tiposDocumentoAcademico = service.allTipoDocumentoAcademico();

        TramiteDocumentoAcademico documentoAcademico = service.findTramite(new TramiteDocumentoAcademico(idSolicitud));

        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

        if (documentoAcademico != null) {

            node = JaneHelper.from(documentoAcademico)
                    .join("idioma")
                    .join("tramite")
                    .join("estadoTramite")
                    .join("tramite.alumno")
                    .join("tramite.alumno.carrera")
                    .join("tramite.alumno.carrera.facultad")
                    .join("tramite.alumno.persona")
                    .join("tramite.alumno.persona.tipoDocumento")
                    .join("tipoDocumentoAcademico")
                    .json();

            Long idAlumno = (Long) ObjectUtil.getParentTree(documentoAcademico, "tramite.alumno.id");
            logger.debug("idAlumno {}", idAlumno);
            model.addAttribute("idAlumno", idAlumno);
        }

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);

        for (TipoDocumentoAcademico tipoDocumentoAcademico : tiposDocumentoAcademico) {

            ArrayNode arrayIdiomas = new ArrayNode(JsonNodeFactory.instance);

            ObjectNode objectNode = JaneHelper.from(tipoDocumentoAcademico).json();

            ArrayNode arrayPrecios = new ArrayNode(JsonNodeFactory.instance);

            for (PrecioDocumento precioDocumento : tipoDocumentoAcademico.getPrecioDocumento()) {

                arrayPrecios.add(JaneHelper.from(precioDocumento).join("idioma").json());

                arrayIdiomas.add(JaneHelper.from(precioDocumento.getIdioma()).json());

            }

            objectNode.set("idiomas", arrayIdiomas);

            objectNode.set("precioDocumento", arrayPrecios);

            arrayNode.add(objectNode);
        }

        model.addAttribute("tiposDocumentoAcademico", arrayNode);
        model.addAttribute("solicitud", node);
        return "tramite/tramiteConstancia/solicitud";
    }

    @RequestMapping("view/{idSolicitud}")
    public String view(@PathVariable(value = "idSolicitud") Long idSolicitud, Model model, HttpSession session, RedirectAttributes redirectAttr) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            ArrayNode node = new ArrayNode(JsonNodeFactory.instance);
            String[] mapperTramite = new String[]{
                "id",
                "*",
                "persona.*",
                "alumno.*",
                "alumno.carrera.*",
                "alumno.carrera.facultad.*",
                "alumno.planCurricular.id",
                "alumno.modalidadEstudio.id",
                "alumno.modalidadEstudio.nombre",
                "alumno.planCurricular.carrera.nombre",
                "alumno.planCurricular.cicloInicioVigencia.descripcion",
                "compania.*",
                "cicloAcademico.*",
                "tipoTramite.codigo",
                "tipoTramite.nombre",
                "tipoTramite.esReincorporacionPregrado",
                "userRegistro.*",
                "userRegistro.persona.*",
                "userRespuesta.*",
                "accionesTramitesAcademico.*",
                "accionesTramitesAcademico.estadoTramiteFinal.*",
                "accionesTramitesAcademico.estadoTramiteInicio.*",
                "accionesTramitesDocumentos.*",
                "accionesTramitesDocumentos.estadoTramiteFinal.*",
                "accionesTramitesDocumentos.estadoTramite.*",
                "formularioEstadoTramite.*"
            };
            TramiteDocumentoAcademico documentoAcademico = service.findTramite(new TramiteDocumentoAcademico(idSolicitud));
            Tramite tramite = documentoAcademico.getTramite();
            PlantillaGenerica plantilla = service.findPlantillaHtml(documentoAcademico, ds.getUsuario());

            List<PlantillaDocumentoAcademico> plantillas = service.allPlantillas();
            for (PlantillaDocumentoAcademico item : plantillas) {
                node.add(JsonHelper.createJson(item, JsonNodeFactory.instance, new String[]{
                    "*",
                    "idioma.*",}));
            }

            model.addAttribute("id", idSolicitud);
            model.addAttribute("contenido", plantilla.getContenido());
            model.addAttribute("incrustaciones", node);
            model.addAttribute("tramite", JsonHelper.createJson(tramite, JsonNodeFactory.instance, mapperTramite));
        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
            return "redirect:/tramite/tramiteConstancia/solicitudConstancia";
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/tramite/tramiteConstancia/solicitudConstancia";
        }

        return "tramite/tramiteConstancia/viewContenido";
    }

    @ResponseBody
    @RequestMapping("validVariables")
    public JsonResponse validVariables(@RequestBody PlantillaIncrustacionGeneralBean plantillaGeneralBean, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            service.validVariables(plantillaGeneralBean, ds);
            PlantillaGenerica plantilla = service.findPlantillaHtml(plantillaGeneralBean.getTramiteDocumentoAcademico(), ds.getUsuario());
            response.setData(JsonHelper.createJson(plantilla, JsonNodeFactory.instance, new String[]{"*"}));
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Se agregó la incrusctación satisfactoriamente");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCicloAcademico")
    public JsonResponse allCicloAcademico(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<CicloAcademico> ciclos = service.allCicloAcademicoByName(nombre);
            ArrayNode jsonList = JaneHelper.from(ciclos).join("modalidadEstudio").array();
            response.setData(jsonList);
            response.setTotal(ciclos.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allTramiteIncrustaciones")
    public JsonResponse allTramiteIncrustaciones(@RequestParam("idTramiteAcademico") Long idTramiteAcademico, HttpSession session) {

        JsonResponse response = new JsonResponse();

        try {

            List<PlantillaIncrustacionDocumento> tramiteIncrustacion = service.allTramiteIncrustaciones(new TramiteDocumentoAcademico(idTramiteAcademico));
            ArrayNode jsonList = JaneHelper.from(tramiteIncrustacion).join("platillaIncrustacion").array();
            response.setData(jsonList);
            response.setTotal(tramiteIncrustacion.size());
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("deleteIncrustacion")
    public JsonResponse deleteIncrustacion(@RequestBody PlantillaIncrustacionDocumento pid, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse response = new JsonResponse();

        try {
            TramiteDocumentoAcademico documentoAcademico = service.deleteIncrustacion(pid);
            PlantillaGenerica generica = service.findPlantillaHtml(documentoAcademico, ds.getUsuario());
            response.setData(generica.getContenido());
            response.setMessage("Se eliminó la incrusctación satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("allParametros")
    public JsonResponse allParametros(@RequestBody PlantillaIncrustacionGeneralBean bean, HttpSession session) {

        JsonResponse response = new JsonResponse();
        ObjectNode nodeVariable = new ObjectNode(JsonNodeFactory.instance);
        response.setSuccess(false);
        try {
            List<VariablePlantilla> plantillas = service.allParametros(bean.getPlantillaDocumentoAcademico());
            nodeVariable.put("haveParams", plantillas.isEmpty() ? false : true);
            nodeVariable.set("lista", new ArrayNode(JsonNodeFactory.instance));
            for (VariablePlantilla plantilla : plantillas) {
                nodeVariable.set("lista", valores(plantilla, bean.getAlumno()));
            }
            response.setData(nodeVariable);
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("uploadBoletaFile")
    public JsonResponse uploadBoletaFile(@RequestParam("file") MultipartFile archivo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            String fileName = TypesUtil.getUnixTime() + archivo.getOriginalFilename();
            String absoluteName = GlobalConstantine.TMP_DIR + fileName;
            FileHelper.saveToDisk(archivo, absoluteName);
            ObjectNode json = new ObjectNode(jFactory);
            json.put("name", fileName);
            json.put("originalFilename", archivo.getOriginalFilename());
            json.put("contentType", archivo.getContentType());
            json.put("size", archivo.getSize());
            json.put("ruta", absoluteName);
            response.setData(json);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveArchivoTramite")
    public JsonResponse saveArchivoTramite(@RequestBody Archivo archivo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

            service.saveArchivoTramite(archivo, new Alumno(archivo.getIdAlumno()), ds);
            response.setMessage("Archivo subido satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("validarBoletaTramite")
    public JsonResponse validarBoletaTramite(@RequestBody TramiteDocumentoAcademico tramiteDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            service.validarBoletaTramite(tramiteDocumentoAcademico);
            response.setMessage("Boleta validado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("entregartramite")
    public JsonResponse entregarTramite(@RequestBody TramiteDocumentoAcademico tramiteDocumentoAcademico) {
        JsonResponse response = new JsonResponse();
        try {

            service.entregarTramite(tramiteDocumentoAcademico);
            response.setMessage("Trámite entregado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "verBoleta/{idTramiteDocumento}", method = RequestMethod.GET)
    public JsonResponse verBoleta(@PathVariable Long idTramiteDocumento, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            Archivo archivo = service.findBoletas(idTramiteDocumento);
            response.setSuccess(Boolean.TRUE);
            response.setData(JsonHelper.createJson(archivo, JsonNodeFactory.instance, new String[]{"*"}));
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "anulartramite/{idTramiteDocumentoAcademico}", method = RequestMethod.GET)
    public JsonResponse anularTramite(@PathVariable Long idTramiteDocumentoAcademico) {
        JsonResponse response = new JsonResponse();
        try {

            service.anularTramite(idTramiteDocumentoAcademico);
            response.setSuccess(Boolean.TRUE);
            response.setMessage("Registro eliminado satisfactoriamente.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "calcularPrecio", method = RequestMethod.POST)
    public JsonResponse calcularPrecio(@RequestBody TramiteDocumentoAcademico tramiteDocumentoAcademico) {

        JsonResponse response = new JsonResponse();

        try {

            response.setSuccess(Boolean.FALSE);

            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            Long cantidadCiclos = service.cantidadCiclosRegularAprobado(tramiteDocumentoAcademico.getTramite().getAlumno());
            BigDecimal costoTotal = service.calcularPrecio(tramiteDocumentoAcademico, cantidadCiclos);
            BigDecimal costoDocumento = service.costoDocumento(tramiteDocumentoAcademico);

            node.put("costoDocumento", costoDocumento.setScale(1, RoundingMode.HALF_UP));
            node.put("costoTotal", costoTotal.setScale(1, RoundingMode.HALF_UP));
            node.put("cantidadCiclos", cantidadCiclos);
            node.put("showCostoDocumento", costoDocumento != null);

            response.setData(node);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private ArrayNode valores(VariablePlantilla plantilla, Alumno alumno) {
        ArrayNode node = new ArrayNode(JsonNodeFactory.instance);
        switch (plantilla.getVariableGenerica().getCodigoVaribleEnum()) {
            case CICLO_ACADEMICO:
                List<AlumnoCiclo> alumnoCiclos = service.allAlumnoCiclo(alumno);
                for (AlumnoCiclo alumnoCiclo : alumnoCiclos) {
                    node.add(JaneHelper.from(alumnoCiclo.getCicloAcademico()).json());
                }
                break;
        }
        return node;
    }

}
