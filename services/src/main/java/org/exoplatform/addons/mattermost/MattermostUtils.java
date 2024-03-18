package org.exoplatform.addons.mattermost;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonGeneratorImpl;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MattermostUtils {

  private static final Log LOG               = ExoLogger.getLogger(MattermostUtils.class.toString());
  public static final String EXO_MATTERMOST_SERVER_URL = "exo.addon.mattermost.url";
  public static final String EXO_ADDON_MATTERMOST_PASSWORD = "exo.addon.mattermost.password";
  public static final String EXO_ADDON_MATTERMOST_USER_NAME = "exo.addon.mattermost.userName";
  public static final String TOKEN = "token";
  public static final String AUTHORIZATION = "Authorization";
  public static final String BEARER = "Bearer ";

  private MattermostUtils() {

  }

  public static Team createTeam(String name, String displayName, String type) throws Exception {
    String url = PropertyManager.getProperty(EXO_MATTERMOST_SERVER_URL) + "/api/v4/teams";
    String payload = """
            {
              "name": "%s",
              "display_name": "%s",
              "type": "%s"
            }
            """.formatted(name, displayName, type);
    try {

      String token = getMattermostToken();
      HttpResponse<String> response = sendHttpPostRequest(url, token, payload);
      if(response.statusCode() >= 200 && response.statusCode() < 300) {
        JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
        return toTeam(jsonGenerator.createJsonObjectFromString(response.body()));
      } else {
        LOG.error("Error creating a team, Mattermost server returned HTTP {} error {}", response.statusCode(), response.body());
        return null; 
      }
    } catch (Exception e) {
      LOG.error("Could not create a team on Mattermost", e);
      throw e;
    }
  }

  public static String getMattermostToken() throws Exception {
    String userName = PropertyManager.getProperty(EXO_ADDON_MATTERMOST_USER_NAME);
    String password = PropertyManager.getProperty(EXO_ADDON_MATTERMOST_PASSWORD);
    String url = PropertyManager.getProperty(EXO_MATTERMOST_SERVER_URL) + "/api/v4/users/login";
    String credentials = """
            {
              "login_id": "%s",
              "password": "%s"
            }
            """.formatted(userName, password);
    if(StringUtils.isBlank(userName) || StringUtils.isBlank(password) || StringUtils.isBlank(url)) {
      throw new IllegalArgumentException("A required parameter for authenticating on Mattermost is missing");
    }
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .POST(HttpRequest.BodyPublishers.ofString(credentials))
            .build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    String token = "";
    if(response.statusCode() == 200 && response.headers().firstValue(TOKEN).isPresent()) {
      token = response.headers().firstValue(TOKEN).get();
    } else {
      throw new Exception("Could not authenticate to Mattermost");
    }
    return token;
  }

  protected static HttpResponse<String> sendHttpGetRequest(String url, String token) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(AUTHORIZATION, BEARER + token)
            .GET()
            .build();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }
  protected static HttpResponse<String> sendHttpPostRequest(String url, String token, String contentAsJson) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(AUTHORIZATION, BEARER + token)
            .POST(HttpRequest.BodyPublishers.ofString(contentAsJson))
            .build();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  protected static HttpResponse<String> sendHttpPutRequest(String url, String token, String contentAsJson) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(AUTHORIZATION, BEARER + token)
            .PUT(HttpRequest.BodyPublishers.ofString(contentAsJson))
            .build();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }
  protected static HttpResponse<String> sendHttpDelRequest(String url, String token) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(AUTHORIZATION, BEARER + token)
            .DELETE()
            .build();
    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  public static Team updateTeam(Team team) throws Exception {
    String url = PropertyManager.getProperty(EXO_MATTERMOST_SERVER_URL) + "/api/v4/teams/" + team.getId();
    String payload = """
            {
              "id": "%s",
              "display_name": "%s",
              "description": "%s",
              "company_name": "%s",
              "allowed_domains": "%s",
              "invite_id": "%s",
              "allow_open_invite": %s
            }
            """.formatted(team.getId(), team.getDisplayName(), team.getDescription(), team.getCompanyName(), team.getAllowedDomains(), team.getInviteId(), team.isAllowOpenInvite());
    try {

      String token = getMattermostToken();
      HttpResponse<String> response = sendHttpPutRequest(url, token, payload);
      if(response.statusCode() >= 200 && response.statusCode() < 300) {
        JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
        return toTeam(jsonGenerator.createJsonObjectFromString(response.body()));
      } else {
        LOG.error("Error Updating a team, Mattermost server returned HTTP {} error {}", response.statusCode(), response.body());
        return null;
      }
    } catch (Exception e) {
      LOG.error("Could not update a team on Mattermost", e);
      throw e;
    }

  }

  public static Team updateTeamPrivacy(Team team, String privacy) throws Exception {
    String url = PropertyManager.getProperty(EXO_MATTERMOST_SERVER_URL) + "/api/v4/teams/" + team.getId() + "/privacy";
    String payload = """
            {
              "privacy": "%s"
            }
            """.formatted(privacy);
    try {

      String token = getMattermostToken();
      HttpResponse<String> response = sendHttpPutRequest(url, token, payload);
      if(response.statusCode() >= 200 && response.statusCode() < 300 ) {
        JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
        return toTeam(jsonGenerator.createJsonObjectFromString(response.body()));
      } else {
        LOG.error("Error Updating a team, Mattermost server returned HTTP {} error {}", response.statusCode(), response.body());
        return null;
      }
    } catch (Exception e) {
      LOG.error("Could not create a team on Mattermost", e);
      throw e;
    }

  }


  public static boolean deleteTeam(Team team) throws Exception {
    String url = PropertyManager.getProperty(EXO_MATTERMOST_SERVER_URL) + "/api/v4/teams/" + team.getId();
    try {
      String token = getMattermostToken();
      HttpResponse<String> response = sendHttpDelRequest(url, token);
      if(response.statusCode() >= 200 && response.statusCode() < 300 ) {
        LOG.info("Team {} deleted successfully!", team);
        return true;
      } else {
        LOG.error("Error Updating a team, Mattermost server returned HTTP {} error {}", response.statusCode(), response.body());
        return false;
      }
    } catch (Exception e) {
      LOG.error("Could not create a team on Mattermost", e);
      throw e;
    }
  }

  /**
   * Sample team object
   * * * {"id":"uqin5inx1bn43xwa8bcuecn1th",
   *     "create_at":1710431111169,
   *     "update_at":1710431111169,
   *     "delete_at":0,
   *     "display_name":"testing",
   *     "name":"testing",
   *     "description":"",
   *     "email":"user.email@mailbox.test",
   *     "type":"I",
   *     "company_name":"",
   *     "allowed_domains":"",
   *     "invite_id":"93suk9d4et8dxxi5ge1wzr6igr",
   *     "allow_open_invite":false,
   *     "scheme_id":null,
   *     "group_constrained":null,
   *     "policy_id":null,
   *     "cloud_limits_archived":false
   *     }
   * @param teamJsonValue
   * @return
   */
  public static Team toTeam(JsonValue teamJsonValue) {
    Team team = new Team();
    team.setId(teamJsonValue.getElement("id").getStringValue());
    team.setName(teamJsonValue.getElement("name").getStringValue());
    team.setDisplayName(teamJsonValue.getElement("display_name").getStringValue());
    team.setType(teamJsonValue.getElement("type").getStringValue());
    team.setDescription(teamJsonValue.getElement("description").getStringValue());
    team.setAllowedDomains(teamJsonValue.getElement("allowed_domains").getStringValue());
    team.setAllowOpenInvite(teamJsonValue.getElement("allow_open_invite").getBooleanValue());
    team.setCompanyName(teamJsonValue.getElement("company_name").getStringValue());
    team.setDescription(teamJsonValue.getElement("description").getStringValue());
    return team;
  }

  public static Team getTeamById(String teamId) throws Exception {
    String url = PropertyManager.getProperty(EXO_MATTERMOST_SERVER_URL) + "/api/v4/teams/" + teamId;
    try {
      String token = getMattermostToken();
      HttpResponse<String> response = sendHttpGetRequest(url, token);
      if(response.statusCode() >= 200 && response.statusCode() < 300 ) {
        JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
        return toTeam(jsonGenerator.createJsonObjectFromString(response.body()));
      } else {
        LOG.error("Error Updating a team, Mattermost server returned HTTP {} error {}", response.statusCode(), response.body());
        return null;
      }
    } catch (Exception e) {
      LOG.error("Could not create a team on Mattermost", e);
      throw e;
    }

  }

  public JsonValue toTeamJsonValue(Team team) throws JsonException {
    JsonGeneratorImpl jsonGenerator = new JsonGeneratorImpl();
    return jsonGenerator.createJsonObject(team);
  }
}
