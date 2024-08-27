import {defineStore} from 'pinia';
import Ticket, {Category, Operation, TicketList} from 'src/model/dto/TicketDto';
import {api} from 'boot/axios';
import {Notify} from 'quasar';
import {handleError} from 'src/common/ErrorHandler';

export const useTicketStore = defineStore('ticket', {
  state: () => ({
    ticketsOfDay: {} as TicketList
  }),

  getters: {
    getTicketsOfDayList(state) {
      return state.ticketsOfDay;
    },
  },

  actions: {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionSaveTicket(ticket: Ticket, callback: any) {
      api.post('/api/service/ticket', ticket)
        .then(() => {
          callback()
          Notify.create({
            color: 'primary',
            position: 'top',
            message: 'Чек сохранён успешно',
            icon: 'check_circle_outline'
          })
        })
        .catch(handleError)
    },
    actionLoadTicketsByFilter(accountId: number,
                              from: string,
                              to: string,
                              name?: string,
                              categories?: Category[],
                              directions?: number[],
    ) {
      api.post('/api/service/ticket/filter',
        {
          accountId: accountId,
          from: from,
          to: to,
          name: name,
          categories: categories,
          directions: directions
        })
        .then((response) => {
          this.ticketsOfDay = response.data
        })
        .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionDeleteOperation(operationId: number, callback: any) {
      const params = {operationId: operationId};
      api.delete('/api/service/ticket/operation', {params})
        .then(() => {
          callback()
          Notify.create({
            color: 'positive',
            position: 'top',
            message: 'Операция удалёна',
            icon: 'done'
          })
        })
        .catch(handleError)
    },
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    actionUpdateOperation(operation: Operation, callback?: any) {
      api.put('/api/service/ticket/operation', operation)
        .then(() => {
          if (callback)
            callback()
          Notify.create({
            color: 'primary',
            position: 'top',
            message: 'Операция обновлена успешно',
            icon: 'check_circle_outline'
          })
        })
        .catch(handleError)
    }
  }
});
