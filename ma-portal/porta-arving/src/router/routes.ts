import {RouteRecordRaw} from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '/', component: () => import('pages/DashboardPage.vue'),
      }
    ]
  },
  {
    path: '/workspace',
    component: () => import('layouts/WorkspaceLayout.vue'),
    children: [
      {
        name: 'account',
        path: 'account', component: () => import('pages/account/AccountPage.vue'),
      },
      {
        path: 'tickets/:accountId',
        name: 'tickets',
        component: () => import('pages/ticket/TicketListPage.vue'),
        props: true,
      },
      {
        path: 'ticket/:accountId/edit',
        name: 'ticketEdit',
        component: () => import('pages/ticket/TicketEditPage.vue'),
        props: true,
      }
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue'),
  },
];

export default routes;
