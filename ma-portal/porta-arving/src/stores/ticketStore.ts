import {defineStore} from 'pinia';
import Ticket, {Category, TicketList} from 'src/model/dto/TicketDto';
import {api} from 'boot/axios';
import {Notify} from 'quasar';

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
    actionSaveTicket(ticket: Ticket, callback: any) {
      api.post('/api/service/ticket', ticket)
        .then(() => {
          Notify.create({
            color: 'primary',
            position: 'top',
            message: 'Чек сохранён успешно',
            icon: 'check_circle_outline'
          })
          if (callback)
            callback()
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
    actionLoadTicketsByFilter(accountId: number,
                              from: string,
                              to: string,
                              name?: string,
                              categories?: Category[],
                              directions?: number[]
    ) {
      api.post('/api/service/ticket/filter',
        {
          accountId: accountId,
          from: from,
          to: to,
          name: name,
          categories: categories,
          directions: directions
        }).then((response) => {
        this.ticketsOfDay = response.data
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
