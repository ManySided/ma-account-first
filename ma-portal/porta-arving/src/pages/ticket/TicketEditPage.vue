<template>
  <q-page>
    <div class="q-pa-md q-gutter-sm">
      <div class="row">
        <div class="col-12" style="padding-left: 5px">
          <!-- ввод данных чека -->
          <q-card flat bordered
                  :style="{ background: thisTicket.ticketDirection==='EXPENDITURE'?'#fbfcfe':'#eef7f0'}">
            <q-card-section class="q-pt-xs">
              <!-- Секция чека -->
              <q-btn flat color="primary" @click="leavePage">
                <q-icon left name="arrow_back"/>
                Вернуться
              </q-btn>
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
              <q-scroll-area :style="{ height: windowHeight-250+'px', width: 100+'%' }"
                             ref="scrollOperationRef">
                <q-list separator>
                  <q-item v-for="(itemOperation, indexOperation) in thisTicket.operations" :key="indexOperation">
                    <q-item-section side top>
                      <q-badge outline color="primary" :label="getNumberPosition(indexOperation)"/>
                      <q-icon name="delete_outline" style="font-size: 2em" @click="removeOperation(indexOperation)"/>
                    </q-item-section>

                    <q-item-section>
                      <operation-edit-row
                        v-model:operationVariable="thisTicket.operations[indexOperation]"
                        :account-id="storeStuff.getAccountId"
                        @change-sum="refreshTotalSum"
                      />
                    </q-item-section>
                  </q-item>
                  <div class="col-2 column items-end">
                    <q-btn flat
                           @click="addOperation"
                           padding="md"
                           icon="add_circle_outline"
                           color="secondary">Добавить операцию
                    </q-btn>
                  </div>
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
import {useTicketStore} from 'stores/ticketStore';
import {useRouter} from 'vue-router';
import {useStuffStore} from 'stores/stuffStore';
import {useTagStore} from 'stores/tagsStore';

export default defineComponent({
  name: 'TicketEditPage',
  components: {CustomFieldDate, OperationEditRow},
  setup() {
    // store
    const storeTicket = useTicketStore();
    const storeStuff = useStuffStore();
    const storeTags = useTagStore();
    // init
    storeStuff.actionUpdateTitlePage('Создание чека');
    storeTags.actionLoadTags(storeStuff.getAccountId)
    const router = useRouter();
    const ticketOptions = ref(ticketDirection)
    const thisTicket = ref({
      ticketDirection: 'EXPENDITURE',
      date: date.formatDate(Date.now(), 'YYYY-MM-DD'),
      accountId: storeStuff.getAccountId,
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

    const windowWidth = ref(window.innerWidth)
    const windowHeight = ref(window.innerHeight)
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
      if (isValidTicket(thisTicket.value)) {
        storeTicket.actionSaveTicket(thisTicket.value, () => {
          leavePage()
        })
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
      router.push({name: 'tickets'})
    }

    return {
      // store
      storeStuff,
      // variable
      scrollOperationRef,
      ticketOptions,
      thisTicket,
      windowWidth,
      windowHeight,
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
