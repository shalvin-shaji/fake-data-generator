export const FIELD_TYPES = [
  'UUID', 'SEQUENCE', 'REFERENCE',
  'INTEGER', 'DECIMAL', 'BOOLEAN', 'STRING',
  'FIRST_NAME', 'LAST_NAME', 'FULL_NAME', 'EMAIL', 'PHONE', 'DATE_OF_BIRTH',
  'STREET', 'CITY', 'STATE', 'COUNTRY', 'POSTAL_CODE',
  'IBAN', 'ACCOUNT_NUMBER', 'CURRENCY', 'CUSIP', 'ISIN', 'TICKER',
  'DATE', 'TIMESTAMP',
  'ENUM', 'WEIGHTED_ENUM', 'REGEX',
] as const

export type FieldType = typeof FIELD_TYPES[number]

export interface FieldDefinition {
  name: string
  type: FieldType
  config: Record<string, unknown>
  nullProbability: number
}

export interface ModelDefinition {
  id?: string
  name: string
  kafkaTopic: string
  keyField?: string
  fields: FieldDefinition[]
}

export interface ProduceResult {
  modelId: string
  topic: string
  sent: number
}
