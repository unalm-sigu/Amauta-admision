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
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.model.tramite.VariablePlantilla;
import pe.edu.lamolina.pivot.dao.academico.AlumnoDAO;
import pe.edu.lamolina.pivot.dao.general.IdiomaDAO;
import pe.edu.lamolina.pivot.dao.tramite.ConstanciaPlantillaDAO;
import pe.edu.lamolina.pivot.dao.tramite.VariableGenericaDAO;
import pe.edu.lamolina.pivot.dao.tramite.VariablePlantillaDAO;
import pe.edu.lamolina.pivot.dao.tramite.PlantillaDocumentoAcademicoDAO;

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
    
    @Autowired
    PlantillaDocumentoAcademicoDAO plantillaDocumentoAcademicoDAO;
    
    @Autowired
    ConstanciaPlantillaDAO constanciaPlantillaDAO;
    
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
        plantilla.setContenido(plantillaDoc.getContenido());
        //plantillaConstanciaDAO.update(plantilla);
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
                allVariableMap.put(variable.getCodigo(), vp);
            }
        }
        plantilla.setVariablePlantilla(new ArrayList(allVariableMap.values()));
        return plantilla;
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
    
    @Override
    public PlantillaGenerica fillPlantilla(Alumno alumno, PlantillaDocumentoAcademico plantillaForm) {
        
        PlantillaGenerica plantilla = new PlantillaGenerica();
        PlantillaDocumentoAcademico pda = plantillaDocumentoAcademicoDAO.find(plantillaForm.getId());
        plantilla.setContenido(pda.getContenido());
        
        if (TipoDocumentoAcademicoEnum.ALIANZAESTRATEGICAEMPRESARIAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALIANZAESTRATEGICAEMPRESARIAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNOESPECIAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNOESPECIAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNOREGULAR.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNOREGULAR.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNOVISITANTE.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ALUMNOVISITANTE.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.BACHILLER.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.BACHILLER.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.BACHILLERCONFECHAEGRESO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.BACHILLERCONFECHAEGRESO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.COLEGIATURA.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.COLEGIATURA.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.COMBINANDOTERICIOYQUINTO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.COMBINANDOTERICIOYQUINTO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.COMPARATIVO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.COMPARATIVO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.CUADRODEHORAS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.CUADRODEHORAS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESCUELANACIONALDEAGRICULTURAESPECIAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESCUELANACIONALDEAGRICULTURAESPECIAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALCOMPARATIVOYPORCENTAJE.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALCOMPARATIVOYPORCENTAJE.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALCONTINUARESTUDIOSENELEXTRANJERO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALCONTINUARESTUDIOSENELEXTRANJERO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALCONVERSIONDESISTEMACALIFICACION.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALCONVERSIONDESISTEMACALIFICACION.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALDURACIONCICLO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALDURACIONCICLO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALDURACIONDECICLO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALDURACIONDECICLO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALPRIMERAMATRICULA.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALPRIMERAMATRICULA.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALPROMEDIOACUMULADODELOSCICLOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALPROMEDIOACUMULADODELOSCICLOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALPROMEDIOVIGESIMAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESPECIALPROMEDIOVIGESIMAL.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ESTUDIOSININTERRUMPIDOSOCONTINUOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
            plantilla.setAlumno("holamama");
            plantilla.setCodigoalumno("holamama");
            plantilla.setContenido("holamama");
            plantilla.setFacultad("holamama");
            plantilla.setFecha("holamama");
            plantilla.setJefeoficina("holamama");
            plantilla.setMatriculado("holamama");
            plantilla.setNombre("holamama");
            plantilla.setNumero("holamama");
            plantilla.setSerie("holamama");
            plantilla.setYearfinciclo("holamama");
            plantilla.setYeariniciociclo("holamama");
        } else if (TipoDocumentoAcademicoEnum.ESTUDIOSININTERRUMPIDOSOCONTINUOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.NIVELACADEMICO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.NIVELACADEMICO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.NIVELACADEMICODEEXALUMNOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.NIVELACADEMICODEEXALUMNOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.NOSEPARADO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.NOSEPARADO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOALUMNO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOALUMNO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOALUMNOSVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOALUMNOSVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOEGRESADOFACULTADESPECIALIDADPROMOCION.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOEGRESADOFACULTADESPECIALIDADPROMOCION.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOEGRESADOVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEMERITOEGRESADOVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENDEVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENMERITOCONTERCIOYQUINTO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.ORDENMERITOCONTERCIOYQUINTO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.QUINTOSUPERIORALUMNO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.QUINTOSUPERIORALUMNO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.QUINTOSUPERIORVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.QUINTOSUPERIORVARIOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.SISTEMACALIFICACION.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.SISTEMACALIFICACION.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TEORIAPRACTICACREDITO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TEORIAPRACTICACREDITO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TERCIODELOSCICLOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TERCIODELOSCICLOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TERCIOSUPERIOR.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TERCIOSUPERIOR.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TERCIOQUINTOCOMBINADOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TERCIOQUINTOCOMBINADOS.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TITULO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.TITULO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.CURSOSDELPRIMERCICLO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.ES.getAcronimo()) {
        } else if (TipoDocumentoAcademicoEnum.CURSOSDELPRIMERCICLO.getValue() == pda.getTipoDocumentoAcademico().getId().longValue() && pda.getIdioma().getCodigo() == IdiomaEnum.EN.getAcronimo()) {
        }
        return plantilla;
    }
}
