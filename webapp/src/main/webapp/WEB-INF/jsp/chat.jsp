<div id="mattermostChatButton">
  <div class="VuetifyApp">
    <div data-app="true"
      class="v-application v-application--is-ltr theme--light"
      id="mattermostChat">
      <div class="v-application--wrap">
        <a id="mattermostChatServerLink" href="" target="_blank" class="mx-2 d-none d-sm-block v-btn v-btn--flat v-btn--icon v-btn--round theme--light v-size--default">
          <span class="v-btn__content">
            <i aria-hidden="true" role="button" aria-haspopup="true" aria-expanded="false" class="v-icon notranslate icon-default-color fas fa-comments theme--light" style="font-size: 22px;"></i>
          </span>
        </a>
      </div>
    </div>
      <script type="text/javascript">
        if(eXo.mattermostUrl) {
          document.getElementById('mattermostChatServerLink').setAttribute("href", eXo.mattermostUrl);
          require(['PORTLET/exo-mattermost-webapp/MattermostChatButton'], app => app.init(eXo.mattermostUrl));
        }
      </script>
  </div>
</div>
