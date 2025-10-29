package pe.edu.lamolina.amauta.controller.general.oficina.colaborador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.ColaboradorEstadoEnum;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.DESP;
import static pe.edu.lamolina.model.enums.ColaboradorEstadoEnum.RET;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.NivelOficinaEnum;
import pe.edu.lamolina.model.enums.PerfilColaboradorEnum;
import pe.edu.lamolina.model.enums.PerfilEstadoEnum;
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
import pe.edu.lamolina.amauta.dao.academico.CarreraDAO;
import pe.edu.lamolina.amauta.dao.academico.DepartamentoAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.DocenteDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.ColaboradorEstadoDAO;
import pe.edu.lamolina.amauta.dao.general.FuncionColaboradorDAO;
import pe.edu.lamolina.amauta.dao.general.OficinaDAO;
import pe.edu.lamolina.amauta.dao.general.PerfilCompaniaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioRolDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.amauta.dao.general.PersonaCargoDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaHistorialDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.medico.MedicoDAO;
import pe.edu.lamolina.amauta.dao.seguridad.FuncionRolDAO;
import pe.edu.lamolina.amauta.zelper.mail.MailerService;
import pe.edu.lamolina.model.academico.DepartamentoAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.enums.DocenteEstadoEnum;
import static pe.edu.lamolina.model.enums.SexoEnum.F;
import pe.edu.lamolina.model.enums.oficina.OficinaEnum;
import pe.edu.lamolina.model.enums.persona.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.PersonaHistorial;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class ColaboradorServiceImp implements ColaboradorService {

    private final CarreraDAO carreraDAO;
    private final ColaboradorDAO colaboradorDAO;
    private final ColaboradorEstadoDAO colaboradorEstadoDAO;
    private final DepartamentoAcademicoDAO departamentoAcademicoDAO;
    private final DocenteDAO docenteDAO;
    private final FuncionColaboradorDAO funcionColaboradorDAO;
    private final FuncionRolDAO funcionRolDAO;
    private final MedicoDAO medicoDAO;
    private final OficinaDAO oficinaDAO;
    private final PerfilCompaniaDAO perfilCompaniaDAO;
    private final PersonaCargoDAO personaCargoDAO;
    private final PersonaDAO personaDAO;
    private final PersonaHistorialDAO personaHistorialDAO;
    private final TipoDocIdentidadDAO tipoDocIdentidadDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioRolDAO usuarioRolDAO;

    private final VerificadorService verificadorService;
    private final MailerService mailerService;

    private final List<String> PERFILES_MEDICOS = Arrays.asList(
            PerfilColaboradorEnum.JMEDICO.name(),
            PerfilColaboradorEnum.MEDICO.name(),
            PerfilColaboradorEnum.TECENF.name()
    );

    @Override
    public Oficina findOficina(Oficina oficina) {
        return oficinaDAO.find(oficina.getId());
    }

    @Override
    public ResumenColaborador getResumenColoboradores(Oficina oficina) {
        List<Oficina> oficinas = allAreasByMain(oficina);
        ResumenColaborador colaboradors = colaboradorDAO.countByOficinas(oficinas);
        return colaboradors;
    }

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
        Assert.isFalse(empleadoBD.getEsJefeOficina(), "No puede modificar el estado del jefe de esta unidad por esta opción");
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
            if (pp.getEstadoEnum() == PerfilEstadoEnum.ACT) {
                pp.setEstadoEnum(PerfilEstadoEnum.INA);
                pp.setFechaFin(empleadoForm.getFechaFin());
                pp.setFechaModificacion(new Date());
                pp.setUserModificacion(ds.getUsuario());
                personaCargoDAO.update(pp);
            }
        }

        List<FuncionColaborador> funciones = funcionColaboradorDAO.allByColaborador(empleadoBD);
        for (FuncionColaborador fc : funciones) {
            if (fc.getEstadoEnum() == EstadoEnum.ACT) {
                fc.setEstadoEnum(EstadoEnum.INA);
                fc.setFechaFin(empleadoForm.getFechaFin());
                fc.setUserModificacion(ds.getUsuario());
                fc.setFechaModificacion(new Date());
                funcionColaboradorDAO.update(fc);
            }
        }

        Usuario user = usuarioDAO.findActivoByPersona(empleadoBD.getPersona());

        List<UsuarioRol> userRoles = usuarioRolDAO.allByUserOficina(user, empleadoBD.getOficina());
        for (UsuarioRol ur : userRoles) {
            if (ur.getEstadoEnum() == UserEstadoEnum.ACT) {
                ur.setEstadoEnum(UserEstadoEnum.INA);
                ur.setFechaFin(new Date());
                ur.setUserFinaliza(ds.getUsuario());
                usuarioRolDAO.update(ur);
            }
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

    @Override
    public Colaborador findColaborador(Colaborador colaboradorForm) {
        log.info("colaboradorForm={}", colaboradorForm);
        Colaborador colaboradorBD = colaboradorDAO.find(colaboradorForm);
        log.info("colaboradorBD={}", colaboradorBD);

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
        colaborador.setEsJefeOficina(false);
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

        PersonaHistorial personaHistorial = new PersonaHistorial();
        personaHistorial.setUsuario(ds.getUsuario());
        personaHistorial.setPersona(personaBD);
        personaHistorial.setFecha(new Date());
        personaHistorial.setNumeroDocumentoFrom(personaBD.getNumeroDocIdentidad());
        personaHistorial.setNumeroDocumentoTo(personaForm.getNumeroDocIdentidad());
        personaHistorial.setTipoDocumentoFrom(personaBD.getTipoDocumento());
        personaHistorial.setTipoDocumentoTo(personaForm.getTipoDocumento());
        personaHistorialDAO.save(personaHistorial);

        if (StringUtils.isBlank(personaForm.getEmailCompania())) {
            personaForm.setEmailCompania(null);
        }

        log.info("[saveColaboradorExistente] persona.id={} emailCia={}",
                personaBD.getId(), personaForm.getEmailCompania());

        personaBD.setPaterno(personaForm.getPaterno());
        personaBD.setMaterno(personaForm.getMaterno());
        personaBD.setNombres(personaForm.getNombres());
        personaBD.setSexo(personaForm.getSexo());
        personaBD.setEmailCompania(personaForm.getEmailCompania());
        personaBD.setTipoDocumento(personaForm.getTipoDocumento());
        personaBD.setNumeroDocIdentidad(personaForm.getNumeroDocIdentidad());
        personaBD.setUserModificacion(ds.getUsuario());
        personaDAO.update(personaBD);

        colaboradorForm.setFechaRegistro(new Date());
        colaboradorForm.setUserRegistro(ds.getUsuario());
        colaboradorForm.setEstado(ColaboradorEstadoEnum.ACT.name());
        colaboradorForm.setCodigo(getCodigoColaborador() + "");
        colaboradorForm.setEsJefeOficina(false);
        this.setCodigoColaboradorDocente(colaboradorForm, personaBD, oficinaColaborador);
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
        log.debug("funcionRol size {}", funcionRol.size());
        Map<Long, List<Rol>> mapRol = TypesUtil.convertListToMapList("perfilCompania.id", "rol", funcionRol);
        log.debug("mapRol size {}", mapRol.size());
        List<UsuarioRol> userRoles = usuarioRolDAO.allByUserOficina(userColaborador, oficinaMain);
        Map<Long, List<UsuarioRol>> mapUserRol = TypesUtil.convertListToMapList("rol.id", userRoles);

        for (PerfilCompania perfil : perfiles) {
            List<Rol> roless = mapRol.get(perfil.getId());
            log.debug("mapRol size {} {}  ", perfil.getId(), roless);
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
        log.info("oficina anterior={} nueva={}", oficinaAnterior.getId(), oficinaNueva.getId());

        boolean noCambioDatos = ObjectUtil.verificarIgualdad(colaboradorBD, colaboradorForm, Arrays.asList("cargo.id", "oficina.id", "fechaInicio"));
        if (!noCambioDatos) {
            colaboradorBD.setFechaModificacion(new Date());
            colaboradorBD.setUserModificacion(ds.getUsuario());
            colaboradorBD.setCargo(colaboradorForm.getCargo());
            colaboradorBD.setOficina(colaboradorForm.getOficina());
            colaboradorBD.setFechaInicio(colaboradorForm.getFechaInicio());
            colaboradorDAO.update(colaboradorBD);
        }

        boolean involucraCentroMedico = dentroCentroMedico(Arrays.asList(oficinaAnterior, oficinaNueva));
        if (involucraCentroMedico) {
            this.checkCambiosCentroMedico(oficinaNueva, colaboradorForm, ds);
        }

        if (colaboradorForm.getOficina().getId() != oficinaAnterior.getId().longValue()) {

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
        log.info("[updateColaborador] funcionesEmp.size={}", funcionesEmp.size());
        Map<Long, FuncionColaborador> mapNuevo = TypesUtil.convertListToMap("funcion.id", colaboradorForm.getFuncionColaborador());
        log.info("[updateColaborador] mapNuevo.size={}", mapNuevo.size());
        Map<Long, FuncionColaborador> mapTengo = TypesUtil.convertListToMap("funcion.id", funcionesEmp);
        log.info("[updateColaborador] mapTengo.size={}", mapTengo.size());

        for (FuncionColaborador funcionColaborador : funcionesEmp) {
            if (mapNuevo.get(funcionColaborador.getFuncion().getId()) == null) {
                funcionColaborador.setFechaFin(new Date());
                funcionColaborador.setEstado(EstadoEnum.INA.name());
                funcionColaboradorDAO.update(funcionColaborador);
                log.info("[updateColaborador] 1.INA -> funcionColaborador.id={} estado={}", funcionColaborador.getId(), funcionColaborador.getEstado());

            } else {
                log.info("[updateColaborador] 1.NONE -> funcionColaborador.id={} estad={}", funcionColaborador.getId(), funcionColaborador.getEstado());
            }
        }

        log.info("[updateColaborador] colaborador.funcionesColborador.size={}", colaboradorForm.getFuncionColaborador().size());
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
                log.info("[updateColaborador] 2.SAVE -> funcionColaborador.id={} estado={}", funcionColaborador.getId(), funcionColaborador.getEstado());

            } else {
                log.info("[updateColaborador] 2.NONE -> funcionColaborador.id={} estado={}", funcionColaborador.getId(), funcionColaborador.getEstado());
            }
        }

        Usuario usuarioColaborador = usuarioDAO.findActivoByPersona(colaboradorForm.getPersona());
        ArrayList<PerfilCompania> perfiles = new ArrayList();
        for (FuncionColaborador funcionColaborador : mapNuevo.values()) {
            PerfilCompania perfil = funcionColaborador.getFuncion();
            perfiles.add(perfil);
            log.info("[updateColaborador] perfil-nuevo.id={}", perfil.getId());
        }

        log.info("[updateColaborador] 1.perfiles-nuevos.size={}", perfiles.size());
        if (usuarioColaborador != null) {
            Oficina oficinaMain = oficinaDAO.find(colaboradorForm.getOficina().getId());
            perfiles.add(colaboradorForm.getCargo());
            log.info("[updateColaborador] 2.perfiles-nuevos.size={}", perfiles.size());
            updateUserRol(usuarioColaborador, perfiles, oficinaMain, colaboradorForm, ds);
        }
    }

    private void checkCambiosCentroMedico(
            Oficina oficinaNueva,
            Colaborador colaborador,
            DataSessionPivot ds) {

        boolean entraAlCentroMedico = dentroCentroMedico(Arrays.asList(oficinaNueva));

        if (PERFILES_MEDICOS.contains(colaborador.getCargo().getCodigo()) && entraAlCentroMedico) {
            Medico medico = medicoDAO.findByColaborador(colaborador);
            if (medico == null) {
                medico = new Medico();
                medico.setColaborador(colaborador);
                medico.setFechaRegistro(new Date());
                medico.setUserRegistro(ds.getUsuario());
                medicoDAO.save(medico);
            }
        }
    }

    private boolean dentroCentroMedico(List<Oficina> oficinas) {

        Oficina centroMedico = new Oficina(OficinaEnum.CENMED);
        for (Oficina oficina : oficinas) {
            if (oficina.getId().equals(centroMedico.getId())) {
                return true;
            }
            Oficina oficinaSuper = oficina.getOficinaSuperior();
            if (oficinaSuper == null) {
                continue;
            }
            if (oficinaSuper.getId().equals(centroMedico.getId())) {
                return true;
            }
        }

        return false;
    }

    private void updateUserRol(
            Usuario usuarioColaborador,
            List<PerfilCompania> perfilesCompaniaNuevos,
            Oficina oficinaMain,
            Colaborador colaborador, DataSessionPivot ds) {

        log.info("[updateUserRol] Inicio");
        log.info("[updateUserRol] perfilesNuevos.size={}", perfilesCompaniaNuevos.size());
        for (PerfilCompania perfil : perfilesCompaniaNuevos) {
            log.info("[updateUserRol] perfilNuevo.id={}", perfil.getId());
        }

        List<FuncionRol> funcionRolNuevos = funcionRolDAO.allByPerfiles(perfilesCompaniaNuevos);
        log.info("[updateUserRol] funcionRolNuevos.size={}", funcionRolNuevos.size());
        for (FuncionRol fr : funcionRolNuevos) {
            log.info("[updateUserRol] funcionRolNuevo.id={} rol={} funcion={}", fr.getId(), fr.getRol().getId(), fr.getPerfilCompania().getId());
        }
        Map<Long, List<Rol>> mapRolNuevos = TypesUtil.convertListToMapList("rol.id", "rol", funcionRolNuevos);

        List<UsuarioRol> rolesUsuarioTengo = usuarioRolDAO.allByUserOficina(usuarioColaborador, oficinaMain);
        log.info("[updateUserRol] rolesUsuarioTengo.size={}", rolesUsuarioTengo.size());
        Map<Long, List<Rol>> mapRolTengo = TypesUtil.convertListToMapList("rol.id", "rol", rolesUsuarioTengo);

        for (UsuarioRol usuarioRol : rolesUsuarioTengo) {
            log.info("[updateUserRol] buscar-rol-tengo={}", usuarioRol.getRol().getId());
            if (mapRolNuevos.get(usuarioRol.getRol().getId()) == null) {
                usuarioRol.setFechaFin(new Date());
                usuarioRol.setUsuario(usuarioColaborador);
                usuarioRol.setEstadoEnum(UserEstadoEnum.INA);
                usuarioRolDAO.update(usuarioRol);
                log.info("[updateUserRol] anular-rol-tengo={}", usuarioRol.getId());
            }
        }

        for (FuncionRol funcionRolNuevo : funcionRolNuevos) {
            log.info("[updateUserRol] buscar-rol-nuevo={}", funcionRolNuevo.getRol().getId());
            if (!mapRolTengo.containsKey(funcionRolNuevo.getRol().getId())) {

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
                log.info("[updateUserRol] save-rol-nuevo={}", usuarioRol.getId());
            }
        }
    }

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
        if (verificadorService.puedeEditarOficinas(ds) || verificadorService.esAdministradorTutoria(ds)) {
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

    private void setCodigoColaboradorDocente(Colaborador colaboradorForm, Persona personaBD, Oficina oficinaColaborador) {

        PerfilCompania perfilCompaniaDocente = perfilCompaniaDAO.findByCodigo(PerfilColaboradorEnum.DOC);

        if (perfilCompaniaDocente == null) {
            throw new PhobosException("No hay forma de determinar si es un docente");
        }

        if (perfilCompaniaDocente.getId() != colaboradorForm.getCargo().getId().longValue()) {
            log.debug("El cargo que ocupa no necesita asociarse al docente");
            return;
        }

        List<Docente> docentes = docenteDAO.allByPersona(personaBD);

        if (docentes.isEmpty()) {
            log.debug("El colaborador no tiene registros de docentes");
            return;
        }

        List<Docente> docentesActivos = docentes.stream()
                .filter(x -> x.getEstadoEnum() == DocenteEstadoEnum.ACT)
                .collect(Collectors.toList());

        if (docentesActivos.isEmpty()) {
            log.debug("El colaborador no tiene registros de docentes activos");
            return;
        }

        if (oficinaColaborador.getTipoOficina().getCodigoEnum() != TipoOficinaEnum.DPTO) {
            log.debug("La oficina no es del tipo de departamento académico");
            return;
        }

        DepartamentoAcademico departamentoAcademicoOficina = departamentoAcademicoDAO.find(oficinaColaborador.getInstanciaOficina());

        if (departamentoAcademicoOficina == null) {
            log.debug("La oficina no es del tipo de departamento académico");
            return;
        }

        if (docentesActivos.size() == 1) {

            log.debug("El colaborador  tiene un registros de docente activo");

            if (StringUtils.isBlank(docentesActivos.get(0).getCodigo())) {
                throw new PhobosException("El código del docente no fue correctamente configurado");
            }

            DepartamentoAcademico departamentoAcademico = docentesActivos.get(0).getDepartamentoAcademico();

            if (departamentoAcademico == null) {
                throw new PhobosException("El docente no trabaja en este departamento académico");
            }

            if (departamentoAcademico.getId() != departamentoAcademicoOficina.getId().longValue()) {
                throw new PhobosException("El docente no trabaja en este departamento académico");
            }

            colaboradorForm.setCodigo(docentesActivos.get(0).getCodigo());
            return;
        }

        Optional<Docente> docenteOptional = docentesActivos.stream()
                .filter(x -> x.getDepartamentoAcademico().getId() == departamentoAcademicoOficina.getId().longValue())
                .findFirst();

        if (!docenteOptional.isPresent()) {

            throw new PhobosException("El docente no trabaja en este departamento académico");

        }

        colaboradorForm.setCodigo(docenteOptional.get().getCodigo());

    }

    @Override
    @Transactional
    public void passwordUsuario(Long idPersona, Usuario usuario) {

        Usuario usuarioActivo = usuarioDAO.findActivoByPersona(new Persona(idPersona));
        usuarioActivo.setClave(TypesUtil.toMD5(usuario.getClave()));
        usuarioDAO.update(usuarioActivo);

    }

    @Override
    @Transactional
    public void passwordUsuarioEmail(Long idPersona, Usuario usuario) {

        String pass = usuario.getClave();
        this.passwordUsuario(idPersona, usuario);
        Persona persona = personaDAO.find(idPersona);
        String estimado = persona.getSexoEnum() == F ? "Estimada" : "Estimado";
        mailerService.enviarNotificacionUsuarioContrasena(estimado, persona.getNombreCompleto(), persona.getEmailCompania(), pass);
    }

}
