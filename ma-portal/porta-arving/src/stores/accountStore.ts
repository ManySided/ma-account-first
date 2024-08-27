import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import Account from 'src/model/dto/AccountDto';
import {handleError} from 'src/common/ErrorHandler';

export const useAccountStore = defineStore('account', {
  state: () => ({
    accounts: [] as Account[],
    currentAccount: {} as Account
  }),

  getters: {
    getAccounts(state) {
      return state.accounts;
    },
    getActualAccounts(state) {
      return state.accounts
        .filter((value) => value.actual)
        .sort((n1, n2) => {
          if (n1.id && n2.id) {
            if (n1.id > n2.id)
              return 1;
            if (n1.id < n2.id)
              return -1;
          }
          return 0
        });
    },
    getCurrentAccount(state): Account {
      return state.currentAccount;
    },
    getCurrentAccountName(state) {
      return state.currentAccount.name;
    },
    getCurrentAccountSum(state) {
      return state.currentAccount.currentSum;
    }
  },

  actions: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    loadAccountById(id: number, callback?: any) {
      api.get('/api/service/account/getAccountById', {params: {request: id}}).then((response) => {
        this.currentAccount = response.data
        if (callback)
          callback(response.data)
      })
        .catch(handleError)
    },
    loadAccounts() {
      api.get('/api/service/account/getAccounts').then((response) => {
        this.accounts = response.data
      })
        .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    saveAccount(item: Account, callback?: any) {
      if (item.id) {
        api.put('/api/service/account', item)
          .then(() => {
            if (callback)
              callback()

            Notify.create({
              color: 'positive',
              position: 'top',
              message: 'Счёт обновлён',
              icon: 'done'
            })
          })
          .catch(handleError)

      } else {
        api.post('/api/service/account', item)
          .then(() => {
            if (callback)
              callback()

            Notify.create({
              color: 'positive',
              position: 'top',
              message: 'Счёт создан',
              icon: 'done'
            })
          })
          .catch(handleError)
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
        .catch(handleError)
    }
  }
});
