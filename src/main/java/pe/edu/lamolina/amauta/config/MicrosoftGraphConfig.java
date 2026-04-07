package pe.edu.lamolina.amauta.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MicrosoftGraphConfig {

    @Value("${microsoft.graph.tenant-id}")
    private String tenantId;

    @Value("${microsoft.graph.client-id}")
    private String clientId;

    @Value("${microsoft.graph.client-secret}")
    private String clientSecret;

    private String accessToken;
    private long tokenExpiration;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public synchronized String obtenerAccessToken() {
        long now = System.currentTimeMillis();
        if (accessToken != null && now < tokenExpiration) {
            return accessToken;
        }

        try {
            String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

            HttpResponse<String> response = Unirest.post(tokenUrl)
                    .header("content-type", "application/x-www-form-urlencoded")
                    .field("grant_type", "client_credentials")
                    .field("client_id", clientId)
                    .field("client_secret", clientSecret)
                    .field("scope", "https://graph.microsoft.com/.default")
                    .asString();

            if (response.getStatus() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getBody());
                accessToken = jsonNode.get("access_token").asText();
                int expiresIn = jsonNode.get("expires_in").asInt();
                tokenExpiration = now + (expiresIn * 1000L) - 60000;
                return accessToken;
            } else {
                log.error("Error al obtener access token de Microsoft Graph, status: {}, body: {}",
                        response.getStatus(), response.getBody());
                throw new RuntimeException("No se pudo obtener access token de Microsoft Graph");
            }
        } catch (UnirestException | java.io.IOException e) {
            log.error("Error al obtener access token de Microsoft Graph", e);
            throw new RuntimeException("Error al obtener access token de Microsoft Graph", e);
        }
    }

    public String buildJsonTeamsEvent(String subject) throws JsonProcessingException {
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        return buildJsonTeamsEvent(subject, startDateTime, endDateTime);
    }

    public String buildJsonTeamsEvent(String subject, Date fechaInicio, Date fechaFin) throws JsonProcessingException {
        LocalDateTime start = new java.sql.Timestamp(fechaInicio.getTime()).toLocalDateTime().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime end = new java.sql.Timestamp(fechaFin.getTime()).toLocalDateTime().withHour(23).withMinute(0).withSecond(0);
        return buildJsonTeamsEvent(subject, start, end);
    }

    private String buildJsonTeamsEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("subject", subject);

        ObjectNode start = objectMapper.createObjectNode();
        start.put("dateTime", startDateTime.format(FORMATTER));
        start.put("timeZone", "America/Lima");
        objectNode.set("start", start);

        ObjectNode end = objectMapper.createObjectNode();
        end.put("dateTime", endDateTime.format(FORMATTER));
        end.put("timeZone", "America/Lima");
        objectNode.set("end", end);

        objectNode.put("isOnlineMeeting", true);
        objectNode.put("onlineMeetingProvider", "teamsForBusiness");

        return objectMapper.writeValueAsString(objectNode);
    }

    public String buildJsonPatchOnlineMeeting() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("isOnlineMeeting", true);
        objectNode.put("onlineMeetingProvider", "teamsForBusiness");
        return objectMapper.writeValueAsString(objectNode);
    }

    public String buildJsonOnlineMeeting(String subject) throws JsonProcessingException {
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        return buildJsonOnlineMeeting(subject, startDateTime, endDateTime);
    }

    public String buildJsonOnlineMeeting(String subject, Date fechaInicio, Date fechaFin) throws JsonProcessingException {
        LocalDateTime start = new java.sql.Timestamp(fechaInicio.getTime()).toLocalDateTime().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime end = new java.sql.Timestamp(fechaFin.getTime()).toLocalDateTime().withHour(23).withMinute(0).withSecond(0);
        return buildJsonOnlineMeeting(subject, start, end);
    }

    private String buildJsonOnlineMeeting(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("subject", subject);
        objectNode.put("startDateTime", startDateTime.format(FORMATTER) + ".0000000Z");
        objectNode.put("endDateTime", endDateTime.format(FORMATTER) + ".0000000Z");
        return objectMapper.writeValueAsString(objectNode);
    }
}
