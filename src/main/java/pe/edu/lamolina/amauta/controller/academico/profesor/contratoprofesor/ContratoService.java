package pe.edu.lamolina.amauta.controller.academico.profesor.contratoprofesor;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.rrhh.ContratoDocente;
import pe.edu.lamolina.model.tramite.Resolucion;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;
import pe.edu.lamolina.model.rrhh.CategoriaDocente;
import pe.edu.lamolina.model.rrhh.DedicacionDocente;
import pe.edu.lamolina.model.rrhh.SituacionDocente;

public interface ContratoService {

    void addResolucionConsejo(ContratoDocente contratoDocente, Resolucion resolucionConsejo, DataSessionPivot ds);

    void addResolucionFacultad(ContratoDocente contratoDocente, Resolucion resolucionFacultad, DataSessionPivot ds);

    void addVistoBueno(ContratoDocente contratoDocente, DataSessionPivot ds);

    public List<ContratoDocente> allByDynatable(DynatableFilter filter, Docente docente);

    List<CicloAcademico> allCicloByName(String nombre);

    public void finalizar(ContratoDocente contratoDocente, DataSessionPivot ds);

    void save(Docente docente, ContratoDocente contratoDocente, DataSessionPivot ds);

    public List<Resolucion> searchResolucionConsejo(String nombre);

    public List<Resolucion> searchResolucionFacultad(String nombre);

    public void generarGeneral(CicloAcademico cicloOrigen, CicloAcademico cicloDestino, DataSessionPivot ds);

    public void eliminarGeneral(CicloAcademico cicloEliminar);

    public List<CicloAcademico> allCicloAcademicoContrato();

    public void eliminarContratoDocente(ContratoDocente contratoDocente);

    public void updateContratoDocente(DataSessionPivot ds, ContratoDocente contratoDocente);

    List<SituacionDocente> allSituaciones();

    List<CategoriaDocente> allCategorias();

    List<DedicacionDocente> allDedicaciones();

    public List<CicloAcademico> allCicloAcademico();

}
