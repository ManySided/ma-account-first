<template>
  <q-page>
    <div class="q-pa-md example-row-equal-width">
      <div class="row">
        <div class="col-md-3">
          <!-- Блок информации об счёте -->
          <div class="row-md-3" style="padding-bottom: 5px">
            <q-card class="my-card" flat bordered>
              <q-card-section style="background-color: #f2f2f2">
                <div class="text-h6">информация</div>
              </q-card-section>
              <q-separator/>

              <q-list>
                <q-item>
                  <q-item-section>
                    <q-item
                      clickable
                      v-ripple
                      style="border: 1px solid #c2c2c2"
                      class="rounded-borders bg-grey-2"
                    >
                      <q-item-section>
                        <q-item-label style="color: #a4a4a4; font-size: 12px">
                          счёт
                        </q-item-label>
                        {{ storeAccount.getCurrentAccountName }}
                      </q-item-section>
                    </q-item>
                  </q-item-section>
                </q-item>
                <q-item>
                  <q-item-section>
                    <q-item
                      clickable
                      v-ripple
                      style="border: 1px solid #c2c2c2"
                      class="rounded-borders bg-grey-2"
                    >
                      <q-item-section>
                        <q-item-label style="color: #a4a4a4; font-size: 12px">
                          сумма
                        </q-item-label>
                        {{ formattedNumber(storeAccount.getCurrentAccountSum) }}
                      </q-item-section>
                    </q-item>
                  </q-item-section>
                </q-item>
              </q-list>
            </q-card>
          </div>

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
                  <q-btn round color="primary" icon="add" size="10px">
                    <q-tooltip>
                      Добавить новый чек
                    </q-tooltip>
                  </q-btn>
                </div>
              </div>
            </q-card-section>
            <q-separator/>
            <q-card-section>
              <div v-if="isEmptyList(storeTicket.getTicketList.days)">Операции не найдены</div>
              <q-timeline color="secondary" v-if="!isEmptyList(storeTicket.getTicketList.days)">
                <q-timeline-entry v-for="(itemDay, indexDay) in storeTicket.getTicketList.days" :key="indexDay"
                                  :title="formattedDate(itemDay.dayDate)"
                                  :subtitle="formattedNumber(itemDay.sumOfDay)">
                  <div v-for="(itemTicket, indexTicket) in itemDay.tickets"
                       :key="indexTicket" style="padding-bottom: 20px">
                    <q-card class="my-card" flat bordered>
                      <!-- Заголовок чека -->
                      <q-card-section>
                        <div class="text-h6 q-mb-xs" v-if="itemTicket.ticketDirection==='EXPENDITURE'">
                          <q-icon name="arrow_circle_down" color="blue" size="30px"/>
                          Расход
                        </div>
                        <div class="text-h6 q-mb-xs" v-if="itemTicket.ticketDirection==='INCOME'">
                          <q-icon name="arrow_circle_up" color="green" size="30px"/>
                          Доход
                        </div>
                      </q-card-section>
                      <q-separator/>
                      <!-- Тело чека -->
                      <q-card-section>
                        <!-- Список операций -->
                        <q-list>
                          <!-- Отдельная операция -->
                          <q-item clickable
                                  v-for="(itemOperation, indexOperation) in itemTicket.operations"
                                  :key="indexOperation">
                            <q-item-section>
                              <q-item-label>{{ itemOperation.name }}</q-item-label>
                              <q-item-label caption v-if="itemOperation.comment">
                                {{ itemOperation.comment }}
                              </q-item-label>
                            </q-item-section>
                            <q-item-section>
                              <div class="col-1">
                                <q-badge outline color="primary" style="size: 5px">
                                  {{ itemOperation.category.name }}
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
                              <div style="text-align: right">{{ formattedNumber(itemTicket.totalSum) }}</div>
                            </q-item-section>
                          </q-item>
                        </q-list>
                      </q-card-section>
                    </q-card>
                  </div>
                </q-timeline-entry>
              </q-timeline>
            </q-card-section>
            <q-separator/>
          </q-card>
        </div>
      </div>
    </div>

  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import {useAccountStore} from 'stores/accountStore';
import {date} from 'quasar';
import CustomFieldDate from 'components/utils/CustomFieldDate.vue';
import {useTicketStore} from 'stores/ticketStore';
import {TicketsOfDay} from 'src/model/dto/TicketDto';

export default defineComponent({
  name: 'TicketListPage',
  components: {CustomFieldDate},
  props: ['accountId'],
  setup(props) {
    // init
    const storeAccount = useAccountStore();
    const storeTicket = useTicketStore();

    const startDate = (date.formatDate(date.startOfDate(new Date(), 'month'), 'YYYY-MM-DD'));
    const endDate = (date.formatDate(date.startOfDate(new Date(), 'month'), 'YYYY-MM-DD'));

    // load data
    storeAccount.loadAccountById(props.accountId)
    storeTicket.loadTicketsByFilter(props.accountId, startDate, endDate)

    const filterName = ref('');
    const filterStartDate = ref(startDate);
    const filterEndDate = ref(endDate);

    const findTicketsMethod = () => {
      storeTicket.loadTicketsByFilter(props.accountId, filterStartDate.value, filterEndDate.value)
    }

    return {
      // init
      storeAccount,
      storeTicket,
      // variable
      filterName,
      filterStartDate,
      filterEndDate,
      //methods
      findTicketsMethod,
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
    isEmptyList(days: TicketsOfDay[]) {
      return days === undefined || days.length == 0;
    }
  }
})
</script>

<style scoped>

</style>
