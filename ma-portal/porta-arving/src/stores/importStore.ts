import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import {handleError} from 'src/common/ErrorHandler';
import UploadModel from 'src/model/dto/UploadCsv';

export const useImportStore = defineStore('importData', {
  state: () => ({
    success: true,
    prepareModel: {} as UploadModel
  }),

  getters: {
    getSuccess(state) {
      return state.success;
    },
    getModel(state) {
      return state.prepareModel
    }
  },

  actions: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    importCsv() {
      api.post('/api/service/upload/importDataModel',
        this.prepareModel)
        .then((response) => {
          console.log(response)
          Notify.create({
            color: 'positive',
            position: 'top',
            message: 'Импорт данных заверщён',
            icon: 'done'
          })
        })
        .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    prepareCsv(accountId: number, file: any) {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('accountId', accountId.toString());

      api.post('/api/service/upload/prepareDataModel',
        formData)
        .then((response) => {
          this.prepareModel = response.data

          if (this.prepareModel.statusLoad) {
            Notify.create({
              color: 'positive',
              position: 'top',
              message: 'Файл успешно преобразован',
              icon: 'done'
            })
          } else {
            Notify.create({
              color: 'negative',
              position: 'top',
              message: this.prepareModel.errorText,
              icon: 'report_problem'
            })
          }
        })
        .catch(handleError)
    }
  }
});
