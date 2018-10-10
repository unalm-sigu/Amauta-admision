package pe.edu.lamolina.pivot.controller.tramite.tipoConstancia;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.enums.TipoConstanciaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.tramite.ConfiguracionFirmaDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/tipoconstancia")
public class TipoConstanciaController {

    @Autowired
    TipoConstanciaService service;

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
    public String index(Model model) {
        model.addAttribute("tipos", TipoConstanciaEnum.getJsonValues());
        return "tramite/tipoConstancia/tipoConstancia";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(TipoDocumentoAcademico tramiteDocumentoAcademico, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {

            if (tramiteDocumentoAcademico.getId() == null) {
                service.save(tramiteDocumentoAcademico, ds.getUsuario());
                response.setMessage("Se guardó");
            } else {
                service.update(tramiteDocumentoAcademico, ds.getUsuario());
                response.setMessage("Se actualizó");
            }
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
            List<TipoDocumentoAcademico> list = service.all(filter);
            json.setData(new TipoDocumentoAcademico().toArrayJson(list));
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
            TipoDocumentoAcademico tipoDocumentoAcademico = service.findById(new TipoDocumentoAcademico(id));
            response.setData(tipoDocumentoAcademico);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("delete")
    public JsonResponse delete(TipoDocumentoAcademico tipoDocumento, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            service.delete(tipoDocumento);
            response.setMessage("Registro removido satisfactoriamente");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allOficina")
    public JsonResponse allOficina(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<Oficina> oficinas = service.allOficina(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (Oficina oficina : oficinas) {
                jsonList.add(createOficinaJson(oficina));
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
    @RequestMapping("allTipoOficina")
    public JsonResponse allTipoOficina(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<TipoOficina> TipoOficinas = service.allTipoOficina(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);
            for (TipoOficina tipoOficina : TipoOficinas) {
                jsonList.add(JsonHelper.createJson(tipoOficina, jsonFactory, true, new String[]{"*"}));
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
    @RequestMapping("update")
    public JsonResponse update(TipoDocumentoAcademico tipoDocumento) {
        JsonResponse response = new JsonResponse();
        try {
            TipoDocumentoAcademico tipoDocumentoAcademico = service.findTipoDocumentoAcademico(tipoDocumento);
            ObjectNode jTipoDocumento = service.toJson(tipoDocumentoAcademico);
            ArrayNode firmas = new ArrayNode(JsonNodeFactory.instance);
            for (ConfiguracionFirmaDocumento configuracionFirmaDocumento : tipoDocumentoAcademico.getConfiguracionFirmaDocumento()) {
                ObjectNode firma = service.toJson(configuracionFirmaDocumento);
                firma.put("oficina", "");
                firma.put("tipoOficina", "");
                if (configuracionFirmaDocumento.getOficina() != null) {
                    firma.put("oficina", service.toJson(configuracionFirmaDocumento.getOficina()));
                }
                if (configuracionFirmaDocumento.getTipoOficina() != null) {
                    firma.put("tipoOficina", service.toJson(configuracionFirmaDocumento.getTipoOficina()));
                }
                firmas.add(firma);
            }
            jTipoDocumento.put("firmasDocumento", firmas);
            response.setData(jTipoDocumento);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private ObjectNode createOficinaJson(Oficina oficina) {
        ObjectNode node = JsonHelper.createJson(oficina, JsonNodeFactory.instance, true, new String[]{
            "id", "nombre", "codigo", "estadoEnum", "estado", "motivoAusenciaJefe",
            "fechaInicioJefatura", "fechaEncargatura",
            "tipoOficina.nivelEnum",
            "tipoOficina.codigoEnum",
            "oficinaSuperior.id",
            "oficinaSuperior.nombre",
            "cargoJefe.nombre",
            "jefeEncargado.id",
            "jefeEncargado.nombreConTitulo",
            "personaJefe.id",
            "personaJefe.nombreConTitulo"
        });
        return node;
    }

}
