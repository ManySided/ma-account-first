<template>
  <q-page>
    <div class="q-pa-md">
      <!-- Блок загрузки файла -->
      <q-card class="q-mb-md">
        <q-card-section>
          <div class="text-subtitle1 q-mb-sm">Загрузка файла импорта</div>

          <q-file
            v-model="fileModel"
            label="Выберите файл для импорта"
            filled
            accept=".csv"
            :loading="uploading"
            @update:model-value="uploadFile"
            clearable
          >
            <template v-slot:prepend>
              <q-icon name="attach_file"/>
            </template>
          </q-file>

          <div class="text-caption text-grey-6 q-mt-sm">
            Поддерживаемые форматы: CSV
          </div>
        </q-card-section>
      </q-card>

      <!-- Скрытый раздел с описанием формата файла -->
      <div v-show="showFormatExample">
        <q-card class="q-mb-md">
          <q-card-section>
            <div class="row items-center justify-between">
              <div class="text-subtitle1">Пример формата файла</div>
              <q-btn
                flat
                round
                dense
                icon="visibility_off"
                @click="toggleFormatExample"
              >
                <q-tooltip>Скрыть</q-tooltip>
              </q-btn>
            </div>
          </q-card-section>

          <q-card-section>
            <div class="text-body2 q-mb-sm">
              <strong>Файл должен содержать массив объектов со следующими полями:</strong>
            </div>

            <div class="bg-grey-1 q-pa-md" style="border-radius: 8px">
            <pre class="text-caption" style="margin: 0; white-space: pre-wrap;">
              1. Дата операции;
              2. Сумма Расхода;
              3. Сумма Дохода;
              4. Название операции;
              5. Комментарий операции;
              6. Название Категории;
              7. Название Тега;
            </pre>
            </div>

            <div class="text-body2 q-mt-md">
              <strong>Требования к полям:</strong>
              <ul class="q-mt-sm" style="margin-left: 20px">
                <li>Поле <code>Дата операции</code> - дата должна быть формата ДД.ММ.ГГГГ</li>
                <li>Сумма расхода/дохода заполеняется в зависимости от вида операции</li>
                <li>Поле <code>Название операции</code> - заполняется в свободной форме (обязательное)</li>
                <li>Поле <code>Комментарий операции</code> - заполняется в свободной форме (необязательное)</li>
                <li>Поле <code>Название Категории</code> - поле будет искаться в справочнике категорий, должно быть уже
                  создано (обязательное)
                </li>
                <li>Поле <code>Название Категории</code> - поле будет искаться в справочнике тегов (необязательное)</li>
                <li>Поддерживаемые форматы: CSV</li>
                <li>Разделитель должен быть символ "|"</li>
              </ul>
            </div>

            <!-- Дополнительный пример для CSV -->
            <div class="bg-grey-1 q-pa-md q-mt-md" style="border-radius: 8px">
              <div class="text-caption">
                <strong>Пример CSV:</strong><br>
                29.03.2026|34400||Оплата жилья|Оставшиеся сумма|Отпуск|нижний.новгород.отпуск.2026<br>
                30.03.2026||1200|Выйгрыш в конкурсе||Развелечения|нижний.новгород.отпуск.2026
              </div>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <!-- Кнопка показа формата -->
      <div v-if="!showFormatExample" class="q-mb-md">
        <q-btn
          outline
          color="primary"
          label="Показать пример формата файла"
          icon="visibility"
          @click="toggleFormatExample"
        />
      </div>

      <!-- Таблица с данными из файла -->
      <div v-if="getLengthRecords(storeImport.getModel) > 0">
        <q-card class="q-mb-md">
          <q-card-section>
            <div class="text-subtitle1">Предварительный просмотр данных</div>
            <div class="text-caption text-grey-6">
              Найдено записей: {{ getLengthRecords(storeImport.getModel) }}
            </div>
          </q-card-section>

          <q-card-section>
            <q-table
              :rows="storeImport.getModel.records"
              :columns="tableColumns"
              row-key="id"
              :pagination="initialPagination"
              :loading="tableLoading"
              dense
              bordered
            >
              <template v-slot:body-cell-date="props">
                <q-td :props="props">
                  {{ formatDate(props.value) }}
                </q-td>
              </template>

              <template v-slot:body-cell-sum="props">
                <q-td :props="props">
                <span :class="getSumClass(props.row)">
                  {{ formatSum(props.value) }}
                  <q-icon
                    v-if="props.row.ticketDirection === 'EXPENDITURE'"
                    name="trending_down"
                    size="sm"
                    class="q-ml-xs"
                  />
                  <q-icon
                    v-else-if="props.row.ticketDirection === 'INCOME'"
                    name="trending_up"
                    size="sm"
                    class="q-ml-xs"
                  />
                </span>
                </q-td>
              </template>

              <template v-slot:body-cell-category="props">
                <q-td :props="props">
                  <div v-if="props.value">
                  <span :class="getStructureRecordClass(props.value)">
                    {{ props.value.name }}
                  </span>
                    <q-tooltip v-if="props.value.id">
                      ID: {{ props.value.id }}
                    </q-tooltip>
                  </div>
                  <span v-else class="text-grey-5">—</span>
                </q-td>
              </template>

              <template v-slot:body-cell-tags="props">
                <q-td :props="props">
                  <div v-if="props.value">
                  <span :class="getStructureRecordClass(props.value)">
                    {{ props.value.name }}
                  </span>
                    <q-tooltip v-if="props.value.id">
                      ID: {{ props.value.id }}
                    </q-tooltip>
                  </div>
                  <span v-else class="text-grey-5">—</span>
                </q-td>
              </template>

              <template v-slot:body-cell-comment="props">
                <q-td :props="props">
                  <div v-if="props.value" class="ellipsis" style="max-width: 200px">
                    {{ props.value }}
                    <q-tooltip v-if="props.value.length > 50">
                      {{ props.value }}
                    </q-tooltip>
                  </div>
                  <span v-else class="text-grey-5">—</span>
                </q-td>
              </template>

              <template v-slot:loading>
                <q-inner-loading showing>
                  <q-spinner-dots size="50px" color="primary"/>
                </q-inner-loading>
              </template>
            </q-table>
          </q-card-section>
        </q-card>

        <!-- Кнопка импорта -->
        <div class="row justify-end">
          <q-btn
            color="positive"
            label="Начать импорт"
            icon="cloud_upload"
            size="lg"
            :loading="importing"
            @click="importData"
          />
        </div>
      </div>

    </div>
  </q-page>
</template>

<script lang="ts">
import {computed, defineComponent, ref} from 'vue';
import {useStuffStore} from 'stores/stuffStore';
import {useImportStore} from 'stores/importStore';
import UploadModel, {StructureRecord, UploadRecord} from 'src/model/dto/UploadCsv';
import {QTableColumn} from 'quasar';

export default defineComponent({
  name: 'ImportCsvPage',
  setup() {
    const storeStuff = useStuffStore();
    const storeImport = useImportStore();

    storeStuff.actionUpdateTitlePage('Импорт данных');

    const showFormatExample = ref(false)
    const uploading = ref(false)
    const importing = ref(false)
    const tableLoading = ref(false)
    const tableColumns = computed<QTableColumn<UploadRecord>[]>(() => [
      {
        name: 'date',
        label: 'Дата',
        field: 'date',
        align: 'left',
        sortable: true,
        style: 'width: 120px'
      },
      {
        name: 'sum',
        label: 'Сумма',
        field: 'sum',
        align: 'right',
        sortable: true,
        style: 'width: 120px'
      },
      {
        name: 'name',
        label: 'Название',
        field: 'name',
        align: 'left',
        sortable: true,
        style: 'min-width: 200px'
      },
      {
        name: 'comment',
        label: 'Комментарий',
        field: 'comment',
        align: 'left',
        sortable: false,
        style: 'min-width: 200px'
      },
      {
        name: 'category',
        label: 'Категория',
        field: (row: UploadRecord) => row.category,
        align: 'left',
        sortable: true,
        style: 'width: 150px'
      },
      {
        name: 'tags',
        label: 'Теги',
        field: (row: UploadRecord) => row.tagRecord,
        align: 'left',
        sortable: true,
        style: 'width: 150px'
      }
    ])
    // Параметры пагинации таблицы
    const initialPagination = {
      sortBy: 'id',
      descending: false,
      page: 1,
      rowsPerPage: 10
    }

    const fileModel = ref(null);

    // Загрузка файла на сервер
    const uploadFile = async () => {
      if (fileModel.value) {
        storeImport.prepareCsv(storeStuff.getAccountId, fileModel.value)
        return
      }
    };
    // Переключение показа примера формата
    const toggleFormatExample = () => {
      showFormatExample.value = !showFormatExample.value
    };
    // Импорт данных
    const importData = async () => {
      storeImport.importCsv()
    };
    // Форматирование даты
    const formatDate = (date: string) => {
      if (!date) return '—'
      try {
        return new Date(date).toLocaleDateString('ru-RU', {
          day: '2-digit',
          month: '2-digit',
          year: 'numeric'
        })
      } catch {
        return date
      }
    }
    // Форматирование суммы
    const formatSum = (sum: number) => {
      if (sum)
        return sum.toLocaleString();
      return '0';
    }
    // Определение класса для StructureRecord
    const getStructureRecordClass = (record: StructureRecord) => {
      if (record.id && record.id > 0) {
        return 'text-positive text-weight-medium'
      }
      return 'text-negative text-weight-medium'
    }
    const getLengthRecords = (response: UploadModel) => {
      if (response) {
        if (response.records)
          return response.records.length
      }
      return 0;
    }
    // Получение класса для суммы в зависимости от направления
    const getSumClass = (record: UploadRecord) => {
      if (record.ticketDirection === 'EXPENDITURE') {
        return 'text-expense' // Расход - мягкий синий
      } else if (record.ticketDirection === 'INCOME') {
        return 'text-income' // Доход - мягкий зеленый
      }
      return '' // По умолчанию без цвета
    }

    return {
      storeImport,
      fileModel,
      uploading,
      showFormatExample,
      importing,
      tableLoading,
      tableColumns,
      initialPagination,
      uploadFile,
      toggleFormatExample,
      importData,
      formatDate,
      formatSum,
      getStructureRecordClass,
      getLengthRecords,
      getSumClass
    }
  }
})
</script>

<style scoped>
/* Цвета для суммы */
.text-expense {
  color: #2196f3; /* Мягкий синий для расходов */
  font-weight: 500;
}

.text-income {
  color: #4caf50; /* Приятный зеленый для доходов */
  font-weight: 500;
}

/* Цвета для структуры */
.text-positive-weight {
  color: #4caf50;
  font-weight: 500;
}

.text-negative-weight {
  color: #f44336;
  font-weight: 500;
}

.text-grey-5 {
  color: #9e9e9e;
}

.bg-yellow-1 {
  background-color: #fff9c4;
}

pre {
  overflow-x: auto;
  background: #f5f5f5;
  padding: 12px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 12px;
  margin: 0;
}

ul {
  margin-left: 0;
  padding-left: 20px;
}

.ellipsis {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
