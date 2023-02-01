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
import pe.edu.lamolina.model.encuestaestudiantil.RespuestaEncuestaAlumno;
import pe.edu.lamolina.model.enums.EncuestaEstudiantilEstadoEnum;

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
    public List<PuntajeEncuestaDocente> allByDocenteModalidadCicloAcademicoActivo(Docente docente, ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .join("ed.docenteSeccion ds", "ds.docente d", "ds.seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso c", "c.modalidadEstudio me")
                .filter("d.id", docente)
                .filter("ca.id", cicloAcademico)
                .filter("me.id", modalidadEstudio)
                .filter("ed.estado", EncuestaEstudiantilEstadoEnum.ACT.name())
                .orderBy("c.codigo");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocente> allByModalidadCicloAcademico(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .join("ed.docenteSeccion ds", "ds.docente d", "ds.seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso c", "c.modalidadEstudio me")
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
    @Override
    public List<PuntajeEncuestaDocente> allByEncuestaDocenteActivo(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .filter("ed.id", encuestaDocente)
                .filter("ed.estado", EncuestaEstudiantilEstadoEnum.ACT.name())
                .orderBy("te.nombre");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocente> allByModalidadEncuestaCicloAcademico(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .join("ed.docenteSeccion ds", "ds.docente d", "ds.seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso c", "c.modalidadEstudio me")
                .join("ed.modalidadEstudio mo")
                .filter("ca.id", cicloAcademico)
                .filter("mo.id", modalidadEstudio)
                .orderBy("c.codigo");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocente> allByModalidadEncuestaCicloAcademicoACT(ModalidadEstudio modalidadEstudio, CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query(PuntajeEncuestaDocente.class, "ped")
                .join("encuestaDocente ed", "temaEncuesta te")
                .join("ed.docenteSeccion ds", "ds.docente d", "ds.seccion s", "s.grupoSeccion gs", "gs.cicloAcademico ca", "gs.curso c", "c.modalidadEstudio me")
                .join("ed.modalidadEstudio mo")
                .filter("ca.id", cicloAcademico)
                .filter("mo.id", modalidadEstudio)
                .filter("ed.estado", EncuestaEstudiantilEstadoEnum.ACT.name())
                .orderBy("c.codigo");

        return all(sql);
    }

    @Override
    public List<PuntajeEncuestaDocente> findInfo(EncuestaDocente encuestaDocente) {
        Octavia sql = Octavia.query()
                .select("rea.encuestaDocente", "pe.tema", "avg(op.numero)", "stddev(op.numero)")
                .into(PuntajeEncuestaDocente.class)
                .from(RespuestaEncuestaAlumno.class, "rea")
                .join("rea.opcion op", "op.pregunta pe", "rea.encuestaDocente ed", "pe.tema")
                //.filter("pe.tipo", TipoPreguntaEncuestaEnum.LIKERT)
                .filter("ed.id", encuestaDocente)
                .groupBy("rea.encuestaDocente", "pe.tema");

        return all(sql);
    }

}
