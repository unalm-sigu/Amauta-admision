package pe.edu.lamolina.amauta.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.Docente;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.encuestaestudiantil.EncuestaDocente;
import pe.edu.lamolina.model.encuestaestudiantil.PuntajeEncuestaDocente;
import pe.edu.lamolina.amauta.dao.encuesta.PuntajeEncuestaDocenteDAO;

@Repository
public class PuntajeEncuestaDocenteDAOH extends AbstractEasyDAO<PuntajeEncuestaDocente> implements PuntajeEncuestaDocenteDAO {

    public PuntajeEncuestaDocenteDAOH() {
        super();
        setClazz(PuntajeEncuestaDocente.class);
    }

    @Override
    public List<PuntajeEncuestaDocente> allByCicloAcademico(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .join("ed.docenteSeccion ds", "ds.docente d", "ds.seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso c", "c.modalidadEstudio me")
                .filter("ca.id", cicloAcademico)
                .orderBy("c.codigo");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocente> allByDocenteModalidadCicloAcademico(Docente docente, ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .join("ed.docenteSeccion ds", "ds.docente d", "ds.seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso c", "c.modalidadEstudio me")
                .filter("d.id", docente)
                .filter("ca.id", cicloAcademico)
                .filter("me.id", modalidadEstudio)
                .orderBy("c.codigo");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocente> allByEncuestaDocente(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .filter("ed.id", encuestaDocente)
                .orderBy("te.nombre");

        return all(sql);
    }

}
