package pe.edu.lamolina.pivot.controller.ingresante.muestraslab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.TypesUtil;
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
    public CicloAcademico findCicloActivoAdmision() {
        return cicloAcademicoDAO.findActivoAdmisionPregrado();
    }

    @Override
    @Transactional
    public List<RecorridoIngresante> allRecorridosByDynatable(DynatableFilter filter, CicloAcademico ciclo, DataSessionPivot ds) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allByDynatableCiclo(filter, ciclo);

        List<Alumno> alumnos = recorridos.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(personas);
        List<HistoriaClinica> historiasClinicas = historiaClinicaDAO.allByPersonas(personas);
        List<HistoriaEnfermedad> historiasEnfermedades = historiaEnfermedadDAO.allRiesgoByHistoriasClinicas(historiasClinicas);
        Map<Long, HistoriaLaboratorio> mapLaboratorio = TypesUtil.convertListToMap("historiaClinica.paciente.persona.id", laboratorios);
        Map<Long, HistoriaClinica> mapHistoriaClinica = TypesUtil.convertListToMap("paciente.persona.id", historiasClinicas);
        Map<Long, List<HistoriaEnfermedad>> mapHistoriaEnfermedad = TypesUtil.convertListToMapList("historiaClinica.paciente.persona.id", historiasEnfermedades);

        for (RecorridoIngresante reco : recorridos) {
            Persona persona = reco.getAlumno().getPersona();
            HistoriaClinica historiaClinica = mapHistoriaClinica.get(persona.getId());
            historiaClinica = (historiaClinica == null) ? crearHistoriaClinica(persona, ds) : historiaClinica;

            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            laboratorio = (laboratorio == null) ? new HistoriaLaboratorio() : laboratorio;
            laboratorio.setHistoriaClinica(historiaClinica);
            reco.setLaboratorio(laboratorio);

            List<HistoriaEnfermedad> historiaEnfermedades = mapHistoriaEnfermedad.get(persona.getId());
            if (historiaEnfermedades != null && !historiaEnfermedades.isEmpty()) {
                reco.setTieneRiesgo(Boolean.TRUE);
            }

        }

        return recorridos;
    }

    @Override
    @Transactional
    public List<RecorridoIngresante> allRecorridosByDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo, DataSessionPivot ds) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allByDynatableCicloTurno(filter, ciclo, turno);

        List<Alumno> alumnos = recorridos.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(personas);
        List<HistoriaClinica> historiasClinicas = historiaClinicaDAO.allByPersonas(personas);
        List<HistoriaEnfermedad> historiasEnfermedades = historiaEnfermedadDAO.allRiesgoByHistoriasClinicas(historiasClinicas);
        Map<Long, HistoriaLaboratorio> mapLaboratorio = TypesUtil.convertListToMap("historiaClinica.paciente.persona.id", laboratorios);
        Map<Long, HistoriaClinica> mapHistoriaClinica = TypesUtil.convertListToMap("paciente.persona.id", historiasClinicas);
        Map<Long, List<HistoriaEnfermedad>> mapHistoriaEnfermedad = TypesUtil.convertListToMapList("historiaClinica.paciente.persona.id", historiasEnfermedades);

        for (RecorridoIngresante reco : recorridos) {
            Persona persona = reco.getAlumno().getPersona();
            HistoriaClinica historiaClinica = mapHistoriaClinica.get(persona.getId());
            historiaClinica = (historiaClinica == null) ? crearHistoriaClinica(persona, ds) : historiaClinica;

            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            laboratorio = (laboratorio == null) ? new HistoriaLaboratorio() : laboratorio;
            laboratorio.setHistoriaClinica(historiaClinica);
            reco.setLaboratorio(laboratorio);

            List<HistoriaEnfermedad> historiaEnfermedades = mapHistoriaEnfermedad.get(persona.getId());
            if (historiaEnfermedades != null && !historiaEnfermedades.isEmpty()) {
                reco.setTieneRiesgo(Boolean.TRUE);
            }

        }

        return recorridos;
    }

    @Override
    @Transactional
    public List<RecorridoIngresante> allAtendidosByDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo, DataSessionPivot ds) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allAtendidosByDynatableCicloFecha(filter, ciclo, turno.getFecha());

        List<Alumno> alumnos = recorridos.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(personas);
        List<HistoriaClinica> historiasClinicas = historiaClinicaDAO.allByPersonas(personas);
        List<HistoriaEnfermedad> historiasEnfermedades = historiaEnfermedadDAO.allRiesgoByHistoriasClinicas(historiasClinicas);
        Map<Long, HistoriaLaboratorio> mapLaboratorio = TypesUtil.convertListToMap("historiaClinica.paciente.persona.id", laboratorios);
        Map<Long, HistoriaClinica> mapHistoriaClinica = TypesUtil.convertListToMap("paciente.persona.id", historiasClinicas);
        Map<Long, List<HistoriaEnfermedad>> mapHistoriaEnfermedad = TypesUtil.convertListToMapList("historiaClinica.paciente.persona.id", historiasEnfermedades);

        for (RecorridoIngresante reco : recorridos) {
            Persona persona = reco.getAlumno().getPersona();
            HistoriaClinica historiaClinica = mapHistoriaClinica.get(persona.getId());
            historiaClinica = (historiaClinica == null) ? crearHistoriaClinica(persona, ds) : historiaClinica;

            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            laboratorio = (laboratorio == null) ? new HistoriaLaboratorio() : laboratorio;
            laboratorio.setHistoriaClinica(historiaClinica);
            reco.setLaboratorio(laboratorio);

            List<HistoriaEnfermedad> historiaEnfermedades = mapHistoriaEnfermedad.get(persona.getId());
            if (historiaEnfermedades != null && !historiaEnfermedades.isEmpty()) {
                reco.setTieneRiesgo(Boolean.TRUE);
            }

        }

        return recorridos;
    }

    private HistoriaClinica crearHistoriaClinica(Persona persona, DataSessionPivot ds) {
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

        HistoriaClinica historiaClinica = new HistoriaClinica();
        historiaClinica.setPaciente(paciente);
        historiaClinica.setUserRegistro(ds.getUsuario());
        historiaClinica.setFechaRegistro(new Date());
        historiaClinica.setTieneSeguro(Boolean.FALSE);
        historiaClinicaDAO.save(historiaClinica);

        return historiaClinica;
    }

    @Override
    public List<RecorridoIngresante> allIngresantesConTurno(CicloAcademico ciclo) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allConTurno(ciclo);
        List<Alumno> alumnos = recorridos.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(personas);
        Map<Long, HistoriaLaboratorio> mapLaboratorio = TypesUtil.convertListToMap("historiaClinica.paciente.persona.id", laboratorios);

        for (RecorridoIngresante reco : recorridos) {
            Persona persona = reco.getAlumno().getPersona();
            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            reco.setLaboratorio(laboratorio);

        }

        Collections.sort(recorridos, new RecorridoIngresante.CompareNombres());

        return recorridos;
    }

    @Override
    public List<RecorridoIngresante> allIngresantesConTurno(TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allConTurno(turno, ciclo);
        List<Alumno> alumnos = recorridos.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(personas);
        Map<Long, HistoriaLaboratorio> mapLaboratorio = TypesUtil.convertListToMap("historiaClinica.paciente.persona.id", laboratorios);

        for (RecorridoIngresante reco : recorridos) {
            Persona persona = reco.getAlumno().getPersona();
            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            reco.setLaboratorio(laboratorio);

        }

        Collections.sort(recorridos, new RecorridoIngresante.CompareNombres());

        return recorridos;
    }

    @Override
    public List<RecorridoIngresante> allAtendidos(TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        Date fecha = turno.getFecha();
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allAtendidos(fecha, ciclo);

        List<Alumno> alumnos = recorridos.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(personas);
        Map<Long, HistoriaLaboratorio> mapLaboratorio = TypesUtil.convertListToMap("historiaClinica.paciente.persona.id", laboratorios);

        for (RecorridoIngresante reco : recorridos) {
            Persona persona = reco.getAlumno().getPersona();
            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            reco.setLaboratorio(laboratorio);

        }

        Collections.sort(recorridos, new RecorridoIngresante.CompareAtencion());

        return recorridos;
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

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(listaPersonas);

        long numLab = 0;
        for (HistoriaLaboratorio laboratorio : laboratorios) {
            if (laboratorio.getNumeroMuestra() != null && laboratorio.getNumeroMuestra() > numLab) {
                numLab = laboratorio.getNumeroMuestra();
            }
        }
        numLab++;

        visorMuestrasLab.setCicloAcademico(ciclo);
        visorMuestrasLab.setNumeroLab(numLab);

    }

    @Override
    public List<HistoriaLaboratorio> allLabByPersonas(List<Persona> personas) {
        return historiaLaboratorioDAO.allByPersonas(personas);
    }

    @Override
    @Transactional
    public void saveLaboratorio(HistoriaLaboratorio laboratorio, DataSessionPivot ds) {
        Date today = new Date();
        laboratorio.setNumeroMuestra(visorMuestrasLab.getNumeroLab());
        laboratorio.setFechaMuestra(today);
        laboratorio.setFechaRegistro(today);
        laboratorio.setUserRegistro(ds.getUsuario());
        historiaLaboratorioDAO.save(laboratorio);

        visorMuestrasLab.incrementaNumLab();
    }

    @Override
    public List<HistoriaClinica> allHistoriaByPersonas(List<Persona> personas) {
        return historiaClinicaDAO.allByPersonas(personas);
    }

    @Override
    @Transactional
    public void deleteLaboratorio(HistoriaLaboratorio laboratorioForm) {
        HistoriaLaboratorio laboratorioBD = historiaLaboratorioDAO.find(laboratorioForm.getId());
        Assert.isNotNull(laboratorioBD, "Ya no existe este registro. Es imposible ejecutar solicitud.");
        if (laboratorioBD.getNumeroMuestra() + 1 == visorMuestrasLab.getNumeroLab().longValue()) {
            visorMuestrasLab.decrementaNumLab();
        }

        Date today = new LocalDate().toDate();
        Date fechaMuestra = new DateTime(laboratorioBD.getFechaMuestra()).toLocalDate().toDate();
        Assert.isTrue(today.equals(fechaMuestra), "No puede eliminarse la muestra de una fecha pasada");

        historiaLaboratorioDAO.delete(laboratorioBD);
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

//    @Override
//    public HistoriaClinica crearHistoriaClinica(RecorridoIngresante recorrido, DataSessionPivot ds) {
//        //buscar paciente
//        //si no existe, crearlo
//        //crear historia clinica 
//
//        Persona persona = personaDAO.find(recorrido.getAlumno().getPersona().getId());
//        Paciente pacienteDB = pacienteDAO.findByPersona(persona);
//
//        Paciente paciente = new Paciente();
//        if (pacienteDB == null) {
//            paciente.setPersona(persona);
//            paciente.setUserRegistro(ds.getUsuario());
//            paciente.setFechaRegistro(new Date());
//            pacienteDAO.save(paciente);
//        } else {
//            paciente = pacienteDB;
//        }
//
//        HistoriaClinica hc = new HistoriaClinica();
//        hc.setPaciente(paciente);
//        hc.setUserRegistro(ds.getUsuario());
//        hc.setFechaRegistro(new Date());
//        hc.setTieneSeguro(Boolean.FALSE);
//        historiaClinicaDAO.save(hc);
//
//        return hc;
//    }
}
