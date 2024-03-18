<template>
  <v-tooltip bottom>
    <template #activator="{ on, attrs }">
      <div
          v-bind="attrs"
          v-on="on">
        <v-btn
            :ripple="false"
            icon
            color="primary"
            @click="openMattermostChat($event)">
          <v-icon size="18">fas fa-comments</v-icon>
        </v-btn>
      </div>
    </template>
    <span>
      {{ $t('exo.mattermost.chat.button.tooltip') }}
    </span>
  </v-tooltip>
</template>
<script>
  export default {
    props: {
      identityType: {
        type: String,
        default: '',
      },
      identityId: {
        type: String,
        default: ''
      }
    },
    data: () => ({
      mattermostTeamUrl: '',
    }),
    created() {
      return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/mattermost/teamUrl?spaceId=${this.identityId}`, {
        method: 'GET',
        credentials: 'include',
      }).then(resp => {
        if (!resp || !resp.ok) {
          throw new Error('Could not get the URL of the team !');
        } else {
          return resp.text();
        }
      }).then(data => {
        this.mattermostTeamUrl = data || '';
        console.log(` data loaded ${this.mattermostTeamUrl}`);
      });
    },
    methods: {
      openMattermostChat(event){
        if (event){
          event.preventDefault();
          event.stopPropagation();
        }
        console.log(` before click ${this.mattermostTeamUrl}`);
        if(this.mattermostTeamUrl) {
          window.open(this.mattermostTeamUrl, '_blank');
        }
      }
    }
  };
</script>
