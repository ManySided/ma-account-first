import Currency from 'src/model/dto/DictionaryDto';

export default interface Account {
  id?: number;
  name: string;
  comment: string
  startSum: number
  currentSum: number
  currency: Currency
  created: null | string
  actual: boolean
}
