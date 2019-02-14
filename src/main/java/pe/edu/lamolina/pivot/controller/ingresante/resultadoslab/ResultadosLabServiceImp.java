package pe.edu.lamolina.pivot.controller.ingresante.resultadoslab;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.RecorridoIngresante;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.medico.DiarioLaboratorio;
import pe.edu.lamolina.model.medico.HistoriaClinica;
import pe.edu.lamolina.model.medico.HistoriaLaboratorio;
import pe.edu.lamolina.pivot.dao.academico.RecorridoIngresanteDAO;
import pe.edu.lamolina.pivot.dao.laboratorio.DiarioLaboratorioDAO;
import pe.edu.lamolina.pivot.dao.laboratorio.HistoriaLaboratorioDAO;
import pe.edu.lamolina.pivot.dao.medico.HistoriaClinicaDAO;

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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<RecorridoIngresante> ingresantesDynatableCiclo(DynatableFilter filter, CicloAcademico ciclo) {

        return recorridoIngresanteDAO.allByDynatableCiclo(filter, ciclo);
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

        return historiaLaboratorioDAO.allByPersona(personas);

    }

    @Override
    @Transactional
    public void saveLaboratorio(HistoriaLaboratorio laboratorio) {
        if (laboratorio.getId() != null) {
            HistoriaLaboratorio labBd = historiaLaboratorioDAO.find(laboratorio.getId());
            labBd.setDiarioLaboratorio(laboratorio.getDiarioLaboratorio());
            labBd.setFechaMuestra(laboratorio.getFechaMuestra());
            labBd.setNumeroMuestra(laboratorio.getNumeroMuestra());
            historiaLaboratorioDAO.update(labBd);
        } else {
            historiaLaboratorioDAO.save(laboratorio);
        }
    }

    @Override
    public List<HistoriaClinica> allHistoriaByPersonas(List<Persona> personas) {
        return historiaClinicaDAO.allByPersona(personas);
    }

    @Override
    public DiarioLaboratorio getDiarioLabActual() {
        List<DiarioLaboratorio> diarioLabs = diarioLaboratorioDAO.allFechaAsc();
        if (diarioLabs.size() > 0) {
            return diarioLabs.get(0);
        } else {
            return null;
        }
    }

}
