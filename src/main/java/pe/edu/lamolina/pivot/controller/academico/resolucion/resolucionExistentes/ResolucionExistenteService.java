package pe.edu.lamolina.pivot.controller.academico.resolucion.resolucionExistentes;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bean.AlumnoCicloCursoBean;
import pe.edu.lamolina.model.general.Compania;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface ResolucionExistenteService {

    List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina);

    String saveReincorporacion(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    Resolucion findByResolucion(Long resolucion, DataSessionPivot ds);

    List<TipoResolucion> allTipoResolucion();

    String saveRetiroCiclo(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    List<CicloAcademico> ciclosAnteriores(int i);

    List<Reincorporacion> allReincorporacionByResolucion(Resolucion resolucionDB);

    List<RetiroCiclo> allRetiroCicloByResolucion(Resolucion resolucionDB);

    String saveCambioNota(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    List<CambioNota> allCambioNota(Resolucion resolucionDB);

    List<CursoDirigido> allCursodirigido(Resolucion resolucionDB);

    List<String> saveCursoDirigido(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    void saveTramiteTraslado(Resolucion resolucion, Usuario usuario, CicloAcademico cicloAcademico, Compania compania);

    List<TramiteTraslado> allTramiteTraslado(Resolucion resolucionDB);

    void saveIngresoHisto(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    public List<Carrera> allCarrera();

    public void generarNuevoPlan(Resolucion resolucion, DataSessionPivot ds);

    String saveNotaMasBaja(Resolucion resolucionForm, Usuario usuario, CicloAcademico cicloAcademico, Compania compania);

    public List<AlumnoCicloCursoBean> allCiclosRepetido(Long idAlumno, DataSessionPivot ds);

}
