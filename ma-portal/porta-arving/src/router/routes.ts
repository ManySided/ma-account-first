import {RouteRecordRaw} from 'vue-router';

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        name: 'dashboard',
        path: '/', component: () => import('pages/DashboardPage.vue'),
      }
    ]
  },
  {
    path: '/workspace/:accountId',
    component: () => import('layouts/WorkspaceLayout.vue'),
    props: true,
    children: [
      {
        name: 'account',
        path: 'account', component: () => import('pages/account/AccountPage.vue'),
      },
      {
        path: 'tickets',
        name: 'tickets',
        component: () => import('pages/ticket/TicketListPage.vue'),

      },
      {
        path: 'ticket/edit',
        name: 'ticketEdit',
        component: () => import('pages/ticket/TicketEditPage.vue'),
      },
      {
        path: 'import/csv',
        name: 'importCsv',
        component: () => import('pages/upload/ImportCsvPage.vue'),
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
