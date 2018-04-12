package pe.edu.lamolina.pivot.controller.general.oficina;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.OficinaEstadoEnum;
import pe.edu.lamolina.model.enums.RolEnum;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.AusenciaJefe;
import pe.edu.lamolina.model.general.Colaborador;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.general.PerfilCompania;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.PersonaCargo;
import pe.edu.lamolina.model.seguridad.Rol;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.seguridad.UsuarioRol;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.DocenteDAO;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import pe.edu.lamolina.pivot.dao.general.AusenciaJefeDAO;
import pe.edu.lamolina.pivot.dao.general.ColaboradorDAO;
import pe.edu.lamolina.pivot.dao.general.OficinaDAO;
import pe.edu.lamolina.pivot.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.seguridad.RolDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.pivot.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;
import pe.edu.lamolina.pivot.dao.general.PersonaCargoDAO;

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
        oficinaBD.setTipoOficina(oficina.getTipoOficina());
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
        String tipo = oficina.getTipoOficina().getCodigo();
        if (TipoOficinaEnum.DPTO.name().equalsIgnoreCase(tipo)) {
            DepartamentoAcademico departamento = departamentoAcademicoDAO.find(oficina.getInstanciaOficina());
            oficina.setInstanciaOficinaCodigo(departamento.getCodigo());
            oficina.setInstanciaOficinaNombre(departamento.getNombreLargo());
        }
        if (TipoOficinaEnum.ESP.name().equalsIgnoreCase(tipo)) {
            Carrera carrera = carreraDAO.find(oficina.getInstanciaOficina());
            oficina.setInstanciaOficinaCodigo(carrera.getCodigo());
            oficina.setInstanciaOficinaNombre(carrera.getNombre());
        }
        if (TipoOficinaEnum.FAC.name().equalsIgnoreCase(tipo)) {
            Facultad facultad = facultadDAO.find(oficina.getInstanciaOficina());
            oficina.setInstanciaOficinaCodigo(facultad.getCodigo());
            oficina.setInstanciaOficinaNombre(facultad.getNombre());
        }
    }

    @Override
    @Transactional
    public void asignarJefe(Oficina oficina, DataSessionPivot ds) {
        Oficina oficinaBD = oficinaDAO.find(oficina.getId());
        Docente docenteDB = docenteDAO.findDocenteByPersona(oficina.getPersonaJefe());

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

        if (docenteDB == null) {
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

        this.asignarRol(oficina.getPersonaJefe(), RolEnum.JEFE_DPTO_ACA, ds);

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
        Docente docenteDb = docenteDAO.findDocenteByPersona(personaJefe);

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
}
