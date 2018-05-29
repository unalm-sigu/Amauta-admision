package pe.edu.lamolina.pivot.controller.tramite.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import de.akquinet.commons.image.io.Image;
import de.akquinet.commons.image.io.ImageMetadata;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Curso;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.ContenidoCartaEnum;
import static pe.edu.lamolina.model.enums.ContenidoVariableEnum.__ESTIMADO__;
import static pe.edu.lamolina.model.enums.ContenidoVariableEnum.__NOMBREPERSONA__;
import pe.edu.lamolina.model.enums.TipoConstanciaEnum;
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.ContenidoCarta;
import pe.edu.lamolina.model.misc.FotoHelper;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.Tramite;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PDFFormatoEnum;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PdfHtmlView;

@Controller
@RequestMapping("tramite/solicitudconstancia/updatehistorial")
public class UpdateHistorialAcademicoController {

    @Autowired
    UpdateHistorialAcademicoService service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    PdfHtmlView pdfHtmlView;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        return "tramite/updatehistorialacademico/updateHistorialAcademicoList";
    }

    @RequestMapping("{idAlumno}/updatehistorial")
    public String datoacademico(@PathVariable("idAlumno") Long idAlumno, Model model, HttpSession session) {
        FotoHelper helper = new FotoHelper();
        Alumno alumno = service.allInfo(new Alumno(idAlumno));
        List<CicloAcademico> ciclosAcademico = service.allCicloAcademico();
        ObjectNode alumnoJson = alumno.toJsonInfoAcademico();
        alumnoJson.put("rutaFoto", helper.getRutaFoto(alumno.getPersona().getFoto(), alumno.getPersona().getSexo()));
        model.addAttribute("datoAlumno", alumnoJson);
        model.addAttribute("ciclosAcademico", ciclosAcademico);
        return "tramite/updatehistorialacademico/updateHistorialAcademico";
    }

    @ResponseBody
    @RequestMapping("savehistorial")
    public JsonResponse update(Alumno alumnoForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.updateHistorialAcademico(alumnoForm, ds);
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("notas")
    public JsonResponse notas(Alumno alumnoForm) {
        JsonResponse response = new JsonResponse();

        try {

            List<AlumnoCiclo> notas = service.allPromediosByAlumno(alumnoForm);
            ArrayNode arrayNotas = new ArrayNode(JsonNodeFactory.instance);
            for (AlumnoCiclo nota : notas) {

                SituacionAcademica situacionAcademica = nota.getSituacionFinal();
                CicloAcademico cicloAcademico = nota.getCicloAcademico();

                ObjectNode alumnoCicloNode = service.toJson(nota);
                alumnoCicloNode.put("cicloAcademico", service.toJson(cicloAcademico));
                alumnoCicloNode.put("situacionAcademica", service.toJson(situacionAcademica));

                List<AlumnoCicloCurso> cursos = nota.getAlumnoCicloCurso();
                ArrayNode cursosArray = new ArrayNode(JsonNodeFactory.instance);

                for (AlumnoCicloCurso alumnoCicloCurso : cursos) {
                    ObjectNode alumnoCicloCursoNode = service.toJson(alumnoCicloCurso);
                    Curso curso = alumnoCicloCurso.getCurso();
                    alumnoCicloCursoNode.put("curso", service.toJson(curso));
                    cursosArray.add(alumnoCicloCursoNode);
                }

                alumnoCicloNode.set("alumnociclocursos", cursosArray);
                arrayNotas.add(alumnoCicloNode);
            }

            response.setData(arrayNotas);
            response.setTotal(arrayNotas.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("searchcurso")
    public JsonResponse searchCurso(@RequestParam("nombre") String nombre, @RequestParam("idCursos[]") ArrayList<Long> idCursos) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Curso> cursos = service.allCursoByNameExceptList(nombre, idCursos);
            ArrayNode jCursos = new ArrayNode(jsonFactory);
            for (Curso curso : cursos) {
                jCursos.add(service.toJson(curso));
            }
            response.setData(jCursos);
            response.setTotal(jCursos.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("searchciclo")
    public JsonResponse searchciclo(@RequestParam("nombre") String nombre, @RequestParam("idCiclos[]") ArrayList<Long> idCiclos) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<CicloAcademico> ciclos = service.allCicloByNameExceptList(nombre, idCiclos);
            ArrayNode jCiclo = new ArrayNode(jsonFactory);
            for (CicloAcademico ciclo : ciclos) {
                jCiclo.add(service.toJson(ciclo));
            }
            response.setData(jCiclo);
            response.setTotal(jCiclo.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
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

                ObjectNode json = new ObjectNode(jsonFactory);

                json.put("id", alumno.getId());
                json.put("nombre", alumno.getPersona().getNombreCompleto());
                json.put("email", alumno.getPersona().getEmailCompania());
                json.put("telefono", alumno.getPersona().getTelefono());
                json.put("celular", alumno.getPersona().getCelular());
                json.put("codigoMatricula", alumno.getCodigo());
                json.put("carrera", alumno.getCarrera().getNombre());
                json.put("facultad", alumno.getCarrera().getFacultad().getNombre());
                json.put("tipo", alumno.getPersona().getTipoDocumento().getSimbolo());
                json.put("numero", alumno.getPersona().getNumeroDocIdentidad());
                json.put("rutaFoto", helper.getRutaFoto(alumno.getPersona().getFoto(), alumno.getPersona().getSexo()));
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
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter) {

        DynatableResponse json = new DynatableResponse();
        try {
            List<TramiteDocumentoAcademico> tipos = service.allTramiteDocumentoAcademico(filter);
            List<PrecioDocumento> precios = service.allPrecioDocumento();
            Map<Long, List<PrecioDocumento>> preciosMap = TypesUtil.convertListToMapList("tipoDocumento.id", precios);
            FotoHelper helper = new FotoHelper();
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (TramiteDocumentoAcademico tramiteDoc : tipos) {

                ObjectNode node = service.toJson(tramiteDoc.toJson());
                Tramite tramite = tramiteDoc.getTramite();
                Alumno alumno = tramiteDoc.getTramite().getAlumno();
                TipoDocumentoAcademico tipoDocumento = tramiteDoc.getTipoDocumentoAcademico();
                Carrera carrera = alumno.getCarrera();
                Facultad facultad = carrera.getFacultad();

                node.put("id", tramiteDoc.getId());

                node.put("nombre", alumno.getPersona().getApellidosNombres());
                node.put("idalumno", alumno.getId());
                node.put("carrera", alumno.getCarrera().getNombre());
                node.put("facultad", alumno.getCarrera().getFacultad().getNombre());
                node.put("codigoMatricula", alumno.getCodigo());
                node.put("tipo", alumno.getPersona().getTipoDocumento().getSimbolo());
                node.put("dni", alumno.getPersona().getNumeroDocIdentidad());
                node.put("showfacultad", !facultad.getCodigo().equals(carrera.getCodigo()));
                node.put("rutaFoto", helper.getRutaFoto(alumno.getPersona().getFoto(), alumno.getPersona().getSexo()));
                if (!Strings.isNullOrEmpty(tipoDocumento.getTipo())) {
                    node.put("documentoName", TipoConstanciaEnum.valueOf(tipoDocumento.getTipo()).getValue());
                }
                node.put("documentoTipo", (String) ObjectUtil.getParentTree(tramiteDoc, "tipoDocumentoAcademico.tipo"));
                node.put("showUpdateHistorial", TipoConstanciaEnum.CERT.name().equalsIgnoreCase(tipoDocumento.getTipo()));
                node.put("numero", tramiteDoc.getTramite().getSerie() + "-" + tramiteDoc.getTramite().getNumero());
                node.put("documento", (String) ObjectUtil.getParentTree(tramiteDoc, "tipoDocumentoAcademico.nombre"));
                node.put("fecha", new DateTime(tramite.getFechaRegistro()).toString("dd/MM/yyyy"));
                node.put("estado", tramiteDoc.getEstado());
                node.put("estadoEnum", tramiteDoc.getEstadoEnum().getValue());
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
    @RequestMapping("save")
    public JsonResponse save(TramiteDocumentoAcademico solicitudConstanciaForm, HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            if (solicitudConstanciaForm.getId() == null) {
                service.saveTramiteDocumentoAcademico(solicitudConstanciaForm, ds);
                response.setMessage("Solicitud creado satisfactoriamente");
            } else {
                service.updateTramiteDocumentoAcademico(solicitudConstanciaForm, ds);
                response.setMessage("Solicitud actualizado satisfactoriamente");
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
    @RequestMapping("delete")
    public JsonResponse delete(TramiteDocumentoAcademico solicitudConstancia) {
        JsonResponse response = new JsonResponse();
        try {
            service.delete(solicitudConstancia);
            response.setMessage("Tipo de documento eliminado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cancelar")
    public JsonResponse cancelar(TramiteDocumentoAcademico solicitudConstancia) {
        JsonResponse response = new JsonResponse();
        try {
            service.cancelar(solicitudConstancia);
            response.setMessage("Tipo de documento cancelado satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session, RedirectAttributes redirectAttr) {

        try {

            List<Idioma> idiomas = service.allIdiomas();
            List<TipoDocumentoAcademico> tiposDocumentoAcademico = service.allTipoDocumentoAcademico();
            model.addAttribute("idiomas", idiomas);
            model.addAttribute("tiposDocumentoAcademico", tiposDocumentoAcademico);
            model.addAttribute("solicitud", new TramiteDocumentoAcademico());

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
            return "redirect:/tramite/solicitudconstancia/updatehistorial";
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/tramite/solicitudconstancia/updatehistorial";
        }

        return "tramite/updatehistorialacademico/updateHistorialAcademicoForm";
    }

    @RequestMapping("{idTramite}/update")
    public String update(@PathVariable("idTramite") Long idTramite, Model model, HttpSession session, RedirectAttributes redirectAttr) {
        try {

            List<Idioma> idiomas = service.allIdiomas();
            List<TipoDocumentoAcademico> tiposDocumentoAcademico = service.allTipoDocumentoAcademico();
            model.addAttribute("idiomas", idiomas);
            model.addAttribute("tiposDocumentoAcademico", tiposDocumentoAcademico);
            model.addAttribute("solicitud", new TramiteDocumentoAcademico(idTramite));

        } catch (PhobosException ex) {
            ExceptionHandler.handleException(ex, redirectAttr);
            return "redirect:/tramite/solicitudconstancia/updatehistorial";
        } catch (Exception e) {
            ExceptionHandler.handleException(e, redirectAttr);
            return "redirect:/tramite/solicitudconstancia/updatehistorial";
        }
        return "tramite/updatehistorialacademico/updateHistorialAcademicoForm";
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(TramiteDocumentoAcademico solicitudConstancia) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            FotoHelper helper = new FotoHelper();

            ObjectNode data = new ObjectNode(jsonFactory);
            TramiteDocumentoAcademico tramiteDocumento = service.findTramiteDocumentoAcademico(solicitudConstancia);
            Alumno alumno = tramiteDocumento.getTramite().getAlumno();

            ObjectNode jSolicitudConstancia = JsonHelper.createJson(tramiteDocumento, jsonFactory, true, new String[]{
                "*",
                "tramite.*",
                "tramite.persona.fullRutaFotoTemporal",
                "tramite.persona.rutaFotoTemporal",
                "tipoDocumentoAcademico.*",
                "idioma.*"
            });

            data.put("solicitud", jSolicitudConstancia);
            Persona persona = alumno.getPersona();
            Carrera carrera = alumno.getCarrera();
            Facultad facultad = carrera.getFacultad();

            ObjectNode jAlumno = service.toJson(alumno);
            jAlumno.put("nombre", persona.getNombreCompleto());
            jAlumno.put("email", persona.getEmailCompania());
            jAlumno.put("telefono", persona.getTelefono());
            jAlumno.put("celular", persona.getCelular());
            jAlumno.put("tipo", persona.getTipoDocumento().getSimbolo());
            jAlumno.put("numero", persona.getNumeroDocIdentidad());
            jAlumno.put("rutaFoto", helper.getRutaFoto(persona.getFoto(), persona.getSexo()));
            jAlumno.put("codigoMatricula", alumno.getCodigo());
            jAlumno.put("carrera", alumno.getCarrera().getNombre());
            jAlumno.put("facultad", alumno.getCarrera().getFacultad().getNombre());
            jAlumno.put("showfacultad", !facultad.getCodigo().equals(carrera.getCodigo()));
            data.put("alumno", jAlumno);

            response.setData(data);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("tipodocumento")
    public JsonResponse tipodocumento(HttpSession session) {
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);
        try {
            ArrayNode arrayTipoDocumentoAcademico = new ArrayNode(JsonNodeFactory.instance);
            service.fillTipoDocumentoAcademico(arrayTipoDocumentoAcademico);
            response.setData(arrayTipoDocumentoAcademico);
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

        cabecera = cabecera.replaceAll(__NOMBREPERSONA__.name(), persona.getNombreCompleto());
        cabecera = cabecera.replaceAll(__ESTIMADO__.name(), estimado);

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
    @RequestMapping("revision")
    public JsonResponse revision(TramiteDocumentoAcademico solicitudConstancia) {
        JsonResponse response = new JsonResponse();
        try {
            service.revision(solicitudConstancia);
            response.setMessage("solicitud enviada a revisión satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
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
            if (Constantine.IMAGE_DPIHEIGHT > metadata.getDpiHeight()) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_DPIHEIGHT_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_DPIHEIGHT_MSG);
            }
            logger.debug("DpiWidth {}", metadata.getDpiWidth());
            if (Constantine.IMAGE_DPIWIDTH > metadata.getDpiWidth()) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_DPIWIDTH_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_DPIWIDTH_MSG);
            }
            logger.debug("Height {}", metadata.getHeight());
            int sizeHeight = Math.abs(Constantine.IMAGE_HEIGHT - metadata.getHeight());
            if (sizeHeight > Constantine.IMAGE_DELTA_SIZE) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_HEIGHT_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_HEIGHT_MSG);
            }
            logger.debug("Width {}", metadata.getWidth());
            int sizeWidth = Math.abs(Constantine.IMAGE_WIDTH - metadata.getWidth());
            if (sizeWidth > Constantine.IMAGE_DELTA_SIZE) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_WIDTH_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_WIDTH_MSG);
            }
            logger.debug("Format {}", metadata.getFormat());
            if (!Arrays.asList(Constantine.IMAGE_FORMAT).contains(metadata.getFormat().toString())) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_FORMAT_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_FORMAT_MSG);
            }
            logger.debug("ColorType {}", metadata.getColorType());
            if (!Constantine.IMAGE_COLORTYPE.equalsIgnoreCase(metadata.getColorType().toString())) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_COLORTYPE_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_COLORTYPE_MSG);
            }
            logger.debug("BitsPerPixel {}", metadata.getBitsPerPixel());
            if (Constantine.IMAGE_BITSPERPIXEL > metadata.getBitsPerPixel()) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_BITSPERPIXEL_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_BITSPERPIXEL_MSG);
            }
            logger.debug("Transparent {}", metadata.isTransparent());
            if (metadata.isTransparent()) {
                formatook = Boolean.FALSE;
                nocumplerequisito.append(Constantine.IMAGE_TRANSPARENT_MSG);
                nocumplerequisito.append(" , ");
                logger.debug("{}", Constantine.IMAGE_TRANSPARENT_MSG);
            }

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
    @RequestMapping("allTipoDocumento")
    public JsonResponse allTipoDocumento(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {

            ArrayNode jsonList = new ArrayNode(jsonFactory);
            List<TipoDocumentoAcademico> tipos = service.allTipoDocumentoAcademicoByName(nombre);
            for (TipoDocumentoAcademico tipo : tipos) {

                ObjectNode nodeTipoDocumento = JsonHelper.createJson(tipo, jsonFactory, true, new String[]{
                    "*",
                    "precioDocumento.*",
                    "precioDocumento.idioma.*",
                    "precioDocumento.tipoDocumento.*"
                });

                nodeTipoDocumento.put("tipoName", TipoConstanciaEnum.valueOf(tipo.getTipo()).getValue());
                jsonList.add(nodeTipoDocumento);
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);

        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }

        return response;
    }

}
