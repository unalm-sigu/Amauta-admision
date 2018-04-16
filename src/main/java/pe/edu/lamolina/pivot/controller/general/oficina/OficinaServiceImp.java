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
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Colaborador;
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
        oficina.setEstado(OficinaEstadoEnum.ACT);
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
            oficinaBD.setEstado(OficinaEstadoEnum.ACT);
        } else {
            oficinaBD.setEstado(OficinaEstadoEnum.INA);
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

        if (docentesBD.isEmpty()) {
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
        perfil.setEstado(EstadoEnum.ACT);
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
        colaborador.setCodigo(codigo() + "");
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
            perfil.setEstado(EstadoEnum.ACT);
            perfil.setFechaInicio(oficinaBD.getFechaInicioJefatura());
            perfil.setFechaRegistro(new Date());
            perfil.setUserRegistro(ds.getUsuario());
            personaPerfilDAO.save(perfil);
        }

        perfil.setFechaFin(oficina.getFechaFinJefatura());
        perfil.setEstado(EstadoEnum.CER);
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
        List<Oficina> list = new ArrayList<>();
        List<Oficina> oficinas = oficinaDAO.allAndSuperiorOfi();
        Map<Long, Oficina> map = TypesUtil.convertListToMap("id", oficinas);
        for (Oficina oficina : oficinas) {
            if (oficina.getOficinaSuperior() != null) {
                oficina.setOficinaSuperior(map.get(oficina.getId()));
            }
        }
        List<Oficina> oficinasUsuario = oficinaDAO.allByUser(persona);
        for (Oficina oficina : oficinasUsuario) {
            Oficina ofi = find(map, oficina);
            if (ofi != null) {

                list.add(ofi);
            }
        }
        return list;
    }

    public Oficina find(Map<Long, Oficina> map, Oficina oficina) {
        Oficina o = map.get(oficina.getId());
        if (o.getTipoOficina().getNivel().equalsIgnoreCase(TipoOficinaEnum.OFI.name())) {
            return o;
        } else {
            return find(map, o.getOficinaSuperior());
        }
    }

    @Override
    public Colaboradores countColaborador(Oficina oficina) {
        Colaboradores colaboradors = colaboradorDAO.countColaboradores(oficina);
        return colaboradors;
    }

    @Override
    public ArrayNode getData(DynatableFilter filter, Oficina oficinaMain) {
        List<Oficina> oficinas = listaOficinas(oficinaMain);

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
                array.add(objNode);
            } else {
                for (ColaboradorEstadoEnum value : ColaboradorEstadoEnum.values()) {
                    objNode = new ObjectNode(JsonNodeFactory.instance);
                    if (value.name().equalsIgnoreCase(colaborador.getEstado())) {

                    } else {
                        objNode.put("id", colaborador.getId());
                        objNode.put("name", value.name());
                        objNode.put("value", value.getValue());
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

    private List<Oficina> listaOficinas(Oficina oficinaMain) {
        List<Oficina> oficinas = oficinaDAO.all();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinas);

        for (Oficina oficina : oficinas) {
            oficina.setOficinasDependientes(new ArrayList<>());
        }
        for (Oficina oficina : oficinas) {
            if (oficina.getOficinaSuperior() != null) {
                Oficina sup = mapOficina.get(oficina.getOficinaSuperior().getId());
                sup.getOficinasDependientes().add(oficina);
            }
        }
        Oficina oficinaDb = mapOficina.get(oficinaMain.getId());
        List<Oficina> listOficinas = new ArrayList();
        listOficinas.add(oficinaDb);
        addOficinaDependiente(oficinaDb, listOficinas);
        return listOficinas;
    }

    private void addOficinaDependiente(Oficina oficinaMain, List<Oficina> oficinas) {
        for (Oficina oficinasDependiente : oficinaMain.getOficinasDependientes()) {
            oficinas.add(oficinasDependiente);
            addOficinaDependiente(oficinasDependiente, oficinas);
        }
    }

    public Long codigo() {
        Long código = 10001l;
        Colaborador colaborador = colaboradorDAO.findCodigo();
        if (colaborador.getCodigo() != null) {
            código = Long.valueOf(colaborador.getCodigo()) + 1;
        }
        return código;
    }

    @Override
    @Transactional
    public void updateEstado(Colaborador colaborador, Usuario usuario) {
        Colaborador col = colaboradorDAO.find(colaborador.getId());
        col.setEstado(ColaboradorEstadoEnum.getName(colaborador.getEstado()));
        col.setFechaModificacion(new Date());
        colaboradorDAO.update(col);
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
    public List<TipoDocIdentidad> findTipoDoc() {
        return tipoDocIdentidadDAO.all();
    }

    @Override
    public List<Oficina> findOficinas(Oficina oficina) {
        return listaOficinas(oficina);
    }

    @Override
    public List<PerfilCompania> allCargos() {
        return perfilCompaniaDAO.allTipoCargo();
    }

    @Override
    @Transactional
    public void saveColaborador(Colaborador colaborador, Usuario usuario) {
        Persona persona = colaborador.getPersona();
        persona.setFechaRegistro(new Date());
        persona.setUserRegistro(usuario);
        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setSexo(SexoEnum.get(colaborador.getPersona().getSexo()).name());
        personaDAO.save(persona);

        colaborador.setFechaRegistro(new Date());
        colaborador.setUserRegistro(usuario);
        colaborador.setEstado(ColaboradorEstadoEnum.ACT.name());
        colaborador.setCodigo(codigo() + "");
        colaborador.setFechaInicio(new Date());
        colaborador.setPersona(persona);
        colaboradorDAO.save(colaborador);

        ArrayList<PerfilCompania> list = new ArrayList();
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            funcionColaborador.setId(null);
            funcionColaborador.setFechaRegistro(new Date());
            funcionColaborador.setIdUserRegistro(BigInteger.valueOf(usuario.getId()));
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
            usuario1.setUserRegistro(usuario);
            usuarioDAO.save(usuario1);
            addUserRoll(list, colaborador, usuario, usuario1);
        }

    }

    @Override
    public void saveColaboradorExit(Colaborador colaborador, Usuario usuario) {
        Persona persona = colaborador.getPersona();
        personaDAO.update(persona);

        colaborador.setFechaRegistro(new Date());
        colaborador.setUserRegistro(usuario);
        colaborador.setEstado(ColaboradorEstadoEnum.ACT.name());
        colaborador.setCodigo(codigo() + "");
        colaborador.setFechaInicio(new Date());
        colaborador.setPersona(persona);
        colaboradorDAO.save(colaborador);

        ArrayList<PerfilCompania> list = new ArrayList();
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            funcionColaborador.setId(null);
            funcionColaborador.setFechaRegistro(new Date());
            funcionColaborador.setIdUserRegistro(BigInteger.valueOf(usuario.getId()));
            funcionColaborador.setEstado(EstadoEnum.ACT.name());
            funcionColaborador.setFuncion(perfil);
            funcionColaborador.setFechaInico(new Date());
            funcionColaborador.setColaborador(colaborador);
            funcionColaboradorDAO.save(funcionColaborador);
            list.add(perfil);
            funcionColaborador.setColaborador(colaborador);

        }
        Usuario usuario1 = new Usuario();
        usuario1 = usuarioDAO.findByPersona(persona);
        if (usuario1 == null) {
            if (colaborador.getPersona().getEmailCompania() != null) {
                usuario1.setEstadoEnum(UserEstadoEnum.ACT);
                usuario1.setGoogle(persona.getEmailCompania());
                usuario1.setPersona(persona);
                usuario1.setUserRegistro(usuario);
                usuarioDAO.save(usuario1);
                addUserRoll(list, colaborador, usuario, usuario1);
            }
        }

    }

    public void addUserRoll(List<PerfilCompania> list, Colaborador colaborador, Usuario usuario, Usuario colabo) {
        List<FuncionRol> funcionRol = funcionRolDAO.allByPerfilCompania(list);
        Map<Long, List<Rol>> mapRol = TypesUtil.convertListToMapList("perfilCompania.id", "rol", funcionRol);
        for (PerfilCompania compania : list) {
            for (Rol rol : mapRol.get(compania.getId())) {
                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setEstado(UserEstadoEnum.ACT);
                usuarioRol.setFechaInicio(new Date());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setOficina(colaborador.getOficina());
                usuarioRol.setRol(rol);
                usuarioRol.setUserRegistro(usuario);
                usuarioRol.setUsuario(colabo);
                usuarioRolDAO.save(usuarioRol);
            }
        }
    }

    @Override
    @Transactional
    public void updateColaborador(Colaborador colaborador, Usuario usuario) {
        Colaborador cola = colaboradorDAO.find(colaborador.getId());
        cola.setFechaModificacion(new Date());
        cola.setUserModificacion(usuario);
        cola.setFuncionColaborador(colaborador.getFuncionColaborador());
        cola.setCargo(colaborador.getCargo());
        cola.setOficina(colaborador.getOficina());
        colaboradorDAO.save(cola);

        List<FuncionColaborador> funcionColaboradors = funcionColaboradorDAO.findFuncionByColaborador(colaborador);
        Map<Long, FuncionColaborador> mapNuevo = TypesUtil.convertListToMap("id", colaborador.getFuncionColaborador());
        Map<Long, FuncionColaborador> mapTengo = TypesUtil.convertListToMap("funcion.id", funcionColaboradors);
        for (FuncionColaborador funcionColaborador1 : funcionColaboradors) {
            if (mapNuevo.get(funcionColaborador1.getFuncion().getId()) == null) {
                funcionColaborador1.setFechaFin(new Date());
                funcionColaborador1.setEstado(EstadoEnum.INA.name());
                funcionColaboradorDAO.update(funcionColaborador1);
            }
        }
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = new PerfilCompania();
            perfil.setId(funcionColaborador.getId());
            if (mapTengo.get(perfil.getId()) == null) {

                funcionColaborador.setId(null);
                funcionColaborador.setFechaRegistro(new Date());
                funcionColaborador.setIdUserRegistro(BigInteger.valueOf(usuario.getId()));
                funcionColaborador.setEstado(EstadoEnum.ACT.name());
                funcionColaborador.setColaborador(colaborador);
                funcionColaborador.setFuncion(perfil);
                funcionColaborador.setFechaInico(new Date());
                funcionColaboradorDAO.save(funcionColaborador);
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
}
