import Currency from 'src/model/dto/DictionaryDto';
import {date} from 'quasar';

export default interface Account {
  id?: number
  name: string
  comment: string
  startSum: number
  currentSum: number
  currency: Currency
  created: null | string
  actual: boolean
}

export function createEmptyAccount(): Account {
  const nowDate = date.formatDate(Date.now(), 'YYYY-MM-DDTHH:mm:ss');
  return {
    name: '',
    comment: '',
    startSum: 0,
    currentSum: 0,
    created: nowDate,
    actual: true
  } as Account
}

export function isValidAccount(account: Account) {
  if (account) {
    const isValidName = account.name && account.name.length > 0
    const isValidStartSum = Number(account.startSum) >= 0
    let isValidCurrency = false
    if (account.currency)
      isValidCurrency = account.currency.id > 0
    return isValidName && isValidStartSum && isValidCurrency
  }
  return false;
}
