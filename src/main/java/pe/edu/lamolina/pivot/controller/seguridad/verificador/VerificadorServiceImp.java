package pe.edu.lamolina.pivot.controller.seguridad.verificador;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.pivot.dao.seguridad.MenuRolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class VerificadorServiceImp implements VerificadorService {

    @Autowired
    MenuRolDAO menuRolDAO;

    @Autowired
    UsuarioRolDAO usuarioRolDAO;

    @Override
    public void revisarPermiso(HttpServletRequest request, DataSessionPivot ds) {
        Assert.isNotNull(findMenu(ds.getMenu(), obtainPath(request)), "No tiene permiso para acceder a este recurso");
    }

    @Override
    public List<Object> allInstanciasByMenuRol(TipoOficinaEnum tipoOficina, HttpServletRequest request, DataSessionPivot ds) {
        Menu menu = findMenu(ds.getMenu(), obtainPath(request));

        if (menu == null) {
            return new ArrayList<>();
        }

        List<Long> instancias = usuarioRolDAO.allInstanciasByUsuarioMenuTipoOficna(ds.getUsuario(), menu, tipoOficina);

        switch (tipoOficina) {
            case FAC:
                return (List) instancias.stream().map(x -> new Facultad(x)).collect(Collectors.toList());
            case OFI:
                return (List) instancias.stream().map(x -> new Oficina(x)).collect(Collectors.toList());
            case DPTO:
                return (List) instancias.stream().map(x -> new DepartamentoAcademico(x)).collect(Collectors.toList());
            default:
                throw new AssertionError();
        }
    }

    private String obtainPath(HttpServletRequest request) {
        String base = request.getServletPath();

        int thirdIndex = StringUtils.ordinalIndexOf(base, "/", 3);

        if (thirdIndex > 0) {
            return base.substring(0, thirdIndex);
        } else {
            return base;
        }
    }

    private Menu findMenu(List<Menu> menus, String recurso) {
        for (Menu menu : menus) {
            if (StringUtils.isBlank(menu.getRuta())) {
                continue;
            }
            String pattern = menu.getRuta() + "(.*)";
            if (recurso.matches(pattern)) {
                return menu;
            }
        }
        for (Menu menu : menus) {
            return findMenu(menu.getMenus(), recurso);
        }

        return null;
    }

}
