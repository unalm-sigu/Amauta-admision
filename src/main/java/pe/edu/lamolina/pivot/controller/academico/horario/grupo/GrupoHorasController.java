package pe.edu.lamolina.pivot.controller.academico.horario.grupo;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.model.horario.GrupoHoras;

@Controller
@RequestMapping("academico/horario/grupo")
public class GrupoHorasController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GrupoHorasService service;

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
        return "academico/horario/grupo/grupo";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();

        try {

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<GrupoHoras> grupos = service.allGrupoHoras(filter);

            for (GrupoHoras grupo : grupos) {

                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                node.put("id", grupo.getId());
                node.put("codigo", grupo.getCodigo());
                node.put("letra", grupo.getLetra());
                node.put("tipoCiclo", grupo.getTipoCiclo());
                node.put("tipoGrupoHoras", grupo.getTipoGrupoHoras()!=null?grupo.getTipoGrupoHoras().getCodigo():"");
                node.put("tipoSeccion", grupo.getTipoSeccion());
                node.put("color", grupo.getColor());
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
}
