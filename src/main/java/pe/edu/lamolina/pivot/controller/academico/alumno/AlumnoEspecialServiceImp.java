package pe.edu.lamolina.pivot.controller.academico.alumno;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.zelpers.miscelanea.NumberFormat;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.academico.SituacionAcademica;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.ModalidadEstudioEnum;
import pe.edu.lamolina.model.enums.PersonaEstadoEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.academico.CarreraDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.ModalidadEstudioDAO;
import pe.edu.lamolina.pivot.dao.academico.SituacionAcademicaDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.general.TipoDocIdentidadDAO;

@Service
@Transactional(readOnly = true)
public class AlumnoEspecialServiceImp implements AlumnoEspecialService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TipoDocIdentidadDAO tipoDocIdentidadDAO;
    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;
    @Autowired
    SituacionAcademicaDAO situacionAcademicaDAO;
    @Autowired
    PersonaDAO personaDAO;
    @Autowired
    AlumnoDAO alumnoDAO;
    @Autowired
    CarreraDAO carreraDAO;
    @Autowired
    ModalidadEstudioDAO modalidadEstudioDAO;

    @Override
    public List<TipoDocIdentidad> allDocumentos() {
        return tipoDocIdentidadDAO.all();
    }

    @Override
    public List<CicloAcademico> allCiclos() {
        return cicloAcademicoDAO.all();
    }

    @Override
    public List<SituacionAcademica> allSituaciones() {
        return situacionAcademicaDAO.all();
    }

    @Override
    public void saveAlumno(Alumno alumno, Usuario usuario) {

        CicloAcademico ciclo = cicloAcademicoDAO.find(alumno.getCicloIngreso().getId());
        int sgt = ciclo.getMatriculaSiguiente();
        if (StringUtils.isBlank(alumno.getCodigo())) {
            String year = ciclo.getYear().toString();
            String cod;
            if (ciclo.getMatriculaInicio() > sgt) {
                sgt = ciclo.getMatriculaInicio();
            }
            cod = NumberFormat.codigo(sgt, 4);
            alumno.setCodigo(year + cod);
        }

        Persona personaForm = alumno.getPersona();
        String emailCompania = StringUtils.isEmpty(personaForm.getEmailCompania()) ? null : personaForm.getEmailCompania();
        if (emailCompania == null) {
            throw new PhobosException("El correo principal es obligatorio");
        }

        String email = StringUtils.isEmpty(personaForm.getEmail()) ? null : personaForm.getEmail();
        personaForm.setEmail(email);
        personaForm.setEmailCompania(emailCompania);

        this.validarDNI(personaForm);
        this.validarCodigo(alumno.getCodigo());
        if (personaForm.getId() == null) {
            this.validarEmailsinPersona(personaForm.getEmail());
            this.validarEmailEmpresaSinPersona(personaForm.getEmailCompania());
            personaForm.setEstado(PersonaEstadoEnum.ACT);
            personaForm.setUserRegistro(usuario);
            personaForm.setFechaRegistro(new Date());
            personaDAO.save(personaForm);

        } else {
            this.validarEmailConPersona(email, personaForm);
            this.validarEmailEmpresaConPersona(emailCompania, personaForm);
            Persona personaBD = this.getPersonaBD(personaForm);

            personaForm = personaBD;
        }

        if (personaForm.getId() == null) {
            personaForm.setUserRegistro(usuario);
            personaForm.setEstado(PersonaEstadoEnum.ACT);
            personaForm.setFechaRegistro(new Date());
            personaDAO.save(personaForm);
        } else {
            personaDAO.update(personaForm);
        }

        if (alumno.getId() == null) {

            Carrera carrera = carreraDAO.findByCodigo("000");
            ModalidadEstudio modalidadEstudio = modalidadEstudioDAO.findByCodigo(ModalidadEstudioEnum.ESP);
            SituacionAcademica situacion = situacionAcademicaDAO.findByCodigo("N");

            alumno.setPersona(personaForm);
            alumno.setCarrera(carrera);
            alumno.setModalidadEstudio(modalidadEstudio);
            alumno.setCicloActivo(alumno.getCicloIngreso());
            alumno.setSituacionAcademica(situacion);

            alumno.setRetirosCursos(0);
            alumno.setRetirosCiclos(0);
            alumno.setRetirosExtemporaneos(0);
            alumno.setCreditosCursados(0);
            alumno.setCreditosAprobados(0);
            alumno.setCursosInscritos(0);
            alumno.setCursosAprobados(0);
            alumno.setPromedioAcumulado(BigDecimal.ZERO);
            alumno.setCreditosCarreraCursados(0);
            alumno.setCreditosCarreraAprobados(0);
            alumno.setCursosCarreraInscritos(0);
            alumno.setCursosCarreraAprobados(0);
            alumno.setPromedioCarreraAcumulado(BigDecimal.ZERO);

            alumnoDAO.save(alumno);
        }

        ciclo.setMatriculaSiguiente(sgt + 1);
        cicloAcademicoDAO.update(ciclo);

        logger.debug("PERSONA ID- {}", personaForm.getId());
    }

    private void validarCodigo(String codigo) {
        Alumno alumnoDB = alumnoDAO.findByCodigo(codigo);
        if (alumnoDB != null) {
            throw new PhobosException("El código o matrícula ya se encuentra ocupado por otro alumno.");
        }
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
                throw new PhobosException("El correo InnovaSchools ya pertenece a otra persona con documento " + tipo.getSimbolo() + " " + pEmail.getNumeroDocIdentidad());
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

    private Persona getPersonaBD(Persona persona) {

        Persona personaBD = personaDAO.find(persona.getId());

        boolean sinCambios = ObjectUtil.verificarIgualdad(personaBD, persona, Arrays.asList("email", "emailCompania", "paterno", "materno", "nombres", "sexo", "fechaNacer", "direccion", "celular", "telefono"));
        if (sinCambios) {
            logger.debug("No se encontró cambios de datos en la persona {}", personaBD.getId());
            return personaBD;
        }

        personaBD.setNombres(persona.getNombres());
        personaBD.setPaterno(persona.getPaterno());
        personaBD.setMaterno(persona.getMaterno());
        personaBD.setSexo(persona.getSexo());
        personaBD.setFechaNacer(persona.getFechaNacer());
        personaBD.setDireccion(persona.getDireccion());
        personaBD.setCelular(persona.getCelular());
        personaBD.setTelefono(persona.getTelefono());
        personaBD.setEmail(persona.getEmail());
        personaBD.setEmailCompania(persona.getEmailCompania());
        personaDAO.update(personaBD);
        return personaBD;
    }
}
