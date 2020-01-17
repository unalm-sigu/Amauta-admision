package pe.edu.lamolina.pivot.controller.seguridad.verificador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import static pe.edu.lamolina.model.enums.OficinaEnum.BAN;
import static pe.edu.lamolina.model.enums.OficinaEnum.EPG;
import static pe.edu.lamolina.model.enums.OficinaEnum.OERA;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.DPTO;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.ESP;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.FAC;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
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

    public enum CantidadItemsEnum {
        TODOS, PARCIAL, SIN_PERMISO
    };

    @Override
    public void revisarPermiso(HttpServletRequest request, DataSessionPivot ds) {
        if (1 == 2) {
            System.out.println("Buscando permiso para " + obtainPath(request));
            Assert.isNotNull(findMenu(ds.getMenu(), obtainPath(request)), "No tiene permiso para acceder a este recurso");
        }
    }

    @Override
    public CantidadItemsEnum verificarCantidad(TipoOficinaEnum tipoOficinaSolicitud, HttpServletRequest request, DataSessionPivot ds) {
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA) {
                return CantidadItemsEnum.TODOS;
            }
            if (Arrays.asList(OERA, BAN).contains(oficina.getCodigoEnum())) {
                if (tipoOficinaSolicitud == ESP) {
                    return CantidadItemsEnum.TODOS;
                }
            }
        }

        Menu menu = findMenu(ds.getMenu(), obtainPath(request));

        if (menu == null) {
            return CantidadItemsEnum.SIN_PERMISO;
        }

        List<Oficina> oficinas = oficinaDAO.allOficinaByUserMenu(ds.getUsuario(), menu);
        if (oficinas.isEmpty()) {
            return CantidadItemsEnum.SIN_PERMISO;
        }
        return CantidadItemsEnum.PARCIAL;
    }

    @Override
    public List<Object> allInstanciasByMenuRol(TipoOficinaEnum tipoSolicitud, HttpServletRequest request, DataSessionPivot ds) {
        List<Object> lista = new ArrayList();
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA) {
                if (tipoSolicitud == DPTO) {
                    lista.addAll(departamentoAcademicoDAO.all());
                    return lista;
                } else if (tipoSolicitud == FAC) {
                    lista.addAll(facultadDAO.all());
                    return lista;
                }

            }
            if (oficina.getCodigoEnum() == EPG) {
                if (tipoSolicitud == ESP) {
                    lista.addAll(carreraDAO.allPosGrado());
                    return lista;
                }
            }
            if (Arrays.asList(OERA, BAN).contains(oficina.getCodigoEnum())) {
                if (tipoSolicitud == ESP) {
                    lista.addAll(carreraDAO.allPrePosGrado());
                    return lista;
                }
            }
        }

        Menu menu = findMenu(ds.getMenu(), obtainPath(request));
        if (menu == null) {
            menu = findMenu(ds.getMenu(), obtainPath(request, 4));
            if (menu == null) {
                return new ArrayList();
            }
        }

        List<Oficina> oficinas = oficinaDAO.allOficinaByUserMenu(ds.getUsuario(), menu);
        if (oficinas.isEmpty()) {
            return lista;
        }

        List<Carrera> carreras = carreraDAO.allPrePosGrado();
        List<Facultad> facultades = facultadDAO.all();
        List<DepartamentoAcademico> departamentos = departamentoAcademicoDAO.all();
        Map<Long, Carrera> mapCarreras = TypesUtil.convertListToMap("id", carreras);
        Map<Long, List<Carrera>> mapCarrerasByFacultad = TypesUtil.convertListToMapList("facultad.id", carreras);
        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("id", facultades);
        Map<Long, DepartamentoAcademico> mapDepartamento = TypesUtil.convertListToMap("id", departamentos);
        Map<Long, List<DepartamentoAcademico>> mapDepartamentoByFacultad = TypesUtil.convertListToMapList("facultad.id", departamentos);

        for (Oficina oficina : oficinas) {
            if (tipoSolicitud == ESP && oficina.getTipoOficina().getCodigoEnum().getClazz() == Carrera.class) {
                lista.add(mapCarreras.get(oficina.getInstanciaOficina()));

            } else if (tipoSolicitud == ESP && oficina.getTipoOficina().getCodigoEnum().getClazz() == Facultad.class) {
                Facultad facultad = mapFacultad.get(oficina.getInstanciaOficina());
                lista.addAll(TypesUtil.getListNotNull(mapCarrerasByFacultad.get(facultad.getId())));

            } else if (tipoSolicitud == FAC && oficina.getTipoOficina().getCodigoEnum().getClazz() == Carrera.class) {
                // IMPLEMENTAR LOGICA

            } else if (tipoSolicitud == FAC && oficina.getTipoOficina().getCodigoEnum().getClazz() == Facultad.class) {
                lista.add(mapFacultad.get(oficina.getInstanciaOficina()));

            } else if (tipoSolicitud == DPTO && oficina.getTipoOficina().getCodigoEnum().getClazz() == Facultad.class) {
                Facultad facultad = mapFacultad.get(oficina.getInstanciaOficina());
                lista.addAll(TypesUtil.getListNotNull(mapDepartamentoByFacultad.get(facultad.getId())));

            } else if (tipoSolicitud == DPTO && oficina.getTipoOficina().getCodigoEnum().getClazz() == DepartamentoAcademico.class) {
                lista.add(mapDepartamento.get(oficina.getInstanciaOficina()));

            } else if (tipoSolicitud == FAC && oficina.getTipoOficina().getCodigoEnum() == DPTO) {
                // IMPLEMENTAR LOGICA
            }
        }
        return lista;
    }

    private String obtainPath(HttpServletRequest request) {
        return obtainPath(request, 3);
    }

    private String obtainPath(HttpServletRequest request, int pos) {
        String base = request.getServletPath();
        System.out.println("base=" + base);

        int posIndex = StringUtils.ordinalIndexOf(base, "/", pos);

        if (posIndex > 0) {
            return base.substring(0, posIndex);
        } else {
            return base;
        }
    }

    private Menu findMenu(List<Menu> menus, String recurso) {
        System.out.println("recurso:::" + recurso);
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
            Menu menuSup = findMenu(menu.getMenus(), recurso);
            if (menuSup != null) {
                return menuSup;
            }
        }

        return null;
    }

    @Override
    public boolean puedeOperarMatricula(DataSessionPivot ds) {
        boolean puedeOperar = false;
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.OPER_MATRICULA_OERA) {
                puedeOperar = true;
                break;
            }
            if (rol.getCodigoEnum() == RolEnum.IOREA) {
                puedeOperar = true;
                break;
            }
        }
        return puedeOperar;
    }

    @Override
    public boolean puedeMatricularPosgrado(DataSessionPivot ds) {
        List<Carrera> maestrias = carreraDAO.allByModalidadEnum(ModalidadEstudioEnum.EPG);
        Map<Long, Carrera> mapMaestrias = TypesUtil.convertListToMap("id", maestrias);
        boolean puedeApoyar = false;

        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == EPG) {
                return true;
            }
        }

        boolean esTrabajadorMaestria = false;
        for (Oficina oficina : oficinasMain) {
            if (oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP) {
                Carrera maestria = mapMaestrias.get(oficina.getInstanciaOficina());
                if (maestria != null) {
                    esTrabajadorMaestria = true;
                }
            }
        }

        if (esTrabajadorMaestria) {
            for (Rol rol : ds.getRoles()) {
                if (rol.getCodigoEnum() == RolEnum.COORD_ESP_EPG) {
                    puedeApoyar = true;
                    break;
                }
                if (rol.getCodigoEnum() == RolEnum.OPER_MATRICULA_ESP_EPG) {
                    puedeApoyar = true;
                    break;
                }
            }
        }
        return puedeApoyar;
    }

    @Override
    public boolean puedeEditarAlumno(DataSessionPivot ds) {
        boolean puedeEditar = false;
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.EDITOR_ALUMNO_OERA) {
                puedeEditar = true;
                break;
            }
            if (rol.getCodigoEnum() == RolEnum.EDITOR_ALUMNO_EPG) {
                puedeEditar = true;
                break;
            }
            if (rol.getCodigoEnum() == RolEnum.IOREA) {
                puedeEditar = true;
                break;
            }
        }
        return puedeEditar;
    }

    @Override
    public boolean puedeGestionarSuOficina(DataSessionPivot ds) {
        boolean puedeEditar = false;
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.GESTOR_OFICINA) {
                puedeEditar = true;
                break;
            }
            if (rol.getCodigoEnum() == RolEnum.GESTOR_OFICINA_EPG) {
                puedeEditar = true;
                break;
            }
            if (rol.getCodigoEnum() == RolEnum.IOREA) {
                puedeEditar = true;
                break;
            }
        }
        return puedeEditar;
    }

    @Override
    public boolean puedeEditarOficinas(DataSessionPivot ds) {
        boolean puede = false;
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.IOREA) {
                puede = true;
                break;
            }
        }
        return puede;
    }

    @Override
    public boolean puedeVerOficina(Oficina oficinaRevision, DataSessionPivot ds) {
        boolean esGestorOficina = this.puedeGestionarSuOficina(ds);
        if (!esGestorOficina) {
            return false;
        }
        boolean puedeVerOficina = false;
        List<Oficina> oficinasMain = this.allOficinasAcceso(ds);
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OficinaEnum.UNA) {
                puedeVerOficina = true;
                break;
            }
            if (oficina.getId() == oficinaRevision.getId().longValue()) {
                puedeVerOficina = true;
                break;
            }
        }

        return puedeVerOficina;
    }

    @Override
    public List<Oficina> allOficinasAcceso(DataSessionPivot ds) {
        List<Oficina> oficinas = new ArrayList();
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.GESTOR_OFICINA) {
                List<UsuarioRol> usuarioRol = usuarioRolDAO.allWithOfficeByUserRol(ds.getUsuario(), rol);
                for (UsuarioRol ur : usuarioRol) {
                    oficinas.add(ur.getOficina());
                }
            }

            if (rol.getCodigoEnum() == RolEnum.GESTOR_OFICINA_EPG) {
                List<Oficina> direccionesPosgrado = oficinaDAO.allDireccionPosgrado();
                List<Oficina> especialidadesPosgrado = oficinaDAO.allEspecialidadPosgrado();

                oficinas.addAll(direccionesPosgrado);
                oficinas.addAll(especialidadesPosgrado);
            }

            if (rol.getCodigoEnum() == RolEnum.IOREA) {
                Oficina oficinaUNA = oficinaDAO.findByCode(OficinaEnum.UNA.name());
                oficinas.add(oficinaUNA);
            }
        }
        return oficinas;
    }

    @Override
    public String getOrigen(String origen, String defecto) {
        if (StringUtils.isEmpty(origen)) {
            return defecto;
        }
        byte[] decoded = Base64.getMimeDecoder().decode(origen);
        String output = new String(decoded);
        return output;
    }

    @Override
    public Oficina findOficina(Oficina oficina) {
        return oficinaDAO.find(oficina);
    }

    @Override
    public boolean isGestorOficinaEPG(DataSessionPivot ds) {
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.GESTOR_OFICINA_EPG) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEditorEncuestas(DataSessionPivot ds) {
        boolean esTrabajadorOERA = false;
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA) {
                esTrabajadorOERA = true;
            }
        }

        if (esTrabajadorOERA) {
            for (Rol rol : ds.getRoles()) {
                if (rol.getCodigoEnum() == RolEnum.EDITOR_ENCU) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean isRevisorEncuestas(DataSessionPivot ds) {
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.EDITOR_ENCU) {
                return true;
            }
            if (rol.getCodigoEnum() == RolEnum.REVISOR_ENCU) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRevisorCurriculas(DataSessionPivot ds) {
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.EDITOR_CURRICULA) {
                return true;
            }
            if (rol.getCodigoEnum() == RolEnum.EDITOR_CURRICULA_EPG) {
                return true;
            }
            if (rol.getCodigoEnum() == RolEnum.REVISOR_CURRICULA_EPG) {
                return true;
            }
            if (rol.getCodigoEnum() == RolEnum.REVISOR_CURRICULA_PRE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean puedeVerAllFacultades(DataSessionPivot ds, String contexto) {
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA && contexto.equals("ENCUESTA_ESTUDIANTIL")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean puedeVerAllDepartamentos(DataSessionPivot ds, String contexto) {
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA && contexto.equals("ENCUESTA_ESTUDIANTIL")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEditorCurriculas(DataSessionPivot ds) {
        boolean esEditorAll = isEditorCurriculasAll(ds);
        if (esEditorAll) {
            return true;
        }

        return isEditorCurriculasEpg(ds);
    }

    @Override
    public boolean isEditorCurriculasAll(DataSessionPivot ds) {
        boolean esTrabajadorOERA = false;
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA) {
                esTrabajadorOERA = true;
            }
        }

        if (esTrabajadorOERA) {
            for (Rol rol : ds.getRoles()) {
                if (rol.getCodigoEnum() == RolEnum.EDITOR_CURRICULA) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isEditorCurriculasEpg(DataSessionPivot ds) {
        boolean esTrabajadorEPG = false;
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == EPG) {
                esTrabajadorEPG = true;
            }
        }
        if (esTrabajadorEPG) {
            for (Rol rol : ds.getRoles()) {
                if (rol.getCodigoEnum() == RolEnum.EDITOR_CURRICULA_EPG) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<ModalidadEstudio> modalidadesPermitidasForCursos(DataSessionPivot ds, List<ModalidadEstudio> modalidades) {
        boolean esTrabajadorOERA = false;
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA) {
                esTrabajadorOERA = true;
            }
        }

        if (esTrabajadorOERA) {
            for (Rol rol : ds.getRoles()) {
                if (rol.getCodigoEnum() == RolEnum.EDITOR_CURSOS) {
                    return modalidades;
                }
            }
        }

        boolean esTrabajadorEPG = false;
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == EPG) {
                esTrabajadorEPG = true;
            }
        }
        if (esTrabajadorEPG) {
            for (Rol rol : ds.getRoles()) {
                if (rol.getCodigoEnum() == RolEnum.REVISOR_CURSOS_EPG) {
                    List<ModalidadEstudio> modas = new ArrayList();
                    modas.add(modalidades.stream().filter(x -> x.getCodigoEnum() == ModalidadEstudioEnum.EPG).findAny().orElse(null));
                    return modas;
                }
            }
        }
        return null;
    }

    @Override
    public List<Oficina> allOficinasAccesoByRolEnum(DataSessionPivot ds, RolEnum rolEnum) {
        Rol rolUser = null;
        List<Rol> roles = ds.getRoles();
        for (Rol role : roles) {
            if (role.getCodigoEnum() == rolEnum) {
                rolUser = role;
                break;
            }
        }

        List<Oficina> oficinas = new ArrayList();
        if (rolUser == null) {
            return oficinas;
        }

        List<Oficina> oficinasUser = ds.getOficinas();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasUser);
        List<UsuarioRol> userRoles = usuarioRolDAO.allWithOfficeByUserRol(ds.getUsuario(), rolUser);
        List<Carrera> carreras = carreraDAO.all();
        Map<Long, Carrera> mapCarrera = TypesUtil.convertListToMap("id", carreras);

        for (UsuarioRol userRole : userRoles) {
            Oficina ofi = mapOficina.get(userRole.getOficina().getId());
            if (ofi != null) {
                ofi = userRole.getOficina();
                if (ofi.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP) {
                    Carrera carrera = mapCarrera.get(ofi.getInstanciaOficina());
                    if (carrera.getModalidadEstudio().isPostgrado()) {
                        oficinas.add(userRole.getOficina());
                    }
                }
            }
        }

        return oficinas;
    }

}
