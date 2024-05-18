import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import Account from 'src/model/dto/AccountDto';

export const useAccountStore = defineStore('account', {
  state: () => ({
    accounts: [] as Account[],
    currentAccount: {} as Account
  }),

  getters: {
    getAccounts(state) {
      return state.accounts;
    },
    getCurrentAccountName(state) {
      return state.currentAccount.name;
    },
    getCurrentAccountSum(state) {
      return state.currentAccount.currentSum;
    }
  },

  actions: {
    loadAccountById(id: number) {
      api.get('/api/service/account/getAccountById', {params: {request: id}}).then((response) => {
        this.currentAccount = response.data
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
    loadAccounts() {
      api.get('/api/service/account/getAccounts').then((response) => {
        this.accounts = response.data
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
    saveAccount(item: Account) {
      if (item.id) {
        api.post('/api/service/account', {item})
          .then(() => {
            Notify.create({
              color: 'positive',
              position: 'top',
              message: 'Счёт создан',
              icon: 'done'
            })
          })
          .catch((error) => {
            Notify.create({
              color: 'negative',
              position: 'top',
              message: error.message || error.errors,
              icon: 'report_problem'
            })
            console.log(error)
          })
      } else {
        api.put('/api/service/account', {item})
          .then(() => {
            Notify.create({
              color: 'positive',
              position: 'top',
              message: 'Счёт обновлён',
              icon: 'done'
            })
          })
          .catch((error) => {
            Notify.create({
              color: 'negative',
              position: 'top',
              message: error.message || error.errors,
              icon: 'report_problem'
            })
            console.log(error)
          })
      }
    },
    removeAccount(item: Account) {
      const params = {accountId: item.id};
      api.delete('/api/service/account', {params})
        .then(() => {
          Notify.create({
            color: 'positive',
            position: 'top',
            message: 'Счёт удалён',
            icon: 'done'
          })
        })
        .catch((error) => {
          Notify.create({
            color: 'negative',
            position: 'top',
            message: error.message,
            icon: 'report_problem'
          })
          console.log(error)
        })
    }
  }
});
