package pe.edu.lamolina.amauta.controller.tramite.constanciacertificado.incrustacion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
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
import pe.edu.lamolina.model.enums.TipoPlantillaDocumentoEnum;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.constantines.GlobalMessages;

@Slf4j
@Controller
@RequestMapping("tramite/plantillainscrustacion")
public class PlantillaIncrustacionController {

    @Autowired
    PlantillaIncrustacionService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {

        ArrayNode tipoPlantilla = new ArrayNode(JsonNodeFactory.instance);

        List<Idioma> listIdioma = service.allIdioma();

        for (TipoPlantillaDocumentoEnum value : TipoPlantillaDocumentoEnum.values()) {
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("name", value.name());
            node.put("value", value.getValue());
            tipoPlantilla.add(node);
        }

        model.addAttribute("tipos", tipoPlantilla);
        model.addAttribute("idiomas", JaneHelper.from(listIdioma).array().toString());
        return "tramite/plantillaIncrustacion/plantillaIncrustacion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse all(DynatableFilter filter, HttpSession session) {

        DynatableResponse json = new DynatableResponse();
        json.setTotal(0);
        List<PlantillaDocumentoAcademico> plantillaDocumentoAcademicos = service.all(filter);
        ArrayNode arrayNode = JaneHelper.from(plantillaDocumentoAcademicos)
                .join("idioma").array();
        json.setData(arrayNode);
        json.setTotal(filter.getTotal());
        json.setFiltered(filter.getFiltered());
        return json;
    }

    @ResponseBody
    @RequestMapping("save")
    public String update(@RequestBody PlantillaDocumentoAcademico plantillaDocumentoAcademico, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(GlobalConstantine.SESSION_USUARIO);

        if (plantillaDocumentoAcademico.getId() == null) {
            service.save(plantillaDocumentoAcademico, ds);
            return GlobalMessages.CREATED;
        } else {
            service.update(plantillaDocumentoAcademico, ds);
            return GlobalMessages.UPDATED;
        }
    }

    @ResponseBody
    @RequestMapping("delete/{id}")
    public String delete(@PathVariable(value = "id") Long id, HttpSession session) {
        service.delete(id);
        return GlobalMessages.DELETED;
    }
}
