<template>
  <q-page>
    <div class="q-pa-md example-row-equal-width">
      <div class="row">

      </div>
      <div class="row">
        <div class="col-md-3">
          <!-- Фильтр по операциям -->
          <div class="row-md-3">
            <q-card class="my-card" flat bordered>
              <q-card-section style="background-color: #f2f2f2">
                <div class="text-h6">фильтр</div>
              </q-card-section>
              <q-separator/>

              <q-list>
                <q-item>
                  <q-item-section>
                    <custom-field-date v-model:dateVariable="filterStartDate"
                                       field-name="дата начала"
                                       can-change/>
                  </q-item-section>
                </q-item>
                <q-item>
                  <q-item-section>
                    <custom-field-date v-model:dateVariable="filterEndDate"
                                       field-name="дата окончания"
                                       can-change/>
                  </q-item-section>
                </q-item>
                <q-item>
                  <q-item-section>
                    <q-input outlined v-model="filterName" type="text" label="описание"/>
                  </q-item-section>
                </q-item>
              </q-list>
              <q-separator/>

              <q-card-actions vertical>
                <q-btn flat @click="findTicketsMethod">найти</q-btn>
              </q-card-actions>
            </q-card>
          </div>
        </div>

        <!-- Список операций по дням -->
        <div class="col-9" style="padding-left: 5px">
          <q-card class="my-card" flat bordered>
            <q-card-section style="background-color: #f2f2f2">
              <div class="row items-center no-wrap">
                <div class="col">
                  <div class="text-h6">Операции</div>
                </div>

                <div class="col-auto">
                  <q-btn color="primary"
                         icon="add"
                         size="10px"
                         @click="$router.push({name:'ticketEdit'})">
                    Добавить новый чек
                  </q-btn>
                </div>
              </div>
            </q-card-section>
            <q-separator/>
            <q-card-section>
              <q-scroll-area :style="{ height: windowHeight-200+'px', width: 'auto' }">
                <div v-if="isEmptyList(storeTicket.getTicketsOfDayList)">Операции не найдены</div>
                <q-timeline color="secondary" v-if="!isEmptyList(storeTicket.getTicketsOfDayList)">
                  <q-timeline-entry v-for="(itemDay, indexDay) in storeTicket.getTicketsOfDayList.days" :key="indexDay"
                                    :subtitle="formattedDate(itemDay.dayDate)"
                                    :title="formattedNumber(itemDay.sumOfDay)">
                    <div v-for="(itemTicket, indexTicket) in itemDay.tickets"
                         :key="indexTicket" style="padding-bottom: 20px">
                      <q-card class="my-card"
                              flat bordered
                              :style="{ background: itemTicket.ticketDirection==='EXPENDITURE'?'#fbfcfe':'#eef7f0'}">
                        <!-- Заголовок чека -->
                        <div style="padding-left: 20px; padding-top: 5px">
                          <div class="text q-mb-sm" v-if="itemTicket.ticketDirection==='EXPENDITURE'">
                            <q-icon name="arrow_circle_down" color="blue" size="20px"/>
                            Расход
                          </div>
                          <div class="text q-mb-sm" v-if="itemTicket.ticketDirection==='INCOME'">
                            <q-icon name="arrow_circle_up" color="green" size="20px"/>
                            Доход
                          </div>
                        </div>
                        <q-separator/>
                        <!-- Тело чека -->

                        <!-- Список операций -->
                        <q-list>
                          <!-- Отдельная операция -->
                          <q-item clickable
                                  v-for="(itemOperation, indexOperation) in itemTicket.operations"
                                  :key="indexOperation">
                            <q-menu auto-close>
                              <q-list style="min-width: 100px">
                                <q-item clickable @click="viewEditOperationMethod(itemOperation)">
                                  <q-item-section avatar>
                                    <q-icon name="edit"/>
                                  </q-item-section>
                                  <q-item-section>
                                    Редактировать
                                  </q-item-section>
                                </q-item>
                                <q-item clickable @click="viewDeleteOperationMethod(itemOperation)">
                                  <q-item-section avatar>
                                    <q-icon name="delete_outline"/>
                                  </q-item-section>
                                  <q-item-section>Удалить</q-item-section>
                                </q-item>
                              </q-list>
                            </q-menu>
                            <q-item-section>
                              <q-item-label>{{ itemOperation.name }}</q-item-label>
                              <q-item-label caption v-if="itemOperation.comment">
                                {{ itemOperation.comment }}
                              </q-item-label>
                              <q-item-label caption>
                                <q-chip
                                  size="sm"
                                  color="primary"
                                  text-color="white"
                                  v-for="(itemTag, indexTag) in itemOperation.tags" :key="indexTag">
                                  {{ itemTag.name }}
                                </q-chip>
                              </q-item-label>
                            </q-item-section>
                            <q-item-section>
                              <div class="column items-center self-start">
                                <q-badge outline color="primary" style="size: 5px">
                                  {{ getCategoryName(itemOperation) }}
                                </q-badge>
                              </div>
                            </q-item-section>
                            <q-item-section>
                              <!-- Описание товара (количество * цена за единицу) -->
                              <div style="text-align: right"></div>
                            </q-item-section>
                            <q-item-section>
                              <div style="text-align: right">{{ formattedNumber(itemOperation.sum) }}</div>
                            </q-item-section>
                          </q-item>
                        </q-list>
                        <q-separator/>
                        <!-- Итог по чеку -->
                        <q-list>
                          <q-item>
                            <q-item-section>
                              <div style="text-align: right"><b>{{ formattedNumber(itemTicket.totalSum) }}</b></div>
                            </q-item-section>
                          </q-item>
                        </q-list>

                      </q-card>
                    </div>
                  </q-timeline-entry>
                </q-timeline>
              </q-scroll-area>
            </q-card-section>
            <q-separator/>
          </q-card>
        </div>
      </div>
    </div>

    <q-dialog v-model="deleteOperationView" persistent backdrop-filter="blur(4px)">
      <q-card>
        <q-bar>
          <div>Удаление операции</div>
          <q-space/>
          <q-btn dense flat icon="close" v-close-popup>
            <q-tooltip class="bg-white text-primary">Close</q-tooltip>
          </q-btn>
        </q-bar>
        <q-card-section>
          Удалить операция "{{ currentOperation.name }}" на сумму {{ formattedNumber(currentOperation.sum) }}?
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup/>
          <q-btn flat label="Удалить" v-close-popup @click="deleteOperationMethod"/>
        </q-card-actions>
      </q-card>
    </q-dialog>

    <q-dialog
      v-model="editOperationView"
      persistent
      backdrop-filter="blur(4px)"
      full-width>
      <q-card>
        <q-bar>
          <div>Редактирование операции</div>
          <q-space/>
          <q-btn dense flat icon="close" v-close-popup>
            <q-tooltip class="bg-white text-primary">Close</q-tooltip>
          </q-btn>
        </q-bar>
        <q-card-section>
          <operation-edit-row
            v-model:operationVariable="currentOperation"
            :account-id="storeStuff.getAccountId"
            @change-sum="refreshTotalSumMethod"
          />
        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="Отмена" v-close-popup/>
          <q-btn flat label="Сохранить" v-close-popup @click="saveOperationMethod"/>
        </q-card-actions>
      </q-card>
    </q-dialog>

  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useAccountStore} from 'stores/accountStore';
import {useTicketStore} from 'stores/ticketStore';
import {useStuffStore} from 'stores/stuffStore';
import {useTagStore} from 'stores/tagsStore';
import CustomFieldDate from 'components/utils/CustomFieldDate.vue';
import OperationEditRow from 'components/utils/OperationEditRow.vue';
import {isValidOperation, Operation, TicketList} from 'src/model/dto/TicketDto';
import {date} from 'quasar';

export default defineComponent({
  name: 'TicketListPage',
  components: {CustomFieldDate, OperationEditRow},

  setup() {
    // init
    const windowWidth = ref(window.innerWidth)
    const windowHeight = ref(window.innerHeight)

    const storeStuff = useStuffStore();
    const storeAccount = useAccountStore();
    const storeTicket = useTicketStore();
    const storeTags = useTagStore();

    const startDate = (date.formatDate(storeStuff.getTicketsFilterPeriodFrom, 'YYYY-MM-DD'));
    const endDate = (date.formatDate(storeStuff.getTicketsFilterPeriodTo, 'YYYY-MM-DD'));

    // load data
    storeStuff.actionUpdateTitlePage('Просмотр чеков');
    storeStuff.actionUpdateAccountData();
    storeAccount.loadAccountById(storeStuff.getAccountId)
    storeTicket.actionLoadTicketsByFilter(storeStuff.getAccountId, startDate, endDate)

    const filterName = ref('');
    const filterStartDate = ref(startDate);
    const filterEndDate = ref(endDate);

    const currentOperation = ref({
      sum: 0,
      name: '',
      comment: '',
      category: {}
    } as Operation);
    const deleteOperationView = ref(false);
    const editOperationView = ref(false);

    const findTicketsMethod = () => {
      storeStuff.actionUpdateTicketsFilterPeriodFrom(
        date.extractDate(filterStartDate.value, 'YYYY-MM-DD'));
      storeStuff.actionUpdateTicketsFilterPeriodTo(
        date.extractDate(filterEndDate.value, 'YYYY-MM-DD'));
      storeTicket.actionLoadTicketsByFilter(storeStuff.getAccountId,
        filterStartDate.value,
        filterEndDate.value,
        filterName.value)
    }
    const viewDeleteOperationMethod = (operation: Operation) => {
      currentOperation.value = operation;
      deleteOperationView.value = true;
    }
    const deleteOperationMethod = () => {
      if (currentOperation.value.id)
        storeTicket.actionDeleteOperation(
          currentOperation.value.id,
          () => {
            storeStuff.actionUpdateAccountData();
            findTicketsMethod();
          });
    }

    // eslint-disable-next-line @typescript-eslint/no-empty-function
    const refreshTotalSumMethod = () => {
    }
    const viewEditOperationMethod = (operation: Operation) => {
      storeTags.actionLoadTags(storeStuff.getAccountId)
      currentOperation.value = operation;
      editOperationView.value = true;
    }
    const saveOperationMethod = () => {
      if (currentOperation.value && isValidOperation(currentOperation.value)) {
        const callback = () => {
          findTicketsMethod()
        }
        storeTicket.actionUpdateOperation(currentOperation.value, callback);
      }
    }

    return {
      // init
      storeAccount,
      storeTicket,
      storeStuff,
      // variable
      filterName,
      filterStartDate,
      filterEndDate,
      windowWidth,
      windowHeight,
      currentOperation,
      deleteOperationView,
      editOperationView,
      //methods
      findTicketsMethod,
      viewDeleteOperationMethod,
      deleteOperationMethod,
      viewEditOperationMethod,
      refreshTotalSumMethod,
      saveOperationMethod
    }
  },
  methods: {
    formattedNumber(n: number) {
      if (n)
        return n.toLocaleString();
      return '';
    },
    formattedDate(d: string) {
      if (d) {
        const cloneDate = date.extractDate(d, 'YYYY-MM-DD')
        return date.formatDate(cloneDate, 'DD.MM.YYYY');
      }
      return '';
    },
    isEmptyList(ticketList: TicketList) {
      if (ticketList)
        return ticketList.days === undefined || ticketList.days.length == 0;
      return true
    },
    getCategoryName(operation: Operation) {
      if (operation && operation.category)
        return operation.category.name
      return '';
    }
  }
})
</script>

<style scoped>

</style>
