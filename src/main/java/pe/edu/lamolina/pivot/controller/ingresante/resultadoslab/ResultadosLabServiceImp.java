package pe.edu.lamolina.pivot.controller.ingresante.resultadoslab;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.inscripcion.TurnoEntrevistaObuae;
import pe.edu.lamolina.model.medico.DiarioLaboratorio;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaEnfermedad;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.dao.academico.CicloAcademicoDAO;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.laboratorio.DiarioLaboratorioDAO;
import pe.edu.lamolina.pivot.dao.laboratorio.HistoriaLaboratorioDAO;
import pe.edu.lamolina.pivot.dao.medico.HistoriaClinicaDAO;
import pe.edu.lamolina.pivot.dao.medico.HistoriaEnfermedadDAO;
import pe.edu.lamolina.pivot.dao.sip.TurnoEntrevistaObuaeDAO;

@Service
@Transactional(readOnly = true)
public class ResultadosLabServiceImp implements ResultadosLabService {

    @Autowired
    HistoriaLaboratorioDAO historiaLaboratorioDAO;

    @Autowired
    RecorridoIngresanteDAO recorridoIngresanteDAO;

    @Autowired
    HistoriaClinicaDAO historiaClinicaDAO;

    @Autowired
    DiarioLaboratorioDAO diarioLaboratorioDAO;

    @Autowired
    CicloAcademicoDAO cicloAcademicoDAO;

    @Autowired
    TurnoEntrevistaObuaeDAO turnoEntrevistaObuaeDAO;

    @Autowired
    HistoriaEnfermedadDAO historiaEnfermedadDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CicloAcademico findCicloActivoAdmision() {
        return cicloAcademicoDAO.findActivoAdmisionPregrado();
    }

    @Override
    public List<TurnoEntrevistaObuae> allTurnos(CicloAcademico ciclo) {
        List<TurnoEntrevistaObuae> turnos = turnoEntrevistaObuaeDAO.allByCiclo(ciclo);
        return turnos;
    }

    @Override
    public List<RecorridoIngresante> ingresantesCiclo(CicloAcademico ciclo) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allConMuestraByCiclo(ciclo);
        infoRecorridosExcel(recorridos);
        return recorridos;
    }

    @Override
    public List<RecorridoIngresante> allRecorridosConMuestra(TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        Date fecha = turno.getFecha();
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allConMuestraByFechaCiclo(fecha, ciclo);
        infoRecorridosExcel(recorridos);
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
    public HistoriaClinica findHistoriaClinica(RecorridoIngresante recorrido) {
        Alumno alumno = recorrido.getAlumno();
        Persona persona = alumno.getPersona();
        HistoriaClinica historia = historiaClinicaDAO.findByPersona(persona);
        return historia;
    }

    @Override
    public List<HistoriaLaboratorio> allLabByPersonas(List<Persona> personas) {

        return historiaLaboratorioDAO.allByPersonas(personas);

    }

    @Override
    @Transactional
    public void saveLaboratorio(HistoriaLaboratorio laboratorio) {
        DiarioLaboratorio diario = getDiarioLabActual();
        laboratorio.setDiarioLaboratorio(diario);
        laboratorio.setFechaAnalisis(new Date());

        if (laboratorio.getValorMuestra() == null && laboratorio.getEstandar() == null) {
            throw new PhobosException("Datos incompletos");
        }

        BigDecimal valorMuestra = laboratorio.getValorMuestra();
        BigDecimal estandar = laboratorio.getEstandar();
        BigDecimal hemoglobina = valorMuestra.multiply(new BigDecimal(18)).divide(estandar, 2, RoundingMode.DOWN);
        BigDecimal tope = new BigDecimal(0.5);
        BigDecimal decimalRevisar = hemoglobina.multiply(new BigDecimal(10)).remainder(BigDecimal.ONE);
        if (decimalRevisar.compareTo(tope) == 1) {
            // redondear
            hemoglobina = hemoglobina.setScale(1, RoundingMode.HALF_UP);
        } else {
            //truncar
            hemoglobina = hemoglobina.setScale(1, RoundingMode.DOWN);
        }

        if (laboratorio.getId() != null) {
            HistoriaLaboratorio labBd = historiaLaboratorioDAO.find(laboratorio.getId());
            labBd.setDiarioLaboratorio(laboratorio.getDiarioLaboratorio());
            labBd.setFechaAnalisis(laboratorio.getFechaMuestra());
            labBd.setValorMuestra(laboratorio.getValorMuestra());
            labBd.setHemoglobina(hemoglobina);
            labBd.setEstandar(estandar);
            labBd.setFactorRH(laboratorio.getFactorRHEnum().name());
            labBd.setTipoSangre(laboratorio.getTipoSangreEnum().name());
            labBd.setObservaciones(laboratorio.getObservaciones());
            historiaLaboratorioDAO.update(labBd);
        } else {
            laboratorio.setHemoglobina(hemoglobina);
            historiaLaboratorioDAO.save(laboratorio);
        }
    }

    @Override
    @Transactional
    public void saveOtherColumns(HistoriaLaboratorio laboratorio) {

        BigDecimal valorMuestra = laboratorio.getValorMuestra();
        BigDecimal estandar = laboratorio.getEstandar();
        BigDecimal hemoglobina = null;
        if (valorMuestra != null && estandar != null) {
            hemoglobina = valorMuestra.multiply(new BigDecimal(18)).divide(estandar, 2, RoundingMode.DOWN);
            BigDecimal tope = new BigDecimal(0.5);
            BigDecimal decimalRevisar = hemoglobina.multiply(new BigDecimal(10)).remainder(BigDecimal.ONE);
            if (decimalRevisar.compareTo(tope) == 1) {
                // redondear
                hemoglobina = hemoglobina.setScale(1, RoundingMode.HALF_UP);
            } else {
                //truncar
                hemoglobina = hemoglobina.setScale(1, RoundingMode.DOWN);
            }
        }
        if (laboratorio.getId() == null) {
            DiarioLaboratorio diario = getDiarioLabActual();
            laboratorio.setDiarioLaboratorio(diario);
            laboratorio.setFechaAnalisis(new Date());

            laboratorio.setValorMuestra(valorMuestra);
            laboratorio.setEstandar(estandar);
            laboratorio.setHemoglobina(hemoglobina);
            historiaLaboratorioDAO.save(laboratorio);
        } else {
            HistoriaLaboratorio historiaLaboratorioUpd = new HistoriaLaboratorio(laboratorio.getId());
            historiaLaboratorioUpd.setValorMuestra(laboratorio.getValorMuestra());
            historiaLaboratorioUpd.setEstandar(laboratorio.getEstandar());
            historiaLaboratorioUpd.setHemoglobina(hemoglobina);
            historiaLaboratorioDAO.updateColumns(historiaLaboratorioUpd, "valorMuestra", "estandar", "hemoglobina");
        }
    }

    @Override
    @Transactional
    public void saveSangre(HistoriaLaboratorio laboratorio) {

//        if (laboratorio.getValorMuestra() == null && laboratorio.getEstandar() == null) {
//            throw new PhobosException("Ingrese la Muestra Abs y la Estándar Abs");
//        }
        if (laboratorio.getId() == null) {
            DiarioLaboratorio diario = getDiarioLabActual();
            laboratorio.setDiarioLaboratorio(diario);
            laboratorio.setFechaAnalisis(new Date());

            HistoriaLaboratorio historiaLaboratorioSave = new HistoriaLaboratorio(laboratorio.getId());
            historiaLaboratorioSave.setTipoSangre(laboratorio.getTipoSangreEnum().name());
            historiaLaboratorioSave.setFactorRH(laboratorio.getFactorRHEnum().name());
            historiaLaboratorioDAO.save(laboratorio);
        } else {
            HistoriaLaboratorio historiaLaboratorioUpd = new HistoriaLaboratorio(laboratorio.getId());
            historiaLaboratorioUpd.setTipoSangre(laboratorio.getTipoSangreEnum().name());
            historiaLaboratorioUpd.setFactorRH(laboratorio.getFactorRHEnum().name());
            historiaLaboratorioDAO.updateColumns(historiaLaboratorioUpd, "tipoSangre", "factorRH");
        }
    }

    @Override
    public List<HistoriaClinica> allHistoriaByPersonas(List<Persona> personas) {
        return historiaClinicaDAO.allByPersonas(personas);
    }

    @Override
    public DiarioLaboratorio getDiarioLabActual() {
        List<DiarioLaboratorio> diarioLabs = diarioLaboratorioDAO.allFechaDesc();
        if (diarioLabs.size() > 0) {
            return diarioLabs.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<RecorridoIngresante> allIngresantesDynatableByPersona(DynatableFilter filter, List<Persona> personas) {
        return recorridoIngresanteDAO.allIngresantesDynatableByPersona(filter, personas);
    }

    @Override
    public List<RecorridoIngresante> allIngresantesByPersona(List<Persona> personas) {
        return recorridoIngresanteDAO.allIngresantesByPersonas(personas);
    }

    @Override
    public List<RecorridoIngresante> allConMuestraByDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allConMuestaByDynatableCiclo(filter, ciclo);
        completarInfoRecorridos(recorridos);
        return recorridos;
    }

    @Override
    public List<RecorridoIngresante> allConMuestraByDynatableTurnoCiclo(DynatableFilter filter, TurnoEntrevistaObuae turno, CicloAcademico ciclo) {
        List<RecorridoIngresante> recorridos = recorridoIngresanteDAO.allConMuestaByDynatableFechaCiclo(filter, turno.getFecha(), ciclo);
        completarInfoRecorridos(recorridos);
        return recorridos;
    }

    private void completarInfoRecorridos(List<RecorridoIngresante> recorridos) {
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
            historiaClinica = (historiaClinica == null) ? new HistoriaClinica() : historiaClinica;

            HistoriaLaboratorio laboratorio = mapLaboratorio.get(persona.getId());
            laboratorio = (laboratorio == null) ? new HistoriaLaboratorio() : laboratorio;
            laboratorio.setHistoriaClinica(historiaClinica);
            laboratorio.setIdRecorridoIngresante(reco.getId());
            reco.setLaboratorio(laboratorio);

            List<HistoriaEnfermedad> historiaEnfermedades = mapHistoriaEnfermedad.get(persona.getId());
            if (historiaEnfermedades != null && !historiaEnfermedades.isEmpty()) {
                reco.setTieneRiesgo(Boolean.TRUE);
            }

        }
    }

    @Override
    public TurnoEntrevistaObuae findTurno(Long idTurno) {
        return turnoEntrevistaObuaeDAO.find(idTurno);
    }

}
