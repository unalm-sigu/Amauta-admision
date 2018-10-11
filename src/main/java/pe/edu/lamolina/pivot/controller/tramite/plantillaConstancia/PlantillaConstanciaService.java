package pe.edu.lamolina.pivot.controller.tramite.plantillaConstancia;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.Idioma;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.PlantillaDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariableGenerica;
import pe.edu.lamolina.model.tramite.TipoDocumentoAcademico;
import pe.edu.lamolina.model.tramite.VariablePlantilla;

public interface PlantillaConstanciaService {

    void update(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);

    void save(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);

    List<PlantillaDocumentoAcademico> all(DynatableFilter filter);

    PlantillaDocumentoAcademico find(PlantillaDocumentoAcademico plantillaDocumentoAcademico);

    List<Idioma> allIdioma();

    PlantillaDocumentoAcademico updateContenido(PlantillaDocumentoAcademico plantillaDocumentoAcademico, Usuario usuario);

    Alumno findAlumno(Long idalumno);

    PlantillaGenerica fillPlantilla( PlantillaDocumentoAcademico plantillaForm);

    List<VariableGenerica> allVariableGenericaByPlantilla(PlantillaDocumentoAcademico plantillaDocumentoAcademico);

    AlumnoConstancia findAlumnoConstancia(TipoDocumentoAcademico tipoDoc, Idioma idioma, Alumno alumno, CicloAcademico cicloActual);

    public List<VariablePlantilla> allVariablePlantilla(PlantillaDocumentoAcademico documentoAcademico);

    public List<VariableGenerica> allVariableGeneral();

    public void updateVariable(VariablePlantilla variablePlantilla, Usuario usuario);

    public void saveVariable( VariablePlantilla variablePlantilla, Usuario usuario);

    public void deleteVariable(Integer idVariablePlantilla);
}
