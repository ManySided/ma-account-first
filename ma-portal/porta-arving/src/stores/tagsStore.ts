import {defineStore} from 'pinia';
import {OperationTag} from 'src/model/dto/TicketDto';
import {api} from 'boot/axios';
import {handleError} from 'src/common/ErrorHandler';

export const useTagStore = defineStore('tag', {
  state: () => ({
    tags: [] as OperationTag[],
    tempTags: [] as OperationTag[]
  }),

  getters: {
    getTags(state) {
      return state.tags;
    },
    getTempTags(state) {
      return state.tempTags;
    },
    getHasTempTag(state) {
      if (state.tempTags && state.tempTags.length > 0)
        return true;
      return false;
    }
  },

  actions: {
    actionAddTempTag(tempTag: OperationTag) {
      if (this.tempTags) {
        const findTempTag = this.tempTags.find(e => e.name === tempTag.name);
        if (!findTempTag)
          this.tempTags.push(tempTag)
      }
    },
    actionLoadTags(accountId: number) {
      api.get('/api/service/tag/all',
        {
          params: {
            request: accountId
          }
        })
        .then((response) => {
          this.tags = response.data
          // зачистка списка временных тегов
          response.data.forEach((item: OperationTag): void => {
            const findTempTag = this.tempTags.find(e => e.name === item.name);
            if (findTempTag) {
              const tagIndex = this.tempTags.indexOf(findTempTag, 0)
              this.tempTags.splice(tagIndex, 1)
            }
          })
        })
        .catch(handleError)
    },
  }
});
