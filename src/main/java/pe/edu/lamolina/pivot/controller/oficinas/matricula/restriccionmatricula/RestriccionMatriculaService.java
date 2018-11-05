package pe.edu.lamolina.pivot.controller.oficinas.matricula.restriccionmatricula;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import pe.albatross.octavia.dynatable.DynatableFilter;
import pe.edu.lamolina.model.academico.DeudaMaterialAlumno;
import pe.edu.lamolina.model.academico.TipoDeudaMaterial;
import pe.edu.lamolina.pivot.zelper.model.DataSessionPivot;

public interface RestriccionMatriculaService {

    List<DeudaMaterialAlumno> allDeudaAlumno(DynatableFilter filter);

    void anularDeuda(DeudaMaterialAlumno deuda, DataSessionPivot ds);

    void levantarDeuda(DeudaMaterialAlumno deuda, DataSessionPivot ds);

    void guardarDeuda(DeudaMaterialAlumno deudaForm);

    List<TipoDeudaMaterial> allTipoDeudaAlumno();

    List<String> cargarDeudas(MultipartFile file, TipoDeudaMaterial tipo, DataSessionPivot ds);

}
