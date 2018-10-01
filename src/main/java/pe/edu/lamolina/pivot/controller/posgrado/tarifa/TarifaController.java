package pe.edu.lamolina.pivot.controller.posgrado.tarifa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("posgrado/tarifa")
public class TarifaController {
    
    @Autowired
    TarifaService service;
    
    
}
