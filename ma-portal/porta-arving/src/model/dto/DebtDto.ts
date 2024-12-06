import Ticket from 'src/model/dto/TicketDto';
import {date} from 'quasar';

export interface DebtOperation {
  id?: number
  firstOperation: boolean
  ticket: Ticket
  debtorFlag: boolean
  sumOperation: number
}

export interface Debt {
  id?: number
  isActive: boolean
  createDate: string
  closeDate?: string
  name: string
  description: string
  sumDebt: number
  sumDebtCurrent: number
  accountId?: number
  debtorFlag: boolean
  deleteFlag: boolean
  debtOperations: DebtOperation[]
}

export function createEmptyDebt(): Debt {
  const nowDate = date.formatDate(Date.now(), 'YYYY-MM-DD');
  return {
    isActive: true,
    createDate: nowDate,
    name: '',
    description: '',
    sumDebt: 0,
    sumDebtCurrent: 0,
    debtorFlag: false,
    deleteFlag: false,
    debtOperations: [] as DebtOperation[]
  } as Debt
}

export const isValidName = (d: Debt) => {
  if (d) {
    return d.name && d.name.length > 0
  }
  return false;
}
export const isValidDate = (d: Debt) => {
  if (d) {
    return d.createDate && d.createDate.length > 0;
  }
  return false;
}
export const isValidSum = (d: Debt) => {
  if (d) {
    return Number(d.sumDebt) > 0;
  }
  return false;
}
export const isValidDebt = (d: Debt) => {
  return isValidName(d) && isValidDate(d) && isValidSum(d)
}
