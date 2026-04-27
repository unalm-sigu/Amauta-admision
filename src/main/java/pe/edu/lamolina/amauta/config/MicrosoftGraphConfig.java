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
        return buildJsonTeamsEvent(subject, startDateTime, endDateTime, java.util.Collections.emptyList());
    }

    public String buildJsonTeamsEvent(String subject, java.util.List<String> emailsCoDocentes) throws JsonProcessingException {
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        return buildJsonTeamsEvent(subject, startDateTime, endDateTime, emailsCoDocentes);
    }

    public String buildJsonTeamsEvent(String subject, Date fechaInicio, Date fechaFin) throws JsonProcessingException {
        LocalDateTime start = new java.sql.Timestamp(fechaInicio.getTime()).toLocalDateTime().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime end = new java.sql.Timestamp(fechaFin.getTime()).toLocalDateTime().withHour(23).withMinute(0).withSecond(0);
        return buildJsonTeamsEvent(subject, start, end, java.util.Collections.emptyList());
    }

    public String buildJsonTeamsEvent(String subject, Date fechaInicio, Date fechaFin, java.util.List<String> emailsCoDocentes) throws JsonProcessingException {
        LocalDateTime start = new java.sql.Timestamp(fechaInicio.getTime()).toLocalDateTime().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime end = new java.sql.Timestamp(fechaFin.getTime()).toLocalDateTime().withHour(23).withMinute(0).withSecond(0);
        return buildJsonTeamsEvent(subject, start, end, emailsCoDocentes);
    }

    private String buildJsonTeamsEvent(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime, java.util.List<String> emailsCoDocentes) throws JsonProcessingException {
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

        if (emailsCoDocentes != null && !emailsCoDocentes.isEmpty()) {
            com.fasterxml.jackson.databind.node.ArrayNode attendeesArray = objectMapper.createArrayNode();
            for (String email : emailsCoDocentes) {
                ObjectNode attendee = objectMapper.createObjectNode();
                ObjectNode emailAddress = objectMapper.createObjectNode();
                emailAddress.put("address", email);
                attendee.set("emailAddress", emailAddress);
                attendee.put("type", "required");
                attendeesArray.add(attendee);
            }
            objectNode.set("attendees", attendeesArray);
        }

        return objectMapper.writeValueAsString(objectNode);
    }

    public String buildJsonEventBodyWithJoinUrl(String subject, String joinUrl) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("subject", subject);
        ObjectNode body = objectMapper.createObjectNode();
        body.put("contentType", "HTML");
        body.put("content",
                "<p>Reunión creada automáticamente por el sistema académico.</p>"
                + "<p><strong>Enlace Teams:</strong> <a href=\"" + joinUrl + "\">" + joinUrl + "</a></p>");
        objectNode.set("body", body);
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
        return buildJsonOnlineMeeting(subject, startDateTime, endDateTime, java.util.Collections.emptyList());
    }

    public String buildJsonOnlineMeeting(String subject, java.util.List<String> azureIdsCoDocentes) throws JsonProcessingException {
        LocalDateTime startDateTime = LocalDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0);
        LocalDateTime endDateTime = startDateTime.plusHours(2);
        return buildJsonOnlineMeeting(subject, startDateTime, endDateTime, azureIdsCoDocentes);
    }

    public String buildJsonOnlineMeeting(String subject, Date fechaInicio, Date fechaFin) throws JsonProcessingException {
        LocalDateTime start = new java.sql.Timestamp(fechaInicio.getTime()).toLocalDateTime().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime end = new java.sql.Timestamp(fechaFin.getTime()).toLocalDateTime().withHour(23).withMinute(0).withSecond(0);
        return buildJsonOnlineMeeting(subject, start, end, java.util.Collections.emptyList());
    }

    public String buildJsonOnlineMeeting(String subject, Date fechaInicio, Date fechaFin, java.util.List<String> azureIdsCoDocentes) throws JsonProcessingException {
        LocalDateTime start = new java.sql.Timestamp(fechaInicio.getTime()).toLocalDateTime().withHour(8).withMinute(0).withSecond(0);
        LocalDateTime end = new java.sql.Timestamp(fechaFin.getTime()).toLocalDateTime().withHour(23).withMinute(0).withSecond(0);
        return buildJsonOnlineMeeting(subject, start, end, azureIdsCoDocentes);
    }

    private String buildJsonOnlineMeeting(String subject, LocalDateTime startDateTime, LocalDateTime endDateTime, java.util.List<String> azureIdsCoDocentes) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("subject", subject);
        objectNode.put("startDateTime", startDateTime.format(FORMATTER) + ".0000000Z");
        objectNode.put("endDateTime", endDateTime.format(FORMATTER) + ".0000000Z");

        ObjectNode lobbySettings = objectMapper.createObjectNode();
        lobbySettings.put("scope", "organizer");
        lobbySettings.put("isDialInBypassEnabled", false);
        objectNode.set("lobbyBypassSettings", lobbySettings);

        objectNode.put("allowedPresenters", "roleIsPresenter");
        objectNode.put("allowBreakoutRooms", true);

        if (azureIdsCoDocentes != null && !azureIdsCoDocentes.isEmpty()) {
            com.fasterxml.jackson.databind.node.ArrayNode attendeesArray = objectMapper.createArrayNode();
            for (String azureId : azureIdsCoDocentes) {
                ObjectNode attendee = objectMapper.createObjectNode();
                ObjectNode identity = objectMapper.createObjectNode();
                ObjectNode user = objectMapper.createObjectNode();
                user.put("id", azureId);
                identity.set("user", user);
                attendee.set("identity", identity);
                attendee.put("role", "presenter");
                attendeesArray.add(attendee);
            }
            ObjectNode participants = objectMapper.createObjectNode();
            participants.set("attendees", attendeesArray);
            objectNode.set("participants", participants);
        }

        return objectMapper.writeValueAsString(objectNode);
    }

    public String obtenerAzureIdPorEmail(String email, String token) {
        try {
            String url = "https://graph.microsoft.com/v1.0/users/" + email + "?$select=id";
            HttpResponse<String> response = Unirest.get(url)
                    .header("authorization", "Bearer " + token)
                    .header("accept", "application/json")
                    .asString();
            if (response.getStatus() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response.getBody());
                return jsonNode.path("id").asText();
            } else {
                log.warn("No se pudo obtener Azure ID para email: {}, status: {}", email, response.getStatus());
                return null;
            }
        } catch (Exception e) {
            log.warn("Error al obtener Azure ID para email {}: {}", email, e.getMessage());
            return null;
        }
    }

    public java.util.Map<String, String> resolverUsuarioAzure(String email, String token) {
        try {
            String url = "https://graph.microsoft.com/v1.0/users/" + email
                    + "?$select=id,displayName,mail,userPrincipalName,accountEnabled";
            HttpResponse<String> response = Unirest.get(url)
                    .header("authorization", "Bearer " + token)
                    .header("accept", "application/json")
                    .asString();
            if (response.getStatus() == 200) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(response.getBody());
                String userId = node.path("id").asText();
                String mail = node.path("mail").isNull() ? null : node.path("mail").asText(null);
                String upn = node.path("userPrincipalName").asText(null);
                String correoCanonico = (mail != null && !mail.trim().isEmpty()) ? mail : upn;
                boolean accountEnabled = node.path("accountEnabled").asBoolean(true);
                java.util.Map<String, String> result = new java.util.HashMap<>();
                result.put("userId", userId);
                result.put("correoCanonico", correoCanonico);
                result.put("accountEnabled", String.valueOf(accountEnabled));
                return result;
            } else {
                log.warn("No se pudo resolver usuario Azure para email: {}, status: {}", email, response.getStatus());
                return null;
            }
        } catch (Exception e) {
            log.warn("Error al resolver usuario Azure para email {}: {}", email, e.getMessage());
            return null;
        }
    }
}
