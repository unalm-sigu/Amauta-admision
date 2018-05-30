package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaConstanciaDAO;

@Service
@Transactional(readOnly = true)
public class PlantillaConstanciaServiceImpl implements PlantillaConstanciaService {

    @Autowired
    PlantillaConstanciaDAO plantillaConstanciaDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Override
    @Transactional
    public void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {

        plantillaConstanciaDAO.update(plantillaDocumentoAcademico);
    }

    @Override
    @Transactional
    public void updateContenido(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {
        PlantillaDocumentoAcademico academico = plantillaConstanciaDAO.find(plantillaDocumentoAcademico.getId());
        academico.setContenido(plantillaDocumentoAcademico.getContenido());
        Map<String, String> mapVariables = this.getConstants(plantillaDocumentoAcademico.getContenido());
        VariablePlantilla vp = new VariablePlantilla();
        vp.setPlantillaDocumentoAcademico(plantillaDocumentoAcademico);
        vp.setFechaRegistro(new Date());
//        vp.setVariableGenerica(variableGenerica);
        VariableGenerica vg = new VariableGenerica();
//        vg.setCodigo(codigo);
//        vg.setDescripcion(codigo);

        plantillaConstanciaDAO.update(academico);
    }

    @Override
    @Transactional
    public void save(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {
        plantillaDocumentoAcademico.setFechaRegistro(new Date());
        plantillaDocumentoAcademico.setIdUserRegistro(usuario.getId());
        plantillaDocumentoAcademico.setContenido("Constancia");
        plantillaConstanciaDAO.save(plantillaDocumentoAcademico);
    }

    @Override
    public PlantillaDocumentoAcademico findById(PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        return plantillaConstanciaDAO.findById(plantillaDocumentoAcademico.getId());
    }

    @Override
    public List<PlantillaDocumentoAcademico> all(DynatableFilter filter) {
        return plantillaConstanciaDAO.allDynatable(filter);
    }

    @Override
    public List<Idioma> allIdioma() {
        return idiomaDAO.all();
    }

    private Map<String, String> getConstants(String contenido) {
        String partes[] = contenido.split("__");
        Map<String, String> mapVariables = new LinkedHashMap();
        for (String parte : partes) {
            if (this.isAlpha(parte)) {
                String variable = "__" + parte + "__";
                if (contenido.contains(variable)) {
                    mapVariables.put(variable, variable);
                }
            }
        }
        for (Map.Entry<String, String> entry : mapVariables.entrySet()) {
            System.out.println(entry.getKey());
        }
        return mapVariables;
    }

    public static boolean isAlpha(String name) {
        return name.matches("[0-9A-Z]+");
    }
}
