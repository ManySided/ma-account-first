import {defineStore} from 'pinia';
import {api} from 'boot/axios';
import {handleError} from 'src/common/ErrorHandler';
import {createEmptyDebt, Debt} from 'src/model/dto/DebtDto';

export const useDebtStore = defineStore('debt', {
  state: () => ({
    debts: [] as Debt[],
    currentDebt: {} as Debt
  }),

  getters: {
    getDebts(state) {
      return state.debts;
    },
    getCurrentDebt(state) {
      return state.currentDebt;
    }
  },

  actions: {
    actionInitDebt(debtId: number) {
      if (debtId) {
        api.get('/api/service/debt',
          {
            params: {
              id: debtId
            }
          })
          .then((response) => {
            this.currentDebt = response.data
          })
          .catch(handleError)
      } else this.currentDebt = createEmptyDebt();
    },
    actionDebtList(accountId: number) {
      this.debts = []
      api.get('/api/service/debt/all',
        {
          params: {
            request: accountId
          }
        })
        .then((response) => {
          this.debts = response.data
        })
        .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionAddDebtOperation(debtId: number, date: string, sum: number, callback?: any) {
      api.post('/api/service/debt/operation',
        {
          debtId: debtId,
          date: date,
          sum: sum
        })
        .then(() => {
          if (callback) callback()
        })
        .catch(handleError)
    },
    actionUpdateDebt(debtId: number) {
      api.get('/api/service/debt',
        {
          params: {
            id: debtId
          }
        })
        .then((response) => {
          this.debts.forEach((item: Debt): void => {
            if (item.id === debtId) {
              item.sumDebt = response.data.sumDebt
              item.sumDebtCurrent = response.data.sumDebtCurrent
              item.isActive = response.data.isActive
              item.deleteFlag = response.data.deleteFlag
            }
          })
        })
        .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionCloseDebt(debtId: number | undefined, callback?: any) {
      if (debtId)
        api.post('/api/service/debt/close',
          {
            id: debtId
          })
          .then(() => {
            if (callback) callback()
          })
          .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionDeleteDebt(debtId: number | undefined, callback?: any) {
      const params = {id: debtId};
      if (debtId)
        api.delete('/api/service/debt', {params})
          .then(() => {
            if (callback) callback()
          })
          .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionSaveDebt(accountId: number, callback?: any) {
      if (this.currentDebt) {
        this.currentDebt.accountId = accountId
        api.post('/api/service/debt',
          this.currentDebt)
          .then(() => {
            if (callback) callback()
          })
          .catch(handleError)
      }
    }
  }
});
