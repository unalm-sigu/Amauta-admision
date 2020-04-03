package pe.edu.lamolina.pivot.controller.academico.anexoboletin;

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
import org.springframework.web.bind.annotation.RequestBody;
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
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.pivot.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/anexo")
public class AnexoBoletinController {

    @Autowired
    AnexoBoletinService service;
    @Autowired
    VerificadorService verificadorService;

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
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        List<AnexoBoletin> anexosSup = service.allAnexosSuperiores();
        List<DepartamentoAcademico> departamentos = service.allDepartamentosAcademicos();
        List<Carrera> carreras = service.allCarrerasPosgrado();

        ObjectNode cicloJson = createCicloJson(findCicloAnexo(ds, session));
        ArrayNode anexosSuperJson = createAnexosSuperioresJson(anexosSup);
        ArrayNode departamentosJson = createDepartamentosJson(departamentos);
        ArrayNode carrerasJson = createCarrerasJson(carreras);

        model.addAttribute("resumen", service.resumen());
        model.addAttribute("cicloJson", cicloJson.toString());
        model.addAttribute("anexosSuperJson", anexosSuperJson.toString());
        model.addAttribute("departamentosJson", departamentosJson.toString());
        model.addAttribute("carrerasJson", carrerasJson.toString());
        model.addAttribute("puedeEditar", verificadorService.puedeEditarAnexos(ds));
        model.addAttribute("puedeEditarPosgrado", verificadorService.puedeEditarAnexosPosgrado(ds));
        return "academico/anexoboletin/anexo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            CicloAcademico ciclo = findCicloAnexo(ds, session);

            List<AnexoBoletin> anexos = service.allByDynatable(filter, ciclo);
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            Integer maximo = 0;
            for (AnexoBoletin anexo : anexos) {
                if (anexo.getEstadoEnum() != EstadoEnum.ACT) {
                    continue;
                }
                if (anexo.getOrden() > maximo) {
                    maximo = anexo.getOrden();
                }
            }

            for (AnexoBoletin anexo : anexos) {
                ObjectNode node = createAnexoJson(anexo);
                node.put("ordenMaximo", maximo);
                array.add(node);
            }
            json.setData(array);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @RequestMapping("nuevo")
    public String nuevo(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        AnexoBoletin anexo = new AnexoBoletin();

        model.addAttribute("anexo", anexo);
        model.addAttribute("anexos", service.allAnexosSuperiores());
        return "academico/anexoboletin/anexoForm";
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody AnexoBoletin anexo, HttpSession session) {
        JsonResponse response = new JsonResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
            service.save(anexo, ds.getUsuario());
            response.setMessage("Se guardo el anexo satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("cambiarEstado/{accion}")
    public JsonResponse cambiarEstadoCarrera(
            @RequestBody AnexoBoletin anexoBoletin,
            @PathVariable("accion") String accion) {

        JsonResponse response = new JsonResponse();
        try {
            service.cambiarEstado(anexoBoletin, accion);

            if (accion.equals("eliminar")) {
                response.setMessage("Registro eliminado satisfactoriamente.");
            } else {
                response.setMessage("Se cambio de estado satisfactoriamente.");
            }
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (RuntimeException e) {
            ExceptionHandler.handleSpecial(e, response, "El registro se encuentra relacionado a otros elementos del sistema y no puede ser eliminado");
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("{idAnexo}/cambiarOrden/{direccion}")
    public JsonResponse cambiarOrden(
            @PathVariable("idAnexo") Long idAnexo,
            @PathVariable("direccion") String direccion) {

        JsonResponse response = new JsonResponse();
        response.setSuccess(false);

        try {
            service.cambiarOrden(new AnexoBoletin(idAnexo), direccion);
            response.setMessage("Se cambio de orden satisfactoriamente.");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("allCiclos")
    public JsonResponse allCiclos(@RequestParam("nombre") String nombre, HttpSession session) {

        JsonNodeFactory jsonFactory = JsonNodeFactory.instance;
        JsonResponse response = new JsonResponse();

        try {
            List<CicloAcademico> ciclos = service.allCiclosByNombre(nombre);
            ArrayNode jsonList = new ArrayNode(jsonFactory);

            for (CicloAcademico ciclo : ciclos) {
                ObjectNode json = createCicloJson(ciclo);
                jsonList.add(json);
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
    @RequestMapping("changeCiclo")
    public JsonResponse changeCiclo(@RequestBody CicloAcademico ciclo, HttpSession session) {

        JsonResponse response = new JsonResponse();
        try {

            CicloAcademico cicloBD = service.findCiclo(ciclo);
            session.setAttribute(Constantine.CICLO_ANEXO_BOLETIN, cicloBD);

            response.setMessage("Se cambio de ciclo satisfactoriamente");
            response.setSuccess(true);

        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    private CicloAcademico findCicloAnexo(DataSessionPivot ds, HttpSession session) {
        CicloAcademico ciclo = (CicloAcademico) session.getAttribute(Constantine.CICLO_ANEXO_BOLETIN);
        if (ciclo == null) {
            ciclo = ds.getCicloAcademico();
            session.setAttribute(Constantine.CICLO_ANEXO_BOLETIN, ciclo);
        }
        return ciclo;
    }

    private ObjectNode createCicloJson(CicloAcademico ciclo) {
        ObjectNode json = JsonHelper.createJson(ciclo, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "descripcion", "descripcion2"
        });
        return json;
    }

    private ObjectNode createAnexoJson(AnexoBoletin anexo) {
        ObjectNode node = JsonHelper.createJson(anexo, JsonNodeFactory.instance, true, new String[]{
            "id", "codigo", "nombre", "orden", "estado", "estadoEnum", "motivoAnulacion", "cantidadGpoSecc",
            "departamentoAcademico.id",
            "departamentoAcademico.nombre",
            "carrera.id",
            "carrera.tipoEnum",
            "carrera.nombre",
            "anexoSuperior.id",
            "anexoSuperior.codigo",
            "anexoSuperior.nombre"
        });
        return node;
    }

    private ArrayNode createAnexosSuperioresJson(List<AnexoBoletin> anexosSup) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (AnexoBoletin anexo : anexosSup) {
            ObjectNode node = JsonHelper.createJson(anexo, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre", "orden", "estado", "estadoEnum"
            });
            array.add(node);
        }
        return array;
    }

    private ArrayNode createDepartamentosJson(List<DepartamentoAcademico> departamentos) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (DepartamentoAcademico dep : departamentos) {
            ObjectNode node = JsonHelper.createJson(dep, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre",
                "facultad.id",
                "facultad.codigo",
                "facultad.nombre"
            });
            array.add(node);
        }
        return array;
    }

    private ArrayNode createCarrerasJson(List<Carrera> carreras) {
        ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
        for (Carrera carr : carreras) {
            ObjectNode node = JsonHelper.createJson(carr, JsonNodeFactory.instance, true, new String[]{
                "id", "codigo", "nombre", "tipoEnum",
                "facultad.id",
                "facultad.codigo",
                "facultad.nombre",
                "modalidadEstudio.nombre"
            });
            array.add(node);
        }
        return array;
    }

}
