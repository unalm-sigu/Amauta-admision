package pe.edu.lamolina.pivot.dao.inscripcion.hibernate;

import java.util.List;
import org.springframework.stereotype.Repository;
import pe.albatross.octavia.Octavia;
import pe.albatross.octavia.easydao.AbstractEasyDAO;
import pe.edu.lamolina.model.academico.CicloAcademico;
import pe.edu.lamolina.model.inscripcion.CicloPostula;
import pe.edu.lamolina.model.inscripcion.ModalidadIngreso;
import pe.edu.lamolina.model.inscripcion.ModalidadIngresoCiclo;
import pe.edu.lamolina.pivot.dao.inscripcion.ModalidadIngresoCicloDAO;

@Repository
public class ModalidadIngresoCicloDAOH extends AbstractEasyDAO<ModalidadIngresoCiclo> implements ModalidadIngresoCicloDAO {

    public ModalidadIngresoCicloDAOH() {
        super();
        setClazz(ModalidadIngresoCiclo.class);
    }

    @Override
    public ModalidadIngresoCiclo find(long id) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi")
                .leftJoin("mi.modalidadSuperior")
                .filter("mic.id", id);

        return find(sql);
    }

    @Override
    public List<ModalidadIngresoCiclo> allByCicloPostula(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi", "mi.modalidadEstudio")
                .leftJoin("mi.modalidadSuperior")
                .filter("cp.id", ciclo)
                .orderBy("mi.orden");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<ModalidadIngresoCiclo> allByCicloAcademico(CicloAcademico ciclo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi", "mi.modalidadEstudio", "cp.cicloAcademico ca")
                .leftJoin("mi.modalidadSuperior")
                .filter("ca.id", ciclo)
                .orderBy("mi.orden");

        return sql.all(getCurrentSession());
    }

    @Override
    public List<ModalidadIngresoCiclo> allByFilter(CicloPostula ciclo, Integer rindeExamenAdmision) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi", "mi.modalidadEstudio")
                .leftJoin("mi.modalidadSuperior")
                .filter("cp.id", ciclo)
                .orderBy("mi.orden");

        if (rindeExamenAdmision != null) {
            sql.filter("mic.rindeExamenAdmision", rindeExamenAdmision);
        }

        return all(sql);
    }

    @Override
    public List<ModalidadIngresoCiclo> allSuperioresByCiclo(CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi", "mi.modalidadEstudio")
                .leftJoin("mi.modalidadSuperior ms")
                .filter("cp.id", ciclo)
                .isNull("ms.id")
                .orderBy("mi.orden");

        return all(sql);
    }

    @Override
    public List<ModalidadIngresoCiclo> allByModalidadPadreCiclo(ModalidadIngreso modalidad, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi", "mi.modalidadEstudio", "mi.modalidadSuperior ms")
                .filter("cp.id", ciclo)
                .filter("ms.id", modalidad);

        return all(sql);
    }

    @Override
    public ModalidadIngresoCiclo findByModalidadCiclo(ModalidadIngreso modalidad, CicloPostula ciclo) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi", "mi.modalidadEstudio")
                .leftJoin("mi.modalidadSuperior")
                .filter("mi.id", modalidad)
                .filter("cp.id", ciclo);

        return find(sql);
    }

    @Override
    public List<ModalidadIngresoCiclo> allByModalidadesCiclo(List<ModalidadIngreso> modalidades, List<CicloPostula> ciclos) {
        Octavia sql = Octavia.query()
                .from(ModalidadIngresoCiclo.class, "mic")
                .join("cicloPostula cp", "modalidadIngreso mi")
                .leftJoin("mi.modalidadEstudio", "mi.modalidadSuperior ms")
                .in("cp.id", ciclos)
                .in("mi.id", modalidades);
        return all(sql);
    }

}
