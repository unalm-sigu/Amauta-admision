package pe.edu.lamolina.amauta.controller.academico.pronabec;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.*;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.edu.lamolina.amauta.controller.academico.pronabec.reporte.BecadosCicloActualExcelView;
import pe.edu.lamolina.amauta.controller.academico.pronabec.reporte.BecadosCicloAnteriorExcelView;
import pe.edu.lamolina.amauta.controller.academico.pronabec.reporte.BecadosFilterExcelView;
import pe.edu.lamolina.amauta.controller.academico.pronabec.reporte.RecordNotasBecadosExcelView;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.constantines.GlobalMessages;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.pronabec.InformacionBeca;
import pe.edu.lamolina.model.pronabec.TipoBeca;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Controller
@RequestMapping("academico/becaspronabec")
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
public class BecasPronabecController {

    private final BecasPronabecService serviceBecasPronabec;
    private final TipoBecaPronabecService tipoBecaPronabecService;
    RecordNotasBecadosExcelView recordNotasBecadosExcelView;
    private final BecadosFilterExcelView becadosFilterExcelView;
    private final BecadosCicloActualExcelView becadosCicloActualExcelView;
    private final BecadosCicloAnteriorExcelView becadosCicloAnteriorExcelView;



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

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        List<TipoBeca> tiposBecaList=tipoBecaPronabecService.allTipoBecaPronabec();

        ArrayNode arrayTipoBeca = JaneHelper.from(tiposBecaList).array();
        model.addAttribute("tipoBecas", arrayTipoBeca);
        model.addAttribute("ciclos", JaneHelper.from(serviceBecasPronabec.allCicloRegular())
                .only("id,descripcion,codigo,year,descripcion2")
                .array().toString());

        return "academico/pronabec/becasPronabec";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allBecasPronabec(DynatableFilter filter, HttpSession session, HttpServletRequest request){
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        log.debug("Iniciando controlador: allBecasPronabec");

        try {

            List<InformacionBeca> becasPronabec;
            becasPronabec = serviceBecasPronabec.allByDynatable(filter);
            log.debug("Número de becas obtenidas: " + becasPronabec.size());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (InformacionBeca becPro : becasPronabec){
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                //Alumno alumno = becPro.getAlumno();
                Persona persona = becPro.getPersona();



                node.put("id", becPro.getId());
                node.put("yearConvocatoria", becPro.getYearConvocatoria());
                node.put("nombre", persona.getApellidosNombres());
                //node.put("tipoDoc", (String) ObjectUtil.getParentTree(persona, "tipoDocumento.simbolo"));
                node.put("nroDocumento", persona.getNumeroDocIdentidad());
                node.put("fechaOtorgamiento", TypesUtil.getStringDate( becPro.getFechaOtorgamiento(),"dd/MM/yyyy"));
                node.put("fechaInicio", TypesUtil.getStringDate(becPro.getFechaInicio(),"dd/MM/yyyy"));
                node.put("fechaFin", TypesUtil.getStringDate(becPro.getFechaFin(),"dd/MM/yyyy"));
                node.put("tipoBeca",becPro.getTipoBeca().getNombre());
                node.put("email", persona.getEmail());
                node.put("emailEmpresa", persona.getEmailCompania());
                node.put("rutaFoto", persona.getRutaFoto());
                node.put("tipoFoto", persona.getTipoFoto());
                node.put("condicion",becPro.getCondicion());
                node.put("situacion", becPro.getSituacion());
                node.put("modificador",becPro.getUsuario().getPersona().getApellidosNombres());
                node.put("fechaModificacion", becPro.getFechaRegistro().toString());
                node.put("carrera",becPro.getCarrera());
                node.put("estado",becPro.getEstado());
                array.add(node);
            }

            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());
        } catch (Exception e){
            e.printStackTrace();
            json.setTotal(0);
        }

        return json;
    }

    @RequestMapping("load")
    public String load(){
        return "academico/pronabec/loadCargaPronabec";
    }

    @ResponseBody
    @RequestMapping(value = "historial")
    public JsonResponse obtenerHistorialBecas(InformacionBeca infoBeca, HttpSession session ) {
        JsonResponse response = new JsonResponse();
        try {
            List<InformacionBeca> listBeca = serviceBecasPronabec.getHistorialBecas(infoBeca);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for(InformacionBeca informacionBeca : listBeca){
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                Persona persona = informacionBeca.getPersona();
                TipoBeca tipoBeca = informacionBeca.getTipoBeca();

                node.put("id",informacionBeca.getId());
                node.put("tipoBeca",tipoBeca.getNombre());
                node.put("yearConvocatoria",informacionBeca.getYearConvocatoria());
                node.put("fechaInicio", TypesUtil.getStringDate(informacionBeca.getFechaInicio(),"dd/MM/yyyy"));
                node.put("fechaFin", TypesUtil.getStringDate(informacionBeca.getFechaFin(),"dd/MM/yyyy"));
                array.add(node);
            }
            response.setData(array);
            response.setSuccess(Boolean.TRUE);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;

    }

    @ResponseBody
    @RequestMapping("cargarDatos")
    public JsonResponse cargarDatos(@RequestParam("file") MultipartFile file, HttpSession session){

        JsonResponse json = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {
            List<String> observados = serviceBecasPronabec.cargarBecados(file,ds);
            if (observados.isEmpty()) {
                json.setData(null);
                json.setSuccess(true);
                json.setMessage("Becados Pronabec guardadas");
            } else {
                log.debug("Hay observaciones");
                JsonNodeFactory factory = JsonNodeFactory.instance;
                ArrayNode observaciones = new ArrayNode(factory);
                for (String observado : observados) {
                    observaciones.add(observado);
                }
                json.setData(observaciones);
                json.setSuccess(false);
                json.setMessage("Hay "  + observaciones.size() + " observacion(es) y no se guardo " + observaciones.size() + " fila(s)");
            }
        } catch (PhobosException e) {
            pe.albatross.zelpers.miscelanea.ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);
        } finally {
            return json;
        }
    }

    @ResponseBody
    @RequestMapping("allAlumnoByNombre")
    public JsonResponse allAlumnoByNombre(@RequestParam("nombre") String nombre, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {

            JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
            List<Persona> lista = serviceBecasPronabec.allPersonaAlumno(nombre, ds);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (Persona alum : lista) {
                jsonList.add(JsonHelper.createJson(alum, jsonFactory, true,
                        new String[]{
                                "id",
                                "numeroDocIdentidad",
                                "nombreCompleto",
                                "paterno",
                                "materno",
                                "nombres",
                                "emailCompania",
                        }));
            }

            response.setData(jsonList);
            response.setTotal(jsonList.size());
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("saveBecado")
    public JsonResponse saveBecado(@RequestBody InformacionBeca informacionBeca, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        try {

            serviceBecasPronabec.saveBecado(informacionBeca, ds);
            response.setSuccess(true);
            response.setMessage("Se realizó el registro.");
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value = "eliminar",method = RequestMethod.POST)
    public JsonResponse eliminar(@RequestParam Long id, HttpSession session){
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            serviceBecasPronabec.eliminarBecado(id);
            response.setMessage("El registro fue eliminado satisfactoriamente.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value="anular", method = RequestMethod.POST)
    public JsonResponse anularBecadoPronabec(@RequestParam Long id, HttpSession session){
        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            serviceBecasPronabec.anularBecado(id);
            response.setMessage("El registro fue eliminado satisfactoriamente.");
            response.setSuccess(true);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, GlobalMessages.FK_ERROR_UPDATE);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping(value="updateBeca", method = RequestMethod.POST)
    public JsonResponse actualizarInformaBec(@RequestBody InformacionBeca informacionBeca,HttpSession session){
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        JsonResponse json = new JsonResponse();
        json.setSuccess(false);

        try {
            serviceBecasPronabec.actualizarInformBecado(informacionBeca,ds);
            json.setSuccess(true);
            json.setMessage(GlobalMessages.UPDATED);
        }catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, json);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, json);

        } finally {
            return json;
        }
    }

    @RequestMapping("exportExcel")
    public ModelAndView recordMatriculados(HttpSession session, Model model){
        try{
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<MatriculadosBecadosBean> listMatriculadosBecadosBean = serviceBecasPronabec.allMatriculadosBecados(ds.getCicloAcademico());
            model.addAttribute("listMatriculadosBecadosBean",listMatriculadosBecadosBean);

        }catch (PhobosException e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }catch (Exception e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView(recordNotasBecadosExcelView);
    }

    @ResponseBody
    @RequestMapping("filtroBecadosExcel")
    public ModelAndView descargaeBecadosFiltro(@RequestBody BecadosFilterBean becadosFilterBean, Model model, HttpSession session){

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<BecadosFilterBean> listBecadosFilter = serviceBecasPronabec.filterBecadosExcel(ds.getCicloAcademico(), becadosFilterBean);
            model.addAttribute("listBecadosFilter", listBecadosFilter);
            model.addAttribute("objtBecados", becadosFilterBean);

        }catch (PhobosException e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }catch (Exception e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView(becadosFilterExcelView);
    }

    @ResponseBody
    @RequestMapping("cicloActual/descargar")
    public ModelAndView descargarCicloActualBecados(@RequestBody BecadosFilterBean becadosFilterBean,Model model, HttpSession session){
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<BecadosFilterBean> listBecadosFilter = serviceBecasPronabec.filterActualBecados(ds.getCicloAcademico(), becadosFilterBean);
            model.addAttribute("listBecadosFilter", listBecadosFilter);
            model.addAttribute("objtBecados", becadosFilterBean);

        }catch (PhobosException e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }catch (Exception e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView(becadosCicloActualExcelView);
    }

    @ResponseBody
    @RequestMapping("cicloAnterior/descargar")
    public ModelAndView descargarCicloAnteriorBecados(@RequestBody BecadosFilterBean becadosFilterBean, Model model, HttpSession session){
        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
            List<BecadosFilterBean> listBecadosFilter = serviceBecasPronabec.filterAnteriorBecados(ds.getCicloAcademico(), becadosFilterBean);
            model.addAttribute("listBecadosFilter", listBecadosFilter);
            model.addAttribute("objtBecados", becadosFilterBean);

        }catch (PhobosException e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }catch (Exception e){
            e.printStackTrace();
            return new ModelAndView("redirect:/");
        }
        return new ModelAndView(becadosCicloAnteriorExcelView);
    }
}
