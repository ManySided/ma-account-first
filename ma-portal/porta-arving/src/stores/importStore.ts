import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Notify} from 'quasar';

export const useImportStore = defineStore('importData', {
  state: () => ({
    success: true
  }),

  getters: {
    getSuccess(state) {
      return state.success;
    }
  },

  actions: {
    importCsv(accountId: number, file: any) {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('accountId', accountId.toString());

      api.post('/api/service/upload/importDataFile',
        formData)
        .then(() => {
          Notify.create({
            color: 'positive',
            position: 'top',
            message: 'Импорт данных заверщён',
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
        })
    }
  }
});
