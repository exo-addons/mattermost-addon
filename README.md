# eXo Mattermost addon
This addon integrates eXo platform with Mattermost to : 
 - replace eXo chat app with Mattermost teams
 - when a space is created on eXo , a new team is created on Mattermost and linked to that space
 - when a space is renamed, the mattermost display name is changed too


## Configuration
To connect eXo platform to Mattermost, some parameters are needed :

 - **exo.addon.mattermost.url** : the url of the mattermost server
 - **exo.addon.mattermost.userName** and **exo.addon.mattermost.password** : the credentials of a user able to create and edit a team and invite users to it



## Link an existing space to an existing team
To link an existing space to an existing space, use this URL :
'''
http://EXO_SERVER/portal/rest/mattermost/linkSpaceToTeam?spaceName=SPACE_PRETTY_NAME=&teamId=TEAM_ID&teamName=TEAM_NAME
'''
where :

 - SPACE_PRETTY_NAME : the pretty name of the space, we can get it from the space URL
 - TEAM_ID: the ID of the team on Mattermost, use this Call to get the full list of teams and get this parameter http://mattermost_server/api/v4/teams
 - TEAM_NAME: the name of the team on Mattermost, use this Call to get the full list of teams and get this parameter http://mattermost_server/api/v4/teams
