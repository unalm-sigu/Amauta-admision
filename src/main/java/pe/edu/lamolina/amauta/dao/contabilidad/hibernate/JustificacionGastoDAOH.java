package pe.edu.lamolina.amauta.dao.contabilidad.hibernate;

import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.amauta.dao.contabilidad.JustificacionGastoDAO;
import pe.edu.lamolina.model.bienestar.ViajeCurso;
import pe.edu.lamolina.model.contabilidad.JustificacionGasto;

@Repository
public class JustificacionGastoDAOH extends AbstractEasyDAO<JustificacionGasto> implements JustificacionGastoDAO {

    public JustificacionGastoDAOH() {
        super();
        setClazz(JustificacionGasto.class);
    }

    @Override
    public JustificacionGasto find(long id) {
        Octavia sql = Octavia.query()
                .from(JustificacionGasto.class, "jg")
                .join("viajeCurso vc")
                .join("vc.curso", "vc.seccion", "vc.cicloAcademico", "vc.alumnoDelegado", "vc.docenteCreador")
                .filter("jg.id", id);

        return find(sql);
    }

    @Override
    public JustificacionGasto findByViajeCurso(ViajeCurso viajeCurso) {
        Octavia sql = Octavia.query()
                .from(JustificacionGasto.class, "jg")
                .join("viajeCurso vc")
                .join("vc.curso", "vc.seccion", "vc.cicloAcademico", "vc.alumnoDelegado", "vc.docenteCreador")
                .filter("vc.id", viajeCurso);

        return find(sql);
    }

}
