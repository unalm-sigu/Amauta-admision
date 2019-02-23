package pe.edu.lamolina.pivot.controller.general.tipocarpeta;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.general.TipoCarpeta;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("general/tipocarpeta")
public class TipoCarpetaController {

    @Autowired
    TipoCarpetaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        return "general/tipocarpeta/tipocarpeta";
    }

    @ResponseBody
    @RequestMapping("allTipoCarpeta")
    public DynatableResponse allTipoCarpeta(DynatableFilter filter, HttpSession session) {

        DynatableResponse responce = new DynatableResponse();

        try {

            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<TipoCarpeta> tipoCarpetas = service.allByDynatable(filter, ds);

            JsonNodeFactory factory = JsonNodeFactory.instance;

            ArrayNode array = new ArrayNode(factory);

            for (TipoCarpeta tipoCarpeta : tipoCarpetas) {
                ObjectNode json = JsonHelper.createJson(tipoCarpeta, factory, true, new String[]{
                    "*"
                });
                array.add(json);
            }

            responce.setData(array);
            responce.setTotal(filter.getTotal());
            responce.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            responce.setTotal(0);
        }

        return responce;
    }

}
