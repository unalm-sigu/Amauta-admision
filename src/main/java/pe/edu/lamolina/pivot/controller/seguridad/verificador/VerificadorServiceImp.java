package pe.edu.lamolina.pivot.controller.seguridad.verificador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import static pe.edu.lamolina.model.enums.OficinaEnum.BAN;
import static pe.edu.lamolina.model.enums.OficinaEnum.OERA;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.DPTO;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.ESP;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.FAC;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.pivot.controller.general.oficina.OficinaService;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
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

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    OficinaService oficinaService;

    @Override
    public void revisarPermiso(HttpServletRequest request, DataSessionPivot ds) {
        if (1 == 2) {
            System.out.println("Buscando permiso para " + obtainPath(request));
            Assert.isNotNull(findMenu(ds.getMenu(), obtainPath(request)), "No tiene permiso para acceder a este recurso");
        }
    }

    @Override
    public List<Object> allInstanciasByMenuRol(TipoOficinaEnum tipoOficinaSolicitud, HttpServletRequest request, DataSessionPivot ds) {
        List<Object> lista = new ArrayList();
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        System.out.println("oficinas:::" + oficinasMain.size());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA) {
                if (tipoOficinaSolicitud == DPTO) {
                    lista.addAll(departamentoAcademicoDAO.all());
                    return lista;
                } else if (tipoOficinaSolicitud == FAC) {
                    lista.addAll(facultadDAO.all());
                    return lista;
                }

            }
            if (Arrays.asList(OERA, BAN).contains(oficina.getCodigoEnum())) {
                if (tipoOficinaSolicitud == ESP) {
                    lista.addAll(carreraDAO.allPrePosGrado());
                    return lista;
                }

            }
        }

        Menu menu = findMenu(ds.getMenu(), obtainPath(request));

        if (menu == null) {
            System.out.println("No tiene acceso a ningun menu");
            return new ArrayList();
        }

        List<Oficina> oficinas = oficinaDAO.allOficinaByUserMenu(ds.getUsuario(), menu);
        System.out.println("cantidad Oficinas " + oficinas.size());
        if (oficinas.isEmpty()) {
            return lista;
        }

        List<Carrera> carreras = carreraDAO.all();
        List<Facultad> facultades = facultadDAO.all();
        List<DepartamentoAcademico> departamentos = departamentoAcademicoDAO.all();
        Map<Long, Carrera> mapCarreras = TypesUtil.convertListToMap("id", carreras);
        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("id", facultades);
        Map<Long, DepartamentoAcademico> mapDepartamento = TypesUtil.convertListToMap("id", departamentos);

        for (Oficina oficina : oficinas) {
            if (tipoOficinaSolicitud == ESP && oficina.getTipoOficina().getCodigoEnum() == ESP) {
                lista.add(mapCarreras.get(oficina.getInstanciaOficina()));

            } else if (tipoOficinaSolicitud == FAC && oficina.getTipoOficina().getCodigoEnum() == ESP) {
                // IMPLEMENTAR LOGICA
                //lista.addAll(facultadDAO.all());

            } else if (tipoOficinaSolicitud == FAC && oficina.getTipoOficina().getCodigoEnum() == FAC) {
                lista.add(mapFacultad.get(oficina.getInstanciaOficina()));

            } else if (tipoOficinaSolicitud == DPTO && oficina.getTipoOficina().getCodigoEnum() == DPTO) {
                lista.add(mapDepartamento.get(oficina.getInstanciaOficina()));

            } else if (tipoOficinaSolicitud == FAC && oficina.getTipoOficina().getCodigoEnum() == DPTO) {
                // IMPLEMENTAR LOGICA
                //lista.addAll(facultadDAO.all());
            }
        }
        return lista;
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
            System.out.println("verificando " + menu.getRuta());
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
