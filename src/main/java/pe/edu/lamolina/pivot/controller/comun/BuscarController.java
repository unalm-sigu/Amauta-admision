package pe.edu.lamolina.pivot.controller.comun;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;

@Controller
@RequestMapping("comun/buscar")
public class BuscarController {

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

    @ResponseBody
    @RequestMapping("cursos")
    public DynatableResponse list(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            String noTiene = "NO TIENE";
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 5);
                node.put("codigo", "CC2389");
                node.put("nombre", "Cálculo Diferencial");
                node.put("departamentoAcademico", "Matemáticas");
                node.put("sistemaCalificacion", "SC-0025");
                node.put("tpc", "2-2-3");
                array.add(node);
            }
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 34);
                node.put("codigo", "MT5241");
                node.put("nombre", "Geometría Descriptiva");
                node.put("departamentoAcademico", "Matemáticas");
                node.put("sistemaCalificacion", noTiene);
                node.put("tpc", "2-2-3");
                array.add(node);
            }
            {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", 55);
                node.put("codigo", "AF7214");
                node.put("nombre", "Matemáticas III");
                node.put("departamentoAcademico", "Matemáticas");
                node.put("sistemaCalificacion", noTiene);
                node.put("tpc", "3-2-4");
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
