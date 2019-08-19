package pe.edu.lamolina.pivot.controller.programacionhorarios.gposeccion.aula;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonHelper;
import pe.edu.lamolina.model.almacen.ResumenInventario;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion/secciones")
public class ProgramacionAulasController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProgramacionAulaService service;

    @RequestMapping(method = RequestMethod.GET)
    public String index(Model model, HttpSession session) {
        DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

        return "posgrado/tarifa/tarifa";
    }

    @ResponseBody
    @RequestMapping("listSeccionesSinAula")
    public DynatableResponse listAulasSinHorario(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<Aula> aulas = service.allAulasSinHorarioDyna(filter);
            JsonNodeFactory jFactory = JsonNodeFactory.instance;

            ArrayNode array = new ArrayNode(jFactory);

            for (Aula aula : aulas) {
                ObjectNode node = JsonHelper.createJson(aula, jFactory, true, new String[]{
                    "id", "codigo", "nombre", "tipoAmbienteEnum", "tipoAmbiente", "piso", "pisos",
                    "aforo", "capacidadAula", "estado", "estadoEnum", "motivoAnulacion",
                    "aulaSuperior.id", "aulaSuperior.nombre",
                    "sede.id", "sede.nombre",
                    "tipoAula.id", "tipoAula.nombre",
                    "tipoCarpeta.id", "tipoCarpeta.nombre",
                    "oficinaSupervisora.id", "oficinaSupervisora.nombre"
                });

                node.put("aulasContenido", aula.getAulasContenido().size());

                ArrayNode arrayHijas = new ArrayNode(jFactory);
                List<Aula> aulasHijas = aula.getAulasContenido();
                for (Aula aulaHija : aulasHijas) {
                    ObjectNode nodeHija = JsonHelper.createJson(aulaHija, jFactory, true, new String[]{"id", "codigo", "nombre"});
                    arrayHijas.add(nodeHija);
                }
                node.set("aulasHijas", arrayHijas);

                ArrayNode inventariosHijas = new ArrayNode(jFactory);
                List<ResumenInventario> inventarios = aula.getInventario();
                logger.debug("aula {} items {}", aula.getId(), inventarios != null ? inventarios.size() : 0);
                if (inventarios != null) {
                    for (ResumenInventario inventario : inventarios) {
                        ObjectNode jinventario = JsonHelper.createJson(inventario, jFactory, new String[]{"*", "producto.*"});
                        inventariosHijas.add(jinventario);
                    }
                }
                node.set("inventarios", inventariosHijas);
                node.put("cantidadinventarios", inventariosHijas.size());

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
