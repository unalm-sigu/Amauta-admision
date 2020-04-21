package pe.edu.lamolina.amauta.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.amauta.dao.tramite.VariablePlantillaDAO;

@Repository
public class VariablePlantillaDAOH extends AbstractEasyDAO<VariablePlantilla> implements VariablePlantillaDAO {

    public VariablePlantillaDAOH() {
        super();
        setClazz(VariablePlantilla.class);
    }

    @Override
    public List<VariablePlantilla> allByPlantilla(PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        Octavia sql = Octavia.query()
                .from(VariablePlantilla.class, "vp")
                .join("plantillaDocumentoAcademico pda", "variableGenerica vg")
                .filter("pda.id", plantillaDocumentoAcademico);
        return all(sql);
    }

    @Override
    public List<VariablePlantilla> allByPlantillaParametro(PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        Octavia sql = Octavia.query()
                .from(VariablePlantilla.class, "vp")
                .join("plantillaDocumentoAcademico pda", "variableGenerica vg")
                .filter("vp.esParametro", 1)
                .filter("pda.id", plantillaDocumentoAcademico);
        return all(sql);
    }

}
