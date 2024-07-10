package pe.edu.lamolina.amauta.controller.general.persona;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.json.JaneHelper;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.amauta.controller.comun.s3.UploadFileS3;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorService;
import pe.edu.lamolina.amauta.dao.general.ArchivoDAO;
import pe.edu.lamolina.amauta.dao.general.EmpresaEtiquetadaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaCuentaBancariaDAO;
import pe.edu.lamolina.model.enums.UserEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.amauta.dao.general.PersonaDAO;
import pe.edu.lamolina.amauta.dao.general.PersonaFotoDAO;
import pe.edu.lamolina.amauta.dao.general.TipoDocIdentidadDAO;
import pe.edu.lamolina.amauta.dao.general.ValidacionPersonaDAO;
import pe.edu.lamolina.amauta.dao.seguridad.UsuarioDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.constantines.AcademicoConstantine;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.InstanciaEnum;
import pe.edu.lamolina.model.enums.persona.OrigenValidacionEnum;
import pe.edu.lamolina.model.enums.persona.ValidacionEstadoEnum;
import pe.edu.lamolina.model.general.Archivo;
import pe.edu.lamolina.model.general.EmpresaEtiquetada;
import pe.edu.lamolina.model.general.PersonaCuentaBancaria;
import pe.edu.lamolina.model.general.PersonaFoto;
import pe.edu.lamolina.model.general.ValidacionPersona;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class PersonaServiceImp implements PersonaService {

    private final ArchivoDAO archivoDAO;
    private final EmpresaEtiquetadaDAO empresaEtiquetadaDAO;
    private final PersonaDAO personaDAO;
    private final PersonaCuentaBancariaDAO personaCuentaBancariaDAO;
    private final PersonaFotoDAO personaFotoDAO;
    private final TipoDocIdentidadDAO tipoDocIdentidadDAO;
    private final UsuarioDAO usuarioDAO;
    private final ValidacionPersonaDAO validacionPersonaDAO;

    private final VerificadorService verificadorService;
    private final UploadFileS3 uploadFileS3;

    @Override
    public List<Persona> allByDynatable(DynatableFilter filter) {
        return personaDAO.allByFilter(filter);
    }

    @Transactional
    private void crearUsuario(Persona persona, DataSessionPivot ds) {
        String emailEmpresa = StringUtils.isEmpty(persona.getEmailCompania()) ? null : persona.getEmailCompania();
        if (emailEmpresa == null) {
            throw new PhobosException("El correo Institucional es obligatorio");
        }
        Usuario usuario = new Usuario();
        usuario.setEstadoEnum(UserEstadoEnum.ACT);
        usuario.setGoogle(persona.getEmailCompania());
        usuario.setPersona(persona);
        usuario.setUserRegistro(ds.getUsuario());
        usuario.setFechaRegistro(new Date());
        usuarioDAO.save(usuario);

    }

    @Override
    public Persona find(Persona persona) {
        return personaDAO.find(persona.getId());
    }

    @Override
    public List<PersonaCuentaBancaria> allCtasBancarias(Persona persona) {
        List<PersonaCuentaBancaria> ctasOrden = new ArrayList();
        List<PersonaCuentaBancaria> ctas = personaCuentaBancariaDAO.allByPersona(persona);
        for (PersonaCuentaBancaria cta : ctas) {
            if (cta.getEstadoEnum() == EstadoEnum.ACT) {
                ctasOrden.add(cta);
                break;
            }
        }
        for (PersonaCuentaBancaria cta : ctas) {
            if (cta.getEstadoEnum() != EstadoEnum.ACT) {
                ctasOrden.add(cta);
            }
        }
        return ctasOrden;
    }

    @Override
    public List<PersonaFoto> allFotosPersonaByTipo(Persona persona, String tipo) {
        return personaFotoDAO.allByTipo(persona, tipo);
    }

    @Override
    public List<EmpresaEtiquetada> allBancos() {
        return empresaEtiquetadaDAO.allBancos();
    }

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.allForPersonaNatural();
    }

    @Override
    @Transactional
    public void savePersona(Persona persona, DataSessionPivot ds) {

        boolean personaNueva = persona.getId() == null;
        Persona personaForm = persona;
        String emailCompania = StringUtils.isEmpty(personaForm.getEmailCompania()) ? null : personaForm.getEmailCompania();

        String email = StringUtils.isEmpty(personaForm.getEmail()) ? null : personaForm.getEmail();
        personaForm.setEmail(email);
        personaForm.setEmailCompania(emailCompania);

        Assert.isNotNull(personaForm.getNumeroDocIdentidad(), "Tiene que indicar el número de documento");
        Assert.isNotNull(personaForm.getTipoDocumento(), "Tiene que indicar el tipo de documento");
        Assert.isNotNull(personaForm.getTipoDocumento().getId(), "Tiene que indicar el tipo de documento");
        TipoDocIdentidad tipoDoc = personaForm.getTipoDocumento();
        String numeroDoc = personaForm.getNumeroDocIdentidad();

        this.validarDNI(personaForm);
        if (personaForm.getId() == null) {
            this.validarEmailsinPersona(personaForm.getEmail());
            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            personaDAO.save(personaForm);

        } else {
            this.validarEmailConPersona(email, personaForm);
            this.validarEmailEmpresaConPersona(emailCompania, personaForm);
            Persona personaBD = this.getPersonaBD(personaForm, ds);

            personaForm = personaBD;
            personaForm.setTipoDocumento(tipoDoc);
            personaForm.setNumeroDocIdentidad(numeroDoc);
        }

        if (persona.getId() == null) {
            personaDAO.save(personaForm);

        } else {
            personaForm.setUserModificacion(ds.getUsuario());
            personaDAO.update(personaForm);
        }

        if (!personaNueva) {
            Usuario usuario = usuarioDAO.findActivoByPersona(persona);
            if (usuario == null) {
                if (emailCompania != null) {
                    this.crearUsuario(persona, ds);
                }
            } else {
                usuario.setUserModifica(ds.getUsuario());
                usuario.setFechaModifica(new Date());
                usuario.setGoogle(personaForm.getEmailCompania());
                usuarioDAO.update(usuario);
            }
        } else {
            if (emailCompania != null) {
                this.crearUsuario(persona, ds);
            }
        }

        log.debug("PERSONA ID- {}", persona.getId());
    }

    private void validarDNI(Persona personaForm) {
        TipoDocIdentidad doc = personaForm.getTipoDocumento();

        Persona personaBD = personaDAO.findByDocIdentidad(doc, personaForm.getNumeroDocIdentidad());
        if (personaForm.getId() != null && personaBD != null && personaBD.getId().longValue() != personaForm.getId()) {
            throw new PhobosException("El DNI ingresado ya se encuentra relacionado con otra persona: " + personaBD.getApellidosNombres());

        } else if (personaForm.getId() == null && personaBD != null) {
            throw new PhobosException("El DNI ingresado ya se encuentra relacionado con otra persona: " + personaBD.getApellidosNombres());
        }
    }

    private void validarEmailsinPersona(String email) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmail(email);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailConPersona(String email, Persona persona) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailWithoutPersona(persona);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailEmpresaSinPersona(String email) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailEmpresa(email);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo UNALM ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private void validarEmailEmpresaConPersona(String email, Persona persona) {
        if (email != null) {
            List<Persona> personas = personaDAO.allByEmailEmpresaWithoutPersona(persona);
            if (!personas.isEmpty()) {
                Persona pEmail = personas.get(0);
                TipoDocIdentidad tipo = pEmail.getTipoDocumento();
                throw new PhobosException("El correo InnovaSchools ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
            }
        }
    }

    private Persona getPersonaBD(Persona persona, DataSessionPivot ds) {

        Persona personaBD = personaDAO.find(persona.getId());

        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, persona, Arrays.asList("email", "emailCompania", "paterno", "materno", "nombres", "sexo", "fechaNacer", "direccion", "celular", "telefono", "tituloAcademico"));
        if (sinCambios) {
            log.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }

        personaBD.setNombres(persona.getNombres());
        personaBD.setPaterno(persona.getPaterno());
        personaBD.setMaterno(persona.getMaterno());
        personaBD.setTituloAcademico(persona.getTituloAcademico());
        personaBD.setSexo(persona.getSexo());
        personaBD.setFechaNacer(persona.getFechaNacer());
        personaBD.setUbicacionDomicilio(persona.getUbicacionDomicilio());
        personaBD.setDireccion(persona.getDireccion());
        personaBD.setCelular(persona.getCelular());
        personaBD.setTelefono(persona.getTelefono());
        personaBD.setEmail(persona.getEmail());
        personaBD.setEmailCompania(persona.getEmailCompania());
        personaBD.setUserModificacion(ds.getUsuario());
        personaDAO.update(personaBD);
        return personaBD;
    }

    @Override
    public String validarEmailByPersona(String email, Persona persona) {
        List<Persona> personas = null;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmail(email);

        } else {
            persona.setEmail(email);
            personas = personaDAO.allByEmailWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Override
    public String validarEmailEmpresaByPersona(String email, Persona persona) {
        List<Persona> personas = null;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmailEmpresa(email);

        } else {
            persona.setEmailCompania(email);
            personas = personaDAO.allByEmailEmpresaWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Override
    public Persona findPersona(Persona personaTmp) {
        Persona persona = personaDAO.findByDocIdentidad(personaTmp.getTipoDocumento(), personaTmp.getNumeroDocIdentidad());
        if (persona != null) {
            throw new PhobosException("La persona con documento de identidad " + persona.getTipoDocumento().getSimbolo() + " " + persona.getNumeroDocIdentidad() + " ya se encuentra registrado como usuario del sistema.");
        }

        return new Persona();
    }

    @Override
    public String validarEmailCompaniaByPersona(String email, Persona persona) {
        List<Persona> personas;
        if (persona.getId() == null) {
            personas = personaDAO.allByEmailCompania(email);

        } else {
            persona.setEmailCompania(email);
            personas = personaDAO.allByEmailCompaniaWithoutPersona(persona);
        }

        if (personas.isEmpty()) {
            return null;
        }

        int loop = 0;
        String msg = "Este correo ya pertenece al: ";
        for (Persona per : personas) {
            TipoDocIdentidad tipo = per.getTipoDocumento();
            msg += (loop == 0) ? "" : ", ";
            msg += tipo.getSimbolo() + " " + per.getNumeroDocIdentidad();
            loop++;
        }
        return msg;
    }

    @Override
    @Transactional
    public void updatePersonaAlumno(Persona persona, DataSessionPivot ds) {
        Persona personaDB = personaDAO.find(persona.getId());
        personaDB.setNombres(persona.getNombres());
        personaDB.setUserModificacion(ds.getUsuario());

        personaDAO.update(personaDB);
    }

    @Override
    @Transactional
    public void saveCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds) {
        boolean esOperadoGastoPosgrado = verificadorService.isOperadorGastoPosgrado(ds);
        Assert.isTrue(esOperadoGastoPosgrado, "No tiene permiso para efectuar esta operación");

        boolean sinCta = StringUtils.isBlank(cuentaBanco.getNumeroCuenta());
        boolean sinCci = StringUtils.isBlank(cuentaBanco.getCuentaInterbancaria());
        Assert.isFalse(sinCci && sinCta, "Debe indicar el Nº de la cuenta bancaria o el CCI");

        EmpresaEtiquetada banco = empresaEtiquetadaDAO.find(cuentaBanco.getBanco().getId());
        boolean esBCP = banco.getEmpresa().getNumeroDocIdentidad().equals(GlobalConstantine.RUC_BCP);
        if (esBCP) {
            Assert.isFalse(sinCta, "Es obligatorio indicar el Nº de la cuenta bancaria si son del BCP");
        } else {
            Assert.isFalse(sinCci, "Es obligatorio indicar el CCI para bancos diferentes del BCP");
        }

        PersonaCuentaBancaria ctaBancoActiva = personaCuentaBancariaDAO.findActivo(cuentaBanco.getPersona());
        if (ctaBancoActiva == null) {
            cuentaBanco.setEstadoEnum(EstadoEnum.ACT);
        } else {
            cuentaBanco.setEstadoEnum(EstadoEnum.INA);
        }

        cuentaBanco.setNumeroCuenta(sinCta ? null : cuentaBanco.getNumeroCuenta().trim());
        cuentaBanco.setCuentaInterbancaria(sinCci ? null : cuentaBanco.getCuentaInterbancaria().trim());
        cuentaBanco.setUserRegistro(ds.getUsuario());
        cuentaBanco.setFechaRegistro(new Date());
        personaCuentaBancariaDAO.save(cuentaBanco);
    }

    @Override
    @Transactional
    public void deleteCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds) {
        boolean esOperadoGastoPosgrado = verificadorService.isOperadorGastoPosgrado(ds);
        Assert.isTrue(esOperadoGastoPosgrado, "No tiene permiso para efectuar esta operación");

        PersonaCuentaBancaria cuentaBancoBD = personaCuentaBancariaDAO.find(cuentaBanco.getId());
        Assert.isNotNull(cuentaBancoBD, "No se pudo ubicar el registro de esta cuenta bancaria");
        personaCuentaBancariaDAO.delete(cuentaBancoBD);
    }

    @Override
    @Transactional
    public void activarCtaBanco(PersonaCuentaBancaria cuentaBanco, DataSessionPivot ds) {
        boolean esOperadoGastoPosgrado = verificadorService.isOperadorGastoPosgrado(ds);
        Assert.isTrue(esOperadoGastoPosgrado, "No tiene permiso para efectuar esta operación");

        PersonaCuentaBancaria cuentaBancoBD = personaCuentaBancariaDAO.find(cuentaBanco.getId());
        Assert.isNotNull(cuentaBancoBD, "No se pudo ubicar el registro de esta cuenta bancaria");

        PersonaCuentaBancaria ctaBancoActiva = personaCuentaBancariaDAO.findActivo(cuentaBancoBD.getPersona());
        if (ctaBancoActiva == null) {
        } else {
            ctaBancoActiva.setEstadoEnum(EstadoEnum.INA);
            personaCuentaBancariaDAO.update(ctaBancoActiva);
        }
        cuentaBancoBD.setEstadoEnum(EstadoEnum.ACT);
        personaCuentaBancariaDAO.update(cuentaBancoBD);

    }

    @Override
    @Transactional
    public void saveFirma(PersonaFoto personaFirma, DataSessionPivot ds) {
        boolean esOperadoGastoPosgrado = verificadorService.isOperadorGastoPosgrado(ds);
        Assert.isTrue(esOperadoGastoPosgrado, "No tiene permiso para efectuar esta operación");

        String tipoFoto = "FIRMA";
        PersonaFoto firmaActiva = personaFotoDAO.findActiva(personaFirma.getPersona(), tipoFoto);
        Assert.isNull(firmaActiva, "Antes de agregar una firma, primero debe desactivar la firma actual");

        Archivo archivo = personaFirma.getArchivo();
        uploadFileS3.uploadSync(AcademicoConstantine.S3_DIR_FOTO_CARNET, GlobalConstantine.TMP_DIR, archivo.getNombre(), true);
        String path = uploadFileS3.getPathFile(AcademicoConstantine.S3_DIR_FOTO_CARNET, archivo.getNombre());
        archivo.setRuta(path);
        archivo.setIdInstancia(123L);
        archivo.setInstancia(InstanciaEnum.PERSONA_FOTO.name());
        archivo.setUsuarioRegistro(ds.getUsuario());
        archivo.setFechaRegistro(new Date());
        archivoDAO.save(archivo);

        personaFirma.setEstadoEnum(EstadoEnum.ACT);
        personaFirma.setTipoFoto(tipoFoto);
        personaFirma.setUserRegistro(ds.getUsuario());
        personaFirma.setFechaRegistro(new Date());
        personaFotoDAO.save(personaFirma);

        archivo.setIdInstancia(personaFirma.getId());
        archivoDAO.update(archivo);
    }

    @Override
    @Transactional
    public void anularFirma(PersonaFoto firmaPersonaForm, DataSessionPivot ds) {
        boolean esOperadoGastoPosgrado = verificadorService.isOperadorGastoPosgrado(ds);
        Assert.isTrue(esOperadoGastoPosgrado, "No tiene permiso para efectuar esta operación");

        String tipoFoto = "FIRMA";
        PersonaFoto firmaPersonaBD = personaFotoDAO.find(firmaPersonaForm.getId());
        Assert.isNotNull(firmaPersonaBD, "No se ubicó el registro indicado");
        Assert.isTrue(firmaPersonaBD.getTipoFoto().equals(tipoFoto), "El registro no pertenece a una foto de firma");
        Assert.isTrue(firmaPersonaBD.getEstadoEnum() == EstadoEnum.ACT, "El registro no se encuentra activo");

        LocalDate ayer = new LocalDate().minusDays(5);

        if (firmaPersonaBD.getFechaRegistro().before(ayer.toDate())) {
            firmaPersonaBD.setEstadoEnum(EstadoEnum.INA);
            firmaPersonaBD.setFechaDesactivacion(new Date());
            firmaPersonaBD.setUserDesactivacion(ds.getUsuario());
            personaFotoDAO.update(firmaPersonaBD);

        } else {
            personaFotoDAO.delete(firmaPersonaBD);
        }

    }

    @Override
    public String getPersonaJsonValidacion(Persona persona) {
        return JaneHelper
                .from(persona)
                .only("id,paterno,materno,nombres,sexo,fechaNacer,numeroDocIdentidad")
                .join("tipoDocumento", "id,simbolo")
                .json().toString();
    }

    @Override
    @Transactional
    public void registrarValidacionDocente(Persona persona, Docente docente, DataSessionPivot ds) {
        DateTime today = new DateTime();

        persona.setEstadoValidacionEnum(ValidacionEstadoEnum.VALIDADO);
        persona.setOrigenValidacionEnum(OrigenValidacionEnum.DOCENTE);
        persona.setFechaValidacion(today.toDate());
        persona.setUserValidacion(ds.getUsuario());
        persona.setUserModificacion(ds.getUsuario());
        personaDAO.update(persona);

        String personaJson = this.getPersonaJsonValidacion(persona);

        ValidacionPersona validacion = new ValidacionPersona();
        validacion.setPersona(persona);
        validacion.setDataFinal(personaJson);
        validacion.setOrigenEnum(OrigenValidacionEnum.DOCENTE);
        validacion.setInstanciaOrigen(docente.getId());
        validacion.setUserValidacion(ds.getUsuario());
        validacion.setFechaValidacion(today.toDate());
        validacionPersonaDAO.save(validacion);
    }

    @Override
    @Transactional
    public void registrarValidacionDocente2(Persona persona, Docente docente, String personaJsonInicial, DataSessionPivot ds) {
        DateTime today = new DateTime();

        persona.setEstadoValidacionEnum(ValidacionEstadoEnum.VALIDADO);
        persona.setOrigenValidacionEnum(OrigenValidacionEnum.DOCENTE);
        persona.setFechaValidacion(today.toDate());
        persona.setUserValidacion(ds.getUsuario());
        persona.setUserModificacion(ds.getUsuario());
        personaDAO.update(persona);

        String personaJsonFinal = this.getPersonaJsonValidacion(persona);

        ValidacionPersona validacion = new ValidacionPersona();
        validacion.setPersona(persona);
        validacion.setDataInicio(personaJsonInicial);
        validacion.setDataFinal(personaJsonFinal);
        validacion.setOrigenEnum(OrigenValidacionEnum.DOCENTE);
        validacion.setInstanciaOrigen(docente.getId());
        validacion.setUserValidacion(ds.getUsuario());
        validacion.setFechaValidacion(today.toDate());
        validacionPersonaDAO.save(validacion);
    }

    @Override
    public String corregirNombre(String nombre, String tipo) {
        if (StringUtils.isBlank(nombre)) {
            return null;
        }

        nombre = nombre.replaceAll("\\s+", " ").trim();
        Assert.isTrue(nombre.length() > 1, "El " + tipo + " debe contener al menos 2 letras");

        String[] palabras = nombre.split(" ");
        for (int i = 0; i < palabras.length; i++) {
            String palabra = palabras[i].toLowerCase();
            if (i > 0 && Arrays.asList("de", "del", "la", "y").contains(palabra)) {
                palabras[i] = palabra;
            } else {
                palabras[i] = capitalizarPalabra(palabra);
            }
        }

        return String.join(" ", palabras);
    }

    private String capitalizarPalabra(String palabra) {
        if (palabra.contains("'")) {
            String[] partes = palabra.split("'");
            StringBuilder palabraCorregida = new StringBuilder();
            for (int i = 0; i < partes.length; i++) {
                palabraCorregida.append(capitalizarPalabra(partes[i]));
                if (i < partes.length - 1) {
                    palabraCorregida.append("'");
                }
            }
            palabra = palabraCorregida.toString();
        }

        if (palabra.contains("-")) {
            String[] partes = palabra.split("-");
            StringBuilder palabraCorregida = new StringBuilder();
            for (int i = 0; i < partes.length; i++) {
                palabraCorregida.append(capitalizarPalabra(partes[i]));
                if (i < partes.length - 1) {
                    palabraCorregida.append("-");
                }
            }
            palabra = palabraCorregida.toString();
        }

        return palabra.substring(0, 1).toUpperCase() + palabra.substring(1);
    }

}
