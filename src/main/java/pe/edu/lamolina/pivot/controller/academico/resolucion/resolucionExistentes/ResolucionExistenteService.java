package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionExistentes;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResolucionExistenteService {

    public List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina);

    public List<Alumno> saveReincorporacion(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    public Resolucion findByResolucion(Long resolucion, DataSessionPivot ds);

    public List<TipoResolucion> allTipoResolucion();

    public List<Alumno> saveRetiroCiclo(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    public List<CicloAcademico> ciclosAnteriores(int i);

    public List<Reincorporacion> allReincorporacionByResolucion(Resolucion resolucionDB);

    public List<RetiroCiclo> allRetiroCicloByResolucion(Resolucion resolucionDB);

    public List<Alumno> saveCambioNota(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    public List<CambioNota> allCambioNota(Resolucion resolucionDB);

}
