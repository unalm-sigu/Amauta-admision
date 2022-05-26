package pe.edu.lamolina.amauta.controller.academico.promedio;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.Reincorporacion;

@Getter
@Setter
public class ListBeanPromedios {

    private List<CicloAcademico> ciclos;
    private List<CicloAcademico> ciclosActivos;
    private CicloAcademico cicloPregrado;
    private CicloAcademico cicloPosgrado;
    private List<Egresado> egresados;
    private List<AlumnoCiclo> alumnosCiclosAll;
    private List<AlumnoCicloCurso> alumnosCiclosCursosActivos;
    private List<AlumnoCicloCurso> alumnosCiclosCursosAll;
    private List<Reincorporacion> reincorporaciones;
    private List<ObtencionGrado> graduados;

}
