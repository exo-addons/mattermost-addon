<template>
  <div class="VuetifyApp">
    <div class="v-application v-application--is-ltr theme--light">
      <div class="v-application--wrap">
        <v-btn
            id="btnChatButton"
            :title="$t('exo.mattermost.chat.button.tooltip')"
            @click="openMattermostChat($event)"
            icon>
          <v-icon size="22" class="my-auto uiIconStatus icon-default-color fas fa-comments" />
        </v-btn>
      </div>
    </div>
  </div>
</template>
<script>
  export default {
    methods: {
      openMattermostChat(event){
      console.log('test again');
        if (event){
          event.preventDefault();
          event.stopPropagation();
        }
        if(this.$root?.url) {
          window.open(this.$root.url, '_blank');
        }
      }
    }
  };
</script>
