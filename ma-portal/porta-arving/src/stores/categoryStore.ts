import {defineStore} from 'pinia';
import {Category} from 'src/model/dto/TicketDto';
import {api} from 'boot/axios';
import {Notify} from 'quasar';

export const useCategoryStore = defineStore('category', {
  state: () => ({
    treeCategory: [] as Category[],
    categoriesMap: new Map<number, Category>(),
  }),

  getters: {
    getTreeCategory(state) {
      return state.treeCategory;
    },
    getCategoryMap(state) {
      return state.categoriesMap;
    }
  },

  actions: {
    loadTreeCategory(accountId: number) {
      api.get('/api/service/category/getTreeCategories',
        {params: {request: accountId}})
        .then((response) => {
          this.treeCategory = response.data
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
    loadCategories(accountId: number) {
      api.get('/api/service/category/getCategories',
        {params: {request: accountId}})
        .then((response) => {
          response.data.forEach((item: Category): void => {
            if (item.id)
              this.categoriesMap.set(item.id, item);
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
