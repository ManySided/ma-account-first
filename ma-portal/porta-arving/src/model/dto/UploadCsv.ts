export default interface UploadModel {
  statusLoad: boolean
  errorText: string
  accountId: number
  records: UploadRecord[]
}

export interface UploadRecord {
  date: string
  sum: number
  ticketDirection: string
  name: string
  comment?: string
  category?: StructureRecord
  tagRecord?: StructureRecord
}

export interface StructureRecord {
  id: number
  name: string
}
