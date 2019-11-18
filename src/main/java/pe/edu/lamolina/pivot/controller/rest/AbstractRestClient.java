package pe.edu.lamolina.pivot.controller.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pe.albatross.zelpers.dynatable.DynatableFilter;
import pe.albatross.zelpers.dynatable.DynatableResponse;
import pe.albatross.zelpers.miscelanea.JsonResponse;
import pe.albatross.zelpers.miscelanea.PhobosException;

public abstract class AbstractRestClient<T> {

    @Autowired
    BackendInfo backendInfo;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected T findBackEnd(String urlBackurlBack, Class<T> responseType) {

        RestTemplate rest = new RestTemplate();
        rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

        T t = rest.getForObject(urlBackurlBack, responseType);

        logger.debug("OBJETO: {}", t.toString());

        return t;
    }

    protected JsonResponse getToBackEnd(String urlBack) {

        RestTemplate rest = new RestTemplate();
        rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

        JsonResponse json = rest.getForObject(urlBack, JsonResponse.class);

        logger.debug("JSON: {}", json);

        return json;
    }

    protected List<T> allBackEnd(String urlBack, Class<T> responseType) {
        try {

            RestTemplate rest = new RestTemplate();
            rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

            ResponseEntity<String> responseEntity = rest.exchange(
                    urlBack,
                    HttpMethod.GET,
                    new HttpEntity(null),
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, responseType);

            System.out.println(responseEntity.getBody());

            List<T> list = mapper.readValue(responseEntity.getBody(), javaType);

            logger.debug("All Get {}", list.size());

            return list;
        } catch (RestClientException | IllegalArgumentException | IOException e) {
            e.printStackTrace();
            throw new PhobosException("Error de conexión con el servidor.");
        }

    }

    protected List<T> allBackEnd(String urlBack, Object filter, Class<T> responseType) {
        try {

            String jsonEnvio = this.serialize(filter);

            HttpEntity httpEntity = new HttpEntity(jsonEnvio);

            RestTemplate rest = new RestTemplate();
            rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

            ResponseEntity<String> responseEntity = rest.exchange(
                    urlBack,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final CollectionType javaType = mapper.getTypeFactory().constructCollectionType(List.class, responseType);

            List<T> list = mapper.readValue(responseEntity.getBody(), javaType);

            logger.debug("List Filtered: {}", list.size());

            return list;
        } catch (RestClientException | IllegalArgumentException | IOException e) {
            logger.debug("error {}", e.toString());
            throw new PhobosException("Error de conexión con el servidor.");
        }

    }

    protected DynatableResponse dynatableBackend(String urlBack, DynatableFilter filter) {

        String jsonEnvio = this.serialize(filter);

        HttpEntity httpEntity = new HttpEntity(jsonEnvio);

        RestTemplate rest = new RestTemplate();
        rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

        ResponseEntity<DynatableResponse> dyna = rest.exchange(urlBack, HttpMethod.POST, httpEntity, DynatableResponse.class);

        logger.debug("DynaTable: {}", dyna.getBody().getTotal());

        return dyna.getBody();

    }

    protected JsonResponse postToBackEnd(String urlBack, Object envio) {
        return this.exchangeBackend(urlBack, envio, HttpMethod.POST);
    }

    protected JsonResponse putToBackEnd(String urlBack, Object envio) {
        return this.exchangeBackend(urlBack, envio, HttpMethod.PUT);
    }

    private JsonResponse exchangeBackend(String urlBack, Object envio, HttpMethod method) {

        String jsonEnvio = this.serialize(envio);

        RestTemplate rest = new RestTemplate();
        rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

        HttpEntity httpEntity = new HttpEntity(jsonEnvio);

        ResponseEntity<JsonResponse> responseEntity = rest.exchange(
                urlBack,
                method,
                httpEntity,
                JsonResponse.class
        );

        JsonResponse response = responseEntity.getBody();

        logger.debug("Respuesta: {} ", response.getSuccess());
        logger.debug("\t {} ", response.getData());

        return response;
    }

    protected JsonResponse exchangeBackendHeaders(String urlBack, Object envio, HttpMethod method, HttpHeaders headers) {

        String jsonEnvio = this.serialize(envio);

        RestTemplate rest = new RestTemplate();
        rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

        HttpEntity httpEntity = new HttpEntity(jsonEnvio, headers);

        ResponseEntity<JsonResponse> responseEntity = rest.exchange(
                urlBack,
                method,
                httpEntity,
                JsonResponse.class
        );

        JsonResponse response = responseEntity.getBody();

        logger.debug("Respuesta: {} ", response.getSuccess());
        logger.debug("\t {} ", response.getData());

        return response;
    }

    protected void deleteBackEnd(String urlBack) {
        RestTemplate rest = new RestTemplate();
        rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

        ResponseEntity<String> responseEntity = rest.exchange(
                urlBack,
                HttpMethod.DELETE,
                new HttpEntity(null),
                String.class
        );

        logger.debug("Status: {} (note: 200 is ok)", responseEntity.getStatusCode());
    }

    protected T postToBackEndForObject(String urlBack, Object envio, Class<T> responseType) {

        String jsonEnvio = this.serialize(envio);

        RestTemplate rest = new RestTemplate();
        rest.setInterceptors(Collections.singletonList(new ClientRequestInterceptor()));

        HttpEntity httpEntity = new HttpEntity(jsonEnvio);
        ResponseEntity<T> responseEntity = rest.exchange(
                urlBack,
                HttpMethod.POST,
                httpEntity,
                responseType
        );

        T response = responseEntity.getBody();

        return response;

    }

    private String serialize(Object object) {
        try {

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return mapper.writeValueAsString(object);

        } catch (Exception e) {
            logger.debug("Error al Serialziar");
            return "";
        }

    }

    public class ClientRequestInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(
                HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
                throws IOException {

            HttpHeaders headers = request.getHeaders();
            headers.add("x-token", backendInfo.getToken());

            return execution.execute(request, body);
        }
    }

}
