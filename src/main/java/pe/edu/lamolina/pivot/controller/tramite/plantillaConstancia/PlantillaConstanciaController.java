package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.controller.tramite.tipoConstancia.TipoConstanciaService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PDFFormatoEnum;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PdfHtmlView;

@Controller
@RequestMapping("tramite/plantillaconstancia")
public class PlantillaConstanciaController {

    @Autowired
    PlantillaConstanciaService service;

    @Autowired
    TipoConstanciaService tipoConstanciaService;

    @Autowired
    PdfHtmlView pdfHtmlView;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {

        dataBinder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new SimpleDateFormat("dd/MM/yyyy").parse(value));
                } catch (ParseException e) {
                    setValue(null);
                }
            }
        });

        dataBinder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String value) {
                try {
                    setValue(new BigDecimal(value.replaceAll(",", "")));
                } catch (Exception e) {
                    setValue(null);
                }
            }
        });
    }

    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public String editarContenido(@PathVariable("id") Long idPlantilla, Model model) {
        PlantillaDocumentoAcademico documentoAcademico = service.findById(new PlantillaDocumentoAcademico(idPlantilla));
        model.addAttribute("id", documentoAcademico.getId());
        model.addAttribute("contenido", documentoAcademico.getContenido());
        model.addAttribute("tipoDocumentoNombre", documentoAcademico.getTipoDocumentoAcademico().getNombre());
        model.addAttribute("idioma", documentoAcademico.getIdioma().getNombre());

        return "tramite/editarContenido/contenido";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        List<TipoDocumentoAcademico> list = tipoConstanciaService.all();
        List<Idioma> listIdioma = service.allIdioma();

        model.addAttribute("tipoDocumento", list.size() == 0 ? new ArrayList<TipoDocumentoAcademico>() : new TipoDocumentoAcademico().toArrayJson(list));
        model.addAttribute("idiomas", listIdioma.size() == 0 ? new ArrayList<Idioma>() : new Idioma().toArrayJson(listIdioma));
        return "tramite/plantillaConstancia/plantillaConstancia";
    }

    @ResponseBody
    @RequestMapping("updateContenido")
    public JsonResponse updateContenido(PlantillaDocumentoAcademico documentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            response.setSuccess(false);
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            PlantillaDocumentoAcademico psa = service.updateContenido(documentoAcademico, ds.getUsuario());

            ObjectNode jPlantillaDocumentoAcademico = JsonHelper.createJson(psa, jsonFactory, true, new String[]{
                "id",
                "variablePlantilla.*",
                "variablePlantilla.variableGenerica.*"
            });

            response.setData(jPlantillaDocumentoAcademico);
            response.setMessage("Registro actualizado satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(@RequestBody PlantillaDocumentoAcademico plantillaDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        response.setSuccess(false);
        try {
            System.out.println("ENTRE...... -->");
            service.update(plantillaDocumentoAcademico, ds.getUsuario());
            response.setMessage("Se actualizó");
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
    @RequestMapping("save")
    public JsonResponse save(@RequestBody PlantillaDocumentoAcademico plantillaDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.save(plantillaDocumentoAcademico, ds.getUsuario());
            response.setMessage("Se guardó");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            List<PlantillaDocumentoAcademico> list = service.all(filter);
            json.setData(new PlantillaDocumentoAcademico().toArrayJson(list));
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("{id}/find")
    public JsonResponse find(@PathVariable("id") Long id, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            PlantillaDocumentoAcademico documentoAcademico = service.findById(new PlantillaDocumentoAcademico(id));
            response.setData(documentoAcademico);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("preview")
    public JsonResponse preview(PlantillaDocumentoAcademico plantillaForm, Long idalumno) {
        JsonResponse response = new JsonResponse();
        try {

            Alumno alumno = service.findAlumno(idalumno);

            String contenido = plantillaForm.getContenido();

            contenido = contenido.replaceAll("__NUMERO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__SERIE__", alumno.getCodigo());
            contenido = contenido.replaceAll("__NOMBRE__", alumno.getPersona().getNombreCompleto());
            contenido = contenido.replaceAll("__CODIGOALUMNO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__ALUMNO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__FACULTAD__", alumno.getCarrera().getFacultad().getNombre());
            contenido = contenido.replaceAll("__YEARINICIOCICLO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__YEARFINCICLO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__MATRICULADO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__FECHA__", alumno.getCodigo());
            contenido = contenido.replaceAll("__JEFEOFICINA__", alumno.getCodigo());
            contenido = contenido.replaceAll("__CICLOINICIOROMANO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__CICLOFINROMANO__", alumno.getCodigo());
            contenido = contenido.replaceAll("__CICLOACTUAL__", alumno.getCodigo());

            response.setData(contenido);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @RequestMapping("previewpdf")
    public ModelAndView previewpdf(PlantillaDocumentoAcademico plantillaForm, Long idalumno, Model model) {

        Alumno alumno = service.findAlumno(idalumno);

        String contenido = plantillaForm.getContenido();

        contenido = contenido.replaceAll("__NUMERO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__SERIE__", alumno.getCodigo());
        contenido = contenido.replaceAll("__NOMBRE__", alumno.getPersona().getNombreCompleto());
        contenido = contenido.replaceAll("__CODIGOALUMNO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__ALUMNO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__FACULTAD__", alumno.getCarrera().getFacultad().getNombre());
        contenido = contenido.replaceAll("__YEARINICIOCICLO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__YEARFINCICLO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__MATRICULADO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__FECHA__", alumno.getCodigo());
        contenido = contenido.replaceAll("__JEFEOFICINA__", alumno.getCodigo());
        contenido = contenido.replaceAll("__CICLOINICIOROMANO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__CICLOFINROMANO__", alumno.getCodigo());
        contenido = contenido.replaceAll("__CICLOACTUAL__", alumno.getCodigo());

        model.addAttribute("contenido", contenido);

        model.addAttribute("formatoEnum", PDFFormatoEnum.PLANTILLA_CERTIFICADO);
        model.addAttribute("nombrePdf", "CertificadoEstudio");
        model.addAttribute("title", "untitle");

        return new ModelAndView(pdfHtmlView);
    }

}
