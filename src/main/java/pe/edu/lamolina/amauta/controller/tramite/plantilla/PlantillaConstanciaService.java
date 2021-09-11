package pe.edu.lamolina.amauta.controller.tramite.plantilla;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;

public interface PlantillaConstanciaService {

    void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, DataSessionPivot ds);

    void save(PlantillaDocumentoAcademico plantillaDocumentoAcademico, DataSessionPivot ds);

    List<PlantillaDocumentoAcademico> all(DynatableFilter filter);

    PlantillaDocumentoAcademico find(PlantillaDocumentoAcademico plantillaDocumentoAcademico);

    List<Idioma> allIdioma();

    PlantillaDocumentoAcademico updateContenido(PlantillaDocumentoAcademico plantillaDocumentoAcademico, DataSessionPivot ds);

    Alumno findAlumno(Long idalumno);

    PlantillaGenerica fillPlantilla(PlantillaDocumentoAcademico plantillaForm);

    List<VariableGenerica> allVariableGenericaByPlantilla(PlantillaDocumentoAcademico plantillaDocumentoAcademico);

    AlumnoConstancia findAlumnoConstancia(TipoDocumentoAcademico tipoDoc, Idioma idioma, Alumno alumno, CicloAcademico cicloActual);

    List<VariablePlantilla> allVariablePlantilla(PlantillaDocumentoAcademico documentoAcademico);

    List<VariableGenerica> allVariableGeneral();

    void updateVariable(VariablePlantilla variablePlantilla, DataSessionPivot ds);

    void saveVariable(VariablePlantilla variablePlantilla, DataSessionPivot ds);

    void deleteVariable(Integer idVariablePlantilla);

    void deleteVariables(PlantillaDocumentoAcademico plantillaDocumentoAcademico, DataSessionPivot ds);

    void deletePlantilla(PlantillaDocumentoAcademico plantillaDocumentoAcademico, DataSessionPivot ds);

    List<VariableGenerica> allVariableGeneralFilterByCodigoEnum();

}
