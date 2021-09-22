package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bean.AlumnoCicloCursoBean;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.CambioNota;
import pe.edu.lamolina.model.tramite.CursoDirigido;
import pe.edu.lamolina.model.tramite.Reincorporacion;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.model.tramite.RetiroCiclo;
import pe.edu.lamolina.model.tramite.TipoResolucion;
import pe.edu.lamolina.model.tramite.TramiteTraslado;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.general.Oficina;
import pe.edu.lamolina.model.tramite.CambioPlanCurricular;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.PracticasPreProfesional;
import pe.edu.lamolina.model.tramite.Readmision;
import pe.edu.lamolina.model.tramite.TramiteBachiller;
import pe.edu.lamolina.model.tramite.TramiteTitulo;

public interface ResolucionExistenteService {

    List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina);

    Resolucion findByResolucion(Long resolucion, DataSessionPivot ds);

    List<TipoResolucion> allTipoResolucionByCodigo(List<String> codigos);

    List<CicloAcademico> ciclosAnteriores(int i);

    List<Reincorporacion> allReincorporacionByResolucion(Resolucion resolucionDB);

    List<RetiroCiclo> allRetiroCicloByResolucion(Resolucion resolucionDB);

    List<CambioNota> allCambioNota(Resolucion resolucionDB);

    List<CursoDirigido> allCursodirigido(Resolucion resolucionDB);

    List<TramiteTraslado> allTramiteTraslado(Resolucion resolucionDB);

    List<Carrera> allCarrera();

    void generarNuevoPlan(Resolucion resolucion, DataSessionPivot ds);

    List<AlumnoCicloCursoBean> allCiclosRepetido(Long idAlumno, DataSessionPivot ds);

    List<String> updateResolucion(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    List<ObtencionGrado> allObtencionGrado(Resolucion resolucion);

    public List<TramiteBachiller> allTramiteBachiller(Resolucion resolucionDB);

    public List<TramiteTitulo> allTramiteTitulo(Resolucion resolucionDB);

    public List<PracticasPreProfesional> allPracticasPreProfesionales(Resolucion resolucionDB);

    public List<TramiteBachiller> allBachiller(DataSessionPivot ds);

    public List<TramiteTitulo> allTitulos(DataSessionPivot ds);

    public List<PracticasPreProfesional> allPracticas(DataSessionPivot ds);

    public List<RetiroCiclo> allRetiroCiclo(DataSessionPivot ds);

    public List<Reincorporacion> allReincorporacion();

    public List<Readmision> allReadmision();

    public List<CambioPlanCurricular> allCambioPlanCurricular();

    public List<Readmision> allReadmisionByResolucion(Resolucion resolucion);

    public List<CambioPlanCurricular> allCambioPlanCurricularByResolucion(Resolucion resolucion);

    public List<TramiteTraslado> allTrasladoInterno(CicloAcademico cicloAcademico);

    public List<Oficina> allOFicinasByUser(DataSessionPivot ds);

    public List<String> saveResolucion(Resolucion resolucion, DataSessionPivot ds);

}
