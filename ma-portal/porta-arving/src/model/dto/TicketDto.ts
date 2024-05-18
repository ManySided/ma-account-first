export default interface Ticket {
  id?: number
  ticketDirection: number
  date: string
  accountId: number
  discount?: number
  operations: Operation[]
  totalSum: number
}

export interface Category {
  id?: number
  name: string
}

export interface Operation {
  id?: number
  sum: number
  name: string
  comment?: string
  category: Category
  purchaseId: number
  isActive: boolean
}

export interface TicketsOfDay {
  sumOfDay: number
  dayDate: string
  tickets: Ticket[]
}

export interface TicketList {
  days: TicketsOfDay[]
}
