<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <div class="row">
        <div class="col-2">
          <!-- информация о счёте -->
          <q-card flat bordered>
            <q-card-section class="q-pt-xs">
              <div class="text-overline">Счёт</div>
            </q-card-section>
            <div style="padding-left: 5px; padding-right: 5px; padding-bottom: 5px;">
              <q-input outlined
                       label="Название"
                       v-model="storeAccount.getCurrentAccountName"
                       hide-bottom-space readonly/>
            </div>
            <div style="padding-left: 5px; padding-right: 5px; padding-bottom: 5px;">
              <q-input outlined
                       label="Текущая сумма"
                       v-model="storeAccount.getCurrentAccountSum"
                       type="number" hide-bottom-space readonly/>
            </div>
            <q-separator/>
            <q-card-actions>
              <q-btn flat color="primary" style="width: 100%" @click="leavePage">Вернуться</q-btn>
            </q-card-actions>
          </q-card>
        </div>
        <div class="col-10" style="padding-left: 5px">
          <!-- ввод данных чека -->
          <q-card flat bordered>
            <q-card-section class="q-pt-xs">
              <!-- Секция чека -->
              <div class="text-overline">Редактирование чека</div>
            </q-card-section>
            <div class="row" style="padding-left: 5px; padding-right: 5px; padding-bottom: 5px;">
              <div class="col-2" style="padding-right: 5px">
                <custom-field-date fieldName="Дата чека" v-model:dateVariable="thisTicket.date" canChange/>
              </div>
              <div class="col-2" style="padding-right: 5px">
                <q-select
                  v-model="thisTicket.ticketDirection"
                  :options="ticketOptions"
                  :label-color="thisTicket.ticketDirection==='INCOME'?'secondary':'primary'"
                  option-value="value"
                  option-label="label"
                  label="Направление чека"
                  emit-value
                  map-options
                  outlined
                  hide-bottom-space/>
              </div>
              <div class="col-2" style="padding-right: 5px">
                <q-item
                  clickable
                  v-ripple
                  style="border: 1px solid #c2c2c2"
                  class="rounded-borders"
                  :class="$q.dark.isActive ? 'bg-grey-9 text-white' : 'bg-grey-2'"
                >
                  <q-item-section>
                    <q-item-label style="color: #a4a4a4; font-size: 14px">
                      Итого
                    </q-item-label>
                    {{ formattedNumber(thisTicket.totalSum) }}
                  </q-item-section>
                </q-item>
              </div>
              <div class="col-2">
                <q-btn flat
                       @click="addOperation"
                       padding="md"
                       icon="add_circle_outline"
                       color="secondary">Добавить операцию
                </q-btn>
              </div>
              <div class="col-4 column items-end">
                <q-btn flat
                       @click="saveTicket"
                       padding="md"
                       icon="note_add"
                       color="primary">Сохранить чек
                </q-btn>
              </div>
            </div>
            <q-separator/>
            <q-card-section>
              <!-- Секция операций -->
              <q-scroll-area style="height: 900px; width: 100%;" ref="scrollOperationRef">
                <q-list separator>
                  <q-item v-for="(itemOperation, indexOperation) in thisTicket.operations" :key="indexOperation">
                    <q-item-section side top>
                      <q-badge outline color="primary" :label="getNumberPosition(indexOperation)"/>
                      <q-icon name="delete_outline" style="font-size: 2em" @click="removeOperation(indexOperation)"/>
                    </q-item-section>

                    <q-item-section>
                      <operation-edit-row
                        v-model:operationVariable="thisTicket.operations[indexOperation]"
                        :account-id="accountId"
                        @change-sum="refreshTotalSum"
                      />
                    </q-item-section>
                  </q-item>
                </q-list>
              </q-scroll-area>
            </q-card-section>
          </q-card>
        </div>
      </div>
    </div>
  </q-page>
</template>

<script lang="ts">
import {defineComponent, ref} from 'vue';
import CustomFieldDate from 'components/utils/CustomFieldDate.vue';
import OperationEditRow from 'components/utils/OperationEditRow.vue';
import {date, Notify} from 'quasar';
import Ticket, {isValidTicket, ticketDirection} from 'src/model/dto/TicketDto';
import {useAccountStore} from 'stores/accountStore';
import {useTicketStore} from 'stores/ticketStore';
import {useRouter} from 'vue-router';

export default defineComponent({
  name: 'TicketEditPage',
  components: {CustomFieldDate, OperationEditRow},
  props: ['accountId'],
  setup(props) {
    // init
    const router = useRouter();
    const ticketOptions = ref(ticketDirection)
    const thisTicket = ref({
      ticketDirection: 'EXPENDITURE',
      date: date.formatDate(Date.now(), 'YYYY-MM-DD'),
      accountId: props.accountId,
      operations: [
        {
          sum: 0,
          name: '',
          comment: '',
          category: {}
        }
      ],
      totalSum: 0
    } as Ticket);
    const scrollOperationRef = ref(null)
    // store
    const storeAccount = useAccountStore();
    const storeTicket = useTicketStore();
    // load data
    storeAccount.loadAccountById(props.accountId)
    // methods
    const formattedNumber = (n: number) => {
      if (n)
        return n.toLocaleString();
      return '0';
    }
    const addOperation = () => {
      thisTicket.value.operations.push({
        sum: 0,
        name: '',
        comment: '',
        category: {name: ''},
        isActive: true
      });
      if (scrollOperationRef.value)
        scrollOperationRef.value.setScrollPosition('vertical', 3000)
    }
    const getNumberPosition = (itemIndex: number) => {
      if (itemIndex) {
        const position = itemIndex + 1
        if (String(position).length == 1)
          return '0' + position;
        return position
      }
      return '01'
    }
    const removeOperation = (operationIndex: number) => {
      if (operationIndex) {
        thisTicket.value.operations.splice(operationIndex, 1);
        refreshTotalSum()
      }
    }
    const refreshTotalSum = () => {
      thisTicket.value.totalSum = thisTicket.value.operations
        .reduce((sum, current) => Number(sum) + Number(current.sum), Number(0));
    }
    const saveTicket = () => {
      console.log("сохраянем чек. Валидация")
      console.log(isValidTicket(thisTicket.value))
      if (isValidTicket(thisTicket.value)) {
        storeTicket.actionSaveTicket(thisTicket.value, () => {
          leavePage()
        })
        leavePage()
      } else {
        Notify.create({
          color: 'negative',
          position: 'top',
          message: 'Чек заполнен некорректно ',
          icon: 'report_problem'
        })
      }
    }
    const leavePage = () => {
      router.push({name: 'tickets', params: {accountId: props.accountId}})
    }

    return {
      // store
      storeAccount,
      // variable
      scrollOperationRef,
      ticketOptions,
      thisTicket,
      // methods
      formattedNumber,
      addOperation,
      removeOperation,
      refreshTotalSum,
      saveTicket,
      getNumberPosition,
      leavePage
    }
  }
})
</script>

<style lang="sass" scoped>
.field-padding
  padding-right: 5px
  padding-bottom: 5px
</style>
