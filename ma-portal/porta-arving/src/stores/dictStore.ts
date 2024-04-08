import {defineStore} from 'pinia';
import Currency from 'src/model/dto/DictionaryDto';
import {api} from 'boot/axios';
import {Notify} from 'quasar'


export const useDictionaryStore = defineStore('dictionary', {
  state: () => ({
    currency: [] as Currency[]
  }),

  getters: {
    getCurrency(state) {
      return state.currency;
    }
  },

  actions: {
    upLoadDictionary() {
      api.get('/api/service/dict/currency/list').then((response) => {
        this.currency = response.data
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
    }
  },


});
