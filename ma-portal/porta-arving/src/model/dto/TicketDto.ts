export default interface Ticket {
  id?: number
  ticketDirection: string
  date: string
  accountId: number
  discount?: number
  operations: Operation[]
  totalSum: number
}

export interface Category {
  id?: number
  name: string
  subCategories?: Category[]
  parent?: number
  accountId?: number
}

export interface Operation {
  id?: number
  sum: number
  name: string
  comment?: string
  category?: Category
  purchaseId?: number
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

export const ticketDirection = [
  {value: 'EXPENDITURE', label: 'Расход'},
  {value: 'INCOME', label: 'Доход'}
]

// common
export interface CategoryDeleteRequest {
  categoryId: number
  reserveCategoryId?: number
}

export const isValidOperation = (o: Operation) => {
  if (o) {
    const isValidName = o.name && o.name.length > 0
    const isValidCategory = o.category && o.category.id && o.category.id > 0
    const isValidSum = o.sum && Number(o.sum) > 0
    return isValidName && isValidCategory && isValidSum
  }
  return false;
}

export const isValidTicket = (t: Ticket) => {
  if (t) {
    const isValidTicketDirection = t.ticketDirection && t.ticketDirection.length > 0;
    const isValidDate = t.date && t.date.length > 0;

    const isValidOperations = t.operations.every(v => isValidOperation(v) === true);

    return isValidTicketDirection && isValidDate && isValidOperations;
  }
  return false;
}

export function getClearCategory(): Category {
  return {
    name: '',
    parent: undefined
  } as Category
}
