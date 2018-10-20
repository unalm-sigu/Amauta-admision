package pe.edu.lamolina.pivot.dao.tramite.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.PlantillaIncrustacionDocumento;
import pe.edu.lamolina.model.tramite.TramiteDocumentoAcademico;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaIncrustacionDAO;

@Repository
public class PlantillaIncrustacionDAOH extends AbstractEasyDAO<PlantillaIncrustacionDocumento> implements PlantillaIncrustacionDAO {

    public PlantillaIncrustacionDAOH() {
        super();
        setClazz(PlantillaIncrustacionDocumento.class);
    }

    @Override
    public List<PlantillaIncrustacionDocumento> allIncrustacionesByTramite(TramiteDocumentoAcademico documentoAcademico) {
        Octavia sql = new Octavia()
                .from(PlantillaIncrustacionDocumento.class, "pid")
                .join("platillaIncrustacion etra", "tramiteDocumento tt")
                .filter("tt.id", documentoAcademico)
                .orderBy("pid.orden");

        return all(sql);
    }

    @Override
    public PlantillaIncrustacionDocumento findTramiteAndPlantilla(TramiteDocumentoAcademico tramiteDocumentoAcademico, PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        Octavia sql = new Octavia()
                .from(PlantillaIncrustacionDocumento.class, "pid")
                .join("platillaIncrustacion etra", "tramiteDocumento tt")
                .filter("tt.id", tramiteDocumentoAcademico)
                .filter("etra.id", plantillaDocumentoAcademico)
                .orderBy("pid.orden");

        return find(sql);
    }

}
