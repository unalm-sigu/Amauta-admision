package pe.edu.lamolina.amauta.controller.seguridad.menucolaborador;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.joda.time.DateTime;
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
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.model.enums.MenuTipoEnum;
import pe.edu.lamolina.model.seguridad.ColaboradorMenu;
import pe.edu.lamolina.model.seguridad.Menu;

@Controller
@RequestMapping("seguridad/menucolaborador")
public class MenuColaboradorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    MenuColaboradorService service;

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

        List<Menu> reportes = service.allMenuReportes(MenuTipoEnum.BOTON);

        model.addAttribute("reportes", reportes);

        return "seguridad/menucolaborador/menuColaborador";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            List<ColaboradorMenu> menus = service.allMenuColaborador();

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (ColaboradorMenu menu : menus) {

            }
//            for (CicloPostula ciclo : ciclos) {
//                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
//
//                node.put("id", ciclo.getId());
//                
//                array.add(node);
//            }
//            json.setData(array);
//            json.setTotal(filter.getTotal());
//            json.setFiltered(filter.getFiltered());

        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }

    private String getFechaString(Date fecha) {
        if (fecha == null) {
            return null;
        }
        DateTime dt = new DateTime(fecha);
        return dt.toString("dd/MM/yyyy");
    }

}
