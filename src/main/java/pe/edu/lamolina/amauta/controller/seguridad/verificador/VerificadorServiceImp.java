package pe.edu.lamolina.amauta.controller.seguridad.verificador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.AnexoBoletin;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CodigoAnexoBoletinEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.DPTO;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.ESP;
import static pe.edu.lamolina.model.enums.TipoOficinaEnum.FAC;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.seguridad.Menu;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.amauta.dao.academico.AnexoBoletinDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.controller.general.oficina.util.OficinaService;
import pe.edu.lamolina.amauta.dao.consejeria.ConsejeroDAO;
import pe.edu.lamolina.model.consejeria.Consejero;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.ASOERA;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.BAN;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.DEPACT;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.DEPFIS;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.EPG;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.OBUAE;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.OERA;
import static pe.edu.lamolina.model.enums.oficina.OficinaEnum.VACA;
import pe.edu.lamolina.model.general.Persona;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class VerificadorServiceImp implements VerificadorService {

    private final AnexoBoletinDAO anexoBoletinDAO;
    private final CarreraDAO carreraDAO;
    private final ConsejeroDAO consejeroDAO;
    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private final FacultadDAO facultadDAO;
    private final OficinaDAO oficinaDAO;
    private final UsuarioRolDAO usuarioRolDAO;

    private final OficinaService oficinaService;

    public enum CantidadItemsEnum {
        TODOS, PARCIAL, SIN_PERMISO
    };

    @Override
    public String generateCodeRequest() {
        return RandomStringUtils.randomAlphanumeric(7);
    }

    @Override
    public boolean isOperadorActaNotas(DataSessionPivot ds) {
        return this.esTrabajadorOeraConRol(RolEnum.OPER_ACTANOTAS_OERA, ds);
    }

    @Override
    public boolean isOperadorGastoPosgrado(DataSessionPivot ds) {
        return this.esTrabajadorEpgConRol(RolEnum.OPER_GASTOS_EPG, ds);
    }

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
            if (oficina.getCodigoEnum() == VACA || oficina.getCodigoEnum() == OERA) {
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
    public List<Object> allInstanciasByMenuRol(TipoOficinaEnum tipoSolicitud, HttpServletRequest request, DataSessionPivot ds, String codeRequest) {
        log.info("RQ={} iniclio allInstanciasByMenuRol", codeRequest);

        List<Object> lista = new ArrayList();
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());

        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA || oficina.getCodigoEnum() == VACA) {
                if (tipoSolicitud == DPTO) {
                    lista.addAll(departamentoAcademicoDAO.all());
                    return lista;
                } else if (tipoSolicitud == FAC) {
                    lista.addAll(facultadDAO.all());
                    return lista;
                }

            }
            if (oficina.getCodigoEnum() == EPG) {
                if (tipoSolicitud == DPTO) {
                    lista.addAll(departamentoAcademicoDAO.all());
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

        List<Carrera> carrerasPregrado = carreraDAO.allPreGrado();
        List<Carrera> carrerasPosgrado = carreraDAO.allPosGrado();
        List<Facultad> facultades = facultadDAO.all();
        List<DepartamentoAcademico> departamentos = departamentoAcademicoDAO.all();
        Map<Long, Carrera> mapCarrerasPregado = TypesUtil.convertListToMap("id", carrerasPregrado);
        Map<Long, Carrera> mapCarrerasPosgado = TypesUtil.convertListToMap("id", carrerasPosgrado);
        Map<Long, List<Carrera>> mapCarrerasPregradoByFacultad = TypesUtil.convertListToMapList("facultad.id", carrerasPregrado);
        Map<Long, List<Carrera>> mapCarrerasPosgradoByFacultad = TypesUtil.convertListToMapList("facultad.id", carrerasPosgrado);
        Map<Long, Facultad> mapFacultad = TypesUtil.convertListToMap("id", facultades);
        Map<Long, DepartamentoAcademico> mapDepartamento = TypesUtil.convertListToMap("id", departamentos);
        Map<Long, List<DepartamentoAcademico>> mapDepartamentoByFacultad = TypesUtil.convertListToMapList("facultad.id", departamentos);

        log.info("RQ={} tipoSolicitud={} listaInicial.size={}", codeRequest, tipoSolicitud, lista.size());

        for (Oficina oficina : oficinas) {
            TipoOficinaEnum tipoOficinaEnum = oficina.getTipoOficina().getCodigoEnum();
            log.info("RQ={} tipoOficinaEnum={}", codeRequest, tipoOficinaEnum.getClazz());

            if (tipoSolicitud == ESP && tipoOficinaEnum.getClazz() == Carrera.class) {
                Carrera carreraPregrado = mapCarrerasPregado.get(oficina.getInstanciaOficina());
                if (carreraPregrado != null) {
                    log.info("RQ={} agregando-carrera-pregado={}", codeRequest, carreraPregrado.getCodigo());
                    lista.add(carreraPregrado);
                }
                Carrera carreraPosgrado = mapCarrerasPosgado.get(oficina.getInstanciaOficina());
                if (carreraPosgrado != null) {
                    log.info("RQ={} agregando-carrera-posgado={}", codeRequest, carreraPosgrado.getCodigo());
                    lista.add(carreraPosgrado);
                }

            } else if (tipoSolicitud == ESP && tipoOficinaEnum.getClazz() == Facultad.class) {
                Facultad facultad = mapFacultad.get(oficina.getInstanciaOficina());
                if (facultad != null) {
                    List<Carrera> carrerasPre = TypesUtil.getListNotNull(mapCarrerasPregradoByFacultad.get(facultad.getId()));
                    List<Carrera> carrerasEpg = TypesUtil.getListNotNull(mapCarrerasPosgradoByFacultad.get(facultad.getId()));
                    lista.addAll(carrerasPre);
                    lista.addAll(carrerasEpg);
                    log.info("RQ={} agregando-count-carreras-pre={} desde-facultad={}", codeRequest, carrerasPre.size(), facultad.getCodigo());
                    log.info("RQ={} agregando-count-carreras-epg={} desde-facultad={}", codeRequest, carrerasEpg.size(), facultad.getCodigo());
                }

            } else if (tipoSolicitud == FAC && tipoOficinaEnum.getClazz() == Carrera.class) {
                log.info("RQ={} sin-implementar clazz={}", codeRequest, tipoOficinaEnum.getClazz());
                // IMPLEMENTAR LOGICA

            } else if (tipoSolicitud == FAC && tipoOficinaEnum.getClazz() == Facultad.class) {
                Facultad facultad = mapFacultad.get(oficina.getInstanciaOficina());
                if (facultad != null) {
                    lista.add(facultad);
                    log.info("RQ={} agregando-facultad={}", codeRequest, facultad.getCodigo());
                }

            } else if (tipoSolicitud == DPTO && tipoOficinaEnum.getClazz() == Facultad.class) {
                Facultad facultad = mapFacultad.get(oficina.getInstanciaOficina());
                if (facultad != null) {
                    List<DepartamentoAcademico> dptos = TypesUtil.getListNotNull(mapDepartamentoByFacultad.get(facultad.getId()));
                    lista.addAll(dptos);
                    log.info("RQ={} agregando-count-dptos={} desde-facultad={}", codeRequest, dptos.size(), facultad.getCodigo());
                }

            } else if (tipoSolicitud == DPTO && tipoOficinaEnum.getClazz() == DepartamentoAcademico.class) {
                DepartamentoAcademico dpto = mapDepartamento.get(oficina.getInstanciaOficina());
                if (dpto != null) {
                    lista.add(dpto);
                    log.info("RQ={} agregando-dpto-academico={}", codeRequest, dpto.getCodigo());
                }

            } else if (tipoSolicitud == FAC && oficina.getTipoOficina().getCodigoEnum() == DPTO) {
                log.info("RQ={} sin-implementar clazz={}", codeRequest, tipoOficinaEnum.getClazz());
                // IMPLEMENTAR LOGICA
            }
        }
        log.info("listaFinal.size={}", lista.size());
        return lista;
    }

    private String obtainPath(HttpServletRequest request) {
        return obtainPath(request, 3);
    }

    private String obtainPath(HttpServletRequest request, int pos) {
        String base = request.getServletPath();

        int posIndex = StringUtils.ordinalIndexOf(base, "/", pos);

        if (posIndex > 0) {
            return base.substring(0, posIndex);
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
            Menu menuSup = findMenu(menu.getMenus(), recurso);
            if (menuSup != null) {
                return menuSup;
            }
        }

        return null;
    }

    @Override
    public boolean puedeOperarMatricula(DataSessionPivot ds) {
        boolean puedeOperar = this.esTrabajadorOeraConRol(RolEnum.OPER_MATRICULA_OERA, ds);
        if (puedeOperar) {
            return puedeOperar;
        }

        puedeOperar = this.esTrabajadorOeraConRol(RolEnum.IOREA, ds);
        if (puedeOperar) {
            return puedeOperar;
        }

        Oficina areaSistemasOera = oficinaDAO.findByCode(ASOERA.name());
        return this.esJefeOrEncargadoOficina(areaSistemasOera, ds);
    }

    @Override
    public boolean puedeMatricularPosgrado(DataSessionPivot ds) {
        boolean puedeMatricular = this.esTrabajadorEpgConRol(RolEnum.OPER_MATRICULA_EPG, ds);
        if (puedeMatricular) {
            return puedeMatricular;
        }

        List<Carrera> maestrias = carreraDAO.allByModalidadEnum(ModalidadEstudioEnum.EPG);
        Map<Long, Carrera> mapMaestrias = TypesUtil.convertListToMap("id", maestrias);

        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        Optional<Oficina> oficinaMaestria = oficinasMain.stream()
                .filter(oficina -> oficina.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP)
                .filter(oficina -> mapMaestrias.get(oficina.getInstanciaOficina()) != null)
                .filter(maestria -> this.puedeMatricularMaestria(maestria, ds))
                .findFirst();

        return oficinaMaestria.isPresent();
    }

    private boolean puedeMatricularMaestria(Oficina maestria, DataSessionPivot ds) {
        boolean puedeMatricular = this.esTrabajadorOficinaConRol(maestria, RolEnum.OPER_MATRICULA_ESP_EPG, ds);
        if (puedeMatricular) {
            return puedeMatricular;
        }
        puedeMatricular = this.esTrabajadorOficinaConRol(maestria, RolEnum.COORD_ESP_EPG, ds);
        return puedeMatricular;
    }

    @Override
    public boolean puedeEditarAlumno(DataSessionPivot ds) {
        boolean puedeEditar = this.esTrabajadorOeraConRol(RolEnum.EDITOR_ALUMNO_OERA, ds);
        if (puedeEditar) {
            return puedeEditar;
        }

        puedeEditar = this.esTrabajadorEpgConRol(RolEnum.EDITOR_ALUMNO_EPG, ds);
        if (puedeEditar) {
            return puedeEditar;
        }

        puedeEditar = this.esTrabajadorOeraConRol(RolEnum.IOREA, ds);
        if (puedeEditar) {
            return puedeEditar;
        }

        Oficina areaSistemasOERA = oficinaDAO.findByCode(ASOERA.name());
        return this.esJefeOrEncargadoOficina(areaSistemasOERA, ds);
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
            if (rol.getCodigoEnum() == RolEnum.ADMINISTRADOR_TUTORIA) {
                puedeEditar = true;
                break;
            }
        }
        return puedeEditar;
    }

    @Override
    public boolean puedeEditarOficinas(DataSessionPivot ds) {
        boolean puedeEditar = this.esTrabajadorOeraConRol(RolEnum.IOREA, ds);
        if (puedeEditar) {
            return puedeEditar;
        }

        Oficina areaSistemasOERA = oficinaDAO.findByCode(ASOERA.name());
        return this.esJefeOrEncargadoOficina(areaSistemasOERA, ds);
    }

    @Override
    public boolean esCoordinadorIOREA(DataSessionPivot ds) {
        boolean puedeEditar = this.esTrabajadorOeraConRol(RolEnum.IOREA, ds);
        if (puedeEditar) {
            return puedeEditar;
        }
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.COORD_TUTO) {
                return true;
            }
        }
        return false;
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

        {
            boolean esGestorEPG = this.esTrabajadorEpgConRol(RolEnum.GESTOR_OFICINA_EPG, ds);
            if (esGestorEPG) {
                log.info("-Usuario {} tiene el rol {} en la EPG", ds.getUsuario().getId(), RolEnum.GESTOR_OFICINA_EPG.name());
                List<Oficina> direccionesPosgrado = oficinaDAO.allDireccionPosgrado();
                List<Oficina> especialidadesPosgrado = oficinaDAO.allEspecialidadPosgrado();

                oficinas.add(new Oficina(EPG));
                oficinas.addAll(direccionesPosgrado);
                oficinas.addAll(especialidadesPosgrado);

            } else {
                log.info("-Usuario {} no tiene el rol {} en la EPG", ds.getUsuario().getId(), RolEnum.GESTOR_OFICINA_EPG.name());
            }
        }

        {
            boolean esInformaticoOERA = this.esTrabajadorOeraConRol(RolEnum.IOREA, ds);
            if (!esInformaticoOERA) {
                Oficina areaSistemasOERA = oficinaDAO.findByCode(ASOERA.name());
                esInformaticoOERA = this.esJefeOrEncargadoOficina(areaSistemasOERA, ds);
            }

            if (esInformaticoOERA) {
                Oficina oficinaUNA = oficinaDAO.findByCode(OficinaEnum.UNA.name());
                oficinas.add(oficinaUNA);

            } else {
                log.info("-Usuario {} no tiene el rol {} en OERA", ds.getUsuario().getId(), RolEnum.IOREA.name());
            }
        }

        {
            boolean esGestorOBU = this.esTrabajadorObuaeConRol(RolEnum.INF_OBUAE, ds);
            if (esGestorOBU) {
                log.info("-Usuario {} tiene el rol {} en la OBUAE", ds.getUsuario().getId(), RolEnum.INF_OBUAE.name());
                Oficina depFis = new Oficina(DEPFIS);
                Oficina depAct = new Oficina(DEPACT);

                oficinas.add(new Oficina(OBUAE));
                oficinas.add(depFis);
                oficinas.add(depAct);

            } else {
                log.info("-Usuario {} no tiene el rol {} en OBUAE", ds.getUsuario().getId(), RolEnum.INF_OBUAE.name());
            }
        }

        {
            boolean esAdministradorTutor = this.esAdministradorTutor(RolEnum.ADMINISTRADOR_TUTORIA, ds);

            if (esAdministradorTutor) {
                log.info("-Usuario {} tiene el rol {} de ADMINISTRADOR_TUTOR", ds.getUsuario().getId(), RolEnum.ADMINISTRADOR_TUTORIA);
                List<Oficina> oficinasTutoria = oficinaDAO.allCoordinacionTutoria();
                oficinas.addAll(oficinasTutoria);

            } else {
                log.info("-Usuario {} no tiene el rol {} de ADMINISTRADOR_TUTOR", ds.getUsuario().getId(), RolEnum.ADMINISTRADOR_TUTORIA);
            }
        }

        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == EPG) {
                continue;
            }
            if (oficina.getCodigoEnum() == OERA) {
                continue;
            }
            if (oficina.getCodigoEnum() == OBUAE) {
                continue;
            }
            boolean esGestorOficina = this.esTrabajadorOficinaConRol(oficina, RolEnum.GESTOR_OFICINA, ds);
            if (esGestorOficina) {
                oficinas.add(oficina);
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
        return this.esTrabajadorEpgConRol(RolEnum.GESTOR_OFICINA_EPG, ds);
    }

    @Override
    public boolean isEditorEncuestas(DataSessionPivot ds) {
        return this.esTrabajadorOeraConRol(RolEnum.EDITOR_ENCU, ds);
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
            if (rol.getCodigoEnum() == RolEnum.EDITOR_CURRICULA_ESP_EPG) {
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
        return this.esTrabajadorOeraConRol(RolEnum.EDITOR_CURRICULA, ds);
    }

    @Override
    public boolean isEditorCurriculasEpg(DataSessionPivot ds) {
        boolean esEditor = this.esTrabajadorEpgConRol(RolEnum.EDITOR_CURRICULA_EPG, ds);
        if (esEditor) {
            return esEditor;
        }

        List<UsuarioRol> userRolesAll = usuarioRolDAO.allActivosByUser(ds.getUsuario());
        if (userRolesAll.isEmpty()) {
            return false;
        }

        List<UsuarioRol> userRoles = new ArrayList();
        for (UsuarioRol userRol : userRolesAll) {
            Oficina oficinaUser = userRol.getOficina();
            if (oficinaUser == null) {
                continue;
            }
            userRoles.add(userRol);
        }

        Map<Long, Oficina> mapOficinaMain = new HashMap();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = oficinaService.findOficinaMain(userRol.getOficina());
            mapOficinaMain.put(userRol.getOficina().getId(), oficinaMain);
        }

        List<Carrera> carrerasAll = carreraDAO.all();
        List<Carrera> carrerasPosgrado = carrerasAll.stream()
                .filter(x -> x.getModalidadEstudio().isPostgrado())
                .collect(Collectors.toList());
        Map<Long, Carrera> mapCarreraPosgrado = TypesUtil.convertListToMap("id", carrerasPosgrado);

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();
            Carrera carreraPosgrado = mapCarreraPosgrado.get(oficinaMain.getInstanciaOficina());

            boolean esRolMaestria = rol.getCodigoEnum() == RolEnum.OPER_PROGH_ESP_EPG;
            boolean esOficinaMaestria = oficinaMain.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP;
            boolean esCarreraPosgrado = carreraPosgrado != null;
            if (esRolMaestria && esOficinaMaestria && esCarreraPosgrado) {
                return true;
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

    @Override
    public List<AnexoBoletin> anexosSuperioresByOficina(DataSessionPivot ds) {
        List<UsuarioRol> userRolesAll = usuarioRolDAO.allActivosByUser(ds.getUsuario());
        if (userRolesAll.isEmpty()) {
            return new ArrayList();
        }

        List<UsuarioRol> userRoles = new ArrayList();
        for (UsuarioRol userRol : userRolesAll) {
            Oficina oficinaUser = userRol.getOficina();
            if (oficinaUser == null) {
                continue;
            }
            userRoles.add(userRol);
        }

        Map<Long, Oficina> mapOficinaMain = new HashMap();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = oficinaService.findOficinaMain(userRol.getOficina());
            mapOficinaMain.put(userRol.getOficina().getId(), oficinaMain);
        }

        List<AnexoBoletin> anexosAll = anexoBoletinDAO.allAnexosSuperiores();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esORolOERA = rol.getCodigoEnum() == RolEnum.OPER_PROGH_OERA;
            boolean esOficinaOERA = oficinaMain.getCodigoEnum() == OERA;
            if (esORolOERA && esOficinaOERA) {
                return anexosAll;
            }
        }

        List<AnexoBoletin> anexos = new ArrayList();
        Map<Long, AnexoBoletin> mapAnexos = new HashMap();

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esRolEPG = rol.getCodigoEnum() == RolEnum.OPER_PROGH_EPG || rol.getCodigoEnum() == RolEnum.REVISOR_PROGH_EPG;
            boolean esOficinaEPG = oficinaMain.getCodigoEnum() == EPG;
            if (esRolEPG && esOficinaEPG) {
                AnexoBoletin anexoNew = new AnexoBoletin(CodigoAnexoBoletinEnum.G04);
                AnexoBoletin anexoOld = mapAnexos.get(anexoNew.getId());
                if (anexoOld == null) {
                    anexos.add(anexoNew);
                    mapAnexos.put(anexoNew.getId(), anexoNew);
                }
            }
        }

        List<Carrera> carrerasAll = carreraDAO.all();
        List<Carrera> carrerasPosgrado = carrerasAll.stream()
                .filter(x -> x.getModalidadEstudio().isPostgrado())
                .collect(Collectors.toList());
        Map<Long, Carrera> mapCarreraPosgrado = TypesUtil.convertListToMap("id", carrerasPosgrado);

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();
            Carrera carreraPosgrado = mapCarreraPosgrado.get(oficinaMain.getInstanciaOficina());

            boolean esRolMaestria = rol.getCodigoEnum() == RolEnum.OPER_PROGH_ESP_EPG;
            boolean esOficinaMaestria = oficinaMain.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP;
            boolean esCarreraPosgrado = carreraPosgrado != null;
            if (esRolMaestria && esOficinaMaestria && esCarreraPosgrado) {
                AnexoBoletin anexoNew = new AnexoBoletin(CodigoAnexoBoletinEnum.G04);
                AnexoBoletin anexoOld = mapAnexos.get(anexoNew.getId());
                if (anexoOld == null) {
                    anexos.add(anexoNew);
                    mapAnexos.put(anexoNew.getId(), anexoNew);
                }
            }
        }

        return anexos;
    }

    @Override
    public List<AnexoBoletin> anexosInferioresByOficina(DataSessionPivot ds, List<AnexoBoletin> anexosAll) {
        List<UsuarioRol> userRolesAll = usuarioRolDAO.allActivosByUser(ds.getUsuario());
        if (userRolesAll.isEmpty()) {
            return new ArrayList();
        }

        List<UsuarioRol> userRoles = new ArrayList();
        for (UsuarioRol userRol : userRolesAll) {
            Oficina oficinaUser = userRol.getOficina();
            if (oficinaUser == null) {
                continue;
            }
            userRoles.add(userRol);
        }

        Map<Long, Oficina> mapOficinaMain = new HashMap();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = oficinaService.findOficinaMain(userRol.getOficina());
            mapOficinaMain.put(userRol.getOficina().getId(), oficinaMain);
        }

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esORolOERA = rol.getCodigoEnum() == RolEnum.OPER_PROGH_OERA;
            boolean esOficinaOERA = oficinaMain.getCodigoEnum() == OERA;
            if (esORolOERA && esOficinaOERA) {
                return anexosAll;
            }
        }

        //Map<Long, AnexoBoletin> mapAnexoByDpto = TypesUtil.convertListToMap("departamentoAcademico.id", anexosAll);
        Map<Long, AnexoBoletin> mapAnexoByCarrera = TypesUtil.convertListToMap("carrera.id", anexosAll);
        //Map<Long, List<AnexoBoletin>> mapAnexoByFacultad = TypesUtil.convertListToMapList("carrera.facultad.id", anexosAll);
        Map<String, List<AnexoBoletin>> mapAnexoBySuperior = TypesUtil.convertListToMapList("anexoSuperior.codigo", anexosAll);

        List<Carrera> carrerasAll = carreraDAO.all();
        List<Carrera> carrerasPosgrado = carrerasAll.stream()
                .filter(x -> x.getModalidadEstudio().isPostgrado())
                .collect(Collectors.toList());
        Map<Long, Carrera> mapCarreraPosgrado = TypesUtil.convertListToMap("id", carrerasPosgrado);

        List<AnexoBoletin> anexos = new ArrayList();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esRolEPG = rol.getCodigoEnum() == RolEnum.OPER_PROGH_EPG || rol.getCodigoEnum() == RolEnum.REVISOR_PROGH_EPG;
            boolean esOficinaEPG = oficinaMain.getCodigoEnum() == EPG;
            if (esRolEPG && esOficinaEPG) {
                anexos.addAll(mapAnexoBySuperior.get(CodigoAnexoBoletinEnum.G04.name()));
            }
        }

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();
            Carrera carreraPosgrado = mapCarreraPosgrado.get(oficinaMain.getInstanciaOficina());

            boolean esRolMaestria = rol.getCodigoEnum() == RolEnum.OPER_PROGH_ESP_EPG;
            boolean esOficinaMaestria = oficinaMain.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP;
            boolean esCarreraPosgrado = carreraPosgrado != null;
            if (esRolMaestria && esOficinaMaestria && esCarreraPosgrado) {
                AnexoBoletin anexo = mapAnexoByCarrera.get(carreraPosgrado.getId());
                if (anexo != null) {
                    anexos.add(anexo);
                }
            }
        }

        return anexos;
    }

    @Override
    public boolean puedeEditarAnexos(DataSessionPivot ds) {
        List<UsuarioRol> userRolesAll = usuarioRolDAO.allActivosByUser(ds.getUsuario());
        if (userRolesAll.isEmpty()) {
            return false;
        }

        List<UsuarioRol> userRoles = new ArrayList();
        for (UsuarioRol userRol : userRolesAll) {
            Oficina oficinaUser = userRol.getOficina();
            if (oficinaUser == null) {
                continue;
            }
            userRoles.add(userRol);
        }

        Map<Long, Oficina> mapOficinaMain = new HashMap();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = oficinaService.findOficinaMain(userRol.getOficina());
            mapOficinaMain.put(userRol.getOficina().getId(), oficinaMain);
        }

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esORolOERA = rol.getCodigoEnum() == RolEnum.OPER_PROGH_OERA;
            boolean esOficinaOERA = oficinaMain.getCodigoEnum() == OERA;
            if (esORolOERA && esOficinaOERA) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean puedeEditarAnexosPosgrado(DataSessionPivot ds) {
        boolean puedeEditar = puedeEditarAnexos(ds);
        if (puedeEditar) {
            return puedeEditar;
        }

        List<UsuarioRol> userRolesAll = usuarioRolDAO.allActivosByUser(ds.getUsuario());
        if (userRolesAll.isEmpty()) {
            return false;
        }

        List<UsuarioRol> userRoles = new ArrayList();
        for (UsuarioRol userRol : userRolesAll) {
            Oficina oficinaUser = userRol.getOficina();
            if (oficinaUser == null) {
                continue;
            }
            userRoles.add(userRol);
        }

        Map<Long, Oficina> mapOficinaMain = new HashMap();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = oficinaService.findOficinaMain(userRol.getOficina());
            mapOficinaMain.put(userRol.getOficina().getId(), oficinaMain);
        }

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esRolEPG = rol.getCodigoEnum() == RolEnum.OPER_PROGH_EPG;
            boolean esOficinaEPG = oficinaMain.getCodigoEnum() == OficinaEnum.EPG;
            if (esRolEPG && esOficinaEPG) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEditorProgramacionOera(DataSessionPivot ds) {
        List<UsuarioRol> userRolesAll = usuarioRolDAO.allActivosByUser(ds.getUsuario());
        if (userRolesAll.isEmpty()) {
            return false;
        }

        List<UsuarioRol> userRoles = new ArrayList();
        for (UsuarioRol userRol : userRolesAll) {
            Oficina oficinaUser = userRol.getOficina();
            if (oficinaUser == null) {
                continue;
            }
            userRoles.add(userRol);
        }

        Map<Long, Oficina> mapOficinaMain = new HashMap();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = oficinaService.findOficinaMain(userRol.getOficina());
            mapOficinaMain.put(userRol.getOficina().getId(), oficinaMain);
        }

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esORolOERA = rol.getCodigoEnum() == RolEnum.OPER_PROGH_OERA;
            boolean esOficinaOERA = oficinaMain.getCodigoEnum() == OERA;
            if (esORolOERA && esOficinaOERA) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEditorProgramacionMaestria(DataSessionPivot ds) {
        List<UsuarioRol> userRolesAll = usuarioRolDAO.allActivosByUser(ds.getUsuario());
        if (userRolesAll.isEmpty()) {
            return false;
        }

        List<UsuarioRol> userRoles = new ArrayList();
        for (UsuarioRol userRol : userRolesAll) {
            Oficina oficinaUser = userRol.getOficina();
            if (oficinaUser == null) {
                continue;
            }
            userRoles.add(userRol);
        }

        Map<Long, Oficina> mapOficinaMain = new HashMap();
        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = oficinaService.findOficinaMain(userRol.getOficina());
            mapOficinaMain.put(userRol.getOficina().getId(), oficinaMain);
        }

        List<Carrera> carrerasAll = carreraDAO.all();
        Map<Long, Carrera> mapCarrera = TypesUtil.convertListToMap("id", carrerasAll);

        for (UsuarioRol userRol : userRoles) {
            Oficina oficinaMain = mapOficinaMain.get(userRol.getOficina().getId());
            Rol rol = userRol.getRol();

            boolean esProgramadorMaestria = rol.getCodigoEnum() == RolEnum.OPER_PROGH_ESP_EPG;
            boolean esOficinaEspecialidad = oficinaMain.getTipoOficina().getCodigoEnum() == ESP;
            boolean esMaestria = false;

            if (esOficinaEspecialidad) {
                Carrera carrera = mapCarrera.get(oficinaMain.getInstanciaOficina());
                esMaestria = carrera.getModalidadEstudio().isPostgrado();
            }

            if (esProgramadorMaestria && esOficinaEspecialidad && esMaestria) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean puedeVerHeadAlumno(DataSessionPivot ds) {
        boolean puedeVerHead = true;
        List<RolEnum> rolCodigos = new ArrayList();
        for (Rol rol : ds.getRoles()) {
            rolCodigos.add(rol.getCodigoEnum());
        }

        if (rolCodigos.contains(RolEnum.REVISOR_FAC_ECONOMIA)) {
            puedeVerHead = false;
        }
        return puedeVerHead;
    }

    @Override
    public boolean isTrabajadorOera(DataSessionPivot ds) {
        boolean esTrabajadorOERA = false;
        List<Oficina> oficinasMain = oficinaService.allOficinasMainByPersona(ds.getPersona());
        for (Oficina oficina : oficinasMain) {
            if (oficina.getCodigoEnum() == OERA) {
                esTrabajadorOERA = true;
            }
        }
        return esTrabajadorOERA;
    }

    @Override
    public boolean isRevisorActaNotas(DataSessionPivot ds) {
        return this.esTrabajadorOeraConRol(RolEnum.REVISOR_ACTANOTAS_OERA, ds);
    }

    @Override
    public boolean isRolCape(DataSessionPivot ds) {
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.CAPE) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRolRacd(DataSessionPivot ds) {
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.RACD) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isDeveloperOERA(DataSessionPivot ds) {
        boolean esInformaticoOera = this.esTrabajadorOeraConRol(RolEnum.IOREA, ds);
        if (esInformaticoOera) {
            return esInformaticoOera;
        }

        Oficina areaSistemasOERA = oficinaDAO.findByCode(ASOERA.name());
        return this.esJefeOrEncargadoOficina(areaSistemasOERA, ds);
    }

    private boolean esTrabajadorOeraConRol(RolEnum rolEnum, DataSessionPivot ds) {
        log.info("ver-rol-trabajador rol={} user.id={} user=google={}", rolEnum.name(), ds.getUsuario().getId(), ds.getUsuario().getGoogle());

        List<Oficina> oficinasOrganizadas = oficinaService.allOficinasOrganizadas();
        List<UsuarioRol> rolesUser = usuarioRolDAO.allWithOfficeByUserRolEnum(ds.getUsuario(), rolEnum);
        log.info("roles-user-size = {}", rolesUser.size());

        for (UsuarioRol userRol : rolesUser) {
            log.info("rol={} oficina={}", userRol.getRol().getCodigo(), userRol.getOficina().getCodigo());
        }

        Optional<UsuarioRol> rolBuscado = rolesUser.stream()
                .filter(userRol -> areaDentroOERA(userRol.getOficina(), oficinasOrganizadas))
                .findFirst();

        log.info("rolBuscado = {}", rolBuscado.orElse(null));

        return rolBuscado.isPresent();
    }

    private boolean esTrabajadorEpgConRol(RolEnum rolEnum, DataSessionPivot ds) {
        log.info("ver-rol-trabajador rol={} user.id={} user=google={}", rolEnum.name(), ds.getUsuario().getId(), ds.getUsuario().getGoogle());

        List<Oficina> oficinasOrganizadas = oficinaService.allOficinasOrganizadas();
        List<UsuarioRol> rolesUser = usuarioRolDAO.allWithOfficeByUserRolEnum(ds.getUsuario(), rolEnum);
        log.info("roles-user-size = {}", rolesUser.size());

        for (UsuarioRol userRol : rolesUser) {
            log.info("rol={} oficina={}", userRol.getRol().getCodigo(), userRol.getOficina().getCodigo());
        }

        Optional<UsuarioRol> rolBuscado = rolesUser.stream()
                .filter(userRol -> areaDentroEPG(userRol.getOficina(), oficinasOrganizadas))
                .findFirst();

        log.info("rolBuscado = {}", rolBuscado.orElse(null));

        return rolBuscado.isPresent();
    }

    private boolean esTrabajadorObuaeConRol(RolEnum rolEnum, DataSessionPivot ds) {
        log.info("ver-rol-trabajador rol={} user.id={} user=google={}", rolEnum.name(), ds.getUsuario().getId(), ds.getUsuario().getGoogle());

        List<Oficina> oficinasOrganizadas = oficinaService.allOficinasOrganizadas();
        List<UsuarioRol> rolesUser = usuarioRolDAO.allWithOfficeByUserRolEnum(ds.getUsuario(), rolEnum);
        log.info("roles-user-size = {}", rolesUser.size());

        for (UsuarioRol userRol : rolesUser) {
            log.info("rol={} oficina={}", userRol.getRol().getCodigo(), userRol.getOficina().getCodigo());
        }

        Optional<UsuarioRol> rolBuscado = rolesUser.stream()
                .filter(userRol -> areaDentroOBUAE(userRol.getOficina(), oficinasOrganizadas))
                .findFirst();

        log.info("rolBuscado = {}", rolBuscado.orElse(null));

        return rolBuscado.isPresent();
    }

    private boolean esAdministradorTutor(RolEnum rolEnum, DataSessionPivot ds) {
        log.info("ver-rol-trabajador rol={} user.id={} user=google={}", rolEnum.name(), ds.getUsuario().getId(), ds.getUsuario().getGoogle());

        List<Oficina> oficinasOrganizadas = oficinaService.allOficinasOrganizadas();
        List<UsuarioRol> rolesUser = usuarioRolDAO.allWithOfficeByUserRolEnum(ds.getUsuario(), rolEnum);
        log.info("roles-user-size = {}", rolesUser.size());

        for (UsuarioRol userRol : rolesUser) {
            log.info("rol={} oficina={}", userRol.getRol().getCodigo(), userRol.getOficina().getCodigo());
        }

        Optional<UsuarioRol> rolBuscado = rolesUser.stream()
                .filter(userRol -> areaDentroOERA(userRol.getOficina(), oficinasOrganizadas))
                .findFirst();

        log.info("rolBuscado = {}", rolBuscado.orElse(null));

        return rolBuscado.isPresent();
    }

    private boolean esTrabajadorOficinaConRol(Oficina oficinaMain, RolEnum rolEnum, DataSessionPivot ds) {
        log.info("ver-rol-trabajador rol={} user.id={} user=google={}", rolEnum.name(), ds.getUsuario().getId(), ds.getUsuario().getGoogle());

        List<Oficina> oficinasOrganizadas = oficinaService.allOficinasOrganizadas();
        List<UsuarioRol> rolesUser = usuarioRolDAO.allWithOfficeByUserRolEnum(ds.getUsuario(), rolEnum);
        log.info("roles-user-size = {}", rolesUser.size());

        for (UsuarioRol userRol : rolesUser) {
            log.info("rol={} oficina={}", userRol.getRol().getCodigo(), userRol.getOficina().getCodigo());
        }

        Optional<UsuarioRol> rolBuscado = rolesUser.stream()
                .filter(userRol -> areaDentroOficinaMain(userRol.getOficina(), oficinasOrganizadas, oficinaMain))
                .findFirst();

        log.info("rolBuscado = {}", rolBuscado.orElse(null));

        return rolBuscado.isPresent();
    }

    private boolean areaDentroOficinaMain(Oficina area, List<Oficina> oficinasOrganizadas, OficinaEnum oficinaEnum) {
        log.info("verificando-oficina={} dentro-oficina-main={}", area.getCodigo(), oficinaEnum.name());
        boolean dentroOficina = oficinaService.findOficinaMain(area, oficinasOrganizadas).getCodigoEnum() == oficinaEnum;
        log.info("dentro-oficina {}", dentroOficina);
        return dentroOficina;
    }

    private boolean areaDentroOficinaMain(Oficina area, List<Oficina> oficinasOrganizadas, Oficina oficina) {
        log.info("verificando-oficina={} dentro-oficina-main={}", area.getCodigo(), oficina.getCodigo());
        boolean dentroOficina = oficinaService.findOficinaMain(area, oficinasOrganizadas).getCodigoEnum() == oficina.getCodigoEnum();
        log.info("dentro-oficina {}", dentroOficina);
        return dentroOficina;
    }

    private boolean areaDentroOBUAE(Oficina area, List<Oficina> oficinasOrganizadas) {
        return areaDentroOficinaMain(area, oficinasOrganizadas, OBUAE);
    }

    private boolean areaDentroOERA(Oficina area, List<Oficina> oficinasOrganizadas) {
        return areaDentroOficinaMain(area, oficinasOrganizadas, OERA);
    }

    private boolean areaDentroEPG(Oficina area, List<Oficina> oficinasOrganizadas) {
        return areaDentroOficinaMain(area, oficinasOrganizadas, EPG);
    }

    private boolean esJefeOrEncargadoOficina(Oficina oficina, DataSessionPivot ds) {

        Persona jefe = oficina.getPersonaJefe();
        if (jefe != null && jefe.getId().equals(ds.getPersona().getId())) {
            return true;
        }

        Persona enbcargado = oficina.getJefeEncargado();
        if (enbcargado != null && enbcargado.getId().equals(ds.getPersona().getId())) {
            return true;
        }

        return false;
    }

    @Override
    public boolean isRevisorActaNotasDepartamento(DataSessionPivot ds) {
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.REVISOR_ACTANOTAS_OERA) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean esInformaticoOERA(DataSessionPivot ds) {
        boolean puedeEditar = this.esTrabajadorOeraConRol(RolEnum.IOREA, ds);
        if (puedeEditar) {
            return puedeEditar;
        }
        return false;
    }

    @Override
    public boolean soloEditarDatosAlumno(DataSessionPivot ds) {
        for (Rol rol : ds.getRoles()) {
            if (rol.getCodigoEnum() == RolEnum.TRAM_DOCUM_OERA) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean esConsejeroCarrera(DataSessionPivot ds, Carrera carrera) {
        Consejero consejero = consejeroDAO.findByPersonaCarrera(ds.getPersona(), carrera);
        if (consejero == null) {
            return false;
        }
        return (consejero.getEstadoEnum() == EstadoEnum.ACT);
    }

    @Override
    public boolean esCoordinadorConsejeria(DataSessionPivot ds, Carrera carrera) {
        String codigo = "CT-" + carrera.getCodigo();
        Oficina oficina = oficinaService.findByCodigo(codigo);
        if (oficina == null) {
            return false;
        }

        if (oficina.getPersonaJefe() != null && oficina.getPersonaJefe().equals(ds.getPersona())) {
            return true;
        }
        if (oficina.getJefeEncargado() != null && oficina.getJefeEncargado().equals(ds.getPersona())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean esJefeCarrera(DataSessionPivot ds, Carrera carrera) {
        String codigo = "E" + carrera.getCodigo();
        Oficina oficina = oficinaService.findByCodigo(codigo);
        if (oficina == null) {
            return false;
        }

        if (oficina.getPersonaJefe() != null && oficina.getPersonaJefe().equals(ds.getPersona())) {
            return true;
        }
        if (oficina.getJefeEncargado() != null && oficina.getJefeEncargado().equals(ds.getPersona())) {
            return true;
        }
        return false;
    }

}
