package pe.edu.lamolina.amauta.controller.academico.promedio;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import pe.edu.lamolina.model.academico.Alumno;
import pe.edu.lamolina.model.academico.AlumnoCiclo;
import pe.edu.lamolina.model.academico.AlumnoCicloCurso;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Egresado;
import pe.edu.lamolina.model.tramite.ObtencionGrado;
import pe.edu.lamolina.model.tramite.Reincorporacion;

@Getter
@Setter
public class BeanPromedios {

    private Alumno alumno;
    private CicloAcademico cicloActivo;
    private ObtencionGrado graduado;
    private Egresado egresado;
    private List<CicloAcademico> ciclos;
    private List<AlumnoCiclo> alumnoCiclos;
    private List<AlumnoCicloCurso> alumnoCicloCursosOperativos;
    private List<AlumnoCicloCurso> alumnoCicloCursosAll;
    private List<Reincorporacion> reincorporaciones;

}
