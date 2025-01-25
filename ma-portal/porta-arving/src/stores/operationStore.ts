import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {Operation} from 'src/model/dto/TicketDto';
import {handleError} from 'src/common/ErrorHandler';

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
        .catch(handleError)
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
          operation.name = response.data.name
          operation.category = response.data.category
          operation.tags = response.data.tags
        })
        .catch(handleError)
    }
  }
});
