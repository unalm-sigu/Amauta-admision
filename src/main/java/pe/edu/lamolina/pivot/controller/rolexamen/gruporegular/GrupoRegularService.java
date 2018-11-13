package pe.edu.lamolina.pivot.controller.rolexamen.gruporegular;

import java.util.List;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface GrupoRegularService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<LetraGrupoRegular> listGruposRegulares(RolExamenes rolExamenes);

    void excluirGrupoRegular(SeccionGrupoRegular seccionGrupoRegular, Usuario usuario);

    void excluirGrupoRegular(GrupoRegularExamen grupoRegularExamen, Usuario usuario);

    void excluirGrupoRegular(AlumnoGrupoRegular seccionGrupoRegular, Usuario usuario);

}
