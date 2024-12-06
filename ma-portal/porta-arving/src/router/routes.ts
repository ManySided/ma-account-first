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
        path: 'account',
        component: () => import('pages/account/AccountPage.vue'),
      },
      {
        path: 'category',
        name: 'category',
        component: () => import('pages/category/CategoryPage.vue'),
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
        path: 'debts',
        name: 'debts',
        component: () => import('pages/debt/DebtListPage.vue'),
      },
      {
        path: 'debt/:debtId',
        name: 'debtEdit',
        component: () => import('pages/debt/DebtEditPage.vue'),
        props: true
      },
      {
        path: 'debt',
        name: 'debtView',
        component: () => import('pages/debt/DebtEditPage.vue')
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
