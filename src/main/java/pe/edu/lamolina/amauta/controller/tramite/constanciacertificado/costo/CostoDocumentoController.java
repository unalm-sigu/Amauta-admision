package pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.costo;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.ExceptionHandler;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.tipoconstancia.TipoConstanciaService;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PrecioDocumento;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("tramite/costodocumento")
public class CostoDocumentoController {

    @Autowired
    CostoDocumentoService service;

    @Autowired
    TipoConstanciaService tipoConstanciaService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        List<TipoDocumentoAcademico> tiposDocumentos = tipoConstanciaService.all();
        List<Idioma> idiomas = service.allIdioma();

        model.addAttribute("tipoDocumento", JaneHelper.from(tiposDocumentos)
                .join("oficinaEmisora")
                .array()
                .toString());
        model.addAttribute("idiomas", JaneHelper.from(idiomas)
                .array()
                .toString());
        return "tramite/costoDocumento/costoDocumento";
    }

    @ResponseBody
    @RequestMapping("all")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {

            json.setData(JaneHelper.from(service.all(filter))
                    .join("tipoDocumento")
                    .join("idioma")
                    .array());

            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    @ResponseBody
    @RequestMapping("update")
    public JsonResponse update(@RequestBody PrecioDocumento precioDocumento, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            service.update(precioDocumento, ds);
            response.setMessage("Se actualizó");
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("save")
    public JsonResponse save(@RequestBody PrecioDocumento precioDocumento, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            service.save(precioDocumento, ds);
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
    @RequestMapping("{id}/find")
    public JsonResponse find(@PathVariable("id") Long id, HttpSession session) {
        JsonResponse response = new JsonResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);
        try {
            PrecioDocumento precioDocumento = service.findById(new PrecioDocumento(id));
            response.setData(precioDocumento);
            response.setSuccess(Boolean.TRUE);
        } catch (PhobosException e) {
            ExceptionHandler.handlePhobosEx(e, response);
        } catch (Exception e) {
            ExceptionHandler.handleException(e, response);
        }
        return response;
    }
}
