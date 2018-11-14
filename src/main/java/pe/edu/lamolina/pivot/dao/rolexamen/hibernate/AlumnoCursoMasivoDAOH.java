package pe.edu.lamolina.pivot.dao.rolexamen.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.enums.AlumnoRolExamenEstadoEnum;
import pe.edu.lamolina.model.rolexamen.AlumnoCursoMasivo;
import pe.edu.lamolina.model.rolexamen.CursoMasivoExamen;
import pe.edu.lamolina.pivot.dao.rolexamen.AlumnoCursoMasivoDAO;

@Repository
public class AlumnoCursoMasivoDAOH extends AbstractEasyDAO<AlumnoCursoMasivo> implements AlumnoCursoMasivoDAO {

    public AlumnoCursoMasivoDAOH() {
        super();
        setClazz(AlumnoCursoMasivo.class);
    }

    
//    @Override
//    public List<AlumnoGrupoRegular> allByLetraGrupoAndEstado(LetraGrupoRegular letraGrupoRegular, AlumnoRolExamenEstadoEnum estadoEnum) {
//        Octavia sql = Octavia.query()
//                .from(AlumnoGrupoRegular.class, "agr")
//                .join("letraGruposRegulares gs", "userRegistro cur")
//                .filter("agr.estado", estadoEnum)
//                .filter("gs.id", letraGrupoRegular);
//        return all(sql);
//    }
//
//    @Override
//    public List<AlumnoGrupoRegular> allByLetraGrupoRegularAndEstados(LetraGrupoRegular letrasGruposRegular,
//            List<AlumnoRolExamenEstadoEnum> estados) {
//        Octavia sql = Octavia.query()
//                .from(AlumnoGrupoRegular.class, "agr")
//                .join("letraGrupoRegular lgr", "userRegistro cur", "agr.alumno alu")
//                .join("alu.persona per")
//                .in("agr.estado", estados)
//                .filter("lgr.id", letrasGruposRegular);
//        return all(sql);
//    }

    @Override
    public List<AlumnoCursoMasivo> allByLetraGrupoRegularAndEstados(CursoMasivoExamen cursoMasivoExamen, List<AlumnoRolExamenEstadoEnum> asList) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
