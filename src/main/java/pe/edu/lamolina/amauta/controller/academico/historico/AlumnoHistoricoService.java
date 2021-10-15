package pe.edu.lamolina.amauta.controller.academico.historico;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.amauta.controller.seguridad.verificador.VerificadorServiceImp;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.general.TipoDocIdentidad;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.enums.TipoOficinaEnum;
import pe.edu.lamolina.model.general.Persona;

public interface AlumnoHistoricoService {

    List<TipoDocIdentidad> allTiposDocIdentidad();

    List<CicloAcademico> allCicloAcademico();

    public List<Alumno> allAlumnosbyDynatable(DynatableFilter filter, List<Carrera> carreras, String name);

    public String generateCodeRequest();

    public VerificadorServiceImp.CantidadItemsEnum verificarCantidad(TipoOficinaEnum tipoOficinaEnum, HttpServletRequest request, DataSessionPivot ds);

    public List<Carrera> allInstanciasByMenuRol(TipoOficinaEnum tipoOficinaEnum, HttpServletRequest request, DataSessionPivot ds, String codeRequest);

    public Alumno findAlumno(Long idAlumno);

    public void save(Alumno alumno, DataSessionPivot ds);

    public void update(Alumno alumno, DataSessionPivot ds);

    public void delete(Alumno alumno);

    public Persona validarAlumnoDocumento(Persona persona);

}
