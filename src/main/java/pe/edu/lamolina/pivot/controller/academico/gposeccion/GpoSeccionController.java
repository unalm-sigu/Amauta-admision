package pe.edu.lamolina.pivot.controller.academico.gposeccion;

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
import pe.edu.lamolina.pivot.model.academico.CicloAcademico;
import pe.edu.lamolina.pivot.model.academico.DocenteSeccion;
import pe.edu.lamolina.pivot.model.academico.GrupoSeccion;
import pe.edu.lamolina.pivot.model.academico.Seccion;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.enums.EstadoEnum;
import pe.edu.lamolina.pivot.zelper.enums.EstadoGrupoSeccionEnum;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Controller
@RequestMapping("academico/gposeccion")
public class GpoSeccionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GpoSeccionService service;

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
        CicloAcademico ciclo = ds.getCicloAcademico();
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("resumen", service.resumen());
        return "academico/gposeccion/gpoSeccion";
    }

    @ResponseBody
    @RequestMapping("list")
    public DynatableResponse allByDynatable(DynatableFilter filter, HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            DataSessionPivot ds = (DataSessionPivot) session.getAttribute(Constantine.SESSION_USUARIO);

            List<GrupoSeccion> gpoSecciones = service.allByDynatable(filter, ds.getCicloAcademico());

            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);

            for (GrupoSeccion gpoSeccion : gpoSecciones) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);

                node.put("id", gpoSeccion.getId());
                node.put("curso", gpoSeccion.getCurso().getNombre());
                node.put("codigo", gpoSeccion.getCodigo());
                node.put("teoria", gpoSeccion.getCurso().getHorasTeoria());
                node.put("practica", gpoSeccion.getCurso().getHorasPractica());
                node.put("creditos", gpoSeccion.getCurso().getCreditos());
                node.put("anexo", gpoSeccion.getAnexoBoletin().getNombre());
                node.put("estado", gpoSeccion.getEstado());
                node.put("estadoValue", gpoSeccion.getEstado() != null ? EstadoEnum.valueOf(gpoSeccion.getEstado()).getValue() : "");

                ArrayNode secciones = new ArrayNode(JsonNodeFactory.instance);
                for (Seccion seccion : gpoSeccion.getSecciones()) {
                    ObjectNode node2 = new ObjectNode(JsonNodeFactory.instance);
                    node2.put("tipo", seccion.getTipoSeccion());
                    node2.put("tipoValue", seccion.getTipoSeccionEnum().getValue());
                    node2.put("codigo", seccion.getCodigo());
                    node2.put("estadoSec", seccion.getEstado());
                    node2.put("estadoValueSec", seccion.getEstadoEnum().getValue());
                    secciones.add(node2);

                    ArrayNode arrayDocentes = new ArrayNode(JsonNodeFactory.instance);
                    for (DocenteSeccion docSeccion : seccion.getDocenteSeccion()) {
                        ObjectNode node3 = new ObjectNode(JsonNodeFactory.instance);
                        node3.put("codigo", docSeccion.getDocente().getCodigo());
                        node3.put("docente", docSeccion.getDocente().getPersona().getApellidosNombres());
                        arrayDocentes.add(node3);
                    }
                    node2.set("docentes", arrayDocentes);
                }

                node.set("secciones", secciones);
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
