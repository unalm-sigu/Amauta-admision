package pe.edu.lamolina.amauta.controller.log;

import java.util.List;
import pe.edu.lamolina.amauta.zelper.enums.TipoRestriccionEnum;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.academico.TipoRepitencia;
import pe.edu.lamolina.model.general.Aula;
import pe.edu.lamolina.model.horario.GrupoHoras;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface UsuarioProgramacionService {

    public void creacionSeccion(Seccion seccion, Usuario usuario);

    public void creacionDocenteSeccion(DocenteSeccion docenteSeccion, Usuario usuario);

    public void asignacionGrupoHoras(Seccion seccion, GrupoHoras gpoHoras, Usuario usuario);

    public void asignacionDocente(Docente docente, DocenteSeccion docenteSeccion, Usuario usuario);

    public void asignacionAula(Seccion seccion, Aula aula, Usuario usuario);

    public void asignarVacanteSeccion(Seccion seccionForm, Usuario usuario);

    public void activarSeccion(Seccion seccionBD, Usuario usuario);

    public void anularSeccion(Seccion seccioForm, Usuario usuario);

    public void updateRestriccionCapa(Seccion seccionForm, Usuario usuario);

    public void restriccionRepitencia(Seccion seccion, List<TipoRepitencia> tiposRestriccionesSeleccionados, Usuario usuario);

    public void restriccionModalidad(Seccion seccion, TipoRestriccionEnum tipoRestriccionEnum,String restricciones, Usuario usuario);

    public void cancelarSeccion(Seccion seccionBD, Usuario usuario);

    public void bloquearSeccion(Seccion seccion, Usuario usuario);

}
