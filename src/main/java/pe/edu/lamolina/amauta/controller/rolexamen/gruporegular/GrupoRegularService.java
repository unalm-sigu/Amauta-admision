package pe.edu.lamolina.amauta.controller.rolexamen.gruporegular;

import java.util.List;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.rolexamen.AlumnoGrupoRegular;
import pe.edu.lamolina.model.rolexamen.GrupoRegularExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.amauta.zelper.model.DataSessionPivot;

public interface GrupoRegularService {

    List<RolExamenes> allRolExamenesActives(CicloAcademico cicloAcademico);

    RolExamenes findRolExamenes(long rolExamenId);

    LetraGrupoRegular findLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    void calcularExamenesGrupoRegular(RolExamenes rolExamenes, CicloAcademico cicloAcademico, DataSessionPivot ds);

    List<LetraGrupoRegular> listGruposRegulares(RolExamenes rolExamenes);

    List<GrupoRegularExamen> allGruposRegularExamenByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    List<SeccionGrupoRegular> allSeccionesGrupoRegularExamenByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    List<AlumnoGrupoRegular> allAlumnosGrupoRegularByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    void excluirGrupoRegular(SeccionGrupoRegular seccionGrupoRegular, DataSessionPivot ds);

    void excluirGrupoRegular(GrupoRegularExamen grupoRegularExamen, DataSessionPivot ds);

    void excluirGrupoRegular(AlumnoGrupoRegular seccionGrupoRegular, DataSessionPivot ds);

    void activarGrupoRegular(SeccionGrupoRegular seccionGrupoRegular, DataSessionPivot ds);

    List<SeccionGrupoRegular> allSeccionesGpoRegByDynatableLetra(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular);

    List<SeccionGrupoRegular> allSeccionesGpoRegByDynatableRol(DynatableFilter filter, RolExamenes rolExamenes);

    List<AlumnoGrupoRegular> allAlumnosGrupoRegularDynaByLetraGrupoReg(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular);

    void eliminarGruposRegulares(RolExamenes rolExamenes);

    void deleteGrupoRegular(RolExamenes rolExamenes);

    void agregarGruposNuevos(RolExamenes rolExamenes, DataSessionPivot ds);

    List<String> cambiarAula(SeccionGrupoRegular seccionGpoReg, DataSessionPivot ds);

}
