package org.exoplatform.addons.mattermost;

import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.utils.CommonsUtils;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.SpaceListenerPlugin;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceLifeCycleEvent;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

public class MattermostSpaceListener extends SpaceListenerPlugin {

  private static final Log LOG               = ExoLogger.getLogger(MattermostSpaceListener.class.toString());
  public static final String MATTERMOST_TEAM_ID = "MattermostTeamID";
  public static final String MATTERMOST_TEAM_NAME = "MattermostTeamName";

  @Override
  public void spaceCreated(SpaceLifeCycleEvent event) {
    Space space = event.getSpace();
    String type = Space.OPEN.equalsIgnoreCase(space.getRegistration()) && Space.PRIVATE.equalsIgnoreCase(space.getVisibility()) ? "O" : "I";
    RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
    try {
      String cleanName = space.getPrettyName().replaceAll("[^a-zA-Z0-9]+", "");
      Team mattermostTeam = MattermostUtils.createTeam(cleanName, space.getDisplayName(), type);
      if(mattermostTeam != null) {
        IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
        Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
        Profile spaceProfile = spaceIdentity.getProfile();
        spaceProfile.setProperty(MATTERMOST_TEAM_ID, mattermostTeam.getId());
        spaceProfile.setProperty(MATTERMOST_TEAM_NAME, cleanName);
        identityManager.updateProfile(spaceProfile);
        LOG.info("Mattermost integration: successfully created a team for the space {}", space.getDisplayName());
      }
    } catch (Exception e) {
      LOG.error("Mattermost integration: Could not create a team for space {}", space.getDisplayName(), e);
    } finally {
      RequestLifeCycle.end();
    }
  }

  @Override
  public void spaceRenamed(SpaceLifeCycleEvent event) {
    Space space = event.getSpace();
    LOG.info("Mattermost integration: Updating team bound to the space '{}'", space.getDisplayName());
    RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
    try {
      IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
      Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
      String teamId = (String) spaceIdentity.getProfile().getProperty(MATTERMOST_TEAM_ID);
      if (StringUtils.isNotBlank(teamId)) {
        try {
          Team team = MattermostUtils.getTeamById(teamId);
          if (team != null) {
            team.setDisplayName(space.getDisplayName());
            MattermostUtils.updateTeam(team);
          } else {
            LOG.warn("Could not find a team with Id {} in Mattermost", teamId);
          }
        } catch (Exception e) {
          LOG.error("Mattermost integration: Could not update the display name of team '{}' for space '{}'", teamId, space.getDisplayName(), e);
        }
        LOG.info("Mattermost integration: successfully updated the display name of the team with id {} bound to the space {}", teamId, space.getDisplayName());
      }
    }finally {
      RequestLifeCycle.end();
    }
  }

  @Override
  public void spaceAccessEdited(SpaceLifeCycleEvent event) {
    updateTeamPrivacy(event);
  }

  @Override
  public void spaceRegistrationEdited(SpaceLifeCycleEvent event) {
    updateTeamPrivacy(event);
  }

  private void updateTeamPrivacy(SpaceLifeCycleEvent event) {
    Space space = event.getSpace();
    LOG.info("Mattermost integration: Updating team bound to the space '{}'", space.getDisplayName());
    RequestLifeCycle.begin(ExoContainerContext.getCurrentContainer());
    IdentityManager identityManager = CommonsUtils.getService(IdentityManager.class);
    String teamId = "";
    try {
      Identity spaceIdentity = identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
      teamId = (String) spaceIdentity.getProfile().getProperty(MATTERMOST_TEAM_ID);
      if(StringUtils.isNotBlank(teamId)) {
        Team team = MattermostUtils.getTeamById(teamId);
        if (team != null) {
          String type = Space.OPEN.equals(space.getRegistration()) && Space.PRIVATE.equals(space.getVisibility()) ? "O" : "I";
          MattermostUtils.updateTeamPrivacy(team, type);
        }
      }
    } catch (Exception e) {
      LOG.error("Mattermost integration: Could not update the team '{}' for space '{}'", teamId, space.getDisplayName(), e);
    } finally {
      RequestLifeCycle.end();
    }
    LOG.info("Mattermost integration: successfully updated the team with id {} bound to the space {}", teamId, space.getDisplayName());
  }

}
