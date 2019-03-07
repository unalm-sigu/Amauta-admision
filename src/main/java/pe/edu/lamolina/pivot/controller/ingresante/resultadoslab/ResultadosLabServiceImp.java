package pe.edu.lamolina.pivot.controller.ingresante.resultadoslab;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.PhobosException;
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
    public List<RecorridoIngresante> ingresantesCiclo(CicloAcademico ciclo) {

        return recorridoIngresanteDAO.allByCiclo(ciclo);
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
            labBd.setFechaMuestra(laboratorio.getFechaMuestra());
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

}
