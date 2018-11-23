package pe.edu.lamolina.pivot.controller.general.inventarioaula;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.lamolina.model.general.Aula;

@Controller
@RequestMapping("general/aula/inventario")
public class InventarioAulaController {

    @Autowired
    InventarioAulaService service;

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

    @RequestMapping("{idaula}")
    public String index(@PathVariable("idaula") Long idaula, Model model, HttpSession session) {
        Aula aula = service.findAula(idaula);
        model.addAttribute("aula", aula);
        return "general/inventarioaula/inventarioaula";
    }

    @RequestMapping("{idaula}/resumen")
    public String resumen(@PathVariable("idaula") Long idaula, Model model, HttpSession session) {
        Aula aula = service.findAula(idaula);
        model.addAttribute("aula", aula);
        return "general/inventarioaula/inventarioaularesumen";
    }

}
