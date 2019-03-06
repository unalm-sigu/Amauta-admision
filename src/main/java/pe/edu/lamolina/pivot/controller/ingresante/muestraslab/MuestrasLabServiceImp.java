package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaEnfermedad;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.model.medico.Paciente;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.general.PersonaDAO;
import pe.edu.lamolina.pivot.dao.laboratorio.HistoriaLaboratorioDAO;
import pe.edu.lamolina.pivot.dao.medico.HistoriaClinicaDAO;
import pe.edu.lamolina.pivot.dao.medico.HistoriaEnfermedadDAO;
import pe.edu.lamolina.pivot.dao.medico.PacienteDAO;
import pe.edu.lamolina.pivot.dao.sip.TurnoEntrevistaObuaeDAO;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

@Service
@Transactional(readOnly = true)
public class MuestrasLabServiceImp implements MuestrasLabService {

    @Autowired
    HistoriaLaboratorioDAO historiaLaboratorioDAO;

    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;

    @Autowired
    HistoriaClinicaDAO historiaClinicaDAO;

    @Autowired
    TurnoEntrevistaObuaeDAO turnoEntrevistaObuaeDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    HistoriaEnfermedadDAO historiaEnfermedadDAO;

    @Autowired
    VisorMuestrasLab visorMuestrasLab;

    @Autowired
    PacienteDAO pacienteDAO;

    @Autowired
    PersonaDAO personaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<RecorridoIngresante> ingresantesDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo) {

        return recorridoIngresanteDAO.allByDynatableCicloTurno(filter, ciclo, turno);
    }

    @Override
    public List<RecorridoIngresante> allIngresantesConTurno(CicloAcademico ciclo) {
        return recorridoIngresanteDAO.allConTurno(ciclo);
    }

    @Override
    public List<RecorridoIngresante> allIngresantesConTurno(TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        return recorridoIngresanteDAO.allConTurno(turno, ciclo);
    }

    @Override
    public HistoriaLaboratorio findLaboratorioByRecorridoIngresante(RecorridoIngresante recorrido) {
        Alumno alumno = recorrido.getAlumno();
        Persona persona = alumno.getPersona();
        HistoriaClinica historia = historiaClinicaDAO.findByPersona(persona);
        if (historia != null) {
            return historiaLaboratorioDAO.findByHistoriaClinica(historia);
        } else {
            return null;
        }
    }

    @Override
    public List<TurnoEntrevistaObuae> allTurnos(CicloAcademico ciclo) {
        List<RecorridoIngresante> listaRecorridos = recorridoIngresanteDAO.allByCiclo(ciclo);
        List<TurnoEntrevistaObuae> turnos = new ArrayList();
        for (RecorridoIngresante recorrido : listaRecorridos) {
            if (!turnos.contains(recorrido.getTurnoEntrevistaObuae()) && recorrido.getTurnoEntrevistaObuae() != null) {
                turnos.add(recorrido.getTurnoEntrevistaObuae());
            }
        }
        return turnos;
    }

    @Override
    public HistoriaClinica findHistoriaClinica(RecorridoIngresante recorrido) {
        Alumno alumno = recorrido.getAlumno();
        Persona persona = alumno.getPersona();
        HistoriaClinica historia = historiaClinicaDAO.findByPersona(persona);
        return historia;
    }

    @Override
    public void inicializarVisor() {
        CicloAcademico ciclo = cicloAcademicoDAO.findActivoAdmisionPregrado();
        List<RecorridoIngresante> listaRecorridos = recorridoIngresanteDAO.allByCiclo(ciclo);
        List<Persona> listaPersonas = new ArrayList();
        for (RecorridoIngresante elem : listaRecorridos) {
            listaPersonas.add(elem.getAlumno().getPersona());
        }

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersona(listaPersonas);

        long numLab = 0;
        for (HistoriaLaboratorio laboratorio : laboratorios) {
            if (laboratorio.getNumeroMuestra() != null && laboratorio.getNumeroMuestra() > numLab) {
                numLab = laboratorio.getNumeroMuestra();
            }
        }
        numLab++;

        visorMuestrasLab.setNumeroLab(numLab);

    }

    @Override
    public List<HistoriaLaboratorio> allLabByPersonas(List<Persona> personas) {

        return historiaLaboratorioDAO.allByPersona(personas);

    }

    @Override
    @Transactional
    public void saveLaboratorio(HistoriaLaboratorio laboratorio) {
        if (laboratorio.getId() != null) {
            historiaLaboratorioDAO.update(laboratorio);
        } else {
            historiaLaboratorioDAO.save(laboratorio);
        }
    }

    @Override
    public List<HistoriaClinica> allHistoriaByPersonas(List<Persona> personas) {
        return historiaClinicaDAO.allByPersona(personas);
    }

    @Override
    @Transactional
    public void deleteLaboratorio(HistoriaLaboratorio laboratorio) {
        historiaLaboratorioDAO.delete(laboratorio);
    }

    @Override
    public List<RecorridoIngresante> ingresantesDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        return recorridoIngresanteDAO.allByDynatableCiclo(filter, ciclo);
    }

    @Override
    public TurnoEntrevistaObuae findTurno(long idTurno) {
        return turnoEntrevistaObuaeDAO.find(idTurno);
    }

    @Override
    public List<HistoriaLaboratorio> allLabByPersonasFilterFecha(List<Persona> personas, Date fecha) {
        return historiaLaboratorioDAO.allByPersonaFilterFecha(personas, fecha);
    }

    @Override
    public List<RecorridoIngresante> allIngresantesCiclo(CicloAcademico ciclo) {
        return recorridoIngresanteDAO.allByCiclo(ciclo);
    }

    @Override
    public List<RecorridoIngresante> allIngresantesDynatableByPersona(DynatableFilter filter, List<Persona> personas) {
        return recorridoIngresanteDAO.allIngresantesDynatableByPersona(filter, personas);
    }

    @Override
    public Boolean findRiesgoAlumno(HistoriaClinica historia) {
        List<HistoriaEnfermedad> enfermedadesAntecedente = historiaEnfermedadDAO.allByHistoriaClinica(historia);
        for (HistoriaEnfermedad enfermedad : enfermedadesAntecedente) {
            if (enfermedad.getEnfermedad().getTieneRiesgo()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public HistoriaClinica crearHistoriaClinica(RecorridoIngresante recorrido, DataSessionPivot ds) {
        //buscar paciente
        //si no existe, crearlo
        //crear historia clinica 

        Persona persona = personaDAO.find(recorrido.getAlumno().getPersona().getId());
        Paciente pacienteDB = pacienteDAO.findByPersona(persona);

        Paciente paciente = new Paciente();
        if (pacienteDB == null) {
            paciente.setPersona(persona);
            paciente.setUserRegistro(ds.getUsuario());
            paciente.setFechaRegistro(new Date());
            pacienteDAO.save(paciente);
        } else {
            paciente = pacienteDB;
        }

        HistoriaClinica hc = new HistoriaClinica();
        hc.setPaciente(paciente);
        hc.setUserRegistro(ds.getUsuario());
        hc.setFechaRegistro(new Date());
        hc.setTieneSeguro(Boolean.FALSE);
        historiaClinicaDAO.save(hc);

        return hc;
    }

}
