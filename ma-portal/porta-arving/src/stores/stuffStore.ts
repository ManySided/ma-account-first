import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {date, Notify} from 'quasar';

export const useStuffStore = defineStore('counter', {
  state: () => ({
    accountId: 0,
    accountName: 'Текущий счёт',
    accountCurrentSum: 0.0,
    accountCurrencyShortName: '',
    titlePage: '',
    showLoadState: false,
    ticketsFilterPeriodFrom: date.startOfDate(new Date(), 'month'),
    ticketsFilterPeriodTo: date.endOfDate(new Date(), 'month')
  }),

  getters: {
    getAccountId(state) {
      return state.accountId
    },
    getAccountName(state) {
      return state.accountName
    },
    getAccountCurrentSum(state) {
      if (state.accountCurrentSum)
        return state.accountCurrentSum.toLocaleString();
      return 0.0;
    },
    getAccountCurrencyShortName(state) {
      return state.accountCurrencyShortName
    },
    getTitlePage(state) {
      return state.titlePage;
    },
    isShowLoadState(state) {
      return state.showLoadState;
    },
    getTicketsFilterPeriodFrom(state) {
      return state.ticketsFilterPeriodFrom;
    },
    getTicketsFilterPeriodTo(state) {
      return state.ticketsFilterPeriodTo;
    }
  },

  actions: {
    actionSetAccountId(id: number) {
      this.accountId = id;
    },
    actionUpdateAccountData() {
      api.get('/api/service/account/getAccountById',
        {
          params: {request: this.accountId}
        }).then((response) => {
        this.accountName = response.data.name;
        this.accountCurrentSum = response.data.currentSum;
        this.accountCurrencyShortName = response.data.currency.shortName;
      })
        .catch((error) => {
          Notify.create({
            color: 'negative',
            position: 'top',
            message: error.message,
            icon: 'report_problem'
          })
        })
    },
    actionUpdateTicketsFilterPeriodFrom(d: Date) {
      this.ticketsFilterPeriodFrom = d;
    },
    actionUpdateTicketsFilterPeriodTo(d: Date) {
      this.ticketsFilterPeriodTo = d;
    },
    actionUpdateTitlePage(title: string) {
      this.titlePage = title;
    },
    actionUpdateFlagShowLoad(flag: boolean) {
      this.showLoadState = flag;
    }
  }
});
