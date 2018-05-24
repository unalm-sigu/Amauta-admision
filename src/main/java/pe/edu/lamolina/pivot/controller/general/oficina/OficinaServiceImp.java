package pe.edu.lamolina.pivot.controller.general.oficina;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigInteger;
import java.util.ArrayList;
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
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import pe.edu.lamolina.model.enums.EstadoEnum;
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
        ObjectUtil.eliminarAttrSinId(oficina, "oficinaSuperior");
        ObjectUtil.eliminarAttrSinId(oficina, "cargoJefe");
        ObjectUtil.eliminarAttrSinId(oficina, "personaJefe");
        ObjectUtil.eliminarAttrSinId(oficina, "jefeEncargado");
        
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        oficinaBD.setOficinaSuperior(oficina.getOficinaSuperior());
        oficinaBD.setNombre(oficina.getNombre());
        oficinaBD.setCodigo(oficina.getCodigo());
        oficina.setTipoOficina(oficina.getTipoOficina());
        oficinaBD.setInstanciaOficina(oficina.getInstanciaOficina());
        oficinaBD.setCargoJefe(oficina.getCargoJefe());
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
    
    @Override
    @Transactional
    public void delete(Oficina oficina) {
        oficinaDAO.delete(oficina);
    }
    
    @Override
    public List<Colaborador> allColaborador(List<Oficina> oficinas) {
        if (oficinas.size() < 1) {
            return new ArrayList();
        }
        return colaboradorDAO.allColaborador(oficinas);
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
    public void estado(Oficina oficina) {
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        if (OficinaEstadoEnum.INA.name().equalsIgnoreCase(oficinaBD.getEstado())) {
            oficinaBD.setEstadoEnum(OficinaEstadoEnum.ACT);
        } else {
            oficinaBD.setEstadoEnum(OficinaEstadoEnum.INA);
        }
        oficinaDAO.update(oficinaBD);
    }
    
    @Override
    public List<Persona> allPersona(String nombre) {
        return personaDAO.allByNombre(nombre);
    }
    
    @Override
    public List<Colaborador> allColaboradorByOficina(Oficina oficina) {
        return colaboradorDAO.allColaboradorByOficina(oficina);
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
        if (ofi.getTipoOficina().getNivel().equalsIgnoreCase(TipoOficinaEnum.OFI.name())) {
            return ofi;
        } else {
            return find(mapOficinasTodas, ofi.getOficinaSuperior());
        }
    }
    
    @Override
    public Colaboradores countColaborador(Oficina oficina) {
        List<Oficina> oficinas = allOficinasByMain(oficina);
        Colaboradores colaboradors = colaboradorDAO.countColaboradores(oficinas);
        return colaboradors;
    }
    
    @Override
    public ArrayNode getColaboradoresJson(DynatableFilter filter, Oficina oficinaMain) {
        List<Oficina> oficinas = allOficinasByMain(oficinaMain);
        
        List<Colaborador> colaboradors = colaboradorDAO.allByOficina(filter, oficinas);
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
            node.put("dni", colaborador.getPersona() == null ? "" : colaborador.getPersona().getNumeroDocIdentidad());
            node.put("funciones", map.get(colaborador.getId()) == null ? 0 : map.get(colaborador.getId()).size());
            node.put("codigo", colaborador.getCodigo() == null ? "" : colaborador.getCodigo());
            node.set("estados", array);
            arrayNode.add(node);
        }
        
        return arrayNode;
    }
    
    private List<Oficina> allOficinasByMain(Oficina oficinaMain) {
        List<Oficina> oficinasTodas = oficinaDAO.all();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasTodas);
        
        for (Oficina oficina : oficinasTodas) {
            oficina.setOficinasDependientes(new ArrayList());
        }
        for (Oficina oficina : oficinasTodas) {
            if (oficina.getOficinaSuperior() != null) {
                Oficina sup = mapOficina.get(oficina.getOficinaSuperior().getId());
                sup.getOficinasDependientes().add(oficina);
            }
        }
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
    public List<Oficina> findOficinas(Oficina oficina) {
        return allOficinasByMain(oficina);
    }
    
    @Override
    public List<PerfilCompania> allCargos(Oficina oficina) {
        List<PerfilCompania> oficinaCompanias = perfilCompaniaDAO.allTipoCargoByOfi(oficina);
        List<PerfilCompania> companias = perfilCompaniaDAO.allTipoCargo();
        List<PerfilCompania> allCompanias = new ArrayList<PerfilCompania>();
        
        allCompanias.addAll(oficinaCompanias);
        allCompanias.addAll(companias);
        return allCompanias;
    }
    
    @Override
    public List<PerfilCompania> allCargosByOficina(Oficina oficina) {
        
        return perfilCompaniaDAO.allTipoCargoByOfi(oficina);
    }
    
    @Override
    @Transactional
    public void saveColaborador(Colaborador colaborador, Oficina oficinaMean, DataSessionPivot ds) {
        Persona persona = colaborador.getPersona();
        persona.setFechaRegistro(new Date());
        persona.setUserRegistro(ds.getUsuario());
        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setSexo(colaborador.getPersona().getSexo());
        personaDAO.save(persona);
        
        colaborador.setFechaRegistro(new Date());
        colaborador.setUserRegistro(ds.getUsuario());
        colaborador.setEstado(ColaboradorEstadoEnum.ACT.name());
        colaborador.setCodigo(getCodigoColaborador() + "");
        colaborador.setPersona(persona);
        colaboradorDAO.save(colaborador);
        
        ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
        colaboradorEstado.setColaborador(colaborador);
        colaboradorEstado.setEstado(ColaboradorEstadoEnum.ACT);
        colaboradorEstado.setUserRegistro(ds.getUsuario());
        colaboradorEstado.setFechaRegistro(new Date());
        colaboradorEstadoDAO.save(colaboradorEstado);
        
        PersonaCargo personaCargo = new PersonaCargo();
        personaCargo.setCompania(ds.getCompania());
        personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
        personaCargo.setFechaInicio(colaborador.getFechaInicio());
        personaCargo.setFechaRegistro(new Date());
        personaCargo.setOficina(colaborador.getOficina());
        personaCargo.setPerfilCompania(colaborador.getCargo());
        personaCargo.setPersona(persona);
        personaCargo.setUserRegistro(ds.getUsuario());
        personaPerfilDAO.save(personaCargo);
        
        ArrayList<PerfilCompania> list = new ArrayList();
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            funcionColaborador.setFechaRegistro(new Date());
            funcionColaborador.setUserRegistro(ds.getUsuario());
            funcionColaborador.setEstado(EstadoEnum.ACT.name());
            funcionColaborador.setColaborador(colaborador);
            funcionColaborador.setFuncion(perfil);
            funcionColaborador.setFechaInico(new Date());
            funcionColaboradorDAO.save(funcionColaborador);
            list.add(perfil);
        }
        Usuario usuario1 = new Usuario();
        
        if (persona.getEmailCompania() != null) {
            usuario1.setEstadoEnum(UserEstadoEnum.ACT);
            usuario1.setGoogle(persona.getEmailCompania());
            usuario1.setPersona(persona);
            usuario1.setUserRegistro(ds.getUsuario());
            usuario1.setFechaRegistro(new Date());
            usuarioDAO.save(usuario1);
            addUserRoll(list, oficinaMean, usuario1, colaborador, ds);
        }
        
    }
    
    @Override
    @Transactional
    public Boolean saveColaboradorExit(Colaborador colaborador, Oficina oficinaMean, DataSessionPivot ds) throws PhobosException {
        Oficina oficina = colaborador.getOficina();
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
        
        Colaborador colaboradors = colaboradorDAO.allActivosByPersonaAndOficina(oficina, personaBD);
        if (colaboradors != null) {
            throw new PhobosException("El colaborador existe en la oficina");
            
        } else {
            colaborador.setFechaRegistro(new Date());
            colaborador.setUserRegistro(ds.getUsuario());
            colaborador.setEstado(ColaboradorEstadoEnum.ACT.name());
            colaborador.setCodigo(getCodigoColaborador() + "");
            colaboradorDAO.save(colaborador);
            
            ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
            colaboradorEstado.setColaborador(colaborador);
            colaboradorEstado.setEstado(ColaboradorEstadoEnum.ACT);
            colaboradorEstado.setUserRegistro(ds.getUsuario());
            colaboradorEstado.setFechaRegistro(new Date());
            colaboradorEstadoDAO.save(colaboradorEstado);
            
            PersonaCargo personaCargo = new PersonaCargo();
            personaCargo.setCompania(ds.getCompania());
            personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
            personaCargo.setFechaInicio(colaborador.getFechaInicio());
            personaCargo.setFechaRegistro(new Date());
            personaCargo.setOficina(colaborador.getOficina());
            personaCargo.setPerfilCompania(colaborador.getCargo());
            personaCargo.setPersona(personaBD);
            personaCargo.setUserRegistro(ds.getUsuario());
            personaPerfilDAO.save(personaCargo);
        }
        
        ArrayList<PerfilCompania> perfiles = new ArrayList();
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            funcionColaborador.setFechaRegistro(new Date());
            funcionColaborador.setUserRegistro(ds.getUsuario());
            funcionColaborador.setEstado(EstadoEnum.ACT.name());
            funcionColaborador.setFuncion(perfil);
            funcionColaborador.setFechaInico(new Date());
            funcionColaborador.setColaborador(colaborador);
            funcionColaboradorDAO.save(funcionColaborador);
            perfiles.add(perfil);
        }
        
        Usuario usuario1 = usuarioDAO.findByPersona(personaForm);
        if (usuario1 == null) {
            usuario1 = new Usuario();
            if (colaborador.getPersona().getEmailCompania() != null) {
                usuario1 = addUser(personaForm, ds.getUsuario());
                addUserRoll(perfiles, oficinaMean, usuario1, colaborador, ds);
            }
        } else {
            UsuarioRol ur = usuarioRolDAO.findUsuarioAndOficina(usuario1, oficina);
            if (ur == null) {
                addUserRoll(perfiles, oficinaMean, usuario1, colaborador, ds);
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
    
    private void addUserRoll(List<PerfilCompania> perfilesCompania, Oficina oficinaMean, Usuario Usuariocolaborador, Colaborador colaborador, DataSessionPivot ds) {
        List<FuncionRol> funcionRol = funcionRolDAO.allByPerfilCompania(perfilesCompania);
        Map<Long, List<Rol>> mapRol = TypesUtil.convertListToMapList("perfilCompania.id", "rol", funcionRol);
        for (PerfilCompania compania : perfilesCompania) {
            for (Rol rol : mapRol.get(compania.getId())) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setEstado(UserEstadoEnum.ACT);
                usuarioRol.setFechaInicio(colaborador.getFechaInicio());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setOficina(oficinaMean);
                usuarioRol.setRol(rol);
                usuarioRol.setUserRegistro(ds.getUsuario());
                usuarioRol.setUsuario(Usuariocolaborador);
                usuarioRolDAO.save(usuarioRol);
            }
        }
    }
    
    @Override
    @Transactional
    public void updateColaborador(Colaborador colaboradorForm, Oficina oficinaMea, DataSessionPivot ds) {
        Colaborador colaboradorBD = colaboradorDAO.find(colaboradorForm.getId());
        Oficina oficinaAnterior = colaboradorBD.getOficina();
        
        ObjectUtil.printAttr(colaboradorForm);
        
        colaboradorBD.setFechaModificacion(new Date());
        colaboradorBD.setUserModificacion(ds.getUsuario());
        colaboradorBD.setCargo(colaboradorForm.getCargo());
        colaboradorBD.setOficina(colaboradorForm.getOficina());
        colaboradorBD.setFechaInicio(colaboradorForm.getFechaInicio());
        colaboradorDAO.update(colaboradorBD);
        
        if (colaboradorForm.getOficina().getId() != oficinaAnterior.getId()) {
            PersonaCargo personaCargo = personaPerfilDAO.findCargoByPersona(oficinaAnterior, colaboradorForm.getCargo(), colaboradorForm.getPersona());
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
            updateUserRol(usuarioColaborador, perfiles, oficinaMea, colaboradorForm, ds);
        }
    }
    
    private void updateUserRol(Usuario usuarioColaborador, List<PerfilCompania> perfilesCompaniaNuevos, Oficina oficinaMean, Colaborador colaborador, DataSessionPivot ds) {
        List<FuncionRol> funcionRolNuevos = funcionRolDAO.allByPerfilCompania(perfilesCompaniaNuevos);
        Map<Long, List<Rol>> mapRolNuevos = TypesUtil.convertListToMapList("rol.id", "rol", funcionRolNuevos);
        
        List<UsuarioRol> rolesUsuarioTengo = usuarioRolDAO.allUsuarioAndOficina(usuarioColaborador, oficinaMean);
        Map<Long, List<Rol>> mapRolTengo = TypesUtil.convertListToMapList("rol.id", "rol", rolesUsuarioTengo);
        
        for (UsuarioRol usuarioRol : rolesUsuarioTengo) {
            if (mapRolNuevos.get(usuarioRol.getRol().getId()) == null) {
                usuarioRol.setFechaFin(new Date());
                usuarioRol.setUsuario(usuarioColaborador);
                usuarioRol.setEstado(UserEstadoEnum.INA);
                usuarioRolDAO.update(usuarioRol);
            }
        }
        
        for (FuncionRol funcionRolNuevo : funcionRolNuevos) {
            if (mapRolTengo.get(funcionRolNuevo.getRol().getId()) == null) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setEstado(UserEstadoEnum.ACT);
                usuarioRol.setFechaInicio(colaborador.getFechaInicio());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setOficina(oficinaMean);
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
}
