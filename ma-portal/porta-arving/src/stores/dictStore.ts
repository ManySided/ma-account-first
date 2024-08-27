import {defineStore} from 'pinia';
import Currency from 'src/model/dto/DictionaryDto';
import {api} from 'boot/axios';
import {handleError} from 'src/common/ErrorHandler';


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
        .catch(handleError)
    }
  },


});
