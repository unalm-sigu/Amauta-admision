package pe.edu.lamolina.pivot.dao.encuesta.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.ModalidadEstudio;
import pe.edu.lamolina.model.enums.CicloEstadoEnum;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.pivot.dao.encuesta.CicloPostulaDAO;

@Repository
public class CicloPostulaDAOH extends AbstractEasyDAO<CicloPostula> implements CicloPostulaDAO {

    public CicloPostulaDAOH() {
        super();
        setClazz(CicloPostula.class);
    }

    @Override
    public CicloPostula find(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca")
                .filter("cp.id", ciclo);

        return find(sql);
    }

    @Override
    public CicloPostula find(long id) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("cp.id", id);

        return find(sql);
    }

    @Override
    public CicloPostula findActivo(ModalidadEstudio modalidad) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("me.id", modalidad)
                .filter("cp.estado", CicloEstadoEnum.ACT);

        return find(sql);
    }

    @Override
    public CicloPostula findByCicloAcademico(CicloAcademico cicloAcad) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("ca.id", cicloAcad);

        return find(sql);
    }

    @Override
    public CicloPostula findCicloAnterior() {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("cp.estado", CicloEstadoEnum.CER)
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public CicloPostula findByCodigo(String codigo, ModalidadEstudio modalidad) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("me.id", modalidad)
                .filter("ca.codigo", codigo);

        return find(sql);
    }

    @Override
    public CicloPostula findUltimo() {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(1);

        return find(sql);
    }

    @Override
    public List<CicloPostula> allByDaynatable(DynatableFilter filter, ModalidadEstudio modalidad) {
        DynatableSql sql = new DynatableSql(filter)
                .from(CicloPostula.class, "cip")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("me.id", modalidad)
                .searchFields("ca.year", "ca.numeroCiclo", "ca.codigo", "cip.estado")
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC");

        return all(sql);
    }

    @Override
    public List<CicloPostula> allCicloPostula() {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me");

        return all(sql);
    }

    @Override
    public List<CicloPostula> allCiclosMenores(CicloPostula cicloPostula) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("ca.codigo", "<", cicloPostula.getCicloAcademico().getCodigo())
                .orderBy("ca.codigo desc");

        return all(sql);

    }

    @Override
    public List<CicloPostula> allAnteriores(int ciclos, CicloPostula cicloPostula) {
        Octavia sql = Octavia.query()
                .from(CicloPostula.class, "cp")
                .join("cicloAcademico ca", "ca.modalidadEstudio me")
                .filter("ca.codigo", "<", cicloPostula.getCicloAcademico().getCodigo())
                .orderBy("ca.year DESC", "ca.numeroCiclo DESC")
                .limit(ciclos);

        return all(sql);
    }

}
