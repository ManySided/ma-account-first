import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import {handleError} from 'src/common/ErrorHandler';

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
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
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
        .catch(handleError)
    }
  }
});
