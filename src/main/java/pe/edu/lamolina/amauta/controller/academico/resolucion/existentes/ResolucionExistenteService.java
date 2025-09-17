package pe.edu.lamolina.amauta.controller.academico.resolucion.existentes;

import java.util.List;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.Carrera;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.bean.AlumnoCicloCursoBean;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.model.tramite.*;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.enums.TipoResolucionEnum;
import pe.edu.lamolina.model.general.Oficina;

public interface ResolucionExistenteService {

    List<Alumno> allAlumnoByOficina(String nombre, Long instanciaOficina);

    Resolucion findByResolucion(Long resolucion, DataSessionPivot ds);

    List<TipoResolucion> allTipoResolucionByCodigo(List<TipoResolucionEnum> codigos);

    List<CicloAcademico> ciclosAnteriores(int i);

    List<Reincorporacion> allReincorporacionByResolucion(Resolucion resolucionDB);

    List<RetiroCiclo> allRetiroCicloByResolucion(Resolucion resolucionDB);

    List<CambioNota> allCambioNota(Resolucion resolucionDB);

    List<CursoDirigido> allCursodirigido(Resolucion resolucionDB);

    List<CursoDirigido> allCursodirigidoPregrado(Resolucion resolucionDB);

    List<TramiteTraslado> allTramiteTraslado(Resolucion resolucionDB);

    List<Carrera> allCarrera();

    void generarNuevoPlan(Resolucion resolucion, DataSessionPivot ds);

    List<AlumnoCicloCursoBean> allCiclosRepetido(Long idAlumno, DataSessionPivot ds);

    List<String> updateResolucion(Resolucion resolucion, Usuario usuario, DataSessionPivot ds);

    List<ObtencionGrado> allObtencionGrado(Resolucion resolucion);

    List<TramiteBachiller> allTramiteBachiller(Resolucion resolucionDB);

    List<TramiteBachiller> allTramiteBachillerFacultad(Resolucion resolucionDB);

    List<TramiteTitulo> allTramiteTitulo(Resolucion resolucionDB);

    List<TramiteTitulo> allTramiteTituloFacultad(Resolucion resolucion);

    List<PracticasPreProfesional> allPracticasPreProfesionales(Resolucion resolucionDB);

    List<TramiteBachiller> allBachiller(DataSessionPivot ds);

    List<TramiteBachiller> allBachillerFacultad(DataSessionPivot ds);

    List<TramiteTitulo> allTitulos(DataSessionPivot ds);

    List<TramiteTitulo> allTitulosFacultad(DataSessionPivot ds, String facultad);

    List<PracticasPreProfesional> allPracticas(DataSessionPivot ds);

    List<RetiroCiclo> allRetiroCiclo(DataSessionPivot ds);

    List<SancionDisciplina> allSancionDisciplina();
    List<SancionDisciplina> allSancionDisciplinaByResolucion(Resolucion resolucion);
    List<SancionDisciplinaCiclo> allSancioCicloAlumnoByResolucion(Resolucion resolucion);

    List<Reincorporacion> allReincorporacion();

    List<Readmision> allReadmision();

    List<CambioPlanCurricular> allCambioPlanCurricular();

    List<Readmision> allReadmisionByResolucion(Resolucion resolucion);

    List<CambioPlanCurricular> allCambioPlanCurricularByResolucion(Resolucion resolucion);

    List<TramiteTraslado> allTrasladoInterno(CicloAcademico cicloAcademico);

    List<String> saveResolucion(Resolucion resolucion, DataSessionPivot ds);

    List<Oficina> allOficinasResolucion(DataSessionPivot ds);

    List<CicloAcademico> allCicloAplica(DataSessionPivot ds);

    List<TramiteBachiller> allResulucionFacultad(Resolucion resolucion);

    List<TramiteTraslado> allTramiteTrasladoByResolucion(Resolucion resolucion);

    boolean anularAlumnoDeResolucionTitulo(Resolucion resolucion, TramiteTitulo tramiteTitulo, Usuario usuario, DataSessionPivot ds);

    boolean anularAlumnoDeResolucionBachiller(Alumno alumno, Resolucion resolucion, TramiteBachiller tramiteBachiller, Usuario usuario, DataSessionPivot ds);

    boolean anularAlumnoDeResolucionCursoDirigido(Alumno alumno, Resolucion resolucion, CursoDirigido cursoDirigido, DataSessionPivot ds);

    boolean anularAlumnoDeResolucionTramiteTraslado(Alumno alumno, Resolucion resolucion, TramiteTraslado tramiteTraslado, DataSessionPivot ds);

    List<TramiteTitulo> allResulucionTituloFacultad(Resolucion resolucion);

    List<TramiteTitulo> allResulucionTituloFacultadRes(Resolucion resolucion);

    public List<TramiteRenunciaAlumno> allResolucionRenunciaAlumno(Resolucion resolucion);

    public List<TramiteRenunciaAlumno> allRenunciaSolicitados(DataSessionPivot ds);

    public List<TramiteRenunciaAlumno> allRenunciaSolicitadosCarrera(DataSessionPivot ds);

    public void anularSancionDisciplina(Long sancionId);

    List<Alumno> findAlumnosSancionados(String nombre, Long instanciaOficina);
}
