package pe.edu.lamolina.pivot.controller.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pe.albatross.zelpers.miscelanea.TypesUtil;
import pe.edu.lamolina.model.academico.Alumno;

@Component
public class VisorCalculoNotas {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final static String TOKEN_HISTORIAL = "-token-historial";
    public final static String TOKEN_PROMEDIOS = "-token-promedios";
    public final static String TOKEN_CURRICULA = "-token-curriculas";
    public final static String TOKEN_MATRICULABLE = "-token-matriculable";

    private final Map<String, List<Alumno>> mapTokenAlumnos;
    private final Map<String, Integer> mapContador;

    public VisorCalculoNotas() {
        mapTokenAlumnos = new HashMap();
        mapContador = new HashMap();
    }

    public synchronized void createToken(String token, List<Alumno> alumnos) {
        List<Alumno> alumnosToken = mapTokenAlumnos.get(token);
        if (alumnosToken == null) {
            mapTokenAlumnos.put(token, alumnos);
            mapContador.put(token, 0);
            System.out.println(token + " = " + alumnos.size() + " alumnos");
        }
    }

    public synchronized void incrementarToken(String token) {
        if (token == null) {
            return;
        }
        Integer cant = mapContador.get(token);
        System.out.println("token=" + token + " cant=" + cant);
        mapContador.put(token, cant + 1);
    }

    public int getCantidadByToken(String token) {
        if (token == null) {
            return 0;
        }
        return mapContador.get(token);
    }

    public synchronized void destroyToken(String token) {
        List<Alumno> alumnos = mapTokenAlumnos.get(token);
        if (alumnos != null) {
            mapTokenAlumnos.remove(token);
            mapContador.remove(token);
        }
    }

    public boolean estaCompletoToken(String token) {
        List<Alumno> alumnos = TypesUtil.getListNotNull(mapTokenAlumnos.get(token));
        if (alumnos.isEmpty()) {
            System.out.println(token);
            System.out.println("Lista de alumnos del VISOR está vacía = " + alumnos.size());
            return true;
        }
        Integer cant = mapContador.get(token);
        return cant >= alumnos.size();
    }

    public List<Alumno> allAlumnosByToken(String token) {
        return mapTokenAlumnos.get(token);
    }

}
