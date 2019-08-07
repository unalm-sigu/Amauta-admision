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
import pe.edu.lamolina.model.academico.ActividadIngresante;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.academico.TipoActividadIngresante;
import pe.edu.lamolina.model.constantines.GlobalConstantine;
import pe.edu.lamolina.model.enums.ExamenMedicoEstadoEnum;
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaEnfermedad;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.model.medico.Paciente;
import pe.edu.lamolina.pivot.dao.academico.ActividadIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.academico.TipoActividadIngresanteDAO;
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

    @Autowired
    TipoActividadIngresanteDAO tipoActividadIngresanteDAO;

    @Autowired
    ActividadIngresanteDAO actividadIngresanteDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CicloAcademico findCicloActivoAdmision() {
        return cicloAcademicoDAO.findActivoAdmisionPregrado();
    }

    @Override
    @Transactional
    public List<RecorridoIngresante> allRecorridosByDynatable(DynatableFilter filter, CicloAcademico ciclo, DataSessionPivot ds) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allByDynatableCiclo(filter, ciclo);
        completarInfoRecorridos(recorridos, ds);
        return recorridos;
    }

    @Override
    @Transactional
    public List<RecorridoIngresante> allRecorridosByDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo, DataSessionPivot ds) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allByDynatableCicloTurno(filter, ciclo, turno);
        completarInfoRecorridos(recorridos, ds);
        return recorridos;
    }

    @Override
    @Transactional
    public List<RecorridoIngresante> allAtendidosByDynatableTurno(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo, DataSessionPivot ds) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allAtendidosByDynatableCicloFecha(filter, ciclo, turno.getFecha());
        completarInfoRecorridos(recorridos, ds);
        return recorridos;
    }

    private void completarInfoRecorridos(List<RecorridoIngresante> recorridos, DataSessionPivot ds) {
        List<Alumno> alumnos = recorridos.stream()
                .map(RecorridoIngresante::getAlumno)
                .collect(Collectors.toList());

        List<Persona> personas = alumnos.stream()
                .map(Alumno::getPersona)
                .collect(Collectors.toList());

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(personas);
        List<HistoriaClinica> historiasClinicas = historiaClinicaDAO.allByPersonas(personas);
        List<ActividadIngresante> actividadIngresantes = actividadIngresanteDAO.allByRecorridoIngresantes(recorridos);
        List<HistoriaEnfermedad> historiasEnfermedades = historiaEnfermedadDAO.allRiesgoByHistoriasClinicas(historiasClinicas);

        Map<Long, HistoriaLaboratorio> mapLaboratorio = TypesUtil.convertListToMap("historiaClinica.paciente.persona.id", laboratorios);
        Map<Long, HistoriaClinica> mapHistoriaClinica = TypesUtil.convertListToMap("paciente.persona.id", historiasClinicas);
        Map<Long, List<HistoriaEnfermedad>> mapHistoriaEnfermedad = TypesUtil.convertListToMapList("historiaClinica.paciente.persona.id", historiasEnfermedades);
        Map<Long, List<ActividadIngresante>> mapRecorridoIngresantes = TypesUtil.convertListToMapList("recorridoIngresante.id", actividadIngresantes);

        for (RecorridoIngresante reco : recorridos) {
            Persona persona = reco.getAlumno().getPersona();
            HistoriaClinica historiaClinica = mapHistoriaClinica.get(persona.getId());
            historiaClinica = (historiaClinica == null) ? crearHistoriaClinica(persona, ds) : historiaClinica;

            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            laboratorio = (laboratorio == null) ? new HistoriaLaboratorio() : laboratorio;
            laboratorio.setHistoriaClinica(historiaClinica);
            laboratorio.setIdRecorridoIngresante(reco.getId());
            reco.setLaboratorio(laboratorio);

            List<HistoriaEnfermedad> historiaEnfermedades = mapHistoriaEnfermedad.get(persona.getId());
            if (historiaEnfermedades != null && !historiaEnfermedades.isEmpty()) {
                reco.setTieneRiesgo(Boolean.TRUE);
            }
            reco.setActividadIngresante(mapRecorridoIngresantes.get(reco.getId()));
        }
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
        infoRecorridosExcel(recorridos);
        Collections.sort(recorridos, new RecorridoIngresante.CompareNombres());

        return recorridos;
    }

    @Override
    public List<RecorridoIngresante> allIngresantesConTurno(TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allConTurno(turno, ciclo);
        infoRecorridosExcel(recorridos);
        Collections.sort(recorridos, new RecorridoIngresante.CompareNombres());

        return recorridos;
    }

    @Override
    public List<RecorridoIngresante> allAtendidos(TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        Date fecha = turno.getFecha();
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allAtendidos(fecha, ciclo);
        infoRecorridosExcel(recorridos);
        Collections.sort(recorridos, new RecorridoIngresante.CompareAtencion());

        return recorridos;
    }

    private void infoRecorridosExcel(List<RecorridoIngresante> recorridos) {
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
        return turnoEntrevistaObuaeDAO.allByCiclo(ciclo);
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
        System.out.println("ciclo.lab=" + ciclo.getId());
        List<RecorridoIngresante> listaRecorridos = recorridoIngresanteDAO.allByCiclo(ciclo);
        System.out.println("Recorridos=" + listaRecorridos.size());
        List<Persona> listaPersonas = new ArrayList();
        for (RecorridoIngresante elem : listaRecorridos) {
            listaPersonas.add(elem.getAlumno().getPersona());
        }
        System.out.println("Personas=" + listaPersonas.size());

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
        System.out.println("numLab=" + numLab);

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

        RecorridoIngresante recorrido = recorridoIngresanteDAO.find(laboratorio.getIdRecorridoIngresante());
        recorrido.setNumeroMuestraSangre(laboratorio.getNumeroMuestra());
        recorridoIngresanteDAO.update(recorrido);

        TipoActividadIngresante tipoActividad = tipoActividadIngresanteDAO.findCodigo(TipoActividadIngresanteEnum.LAB);
        ActividadIngresante actividad = actividadIngresanteDAO.findByRecorridoTipoActividad(recorrido, tipoActividad);
        if (actividad == null) {
            actividad = new ActividadIngresante();
            actividad.setTipoActividadIngresante(tipoActividad);
            actividad.setRecorridoIngresante(recorrido);

            actividad.setFechaEjecucion(today);
            actividad.setFechaRegistro(today);
            actividad.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
            actividad.setUserEjecucion(GlobalConstantine.USUARIO_ADMIN);
            actividadIngresanteDAO.save(actividad);
        } else {
            actividad.setFechaEjecucion(today);
            actividad.setFechaRegistro(today);
            actividad.setEstadoEnum(RecorridoIngresanteEstadoEnum.ACT);
            actividad.setUserEjecucion(GlobalConstantine.USUARIO_ADMIN);
            actividadIngresanteDAO.update(actividad);
        }

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

        RecorridoIngresante recorrido = recorridoIngresanteDAO.find(laboratorioForm.getIdRecorridoIngresante());
        recorrido.setNumeroMuestraSangre(null);
        recorridoIngresanteDAO.update(recorrido);

        historiaLaboratorioDAO.delete(laboratorioBD);

        TipoActividadIngresante tipoActividad = tipoActividadIngresanteDAO.findCodigo(TipoActividadIngresanteEnum.PAGOEXAMED);
        ActividadIngresante actividad = actividadIngresanteDAO.findByRecorridoTipoActividad(recorrido, tipoActividad);
        actividadIngresanteDAO.delete(actividad);
    }

    @Override
    public List<RecorridoIngresante> ingresantesDynatable(DynatableFilter filter, CicloAcademico ciclo) {
        return recorridoIngresanteDAO.allByDynatableCiclo(filter, ciclo);
    }

    @Override
    public TurnoEntrevistaObuae findTurno(Long idTurno) {
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
