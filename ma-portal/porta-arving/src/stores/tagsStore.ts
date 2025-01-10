import {defineStore} from 'pinia';
import {OperationTag} from 'src/model/dto/TicketDto';
import {api} from 'boot/axios';
import {handleError} from 'src/common/ErrorHandler';

export const useTagStore = defineStore('tag', {
  state: () => ({
    tags: [] as OperationTag[]
  }),

  getters: {
    getTags(state) {
      return state.tags;
    }
  },

  actions: {
    actionLoadTags(accountId: number) {
      api.get('/api/service/tag/all',
        {
          params: {
            request: accountId
          }
        })
        .then((response) => {
          this.tags = response.data
        })
        .catch(handleError)
    },
  }
});
