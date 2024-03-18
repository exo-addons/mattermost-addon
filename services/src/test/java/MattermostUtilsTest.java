import org.exoplatform.addons.mattermost.MattermostUtils;
import org.exoplatform.addons.mattermost.Team;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;


public class MattermostUtilsTest {

  @Before
  public void setUp() {
    System.setProperty("exo.addon.mattermost.userName", "admin");
    System.setProperty("exo.addon.mattermost.password", "Pass@123456");
    System.setProperty("exo.addon.mattermost.url", "http://localhost:8065");
  }

  public void connectToMattermost() {
    try {
      String token = MattermostUtils.getMattermostToken();
      assertNotNull(token);
    } catch (Exception e) {
      fail();
    }
  }

  public void createTeam() {
    try {
      Team team = MattermostUtils.createTeam("test1", "Test Team one", "I");
    } catch (Exception e) {
      fail();
    }
  }
  public void updateTeam() {
    try {
      String teamRandomPrefix = String.valueOf(Math.random() * 1000).substring(0, 3);
      Team team = MattermostUtils.createTeam("team" + teamRandomPrefix, "Test Team " + teamRandomPrefix, "I");
      team.setDisplayName(team.getDisplayName() + " updated");
      Team updatedTeam = MattermostUtils.updateTeam(team);
    } catch (Exception e) {
      fail();
    }
  }

  public void updateTeamVisibility() {
    try {
      String teamRandomPrefix = String.valueOf(Math.random() * 1000).substring(0, 3);
      Team team = MattermostUtils.createTeam("team" + teamRandomPrefix, "Test Team " + teamRandomPrefix, "I");
      Team updatedTeam = MattermostUtils.updateTeamPrivacy(team, "O");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  public void deleteTeam() {
    try {
      Team team = MattermostUtils.getTeamById("s1mrzg8fppgc5ku47znz9ay1uw");
      MattermostUtils.deleteTeam(team);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }
}
