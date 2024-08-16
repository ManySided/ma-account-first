import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import {Operation} from 'src/model/dto/TicketDto';

export const useOperationStore = defineStore('operation', {
  state: () => ({
    likedGroups: Array<string>(),
    lastOperation: {} as Operation
  }),

  getters: {
    getLikedGroups(state) {
      return state.likedGroups;
    },
    getLastOperation(state) {
      return state.lastOperation;
    }
  },

  actions: {
    actionFindLikedGroups(searchText: string, accountId: number) {
      api.get('/api/service/operation/findGroupOperationsByName',
        {
          params: {
            accountId: accountId,
            name: searchText
          }
        })
        .then((response) => {
          this.likedGroups = response.data
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
    actionClearLikedGroups() {
      this.likedGroups = new Array<string>();
    },
    actionFindLastOperationByNameAndFill(operationName: string, accountId: number, operation: Operation) {
      api.get('/api/service/operation/findLastOperationByName',
        {
          params: {
            accountId: accountId,
            name: operationName
          }
        })
        .then((response) => {
          console.log(response.data)
          operation.name = response.data.name
          operation.category = response.data.category
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
