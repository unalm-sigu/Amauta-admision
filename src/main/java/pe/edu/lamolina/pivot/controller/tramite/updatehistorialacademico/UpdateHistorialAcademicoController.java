package pe.edu.lamolina.pivot.controller.tramite.updatehistorialacademico;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpSession;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
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
import pe.edu.lamolina.model.finanzas.CuentaBancaria;
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
        Alumno alumno = service.allInfo(new Alumno(idAlumno));
        List<CicloAcademico> ciclosAcademico = service.allCicloAcademico();
        ObjectNode alumnoJson = alumno.toJsonInfoAcademico();
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
    public JsonResponse notas(Alumno alumnoForm, Model model, HttpSession session) {
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
    public JsonResponse searchCurso(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();
        try {
            List<Curso> cursos = service.allCursoByName(nombre);
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
    @RequestMapping("searchalumno")
    public JsonResponse searchalumno(@RequestParam("nombre") String nombre, HttpSession session) {
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
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        try {
            List<TramiteDocumentoAcademico> tipos = service.allTramiteDocumentoAcademico(filter);
            List<PrecioDocumento> precios = service.allPrecioDocumento();
            Map<Long, List<PrecioDocumento>> preciosMap = TypesUtil.convertListToMapList("tipoDocumento.id", precios);

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (TramiteDocumentoAcademico tramiteDoc : tipos) {

                ObjectNode node = service.toJson(tramiteDoc.toJson());
                Tramite tramite = tramiteDoc.getTramite();
                Alumno alumno = tramiteDoc.getTramite().getAlumno();
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

                node.put("numero", tramiteDoc.getTramite().getSerie() + "-" + tramiteDoc.getTramite().getNumero());
                node.put("documentoName", (String) ObjectUtil.getParentTree(tramiteDoc, "tipoDocumentoAcademico.nombre"));
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
            Persona persona = alumno.getPersona();
            Carrera carrera = alumno.getCarrera();
            Facultad facultad = carrera.getFacultad();

            ObjectNode jSolicitudConstancia = service.toJson(tramiteDocumento);
            jSolicitudConstancia.put("tramite", service.toJson(tramiteDocumento.getTramite()));
            jSolicitudConstancia.put("tipoDocumentoAcademico", service.toJson(tramiteDocumento.getTipoDocumentoAcademico()));
            jSolicitudConstancia.put("idioma", service.toJson(tramiteDocumento.getIdioma()));
            data.put("solicitud", jSolicitudConstancia);

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

}
