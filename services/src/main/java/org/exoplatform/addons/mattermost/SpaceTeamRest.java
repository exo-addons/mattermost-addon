package org.exoplatform.addons.mattermost;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.exoplatform.commons.utils.PropertyManager;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;
import org.exoplatform.social.core.identity.model.Identity;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.social.core.space.model.Space;
import org.exoplatform.social.core.space.spi.SpaceService;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import static org.exoplatform.addons.mattermost.MattermostSpaceListener.MATTERMOST_TEAM_ID;
import static org.exoplatform.addons.mattermost.MattermostSpaceListener.MATTERMOST_TEAM_NAME;
import static org.exoplatform.addons.mattermost.MattermostUtils.EXO_MATTERMOST_SERVER_URL;

@Path("/mattermost")
@Tag(name = "/mattermost", description = "Managing connection between eXo and Mattermost")
public class SpaceTeamRest implements ResourceContainer {
  private static final Log LOG               = ExoLogger.getLogger(SpaceTeamRest.class);

  private IdentityManager identityManager;
  private SpaceService spaceService;

  public SpaceTeamRest(IdentityManager identityManager, SpaceService spaceService) {
    this.identityManager = identityManager;
    this.spaceService = spaceService;
  }

  @GET
  @Path("/teamUrl")
  @RolesAllowed("users")
  public Response getMattermostTeamUrl(@QueryParam("spaceId") String spaceId) {
    if(StringUtils.isBlank(spaceId)) {
      LOG.error("Could not get the URL for the space, missing space ID");
      return Response.status(Response.Status.BAD_REQUEST).build();
    }
    String matterMostUrl = PropertyManager.getProperty(EXO_MATTERMOST_SERVER_URL);
    Space space = this.spaceService.getSpaceById(spaceId);
    Identity spaceIdentity = this.identityManager.getOrCreateSpaceIdentity(space.getPrettyName());
    if(spaceIdentity != null) {
      matterMostUrl = matterMostUrl.lastIndexOf("/") == matterMostUrl.length() -1 ? matterMostUrl : matterMostUrl + "/";
      matterMostUrl = matterMostUrl + spaceIdentity.getProfile().getProperty(MATTERMOST_TEAM_NAME);
    }
    return Response.ok(matterMostUrl).build();
  }

  @GET
  @Path("/linkSpaceToTeam")
  @RolesAllowed("administrators")
  public Response linkSpaceToTeam(@QueryParam("spaceName") String spaceName,
                                  @QueryParam("teamName") String teamName,
                                  @QueryParam("teamId") String teamId) {
    RequestLifeCycle.begin(PortalContainer.getInstance());
    try {
      if(StringUtils.isBlank(spaceName) || StringUtils.isBlank(teamName) || StringUtils.isBlank(teamId)) {
        LOG.error("Could not connect the space to a team, one of the following parameters is missing => spaceName: {}, teamId: {}, teamName : {}", spaceName, teamId, teamName);
        return Response.status(Response.Status.BAD_REQUEST).build();
      }
      Identity spaceIdentity = this.identityManager.getOrCreateSpaceIdentity(spaceName);
      Profile spaceProfile = spaceIdentity.getProfile();
      spaceProfile.setProperty(MATTERMOST_TEAM_ID, teamId);
      spaceProfile.setProperty(MATTERMOST_TEAM_NAME, teamName);
      this.identityManager.updateProfile(spaceProfile);
      return Response.ok("Space %s was linked to team with ID %s successfully ".formatted(spaceIdentity.getProfile().getFullName(), teamId)).build();
    } catch (Exception e) {
      LOG.error("Could not link space {} to Mattermost team {}", spaceName, teamId);
      return Response.serverError().entity(e.getMessage()).build();
    } finally {
      RequestLifeCycle.end();
    }
  }
}
