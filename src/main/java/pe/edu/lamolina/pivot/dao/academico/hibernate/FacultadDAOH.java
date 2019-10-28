package pe.edu.lamolina.pivot.dao.academico.hibernate;

import java.util.List;
import pe.edu.lamolina.pivot.dao.academico.FacultadDAO;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.albatross.octavia.dynatable.DynatableSql;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.academico.DocenteSeccion;
import pe.edu.lamolina.model.academico.Facultad;
import pe.edu.lamolina.model.academico.Seccion;
import pe.edu.lamolina.model.enums.EstadoEnum;
import pe.edu.lamolina.model.enums.EnteAcademicoEstadoEnum;
import pe.edu.lamolina.model.general.Compania;

@Repository
public class FacultadDAOH extends AbstractEasyDAO<Facultad> implements FacultadDAO {

    public FacultadDAOH() {
        super();
        setClazz(Facultad.class);
    }

    @Override
    public List<Facultad> allDynatable(DynatableFilter filter, List<Facultad> facultads) {

        DynatableSql sql = new DynatableSql(filter)
                .from(Facultad.class, "fa")
                .in("fa.id", facultads)
                .searchFields("fa.nombre", "fa.codigo", "fa.estado")
                .orderBy("fa.id desc");

        return all(sql);
    }

    @Override
    public List<Facultad> allByCompania(Compania compania) {
        Octavia sql = Octavia.query()
                .from(Facultad.class, "fa")
                .join("compania co")
                .filter("co.id", compania);

        return all(sql);
    }

    @Override
    public List<Facultad> allActivos() {
        Octavia sql = Octavia.query()
                .from(Facultad.class, "fa")
                .join("compania")
                .filter("estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public List<Facultad> allFacultad(String nombre, Compania compania) {
        Octavia sql = Octavia.query()
                .from(Facultad.class, "fa")
                .join("compania cia")
                .filter("cia.id", compania)
                .filter("fa.estado", EnteAcademicoEstadoEnum.ACT)
                .beginBlock()
                .__().like("fa.codigo", nombre)
                .__().like("fa.nombre", nombre)
                .endBlock()
                .orderBy("fa.nombre")
                .limit(10);

        return all(sql);
    }

    @Override
    public List<Facultad> allNormal() {
        Octavia sql = Octavia.query()
                .from(Facultad.class, "fa")
                .join("compania")
                .between("codigo", "010", "080")
                .filter("estado", EstadoEnum.ACT);

        return all(sql);
    }

    @Override
    public Facultad findByCodigo(String codigo) {
        Octavia sql = Octavia.query()
                .from(Facultad.class, "fa")
                .filter("fa.codigo", codigo);

        return find(sql);
    }

    @Override
    public List<Facultad> allFromDocentesByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("fac")
                .from(DocenteSeccion.class, "ds")
                .join("seccion sec", "sec.grupoSeccion gs", "gs.cicloAcademico cic")
                .join("ds.docente doc", "doc.departamentoAcademico da", "da.facultad fac")
                .filter("cic.id", cicloAcademico)
                .filter("ds.estado", EstadoEnum.ACT)
                .filter("sec.estado", EstadoEnum.ACT)
                .orderBy("fac.nombre");

        return all(sql);
    }

    @Override
    public List<Facultad> allFromCursosByCiclo(CicloAcademico cicloAcademico) {
        Octavia sql = Octavia.query()
                .selectDistinct("fac")
                .from(Seccion.class, "sec")
                .join("grupoSeccion gs", "gs.cicloAcademico cic")
                .join("gs.curso cur", "cur.departamentoAcademico da", "da.facultad fac")
                .filter("cic.id", cicloAcademico)
                .filter("estado", EstadoEnum.ACT)
                .orderBy("fac.nombre");

        return all(sql);
    }

}
