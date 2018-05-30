package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pe.edu.lamolina.pivot.dao.tramite.VariableGenericaDAO;
import pe.edu.lamolina.pivot.dao.tramite.VariablePlantillaDAO;

@Service
@Transactional(readOnly = true)
public class PlantillaConstanciaServiceImpl implements PlantillaConstanciaService {

    @Autowired
    PlantillaConstanciaDAO plantillaConstanciaDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Autowired
    VariableGenericaDAO variableGenericaDAO;

    @Autowired
    VariablePlantillaDAO variablePlantillaDAO;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {

        plantillaConstanciaDAO.update(plantillaDocumentoAcademico);
    }

    @Override
    @Transactional
    public void updateContenido(PlantillaDocumentoAcademico plantillaDoc, Usuario usuario) {
        PlantillaDocumentoAcademico academico = plantillaConstanciaDAO.find(plantillaDoc.getId());
        academico.setContenido(plantillaDoc.getContenido());
        plantillaConstanciaDAO.update(academico);
        Map<String, String> mapVariables = this.getConstants(plantillaDoc.getContenido());
        List<String> listVariable = new ArrayList(mapVariables.values());
        List<VariableGenerica> VariableGenerica = variableGenericaDAO.allByCodigo(listVariable);
        List<VariablePlantilla> misVariable = variablePlantillaDAO.allByPlantilla(plantillaDoc);

        VariablePlantilla vp = new VariablePlantilla();
        vp.setPlantillaDocumentoAcademico(plantillaDoc);
        vp.setFechaRegistro(new Date());
//        vp.setVariableGenerica(variableGenerica);
        VariableGenerica vg = new VariableGenerica();
//        vg.setCodigo(codigo);
//        vg.setDescripcion(codigo);

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
        return mapVariables;
    }

    public static boolean isAlpha(String name) {
        return name.matches("[0-9A-Z]+");
    }
}
