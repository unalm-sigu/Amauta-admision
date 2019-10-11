package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import com.itextpdf.text.Document;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.Assert;
import pe.albatross.zelpers.miscelanea.PhobosException;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.enums.SexoEnum;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.general.Persona;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.dao.tramite.ConstanciaPlantillaDAO;
import pe.edu.lamolina.pivot.dao.tramite.VariableGenericaDAO;
import pe.edu.lamolina.pivot.dao.tramite.VariablePlantillaDAO;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaDocumentoAcademicoDAO;
import pe.edu.lamolina.pivot.zelper.constant.Constantine;

@Service
@Transactional(readOnly = true)
public class PlantillaConstanciaServiceImpl implements PlantillaConstanciaService {

    @Autowired
    PlantillaDocumentoAcademicoDAO plantillaConstanciaDAO;

    @Autowired
    IdiomaDAO idiomaDAO;

    @Autowired
    VariableGenericaDAO variableGenericaDAO;

    @Autowired
    VariablePlantillaDAO variablePlantillaDAO;

    @Autowired
    AlumnoDAO alumnoDAO;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    @Transactional
    public void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {

        plantillaConstanciaDAO.update(plantillaDocumentoAcademico);
    }

    @Override
    @Transactional
    public PlantillaDocumentoAcademico updateContenido(PlantillaDocumentoAcademico plantillaDoc, Usuario usuario) {
        PlantillaDocumentoAcademico plantilla = plantillaConstanciaDAO.find(plantillaDoc.getId());
        plantilla.setContenido(Constantine.HTML_PRE + plantillaDoc.getContenido() + Constantine.HTML_SUB);
        plantillaConstanciaDAO.update(plantilla);
        Map<String, String> mapVariables = this.getConstants(plantillaDoc.getContenido());
        List<String> formVariable = new ArrayList(mapVariables.values());
        List<VariableGenerica> regVariable = variableGenericaDAO.allByCodigo(formVariable);
        Map<String, VariableGenerica> regVariableMap = TypesUtil.convertListToMap("codigo", regVariable);
        if (regVariableMap == null) {
            regVariableMap = new LinkedHashMap();
        }
        for (String variable : formVariable) {
            VariableGenerica vg = regVariableMap.get(variable);
            if (vg == null) {
                vg = new VariableGenerica();
                vg.setCodigo(variable);
                vg.setDescripcion(variable);
                regVariableMap.put(variable, vg);
            }
        }
        List<VariablePlantilla> allVariable = variablePlantillaDAO.allByPlantilla(plantilla);
        Map<String, VariablePlantilla> allVariableMap = TypesUtil.convertListToMap("variableGenerica.codigo", allVariable);
        if (allVariableMap == null) {
            allVariableMap = new LinkedHashMap();
        }
        for (VariableGenerica variable : regVariableMap.values()) {
            VariablePlantilla vp = allVariableMap.get(variable.getCodigo());
            if (vp == null) {
                vp = new VariablePlantilla();
                vp.setPlantillaDocumentoAcademico(plantilla);
                vp.setUserRegistro(usuario);
                vp.setVariableGenerica(variable);
                vp.setFechaRegistro(new Date());
                allVariableMap.put(variable.getCodigo(), vp);
            }
        }
        plantilla.setVariablePlantilla(new ArrayList(allVariableMap.values()));
        return plantilla;
    }

    @Override
    @Transactional
    public void save(PlantillaDocumentoAcademico plantilla, Usuario usuario) {

        PlantillaDocumentoAcademico plantillaDocumentoAcaDB = plantillaConstanciaDAO.findTipoDocumento(plantilla.getTipoDocumentoAcademico(), plantilla.getIdioma());

        Assert.isNull(plantillaDocumentoAcaDB, "Existe Plantilla en " + plantilla.getIdioma().getNombre() + " para " + plantilla.getTipoDocumentoAcademico().getNombre());

        plantilla.setFechaRegistro(new Date());
        plantilla.setIdUserRegistro(usuario.getId());
        plantilla.setContenido("Constancia");
        plantillaConstanciaDAO.save(plantilla);
    }

    @Override
    public PlantillaDocumentoAcademico find(PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        return plantillaConstanciaDAO.find(plantillaDocumentoAcademico.getId());
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
                    logger.debug("{}", variable);
                    mapVariables.put(variable, variable);
                }
            }
        }
        return mapVariables;
    }

    public static boolean isAlpha(String name) {
        return name.matches("[0-9A-Z]+");
    }

    @Override
    public Alumno findAlumno(Long idalumno) {
        return alumnoDAO.find(new Alumno(idalumno));
    }

    @Autowired
    PlantillaDocumentoAcademicoDAO plantillaDocumentoAcademicoDAO;

    @Autowired
    ConstanciaPlantillaDAO constanciaPlantillaDAO;

    @Override
    public PlantillaGenerica fillPlantilla(PlantillaDocumentoAcademico plantillaForm) {

        PlantillaGenerica plantilla = new PlantillaGenerica();
        PlantillaDocumentoAcademico pda = plantillaDocumentoAcademicoDAO.find(plantillaForm.getId());
        List<VariablePlantilla> vp = variablePlantillaDAO.allByPlantilla(pda);
        String html = pda.getContenido();
        for (VariablePlantilla var : vp) {
            while (html.indexOf(var.getVariableGenerica().getCodigo()) > -1) {
                html = html.replace(var.getVariableGenerica().getCodigo(), var.getEjemplo());
            }
        }
        plantilla.setContenido(html);

        return plantilla;
    }

    @Override
    public List<VariableGenerica> allVariableGenericaByPlantilla(PlantillaDocumentoAcademico plantillaDocumentoAcademico) {
        List<VariablePlantilla> vp = variablePlantillaDAO.allByPlantilla(plantillaDocumentoAcademico);
        Map<Long, VariableGenerica> vgMap = TypesUtil.convertListToMap("variableGenerica.id", "variableGenerica", vp);
        if (vgMap == null) {
            return new ArrayList();
        }
        return new ArrayList(vgMap.values());

    }

    @Override
    public AlumnoConstancia findAlumnoConstancia(TipoDocumentoAcademico tipoDoc, Idioma idioma, Alumno alumno, CicloAcademico cicloActual) {
        if (tipoDoc.getNombre().equals("Alumno Especial") && idioma.getCodigo().equals("ES")) {
            return forAlumnoEspecialEspanol(alumno, cicloActual);
        } else if (tipoDoc.getNombre().equals("Alumno Especial") && idioma.getCodigo().equals("EN")) {
            return forAlumnoEspecialIngles(alumno, cicloActual);
        }
        return null;
    }

    private AlumnoConstancia forAlumnoEspecialEspanol(Alumno alumno, CicloAcademico cicloActual) {
        AlumnoConstancia alu = new AlumnoConstancia();
        Alumno alumnoBD = alumnoDAO.find(alumno);
        alu.setCodigo(alumnoBD.getCodigo());

        Persona persona = alumnoBD.getPersona();
        if (persona.getSexoEnum() == SexoEnum.F) {
            alu.setMatriculado("matriculada");
        } else if (persona.getSexoEnum() == SexoEnum.M) {
            alu.setMatriculado("matriculado");
        } else {
            throw new PhobosException("Este alumno no tiene definido el valor de SEXO");
        }
        alu.setCicloInicio("2014-II");
        alu.setCicloFin("2017-II");

        return alu;
    }

    private AlumnoConstancia forAlumnoEspecialIngles(Alumno alumno, CicloAcademico cicloActual) {
        return null;
    }

    @Override
    public List<VariablePlantilla> allVariablePlantilla(PlantillaDocumentoAcademico documentoAcademico) {
        return variablePlantillaDAO.allByPlantilla(documentoAcademico);
    }

    @Override
    public List<VariableGenerica> allVariableGeneral() {
        return variableGenericaDAO.all();
    }

    @Override
    @Transactional
    public void updateVariable(VariablePlantilla variablePlantillaForm, Usuario usuario) {
        VariablePlantilla plantilla = variablePlantillaDAO.find(variablePlantillaForm.getId());
        plantilla.setEjemplo(variablePlantillaForm.getEjemplo());
        plantilla.setEsParametro(variablePlantillaForm.getEsParametro());
        variablePlantillaDAO.update(plantilla);
    }

    @Override
    @Transactional
    public void saveVariable(VariablePlantilla variablePlantilla, Usuario usuario) {
        variablePlantilla.setUserRegistro(usuario);
        variablePlantilla.setFechaRegistro(new Date());
        variablePlantilla.setEsParametro(variablePlantilla.getEsParametro());
        variablePlantillaDAO.save(variablePlantilla);
    }

    @Override
    @Transactional
    public void deleteVariable(Integer idVariablePlantilla) {
        variablePlantillaDAO.delete(new VariablePlantilla(Long.parseLong(idVariablePlantilla + "")));
    }

    @Override
    @Transactional
    public void deleteVariables(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {
        List< VariablePlantilla> variablePlantillas = variablePlantillaDAO.allByPlantilla(plantillaDocumentoAcademico);
        PlantillaDocumentoAcademico plantillaDocumentoAcademicoDB = plantillaDocumentoAcademicoDAO.find(plantillaDocumentoAcademico);
        for (VariablePlantilla variablePlantilla : variablePlantillas) {
            plantillaDocumentoAcademicoDB = variablePlantilla.getPlantillaDocumentoAcademico();
            variablePlantillaDAO.delete(variablePlantilla);
        }
        
        plantillaDocumentoAcademicoDAO.delete(plantillaDocumentoAcademicoDB);
    }

    @Override
    public void deletePlantilla(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario) {
    }
}
