package pe.edu.lamolina.pivot.controller.comun;

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
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.edu.lamolina.pivot.model.academico.Curso;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;
import pe.edu.lamolina.pivot.zelper.model.DataSession;

@Controller
@RequestMapping("comun/buscar")
public class BuscarController {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    BuscarService buscarService;
    
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
    @RequestMapping("cursosSCA")
    public DynatableResponse cursosSCA(DynatableFilter filter,
            @RequestParam("nombre") String nombre,
            @RequestParam("planCalificacion") Long planCalificacion,
            HttpSession session) {
        DynatableResponse json = new DynatableResponse();
        try {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            DataSession ds = (DataSession) session.getAttribute(Constantine.SESSION_USUARIO);
            logger.debug("el plan calificacion es " + planCalificacion);
            List<Curso> cursos = buscarService.allCursosAutocomplete(nombre, ds.getDepartamentoAcademico().getId(), planCalificacion);
            
            for (Curso curso : cursos) {
                ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
                
                node.put("id", curso.getId());
                node.put("codigo", curso.getCodigo());
                node.put("nombre", curso.getNombre());
                node.put("departamentoAcademico", curso.getDepartamentoAcademico() != null ? curso.getDepartamentoAcademico().getNombre() : "");
                node.put("sistemaCalificacion", curso.getPlanCalificacion() != null ? curso.getPlanCalificacion().getNotaBase().toString() : "");
                node.put("tpc", curso.getTipoCurso());
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
