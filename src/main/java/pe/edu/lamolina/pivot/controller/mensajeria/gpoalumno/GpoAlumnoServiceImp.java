package pe.edu.lamolina.pivot.controller.mensajeria.gpoalumno;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.zelpers.miscelanea.ObjectUtil;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DetalleGrupoAlumno;
import pe.edu.lamolina.model.academico.GrupoAlumno;
import pe.edu.lamolina.model.seguridad.Usuario;
import pe.edu.lamolina.pivot.dao.mensajeria.DetalleGrupoAlumnoDAO;
import pe.edu.lamolina.pivot.dao.mensajeria.GrupoAlumnoDAO;

@Service
@Transactional(readOnly = true)
public class GpoAlumnoServiceImp implements GpoAlumnoService {

    @Autowired
    GrupoAlumnoDAO grupoAlumnoDAO;
    @Autowired
    DetalleGrupoAlumnoDAO detalleGrupoAlumnoDAO;

    @Override
    public List<GrupoAlumno> allByDynatble(DynatableFilter filter) {
        return grupoAlumnoDAO.allByDynatble(filter);
    }

    @Override
    @Transactional
    public void save(GrupoAlumno gpoAlumno, CicloAcademico cicloAcademico, Usuario usuario) {
        grupoAlumnoDAO.save(gpoAlumno);
    }

    @Override
    @Transactional
    public void update(GrupoAlumno gpoAlumno, CicloAcademico cicloAcademico, Usuario usuario) {
        grupoAlumnoDAO.update(gpoAlumno);
    }

    @Override
    @Transactional
    public void eliminar(GrupoAlumno gpoAlumno) {
        grupoAlumnoDAO.delete(gpoAlumno);
    }

    @Override
    public List<DetalleGrupoAlumno> allDetallesByDynatbleGrupoAlumno(DynatableFilter filter, GrupoAlumno grupo) {
        return detalleGrupoAlumnoDAO.allByDynatbleGrupoAlumno(filter, grupo);
    }

    @Override
    public GrupoAlumno findGrupoById(Long id) {
        return grupoAlumnoDAO.find(id);
    }

    @Override
    @Transactional
    public void saveDetalleGrupo(DetalleGrupoAlumno detalleGrupo) {

        if (detalleGrupo.getId() != null) {
            ObjectUtil.eliminarAttrSinId(detalleGrupo);
            detalleGrupoAlumnoDAO.update(detalleGrupo);
        } else {
            detalleGrupoAlumnoDAO.save(detalleGrupo);
        }

    }

    @Override
    @Transactional
    public void eliminarDetalle(DetalleGrupoAlumno detalleGrupo) {
        detalleGrupoAlumnoDAO.delete(detalleGrupo.getId());
    }

}
