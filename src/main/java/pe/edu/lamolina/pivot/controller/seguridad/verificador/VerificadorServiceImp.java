package pe.edu.lamolina.pivot.controller.seguridad.verificador;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerMapping;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.pivot.dao.inscripcion.PrelamolinaDAO;
import pe.edu.lamolina.pivot.model.seguridad.Menu;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class VerificadorServiceImp implements VerificadorService {

    @Autowired
    PrelamolinaDAO prelamolinaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void revisarPermiso(HttpServletRequest request, DataSessionPivot ds) {
        String mapping = request.getServletPath();
        logger.debug("MAPPING = [[{}]]", mapping);
        String pattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        logger.debug("PATTERN = [[{}]]", pattern);

        if (existeMenu(ds.getMenu(), mapping)) {
            return;
        }
        throw new PhobosException("No tiene permiso para acceder a este recurso");

    }

    private boolean existeMenu(List<Menu> menus, String recurso) {
        for (Menu menu : menus) {
            if (StringUtils.isBlank(menu.getRuta())) {
                continue;
            }

            String pattern = menu.getRuta() + "(.*)";
            logger.debug("el pattern de comparacion es {}", pattern);
            if (recurso.matches(pattern)) {
                return true;
            }
        }
        for (Menu menu : menus) {
            if (existeMenu(menu.getMenus(), recurso)) {
                return true;
            }
        }
        return false;
    }

}
