package pe.edu.lamolina.pivot.controller.general.oficina;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.NivelOficinaEnum;
import pe.edu.lamolina.model.enums.OficinaEstadoEnum;
import pe.edu.lamolina.model.enums.PerfilEstadoEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.ColaboradorEstado;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.general.TipoOficina;
import pe.edu.lamolina.model.medico.Medico;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.general.AusenciaJefeDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorEstadoDAO;
import pe.edu.lamolina.pivot.dao.general.FuncionColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.general.PersonaCargoDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.pivot.dao.general.TipoOficinaDAO;
import pe.edu.lamolina.pivot.dao.medico.MedicoDAO;
import pe.edu.lamolina.pivot.dao.seguridad.FuncionRolDAO;

@Service
@Transactional(readOnly = true)
public class OficinaServiceImp implements OficinaService {

    @Autowired
    OficinaDAO oficinaDAO;

    @Autowired
    ColaboradorDAO colaboradorDAO;

    @Autowired
    DepartamentoAcademicoDAO departamentoAcademicoDAO;

    @Autowired
    CarreraDAO carreraDAO;

    @Autowired
    FacultadDAO facultadDAO;

    @Autowired
    PersonaDAO personaDAO;

    @Autowired
    PerfilCompaniaDAO perfilCompaniaDAO;

    @Autowired
    AusenciaJefeDAO ausenciaJefeDAO;

    @Autowired
    PersonaCargoDAO personaPerfilDAO;

    @Autowired
    DocenteDAO docenteDAO;

    @Autowired
    UsuarioDAO usuarioDAO;

    @Autowired
    RolDAO rolDAO;

    @Autowired
    UsuarioRolDAO usuarioRolDAO;

    @Autowired
    FuncionColaboradorDAO funcionColaboradorDAO;

    @Autowired
    TipoOficinaDAO tipoOficinaDAO;

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;

    @Autowired
    FuncionRolDAO funcionRolDAO;

    @Autowired
    ColaboradorEstadoDAO colaboradorEstadoDAO;

    @Autowired
    MedicoDAO medicoDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<Oficina> allByDynatable(DynatableFilter filter, Compania compania) {
        return oficinaDAO.allByFilter(filter, compania);
    }

    @Override
    public Oficina find(Oficina persona) {
        return oficinaDAO.find(persona.getId());
    }

    @Override
    @Transactional
    public void update(Oficina oficina, DataSessionPivot ds) {
        ObjectUtil.eliminarAttrSinId(oficina);

        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        oficinaBD.setOficinaSuperior(oficina.getOficinaSuperior());
        oficinaBD.setNombre(oficina.getNombre());
        oficinaBD.setCodigo(oficina.getCodigo());
        oficinaBD.setInstanciaOficina(oficina.getInstanciaOficina());
        oficinaBD.setCargoJefe(oficina.getCargoJefe());
        oficinaBD.setTipoOficina(oficina.getTipoOficina());
        oficinaDAO.update(oficinaBD);
    }

    @Override
    @Transactional
    public void save(Oficina oficina, DataSessionPivot ds) {
        ObjectUtil.eliminarAttrSinId(oficina, "oficinaSuperior");
        ObjectUtil.eliminarAttrSinId(oficina, "cargoJefe");
        ObjectUtil.eliminarAttrSinId(oficina, "personaJefe");
        ObjectUtil.eliminarAttrSinId(oficina, "jefeEncargado");
        oficina.setTipoOficina(oficina.getTipoOficina());
        oficina.setEstadoEnum(OficinaEstadoEnum.ACT);
        oficina.setFechaRegistro(new Date());
        oficina.setUserRegistro(ds.getUsuario());
        oficinaDAO.save(oficina);
    }

//    @Override
//    @Transactional
//    public void delete(Oficina oficina) {
//        oficinaDAO.delete(oficina);
//    }
    @Override
    public List<Colaborador> allColaborador(List<Oficina> oficinas) {
        if (oficinas.size() < 1) {
            return new ArrayList();
        }
        return colaboradorDAO.allByOficinas(oficinas);
    }

    @Override
    public List<Oficina> allUnidadSuperior(String nombre, Compania compania) {
        return oficinaDAO.allUnidadSuperior(nombre, compania);
    }

    @Override
    public List<DepartamentoAcademico> allDepartamento(Compania compania) {
        return departamentoAcademicoDAO.allByCompania(compania);
    }

    @Override
    public List<Carrera> allCarrera(Compania compania) {
        return carreraDAO.allByCompania(compania);
    }

    @Override
    public List<Facultad> allFacultad(Compania compania) {
        return facultadDAO.allByCompania(compania);
    }

    @Override
    @Transactional
    public void cambiarEstado(Oficina oficina, String accion) {
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        Assert.isNotNull(oficinaBD, "El registro de la oficina no existe en el sistema");

        if (accion.equals("desactivar")) {
            Assert.isFalse(oficinaBD.getEstadoEnum() == OficinaEstadoEnum.INA, "La oficina ya se encuentra desactivada");
            List<Colaborador> colaboradores = colaboradorDAO.allActivosByOficina(oficinaBD);
            Assert.isTrue(colaboradores.isEmpty(), "No puede desactivar una oficina que contiene colaboradores activos");

            oficinaBD.setEstadoEnum(OficinaEstadoEnum.INA);
            oficinaDAO.update(oficinaBD);

        } else if (accion.equals("activar")) {
            Assert.isFalse(oficinaBD.getEstadoEnum() == OficinaEstadoEnum.ACT, "La oficina ya se encuentra activada");
            oficinaBD.setEstadoEnum(OficinaEstadoEnum.ACT);
            oficinaDAO.update(oficinaBD);

        } else if (accion.equals("eliminar")) {
            List<Colaborador> colaboradores = colaboradorDAO.allByOficina(oficinaBD);
            Assert.isTrue(colaboradores.isEmpty(), "El registro de esta oficina se encuentra relacionada a otros elementos del sistema y no podrá ser eliminada");
            oficinaDAO.delete(oficinaBD);
        }
    }

    @Override
    public List<Persona> allPersona(String nombre) {
        return personaDAO.allByNombre(nombre);
    }

    @Override
    public List<Colaborador> allColaboradorByOficina(Oficina oficina) {
        return colaboradorDAO.allByOficina(oficina);
    }

    @Override
    public List<PerfilCompania> allCargo(String nombre) {
        return perfilCompaniaDAO.allByNombre(nombre);
    }

    @Override
    public void fillReferencia(Oficina oficina) {
        TipoOficina tipo = oficina.getTipoOficina();
        if (TipoOficinaEnum.DPTO.name().equalsIgnoreCase(tipo.getCodigo())) {
            DepartamentoAcademico departamento = departamentoAcademicoDAO.find(oficina.getInstanciaOficina());
            oficina.setInstanciaOficinaCodigo(departamento.getCodigo());
            oficina.setInstanciaOficinaNombre(departamento.getNombreLargo());
        }
        if (TipoOficinaEnum.ESP.name().equalsIgnoreCase(tipo.getCodigo())) {
            Carrera carrera = carreraDAO.find(oficina.getInstanciaOficina());
            oficina.setInstanciaOficinaCodigo(carrera.getCodigo());
            oficina.setInstanciaOficinaNombre(carrera.getNombre());
        }
        if (TipoOficinaEnum.FAC.name().equalsIgnoreCase(tipo.getCodigo())) {
            Facultad facultad = facultadDAO.find(oficina.getInstanciaOficina());
            oficina.setInstanciaOficinaCodigo(facultad.getCodigo());
            oficina.setInstanciaOficinaNombre(facultad.getNombre());
        }
    }

    @Override
    @Transactional
    public void asignarJefe(Oficina oficina, DataSessionPivot ds) {
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        TipoOficina tipo = oficinaBD.getTipoOficina();
        List<Docente> docentesBD = docenteDAO.allByPersona(oficina.getPersonaJefe());

        if (oficinaBD.getPersonaJefe() != null) {
            throw new PhobosException("Esta Unidad ya tiene asignado un jefe");
        }

        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
        if (oficina.getFechaInicioJefatura().after(hoy)) {
            throw new PhobosException("No puede poner como fecha de inicio un día futuro");
        }

        if (oficinaBD.getCargoJefe() == null) {
            throw new PhobosException("Falta definir el Cargo de la jefatura de esta Unidad");
        }

        if (tipo.getNivel().equals("OFI") && docentesBD.isEmpty()) {
            throw new PhobosException("La persona seleccionada no es un docente. Elija un docente activo.");
        }

        oficinaBD.setPersonaJefe(oficina.getPersonaJefe());
        oficinaBD.setFechaInicioJefatura(oficina.getFechaInicioJefatura());
        oficinaDAO.update(oficinaBD);

        if (oficina.getPersonaJefe().getTituloAcademico() != null) {
            Persona jefeBD = personaDAO.find(oficina.getPersonaJefe().getId());
            jefeBD.setTituloAcademico(oficina.getPersonaJefe().getTituloAcademico());
            personaDAO.update(jefeBD);
        }

        PersonaCargo perfil = new PersonaCargo();
        perfil.setCompania(ds.getCompania());
        perfil.setPersona(oficinaBD.getPersonaJefe());
        perfil.setPerfilCompania(oficinaBD.getCargoJefe());
        perfil.setOficina(oficinaBD);
        perfil.setEstadoEnum(PerfilEstadoEnum.ACT);
        perfil.setFechaInicio(oficinaBD.getFechaInicioJefatura());
        perfil.setFechaRegistro(new Date());
        perfil.setUserRegistro(ds.getUsuario());
        personaPerfilDAO.save(perfil);

        Colaborador colaborador = new Colaborador();
        colaborador.setEstado(ColaboradorEstadoEnum.ACT.toString());
        colaborador.setFechaInicio(new Date());
        colaborador.setOficina(oficinaBD);
        colaborador.setPersona(oficinaBD.getPersonaJefe());
        colaborador.setUserRegistro(ds.getUsuario());
        colaborador.setCargo(oficinaBD.getCargoJefe());
        colaborador.setCodigo(getCodigoColaborador() + "");
        colaboradorDAO.save(colaborador);

        this.asignarRol(oficinaBD.getPersonaJefe(), RolEnum.JEFE_DPTO_ACA, ds);

    }

    @Override
    @Transactional
    public void retirarJefe(Oficina oficina, DataSessionPivot ds) {
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());

        Long idJefe = (Long) ObjectUtil.getParentTree(oficinaBD, "personaJefe.id");
        if (idJefe == null) {
            throw new PhobosException("Esta Unidad no tiene jefe asignado");
        }
        if (idJefe.longValue() != oficina.getPersonaJefe().getId()) {
            throw new PhobosException("No coinciden el Jefe de la Unidad y los datos enviados");
        }

        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
        if (oficina.getFechaFinJefatura().after(hoy)) {
            throw new PhobosException("No puede poner como fecha final un día futuro");
        }

        if (oficinaBD.getFechaInicioJefatura().after(oficina.getFechaFinJefatura())) {
            throw new PhobosException("La fecha final no puede ser antes de la fecha de inicio");
        }

        PersonaCargo perfil = personaPerfilDAO.findSinCerrar(oficinaBD, ds.getCompania());
        if (perfil == null) {
            perfil = new PersonaCargo();
            perfil.setCompania(ds.getCompania());
            perfil.setPersona(oficinaBD.getPersonaJefe());
            perfil.setPerfilCompania(oficinaBD.getCargoJefe());
            perfil.setOficina(oficinaBD);
            perfil.setEstadoEnum(PerfilEstadoEnum.ACT);
            perfil.setFechaInicio(oficinaBD.getFechaInicioJefatura());
            perfil.setFechaRegistro(new Date());
            perfil.setUserRegistro(ds.getUsuario());
            personaPerfilDAO.save(perfil);
        }

        perfil.setFechaFin(oficina.getFechaFinJefatura());
        perfil.setEstadoEnum(PerfilEstadoEnum.CER);
        perfil.setUserModificacion(ds.getUsuario());
        perfil.setFechaModificacion(new Date());
        personaPerfilDAO.update(perfil);

        oficinaBD.setPersonaJefe(null);
        oficinaBD.setFechaInicioJefatura(null);
        oficinaDAO.update(oficinaBD);

    }

    @Override
    @Transactional

    public void asignarEncargado(Oficina oficina, DataSessionPivot ds) {

        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        if (oficinaBD.getJefeEncargado() != null) {
            throw new PhobosException("Esta Unidad ya tiene asignado un jefe encargado");
        }

        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
        if (oficina.getFechaEncargatura().after(hoy)) {
            throw new PhobosException("No puede poner como fecha de inicio un día futuro");
        }

        if (oficinaBD.getCargoJefe() == null) {
            throw new PhobosException("Falta definir el Cargo de la jefatura de esta Unidad");
        }

        oficinaBD.setJefeEncargado(oficina.getJefeEncargado());
        oficinaBD.setMotivoAusenciaJefe(oficina.getMotivoAusenciaJefe());
        oficinaBD.setFechaEncargatura(oficina.getFechaEncargatura());
        oficinaDAO.update(oficinaBD);

        if (oficina.getJefeEncargado().getTituloAcademico() != null) {
            Persona jefeBD = personaDAO.find(oficina.getJefeEncargado().getId());
            jefeBD.setTituloAcademico(oficina.getJefeEncargado().getTituloAcademico());
            personaDAO.update(jefeBD);
        }

        AusenciaJefe ausenciaJefe = new AusenciaJefe();
        ausenciaJefe.setJefe(oficinaBD.getPersonaJefe());
        ausenciaJefe.setEncargado(oficina.getJefeEncargado());
        ausenciaJefe.setFechaInicioEncargatura(oficina.getFechaEncargatura());
        ausenciaJefe.setFechaRegistro(new Date());
        ausenciaJefe.setOficina(oficinaBD);
        ausenciaJefe.setUserRegistro(ds.getUsuario());

        ausenciaJefe.setMotivo(oficina.getMotivoAusenciaJefe());
        if (oficinaBD.getPersonaJefe() == null) {
            ausenciaJefe.setMotivo("Encargado por falta de nombramiento de jefe oficial");
        }
        ausenciaJefeDAO.save(ausenciaJefe);

    }

    @Override
    @Transactional
    public void retirarEncargado(AusenciaJefe ausencia, DataSessionPivot ds) {

        Oficina oficinaBD = oficinaDAO.find(ausencia.getOficina().getId());
        AusenciaJefe ausenciaBD = ausenciaJefeDAO.findSinCerrar(ausencia);

        if (ausenciaBD == null) {
            throw new PhobosException("No existe una encargatura pendiente de cierre con estos datos para esta unidad");
        }
        Long idEncargado = (Long) ObjectUtil.getParentTree(oficinaBD, "jefeEncargado.id");
        if (idEncargado == null) {
            throw new PhobosException("No existe Jefe Encargado para esta unidad");
        }
        if (idEncargado.longValue() != ausencia.getEncargado().getId()) {
            throw new PhobosException("No coinciden el Jefe Encargado de la Unidad y la encargatura que desea cerrar");
        }
        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
        if (ausencia.getFechaFinEncargatura().after(hoy)) {
            throw new PhobosException("No puede poner como fecha final un día futuro");
        }
        if (ausenciaBD.getFechaInicioEncargatura().after(ausencia.getFechaFinEncargatura())) {
            throw new PhobosException("La fecha final no puede ser antes de la fecha de inicio");
        }

        ausenciaBD.setFechaFinEncargatura(ausencia.getFechaFinEncargatura());
        ausenciaBD.setUserModificacion(ds.getUsuario());
        ausenciaBD.setFechaModificacion(new Date());
        ausenciaJefeDAO.update(ausenciaBD);

        oficinaBD.setJefeEncargado(null);
        oficinaBD.setFechaEncargatura(null);
        oficinaBD.setMotivoAusenciaJefe(null);
        oficinaDAO.update(oficinaBD);
    }

    private void asignarRol(Persona personaJefe, RolEnum rolEnum, DataSessionPivot ds) {
        Usuario usuarioDb = usuarioDAO.findByPersona(personaJefe);

        if (usuarioDb == null) {
            throw new PhobosException("La persona asignada no está registrado como usuario.");
        } else {
            Rol rol = rolDAO.findByCode(rolEnum);
            UsuarioRol userRol = usuarioRolDAO.findByUsuarioAndRol(usuarioDb, rol);
            if (userRol == null) {
                logger.debug("creando rol");
                userRol = new UsuarioRol();
                userRol.setEstado(UserEstadoEnum.ACT);
                userRol.setFechaInicio(new Date());
                userRol.setUserRegistro(ds.getUsuario());
                userRol.setUsuario(usuarioDb);
                userRol.setRol(rol);
                usuarioRolDAO.save(userRol);
            }
        }
    }

    @Override
    public List<Oficina> allOficina(Persona persona) {
        List<Oficina> oficinasSuperiorPersona = new ArrayList<>();
        List<Oficina> oficinasTodas = oficinaDAO.allAndSuperiorOfi();
        Map<Long, Oficina> mapOficinasTodas = TypesUtil.convertListToMap("id", oficinasTodas);
        for (Oficina oficina : oficinasTodas) {
            if (oficina.getOficinaSuperior() != null) {
                oficina.setOficinaSuperior(mapOficinasTodas.get(oficina.getOficinaSuperior().getId()));
            }
        }
        List<Oficina> oficinasPersona = oficinaDAO.allByUser(persona);
        for (Oficina oficinaPerso : oficinasPersona) {
            Oficina oficina = find(mapOficinasTodas, oficinaPerso);
            if (oficina != null) {
                oficinasSuperiorPersona.add(oficina);
            }
        }
        return oficinasSuperiorPersona;
    }

    private Oficina find(Map<Long, Oficina> mapOficinasTodas, Oficina oficina) {
        Oficina ofi = mapOficinasTodas.get(oficina.getId());
        if (ofi.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
            return ofi;
        } else {
            return find(mapOficinasTodas, ofi.getOficinaSuperior());
        }
    }

    @Override
    public Colaboradores countColaborador(Oficina oficina) {
        List<Oficina> oficinas = allOficinasByMain(oficina);
        Colaboradores colaboradors = colaboradorDAO.countByOficinas(oficinas);
        return colaboradors;
    }

    @Override
    public ArrayNode getColaboradoresJson(DynatableFilter filter, Oficina oficinaMain) {
        List<Oficina> oficinas = allOficinasByMain(oficinaMain);

        List<Colaborador> colaboradors = colaboradorDAO.allDynatableByOficina(filter, oficinas);
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        List<FuncionColaborador> funcion = funcionColaboradorDAO.findFuncionByColaborador();
        Map<Long, List<Colaborador>> map = TypesUtil.convertListToMapList("colaborador.id", "colaborador", funcion);
        for (Colaborador colaborador : colaboradors) {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            ObjectNode objNode = new ObjectNode(JsonNodeFactory.instance);

            if (ColaboradorEstadoEnum.DESP.name().equals(colaborador.getEstado())) {
                objNode.put("id", colaborador.getId());
                objNode.put("name", ColaboradorEstadoEnum.ACT.name());
                objNode.put("value", ColaboradorEstadoEnum.ACT.getValue());
                objNode.put("estado", colaborador.getEstado());
                array.add(objNode);

            } else {
                for (ColaboradorEstadoEnum value : ColaboradorEstadoEnum.values()) {
                    objNode = new ObjectNode(JsonNodeFactory.instance);
                    if (value.name().equalsIgnoreCase(colaborador.getEstado())) {

                    } else {
                        objNode.put("id", colaborador.getId());
                        objNode.put("name", value.name());
                        objNode.put("value", value.getValue());
                        objNode.put("estado", colaborador.getEstado());
                        array.add(objNode);
                    }
                }
            }

            node = new ObjectNode(JsonNodeFactory.instance);
            node.put("id", colaborador.getId());
            node.put("area", colaborador.getOficina().getNombre());
            node.put("cargo", colaborador.getCargo().getNombre());
            node.put("estado", ColaboradorEstadoEnum.getNombre(colaborador.getEstado()));
            node.put("persona", colaborador.getPersona() == null ? "" : colaborador.getPersona().getNombreCompleto());
            node.put("personaId", colaborador.getPersona() == null ? null : colaborador.getPersona().getId());
            node.put("dni", colaborador.getPersona() == null ? "" : colaborador.getPersona().getNumeroDocIdentidad());
            node.put("funciones", map.get(colaborador.getId()) == null ? 0 : map.get(colaborador.getId()).size());
            node.put("codigo", colaborador.getCodigo() == null ? "" : colaborador.getCodigo());
            node.set("estados", array);
            arrayNode.add(node);
        }

        return arrayNode;
    }

    private List<Oficina> allOficinasByMain(Oficina oficinaMain) {
        List<Oficina> oficinasTodas = getOficinasOrganizadas();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasTodas);

        Oficina oficinaBD = mapOficina.get(oficinaMain.getId());
        List<Oficina> oficinas = new ArrayList();
        oficinas.add(oficinaBD);
        agregarOficinasHijas(oficinaBD, oficinas);

        return oficinas;
    }

    private void agregarOficinasHijas(Oficina oficinaMain, List<Oficina> oficinas) {
        for (Oficina oficinasDependiente : oficinaMain.getOficinasDependientes()) {
            oficinas.add(oficinasDependiente);
            agregarOficinasHijas(oficinasDependiente, oficinas);
        }
    }

    @Override
    public List<Oficina> allOficinasMainByPersona(Persona persona) {
        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(persona);
        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("oficina.id", "oficina", colaboradores);
        List<Oficina> oficinasHijas = new ArrayList(mapOficinas.values());
        List<Oficina> oficinasMain = new ArrayList();

        List<Oficina> oficinasTodas = getOficinasOrganizadas();
        for (Oficina ofi : oficinasHijas) {
            Oficina main = findOficinaMain(ofi, oficinasTodas);
            oficinasMain.add(main);
        }
        return oficinasMain;

    }

    private Oficina findOficinaMain(Oficina oficinaHija, List<Oficina> oficinas) {
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinas);
        Oficina oficinaTempo = mapOficina.get(oficinaHija.getId());
        if (oficinaTempo.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
            return oficinaTempo;
        }
        for (;;) {
            Oficina sup = oficinaTempo.getOficinaSuperior();
            if (sup == null) {
                return null;
            }
            if (sup.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
                return sup;
            }
            oficinaTempo = sup;
        }

    }

    @Override
    public Oficina findOficinaHija(Persona persona, Oficina oficinaMain) {
        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(persona);
        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("oficina.id", "oficina", colaboradores);
        List<Oficina> oficinasHijas = new ArrayList(mapOficinas.values());

        List<Oficina> oficinasTodas = getOficinasOrganizadas();
        for (Oficina ofi : oficinasHijas) {
            Oficina main = findOficinaMain(ofi, oficinasTodas);
            if (main.getId() == oficinaMain.getId().longValue()) {
                return ofi;
            }
        }
        return null;
    }

    private List<Oficina> getOficinasOrganizadas() {
        List<Oficina> oficinasTodas = oficinaDAO.all();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasTodas);

        for (Oficina oficina : oficinasTodas) {
            oficina.setOficinasDependientes(new ArrayList());
        }
        for (Oficina oficina : oficinasTodas) {
            if (oficina.getOficinaSuperior() != null) {
                Oficina sup = mapOficina.get(oficina.getOficinaSuperior().getId());
                sup.getOficinasDependientes().add(oficina);
                oficina.setOficinaSuperior(sup);
            }
        }
        return oficinasTodas;
    }

    public Long getCodigoColaborador() {
        Long código = 10001l;
        Colaborador colaborador = colaboradorDAO.findMaxCodigo();
        if (colaborador != null && colaborador.getCodigo() != null) {
            código = Long.valueOf(colaborador.getCodigo()) + 1;
        }
        return código;
    }

    @Override
    @Transactional
    public void updateEstado(Colaborador colaborador, DataSessionPivot dataSessionPivot) {
        Usuario usuario = dataSessionPivot.getUsuario();
        Colaborador col = colaboradorDAO.find(colaborador.getId());
        col.setEstado(ColaboradorEstadoEnum.getName(colaborador.getEstado()));
        col.setFechaModificacion(new Date());
        colaboradorDAO.update(col);

        ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
        colaboradorEstado.setColaborador(colaborador);
        colaboradorEstado.setEstado(ColaboradorEstadoEnum.valueOf(ColaboradorEstadoEnum.getName(colaborador.getEstado())));
        colaboradorEstado.setUserRegistro(usuario);
        colaboradorEstado.setFechaRegistro(new Date());
        colaboradorEstadoDAO.save(colaboradorEstado);

        String estadoEnum = ColaboradorEstadoEnum.getName(colaborador.getEstado());
        if (ColaboradorEstadoEnum.DESP.toString().equals(estadoEnum)) {
            Colaborador emp = colaboradorDAO.find(colaborador);
            Usuario usuarioColaborador = usuarioDAO.findByPersona(emp.getPersona());
            usuarioRolDAO.update(colaborador, usuarioColaborador);

            // FALTA INHABILITAR LAS FUNCIONES
        }
    }

    @Override
    public List<TipoOficina> allTipoOficina() {

        return tipoOficinaDAO.all();
    }

    @Override
    public TipoOficina findTipoById(String id) {
        return tipoOficinaDAO.find(Long.valueOf(id));
    }

    @Override
    public Colaborador findColarador(Colaborador colaborador) {
        Colaborador colab = colaboradorDAO.find(colaborador);
        List<FuncionColaborador> list = funcionColaboradorDAO.findFuncionByColaborador(colaborador);
        colab.setFuncionColaborador(list);
        return colab;
    }

    @Override
    public List<TipoDocIdentidad> allDocumentosIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<Oficina> allOficinasByOficinaMain(Oficina oficina) {
        return allOficinasByMain(oficina);
    }

    @Override
    public List<PerfilCompania> allCargos(Oficina oficina) {
        ObjectUtil.printAttr(oficina);
        List<PerfilCompania> oficinaCompanias = perfilCompaniaDAO.allTipoCargoByOfi(oficina);
        logger.debug("CANTIDAD DE FUNCS = {}", oficinaCompanias.size());
        List<PerfilCompania> companias = perfilCompaniaDAO.allTipoCargo();
        List<PerfilCompania> allCompanias = new ArrayList<PerfilCompania>();

        allCompanias.addAll(oficinaCompanias);
        allCompanias.addAll(companias);
        logger.debug("CANTIDAD DE total = {}", allCompanias.size());
        return allCompanias;
    }

    @Override
    public List<PerfilCompania> allCargosByOficina(Oficina oficina) {

        return perfilCompaniaDAO.allTipoCargoByOfi(oficina);
    }

    @Override
    @Transactional
    public void saveColaborador(Colaborador colaborador, Oficina oficinaMean, Usuario usuario, Compania compania) {
        oficinaMean = oficinaDAO.find(oficinaMean.getId());
//        Usuario usuario = dataSessionPivot.getUsuario();
        Persona persona = colaborador.getPersona();
        persona.setFechaRegistro(new Date());
        persona.setUserRegistro(usuario);
        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setSexo(colaborador.getPersona().getSexo());
        personaDAO.save(persona);

        colaborador.setFechaRegistro(new Date());
        colaborador.setUserRegistro(usuario);
        colaborador.setEstado(ColaboradorEstadoEnum.ACT.name());
        colaborador.setCodigo(getCodigoColaborador() + "");
        colaborador.setPersona(persona);
        colaboradorDAO.save(colaborador);

        ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
        colaboradorEstado.setColaborador(colaborador);
        colaboradorEstado.setEstado(ColaboradorEstadoEnum.ACT);
        colaboradorEstado.setUserRegistro(usuario);
        colaboradorEstado.setFechaRegistro(new Date());
        colaboradorEstadoDAO.save(colaboradorEstado);

        PersonaCargo personaCargo = new PersonaCargo();
        personaCargo.setCompania(compania);
        personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
        personaCargo.setFechaInicio(colaborador.getFechaInicio());
        personaCargo.setFechaRegistro(new Date());
        personaCargo.setOficina(colaborador.getOficina());
        personaCargo.setPerfilCompania(colaborador.getCargo());
        personaCargo.setPersona(persona);
        personaCargo.setUserRegistro(usuario);
        personaPerfilDAO.save(personaCargo);

        Oficina oficinaColaborador = oficinaDAO.find(colaborador.getOficina().getId());
        Oficina oficinaCentroMedico = oficinaDAO.findByCode("CENMED");

        if ((oficinaColaborador.getId().equals(oficinaCentroMedico.getId()) || (oficinaColaborador.getOficinaSuperior() != null && oficinaColaborador.getOficinaSuperior().getId().equals(oficinaCentroMedico.getId())))
                && Arrays.asList("MEDICO", "JMEDICO").contains(colaborador.getCargo().getCodigo())) {

            Medico medico = new Medico();
            medico.setColaborador(colaborador);
            medico.setFechaRegistro(new Date());
            medico.setUserRegistro(usuario);
            medicoDAO.save(medico);
//            addRol(persona, RolEnum.MED, usuario);
        }

        ArrayList<PerfilCompania> listPerfiles = new ArrayList();
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            funcionColaborador.setFechaRegistro(new Date());
            funcionColaborador.setUserRegistro(usuario);
            funcionColaborador.setEstado(EstadoEnum.ACT.name());
            funcionColaborador.setColaborador(colaborador);
            funcionColaborador.setFuncion(perfil);
            funcionColaborador.setFechaInico(new Date());
            funcionColaboradorDAO.save(funcionColaborador);
            listPerfiles.add(perfil);
        }
        Usuario user = new Usuario();

        if (persona.getEmailCompania() != null) {
            user.setEstadoEnum(UserEstadoEnum.ACT);
            user.setGoogle(persona.getEmailCompania());
            user.setPersona(persona);
            user.setUserRegistro(usuario);
            user.setFechaRegistro(new Date());
            usuarioDAO.save(user);
            listPerfiles.add(colaborador.getCargo());
            addUserRoll(listPerfiles, oficinaColaborador, user, colaborador, usuario);
        }

    }

//    @Transactional
//    private void addRol(Persona p, RolEnum rol, Usuario userRegistro) {
//        Usuario usuario = usuarioDAO.findByPersona(p);
//        if (usuario == null) {
//            logger.debug("No se puede agregar rol porque no tiene usuario");
//            return;
//        }
//
//        Rol rolBD = rolDAO.findByCode(rol);
//        UsuarioRol ur = usuarioRolDAO.findByUsuarioAndRol(usuario, rolBD);
//
//        if (ur != null) {
//            logger.debug("No se puede agregar rol porque ya lo tiene");
//            return;
//        }
//
//        ur = new UsuarioRol();
//        ur.setRol(rolBD);
//        ur.setUsuario(usuario);
//        ur.setEstado(UserEstadoEnum.ACT);
//        ur.setFechaInicio(new Date());
//        ur.setFechaRegistro(new Date());
//        ur.setUserRegistro(userRegistro);
//
//        usuarioRolDAO.save(ur);
//    }
    @Override
    @Transactional
    public Boolean saveColaboradorExistente(Colaborador colaborador, Oficina oficinaMean, Usuario usuario, Compania compania) throws PhobosException {
        Oficina oficinaColaborador = oficinaDAO.find(colaborador.getOficina().getId());
        Persona personaForm = colaborador.getPersona();

        Persona personaBD = personaDAO.find(personaForm.getId());
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setNombres(personaForm.getNombres());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setEmailCompania(personaForm.getEmailCompania());
        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());
        personaDAO.update(personaBD);

        Colaborador colaboradors = colaboradorDAO.findActivoByPersonaOficina(oficinaColaborador, personaBD);
        if (colaboradors != null) {
            throw new PhobosException("El colaborador existe en la oficina");

        } else {
            colaborador.setFechaRegistro(new Date());
            colaborador.setUserRegistro(usuario);
            colaborador.setEstado(ColaboradorEstadoEnum.ACT.name());
            colaborador.setCodigo(getCodigoColaborador() + "");
            colaboradorDAO.save(colaborador);

            ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
            colaboradorEstado.setColaborador(colaborador);
            colaboradorEstado.setEstado(ColaboradorEstadoEnum.ACT);
            colaboradorEstado.setUserRegistro(usuario);
            colaboradorEstado.setFechaRegistro(new Date());
            colaboradorEstadoDAO.save(colaboradorEstado);

            PersonaCargo personaCargo = new PersonaCargo();
            personaCargo.setCompania(compania);
            personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
            personaCargo.setFechaInicio(colaborador.getFechaInicio());
            personaCargo.setFechaRegistro(new Date());
            personaCargo.setOficina(colaborador.getOficina());
            personaCargo.setPerfilCompania(colaborador.getCargo());
            personaCargo.setPersona(personaBD);
            personaCargo.setUserRegistro(usuario);
            personaPerfilDAO.save(personaCargo);

        }

        Oficina oficinaCentroMedico = oficinaDAO.findByCode("CENMED");

        if ((oficinaColaborador.getId().equals(oficinaCentroMedico.getId()) || (oficinaColaborador.getOficinaSuperior() != null && oficinaColaborador.getOficinaSuperior().getId().equals(oficinaCentroMedico.getId())))
                && Arrays.asList("MEDICO", "JMEDICO").contains(colaborador.getCargo().getCodigo())) {

            Medico medico = new Medico();
            medico.setColaborador(colaborador);
            medico.setFechaRegistro(new Date());
            medico.setUserRegistro(usuario);
            medicoDAO.save(medico);
//            addRol(personaBD, RolEnum.MED, usuario);
        }

        ArrayList<PerfilCompania> perfiles = new ArrayList();
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            funcionColaborador.setFechaRegistro(new Date());
            funcionColaborador.setUserRegistro(usuario);
            funcionColaborador.setEstado(EstadoEnum.ACT.name());
            funcionColaborador.setFuncion(perfil);
            funcionColaborador.setFechaInico(new Date());
            funcionColaborador.setColaborador(colaborador);
            funcionColaboradorDAO.save(funcionColaborador);
            perfiles.add(perfil);
        }

        Usuario user = usuarioDAO.findByPersona(personaForm);
        if (user == null) {
            user = new Usuario();
            if (colaborador.getPersona().getEmailCompania() != null) {
                user = addUser(personaForm, usuario);
                perfiles.add(colaborador.getCargo());
                addUserRoll(perfiles, oficinaColaborador, user, colaborador, usuario);
            }
        } else {
            UsuarioRol ur = usuarioRolDAO.findUsuarioAndOficina(user, oficinaColaborador);
            if (ur == null) {
                perfiles.add(colaborador.getCargo());
                addUserRoll(perfiles, oficinaColaborador, user, colaborador, usuario);
            }
        }
        return true;
    }

    @Transactional
    public Usuario addUser(Persona personaForm, Usuario usuario) {
        Usuario usuario1 = new Usuario();
        usuario1.setEstadoEnum(UserEstadoEnum.ACT);
        usuario1.setGoogle(personaForm.getEmailCompania());
        usuario1.setPersona(personaForm);
        usuario1.setUserRegistro(usuario);
        usuarioDAO.save(usuario1);

        return usuario1;
    }

    private void addUserRoll(List<PerfilCompania> perfilesCompania, Oficina oficinaMean, Usuario Usuariocolaborador, Colaborador colaborador, Usuario usuario) {
        oficinaMean = oficinaDAO.find(oficinaMean.getId());
        List<FuncionRol> funcionRol = funcionRolDAO.allByPerfilCompania(perfilesCompania);
        logger.debug("funcionRol size {}", funcionRol.size());
        Map<Long, List<Rol>> mapRol = TypesUtil.convertListToMapList("perfilCompania.id", "rol", funcionRol);
        logger.debug("mapRol size {}", mapRol.size());

        for (PerfilCompania perfilComp : perfilesCompania) {
            List<Rol> roless = mapRol.get(perfilComp.getId());
            logger.debug("mapRol size {} {}  ", perfilComp.getId(), roless);
            if (roless == null) {
                continue;
            }
            for (Rol rol : roless) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setEstado(UserEstadoEnum.ACT);
                usuarioRol.setFechaInicio(colaborador.getFechaInicio());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setOficina(oficinaMean);
                usuarioRol.setIdInstancia(oficinaMean.getInstanciaOficina());
                usuarioRol.setTipoOficina(oficinaMean.getTipoOficina().getCodigo());
                usuarioRol.setRol(rol);
                usuarioRol.setUserRegistro(usuario);
                usuarioRol.setUsuario(Usuariocolaborador);
                usuarioRolDAO.save(usuarioRol);
            }
        }
    }

    @Override
    @Transactional
    public void updateColaborador(Colaborador colaboradorForm, Oficina oficinaMea, DataSessionPivot ds) {
        oficinaMea = oficinaDAO.find(colaboradorForm.getOficina().getId());
        Colaborador colaboradorBD = colaboradorDAO.find(colaboradorForm.getId());
        Oficina oficinaAnterior = colaboradorBD.getOficina();
        Oficina oficinaNueva = oficinaDAO.find(colaboradorForm.getOficina().getId());
        ObjectUtil.printAttr(colaboradorForm);

        colaboradorBD.setFechaModificacion(new Date());
        colaboradorBD.setUserModificacion(ds.getUsuario());
        colaboradorBD.setCargo(colaboradorForm.getCargo());
        colaboradorBD.setOficina(colaboradorForm.getOficina());
        colaboradorBD.setFechaInicio(colaboradorForm.getFechaInicio());
        colaboradorDAO.update(colaboradorBD);

        if (colaboradorForm.getOficina().getId() != oficinaAnterior.getId()) {

            Oficina oficinaCentroMedico = oficinaDAO.findByCode("CENMED");

            if ((oficinaNueva.getId().equals(oficinaCentroMedico.getId()) || (oficinaNueva.getOficinaSuperior() != null && oficinaNueva.getOficinaSuperior().getId().equals(oficinaCentroMedico.getId())))
                    && Arrays.asList("MEDICO", "JMEDICO").contains(colaboradorForm.getCargo().getCodigo())) {

                Medico antiguo = medicoDAO.findByColaborador(colaboradorBD);
                if (antiguo == null) {
                    Medico medico = new Medico();
                    medico.setColaborador(colaboradorBD);
                    medico.setFechaRegistro(new Date());
                    medico.setUserRegistro(ds.getUsuario());
                    medicoDAO.save(medico);
//                    addRol(colaboradorBD.getPersona(), RolEnum.MED, ds.getUsuario());
                }
            }

            if ((oficinaAnterior.getId().equals(oficinaCentroMedico.getId()) || (oficinaAnterior.getOficinaSuperior() != null && oficinaAnterior.getOficinaSuperior().getId().equals(oficinaCentroMedico.getId())))
                    && Arrays.asList("MEDICO", "JMEDICO").contains(colaboradorBD.getCargo().getCodigo())) {
                Medico antiguo = medicoDAO.findByColaborador(colaboradorBD);
                medicoDAO.update(antiguo);
            }

            PersonaCargo personaCargo = personaPerfilDAO.findCargoByPersona(oficinaAnterior, colaboradorForm.getPersona());
            personaCargo.setEstadoEnum(PerfilEstadoEnum.INA);
            personaCargo.setFechaFin(new Date());
            personaCargo.setFechaModificacion(new Date());
            personaCargo.setUserModificacion(ds.getUsuario());
            personaPerfilDAO.update(personaCargo);

            personaCargo = new PersonaCargo();
            personaCargo.setCompania(ds.getCompania());
            personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
            personaCargo.setFechaInicio(colaboradorForm.getFechaInicio());
            personaCargo.setFechaRegistro(new Date());
            personaCargo.setOficina(colaboradorForm.getOficina());
            personaCargo.setPerfilCompania(colaboradorForm.getCargo());
            personaCargo.setPersona(colaboradorForm.getPersona());
            personaCargo.setUserRegistro(ds.getUsuario());
            personaPerfilDAO.save(personaCargo);
        } else {
            PersonaCargo personaCargo = personaPerfilDAO.findCargoByPersona(oficinaAnterior, colaboradorForm.getPersona());
            personaCargo.setFechaModificacion(new Date());
            personaCargo.setUserModificacion(ds.getUsuario());
            personaCargo.setPerfilCompania(colaboradorForm.getCargo());
            personaPerfilDAO.update(personaCargo);
        }

        List<FuncionColaborador> funcionesEmp = funcionColaboradorDAO.findFuncionByColaborador(colaboradorForm);
        Map<Long, FuncionColaborador> mapNuevo = TypesUtil.convertListToMap("funcion.id", colaboradorForm.getFuncionColaborador());
        Map<Long, FuncionColaborador> mapTengo = TypesUtil.convertListToMap("funcion.id", funcionesEmp);
        for (FuncionColaborador funcionColaborador : funcionesEmp) {
            if (mapNuevo.get(funcionColaborador.getFuncion().getId()) == null) {
                funcionColaborador.setFechaFin(new Date());
                funcionColaborador.setEstado(EstadoEnum.INA.name());
                funcionColaboradorDAO.update(funcionColaborador);
            }
        }
        for (FuncionColaborador funcionColaborador : colaboradorForm.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            if (mapTengo.get(perfil.getId()) == null) {
                funcionColaborador.setFechaRegistro(new Date());
                funcionColaborador.setUserRegistro(ds.getUsuario());
                funcionColaborador.setEstado(EstadoEnum.ACT.name());
                funcionColaborador.setColaborador(colaboradorForm);
                funcionColaborador.setFuncion(perfil);
                funcionColaborador.setFechaInico(new Date());
                funcionColaboradorDAO.save(funcionColaborador);

            }
        }
        Usuario usuarioColaborador = usuarioDAO.findByPersona(colaboradorForm.getPersona());
        ArrayList<PerfilCompania> perfiles = new ArrayList();
        for (FuncionColaborador funcionColaborador : mapNuevo.values()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            perfiles.add(perfil);
        }
        if (usuarioColaborador != null) {
            perfiles.add(colaboradorForm.getCargo());
            updateUserRol(usuarioColaborador, perfiles, oficinaMea, colaboradorForm, ds);
        }
    }

    @Transactional
    private void updateUserRol(Usuario usuarioColaborador, List<PerfilCompania> perfilesCompaniaNuevos, Oficina oficinaMean, Colaborador colaborador, DataSessionPivot ds) {
        logger.info("ENTRA A UPDATE USER ROL");
        List<FuncionRol> funcionRolNuevos = funcionRolDAO.allByPerfilCompania(perfilesCompaniaNuevos);
        Map<Long, List<Rol>> mapRolNuevos = TypesUtil.convertListToMapList("rol.id", "rol", funcionRolNuevos);

        List<UsuarioRol> rolesUsuarioTengo = usuarioRolDAO.allUsuarioAndOficina(usuarioColaborador, oficinaMean);
        Map<Long, List<Rol>> mapRolTengo = TypesUtil.convertListToMapList("rol.id", "rol", rolesUsuarioTengo);

        for (UsuarioRol usuarioRol : rolesUsuarioTengo) {
            logger.info("ENTRA AL PRIMER LOOP");
            if (mapRolNuevos.get(usuarioRol.getRol().getId()) == null) {
                usuarioRol.setFechaFin(new Date());
                usuarioRol.setUsuario(usuarioColaborador);
                usuarioRol.setEstado(UserEstadoEnum.INA);
                usuarioRolDAO.update(usuarioRol);
            }
        }

        for (FuncionRol funcionRolNuevo : funcionRolNuevos) {
            logger.info("ENTRA AL SEGUNDO LOOP");
            if (!mapRolTengo.containsKey(funcionRolNuevo.getRol().getId())) {
                logger.info("ENTRA AL IF");

                ObjectUtil.printAttr(oficinaMean);

                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setEstado(UserEstadoEnum.ACT);
                usuarioRol.setFechaInicio(colaborador.getFechaInicio());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setOficina(oficinaMean);
                usuarioRol.setIdInstancia(oficinaMean.getInstanciaOficina());
                usuarioRol.setTipoOficina(oficinaMean.getTipoOficina().getCodigoEnum().name());
                usuarioRol.setUserRegistro(ds.getUsuario());
                usuarioRol.setUsuario(usuarioColaborador);
                usuarioRol.setRol(funcionRolNuevo.getRol());
                usuarioRolDAO.save(usuarioRol);
            }
        }
    }

    @Override
    public List<PerfilCompania> allFunciones() {
        return perfilCompaniaDAO.allTipoFuncion();
    }

    @Override
    public Persona verifiDocumento(Persona persona) {
        return personaDAO.findByDoc(persona);
    }

    @Override
    public Usuario verifiEmail(Persona persona) {
        Usuario u = usuarioDAO.findByGoogleEmail(persona.getEmailCompania());
        return u;
    }

    @Override
    @Transactional
    public void addCargo(PerfilCompania perfilCompania, DataSessionPivot dsp) {
        perfilCompania.setCodigo(getCodigoPerfilCompania());
        perfilCompania.setTipo(TipoPerfilCompaniaEnum.CARGO.toString());
        perfilCompania.setCompania(dsp.getCompania());
        perfilCompania.setEsAutomatico(1l);
        perfilCompaniaDAO.save(perfilCompania);
    }

    public String getCodigoPerfilCompania() {
        String codigoNuevo = "CAR";
        PerfilCompania compania = perfilCompaniaDAO.findUltimoCodigo();
        if (compania != null) {

            String codigoNume = compania.getCodigo().substring(3);

            codigoNuevo = codigoNuevo + (Long.parseLong(codigoNume) + 1);
        } else {
            codigoNuevo = codigoNuevo.concat("10001");
        }

        return codigoNuevo;

    }

    @Override
    public List<Persona> allPersonasByNombre(String nombre) {
        return personaDAO.allByNombre(nombre);
    }

    @Override
    @Transactional
    public void addFuncion(PerfilCompania perfilCompania, DataSessionPivot dsp) {
        PerfilCompania perfilCompaniaName = perfilCompaniaDAO.findFuncionByNombre(perfilCompania.getNombre());
        if (perfilCompaniaName != null) {
            throw new PhobosException("La función ingresada ya existe");
        }
        perfilCompania.setCodigo(this.getCodigoFuncionCompania());
        perfilCompania.setTipo(TipoPerfilCompaniaEnum.FUNCION.name());
        perfilCompania.setCompania(dsp.getCompania());
        perfilCompania.setEsAutomatico(1l);
        perfilCompaniaDAO.save(perfilCompania);
    }

    private String getCodigoFuncionCompania() {
        String codigoNuevo = "F10001";
        PerfilCompania perfilCompania = perfilCompaniaDAO.findUltimoCodigoFuncion();
        logger.debug("{}", perfilCompania);
        if (perfilCompania != null) {
            String codigoNume = perfilCompania.getCodigo().substring(1);
            codigoNuevo = "F" + (Long.parseLong(codigoNume) + 1);
        }
        return codigoNuevo;
    }

    @Override
    public List<PerfilCompania> allCargoByOficina(Oficina oficina) {
        return perfilCompaniaDAO.allCargoByOficina(oficina);
    }

    @Override
    public List<PerfilCompania> allFuncionByOficina(Oficina oficina) {
        return perfilCompaniaDAO.allFuncionByOficina(oficina);
    }

    @Override
    public List<PerfilCompania> allFuncionByColaborador(Colaborador colaborador) {
        List<FuncionColaborador> funcionColaborador = funcionColaboradorDAO.allByColaborador(colaborador);
        Map<Long, PerfilCompania> funcionesMap = TypesUtil.convertListToMap("funcion.id", "funcion", funcionColaborador);
        List<PerfilCompania> funciones = new ArrayList();
        for (PerfilCompania value : funcionesMap.values()) {
            funciones.add(value);
        }
        return funciones;
    }

}
