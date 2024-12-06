import {defineStore} from 'pinia';
import {Category, CategoryDeleteRequest} from 'src/model/dto/TicketDto';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import {handleError} from 'src/common/ErrorHandler';

export const useCategoryStore = defineStore('category', {
  state: () => ({
    treeCategory: [] as Category[],
    treeCategoryRelevant: [] as Category[],
    categoriesMap: new Map<number, Category>(),
    arrayCategory: [] as Category[],
    currentCategory: {} as Category
  }),

  getters: {
    getTreeCategory(state) {
      return state.treeCategory;
    },
    getTreeCategoryRelevant(state) {
      return state.treeCategoryRelevant;
    },
    getCategoryMap(state) {
      return state.categoriesMap;
    },
    getArrayCategoryWithoutCurrent(state) {
      return state.arrayCategory.filter((item) => {
        if (this.currentCategory)
          return item.id != state.currentCategory.id
        return true
      });
    }
  },

  actions: {
    loadTreeCategory(accountId: number) {
      api.get('/api/service/category/getTreeCategories',
        {params: {accountId: accountId}})
        .then((response) => {
          this.treeCategory = response.data
        })
        .catch(handleError)
    },
    actionLoadTreeCategoryRelevant(accountId: number, relevant: boolean) {
      api.get('/api/service/category/getTreeCategories',
        {
          params: {
            accountId: accountId,
            showRelevantCategory: relevant
          }
        })
        .then((response) => {
          this.treeCategoryRelevant = response.data
        })
        .catch(handleError)
    },
    loadCategories(accountId: number) {
      api.get('/api/service/category/getCategories',
        {params: {request: accountId}})
        .then((response) => {
          this.arrayCategory = response.data;
          response.data.forEach((item: Category): void => {
            if (item.id)
              this.categoriesMap.set(item.id, item);
          })
        })
        .catch(handleError)
    },
    actionSetCurrentCategory(category: Category) {
      this.currentCategory = category
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionSaveCategory(category: Category, callback?: any) {
      api.post('/api/service/category', category)
        .then(() => {
          if (callback) callback()
          Notify.create({
            color: 'primary',
            position: 'top',
            message: 'Категория сохранена',
            icon: 'check_circle_outline'
          })
        })
        .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionDeleteCategory(request: CategoryDeleteRequest, callback: any) {
      const params = {
        categoryId: request.categoryId,
        reserveCategoryId: request.reserveCategoryId
      };
      api.delete('/api/service/category', {params})
        .then(() => {
          if (callback) callback()
          Notify.create({
            color: 'primary',
            position: 'top',
            message: 'Категория удалена',
            icon: 'check_circle_outline'
          })
        })
        .catch(handleError)
    }
  }
});
