/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import MattermostChatButton from './components/chatButton.vue';
import MattermostChatPopoverButton from './components/chatPopover.vue';

const components = {
  'mattermost-chat-button': MattermostChatButton,
  'mattermost-chat-popover-button': MattermostChatPopoverButton,
};

for (const key in components) {
  Vue.component(key, components[key]);
}

const appId = 'mattermostChatButton';
const lang = window?.eXo?.env?.portal?.language || 'fr';
const i18NUrl = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.mattermostChatButton-${lang}.json`;

export function init(url) {
  console.log(`init chat app ${url}`)
  extensionRegistry.registerComponent('SpacePopover', 'space-popover-action', {
    id: 'mattermostChat',
    vueComponent: Vue.options.components['mattermost-chat-popover-button'],
    rank: 40,
  });

  exoi18n.loadLanguageAsync(lang, i18NUrl).then(i18n => {
    Vue.createApp({
      data: {
        url: url
      },
      template: `<mattermost-chat-button id="${appId}" />`,
      vuetify: Vue.prototype.vuetifyOptions,
      i18n
    },
    `#${appId}`, 'Mattermost chat button');
  });
}
