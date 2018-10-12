package pe.edu.lamolina.pivot.controller.tramite.plantillaIncrustacion;

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
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.enums.TipoPlantillaDocumentoEnum;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.PlantillaIncrustacionDocumento;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.zelper.pdf.pdfHtml.PdfHtmlView;

@Controller
@RequestMapping("tramite/plantillainscrustacion")
public class PlantillaIncrustacionController {

    @Autowired
    PlantillaIncrustacionService service;

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

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        ArrayNode tipoPlantilla = new ArrayNode(JsonNodeFactory.instance);
        for (TipoPlantillaDocumentoEnum value : TipoPlantillaDocumentoEnum.values()) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("name", value.name());
            node.put("value", value.getValue());
            tipoPlantilla.add(node);
        }
        model.addAttribute("tipos", tipoPlantilla);
        return "tramite/plantillaIncrustacion/plantillaIncrustacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);
        try {
            List<PlantillaDocumentoAcademico> list = service.all(filter);
            ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
            for (PlantillaDocumentoAcademico plantilla : list) {
                arrayNode.add(JsonHelper.createJson(plantilla, JsonNodeFactory.instance, new String[]{
                    "*",
                    "idioma.*"
                }));
            }
            json.setData(arrayNode);
            json.setTotal(filter.getTotal());
            json.setFiltered(filter.getFiltered());
        } catch (Exception e) {
            e.printStackTrace();
            json.setTotal(0);
        }
        return json;
    }
}
