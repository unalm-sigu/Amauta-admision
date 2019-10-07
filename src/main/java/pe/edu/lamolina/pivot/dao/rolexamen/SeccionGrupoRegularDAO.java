package pe.edu.lamolina.pivot.dao.rolexamen;

import java.util.Date;
import java.util.List;
import java.util.Map;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.easydao.EasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.bean.RolExamenDocente;
import pe.edu.lamolina.model.enums.SeccionRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.GrupoHorasExamen;
import pe.edu.lamolina.model.rolexamen.LetraGrupoRegular;
import pe.edu.lamolina.model.rolexamen.RolExamenes;
import pe.edu.lamolina.model.rolexamen.SeccionGrupoRegular;
import pe.edu.lamolina.model.seguridad.Usuario;

public interface SeccionGrupoRegularDAO extends EasyDAO<SeccionGrupoRegular> {

    SeccionGrupoRegular findBySeccionRolExamenes(Seccion seccion, RolExamenes rolExamenes);

    List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
            LetraGrupoRegular letrasGruposRegular, SeccionRolExamenEstadoEnum... estados);

    List<SeccionGrupoRegular> allByLetraGrupoRegularAndEstados(
            List<LetraGrupoRegular> letrasGruposRegular, SeccionRolExamenEstadoEnum... estados);

    void updateEstadoExclusion(SeccionGrupoRegular seccionGrupoRegularUpd);

    void updateEstado(SeccionGrupoRegular seccionGrupoRegularUpd);

    Map<Long, Integer> countByLetrasGruposRegulares(List<LetraGrupoRegular> letraGrupoRegulars, SeccionRolExamenEstadoEnum... estados);

    List<SeccionGrupoRegular> allByLetraGrupoRegularAndSecciones(
            LetraGrupoRegular letrasGruposRegular, List<Seccion> secciones);

    List<SeccionGrupoRegular> allBySecciones(List<Seccion> secciones);

    void deleteByLetraGrupoRegular(LetraGrupoRegular letraGrupoRegular);

    List<SeccionGrupoRegular> allByDynatableLetra(DynatableFilter filter, LetraGrupoRegular letraGrupoRegular);

    List<SeccionGrupoRegular> allByDynatableRol(DynatableFilter filter, RolExamenes rolExamenes);

    List<SeccionGrupoRegular> allByRolExamenes(
            RolExamenes rolExamenes, SeccionRolExamenEstadoEnum... seccionRolExamenEstadoEnums);

    List<RolExamenDocente> allByDocenteAndCiclo(Docente docente, CicloAcademico cicloAcademico);

    List<SeccionGrupoRegular> allByGrupoHorasExamenAndEstados(GrupoHorasExamen grupoHorasExamen, SeccionRolExamenEstadoEnum... seccionRolExamenEstadosEnum);

    List<SeccionGrupoRegular> allByGrupoHorasExamen(List<GrupoHorasExamen> grupoHorasExamenes);

    SeccionGrupoRegular findByRolExamenesSeccion(RolExamenes rol, Seccion seccion, SeccionRolExamenEstadoEnum... estados);

    void createForLetraGrupoRegular(
            List<SeccionGrupoRegular> seccionesGpoRegular,
            LetraGrupoRegular letraGpoRegular,
            Date fecha,
            Usuario user);

    void updateAula(SeccionGrupoRegular seccionGrupoRegularUpd);

    int saveList(List<SeccionGrupoRegular> seccionesGpoReg);

    List<RolExamenDocente> allBySeccionesAndRolExam(RolExamenes rolExam, List<Seccion> listSeccion);

}
