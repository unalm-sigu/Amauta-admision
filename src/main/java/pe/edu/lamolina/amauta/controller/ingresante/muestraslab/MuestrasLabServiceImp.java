package pe.edu.lamolina.amauta.controller.ingresante.muestraslab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
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
import pe.edu.lamolina.model.enums.RecorridoIngresanteEstadoEnum;
import pe.edu.lamolina.model.enums.TipoActividadIngresanteEnum;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaEnfermedad;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.model.medico.Paciente;
import pe.edu.lamolina.amauta.dao.academico.ActividadIngresanteDAO;
import pe.edu.lamolina.amauta.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.amauta.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.amauta.dao.academico.TipoActividadIngresanteDAO;
import pe.edu.lamolina.amauta.dao.laboratorio.HistoriaLaboratorioDAO;
import pe.edu.lamolina.amauta.dao.medico.HistoriaClinicaDAO;
import pe.edu.lamolina.amauta.dao.medico.HistoriaEnfermedadDAO;
import pe.edu.lamolina.amauta.dao.medico.PacienteDAO;
import pe.edu.lamolina.amauta.dao.sip.TurnoEntrevistaObuaeDAO;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.medico.TipoPacienteEnum;

@Slf4j
@Service
@AllArgsConstructor(onConstructor = @__(
        @Autowired))
@Transactional(readOnly = true)
public class MuestrasLabServiceImp implements MuestrasLabService {

    private final ActividadIngresanteDAO actividadIngresanteDAO;
    private final CicloAcademicoDAO cicloAcademicoDAO;
    private final HistoriaClinicaDAO historiaClinicaDAO;
    private final HistoriaEnfermedadDAO historiaEnfermedadDAO;
    private final HistoriaLaboratorioDAO historiaLaboratorioDAO;
    private final PacienteDAO pacienteDAO;
    private final RecorridoIngresanteDAO recorridoIngresanteDAO;
    private final TipoActividadIngresanteDAO tipoActividadIngresanteDAO;
    private final TurnoEntrevistaObuaeDAO turnoEntrevistaObuaeDAO;
    private final VisorMuestrasLab visorMuestrasLab;

    @Override
    public CicloAcademico findCicloActivoAdmision() {
        return cicloAcademicoDAO.findActivoAdmision();
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
            historiaClinica = (historiaClinica == null) ? crearHistoriaClinica(persona, reco.getAlumno(), ds) : historiaClinica;

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

    private HistoriaClinica crearHistoriaClinica(Persona persona, Alumno alumno, DataSessionPivot ds) {
        Paciente pacienteDB = pacienteDAO.findByPersona(persona);

        Paciente paciente = new Paciente();
        if (pacienteDB == null) {
            paciente.setPersona(persona);
            paciente.setTipoPacienteEnum(TipoPacienteEnum.ALU);
            paciente.setAlumno(alumno);
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
        CicloAcademico ciclo = cicloAcademicoDAO.findActivoAdmision();
        List<RecorridoIngresante> listaRecorridos = recorridoIngresanteDAO.allByCiclo(ciclo);
        List<Persona> listaPersonas = new ArrayList();
        for (RecorridoIngresante elem : listaRecorridos) {
            listaPersonas.add(elem.getAlumno().getPersona());
        }

        List<HistoriaLaboratorio> laboratorios = historiaLaboratorioDAO.allByPersonas(listaPersonas);
        List<TurnoEntrevistaObuae> turnos = turnoEntrevistaObuaeDAO.allByCiclo(ciclo);
        TurnoEntrevistaObuae primerTurno = null;
        if (!turnos.isEmpty()) {
            primerTurno = turnos.get(0);
        }

        long numLab = 0;
        if (primerTurno != null) {
            for (HistoriaLaboratorio laboratorio : laboratorios) {
                if (laboratorio.getFechaMuestra().compareTo(primerTurno.getFecha()) >= 0
                        && laboratorio.getNumeroMuestra() != null
                        && laboratorio.getNumeroMuestra() > numLab) {
                    numLab = laboratorio.getNumeroMuestra();
                }
            }
        }
        numLab++;

        visorMuestrasLab.setCicloAcademico(ciclo);
        visorMuestrasLab.setNumeroLab(numLab);
        log.info("[inicializarVisor] Ciclo {} de arranque ", ciclo.getId());
        log.info("[inicializarVisor] Num-lab {} de arranque ", numLab);

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

        Date today = new LocalDate().toDate();
        Date fechaMuestra = new DateTime(laboratorioBD.getFechaMuestra()).toLocalDate().toDate();
        Assert.isTrue(today.equals(fechaMuestra), "No puede eliminarse la muestra de una fecha pasada");

        // Se evalúa si es el último número asignado ANTES de borrar, pero el decremento del
        // contador en memoria se posterga hasta el final: si alguna validación previa rechaza
        // la operación (p. ej. fecha pasada), la transacción hace rollback en BD, pero el
        // decremento en memoria NO es transaccional y dejaría el contador desincronizado.
        boolean eraUltimaMuestra = (laboratorioBD.getNumeroMuestra() + 1 == visorMuestrasLab.getNumeroLab().longValue());

        RecorridoIngresante recorrido = recorridoIngresanteDAO.find(laboratorioForm.getIdRecorridoIngresante());
        recorrido.setNumeroMuestraSangre(null);
        recorridoIngresanteDAO.update(recorrido);

        historiaLaboratorioDAO.delete(laboratorioBD);

        // Solo se decrementa el correlativo tras completar el borrado con éxito.
        if (eraUltimaMuestra) {
            visorMuestrasLab.decrementaNumLab();
        }
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
}
