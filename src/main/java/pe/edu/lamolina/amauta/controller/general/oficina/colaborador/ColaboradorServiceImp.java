package pe.edu.lamolina.amauta.controller.general.oficina.colaborador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.DESP;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.RET;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.NivelOficinaEnum;
import pe.edu.lamolina.model.enums.OficinaEnum;
import pe.edu.lamolina.model.enums.PerfilColaboradorEnum;
import pe.edu.lamolina.model.enums.PerfilEstadoEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.TipoPerfilCompaniaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.ColaboradorEstado;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.FuncionColaborador;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.medico.Medico;
import pe.edu.lamolina.model.seguridad.FuncionRol;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.academico.AlumnoDAO;
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.academico.FacultadDAO;
import pe.edu.lamolina.amauta.dao.general.AusenciaJefeDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorEstadoDAO;
import pe.edu.lamolina.amauta.dao.general.FuncionColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.RolDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.dao.general.PersonaCargoDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.general.TipoOficinaDAO;
import pe.edu.lamolina.amauta.dao.medico.MedicoDAO;
import pe.edu.lamolina.amauta.dao.seguridad.FuncionRolDAO;

@Service
@Transactional(readOnly = true)
public class ColaboradorServiceImp implements ColaboradorService {

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
    PersonaCargoDAO personaCargoDAO;

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

    @Autowired
    AlumnoDAO alumnoDAO;

    @Autowired
    VerificadorService verificadorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Override
//    public List<Oficina> allByDynatable(DynatableFilter filter, Compania compania) {
//        return oficinaDAO.allByFilter(filter, compania);
//    }
    @Override
    public Oficina findOficina(Oficina oficina) {
        return oficinaDAO.find(oficina.getId());
    }

//    @Override
//    @Transactional
//    public void update(Oficina oficina, DataSessionPivot ds) {
//        ObjectUtil.eliminarAttrSinId(oficina);
//
//        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
//        oficinaBD.setOficinaSuperior(oficina.getOficinaSuperior());
//        oficinaBD.setNombre(oficina.getNombre());
//        oficinaBD.setCodigo(oficina.getCodigo());
//        oficinaBD.setInstanciaOficina(oficina.getInstanciaOficina());
//        oficinaBD.setCargoJefe(oficina.getCargoJefe());
//        oficinaBD.setTipoOficina(oficina.getTipoOficina());
//        oficinaDAO.update(oficinaBD);
//    }
//
//    @Override
//    @Transactional
//    public void save(Oficina oficina, DataSessionPivot ds) {
//        ObjectUtil.eliminarAttrSinId(oficina, "oficinaSuperior");
//        ObjectUtil.eliminarAttrSinId(oficina, "cargoJefe");
//        ObjectUtil.eliminarAttrSinId(oficina, "personaJefe");
//        ObjectUtil.eliminarAttrSinId(oficina, "jefeEncargado");
//        oficina.setTipoOficina(oficina.getTipoOficina());
//        oficina.setEstadoEnum(OficinaEstadoEnum.ACT);
//        oficina.setFechaRegistro(new Date());
//        oficina.setUserRegistro(ds.getUsuario());
//        oficinaDAO.save(oficina);
//    }
//    @Override
//    @Transactional
//    public void delete(Oficina oficina) {
//        oficinaDAO.delete(oficina);
//    }
//    @Override
//    public List<Colaborador> allColaborador(List<Oficina> oficinas) {
//        if (oficinas.size() < 1) {
//            return new ArrayList();
//        }
//        return colaboradorDAO.allByOficinas(oficinas);
//    }
//
//    @Override
//    public List<Oficina> allUnidadSuperior(String nombre, Compania compania) {
//        return oficinaDAO.allUnidadSuperior(nombre, compania);
//    }
//
//    @Override
//    public List<DepartamentoAcademico> allDepartamento(Compania compania) {
//        return departamentoAcademicoDAO.allByCompania(compania);
//    }
//
//    @Override
//    public List<Carrera> allCarrera(Compania compania) {
//        return carreraDAO.allByCompania(compania);
//    }
//
//    @Override
//    public List<Facultad> allFacultad(Compania compania) {
//        return facultadDAO.allByCompania(compania);
//    }
//
//    @Override
//    @Transactional
//    public void cambiarEstado(Oficina oficina, String accion) {
//        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
//        Assert.isNotNull(oficinaBD, "El registro de la oficina no existe en el sistema");
//
//        if (accion.equals("desactivar")) {
//            Assert.isFalse(oficinaBD.getEstadoEnum() == OficinaEstadoEnum.INA, "La oficina ya se encuentra desactivada");
//            List<Colaborador> colaboradores = colaboradorDAO.allActivosByOficina(oficinaBD);
//            Assert.isTrue(colaboradores.isEmpty(), "No puede desactivar una oficina que contiene colaboradores activos");
//
//            oficinaBD.setEstadoEnum(OficinaEstadoEnum.INA);
//            oficinaDAO.update(oficinaBD);
//
//        } else if (accion.equals("activar")) {
//            Assert.isFalse(oficinaBD.getEstadoEnum() == OficinaEstadoEnum.ACT, "La oficina ya se encuentra activada");
//            oficinaBD.setEstadoEnum(OficinaEstadoEnum.ACT);
//            oficinaDAO.update(oficinaBD);
//
//        } else if (accion.equals("eliminar")) {
//            List<Colaborador> colaboradores = colaboradorDAO.allByOficina(oficinaBD);
//            Assert.isTrue(colaboradores.isEmpty(), "El registro de esta oficina se encuentra relacionada a otros elementos del sistema y no podrá ser eliminada");
//            oficinaDAO.delete(oficinaBD);
//        }
//    }
//
//    @Override
//    public List<Persona> allPersona(String nombre) {
//        return personaDAO.allByNombre(nombre);
//    }
//
//    @Override
//    public List<Colaborador> allColaboradorByOficina(Oficina oficina) {
//        return colaboradorDAO.allByOficina(oficina);
//    }
//
//    @Override
//    public List<PerfilCompania> allCargo(String nombre) {
//        return perfilCompaniaDAO.allByNombre(nombre);
//    }
//
//    @Override
//    public void fillReferencia(Oficina oficina) {
//        TipoOficina tipo = oficina.getTipoOficina();
//        if (TipoOficinaEnum.DPTO.name().equalsIgnoreCase(tipo.getCodigo())) {
//            DepartamentoAcademico departamento = departamentoAcademicoDAO.find(oficina.getInstanciaOficina());
//            oficina.setInstanciaOficinaCodigo(departamento.getCodigo());
//            oficina.setInstanciaOficinaNombre(departamento.getNombreLargo());
//        }
//        if (TipoOficinaEnum.ESP.name().equalsIgnoreCase(tipo.getCodigo())) {
//            Carrera carrera = carreraDAO.find(oficina.getInstanciaOficina());
//            oficina.setInstanciaOficinaCodigo(carrera.getCodigo());
//            oficina.setInstanciaOficinaNombre(carrera.getNombre());
//        }
//        if (TipoOficinaEnum.FAC.name().equalsIgnoreCase(tipo.getCodigo())) {
//            Facultad facultad = facultadDAO.find(oficina.getInstanciaOficina());
//            oficina.setInstanciaOficinaCodigo(facultad.getCodigo());
//            oficina.setInstanciaOficinaNombre(facultad.getNombre());
//        }
//    }
//
//    @Override
//    @Transactional
//    public void asignarJefe(Oficina oficina, DataSessionPivot ds) {
//        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
//        TipoOficina tipo = oficinaBD.getTipoOficina();
//        List<Docente> docentesBD = docenteDAO.allByPersona(oficina.getPersonaJefe());
//
//        if (oficinaBD.getPersonaJefe() != null) {
//            throw new PhobosException("Esta Unidad ya tiene asignado un jefe");
//        }
//
//        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
//        if (oficina.getFechaInicioJefatura().after(hoy)) {
//            throw new PhobosException("No puede poner como fecha de inicio un día futuro");
//        }
//
//        if (oficinaBD.getCargoJefe() == null) {
//            throw new PhobosException("Falta definir el Cargo de la jefatura de esta Unidad");
//        }
//
//        if (tipo.getNivel().equals("OFI") && docentesBD.isEmpty()) {
//            throw new PhobosException("La persona seleccionada no es un docente. Elija un docente activo.");
//        }
//
//        oficinaBD.setPersonaJefe(oficina.getPersonaJefe());
//        oficinaBD.setFechaInicioJefatura(oficina.getFechaInicioJefatura());
//        oficinaDAO.update(oficinaBD);
//
//        if (oficina.getPersonaJefe().getTituloAcademico() != null) {
//            Persona jefeBD = personaDAO.find(oficina.getPersonaJefe().getId());
//            jefeBD.setTituloAcademico(oficina.getPersonaJefe().getTituloAcademico());
//            personaDAO.update(jefeBD);
//        }
//
//        PersonaCargo perfil = new PersonaCargo();
//        perfil.setCompania(ds.getCompania());
//        perfil.setPersona(oficinaBD.getPersonaJefe());
//        perfil.setPerfilCompania(oficinaBD.getCargoJefe());
//        perfil.setOficina(oficinaBD);
//        perfil.setEstadoEnum(PerfilEstadoEnum.ACT);
//        perfil.setFechaInicio(oficinaBD.getFechaInicioJefatura());
//        perfil.setFechaRegistro(new Date());
//        perfil.setUserRegistro(ds.getUsuario());
//        personaCargoDAO.save(perfil);
//
//        Colaborador colaborador = new Colaborador();
//        colaborador.setEstado(ColaboradorEstadoEnum.ACT.toString());
//        colaborador.setFechaInicio(new Date());
//        colaborador.setOficina(oficinaBD);
//        colaborador.setPersona(oficinaBD.getPersonaJefe());
//        colaborador.setUserRegistro(ds.getUsuario());
//        colaborador.setCargo(oficinaBD.getCargoJefe());
//        colaborador.setCodigo(getCodigoColaborador() + "");
//        colaboradorDAO.save(colaborador);
//
//        this.asignarRol(oficinaBD.getPersonaJefe(), RolEnum.JEFE_DPTO_ACA, ds);
//
//    }
//
//    @Override
//    @Transactional
//    public void retirarJefe(Oficina oficina, DataSessionPivot ds) {
//        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
//
//        Long idJefe = (Long) ObjectUtil.getParentTree(oficinaBD, "personaJefe.id");
//        if (idJefe == null) {
//            throw new PhobosException("Esta Unidad no tiene jefe asignado");
//        }
//        if (idJefe.longValue() != oficina.getPersonaJefe().getId()) {
//            throw new PhobosException("No coinciden el Jefe de la Unidad y los datos enviados");
//        }
//
//        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
//        if (oficina.getFechaFinJefatura().after(hoy)) {
//            throw new PhobosException("No puede poner como fecha final un día futuro");
//        }
//
//        if (oficinaBD.getFechaInicioJefatura().after(oficina.getFechaFinJefatura())) {
//            throw new PhobosException("La fecha final no puede ser antes de la fecha de inicio");
//        }
//
//        PersonaCargo perfil = personaCargoDAO.findSinCerrarByOficina(oficinaBD, ds.getCompania());
//        if (perfil == null) {
//            perfil = new PersonaCargo();
//            perfil.setCompania(ds.getCompania());
//            perfil.setPersona(oficinaBD.getPersonaJefe());
//            perfil.setPerfilCompania(oficinaBD.getCargoJefe());
//            perfil.setOficina(oficinaBD);
//            perfil.setEstadoEnum(PerfilEstadoEnum.ACT);
//            perfil.setFechaInicio(oficinaBD.getFechaInicioJefatura());
//            perfil.setFechaRegistro(new Date());
//            perfil.setUserRegistro(ds.getUsuario());
//            personaCargoDAO.save(perfil);
//        }
//
//        perfil.setFechaFin(oficina.getFechaFinJefatura());
//        perfil.setEstadoEnum(PerfilEstadoEnum.CER);
//        perfil.setUserModificacion(ds.getUsuario());
//        perfil.setFechaModificacion(new Date());
//        personaCargoDAO.update(perfil);
//
//        oficinaBD.setPersonaJefe(null);
//        oficinaBD.setFechaInicioJefatura(null);
//        oficinaDAO.update(oficinaBD);
//
//    }
//
//    @Override
//    @Transactional
//
//    public void asignarEncargado(Oficina oficina, DataSessionPivot ds) {
//
//        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
//        if (oficinaBD.getJefeEncargado() != null) {
//            throw new PhobosException("Esta Unidad ya tiene asignado un jefe encargado");
//        }
//
//        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
//        if (oficina.getFechaEncargatura().after(hoy)) {
//            throw new PhobosException("No puede poner como fecha de inicio un día futuro");
//        }
//
//        if (oficinaBD.getCargoJefe() == null) {
//            throw new PhobosException("Falta definir el Cargo de la jefatura de esta Unidad");
//        }
//
//        oficinaBD.setJefeEncargado(oficina.getJefeEncargado());
//        oficinaBD.setMotivoAusenciaJefe(oficina.getMotivoAusenciaJefe());
//        oficinaBD.setFechaEncargatura(oficina.getFechaEncargatura());
//        oficinaDAO.update(oficinaBD);
//
//        if (oficina.getJefeEncargado().getTituloAcademico() != null) {
//            Persona jefeBD = personaDAO.find(oficina.getJefeEncargado().getId());
//            jefeBD.setTituloAcademico(oficina.getJefeEncargado().getTituloAcademico());
//            personaDAO.update(jefeBD);
//        }
//
//        AusenciaJefe ausenciaJefe = new AusenciaJefe();
//        ausenciaJefe.setJefe(oficinaBD.getPersonaJefe());
//        ausenciaJefe.setEncargado(oficina.getJefeEncargado());
//        ausenciaJefe.setFechaInicioEncargatura(oficina.getFechaEncargatura());
//        ausenciaJefe.setFechaRegistro(new Date());
//        ausenciaJefe.setOficina(oficinaBD);
//        ausenciaJefe.setUserRegistro(ds.getUsuario());
//
//        ausenciaJefe.setMotivo(oficina.getMotivoAusenciaJefe());
//        if (oficinaBD.getPersonaJefe() == null) {
//            ausenciaJefe.setMotivo("Encargado por falta de nombramiento de jefe oficial");
//        }
//        ausenciaJefeDAO.save(ausenciaJefe);
//
//    }
//
//    @Override
//    @Transactional
//    public void retirarEncargado(AusenciaJefe ausencia, DataSessionPivot ds) {
//
//        Oficina oficinaBD = oficinaDAO.find(ausencia.getOficina().getId());
//        AusenciaJefe ausenciaBD = ausenciaJefeDAO.findSinCerrar(ausencia);
//
//        if (ausenciaBD == null) {
//            throw new PhobosException("No existe una encargatura pendiente de cierre con estos datos para esta unidad");
//        }
//        Long idEncargado = (Long) ObjectUtil.getParentTree(oficinaBD, "jefeEncargado.id");
//        if (idEncargado == null) {
//            throw new PhobosException("No existe Jefe Encargado para esta unidad");
//        }
//        if (idEncargado.longValue() != ausencia.getEncargado().getId()) {
//            throw new PhobosException("No coinciden el Jefe Encargado de la Unidad y la encargatura que desea cerrar");
//        }
//        Date hoy = new DateTime().withTimeAtStartOfDay().toDate();
//        if (ausencia.getFechaFinEncargatura().after(hoy)) {
//            throw new PhobosException("No puede poner como fecha final un día futuro");
//        }
//        if (ausenciaBD.getFechaInicioEncargatura().after(ausencia.getFechaFinEncargatura())) {
//            throw new PhobosException("La fecha final no puede ser antes de la fecha de inicio");
//        }
//
//        ausenciaBD.setFechaFinEncargatura(ausencia.getFechaFinEncargatura());
//        ausenciaBD.setUserModificacion(ds.getUsuario());
//        ausenciaBD.setFechaModificacion(new Date());
//        ausenciaJefeDAO.update(ausenciaBD);
//
//        oficinaBD.setJefeEncargado(null);
//        oficinaBD.setFechaEncargatura(null);
//        oficinaBD.setMotivoAusenciaJefe(null);
//        oficinaDAO.update(oficinaBD);
//    }
//
//    private void asignarRol(Persona personaJefe, RolEnum rolEnum, DataSessionPivot ds) {
//        Usuario usuarioDb = usuarioDAO.findActivoByPersona(personaJefe);
//
//        if (usuarioDb == null) {
//            throw new PhobosException("La persona asignada no está registrado como usuario.");
//        } else {
//            Rol rol = rolDAO.findByCode(rolEnum);
//            UsuarioRol userRol = usuarioRolDAO.findByUsuarioAndRol(usuarioDb, rol);
//            if (userRol == null) {
//                logger.debug("creando rol");
//                userRol = new UsuarioRol();
//                userRol.setEstado(UserEstadoEnum.ACT);
//                userRol.setFechaInicio(new Date());
//                userRol.setUserRegistro(ds.getUsuario());
//                userRol.setUsuario(usuarioDb);
//                userRol.setRol(rol);
//                usuarioRolDAO.save(userRol);
//            }
//        }
//    }
//
//    @Override
//    public List<Oficina> allOficina(Persona persona) {
//        List<Oficina> oficinasSuperiorPersona = new ArrayList<>();
//        List<Oficina> oficinasTodas = oficinaDAO.allAndSuperiorOfi();
//        Map<Long, Oficina> mapOficinasTodas = TypesUtil.convertListToMap("id", oficinasTodas);
//        for (Oficina oficina : oficinasTodas) {
//            if (oficina.getOficinaSuperior() != null) {
//                oficina.setOficinaSuperior(mapOficinasTodas.get(oficina.getOficinaSuperior().getId()));
//            }
//        }
//        List<Oficina> oficinasPersona = oficinaDAO.allByUser(persona);
//        for (Oficina oficinaPerso : oficinasPersona) {
//            Oficina oficina = find(mapOficinasTodas, oficinaPerso);
//            if (oficina != null) {
//                oficinasSuperiorPersona.add(oficina);
//            }
//        }
//        return oficinasSuperiorPersona;
//    }
//
//    private Oficina find(Map<Long, Oficina> mapOficinasTodas, Oficina oficina) {
//        Oficina ofi = mapOficinasTodas.get(oficina.getId());
//        if (ofi.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
//            return ofi;
//        } else {
//            return find(mapOficinasTodas, ofi.getOficinaSuperior());
//        }
//    }
    @Override
    public ResumenColaborador getResumenColoboradores(Oficina oficina) {
        List<Oficina> oficinas = allAreasByMain(oficina);
        ResumenColaborador colaboradors = colaboradorDAO.countByOficinas(oficinas);
        return colaboradors;
    }

//    @Override
//    public ArrayNode getColaboradoresJson(DynatableFilter filter, Oficina oficinaMain) {
//        List<Oficina> oficinas = allAreasByMain(oficinaMain);
//
//        List<Colaborador> colaboradores = colaboradorDAO.allDynatableByOficina(filter, oficinas);
//        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
//        List<FuncionColaborador> funcionesColaboradores = funcionColaboradorDAO.allByColaboradores(colaboradores);
//        Map<Long, List<FuncionColaborador>> mapFunciones = TypesUtil.convertListToMapList("colaborador.id", funcionesColaboradores);
//
//        for (Colaborador colaborador : colaboradores) {
//            String columnas = "id,estado,estadoEnum,codigo,"
//                    + "oficina.nombre,cargo.nombre,persona.tipoDocumento.simbolo,"
//                    + "persona.id,persona.nombreCompleto,persona.numeroDocIdentidad,persona.emailCompania";
//
//            ObjectNode node = JsonHelper.createJson(colaborador, JsonNodeFactory.instance, true, columnas.split(","));
//            node.put("funciones", TypesUtil.getListNotNull(mapFunciones.get(colaborador.getId())).size());
//            arrayNode.add(node);
//        }
//
//        return arrayNode;
//    }
    @Override
    public List<Colaborador> getColaboradores(DynatableFilter filter, Oficina oficinaMain) {
        List<Oficina> oficinas = allAreasByMain(oficinaMain);
        return colaboradorDAO.allDynatableByOficina(filter, oficinas);
    }

    @Override
    public List<FuncionColaborador> allFuncionesByColaboradores(List<Colaborador> colaboradores) {
        return funcionColaboradorDAO.allByColaboradores(colaboradores);
    }

    private List<Oficina> allAreasByMain(Oficina oficinaMain) {
        List<Oficina> oficinasTodas = getOficinasOrganizadas();
        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinasTodas);

        Oficina oficinaMainBD = mapOficina.get(oficinaMain.getId());
        List<Oficina> oficinas = new ArrayList();
        oficinas.add(oficinaMainBD);
        agregarOficinasHijas(oficinaMainBD, oficinas);

        return oficinas;
    }

    private void agregarOficinasHijas(Oficina oficinaMain, List<Oficina> oficinas) {
        for (Oficina oficinaHija : oficinaMain.getOficinasDependientes()) {
            if (oficinaHija.getTipoOficina().getNivelEnum() == NivelOficinaEnum.UNA) {
                oficinas.add(oficinaHija);
                agregarOficinasHijas(oficinaHija, oficinas);
            }
        }
    }

//    @Override
//    public List<Oficina> allOficinasMainByPersona(Persona persona) {
//        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(persona);
//        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("oficina.id", "oficina", colaboradores);
//        List<Oficina> oficinasHijas = new ArrayList(mapOficinas.values());
//        List<Oficina> oficinasMain = new ArrayList();
//
//        List<Oficina> oficinasTodas = getOficinasOrganizadas();
//        for (Oficina ofi : oficinasHijas) {
//            Oficina main = findOficinaMain(ofi, oficinasTodas);
//            oficinasMain.add(main);
//        }
//        return oficinasMain;
//
//    }
//
//    private Oficina findOficinaMain(Oficina oficinaHija, List<Oficina> oficinas) {
//        Map<Long, Oficina> mapOficina = TypesUtil.convertListToMap("id", oficinas);
//        Oficina oficinaTempo = mapOficina.get(oficinaHija.getId());
//        if (oficinaTempo.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
//            return oficinaTempo;
//        }
//        for (;;) {
//            Oficina sup = oficinaTempo.getOficinaSuperior();
//            if (sup == null) {
//                return null;
//            }
//            if (sup.getTipoOficina().getNivelEnum() == NivelOficinaEnum.OFI) {
//                return sup;
//            }
//            oficinaTempo = sup;
//        }
//
//    }
//    @Override
//    public Oficina findOficinaHija(Persona persona, Oficina oficinaMain) {
//        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(persona);
//        Map<Long, Oficina> mapOficinas = TypesUtil.convertListToMap("oficina.id", "oficina", colaboradores);
//        List<Oficina> oficinasHijas = new ArrayList(mapOficinas.values());
//
//        List<Oficina> oficinasTodas = getOficinasOrganizadas();
//        for (Oficina ofi : oficinasHijas) {
//            Oficina main = findOficinaMain(ofi, oficinasTodas);
//            if (main.getId() == oficinaMain.getId().longValue()) {
//                return ofi;
//            }
//        }
//        return null;
//    }
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
    public void updateEstado(Colaborador empleadoForm, Oficina oficina, DataSessionPivot ds) {
        boolean puedeVerOficina = verificadorService.puedeVerOficina(oficina, ds);
        Assert.isTrue(puedeVerOficina, "No tiene permiso de modificar los estados de los colaboradores en esta oficina");

        Colaborador empleadoBD = colaboradorDAO.find(empleadoForm.getId());
        Assert.isNotNull(empleadoBD, "No existe el colaborador que desea modificar su estado");
        Assert.isFalse(empleadoBD.getEstadoEnum() == empleadoForm.getEstadoEnum(),
                "El estado del colaborador ya es " + empleadoForm.getEstadoEnum().getValue());

        ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
        colaboradorEstado.setColaborador(empleadoBD);
        colaboradorEstado.setEstadoEnum(empleadoForm.getEstadoEnum());
        colaboradorEstado.setUserRegistro(ds.getUsuario());
        colaboradorEstado.setFechaRegistro(new Date());
        colaboradorEstadoDAO.save(colaboradorEstado);

        if (Arrays.asList(DESP, RET).contains(empleadoForm.getEstadoEnum())) {
            despedirEmpleado(empleadoBD, empleadoForm, ds);
        } else if (empleadoForm.getEstadoEnum() == ColaboradorEstadoEnum.ACT) {
            reactivarEmpleado(empleadoBD, empleadoForm, ds);
        } else {
            empleadoBD.setEstadoEnum(empleadoForm.getEstadoEnum());
            empleadoBD.setUserModificacion(ds.getUsuario());
            empleadoBD.setFechaModificacion(new Date());
            colaboradorDAO.update(empleadoBD);
        }

    }

    private void despedirEmpleado(Colaborador empleadoBD, Colaborador empleadoForm, DataSessionPivot ds) {
        empleadoBD.setEstadoEnum(empleadoForm.getEstadoEnum());
        empleadoBD.setFechaFin(empleadoForm.getFechaFin());
        empleadoBD.setUserModificacion(ds.getUsuario());
        empleadoBD.setFechaModificacion(new Date());
        colaboradorDAO.update(empleadoBD);

        List<PersonaCargo> personaCargo = personaCargoDAO.allByPersonaOficina(empleadoBD.getPersona(), empleadoBD.getOficina());
        for (PersonaCargo pp : personaCargo) {
            pp.setEstadoEnum(PerfilEstadoEnum.INA);
            pp.setFechaFin(empleadoForm.getFechaFin());
            pp.setFechaModificacion(new Date());
            pp.setUserModificacion(ds.getUsuario());
            personaCargoDAO.update(pp);
        }

        List<FuncionColaborador> funciones = funcionColaboradorDAO.allByColaborador(empleadoBD);
        for (FuncionColaborador fc : funciones) {
            fc.setEstadoEnum(EstadoEnum.INA);
            fc.setFechaFin(empleadoForm.getFechaFin());
            fc.setUserModificacion(ds.getUsuario());
            fc.setFechaModificacion(new Date());
            funcionColaboradorDAO.update(fc);
        }

        Usuario userEmpleado = usuarioDAO.findActivoByPersona(empleadoBD.getPersona());

        List<UsuarioRol> userRoles = usuarioRolDAO.allByUserOficina(userEmpleado, empleadoBD.getOficina());
        for (UsuarioRol ur : userRoles) {
            ur.setEstadoEnum(UserEstadoEnum.INA);
            ur.setFechaFin(new Date());
            ur.setUserFinaliza(ds.getUsuario());
            usuarioRolDAO.update(ur);
        }

        List<Alumno> alumnos = alumnoDAO.allByPersona(empleadoBD.getPersona());
        List<Docente> docentes = docenteDAO.allByPersona(empleadoBD.getPersona());

        int activos = 0;
        List<Colaborador> colaboradores = colaboradorDAO.allActivosByPersona(empleadoBD.getPersona());
        for (Colaborador emp : colaboradores) {
            if (emp.getId() != empleadoBD.getId().longValue()) {
                activos++;
            }
        }

        if (Arrays.asList(DESP, RET).contains(empleadoBD.getEstadoEnum())
                && activos == 0 && alumnos.isEmpty() && docentes.isEmpty()) {

            usuarioRolDAO.updateInactivar(empleadoBD, userEmpleado);
        }

    }

    private void reactivarEmpleado(Colaborador empleadoBD, Colaborador empleadoForm, DataSessionPivot ds) {
        PerfilCompania cargoBD = perfilCompaniaDAO.find(empleadoForm.getCargo().getId());
        Oficina oficinaBD = oficinaDAO.find(empleadoForm.getOficina());

        empleadoBD.setEstadoEnum(ColaboradorEstadoEnum.ACT);
        empleadoBD.setFechaInicio(empleadoForm.getFechaInicio());
        empleadoBD.setFechaFin(null);
        empleadoBD.setOficina(oficinaBD);
        empleadoBD.setCargo(cargoBD);
        empleadoBD.setUserModificacion(ds.getUsuario());
        empleadoBD.setFechaModificacion(new Date());
        colaboradorDAO.update(empleadoBD);

        empleadoBD.setFuncionColaborador(new ArrayList());

        revisarPerfiles(empleadoBD, empleadoBD.getPersona(), oficinaBD, ds);
    }

//    @Override
//    public List<TipoOficina> allTipoOficina() {
//
//        return tipoOficinaDAO.all();
//    }
//
//    @Override
//    public TipoOficina findTipoById(String id) {
//        return tipoOficinaDAO.find(Long.valueOf(id));
//    }
    @Override
    public Colaborador findColaborador(Colaborador colaboradorForm) {
        Colaborador colaboradorBD = colaboradorDAO.find(colaboradorForm);
        List<FuncionColaborador> funcionesColaborador = funcionColaboradorDAO.allByColaborador(colaboradorForm);
        colaboradorBD.setFuncionColaborador(funcionesColaborador);
        return colaboradorBD;
    }

    @Override
    public List<TipoDocIdentidad> allDocumentosIdentidad() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    public List<Oficina> allAreasByOficinaMain(Oficina oficina) {
        return allAreasByMain(oficina);
    }
//
//    @Override
//    public List<PerfilCompania> allCargos(Oficina oficina) {
//        List<PerfilCompania> oficinaCompanias = perfilCompaniaDAO.allTipoCargoByOfi(oficina);
//        logger.debug("CANTIDAD DE FUNCS = {}", oficinaCompanias.size());
//        List<PerfilCompania> companias = perfilCompaniaDAO.allTipoCargo();
//        List<PerfilCompania> allCompanias = new ArrayList<PerfilCompania>();
//
//        allCompanias.addAll(oficinaCompanias);
//        allCompanias.addAll(companias);
//        logger.debug("CANTIDAD DE total = {}", allCompanias.size());
//        return allCompanias;
//    }
//
//    @Override
//    public List<PerfilCompania> allCargosByOficina(Oficina oficina) {
//        return perfilCompaniaDAO.allTipoCargoByOfi(oficina);
//    }

    @Override
    @Transactional
    public void saveColaborador(Colaborador colaborador, Oficina oficinaForm, Compania compania, DataSessionPivot ds) {
        boolean puedeVerOficina = verificadorService.puedeVerOficina(oficinaForm, ds);
        Assert.isTrue(puedeVerOficina, "No tiene permiso de crear colaboradores en esta oficina");

        Oficina oficinaBD = oficinaDAO.find(oficinaForm.getId());
//        Usuario usuario = dataSessionPivot.getUsuario();
        Persona persona = colaborador.getPersona();
        persona.setFechaRegistro(new Date());
        persona.setUserRegistro(ds.getUsuario());
        persona.setEstadoEnum(PersonaEstadoEnum.ACT);
        persona.setSexo(colaborador.getPersona().getSexo());
        personaDAO.save(persona);

        colaborador.setFechaRegistro(new Date());
        colaborador.setUserRegistro(ds.getUsuario());
        colaborador.setEstadoEnum(ColaboradorEstadoEnum.ACT);
        colaborador.setCodigo(getCodigoColaborador() + "");
        colaborador.setPersona(persona);
        colaboradorDAO.save(colaborador);

        ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
        colaboradorEstado.setColaborador(colaborador);
        colaboradorEstado.setEstadoEnum(ColaboradorEstadoEnum.ACT);
        colaboradorEstado.setUserRegistro(ds.getUsuario());
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
        personaCargo.setUserRegistro(ds.getUsuario());
        personaCargoDAO.save(personaCargo);

        Oficina oficinaColaborador = oficinaDAO.find(colaborador.getOficina().getId());
        revisarPerfiles(colaborador, persona, oficinaColaborador, ds);

    }

    @Override
    @Transactional
    public Boolean saveColaboradorExistente(Colaborador colaboradorForm, Oficina oficinaForm, Compania compania, DataSessionPivot ds) {
        boolean puedeVerOficina = verificadorService.puedeVerOficina(oficinaForm, ds);
        Assert.isTrue(puedeVerOficina, "No tiene permiso de crear colaboradores en esta oficina");

        Colaborador colaboradorBD = colaboradorDAO.findActivoByPersonaOficina(colaboradorForm.getOficina(), colaboradorForm.getPersona());
        Assert.isNull(colaboradorBD, "El colaborador existe en la oficina");

        Oficina oficinaColaborador = oficinaDAO.find(colaboradorForm.getOficina().getId());
        Persona personaForm = colaboradorForm.getPersona();

        Persona personaBD = personaDAO.find(personaForm.getId());
        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setNombres(personaForm.getNombres());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setEmailCompania(personaForm.getEmailCompania());
        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());
        personaDAO.update(personaBD);

        colaboradorForm.setFechaRegistro(new Date());
        colaboradorForm.setUserRegistro(ds.getUsuario());
        colaboradorForm.setEstado(ColaboradorEstadoEnum.ACT.name());
        colaboradorForm.setCodigo(getCodigoColaborador() + "");
        colaboradorDAO.save(colaboradorForm);

        ColaboradorEstado colaboradorEstado = new ColaboradorEstado();
        colaboradorEstado.setColaborador(colaboradorForm);
        colaboradorEstado.setEstadoEnum(ColaboradorEstadoEnum.ACT);
        colaboradorEstado.setUserRegistro(ds.getUsuario());
        colaboradorEstado.setFechaRegistro(new Date());
        colaboradorEstadoDAO.save(colaboradorEstado);

        PersonaCargo personaCargo = new PersonaCargo();
        personaCargo.setCompania(compania);
        personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
        personaCargo.setFechaInicio(colaboradorForm.getFechaInicio());
        personaCargo.setFechaRegistro(new Date());
        personaCargo.setOficina(colaboradorForm.getOficina());
        personaCargo.setPerfilCompania(colaboradorForm.getCargo());
        personaCargo.setPersona(personaBD);
        personaCargo.setUserRegistro(ds.getUsuario());
        personaCargoDAO.save(personaCargo);

        revisarPerfiles(colaboradorForm, personaBD, oficinaColaborador, ds);

        return true;
    }

    private void revisarPerfiles(Colaborador colaborador, Persona persona, Oficina oficinaColaborador, DataSessionPivot ds) {
        Oficina oficinaCentroMedico = oficinaDAO.findByCode(OficinaEnum.CENMED.name());

        boolean esOficinaCentroMedico = oficinaColaborador.getId().equals(oficinaCentroMedico.getId());
        boolean esOficinaSuperCentroMedico
                = oficinaColaborador.getOficinaSuperior() != null
                && oficinaColaborador.getOficinaSuperior().getId().equals(oficinaCentroMedico.getId());

        if ((esOficinaCentroMedico || esOficinaSuperCentroMedico)
                && Arrays.asList("MEDICO", "JMEDICO").contains(colaborador.getCargo().getCodigo())) {

            Medico medico = medicoDAO.findByColaborador(colaborador);
            if (medico == null) {
                medico = new Medico();
                medico.setColaborador(colaborador);
                medico.setFechaRegistro(new Date());
                medico.setUserRegistro(ds.getUsuario());
                medicoDAO.save(medico);
            }
        }

        List<PerfilCompania> perfiles = new ArrayList();
        for (FuncionColaborador funcionColaborador : colaborador.getFuncionColaborador()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            funcionColaborador.setColaborador(colaborador);
            funcionColaborador.setFechaInico(new Date());
            funcionColaborador.setFuncion(perfil);
            funcionColaborador.setEstado(EstadoEnum.ACT.name());
            funcionColaborador.setFechaRegistro(new Date());
            funcionColaborador.setUserRegistro(ds.getUsuario());
            funcionColaboradorDAO.save(funcionColaborador);
            perfiles.add(perfil);
        }

        Usuario user = usuarioDAO.findActivoByPersona(persona);
        if (user == null) {
            if (colaborador.getPersona().getEmailCompania() != null) {
                user = addUser(persona, ds);
                perfiles.add(colaborador.getCargo());
                addUserRoll(perfiles, oficinaColaborador, user, colaborador, ds);
            }
        } else {
            perfiles.add(colaborador.getCargo());
            addUserRoll(perfiles, oficinaColaborador, user, colaborador, ds);
        }
    }

    private Usuario addUser(Persona personaForm, DataSessionPivot ds) {
        Usuario user = new Usuario();
        user.setEstadoEnum(UserEstadoEnum.ACT);
        user.setGoogle(personaForm.getEmailCompania());
        user.setPersona(personaForm);
        user.setUserRegistro(ds.getUsuario());
        user.setFechaRegistro(new Date());
        usuarioDAO.save(user);

        return user;
    }

    private void addUserRoll(
            List<PerfilCompania> perfiles,
            Oficina oficinaMain,
            Usuario userColaborador,
            Colaborador colaborador, DataSessionPivot ds) {

        if (oficinaMain.getInstanciaOficina() == null) {
            oficinaMain = oficinaDAO.find(oficinaMain.getId());
        }
        List<FuncionRol> funcionRol = funcionRolDAO.allByPerfiles(perfiles);
        logger.debug("funcionRol size {}", funcionRol.size());
        Map<Long, List<Rol>> mapRol = TypesUtil.convertListToMapList("perfilCompania.id", "rol", funcionRol);
        logger.debug("mapRol size {}", mapRol.size());
        List<UsuarioRol> userRoles = usuarioRolDAO.allByUserOficina(userColaborador, oficinaMain);
        Map<Long, List<UsuarioRol>> mapUserRol = TypesUtil.convertListToMapList("rol.id", userRoles);

        for (PerfilCompania perfil : perfiles) {
            List<Rol> roless = mapRol.get(perfil.getId());
            logger.debug("mapRol size {} {}  ", perfil.getId(), roless);
            if (roless == null) {
                continue;
            }
            for (Rol rol : roless) {
                boolean existe = false;
                List<UsuarioRol> userRolBD = TypesUtil.getListNotNull(mapUserRol.get(rol.getId()));
                for (UsuarioRol ur : userRolBD) {
                    if (ur.getOficina().getId() == oficinaMain.getId().longValue()) {
                        existe = true;
                        break;
                    }
                }
                if (existe) {
                    continue;
                }

                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setEstadoEnum(UserEstadoEnum.ACT);
                usuarioRol.setFechaInicio(colaborador.getFechaInicio());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setOficina(oficinaMain);
                usuarioRol.setIdInstancia(oficinaMain.getInstanciaOficina());
                usuarioRol.setTipoOficina(oficinaMain.getTipoOficina().getCodigo());
                usuarioRol.setRol(rol);
                usuarioRol.setUserRegistro(ds.getUsuario());
                usuarioRol.setUsuario(userColaborador);
                usuarioRolDAO.save(usuarioRol);
            }
        }
    }

    @Override
    @Transactional
    public void updateColaborador(Colaborador colaboradorForm, Oficina xxx, DataSessionPivot ds) {

        Colaborador colaboradorBD = colaboradorDAO.find(colaboradorForm.getId());
        Oficina oficinaAnterior = colaboradorBD.getOficina();
        Oficina oficinaNueva = oficinaDAO.find(colaboradorForm.getOficina().getId());
        logger.info("oficina anterior={} nueva={}", oficinaAnterior.getId(), oficinaNueva.getId());

        boolean noCambioDatos = ObjectUtil.verificarIgualdad(colaboradorBD, colaboradorForm, Arrays.asList("cargo.id", "oficina.id", "fechaInicio"));
        if (!noCambioDatos) {
            colaboradorBD.setFechaModificacion(new Date());
            colaboradorBD.setUserModificacion(ds.getUsuario());
            colaboradorBD.setCargo(colaboradorForm.getCargo());
            colaboradorBD.setOficina(colaboradorForm.getOficina());
            colaboradorBD.setFechaInicio(colaboradorForm.getFechaInicio());
            colaboradorDAO.update(colaboradorBD);
        }

        if (colaboradorForm.getOficina().getId() != oficinaAnterior.getId().longValue()) {

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

            PersonaCargo personaCargo = personaCargoDAO.findCargoByPersona(oficinaAnterior, colaboradorForm.getPersona());
            if (personaCargo != null) {
                personaCargo.setEstadoEnum(PerfilEstadoEnum.INA);
                personaCargo.setFechaFin(new Date());
                personaCargo.setFechaModificacion(new Date());
                personaCargo.setUserModificacion(ds.getUsuario());
                personaCargoDAO.update(personaCargo);
            }

            personaCargo = new PersonaCargo();
            personaCargo.setCompania(ds.getCompania());
            personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
            personaCargo.setFechaInicio(colaboradorForm.getFechaInicio());
            personaCargo.setOficina(colaboradorForm.getOficina());
            personaCargo.setPerfilCompania(colaboradorForm.getCargo());
            personaCargo.setPersona(colaboradorForm.getPersona());
            personaCargo.setFechaRegistro(new Date());
            personaCargo.setUserRegistro(ds.getUsuario());
            personaCargoDAO.save(personaCargo);

        } else {
            PersonaCargo personaCargo = personaCargoDAO.findCargoByPersona(oficinaAnterior, colaboradorForm.getPersona());
            if (personaCargo != null) {
                personaCargo.setFechaModificacion(new Date());
                personaCargo.setUserModificacion(ds.getUsuario());
                personaCargo.setPerfilCompania(colaboradorForm.getCargo());
                personaCargoDAO.update(personaCargo);
            } else {
                personaCargo = new PersonaCargo();
                personaCargo.setCompania(ds.getCompania());
                personaCargo.setEstadoEnum(PerfilEstadoEnum.ACT);
                personaCargo.setFechaInicio(colaboradorForm.getFechaInicio());
                personaCargo.setFechaRegistro(new Date());
                personaCargo.setOficina(colaboradorForm.getOficina());
                personaCargo.setPerfilCompania(colaboradorForm.getCargo());
                personaCargo.setPersona(colaboradorForm.getPersona());
                personaCargo.setUserRegistro(ds.getUsuario());
                personaCargoDAO.save(personaCargo);
            }
        }

        List<FuncionColaborador> funcionesEmp = funcionColaboradorDAO.allByColaborador(colaboradorForm);
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

        Usuario usuarioColaborador = usuarioDAO.findActivoByPersona(colaboradorForm.getPersona());
        ArrayList<PerfilCompania> perfiles = new ArrayList();
        for (FuncionColaborador funcionColaborador : mapNuevo.values()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            perfiles.add(perfil);
        }
        if (usuarioColaborador != null) {
            Oficina oficinaMain = oficinaDAO.find(colaboradorForm.getOficina().getId());
            perfiles.add(colaboradorForm.getCargo());
            updateUserRol(usuarioColaborador, perfiles, oficinaMain, colaboradorForm, ds);
        }
    }

    private void updateUserRol(
            Usuario usuarioColaborador,
            List<PerfilCompania> perfilesCompaniaNuevos,
            Oficina oficinaMain,
            Colaborador colaborador, DataSessionPivot ds) {

        logger.info("ENTRA A UPDATE USER ROL");
        List<FuncionRol> funcionRolNuevos = funcionRolDAO.allByPerfiles(perfilesCompaniaNuevos);
        Map<Long, List<Rol>> mapRolNuevos = TypesUtil.convertListToMapList("rol.id", "rol", funcionRolNuevos);

        List<UsuarioRol> rolesUsuarioTengo = usuarioRolDAO.allByUserOficina(usuarioColaborador, oficinaMain);
        Map<Long, List<Rol>> mapRolTengo = TypesUtil.convertListToMapList("rol.id", "rol", rolesUsuarioTengo);

        for (UsuarioRol usuarioRol : rolesUsuarioTengo) {
            logger.info("ENTRA AL PRIMER LOOP");
            if (mapRolNuevos.get(usuarioRol.getRol().getId()) == null) {
                usuarioRol.setFechaFin(new Date());
                usuarioRol.setUsuario(usuarioColaborador);
                usuarioRol.setEstadoEnum(UserEstadoEnum.INA);
                usuarioRolDAO.update(usuarioRol);
            }
        }

        for (FuncionRol funcionRolNuevo : funcionRolNuevos) {
            logger.info("ENTRA AL SEGUNDO LOOP");
            if (!mapRolTengo.containsKey(funcionRolNuevo.getRol().getId())) {
                logger.info("ENTRA AL IF");

                UsuarioRol usuarioRol = new UsuarioRol();
                usuarioRol.setEstadoEnum(UserEstadoEnum.ACT);
                usuarioRol.setFechaInicio(colaborador.getFechaInicio());
                usuarioRol.setFechaRegistro(new Date());
                usuarioRol.setOficina(oficinaMain);
                usuarioRol.setIdInstancia(oficinaMain.getInstanciaOficina());
                usuarioRol.setTipoOficina(oficinaMain.getTipoOficina().getCodigoEnum().name());
                usuarioRol.setUserRegistro(ds.getUsuario());
                usuarioRol.setUsuario(usuarioColaborador);
                usuarioRol.setRol(funcionRolNuevo.getRol());
                usuarioRolDAO.save(usuarioRol);
            }
        }
    }

//    @Override
//    public List<PerfilCompania> allFunciones() {
//        return perfilCompaniaDAO.allTipoFuncion();
//    }
    @Override
    public Persona verificarDocumento(Persona persona) {
        return personaDAO.findByDoc(persona);
    }

    @Override
    public Usuario verificarEmail(Persona persona) {
        Usuario u = usuarioDAO.findByGoogleEmail(persona.getEmailCompania());
        return u;
    }

    private String getCodigoPerfilCompania() {
        String codigoNuevo = "CAR10001";
        PerfilCompania perfil = perfilCompaniaDAO.findUltimoCodigoCargo();
        if (perfil != null) {
            String codigoNume = perfil.getCodigo().substring(3);
            codigoNuevo = "CAR" + (Long.parseLong(codigoNume) + 1);
        }

        return codigoNuevo;
    }

    @Override
    public List<Persona> allPersonasByNombre(String nombre) {
        return personaDAO.allByNombre(nombre);
    }

    @Override
    @Transactional
    public void addCargo(PerfilCompania perfilCompania, Oficina oficina, DataSessionPivot ds) {
        boolean puedeEditarOficinas = verificadorService.puedeEditarOficinas(ds);
        Assert.isTrue(puedeEditarOficinas, "No tiene permiso para agregar cargos");

        PerfilCompania perfilCompaniaName = perfilCompaniaDAO.findFuncionByNombre(perfilCompania.getNombre());
        if (perfilCompaniaName != null) {
            throw new PhobosException("El cargo ingresada ya existe");
        }

        perfilCompania.setCodigo(getCodigoPerfilCompania());
        perfilCompania.setTipo(TipoPerfilCompaniaEnum.CARGO.toString());
        perfilCompania.setCompania(ds.getCompania());
        perfilCompania.setEsAutomatico(1l);
        perfilCompania.setOficinaContiene(oficina);
        perfilCompania.setUserRegistro(ds.getUsuario());
        perfilCompania.setFechaRegistro(new Date());
        perfilCompaniaDAO.save(perfilCompania);
    }

    @Override
    @Transactional
    public void addFuncion(PerfilCompania perfilCompania, Oficina oficina, DataSessionPivot ds) {
        boolean puedeEditarOficinas = verificadorService.puedeEditarOficinas(ds);
        Assert.isTrue(puedeEditarOficinas, "No tiene permiso para agregar funciones");

        PerfilCompania perfilCompaniaName = perfilCompaniaDAO.findFuncionByNombre(perfilCompania.getNombre());
        if (perfilCompaniaName != null) {
            throw new PhobosException("La función ingresada ya existe");
        }

        perfilCompania.setCodigo(this.getCodigoFuncionCompania());
        perfilCompania.setTipo(TipoPerfilCompaniaEnum.FUNCION.name());
        perfilCompania.setCompania(ds.getCompania());
        perfilCompania.setEsAutomatico(1L);
        perfilCompania.setOficinaContiene(oficina);
        perfilCompania.setUserRegistro(ds.getUsuario());
        perfilCompania.setFechaRegistro(new Date());
        perfilCompaniaDAO.save(perfilCompania);
    }

    private String getCodigoFuncionCompania() {
        String codigoNuevo = "F10001";
        PerfilCompania perfil = perfilCompaniaDAO.findUltimoCodigoFuncion();
        if (perfil != null) {
            String codigoNume = perfil.getCodigo().substring(1);
            codigoNuevo = "F" + (Long.parseLong(codigoNume) + 1);
        }
        return codigoNuevo;
    }

    @Override
    public List<PerfilCompania> allCargoByOficinaAltoNivel(Oficina oficina, DataSessionPivot ds) {
        List<PerfilCompania> cargos;
        if (verificadorService.puedeEditarOficinas(ds)) {
            cargos = perfilCompaniaDAO.allCargoByOficinaAltoPerfil(oficina);

        } else {
            cargos = perfilCompaniaDAO.allCargoByOficina(oficina);
            PerfilCompania administrativo = perfilCompaniaDAO.findByCodigo(PerfilColaboradorEnum.ADMTVO);
            cargos.add(administrativo);

            if (verificadorService.isGestorOficinaEPG(ds)) {
                List<PerfilCompania> cargosEPG = perfilCompaniaDAO.allCargosByContexto("EPG");
                cargos.addAll(cargosEPG);
            }
        }

        Collections.sort(cargos, new PerfilCompania.CompareNombre());
        return cargos;
    }

    @Override
    public List<PerfilCompania> allCargoByOficina(Oficina oficina, DataSessionPivot ds) {
        List<PerfilCompania> cargosAll = new ArrayList();
        Oficina oficinaBD = verificadorService.findOficina(oficina);
        if (verificadorService.puedeVerOficina(oficinaBD, ds)) {
            List<PerfilCompania> cargos = perfilCompaniaDAO.allCargoByOficina(oficinaBD);
            cargosAll.addAll(cargos);
        }

        if (verificadorService.isGestorOficinaEPG(ds)) {
            if (oficinaBD.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP) {
                Carrera carrera = carreraDAO.find(oficinaBD.getInstanciaOficina());
                if (carrera.getModalidadEstudio().isPostgrado()) {
                    List<PerfilCompania> cargos = perfilCompaniaDAO.allCargosByContexto("EPG");
                    cargosAll.addAll(cargos);
                }
            }
        }

        Collections.sort(cargosAll, new PerfilCompania.CompareNombre());
        return cargosAll;
    }

    @Override
    public List<PerfilCompania> allFuncionByOficinaAltoNivel(Oficina oficina, DataSessionPivot ds) {
        List<PerfilCompania> funciones;
        if (verificadorService.puedeEditarOficinas(ds)) {
            funciones = perfilCompaniaDAO.allFuncionesByOficinaAltoPerfil(oficina);

        } else {
            funciones = perfilCompaniaDAO.allFuncionesByOficina(oficina);
            if (verificadorService.isGestorOficinaEPG(ds)) {
                List<PerfilCompania> funcionesEPG = perfilCompaniaDAO.allFuncionesByContexto("EPG");
                funciones.addAll(funcionesEPG);
            }
        }

        Collections.sort(funciones, new PerfilCompania.CompareNombre());
        return funciones;
    }

    @Override
    public List<PerfilCompania> allFuncionByOficina(Oficina oficina, DataSessionPivot ds) {
        List<PerfilCompania> funcionesAll = new ArrayList();
        Oficina oficinaBD = verificadorService.findOficina(oficina);
        if (verificadorService.puedeVerOficina(oficina, ds)) {
            List<PerfilCompania> funciones = perfilCompaniaDAO.allFuncionesByOficina(oficina);
            funcionesAll.addAll(funciones);
        }

        if (verificadorService.isGestorOficinaEPG(ds)) {
            if (oficinaBD.getTipoOficina().getCodigoEnum() == TipoOficinaEnum.ESP) {
                Carrera carrera = carreraDAO.find(oficinaBD.getInstanciaOficina());
                if (carrera.getModalidadEstudio().isPostgrado()) {
                    List<PerfilCompania> funciones = perfilCompaniaDAO.allFuncionesByContexto("EPG");
                    funcionesAll.addAll(funciones);
                }
            }
        }

        Collections.sort(funcionesAll, new PerfilCompania.CompareNombre());
        return funcionesAll;
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
