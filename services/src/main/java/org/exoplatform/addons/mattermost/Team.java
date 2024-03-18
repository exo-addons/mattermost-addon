package org.exoplatform.addons.mattermost;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Team {
  private String id;

  private String name;

  private String displayName;

  private String type;

  private String description;

  private String companyName;

  private String allowedDomains;

  private String inviteId;

  private boolean allowOpenInvite;
}
