import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import Account from 'src/model/dto/AccountDto';

export const useAccountStore = defineStore('account', {
  state: () => ({
    accounts: [] as Account[],
    showOnlyAction: true
  }),

  getters: {
    getAccounts(state) {
      return state.accounts;
    }
  },

  actions: {
    loadAccounts() {
      api.get('/api/service/account/getAccounts').then((response) => {
        this.accounts = response.data
      })
        .catch((error) => {
          Notify.create({
            color: 'negative',
            position: 'top',
            message: error.errors,
            icon: 'report_problem'
          })
          console.log(error)
        })
    },
    saveAccount(item: Account) {
      console.log(item)
    },
    removeAccount(item: Account) {
      console.log(item)
    }
  }
});
